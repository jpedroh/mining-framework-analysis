package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable public final class RtContentsTest {
  /**
     * RtContents can fetch the default branch readme file.
     * @throws Exception if some problem inside.
     */
  @Test public void canFetchReadmeFile() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{\"path\": \"README.md\"}")).start();
    final Contents contents = new RtContents(new FakeRequest(), RtContentsTest.repo());
    MatcherAssert.assertThat(contents.readme(), Matchers.notNullValue());
    container.stop();
  }

  /**
     * RtContents can fetch the readme file from the specified branch.
     *
     * @todo #119 RtContents should fetch the readme file for any branch.
     *  Let's implement a test here and a method of RtContents.
     *  The method should receive the branch name as a parameter.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test public void canFetchReadmeFileFromSpecifiedBranch() throws IOException {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{\"name\": \"README.md\"}")).start();
    final Contents contents = new RtContents(new FakeRequest(), RtContentsTest.repo());
    MatcherAssert.assertThat(contents.readme("somebranch"), Matchers.notNullValue());
    container.stop();
  }

  /**
     * RtContents can fetch files from the repository.
     *
     * @todo #119 RtContents should be able to fetch files from the repository.
     *  Let's implement a test here and a method of RtContents.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canFetchFilesFromRepository() {
  }

  /**
     * RtContents can create a file in the repository.
     *
     * @todo #119 RtContents should be able to create files in the repository.
     *  Let's implement a test here and a method of RtContents.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canCreateFilesFromRepository() {
  }

  /**
     * RtContents can delete files from the repository.
     *
     * @todo #119 RtContents should be able to delete files from the repository.
     *  Let's implement a test here and a method of RtContents.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canDeleteFilesFromRepository() {
  }

  /**
     * RtContents can update files into the repository.
     *
     * @todo #119 RtContents should be able to update files into the repository.
     *  Let's implement a test here and a method of RtContents.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canUpdateFilesInRepository() {
  }

  /**
     * Create and return repo for testing.
     * @return Repo
     */
  private static Repo repo() {
    final Repo repo = Mockito.mock(Repo.class);
    Mockito.doReturn(new Coordinates.Simple("test", "contents")).when(repo).coordinates();
    return repo;
  }
}