package com.premiumminds.billy.spain.test;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class SpainTestPersistenceDependencyModule extends AbstractModule {
  @Override protected void configure() {
    JpaPersistModule persistModule = new JpaPersistModule("BillySpainTestPersistenceUnit");
    this.install(persistModule);
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