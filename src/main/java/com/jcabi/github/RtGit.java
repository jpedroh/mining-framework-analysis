package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import javax.validation.constraints.NotNull;
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
     * @param repo Repository
     * @param req Entry request
     */
  public RtGit(final Request req, final Repo repo) {
    this.entry = req;
    this.owner = repo;
  }

  @Override @NotNull(message = "repository can\'t be NULL") public Repo repo() {
    return this.owner;
  }

  @Override @NotNull(message = "blobs can\'t be NULL") public Blobs blobs() throws IOException {
    return new RtBlobs(this.entry, this.repo());
  }

  @Override @NotNull(message = "commits can\'t be NULL") public Commits commits() {
    throw new UnsupportedOperationException("Commits not yet implemented");
  }

  @Override @NotNull(message = "references can\'t be NULL") public References references() {
    return new RtReferences(this.entry, this.owner);
  }

  @Override @NotNull(message = "tags can\'t be NULL") public Tags tags() {
    return new RtTags(this.entry, this.owner);
  }

  @Override @NotNull(message = "trees can\'t be NULL") public Trees trees() {
    throw new UnsupportedOperationException("Trees not yet implemented");
  }
}