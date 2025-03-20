package com.sforce.ws.wsdl;

/**
 * This exception is thrown when there is an error in parsing WSDL.
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0   Nov 5, 2005
 */
public class WsdlParseException extends Exception {
  /**
	 * 
	 */
  private static final long serialVersionUID = -8191652328455635770L;

  public WsdlParseException(Throwable th) {
    super("Parse error: " + th.getMessage(), th);
  }

  public WsdlParseException(String message) {
    super(message);
  }

  public WsdlParseException(String message, Throwable th) {
    super(message, th);
  }
}