package io.termd.core.pty;
import io.termd.core.http.vertx.VertxSockJSBootstrap;
import io.termd.core.tty.TtyConnection;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class PtyBootstrap implements Consumer<TtyConnection> {
  public PtyBootstrap() {
  }

  public static void main(String[] args) throws Exception {
    PtyBootstrap bootstrap = new PtyBootstrap();
    VertxSockJSBootstrap sockJSBootstrap = new VertxSockJSBootstrap("localhost", 8080, bootstrap);
    final CountDownLatch latch = new CountDownLatch(1);
    sockJSBootstrap.bootstrap((event) -> {
      if (event.succeeded()) {
        System.out.println("Server started on " + 8080);
      } else {
        System.out.println("Could not start");
        event.cause().printStackTrace();
        latch.countDown();
      }
    });
    latch.await();
  }

  @Override public void accept(final TtyConnection conn) {
    TtyBridge bridge = new TtyBridge(conn);
    bridge.
<<<<<<< /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyBootstrap.java/left.java
    stdoutHandler().accept(Helper.toCodePoints("Welcome sir\r\n"))
=======
    readline()
>>>>>>> /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyBootstrap.java/right.java
    ;
  }
}