package com.premiumminds.billy.spain.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.services.entities.ESCustomer;
import com.premiumminds.billy.spain.services.persistence.ESCustomerPersistenceService;

public class Customers {
  private Config configuration = new Config();

  private final Injector injector;

  private final ESCustomerPersistenceService persistenceService;

  public Customers(Injector injector) {
    this.injector = injector;
    this.persistenceService = this.getInstance(ESCustomerPersistenceService.class);
  }

  public ESCustomer.Builder builder() {
    return this.getInstance(ESCustomer.Builder.class);
  }

  public ESCustomer.Builder builder(ESCustomer customer) {
    ESCustomer.Builder builder = this.getInstance(ESCustomer.Builder.class);
    BuilderManager.setTypeInstance(builder, customer);
    return builder;
  }

  public ESCustomerPersistenceService persistence() {
    return this.persistenceService;
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}