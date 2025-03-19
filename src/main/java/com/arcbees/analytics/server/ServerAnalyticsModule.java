package com.arcbees.analytics.server;
import com.arcbees.analytics.server.options.ServerOptionsCallback;
import com.arcbees.analytics.shared.Analytics;
import com.arcbees.analytics.shared.GaAccount;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

public class ServerAnalyticsModule extends ServletModule {
  private final String userAccount;

  public ServerAnalyticsModule(String userAccount) {
    this.userAccount = userAccount;
  }

  @Override protected void configureServlets() {
    bindConstant().annotatedWith(GaAccount.class).to(userAccount);
    bind(ServerOptionsCallback.class).toProvider(ServerOptionsCallbackProvider.class).in(RequestScoped.class);
    bind(Analytics.class).to(ServerAnalytics.class).in(Singleton.class);
    filter("/*").through(ServerOptionsCallbackProvider.class);
  }
}