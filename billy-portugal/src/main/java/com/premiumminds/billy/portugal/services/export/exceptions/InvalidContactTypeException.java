package com.premiumminds.billy.portugal.services.export.exceptions;

public class InvalidContactTypeException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidContactTypeException(String type) {
    super("Invalid contact type: " + type);
  }
}