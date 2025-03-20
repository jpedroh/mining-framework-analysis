package com.premiumminds.billy.core.services.exceptions;

public class ParameterNotFoundException extends DocumentIssuingException {
  private static final long serialVersionUID = 1L;

  public ParameterNotFoundException() {
  }

  public ParameterNotFoundException(String message) {
    super(message);
  }

  public ParameterNotFoundException(Throwable t) {
    super(t);
  }

  public ParameterNotFoundException(String message, Throwable t) {
    super(message, t);
  }
}