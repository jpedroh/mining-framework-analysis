package net.tridentsdk.config;
import net.tridentsdk.Impl;
import net.tridentsdk.util.Misc;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertNotNull;

public class ConfigTest {
  private static final String TEST_PATH = Misc.HOME + "/kek/cfg.json";

  static {
    Impl.setImpl(Mockito.mock(Impl.ImplementationProvider.class));
    Mockito.when(Impl.get().newCfg(Paths.get(TEST_PATH))).thenReturn(Mockito.mock(Config.class));
  }

  @Test public void testPathString() {
    Config cfg = Config.load(TEST_PATH);

<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/b53a148ca59225dcbceb1fc65321f98a0c5c13f9/src/test/java/net/tridentsdk/config/ConfigTest.java/left.java
    assertNotNull(cfg)
=======
    assertEquals(TEST_PATH, cfg.getPath().toString())
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/b53a148ca59225dcbceb1fc65321f98a0c5c13f9/src/test/java/net/tridentsdk/config/ConfigTest.java/right.java
    ;
  }

  @Test public void testPath() {
    Path path = Paths.get(TEST_PATH);
    Config cfg = Config.load(path);

<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/b53a148ca59225dcbceb1fc65321f98a0c5c13f9/src/test/java/net/tridentsdk/config/ConfigTest.java/left.java
    assertNotNull(cfg)
=======
    assertEquals(path, cfg.getPath())
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/b53a148ca59225dcbceb1fc65321f98a0c5c13f9/src/test/java/net/tridentsdk/config/ConfigTest.java/right.java
    ;
  }

  @Test public void testFile() {
    File file = new File(TEST_PATH);
    Config cfg = Config.load(file);

<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/b53a148ca59225dcbceb1fc65321f98a0c5c13f9/src/test/java/net/tridentsdk/config/ConfigTest.java/left.java
    assertNotNull(cfg)
=======
    assertEquals(file, cfg.getFile())
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/b53a148ca59225dcbceb1fc65321f98a0c5c13f9/src/test/java/net/tridentsdk/config/ConfigTest.java/right.java
    ;
  }
}