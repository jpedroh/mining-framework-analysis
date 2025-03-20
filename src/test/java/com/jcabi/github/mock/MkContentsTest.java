package com.jcabi.github.mock;
import com.jcabi.github.Contents;
import com.jcabi.github.Repo;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MkContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
public final class MkContentsTest {
  /**
     * MkContents can fetch the default branch readme file.
     * @throws Exception if some problem inside
     */
  @Test public void canFetchReadmeFile() throws Exception {
    final Contents contents = MkContentsTest.repo().contents();
    MatcherAssert.assertThat(contents.readme(), Matchers.notNullValue());
  }

  /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     * @todo #314 MkContents should support the creation of mock contents.
     *  This method should create a new instance of MkContent. Do not
     *  forget to implement a unit test for it here and remove the Ignore
     *  annotation.
     */
  @Test @Ignore public void canCreateFile() throws Exception {
  }

  /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     * @todo #311 MkContents should support the removal of mock contents.
     *  This method should return a new instance of MkCommit. Do not
     *  forget to implement a unit test for it here and remove the Ignore
     *  annotation.
     */
  @Test @Ignore public void canRemoveFile() throws Exception {
  }

  /**
     * Create a repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
  private static Repo repo() throws Exception {
    return new MkGithub().repos().create(Json.createObjectBuilder().add("name", "test").build());
  }
}