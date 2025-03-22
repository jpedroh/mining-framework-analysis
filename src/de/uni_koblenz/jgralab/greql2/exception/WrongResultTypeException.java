package de.uni_koblenz.jgralab.greql2.exception;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

/**
 * Should be thrown if there is a Vertex in the GReQL Syntaxgraph for which no
 * VertexEvaluator exists
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class WrongResultTypeException extends QuerySourceException {
  static final long serialVersionUID = -1234565;

  public WrongResultTypeException(Greql2Vertex vertex, String expectedType, String realType, List<SourcePosition> sourcePositions, Exception cause) {
    super("Wrong result type: got " + realType + " but expected " + expectedType + "!", vertex, sourcePositions, cause);
  }

  public WrongResultTypeException(Greql2Vertex vertex, String expectedType, String realType, List<SourcePosition> sourcePositions) {
    super("Wrong result type: got " + realType + " but expected " + expectedType + "!", vertex, sourcePositions);
  }
}