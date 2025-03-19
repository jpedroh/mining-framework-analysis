package com.wrapper.spotify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wrapper.spotify.exceptions.*;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import static com.wrapper.spotify.UrlUtil.getParametersList;
import com.wrapper.spotify.UtilProtos.Url;
import com.wrapper.spotify.exceptions.EmptyResponseException;
import org.apache.commons.httpclient.methods.*;

public class SpotifyHttpManager implements HttpManager {

  private HttpClientConnectionManager connectionManager = null;

  /**
   * Construct a new SpotifyHttpManager instance.
   *
   * @param builder The builder.
   */
  public SpotifyHttpManager(Builder builder) {
    if (builder.connectionManager != null) {
      connectionManager = builder.connectionManager;
    } else {
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/aef7efe10ad88bbe4c1aa4cf39593f8c53eb7c90/src/main/java/com/wrapper/spotify/SpotifyHttpManager.java/left.java
      connectionManager = new PoolingHttpClientConnectionManager();
||||||| /usr/src/app/output/thelinmichael/spotify-web-api-java/aef7efe10ad88bbe4c1aa4cf39593f8c53eb7c90/src/main/java/com/wrapper/spotify/SpotifyHttpManager.java/base.java
      connectionManager = new MultiThreadedHttpConnectionManager();
    }
  }

