package de.uni_koblenz.jgralab.greql2.exception;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Should be thrown if a undefined Variable is used
 *
 * @author ist@uni-koblenz.de
 *
 */
public class UndefinedVariableException extends QuerySourceException {
  static final long serialVersionUID = -1234567;

  public UndefinedVariableException(Variable variable, List<SourcePosition> sourcePositions) {
    super("Undefined variable " + variable.get_name(), variable, sourcePositions);
  }
}