package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URL;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable @SuppressWarnings(value = { "PMD.TooManyMethods" }) public interface User extends JsonReadable, JsonPatchable {
  @NotNull(message = "Github is never NULL") Github github();

  @NotNull(message = "login is never NULL") String login() throws IOException;

  @NotNull(message = "organizations is never NULL") Organizations organizations();

  @Immutable @ToString @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "user") final class Smart implements User {
    private final transient User user;

    public Smart(final User usr) {
      this.user = usr;
    }

    @SuppressWarnings(value = { "PMD.ShortMethodName" }) public int id() throws IOException {
      return this.user.json().getJsonNumber("id").intValue();
    }

    public URL avatarUrl() throws IOException {
      return new URL(new SmartJson(this).text("avatar_url"));
    }

    public URL url() throws IOException {
      return new URL(new SmartJson(this).text("url"));
    }

    public String name() throws IOException {
      final JsonObject json = this.json();
      if (!json.containsKey("name")) {
        throw new IllegalStateException(String.format("User %s doesn\'t have a name specified in his/her Github account; use #hasName() first.", this.login()));
      }
      return json.getString("name");
    }

    public boolean hasName() throws IOException {
      return this.json().containsKey("name");
    }

    public String company() throws IOException {
      return new SmartJson(this).text("company");
    }

    public String location() throws IOException {
      return new SmartJson(this).text("location");
    }

    public String email() throws IOException {
      return new SmartJson(this).text("email");
    }

    @Override public Github github() {
      return this.user.github();
    }

    @Override public String login() throws IOException {
      return this.user.login();
    }

    @Override public Organizations organizations() {
      return this.user.organizations();
    }

    @Override public JsonObject json() throws IOException {
      return this.user.json();
    }

    @Override public void patch(final JsonObject json) throws IOException {
      this.user.patch(json);
    }

    @Override public PublicKeys keys() {
      return this.user.keys();
    }
  }

  @NotNull(message = "keys is never NULL") PublicKeys keys();
}