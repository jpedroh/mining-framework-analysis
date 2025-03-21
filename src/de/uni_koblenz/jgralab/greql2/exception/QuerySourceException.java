package de.uni_koblenz.jgralab.greql2.exception;
import java.util.ArrayList;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Graph;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;
import de.uni_koblenz.jgralab.greql2.serialising.GreqlSerializer;

/**
 * This is the base class for all exceptions that refeer to the querysource with
 * offset/length pairs
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class QuerySourceException extends GreqlException {
  private static final long serialVersionUID = 8525494291742693931L;

  /**
	 * the position in the query where exception occured
	 */
  private List<SourcePosition> positions;

  /**
	 * the element that causes the error
	 */
  private Greql2Vertex element;

  /**
	 * 
	 * @param element
	 *            the element that caused the error
	 * @param sourcePositions
	 *            a list of sourceposition where the error possible occurs
	 */
  public QuerySourceException(String errorMessage, Greql2Vertex element, List<SourcePosition> sourcePositions, Throwable cause) {
    super(errorMessage, cause);
    this.element = element;
    if (sourcePositions != null) {
      positions = sourcePositions;
    } else {
      positions = new ArrayList<SourcePosition>();
    }
  }

  /**
	 * 
	 * @param element
	 *            the element that caused the error
	 * @param sourcePosition
	 *            the sourceposition where the error occurs
	 */
  public QuerySourceException(String errorMessage, Greql2Vertex element, SourcePosition sourcePosition, Exception cause) {
    super(errorMessage, cause);
    this.element = element;
    positions = new ArrayList<SourcePosition>();
    positions.add(sourcePosition);
  }

  /**
	 * 
	 * @param element
	 *            the element that caused the error
	 * @param sourcePositions
	 *            a list of sourceposition where the error possible occurs
	 */
  public QuerySourceException(String errorMessage, Greql2Vertex element, List<SourcePosition> sourcePositions) {
    this(errorMessage, element, sourcePositions, null);
  }

  /**
	 * 
	 * @param element
	 *            the element that caused the error
	 * @param sourcePosition
	 *            the sourceposition where the error occurs
	 */
  public QuerySourceException(String errorMessage, Greql2Vertex element, SourcePosition sourcePosition) {
    this(errorMessage, element, sourcePosition, null);
  }

  /**
	 * returns the string of the message
	 */
  @Override public String getMessage() {
    StringBuilder sb = new StringBuilder();
    if (positions.size() > 0) {
      sb.append(super.getMessage());
      sb.append(": query part \'");
      sb.append(element != null ? GreqlSerializer.serializeVertex(element) : "<unknown element>");
      sb.append("\' at position (");
      sb.append(positions.get(0).get_offset());
      sb.append(", ");
      sb.append(positions.get(0).get_length());
      sb.append(")");
    } else {
      sb.append(super.getMessage());
      sb.append(": query part \'");
      sb.append(element != null ? GreqlSerializer.serializeVertex(element) : "<unknown element>");
      sb.append("\' at unknown position in query");
    }
    if (element != null) {
      sb.append("\nComplete (optimized) Query: ");
      sb.append(GreqlSerializer.serializeGraph((
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/exception/QuerySourceException.java/left.java
      Greql2Graph
=======
      Greql2
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/exception/QuerySourceException.java/right.java
      ) element.getGraph()));
    }
    return sb.toString();
  }

  /**
	 * returns the list of sourcepositions
	 */
  public List<SourcePosition> getSourcePositions() {
    return positions;
  }

  /**
	 * @return the position where the undefined varialbe is used
	 */
  public int getOffset() {
    if (positions.size() < 0) {
      return 0;
    }
    return positions.get(0).get_offset();
  }

  /**
	 * @return the length of the usage of the undefined variable
	 */
  public int getLength() {
    if (positions.size() < 0) {
      return 0;
    }
    return positions.get(0).get_length();
  }

  /**
	 * @return the broken element
	 */
  public Greql2Vertex getElement() {
    return element;
  }
}