  @Override
  public String get(Url url) throws WebApiException, IOException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final GetMethod method = new GetMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  @Override
  public String post(UtilProtos.Url url) throws IOException, WebApiException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final PostMethod method = new PostMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }

    if (url.hasJsonBody()) {

      StringRequestEntity requestEntity = new StringRequestEntity(
              url.getJsonBody(),
              "application/json",
              "UTF-8");
      method.setRequestEntity(requestEntity);
    } else {
      method.setRequestBody(getBodyParametersAsNamedValuePairArray(url));
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  @Override
  public String put(UtilProtos.Url url) throws IOException, WebApiException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final PutMethod method = new PutMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }

    if (url.hasJsonBody()) {

      StringRequestEntity requestEntity = new StringRequestEntity(
          url.getJsonBody(),
          "application/json",
          "UTF-8");
      method.setRequestEntity(requestEntity);
    } else {
      method.setRequestBody(String.valueOf(getBodyParametersAsNamedValuePairArray(url)));
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }


  // TODO(michael): Allow JSON body to be sent.
  @Override
  public String delete(UtilProtos.Url url) throws IOException, WebApiException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final DeleteMethod method = new DeleteMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }

    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  private NameValuePair[] getParametersAsNamedValuePairArray(Url url) {
    List<NameValuePair> out = new ArrayList<NameValuePair>();
    for (Url.Parameter parameter : url.getParametersList()) {
      if (parameter.hasName() && parameter.hasValue()) {
        out.add(new NameValuePair(parameter.getName(), parameter.getValue().toString()));
      }
    }
    return out.toArray(new NameValuePair[out.size()]);
  }

  private NameValuePair[] getBodyParametersAsNamedValuePairArray(Url url) {
    List<NameValuePair> out = new ArrayList<NameValuePair>();
    for (Url.Parameter parameter : url.getBodyParametersList()) {
      if (parameter.hasName() && parameter.hasValue()) {
        out.add(new NameValuePair(parameter.getName(), parameter.getValue().toString()));
      }
    }
    return out.toArray(new NameValuePair[out.size()]);
  }

  private String execute(HttpMethod method) throws WebApiException, IOException {
    final HttpClient httpClient = new HttpClient(connectionManager);
    try {
      httpClient.executeMethod(method);

      handleErrorStatusCode(method);
      String responseBody = method.getResponseBodyAsString();

      handleErrorResponseBody(responseBody);
      return responseBody;

    } catch (IOException e) {
      throw new IOException();
    } finally {
      method.releaseConnection();
    }
  }

  /*
   * Todo: Error handling could be more granular and throw a different exception depending on status code.
   * It could also look into the JSON object to find an error message.
   */
  private void handleErrorStatusCode(HttpMethod method) throws BadRequestException, ServerErrorException {
    int statusCode = method.getStatusCode();

    if (statusCode >= 400 && statusCode < 500) {
      throw new BadRequestException(String.valueOf(statusCode));
    }
    if (statusCode >= 500) {
      throw new ServerErrorException(String.valueOf(statusCode));
    }

  }

  private void handleErrorResponseBody(String responseBody) throws WebApiException {
    if (responseBody == null) {
      throw new EmptyResponseException("No response body");
    }

    if (!responseBody.equals("") && responseBody.startsWith("{")) {
      final JSONObject jsonObject = JSONObject.fromObject(responseBody);
      if (jsonObject.has("error")) {
        throw new WebApiException(jsonObject.getString("error"));
      }
=======
      connectionManager = new MultiThreadedHttpConnectionManager();
    }
  }

  @Override
  public String get(Url url) throws WebApiException, IOException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final GetMethod method = new GetMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  @Override
  public String post(UtilProtos.Url url) throws IOException, WebApiException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final PostMethod method = new PostMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }

    if (url.hasJsonBody()) {

      StringRequestEntity requestEntity = new StringRequestEntity(
              url.getJsonBody(),
              "application/json",
              "UTF-8");
      method.setRequestEntity(requestEntity);
    } else {
      method.setRequestBody(getBodyParametersAsNamedValuePairArray(url));
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  @Override
  public String put(UtilProtos.Url url) throws IOException, WebApiException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    final PutMethod method = new PutMethod(uri);

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }

    if (url.hasJsonBody()) {

      StringRequestEntity requestEntity = new StringRequestEntity(
          url.getJsonBody(),
          "application/json",
          "UTF-8");
      method.setRequestEntity(requestEntity);
    } else {
      method.setRequestBody(String.valueOf(getBodyParametersAsNamedValuePairArray(url)));
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  @Override
  public String delete(UtilProtos.Url url) throws IOException, WebApiException {
    assert (url != null);

    final String uri = UrlUtil.assemble(url);
    //We can not use the DeleteMethod object here because it does not allow a body
    final EntityEnclosingMethod method = new EntityEnclosingMethod(uri)
    {
      public String getName() {
        return "DELETE";
      }
    };

    for (Url.Parameter header : url.getHeaderParametersList()) {
      method.setRequestHeader(header.getName(), header.getValue());
    }

    if (url.hasJsonBody()) {

      StringRequestEntity requestEntity = new StringRequestEntity(
          url.getJsonBody(),
          "application/json",
          "UTF-8");
      method.setRequestEntity(requestEntity);
    } else {
      method.setRequestBody(String.valueOf(getBodyParametersAsNamedValuePairArray(url)));
    }
    method.setQueryString(getParametersAsNamedValuePairArray(url));
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    return execute(method);
  }

  private NameValuePair[] getParametersAsNamedValuePairArray(Url url) {
    List<NameValuePair> out = new ArrayList<NameValuePair>();
    for (Url.Parameter parameter : url.getParametersList()) {
      if (parameter.hasName() && parameter.hasValue()) {
        out.add(new NameValuePair(parameter.getName(), parameter.getValue().toString()));
      }
    }
    return out.toArray(new NameValuePair[out.size()]);
  }

  private NameValuePair[] getBodyParametersAsNamedValuePairArray(Url url) {
    List<NameValuePair> out = new ArrayList<NameValuePair>();
    for (Url.Parameter parameter : url.getBodyParametersList()) {
      if (parameter.hasName() && parameter.hasValue()) {
        out.add(new NameValuePair(parameter.getName(), parameter.getValue().toString()));
      }
    }
    return out.toArray(new NameValuePair[out.size()]);
  }

  private String execute(HttpMethod method) throws WebApiException, IOException {
    final HttpClient httpClient = new HttpClient(connectionManager);
    try {
      httpClient.executeMethod(method);

      handleErrorStatusCode(method);
      String responseBody = method.getResponseBodyAsString();

      handleErrorResponseBody(responseBody);
      return responseBody;

    } catch (IOException e) {
      throw new IOException();
    } finally {
      method.releaseConnection();
    }
  }

  /*
   * Todo: Error handling could be more granular and throw a different exception depending on status code.
   * It could also look into the JSON object to find an error message.
   */
  private void handleErrorStatusCode(HttpMethod method) throws BadRequestException, ServerErrorException {
    int statusCode = method.getStatusCode();

    if (statusCode >= 400 && statusCode < 500) {
      throw new BadRequestException(String.valueOf(statusCode));
    }
    if (statusCode >= 500) {
      throw new ServerErrorException(String.valueOf(statusCode));
    }

  }

  private void handleErrorResponseBody(String responseBody) throws WebApiException {
    if (responseBody == null) {
      throw new EmptyResponseException("No response body");
    }

    if (!responseBody.equals("") && responseBody.startsWith("{")) {
      final JSONObject jsonObject = JSONObject.fromObject(responseBody);
      if (jsonObject.has("error")) {
        throw new WebApiException(jsonObject.getString("error"));
      }
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/aef7efe10ad88bbe4c1aa4cf39593f8c53eb7c90/src/main/java/com/wrapper/spotify/SpotifyHttpManager.java/right.java
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String get(Url url) throws
          IOException,
          NoContentException,
          BadRequestException,
          UnauthorizedException,
          ForbiddenException,
          NotFoundException,
          TooManyRequestsException,
          InternalServerErrorException,
          BadGatewayException,
          ServiceUnavailableException {
    assert (url != null);

    final HttpGet method = new HttpGet(UrlUtil.urlToUri(url));
    method.setHeaders(UrlUtil.getHeaders(url));

    String responseBody = getResponseBody(execute(method));
    method.releaseConnection();

    return responseBody;
  }

  @Override
  public String post(UtilProtos.Url url) throws
          IOException,
          NoContentException,
          BadRequestException,
          UnauthorizedException,
          ForbiddenException,
          NotFoundException,
          TooManyRequestsException,
          InternalServerErrorException,
          BadGatewayException,
          ServiceUnavailableException {
    assert (url != null);

    final HttpPost method = new HttpPost(UrlUtil.urlToUri(url));
    method.setHeaders(UrlUtil.getHeaders(url));

    if (url.hasJsonBody()) {
      method.setEntity(new StringEntity(url.getJsonBody(), ContentType.APPLICATION_JSON));
    } else {
      method.setEntity(new UrlEncodedFormEntity(getParametersList(url)));
    }

    String responseBody = getResponseBody(execute(method));
    method.releaseConnection();

    return responseBody;
  }

  @Override
  public String put(UtilProtos.Url url) throws
          IOException,
          NoContentException,
          BadRequestException,
          UnauthorizedException,
          ForbiddenException,
          NotFoundException,
          TooManyRequestsException,
          InternalServerErrorException,
          BadGatewayException,
          ServiceUnavailableException {
    assert (url != null);

    final HttpPut method = new HttpPut(UrlUtil.urlToUri(url));
    method.setHeaders(UrlUtil.getHeaders(url));

    if (url.hasJsonBody()) {
      method.setEntity(new StringEntity(url.getJsonBody(), ContentType.APPLICATION_JSON));
    } else {
      method.setEntity(new UrlEncodedFormEntity(getParametersList(url)));
    }

    String responseBody = getResponseBody(execute(method));
    method.releaseConnection();

    return responseBody;
  }

  @Override
  public String delete(UtilProtos.Url url) throws
          IOException,
          NoContentException,
          BadRequestException,
          UnauthorizedException,
          ForbiddenException,
          NotFoundException,
          TooManyRequestsException,
          InternalServerErrorException,
          BadGatewayException,
          ServiceUnavailableException {
    assert (url != null);

    final HttpDelete method = new HttpDelete(UrlUtil.urlToUri(url));
    method.setHeaders(UrlUtil.getHeaders(url));

    String responseBody = getResponseBody(execute(method));
    method.releaseConnection();

    return responseBody;
  }

  private CloseableHttpResponse execute(HttpRequestBase method) throws
          IOException,
          NoContentException,
          BadRequestException,
          UnauthorizedException,
          ForbiddenException,
          NotFoundException,
          TooManyRequestsException,
          InternalServerErrorException,
          BadGatewayException,
          ServiceUnavailableException {
    final ConnectionConfig connectionConfig = ConnectionConfig
            .custom()
            .setCharset(Charset.forName("UTF-8"))
            .build();
    final RequestConfig requestConfig = RequestConfig
            .custom()
            .setCookieSpec(CookieSpecs.DEFAULT)
            .build();
    final CloseableHttpClient httpClient = HttpClients
            .custom()
            .setConnectionManager(connectionManager)
            .setDefaultConnectionConfig(connectionConfig)
            .setDefaultRequestConfig(requestConfig)
            .build();

    return httpClient.execute(method);
  }

  private String getResponseBody(CloseableHttpResponse httpResponse) throws
          IOException,
          NoContentException,
          BadRequestException,
          UnauthorizedException,
          ForbiddenException,
          NotFoundException,
          TooManyRequestsException,
          InternalServerErrorException,
          BadGatewayException,
          ServiceUnavailableException {
    StatusLine statusLine = httpResponse.getStatusLine();
    String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

    final JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();

    switch (statusLine.getStatusCode()) {
      case HttpStatus.SC_OK:
        return responseBody;
      case HttpStatus.SC_CREATED:
        return responseBody;
      case HttpStatus.SC_ACCEPTED:
        return responseBody;
      case HttpStatus.SC_NO_CONTENT:
        throw new NoContentException(statusLine.getReasonPhrase());
      case HttpStatus.SC_NOT_MODIFIED:
        return responseBody;
      case HttpStatus.SC_BAD_REQUEST:
        if (jsonObject.has("error")) {
          throw new BadRequestException(jsonObject.get("error").getAsString());
        }
      case HttpStatus.SC_UNAUTHORIZED:
        throw new UnauthorizedException(statusLine.getReasonPhrase());
      case HttpStatus.SC_FORBIDDEN:
        throw new ForbiddenException(statusLine.getReasonPhrase());
      case HttpStatus.SC_NOT_FOUND:
        throw new NotFoundException(statusLine.getReasonPhrase());
      case 429: // TOO_MANY_REQUESTS (additional status code, RFC 6585)
        throw new TooManyRequestsException(statusLine.getReasonPhrase());
      case HttpStatus.SC_INTERNAL_SERVER_ERROR:
        throw new InternalServerErrorException(statusLine.getReasonPhrase());
      case HttpStatus.SC_BAD_GATEWAY:
        throw new BadGatewayException(statusLine.getReasonPhrase());
      case HttpStatus.SC_SERVICE_UNAVAILABLE:
        throw new ServiceUnavailableException(statusLine.getReasonPhrase());
      default:
        return responseBody;
    }
  }

  public static class Builder {
    private PoolingHttpClientConnectionManager connectionManager = null;

    public Builder() {
    }

    public SpotifyHttpManager build() {
      return new SpotifyHttpManager(this);
    }
  }
}
