package org.sonar.plugins.findbugs;
import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;

class FindbugsPluginTest {
  @Test void testGetExtensions() {
    Plugin.Context ctx = new Plugin.Context(mock(SonarRuntime.class));
    FindbugsPlugin plugin = new FindbugsPlugin();
    plugin.define(ctx);
    assertEquals(
<<<<<<< /usr/src/app/output/sonarsource/sonar-findbugs/91cc3c30b31fa91cd854d16ff0d4023c5eac065a/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/left.java
    24
=======
    22
>>>>>>> /usr/src/app/output/sonarsource/sonar-findbugs/91cc3c30b31fa91cd854d16ff0d4023c5eac065a/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/right.java
    , ctx.getExtensions().size(), "extension count");
  }
}