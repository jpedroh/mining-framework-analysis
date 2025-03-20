package com.googlecode.greysanatomy.console.client;
import com.googlecode.greysanatomy.Configer;
import com.googlecode.greysanatomy.console.GreysAnatomyConsole;
import com.googlecode.greysanatomy.console.rmi.req.ReqHeart;
import com.googlecode.greysanatomy.console.server.ConsoleServerService;
import com.googlecode.greysanatomy.exception.ConsoleException;
import com.googlecode.greysanatomy.exception.PIDNotMatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.rmi.Naming;

/**
 * ����̨�ͻ���
 *
 * @author chengtongda
 */
public class ConsoleClient {
  private static final Logger logger = LoggerFactory.getLogger("greysanatomy");

  private final ConsoleServerService consoleServer;

  private final long sessionId;

  private ConsoleClient(Configer configer) throws Exception {
    this.consoleServer = (ConsoleServerService) Naming.lookup(String.format("rmi://%s:%d/RMI_GREYS_ANATOMY", configer.getTargetIp(), configer.getTargetPort()));
    if (!consoleServer.checkPID(configer.getJavaPid())) {
      throw new PIDNotMatchException();
    }
    this.sessionId = this.consoleServer.register();
    new GreysAnatomyConsole(configer, sessionId).start(consoleServer);
    heartBeat();
  }

  /**
     * ������������߳�
     */
  private void heartBeat() {
    Thread heartBeatDaemon = new Thread("ga-console-client-heartbeat") {
      @Override public void run() {
        while (true) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
          if (null == consoleServer) {
            logger.info("disconnect to ga-console-server, shutdown jvm.");
            System.exit(0);
            break;
          } else {
            boolean hearBeatResult = false;
            try {
              hearBeatResult = consoleServer.sessionHeartBeat(new ReqHeart(sessionId));
            } catch (Exception e) {
            }
            if (!hearBeatResult) {
              logger.info("session time out to ga-console-server, shutdown jvm.");
              System.exit(0);
              break;
            }
          }
        }
      }
    };
    heartBeatDaemon.setDaemon(true);
    heartBeatDaemon.start();
  }

  private static volatile ConsoleClient instance;

  /**
     * ��������̨�ͻ���
     *
     * @param configer
     * @throws ConsoleException
     * @throws IOException
     */
  public static synchronized void getInstance(Configer configer) throws Exception {
    if (null == instance) {
      instance = new ConsoleClient(configer);
    }
  }
}