package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Github pull request.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "request", "owner", "num" }) @SuppressWarnings(value = { "PMD.TooManyMethods" }) final class RtPull implements Pull {
  /**
     * API entry point.
     */
  private final transient Request entry;

  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Repository we're in.
     */
  private final transient Repo owner;

  /**
     * Pull request number.
     */
  private final transient int num;

  /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param number Number of the get
     */
  RtPull(final Request req, final Repo repo, final int number) {
    this.entry = req;
    final Coordinates coords = repo.coordinates();
    this.request = this.entry.uri().path("/repos").path(coords.user()).path(coords.repo()).path("/pulls").path(Integer.toString(number)).back();
    this.owner = repo;
    this.num = number;
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public Repo repo() {
    return this.owner;
  }

  @Override public int number() {
    return this.num;
  }

  @Override public Iterable<Commit> commits() throws IOException {
    return new RtPagination<Commit>(this.request.uri().path("/commits").back(), new RtValuePagination.Mapping<Commit, JsonObject>() {
      @Override public Commit map(final JsonObject object) {
        return new RtCommit(RtPull.this.entry, RtPull.this.owner, object.getString("sha"));
      }
    });
  }

  @Override public Iterable<JsonObject> files() throws IOException {
    return new RtPagination<JsonObject>(this.request.uri().path("/files").back(), RtPagination.COPYING);
  }

  @Override public void merge(final String msg) throws IOException {
    final JsonStructure json = Json.createObjectBuilder().add("commit_message", msg).build();
    this.merge(json).assertStatus(HttpURLConnection.HTTP_OK);
  }

  @Override public MergeState merge(final String msg, final String sha) throws IOException {
    final JsonObjectBuilder builder = Json.createObjectBuilder().add("commit_message", msg).add("sha", sha);
    final RestResponse response = this.merge(builder.build()).assertStatus(Matchers.isOneOf(HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_BAD_METHOD, HttpURLConnection.HTTP_CONFLICT));
    final MergeState mergeState;
    switch (response.status()) {
      case HttpURLConnection.HTTP_OK:
      mergeState = MergeState.SUCCESS;
      break;
      case HttpURLConnection.HTTP_BAD_METHOD:
      mergeState = MergeState.NOT_MERGEABLE;
      break;
      default:
      mergeState = MergeState.BAD_HEAD;
      break;
    }
    return mergeState;
  }

  @Override public PullComments comments() throws IOException {
    return new RtPullComments(this.entry, this);
  }

  @Override public PullRef base() throws IOException {
    return new RtPullRef(this.owner.github(), this.json().getJsonObject("base"));
  }

  @Override public PullRef head() throws IOException {
    return new RtPullRef(this.owner.github(), this.json().getJsonObject("head"));
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }

  @Override public void patch(final JsonObject json) throws IOException {
    new RtJson(this.request).patch(json);
  }

  @Override public int compareTo(final Pull pull) {
    return this.number() - pull.number();
  }

  /**
     * Helper method for merge operations.
     * @param payload The JSON payload for the merge
     * @return Response received from GitHub
     * @throws IOException If there is any I/O problem
     */
  private RestResponse merge(final JsonStructure payload) throws IOException {
    return this.request.uri().path("/merge").back().body().set(payload).back().method(Request.PUT).fetch().as(RestResponse.class);
  }
}