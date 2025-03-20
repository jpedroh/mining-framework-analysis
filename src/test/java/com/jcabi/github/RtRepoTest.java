package com.jcabi.github;
import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.request.ApacheRequest;
import com.rexsl.test.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRepo}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
@SuppressWarnings(value = { "PMD.TooManyMethods" }) public final class RtRepoTest {
  /**
     * RtRepo can fetch events.
     *
     * @throws Exception If some problem inside
     */
  @Test public void iteratesEvents() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, Json.createArrayBuilder().add(event(Event.ASSIGNED)).add(event(Event.MENTIONED)).build().toString())).start();
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new ApacheRequest(container.home()), new Coordinates.Simple("octocat", "master"));
    MatcherAssert.assertThat(repo.events(), Matchers.<Event>iterableWithSize(2));
    container.stop();
  }

  /**
     * RtRepo can fetch its labels.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchesLabels() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("jeff", "jeff-branch"));
    MatcherAssert.assertThat(repo.labels(), Matchers.notNullValue());
  }

  /**
     * RtRepo can fetch its issues.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchesIssues() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("mark", "mark-branch"));
    MatcherAssert.assertThat(repo.issues(), Matchers.notNullValue());
  }

  /**
     * RtRepo can fetch its pulls.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchesPulls() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("kendy", "kendy-branch"));
    MatcherAssert.assertThat(repo.pulls(), Matchers.notNullValue());
  }

  /**
     * RtRepo can fetch its hooks.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchHooks() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("paul", "paul-branch"));
    MatcherAssert.assertThat(repo.hooks(), Matchers.notNullValue());
  }

  /**
     * RtRepo can fetch its keys.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchKeys() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("andres", "andres-branch"));
    MatcherAssert.assertThat(repo.keys(), Matchers.notNullValue());
  }

  /**
     * RtRepo can fetch its releases.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchReleases() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("phil", "phil-branch"));
    MatcherAssert.assertThat(repo.releases(), Matchers.notNullValue());
  }

  /**
     * RtRepo can fetch its contents.
     *
     * @throws Exception if a problem occurs.
     */
  @Test public void fetchContents() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("andres-contents", "contents-branch"));
    MatcherAssert.assertThat(repo.contents(), Matchers.notNullValue());
  }

  /**
     * RtRepo can identify itself.
     * @throws Exception If some problem inside
     */
  @Test public void identifiesItself() throws Exception {
    final Coordinates coords = new Coordinates.Simple("me", "me-branch");
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), coords);
    MatcherAssert.assertThat(repo.coordinates(), Matchers.sameInstance(coords));
  }

  /**
     * RtRepo can execute PATCH request.
     *
     * @throws Exception if there is any problem
     */
  @Test public void executePatchRequest() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, event(Event.ASSIGNED).toString())).start();
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new ApacheRequest(container.home()), new Coordinates.Simple("test", "test-branch"));
    repo.patch(event(Event.ASSIGNED));
    MatcherAssert.assertThat(container.take().method(), Matchers.equalTo(Request.PATCH));
    container.stop();
  }

  /**
     * RtRepo can describe as a JSON object.
     *
     * @throws Exception if there is any problem
     */
  @Test public void describeAsJson() throws Exception {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest().withBody(Json.createObjectBuilder().add("full_name", "octocat/Hello-World").add("fork", true).build().toString()), new Coordinates.Simple("oct", "oct-branch"));
    MatcherAssert.assertThat(repo.json().toString(), Matchers.equalTo("{\"full_name\":\"octocat/Hello-World\",\"fork\":true}"));
  }

  /**
     * RtRepo can fetch commits.
     */
  @Test public void fetchCommits() {
    final Repo repo = new RtRepo(Mockito.mock(Github.class), new FakeRequest(), new Coordinates.Simple("testuser", "testrepo"));
    MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
  }

  /**
     * Create and return JsonObject to test.
     * @param event Event type
     * @return JsonObject
     * @throws Exception if some problem inside
     */
  private static JsonObject event(final String event) throws Exception {
    return Json.createObjectBuilder().add("id", 1).add("event", event).build();
  }
}