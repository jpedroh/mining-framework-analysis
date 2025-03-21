package hudson.plugins.copyartifact;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import org.jvnet.hudson.test.recipes.LocalData;
import hudson.XmlFile;
import hudson.model.Saveable;
import hudson.model.User;
import hudson.model.listeners.SaveableListener;

/**
 * Tests for {@link CopyArtifactConfiguration}
 */
public class CopyArtifactConfigurationTest {
  @Rule public JenkinsRule j = new JenkinsRule();

  @Test public void configProduction() throws Exception {
    CopyArtifactConfiguration config = CopyArtifactConfiguration.get();
    config.setMode(CopyArtifactCompatibilityMode.PRODUCTION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
    HtmlForm form = j.createWebClient().goTo("configureSecurity").getFormByName("config");
    config.setMode(CopyArtifactCompatibilityMode.MIGRATION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.MIGRATION));
    j.submit(form);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
  }

  @Test public void configMigration() throws Exception {
    CopyArtifactConfiguration config = CopyArtifactConfiguration.get();
    config.setMode(CopyArtifactCompatibilityMode.MIGRATION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.MIGRATION));
    HtmlForm form = j.createWebClient().goTo("configureSecurity").getFormByName("config");
    config.setMode(CopyArtifactCompatibilityMode.PRODUCTION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
    j.submit(form);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.MIGRATION));
  }

  @Test public void productionMode_forFresh() throws Exception {
    CopyArtifactConfiguration config = CopyArtifactConfiguration.get();
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
    assertFalse(config.isFirstLoad());
    config.load();
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
    assertFalse(config.isFirstLoad());
    config.setMode(CopyArtifactCompatibilityMode.MIGRATION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.MIGRATION));
    config.setToFirstLoad();
    config.load();
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
  }

  @Test @Ignore(value = "No way to detect we are in a new version of the plugin within a test") public void migrationMode_forUpgrade() throws Exception {
    CopyArtifactConfiguration config = CopyArtifactConfiguration.get();
    config.setMode(CopyArtifactCompatibilityMode.PRODUCTION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
    config.setToFirstLoad();
    config.load();
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.MIGRATION));
  }

  @Ignore(value = "Currently fails with circular dependency error") @Issue(value = "JENKINS-62267") @LocalData @Test public void circularDependencyTest() throws Exception {
    assertNotNull(CopyArtifactConfiguration.get());
  }

  @Test public void productionMode_storedToTheDisk() throws Exception {
    CopyArtifactConfiguration config = CopyArtifactConfiguration.get();
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.PRODUCTION));
    assertFalse(config.isFirstLoad());
    config.setModeWithoutSave(CopyArtifactCompatibilityMode.MIGRATION);
    assertThat(config.getMode(), Matchers.is(CopyArtifactCompatibilityMode.MIGRATION));
    config.load();
  }

  @Issue(value = "JENKINS-62267") @Test public void circularDependencyTestWithSavableListener() throws Exception {
    assertNotNull(CopyArtifactConfiguration.get());
  }

  @TestExtension(value = "circularDependencyTestWithSavableListener") public static class LoadingExtensionFinderSavableListener extends SaveableListener {
    Logger LOG = Logger.getLogger(LoadingExtensionFinderSavableListener.class.getName());

    @Override public void onChange(Saveable config, XmlFile file) {
      User user = User.current();
      LOG.log(Level.INFO, "LoadingExtensionFinderSavableListener#onChange with: {0}", (user != null) ? user.getId() : "NULL");
    }
  }
}