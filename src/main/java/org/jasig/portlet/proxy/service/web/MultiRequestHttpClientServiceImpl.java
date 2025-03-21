package org.jasig.portlet.proxy.service.web;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.portlet.util.PortletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MultiRequestHttpClientServiceImpl associates a single HTTP client with each
 * user's portlet session.  This client may optionally be shared among multiple
 * portlets to provide cross-portlet session state sharing. 
 * 
 * @author Jen Bourey
 */
@Service public class MultiRequestHttpClientServiceImpl implements IHttpClientService {
  private static final Logger LOG = LoggerFactory.getLogger(MultiRequestHttpClientServiceImpl.class);

  private static final String HTTP_CLIENT_CONNECTION_TIMEOUT = "httpClientConnectionTimeout";

  private static final String HTTP_CLIENT_SOCKET_TIMEOUT = "httpClientSocketTimeout";

  private static final int DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT = 10000;

  private static final int DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT = 10000;

  protected static final String CLIENT_SESSION_KEY = "httpClient";

  protected static final String SHARED_SESSION_KEY = "sharedSessionKey";

  private PoolingClientConnectionManager connectionManager;

  @Autowired(required = true) public void setPoolingClientConnectionManager(PoolingClientConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @Override public AbstractHttpClient getHttpClient(PortletRequest request) {
    final PortletSession session = request.getPortletSession();
    final PortletPreferences preferences = request.getPreferences();
    final String sharedSessionKey = preferences.getValue(SHARED_SESSION_KEY, null);
    final int scope = sharedSessionKey != null ? PortletSession.APPLICATION_SCOPE : PortletSession.PORTLET_SCOPE;
    final String clientSessionKey = sharedSessionKey != null ? sharedSessionKey : CLIENT_SESSION_KEY;
    AbstractHttpClient client;
    synchronized (PortletUtils.getSessionMutex(session)) {
      client = (AbstractHttpClient) session.getAttribute(clientSessionKey, scope);
      if (client == null) {
        client = createHttpClient(request);
        session.setAttribute(clientSessionKey, client, scope);
      }
    }
    client = setHttpClientTimeouts(request, client);
    return client;
  }

  /**
     * Create a new HTTP Client for the provided portlet request.
     * 
     * @param request
     * @return
     */
  protected AbstractHttpClient createHttpClient(PortletRequest request) {
    final AbstractHttpClient client = new DefaultHttpClient(this.connectionManager);
    client.addResponseInterceptor(new RedirectTrackingResponseInterceptor());
    return client;
  }

  private AbstractHttpClient setHttpClientTimeouts(PortletRequest request, AbstractHttpClient client) {
    PortletPreferences prefs = request.getPreferences();
    HttpParams params = client.getParams();
    if (params == null) {
      params = new BasicHttpParams();
      client.setParams(params);
    }
    int httpClientConnectionTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_CONNECTION_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT)));
    int httpClientSocketTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_SOCKET_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT)));
    HttpConnectionParams.setConnectionTimeout(params, httpClientConnectionTimeout);
    HttpConnectionParams.setSoTimeout(params, httpClientSocketTimeout);
    return client;
  }
}