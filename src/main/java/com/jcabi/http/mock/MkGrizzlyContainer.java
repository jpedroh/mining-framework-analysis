package com.jcabi.http.mock;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;

/**
 * Implementation of {@link MkContainer} based on Grizzly Server.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.10
 * @see MkContainer
 * @see <a href="http://www.rexsl.com/rexsl-test/example-mock-servlet.html">Examples</a>
 */
@EqualsAndHashCode(of = { "adapter", "gws", "port" }) @Loggable(value = Loggable.DEBUG) public final class MkGrizzlyContainer implements MkContainer {
  /**
     * Grizzly adapter.
     */
  private final transient MkGrizzlyAdapter adapter = new MkGrizzlyAdapter();

  /**
     * Grizzly container.
     */
  private transient GrizzlyWebServer gws;

  /**
     * Port where it works.
     */
  private transient int port;

  @Override public MkContainer next(final MkAnswer answer) {
    return this.next(answer, new IsAnything<MkQuery>());
  }

  @Override public MkContainer next(final MkAnswer answer, final Matcher<MkQuery> condition) {
    return this.next(answer, condition, 1);
  }

  @Override public MkContainer next(final MkAnswer answer, final Matcher<MkQuery> condition, final int count) {
    this.adapter.next(answer, condition, count);
    return this;
  }

  @Override public MkQuery take() {
    return this.adapter.take();
  }

  @Override public int queries() {
    return this.adapter.queries();
  }

  @Override public MkContainer start() throws IOException {
    return this.start(MkGrizzlyContainer.reserve());
  }

  @Override public MkContainer start(final int prt) throws IOException {
    if (this.port != 0) {
      throw new IllegalStateException(String.format("already listening on port %d, use #stop() first", this.port));
    }
    this.port = prt;
    this.gws = new GrizzlyWebServer(this.port);
    this.gws.addGrizzlyAdapter(this.adapter, new String[] { "/" });
    this.gws.start();
    Logger.info(this, "started on port #%s", prt);
    return this;
  }

  @Override public void stop() {
    this.gws.stop();
    Logger.info(this, "stopped on port #%s", this.port);
    this.port = 0;
  }

  @Override public URI home() {
    return URI.create(String.format("http://localhost:%d/", this.port));
  }

  /**
     * Reserve port.
     * @return Reserved TCP port
     * @throws IOException If fails
     */
  private static int reserve() throws IOException {
    int reserved;
    final ServerSocket socket = new ServerSocket(0);
    try {
      reserved = socket.getLocalPort();
    }  finally {
      socket.close();
    }
    return reserved;
  }
}