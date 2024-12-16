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
package com.mpush.tools.config;

import com.mpush.api.spi.net.DnsMapping;
import com.mpush.tools.common.Profiler;
import com.mpush.tools.config.data.RedisNode;
import com.typesafe.config.*;
import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.toCollection;


/**
 * mpush 配置中心
 * Created by yxx on 2016/5/20.
 *
 * @author ohun@live.cn
 */
public interface CC {
    Config cfg = load();

    static Config load() {
        Config config = ConfigFactory.load();//扫描加载所有可用的配置文件
        String custom_conf = "mp.conf";//加载自定义配置, 值来自jvm启动参数指定-Dmp.conf
        if (config.hasPath(custom_conf)) {
            File file = new File(config.getString(custom_conf));
            if (file.exists()) {
                Config custom = ConfigFactory.parseFile(file);
                config = custom.withFallback(config);
            }
        }
        return config;
    }

    interface mp {
        Config cfg = CC.cfg.getObject("mp").toConfig();

        String log_dir = cfg.getString("log-dir");

        String log_level = cfg.getString("log-level");

        String log_conf_path = cfg.getString("log-conf-path");

        interface core {
            public static final Config cfg = mp.cfg.getObject("core").toConfig();

            public static final int session_expired_time = ((int) (cfg.getDuration("session-expired-time").getSeconds()));

            public static final int max_heartbeat = ((int) (cfg.getDuration("max-heartbeat", TimeUnit.MILLISECONDS)));

            public static final int max_packet_size = ((int) (cfg.getMemorySize("max-packet-size").toBytes()));

            public static final int min_heartbeat = ((int) (cfg.getDuration("min-heartbeat", TimeUnit.MILLISECONDS)));

            public static final long compress_threshold = cfg.getBytes("compress-threshold");

            public static final int max_hb_timeout_times = cfg.getInt("max-hb-timeout-times");

            public static final String epoll_provider = cfg.getString("epoll-provider");

            public static boolean useNettyEpoll() {
                if (!"netty".equals(CC.mp.core.epoll_provider)) {
                    return false;
                }
                String name = CC.cfg.getString("os.name").toLowerCase(Locale.UK).trim();
                return name.startsWith("linux");// 只在linux下使用netty提供的epoll库

            }
        }

        interface net {
            Config cfg = mp.cfg.getObject("net").toConfig();

            int connect_server_port = cfg.getInt("connect-server-port");

            int gateway_server_port = cfg.getInt("gateway-server-port");

            int admin_server_port = cfg.getInt("admin-server-port");

            int gateway_client_port = cfg.getInt("gateway-client-port");

            String gateway_server_net = cfg.getString("gateway-server-net");

            String gateway_server_multicast = cfg.getString("gateway-server-multicast");

            String gateway_client_multicast = cfg.getString("gateway-client-multicast");

            int ws_server_port = cfg.getInt("ws-server-port");

            String ws_path = cfg.getString("ws-path");

            int gateway_client_num = cfg.getInt("gateway-client-num");

            static boolean tcpGateway() {
                return "tcp".equals(gateway_server_net);
            }

            static boolean udpGateway() {
                return "udp".equals(gateway_server_net);
            }

            static boolean wsEnabled() {
                return ws_server_port > 0;
            }

            static boolean udtGateway() {
                return "udt".equals(gateway_server_net);
            }

            static boolean sctpGateway() {
                return "sctp".equals(gateway_server_net);
            }

            interface public_ip_mapping {
                public static final Map<String, Object> mappings = net.cfg.getObject("public-host-mapping").unwrapped();

                public static String getString(String localIp) {
                    return ((String) (mappings.get(localIp)));
                }
            }

            interface snd_buf {
                public static final Config cfg = net.cfg.getObject("snd_buf").toConfig();

                public static final int connect_server = ((int) (cfg.getMemorySize("connect-server").toBytes()));

                public static final int gateway_server = ((int) (cfg.getMemorySize("gateway-server").toBytes()));

                public static final int gateway_client = ((int) (cfg.getMemorySize("gateway-client").toBytes()));
            }

            interface rcv_buf {
                public static final Config cfg = net.cfg.getObject("rcv_buf").toConfig();

                public static final int connect_server = ((int) (cfg.getMemorySize("connect-server").toBytes()));

