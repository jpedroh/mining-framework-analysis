package com.premiumminds.billy.portugal.services.documents.exceptions;

public class InvalidSourceBillingException extends PTDocumentIssuingException {
  private static final long serialVersionUID = 1L;

  public InvalidSourceBillingException(String series, String sourceBilling, String expectedSourceBilling) {
    super("Expected source billing " + expectedSourceBilling + " in series " + series + " but received " + sourceBilling);
  }
}