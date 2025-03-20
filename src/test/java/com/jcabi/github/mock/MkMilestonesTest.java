package com.jcabi.github.mock;
import com.jcabi.github.Milestone;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.immutable.ArrayMap;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for MkMilestones.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkMilestonesTest {
  /**
     * This tests that MkMilestones can return its owner repo.
     * @throws Exception - if something goes wrong.
     * @todo #111 Method repo() in MkMilestones has to be implemented.
     *  Until then, this test is ignored.
     */
  @Test public void returnsRepo() throws Exception {
    final Repo repo = this.repo();
    final Repo owner = repo.milestones().repo();
    MatcherAssert.assertThat(repo, Matchers.is(owner));
  }

  /**
     * This tests that MkMilestones can create a MkMilestone.
     * @throws Exception - if something goes wrong.
     */
  @Test public void createsMilestone() throws Exception {
    final Milestones milestones = this.repo().milestones();
    final Milestone milestone = milestones.create("test milestone");
    MatcherAssert.assertThat(milestone, Matchers.notNullValue());
    MatcherAssert.assertThat(milestones.create("another milestone"), Matchers.notNullValue());
  }

  /**
     * This tests that MkMilestones can return a certain MkMilestone, by number.
     * @throws Exception - if something goes wrong.
     */
  @Test public void getsMilestone() throws Exception {
    final Milestones milestones = this.repo().milestones();
    final Milestone created = milestones.create("test");
    MatcherAssert.assertThat(milestones.get(created.number()), Matchers.notNullValue());
  }

  /**
     * This tests that MkMilestones can remove a certain MkMilestone, by number.
     * @throws Exception - if something goes wrong.
     * @todo #111 Method remove() in MkMilestones has to be implemented.
     *  Until then, this test is ignored.
     */
  @Test @Ignore public void removesMilestone() throws Exception {
    final Milestones milestones = this.repo().milestones();
    final Milestone created = milestones.create("testTitle");
    milestones.remove(created.number());
    MatcherAssert.assertThat(milestones.get(created.number()), Matchers.nullValue());
  }

  /**
     * This tests that the iterate(Map<String, String> params)
     * method in MkMilestones works fine.
     * @throws Exception - if something goes wrong.
     * @todo #111 Method iterate()
     *  in MkMilestones has to be implemented.
     *  Until then, this test is ignored.
     */
  @Test @Ignore public void iteratesMilestones() throws Exception {
    final Milestones milestones = this.repo().milestones();
    milestones.create("testMilestone");
    MatcherAssert.assertThat(milestones.iterate(new ArrayMap<String, String>()), Matchers.<Milestone>iterableWithSize(1));
  }

  /**
     * Repo for testing.
     * @return Repo
     * @throws Exception - if something goes wrong.
     */
  private Repo repo() throws Exception {
    return new MkGithub().repos().create(Json.createObjectBuilder().add("name", "test").build());
  }
}