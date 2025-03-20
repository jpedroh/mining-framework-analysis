package com.premiumminds.billy.portugal;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class PortugalPersistenceDependencyModule extends AbstractModule {
  private final String persistenceUnitId;

  public PortugalPersistenceDependencyModule(String persistenceUnitId) {
    this.persistenceUnitId = persistenceUnitId;
  }

  @Override protected void configure() {
    JpaPersistModule persistModule = new JpaPersistModule(this.persistenceUnitId);
    this.install(persistModule);
  }

  public static class Initializer {
    @Inject public Initializer(PersistService persistService) {
      persistService.start();
    }
  }
}