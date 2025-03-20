package com.premiumminds.billy.spain.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.spain.services.entities.ESAddress;

public class Addresses {
  private final Injector injector;

  public Addresses(Injector injector) {
    this.injector = injector;
  }

  public ESAddress.Builder builder() {
    return this.getInstance(ESAddress.Builder.class);
  }

  public ESAddress.Builder builder(ESAddress customer) {
    ESAddress.Builder builder = this.getInstance(ESAddress.Builder.class);
    BuilderManager.setTypeInstance(builder, customer);
    return builder;
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}