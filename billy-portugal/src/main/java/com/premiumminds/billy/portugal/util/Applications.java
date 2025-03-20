package com.premiumminds.billy.portugal.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.portugal.services.entities.PTApplication;
import com.premiumminds.billy.portugal.services.persistence.PTApplicationPersistenceService;

public class Applications {
  private final Injector injector;

  private final PTApplicationPersistenceService persistenceService;

  public Applications(Injector injector) {
    this.injector = injector;
    this.persistenceService = this.getInstance(PTApplicationPersistenceService.class);
  }

  public PTApplication.Builder builder() {
    return this.getInstance(PTApplication.Builder.class);
  }

  public PTApplication.Builder builder(PTApplication application) {
    PTApplication.Builder builder = this.getInstance(PTApplication.Builder.class);
    BuilderManager.setTypeInstance(builder, application);
    return builder;
  }

  public PTApplicationPersistenceService persistence() {
    return this.persistenceService;
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}