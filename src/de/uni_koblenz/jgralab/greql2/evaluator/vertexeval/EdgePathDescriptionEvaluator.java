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

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.EdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsEdgeRestrOf;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression;

/**
 * Creates a NFA wich accepts a single edge out of the --edge-> - clause
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgePathDescriptionEvaluator extends
		PrimaryPathDescriptionEvaluator<EdgePathDescription> {

	public EdgePathDescriptionEvaluator(EdgePathDescription vertex, Query query) {
		super(vertex, query);
	}

	@Override
	public NFA evaluate(InternalGreqlEvaluator evaluator) {
		Edge evalEdge = vertex.getFirstIsEdgeExprOfIncidence();
		VertexEvaluator<? extends Expression> edgeEval = null;
		if (evalEdge != null) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
			edgeEval = query.getVertexEvaluator((Expression) evalEdge
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/base.java
			edgeEval = greqlEvaluator.getVertexEvaluatorGraphMarker().getVertexEvaluator((Expression) evalEdge
=======
			edgeEval = vertexEvalMarker.getVertexEvaluator((Expression) evalEdge
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
					.getAlpha());
		}
		TypeCollection typeCollection = new TypeCollection();
		// IsTypeRestrOfExpression inc = vertex
		// .getFirstIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
		IsEdgeRestrOf inc = vertex
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/base.java
		IsTypeRestrOf inc = vertex
=======
		IsTypeRestrOfExpression inc = vertex
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
				.getFirstIsEdgeRestrOfIncidence(EdgeDirection.IN);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/base.java
				.getFirstIsTypeRestrOf(EdgeDirection.IN);
=======
				.getFirstIsTypeRestrOfExpressionIncidence(EdgeDirection.IN);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
		EdgeRestrictionEvaluator edgeRestEval = null;
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
		VertexEvaluator<? extends Expression> predicateEvaluator = null;
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/base.java
=======
		VertexEvaluator predicateEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
		if (inc != null) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
		edgeRestEval = (EdgeRestrictionEvaluator) query.getVertexEvaluator(inc.getAlpha());
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/base.java
		edgeRestEval = (EdgeRestrictionEvaluator) greqlEvaluator.getVertexEvaluatorGraphMarker().getVertexEvaluator(inc.getAlpha());
=======
		edgeRestEval = (EdgeRestrictionEvaluator) vertexEvalMarker.getVertexEvaluator(inc.getAlpha());
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
		typeCollection.addTypes(edgeRestEval.getTypeCollection(evaluator));
		predicateEvaluator = edgeRestEval.getPredicateEvaluator();
	}
		createdNFA = NFA.createEdgePathDescriptionNFA(getEdgeDirection(vertex),
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/left.java
				typeCollection, getEdgeRoles(edgeRestEval), edgeEval,
				predicateEvaluator, query);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/base.java
				typeCollection, getEdgeRoles(edgeRestEval), edgeEval);
=======
				typeCollection, getEdgeRoles(edgeRestEval), edgeEval,
				predicateEvaluator, vertexEvalMarker);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/EdgePathDescriptionEvaluator.java/right.java
		return createdNFA;
	}

	// @Override
	// public VertexCosts calculateSubtreeEvaluationCosts() {
	// return greqlEvaluator.getCostModel().calculateCostsEdgePathDescription(
	// this);
	// }

}
