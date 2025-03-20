package com.premiumminds.billy.spain.services.export.exceptions;

public class RequiredFieldNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  public RequiredFieldNotFoundException(String field) {
    super("Required field " + field + " not found!");
  }
}