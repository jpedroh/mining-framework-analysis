package com.premiumminds.billy.portugal.services.export.exceptions;

public class InvalidDocumentTypeException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidDocumentTypeException(String type) {
    super("Invalid document type: " + type);
  }
}