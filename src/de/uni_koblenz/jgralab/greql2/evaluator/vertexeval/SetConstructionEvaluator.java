package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.SetConstruction;

/**
 * Evaluates a SetConstruction vertex in the GReQL 2 syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SetConstructionEvaluator extends ValueConstructionEvaluator<SetConstruction> {
  public SetConstructionEvaluator(SetConstruction vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    return createValue(JGraLab.set(), evaluator);
  }
}