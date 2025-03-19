package org.sonar.plugins.findbugs;
import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import org.sonar.api.utils.Version;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FindbugsPluginTest {
  @ParameterizedTest @CsvSource(value = { "9.7,24", "9.8,25" }) void testGetExtensions(String version, int expectedExtensionsCount) {
    SonarRuntime runtime = mock(SonarRuntime.class);
    when(runtime.getApiVersion()).thenReturn(Version.parse(version));
    Plugin.Context ctx = new Plugin.Context(runtime);
    FindbugsPlugin plugin = new FindbugsPlugin();
    plugin.define(ctx);
    assertEquals(
<<<<<<< /usr/src/app/output/sonarsource/sonar-findbugs/185c5314e370b844d643c036c0bebada30bb7de6/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/left.java
    14
=======
    expectedExtensionsCount
>>>>>>> /usr/src/app/output/sonarsource/sonar-findbugs/185c5314e370b844d643c036c0bebada30bb7de6/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/right.java
    , ctx.getExtensions().size(), "extensions count");
  }
}