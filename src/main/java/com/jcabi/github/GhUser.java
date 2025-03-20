package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @todo #2:30min Organizations of a user.
 *  Let's implements a new method organizations(),
 *  which should return an instance of interface Organisations.
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "ghub", "request" }) final class GhUser implements User {
  /**
     * Github.
     */
  private final transient Github ghub;

  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Login of the user.
     */
  private final transient String self;

  /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
  GhUser(final Github github, final Request req) {
    this(github, req, "");
  }

  /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param login User identity/identity
     */
  GhUser(final Github github, final Request req, final String login) {
    this.ghub = github;
    if (login.isEmpty()) {
      this.request = req.uri().path("/user").back();
    } else {
      this.request = req.uri().path("/users").path(login).back();
    }
    this.self = login;
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public Github github() {
    return this.ghub;
  }

  @Override public String login() throws IOException {
    final String login;
    if (this.self.isEmpty()) {
      login = this.json().getString("login");
    } else {
      login = this.self;
    }
    return login;
  }

  @Override public Organizations organizations() {
    return null;
  }

  @Override public JsonObject json() throws IOException {
    return new GhJson(this.request).fetch();
  }

  @Override public void patch(@NotNull(message = "JSON is never NULL") final JsonObject json) throws IOException {
    new GhJson(this.request).patch(json);
  }
}