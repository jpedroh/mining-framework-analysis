package com.googlecode.greysanatomy.console.server;
import com.googlecode.greysanatomy.console.rmi.req.GaSession;
import com.googlecode.greysanatomy.exception.SessionTimeOutException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static com.googlecode.greysanatomy.probe.ProbeJobs.killJob;

/**
 * ����˵�ǰ������session��job������Ϣ
 *
 * @author chengtongda
 */
public class SessionJobsHolder {
  private static final Logger logger = LoggerFactory.getLogger("greysanatomy");

  private final static Map<Long, GaSession> sessionHolder = new ConcurrentHashMap<Long, GaSession>();

  static {
    Thread sessionHearCheck = new Thread("ga-console-server-heartCheck") {
      private final long tip = 3000;

      @Override public void run() {
        while (true) {
          try {
            Thread.sleep(tip);
          } catch (InterruptedException e) {
          }
          long currentMills = System.currentTimeMillis();
          Set<Long> deadSessionIds = new HashSet<Long>();
          for (GaSession session : sessionHolder.values()) {
            if (currentMills - session.getLastModified() > tip) {
              deadSessionIds.add(session.getSessionId());
            }
          }
          for (Long deadSessionId : deadSessionIds) {
            unRegistSession(deadSessionId);
          }
        }
      }
    };
    sessionHearCheck.setDaemon(true);
    sessionHearCheck.start();
  }

  /**
     * ע��һ���Ự
     */
  public static synchronized long registSession() {
    GaSession session = new GaSession();
    sessionHolder.put(session.getSessionId(), session);
    logger.info("regist session={}", session.getSessionId());
    return session.getSessionId();
  }

  /**
     * session����
     *
     * @param gaSessionId
     * @return falseΪsession��ʧЧ
     */
  public static synchronized boolean heartBeatSession(long gaSessionId) {
    GaSession holderSession = sessionHolder.get(gaSessionId);
    if (holderSession == null || !holderSession.isAlive()) {
      return false;
    }
    holderSession.setLastModified(System.currentTimeMillis());
    return true;
  }

  /**
     * ע��һ������
     *
     * @param gaSessionId
     * @param jobId
     */
  public static synchronized void unRegistJob(long gaSessionId, int jobId) {
    GaSession holderSession = sessionHolder.get(gaSessionId);
    if (holderSession != null) {
      final Iterator<Integer> it = holderSession.getJobIds().iterator();
      while (it.hasNext()) {
        final int id = it.next();
        if (id == jobId) {
          killJob(id);
          it.remove();
          logger.info("unRegist job={} for session={}", id, gaSessionId);
        }
      }
    }
  }

  /**
     * ע��һ��job
     *
     * @param sessionId
     * @param jobId
     * @throws SessionTimeOutException
     */
  public static synchronized void registJob(long sessionId, int jobId) throws SessionTimeOutException {
    GaSession holderSession = sessionHolder.get(sessionId);
    if (holderSession == null || !holderSession.isAlive()) {
      throw new SessionTimeOutException("session is not exsit!");
    }
    holderSession.getJobIds().add(jobId);
    logger.info("regist job={} for session={}", jobId, sessionId);
  }

  /**
     * ע��һ���Ự
     *
     * @param gaSessionId
     */
  public static synchronized void unRegistSession(long gaSessionId) {
    GaSession holderSession = sessionHolder.get(gaSessionId);
    if (holderSession == null) {
      return;
    }
    holderSession.setAlive(false);
    final Iterator<Integer> it = holderSession.getJobIds().iterator();
    while (it.hasNext()) {
      killJob(it.next());
    }
    sessionHolder.remove(gaSessionId);
    logger.info("unRegist session={}", gaSessionId);
  }
}