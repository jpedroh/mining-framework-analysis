package org.concurrentunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concurrent test case.
 * 
 * <p>
 * Call {@link #sleep(long)}, {@link #sleep(long, int)}, {@link #threadWait(long)} or
 * {@link #threadWait(long, int)} from the main unit test thread to wait for some other thread to
 * perform assertions. These operations will block until {@link #resume()} is called, the operation
 * times out, or a threadAssert call fails.
 * 
 * <p>
 * The threadAssert methods can be used from any thread to perform concurrent assertions. Assertion
 * failures will result in the main thread being interrupted and the failure thrown.
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * @Test
 * public void assertAndResume() throws Throwable {
 *   new Thread(new Runnable() {
 *     public void run() {
 *       threadAssertTrue(true);
 *       resume();
 *     }
 *   }).start();
 *   
 *   sleep(500);
 * }
 * </pre>
 * 
 * @author Jonathan Halterman
 */
public abstract class ConcurrentTestCase {
  private static final String TIMEOUT_MESSAGE = "Test timed out while waiting for an expected result";
  private final Thread mainThread;
  private AtomicInteger waitCount;
  private Throwable failure;

<<<<<<< /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/left.java
  /**
   * Creates a new ConcurrentTestCase object.
   */
  public ConcurrentTestCase() {
    mainThread = Thread.currentThread();
  }

  /**
   * Resumes the main thread.
   */
  protected void resume() {
    resume(mainThread);
  }

  /**
   * Resumes a waiting test case.
   * 
   * <p>
   * Note: This method is likely not very useful since a concurrent run of a test case resulting in
   * the need to resume from a separate thread would yield no correlation between the initiating
   * thread and the thread where the resume call takes place.
   * 
   * @param thread Thread to resume
   */
  protected void resume(Thread thread) {
    if (thread != mainThread || waitCount == null || waitCount.decrementAndGet() == 0)
      thread.interrupt();
  }

  /**
   * Fails the current test for the given reason.
   */
  public void threadFail(String reason) {
    threadFail(new AssertionError(reason));
  }

  /**
   * Fails the current test for the given exception.
   */
  public void threadFail(Throwable e) {
    failure = e;
    resume();
  }

  /**
   * If expression not true, set status to indicate current testcase should fail
   */
  public void threadAssertTrue(boolean b) {
    try {
      assertTrue(b);
    } catch (AssertionError e) {
      threadFail(e);
||||||| /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/base.java
    /**
     * Creates a new ConcurrentTestCase object.
     */
    public ConcurrentTestCase() {
        mainThread = Thread.currentThread();
=======
  /**
   * Creates a new ConcurrentTestCase object.
   */
  public ConcurrentTestCase() {
    mainThread = Thread.currentThread();
  }

  /**
   * Wait out termination of a thread pool or fail doing so. Waits {@code waitDuration}
   * {@code waitUnits} for executor termination.
   * 
   * @param executor
   */
  public void joinPool(ExecutorService executor, long waitDuration, TimeUnit waitUnits) {
    try {
      executor.shutdown();
      assertTrue(executor.awaitTermination(waitDuration, waitUnits));
    } catch (SecurityException ok) {
    } catch (InterruptedException e) {
      fail("Unexpected InterruptedException");
>>>>>>> /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/right.java
    }
  }

<<<<<<< /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/left.java
  /**
   * If expression not false, set status to indicate current testcase should fail
   */
  public void threadAssertFalse(boolean b) {
    try {
      assertFalse(b);
    } catch (AssertionError e) {
      threadFail(e);
||||||| /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/base.java
    /**
     * Resumes the main thread.
     */
    protected void resume() {
        resume(mainThread);
=======
  /**
   * @see org.junit.Assert#assertEquals(Object, Object)
   */
  public void threadAssertEquals(Object x, Object y) {
    try {
      assertEquals(x, y);
    } catch (AssertionError e) {
      threadFail(e);
>>>>>>> /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/right.java
    }
  }

<<<<<<< /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/left.java
  /**
   * If argument not null, set status to indicate current testcase should fail
   */
  public void threadAssertNull(Object x) {
    try {
      assertNull(x);
    } catch (AssertionError e) {
      threadFail(e);
||||||| /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/base.java
    /**
     * Resumes a waiting test case.
     * 
     * <p>
     * Note: This method is likely not very useful since a concurrent run of a test case resulting
     * in the need to resume from a separate thread would yield no correlation between the
     * initiating thread and the thread where the resume call takes place.
     * 
     * @param thread Thread to resume
     */
    protected void resume(Thread thread) {
        if (thread != mainThread || waitCount == null || waitCount.decrementAndGet() == 0)
            thread.interrupt();
=======
  /**
   * @see org.junit.Assert#assertFalse(boolean)
   */
  public void threadAssertFalse(boolean b) {
    try {
      assertFalse(b);
    } catch (AssertionError e) {
      threadFail(e);
>>>>>>> /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/right.java
    }
  }

  /**
   * @see org.junit.Assert#assertNotNull(Object)
   */
  public void threadAssertNotNull(Object object) {
    try {
      assertNotNull(object);
    } catch (AssertionError e) {
      threadFail(e);
    }
  }

<<<<<<< /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/left.java
  /**
   * If arguments not equal, set status to indicate current testcase should fail
   */
  public void threadAssertEquals(Object x, Object y) {
    try {
      assertEquals(x, y);
    } catch (AssertionError e) {
      threadFail(e);
||||||| /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/base.java
    /**
     * Fails the current test for the given exception.
     */
    public void threadFail(Throwable e) {
        failure = e;
        resume();
=======
  /**
   * @see org.junit.Assert#assertNull(Object)
   */
  public void threadAssertNull(Object x) {
    try {
      assertNull(x);
    } catch (AssertionError e) {
      threadFail(e);
>>>>>>> /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/right.java
    }
  }

<<<<<<< /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/left.java
  /**
   * Wait out termination of a thread pool or fail doing so. Waits 2500 ms for executor termination.
   * 
   * @param executor
   */
  public void joinPool(ExecutorService executor) {
    try {
      executor.shutdown();
      assertTrue(executor.awaitTermination(2500, MILLISECONDS));
    } catch (SecurityException ok) {
    } catch (InterruptedException ie) {
      fail("Unexpected InterruptedException");
    }
  }

  /**
   * Sleep until the timeout has elapsed or interrupted and throws any exception that is set by any
   * other thread running within the context of this test.
   * 
   * <p>
   * Call {@link #resume()} to interrupt the sleep.
   * 
   * <p>
   * Note: A sleep time of 0 will sleep indefinitely. This is only recommended to use if you are
   * absolutely sure that {@link #resume()} will be called by some thread.
   * 
   * @param sleepTime
   * @throws Throwable If any exception occurs while sleeping
   * @throws TimeoutException If the sleep operation times out while waiting for a result
   */
  protected void sleep(long sleepTime) throws Throwable, TimeoutException {
    try {
      Thread.sleep(sleepTime);
      throw new TimeoutException(TIMEOUT_MESSAGE);
    } catch (InterruptedException e) {
    } finally {
      if (failure != null)
        throw failure;
    }
  }

  /**
   * Waits until resume is called {@code pResumeCount} times.
   * 
   * @param waitTime Time to wait
   * @param resumeCount Number of times resume must be called before wait completes
   * @throws IllegalStateException if called from outside the main test thread
   * @throws TimeoutException if the wait operation times out while waiting for a result
   */
  protected void threadWait(long waitTime, int resumeCount) throws Throwable {
    if (Thread.currentThread() != mainThread)
      throw new IllegalStateException("Must be called from within the main test thread");

    waitCount = new AtomicInteger(resumeCount);

    threadWait(waitTime);
    waitCount = null;
  }

  /**
   * Waits till the wait time has elapsed or the test case's monitor is interrupted, and throws any
   * exception that is set by any other thread running within the context of this test.
   * 
   * <p>
   * Call {@link #finish()} to interrupt the wait.
   * 
   * <p>
   * Note: A wait time of 0 will wait indefinitely. This is only recommended to use if you are
   * absolutely sure that {@link #finish()} will be called by some thread.
   * 
   * @param waitTime Time to wait
   * @throws Throwable If any exception occurs while waiting
   * @throws TimeoutException if the wait operation times out while waiting for a result
   */
  protected void threadWait(long waitTime) throws Throwable, TimeoutException {
    synchronized (this) {
      try {
        wait(waitTime);
        throw new TimeoutException(TIMEOUT_MESSAGE);
      } catch (InterruptedException e) {
      } finally {
        if (failure != null)
          throw failure;
      }
    }
  }
||||||| /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/base.java
    /**
     * If expression not true, set status to indicate current testcase should fail
     */
    public void threadAssertTrue(boolean b) {
        try {
            assertTrue(b);
        } catch (AssertionError e) {
            threadFail(e);
        }
    }

    /**
     * If expression not false, set status to indicate current testcase should fail
     */
    public void threadAssertFalse(boolean b) {
        try {
            assertFalse(b);
        } catch (AssertionError e) {
            threadFail(e);
        }
    }

    /**
     * If argument not null, set status to indicate current testcase should fail
     */
    public void threadAssertNull(Object x) {
        try {
            assertNull(x);
        } catch (AssertionError e) {
            threadFail(e);
        }
    }

    /**
     * @see org.junit.Assert#assertNotNull(Object)
     */
    public void threadAssertNotNull(Object object) {
        try {
            assertNotNull(object);
        } catch (AssertionError e) {
            threadFail(e);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase should fail
     */
    public void threadAssertEquals(Object x, Object y) {
        try {
            assertEquals(x, y);
        } catch (AssertionError e) {
            threadFail(e);
        }
    }

    /**
     * Wait out termination of a thread pool or fail doing so. Waits 2500 ms for executor
     * termination.
     * 
     * @param executor
     */
    public void joinPool(ExecutorService executor) {
        try {
            executor.shutdown();
            assertTrue(executor.awaitTermination(2500, MILLISECONDS));
        } catch (SecurityException ok) {
        } catch (InterruptedException ie) {
            fail("Unexpected InterruptedException");
        }
    }

    /**
     * Sleep until the timeout has elapsed or interrupted and throws any exception that is set by
     * any other thread running within the context of this test.
     * 
     * <p>
     * Call {@link #resume()} to interrupt the sleep.
     * 
     * <p>
     * Note: A sleep time of 0 will sleep indefinitely. This is only recommended to use if you are
     * absolutely sure that {@link #resume()} will be called by some thread.
     * 
     * @param sleepTime
     * @throws Throwable If any exception occurs while sleeping
     * @throws TimeoutException If the sleep operation times out while waiting for a result
     */
    protected void sleep(long sleepTime) throws Throwable, TimeoutException {
        try {
            Thread.sleep(sleepTime);
            throw new TimeoutException(TIMEOUT_MESSAGE);
        } catch (InterruptedException e) {
        } finally {
            if (failure != null)
                throw failure;
        }
    }

    /**
     * Waits until resume is called {@code pResumeCount} times.
     * 
     * @param waitTime Time to wait
     * @param resumeCount Number of times resume must be called before wait completes
     * @throws IllegalStateException if called from outside the main test thread
     * @throws TimeoutException if the wait operation times out while waiting for a result
     */
    protected void threadWait(long waitTime, int resumeCount) throws Throwable {
        if (Thread.currentThread() != mainThread)
            throw new IllegalStateException("Must be called from within the main test thread");

        waitCount = new AtomicInteger(resumeCount);

        threadWait(waitTime);
        waitCount = null;
    }

    /**
     * Waits till the wait time has elapsed or the test case's monitor is interrupted, and throws
     * any exception that is set by any other thread running within the context of this test.
     * 
     * <p>
     * Call {@link #finish()} to interrupt the wait.
     * 
     * <p>
     * Note: A wait time of 0 will wait indefinitely. This is only recommended to use if you are
     * absolutely sure that {@link #finish()} will be called by some thread.
     * 
     * @param waitTime Time to wait
     * @throws Throwable If any exception occurs while waiting
     * @throws TimeoutException if the wait operation times out while waiting for a result
     */
    protected void threadWait(long waitTime) throws Throwable, TimeoutException {
        synchronized (this) {
            try {
                wait(waitTime);
                throw new TimeoutException(TIMEOUT_MESSAGE);
            } catch (InterruptedException e) {
            } finally {
                if (failure != null)
                    throw failure;
            }
        }
    }
=======
  /**
   * @see org.junit.Assert#assertTrue(boolean)
   */
  public void threadAssertTrue(boolean b) {
    try {
      assertTrue(b);
    } catch (AssertionError e) {
      threadFail(e);
    }
  }

  /**
   * Fails the current test for the given reason.
   */
  public void threadFail(String reason) {
    threadFail(new AssertionError(reason));
  }

  /**
   * Fails the current test with the given Throwable.
   */
  public void threadFail(Throwable e) {
    failure = e;
    resume(mainThread);
  }

  /**
   * Resumes the main test thread.
   */
  protected void resume() {
    resume(mainThread);
  }

  /**
   * Resumes a waiting test case if {@code thread} is not the mainThread, the waitCount is null or
   * the decremented waitCount is 0.
   * 
   * <p>
   * Note: This method is likely not very useful to call directly since a concurrent run of a test
   * case resulting in the need to resume from a separate thread would yield no correlation between
   * the initiating thread and the thread where the resume call takes place.
   * 
   * @param thread Thread to resume
   */
  protected void resume(Thread thread) {
    if (thread != mainThread || waitCount == null || waitCount.decrementAndGet() == 0)
      thread.interrupt();
  }

  /**
   * Sleeps until the {@code sleepDuration} has elapsed, {@link #resume()} is called, or the test is
   * failed.
   * 
   * @param sleepDuration
   * @throws TimeoutException if the sleep operation times out while waiting for a result
   * @throws Throwable the last reported test failure
   */
  protected void sleep(long sleepDuration) throws Throwable {
    try {
      Thread.sleep(sleepDuration);
      throw new TimeoutException(TIMEOUT_MESSAGE);
    } catch (InterruptedException ignored) {
    } finally {
      if (failure != null)
        throw failure;
    }
  }

  /**
   * Sleeps until the {@code sleepDuration} has elapsed, {@link #resume()} is called
   * {@code resumeThreshold} times, or the test is failed.
   * 
   * @param sleepDuration Duration to sleep
   * @param resumeThreshold Number of times resume must be called before sleep is interrupted
   * @throws IllegalStateException if called from outside the main test thread
   * @throws TimeoutException if the sleep operation times out while waiting for a result
   * @throws Throwable the last reported test failure
   */
  protected void sleep(long sleepDuration, int resumeThreshold) throws Throwable {
    if (Thread.currentThread() != mainThread)
      throw new IllegalStateException("Must be called from within the main test thread");

    waitCount = new AtomicInteger(resumeThreshold);
    sleep(sleepDuration);
    waitCount = null;
  }

  /**
   * Alias for {@link #resume()} for use within the context of a Thread instance.
   */
  protected void threadResume() {
    resume(mainThread);
  }

  /**
   * Waits until {@link #resume()} is called, or the test is failed.
   * 
   * @throws IllegalStateException if called from outside the main test thread
   * @throws Throwable the last reported test failure
   */
  protected void threadWait() throws Throwable {
    if (Thread.currentThread() != mainThread)
      throw new IllegalStateException("Must be called from within the main test thread");

    synchronized (this) {
      while (true) {
        try {
          wait();
          throw new TimeoutException(TIMEOUT_MESSAGE);
        } catch (InterruptedException e) {
          if (failure != null)
            throw failure;
          break;
        }
      }
    }
  }

  /**
   * Waits until the {@code waitDuration} has elapsed, {@link #resume()} is called, or the test is
   * failed. Delegates to {@link #sleep(long)} to avoid spurious wakeups.
   * 
   * @see #sleep(long)
   */
  protected void threadWait(long waitDuration) throws Throwable {
    if (waitDuration == 0)
      threadWait();
    else
      sleep(waitDuration);
  }

  /**
   * Waits until the {@code waitDuration} has elapsed, {@link #resume()} is called
   * {@code resumeThreshold} times, or the test is failed. Delegates to {@link #sleep(long, int)} to
   * avoid spurious wakeups.
   * 
   * @see #sleep(long, int)
   */
  protected void threadWait(long waitDuration, int resumeThreshold) throws Throwable {
    if (waitDuration == 0) {
      waitCount = new AtomicInteger(resumeThreshold);
      threadWait();
      waitCount = null;
    } else
      sleep(waitDuration, resumeThreshold);
  }
>>>>>>> /usr/src/app/output/jhalterman/concurrentunit/ff9794f7485bb6455f5360b989c22f3173b679a6/src/main/java/org/concurrentunit/ConcurrentTestCase.java/right.java
}
