package com.premiumminds.billy.core.services.entities.util;

public interface EntityFactory<T extends java.lang.Object> {
  public T getEntityInstance();
}