package com.alipay.remoting.demo;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.common.CONNECTEventProcessor;
import com.alipay.remoting.rpc.common.DISCONNECTEventProcessor;
import com.alipay.remoting.rpc.common.RequestBody;
import com.alipay.remoting.rpc.common.SimpleClientUserProcessor;

/**
 * a demo for rpc client, you can just run the main method after started rpc server of {@link RpcServerDemoByMain}
 *
 * @author tsui
 * @version $Id: RpcClientDemoByMain.java, v 0.1 2018-04-10 10:39 tsui Exp $
 */
public class RpcClientDemoByMain {
  static Logger logger = LoggerFactory.getLogger(BasicUsageDemoByJunit.class);

  static RpcClient client;

  static String addr = "127.0.0.1:8999";

  SimpleClientUserProcessor clientUserProcessor = new SimpleClientUserProcessor();

  CONNECTEventProcessor clientConnectProcessor = new CONNECTEventProcessor();

  DISCONNECTEventProcessor clientDisConnectProcessor = new DISCONNECTEventProcessor();

  public RpcClientDemoByMain() {
    client = new RpcClient();
    client.addConnectionEventProcessor(ConnectionEventType.CONNECT, clientConnectProcessor);
    client.addConnectionEventProcessor(ConnectionEventType.CLOSE, clientDisConnectProcessor);
    client.init();
  }

  public static void main(String[] args) {
    new RpcClientDemoByMain();
    RequestBody req = new RequestBody(2, "hello world sync");
    try {
      client.invokeSync(addr, req, 30000);
      System.out.println("invoke sync result = [" + "" + "]");
    } catch (RemotingException e) {
      String errMsg = "RemotingException caught in oneway!";
      logger.error(errMsg, e);
      Assert.fail(errMsg);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    client.shutdown();
  }
}