package com.jcabi.github;
import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.ApacheRequest;
import com.rexsl.test.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssue}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtIssueTest {
  /**
     * RtIssue should be able to fetch its comments.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchesComments() throws Exception {
    final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
    MatcherAssert.assertThat(issue.comments(), Matchers.notNullValue());
  }

  /**
     * RtIssue should be able to fetch its labels.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchesLabels() throws Exception {
    final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
    MatcherAssert.assertThat(issue.labels(), Matchers.notNullValue());
  }

  /**
     * RtIssue should be able to fetch its events.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchesEvents() throws Exception {
    final RtIssue issue = new RtIssue(new FakeRequest(), this.repo(), 1);
    MatcherAssert.assertThat(issue.events(), Matchers.notNullValue());
  }

  /**
     * RtIssue should be able to describe itself in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchIssueAsJson() throws Exception {
    final RtIssue issue = new RtIssue(new FakeRequest().withBody("{\"issue\":\"json\"}"), this.repo(), 1);
    MatcherAssert.assertThat(issue.json().getString("issue"), Matchers.equalTo("json"));
  }

  /**
     * RtIssue should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
  @Test public void canCompareInstances() throws Exception {
    final RtIssue less = new RtIssue(new FakeRequest(), this.repo(), 1);
    final RtIssue greater = new RtIssue(new FakeRequest(), this.repo(), 2);
    MatcherAssert.assertThat(less.compareTo(greater), Matchers.lessThan(0));
    MatcherAssert.assertThat(greater.compareTo(less), Matchers.greaterThan(0));
  }

  /**
     * RtIssue should be able to perform a patch request.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void patchWithJson() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")).start();
    final RtIssue issue = new RtIssue(new ApacheRequest(container.home()), this.repo(), 1);
    issue.patch(Json.createObjectBuilder().add("patch", "test").build());
    final MkQuery query = container.take();
    try {
      MatcherAssert.assertThat(query.method(), Matchers.equalTo(Request.PATCH));
      MatcherAssert.assertThat(query.body(), Matchers.equalTo("{\"patch\":\"test\"}"));
    }  finally {
      container.stop();
    }
  }

  /**
     * Mock repo for GhIssue creation.
     * @return The mock repo.
     */
  private Repo repo() {
    final Repo repo = Mockito.mock(Repo.class);
    final Coordinates coords = Mockito.mock(Coordinates.class);
    Mockito.doReturn(coords).when(repo).coordinates();
    Mockito.doReturn("user").when(coords).user();
    Mockito.doReturn("repo").when(coords).repo();
    return repo;
  }
}