                public static final int gateway_server = ((int) (cfg.getMemorySize("gateway-server").toBytes()));

                public static final int gateway_client = ((int) (cfg.getMemorySize("gateway-client").toBytes()));
            }

            interface write_buffer_water_mark {
                public static final Config cfg = net.cfg.getObject("write-buffer-water-mark").toConfig();

                public static final int connect_server_low = ((int) (cfg.getMemorySize("connect-server-low").toBytes()));

                public static final int connect_server_high = ((int) (cfg.getMemorySize("connect-server-high").toBytes()));

                public static final int gateway_server_low = ((int) (cfg.getMemorySize("gateway-server-low").toBytes()));

                public static final int gateway_server_high = ((int) (cfg.getMemorySize("gateway-server-high").toBytes()));
            }

            interface traffic_shaping {
                public static final Config cfg = net.cfg.getObject("traffic-shaping").toConfig();

                interface gateway_client {
                    public static final Config cfg = traffic_shaping.cfg.getObject("gateway-client").toConfig();

                    public static final boolean enabled = cfg.getBoolean("enabled");

                    public static final long check_interval = cfg.getDuration("check-interval", TimeUnit.MILLISECONDS);

                    public static final long write_global_limit = cfg.getBytes("write-global-limit");

                    public static final long read_global_limit = cfg.getBytes("read-global-limit");

                    public static final long write_channel_limit = cfg.getBytes("write-channel-limit");

                    public static final long read_channel_limit = cfg.getBytes("read-channel-limit");
                }

                interface gateway_server {
                    public static final Config cfg = traffic_shaping.cfg.getObject("gateway-server").toConfig();

                    public static final boolean enabled = cfg.getBoolean("enabled");

                    public static final long check_interval = cfg.getDuration("check-interval", TimeUnit.MILLISECONDS);

                    public static final long write_global_limit = cfg.getBytes("write-global-limit");

                    public static final long read_global_limit = cfg.getBytes("read-global-limit");

                    public static final long write_channel_limit = cfg.getBytes("write-channel-limit");

                    public static final long read_channel_limit = cfg.getBytes("read-channel-limit");
                }

                interface connect_server {
                    public static final Config cfg = traffic_shaping.cfg.getObject("connect-server").toConfig();

                    public static final boolean enabled = cfg.getBoolean("enabled");

                    public static final long check_interval = cfg.getDuration("check-interval", TimeUnit.MILLISECONDS);

                    public static final long write_global_limit = cfg.getBytes("write-global-limit");

                    public static final long read_global_limit = cfg.getBytes("read-global-limit");

                    public static final long write_channel_limit = cfg.getBytes("write-channel-limit");

                    public static final long read_channel_limit = cfg.getBytes("read-channel-limit");
                }
            }
        }

        interface security {
            public static final Config cfg = mp.cfg.getObject("security").toConfig();

            public static final int aes_key_length = cfg.getInt("aes-key-length");

            public static final String public_key = cfg.getString("public-key");

            public static final String private_key = cfg.getString("private-key");
        }

        interface thread {
            Config cfg = mp.cfg.getObject("thread").toConfig();

            interface pool {
                Config cfg = thread.cfg.getObject("pool").toConfig();

                int conn_work = cfg.getInt("conn-work");

                int http_work = cfg.getInt("http-work");

                int push_task = cfg.getInt("push-task");

                int push_client = cfg.getInt("push-client");

                int ack_timer = cfg.getInt("ack-timer");

                int gateway_server_work = cfg.getInt("gateway-server-work");

                int gateway_client_work = cfg.getInt("gateway-client-work");

                interface event_bus {
                    public static final Config cfg = pool.cfg.getObject("event-bus").toConfig();

                    public static final int min = cfg.getInt("min");

                    public static final int max = cfg.getInt("max");

                    public static final int queue_size = cfg.getInt("queue-size");
                }

                interface mq {
                    public static final Config cfg = pool.cfg.getObject("mq").toConfig();

                    public static final int min = cfg.getInt("min");

                    public static final int max = cfg.getInt("max");

                    public static final int queue_size = cfg.getInt("queue-size");
                }
            }
        }

        interface zk {
            public static final Config cfg = mp.cfg.getObject("zk").toConfig();

