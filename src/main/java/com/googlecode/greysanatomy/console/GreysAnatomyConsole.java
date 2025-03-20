package com.googlecode.greysanatomy.console;
import com.googlecode.greysanatomy.Configer;
import com.googlecode.greysanatomy.console.command.Command;
import com.googlecode.greysanatomy.console.command.Commands;
import com.googlecode.greysanatomy.console.command.QuitCommand;
import com.googlecode.greysanatomy.console.command.ShutdownCommand;
import com.googlecode.greysanatomy.console.rmi.RespResult;
import com.googlecode.greysanatomy.console.rmi.req.ReqCmd;
import com.googlecode.greysanatomy.console.rmi.req.ReqGetResult;
import com.googlecode.greysanatomy.console.rmi.req.ReqKillJob;
import com.googlecode.greysanatomy.console.server.ConsoleServerService;
import com.googlecode.greysanatomy.exception.ConsoleException;
import com.googlecode.greysanatomy.util.GaStringUtils;
import jline.console.ConsoleReader;
import jline.console.KeyMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.NoSuchObjectException;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * ����̨
 *
 * @author vlinux
 */
public class GreysAnatomyConsole {
  private static final Logger logger = LoggerFactory.getLogger("greysanatomy");

  private final Configer configer;

  private final ConsoleReader console;

  private volatile boolean isF = true;

  private volatile boolean 
<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
  isShutdown = false
=======
  isQuit = false
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java
  ;

  private final long sessionId;

  private int jobId;

  /**
     * ����GA����̨
     *
     * @param configer
     * @throws IOException
     */
  public GreysAnatomyConsole(Configer configer, long sessionId) throws IOException {
    this.console = new ConsoleReader(System.in, System.out);
    this.configer = configer;
    this.sessionId = sessionId;
    write(GaStringUtils.getLogo());
    Commands.getInstance().registCompleter(console);
  }

  private class GaConsoleInputer implements Runnable {
    private final ConsoleServerService consoleServer;

    private GaConsoleInputer(ConsoleServerService consoleServer) {
      this.consoleServer = consoleServer;
    }

    @Override public void run() {
      while (true) {
        try {
          doRead();
        } catch (ConsoleException ce) {
          write("Error : " + ce.getMessage() + "\n");
          write("Please type help for more information...\n\n");
        } catch (Exception e) {
          logger.warn("console read failed.", e);
        }
      }
    }

    private void doRead() throws Exception {
      final String prompt = isF ? configer.getConsolePrompt() : EMPTY;
      final ReqCmd reqCmd = new ReqCmd(console.readLine(prompt), sessionId);
      if (isBlank(reqCmd.getCommand()) || !isF) {
        return;
      }
      final Command command;
      try {
        command = Commands.getInstance().newRiscCommand(reqCmd.getCommand());
      } catch (Exception e) {
        throw new ConsoleException(e.getMessage());
      }
      isF = false;

<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
      if (command instanceof ShutdownCommand) {
        isShutdown = true;
      }
=======
      if (command instanceof ShutdownCommand || command instanceof QuitCommand) {
        isQuit = true;
      }
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java

      RespResult result = consoleServer.postCmd(reqCmd);
      jobId = result.getJobId();
    }
  }

  private class GaConsoleOutputer implements Runnable {
    private final ConsoleServerService consoleServer;

    private int currentJob;

    private int pos = 0;

    private GaConsoleOutputer(ConsoleServerService consoleServer) {
      this.consoleServer = consoleServer;
    }

    @Override public void run() {
      while (true) {
        try {
          doWrite();
          Thread.sleep(500);
        } catch (NoSuchObjectException nsoe) {
          logger.warn("target RMI\'s server was closed, console will be exit.");
          break;
        } catch (Exception e) {
          logger.warn("console write failed.", e);
        }
      }
    }

    private void doWrite() throws Exception {
      if (isF || sessionId == 0 || jobId == 0) {
        return;
      }
      if (currentJob != jobId) {
        pos = 0;
        currentJob = jobId;
      }
      RespResult resp = consoleServer.getCmdExecuteResult(new ReqGetResult(jobId, sessionId, pos));
      pos = resp.getPos();

<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
      try {
        writeToFile(resp.getMessage(), path);
      } catch (IOException e) {
        consoleServer.killJob(new ReqKillJob(sessionId, jobId));
        isF = true;
        logger.warn("writeToFile failed.", e);
        write(path + ":" + e.getMessage());
        return;
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      write(resp);
      if (
<<<<<<< /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/left.java
      isShutdown
=======
      isQuit
>>>>>>> /usr/src/app/output/oldmanpushcart/greys-anatomy/26f8fde516d364d9aadbda98ad78f6c4255470e3/src/main/java/com/googlecode/greysanatomy/console/GreysAnatomyConsole.java/right.java
      ) {
        logger.info("greys console will be shutdown.");
        System.exit(0);
      }
    }
  }

  /**
     * ����console
     *
     * @param consoleServer
     */
  public synchronized void start(final ConsoleServerService consoleServer) {
    this.console.getKeys().bind("" + KeyMap.CTRL_D, new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {
        if (!isF) {
          try {
            isF = true;
            write("abort it.\n");
            redrawLine();
            consoleServer.killJob(new ReqKillJob(sessionId, jobId));
          } catch (Exception e1) {
            logger.warn("killJob failed.", e);
          }
        }
      }
    });
    new Thread(new GaConsoleInputer(consoleServer), "ga-console-inputer").start();
    new Thread(new GaConsoleOutputer(consoleServer), "ga-console-outputer").start();
  }

  private synchronized void redrawLine() throws IOException {
    final String prompt = isF ? configer.getConsolePrompt() : EMPTY;
    console.setPrompt(prompt);
    console.redrawLine();
    console.flush();
  }

  /**
     * �����̨���������Ϣ
     *
     * @param resp
     */
  private void write(RespResult resp) throws IOException {
    if (!isF) {
      String content = resp.getMessage();
      if (resp.isFinish()) {
        isF = true;
        content += "\n";
      }
      if (!StringUtils.isEmpty(content)) {
        write(content);
        redrawLine();
      }
    }
  }

  /**
     * �����Ϣ
     *
     * @param message
     */
  private void write(String message) {
    final Writer writer = console.getOutput();
    try {
      writer.write(message);
      writer.flush();
    } catch (IOException e) {
      logger.warn("console write failed.", e);
    }
  }
}