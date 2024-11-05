package com.jcabi.github;
import com.jcabi.github.mock.MkGithub;
import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.ApacheRequest;
import java.net.HttpURLConnection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public final class RtGistsTest {
  @Test public void canCreateFiles() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, "{\"id\":\"1\"}")).start();
    final RtGists gists = new RtGists(new MkGithub(), new ApacheRequest(container.home()));
    try {
      MatcherAssert.assertThat(gists.create(Collections.singletonMap("test", "")), Matchers.notNullValue());
      MatcherAssert.assertThat(container.take().body(), Matchers.startsWith("{\"files\":{\"test\":{\"content\":"));
    }  finally {
      container.stop();
    }
  }

  @Test public void canRetrieveSpecificGist() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testing")).start();
    final RtGists gists = new RtGists(new MkGithub(), new ApacheRequest(container.home()));
    try {
      MatcherAssert.assertThat(gists.get("gist"), Matchers.notNullValue());
    }  finally {
      container.stop();
    }
  }

  @Test public void canIterateThrouRtGists() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[{\"id\":\"hello\"}]")).start();
    final RtGists gists = new RtGists(new MkGithub(), new ApacheRequest(container.home()));
    try {
      MatcherAssert.assertThat(gists.iterate().iterator().next(), Matchers.notNullValue());
    }  finally {
      container.stop();
    }
  }

  @Test public void removesGistByName() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")).start();
    final RtGists gists = new RtGists(new MkGithub(), new ApacheRequest(container.home()));
    try {
      gists.remove("test_gist");
      final MkQuery query = container.take();
      MatcherAssert.assertThat(query.method(), Matchers.equalTo(Request.DELETE));
    }  finally {
      container.stop();
    }
  }
}