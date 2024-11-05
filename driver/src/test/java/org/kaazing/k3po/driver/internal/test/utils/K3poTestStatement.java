package org.kaazing.k3po.driver.internal.test.utils;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.driver.internal.Robot;

public class K3poTestStatement extends Statement {
  private final Statement statement;

  private final Latch latch;

  private final List<String> scriptNames;

  private Robot robot;

  public K3poTestStatement(Statement statement, Latch latch, List<String> scriptNames) {
    this.latch = latch;
    this.statement = statement;
    this.scriptNames = scriptNames;
  }

  @Override public void evaluate() throws Throwable {
    robot = new Robot();
    ScriptTestRunner scriptRunner = new ScriptTestRunner(scriptNames, latch, robot);
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
      assertTrue(format("Did you call %s.finish()?", K3poTestRule.class.getSimpleName()), latch.isStartable());
      ScriptPair scripts = scriptFuture.get();
      String expectedScript = scripts.getExpectedScript();
      String observedScript = scripts.getObservedScript();
      assertEquals("Specified behavior did not match", expectedScript, observedScript);
    }  finally {
      scriptFuture.cancel(true);
      robot.destroy();
    }
  }

  public void awaitBarrier(String barrierName) throws Exception {
    robot.awaitBarrier(barrierName);
  }

  public void notifyBarrier(String barrierName) throws Exception {
    robot.notifyBarrier(barrierName);
  }
}