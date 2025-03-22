package de.uni_koblenz.jgralab;

/**
 * exceptions of this class are thrown if an error occurs while creating or
 * manipulating a graph
 * 
 * @author ist@uni-koblenz.de
 */
public class GraphException extends RuntimeException {
  private static final long serialVersionUID = -4207982437756832479L;

  public GraphException() {
  }

  public GraphException(String msg) {
    super(msg);
  }

  public GraphException(Throwable t) {
    super(t);
  }

  public GraphException(String msg, Throwable t) {
    super(msg, t);
  }
}