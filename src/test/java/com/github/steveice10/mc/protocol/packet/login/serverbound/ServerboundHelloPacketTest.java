package com.github.steveice10.mc.protocol.packet.login.serverbound;

import com.github.steveice10.mc.protocol.packet.PacketTest;
import org.junit.Before;

public class ServerboundHelloPacketTest extends PacketTest {
    @Before
    public void setup() {
<<<<<<< /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/test/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacketTest.java/left.java
        this.setPackets(new ServerboundHelloPacket("Username", null, null, null, null));
||||||| /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/test/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacketTest.java/base.java
        this.setPackets(new ServerboundHelloPacket("Username", null, null, null));
=======
        this.setPackets(new ServerboundHelloPacket("Username", null, null));
>>>>>>> /usr/src/app/output/steveice10/mcprotocollib/535d2000ef4f73d3cecddaaa87ead561e11ab648/src/test/java/com/github/steveice10/mc/protocol/packet/login/serverbound/ServerboundHelloPacketTest.java/right.java
    }
}
