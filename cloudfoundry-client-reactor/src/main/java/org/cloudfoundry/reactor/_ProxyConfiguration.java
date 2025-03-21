package org.cloudfoundry.reactor;
import org.immutables.value.Value;
import reactor.ipc.netty.options.ClientOptions;
import reactor.ipc.netty.options.ClientProxyOptions;
import java.util.Optional;
import java.util.function.Function;

/**
 * Proxy configuration
 */
@Value.Immutable abstract class _ProxyConfiguration {
  public void configure(ClientOptions.Builder<?> options) {
    options.proxy((typeSpec) -> {
      ClientProxyOptions.Builder builder = typeSpec.type(ClientProxyOptions.Proxy.HTTP).host(getHost());
      getPort().ifPresent(builder::port);
      getUsername().ifPresent(builder::username);
      getPassword().map((password) -> (Function<String, String>) (s) -> password).ifPresent(builder::password);
      return builder;
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