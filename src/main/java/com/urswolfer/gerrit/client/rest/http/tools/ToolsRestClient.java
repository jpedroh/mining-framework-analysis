package com.urswolfer.gerrit.client.rest.http.tools;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.urswolfer.gerrit.client.rest.http.GerritRestClient;
import com.urswolfer.gerrit.client.rest.tools.Tools;
import org.apache.http.HttpResponse;
import org.apache.http.impl.io.DefaultHttpResponseWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Urs Wolfer
 */
public class ToolsRestClient implements Tools {
  private final GerritRestClient gerritRestClient;

  public ToolsRestClient(GerritRestClient gerritRestClient) {
    this.gerritRestClient = gerritRestClient;
  }

  @Override public InputStream getCommitMessageHook() throws RestApiException {
    try {
      HttpResponse response = gerritRestClient.request("/tools/hooks/commit-msg", null, GerritRestClient.HttpVerb.GET);
      return response.getEntity().getContent();
    } catch (IOException e) {
      throw new RestApiException("Failed to get commit message hook.", e);
    }
  }
}