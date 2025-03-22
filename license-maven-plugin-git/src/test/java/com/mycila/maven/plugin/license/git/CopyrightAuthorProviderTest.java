package com.mycila.maven.plugin.license.git;
import com.mycila.maven.plugin.license.LicenseCheckMojo;
import com.mycila.maven.plugin.license.document.Document;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
class CopyrightAuthorProviderTest {
  private static Path gitRepoRoot;

  @TempDir static File tempFolder;

  @Test void copyrightAuthor() {
    CopyrightAuthorProvider provider = new CopyrightAuthorProvider();
    assertAuthor(provider, "dir1/file1.txt", "Peter Palaga", "ppalaga@redhat.com");
  }

  private void assertAuthor(CopyrightAuthorProvider provider, String path, String copyrightAuthorName, String copyrightAuthorEmail) {
    Properties props = new Properties();
    Document document = newDocument(path);
    Map<String, String> actual = provider.getAdditionalProperties(new LicenseCheckMojo(), props, document);
    HashMap<String, String> expected = new HashMap<String, String>();
    expected.put(CopyrightAuthorProvider.COPYRIGHT_CREATION_AUTHOR_NAME_KEY, copyrightAuthorName);
    expected.put(CopyrightAuthorProvider.COPYRIGHT_CREATION_AUTHOR_EMAIL_KEY, copyrightAuthorEmail);
    Assertions.assertEquals(expected, actual, "for file \'" + path + "\': ");
  }

  private static Document newDocument(String relativePath) {
    Path path = Paths.get(gitRepoRoot.toAbsolutePath() + File.separator + relativePath.replace('/', File.separatorChar));
    return new Document(path.toFile(), null, "utf-8", new String[0], null);
  }

  @BeforeAll static void beforeClass() throws IOException {
    URL url = GitLookupTest.class.getResource("git-test-repo.zip");
    Path unzipDestination = tempFolder.toPath();
    gitRepoRoot = Files.createDirectory(unzipDestination);
    GitLookupTest.unzip(url, unzipDestination);
  }
}