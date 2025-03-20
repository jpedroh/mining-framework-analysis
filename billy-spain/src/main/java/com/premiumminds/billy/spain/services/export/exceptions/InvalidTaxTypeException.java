package com.premiumminds.billy.spain.services.export.exceptions;

public class InvalidTaxTypeException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidTaxTypeException(String type) {
    super("Invalid tax type: " + type);
  }
}