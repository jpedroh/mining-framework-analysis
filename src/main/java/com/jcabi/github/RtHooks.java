package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import com.rexsl.test.response.JsonResponse;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;

@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "entry", "owner", "request" }) public final class RtHooks implements Hooks {
  private final transient Request entry;

  private final transient Request request;

  private final transient Repo owner;

  public RtHooks(final Request req, final Repo repo) {
    this.entry = req;
    final Coordinates coords = repo.coordinates();
    this.request = this.entry.uri().path("/repos").path(coords.user()).path(coords.repo()).path("/hooks").back();
    this.owner = repo;
  }

  @Override public Repo repo() {
    return this.owner;
  }

  @Override public Iterable<Hook> iterate() {
    return Collections.emptyList();
  }

  @Override public void remove(final int number) throws IOException {
    this.request.method(Request.DELETE).uri().path(Integer.toString(number)).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }

  @Override public Hook get(final int number) {
    return new RtHook(this.entry, this.owner, number);
  }

  @Override public Hook create(@NotNull(message = "name can\'t be NULL") final String name, @NotNull(message = "config can\'t be NULL") final Map<String, String> config) throws IOException {
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    for (final Map.Entry<String, String> entr : config.entrySet()) {
      builder.add(entr.getKey(), entr.getValue());
    }
    final JsonStructure json = Json.createObjectBuilder().add("name", name).add("config", builder).build();
    return this.get(this.request.method(Request.POST).body().set(json).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_CREATED).as(JsonResponse.class).json().readObject().getInt("id"));
  }
}