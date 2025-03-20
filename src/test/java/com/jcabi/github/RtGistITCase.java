package com.jcabi.github;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link Gist}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class RtGistITCase {
  /**
     * RtGist can text and write files.
     * @throws Exception If some problem inside
     */
  @Test public void readsAndWritesGists() throws Exception {
    final String filename = "filename.txt";
    final String content = "content of file";
    final Gists gists = RtGistITCase.github().gists();
    Gist.Smart smart = null;
    try {
      final Gist gist = gists.create(Collections.singletonMap(filename, content));
      smart = new Gist.Smart(gist);
      final String file = smart.files().iterator().next();
      gist.write(file, "hey, works for you this way?");
      MatcherAssert.assertThat(gist.read(file), Matchers.startsWith("hey, works for "));
    }  finally {
      if (smart != null) {
        gists.remove(smart.identifier());
      }
    }
  }

  /**
     * RtGist can fork a gist.
     * @throws Exception If some problem inside
     */
  @Test public void forkGist() throws Exception {
    final String key = System.getProperty("failsafe.github.key.second");
    Assume.assumeThat(key, Matchers.notNullValue());
    final Gist gist = new RtGithub(key).gists().get(RtGistITCase.gist().name());
    final String file = new Gist.Smart(gist).files().iterator().next();
    MatcherAssert.assertThat(gist.fork().read(file), Matchers.equalTo(gist.read(file)));
  }

  /**
     * Return github to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
  private static Github github() throws Exception {
    final String key = System.getProperty("failsafe.github.key");
    Assume.assumeThat(key, Matchers.notNullValue());
    return new RtGithub(key);
  }
}