package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github pull comment.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "request", "owner" }) public final class RtPullComments implements PullComments {
  /**
     * API entry point.
     */
  private final transient Request entry;

  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Owner of comments.
     */
  private final transient Pull owner;

  /**
     * Public ctor.
     * @param req Request
     * @param pull Pull
     */
  RtPullComments(final Request req, final Pull pull) {
    this.entry = req;
    final Coordinates coords = pull.repo().coordinates();
    this.request = this.entry.uri().path("/repos").path(coords.user()).path(coords.repo()).path("/pulls").path("/comments").back();
    this.owner = pull;
  }

  @Override @NotNull(message = "Pull is never NUll") public Pull pull() {
    return this.owner;
  }

  @Override @NotNull(message = "PullComment is never NULL") public PullComment get(final int number) {
    return new RtPullComment(this.entry, this.owner, number);
  }

  @Override @NotNull(message = "Iterable of pull comments is never NULL") public Iterable<PullComment> iterate(final Map<String, String> params) {
    return new RtPagination<PullComment>(this.request.uri().queryParams(params).back(), new RtPagination.Mapping<PullComment, JsonObject>() {
      @Override public PullComment map(final JsonObject value) {
        return RtPullComments.this.get(value.getInt("id"));
      }
    });
  }

  @Override @NotNull(message = "Iterable of pull comments is never NULL") public Iterable<PullComment> iterate(final int number, @NotNull(message = "params can\'t be NULL") final Map<String, String> params) {
    throw new UnsupportedOperationException("Iterate not yet implemented.");
  }

  @Override @NotNull(message = "PullComment is never NULL") public PullComment post(@NotNull(message = "body can\'t be NULL") final String body, @NotNull(message = "commit can\'t be NULL") final String commit, @NotNull(message = "path can\'t be NULL") final String path, @NotNull(message = "position can\'t be NULL") final int position) throws IOException {
    final JsonStructure json = Json.createObjectBuilder().add("body", body).add("commit_id", commit).add("path", path).add("position", position).build();
    return this.get(this.request.method(Request.POST).body().set(json).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_CREATED).as(JsonResponse.class).json().readObject().getInt("id"));
  }

  @Override @NotNull(message = "pull comment is never NULL") public PullComment reply(@NotNull(message = "text can\'t be NULL") final String text, @NotNull(message = "comment can\'t be NULL") final int comment) throws IOException {
    throw new UnsupportedOperationException("Reply not yet implemented.");
  }

  @Override public void remove(final int number) throws IOException {
    this.request.uri().path(String.valueOf(number)).back().method(Request.DELETE).fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}