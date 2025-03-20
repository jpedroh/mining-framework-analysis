package com.premiumminds.billy.core.jpa;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.premiumminds.billy.core.CoreDependencyModule;

public class MockDependencyModule extends AbstractModule {
  @Override protected void configure() {
    JpaPersistModule persistModule = new JpaPersistModule("BillyPersistenceUnit");
    this.install(persistModule);
    this.install(new CoreDependencyModule());
  }

  public static class Initializer {
    @Inject public Initializer(PersistService persistService) {
      persistService.start();
    }
  }
}