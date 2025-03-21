package com.testingbot.tunnel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;

import com.testingbot.tunnel.proxy.ForwarderServlet;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
/**
 *
 * @author TestingBot
 */
public class HttpForwarder {
    private App app;
    private final Server httpProxy;
    
    public HttpForwarder(App app) {
        this.app = app;
<<<<<<< /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/left.java
        httpProxy = new Server();
        HttpConfiguration http_config = new HttpConfiguration();
        ServerConnector connector = new ServerConnector(httpProxy,
                new HttpConnectionFactory(http_config));
        connector.setPort(app.getSeleniumPort());
        connector.setIdleTimeout(400000);
||||||| /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/base.java
        Server httpProxy = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Integer.parseInt(app.getSeleniumPort()));
        connector.setMaxIdleTime(400000);
        connector.setThreadPool(new QueuedThreadPool(128));
=======
        httpProxy = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(Integer.parseInt(app.getSeleniumPort()));
        connector.setMaxIdleTime(400000);
        connector.setThreadPool(new QueuedThreadPool(128));
>>>>>>> /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/right.java
        
        httpProxy.setStopAtShutdown(true);
        
        httpProxy.addConnector(connector);
<<<<<<< /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/left.java
||||||| /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/base.java
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(new ForwarderServlet(app)), "/*");
=======
        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = new ServletHolder(new ForwarderServlet(app));
        servletHandler.addServletWithMapping(servletHolder, "/*");
>>>>>>> /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/right.java
        
<<<<<<< /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/left.java
        ServletHolder servletHolder = new ServletHolder(new ForwarderServlet(app));
        servletHolder.setInitParameter("idleTimeout", "300000");
        servletHolder.setInitParameter("timeout", "300000");
        if (app.getProxy() != null) {
            servletHolder.setInitParameter("proxy", app.getProxy());     
        }
        
        if (app.getProxyAuth()!= null) {
            servletHolder.setInitParameter("proxyAuth", app.getProxyAuth());     
        }
        
        ServletContextHandler ctxHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctxHandler.setContextPath("/");
        ctxHandler.addServlet(servletHolder, "/*");
        
        httpProxy.setHandler(ctxHandler);
        
||||||| /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/base.java
        httpProxy.setHandler(servletHandler);
=======
        servletHolder.setInitParameter("idleTimeout", "90000");
        servletHolder.setInitParameter("timeout", "90000");
     
        httpProxy.setHandler(servletHandler);
>>>>>>> /usr/src/app/output/testingbot/testingbot-tunnel/03f9869d5ccd78ac84c044fcb9fdeeed2b7dcf7e/src/main/java/com/testingbot/tunnel/HttpForwarder.java/right.java
        try {
            httpProxy.start();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.INFO, "Could not set up local forwarder. Please make sure this program can open port 4445 on this computer.");
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop() {
        try {
            httpProxy.stop();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean testForwarding() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet("http://127.0.0.1:" + app.getSeleniumPort());
        
        HttpResponse response;
        try {
            response = httpClient.execute(getRequest);
        } catch (IOException ex) {
            return false;
        }

        return (response.getStatusLine().getStatusCode() == 200);
    }
}

