package com.datastax.driver.core.exceptions;
import com.datastax.driver.core.ProtocolVersion;

/**
 * Exception thrown when a feature is not supported by the native protocol
 * currently in use.
 */
public class UnsupportedFeatureException extends DriverException {
  private static final long serialVersionUID = 0;

  public UnsupportedFeatureException(ProtocolVersion currentVersion, String msg) {
    super("Unsupported feature with the native protocol " + currentVersion + " (which is currently in use): " + msg);
  }
}