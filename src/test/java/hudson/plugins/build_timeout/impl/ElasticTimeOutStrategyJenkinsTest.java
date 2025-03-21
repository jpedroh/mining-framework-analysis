package hudson.plugins.build_timeout.impl;
import static org.junit.Assert.*;
import java.util.Arrays;
import hudson.model.FreeStyleBuild;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.plugins.build_timeout.BuildTimeOutOperation;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;
import hudson.plugins.build_timeout.operations.AbortOperation;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.CaptureEnvironmentBuilder;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests for {@link ElasticTimeOutStrategy} using Jenkins
 */
public class ElasticTimeOutStrategyJenkinsTest {
  @Rule public JenkinsRule j = new JenkinsRule();

  @Test public void canConfigureWithWebPage() throws Exception {
    FreeStyleProject p = j.createFreeStyleProject();
    p.getBuildWrappersList().add(new BuildTimeoutWrapper(new ElasticTimeOutStrategy("300", "3", "10"), Arrays.<BuildTimeOutOperation>asList(new AbortOperation()), null));
    p.save();
    String projectName = p.getFullName();
    {
      ElasticTimeOutStrategy strategy = (ElasticTimeOutStrategy) p.getBuildWrappersList().get(BuildTimeoutWrapper.class).getStrategy();
      assertEquals("300", strategy.getTimeoutPercentage());
      assertEquals("3", strategy.getTimeoutMinutesElasticDefault());
      assertEquals("10", strategy.getNumberOfBuilds());
    }
    WebClient wc = j.createWebClient();
    HtmlPage page = wc.getPage(p, "configure");
    HtmlForm form = page.getFormByName("config");
    j.submit(form);
    p = j.jenkins.getItemByFullName(projectName, FreeStyleProject.class);
    {
      ElasticTimeOutStrategy strategy = (ElasticTimeOutStrategy) p.getBuildWrappersList().get(BuildTimeoutWrapper.class).getStrategy();
      assertEquals("300", strategy.getTimeoutPercentage());
      assertEquals("3", strategy.getTimeoutMinutesElasticDefault());
      assertEquals("10", strategy.getNumberOfBuilds());
    }
  }


<<<<<<< /usr/src/app/output/jenkinsci/build-timeout-plugin/d51178be1cd90cf9173edd1705a7bf0dc75ab16d/src/test/java/hudson/plugins/build_timeout/impl/ElasticTimeOutStrategyJenkinsTest.java/left.java
  @Test public void testFailSafeTimeoutWithVariable() throws Exception {
    FreeStyleProject p = j.createFreeStyleProject();
    p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("FailSafeTimeout", null)));
    p.getBuildWrappersList().add(new BuildTimeoutWrapper(new ElasticTimeOutStrategy("200", "${FailSafeTimeout}", "3", true), null, "TIMEOUT"));
    CaptureEnvironmentBuilder ceb = new CaptureEnvironmentBuilder();
    p.getBuildersList().add(ceb);
    FreeStyleBuild b = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("FailSafeTimeout", "30", ""))));
    assertEquals("1800000", ceb.getEnvVars().get("TIMEOUT"));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Test public void failSafeTimeoutWithVariable() throws Exception {
    FreeStyleProject p = j.createFreeStyleProject();
    p.getBuildWrappersList().add(new BuildTimeoutWrapper(new ElasticTimeOutStrategy("200", "${FailSafeTimeout}", "3", true), null, "TIMEOUT"));
    CaptureEnvironmentBuilder ceb = new CaptureEnvironmentBuilder();
    p.getBuildersList().add(ceb);
    FreeStyleBuild b = j.assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserIdCause(), new ParametersAction(new StringParameterValue("FailSafeTimeout", "30", ""))));
    assertEquals("1800000", ceb.getEnvVars().get("TIMEOUT"));
  }
}