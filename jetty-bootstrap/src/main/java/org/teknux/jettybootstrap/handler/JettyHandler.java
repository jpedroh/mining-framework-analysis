package org.teknux.jettybootstrap.handler;
import org.eclipse.jetty.server.Handler;

public class JettyHandler extends AbstractJettyHandler<Handler> {
  private static final String TYPE = "Handler";

  private Handler handler = null;

  @Override protected Handler createHandler() {
    return handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  @Override public String getItemType() {
    return TYPE;
  }

  @Override public String getItemName() {
    return handler.toString();
  }
}