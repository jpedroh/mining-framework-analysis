package org.cloudfoundry.reactor.tokenprovider;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

/**
 * The One-time Passcode Password Grant implementation of {@link TokenProvider}
 */
@Value.Immutable abstract class _OneTimePasscodeTokenProvider extends AbstractUaaTokenProvider {
  /**
     * The passcode
     */
  abstract String getPasscode();

  @Override Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound) {
    return outbound.flatMap((request) -> request.sendForm((form) -> form.multipart(false).attr("client_id", getClientId()).attr("client_secret", getClientSecret()).attr("grant_type", "password").attr("passcode", getPasscode())).then());
  }
}