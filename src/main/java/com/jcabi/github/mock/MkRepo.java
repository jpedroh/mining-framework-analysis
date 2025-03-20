package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Assignees;
import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.DeployKeys;
import com.jcabi.github.Event;
import com.jcabi.github.Forks;
import com.jcabi.github.Github;
import com.jcabi.github.Hooks;
import com.jcabi.github.Issues;
import com.jcabi.github.Labels;
import com.jcabi.github.Milestones;
import com.jcabi.github.Pulls;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import com.jcabi.github.RepoCommits;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github repo.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @todo #9 Implement milestones() method.
 *  Please, implement milestones() method to return
 *  MkMilestones. Don't forget about unit tests
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "self", "coords" }) @SuppressWarnings(value = { "PMD.TooManyMethods" }) final class MkRepo implements Repo {
  /**
     * Storage.
     */
  private final transient MkStorage storage;

  /**
     * Login of the user logged in.
     */
  private final transient String self;

  /**
     * Repo coordinates.
     */
  private final transient Coordinates coords;

  /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param repo Repo name
     */
  MkRepo(final MkStorage stg, final String login, final Coordinates repo) {
    this.storage = stg;
    this.self = login;
    this.coords = repo;
  }

  @Override public Github github() {
    return new MkGithub(this.storage, this.self);
  }

  @Override public Coordinates coordinates() {
    return this.coords;
  }

  @Override public Issues issues() {
    try {
      return new MkIssues(this.storage, this.self, this.coords);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public Milestones milestones() {
    return null;
  }

  @Override public Pulls pulls() {
    try {
      return new MkPulls(this.storage, this.self, this.coords);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public Hooks hooks() {
    try {
      return new MkHooks(this.storage, this.self, this.coords);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public Iterable<Event> events() {
    return null;
  }

  @Override public Labels labels() {
    try {
      return new MkLabels(this.storage, this.self, this.coords);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public Assignees assignees() {
    throw new UnsupportedOperationException();
  }

  @Override public Releases releases() {
    try {
      return new MkReleases(this.storage, this.self, this.coords);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public Forks forks() {
    try {
      return new MkForks(this.storage, this.self, this.coords);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public DeployKeys keys() {
    try {
      return new MkDeployKeys(this.storage, this.self, this.coords);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public Contents contents() {
    try {
      return new MkContents(this.storage, this.self, this.coords);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override public void patch(final JsonObject json) throws IOException {
    new JsonPatch(this.storage).patch(this.xpath(), json);
  }

  @Override public RepoCommits commits() {
    return new MkRepoCommits(this.storage, this.coordinates());
  }

  @Override public JsonObject json() throws IOException {
    return new JsonNode(this.storage.xml().nodes(this.xpath()).get(0)).json();
  }

  /**
     * XPath of this element in XML tree.
     * @return XPath
     */
  private String xpath() {
    return String.format("/github/repos/repo[@coords=\'%s\']", this.coords);
  }
}