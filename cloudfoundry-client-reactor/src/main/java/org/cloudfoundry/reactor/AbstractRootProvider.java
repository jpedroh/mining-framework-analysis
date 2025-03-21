package org.cloudfoundry.reactor;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract implementation of {@link RootProvider} that ensures that returned values are trusted (if configured) and cached.
 */
abstract class AbstractRootProvider implements RootProvider {
  private static final int DEFAULT_PORT = 443;

  private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-.]+$");

  private static final int UNDEFINED_PORT = -1;

  @Value.Check public final void checkForValidApiHost() {
    Matcher matcher = HOSTNAME_PATTERN.matcher(getApiHost());
    if (!matcher.matches()) {
      throw new IllegalArgumentException(String.format("API hostname %s is not correctly formatted (e.g. \'api.local.pcfdev.io\')", getApiHost()));
    }
  }

  /**
     * The hostname of the API root.  Typically something like {@code api.run.pivotal.io}.
     */
  public abstract String getApiHost();

  @Override public final Mono<String> getRoot(ConnectionContext connectionContext) {
    Mono<String> cached = doGetRoot(connectionContext).delayUntil((uri) -> trust(uri.getHost(), uri.getPort(), connectionContext)).map(UriComponents::toUriString);
    return connectionContext.getCacheDuration().map(cached::cache).orElseGet(cached::cache);
  }

  @Override public final Mono<String> getRoot(String key, ConnectionContext connectionContext) {
    Mono<String> cached = doGetRoot(key, connectionContext).delayUntil((uri) -> trust(uri.getHost(), uri.getPort(), connectionContext)).map(UriComponents::toUriString);
    return connectionContext.getCacheDuration().map(cached::cache).orElseGet(cached::cache);
  }

  protected abstract Mono<UriComponents> doGetRoot(ConnectionContext connectionContext);

  protected abstract Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext);

  protected final UriComponents getRoot() {
    UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("https").host(getApiHost());
    getPort().ifPresent(builder::port);
    return normalize(builder);
  }

  protected final UriComponents normalize(UriComponentsBuilder builder) {
    UriComponents components = builder.build();
    builder.scheme(getScheme());
    if (UNDEFINED_PORT == components.getPort()) {
      builder.port(getPort().orElse(DEFAULT_PORT));
    }
    return builder.build().encode();
  }

  /**
     * The port for the Cloud Foundry instance. Defaults to {@code 443}.
     */
  abstract Optional<Integer> getPort();

  /**
     * Whether the connection to the root API should be secure (i.e. using HTTPS).  Defaults to {@code true}.
     */
  abstract Optional<Boolean> getSecure();

  private String getScheme() {
    if (getSecure().orElse(true)) {
      return "https";
    } else {
      return "http";
    }
  }

  private Mono<Void> trust(String host, int port, ConnectionContext connectionContext) {
    return connectionContext.trust(host, port);
  }
}