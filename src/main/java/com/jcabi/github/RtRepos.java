package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import com.rexsl.test.response.JsonResponse;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github repositories.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "ghub", "entry" }) final class RtRepos implements Repos {
  /**
     * Github.
     */
  private final transient Github ghub;

  /**
     * RESTful entry.
     */
  private final transient Request entry;

  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
  RtRepos(final Github github, final Request req) {
    this.ghub = github;
    this.entry = req;
    this.request = this.entry.uri().path("user/repos").back();
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public Github github() {
    return this.ghub;
  }

  /**
     * {@inheritDoc}
     * @todo #23:1hr Create integration test case to create random repo,
     *  ensure success, create again, ensure failure, delete.
     */
  @Override public Repo create(@NotNull(message = "JSON can\'t be NULL") final JsonObject json) throws IOException {
    final String coordinates = this.request.method(Request.POST).body().set(json).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_CREATED).as(JsonResponse.class).json().readObject().getString("full_name");
    return this.get(new Coordinates.Simple(coordinates));
  }

  @Override public Repo get(@NotNull(message = "coordinates can\'t be NULL") final Coordinates name) {
    return new RtRepo(this.ghub, this.entry, name);
  }

  @Override public void remove(@NotNull(message = "coordinates can\'t be NULL") final Coordinates coords) throws IOException {
    this.request.method(Request.DELETE).uri().path(coords.toString()).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}