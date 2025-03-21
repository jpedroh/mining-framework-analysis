package de.uni_koblenz.jgralab.greql2.optimizer;
import java.util.ArrayList;
import java.util.logging.Logger;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.OptimizerException;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf;
import de.uni_koblenz.jgralab.impl.InternalEdge;

/**
 * Replaces all {@link Xor} {@link FunctionApplication}s in the {@link Greql2}
 * graph according the rule
 * <code>a xor b = (a and not b) or (not a and b)</code>.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TransformXorFunctionApplicationOptimizer extends OptimizerBase {
  private static Logger logger = JGraLab.getLogger(TransformXorFunctionApplicationOptimizer.class.getPackage().getName());

  @Override public boolean isEquivalent(Optimizer optimizer) {
    if (optimizer instanceof TransformXorFunctionApplicationOptimizer) {
      return true;
    }
    return false;
  }

  @Override public boolean optimize(GreqlEvaluator eval, Greql2 syntaxgraph) throws OptimizerException {
    ArrayList<FunctionApplication> xors = new ArrayList<FunctionApplication>();
    for (FunctionApplication funApp : syntaxgraph.getFunctionApplicationVertices()) {
      if (OptimizerUtility.isXor(funApp)) {
        xors.add(funApp);
      }
    }
    boolean somethingWasTransformed = false;
    for (FunctionApplication xor : xors) {
      somethingWasTransformed = true;
      IsArgumentOf isArgOf = xor.getFirstIsArgumentOfIncidence(EdgeDirection.IN);
      Expression arg1 = (Expression) isArgOf.getAlpha();
      isArgOf = isArgOf.getNextIsArgumentOfIncidence(EdgeDirection.IN);
      Expression arg2 = (Expression) isArgOf.getAlpha();
      FunctionApplication or = syntaxgraph.createFunctionApplication();
      FunctionId orId = OptimizerUtility.findOrCreateFunctionId("or", syntaxgraph);
      syntaxgraph.createIsFunctionIdOf(orId, or);
      FunctionApplication leftAnd = syntaxgraph.createFunctionApplication();
      FunctionApplication rightAnd = syntaxgraph.createFunctionApplication();
      FunctionId andId = OptimizerUtility.findOrCreateFunctionId("and", syntaxgraph);
      syntaxgraph.createIsFunctionIdOf(andId, leftAnd);
      syntaxgraph.createIsFunctionIdOf(andId, rightAnd);
      FunctionApplication leftNot = syntaxgraph.createFunctionApplication();
      FunctionApplication rightNot = syntaxgraph.createFunctionApplication();
      FunctionId notId = OptimizerUtility.findOrCreateFunctionId("not", syntaxgraph);
      syntaxgraph.createIsFunctionIdOf(notId, leftNot);
      syntaxgraph.createIsFunctionIdOf(notId, rightNot);
      syntaxgraph.createIsArgumentOf(leftAnd, or);
      syntaxgraph.createIsArgumentOf(rightAnd, or);
      syntaxgraph.createIsArgumentOf(arg1, leftAnd);
      syntaxgraph.createIsArgumentOf(leftNot, leftAnd);
      syntaxgraph.createIsArgumentOf(arg2, leftNot);
      syntaxgraph.createIsArgumentOf(arg1, rightNot);
      syntaxgraph.createIsArgumentOf(rightNot, rightAnd);
      syntaxgraph.createIsArgumentOf(arg2, rightAnd);
      ArrayList<InternalEdge> edgesToBeRelinked = new ArrayList<InternalEdge>();
      InternalEdge e = (InternalEdge) xor.getFirstIncidence(EdgeDirection.OUT);
      while (e != null) {
        edgesToBeRelinked.add(e);
        e = (InternalEdge) e.getNextIncidence(EdgeDirection.OUT);
      }
      for (InternalEdge edge : edgesToBeRelinked) {
        edge.setAlpha(or);
      }
      logger.finer(optimizerHeaderString() + "Transformed " + xor + " to (" + arg1 + " & ~" + arg2 + ") | (~" + arg1 + " & " + arg2 + ").");
      xor.delete();
    }
    recreateVertexEvaluators(eval);
    return somethingWasTransformed;
  }
}