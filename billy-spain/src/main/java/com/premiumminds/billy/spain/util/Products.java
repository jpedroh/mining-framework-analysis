package com.premiumminds.billy.spain.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.spain.services.entities.ESProduct;
import com.premiumminds.billy.spain.services.persistence.ESProductPersistenceService;

public class Products {
  private final Injector injector;

  private final ESProductPersistenceService persistenceService;

  public Products(Injector injector) {
    this.injector = injector;
    this.persistenceService = this.getInstance(ESProductPersistenceService.class);
  }

  public ESProduct.Builder builder() {
    return this.getInstance(ESProduct.Builder.class);
  }

  public ESProduct.Builder builder(ESProduct customer) {
    ESProduct.Builder builder = this.getInstance(ESProduct.Builder.class);
    BuilderManager.setTypeInstance(builder, customer);
    return builder;
  }

  public ESProductPersistenceService persistence() {
    return this.persistenceService;
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}