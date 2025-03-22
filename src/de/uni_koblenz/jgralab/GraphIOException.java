package de.uni_koblenz.jgralab;

/**
 * exceptions of this class are thrown if an error occurs while loading or
 * storing of schema and graph in tg file format
 * 
 * @author ist@uni-koblenz.de
 */
public class GraphIOException extends Exception {
  private static final long serialVersionUID = 4569564712278582929L;

  public GraphIOException() {
  }

  public GraphIOException(String msg) {
    super(msg);
  }

  public GraphIOException(Throwable t) {
    super(t);
  }

  public GraphIOException(String msg, Throwable t) {
    super(msg, t);
  }
}