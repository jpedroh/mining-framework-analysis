package org.teknux.jettybootstrap.handler;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.utils.Md5Util;

public class ExplodedWarAppJettyHandler extends AbstractAppJettyHandler {
  private static final String TYPE = "ExplodedWar";

  private static final String TYPE_FROM_CLASSPATH = "ExplodedWarFromClasspath";

  private String webAppBase = null;

  private String webAppBaseFromClasspath = null;

  private String descriptor = null;

  public String getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }

  public String getWebAppBase() {
    return webAppBase;
  }

  public void setWebAppBase(String webAppBase) {
    if (this.webAppBaseFromClasspath != null) {
      throw new InvalidParameterException("You can\'t set both webAppBase and webAppBaseFromClasspath parameters");
    }
    this.webAppBase = webAppBase;
  }

  public String getWebAppBaseFromClasspath() {
    return webAppBaseFromClasspath;
  }

  public void setWebAppBaseFromClasspath(String webAppBaseFromClasspath) {
    if (this.webAppBase != null) {
      throw new InvalidParameterException("You can\'t set both webAppBase and webAppBaseFromClasspath parameters");
    }
    this.webAppBaseFromClasspath = webAppBaseFromClasspath;
  }

  @Override public String getAppTempDirName() {
    try {
      if (webAppBase != null) {
        return Md5Util.hash(webAppBase);
      }
      if (webAppBaseFromClasspath != null) {
        return Md5Util.hash(webAppBaseFromClasspath);
      }
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Md5 Sum Error", e);
    }
    throw new InvalidParameterException("webAppBase or webAppBaseFromClasspath required");
  }

  @Override protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
    if (webAppBase != null) {
      webAppContext.setResourceBase(webAppBase);
    }
    if (webAppBaseFromClasspath != null) {
      webAppContext.setBaseResource(Resource.newClassPathResource(webAppBaseFromClasspath));
    }
    webAppContext.setDescriptor(descriptor);
    return webAppContext;
  }

  @Override public String getItemType() {
    if (webAppBase != null) {
      return TYPE;
    }
    if (webAppBaseFromClasspath != null) {
      return TYPE_FROM_CLASSPATH;
    }
    return null;
  }

  @Override public String getItemName() {
    if (webAppBase != null) {
      return webAppBase;
    }
    if (webAppBaseFromClasspath != null) {
      if (descriptor == null) {
        return webAppBaseFromClasspath;
      } else {
        return webAppBaseFromClasspath + "(" + descriptor + ")";
      }
    }
    return null;
  }
}