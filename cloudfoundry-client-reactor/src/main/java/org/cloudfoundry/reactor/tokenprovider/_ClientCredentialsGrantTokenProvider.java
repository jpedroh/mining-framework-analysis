package org.cloudfoundry.reactor.tokenprovider;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

/**
 * The Client Credentials Grant implementation of {@link TokenProvider}
 */
@Value.Immutable abstract class _ClientCredentialsGrantTokenProvider extends AbstractUaaTokenProvider {
  @Override Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound) {
    return outbound.flatMap((request) -> request.sendForm((form) -> form.multipart(false).attr("client_id", getClientId()).attr("client_secret", getClientSecret()).attr("grant_type", "client_credentials").attr("response_type", "token")).then());
  }
}