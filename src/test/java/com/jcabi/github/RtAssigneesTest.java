package com.jcabi.github;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtAssignees}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class RtAssigneesTest {
  /**
     * RtAssignees can iterate over assignees.
     * @throws Exception Exception If some problem inside
     */
  @Test public void iteratesAssignees() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, Json.createArrayBuilder().add(RtAssigneesTest.json("octocat")).add(RtAssigneesTest.json("dummy")).build().toString())).start();
    final Assignees users = new RtAssignees(new JdkRequest(container.home()), this.repo());
    MatcherAssert.assertThat(users.iterate(), Matchers.<User>iterableWithSize(2));
    container.stop();
  }

  /**
     * RtAssignees can check if user is assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
  @Test public void checkUserIsAssigneeForRepo() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, Json.createArrayBuilder().add(RtAssigneesTest.json("octocat2")).add(RtAssigneesTest.json("dummy")).build().toString())).start();
    final Assignees users = new RtAssignees(new JdkRequest(container.home()), this.repo());
    MatcherAssert.assertThat(users.check("octocat2"), Matchers.equalTo(true));
    container.stop();
  }

  /**
     * RtAssignees can check if user is NOT assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
  @Test public void checkUserIsNotAssigneeForRepo() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND, Json.createArrayBuilder().add(RtAssigneesTest.json("octocat3")).add(RtAssigneesTest.json("dummy")).build().toString())).start();
    final Assignees users = new RtAssignees(new JdkRequest(container.home()), this.repo());
    MatcherAssert.assertThat(users.check("octocat33"), Matchers.equalTo(false));
    container.stop();
  }

  /**
     * Create and return JsonObject to test.
     * @param login Username to login
     * @return JsonObject
     * @throws Exception If some problem inside
     */
  private static JsonValue json(final String login) throws Exception {
    return Json.createObjectBuilder().add("login", login).build();
  }

  /**
     * Create and return repo for testing.
     * @return Repo
     */
  private Repo repo() {
    final Repo repo = Mockito.mock(Repo.class);
    Mockito.doReturn(new Coordinates.Simple("test", "assignee")).when(repo).coordinates();
    Mockito.doReturn(Mockito.mock(Github.class)).when(repo).github();
    return repo;
  }
}