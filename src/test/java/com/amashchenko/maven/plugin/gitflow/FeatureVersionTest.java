package com.amashchenko.maven.plugin.gitflow;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class) public class FeatureVersionTest {
  private final String version;

  private final String featureName;

  private final String expectedVersion;

  public FeatureVersionTest(final String version, final String featureName, final String expectedVersion) {
    this.version = version;
    this.featureName = featureName;
    this.expectedVersion = expectedVersion;
  }

  @Parameters public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { "0.9-SNAPSHOT", "feature", "0.9-feature-SNAPSHOT" }, { "0.9-RC3-SNAPSHOT", "feature", "0.9-RC3-feature-SNAPSHOT" }, { "0.9", "feature", "0.9-feature" }, { "0.9-RC3", "feature", "0.9-RC3-feature" }, { "0.9-RC3", null, "0.9-RC3" } });
  }

  @Test public void testFeatureVersion() throws Exception {
    Assert.assertEquals(expectedVersion, new GitFlowVersionInfo(version, null).featureVersion(featureName));
  }
}