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

import java.util.ArrayList;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.AlternativePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsAlternativePathOf;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates an alternative path description. Creates a NFA that accepts the
 * alternative path description.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/left.java
 * 
 * @author ist@uni-koblenz.de
 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/base.java
 * 
 * @author Daniel Bildhauer <dbildh@uni-koblenz.de> Summer 2006, Diploma Thesis
 * 
=======
 *
 * @author ist@uni-koblenz.de
 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/right.java
 */
public class AlternativePathDescriptionEvaluator extends
		PathDescriptionEvaluator<AlternativePathDescription> {

	/**
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/base.java
	 * The AlternativePathDescription-Vertex this evaluator evaluates
	 */
	private AlternativePathDescription vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Vertex getVertex() {
		return vertex;
	}

	/**
=======
	 * The AlternativePathDescription-Vertex this evaluator evaluates
	 */
	private AlternativePathDescription vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	/**
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/right.java
	 * Creates a new IteratedPathDescriptionEvaluator for the given vertex
	 *
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public AlternativePathDescriptionEvaluator(
			AlternativePathDescription vertex, Query query) {
		super(vertex, query);
	}

	/**
	 * Calculate the costs to re-evaluate this vertex if the given variable
	 * changes. Assumes, that also the variables "right" of the given one
	 * change.
	 */

	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/left.java
	public NFA evaluate(InternalGreqlEvaluator evaluator) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/base.java
	public JValue evaluate() throws EvaluateException {
=======
	public NFA evaluate() {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/right.java
		IsAlternativePathOf inc = vertex
				.getFirstIsAlternativePathOfIncidence(EdgeDirection.IN);
		ArrayList<NFA> nfaList = new ArrayList<NFA>();
		while (inc != null) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/left.java
			PathDescriptionEvaluator<?> pathEval = (PathDescriptionEvaluator<?>) query
					.getVertexEvaluator(inc.getAlpha());
			nfaList.add(pathEval.getNFA(evaluator));
			inc = inc.getNextIsAlternativePathOfIncidence(EdgeDirection.IN);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/base.java
			PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(inc
					.getAlpha());
			nfaList.add(pathEval.getNFA());
			inc = inc.getNextIsAlternativePathOf(EdgeDirection.IN);
=======
			PathDescriptionEvaluator pathEval = (PathDescriptionEvaluator) vertexEvalMarker
					.getMark(inc.getAlpha());
			nfaList.add(pathEval.getNFA());
			inc = inc.getNextIsAlternativePathOfIncidence(EdgeDirection.IN);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/AlternativePathDescriptionEvaluator.java/right.java
		}
		return NFA.createAlternativePathDescriptionNFA(nfaList);
	}

	// @Override
	// public VertexCosts calculateSubtreeEvaluationCosts() {
	// return greqlEvaluator.getCostModel()
	// .calculateCostsAlternativePathDescription(this);
	// }

}
