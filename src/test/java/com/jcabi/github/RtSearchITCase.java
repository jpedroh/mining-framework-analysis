package com.jcabi.github;
import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import java.util.EnumMap;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtSearch}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (140 lines)
 */
@OAuthScope(value = { Scope.REPO, Scope.USER }) @SuppressWarnings(value = { "PMD.AvoidDuplicateLiterals" }) public final class RtSearchITCase {
  /**
     * RtSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canSearchForRepos() throws Exception {
    MatcherAssert.assertThat(RtSearchITCase.github().search().repos("repo", "stars", Search.Order.DESC), Matchers.not(Matchers.emptyIterableOf(Repo.class)));
  }

  /**
     * RtSearch can fetch multiple pages of a large result (more than 25 items).
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canFetchMultiplePages() throws Exception {
    final Iterator<Repo> iter = RtSearchITCase.github().search().repos("java", "", Search.Order.DESC).iterator();
    int count = 0;
    while (iter.hasNext() && count < Tv.HUNDRED) {
      iter.next().coordinates();
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
    final EnumMap<Search.Qualifier, String> qualifiers = new EnumMap<Search.Qualifier, String>(Search.Qualifier.class);
    qualifiers.put(Search.Qualifier.LABEL, "bug");
    MatcherAssert.assertThat(RtSearchITCase.github().search().issues("qualifiers", "updated", Search.Order.DESC, qualifiers), Matchers.not(Matchers.emptyIterableOf(Issue.class)));
  }

  /**
     * RtSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
  @Test public void canSearchForUsers() throws Exception {
    MatcherAssert.assertThat(RtSearchITCase.github().search().users("jcabi", "joined", Search.Order.DESC), Matchers.not(Matchers.emptyIterableOf(User.class)));
  }

  /**
     * RtSearch can search for contents.
     *
     * @throws Exception if a problem occurs
     * @see <a href="https://developer.github.com/v3/search/#search-code">Search API</a> for details
     */
  @Test public void canSearchForContents() throws Exception {
    MatcherAssert.assertThat(RtSearchITCase.github().search().codes("addClass repo:jquery/jquery", "joined", Search.Order.DESC), Matchers.not(Matchers.emptyIterableOf(Content.class)));
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