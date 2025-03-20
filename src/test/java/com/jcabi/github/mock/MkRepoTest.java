package com.jcabi.github.mock;
import com.jcabi.github.Milestones;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Repo}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkRepoTest {
  /**
     * Repo can work.
     * @throws Exception If some problem inside
     */
  @Test public void works() throws Exception {
    final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
    final Repo repo = repos.create(Json.createObjectBuilder().add("name", "test").build());
    MatcherAssert.assertThat(repo.coordinates(), Matchers.hasToString("jeff/test"));
  }

  /**
     * This tests that the milestones() method in MkRepo is working fine.
     * @throws Exception - if anything goes wrong.
     */
  @Test public void returnsMkMilestones() throws Exception {
    final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
    final Repo repo = repos.create(Json.createObjectBuilder().add("name", "test1").build());
    final Milestones milestones = repo.milestones();
    MatcherAssert.assertThat(milestones, Matchers.notNullValue());
  }

  /**
     * Repo can fetch its commits.
     *
     * @throws IOException if some problem inside
     */
  @Test public void fetchCommits() throws IOException {
    final String user = "testuser";
    final Repo repo = new MkRepo(new MkStorage.InFile(), user, new Coordinates.Simple(user, "testrepo"));
    MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
  }
}