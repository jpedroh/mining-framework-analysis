package org.kaazing.k3po.junit.rules;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.junit.rules.internal.ScriptPair;

final class SpecificationStatement extends Statement {
  private final Statement statement;

  private final Latch latch;

  private final ScriptRunner scriptRunner;

  SpecificationStatement(Statement statement, URL controlURL, List<String> scriptNames, Latch latch) {
    this.statement = statement;
    this.latch = latch;
    this.scriptRunner = new ScriptRunner(controlURL, scriptNames, latch);
  }

  @Override public void evaluate() throws Throwable {
    latch.setInterruptOnException(Thread.currentThread());
    FutureTask<ScriptPair> scriptFuture = new FutureTask<>(scriptRunner);
    try {
      new Thread(scriptFuture).start();
      latch.awaitPrepared();
      try {
        statement.evaluate();
      } catch (AssumptionViolatedException e) {
        if (!latch.isFinished()) {
          scriptRunner.abort();
        }
        throw e;
      } catch (Throwable cause) {
        if (latch.hasException()) {
          throw cause;
        } else {
          if (!latch.isFinished()) {
            scriptRunner.abort();
          }
          try {
            ScriptPair scripts = scriptFuture.get(5, SECONDS);
            try {
              assertEquals("Specified behavior did not match", scripts.getExpectedScript(), scripts.getObservedScript());
              throw cause;
            } catch (ComparisonFailure f) {
              f.initCause(cause);
              throw f;
            }
          } catch (ExecutionException ee) {
            throw ee.getCause().initCause(cause);
          } catch (Exception e) {
            throw cause;
          }
        }
      }
      String k3poSimpleName = K3poRule.class.getSimpleName();
      assertTrue(format("Did you instantiate %s with a @Rule and call %s.join()?", k3poSimpleName, k3poSimpleName), latch.isStartable());
      ScriptPair scripts = scriptFuture.get();
      assertEquals("Specified behavior did not match", scripts.getExpectedScript(), scripts.getObservedScript());
    }  finally {
      scriptFuture.cancel(true);
    }
  }

  public void awaitBarrier(String barrierName) throws InterruptedException {
    scriptRunner.awaitBarrier(barrierName);
  }

  public void notifyBarrier(String barrierName) throws InterruptedException {
    scriptRunner.notifyBarrier(barrierName);
  }
}