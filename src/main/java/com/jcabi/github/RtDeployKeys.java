package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github deploy keys.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "request", "owner", "entry" }) final class RtDeployKeys implements DeployKeys {
  /**
     * Repository.
     */
  private final transient Repo owner;

  /**
     * RESTful API entry point.
     */
  private final transient Request entry;

  /**
     * RESTful API request for these deploy keys.
     */
  private final transient Request request;

  /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param repo Repository
     */
  RtDeployKeys(final Request req, final Repo repo) {
    this.owner = repo;
    this.entry = req;
    this.request = req.uri().path("/repos").path(repo.coordinates().user()).path(repo.coordinates().repo()).path("/keys").back();
  }

  @Override @NotNull(message = "repository is never NULL") public Repo repo() {
    return this.owner;
  }

  @Override @NotNull(message = "Iterable of DeployKey can\'t be NULL") public Iterable<DeployKey> iterate() {
    return new RtPagination<DeployKey>(this.request, new RtPagination.Mapping<DeployKey, JsonObject>() {
      @Override public DeployKey map(final JsonObject object) {
        return RtDeployKeys.this.get(object.getInt("id"));
      }
    });
  }

  @Override @NotNull(message = "DeployKey can\'t be NULL") public DeployKey get(final int number) {
    return new RtDeployKey(this.entry, number, this.owner);
  }

  @Override @NotNull(message = "DeployKey is never NULL") public DeployKey create(@NotNull(message = "title can\'t be NULL") final String title, @NotNull(message = "key can\'t be NULL") final String key) throws IOException {
    return this.get(this.request.method(Request.POST).body().set(Json.createObjectBuilder().add("title", title).add("key", key).build()).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_CREATED).as(JsonResponse.class).json().readObject().getInt("id"));
  }
}