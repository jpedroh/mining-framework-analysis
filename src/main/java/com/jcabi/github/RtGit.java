package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Github Git.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "owner" }) public final class RtGit implements Git {
  /**
     * Repository.
     */
  private final transient Repo owner;

  /**
     * RESTful entry.
     */
  private final transient Request entry;

  /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
  public RtGit(final Request req) {
    this.entry = req;
    this.owner = repo;
  }

  @Override public Repo repo() {
    return this.owner;
  }

  @Override public Blobs blobs() throws IOException {
    return new RtBlobs(this.entry, this.repo());
  }

  @Override public Commits commits() {
    throw new UnsupportedOperationException("Commits not yet implemented");
  }

  @Override public References references() {
    return new RtReferences(this.entry, this.owner);
  }

  @Override public Tags tags() {
    return new RtTags(this.entry, this.owner);
  }

  @Override public Trees trees() {
    throw new UnsupportedOperationException("Trees not yet implemented");
  }
}