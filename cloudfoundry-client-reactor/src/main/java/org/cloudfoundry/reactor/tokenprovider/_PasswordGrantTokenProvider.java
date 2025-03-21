package org.cloudfoundry.reactor.tokenprovider;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

/**
 * The OAuth Password Grant implementation of {@link TokenProvider}
 */
@Value.Immutable abstract class _PasswordGrantTokenProvider extends AbstractUaaTokenProvider {
  /**
     * The password
     */
  abstract String getPassword();

  /**
     * The username
     */
  abstract String getUsername();

  @Override Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound) {
    return outbound.flatMap((request) -> request.sendForm((form) -> form.multipart(false).attr("client_id", getClientId()).attr("client_secret", getClientSecret()).attr("grant_type", "password").attr("password", getPassword()).attr("username", getUsername())).then());
  }
}