package com.premiumminds.billy.portugal.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.impl.BuilderManager;
import com.premiumminds.billy.portugal.services.entities.PTAddress;

public class Addresses {
  private final Injector injector;

  public Addresses(Injector injector) {
    this.injector = injector;
  }

  public PTAddress.Builder builder() {
    return this.getInstance(PTAddress.Builder.class);
  }

  public PTAddress.Builder builder(PTAddress customer) {
    PTAddress.Builder builder = this.getInstance(PTAddress.Builder.class);
    BuilderManager.setTypeInstance(builder, customer);
    return builder;
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}