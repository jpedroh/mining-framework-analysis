package com.premiumminds.billy.portugal.services.export.exceptions;

public class SAFTPTExportException extends Exception {
  private static final long serialVersionUID = 1L;

  public SAFTPTExportException() {
    super();
  }

  public SAFTPTExportException(String message) {
    super(message);
  }

  public SAFTPTExportException(Throwable t) {
    super(t);
  }

  public SAFTPTExportException(String message, Throwable t) {
    super(message, t);
  }
}