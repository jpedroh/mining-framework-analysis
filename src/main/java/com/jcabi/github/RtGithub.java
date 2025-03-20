package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.manifests.Manifests;
import com.rexsl.test.Request;
import com.rexsl.test.request.ApacheRequest;
import com.rexsl.test.response.JsonResponse;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github client, starting point to the entire library.
 *
 * <p>This is how you start communicating with Github API:
 *
 * <pre> Github github = new RtGithub(oauthKey);
 * Repo repo = github.repo("jcabi/jcabi-github");
 * Issues issues = repo.issues();
 * Issue issue = issues.post("issue title", "issue body");</pre>
 *
 * <p>It is strongly recommended to use
 * {@link com.rexsl.test.wire.RetryWire} to avoid
 * accidental I/O exceptions:
 *
 * <pre> Github github = new RtGithub(
 *   new RtGithub(oauthKey).entry().through(RetryWire.class)
 * );</pre>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = "request") public final class RtGithub implements Github {
  /**
     * Version of us.
     */
  private static final String USER_AGENT = String.format("jcabi-github %s %s %s", Manifests.read("JCabi-Version"), Manifests.read("JCabi-Build"), Manifests.read("JCabi-Date"));

  /**
     * Default request to start with.
     */
  private static final Request REQUEST = new ApacheRequest("https://api.github.com").header(HttpHeaders.USER_AGENT, RtGithub.USER_AGENT).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

  /**
     * REST request.
     */
  private final transient Request request;

  /**
     * Public ctor, for anonymous access to Github.
     * @since 0.4
     */
  public RtGithub() {
    this(RtGithub.REQUEST);
  }

  /**
     * Public ctor, for HTTP Basic Authentication.
     * @param user User name
     * @param pwd Password
     * @since 0.4
     */
  public RtGithub(@NotNull(message = "user name can\'t be NULL") final String user, @NotNull(message = "password can\'t be NULL") final String pwd) {
    this(RtGithub.REQUEST.uri().userInfo(String.format("%s:%s", user, pwd)).back());
  }

  /**
     * Public ctor, for authentication with OAuth2 token.
     * @param token OAuth token
     */
  public RtGithub(@NotNull(message = "token can\'t be NULL") final String token) {
    this(RtGithub.REQUEST.header(HttpHeaders.AUTHORIZATION, String.format("token %s", token)));
  }

  /**
     * Public ctor, with a custom request.
     * @param req Request to start from
     * @since 0.4
     */
  public RtGithub(@NotNull(message = "request can\'t be NULL") final Request req) {
    this.request = req;
  }

  @Override public Request entry() {
    return this.request;
  }

  @Override @NotNull(message = "repos is never NULL") public Repos repos() {
    return new RtRepos(this, this.request);
  }

  @Override @NotNull(message = "gists are never NULL") public Gists gists() {
    return new RtGists(this, this.request);
  }

  @Override @NotNull(message = "users are never NULL") public Users users() {
    return new RtUsers(this, this.request);
  }

  @Override public Limits limits() {
    return new RtLimits(this, this.request);
  }

  @Override public Search search() {
    return new RtSearch(this);
  }

  @Override public JsonObject meta() throws IOException {
    return this.request.uri().path("meta").back().fetch().as(JsonResponse.class).json().readObject();
  }

  @Override public JsonObject emojis() throws IOException {
    return this.request.uri().path("emojis").back().fetch().as(JsonResponse.class).json().readObject();
  }

  @Override public Markdown markdown() {
    return new RtMarkdown(this, this.request);
  }
}