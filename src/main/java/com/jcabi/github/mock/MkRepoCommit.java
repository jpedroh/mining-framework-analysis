package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommit;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Mock Github commit.
 * @author Carlos Crespo (carlos.a.crespo@gmail.com)
 * @version $Id$
 * @todo #166 Should implement the compareTo() method in MkRepoCommit.
 *  Implement the method and a unit test for it.
 *  Once implemented please remove this puzzle.
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "repository", "hash" }) final class MkRepoCommit implements RepoCommit {
  /**
     * Commit SHA.
     */
  private final transient String hash;

  /**
     * The storage.
     */
  private final transient MkStorage storage;

  /**
     * The repository.
     */
  private final transient Repo repository;

  /**
     * Public ctor.
     * @param stg The storage
     * @param repo The repository
     * @param sha Commit SHA
     */
  MkRepoCommit(@NotNull(message = "storage must not be NULL") final MkStorage stg, @NotNull(message = "repo must not be null") final Repo repo, @NotNull(message = "sha must not be NULL") final String sha) {
    this.storage = stg;
    this.repository = repo;
    this.hash = sha;
  }

  @Override public int compareTo(@NotNull(message = "other can\'t be NULL") final RepoCommit other) {
    return new CompareToBuilder().append(this.repo().coordinates(), other.repo().coordinates()).append(this.sha(), other.sha()).build();
  }

  @Override @NotNull(message = "JSON is never NULL") public JsonObject json() throws IOException {
    return new JsonNode(this.storage.xml().nodes(String.format("/github/repos/repo[@coords=\'%s\']/commits/commit[sha=\'%s\']", this.repo().coordinates(), this.hash)).get(0)).json();
  }

  @Override @NotNull(message = "repository is never NULL") public Repo repo() {
    return this.repository;
  }

  @Override @NotNull(message = "sha should no be NULL") public String sha() {
    return this.hash;
  }
}