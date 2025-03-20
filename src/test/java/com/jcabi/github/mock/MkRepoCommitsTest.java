package com.jcabi.github.mock;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RepoCommits;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkRepoCommits).
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class MkRepoCommitsTest {
  /**
     * MkRepoCommits can return commits' iterator.
     * @throws IOException If some problem inside
     */
  @Test public void returnIterator() throws IOException {
    final String user = "testuser1";
    final RepoCommits commits = new MkRepoCommits(new MkStorage.InFile(), user, new Coordinates.Simple(user, "testrepo1"));
    MatcherAssert.assertThat(commits.iterate(), Matchers.notNullValue());
  }

  /**
     * MkRepoCommits can get a commit.
     * @throws IOException if some problem inside
     */
  @Test public void getCommit() throws IOException {
    final String user = "testuser2";
    final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db5e";
    final RepoCommits commits = new MkRepoCommits(new MkStorage.InFile(), user, new Coordinates.Simple(user, "testrepo2"));
    MatcherAssert.assertThat(commits.get(sha), Matchers.notNullValue());
  }

  /**
     * MkRepoCommits can compare commits.
     * @throws IOException if some problem inside
     */
  @Test public void canCompare() throws IOException {
    final String user = "testuser3";
    MatcherAssert.assertThat(new MkRepoCommits(new MkStorage.InFile(), user, new Coordinates.Simple(user, "testrepo3")).compare("5339b8e35b", "9b2e6efde9"), Matchers.notNullValue());
  }
}