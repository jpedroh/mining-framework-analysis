package com.jcabi.github;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration case for {@link RepoCommits}.
 *
 * <p>
 * WARNING: As there is no way to create Commit directly it was decided to use
 * real commits from jcabi-github repository for integration testing of
 * RtRepoCommits
 *
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @todo #117 Add test getCommit() to check that commit actually got.
 *  See http://developer.github.com/v3/repos/commits/#get-a-single-commit.
 */
public class RtRepoCommitsITCase {
  /**
     * RtRepoCommits can fetch repo commits.
     * @throws Exception if there is no github key provided
     */
  @Test public final void fetchCommits() throws Exception {
    final Iterator<RepoCommit> iterator = RtRepoCommitsITCase.repo().commits().iterate(Collections.<String, String>emptyMap()).iterator();
    final List<String> shas = new ArrayList<String>(5);
    shas.add("1aa4af45aa2c56421c3d911a0a06da513a7316a0");
    shas.add("940dd5081fada0ead07762933036bf68a005cc40");
    shas.add("05940dbeaa6124e4a87d9829fb2fce80b713dcbe");
    shas.add("51cabb8e759852a6a40a7a2a76ef0afd4beef96d");
    shas.add("11bd4d527236f9cb211bc6667df06fde075beded");
    int found = 0;
    while (iterator.hasNext()) {
      if (shas.contains(iterator.next().sha())) {
        found += 1;
      }
    }
    MatcherAssert.assertThat(found, Matchers.equalTo(shas.size()));
  }

  /**
     * RtRepoCommits can compare two commits and return result in patch mode.
     * @throws Exception if there is no github key provided
     */
  @Test public final void compareCommitsPatch() throws Exception {
    final String patch = RtRepoCommitsITCase.repo().commits().patch("5339b8e35b", "9b2e6efde9");
    MatcherAssert.assertThat(patch, Matchers.startsWith("From 9b2e6efde94fabec5876dc481b38811e8b4e992f"));
    MatcherAssert.assertThat(patch, Matchers.containsString("Subject: [PATCH] Issue #430 RepoCommit interface was added"));
  }

  /**
     * RtRepoCommits can compare two commits and return result in diff mode.
     * @throws Exception if there is no github key provided
     */
  @Test public final void compareCommitsDiff() throws Exception {
    final String diff = RtRepoCommitsITCase.repo().commits().diff("2b3814e", "b828dfa");
    MatcherAssert.assertThat(diff, Matchers.startsWith("diff --git"));
  }

  /**
     * Check that commit actually got.
     * @throws Exception If some problem inside
     */
  @Test public final void getCommit() throws Exception {
    final String sha = "94e4216";
    MatcherAssert.assertThat(RtRepoCommitsITCase.repo().commits().get(sha).sha(), Matchers.equalTo(sha));
  }

  /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
  private static Repo repo() throws Exception {
    final String key = System.getProperty("failsafe.github.key");
    Assume.assumeThat(key, Matchers.notNullValue());
    return new RtGithub(key).repos().get(new Coordinates.Simple("jcabi", "jcabi-github"));
  }
}