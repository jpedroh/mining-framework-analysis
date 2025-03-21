package hudson.plugins.build_timeout.operations;
import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SleepBuilder;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.plugins.build_timeout.BuildTimeOutOperation;
import hudson.plugins.build_timeout.QuickBuildTimeOutStrategy;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;

/**
 *
 */
public class WriteDescriptionOperationTest {
  @Rule public JenkinsRule j = new JenkinsRule();

  @Before public void setUp() {
    BuildTimeoutWrapper.MINIMUM_TIMEOUT_MILLISECONDS = 0;
  }

  @Test public void setDescription() throws Exception {
    final String DESCRIPTION = "description to test: {0}, {0}.";
    final String EXPECTED = "description to test: 0, 0.";
    FreeStyleProject p = j.createFreeStyleProject();
    p.getBuildWrappersList().add(new BuildTimeoutWrapper(new QuickBuildTimeOutStrategy(5000), Arrays.asList(new WriteDescriptionOperation(DESCRIPTION), new AbortOperation())));
    p.getBuildersList().add(new SleepBuilder(10000));
    FreeStyleBuild b = p.scheduleBuild2(0).get();
    j.assertBuildStatus(Result.ABORTED, b);
    assertEquals(EXPECTED, b.getDescription());
  }

  @Test public void setDescriptionWithoutAborting() throws Exception {
    final String DESCRIPTION = "description to test: {0}, {0}.";
    final String EXPECTED = "description to test: 0, 0.";
    FreeStyleProject p = j.createFreeStyleProject();
    p.getBuildWrappersList().add(new BuildTimeoutWrapper(new QuickBuildTimeOutStrategy(5000), Arrays.<BuildTimeOutOperation>asList(new WriteDescriptionOperation(DESCRIPTION))));
    p.getBuildersList().add(new SleepBuilder(10000));
    FreeStyleBuild b = p.scheduleBuild2(0).get();
    j.assertBuildStatusSuccess(b);
    assertEquals(EXPECTED, b.getDescription());
  }

  @Test public void setDescriptionTwice() throws Exception {
    final String DESCRIPTION1 = "description to test: {0}, {0}.";
    final String DESCRIPTION2 = "Another message.";
    final String EXPECTED = "Another message.";
    FreeStyleProject p = j.createFreeStyleProject();
    p.getBuildWrappersList().add(new BuildTimeoutWrapper(new QuickBuildTimeOutStrategy(5000), Arrays.asList(new WriteDescriptionOperation(DESCRIPTION1), new AbortOperation(), new WriteDescriptionOperation(DESCRIPTION2))));
    p.getBuildersList().add(new SleepBuilder(10000));
    FreeStyleBuild b = p.scheduleBuild2(0).get();
    j.assertBuildStatus(Result.ABORTED, b);
    assertEquals(EXPECTED, b.getDescription());
  }
}