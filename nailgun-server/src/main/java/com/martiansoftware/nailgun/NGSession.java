package com.martiansoftware.nailgun;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the NailGun stream from the client through the command, then hands off
 * processing to the appropriate class. The NGSession obtains its sockets from
 * an NGSessionPool, which created this NGSession.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 */
public class NGSession extends Thread {
  /**
	 * {@linkplain Logger} instance for this class.
	 */
  private static final Logger 
<<<<<<< /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/left.java
  LOGGER = Logger.getLogger(NGServer.class.getName())
=======
  LOG = Logger.getLogger(NGSession.class.getName())
>>>>>>> /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/right.java
  ;

  /**
     * The server this NGSession is working for
     */
  private final NGServer server;

  /**
     * The pool this NGSession came from, and to which it will return itself
     */
  private final NGSessionPool sessionPool;

  /**
     * Synchronization object
     */
  private final Object lock = new Object();

  /**
     * The next socket this NGSession has been tasked with processing (by
     * NGServer)
     */
  private Socket nextSocket = null;

  /**
     * True if the server has been shutdown and this NGSession should terminate
     * completely
     */
  private boolean done = false;

  /**
     * The instance number of this NGSession. That is, if this is the Nth
     * NGSession to be created, then this is the value for N.
     */
  private final long instanceNumber;

  /**
     * The interval to wait between heartbeats before considering the client to have disconnected.
     */
  private final int heartbeatTimeoutMillis;

  /**
     * The instance counter shared among all NGSessions
     */
  private static AtomicLong instanceCounter = new AtomicLong(0);

  /**
     * signature of main(String[]) for reflection operations
     */
  private final static Class[] mainSignature = { String[].class };

  /**
     * signature of nailMain(NGContext) for reflection operations
     */
  private final static Class[] nailMainSignature = { NGContext.class };

  /**
     * A ClassLoader that may be set by a client. Defaults to the classloader of this class.
     */
  public static volatile ClassLoader classLoader = null;

  static {
    try {
      classLoader = NGSession.class.getClassLoader();
    } catch (SecurityException e) {
      throw e;
    }
  }

  /**
     * Creates a new NGSession running for the specified NGSessionPool and
     * NGServer.
     *
     * @param sessionPool The NGSessionPool we're working for
     * @param server The NGServer we're working for
     */
  NGSession(NGSessionPool sessionPool, NGServer server) {
    super();
    this.sessionPool = sessionPool;
    this.server = server;
    this.heartbeatTimeoutMillis = server.getHeartbeatTimeout();
    this.instanceNumber = instanceCounter.incrementAndGet();
  }

  /**
     * Shuts down this NGSession gracefully
     */
  void shutdown() {
    synchronized (lock) {
      done = true;
      nextSocket = null;
      lock.notifyAll();
    }
  }

  /**
     * Instructs this NGSession to process the specified socket, after which
     * this NGSession will return itself to the pool from which it came.
     *
     * @param socket the socket (connected to a client) to process
     */
  public void run(Socket socket) {
    synchronized (lock) {
      nextSocket = socket;
      lock.notify();
    }
    Thread.yield();
  }

