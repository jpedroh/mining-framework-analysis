package org.teknux.jettybootstrap.handler;
import java.security.NoSuchAlgorithmException;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.utils.Md5Util;

public class WarAppJettyHandler extends AbstractAppJettyHandler {
  private static final String TYPE = "War";

  private String war = null;

  public String getWar() {
    return war;
  }

  public void setWar(String war) {
    this.war = war;
  }

  @Override public String getAppTempDirName() {
    try {
      return Md5Util.hash(getWar());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Override protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
    webAppContext.setWar(war);
    return webAppContext;
  }

  @Override public String getItemType() {
    return TYPE;
  }

  @Override public String getItemName() {
    return war;
  }
}