package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import com.rexsl.test.response.JsonResponse;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github gists.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #64 An integration test is still lacking for this class,
 *  although a unit test has already been created.
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "ghub", "request" }) final class RtGists implements Gists {
  /**
     * API entry point.
     */
  private final transient Request entry;

  /**
     * Github.
     */
  private final transient Github ghub;

  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
  RtGists(final Github github, final Request req) {
    this.entry = req;
    this.ghub = github;
    this.request = this.entry.uri().path("/gists").back();
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public Github github() {
    return this.ghub;
  }

  @Override public Gist create(@NotNull(message = "list of files can\'t be NULL") final Map<String, String> files) throws IOException {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    for (final Map.Entry<String, String> file : files.entrySet()) {
      builder = builder.add(file.getKey(), Json.createObjectBuilder().add("content", file.getValue()));
    }
    final JsonStructure json = Json.createObjectBuilder().add("files", builder).build();
    return this.get(this.request.method(Request.POST).body().set(json).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_CREATED).as(JsonResponse.class).json().readObject().getString("id"));
  }

  @Override public Gist get(@NotNull(message = "gist name can\'t be NULL") final String name) {
    return new RtGist(this.ghub, this.entry, name);
  }

  @Override public Iterable<Gist> iterate() {
    return new RtPagination<Gist>(this.request, new RtPagination.Mapping<Gist>() {
      @Override public Gist map(final JsonObject object) {
        return RtGists.this.get(object.getString("id"));
      }
    });
  }

  @Override public void remove(@NotNull(message = "gist name can\'t be NULL") final String name) throws IOException {
    this.request.method(Request.DELETE).uri().path(name).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}