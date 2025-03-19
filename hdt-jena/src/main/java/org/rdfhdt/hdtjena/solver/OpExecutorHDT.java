package org.rdfhdt.hdtjena.solver;
import org.apache.jena.atlas.lib.Tuple;
import org.apache.jena.atlas.logging.Log;
import org.rdfhdt.hdtjena.HDTGraph;
import org.rdfhdt.hdtjena.HDTJenaConstants;
import org.rdfhdt.hdtjena.bindings.HDTId;
import org.apache.jena.graph.Graph;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpDistinct;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpReduced;
import org.apache.jena.sparql.algebra.optimize.TransformFilterPlacement;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Substitute;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterPeek;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderProc;
import org.apache.jena.sparql.engine.optimizer.reorder.ReorderTransformation;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.mgt.Explain;
import org.apache.jena.util.iterator.Filter;

public class OpExecutorHDT extends OpExecutor {
  public final static OpExecutorFactory opExecFactoryHDT = new OpExecutorFactory() {
    @Override public OpExecutor create(ExecutionContext execCxt) {
      return new OpExecutorHDT(execCxt);
    }
  };

  private boolean isForHDT;

  protected OpExecutorHDT(ExecutionContext execCtx) {
    super(execCtx);
    isForHDT = execCtx.getActiveGraph() instanceof HDTGraph;
  }

  @Override protected QueryIterator execute(OpDistinct opDistinct, QueryIterator input) {
    return super.execute(opDistinct, input);
  }

  @Override protected QueryIterator execute(OpReduced opReduced, QueryIterator input) {
    return super.execute(opReduced, input);
  }

  @Override protected QueryIterator execute(OpFilter opFilter, QueryIterator input) {
    if (!isForHDT) {
      return super.execute(opFilter, input);
    }
    if (OpBGP.isBGP(opFilter.getSubOp())) {
      HDTGraph graph = (HDTGraph) execCxt.getActiveGraph();
      OpBGP opBGP = (OpBGP) opFilter.getSubOp();
      return executeBGP(graph, opBGP, input, opFilter.getExprs(), execCxt);
    }
    return super.execute(opFilter, input);
  }

  @Override protected QueryIterator execute(OpBGP opBGP, QueryIterator input) {
    if (!isForHDT) {
      return super.execute(opBGP, input);
    }
    HDTGraph graph = (HDTGraph) execCxt.getActiveGraph();
    return executeBGP(graph, opBGP, input, null, execCxt);
  }

  /** Execute a BGP (and filters) on a HDT graph, which may be in default storage or it may be a named graph */
  private static QueryIterator executeBGP(HDTGraph graph, OpBGP opBGP, QueryIterator input, ExprList exprs, ExecutionContext execCxt) {
    return optimizeExecuteTriples(graph, input, opBGP.getPattern(), exprs, execCxt);
  }

  private static QueryIterator optimizeExecuteTriples(HDTGraph graph, QueryIterator input, BasicPattern pattern, ExprList exprs, ExecutionContext execCxt) {
    if (!input.hasNext()) {
      return input;
    }
    if (pattern.size() >= 2) {
      ReorderTransformation transform = graph.getReorderTransform();
      if (transform != null) {
        QueryIterPeek peek = QueryIterPeek.create(input, execCxt);
        input = peek;
        pattern = reorder(pattern, peek, transform);
      }
    }
    Op op = null;
    if (exprs != null) {
      op = TransformFilterPlacement.transform(exprs, pattern);
    } else {
      op = new OpBGP(pattern);
    }
    return plainExecute(op, input, execCxt);
  }

  /** Execute without modification of the op - does <b>not</b> apply special graph name translations */
  private static QueryIterator plainExecute(Op op, QueryIterator input, ExecutionContext execCxt) {
    ExecutionContext ec2 = new ExecutionContext(execCxt);
    ec2.setExecutor(plainFactory);
    return QC.execute(op, input, ec2);
  }

  private static BasicPattern reorder(BasicPattern pattern, QueryIterPeek peek, ReorderTransformation transform) {
    if (transform != null) {
      if (!peek.hasNext()) {
        throw new ARQInternalErrorException("Peek iterator is already empty");
      }
      BasicPattern pattern2 = Substitute.substitute(pattern, peek.peek());
      ReorderProc proc = transform.reorderIndexes(pattern2);
      pattern = proc.reorder(pattern);
    }
    return pattern;
  }

  private static OpExecutorFactory plainFactory = new OpExecutorPlainFactoryHDT();

  private static class OpExecutorPlainFactoryHDT implements OpExecutorFactory {
    @Override public OpExecutor create(ExecutionContext execCxt) {
      return new OpExecutorPlainHDT(execCxt);
    }
  }

  private static class OpExecutorPlainHDT extends OpExecutor {
    Filter<Tuple<HDTId>> filter;

    @SuppressWarnings(value = { "unchecked" }) public OpExecutorPlainHDT(ExecutionContext execCxt) {
      super(execCxt);
      filter = (Filter<Tuple<HDTId>>) execCxt.getContext().get(HDTJenaConstants.FILTER_SYMBOL);
    }

    @Override public QueryIterator execute(OpBGP opBGP, QueryIterator input) {
      Graph g = execCxt.getActiveGraph();
      if (g instanceof HDTGraph) {
        BasicPattern bgp = opBGP.getPattern();
        Explain.explain("Execute", bgp, execCxt.getContext());
        return HDTSolverLib.execute((HDTGraph) g, bgp, input, filter, execCxt);
      }
      Log.warn(this, "Non-HDTGraph passed to OpExecutorPlainHDT");
      return super.execute(opBGP, input);
    }
  }
}