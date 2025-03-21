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

import org.pcollections.PSet;

import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.Vertex;

import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;

import de.uni_koblenz.jgralab.greql2.evaluator.Query;

import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;

import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;

import de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices;

import de.uni_koblenz.jgralab.greql2.schema.BackwardVertexSet;

import de.uni_koblenz.jgralab.greql2.schema.Expression;

import de.uni_koblenz.jgralab.greql2.schema.PathDescription;

/**
 * evaluates a BackwardVertexSet
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

import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;

import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;

import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;

import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * evaluates a BackwardVertexSet
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BackwardVertexSetEvaluator extends
		PathSearchEvaluator<BackwardVertexSet> {

	public BackwardVertexSetEvaluator(BackwardVertexSet vertex, Query query) {
		super(vertex, query);
	}

	private boolean initialized = false;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
	private VertexEvaluator<? extends Expression> targetEval = null;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/base.java
=======
	private VertexEvaluator targetEval = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java

	private final void initialize(InternalGreqlEvaluator evaluator) {
		PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(
				EdgeDirection.IN).getAlpha();
		PathDescriptionEvaluator<?> pathDescEval = (PathDescriptionEvaluator<?>) query
				.getVertexEvaluator(p);

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
		Expression targetExpression = vertex.getFirstIsTargetExprOfIncidence(
				EdgeDirection.IN).getAlpha();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/base.java
		Expression ;
=======
		Expression targetExpression = (Expression) vertex
				.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java
		targetEval = query.getVertexEvaluator(targetExpression);
		NFA revertedNFA = NFA.revertNFA(pathDescEval.getNFA(evaluator));
		searchAutomaton = new DFA(revertedNFA);

		initialized = true;
	}

	@Override
	public PSet<Vertex> evaluate(InternalGreqlEvaluator evaluator) {
		if (!initialized) {
			initialize(evaluator);
		}
		Vertex targetVertex = null;
		targetVertex = (Vertex) targetEval.getResult(evaluator);

		return ReachableVertices.search(targetVertex, searchAutomaton);
	}

	// @Override

	// public VertexCosts calculateSubtreeEvaluationCosts() {

	// return greqlEvaluator.getCostModel().calculateCostsBackwardVertexSet(

	// this);

	// }

	//

	// @Override

	// public long calculateEstimatedCardinality() {

	// return greqlEvaluator.getCostModel()

	// .calculateCardinalityBackwardVertexSet(this);

	// }

	private final void initialize() {
		PathDescription p = (PathDescription) vertex.getFirstIsPathOfIncidence(
				EdgeDirection.IN).getAlpha();
		PathDescriptionEvaluator pathDescEval = (PathDescriptionEvaluator) vertexEvalMarker
				.getMark(p);

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/left.java
		Expression targetExpression = vertex.getFirstIsTargetExprOfIncidence(
				EdgeDirection.IN).getAlpha();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/base.java
		Expression ;
=======
		Expression targetExpression = (Expression) vertex
				.getFirstIsTargetExprOfIncidence(EdgeDirection.IN).getAlpha();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/BackwardVertexSetEvaluator.java/right.java
		targetEval = vertexEvalMarker.getMark(targetExpression);
		NFA revertedNFA = NFA.revertNFA(pathDescEval.getNFA());
		searchAutomaton = new DFA(revertedNFA);

		initialized = true;
	}

	@Override
	public PSet<Vertex> evaluate() {
		if (!initialized) {
			initialize();
		}
		Vertex targetVertex = null;
		targetVertex = (Vertex) targetEval.getResult();

		return ReachableVertices.search(targetVertex, searchAutomaton);
	}

}
