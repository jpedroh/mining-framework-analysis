package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github deploy key.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "request") public final class RtDeployKey implements DeployKey {
  /**
     * RESTful API request for this deploy key.
     */
  private final transient Request request;

  /**
     * Id.
     */
  private final transient int key;

  /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param number Id
     * @param repo Repository
     */
  RtDeployKey(final Request req, final int number, final Repo repo) {
    this.key = number;
    this.request = req.uri().path("/repos").path(repo.coordinates().user()).path(repo.coordinates().repo()).path("/keys").path(String.valueOf(number)).back();
  }

  @Override public int number() {
    return this.key;
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }

  @Override public void edit(final String title, final String value) throws IOException {
    this.request.method(Request.PATCH).body().formParam("title", title).formParam("key", value).back().fetch();
  }

  @Override public void remove() throws IOException {
    this.request.method(Request.DELETE).fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}