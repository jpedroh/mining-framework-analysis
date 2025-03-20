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
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "request") final class RtRepoCommits implements RepoCommits {
  /**
     * RESTful request for the commits.
     */
  private final transient Request request;

  /**
     * RESTful API entry point.
     */
  private final transient Request entry;

  /**
     * Github.
     */
  private final transient Github github;

  /**
     * Repository.
     */
  private final transient Repo 
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/left.java
  repo
=======
  owner
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/right.java
  ;

  /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository coordinates
     */
  RtRepoCommits(final Request req, final Repo repo) {
    this.entry = req;
    this.owner = repo;
    this.request = req.uri().path("/repos").path(repo.coordinates().user()).path(repo.coordinates().repo()).path("/commits").back();
    this.github = new RtGithub(this.request);
    this.repo = new RtRepo(this.github, this.request, repo);
  }

  @Override public Iterable<Commit> iterate() {
    return new RtPagination<Commit>(this.request, new RtPagination.Mapping<Commit>() {
      @Override public Commit map(final JsonObject object) {
        return get(object.getString("sha"));
      }
    });
  }

  @Override public Commit get(final String sha) {
    return new RtCommit(this.entry, this.
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/left.java
    repo
=======
    owner
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/right.java
    , sha);
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }
}