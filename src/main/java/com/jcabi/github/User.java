package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URL;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github user.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #1:1hr Fetch list of emails of a user. Let's implement
 *  a new method emails() that returns an instance of class UserEmails with
 *  a few methods: 1) iterate() returning a list of strings, 2) add(String),
 *  and 3) remove(String). Let's use the
 *  new response format suggested by Github:
 *  http://developer.github.com/v3/users/emails/#list-email-addresses-for-a-user
 *  This new UserEmails interface should be implemented by GhUserEmails,
 *  tested in a unit and integration tests. Besides that, we should
 *  implement MkUserEmails class.
 * @todo #1:1hr Public keys of a user. Let's implement a new method
 *  keys(), which should return an instance of interface PublicKeys. This
 *  interface should have at least methods 1) iterate() to list all public
 *  keys of a user, 2) get(String) to get a single public key, 3) remove(String)
 *  to remove a key. Every key should be an instance of interface PublicKey,
 *  extending JsonReadable and JsonPatchable. All of the new classes should
 *  be implemented with GhPublicKeys and GhPublicKey classes. We should
 *  create integration and unit tests, and implement MkPublicKeys
 *  and MkPublicKey classes.
 * @see <a href="http://developer.github.com/v3/users/">User API</a>
 * @since 0.1
 */
@Immutable @SuppressWarnings(value = { "PMD.TooManyMethods" }) public interface User extends JsonReadable, JsonPatchable {
  /**
     * Github we're in.
     * @return Github
     * @since 0.4
     */
  @NotNull(message = "Github is never NULL") Github github();

  /**
     * Get his login.
     * @return Login name
     * @throws IOException If it fails
     */
  @NotNull(message = "login is never NULL") String login() throws IOException;

  /**
     * Get his organizations.
     * @return Organizations organizations
     */
  @NotNull(message = "organizations is never NULL") Organizations organizations();

  /**
     * Get his keys.
     * @return PublicKeys keys
     */
  @NotNull(message = "keys is never NULL") PublicKeys keys();

  @Immutable @ToString @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "user") final class Smart implements User {
    /**
         * Encapsulated user.
         */
    private final transient User user;

    /**
         * Public ctor.
         * @param usr User
         */
    public Smart(final User usr) {
      this.user = usr;
    }

    /**
         * Get his ID.
         * @return Unique user ID
         * @throws IOException If it fails
         * @checkstyle MethodName (3 lines)
         */
    @SuppressWarnings(value = { "PMD.ShortMethodName" }) public int id() throws IOException {
      return this.user.json().getJsonNumber("id").intValue();
    }

    /**
         * Get his avatar URL.
         * @return URL of the avatar
         * @throws IOException If it fails
         */
    public URL avatarUrl() throws IOException {
      return new URL(new SmartJson(this).text("avatar_url"));
    }

    /**
         * Get his URL.
         * @return URL of the user
         * @throws IOException If it fails
         */
    public URL url() throws IOException {
      return new URL(new SmartJson(this).text("url"));
    }

    /**
         * Get his name.
         * @return User name
         * @throws IOException If it fails
         */
    public String name() throws IOException {
      final JsonObject json = this.json();
      if (!json.containsKey("name")) {
        throw new IllegalStateException(String.format("User %s doesn\'t have a name specified in his/her Github account; use #hasName() first.", this.login()));
      }
      return json.getString("name");
    }

    /**
         * Check if user has name.
         * @return True if user has name
         * @throws IOException If it fails
         */
    public boolean hasName() throws IOException {
      return this.json().containsKey("name");
    }

    /**
         * Get his company.
         * @return Company name
         * @throws IOException If it fails
         */
    public String company() throws IOException {
      return new SmartJson(this).text("company");
    }

    /**
         * Get his location.
         * @return Location name
         * @throws IOException If it fails
         */
    public String location() throws IOException {
      return new SmartJson(this).text("location");
    }

    /**
         * Get his email.
         * @return Email
         * @throws IOException If it fails
         */
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

    @Override public PublicKeys keys() {
      return this.user.keys();
    }

    @Override public JsonObject json() throws IOException {
      return this.user.json();
    }

    @Override public void patch(final JsonObject json) throws IOException {
      this.user.patch(json);
    }
  }
}