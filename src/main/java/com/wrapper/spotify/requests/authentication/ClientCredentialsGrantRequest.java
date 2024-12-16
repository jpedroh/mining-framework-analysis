package com.wrapper.spotify.requests.authentication;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.*;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.AbstractRequest;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;


public class ClientCredentialsGrantRequest extends AbstractRequest {
  public ClientCredentialsGrantRequest(Builder builder) {
    super(builder);
  }

  public SettableFuture<ClientCredentials> executeAsync() {
    final SettableFuture<ClientCredentials> future = SettableFuture.create();
    try {
      JsonObject jsonObject = new JsonParser().parse(postJson()).getAsJsonObject();
      future.set(new ClientCredentials.JsonUtil().createModelObject(jsonObject));
    } catch (java.lang.Exception e) {
      future.setException(e);
    }
    return future;
  }

  public ClientCredentials get() throws IOException, NoContentException, BadRequestException, UnauthorizedException, ForbiddenException, NotFoundException, TooManyRequestsException, InternalServerErrorException, BadGatewayException, ServiceUnavailableException {
    JsonObject jsonObject = new JsonParser().parse(postJson()).getAsJsonObject();
    return new ClientCredentials.JsonUtil().createModelObject(jsonObject);
  }

  public static final class Builder extends AbstractRequest.Builder<Builder> {
    public Builder(final String accessToken) {
      super();
    }

    public Builder basicAuthorizationHeader(String clientId, String clientSecret) {
      assert clientId != null;
      assert clientSecret != null;
      String idSecret = (clientId + ":") + clientSecret;
      String idSecretEncoded = new String(Base64.encodeBase64(idSecret.getBytes()));
      return setHeader("Authorization", "Basic " + idSecretEncoded);
    }

    public Builder grantType(String grantType) {
      assert grantType != null;
      return setQueryParameter("grant_type", grantType);
    }

    public Builder scopes(String... scopes) {
      assert scopes != null;
      return setQueryParameter("scope", Joiner.on(" ").join(scopes));
    }

    public ClientCredentialsGrantRequest build() {
      setHost(Api.DEFAULT_AUTHENTICATION_HOST);
      setPort(Api.DEFAULT_AUTHENTICATION_PORT);
      setScheme(Api.DEFAULT_AUTHENTICATION_SCHEME);
      setPath("/api/token");

      return new ClientCredentialsGrantRequest(this);
    }
  }
}