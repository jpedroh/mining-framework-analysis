package org.cloudfoundry.uaa;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.tokens.CheckTokenRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByAuthorizationCodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByOneTimePasscodeRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByOneTimePasscodeResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByOpenIdRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByOpenIdResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByPasswordRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByPasswordResponse;
import org.cloudfoundry.uaa.tokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.tokens.GetTokenKeyResponse;
import org.cloudfoundry.uaa.tokens.ListTokenKeysRequest;
import org.cloudfoundry.uaa.tokens.RefreshTokenRequest;
import org.cloudfoundry.uaa.tokens.TokenFormat;
import org.cloudfoundry.uaa.tokens.TokenKey;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import static org.assertj.core.api.Assertions.assertThat;

public final class TokensTest extends AbstractIntegrationTest {
  @Autowired private String clientId;

  @Autowired private String clientSecret;

  @Autowired private ConnectionContext connectionContext;

  @Autowired private TokenProvider tokenProvider;

  @Autowired private UaaClient uaaClient;

  @Test public void checkTokenNotAuthorized() {
    this.tokenProvider.getToken(this.connectionContext).flatMap((token) -> this.uaaClient.tokens().check(CheckTokenRequest.builder().token(token).clientId(this.clientId).clientSecret(this.clientSecret).scope("password.write").scope("scim.userids").build())).as(StepVerifier::create).consumeErrorWith((t) -> assertThat(t).isInstanceOf(UaaException.class).hasMessage("access_denied: Access is denied")).verify(Duration.ofMinutes(5));
  }

  @Ignore(value = "Ready to Implement - use test authorizationCode") @Test public void getTokenByAuthorizationCode() {
    this.uaaClient.tokens().getByAuthorizationCode(GetTokenByAuthorizationCodeRequest.builder().authorizationCode("some auth code").clientId(this.clientId).clientSecret(this.clientSecret).build()).as(StepVerifier::create).expectNextCount(1).expectComplete().verify(Duration.ofMinutes(5));
  }

  @Test public void getTokenByClientCredentials() {
    this.uaaClient.tokens().getByClientCredentials(GetTokenByClientCredentialsRequest.builder().clientId(this.clientId).clientSecret(this.clientSecret).tokenFormat(TokenFormat.OPAQUE).build()).map(GetTokenByClientCredentialsResponse::getTokenType).as(StepVerifier::create).expectNext("bearer").expectComplete().verify(Duration.ofMinutes(5));
  }

  @Ignore(value = "Ready to Implement - use test one-time passcode") @Test public void getTokenByOneTimePasscode() {
    this.uaaClient.tokens().getByOneTimePasscode(GetTokenByOneTimePasscodeRequest.builder().passcode("Some passcode").clientId(this.clientId).clientSecret(this.clientSecret).tokenFormat(TokenFormat.OPAQUE).build()).map(GetTokenByOneTimePasscodeResponse::getTokenType).as(StepVerifier::create).expectNext("bearer").expectComplete();
  }

  @Ignore(value = "Ready to Implement - use test openid authorizationCode") @Test public void getTokenByOpenId() {
    this.uaaClient.tokens().getByOpenId(GetTokenByOpenIdRequest.builder().authorizationCode("Some authorization code").clientId(this.clientId).clientSecret(this.clientSecret).tokenFormat(TokenFormat.OPAQUE).build()).map(GetTokenByOpenIdResponse::getTokenType).as(StepVerifier::create).expectNext("bearer").expectComplete().verify(Duration.ofMinutes(5));
  }

  @Ignore(value = "Ready to Implement - use test username and password") @Test public void getTokenByPassword() {
    this.uaaClient.tokens().getByPassword(GetTokenByPasswordRequest.builder().password("a-password").username("a-username").clientId(this.clientId).clientSecret(this.clientSecret).tokenFormat(TokenFormat.OPAQUE).build()).map(GetTokenByPasswordResponse::getTokenType).as(StepVerifier::create).expectNext("bearer").expectComplete().verify(Duration.ofMinutes(5));
  }

  @Test public void getTokenKey() {
    this.uaaClient.tokens().getKey(GetTokenKeyRequest.builder().build()).as(StepVerifier::create).expectNextCount(1).expectComplete().verify(Duration.ofMinutes(5));
  }

  @Test public void listTokenKeys() {
    this.uaaClient.tokens().getKey(GetTokenKeyRequest.builder().build()).flatMap((getKey) -> Mono.zip(this.uaaClient.tokens().listKeys(ListTokenKeysRequest.builder().build()).flatMapMany((response) -> Flux.fromIterable(response.getKeys())).filter((tokenKey) -> getKey.getValue().equals(tokenKey.getValue())).single().map(TokenKey::getId), Mono.just(getKey).map(GetTokenKeyResponse::getId))).as(StepVerifier::create).consumeNextWith(tupleEquality()).expectComplete().verify(Duration.ofMinutes(5));
  }

  @Ignore(value = "Ready to Implement - use test refresh token") @Test public void refreshToken() {
    this.uaaClient.tokens().refresh(RefreshTokenRequest.builder().tokenFormat(TokenFormat.OPAQUE).clientId(this.clientId).clientSecret(this.clientSecret).refreshToken("a-refresh-token").build()).as(StepVerifier::create).expectNextCount(1).expectComplete().verify(Duration.ofMinutes(5));
  }
}