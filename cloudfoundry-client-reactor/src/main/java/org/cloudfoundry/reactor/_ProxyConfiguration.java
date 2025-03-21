package org.cloudfoundry.reactor;
import org.immutables.value.Value;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.ProxyProvider.Builder;
import reactor.netty.transport.ProxyProvider.Proxy;
import java.util.Optional;
import java.util.function.Function;

/**
 * Proxy configuration
 */
@Value.Immutable abstract class _ProxyConfiguration {
  public TcpClient configure(TcpClient tcpClient) {
    return tcpClient.proxy((proxyOptions) -> {
      Builder builder = proxyOptions.type(Proxy.HTTP).host(getHost());
      getPort().ifPresent(builder::port);
      getUsername().ifPresent(builder::username);
      getPassword().map((password) -> (Function<String, String>) (s) -> password).ifPresent(builder::password);
    });
  }

  public HttpClient configure(HttpClient client) {
    return client.proxy((proxyOptions) -> {
      Builder builder = proxyOptions.type(Proxy.HTTP).host(getHost());
      getPort().ifPresent(builder::port);
      getUsername().ifPresent(builder::username);
      getPassword().map((password) -> (Function<String, String>) (s) -> password).ifPresent(builder::password);
    });
  }

  /**
     * The proxy host
     */
  abstract String getHost();

  /**
     * The proxy password
     */
  abstract Optional<String> getPassword();

  /**
     * The proxy port
     */
  abstract Optional<Integer> getPort();

  /**
     * The proxy username
     */
  abstract Optional<String> getUsername();
}