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
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.greql2.schema.RecordId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates a record element, this is for instance name:"element" in the
 * record-construction rec( name:"element")
 * 
 * @author ist@uni-koblenz.de November 2006
 * 
 */
public class RecordElementEvaluator extends VertexEvaluator<RecordElement> {

	private String id = null;

	private VertexEvaluator<? extends Expression> expEval = null;

	public String getId() {
		if (id == null) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
			RecordId idVertex = vertex.getFirstIsRecordIdOfIncidence(
					EdgeDirection.IN).getAlpha();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/base.java
			RecordId ;
=======
			RecordId idVertex = (RecordId) vertex
					.getFirstIsRecordIdOfIncidence(EdgeDirection.IN).getAlpha();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java
			id = idVertex.get_name();
		}
		return id;
	}

	/**
	 * Creates a new RecordConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */

	public RecordElementEvaluator(RecordElement vertex, Query query) {
		super(vertex, query);
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/left.java
	@Override
	public Object evaluate(InternalGreqlEvaluator evaluator) {
		if (expEval == null) {
			Expression recordElementExp = vertex
					.getFirstIsRecordExprOfIncidence(EdgeDirection.IN)
					.getAlpha();
			expEval = query.getVertexEvaluator(recordElementExp);
		}
		return expEval.getResult(evaluator);
	}
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/base.java
=======
	@Override
	public Object evaluate() {
		if (expEval == null) {
			Expression recordElementExp = (Expression) vertex
					.getFirstIsRecordExprOfIncidence(EdgeDirection.IN)
					.getAlpha();
			expEval = vertexEvalMarker.getMark(recordElementExp);
		}
		return expEval.getResult();
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/RecordElementEvaluator.java/right.java

	// @Override

	// public VertexCosts calculateSubtreeEvaluationCosts() {

	// return greqlEvaluator.getCostModel().calculateCostsRecordElement(this);

	// }

	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}


}
