package com.jcabi.github;
import com.jcabi.aspects.Tv;
import org.apache.commons.lang3.NotImplementedException;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtSearch}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (41 lines)
 */
public final class RtSearchITCase {
  /**
     * RtSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canSearchForRepos() throws Exception {
    MatcherAssert.assertThat(RtSearchITCase.github().search().repos("repo", "stars", "desc"), Matchers.not(Matchers.emptyIterableOf(Repo.class)));
  }

  /**
     * RtSearch can fetch multiple pages of a large result (more than 25 items).
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canFetchMultiplePages() throws Exception {
    final Iterator<Repo> iter = RtSearchITCase.github().search().repos("java", "", "").iterator();
    int count = 0;
    while (iter.hasNext() && count < Tv.HUNDRED) {
      iter.next();
      count += 1;
    }
    MatcherAssert.assertThat(count, Matchers.greaterThanOrEqualTo(Tv.HUNDRED));
  }

  /**
     * RtSearch can search for issues.
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canSearchForIssues() throws Exception {
    MatcherAssert.assertThat(RtSearchITCase.github().search().issues("issue", "updated", "desc"), Matchers.not(Matchers.emptyIterableOf(Issue.class)));
  }

  /**
     * RtSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canSearchForUsers() throws Exception {
    MatcherAssert.assertThat(RtSearchITCase.github().search().users("jcabi", "joined", "desc"), Matchers.not(Matchers.emptyIterableOf(User.class)));
  }

  /**
     * RtSearch can search for contents.
     *
     * @throws Exception if a problem occurs
     * @todo #217 RtSearchITCase.canSearchForContents() is missing.
     *  Let's implement it and remove this puzzle
     *  @see <a href="https://developer.github.com/v3/search/#search-code">Search API</a>
     *  for details
     */
  @Ignore public void canSearchForContents() throws Exception {
    throw new NotImplementedException("RtSearchITCase#canSearchForContents");
  }

  /**
     * Return github for test.
     * @return Github
     */
  private static Github github() {
    final String key = System.getProperty("failsafe.github.key");
    Assume.assumeThat(key, Matchers.notNullValue());
    return new RtGithub(key);
  }
}