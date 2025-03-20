package com.premiumminds.billy.core.exceptions;
import java.util.ResourceBundle;

public class BillyRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public BillyRuntimeException() {
  }

  public BillyRuntimeException(ResourceBundle b, String messageId) {
    super(b.getString(messageId));
  }

  public BillyRuntimeException(Throwable t) {
    super(t);
  }

  public BillyRuntimeException(ResourceBundle b, String messageId, Throwable t) {
    super(b.getString(messageId), t);
  }

  public BillyRuntimeException(String message) {
    super(message);
  }
}