  /**
     * Returns the next socket to process. This will block the NGSession thread
     * until there's a socket to process or the NGSession has been shut down.
     *
     * @return the next socket to process, or
     * <code>null</code> if the NGSession has been shut down.
     */
  private Socket nextSocket() {
    Socket result = null;
    synchronized (lock) {
      result = nextSocket;
      while (!done && result == null) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          done = true;
        }
        result = nextSocket;
      }
      nextSocket = null;
    }
    return (result);
  }

  /**
     * The main NGSession loop. This gets the next socket to process, runs the
     * nail for the socket, and loops until shut down.
     */
  public void run() {
    updateThreadName(null);
    LOG.log(Level.FINE, "Waiting for first client to connect");
    Socket socket = nextSocket();
    while (socket != null) {
      LOG.log(Level.FINE, "Client connected");
      try {
        DataInputStream sockin = new DataInputStream(socket.getInputStream());
        DataOutputStream sockout = new DataOutputStream(socket.getOutputStream());
        List remoteArgs = new java.util.ArrayList();
        Properties remoteEnv = new Properties();
        String cwd = null;
        String command = null;
        while (command == null) {
          int bytesToRead = sockin.readInt();
          byte chunkType = sockin.readByte();
          byte[] b = new byte[(int) bytesToRead];
          sockin.readFully(b);
          String line = new String(b, "UTF-8");
          switch (chunkType) {
            case NGConstants.CHUNKTYPE_ARGUMENT:
            remoteArgs.add(line);
            break;
            case NGConstants.CHUNKTYPE_ENVIRONMENT:
            int equalsIndex = line.indexOf('=');
            if (equalsIndex > 0) {
              remoteEnv.setProperty(line.substring(0, equalsIndex), line.substring(equalsIndex + 1));
            }
            String key = line.substring(0, equalsIndex);
            break;
            case NGConstants.CHUNKTYPE_COMMAND:
            command = line;
            break;
            case NGConstants.CHUNKTYPE_WORKINGDIRECTORY:
            cwd = line;
            break;
            default:
          }
        }
        String threadName;
        if (socket.getInetAddress() != null) {
          threadName = socket.getInetAddress().getHostAddress() + ": " + command;
        } else {
          threadName = command;
        }
        updateThreadName(threadName);
        InputStream in = null;
        PrintStream out = null;
        PrintStream err = null;
        PrintStream exit = null;
        try {
          in = new NGInputStream(sockin, sockout, server.getLogger(), heartbeatTimeoutMillis);
          out = new PrintStream(new NGOutputStream(sockout, NGConstants.CHUNKTYPE_STDOUT));
          err = new PrintStream(new NGOutputStream(sockout, NGConstants.CHUNKTYPE_STDERR));
          exit = new PrintStream(new NGOutputStream(sockout, NGConstants.CHUNKTYPE_EXIT));
          ((ThreadLocalInputStream) System.in).init(in);
          ((ThreadLocalPrintStream) System.out).init(out);
          ((ThreadLocalPrintStream) System.err).init(err);
          try {
            Alias alias = server.getAliasManager().getAlias(command);
            Class cmdclass = null;
            if (alias != null) {
              cmdclass = alias.getAliasedClass();
            } else {
              if (server.allowsNailsByClassName()) {
                cmdclass = Class.forName(command, true, classLoader);
              } else {
                cmdclass = server.getDefaultNailClass();
              }
            }
            Object[] methodArgs = new Object[1];
            Method mainMethod = null;
            String[] cmdlineArgs = (String[]) remoteArgs.toArray(new String[remoteArgs.size()]);
            boolean isStaticNail = true;
            Class[] interfaces = cmdclass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
              if (interfaces[i].equals(NonStaticNail.class)) {
                isStaticNail = false;
                break;
              }
            }
            if (!isStaticNail) {
              mainMethod = cmdclass.getMethod("nailMain", new Class[] { String[].class });
              methodArgs[0] = cmdlineArgs;
            } else {
              try {
                mainMethod = cmdclass.getMethod("nailMain", nailMainSignature);
                NGContext context = new NGContext();
                context.setArgs(cmdlineArgs);
                context.in = in;
                context.out = out;
                context.err = err;
                context.setCommand(command);
                context.setExitStream(exit);
                context.setNGServer(server);
                context.setEnv(remoteEnv);
                context.setInetAddress(socket.getInetAddress());
                context.setPort(socket.getPort());
                context.setWorkingDirectory(cwd);
                methodArgs[0] = context;
              } catch (NoSuchMethodException toDiscard) {
              }
              if (mainMethod == null) {
                mainMethod = cmdclass.getMethod("main", mainSignature);
                methodArgs[0] = cmdlineArgs;
              }
            }
            if (mainMethod != null) {
              server.nailStarted(cmdclass);
              NGSecurityManager.setExit(exit);
              try {
                if (isStaticNail) {
                  mainMethod.invoke(null, methodArgs);
                } else {
                  mainMethod.invoke(cmdclass.newInstance(), methodArgs);
                }
              } catch (InvocationTargetException ite) {
                throw (ite.getCause());
              } catch (InstantiationException e) {
                throw (e);
              } catch (IllegalAccessException e) {
                throw (e);
              } catch (Throwable t) {
                throw (t);
              } finally {
                server.nailFinished(cmdclass);
              }
              exit.println(0);
            }
          } catch (NGExitException exitEx) {
            LOG.log(Level.INFO, "Server cleanly exited with status " + exitEx.getStatus(), exitEx);
            in.close();
            exit.println(exitEx.getStatus());
            server.getLogger().log(Level.INFO, Thread.currentThread().getName() + " exited with status " + exitEx.getStatus());
          } catch (Throwable t) {
            LOG.log(Level.INFO, "Server unexpectedly exited with unhandled exception", t);
            in.close();
            LOGGER.log(Level.SEVERE, t.getMessage(), t);
            exit.println(NGConstants.EXIT_EXCEPTION);
          }
        }  finally {
          LOG.log(Level.FINE, "Tearing down client socket");
          if (in != null) {
            in.close();
          }
          if (out != null) {
            out.close();
          }
          if (err != null) {
            err.close();
          }
          if (exit != null) {
            exit.close();
          }
          LOG.log(Level.FINE, "Flushing client socket");
          sockout.flush();
          try {
            socket.shutdownInput();
            socket.shutdownOutput();
          } catch (IOException e) {
            LOG.log(Level.FINE, "Error shutting down socket I/O (this is expected if the client disconnected already)", e);
          }
          LOG.log(Level.FINE, "Closing client socket");
          socket.close();
          LOG.log(Level.FINE, "Finished tearing down client socket");
        }
      } catch (Throwable t) {

<<<<<<< /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/left.java
        LOGGER
=======
        LOG
>>>>>>> /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/right.java
        .log(Level.
<<<<<<< /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/left.java
        SEVERE
=======
        WARNING
>>>>>>> /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/right.java
        , 
<<<<<<< /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/left.java
        t.getMessage()
=======
        "Internal error in session"
>>>>>>> /usr/src/app/output/martylamb/nailgun/5396f2f1911f43a2063bf0700ea1d7344ae2291f/nailgun-server/src/main/java/com/martiansoftware/nailgun/NGSession.java/right.java
        , t);
      }
      ((ThreadLocalInputStream) System.in).init(null);
      ((ThreadLocalPrintStream) System.out).init(null);
      ((ThreadLocalPrintStream) System.err).init(null);
      updateThreadName(null);
      sessionPool.give(this);
      LOG.log(Level.FINE, "Waiting for next client to connect");
      socket = nextSocket();
    }
    LOG.log(Level.INFO, "NGSession shutting down");
  }

  /**
     * Updates the current thread name (useful for debugging).
     */
  private void updateThreadName(String detail) {
    setName("NGSession " + instanceNumber + ": " + ((detail == null) ? "(idle)" : detail));
  }
}