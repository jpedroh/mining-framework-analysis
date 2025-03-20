package org.cts.crs;

/**
 *
 * @author Erwan Bocher
 */
public class CRSException extends Exception {
  /**
     * Build a coordinate reference system exception with a message
     *
     * @param message precise description of this exception
     */
  public CRSException(String message) {
    super(message);
  }

  /**
     * Build a coordinate reference system exception based on an exception
     *
     * @param Exception
     */
  public CRSException(Exception ex) {
    super(ex);
  }

  /**
     * Build a coordinate reference system exception based on a message and
     * an exception
     * 
     * @param message
     * @param ex 
     */
  public CRSException(String message, Exception ex) {
    super(message, ex);
  }
}