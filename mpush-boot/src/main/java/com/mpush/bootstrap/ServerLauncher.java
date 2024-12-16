/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */
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
                //7.启动监控
                //8.启动结束
                //6.启动http代理服务，解析dns
                //6.启动http代理服务，解析dns
                //5.启动控制台服务
                //4.启动网关服务
        // 4.启动websocket连接服务
                //3.启动长连接服务
                //2.注册redis sever 到ZK
                //1.启动ZK节点数据变化监听
        chain.boot().setNext(new ServiceRegistryBoot()).setNext(new RedisBoot()).setNext(new ServerBoot(ConnectionServer.I(), CS)).setNext(() -> new <WS_NODE>ServerBoot(WebSocketServer.I()), wsEnabled()).setNext(new ServerBoot(udpGateway() ? GatewayUDPConnector.I() : GatewayServer.I(), GS)).setNext(new ServerBoot(AdminServer.I(), null)).setNext(new PushCenterBoot()).setNext(new HttpProxyBoot()).setNext(new MonitorBoot()).setNext(new LastBoot());
    }

    public void start() {
        chain.start();
    }

    public void stop() {
        chain.stop();
    }
}