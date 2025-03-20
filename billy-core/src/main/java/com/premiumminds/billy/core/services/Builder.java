package com.premiumminds.billy.core.services;
import com.premiumminds.billy.core.exceptions.BillyValidationException;

public interface Builder<T extends java.lang.Object> {
  public T build() throws BillyValidationException;
}