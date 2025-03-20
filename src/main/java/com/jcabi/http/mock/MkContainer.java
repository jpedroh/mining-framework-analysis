package com.jcabi.http.mock;
import java.io.IOException;
import java.net.URI;
import org.hamcrest.Matcher;

/**
 * Mock version of Java Servlet container.
 *
 * <p>A convenient tool to test your application classes against a web
 * service. For example:
 *
 * <pre> MkContainer container = new MkGrizzlyContainer()
 *   .next(new MkAnswer.Simple(200, "works fine!"))
 *   .start();
 * new JdkRequest(container.home())
 *   .header("Accept", "text/xml")
 *   .fetch().as(RestResponse.class)
 *   .assertStatus(200)
 *   .assertBody(Matchers.equalTo("works fine!"));
 * MatcherAssert.assertThat(
 *   container.take().method(),
 *   Matchers.equalTo("GET")
 * );
 * container.stop();</pre>
 *
 * <p>Keep in mind that container automatically reserves a new free TCP port
 * and works until JVM is shut down. The only way to stop it is to call
 * {@link #stop()}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.10
 * @see <a href="http://www.rexsl.com/rexsl-test/example-mock-servlet.html">Examples</a>
 */
public interface MkContainer {
  /**
     * Give this answer on the next request.
     * @param answer Next answer to give
     * @return This object
     */
  MkContainer next(MkAnswer answer);

  /**
     * Give this answer on the next request if the matcher condition is
     * satisfied.
     * @param answer Next answer to give
     * @param condition The condition to match
     * @return This object
     */
  MkContainer next(MkAnswer answer, Matcher<MkQuery> condition);

  /**
     * Give this answer on the next request(s) if the matcher condition is
     * satisfied up to a certain number of consecutive requests.
     * @param answer Next answer to give
     * @param condition The condition to match
     * @param count Number of consecutive requests to match
     * @return This object
     */
  MkContainer next(MkAnswer answer, Matcher<MkQuery> condition, int count);

  /**
     * Get the oldest request received
     * ({@link java.util.NoSuchElementException}
     * if no more elements in the list).
     * @return Request received
     */
  MkQuery take();

  /**
     * How many queries we have left.
     * @return Total number of queries you can retrieve with {@link #take()}
     * @since 1.0
     */
  int queries();

  /**
     * Start it on the first available TCP port.
     * @return This object
     * @throws IOException If fails
     */
  MkContainer start() throws IOException;

  /**
     * Start it on a provided port.
     * @param prt The port where it should start listening
     * @return This object
     * @throws IOException If fails
     */
  MkContainer start(int prt) throws IOException;

  /**
     * Stop container.
     */
  void stop();

  /**
     * Get its home.
     * @return URI of the started container
     */
  URI home();
}