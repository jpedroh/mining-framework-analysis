package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @todo #913:30min Implement operations RtUser.markAsRead().
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "ghub", "request" }) @SuppressWarnings(value = { "PMD.TooManyMethods" }) final class RtUser implements User {
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
  RtUser(@NotNull(message = "github can\'t be NULL") final Github github, @NotNull(message = "req can\'t be NULL") final Request req) {
    this(github, req, "");
  }

  /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param login User identity/identity
     */
  RtUser(@NotNull(message = "github can\'t be NULL") final Github github, @NotNull(message = "req can\'t be NULL") final Request req, @NotNull(message = "login can\'t be NULL") final String login) {
    this.ghub = github;
    if (login.isEmpty()) {
      this.request = req.uri().path("/user").back();
    } else {
      this.request = req.uri().path("/users").path(login).back();
    }
    this.self = login;
  }

  @Override @NotNull(message = "toString is never NULL") public String toString() {
    return this.request.uri().get().toString();
  }

  @Override @NotNull(message = "github is never NULL") public Github github() {
    return this.ghub;
  }

  @Override @NotNull(message = "login is never NULL") public String login() throws IOException {
    final String login;
    if (this.self.isEmpty()) {
      login = this.json().getString("login");
    } else {
      login = this.self;
    }
    return login;
  }

  @Override @NotNull(message = "organizations is never NULL") public Organizations organizations() {
    return new RtOrganizations(this.ghub, this.ghub.entry(), this);
  }

  @Override @NotNull(message = "PublicKeys is never NULL") public PublicKeys keys() {
    return new RtPublicKeys(this.ghub.entry(), this);
  }

  @Override @NotNull(message = "user emails is never NULL") public UserEmails emails() {
    return new RtUserEmails(this.ghub.entry());
  }

  @Override public List<Notification> notifications() throws IOException {
    final List<Notification> list = new LinkedList<Notification>();
    final JsonResponse resp = this.github().entry().uri().path("notifications").back().fetch().as(JsonResponse.class);
    final JsonArray array = resp.json().readArray();
    for (final JsonValue value : array) {
      final JsonObject notif = (JsonObject) value;
      list.add(this.createNotification(notif));
    }
    return list;
  }

  @Override public void markAsRead(final Date lastread) {
  }

  @Override @NotNull(message = "JSON is never NULL") public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }

  @Override public void patch(@NotNull(message = "JSON is never NULL") final JsonObject json) throws IOException {
    new RtJson(this.request).patch(json);
  }

  /**
     * Creates RtNotification object with the id from notifobj.
     * @param notifobj JSON object with notification data.
     * @return RtNotification object with the id from notifobj.
     */
  private Notification createNotification(final JsonObject notifobj) {
    return new RtNotification(Long.parseLong(notifobj.getString("id")));
  }
}