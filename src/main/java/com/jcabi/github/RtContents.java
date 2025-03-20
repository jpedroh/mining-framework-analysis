package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import lombok.EqualsAndHashCode;

/**
 * Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "owner", "request" }) public final class RtContents implements Contents {
  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Repository.
     */
  private final transient Repo owner;

  /**
     * Public ctor.
     * @param repo Repository
     * @param req Request
     */
  public RtContents(final Request req, final Repo repo) {
    this.owner = repo;
    final Coordinates coords = repo.coordinates();
    this.request = req.uri().path("/repos").path(coords.user()).path(coords.repo()).back();
  }

  @Override public Repo repo() {
    return this.owner;
  }

  @Override public Content readme() {
    return new RtReadme(this.request);
  }

  @Override public Content readme(final String branch) {
    return new RtReadme(this.request, branch);
  }
}