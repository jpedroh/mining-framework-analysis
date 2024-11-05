package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "ghub", "entry", "coords" }) @SuppressWarnings(value = { "PMD.TooManyMethods" }) final class RtRepo implements Repo {
  private final transient Github ghub;

  private final transient Request entry;

  private final transient Request request;

  private final transient Coordinates coords;

  RtRepo(final Github github, final Request req, final Coordinates crd) {
    this.ghub = github;
    this.entry = req;
    this.coords = crd;
    this.request = this.entry.uri().path("/repos").path(this.coords.user()).path(this.coords.repo()).back();
  }

  @Override public String toString() {
    return this.coords.toString();
  }

  @Override public Github github() {
    return this.ghub;
  }

  @Override public Coordinates coordinates() {
    return this.coords;
  }

  @Override public Issues issues() {
    return new RtIssues(this.entry, this);
  }

  @Override public Milestones milestones() {
    return new RtMilestones(this.entry, this);
  }

  @Override public Pulls pulls() {
    return new RtPulls(this.entry, this);
  }

  @Override public Hooks hooks() {
    return new RtHooks(this.entry, this);
  }

  @Override public Iterable<Event> events() {
    return new RtPagination<Event>(this.request.uri().path("/issues/events").back(), new RtPagination.Mapping<Event>() {
      @Override public Event map(final JsonObject object) {
        return new RtEvent(RtRepo.this.entry, RtRepo.this, object.getInt("id"));
      }
    });
  }

  @Override public Labels labels() {
    return new RtLabels(this.entry, this);
  }

  @Override public Assignees assignees() {
    return new RtAssignees(this, this.entry);
  }

  @Override public Releases releases() {
    return new RtReleases(this.entry, this);
  }

  @Override public DeployKeys keys() {
    return new RtDeployKeys(this);
  }

  @Override public Forks forks() {
    return new RtForks(this);
  }

  @Override public void patch(@NotNull(message = "JSON is never NULL") final JsonObject json) throws IOException {
    new RtJson(this.request).patch(json);
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }

  @Override public RepoCommits commits() {
    return new RtRepoCommits(this.entry, this.coords);
  }
}