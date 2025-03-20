package com.premiumminds.billy.core;
import javax.inject.Inject;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.premiumminds.billy.core.services.documents.DocumentIssuingService;
import com.premiumminds.billy.core.services.documents.impl.DocumentIssuingServiceImpl;
import com.premiumminds.billy.core.util.NotImplemented;
import com.premiumminds.billy.core.util.NotImplementedInterceptor;
import com.premiumminds.billy.core.util.NotOnUpdate;
import com.premiumminds.billy.core.util.NotOnUpdateInterceptor;

public class CoreDependencyModule extends AbstractModule {
  @Override protected void configure() {
    this.bind(DocumentIssuingService.class).to(DocumentIssuingServiceImpl.class);
    this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(NotImplemented.class), new NotImplementedInterceptor());
    this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(NotOnUpdate.class), new NotOnUpdateInterceptor());
  }

  public static class Initializer {
    @Inject public Initializer() {
    }
  }
}