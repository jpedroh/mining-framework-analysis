package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github repository.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @todo #1:1hr Assignees API should be implemented. Let's add a method
 *  assignees() to this class returning an instance of interface Assignees.
 *  This interface should have at least two methods: 1) iterate() returning
 *  a list of Users and 2) check(String) returning TRUE if provided
 *  login can be used as an assignee in repository. New interface should
 *  be implemented by GhAssignees class and tested in unit and integration
 *  tests. Moreover, we should implement MkAssignees class. See
 *  http://developer.github.com/v3/issues/assignees/
 * @since 0.1
 */
@Immutable @SuppressWarnings(value = { "PMD.TooManyMethods" }) public interface Repo extends JsonReadable, JsonPatchable {
  /**
     * Get its owner.
     * @return Github
     */
  @NotNull(message = "github is never NULL") Github github();

  /**
     * Get its coordinates.
     * @return Coordinates
     */
  @NotNull(message = "coordinates is never NULL") Coordinates coordinates();

  /**
     * Iterate issues.
     * @return Issues
     */
  @NotNull(message = "iterator of issues is never NULL") Issues issues();

  /**
     * Iterate milestones.
     * @return Milestones
     * @since 0.7
     */
  @NotNull(message = "iterator of milestones is never NULL") Milestones milestones();

  /**
     * Pull requests.
     * @return Pulls
     */
  @NotNull(message = "iterator of pull requests is never NULL") Pulls pulls();

  /**
     * Get all events for the repository.
     * @return Events
     * @see <a href="http://developer.github.com/v3/issues/events/#list-events-for-a-repository">List Events for a Repository</a>
     */
  @NotNull(message = "iterable of events is never NULL") Iterable<Event> events();

  /**
     * Get all labels of the repo.
     * @return Labels
     * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
     */
  @NotNull(message = "labels are never NULL") Labels labels();

  /**
     * Get all available assignees to which issues may be assigned.
     * @return Assignees
     * @see @see <a href="http://developer.github.com/v3/issues/assignees/">Assignees API</a>
     */
  @NotNull(message = "labels are never NULL") Assignees assignees();

  @Immutable @ToString @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = "repo") final class Smart implements Repo {
    /**
         * Encapsulated Repo.
         */
    private final transient Repo repo;

    /**
         * Public ctor.
         * @param rep Repo
         */
    public Smart(final Repo rep) {
      this.repo = rep;
    }

    /**
         * Get its description.
         * @return Description
         * @throws IOException If there is any I/O problem
         */
    public String description() throws IOException {
      return new SmartJson(this).text("description");
    }

    @Override public Github github() {
      return this.repo.github();
    }

    @Override public Coordinates coordinates() {
      return this.repo.coordinates();
    }

    @Override public Issues issues() {
      return this.repo.issues();
    }

    @Override public Milestones milestones() {
      return this.repo.milestones();
    }

    @Override public Pulls pulls() {
      return this.repo.pulls();
    }

    @Override public Iterable<Event> events() {
      return this.repo.events();
    }

    @Override public Labels labels() {
      return this.repo.labels();
    }

    @Override public Assignees assignees() {
      return this.repo.assignees();
    }

    @Override public void patch(final JsonObject json) throws IOException {
      this.repo.patch(json);
    }

    @Override public JsonObject json() throws IOException {
      return this.repo.json();
    }
  }
}