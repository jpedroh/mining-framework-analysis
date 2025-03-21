package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.TupleConstruction;
import de.uni_koblenz.jgralab.greql2.types.Tuple;

/**
 * Evaluates a TupleConstruction vertex in the GReQL 2 syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TupleConstructionEvaluator extends ValueConstructionEvaluator<TupleConstruction> {
  public TupleConstructionEvaluator(TupleConstruction vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public PCollection<Object> evaluate(InternalGreqlEvaluator evaluator) {
    return createValue(Tuple.empty(), evaluator);
  }
}