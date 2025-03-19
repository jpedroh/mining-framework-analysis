package com.urswolfer.gerrit.client.rest.http;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBaseHC4;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;

/**
 * Allows custom handling when executing a http request (e.g. custom exception handling).
 *
 * @author Urs Wolfer
 */
public class HttpRequestExecutor {
  public HttpResponse execute(HttpClientBuilder client, HttpRequestBaseHC4 method, HttpContext context) throws IOException {
    return client.build().execute(method, context);
  }
}