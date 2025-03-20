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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;
import org.apache.http.HttpHeaders;
import org.hamcrest.Matcher;

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
  private final transient Queue<Conditional> conditionals = new ConcurrentLinkedQueue<Conditional>();

  @Override @SuppressWarnings(value = { "PMD.AvoidCatchingThrowable", "rawtypes" }) public void service(final GrizzlyRequest request, final GrizzlyResponse response) {
    try {
      final MkQuery query = new GrizzlyQuery(request);
      final Iterator<Conditional> iter = this.conditionals.iterator();
      boolean matched = false;
      while (iter.hasNext()) {
        final Conditional cond = iter.next();
        if (cond.matches(query)) {
          matched = true;
          this.queue.add(query);
          final MkAnswer answer = cond.answer();
          for (final String name : answer.headers().keySet()) {
            for (final String value : answer.headers().get(name)) {
              response.addHeader(name, value);
            }
          }
          response.addHeader(HttpHeaders.SERVER, String.format("%s query #%d, %d answer(s) left", this.getClass().getName(), this.queue.size(), this.conditionals.size()));
          response.setStatus(answer.status());
          final byte[] body = answer.body().getBytes(MkGrizzlyAdapter.CHARSET);
          response.getStream().write(body);
          response.setContentLength(body.length);
          if (cond.decrement() == 0) {
            iter.remove();
          }
          break;
        }
      }
      if (!matched) {
        throw new NoSuchElementException("No matching answers found.");
      }
    } catch (final Throwable ex) {
      MkGrizzlyAdapter.fail(response, ex);
    }
  }

  /**
     * Give this answer on the next request.
     * @param answer Next answer to give
     */
  public void next(final MkAnswer answer, final Matcher<MkQuery> query, final int count) {
    this.conditionals.add(new Conditional(answer, query, count));
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

  @EqualsAndHashCode(of = { "answr", "condition" }) private static final class Conditional {
    /**
         * The MkAnswer.
         */
    private final transient MkAnswer answr;

    /**
         * Condition for this answer.
         */
    private final transient Matcher<MkQuery> condition;

    /**
         * The number of times the answer is expected to appear.
         */
    private transient AtomicInteger count;

    /**
         * Ctor.
         * @param ans The answer.
         * @param matcher The matcher.
         * @param times Number of times the answer should appear.
         */
    public Conditional(final MkAnswer ans, final Matcher<MkQuery> matcher, final int times) {
      this.answr = ans;
      this.condition = matcher;
      if (times < 1) {
        throw new IllegalArgumentException("Answer must be returned at least once.");
      } else {
        this.count = new AtomicInteger(times);
      }
    }

    /**
         * Get the answer.
         * @return The answer
         */
    public MkAnswer answer() {
      return this.answr;
    }

    /**
         * Does the query match the answer?
         * @param query The query to match
         * @return True, if the query matches the condition
         */
    public boolean matches(final MkQuery query) {
      return this.condition.matches(query);
    }

    /**
         * Decrement the count for this conditional.
         * @return The updated count
         */
    public int decrement() {
      return this.count.decrementAndGet();
    }
  }
}