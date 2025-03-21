package org.graylog2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class GelfSender {

    public static final int DEFAULT_PORT = 12201;

    private InetAddress host;
    private int port;
	private DatagramChannel channel;


<<<<<<< /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/left.java
    public GelfSender(String host) throws IOException {
||||||| /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/base.java
    public GelfSender(String host) throws UnknownHostException, SocketException {
=======
    public GelfSender(String host) throws IOException, SocketException {
>>>>>>> /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/right.java
        this(host, DEFAULT_PORT);
    }

<<<<<<< /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/left.java
    public GelfSender(String host, int port) throws IOException {
||||||| /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/base.java
    public GelfSender(String host, int port) throws UnknownHostException, SocketException {
=======
    public GelfSender(String host, int port) throws IOException, SocketException {
>>>>>>> /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/right.java
        this.host = InetAddress.getByName(host);
        this.port = port;
		this.channel = initiateChannel();
    }

    private DatagramChannel initiateChannel() throws IOException {
        DatagramChannel resultingChannel = DatagramChannel.open();
        resultingChannel.socket().bind(new InetSocketAddress(0));
        resultingChannel.connect(new InetSocketAddress(this.host, this.port));
        resultingChannel.configureBlocking(false);

        return resultingChannel;
    }

    public boolean sendMessage(GelfMessage message) {
        return message.isValid() && sendDatagrams(message.toDatagrams());
    }

	public boolean sendDatagrams(ByteBuffer[] bytesList) {
            try {
				channel.write(bytesList);
	} catch (IOException e) {
	    return false;
	}

        return true;
    }

    public void close() {
		try {
    	channel.close();
<<<<<<< /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/left.java
    } catch (IOException e) {
    	e.printStackTrace();
    }
||||||| /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/base.java
    } 
=======
    } catch ( IOException e )
    {
    	e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
>>>>>>> /usr/src/app/output/t0xa/gelfj/f57a572d8c372b60afde1f1f300fdb93e3d1a194/src/main/java/org/graylog2/GelfSender.java/right.java
    }
}
