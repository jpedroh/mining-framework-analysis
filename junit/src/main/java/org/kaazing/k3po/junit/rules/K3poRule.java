package org.kaazing.k3po.junit.rules;
import static java.lang.String.format;
import static org.junit.Assert.assertTrue;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.junit.rules.Verifier;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.net.URLFactory;

public class K3poRule extends Verifier {
  static {
    JUnitCore core = new JUnitCore();
    String version = core.getVersion();
    String[] versionTokens = version.split("\\.");
    Integer[] versionsInt = new Integer[versionTokens.length];
    for (int i = 0; i < versionTokens.length; i++) {
      String versionToken = versionTokens[i];
      if (versionToken.contains("-")) {
        versionToken = versionToken.substring(0, versionToken.indexOf("-"));
      }
      versionsInt[i] = Integer.parseInt(versionToken);
    }
    if (versionsInt[0] < 5) {
      if (versionsInt.length == 1 || versionsInt[0] < 4 || versionsInt[1] < 10) {
        throw new AssertionError("JUnit library 4.10+ required. Found version " + version);
      }
    }
  }

  private final Latch latch;

  private String scriptRoot;

  private URL controlURL;

  private SpecificationStatement statement;

  public K3poRule() {
    latch = new Latch();
  }

  public K3poRule setScriptRoot(String scriptRoot) {
    this.scriptRoot = scriptRoot;
    return this;
  }

  public K3poRule setControlURI(URI controlURI) {
    this.controlURL = createURL(controlURI.toString());
    return this;
  }

  @Override public Statement apply(Statement statement, final Description description) {
    Specification specification = description.getAnnotation(Specification.class);
    String[] scripts = (specification != null) ? specification.value() : null;
    if (scripts != null) {
      String packagePath = this.scriptRoot;
      if (packagePath == null) {
        Class<?> testClass = description.getTestClass();
        String packageName = testClass.getPackage().getName();
        packagePath = packageName.replaceAll("\\.", "/");
      }
      List<String> scriptNames = new LinkedList<>();
      for (String script : scripts) {
        if (script.startsWith("/")) {
          throw new IllegalArgumentException("Script path must be relative");
        }
        String scriptName = format("%s/%s", packagePath, script);
        scriptNames.add(scriptName);
      }
      URL controlURL = this.controlURL;
      if (controlURL == null) {
        controlURL = createURL("tcp://localhost:11642");
      }
      this.statement = new SpecificationStatement(statement, controlURL, scriptNames, latch);
      statement = this.statement;
    }
    return super.apply(statement, description);
  }

  public void start() {
    assertTrue(format("Did you call start() from outside @%s test?", Specification.class.getSimpleName()), latch.isPrepared());
    latch.notifyStartable();
  }

  public void finish() throws Exception {
    assertTrue(format("Did you call finish() from outside @%s test?", Specification.class.getSimpleName()), !latch.isInInitState());
    latch.notifyStartable();
    latch.awaitFinished();
  }

  private static URL createURL(String location) {
    try {
      return URLFactory.createURL("tcp://localhost:11642");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public void awaitBarrier(String barrierName) throws InterruptedException {
    statement.awaitBarrier(barrierName);
  }

  public void notifyBarrier(String barrierName) throws InterruptedException {
    statement.notifyBarrier(barrierName);
  }
}