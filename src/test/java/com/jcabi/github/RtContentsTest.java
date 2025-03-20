package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.ApacheRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable public final class RtContentsTest {
  /**
     * RtContents can fetch the default branch readme file.
     * @todo #119 RtContents should fetch the readme file for the default
     *  branch.
     *  Let's implement a test here and a method of RtContents.
     *  When done, remove this puzzle and Ignore annotation from the method.
     * @throws Exception if some problem inside.
     */
  @Test @Ignore public void canFetchReadmeFile() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]")).start();
    final Contents contents = new RtContents(new ApacheRequest(container.home()), RtContentsTest.repo());
    MatcherAssert.assertThat(contents.readme(), Matchers.notNullValue());
    container.stop();
  }

  /**
     * RtContents can fetch the readme file from the specified branch.
     *
     * @todo #119 RtContents should fetch the readme file for any branch.
     *  Let's implement a test here and a method of RtContents.
     *  The method should receive the branch name as a parameter.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canFetchReadmeFileFromSpecifiedBranch() {
  }

  /**
     * RtContents can fetch files from the repository.
     * @throws IOException Exception if some problem inside.
     */
  @Test public void canFetchFilesFromRepository() throws IOException {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{\"path\": \"somefile\"}")).start();
    final Contents contents = new RtContents(new ApacheRequest(container.home()), RtContentsTest.repo());
    MatcherAssert.assertThat(contents.content("somepath"), Matchers.notNullValue());
    container.stop();
  }

  /**
     * RtContents can create a file in the repository.
     * @todo #314:15min RtContents#create() should be referencing a get()
     *  method instead of creating its own RtContent object, however,
     *  that method is not yet present. When that has been implemented, update
     *  create() to delegate to get() to obtain a RtContent instance.
     * @throws Exception If a problem occurs.
     */
  @Test public void canCreateFilesFromRepository() throws Exception {
    final String path = "test/thefile";
    final String name = "thefile";
    final JsonObject body = Json.createObjectBuilder().add("path", path).add("name", name).build();
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, Json.createObjectBuilder().add("content", body).build().toString())).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body.toString())).start();
    final RtContents contents = new RtContents(new ApacheRequest(container.home()), repo());
    try {
      final Content.Smart smart = new Content.Smart(contents.create(path, "theMessage", "blah"));
      MatcherAssert.assertThat(container.take().uri().toString(), Matchers.endsWith("/repos/test/contents/contents"));
      MatcherAssert.assertThat(smart.path(), Matchers.is(path));
      MatcherAssert.assertThat(smart.name(), Matchers.is(name));
      MatcherAssert.assertThat(container.take().uri().toString(), Matchers.endsWith("/repos/test/contents/contents/test/thefile"));
    }  finally {
      container.stop();
    }
  }

  /**
     * RtContents can delete files from the repository.
     *
     * @throws Exception if a problem occurs.
     * @checkstyle MultipleStringLiteralsCheck (50 lines)
     */
  @Test public void canDeleteFilesFromRepository() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, Json.createObjectBuilder().add("commit", Json.createObjectBuilder().add("sha", "commitSha").build()).build().toString())).start();
    final RtContents contents = new RtContents(new ApacheRequest(container.home()), repo());
    try {
      final Commit commit = contents.remove("to/remove", "Delete me", "fileSha");
      MatcherAssert.assertThat(commit.sha(), Matchers.is("commitSha"));
      final MkQuery query = container.take();
      MatcherAssert.assertThat(query.body(), Matchers.allOf(Matchers.containsString("\"message\":\"Delete me\""), Matchers.containsString("\"sha\":\"fileSha\"")));
      MatcherAssert.assertThat(query.uri().toString(), Matchers.endsWith("/repos/test/contents/contents/to/remove"));
    }  finally {
      container.stop();
    }
  }

  /**
     * RtContents can update files into the repository.
     *
     * @todo #119 RtContents should be able to update files into the repository.
     *  Let's implement a test here and a method of RtContents.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canUpdateFilesInRepository() {
  }

  /**
     * Create and return repo for testing.
     * @return Repo
     */
  private static Repo repo() {
    final Repo repo = Mockito.mock(Repo.class);
    Mockito.doReturn(new Coordinates.Simple("test", "contents")).when(repo).coordinates();
    return repo;
  }
}