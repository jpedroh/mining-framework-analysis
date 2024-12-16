package org.kohsuke.file_leak_detector;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.zip.ZipFile;
import org.kohsuke.asm6.Label;
import org.kohsuke.asm6.MethodVisitor;
import org.kohsuke.asm6.Type;
import org.kohsuke.asm6.commons.LocalVariablesSorter;
import org.kohsuke.file_leak_detector.transform.ClassTransformSpec;
import org.kohsuke.file_leak_detector.transform.CodeGenerator;
import org.kohsuke.file_leak_detector.transform.MethodAppender;
import org.kohsuke.file_leak_detector.transform.TransformerImpl;
import static org.kohsuke.asm6.Opcodes.*;


/**
 * Java agent that instruments JDK classes to keep track of where file descriptors are opened.
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("Since15")
public class AgentMain {
    public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception {
        premain(agentArguments,instrumentation);
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) throws Exception {
        int serverPort = -1;
        if (agentArguments != null) {
            for (String t : agentArguments.split(",")) {
                if (t.equals("help")) {
                    usageAndQuit();
                } else if (t.startsWith("threshold=")) {
                    Listener.THRESHOLD = Integer.parseInt(t.substring(t.indexOf('=') + 1));
                } else if (t.equals("trace")) {
                    Listener.TRACE = new PrintWriter(System.err);
                } else if (t.equals("strong")) {
                    Listener.makeStrong();
                } else if (t.startsWith("http=")) {
                    serverPort = Integer.parseInt(t.substring(t.indexOf('=') + 1));
                } else if (t.startsWith("trace=")) {
                    Listener.TRACE = new PrintWriter(new FileOutputStream(t.substring(6)));
                } else if (t.startsWith("error=")) {
                    Listener.ERROR = new PrintWriter(new FileOutputStream(t.substring(6)));
                } else if (t.startsWith("listener=")) {
                    ActivityListener.LIST.add(((ActivityListener) (AgentMain.class.getClassLoader().loadClass(t.substring(9)).newInstance())));
                } else if (t.equals("dumpatshutdown")) {
                    Runtime.getRuntime().addShutdownHook(new Thread("File handles dumping shutdown hook") {
                        @Override
                        public void run() {
                            Listener.dump(System.err);
                        }
                    });
                } else if (t.startsWith("excludes=")) {
                    BufferedReader reader = new BufferedReader(new FileReader(t.substring(9)));
                    try {
                        while (true) {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            String str = line.trim();
                            // add the entries from the excludes-file, but filter out empty ones and comments
                            if ((!str.isEmpty()) && (!str.startsWith("#"))) {
                                Listener.EXCLUDES.add(str);
                            }
                        } 
                    } finally {
                        reader.close();
                    }
                } else {
                    System.err.println("Unknown option: " + t);
                    usageAndQuit();
                }
            }
        }
        Listener.EXCLUDES.add("sun.nio.ch.PipeImpl$Initializer$LoopbackConnector.run");
        System.err.println("File leak detector installed");
        // Make sure the ActivityListener is loaded to prevent recursive death in instrumentation
        ActivityListener.LIST.size();
        Listener.AGENT_INSTALLED = true;
        instrumentation.addTransformer(new TransformerImpl(createSpec()), true);
        instrumentation.retransformClasses(FileInputStream.class, FileOutputStream.class, RandomAccessFile.class, Class.forName("java.net.PlainSocketImpl"), ZipFile.class, AbstractSelectableChannel.class, AbstractInterruptibleChannel.class);
//                Socket.class,
//                SocketChannel.class,
//                AbstractInterruptibleChannel.class,
//                ServerSocket.class);
        if (serverPort >= 0) {
            runHttpServer(serverPort);
        }
    }

    private static void runHttpServer(int port) throws IOException {
        final ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("localhost", port));
        System.err.println("Serving file leak stats on http://localhost:"+ss.getLocalPort()+"/ for stats");
        final ExecutorService es = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        es.submit(new Callable<Object>() {
            public Object call() throws Exception {
                while (true) {
                    final Socket s = ss.accept();
                    es.submit(new Callable<Void>() {
                        public Void call() throws Exception {
                            try {
                                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                // Read the request line (and ignore it)
                                in.readLine();

                                PrintWriter w = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
                                w.print("HTTP/1.0 200 OK\r\nContent-Type: text/plain;charset=UTF-8\r\n\r\n");
                                Listener.dump(w);
                            } finally {
                                s.close();
                            }
                            return null;
                        }
                    });
                }
            }
        });
    }

    private static void usageAndQuit() {
        System.err.println("File leak detector arguments (to specify multiple values, separate them by ',':");
        printOptions();
        System.exit(-1);
    }

    static void printOptions() {
        System.err.println("  help          - show the help screen.");
        System.err.println("  trace         - log every open/close operation to stderr.");
        System.err.println("  trace=FILE    - log every open/close operation to the given file.");
        System.err.println("  error=FILE    - if 'too many open files' error is detected, send the dump here.");
        System.err.println("                  by default it goes to stderr.");
        System.err.println("  threshold=N   - instead of waiting until 'too many open files', dump once");
        System.err.println("                  we have N descriptors open.");
        System.err.println("  http=PORT     - Run a mini HTTP server that you can access to get stats on demand");
        System.err.println("                  Specify 0 to choose random available port, -1 to disable, which is default.");
        System.err.println("  strong        - Don't let GC auto-close leaking file descriptors");
        System.err.println("  listener=S    - Specify the fully qualified name of ActivityListener class to activate from beginning");
        System.err.println("  dumpatshutdown- Dump open file handles at shutdown");
        System.err.println("  excludes=FILE - Ignore files opened directly/indirectly in specific methods.");
        System.err.println("                  File lists 'some.pkg.ClassName.methodName' patterns.");
    }

    static List<ClassTransformSpec> createSpec() {
        return /*
                java.net.Socket/ServerSocket uses SocketImpl, and this is where FileDescriptors
                are actually managed.

                SocketInputStream/SocketOutputStream does not maintain a separate FileDescritor.
                They just all piggy back on the same SocketImpl instance.
             */
            // Later versions of the JDK abstracted out the parts of PlainSocketImpl above into a super class
        Arrays.asList(newSpec(FileOutputStream.class, "(Ljava/io/File;Z)V"), newSpec(FileInputStream.class, "(Ljava/io/File;)V"), newSpec(RandomAccessFile.class, "(Ljava/io/File;Ljava/lang/String;)V"), newSpec(ZipFile.class, "(Ljava/io/File;I)V"), newFdSpec("java/nio/channels/spi/AbstractInterruptibleChannel", "close", "()V", "close"), newFdSpec("java/nio/channels/spi/AbstractSelectableChannel", "<init>", "(Ljava/nio/channels/spi/SelectorProvider;)V", "ch_open"), // this is where a new file descriptor is allocated.
        // it'll occupy a socket even before it gets connected
        // When a socket is accepted, it goes to "accept(SocketImpl s)"
        // where 's' is the new socket and 'this' is the server socket
        // file descriptor actually get closed in socketClose()
        // socketPreClose() appears to do something similar, but if you read the source code
        // of the native socketClose0() method, then you see that it actually doesn't close
        // a file descriptor.
        new ClassTransformSpec("java/net/PlainSocketImpl", new OpenSocketInterceptor("create", "(Z)V"), new AcceptInterceptor("accept", "(Ljava/net/SocketImpl;)V"), new CloseInterceptor("socketClose")), new ClassTransformSpec("java/net/AbstractPlainSocketImpl", new OpenSocketInterceptor("create", "(Z)V"), new AcceptInterceptor("accept", "(Ljava/net/SocketImpl;)V"), new CloseInterceptor("socketClose")), new ClassTransformSpec("sun/nio/ch/SocketChannelImpl", new OpenSocketInterceptor("<init>", "(Ljava/nio/channels/spi/SelectorProvider;Ljava/io/FileDescriptor;Ljava/net/InetSocketAddress;)V"), new OpenSocketInterceptor("<init>", "(Ljava/nio/channels/spi/SelectorProvider;)V"), new CloseInterceptor("kill")));
    }

    /**
     * Creates {@link ClassTransformSpec} that intercepts
     * a constructor and the close method.
     */
    private static ClassTransformSpec newSpec(final Class c, String constructorDesc) {
        final String binName = c.getName().replace('.', '/');
        return new ClassTransformSpec(binName, new CloseInterceptor());
    }

    /**
     * Creates {@link ClassTransformSpec} that intercepts
     * a constructor and the close method.
     */
    private static ClassTransformSpec newFdSpec(String binName,  String methodName, String constructorDesc, String listenermethod) {
        return new ClassTransformSpec(binName,
                new GenericInterceptor(methodName, constructorDesc, listenermethod)
        );
    }

    /**
     * Intercepts the {@code void close()} method and calls {@link Listener#close(Object)} in the end.
     */
    private static class CloseInterceptor extends MethodAppender {
        public CloseInterceptor() {
            this("close");
        }

        public CloseInterceptor(String methodName) {
            super(methodName, "()V");
        }

        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class, "close", new Class[]{ java.lang.Object.class }, new int[]{ 0 });
        }
    }

    private static class OpenSocketInterceptor extends MethodAppender {
        public OpenSocketInterceptor(String name, String desc) {
            super(name, desc);
        }

        @Override
        public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
            return new OpenInterceptionAdapter(b, access, desc) {
                @Override
                protected boolean toIntercept(String owner, String name) {
                    return name.equals("socketCreate");
                }
            };
        }

        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class,"openSocket",
                    new Class[]{Object.class},
                    new int[]{0});
        }
    }

    /**
     * Used to intercept {@link java.net.PlainSocketImpl#accept(SocketImpl)}
     */
    private static class AcceptInterceptor extends MethodAppender {
        public AcceptInterceptor(String name, String desc) {
            super(name, desc);
        }

        @Override
        public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
            return new OpenInterceptionAdapter(b, access, desc) {
                @Override
                protected boolean toIntercept(String owner, String name) {
                    return name.equals("socketAccept");
                }
            };
        }

        protected void append(CodeGenerator g) {
            // the 's' parameter is the new socket that will own the socket
            g.invokeAppStatic(Listener.class,"openSocket",
                    new Class[]{Object.class},
                    new int[]{1});
        }
    }

    /**
     * Rewrites a method that includes a call to a native method that actually opens a file descriptor
     * (therefore it can throw "too many open files" exception.)
     *
     * surround the call with try/catch, and if "too many open files" exception is thrown
     * call {@link Listener#outOfDescriptors()}.
     */
    private abstract static class OpenInterceptionAdapter extends MethodVisitor {
        private final LocalVariablesSorter lvs;

        private final MethodVisitor base;

        private OpenInterceptionAdapter(MethodVisitor base, int access, String desc) {
            super(ASM5);
            lvs = new LocalVariablesSorter(access, desc, base);
            mv = lvs;
            this.base = base;
        }

        /**
         * Decide if this is the method that needs interception.
         */
        protected abstract boolean toIntercept(String owner, String name);

        protected Class<? extends Exception> getExpectedException() {
            return IOException.class;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (toIntercept(owner, name)) {
                Type exceptionType = Type.getType(getExpectedException());
                CodeGenerator g = new CodeGenerator(mv);
                // start of the try block
                Label s = new Label();
                // end of the try block
                Label e = new Label();
                // handler entry point
                Label h = new Label();
                // where the execution continue
                Label tail = new Label();
                g.visitTryCatchBlock(s, e, h, exceptionType.getInternalName());
                g.visitLabel(s);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                g._goto(tail);
                g.visitLabel(e);
                g.visitLabel(h);
                // [RESULT]
                // catch(E ex) {
                //    boolean b = ex.getMessage().contains("Too many open files");
                int ex = lvs.newLocal(exceptionType);
                g.dup();
                base.visitVarInsn(ASTORE, ex);
                g.invokeVirtual(exceptionType.getInternalName(), "getMessage", "()Ljava/lang/String;");
                g.ldc("Too many open files");
                g.invokeVirtual("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
                // too many open files detected
                //    if (b) { Listener.outOfDescriptors() }
                Label rethrow = new Label();
                g.ifFalse(rethrow);
                g.invokeAppStatic(Listener.class, "outOfDescriptors", new Class[0], new int[0]);
                // rethrow the FileNotFoundException
                g.visitLabel(rethrow);
                base.visitVarInsn(ALOAD, ex);
                g.athrow();
                // normal execution continues here
                g.visitLabel(tail);
            } else // no processing
            {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    /**
     * Intercepts the this.open(...) call in the constructor.
     */
    private static class ConstructorOpenInterceptor extends MethodAppender {
        /**
         * Binary name of the class being transformed.
         */
        private final String binName;

        public ConstructorOpenInterceptor(String constructorDesc, String binName) {
            super("<init>", constructorDesc);
            this.binName = binName;
        }

        @Override
        public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
            final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
            return new OpenInterceptionAdapter(b, access, desc) {
                @Override
                protected boolean toIntercept(String owner, String name) {
                    return owner.equals(binName) && name.startsWith("open");
                }

                @Override
                protected Class<? extends Exception> getExpectedException() {
                    return FileNotFoundException.class;
                }
            };
        }

        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class,"open",
                    new Class[]{Object.class, File.class},
                    new int[]{0,1});
        }
    }

    /**
     * Intercepts the constructor.
     */
    private static class GenericInterceptor extends MethodAppender {
        private final String listenerMethod;

        public GenericInterceptor(String methodName, String constructorDesc, String listenerMethod) {
            super(methodName, constructorDesc);
            this.listenerMethod = listenerMethod;
        }

        protected void append(CodeGenerator g) {
            g.invokeAppStatic(Listener.class, listenerMethod, new Class[]{ java.lang.Object.class }, new int[]{ 0 });
        }
    }
}