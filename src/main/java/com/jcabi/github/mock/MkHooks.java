package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Hook;
import com.jcabi.github.Hooks;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github hooks.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @todo #166 Hooks mock should be implemented.
 *  Need to implement the methods of MkHooks: 1) iterate, returning
 *  a list of hooks, 2) create, which will create a new hook and
 *  3) get, which will fetch hook by id
 *  Don't forget to update the unit test class {@link MkHooks}.
 *  See http://developer.github.com/v3/repos/hooks/
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "self", "coords" }) public final class MkHooks implements Hooks {
  /**
     * Storage.
     */
  private final transient MkStorage storage;

  /**
     * Login of the user logged in.
     */
  private final transient String self;

  /**
     * Repo name.
     */
  private final transient Coordinates coords;

  /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
  public MkHooks(final MkStorage stg, final String login, final Coordinates rep) throws IOException {
    this.storage = stg;
    this.self = login;
    this.coords = rep;
    this.storage.apply(new Directives().xpath(String.format("/github/repos/repo[@coords=\'%s\']", this.coords)).addIf("hooks"));
  }

  @Override public Repo repo() {
    return new MkRepo(this.storage, this.self, this.coords);
  }

  @Override public Iterable<Hook> iterate() {
    return Collections.emptyList();
  }

  @Override public Hook get(final int number) {
    return new MkHook(this.storage, this.self, this.coords, number);
  }

  @Override public Hook create(final String name, final Map<String, String> config) {
    throw new UnsupportedOperationException("Create not yet implemented.");
  }

  @Override public void remove(final int number) throws IOException {
    throw new UnsupportedOperationException("Remove not yet implemented.");
  }
}