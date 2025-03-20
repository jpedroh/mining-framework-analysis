package com.jcabi.http.mock;
import com.jcabi.log.Logger;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.http.HttpHeaders;

/**
 * Mocker of Java Servlet container.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.10
 */
final class MkGrizzlyAdapter extends GrizzlyAdapter {
  /**
     * The encoding to use.
     */
  private static final String ENCODING = "UTF-8";

  /**
     * The Charset to use.
     */
  private static final Charset CHARSET = Charset.forName(ENCODING);

  /**
     * Queries received.
     */
  private final transient Queue<MkQuery> queue = new ConcurrentLinkedQueue<MkQuery>();

  /**
     * Answers to give.
     */
  private final transient Queue<MkAnswer> answers = new ConcurrentLinkedQueue<MkAnswer>();

  @Override @SuppressWarnings(value = { "PMD.AvoidCatchingThrowable", "rawtypes" }) public void service(final GrizzlyRequest request, final GrizzlyResponse response) {
    try {
      this.queue.add(new GrizzlyQuery(request));
      final MkAnswer answer = this.answers.remove();
      for (final String name : answer.headers().keySet()) {
        for (final String value : answer.headers().get(name)) {
          response.addHeader(name, value);
        }
      }
      response.addHeader(HttpHeaders.SERVER, String.format("%s query #%d, %d answer(s) left", this.getClass().getName(), this.queue.size(), this.answers.size()));
      response.setStatus(answer.status());
      final byte[] body = answer.body().getBytes(MkGrizzlyAdapter.CHARSET);
      response.getStream().write(body);
      response.setContentLength(body.length);
    } catch (final Throwable ex) {
      MkGrizzlyAdapter.fail(response, ex);
    }
  }

  /**
     * Give this answer on the next request.
     * @param answer Next answer to give
     */
  public void next(final MkAnswer answer) {
    this.answers.add(answer);
  }

  /**
     * Get the oldest request received.
     * @return Request received
     */
  public MkQuery take() {
    return this.queue.remove();
  }

  /**
     * Total number of available queue.
     * @return Number of them
     */
  public int queries() {
    return this.queue.size();
  }

  /**
     * Notify this response about failure.
     * @param response The response to notify
     * @param failure The failure just happened
     */
  private static void fail(final GrizzlyResponse<?> response, final Throwable failure) {
    response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
    final PrintWriter writer;
    try {
      writer = new PrintWriter(new OutputStreamWriter(response.getStream(), MkGrizzlyAdapter.ENCODING));
    } catch (final UnsupportedEncodingException ex) {
      throw new IllegalStateException(ex);
    }
    try {
      writer.print(Logger.format("%[exception]s", failure));
    }  finally {
      writer.close();
    }
  }
}