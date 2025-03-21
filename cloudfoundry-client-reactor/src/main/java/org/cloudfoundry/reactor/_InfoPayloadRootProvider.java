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

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/v2/info` API for the configured endpoint.
 */
@Value.Immutable abstract class _InfoPayloadRootProvider extends AbstractRootProvider {

<<<<<<< /usr/src/app/output/cloudfoundry/cf-java-client/9f65cf5bd50bead523ac98e52e9938333f3fc08d/cloudfoundry-client-reactor/src/main/java/org/cloudfoundry/reactor/_InfoPayloadRootProvider.java/left.java
  @Override @SuppressWarnings(value = { "unchecked" }) protected Mono<Map<String, String>> doGetPayload(ConnectionContext connectionContext) {
    return getRoot(connectionContext).map((uri) -> UriComponentsBuilder.fromUriString(uri).pathSegment("v2", "info").build().encode().toUriString()).flatMap((uri) -> connectionContext.getHttpClient().get(uri, (request) -> Mono.just(request).map(UserAgent::addUserAgent).map(JsonCodec::addDecodeHeaders).flatMapMany(HttpClientRequest::send)).doOnSubscribe(NetworkLogging.get(uri)).transform(NetworkLogging.response(uri))).transform(JsonCodec.decode(getObjectMapper(), Map.class)).switchIfEmpty(Mono.error(new IllegalArgumentException("Info endpoint does not contain a payload"))).map((m) -> (Map<String, String>) m).checkpoint();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
    return Mono.just(getRoot());
  }

  protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
    return getInfo(connectionContext).map((info) -> {
      if (!info.containsKey(key)) {
        throw new IllegalArgumentException(String.format("Info payload does not contain key \'%s\'", key));
      }
      return normalize(UriComponentsBuilder.fromUriString(info.get(key)));
    });
  }

  abstract ObjectMapper getObjectMapper();

  @SuppressWarnings(value = { "unchecked" }) @Value.Derived private Mono<Map<String, String>> getInfo(ConnectionContext connectionContext) {
    return getRoot(connectionContext).map((uri) -> UriComponentsBuilder.fromUriString(uri).pathSegment("v2", "info").build().encode().toUriString()).then((uri) -> connectionContext.getHttpClient().get(uri, (request) -> Mono.just(request).map(UserAgent::addUserAgent).map(JsonCodec::addDecodeHeaders).flatMapMany(HttpClientRequest::send)).doOnSubscribe(NetworkLogging.get(uri)).transform(NetworkLogging.response(uri))).transform(JsonCodec.decode(getObjectMapper(), Map.class)).switchIfEmpty(Mono.error(new IllegalArgumentException("Info endpoint does not contain a payload"))).map((m) -> (Map<String, String>) m).checkpoint();
  }
}