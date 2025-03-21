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
package de.uni_koblenz.jgralab.greql2.optimizer;

import java.util.ArrayList;
import java.util.logging.Logger;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Graph;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf;

/**
 * Replaces all {@link Xor} {@link FunctionApplication}s in the {@link Greql2}
 * graph according the rule
 * <code>a xor b = (a and not b) or (not a and b)</code>.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/left.java
 * 
 * @author ist@uni-koblenz.de
 * 
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/base.java
 * 
 * @author Tassilo Horn (horn), 2008
 * 
=======
 *
 * @author ist@uni-koblenz.de
 *
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/right.java
 */
public class TransformXorFunctionApplicationOptimizer extends Optimizer {

	private static Logger logger = JGraLab
			.getLogger(TransformXorFunctionApplicationOptimizer.class
					.getPackage().getName());

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/base.java
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.optimizer.Optimizer#isEquivalent(de.uni_koblenz
	 * .jgralab.greql2.optimizer.Optimizer)
	 */
=======
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.greql2.optimizer.Optimizer#isEquivalent(de.uni_koblenz
	 * .jgralab.greql2.optimizer.Optimizer)
	 */
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/right.java
	@Override
	protected boolean isEquivalent(Optimizer optimizer) {
		if (optimizer instanceof TransformXorFunctionApplicationOptimizer) {
			return true;
		}
		return false;
	}

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/base.java
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.optimizer.Optimizer#optimize(de.uni_koblenz
	 * .jgralab.greql2.evaluator.GreqlEvaluator,
	 * de.uni_koblenz.jgralab.greql2.schema.Greql2)
	 */
=======
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.greql2.optimizer.Optimizer#optimize(de.uni_koblenz
	 * .jgralab.greql2.evaluator.GreqlEvaluator,
	 * de.uni_koblenz.jgralab.greql2.schema.Greql2)
	 */
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/right.java
	@Override
	protected boolean optimize(Greql2Graph syntaxgraph, CostModel costModel) {
		ArrayList<FunctionApplication> xors = new ArrayList<FunctionApplication>();
		for (FunctionApplication funApp : syntaxgraph
				.getFunctionApplicationVertices()) {
			if (OptimizerUtility.isXor(funApp)) {
				xors.add(funApp);
			}
		}
		boolean somethingWasTransformed = false;
		for (FunctionApplication xor : xors) {
			somethingWasTransformed = true;
			// Figure out the two arguments of the Xor
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/left.java
			IsArgumentOf isArgOf = xor
					.getFirstIsArgumentOfIncidence(EdgeDirection.IN);
			Expression arg1 = isArgOf.getAlpha();
			isArgOf = isArgOf.getNextIsArgumentOfIncidence(EdgeDirection.IN);
			Expression arg2 = isArgOf.getAlpha();
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/base.java
			IsArgumentOf isArgOf = xor.getFirstIsArgumentOf(EdgeDirection.IN);
			Expression arg1 = (Expression) isArgOf.getAlpha();
			isArgOf = isArgOf.getNextIsArgumentOf(EdgeDirection.IN);
			Expression arg2 = (Expression) isArgOf.getAlpha();
=======
			IsArgumentOf isArgOf = xor
					.getFirstIsArgumentOfIncidence(EdgeDirection.IN);
			Expression arg1 = (Expression) isArgOf.getAlpha();
			isArgOf = isArgOf.getNextIsArgumentOfIncidence(EdgeDirection.IN);
			Expression arg2 = (Expression) isArgOf.getAlpha();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/right.java

			// The rule is: a xor b = a and ~b or ~a and b

			// create the top-level Or
			FunctionApplication or = syntaxgraph.createFunctionApplication();
			FunctionId orId = OptimizerUtility.findOrCreateFunctionId("or",
					syntaxgraph);
			syntaxgraph.createIsFunctionIdOf(orId, or);

			// create the two Ands needed in the replacement
			FunctionApplication leftAnd = syntaxgraph
					.createFunctionApplication();
			FunctionApplication rightAnd = syntaxgraph
					.createFunctionApplication();
			FunctionId andId = OptimizerUtility.findOrCreateFunctionId("and",
					syntaxgraph);
			syntaxgraph.createIsFunctionIdOf(andId, leftAnd);
			syntaxgraph.createIsFunctionIdOf(andId, rightAnd);

			// create the two Nots
			FunctionApplication leftNot = syntaxgraph
					.createFunctionApplication();
			FunctionApplication rightNot = syntaxgraph
					.createFunctionApplication();
			FunctionId notId = OptimizerUtility.findOrCreateFunctionId("not",
					syntaxgraph);
			syntaxgraph.createIsFunctionIdOf(notId, leftNot);
			syntaxgraph.createIsFunctionIdOf(notId, rightNot);

			// connect all vertices
			syntaxgraph.createIsArgumentOf(leftAnd, or);
			syntaxgraph.createIsArgumentOf(rightAnd, or);

			syntaxgraph.createIsArgumentOf(arg1, leftAnd);
			syntaxgraph.createIsArgumentOf(leftNot, leftAnd);
			syntaxgraph.createIsArgumentOf(arg2, leftNot);

			syntaxgraph.createIsArgumentOf(arg1, rightNot);
			syntaxgraph.createIsArgumentOf(rightNot, rightAnd);
			syntaxgraph.createIsArgumentOf(arg2, rightAnd);

			// relink all edges that started in the Xor vertex
			ArrayList<Edge> edgesToBeRelinked = new ArrayList<Edge>();
			Edge e = xor.getFirstIncidence(EdgeDirection.OUT);
			while (e != null) {
				edgesToBeRelinked.add(e);
				e = e.getNextIncidence(EdgeDirection.OUT);
			}
			for (Edge edge : edgesToBeRelinked) {
				edge.setAlpha(or);
			}

			logger.finer(optimizerHeaderString() + "Transformed " + xor
					+ " to (" + arg1 + " & ~" + arg2 + ") | (~" + arg1 + " & "
					+ arg2 + ").");

			// delete the Xor
			xor.delete();
		}
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/left.java
||||||| /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/base.java
=======

		recreateVertexEvaluators(eval);
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/optimizer/TransformXorFunctionApplicationOptimizer.java/right.java
		return somethingWasTransformed;
	}

}
