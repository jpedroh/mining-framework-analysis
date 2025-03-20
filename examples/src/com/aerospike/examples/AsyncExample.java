package com.aerospike.examples;
import java.lang.reflect.Constructor;
import java.util.List;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.async.EventLoopType;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.async.NioEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.proxy.AerospikeClientProxy;
import com.aerospike.client.util.Util;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;

public abstract class AsyncExample {
  /**
	 * Connect and run one or more asynchronous client examples.
	 */
  public static void runExamples(Console console, Parameters params, List<String> examples) throws Exception {
    EventPolicy eventPolicy = new EventPolicy();
    eventPolicy.maxCommandsInProcess = params.maxCommandsInProcess;
    eventPolicy.maxCommandsInQueue = params.maxCommandsInQueue;
    if (params.useProxyClient && params.eventLoopType == EventLoopType.DIRECT_NIO) {
      if (Epoll.isAvailable()) {
        params.eventLoopType = EventLoopType.NETTY_EPOLL;
      } else {
        params.eventLoopType = EventLoopType.NETTY_NIO;
      }
    }
    EventLoops eventLoops;
    switch (params.eventLoopType) {
      default:
      case DIRECT_NIO:
      {
        eventLoops = new NioEventLoops(eventPolicy, 1);
        break;
      }
      case NETTY_NIO:
      {
        EventLoopGroup group = new NioEventLoopGroup(1);
        eventLoops = new NettyEventLoops(eventPolicy, group, params.eventLoopType);
        break;
      }
      case NETTY_EPOLL:
      {
        EventLoopGroup group = new EpollEventLoopGroup(1);
        eventLoops = new NettyEventLoops(eventPolicy, group, params.eventLoopType);
        break;
      }
      case NETTY_KQUEUE:
      {
        EventLoopGroup group = new KQueueEventLoopGroup(1);
        eventLoops = new NettyEventLoops(eventPolicy, group, params.eventLoopType);
        break;
      }
      case NETTY_IOURING:
      {
        EventLoopGroup group = new IOUringEventLoopGroup(1);
        eventLoops = new NettyEventLoops(eventPolicy, group, params.eventLoopType);
        break;
      }
    }
    try {
      ClientPolicy policy = new ClientPolicy();
      policy.eventLoops = eventLoops;
      policy.user = params.user;
      policy.password = params.password;
      policy.authMode = params.authMode;
      policy.tlsPolicy = params.tlsPolicy;
      params.policy = policy.readPolicyDefault;
      params.writePolicy = policy.writePolicyDefault;
      Host[] hosts = Host.parseHosts(params.host, params.port);
      IAerospikeClient client = params.useProxyClient ? new AerospikeClientProxy(policy, hosts) : new AerospikeClient(policy, hosts);
      try {
        EventLoop eventLoop = eventLoops.get(0);
        for (String exampleName : examples) {
          runExample(exampleName, client, eventLoop, params, console);
        }
        System.out.println("Sleep 2 seconds");
        Util.sleep(2000);
        System.out.println("Sleep end");
      }  finally {
        client.close();
      }
    }  finally {
      eventLoops.close();
    }
  }

  /**
	 * Run asynchronous client example.
	 */
  public static void runExample(String exampleName, IAerospikeClient client, EventLoop eventLoop, Parameters params, Console console) throws Exception {
    String fullName = "com.aerospike.examples." + exampleName;
    Class<?> cls = Class.forName(fullName);
    if (AsyncExample.class.isAssignableFrom(cls)) {
      Constructor<?> ctor = cls.getConstructor();
      AsyncExample example = (AsyncExample) ctor.newInstance();
      example.console = console;
      example.params = params;
      example.writePolicy = client.getWritePolicyDefault();
      example.policy = client.getReadPolicyDefault();
      example.run(client, eventLoop);
    } else {
      console.error("Invalid example: " + exampleName);
    }
  }

  protected Console console;

  protected Parameters params;

  protected WritePolicy writePolicy;

  protected Policy policy;

  private boolean completed;

  public void run(IAerospikeClient client, EventLoop eventLoop) {
    console.info("Example: " + this.getClass().getSimpleName());
    runExample(client, eventLoop);
  }

  protected void resetComplete() {
    completed = false;
  }

  protected synchronized void waitTillComplete() {
    while (!completed) {
      try {
        super.wait();
      } catch (InterruptedException ie) {
      }
    }
  }

  protected synchronized void notifyComplete() {
    completed = true;
    super.notify();
  }

  public abstract void runExample(IAerospikeClient client, EventLoop eventLoop);
}