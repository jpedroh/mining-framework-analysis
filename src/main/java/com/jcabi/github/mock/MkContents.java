package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Commit;
import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "self", "coords" }) public final class MkContents implements Contents {
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
  public MkContents(final MkStorage stg, final String login, final Coordinates rep) throws IOException {
    this.storage = stg;
    this.self = login;
    this.coords = rep;
  }

  @Override public Repo repo() {
    return new MkRepo(this.storage, this.self, this.coords);
  }

  @Override public Content readme() {
    return new MkContent();
  }

  @Override public Content create(final String path, final String message, final String content) throws IOException {
    throw new UnsupportedOperationException("Create not yet implemented.");
  }

  @Override public Commit remove(final String path, final String message, final String sha) throws IOException {
    throw new UnsupportedOperationException("Remove not yet implemented.");
  }
}