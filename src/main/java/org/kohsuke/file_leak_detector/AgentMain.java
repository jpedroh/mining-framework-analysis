package org.kohsuke.file_leak_detector;
import static org.kohsuke.asm3.Opcodes.ALOAD;
import static org.kohsuke.asm3.Opcodes.ASTORE;
import java.io.BufferedReader;
import org.kohsuke.asm3.Label;
import java.io.File;
import org.kohsuke.asm3.MethodAdapter;
import java.io.FileInputStream;
import org.kohsuke.asm3.MethodVisitor;
import java.io.FileNotFoundException;
import org.kohsuke.asm3.Type;
import java.io.FileOutputStream;
import org.kohsuke.asm3.commons.LocalVariablesSorter;
import java.io.FileReader;
import java.io.IOException;
import org.kohsuke.file_leak_detector.transform.ClassTransformSpec;
import java.io.PrintWriter;
import org.kohsuke.file_leak_detector.transform.CodeGenerator;
import java.io.RandomAccessFile;
import org.kohsuke.file_leak_detector.transform.MethodAppender;
import java.lang.instrument.Instrumentation;
import org.kohsuke.file_leak_detector.transform.TransformerImpl;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.zip.ZipFile;

/**
 * Java agent that instruments JDK classes to keep track of where file descriptors are opened.
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings(value = { "Since15" }) public class AgentMain {
  public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception {
    premain(agentArguments, instrumentation);
  }

  public static void premain(String agentArguments, Instrumentation instrumentation) throws Exception {
    int serverPort = -1;
    if (agentArguments != null) {
      for (String t : agentArguments.split(",")) {
        if (t.equals("help")) {
          usageAndQuit();
        } else {
          if (t.startsWith("threshold=")) {
            Listener.THRESHOLD = Integer.parseInt(t.substring(t.indexOf('=') + 1));
          } else {
            if (t.equals("trace")) {
              Listener.TRACE = new PrintWriter(System.err);
            } else {
              if (t.equals("strong")) {
                Listener.makeStrong();
              } else {
                if (t.startsWith("http=")) {
                  serverPort = Integer.parseInt(t.substring(t.indexOf('=') + 1));
                } else {
                  if (t.startsWith("trace=")) {
                    Listener.TRACE = new PrintWriter(new FileOutputStream(t.substring(6)));
                  } else {
                    if (t.startsWith("error=")) {
                      Listener.ERROR = new PrintWriter(new FileOutputStream(t.substring(6)));
                    } else {
                      if (t.startsWith(
<<<<<<< /usr/src/app/output/kohsuke/file-leak-detector/f8c0570493ece0c9d3e4b56ae344b307bc9d33e0/src/main/java/org/kohsuke/file_leak_detector/AgentMain.java/left.java
                      "excludes="
=======
                      "listener="
>>>>>>> /usr/src/app/output/kohsuke/file-leak-detector/f8c0570493ece0c9d3e4b56ae344b307bc9d33e0/src/main/java/org/kohsuke/file_leak_detector/AgentMain.java/right.java
                      )) {

<<<<<<< /usr/src/app/output/kohsuke/file-leak-detector/f8c0570493ece0c9d3e4b56ae344b307bc9d33e0/src/main/java/org/kohsuke/file_leak_detector/AgentMain.java/left.java
                        List<String> lines = new ArrayList<String>();
=======
                        ActivityListener.LIST.add((ActivityListener) AgentMain.class.getClassLoader().loadClass(t.substring(9)).newInstance());
>>>>>>> /usr/src/app/output/kohsuke/file-leak-detector/f8c0570493ece0c9d3e4b56ae344b307bc9d33e0/src/main/java/org/kohsuke/file_leak_detector/AgentMain.java/right.java

                        BufferedReader reader = new BufferedReader(new FileReader(t.substring(9)));
                        try {
                          String line = reader.readLine();
                          while (line != null) {
                            lines.add(line);
                            line = reader.readLine();
                          }
                        }  finally {
                          reader.close();
                        }
                        Iterator<String> it = lines.iterator();
                        while (it.hasNext()) {
                          String str = it.next().trim();
                          if (!str.isEmpty() && !str.startsWith("#")) {
                            Listener.EXCLUDES.add(str);
                          }
                        }
                      } else {
                        System.err.println("Unknown option: " + t);
                        usageAndQuit();
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    System.err.println("File leak detector installed");
    Listener.AGENT_INSTALLED = true;
    instrumentation.addTransformer(new TransformerImpl(createSpec()), true);
    instrumentation.retransformClasses(FileInputStream.class, FileOutputStream.class, RandomAccessFile.class, Class.forName("java.net.PlainSocketImpl"), ZipFile.class);
    if (serverPort >= 0) {
      runHttpServer(serverPort);
    }
  }

  private static void runHttpServer(int port) throws IOException {
    final ServerSocket ss = new ServerSocket();
    ss.bind(new InetSocketAddress("localhost", port));
    System.err.println("Serving file leak stats on http://localhost:" + ss.getLocalPort() + "/ for stats");
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
                in.readLine();
                PrintWriter w = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                w.print("HTTP/1.0 200 OK\r\nContent-Type: text/plain;charset=UTF-8\r\n\r\n");
                Listener.dump(w);
              }  finally {
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
    System.err.println("File leak detector arguments (to specify multiple values, separate them by \',\':");
    printOptions();
    System.exit(-1);
  }

  static void printOptions() {
    System.err.println("  help          - show the help screen.");
    System.err.println("  trace         - log every open/close operation to stderr.");
    System.err.println("  trace=FILE    - log every open/close operation to the given file.");
    System.err.println("  error=FILE    - if \'too many open files\' error is detected, send the dump here.");
    System.err.println("                  by default it goes to stderr.");
    System.err.println("  threshold=N   - instead of waiting until \'too many open files\', dump once");
    System.err.println("                  we have N descriptors open.");
    System.err.println("  http=PORT     - Run a mini HTTP server that you can access to get stats on demand");
    System.err.println("                  Specify 0 to choose random available port, -1 to disable, which is default.");
    System.err.println("  strong        - Don\'t let GC auto-close leaking file descriptors");
    System.err.println("  excludes=File - Exclude any opened file where a line in the given exclude-file matches");
    System.err.println(
<<<<<<< /usr/src/app/output/kohsuke/file-leak-detector/f8c0570493ece0c9d3e4b56ae344b307bc9d33e0/src/main/java/org/kohsuke/file_leak_detector/AgentMain.java/left.java
    "                  one of the lines from the stacktrace of the open-call."
=======
    "  listener=S  - Specify the fully qualified name of ActivityListener class to activate from beginning"
>>>>>>> /usr/src/app/output/kohsuke/file-leak-detector/f8c0570493ece0c9d3e4b56ae344b307bc9d33e0/src/main/java/org/kohsuke/file_leak_detector/AgentMain.java/right.java
    );
  }

  static List<ClassTransformSpec> createSpec() {
    return Arrays.asList(newSpec(FileOutputStream.class, "(Ljava/io/File;Z)V"), newSpec(FileInputStream.class, "(Ljava/io/File;)V"), newSpec(RandomAccessFile.class, "(Ljava/io/File;Ljava/lang/String;)V"), newSpec(ZipFile.class, "(Ljava/io/File;I)V"), new ClassTransformSpec("java/net/PlainSocketImpl", new OpenSocketInterceptor("create", "(Z)V"), new AcceptInterceptor("accept", "(Ljava/net/SocketImpl;)V"), new CloseInterceptor("socketClose")), new ClassTransformSpec("sun/nio/ch/SocketChannelImpl", new OpenSocketInterceptor("<init>", "(Ljava/nio/channels/spi/SelectorProvider;)V"), new CloseInterceptor("kill")));
  }

  /**
     * Creates {@link ClassTransformSpec} that intercepts
     * a constructor and the close method.
     */
  private static ClassTransformSpec newSpec(final Class c, String constructorDesc) {
    final String binName = c.getName().replace('.', '/');
    return new ClassTransformSpec(binName, new ConstructorOpenInterceptor(constructorDesc, binName), new CloseInterceptor());
  }

  private static class CloseInterceptor extends MethodAppender {
    public CloseInterceptor() {
      this("close");
    }

    public CloseInterceptor(String methodName) {
      super(methodName, "()V");
    }

    @Override protected void append(CodeGenerator g) {
      g.invokeAppStatic(Listener.class, "close", new Class[] { Object.class }, new int[] { 0 });
    }
  }

  private static class OpenSocketInterceptor extends MethodAppender {
    public OpenSocketInterceptor(String name, String desc) {
      super(name, desc);
    }

    @Override public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
      final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
      return new OpenInterceptionAdapter(b, access, desc) {
        @Override protected boolean toIntercept(String owner, String name) {
          return name.equals("socketCreate");
        }
      };
    }

    @Override protected void append(CodeGenerator g) {
      g.invokeAppStatic(Listener.class, "openSocket", new Class[] { Object.class }, new int[] { 0 });
    }
  }

  private static class AcceptInterceptor extends MethodAppender {
    public AcceptInterceptor(String name, String desc) {
      super(name, desc);
    }

    @Override public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
      final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
      return new OpenInterceptionAdapter(b, access, desc) {
        @Override protected boolean toIntercept(String owner, String name) {
          return name.equals("socketAccept");
        }
      };
    }

    @Override protected void append(CodeGenerator g) {
      g.invokeAppStatic(Listener.class, "openSocket", new Class[] { Object.class }, new int[] { 1 });
    }
  }

  private static abstract class OpenInterceptionAdapter extends MethodAdapter {
    private final LocalVariablesSorter lvs;

    private final MethodVisitor base;

    private OpenInterceptionAdapter(MethodVisitor base, int access, String desc) {
      super(null);
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

    @Override public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      if (toIntercept(owner, name)) {
        Type exceptionType = Type.getType(getExpectedException());
        CodeGenerator g = new CodeGenerator(mv);
        Label s = new Label();
        Label e = new Label();
        Label h = new Label();
        Label tail = new Label();
        g.visitTryCatchBlock(s, e, h, exceptionType.getInternalName());
        g.visitLabel(s);
        super.visitMethodInsn(opcode, owner, name, desc);
        g._goto(tail);
        g.visitLabel(e);
        g.visitLabel(h);
        int ex = lvs.newLocal(exceptionType);
        g.dup();
        base.visitVarInsn(ASTORE, ex);
        g.invokeVirtual(exceptionType.getInternalName(), "getMessage", "()Ljava/lang/String;");
        g.ldc("Too many open files");
        g.invokeVirtual("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
        Label rethrow = new Label();
        g.ifFalse(rethrow);
        g.invokeAppStatic(Listener.class, "outOfDescriptors", new Class[0], new int[0]);
        g.visitLabel(rethrow);
        base.visitVarInsn(ALOAD, ex);
        g.athrow();
        g.visitLabel(tail);
      } else {
        super.visitMethodInsn(opcode, owner, name, desc);
      }
    }
  }

  private static class ConstructorOpenInterceptor extends MethodAppender {
    /**
         * Binary name of the class being transformed.
         */
    private final String binName;

    public ConstructorOpenInterceptor(String constructorDesc, String binName) {
      super("<init>", constructorDesc);
      this.binName = binName;
    }

    @Override public MethodVisitor newAdapter(MethodVisitor base, int access, String name, String desc, String signature, String[] exceptions) {
      final MethodVisitor b = super.newAdapter(base, access, name, desc, signature, exceptions);
      return new OpenInterceptionAdapter(b, access, desc) {
        @Override protected boolean toIntercept(String owner, String name) {
          return owner.equals(binName) && name.startsWith("open");
        }

        @Override protected Class<? extends Exception> getExpectedException() {
          return FileNotFoundException.class;
        }
      };
    }

    @Override protected void append(CodeGenerator g) {
      g.invokeAppStatic(Listener.class, "open", new Class[] { Object.class, File.class }, new int[] { 0, 1 });
    }
  }
}