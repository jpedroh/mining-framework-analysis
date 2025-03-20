package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @todo #117 RtRepoCommits should be able to fetch commits. Let's
 *  implement this method. When done, remove this puzzle and
 *  Ignore annotation from a test for the method.
 * @todo #117 RtRepoCommits should be able to get commit. Let's implement
 *  this method. When done, remove this puzzle and Ignore annotation
 *  from a test for the method.
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "request") final class RtRepoCommits implements RepoCommits {
  /**
     * RESTful API entry point.
     */
  private final transient Request entry;

  /**
     * RESTful request for the commits.
     */
  private final transient Request request;

  /**
     * Parent repository.
     */
  private final transient Repo owner;

  /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository coordinates
     */
  RtRepoCommits(final Request req, final Repo repo) {
    this.entry = req;
    this.owner = repo;
    this.request = req.uri().path("/repos").path(repo.coordinates().user()).path(repo.coordinates().repo()).path("/commits").back();
  }

  @Override public Iterable<Commit> iterate() {
    throw new UnsupportedOperationException();
  }

  @Override public Commit get(final String sha) {
    return new RtCommit(this.entry, this.owner, sha);
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }
}