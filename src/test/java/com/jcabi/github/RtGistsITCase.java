package com.jcabi.github;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Gists}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 */
public final class RtGistsITCase {
  /**
     * RtGists can create a gist.
     * @throws Exception If some problem inside
     */
  @Test public void createGist() throws Exception {
    final String filename = "filename.txt";
    final String content = "content of file";
    final Gists gists = gists();
    final Gist gist = gists.create(Collections.singletonMap(filename, content));
    MatcherAssert.assertThat(new Gist.Smart(gist).read(filename), Matchers.equalTo(content));
    gists.remove(gist.name());
  }

  /**
     * RtGists can iterate all gists.
     * @throws Exception If some problem inside
     */
  @Test public void iterateGists() throws Exception {
    final Gists gists = gists();
    final Gist gist = gists.create(Collections.singletonMap("test.txt", "content"));
    MatcherAssert.assertThat(gists.iterate(), Matchers.hasItem(gist));
    gists.remove(gist.name());
  }

  /**
     * RtGists can get a single gist.
     * @throws Exception If some problem inside
     */
  @Test public void singleGist() throws Exception {
    final String filename = "single-name.txt";
    final Gists gists = gists();
    final Gist gist = gists.create(Collections.singletonMap(filename, "body"));
    MatcherAssert.assertThat(gists.get(gist.name()), Matchers.sameInstance(gist));
    gists.remove(gist.name());
  }

  /**
     * This tests that RtGists can remove a gist by name.
     * @throws Exception - if something goes wrong.
     */
  @Test public void removesGistByName() throws Exception {
    final Gists gists = gists();
    final Gist gist = gists.create(Collections.singletonMap("fileName.txt", "content of test file"));
    MatcherAssert.assertThat(gists.iterate(), Matchers.notNullValue());
    gists.remove(gist.json().getString("id"));
    MatcherAssert.assertThat(gists.iterate(), Matchers.not(Matchers.hasItem(gist)));
  }

  /**
     * Return gists to test.
     * @return Gists
     * @throws Exception If some problem inside
     */
  private static Gists gists() throws Exception {
    final String key = System.getProperty("failsafe.github.key");
    Assume.assumeThat(key, Matchers.notNullValue());
    return new RtGithub(key).gists();
  }
}