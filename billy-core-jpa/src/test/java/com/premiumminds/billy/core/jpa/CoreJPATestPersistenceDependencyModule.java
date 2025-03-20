package com.premiumminds.billy.core.jpa;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class CoreJPATestPersistenceDependencyModule extends AbstractModule {
  @Override protected void configure() {
    this.install(new JpaPersistModule("BillyCoreJPATestPersistenceUnit"));
  }

  public static class Initializer {
    @Inject public Initializer(PersistService persistService) {
      persistService.start();
    }
  }

  public static class Finalizer {
    @Inject public Finalizer(PersistService persistService) {
      persistService.stop();
    }
  }
}