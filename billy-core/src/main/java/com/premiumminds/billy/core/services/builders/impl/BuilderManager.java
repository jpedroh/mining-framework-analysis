package com.premiumminds.billy.core.services.builders.impl;
import com.premiumminds.billy.core.services.Builder;

public class BuilderManager {
  public static <T extends java.lang.Object> void setTypeInstance(Builder<T> b, T entity) {
    @SuppressWarnings(value = { "unchecked" }) AbstractBuilder<?, T> builder = (AbstractBuilder<?, T>) b;
    builder.setTypeInstance(entity);
  }

  public static <T extends java.lang.Object> T getTypeInstance(Builder<T> b) {
    @SuppressWarnings(value = { "unchecked" }) AbstractBuilder<?, T> builder = (AbstractBuilder<?, T>) b;
    return builder.getTypeInstance();
  }
}