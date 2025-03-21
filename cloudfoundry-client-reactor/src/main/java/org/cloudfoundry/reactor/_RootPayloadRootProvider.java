package org.cloudfoundry.reactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.cloudfoundry.reactor.util.UserAgent;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/` API for the configured endpoint.
 */
@Value.Immutable abstract class _RootPayloadRootProvider extends AbstractRootProvider {

<<<<<<< /usr/src/app/output/cloudfoundry/cf-java-client/9f65cf5bd50bead523ac98e52e9938333f3fc08d/cloudfoundry-client-reactor/src/main/java/org/cloudfoundry/reactor/_RootPayloadRootProvider.java/left.java
  @Override @SuppressWarnings(value = { "unchecked" }) protected Mono<Map<String, String>> doGetPayload(ConnectionContext connectionContext) {
    return getRoot(connectionContext).flatMap((uri) -> connectionContext.getHttpClient().get(uri, (request) -> Mono.just(request).map(UserAgent::addUserAgent).map(JsonCodec::addDecodeHeaders).flatMapMany(HttpClientRequest::send)).doOnSubscribe(NetworkLogging.get(uri)).transform(NetworkLogging.response(uri))).transform(JsonCodec.decode(getObjectMapper(), Map.class)).switchIfEmpty(Mono.error(new IllegalArgumentException("Root endpoint does not contain a payload"))).map(this::parsePayload).checkpoint();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
    return Mono.just(getRoot());
  }

  @Override protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
    return getPayload(connectionContext).map((payload) -> {
      if (!payload.containsKey(key)) {
        throw new IllegalArgumentException(String.format("Root payload does not contain key \'%s\'", key));
      }
      return normalize(UriComponentsBuilder.fromUriString(payload.get(key)));
    });
  }

  abstract ObjectMapper getObjectMapper();

  @SuppressWarnings(value = { "unchecked" }) @Value.Derived private Mono<Map<String, String>> getPayload(ConnectionContext connectionContext) {
    return getRoot(connectionContext).then((uri) -> connectionContext.getHttpClient().get(uri, (request) -> Mono.just(request).map(UserAgent::addUserAgent).map(JsonCodec::addDecodeHeaders).flatMapMany(HttpClientRequest::send)).doOnSubscribe(NetworkLogging.get(uri)).transform(NetworkLogging.response(uri))).transform(JsonCodec.decode(getObjectMapper(), Map.class)).switchIfEmpty(Mono.error(new IllegalArgumentException("Root endpoint does not contain a payload"))).map(this::parsePayload).checkpoint();
  }

  private Map<String, String> parsePayload(Map<String, Map<String, Map<String, String>>> payload) {
    return payload.get("links").entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> entry.getValue().get("href")));
  }
}