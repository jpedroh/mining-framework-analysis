package de.uni_koblenz.jgralab.greql2.exception;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

/**
 * Should be thrown if an undefined function should be used, that is a function
 * that is not part of the function libary
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class UndefinedFunctionException extends QuerySourceException {
  static final long serialVersionUID = -1234563;

  public UndefinedFunctionException(FunctionApplication function, String functionName, List<SourcePosition> sourcePositions, Exception cause) {
    super("Undefined Function \'" + functionName + "\'", function, sourcePositions, cause);
  }

  public UndefinedFunctionException(FunctionApplication function, String functionName, List<SourcePosition> sourcePositions) {
    this(function, functionName, sourcePositions, null);
  }
}