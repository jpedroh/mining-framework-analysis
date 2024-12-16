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
package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.exception.UndefinedVariableException;
import de.uni_koblenz.jgralab.greql2.exception.UnknownTypeException;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOfStoreClause;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import java.util.ArrayList;


/**
 * Evaluates a Greql2Expression vertex in the GReQL-2 Syntaxgraph. A
 * GReQL2-Expression is the rootvertex of the GReQL-2Syntaxgraph. It contains
 * the bound/free variables, that are defined via "using" and binds them to the
 * values in the variableMap of the Greql2Evaluator.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
	// @Override
	// public VertexCosts calculateSubtreeEvaluationCosts() {
	// return greqlEvaluator.getCostModel().calculateCostsGreql2Expression(
	// this);
	// }
public class Greql2ExpressionEvaluator extends VertexEvaluator<Greql2Expression> {
	private void initializeBoundVariables(InternalGreqlEvaluator evaluator) {
		IsBoundVarOf inc = vertex.getFirstIsBoundVarOfIncidence(EdgeDirection.IN);
		while (inc != null) {
			Variable currentBoundVariable = inc.getAlpha();
			Object variableValue = evaluator.getVariable(currentBoundVariable.get_name());
			if (variableValue == null) {
				throw new UndefinedVariableException(currentBoundVariable, createSourcePositions(inc));
			}
			VariableEvaluator<Variable> variableEval = ((VariableEvaluator<Variable>) (query.getVertexEvaluator(currentBoundVariable)));
			variableEval.setValue(variableValue, evaluator);
			inc = inc.getNextIsBoundVarOfIncidence(EdgeDirection.IN);
		} 
	}

	/**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
	public Greql2ExpressionEvaluator(Greql2Expression vertex, QueryImpl query) {
		super(vertex, query);
	}

	/**
	 * sets the values of all bound variables and evaluates the queryexpression
	 */
	@Override
	public Object evaluate(InternalGreqlEvaluator evaluator) {
		initializeBoundVariables(evaluator);
		Schema graphSchema = evaluator.getSchemaOfDataGraph();
		if ((vertex.get_importedTypes() != null) && (graphSchema != null)) {
			for (String importedType : vertex.get_importedTypes()) {
				if (importedType.endsWith(".*")) {
					String packageName = importedType.substring(0, importedType.length() - 2);
					Package p = graphSchema.getPackage(packageName);
					if (p == null) {
						throw new UnknownTypeException(packageName, new ArrayList<SourcePosition>());
					}
					// for (Domain elem : p.getDomains().values()) {
					// greqlEvaluator.addKnownType(elem);
					// }
					for (VertexClass elem : p.getVertexClasses()) {
						query.addKnownType(elem);
					}
					for (EdgeClass elem : p.getEdgeClasses()) {
						query.addKnownType(elem);
					}
				} else {
					GraphElementClass<?, ?> elemClass = graphSchema.getGraphClass().getGraphElementClass(importedType);
					if (elemClass == null) {
						throw new UnknownTypeException(importedType, new ArrayList<SourcePosition>());
					}
					query.addKnownType(elemClass);
				}
			}
		}
		Expression boundExpression = vertex.getFirstIsQueryExprOfIncidence(EdgeDirection.IN).getAlpha();
		VertexEvaluator<? extends Expression> eval = query.getVertexEvaluator(boundExpression);
		Object result = eval.getResult(evaluator);
		// if the query contains a "store as " - clause, there is a
		// "isIdOfInc"-Incidence connected with the Greql2Expression
		IsIdOfStoreClause storeInc = vertex.getFirstIsIdOfStoreClauseIncidence(EdgeDirection.IN);
		if (storeInc != null) {
			VertexEvaluator<Identifier> storeEval = query.getVertexEvaluator(storeInc.getAlpha());
			String varName = storeEval.getResult(evaluator).toString();
			// TODO [greqlrenovation] VariableDeclaration has an own
			// toString(InternalGreqlEvaluator)-method. check the use
			evaluator.setVariable(varName, result);
		}
		return result;
	}
}