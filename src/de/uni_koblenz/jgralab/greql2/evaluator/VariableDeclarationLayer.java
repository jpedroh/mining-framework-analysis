/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         https://github.com/jgralab/jgralab
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.greql2.evaluator;

import java.util.List;

import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;

import de.uni_koblenz.jgralab.greql2.exception.WrongResultTypeException;

import de.uni_koblenz.jgralab.greql2.schema.Declaration;

import de.uni_koblenz.jgralab.greql2.schema.Expression;

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

/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         https://github.com/jgralab/jgralab
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

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
	private final List<VariableDeclaration> variableDeclarations;

	/**
	 * this is the list of constraint vertices
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
	private final List<VertexEvaluator<? extends Expression>> constraintList;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
=======
	private List<VertexEvaluator> constraintList;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java

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

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
	public VariableDeclarationLayer(Declaration vertex,
			List<VariableDeclaration> varDecls,
			List<VertexEvaluator<? extends Expression>> constraintList) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
	public VariableDeclarationLayer(ArrayList<VertexEvaluator> constraintList,
			EvaluationLogger logger) {
=======
	public VariableDeclarationLayer(Declaration vertex,
			List<VariableDeclaration> varDecls,
			List<VertexEvaluator> constraintList) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
		declaration = vertex;
		variableDeclarations = varDecls;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
		variableDeclarations = new ArrayList<VariableDeclaration>();
=======
		this.declaration = vertex;
		variableDeclarations = varDecls;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		this.constraintList = constraintList;
	}

	/**
	 * sets the next possible combination of values to the variable-vertices. If
	 * it is called the first time, it returns true if the first possible
	 * combination is valid
	 * 
	 * @return true if another possible combination was found, false otherwise
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
	public boolean iterate(InternalGreqlEvaluator evaluator) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
	public boolean iterate {
=======
	public boolean iterate() {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		StringBuilder sb = null;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
		if (GreqlEvaluatorImpl.DEBUG_DECLARATION_ITERATIONS) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
		if  {
=======
		if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
			sb = new StringBuilder();
			sb.append("### New Declaration Layer Iteration (");
			sb.append(declaration);
			sb.append(")\n");
		}
		boolean constraintsFullfilled = false;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
		if (firstIteration) {
			if (!getFirstCombination(evaluator)) {
				if (GreqlEvaluatorImpl.DEBUG_DECLARATION_ITERATIONS) {
					sb.append("## 1st. iteration: returning false (");
					sb.append(declaration);
					sb.append(")");
					System.out.println(sb.toString());
				}
				return false; // no more combinations exists
			}
			constraintsFullfilled = fullfillsConstraints(evaluator);
			firstIteration = false;
		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
		if (firstIteration) 
=======
		if (firstIteration) {
			if (!getFirstCombination()) {
				if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
					sb.append("## 1st. iteration: returning false (");
					sb.append(declaration);
					sb.append(")");
					System.out.println(sb.toString());
				}
				return false; // no more combinations exists
			}
			constraintsFullfilled = fullfillsConstraints();
			firstIteration = false;
		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		while (!constraintsFullfilled) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
			if (!getNextCombination(false, evaluator)) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
			if  {
=======
			if (!getNextCombination(false)) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
				if (GreqlEvaluatorImpl.DEBUG_DECLARATION_ITERATIONS) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
				if  {
=======
				if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
					sb.append("## nth iteration: returning false (");
					sb.append(declaration);
					sb.append(")");
					System.out.println(sb.toString());
				}
				return false; // no more combinations exists
			}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
			constraintsFullfilled = fullfillsConstraints(evaluator);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
=======
			constraintsFullfilled = fullfillsConstraints();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		}

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
		if (GreqlEvaluatorImpl.DEBUG_DECLARATION_ITERATIONS) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
		if  {
=======
		if (GreqlEvaluator.DEBUG_DECLARATION_ITERATIONS) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
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

	private boolean getFirstCombination(InternalGreqlEvaluator evaluator) {
		variableDeclarations.get(0).reset(evaluator);
		return getNextCombination(true, evaluator);
	}

	/**
	 * Gets the next possible variable combination
	 * 
	 * @return true if a next combination exists, false otherwise
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
	private boolean getNextCombination(boolean firstCombination,
			InternalGreqlEvaluator evaluator) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
	private boolean getNextCombination {
=======
	private boolean getNextCombination(boolean firstCombination) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java

		int pointer = firstCombination ? 0 : variableDeclarations.size() - 1;

		boolean iterate;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
		do {
			iterate = false;
			VariableDeclaration currDecl = null; // pointer = 0
			do {
				if (pointer < 0) {
					return false;
				}
				currDecl = variableDeclarations.get(pointer--); // pointer = -1
			} while (!currDecl.iterate(evaluator));
			pointer += 2; // pointer = 3
			int size = variableDeclarations.size();
			while (pointer < size) {
				currDecl = variableDeclarations.get(pointer++);
				currDecl.reset(evaluator);
				if (!currDecl.iterate(null)) {
					pointer -= 2;
					iterate = true;
					break;
				}
			}
		} while (iterate);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
		do  while (iterate);
=======
		do {
			iterate = false;
			VariableDeclaration currDecl = null; // pointer = 0
			do {
				if (pointer < 0) {
					return false;
				}
				currDecl = variableDeclarations.get(pointer--); // pointer = -1
			} while (!currDecl.iterate());
			pointer += 2; // pointer = 3
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
		} while (iterate);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		return true;
	}

	/**
	 * Checks if the current variable combination fulfills the constraints.
	 * 
	 * @return true if the combination fulfills the constraint, false otherwise
	 */

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
	private boolean fullfillsConstraints(InternalGreqlEvaluator evaluator) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
	private boolean fullfillsConstraints {
=======
	private boolean fullfillsConstraints() {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		if ((constraintList == null) || (constraintList.isEmpty())) {
			return true;
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/left.java
		for (int i = 0; i < constraintList.size(); i++) {
			VertexEvaluator<? extends Expression> currentEval = constraintList
					.get(i);
			Object tempResult = currentEval.getResult(evaluator);

			if (tempResult instanceof Boolean) {
				if ((Boolean) tempResult != Boolean.TRUE) {
					return false;
				}
			} else {
				throw new WrongResultTypeException(currentEval.getVertex(),
						"Boolean", tempResult.getClass().getSimpleName(),
						currentEval.createPossibleSourcePositions());
			}

		}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/base.java
		for (int i = 0; i < constraintList.size(); i++) 
=======
		for (int i = 0; i < constraintList.size(); i++) {
			VertexEvaluator currentEval = constraintList.get(i);
			Object tempResult = currentEval.getResult();

			if (tempResult instanceof Boolean) {
				if ((Boolean) tempResult != Boolean.TRUE) {
					return false;
				}
			} else {
				throw new WrongResultTypeException(currentEval.getVertex(),
						"Boolean", tempResult.getClass().getSimpleName(),
						currentEval.createPossibleSourcePositions());
			}

		}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/VariableDeclarationLayer.java/right.java
		return true;
	}

	public void reset() {
		firstIteration = true;
	}

	/**
	 * The declaration I belong to.
	 */

	/**
	 * Creates a new {@link VariableDeclarationLayer} for iterating over all
	 * variable combinations that fulfill the constraints in constraintList.
	 * 
	 * @param vertex
	 * 
	 * @param constraintList
	 *            a list of constraints
	 */

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

	/**
	 * Checks if the current variable combination fulfills the constraints.
	 * 
	 * @return true if the combination fulfills the constraint, false otherwise
	 */

}
