package com.sun.syndication.io;
import org.jdom2.input.JDOMParseException;

/**
 * Exception thrown by WireFeedInput instance if it can not parse a feed.
 * <p>
 * 
 * @author Elaine Chien
 * 
 */
public class ParsingFeedException extends FeedException {
  private static final long serialVersionUID = -4791878470117677698L;

  /**
     * Creates a FeedException with a message.
     * <p>
     * 
     * @param msg exception message.
     * 
     */
  public ParsingFeedException(final String msg) {
    super(msg);
  }

  /**
     * Creates a FeedException with a message and a root cause exception.
     * <p>
     * 
     * @param msg exception message.
     * @param rootCause root cause exception.
     * 
     */
  public ParsingFeedException(final String msg, final Throwable rootCause) {
    super(msg, rootCause);
  }

  /**
     * Returns the line number of the end of the text where the parse error
     * occurred.
     * <p>
     * The first line in the document is line 1.
     * </p>
     * 
     * @return an integer representing the line number, or -1 if the information
     *         is not available.
     */
  public int getLineNumber() {
    if (getCause() instanceof JDOMParseException) {
      return ((JDOMParseException) getCause()).getLineNumber();
    } else {
      return -1;
    }
  }

  /**
     * Returns the column number of the end of the text where the parse error
     * occurred.
     * <p>
     * The first column in a line is position 1.
     * </p>
     * 
     * @return an integer representing the column number, or -1 if the
     *         information is not available.
     */
  public int getColumnNumber() {
    if (getCause() instanceof JDOMParseException) {
      return ((JDOMParseException) getCause()).getColumnNumber();
    } else {
      return -1;
    }
  }
}