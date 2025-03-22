package com.mpush.bootstrap;
import com.mpush.bootstrap.job.*;
import com.mpush.core.server.*;
import com.mpush.tools.config.CC;
import static com.mpush.common.ServerNodes.CS;
import static com.mpush.common.ServerNodes.GS;
import static com.mpush.tools.config.CC.mp.net.udpGateway;
import static com.mpush.tools.config.CC.mp.net.wsEnabled;

/**
 * Created by yxx on 2016/5/14.
 *
 * @author ohun@live.cn
 */
public final class ServerLauncher {
  private final BootChain chain = BootChain.chain();

  public ServerLauncher() {
    chain.boot().setNext(new ServiceRegistryBoot()).setNext(new RedisBoot()).setNext(new ServerBoot(ConnectionServer.I(), CS)).setNext(
<<<<<<< /usr/src/app/output/mpusher/mpush/684f038dad1d5b95b6f3f60674e12e4ef0b47922/mpush-boot/src/main/java/com/mpush/bootstrap/ServerLauncher.java/left.java
    () -> new ServerBoot(WebSocketServer.I(), WS_NODE)
=======
    new ServerBoot(udpGateway() ? GatewayUDPConnector.I() : GatewayServer.I(), GS)
>>>>>>> /usr/src/app/output/mpusher/mpush/684f038dad1d5b95b6f3f60674e12e4ef0b47922/mpush-boot/src/main/java/com/mpush/bootstrap/ServerLauncher.java/right.java
    , wsEnabled()).setNext(new ServerBoot(udpGateway() ? GatewayUDPConnector.I() : GatewayServer.I(), GS_NODE)).setNext(new ServerBoot(AdminServer.I(), null)).setNext(new PushCenterBoot()).setNext(new HttpProxyBoot()).setNext(new MonitorBoot()).setNext(new LastBoot());
  }

  public void start() {
    chain.start();
  }

  public void stop() {
    chain.stop();
  }
}