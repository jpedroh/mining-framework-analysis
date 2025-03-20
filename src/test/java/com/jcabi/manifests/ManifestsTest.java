package com.jcabi.manifests;
import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Manifests}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.7
 */
public final class ManifestsTest {
  /**
     * Manifests can read a single attribute, which always exist in MANIFEST.MF.
     * @throws Exception If something goes wrong
     */
  @Test public void readsSingleExistingAttribute() {
    MatcherAssert.assertThat(Manifests.read("JCabi-Version"), Matchers.notNullValue());
  }

  /**
     * Manifests can read a default value.
     * @throws Exception If something goes wrong
     */
  @Test public void readsSingleNotExistingAttribute() throws Exception {
    final String value = "default";
    MatcherAssert.assertThat(Manifests.read("invalid-key", value), Matchers.equalTo(value));
  }

  /**
     * Manifests can throw an exception if an attribute is empty.
     * @throws Exception If something goes wrong
     */
  @Test public void throwsExceptionWhenAttributeIsEmpty() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Manifests.read("Jcabi-Test-Empty-Attribute"));
  }

  /**
     * Manifests can throw an exception when attribute is absent.
     * @throws Exception If something goes wrong
     */
  @Test public void throwsExceptionIfAttributeIsMissed() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Manifests.read("absent-property"));
  }

  /**
     * Manifests can throw an exception loading file with empty attribute.
     * @throws Exception If something goes wrong
     */
  @Test public void throwsExceptionWhenNoAttributes() {
    Assertions.assertThrows(IOException.class, () -> {
      final File file = new File(Thread.currentThread().getContextClassLoader().getResource("META-INF/MANIFEST_INVALID.MF").getFile());
      final Manifests mfs = new Manifests();
      mfs.append(new FilesMfs(file));
    });
  }

  /**
     * Manifests can append attributes from file.
     * @throws Exception If something goes wrong
     */
  @Test public void appendsAttributesFromFile() throws Exception {
    final String name = "Test-Attribute-From-File";
    final String value = "some text value of attribute";
    final File file = File.createTempFile("test-", ".MF");
    Files.write(file.toPath(), Logger.format("%s: %s\n", name, value).getBytes(StandardCharsets.UTF_8));
    final MfMap manifests = new Manifests().append(new FilesMfs(file));
    MatcherAssert.assertThat("loaded from file", manifests.containsKey(name) && manifests.get(name).equals(value));
  }

  /**
     * Manifests can append input stream.
     * @throws Exception If something goes wrong
     * @since 0.8
     */
  @Test public void appendsAttributesFromInputStream() throws Exception {
    final MfMap manifests = new Manifests().append(new StreamsMfs(this.getClass().getResourceAsStream("test.mf")));
    MatcherAssert.assertThat(manifests.get("From-File"), Matchers.equalTo("some test attribute"));
  }
}