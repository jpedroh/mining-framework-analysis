package com.jcabi.github.mock;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Test case for {@link MkStorage}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings(value = { "PMD.DoNotUseThreads" }) public final class MkStorageTest {
  /**
     * MkStorage can text and write.
     * @throws Exception If some problem inside
     */
  @Test public void readsAndWrites() throws Exception {
    final MkStorage storage = new MkStorage.InFile();
    storage.lock();
    try {
      storage.apply(new Directives().xpath("/github").add("test").set("hello, world"));
      MatcherAssert.assertThat(storage.xml().xpath("/github/test/text()").get(0), Matchers.endsWith(", world"));
    }  finally {
      storage.unlock();
    }
  }

  /**
     * MkStorage can lock and unlock files.
     * @throws Exception If some problem inside
     */
  @Test public void locksAndUnlocks() throws Exception {
    final MkStorage storage = new MkStorage.InFile();
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Runnable second = new Runnable() {
      @Override public void run() {
        try {
          storage.lock();
        } catch (final IOException ex) {
          throw new IllegalStateException(ex);
        }
      }
    };
    storage.lock();
    Future<?> future = executor.submit(second);
    try {
      future.get(1, TimeUnit.SECONDS);
      MatcherAssert.assertThat("timeout SHOULD happen", false);
    } catch (final TimeoutException ex) {
      future.cancel(true);
    } finally {
      storage.unlock();
    }
    future = executor.submit(second);
    try {
      future.get(1, TimeUnit.SECONDS);
    } catch (final TimeoutException ex) {
      MatcherAssert.assertThat("timeout SHOULD NOT happen", false);
      future.cancel(true);
    }
    executor.shutdown();
  }

  /**
     * MkStorage should require lock on document reading.
     * @todo #745:30min Update 2 tests to check behaviour in multi-threading
     *  environment. Remove this comment and @Ignore annotation.
     * @throws Exception If some problem inside
     */
  @Test(expected = ConcurrentModificationException.class) @Ignore public void xmlRequiresLock() throws Exception {
    new MkStorage.InFile().xml();
  }

  /**
     * MkStorage should require lock on document change.
     * @throws Exception If some problem inside
     */
  @Test(expected = ConcurrentModificationException.class) @Ignore public void applyRequiresLock() throws Exception {
    new MkStorage.InFile().apply(new Directives().xpath("/github").add("test").set("hello, world"));
  }
}