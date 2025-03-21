/*
 * Copyright (C) 2003, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.illposed.osc;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.illposed.osc.utility.OSCByteArrayToJavaConverter;

public class OSCBundleTest extends junit.framework.TestCase {

	public OSCBundleTest(String name) {
		super(name);
	}

	public void testSendBundle() {
		Date timestamp = GregorianCalendar.getInstance().getTime();
		List<OSCPacket> packetsSent = new ArrayList<OSCPacket>(1);
		packetsSent.add(new OSCMessage("/dummy"));
		OSCBundle bundle = new OSCBundle(packetsSent, timestamp);
		byte[] byteArray = bundle.getByteArray();
		OSCByteArrayToJavaConverter converter = new OSCByteArrayToJavaConverter();
		OSCBundle packet = (OSCBundle) converter.convert(byteArray, byteArray.length);
		if (!packet.getTimestamp().equals(timestamp)) {
			fail("Send Bundle did not receive the correct timestamp " + packet.getTimestamp()
				+ "(" + packet.getTimestamp().getTime() +
				") (should be " + timestamp +"( " + timestamp.getTime() + ")) ");
<<<<<<< /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/test/java/com/illposed/osc/OSCBundleTest.java/left.java
		List<OSCPacket> packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets.get(0);
		if (!msg.getAddress().equals("/dummy"))
			fail("Send Bundle's message did not receive the correct address");
||||||| /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/test/java/com/illposed/osc/OSCBundleTest.java/base.java
		OSCPacket[] packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets[0];
		if (!msg.getAddress().equals("/dummy"))
			fail("Send Bundle's message did not receive the correct address");
=======
>>>>>>> /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/test/java/com/illposed/osc/OSCBundleTest.java/right.java
		}
		OSCPacket[] packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets[0];
		if (!msg.getAddress().equals("/dummy")) {
			fail("Send Bundle's message did not receive the correct address");
		}
	}

	public void testSendBundleImmediate() {
		List<OSCPacket> packetsSent = new ArrayList<OSCPacket>(1);
		packetsSent.add(new OSCMessage("/dummy"));
		OSCBundle bundle = new OSCBundle(packetsSent);
		byte[] byteArray = bundle.getByteArray();
		OSCByteArrayToJavaConverter converter = new OSCByteArrayToJavaConverter();
		OSCBundle packet = (OSCBundle) converter.convert(byteArray, byteArray.length);
		if (!packet.getTimestamp().equals(OSCBundle.TIMESTAMP_IMMEDIATE)) {
			fail("Timestamp should have been immediate, not " + packet.getTimestamp()
				+ "(" + packet.getTimestamp().getTime() + ")");
<<<<<<< /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/test/java/com/illposed/osc/OSCBundleTest.java/left.java
		List<OSCPacket> packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets.get(0);
		if (!msg.getAddress().equals("/dummy"))
			fail("Send Bundle's message did not receive the correct address");
||||||| /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/test/java/com/illposed/osc/OSCBundleTest.java/base.java
		OSCPacket[] packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets[0];
		if (!msg.getAddress().equals("/dummy"))
			fail("Send Bundle's message did not receive the correct address");
=======
>>>>>>> /usr/src/app/output/hoijui/javaosc/922809cde5aeaaa89a94a8aa7b4b3e03b3f993e5/modules/core/src/test/java/com/illposed/osc/OSCBundleTest.java/right.java
		}
		OSCPacket[] packets = packet.getPackets();
		OSCMessage msg = (OSCMessage) packets[0];
		if (!msg.getAddress().equals("/dummy")) {
			fail("Send Bundle's message did not receive the correct address");
		}
	}
}
