package com.tumblr.jumblr.request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.responses.JsonElementDeserializer;
import com.tumblr.jumblr.responses.ResponseWrapper;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * Where requests are made from
 * @author jc
 */
public class RequestBuilder {
  private Token token;

  private OAuthService service;

  private 
<<<<<<< /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/left.java
  String
=======
  URI
>>>>>>> /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/right.java
   
<<<<<<< /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/left.java
  hostname = "api.tumblr.com"
=======
  callbackUrl
>>>>>>> /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/right.java
  ;

  private 
<<<<<<< /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/left.java
  String
=======
  Token
>>>>>>> /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/right.java
   
<<<<<<< /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/left.java
  xauthEndpoint = "https://www.tumblr.com/oauth/access_token"
=======
  requestToken
>>>>>>> /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/com/tumblr/jumblr/request/RequestBuilder.java/right.java
  ;

  private String version = "0.0.11";

  private final JumblrClient client;

  public RequestBuilder(JumblrClient client) {
    this.client = client;
    try {
      ServerSocket s = new ServerSocket(0);
      int port = s.getLocalPort();
      s.close();
      callbackUrl = new URI("http", null, "127.0.0.1", port, "/", null, null);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    System.out.println(callbackUrl);
  }

  public String getRedirectUrl(String path) {
    OAuthRequest request = this.constructGet(path, null);
    sign(request);
    boolean presetVal = HttpURLConnection.getFollowRedirects();
    HttpURLConnection.setFollowRedirects(false);
    Response response = request.send();
    HttpURLConnection.setFollowRedirects(presetVal);
    if (response.getCode() == 301) {
      return response.getHeader("Location");
    } else {
      throw new JumblrException(response);
    }
  }

  public ResponseWrapper postMultipart(String path, Map<String, ?> bodyMap) throws IOException {
    OAuthRequest request = this.constructPost(path, bodyMap);
    sign(request);
    OAuthRequest newRequest = RequestBuilder.convertToMultipart(request, bodyMap);
    return clear(newRequest.send());
  }

  public ResponseWrapper post(String path, Map<String, ?> bodyMap) {
    OAuthRequest request = this.constructPost(path, bodyMap);
    sign(request);
    return clear(request.send());
  }

  /**
     * Posts an XAuth request. A new method is needed because the response from
     * the server is not a standard Tumblr JSON response.
     * @param email the user's login email.
     * @param password the user's password.
     * @return the login token.
     */
  public Token postXAuth(final String email, final String password) {
    OAuthRequest request = constructXAuthPost(email, password);
    setToken("", "");
    sign(request);
    return clearXAuth(request.send());
  }

  private OAuthRequest constructXAuthPost(String email, String password) {
    OAuthRequest request = new OAuthRequest(Verb.POST, xauthEndpoint);
    request.addBodyParameter("x_auth_username", email);
    request.addBodyParameter("x_auth_password", password);
    request.addBodyParameter("x_auth_mode", "client_auth");
    return request;
  }

  public ResponseWrapper get(String path, Map<String, ?> map) {
    OAuthRequest request = this.constructGet(path, map);
    sign(request);
    return clear(request.send());
  }

  public OAuthRequest constructGet(String path, Map<String, ?> queryParams) {
    String url = "https://" + hostname + "/v2" + path;
    OAuthRequest request = new OAuthRequest(Verb.GET, url);
    if (queryParams != null) {
      for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
        request.addQuerystringParameter(entry.getKey(), entry.getValue().toString());
      }
    }
    request.addHeader("User-Agent", "jumblr/" + this.version);
    return request;
  }

  private OAuthRequest constructPost(String path, Map<String, ?> bodyMap) {
    String url = "https://" + hostname + "/v2" + path;
    OAuthRequest request = new OAuthRequest(Verb.POST, url);
    for (Map.Entry<String, ?> entry : bodyMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value == null || value instanceof File) {
        continue;
      }
      request.addBodyParameter(key, value.toString());
    }
    request.addHeader("User-Agent", "jumblr/" + this.version);
    return request;
  }

  public void setCallback(URI callbackUrl) {
    if (callbackUrl != null) {
      this.callbackUrl = callbackUrl;
    }
  }

  public void setConsumer(String consumerKey, String consumerSecret) {
    service = new ServiceBuilder().provider(TumblrApi.class).apiKey(consumerKey).apiSecret(consumerSecret).callback(callbackUrl.toString()).build();
  }

  public void setToken(String token, String tokenSecret) {
    setToken(new Token(token, tokenSecret));
  }

  public private void setToken(final Token token) {
    this.token = token;
  }

  private void verify(Verifier verifier) {
    setToken(service.getAccessToken(service.getRequestToken(), verifier));
  }

  public void verify(String verifier) {
    verify(new Verifier(verifier));
  }

  public String getAuthorizationUrl() {
    return service.getAuthorizationUrl(requestToken);
  }

  public boolean authenticate() {
    Token verifier;
    try {
      verifier = Authenticator.autoAuthenticate(service, "oauth_verifier", callbackUrl);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    if (verifier == null) {
      return false;
    }
    setToken(verifier);
    return true;
  }

  ResponseWrapper clear(Response response) {
    if (response.getCode() == 200 || response.getCode() == 201) {
      String json = response.getBody();
      try {
        Gson gson = new GsonBuilder().registerTypeAdapter(JsonElement.class, new JsonElementDeserializer()).create();
        ResponseWrapper wrapper = gson.fromJson(json, ResponseWrapper.class);
        if (wrapper == null) {
          throw new JumblrException(response);
        }
        wrapper.setClient(client);
        return wrapper;
      } catch (JsonSyntaxException ex) {
        throw new JumblrException(response);
      }
    } else {
      throw new JumblrException(response);
    }
  }

  private Token parseXAuthResponse(final Response response) {
    String responseStr = response.getBody();
    if (responseStr != null) {
      String extractedToken = null, extractedSecret = null;
      final String[] values = responseStr.split("&");
      for (String value : values) {
        final String[] kvp = value.split("=");
        if (kvp != null && kvp.length == 2) {
          if (kvp[0].equals("oauth_token")) {
            extractedToken = kvp[1];
          } else {
            if (kvp[0].equals("oauth_token_secret")) {
              extractedSecret = kvp[1];
            }
          }
        }
      }
      if (extractedToken != null && extractedSecret != null) {
        return new Token(extractedToken, extractedSecret);
      }
    }
    throw new JumblrException(response);
  }

  Token clearXAuth(Response response) {
    if (response.getCode() == 200 || response.getCode() == 201) {
      return parseXAuthResponse(response);
    } else {
      throw new JumblrException(response);
    }
  }

  private void sign(OAuthRequest request) {
    if (token != null) {
      service.signRequest(token, request);
    }
  }

  public static OAuthRequest convertToMultipart(OAuthRequest request, Map<String, ?> bodyMap) throws IOException {
    return new MultipartConverter(request, bodyMap).getRequest();
  }

  public String getHostname() {
    return hostname;
  }

  /**
     * Set hostname without protocol
     * @param host such as "api.tumblr.com"
     */
  public void setHostname(String host) {
    this.hostname = host;
  }
}