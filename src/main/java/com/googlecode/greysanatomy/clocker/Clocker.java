package com.googlecode.greysanatomy.clocker;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ��ʱ��
 *
 * @author vlinux
 */
public class Clocker implements Runnable {
  private final ReentrantLock lock = new ReentrantLock();

  private final Condition condition = lock.newCondition();

  private final AtomicLong timestamp = new AtomicLong(System.currentTimeMillis());

  private Clocker() {
    final Thread clockerT = new Thread(this, "ga-clocker");
    clockerT.setDaemon(true);
    clockerT.start();
  }

  @Override public void run() {
    while (true) {
      lock.lock();
      try {
        condition.await(1L, TimeUnit.MILLISECONDS);
        if (timestamp.incrementAndGet() % 1000 == 0) {
          timestamp.set(System.currentTimeMillis());
        }
      } catch (InterruptedException e) {
      } finally {
        lock.unlock();
      }
    }
  }

  /**
     * ��ȡ��ʱ����ǰʱ��
     *
     * @return
     */
  public long getCurrentTimeMillis() {
    return timestamp.get();
  }

  private static volatile Clocker clocker = new Clocker();

  /**
     * ��ȡ������clocker
     *
     * @return
     */
  public static Clocker current() {
    return clocker;
  }
}