package de.uni_koblenz.jgralab.greql2.exception;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

/**
 * This exception should be thrown if a query accesses a type that doesn't exist
 * in the datagraph schema
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class UnknownTypeException extends QuerySourceException {
  static final long serialVersionUID = -1234560;

  public UnknownTypeException(String typeName, List<SourcePosition> sourcePositions, Exception cause) {
    super("Schema doesn\'t contain a type \'" + typeName + "\'", null, sourcePositions, cause);
  }

  public UnknownTypeException(String typeName, List<SourcePosition> sourcePositions) {
    super("Schema doesn\'t contain a type \'" + typeName + "\'", null, sourcePositions);
  }
}