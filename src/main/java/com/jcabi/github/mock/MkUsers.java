package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Github;
import com.jcabi.github.User;
import com.jcabi.github.Users;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github users.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "himself" }) final class MkUsers implements Users {
  /**
     * Storage.
     */
  private final transient MkStorage storage;

  /**
     * Login of the user logged in.
     */
  private final transient String himself;

  /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
  MkUsers(@NotNull(message = "stg can\'t be NULL") final MkStorage stg, @NotNull(message = "login can\'t be NULL") final String login) throws IOException {
    this.storage = stg;
    this.himself = login;
    this.storage.apply(new Directives().xpath("/github").addIf("users"));
  }

  @Override @NotNull(message = "Github is never NULL") public Github github() {
    return new MkGithub(this.storage, this.himself);
  }

  @Override @NotNull(message = "self is never NULL") public User self() {
    return this.get(this.himself);
  }

  @Override @NotNull(message = "user is never NULL") public User get(@NotNull(message = "login is never NULL") final String login) {
    try {
      return new MkUser(this.storage, login);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override @NotNull(message = "Iterable is never NULL") public Iterable<User> iterate(
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/cfdadda97e7adad8170ed49416adb67467b76313/src/main/java/com/jcabi/github/mock/MkUsers.java/left.java
  @NotNull(message = "login can\'t be NULL") final String login
=======
  @NotNull(message = "identifier is never NULL") final String identifier
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/cfdadda97e7adad8170ed49416adb67467b76313/src/main/java/com/jcabi/github/mock/MkUsers.java/right.java
  ) {
    return new MkIterable<User>(this.storage, "/github/users/user", new MkIterable.Mapping<User>() {
      @Override public User map(final XML xml) {
        return MkUsers.this.get(xml.xpath("login/text()").get(0));
      }
    });
  }
}