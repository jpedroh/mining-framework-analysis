package com.libzter.a;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.libzter.a.A.*;
import static com.libzter.a.A.CMD_GET;
import static com.libzter.a.A.CMD_WAIT;
import static org.junit.Assert.*;

/**
 * A base class with all the test cases.
 * The actual transport protocol has to be implemented as well as the broker implementation.
 * This is done in the real test classes. They could test any JMS complaint protocol and broker.
 *
 * This makes it easy to test that the basic functionality works with different ActiveMQ configurations.
 *
 * Created by petter on 2015-01-30.
 */
public abstract class BaseTest {

    protected static final String LN = System.getProperty("line.separator");
    protected static final long TEST_TIMEOUT = 2000L;
    protected Connection connection;
    protected Session session;
    protected ConnectionFactory cf;
    protected ExecutorService executor;
    protected A a;
    protected ATestOutput output;
    protected Destination testTopic, testQueue, sourceQueue, targetQueue;
    protected TextMessage testMessage;

    @Autowired
    protected BrokerService amqBroker;

    protected abstract ConnectionFactory getConnectionFactory();
    protected abstract String getConnectCommand();


    @Before
    public void setupJMS() throws Exception {
        cf = getConnectionFactory();
        connection = cf.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        executor = Executors.newSingleThreadExecutor();
        a = new A();
        output = new ATestOutput();
        a.output = output;

        // Clear
        for(ActiveMQDestination destination : amqBroker.getRegionBroker().getDestinations()){
            amqBroker.getRegionBroker().removeDestination(
                    amqBroker.getRegionBroker().getAdminConnectionContext(),
                    destination,1);
        }

        testTopic = session.createTopic("TEST.TOPIC");
        testQueue = session.createQueue("TEST.QUEUE");
        sourceQueue = session.createQueue("SOURCE.QUEUE");
        targetQueue = session.createQueue("TARGET.QUEUE");
        testMessage = session.createTextMessage("test");
        connection.start();
    }

    @After
    public void disconnectJMS() throws JMSException {
        session.close();
        connection.close();
        executor.shutdown();
    }

    @Test
    public void testPutQueue() throws Exception{
        String cmdLine = getConnectCommand() + "-" + CMD_PUT + "\"test\"" + " TEST.QUEUE";
        a.run(cmdLine.split(" "));
        MessageConsumer mc = session.createConsumer(testQueue);
        TextMessage msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertEquals("test",msg.getText());
    }

    @Test
    public void testPutWithPriority() throws Exception{
        final int priority = 6;
        String cmdLine = getConnectCommand() + "-" + CMD_PRIORITY +" " + priority + " -" + CMD_PUT + "\"test\""
                + " TEST.QUEUE";
        a.run(cmdLine.split(" "));
        MessageConsumer mc = session.createConsumer(testQueue);
        TextMessage msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertEquals("test",msg.getText());
        assertEquals(priority,msg.getJMSPriority());
    }

    @Test
    public void testPutTopic() throws Exception{
        String cmdLine = getConnectCommand() + "-" + CMD_PUT + "\"test\"" + " topic://TEST.TOPIC";
        Future<TextMessage> resultMessage = executor.submit(new Callable<TextMessage>(){
            public TextMessage call() throws Exception {
                MessageConsumer mc = session.createConsumer(testTopic);
                return (TextMessage)mc.receive(TEST_TIMEOUT);
            }
        });
        a.run(cmdLine.split(" "));
        assertEquals("test",resultMessage.get().getText());
    }

    @Test
    public void testGetQueue() throws Exception{
        MessageProducer mp = session.createProducer(testQueue);
        mp.send(testMessage);
        String cmdLine = getConnectCommand() + "-" + CMD_GET + " -" +
                CMD_WAIT + " 2000" + " TEST.QUEUE";
        a.run(cmdLine.split(" "));
        String out = output.grab();
        assertTrue("Payload test expected",out.contains("Payload:"+LN+"test"));
    }

    @Test
    public void testGetTopic() throws Exception{
        final String cmdLine = getConnectCommand() + "-" + CMD_GET + " -" +
                CMD_WAIT + " 4000" + " topic://TEST.TOPIC";
        Future<String> resultString = executor.submit(new Callable<String>(){
            public String call() throws Exception {
                a.run(cmdLine.split(" "));
                return output.grab();
            }
        });
        Thread.sleep(300); // TODO remove somehow?
        MessageProducer mp = session.createProducer(testTopic);
        mp.send(testMessage);
        String result = resultString.get();
        assertTrue("Payload test expected",result.contains("Payload:"+LN+"test"));
    }

    /**
     * Test that all messages are copied (not moved) from one queue to the other.
     * @throws Exception
     */
    @Test
    public void testCopyQueue() throws Exception{
        final String cmdLine = getConnectCommand() + "-" + CMD_COPY_QUEUE + " SOURCE.QUEUE TARGET.QUEUE";
        MessageProducer mp = session.createProducer(sourceQueue);
        mp.send(testMessage);
        mp.send(testMessage);
        a.run(cmdLine.split(" "));
        MessageConsumer mc = session.createConsumer(sourceQueue);
        TextMessage msg = null;
        // Verify messages are left on source queue
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNotNull(msg);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNotNull(msg);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNull(msg);
        // Verify messages are copied to target queue
        mc = session.createConsumer(targetQueue);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNotNull(msg);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNotNull(msg);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNull(msg);
    }

    /**
     * Test that all messages are moved from one queue to the other.
     * @throws Exception
     */
    @Test
    public void testMoveQueue() throws Exception{
        final String cmdLine = getConnectCommand() + "-" + CMD_MOVE_QUEUE + " SOURCE.QUEUE TARGET.QUEUE";
        MessageProducer mp = session.createProducer(sourceQueue);
        mp.send(testMessage);
        mp.send(testMessage);
        a.run(cmdLine.split(" "));
        MessageConsumer mc = session.createConsumer(sourceQueue);
        TextMessage msg = null;
        // Verify NO messages are left on source queue
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNull(msg);
        // Verify messages are moved to target queue
        mc = session.createConsumer(targetQueue);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNotNull(msg);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNotNull(msg);
        msg = (TextMessage)mc.receive(TEST_TIMEOUT);
        assertNull(msg);
    }

    @Test
    public void testGetCount() throws Exception{
        final String cmdLine = getConnectCommand() + "-" + CMD_GET + " -" + CMD_COUNT + "2 TEST.QUEUE";
        MessageProducer mp = session.createProducer(testQueue);
        mp.send(testMessage);
        mp.send(testMessage);
        a.run(cmdLine.split(" "));
        String out = output.grab().replaceFirst("Operation completed in .+","");

        final String expectedOut = "-----------------\n" +
                "Message Properties\n" +
                "Payload:\n" +
                "test\n" +
                "-----------------\n" +
                "Message Properties\n" +
                "Payload:\n" +
                "test\n\n";
        assertEquals(expectedOut,out);
    }


}
