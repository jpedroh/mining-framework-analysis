package com.premiumminds.billy.core.exceptions;
import com.premiumminds.billy.core.util.NotImplemented;

/**
 * @author Francisco Vargas
 *
 *         An exception to be thrown when a functionality is not implemented
 */
public class NotImplementedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
     * Default constructor
     */
  @NotImplemented public NotImplementedException() {
    super();
  }

  /**
     * A Constructor which takes a message
     *
     * @param string
     *        The exception message.
     */
  public NotImplementedException(String string) {
    super(string);
  }
}