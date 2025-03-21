package com.testingbot.tunnel.proxy;

import com.testingbot.tunnel.App;
import com.testingbot.tunnel.Statistics;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.thread.QueuedThreadPool;


public class TunnelProxyServlet extends AsyncProxyServlet {
    
    class TunnelProxyResponseListener extends ProxyResponseListener
    {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        public long startTime = System.currentTimeMillis();
        
        protected TunnelProxyResponseListener(HttpServletRequest request, HttpServletResponse response)
        {
            super(request, response);
            this.request = request;
            this.response = response;
        }
        
        @Override
        public void onBegin(Response proxyResponse)
        {
            startTime = System.currentTimeMillis();
<<<<<<< /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/proxy/TunnelProxyServlet.java/left.java
            super.onBegin(proxyResponse);
||||||| /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/proxy/TunnelProxyServlet.java/base.java
        }
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    
    @Override
    protected void handleOnException(Throwable ex, HttpServletRequest request, HttpServletResponse response)
    {
        if (request.getRequestURL().toString().indexOf("squid-internal") == -1) {
            Logger.getLogger(TunnelProxyServlet.class.getName()).log(Level.WARNING, "{0} for request {1}\n{2}", new Object[]{ex.getMessage(), request.getMethod() + " - " + request.getRequestURL().toString(), ExceptionUtils.getStackTrace(ex)});
=======
        }
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    
    @Override
    protected void handleOnException(Throwable ex, HttpServletRequest request, HttpServletResponse response)
    {
        if (!request.getRequestURL().toString().contains("squid-internal")) {
            if (getServletConfig().getInitParameter("tb_debug") != null) {
                Logger.getLogger(TunnelProxyServlet.class.getName()).log(Level.WARNING, "{0} for request {1}\n{2}", new Object[]{ex.getMessage(), request.getMethod() + " - " + request.getRequestURL().toString(), ExceptionUtils.getStackTrace(ex)});
            } else {
                Logger.getLogger(TunnelProxyServlet.class.getName()).log(Level.WARNING, "{0} for request {1}", new Object[]{ex.getMessage(), request.getMethod() + " - " + request.getRequestURL().toString()});
            }
>>>>>>> /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/proxy/TunnelProxyServlet.java/right.java
        }
        
        @Override
        public void onComplete(Result result)
        {
            long endTime = System.currentTimeMillis();
            Statistics.addRequest();
            
            Logger.getLogger(App.class.getName()).log(Level.INFO, "<< [{0}] {1} ({2}) - {3}", new Object[]{request.getMethod(), request.getRequestURL().toString(), response.toString().substring(9, 12), (endTime-this.startTime) + " ms"});
            if (getServletConfig().getInitParameter("tb_debug") != null) {
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    StringBuilder sb = new StringBuilder();
                    String header;
 
                    while (headerNames.hasMoreElements()) {
                        header = headerNames.nextElement();
                        sb.append(header).append(": ").append(request.getHeader(header)).append(System.getProperty("line.separator"));
                    }
                    
                    Logger.getLogger(App.class.getName()).log(Level.INFO, sb.toString());
                }
            }
            super.onComplete(result);
        }
    }
    
    @Override
    protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length, Callback callback)
    {
        Statistics.addBytesTransfered(length);
        super.onResponseContent(request, response, proxyResponse, buffer, offset, length, callback);
    }
    
    @Override
    protected void onClientRequestFailure(HttpServletRequest clientRequest, Request proxyRequest, HttpServletResponse proxyResponse, Throwable failure)
    {
        if (!clientRequest.getRequestURL().toString().contains("squid-internal")) {
            Logger.getLogger(App.class.getName()).log(Level.WARNING, "{0} for request {1}\n{2}", new Object[]{failure.getMessage(), clientRequest.getMethod() + " - " + clientRequest.getRequestURL().toString(), ExceptionUtils.getStackTrace(failure)});
        }
        
        super.onClientRequestFailure(clientRequest, proxyRequest, proxyResponse, failure);
    }
    
    @Override
    protected Response.Listener newProxyResponseListener(HttpServletRequest request, HttpServletResponse response)
    {
        return new TunnelProxyResponseListener(request, response);
    }
    
