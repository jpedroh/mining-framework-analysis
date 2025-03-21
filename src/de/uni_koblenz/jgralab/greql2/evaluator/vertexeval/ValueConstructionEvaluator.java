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

import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;

import de.uni_koblenz.jgralab.greql2.evaluator.Query;

import de.uni_koblenz.jgralab.greql2.schema.Expression;

import de.uni_koblenz.jgralab.greql2.schema.IsPartOf;

import de.uni_koblenz.jgralab.greql2.schema.ValueConstruction;

import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
/**
 * This is the abstract base class for all ValueConstructions
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
 * 
 * @author ist@uni-koblenz.de
 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/base.java
 * 
 * @author Daniel Bildhauer <dbildh@uni-koblenz.de> Summer 2006, Diploma Thesis
 * 
=======
 *
 * @author ist@uni-koblenz.de
 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
 */
abstract public class ValueConstructionEvaluator<V extends ValueConstruction>
		extends VertexEvaluator<V> {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
	private ArrayList<VertexEvaluator<? extends Expression>> partEvaluators = null;

	public ValueConstructionEvaluator(V vertex, Query query) {
		super(vertex, query);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/base.java
	protected ValueConstruction vertex;
	
	private ArrayList<VertexEvaluator> partEvaluators = null;
	
	
	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Vertex getVertex() {
		return vertex;
=======
	protected ValueConstruction vertex;

	private ArrayList<VertexEvaluator> partEvaluators = null;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
	public final PCollection<Object> createValue(
			PCollection<Object> collection, InternalGreqlEvaluator evaluator) {
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/base.java
	public ValueConstructionEvaluator(ValueConstruction vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	public final JValue createValue(JValueCollection collection)
			throws EvaluateException {
=======
	public ValueConstructionEvaluator(ValueConstruction vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	public final PCollection<Object> createValue(PCollection<Object> collection) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
		if (partEvaluators == null) {
			int partCount = 0;
			IsPartOf inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
			while (inc != null) {
				partCount++;
				inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
			}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
			inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
			partEvaluators = new ArrayList<VertexEvaluator<? extends Expression>>(
					partCount);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/base.java
			inc = vertex.getFirstIsPartOf(EdgeDirection.IN);
			partEvaluators = new ArrayList<VertexEvaluator>(partCount);
=======
			inc = vertex.getFirstIsPartOfIncidence(EdgeDirection.IN);
			partEvaluators = new ArrayList<VertexEvaluator>(partCount);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
			while (inc != null) {
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
				Expression currentExpression = inc.getAlpha();
				VertexEvaluator<? extends Expression> vertexEval = query
						.getVertexEvaluator(currentExpression);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/base.java
				Expression currentExpression = (Expression) inc.getAlpha();
				VertexEvaluator vertexEval = greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(currentExpression);
=======
				Expression currentExpression = (Expression) inc.getAlpha();
				VertexEvaluator vertexEval = vertexEvalMarker
						.getMark(currentExpression);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
				partEvaluators.add(vertexEval);
				inc = inc.getNextIsPartOfIncidence(EdgeDirection.IN);
			}
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/left.java
		for (int i = 0; i < partEvaluators.size(); i++) {
			collection = collection.plus(partEvaluators.get(i).getResult(
					evaluator));
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/base.java
		for (int i=0; i<partEvaluators.size(); i++) {
			collection.add(partEvaluators.get(i).getResult(subgraph));
=======
		for (int i = 0; i < partEvaluators.size(); i++) {
			collection = collection.plus(partEvaluators.get(i).getResult());
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ValueConstructionEvaluator.java/right.java
		}
		return collection;
	}

}
