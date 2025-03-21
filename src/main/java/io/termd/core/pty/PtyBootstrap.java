/*
 * Copyright 2015 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    VertxSockJSBootstrap sockJSBootstrap = new VertxSockJSBootstrap(
        "localhost",
        8080,
        bootstrap);
    final CountDownLatch latch = new CountDownLatch(1);
    sockJSBootstrap.bootstrap(event -> {
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

  @Override
  public void accept(final TtyConnection conn) {
<<<<<<< /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyBootstrap.java/left.java
    InputStream inputrc = KeyDecoder.class.getResourceAsStream("inputrc");
    Keymap keymap = new Keymap(inputrc);
    Readline readline = new Readline(keymap);
    for (io.termd.core.readline.Function function : Helper.loadServices(Thread.currentThread().getContextClassLoader(), io.termd.core.readline.Function.class)) {
      log.trace("Server is adding function to readline: {}", function);

      readline.addFunction(function);
    }
    conn.setTermHandler(term -> {
        // Not used yet but we should propagage this to the process builder
        System.out.println("CLIENT $TERM=" + term);
    });
    conn.stdoutHandler().accept(Helper.toCodePoints("Welcome sir\r\n"));
    read(conn, readline);
  }

  public void read(final TtyConnection conn, final Readline readline) {
    Consumer<String> requestHandler = new Consumer<String>() {
      @Override
      public void accept(String line) {
        PtyMaster task = new PtyMaster(PtyBootstrap.this, conn, readline, line);
        taskCreationListener.accept(task);
        task.start();
      }
    };
    readline.readline(conn, "% ", requestHandler);
||||||| /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyBootstrap.java/base.java
    InputStream inputrc = KeyDecoder.class.getResourceAsStream("inputrc");
    Keymap keymap = new Keymap(inputrc);
    Readline readline = new Readline(keymap);
    for (io.termd.core.readline.Function function : Helper.loadServices(Thread.currentThread().getContextClassLoader(), io.termd.core.readline.Function.class)) {
      log.trace("Server is adding function to readline: {}", function);

      readline.addFunction(function);
    }
    conn.setTermHandler(term -> {
        // Not used yet but we should propagage this to the process builder
        System.out.println("CLIENT $TERM=" + term);
    });
    conn.writeHandler().accept(Helper.toCodePoints("Welcome sir\r\n"));
    read(conn, readline);
  }

  public void read(final TtyConnection conn, final Readline readline) {
    Consumer<String> requestHandler = new Consumer<String>() {
      @Override
      public void accept(String line) {
        PtyMaster task = new PtyMaster(PtyBootstrap.this, conn, readline, line);
        taskCreationListener.accept(task);
        task.start();
      }
    };
    readline.readline(conn, "% ", requestHandler);
=======
    TtyBridge bridge = new TtyBridge(conn);
    bridge.readline();
>>>>>>> /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyBootstrap.java/right.java
  }
}