    @Override
    protected void addProxyHeaders(HttpServletRequest clientRequest, Request proxyRequest)
    {
        super.addProxyHeaders(clientRequest, proxyRequest);
        if (getServletContext().getAttribute("extra_headers") != null) {
            HashMap<String, String> headers = (HashMap<String, String>) getServletContext().getAttribute("extra_headers");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                proxyRequest.header(entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    protected HttpClient createHttpClient() throws ServletException
    {
        ServletConfig config = getServletConfig();

        HttpClient client = newHttpClient();

        // Redirects must be proxied as is, not followed.
        client.setFollowRedirects(false);

        // Must not store cookies, otherwise cookies of different clients will mix.
        client.setCookieStore(new HttpCookieStore.Empty());

        Executor executor;
        String value = config.getInitParameter("maxThreads");
        if (value == null || "-".equals(value))
        {
            executor = (Executor)getServletContext().getAttribute("org.eclipse.jetty.server.Executor");
            if (executor==null)
                throw new IllegalStateException("No server executor for proxy");
        }
        else
        {
            QueuedThreadPool qtp= new QueuedThreadPool(Integer.parseInt(value));
            String servletName = config.getServletName();
            int dot = servletName.lastIndexOf('.');
            if (dot >= 0)
                servletName = servletName.substring(dot + 1);
            qtp.setName(servletName);
            executor=qtp;
        }

        client.setExecutor(executor);

        value = config.getInitParameter("maxConnections");
        if (value == null)
            value = "256";
        client.setMaxConnectionsPerDestination(Integer.parseInt(value));

        value = config.getInitParameter("idleTimeout");
        if (value == null)
            value = "30000";
        client.setIdleTimeout(Long.parseLong(value));

        value = config.getInitParameter("timeout");
        if (value == null)
            value = "60000";
        setTimeout(Long.parseLong(value));

        value = config.getInitParameter("requestBufferSize");
        if (value != null)
            client.setRequestBufferSize(Integer.parseInt(value));

        value = config.getInitParameter("responseBufferSize");
        if (value != null)
            client.setResponseBufferSize(Integer.parseInt(value));

        try
        {
            client.start();

            // Content must not be decoded, otherwise the client gets confused.
            client.getContentDecoderFactories().clear();

            return client;
        }
        catch (Exception x)
        {
            throw new ServletException(x);
        }
    }
    
    @Override
    protected HttpClient newHttpClient()
    {
        HttpClient client = new HttpClient();
        AuthenticationStore auth;
        
        final String proxy = getServletConfig().getInitParameter("proxy");
        if (proxy != null && !proxy.isEmpty())
        {
            String[] splitted = proxy.split(":");
            ProxyConfiguration proxyConfig = client.getProxyConfiguration();
            proxyConfig.getProxies().add(new HttpProxy(splitted[0], Integer.parseInt(splitted[1])));
            
            String proxyAuth = getServletConfig().getInitParameter("proxyAuth");
            if (proxyAuth != null && !proxyAuth.isEmpty())
            {
                String[] credentials = proxyAuth.split(":");
                
                auth = client.getAuthenticationStore();
                Logger.getLogger(App.class.getName()).log(Level.INFO, "Proxy auth {0} : {1}", new Object[]{credentials[0], credentials[1]});
                try {
                    auth.addAuthentication(new BasicAuthentication(new URI("http://" + proxy), Authentication.ANY_REALM, credentials[0], credentials[1]));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        final String basicAuthString = getServletConfig().getInitParameter("basicAuth");
        if (basicAuthString != null) {
            final String[] basicAuth = basicAuthString.split(",");
            if (basicAuth != null && basicAuth.length > 0) {
                for (String authCredentials : basicAuth) {
                    String[] credentials = authCredentials.split(":");
                    auth = client.getAuthenticationStore();
                    Logger.getLogger(App.class.getName()).log(Level.INFO, "Adding Basic Auth for {0} : {1} : {2}", new Object[]{credentials[0] + ":" + credentials[1], credentials[2], credentials[3]});
                    try {
                        auth.addAuthentication(new BasicAuthentication(new URI("http://" + credentials[0] + ":" + credentials[1]), Authentication.ANY_REALM, credentials[2], credentials[3]));
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return client;
    }
<<<<<<< /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/proxy/TunnelProxyServlet.java/left.java
||||||| /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/proxy/TunnelProxyServlet.java/base.java

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
       final int debug = _log.isDebugEnabled()?req.hashCode():0;

        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;

        if ("CONNECT".equalsIgnoreCase(request.getMethod()))
        {
            handleConnect(request,response);
        }
        else
        {
            final InputStream in = request.getInputStream();
            final OutputStream out = response.getOutputStream();

            final Continuation continuation = ContinuationSupport.getContinuation(request);

            if (!continuation.isInitial())
                response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT); // Need better test that isInitial
            else
            {

                String uri = request.getRequestURI();
                if (request.getQueryString() != null)
                    uri += "?" + request.getQueryString();
                if (request.getServerName().equalsIgnoreCase("localhost") && request.getServerPort() == 8087) {
                    throw new ServletException("Bad request on TunnelProxyServlet");
                }
                
                HttpURI url = proxyHttpURI(request,uri);

                if (debug != 0)
                    _log.debug(debug + " proxy " + uri + "-->" + url);

                if (url == null)
                {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                CustomHttpExchange exchange = new CustomHttpExchange()
                {
                    @Override
                    protected void onRequestCommitted() throws IOException
                    {
                    }

                    @Override
                    protected void onRequestComplete() throws IOException
                    {
                    }

                    @Override
                    protected void onResponseContent(Buffer content) throws IOException
                    {
                        if (debug != 0)
                            _log.debug(debug + " content" + content.length());
                        content.writeTo(out);
                    }

                    @Override
                    protected void onResponseHeaderComplete() throws IOException
                    {
                    }

                    @Override
                    protected void onResponseStatus(Buffer version, int status, Buffer reason) throws IOException
                    {
                        if (debug != 0)
                            _log.debug(debug + " " + version + " " + status + " " + reason);

                        if (reason != null && reason.length() > 0)
                            response.setStatus(status,reason.toString());
                        else
                            response.setStatus(status);
                    }

                    
                    @Override
                    protected void onResponseComplete() throws IOException
                    {
                        long endTime = System.currentTimeMillis();
                        Logger.getLogger(TunnelProxyServlet.class.getName()).log(Level.INFO, "<< [{0}] {1} ({2}) - {3}", new Object[]{request.getMethod(), request.getRequestURL().toString(), response.toString().substring(9, 12), (endTime-this.startTime) + " ms"});
                        continuation.complete();
                    }
        
                    @Override
                    protected void onResponseHeader(Buffer name, Buffer value) throws IOException
                    {
                        String nameString = name.toString();
                        String s = nameString.toLowerCase(Locale.ENGLISH);
                        if (!_DontProxyHeaders.contains(s) || (HttpHeaders.CONNECTION_BUFFER.equals(name) && HttpHeaderValues.CLOSE_BUFFER.equals(value)))
                        {
                            if (debug != 0)
                                _log.debug(debug + " " + name + ": " + value);

                            String filteredHeaderValue = filterResponseHeaderValue(nameString,value.toString(),request);
                            if (filteredHeaderValue != null && filteredHeaderValue.trim().length() > 0)
                            {
                                if (debug != 0)
                                    _log.debug(debug + " " + name + ": (filtered): " + filteredHeaderValue);
                                response.addHeader(nameString,filteredHeaderValue);
                            }
                        }
                        else if (debug != 0)
                            _log.debug(debug + " " + name + "! " + value);
                    }

                    @Override
                    protected void onConnectionFailed(Throwable ex)
                    {
                        handleOnConnectionFailed(ex,request,response);

                        // it is possible this might trigger before the
                        // continuation.suspend()
                        if (!continuation.isInitial())
                        {
                            continuation.complete();
                        }
                    }

                    @Override
                    protected void onException(Throwable ex)
                    {
                        if (ex instanceof EofException)
                        {
                            _log.ignore(ex);
                            //return;
                        }
                        handleOnException(ex,request,response);

                        // it is possible this might trigger before the
                        // continuation.suspend()
                        if (!continuation.isInitial())
                        {
                            continuation.complete();
                        }
                    }

                    @Override
                    protected void onExpire()
                    {
                        handleOnExpire(request,response);
                        continuation.complete();
                    }

                };

                exchange.setScheme(HttpSchemes.HTTPS.equals(request.getScheme())?HttpSchemes.HTTPS_BUFFER:HttpSchemes.HTTP_BUFFER);
                exchange.setMethod(request.getMethod());
                exchange.setURL(url.toString());
                exchange.setVersion(request.getProtocol());


                if (debug != 0)
                    _log.debug(debug + " " + request.getMethod() + " " + url + " " + request.getProtocol());

                // check connection header
                String connectionHdr = request.getHeader("Connection");
                if (connectionHdr != null)
                {
                    connectionHdr = connectionHdr.toLowerCase(Locale.ENGLISH);
                    if (connectionHdr.indexOf("keep-alive") < 0 && connectionHdr.indexOf("close") < 0)
                        connectionHdr = null;
                }

                // force host
                if (_hostHeader != null)
                    exchange.setRequestHeader("Host",_hostHeader);

                // copy headers
                boolean xForwardedFor = false;
                boolean hasContent = false;
                long contentLength = -1;
                Enumeration<?> enm = request.getHeaderNames();
                while (enm.hasMoreElements())
                {
                    // TODO could be better than this!
                    String hdr = (String)enm.nextElement();
                    String lhdr = hdr.toLowerCase(Locale.ENGLISH);

                    if (_DontProxyHeaders.contains(lhdr))
                        continue;
                    if (connectionHdr != null && connectionHdr.indexOf(lhdr) >= 0)
                        continue;
                    if (_hostHeader != null && "host".equals(lhdr))
                        continue;

                    if ("content-type".equals(lhdr))
                        hasContent = true;
                    else if ("content-length".equals(lhdr))
                    {
                        contentLength = request.getContentLength();
                        exchange.setRequestHeader(HttpHeaders.CONTENT_LENGTH,Long.toString(contentLength));
                        if (contentLength > 0)
                            hasContent = true;
                    }
                    else if ("x-forwarded-for".equals(lhdr))
                        xForwardedFor = true;

                    Enumeration<?> vals = request.getHeaders(hdr);
                    while (vals.hasMoreElements())
                    {
                        String val = (String)vals.nextElement();
                        if (val != null)
                        {
                            if (debug != 0)
                                _log.debug(debug + " " + hdr + ": " + val);

                            exchange.setRequestHeader(hdr,val);
                        }
                    }
                }

                // Proxy headers
                exchange.setRequestHeader("Via","1.1 (jetty)");
                if (!xForwardedFor)
                {
                    exchange.addRequestHeader("X-Forwarded-For",request.getRemoteAddr());
                    exchange.addRequestHeader("X-Forwarded-Proto",request.getScheme());
                    exchange.addRequestHeader("X-Forwarded-Host",request.getHeader("Host"));
                    exchange.addRequestHeader("X-Forwarded-Server",request.getLocalName());
                }

                if (hasContent)
                {
                    exchange.setRequestContentSource(in);
                }

                customizeExchange(exchange, request);

                /*
                 * we need to set the timeout on the continuation to take into
                 * account the timeout of the HttpClient and the HttpExchange
                 */
                long ctimeout = (_client.getTimeout() > exchange.getTimeout()) ? _client.getTimeout() : exchange.getTimeout();

                // continuation fudge factor of 1000, underlying components
                // should fail/expire first from exchange
                if ( ctimeout == 0 )
                {
                    continuation.setTimeout(0);  // ideally never times out
                }
                else
                {
                    continuation.setTimeout(ctimeout + 1000);
                }

                customizeContinuation(continuation);

                continuation.suspend(response);
                _client.send(exchange);

            }
        }        
    } 
=======

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
       final int debug = _log.isDebugEnabled()?req.hashCode():0;

        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;

        if ("CONNECT".equalsIgnoreCase(request.getMethod()))
        {
            handleConnect(request,response);
        }
        else
        {
            final InputStream in = request.getInputStream();
            final OutputStream out = response.getOutputStream();

            final Continuation continuation = ContinuationSupport.getContinuation(request);

            if (!continuation.isInitial())
                response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT); // Need better test that isInitial
            else
            {

                String uri = request.getRequestURI();
                if (request.getQueryString() != null)
                    uri += "?" + request.getQueryString();

                Integer proxy = Integer.valueOf(getServletConfig().getInitParameter("jetty"));

                if (request.getServerName().equalsIgnoreCase("localhost") && request.getServerPort() == 8087) {
                    throw new ServletException("Bad request on TunnelProxyServlet");
                }
                
                HttpURI url = proxyHttpURI(request,uri);

                if (debug != 0)
                    _log.debug(debug + " proxy " + uri + "-->" + url);

                if (url == null)
                {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                CustomHttpExchange exchange = new CustomHttpExchange()
                {
                    private int requestSize;
                    
                    @Override
                    protected void onRequestCommitted() throws IOException
                    {
                    }

                    @Override
                    protected void onRequestComplete() throws IOException
                    {
                    }

                    @Override
                    protected void onResponseContent(Buffer content) throws IOException
                    {
                        if (debug != 0)
                            _log.debug(debug + " content" + content.length());
                        content.writeTo(out);
                        
                        requestSize = content.length();
                    }

                    @Override
                    protected void onResponseHeaderComplete() throws IOException
                    {
                    }

                    @Override
                    protected void onResponseStatus(Buffer version, int status, Buffer reason) throws IOException
                    {
                        if (debug != 0)
                            _log.debug(debug + " " + version + " " + status + " " + reason);

                        if (reason != null && reason.length() > 0)
                            response.setStatus(status,reason.toString());
                        else
                            response.setStatus(status);
                    }

                    
                    @Override
                    protected void onResponseComplete() throws IOException
                    {
                        long endTime = System.currentTimeMillis();
                        Logger.getLogger(TunnelProxyServlet.class.getName()).log(Level.INFO, "<< [{0}] {1} ({2}) - {3} | {4}", new Object[]{request.getMethod(), request.getRequestURL().toString(), response.toString().substring(9, 12), (endTime-this.startTime) + " ms", requestSize + " bytes"});
                        if (getServletConfig().getInitParameter("tb_debug") != null) {
                            Enumeration<String> headerNames = request.getHeaderNames();
                            if (headerNames != null) {
                                StringBuilder sb = new StringBuilder();
                                String header;

                                while (headerNames.hasMoreElements()) {
                                    header = headerNames.nextElement();
                                    sb.append(header).append(": ").append(request.getHeader(header)).append(System.getProperty("line.separator"));
                                }
                                Logger.getLogger(ForwarderServlet.class.getName()).log(Level.INFO, sb.toString());
                            }
                        }
                        continuation.complete();
                    }
        
                    @Override
                    protected void onResponseHeader(Buffer name, Buffer value) throws IOException
                    {
                        String nameString = name.toString();
                        String s = nameString.toLowerCase(Locale.ENGLISH);
                        if (!_DontProxyHeaders.contains(s) || (HttpHeaders.CONNECTION_BUFFER.equals(name) && HttpHeaderValues.CLOSE_BUFFER.equals(value)))
                        {
                            if (debug != 0)
                                _log.debug(debug + " " + name + ": " + value);

                            String filteredHeaderValue = filterResponseHeaderValue(nameString,value.toString(),request);
                            if (filteredHeaderValue != null && filteredHeaderValue.trim().length() > 0)
                            {
                                if (debug != 0)
                                    _log.debug(debug + " " + name + ": (filtered): " + filteredHeaderValue);
                                response.addHeader(nameString,filteredHeaderValue);
                            }
                        }
                        else if (debug != 0)
                            _log.debug(debug + " " + name + "! " + value);
                    }

                    @Override
                    protected void onConnectionFailed(Throwable ex)
                    {
                        handleOnConnectionFailed(ex,request,response);

                        continuation.complete();
                    }

                    @Override
                    protected void onException(Throwable ex)
                    {
                        if (ex instanceof EofException)
                        {
                            _log.ignore(ex);
                            //return;
                        }
                        handleOnException(ex,request,response);
                        continuation.complete();
                    }

                    @Override
                    protected void onExpire()
                    {
                        handleOnExpire(request,response);
                        continuation.complete();
                    }

                };

                exchange.setScheme(HttpSchemes.HTTPS.equals(request.getScheme())?HttpSchemes.HTTPS_BUFFER:HttpSchemes.HTTP_BUFFER);
                exchange.setMethod(request.getMethod());
                exchange.setURL(url.toString());
                exchange.setVersion(request.getProtocol());


                if (debug != 0)
                    _log.debug(debug + " " + request.getMethod() + " " + url + " " + request.getProtocol());

                // check connection header
                String connectionHdr = request.getHeader("Connection");
                if (connectionHdr != null)
                {
                    connectionHdr = connectionHdr.toLowerCase(Locale.ENGLISH);
                    if (connectionHdr.indexOf("keep-alive") < 0 && connectionHdr.indexOf("close") < 0)
                        connectionHdr = null;
                }

                // force host
                if (_hostHeader != null)
                    exchange.setRequestHeader("Host",_hostHeader);

                // copy headers
                boolean xForwardedFor = false;
                boolean hasContent = false;
                long contentLength = -1;
                Enumeration<?> enm = request.getHeaderNames();
                while (enm.hasMoreElements())
                {
                    // TODO could be better than this!
                    String hdr = (String)enm.nextElement();
                    String lhdr = hdr.toLowerCase(Locale.ENGLISH);

                    if ("transfer-encoding".equals(lhdr))
                    {
                        if (request.getHeader("transfer-encoding").indexOf("chunk")>=0)
                            hasContent = true;
                    }
                    
                    if (_DontProxyHeaders.contains(lhdr))
                        continue;
                    if (connectionHdr != null && connectionHdr.indexOf(lhdr) >= 0)
                        continue;
                    if (_hostHeader != null && "host".equals(lhdr))
                        continue;

                    if ("content-type".equals(lhdr))
                        hasContent = true;
                    else if ("content-length".equals(lhdr))
                    {
                        contentLength = request.getContentLength();
                        exchange.setRequestHeader(HttpHeaders.CONTENT_LENGTH,Long.toString(contentLength));
                        if (contentLength > 0)
                            hasContent = true;
                    }
                    else if ("x-forwarded-for".equals(lhdr))
                        xForwardedFor = true;

                    Enumeration<?> vals = request.getHeaders(hdr);
                    while (vals.hasMoreElements())
                    {
                        String val = (String)vals.nextElement();
                        if (val != null)
                        {
                            if (debug != 0)
                                _log.debug(debug + " " + hdr + ": " + val);

                            exchange.setRequestHeader(hdr,val);
                        }
                    }
                }

                // Proxy headers
                exchange.setRequestHeader("Via","1.1 (jetty)");
                if (!xForwardedFor)
                {
                    exchange.addRequestHeader("X-Forwarded-For",request.getRemoteAddr());
                    exchange.addRequestHeader("X-Forwarded-Proto",request.getScheme());
                    exchange.addRequestHeader("X-Forwarded-Host",request.getHeader("Host"));
                    exchange.addRequestHeader("X-Forwarded-Server",request.getLocalName());
                }

                if (hasContent)
                {
                    exchange.setRequestContentSource(in);
                }

                customizeExchange(exchange, request);

                /*
                 * we need to set the timeout on the continuation to take into
                 * account the timeout of the HttpClient and the HttpExchange
                 */
                long ctimeout = (_client.getTimeout() > exchange.getTimeout()) ? _client.getTimeout() : exchange.getTimeout();

                // continuation fudge factor of 1000, underlying components
                // should fail/expire first from exchange
                if ( ctimeout == 0 )
                {
                    continuation.setTimeout(0);  // ideally never times out
                }
                else
                {
                    continuation.setTimeout(ctimeout + 1000);
                }

                customizeContinuation(continuation);

                continuation.suspend(response);
                _client.send(exchange);

            }
        }        
    } 
>>>>>>> /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/proxy/TunnelProxyServlet.java/right.java
}
