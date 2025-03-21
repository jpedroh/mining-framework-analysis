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
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.schema.ExponentiatedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates an exponentiated path description. Creates a NFA that accepts the
 * exponentiated path description.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ExponentiatedPathDescriptionEvaluator extends
		PathDescriptionEvaluator<ExponentiatedPathDescription> {

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/base.java
	 * The ExponentiatedPathDescription-Vertex this evaluator evaluates
	 */
	private ExponentiatedPathDescription vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Vertex getVertex() {
		return vertex;
	}

	/**
=======
	 * The ExponentiatedPathDescription-Vertex this evaluator evaluates
	 */
	private ExponentiatedPathDescription vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
	 * Creates a new ExponentiatedPathDescriptionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public ExponentiatedPathDescriptionEvaluator(
			ExponentiatedPathDescription vertex, Query query) {
		super(vertex, query);
	}

	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
	public NFA evaluate(InternalGreqlEvaluator evaluator) {
		PathDescription p = vertex.getFirstIsExponentiatedPathOfIncidence()
				.getAlpha();
		PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query
				.getVertexEvaluator(p);
		VertexEvaluator<? extends Expression> exponentEvaluator = query
				.getVertexEvaluator(vertex.getFirstIsExponentOfIncidence(
						EdgeDirection.IN).getAlpha());
		Object exponentValue = exponentEvaluator.getResult(evaluator);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/base.java
	public JValue evaluate() throws EvaluateException {
		PathDescription p = (PathDescription) vertex
				.getFirstIsExponentiatedPathOf().getAlpha();
		PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(p);
		VertexEvaluator exponentEvaluator = greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(vertex
				.getFirstIsExponentOf(EdgeDirection.IN).getAlpha());
		JValue exponentValue = exponentEvaluator.getResult(subgraph);
=======
	public NFA evaluate() {
		PathDescription p = (PathDescription) vertex
				.getFirstIsExponentiatedPathOfIncidence().getAlpha();
		PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker
				.getMark(p);
		VertexEvaluator exponentEvaluator = vertexEvalMarker.getMark(vertex
				.getFirstIsExponentOfIncidence(EdgeDirection.IN).getAlpha());
		Object exponentValue = exponentEvaluator.getResult();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
		int exponent = 0;
		if (exponentValue instanceof Integer) {
			exponent = (Integer) exponentValue;
		} else {
			throw new GreqlException(
					"Exponent of ExponentiatedPathDescription is not convertable to integer value");
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/left.java
		return NFA.createExponentiatedPathDescriptionNFA(
				pathEval.getNFA(evaluator), exponent);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/base.java
		return new JValue(NFA.createExponentiatedPathDescriptionNFA(pathEval
				.getNFA(), exponent));
=======
		return NFA.createExponentiatedPathDescriptionNFA(pathEval.getNFA(),
				exponent);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ExponentiatedPathDescriptionEvaluator.java/right.java
	}

	// @Override
	// public VertexCosts calculateSubtreeEvaluationCosts() {
	// return greqlEvaluator.getCostModel()
	// .calculateCostsExponentiatedPathDescription(this);
	// }

}
