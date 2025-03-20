package com.aerospike.examples;
import java.util.Map;
import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.Info;
import com.aerospike.client.cluster.Node;

public class ServerInfo extends Example {
  public ServerInfo(Console console) {
    super(console);
  }

  /**
	 * Query server configuration, cluster status and namespace configuration.
	 */
  @Override public void runExample(IAerospikeClient client, Parameters params) throws Exception {
    Node node = client.getNodes()[0];
    GetServerConfig(node, params);
    console.write("");
    GetNamespaceConfig(node, params);
  }

  /**
	 * Query server configuration and cluster status.
	 */
  private void GetServerConfig(Node node, Parameters params) throws Exception {
    console.write("Server Configuration");
    Map<String, String> map = Info.request(null, node);
    if (map == null) {
      throw new Exception(String.format("Failed to get server info: host=%s port=%d", params.host, params.port));
    }
    for (Map.Entry<String, String> entry : map.entrySet()) {
      String key = entry.getKey();
      if (key.equals("statistics") || key.equals("query-stat")) {
        LogNameValueTokens(entry.getValue());
      } else {
        console.write(key + '=' + entry.getValue());
      }
    }
  }

  /**
	 * Query namespace configuration.
	 */
  private void GetNamespaceConfig(Node node, Parameters params) throws Exception {
    console.write("Namespace Configuration");
    String filter = "namespace/" + params.namespace;
    String tokens = Info.request(null, node, filter);
    if (tokens == null) {
      throw new Exception(String.format("Failed to get namespace info: host=%s port=%d namespace=%s", params.host, params.port, params.namespace));
    }
    LogNameValueTokens(tokens);
  }

  private void LogNameValueTokens(String tokens) {
    String[] values = tokens.split(";");
    for (String value : values) {
      console.write(value);
    }
  }
}