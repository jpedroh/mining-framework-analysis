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
 *
 */
package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import org.pcollections.PCollection;
import org.pcollections.PMap;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.MapComprehension;

/**
 * @author Tassilo Horn <horn@uni-koblenz.de>
 *
 */
public class MapComprehensionEvaluator extends
		ComprehensionEvaluator<MapComprehension> {

	public MapComprehensionEvaluator(MapComprehension vertex, QueryImpl query) {
		super(vertex, query);
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/left.java
	// @Override
	// protected VertexCosts calculateSubtreeEvaluationCosts() {
	// return greqlEvaluator.getCostModel().calculateCostsMapComprehension(
	// this);
	// }
||||||| /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/base.java
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#
	 * calculateSubtreeEvaluationCosts
	 * (de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel()
				.calculateCostsMapComprehension(this, graphSize);
	}
=======
	/*
	 * (non-Javadoc)
	 *
	 * @seede.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#
	 * calculateSubtreeEvaluationCosts
	 * (de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize)
	 */
	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel()
				.calculateCostsMapComprehension(this, graphSize);
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/right.java

<<<<<<< /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/base.java
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#evaluate
	 * ()
	 */
=======
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#evaluate
	 * ()
	 */
>>>>>>> /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/right.java
	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/left.java
	public Object evaluate(InternalGreqlEvaluator evaluator) {
		VariableDeclarationLayer declLayer = getVariableDeclationLayer(evaluator);

||||||| /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/base.java
	public Object evaluate() {
		VariableDeclarationLayer declLayer = getVariableDeclationLayer();

=======
	public Object evaluate() {
		initializeMaxCount();
		VariableDeclarationLayer declLayer = getVariableDeclationLayer();
>>>>>>> /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/right.java
		PMap<Object, Object> resultMap = JGraLab.map();

		Expression key = (Expression) vertex
				.getFirstIsKeyExprOfComprehensionIncidence(EdgeDirection.IN)
				.getAlpha();
		VertexEvaluator<? extends Expression> keyEval = query
				.getVertexEvaluator(key);
		Expression val = (Expression) vertex
				.getFirstIsValueExprOfComprehensionIncidence(EdgeDirection.IN)
				.getAlpha();
		VertexEvaluator<? extends Expression> valEval = query
				.getVertexEvaluator(val);
		declLayer.reset();
<<<<<<< /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/left.java
		while (declLayer.iterate(evaluator)) {
			Object jkey = keyEval.getResult(evaluator);
			Object jval = valEval.getResult(evaluator);
||||||| /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/base.java
		while (declLayer.iterate()) {
			Object jkey = keyEval.getResult();
			Object jval = valEval.getResult();
=======
		while (declLayer.iterate() && (resultMap.size() < maxCount)) {
			Object jkey = keyEval.getResult();
			Object jval = valEval.getResult();
>>>>>>> /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/right.java
			resultMap = resultMap.plus(jkey, jval);
		}
		return resultMap;
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/left.java
	// @Override
	// public long calculateEstimatedCardinality() {
	// return greqlEvaluator.getCostModel()
	// .calculateCardinalityMapComprehension(this);
	// }
||||||| /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/base.java
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#getVertex
	 * ()
	 */
	@Override
	public Comprehension getVertex() {
		return vertex;
	}
=======
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator#getVertex
	 * ()
	 */
	@Override
	public Comprehension getVertex() {
		return vertex;
	}
>>>>>>> /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/right.java

	@Override
	protected PCollection<Object> getResultDatastructure(
			InternalGreqlEvaluator evaluator) {
		return null;
	}

}
