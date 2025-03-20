package com.premiumminds.billy.core.exceptions;
import java.util.ResourceBundle;

public abstract class AbstractBillyException extends Exception {
  private static final long serialVersionUID = 1L;

  protected AbstractBillyException() {
  }

  protected AbstractBillyException(ResourceBundle b, String messageId) {
    super(b.getString(messageId));
  }

  protected AbstractBillyException(Throwable t) {
    super(t);
  }

  protected AbstractBillyException(ResourceBundle b, String messageId, Throwable t) {
    super(b.getString(messageId), t);
  }
}