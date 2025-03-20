package com.jcabi.github;
import com.jcabi.http.request.FakeRequest;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtRepoCommits}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class RtRepoCommitsTest {
  /**
     * RtRepoCommits can return commits' iterator.
     */
  @Test public void returnIterator() {
    final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db51";
    final RepoCommits commits = new RtRepoCommits(new FakeRequest().withBody(Json.createArrayBuilder().add(Json.createObjectBuilder().add("sha", sha)).build().toString()), RtRepoCommitsTest.repo());
    MatcherAssert.assertThat(commits.iterate().iterator().next().sha(), Matchers.equalTo(sha));
  }

  /**
     * RtRepoCommits can get commit.
     */
  @Test public void getCommit() {
    final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db52";
    final RepoCommits commits = new RtRepoCommits(new FakeRequest().withBody(Json.createObjectBuilder().add("sha", sha).build().toString()), RtRepoCommitsTest.repo());
    MatcherAssert.assertThat(commits.get(sha).sha(), Matchers.equalTo(sha));
  }

  /**
     * Create repository for tests.
     * @return Repository
     */
  private static Repo repo() {
    return new RtGithub().repos().get(new Coordinates.Simple("user", "repo"));
  }
}