package org.cloudfoundry.reactor.tokenprovider;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

/**
 * The OAuth Refresh Token Grant implementation of {@link TokenProvider}
 */
@Value.Immutable abstract class _RefreshTokenGrantTokenProvider extends AbstractUaaTokenProvider {
  /**
     * The refresh token
     */
  abstract String getToken();

  @Override Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound) {
    return outbound.flatMap((request) -> request.sendForm((form) -> form.multipart(false).attr("grant_type", "refresh_token").attr("client_id", getClientId()).attr("client_secret", getClientSecret()).attr("refresh_token", getToken())).then());
  }
}