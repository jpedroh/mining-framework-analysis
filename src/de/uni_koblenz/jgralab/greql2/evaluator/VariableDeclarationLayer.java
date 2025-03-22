package de.uni_koblenz.jgralab.greql2.evaluator;
import java.util.List;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.WrongResultTypeException;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;

/**
 * This class models all Variables of one Declaration-Vertex. It allows to
 * iterate over all possible combinations of this variables using the method
 * iterate(). The value of each variable is stored as temporary attribute at the
 * variable-vertex, so the evaluate()-methods don't need to know if the
 * expression is a variable or some other already evaluated expression.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class VariableDeclarationLayer {
  /**
	 * Holds a VariableDeclaration for each Variable which is declared in this
	 * Declaration
	 */
  private List<VariableDeclaration> variableDeclarations;

  /**
	 * this is the list of constraint vertices
	 */
  private List<VertexEvaluator> constraintList;

  /**
	 * true if the next variable iteration is the first one, that means, if
	 * there was no iteration before
	 */
  private boolean firstIteration = true;

  /**
	 * The declaration I belong to.
	 */
  private Declaration declaration = null;

  /**
	 * Creates a new {@link VariableDeclarationLayer} for iterating over all
	 * variable combinations that fulfill the constraints in constraintList.
	 * 
	 * @param vertex
	 * 
	 * @param constraintList
	 *            a list of constraints
	 */
  public VariableDeclarationLayer(Declaration vertex, List<VariableDeclaration> varDecls, List<VertexEvaluator> constraintList) {
    this.declaration = vertex;
    variableDeclarations = varDecls;
    this.constraintList = constraintList;
  }

  /**
	 * sets the next possible combination of values to the variable-vertices. If
	 * it is called the first time, it returns true if the first possible
	 * combination is valid
	 * 
	 * @return true if another possible combination was found, false otherwise
	 */
  public boolean iterate() {
    StringBuilder sb = null;
    if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
      sb = new StringBuilder();
      sb.append("### New Declaration Layer Iteration (");
      sb.append(declaration);
      sb.append(")\n");
    }
    boolean constraintsFullfilled = false;
    if (firstIteration) {
      if (!getFirstCombination()) {
        if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
          sb.append("## 1st. iteration: returning false (");
          sb.append(declaration);
          sb.append(")");
          System.out.println(sb.toString());
        }
        return false;
      }
      constraintsFullfilled = fullfillsConstraints();
      firstIteration = false;
    }
    while (!constraintsFullfilled) {
      if (!getNextCombination(false)) {
        if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
          sb.append("## nth iteration: returning false (");
          sb.append(declaration);
          sb.append(")");
          System.out.println(sb.toString());
        }
        return false;
      }
      constraintsFullfilled = fullfillsConstraints();
    }
    if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
      boolean first = true;
      for (VariableDeclaration dec : variableDeclarations) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(dec);
      }
      sb.append(" (");
      sb.append(declaration);
      sb.append(")");
      System.out.println(sb.toString());
    }
    return true;
  }

  /**
	 * Gets the first possible Variable Combination
	 * 
	 * @return true if a first combination exists, false otherwise
	 */
  private boolean getFirstCombination() {
    variableDeclarations.get(0).reset();
    return getNextCombination(true);
  }

  /**
	 * Gets the next possible variable combination
	 * 
	 * @return true if a next combination exists, false otherwise
	 */
  private boolean getNextCombination(boolean firstCombination) {
    int pointer = firstCombination ? 0 : variableDeclarations.size() - 1;
    boolean iterate;
    do {
      iterate = false;
      VariableDeclaration currDecl = null;
      do {
        if (pointer < 0) {
          return false;
        }
        currDecl = variableDeclarations.get(pointer--);
      } while(!currDecl.iterate());
      pointer += 2;
      int size = variableDeclarations.size();
      while (pointer < size) {
        currDecl = variableDeclarations.get(pointer++);
        currDecl.reset();
        if (!currDecl.iterate()) {
          pointer -= 2;
          iterate = true;
          break;
        }
      }
    } while(iterate);
    return true;
  }

  /**
	 * Checks if the current variable combination fulfills the constraints.
	 * 
	 * @return true if the combination fulfills the constraint, false otherwise
	 */
  private boolean fullfillsConstraints() {
    if ((constraintList == null) || (constraintList.isEmpty())) {
      return true;
    }
    for (int i = 0; i < constraintList.size(); i++) {
      VertexEvaluator currentEval = constraintList.get(i);
      Object tempResult = currentEval.getResult();
      if (tempResult instanceof Boolean) {
        if ((Boolean) tempResult != Boolean.TRUE) {
          return false;
        }
      } else {
        throw new WrongResultTypeException(currentEval.getVertex(), "Boolean", tempResult.getClass().getSimpleName(), currentEval.createPossibleSourcePositions());
      }
    }
    return true;
  }

  public void reset() {
    firstIteration = true;
  }
}