package org.cloudfoundry.reactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.Optional;

/**
 * Common, reusable, connection context
 */
public interface ConnectionContext {
  /**
     * The duration that stable responses like the payload of the API root should be cached
     */
  Optional<Duration> getCacheDuration();

  /**
     * The {@link HttpClient} to use
     */
  HttpClient getHttpClient();

  /**
     * The {@link ObjectMapper} to use
     */
  ObjectMapper getObjectMapper();

  /**
     * The {@link RootProvider} to use
     */
  RootProvider getRootProvider();

  /**
     * Attempt to explicitly trust the TLS certificate of an endpoint.  Implementations can choose whether any actual trusting will happen.
     *
     * @param host the host of the endpoint to trust
     * @param port the port of the endpoint to trust
     */
  Mono<Void> trust(String host, int port);
}