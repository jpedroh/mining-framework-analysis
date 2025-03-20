package com.jcabi.github;
import com.jcabi.aspects.Tv;
import com.jcabi.immutable.ArrayMap;
import javax.json.Json;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link Milestones}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 *
 * @todo #1:30min Implement integration tests for Milestones.
 *  Now these tests are ignored
 */
public final class RtMilestonesITCase {
  /**
     * Test repos.
     */
  private static Repos repos;

  /**
     * Test repo.
     */
  private static Repo repo;

  /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
  @BeforeClass public static void setUp() throws Exception {
    final String key = System.getProperty("failsafe.github.key");
    Assume.assumeThat(key, Matchers.notNullValue());
    final Github github = new RtGithub(key);
    repos = github.repos();
    repo = repos.create(Json.createObjectBuilder().add("name", RandomStringUtils.randomAlphanumeric(Tv.TEN)).add("auto_init", true).build());
  }

  /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
  @AfterClass public static void tearDown() throws Exception {
    if (repos != null && repo != null) {
      repos.remove(repo.coordinates());
    }
  }

  /**
     * RtMilestones can iterate milestones.
     * @throws Exception If some problem inside
     */
  @Test public void iteratesIssues() throws Exception {
    final Milestones milestones = milestones();
    final Milestone milestone = milestones.create(RandomStringUtils.randomAlphabetic(10));
    try {
      MatcherAssert.assertThat(milestones.iterate(Collections.singletonMap("state", "all")), Matchers.hasItem(milestone));
    }  finally {
      milestones.remove(milestone.number());
    }
  }

  /**
     * RtMilestones can create a new milestone.
     * @throws Exception If some problem inside
     */
  @Test public void createsNewMilestone() throws Exception {
    final Milestones milestones = milestones();
    final Milestone milestone = milestones.create(RandomStringUtils.randomAlphabetic(10));
    try {
      MatcherAssert.assertThat(milestones.iterate(Collections.singletonMap("state", "all")), Matchers.not(Matchers.emptyIterable()));
    }  finally {
      milestones.remove(milestone.number());
    }
  }

  /**
     * RtMilestones can remove a milestone.
     * @throws Exception if some problem inside
     */
  @Test public void deleteMilestone() throws Exception {
    final Milestones milestones = repo.milestones();
    final Milestone milestone = milestones.create("a milestones");
    MatcherAssert.assertThat(milestones.iterate(new ArrayMap<String, String>()), Matchers.hasItem(milestone));
    milestones.remove(milestone.number());
    MatcherAssert.assertThat(milestones.iterate(new ArrayMap<String, String>()), Matchers.not(Matchers.hasItem(milestone)));
  }
}