            public static final int sessionTimeoutMs = ((int) (cfg.getDuration("sessionTimeoutMs", TimeUnit.MILLISECONDS)));

            public static final String watch_path = cfg.getString("watch-path");

            public static final int connectionTimeoutMs = ((int) (cfg.getDuration("connectionTimeoutMs", TimeUnit.MILLISECONDS)));

            public static final String namespace = cfg.getString("namespace");

            public static final String digest = cfg.getString("digest");

            public static final String server_address = cfg.getString("server-address");

            interface retry {
                public static final Config cfg = zk.cfg.getObject("retry").toConfig();

                public static final int maxRetries = cfg.getInt("maxRetries");

                public static final int baseSleepTimeMs = ((int) (cfg.getDuration("baseSleepTimeMs", TimeUnit.MILLISECONDS)));

                public static final int maxSleepMs = ((int) (cfg.getDuration("maxSleepMs", TimeUnit.MILLISECONDS)));
            }
        }

        interface redis {
            public static final Config cfg = mp.cfg.getObject("redis").toConfig();

            public static final boolean write_to_zk = cfg.getBoolean("write-to-zk");

            public static final String password = cfg.getString("password");

            public static final String clusterModel = cfg.getString("cluster-model");

            public static final List<RedisNode> nodes = // 第一纬度数组
            cfg.getList("nodes").stream().map(( v) -> RedisNode.from(v.unwrapped().toString())).collect(toCollection(ArrayList::new));

            public static boolean isCluster() {
                return "cluster".equals(clusterModel);
            }

            public static <T> T getPoolConfig(Class<T> clazz) {
                return ConfigBeanImpl.createInternal(cfg.getObject("config").toConfig(), clazz);
            }
        }

        interface http {
            public static final Config cfg = mp.cfg.getObject("http").toConfig();

            public static final boolean proxy_enabled = cfg.getBoolean("proxy-enabled");

            public static final int default_read_timeout = ((int) (cfg.getDuration("default-read-timeout", TimeUnit.MILLISECONDS)));

            public static final int max_conn_per_host = cfg.getInt("max-conn-per-host");

            public static final long max_content_length = cfg.getBytes("max-content-length");

            public static final Map<String, List<DnsMapping>> dns_mapping = loadMapping();

            public static Map<String, List<DnsMapping>> loadMapping() {
                Map<String, List<DnsMapping>> map = new HashMap<>();
                cfg.getObject("dns-mapping").forEach(( s, v) -> map.put(s, .class.cast(v).stream().map(( cv) -> DnsMapping.parse(((String) (cv.unwrapped())))).collect(toCollection(ArrayList::new))));
                return map;
            }
        }

        interface push {
            public static final Config cfg = mp.cfg.getObject("push").toConfig();

            interface flow_control {
                public static final Config cfg = push.cfg.getObject("flow-control").toConfig();

                interface global {
                    public static final Config cfg = flow_control.cfg.getObject("global").toConfig();

                    public static final int limit = cfg.getNumber("limit").intValue();

                    public static final int max = cfg.getInt("max");

                    public static final int duration = ((int) (cfg.getDuration("duration").toMillis()));
                }

                interface broadcast {
                    public static final Config cfg = flow_control.cfg.getObject("broadcast").toConfig();

                    public static final int limit = cfg.getInt("limit");

                    public static final int max = cfg.getInt("max");

                    public static final int duration = ((int) (cfg.getDuration("duration").toMillis()));
                }
            }
        }

        interface monitor {
            public static final Config cfg = mp.cfg.getObject("monitor").toConfig();

            public static final String dump_dir = cfg.getString("dump-dir");

            public static final boolean dump_stack = cfg.getBoolean("dump-stack");

            public static final boolean print_log = cfg.getBoolean("print-log");

            public static final Duration dump_period = cfg.getDuration("dump-period");

            public static final boolean profile_enabled = cfg.getBoolean("profile-enabled");

            public static final Duration profile_slowly_duration = cfg.getDuration("profile-slowly-duration");
        }

        interface spi {
            public static final Config cfg = mp.cfg.getObject("spi").toConfig();

            public static final String thread_pool_factory = cfg.getString("thread-pool-factory");

            public static final String dns_mapping_manager = cfg.getString("dns-mapping-manager");
        }
    }
}