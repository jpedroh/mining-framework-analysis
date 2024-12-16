package org.concurrentunit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


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
 *
 * @unknown public void sleepShouldSupportAssertionErrors() throws Throwable {
new Thread(new Runnable() {
public void run() {
threadAssertTrue(true);
resume();
}
}).start();
threadWait(500);
}
</pre>
 * @author Jonathan Halterman
 */
public abstract class ConcurrentTestCase {
    private static final String TIMEOUT_MESSAGE = "Test timed out while waiting for an expected result";

    private final Thread mainThread;

    private AtomicInteger waitCount;

    private Throwable failure;

    /**
     * Creates a new ConcurrentTestCase object.
     */
    public ConcurrentTestCase() {
        mainThread = Thread.currentThread();
    }

    /**
<<<<<<< LEFT
   * Wait out termination of a thread pool or fail doing so. Waits 2500 ms for executor termination.
=======
   * Wait out termination of a thread pool or fail doing so. Waits {@code waitDuration}
   * {@code waitUnits} for executor termination.
>>>>>>> RIGHT
   * 
   * @param executor
   */
    public void joinPool(ExecutorService executor, long waitDuration, TimeUnit waitUnits) {
        try {
            executor.shutdown();
            assertTrue(executor.awaitTermination(waitDuration, waitUnits));
        } catch (java.lang.SecurityException ok) {
        } catch (java.lang.InterruptedException e) {
            fail("Unexpected InterruptedException");
        }
    }

    /**
     *
     *
     * @see org.junit.Assert#assertEquals(Object, Object)
     */
    public void threadAssertEquals(Object x, Object y) {
        try {
            assertEquals(x, y);
        } catch (java.lang.AssertionError e) {
            threadFail(e);
        }
    }

    /**
     *
     *
     * @see org.junit.Assert#assertFalse(boolean)
     */
    public void threadAssertFalse(boolean b) {
        try {
            assertFalse(b);
        } catch (java.lang.AssertionError e) {
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
     *
     *
     * @see org.junit.Assert#assertNull(Object)
     */
    public void threadAssertNull(Object x) {
        try {
            assertNull(x);
        } catch (java.lang.AssertionError e) {
            threadFail(e);
        }
    }

    /**
     *
     *
     * @see org.junit.Assert#assertTrue(boolean)
     */
    public void threadAssertTrue(boolean b) {
        try {
            assertTrue(b);
        } catch (java.lang.AssertionError e) {
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
<<<<<<< LEFT
   * Resumes a waiting test case.
   * 
   * <p>
   * Note: This method is likely not very useful since a concurrent run of a test case resulting in
   * the need to resume from a separate thread would yield no correlation between the initiating
   * thread and the thread where the resume call takes place.
=======
   * Resumes a waiting test case if {@code thread} is not the mainThread, the waitCount is null or
   * the decremented waitCount is 0.
   * 
   * <p>
   * Note: This method is likely not very useful to call directly since a concurrent run of a test
   * case resulting in the need to resume from a separate thread would yield no correlation between
   * the initiating thread and the thread where the resume call takes place.
>>>>>>> RIGHT
   * 
   * @param thread Thread to resume
   */
    protected void resume(Thread thread) {
        if (((thread != mainThread) || (waitCount == null)) || (waitCount.decrementAndGet() == 0)) {
            thread.interrupt();
        }
    }

    /**
<<<<<<< LEFT
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
=======
   * Sleeps until the {@code sleepDuration} has elapsed, {@link #resume()} is called, or the test is
   * failed.
   * 
   * @param sleepDuration
   * @throws TimeoutException if the sleep operation times out while waiting for a result
   * @throws Throwable the last reported test failure
>>>>>>> RIGHT
   */
    protected void sleep(long sleepDuration) throws Throwable {
        try {
            Thread.sleep(sleepDuration);
            throw new TimeoutException(TIMEOUT_MESSAGE);
        } catch (java.lang.InterruptedException ignored) {
        } finally {
            if (failure != null) {
                throw failure;
            }
        }
    }

    /**
     * Sleeps until the {@code sleepDuration} has elapsed, {@link #resume()} is called
     * {@code resumeThreshold} times, or the test is failed.
     *
     * @param waitTime
     * 		Time to wait
     * @param resumeCount
     * 		Number of times resume must be called before wait completes
     * @throws IllegalStateException
     * 		if called from outside the main test thread
     * @throws TimeoutException
     * 		if the wait operation times out while waiting for a result
     * @throws Throwable
     * 		the last reported test failure
     */
    protected void sleep(long sleepDuration, int resumeThreshold) throws Throwable {
        if (Thread.currentThread() != mainThread) {
            throw new IllegalStateException("Must be called from within the main test thread");
        }
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
        if (Thread.currentThread() != mainThread) {
            throw new IllegalStateException("Must be called from within the main test thread");
        }
        synchronized(this) {
            while (true) {
                try {
                    wait();
                    throw new TimeoutException(TIMEOUT_MESSAGE);
                } catch (java.lang.InterruptedException e) {
                    if (failure != null) {
                        throw failure;
                    }
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
<<<<<<< LEFT
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
=======
   * Waits until the {@code waitDuration} has elapsed, {@link #resume()} is called
   * {@code resumeThreshold} times, or the test is failed. Delegates to {@link #sleep(long, int)} to
   * avoid spurious wakeups.
   * 
   * @see #sleep(long, int)
>>>>>>> RIGHT
   */
    protected void threadWait(long waitDuration, int resumeThreshold) throws Throwable {
        if (waitDuration == 0) {
            waitCount = new AtomicInteger(resumeThreshold);
            threadWait();
            waitCount = null;
        } else {
            sleep(waitDuration, resumeThreshold);
        }
    }
}