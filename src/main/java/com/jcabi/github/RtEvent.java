package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github event.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #1 Unit test for RtEvent is required. Let's create a simple one,
 *  to check that the class implements key functions correctly.
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "request", "owner", "num" }) final class RtEvent implements Event {
  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Repository we're in.
     */
  private final transient Repo owner;

  /**
     * Event number.
     */
  private final transient int num;

  /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param number Number of the get
     */
  RtEvent(final Request req, final Repo repo, final int number) {
    final Coordinates coords = repo.coordinates();
    this.request = req.uri().path("/repos").path(coords.user()).path(coords.repo()).path("/issues").path("/events").path(Integer.toString(number)).back();
    this.owner = repo;
    this.num = number;
  }

  @Override public String toString() {
    return this.request.uri().get().toString();
  }

  @Override public Repo repo() {
    return this.owner;
  }

  @Override public int number() {
    return this.num;
  }

  @Override public JsonObject json() throws IOException {
    return new RtJson(this.request).fetch();
  }

  @Override public int compareTo(final Event event) {
    return this.number() - event.number();
  }
}