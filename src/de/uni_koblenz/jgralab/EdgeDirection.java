package de.uni_koblenz.jgralab;

/**
 * Specifies direction of edges for traversal methods. IN: Only incoming edges
 * are traversed, OUT: only outgoing edges are traversed, INOUT: both incoming
 * and outgoing edges are traversed.
 * 
 * @author ist@uni-koblenz.de
 */public enum EdgeDirection {
  IN,
  OUT,
  INOUT
  ;

  /**
	 * Parses a given string for edge direction.
	 * 
	 * @param direction
	 *            A string containing only IN, OUT, or INOUT.
	 * @return Matching edge direction.
	 * @throws Exception
	 *             When no edge direction could be matched from string.
	 */
  public static EdgeDirection parse(String direction) throws Exception {
    if (direction.equals("OUT")) {
      return EdgeDirection.OUT;
    } else {
      if (direction.equals("IN")) {
        return EdgeDirection.IN;
      } else {
        if (direction.equals("INOUT")) {
          return EdgeDirection.INOUT;
        } else {
          throw new Exception("Could not determine direction from string.");
        }
      }
    }
  }
}