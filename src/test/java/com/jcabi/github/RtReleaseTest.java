package com.jcabi.github;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtRelease}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public class RtReleaseTest {
  /**
     * An empty JSON string.
     */
  private static final String EMPTY_JSON = "{}";

  /**
     * A test mnemo.
     */
  private static final String TEST_MNEMO = "tstuser/tstbranch";

  /**
     * A mock container used in test to mimic the Github server.
     */
  private transient MkContainer container;

  /**
     * Setting up the test fixture.
     */
  @Before public final void setUp() {
    this.container = new MkGrizzlyContainer();
  }

  /**
     * Tear down the test fixture to return to the original state.
     */
  @After public final void tearDown() {
    if (this.container != null) {
      this.container.stop();
    }
  }

  /**
     * RtRelease can edit a release.
     * @throws Exception If any problem during test execution occurs.
     */
  @Test public final void editRelease() throws Exception {
    this.container.next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, EMPTY_JSON)).start();
    final RtRelease release = RtReleaseTest.release(this.container.home());
    final JsonObject json = Json.createObjectBuilder().add("tag_name", "v1.0.0").build();
    release.patch(json);
    final MkQuery query = this.container.take();
    MatcherAssert.assertThat(query.method(), Matchers.equalTo(Request.PATCH));
    MatcherAssert.assertThat(query.body(), Matchers.equalTo(json.toString()));
  }

  /**
     * RtRelease can delete a release.
     * @throws Exception If any problems in the test occur.
     */
  @Test public final void deleteRelease() throws Exception {
    this.container.next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, EMPTY_JSON)).start();
    final RtRelease release = new RtRelease(new ApacheRequest(this.container.home()), new Coordinates.Simple(TEST_MNEMO), 2);
    release.delete();
    MatcherAssert.assertThat(this.container.take().method(), Matchers.equalTo(Request.DELETE));
    this.container.stop();
  }

  /**
     * RtRelease can list assets for a release.
     * @checkstyle LineLength (4 lines)
     * @todo #180 RtRelease should be able to list assets for a release. Let's
     *  implement this method, add integration test, declare a method in
     *  Release and implement it. See
     *  http://developer.github.com/v3/repos/releases/#list-assets-for-a-release.
     *  When done, remove this puzzle and Ignore annotation from this method.
     */
  @Test @Ignore public void listReleaseAssets() {
  }

  /**
     * RtRelease can upload a release asset.
     * @todo #180 RtRelease should be able to upload a release asset. Let's
     *  implement this method, add integration test, declare a method in
     *  Release and implement it. See
     *  http://developer.github.com/v3/repos/releases/#upload-a-release-asset.
     *  When done, remove this puzzle and Ignore annotation from this method.
     */
  @Test @Ignore public void uploadReleaseAsset() {
  }

  /**
     * RtRelease can get a single release asset.
     * @checkstyle LineLength (4 lines)
     * @todo #180 RtRelease should be able to get a single release asset. Let's
     *  implement this method, add integration test, declare a method in
     *  Release and implement it. See
     *  http://developer.github.com/v3/repos/releases/#get-a-single-release-asset.
     *  When done, remove this puzzle and Ignore annotation from this method.
     */
  @Test @Ignore public void getReleaseAsset() {
  }

  /**
     * RtRelese can execute PATCH request.
     * @throws Exception if there is any problem
     */
  @Test public final void executePatchRequest() throws Exception {
    this.container.next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, EMPTY_JSON)).start();
    final RtRelease release = new RtRelease(new ApacheRequest(this.container.home()), new Coordinates.Simple(TEST_MNEMO), 2);
    release.patch(Json.createObjectBuilder().add("name", "v1").build());
    MatcherAssert.assertThat(this.container.take().method(), Matchers.equalTo(Request.PATCH));
    this.container.stop();
  }

  /**
     * Create a test release.
     * @param uri REST API entry point.
     * @return A test release.
     */
  private static RtRelease release(final URI uri) {
    final RtRelease release = new RtRelease(new ApacheRequest(uri), new Coordinates.Simple(TEST_MNEMO), 2);
    return release;
  }
}