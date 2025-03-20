package com.premiumminds.billy.portugal.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.portugal.services.entities.PTProduct;
import com.premiumminds.billy.portugal.services.persistence.PTProductPersistenceService;

public class Products {
  private final Injector injector;

  private final PTProductPersistenceService persistenceService;

  public Products(Injector injector) {
    this.injector = injector;
    this.persistenceService = this.getInstance(PTProductPersistenceService.class);
  }

  public PTProduct.Builder builder() {
    return this.getInstance(PTProduct.Builder.class);
  }

  public PTProduct.Builder builder(PTProduct customer) {
    PTProduct.Builder builder = this.getInstance(PTProduct.Builder.class);
    BuilderManager.setTypeInstance(builder, customer);
    return builder;
  }

  public PTProductPersistenceService persistence() {
    return this.persistenceService;
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}