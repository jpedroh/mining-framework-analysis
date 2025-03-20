package com.aerospike.examples;
import java.lang.reflect.Constructor;
import java.util.List;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.proxy.AerospikeClientProxy;

public abstract class Example {
  protected static final int DEFAULT_TIMEOUT_MS = 1000;

  /**
	 * Connect and run one or more client examples.
	 */
  public static void runExamples(Console console, Parameters params, List<String> examples) throws Exception {
    ClientPolicy policy = new ClientPolicy();
    policy.user = params.user;
    policy.password = params.password;
    policy.authMode = params.authMode;
    policy.tlsPolicy = params.tlsPolicy;
    params.policy = policy.readPolicyDefault;
    params.writePolicy = policy.writePolicyDefault;
    Host[] hosts = Host.parseHosts(params.host, params.port);
    IAerospikeClient client = params.useProxyClient ? new AerospikeClientProxy(policy, hosts) : new AerospikeClient(policy, hosts);
    try {
      for (String exampleName : examples) {
        runExample(exampleName, client, params, console);
      }
    }  finally {
      client.close();
    }
  }

  /**
	 * Run client example.
	 */
  public static void runExample(String exampleName, IAerospikeClient client, Parameters params, Console console) throws Exception {
    String fullName = "com.aerospike.examples." + exampleName;
    Class<?> cls = Class.forName(fullName);
    if (Example.class.isAssignableFrom(cls)) {
      Constructor<?> ctor = cls.getDeclaredConstructor(Console.class);
      Example example = (Example) ctor.newInstance(console);
      example.run(client, params);
    } else {
      console.error("Invalid example: " + exampleName);
    }
  }

  protected Console console;

  public Example(Console console) {
    this.console = console;
  }

  public void run(IAerospikeClient client, Parameters params) throws Exception {
    console.info(this.getClass().getSimpleName() + " Begin");
    runExample(client, params);
    console.info(this.getClass().getSimpleName() + " End");
  }

  public abstract void runExample(IAerospikeClient client, Parameters params) throws Exception;
}