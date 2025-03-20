package com.premiumminds.billy.spain.exceptions;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;

public class BillySimpleInvoiceException extends BillyRuntimeException {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  public BillySimpleInvoiceException(String message) {
    super(message);
  }
}