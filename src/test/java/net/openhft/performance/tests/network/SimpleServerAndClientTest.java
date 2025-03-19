/*
 * Copyright 2016 higherfrequencytrading.com
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

package net.openhft.performance.tests.network;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.threads.EventLoop;
import net.openhft.chronicle.core.threads.HandlerPriority;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.network.*;
import net.openhft.chronicle.network.connection.TcpChannelHub;
import net.openhft.chronicle.network.connection.TryLock;
import net.openhft.chronicle.threads.EventGroup;
import net.openhft.chronicle.wire.TextWire;
import net.openhft.chronicle.wire.Wire;
import net.openhft.chronicle.wire.WireType;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static net.openhft.chronicle.network.connection.SocketAddressSupplier.uri;
import static net.openhft.performance.tests.network.LegacyHanderFactory.simpleTcpEventHandlerFactory;

/**
 * Created by rob on 26/08/2015.
 */
public class SimpleServerAndClientTest {
    private ThreadDump threadDump;

    @Before
    public void threadDump() {
        threadDump = new ThreadDump();
    }

    @After
    public void checkThreadDump() {

        threadDump.assertNoNewThreads();
    }

    @Test
<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
    public void test() throws IOException, TimeoutException, InterruptedException {
        // this the name of a reference to the host name and port,
        // allocated automatically when to a free port on localhost
        final String desc = "host.port";
        TCPRegistry.createServerSocketChannelFor(desc);
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
    // @Ignore("Fails on Teamcity ")
    public void test() throws IOException, TimeoutException {
        // this the name of a reference to the host name and port,
        // allocated automatically when to a free port on localhost
        final String desc = "host.port";
        TCPRegistry.createServerSocketChannelFor(desc);
=======
    // @Ignore("Fails on Teamcity ")
    public void test() throws IOException, TimeoutException, InterruptedException {
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
        // we use an event loop rather than lots of threads
        try (EventLoop eg = new EventGroup(true)) {
            eg.start();
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
        // we use an event loop rather than lots of threads
        EventLoop eg = new EventGroup(true);
        eg.start();
=======
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
            // an example message that we are going to send from the server to the client and back
            final String expectedMessage = "<my message>";
            createServer(desc, eg);
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
        // an example message that we are going to send from the server to the client and back
        final String expectedMessage = "<my message>";
        createServer(desc, eg);
=======
        for (; ; ) {
            // this the name of a reference to the host name and port,
            // allocated automatically when to a free port on localhost
            final String desc = "host.port";
            TCPRegistry.createServerSocketChannelFor(desc);
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
            // allow time for the server to be created
            Thread.sleep(1);
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
        try (TcpChannelHub tcpChannelHub = createClient(eg, desc)) {
=======
            // we use an event loop rather than lots of threads
            try (EventLoop eg = new EventGroup(true)) {
                eg.start();
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
            try (TcpChannelHub tcpChannelHub = createClient(eg, desc)) {
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            // create the message the client sends to the server
=======
                // an example message that we are going to send from the server to the client and back
                final String expectedMessage = "<my message>";
                createServer(desc, eg);
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                // create the message the client sends to the server
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            // the tid must be unique, its reflected back by the server, it must be at the start
            // of each message sent from the server to the client. Its use by the client to identify which
            // thread will handle this message
            final long tid = tcpChannelHub.nextUniqueTransaction(System.currentTimeMillis());
=======
                try (TcpChannelHub tcpChannelHub = createClient(eg, desc)) {
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                // the tid must be unique, its reflected back by the server, it must be at the start
                // of each message sent from the server to the client. Its use by the client to identify which
                // thread will handle this message
                final long tid = tcpChannelHub.nextUniqueTransaction(System.currentTimeMillis());
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            // we will use a text wire backed by a elasticByteBuffer
            final Wire wire = new TextWire(Bytes.elasticByteBuffer());
=======
                    // create the message the client sends to the server
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                // we will use a text wire backed by a elasticByteBuffer
                final Wire wire = new TextWire(Bytes.elasticByteBuffer());
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            wire.writeDocument(true, w -> w.write(() -> "tid").int64(tid));
            wire.writeDocument(false, w -> w.write(() -> "payload").text(expectedMessage));
=======
                    // the tid must be unique, its reflected back by the server, it must be at the start
                    // of each message sent from the server to the client. Its use by the client to identify which
                    // thread will handle this message
                    final long tid = tcpChannelHub.nextUniqueTransaction(System.currentTimeMillis());
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                wire.writeDocument(true, w -> w.write(() -> "tid").int64(tid));
                wire.writeDocument(false, w -> w.write(() -> "payload").text(expectedMessage));
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            // write the data to the socket
            tcpChannelHub.lock2(() -> tcpChannelHub.writeSocket(wire, true),
                    true, TryLock.TRY_LOCK_WARN);
=======
                    // we will use a text wire backed by a elasticByteBuffer
                    final Wire wire = new TextWire(Bytes.elasticByteBuffer());
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                // write the data to the socket
                tcpChannelHub.lock2(() -> tcpChannelHub.writeSocket(wire, true),
                        true, TryLock.TRY_LOCK_WARN);
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            // read the reply from the socket ( timeout after 1 second ), note: we have to pass the tid
            Wire reply = tcpChannelHub.proxyReply(TimeUnit.SECONDS.toMillis(5), tid);
=======
                    wire.writeDocument(true, w -> w.write(() -> "tid").int64(tid));
                    wire.writeDocument(false, w -> w.write(() -> "payload").text(expectedMessage));
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                // read the reply from the socket ( timeout after 1 second ), note: we have to pass the tid
                Wire reply = tcpChannelHub.proxyReply(TimeUnit.SECONDS.toMillis(5), tid);
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
            // read the reply and check the result
            reply.readDocument(null, data -> {
                final String text = data.read(() -> "payloadResponse").text();
                Assert.assertEquals(expectedMessage, text);
            });
=======
                    // write the data to the socket
                    tcpChannelHub.lock2(() -> tcpChannelHub.writeSocket(wire, true),
                            true, TryLock.TRY_LOCK_WARN);
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java

<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
                // read the reply and check the result
                reply.readDocument(null, data -> {
                    final String text = data.read(() -> "payloadResponse").text();
                    Assert.assertEquals(expectedMessage, text);
                });

            }
        } finally {
            TcpChannelHub.closeAllHubs();
            TCPRegistry.reset();
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
=======
                    // read the reply from the socket ( timeout after 5 second ), note: we have to pass
                    // the tid
                    try {
                        Wire reply = tcpChannelHub.proxyReply(500, tid);

                        // read the reply and check the result
                        reply.readDocument(null, data -> {
                            final String text = data.read(() -> "payloadResponse").text();
                            Assert.assertEquals(expectedMessage, text);
                        });

                    } catch (TimeoutException e) {
                        // retry, you will get this is the client attempts to send a message to
                        // the server and the server is not running or ready
                        continue;
                    }
                    break;
                }

            } finally {
                TcpChannelHub.closeAllHubs();
                TCPRegistry.reset();
            }
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java
        }
<<<<<<< /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/left.java
||||||| /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/base.java
        eg.close();
=======
>>>>>>> /usr/src/app/output/openhft/chronicle-network/0866a74fd59691c127717ca1d8b1864866486a71/src/test/java/net/openhft/performance/tests/network/SimpleServerAndClientTest.java/right.java
    }

    @NotNull
    private TcpChannelHub createClient(EventLoop eg, String desc) {
        return new TcpChannelHub(null, eg, WireType.TEXT, "/", uri(desc), false, null, HandlerPriority.TIMER);
    }

    private void createServer(String desc, EventLoop eg) throws IOException {
        AcceptorEventHandler eah = new AcceptorEventHandler(desc, handlerFactory(), VanillaNetworkContext::new);
        eg.addHandler(eah);
        SocketChannel sc = TCPRegistry.createSocketChannel(desc);
        sc.configureBlocking(false);
    }

    @NotNull
    private Function<NetworkContext, TcpEventHandler> handlerFactory() {
        return simpleTcpEventHandlerFactory(WireEchoRequestHandler::new, WireType.TEXT);
    }
}
