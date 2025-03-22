package de.uni_koblenz.jgralab.greql2.evaluator;
import java.util.Iterator;
import org.pcollections.PSet;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VariableEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.greql2.types.Undefined;

/**
 * This class models the declaration of one variable. It allowes the iteration
 * over all possible values using the method iterate(). THe current value of the
 * variable is stored as temporary attribute at the variable vertex
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class VariableDeclaration {
  /**
	 * Holds the set of possible values the variable may have
	 */
  private PSet<Object> definitionSet;

  /**
	 * Holds the variable-vertex of this declaration.
	 */
  private VariableEvaluator variableEval;

  /**
	 * @return the variableEval
	 */
  VariableEvaluator getVariableEval() {
    return variableEval;
  }

  private VertexEvaluator definitionSetEvaluator;

  /**
	 * Used for simple Iteration over the possible values
	 */
  private Iterator<Object> iter = null;

  /**
	 * Creates a new VariableDeclaration for the given Variable and the given
	 * JValue
	 * 
	 * @param var
	 *            the Variable-Vertex in the GReQL-Syntaxgraph to create a
	 *            VariableDeclaration for
	 * @param definitionSetEvaluator
	 *            the evaluator for the set of possible values this variable may
	 *            have
	 * @param decl
	 *            the SimpleDeclaration which declares the variable
	 * @param eval
	 *            the GreqlEvaluator which is used to evaluate the query
	 */
  public VariableDeclaration(Variable var, VertexEvaluator definitionSetEvaluator, SimpleDeclaration decl, GreqlEvaluator eval) {
    variableEval = (VariableEvaluator) definitionSetEvaluator.getVertexEvalMarker().getMark(var);
    definitionSet = JGraLab.set();
    this.definitionSetEvaluator = definitionSetEvaluator;
  }

  /**
	 * The current iteration number. counts from 1 to definitionSet.size().
	 */
  private int iterationNumber = 0;

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(((Variable) variableEval.getVertex()).get_name());
    sb.append(" = ");
    sb.append(getVariableValue());
    sb.append(" [");
    sb.append(iterationNumber);
    sb.append('/');
    sb.append(definitionSet.size());
    sb.append("]");
    return sb.toString();
  }

  /**
	 * Iterates over all possible values for this variable. Returns true if
	 * another value was found, false otherwise
	 */
  public boolean iterate() {
    iterationNumber++;
    if ((iter != null) && (iter.hasNext())) {
      variableEval.setValue(iter.next());
      return true;
    }
    return false;
  }

  /**
	 * returns the current value of the represented variable. used only for
	 * debugging
	 */
  public Object getVariableValue() {
    return variableEval.getValue();
  }

  /**
	 * Resets the iterator to the first element
	 */
  protected void reset() {
    iterationNumber = 0;
    variableEval.setValue(Undefined.UNDEFINED);
    Object tempAttribute = definitionSetEvaluator.getResult();
    if (tempAttribute instanceof PVector) {
      PVector<?> col = (PVector<?>) tempAttribute;
      definitionSet = JGraLab.set().plusAll(col);
      if (col.size() > definitionSet.size()) {
        throw new GreqlException("A collection that doesn\'t fulfill the set property is used as variable range definition");
      }
    } else {
      if (tempAttribute instanceof PSet) {
        @SuppressWarnings(value = { "unchecked" }) PSet<Object> s = (PSet<Object>) tempAttribute;
        definitionSet = s;
      } else {
        definitionSet = JGraLab.set().plus(tempAttribute);
      }
    }
    iter = definitionSet.iterator();
  }

  /**
	 * Returns the cardinality of the collection this variable is bound to
	 * 
	 * @return the cardinality of the collection this variable is bound to
	 */
  public int getDefinitionCardinality() {
    return 40;
  }
}