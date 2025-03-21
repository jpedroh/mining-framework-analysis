package com.illposed.osc;

<<<<<<< /usr/src/app/output/hoijui/javaosc/f806396898fdfbf9533f2ff881b05ff5b6df5f6e/modules/core/src/test/java/com/illposed/osc/OSCPortTest.java/left.java
import java.util.ArrayList;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/hoijui/javaosc/f806396898fdfbf9533f2ff881b05ff5b6df5f6e/modules/core/src/test/java/com/illposed/osc/OSCPortTest.java/left.java
import java.util.Date;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/hoijui/javaosc/f806396898fdfbf9533f2ff881b05ff5b6df5f6e/modules/core/src/test/java/com/illposed/osc/OSCPortTest.java/left.java
import java.util.List;

=======
>>>>>>> Unknown file: This is a bug in JDime.

public class OSCPortTest extends junit.framework.TestCase {
  private OSCPortOut sender;

  private OSCPortIn receiver;

  /**
	 * @see junit.framework.TestCase#setUp()
	 */
  @Override protected void setUp() throws Exception {
    super.setUp();
    sender = new OSCPortOut();
    receiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
  }

  /**
	 * @see junit.framework.TestCase#tearDown()
	 */
  @Override protected void tearDown() throws Exception {
    sender.close();
    receiver.close();
    super.tearDown();
  }

  public void testStart() throws Exception {
    OSCMessage mesg = new OSCMessage("/sc/stop");
    sender.send(mesg);
  }

  public void testMessageWithArgs() throws Exception {
    List<Object> args = new ArrayList<Object>(2);
    args.add(new Integer(3));
    args.add("hello");
    OSCMessage mesg = new OSCMessage("/foo/bar", args);
    sender.send(mesg);
  }

  public void testBundle() throws Exception {
    List<Object> args = new ArrayList<Object>(2);
    args.add(new Integer(3));
    args.add("hello");
    List<OSCPacket> msgs = new ArrayList<OSCPacket>(1);
    msgs.add(new OSCMessage("/foo/bar", args));
    OSCBundle bundle = new OSCBundle(msgs);
    sender.send(bundle);
  }

  public void testBundle2() throws Exception {
    OSCMessage mesg = new OSCMessage("/foo/bar");
    mesg.addArgument(new Integer(3));
    mesg.addArgument("hello");
    OSCBundle bundle = new OSCBundle();
    bundle.addPacket(mesg);
    sender.send(bundle);
  }

  public void testReceiving() throws Exception {
    OSCMessage mesg = new OSCMessage("/message/receiving");
    TestOSCListener listener = new TestOSCListener();
    receiver.addListener("/message/receiving", listener);
    receiver.startListening();
    sender.send(mesg);
    Thread.sleep(100);
    receiver.stopListening();
    if (!listener.isMessageReceived()) {
      fail("Message was not received");
    }
  }

  public void testBundleReceiving() throws Exception {
    OSCBundle bundle = new OSCBundle();
    bundle.addPacket(new OSCMessage("/bundle/receiving"));
    TestOSCListener listener = new TestOSCListener();
    receiver.addListener("/bundle/receiving", listener);
    receiver.startListening();
    sender.send(bundle);
    Thread.sleep(100);
    receiver.stopListening();
    if (!listener.isMessageReceived()) {
      fail("Message was not received");
    }
    if (!listener.getReceivedTimestamp().equals(bundle.getTimestamp())) {
      fail("Message should have timestamp " + bundle.getTimestamp() + " but has " + listener.getReceivedTimestamp());
    }
  }
}