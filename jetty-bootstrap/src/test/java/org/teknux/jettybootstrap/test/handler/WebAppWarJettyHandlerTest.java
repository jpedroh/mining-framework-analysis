package org.teknux.jettybootstrap.test.handler;
import java.io.IOException;
import org.eclipse.jetty.server.Handler;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.handler.WarAppJettyHandler;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) public class WebAppWarJettyHandlerTest {
  @Test public void do01WebAppWarJettyHandlerTest() throws JettyBootstrapException, IOException {
    WarAppJettyHandler webAppWarJettyHandler = new WarAppJettyHandler();
    webAppWarJettyHandler.setContextPath("myContext");
    webAppWarJettyHandler.setWar("/tmp/myWarFile.war");
    Handler handler = webAppWarJettyHandler.getHandler();
    Assert.assertEquals("WebAppContext", handler.getClass().getSimpleName());
  }
}