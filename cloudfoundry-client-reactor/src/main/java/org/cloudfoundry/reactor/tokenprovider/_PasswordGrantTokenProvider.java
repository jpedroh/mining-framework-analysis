package org.cloudfoundry.reactor.tokenprovider;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

/**
 * The OAuth Password Grant implementation of {@link TokenProvider}
 */
@Value.Immutable abstract class _PasswordGrantTokenProvider extends AbstractUaaTokenProvider {
  /**
     * The login hint
     */
  @Nullable abstract String getLoginHint();

  /**
     * The password
     */
  abstract String getPassword();

  /**
     * The username
     */
  abstract String getUsername();

  @Override Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound) {
    return outbound.
<<<<<<< /usr/src/app/output/cloudfoundry/cf-java-client/efacf882a2aa27f80262e26302d137f9cad07380/cloudfoundry-client-reactor/src/main/java/org/cloudfoundry/reactor/tokenprovider/_PasswordGrantTokenProvider.java/left.java
    flatMap((request) -> request.sendForm((form) -> form.multipart(false).attr("client_id", getClientId()).attr("client_secret", getClientSecret()).attr("grant_type", "password").attr("password", getPassword()).attr("username", getUsername())).then())
=======
    then((request) -> request.sendForm((form) -> form.multipart(false).attr("client_id", getClientId()).attr("client_secret", getClientSecret()).attr("grant_type", "password").attr("password", getPassword()).attr("username", getUsername()).attr("login_hint", getLoginHint())).then())
>>>>>>> /usr/src/app/output/cloudfoundry/cf-java-client/efacf882a2aa27f80262e26302d137f9cad07380/cloudfoundry-client-reactor/src/main/java/org/cloudfoundry/reactor/tokenprovider/_PasswordGrantTokenProvider.java/right.java
    ;
  }
}