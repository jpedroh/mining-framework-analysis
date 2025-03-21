package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphDefinition;

/**
 * Base class for all subgraph definition evaluators
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class SubgraphDefinitionEvaluator<V extends SubgraphDefinition> extends VertexEvaluator<V> {
  public SubgraphDefinitionEvaluator(V vertex, QueryImpl query) {
    super(vertex, query);
  }
}