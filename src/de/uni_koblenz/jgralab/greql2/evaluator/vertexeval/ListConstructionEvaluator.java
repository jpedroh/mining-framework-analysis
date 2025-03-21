package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PCollection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.ListConstruction;

/**
 * Evaluates a ListConstruction vertex in the GReQL 2 syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ListConstructionEvaluator extends ValueConstructionEvaluator<ListConstruction> {
  public ListConstructionEvaluator(ListConstruction vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public PCollection<Object> evaluate(InternalGreqlEvaluator evaluator) {
    return createValue(JGraLab.vector(), evaluator);
  }
}