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

import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.schema.IntLiteral;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;

/**
 * Evaluates a Integer Literal, that means, provides access to the literal value
 * using the getResult(...)-Method. This is needed, because is should make no
 * difference for the other VertexEvaluators, if a value is the result of a
 * maybe complex evaluation or if it is a literal.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class IntLiteralEvaluator extends VertexEvaluator<IntLiteral> {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/left.java
	public IntLiteralEvaluator(IntLiteral vertex, Query query) {
		super(vertex, query);
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/base.java
	private IntLiteral vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Vertex getVertex() {
		return vertex;
	}

	public IntLiteralEvaluator(IntLiteral vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
=======
	private IntLiteral vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	public IntLiteralEvaluator(IntLiteral vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/right.java
	}

	@Override
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/left.java
	public Integer evaluate(InternalGreqlEvaluator evaluator) {
		return vertex.get_intValue();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/base.java
	public JValue evaluate() throws EvaluateException {
		return new JValue(vertex.getIntValue());
=======
	public Integer evaluate() {
		return vertex.get_intValue();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/IntLiteralEvaluator.java/right.java
	}

	// @Override
	// public VertexCosts calculateSubtreeEvaluationCosts() {
	// return new VertexCosts(1, 1, 1);
	// }

}
