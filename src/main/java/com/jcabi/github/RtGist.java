package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import com.rexsl.test.Response;
import com.rexsl.test.response.JsonResponse;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Github gist.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "ghub", "request" }) final class RtGist implements Gist {
  /**
     * RESTful request for the gist.
     */
  private final transient Request request;

  /**
     * Github.
     */
  private final transient Github ghub;

  /**
     * RESTful entry.
     */
  private final transient Request entry;

  /**
     * Gist id.
     */
  private final transient String gist;

  /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param name Name of gist
     */
  RtGist(final Github github, final Request req, final String name) {
    this.ghub = github;
    this.entry = req;
    this.gist = name;
    this.request = req.uri().path("/gists").path(name).back();
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public Github github() {
    return this.ghub;
  }

  @Override public String name() {
    return this.gist;
  }

  @Override public String read(@NotNull(message = "file name can\'t be NULL") final String file) throws IOException {
    final Response response = this.entry.fetch();
    final String url = response.as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_OK).as(JsonResponse.class).json().readObject().getJsonObject("files").getJsonObject(file).getString("raw_url");
    return response.as(RestResponse.class).jump(URI.create(url)).fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_OK).body();
  }

  @Override public void write(@NotNull(message = "file name can\'t be NULL") final String file, @NotNull(message = "file content can\'t be NULL") final String content) throws IOException {
    final JsonObjectBuilder builder = Json.createObjectBuilder().add("content", content);
    final JsonStructure json = Json.createObjectBuilder().add("files", Json.createObjectBuilder().add(file, builder)).build();
    this.entry.method(Request.PATCH).body().set(json).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_OK);
  }

  @Override public void star() throws IOException {
    this.request.uri().path("star").back().method("PUT").fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }

  @Override public boolean starred() throws IOException {
    final RestResponse response = this.request.uri().path("star").back().method("GET").fetch().as(RestResponse.class).assertStatus(Matchers.isOneOf(HttpURLConnection.HTTP_NO_CONTENT, HttpURLConnection.HTTP_NOT_FOUND));
    return response.status() == HttpURLConnection.HTTP_NO_CONTENT;
  }

  @Override public Gist fork() throws IOException {
    final String name = this.request.uri().path("/forks").back().method(Request.POST).fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_CREATED).as(JsonResponse.class).json().readObject().getString("id");
    return new RtGist(this.ghub, this.entry, name);
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }

  @Override public GistComments comments() throws IOException {
    return new RtGistComments(this.entry, this);
  }
}