package org.teknux.jettybootstrap.handler;
import org.eclipse.jetty.server.Handler;
import org.teknux.jettybootstrap.JettyBootstrapException;

public interface IJettyHandler<T extends Handler> {
  T getHandler() throws JettyBootstrapException;

  String getItemType();

  String getItemName();

  String toString();
}