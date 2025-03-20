package com.premiumminds.billy.spain.services.export.exceptions;

public class InvalidTaxCodeException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidTaxCodeException(String code) {
    super("Invalid tax code: " + code);
  }
}