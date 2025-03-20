package com.premiumminds.billy.portugal.services.documents.exceptions;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;

public class PTDocumentIssuingException extends DocumentIssuingException {
  private static final long serialVersionUID = 1L;

  public PTDocumentIssuingException() {
  }

  public PTDocumentIssuingException(String message) {
    super(message);
  }

  public PTDocumentIssuingException(Throwable t) {
    super(t);
  }

  public PTDocumentIssuingException(String message, Throwable t) {
    super(message, t);
  }
}