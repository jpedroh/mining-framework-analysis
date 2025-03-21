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
public class MapComprehensionEvaluator extends ComprehensionEvaluator<MapComprehension> {
  public MapComprehensionEvaluator(MapComprehension vertex, QueryImpl query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    initializeMaxCount();
    VariableDeclarationLayer declLayer = getVariableDeclationLayer(evaluator);
    PMap<Object, Object> resultMap = JGraLab.map();
    Expression key = (Expression) vertex.getFirstIsKeyExprOfComprehensionIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator<? extends Expression> keyEval = query.getVertexEvaluator(key);
    Expression val = (Expression) vertex.getFirstIsValueExprOfComprehensionIncidence(EdgeDirection.IN).getAlpha();
    VertexEvaluator<? extends Expression> valEval = query.getVertexEvaluator(val);
    declLayer.reset();
    while (
<<<<<<< /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/left.java
    declLayer.iterate(evaluator)
=======
    declLayer.iterate() && (resultMap.size() < maxCount)
>>>>>>> /usr/src/app/output/jgralab/jgralab/55c5691ba6b104f745af5ca7f98479dba901c8bb/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/MapComprehensionEvaluator.java/right.java
    ) {
      Object jkey = keyEval.getResult(evaluator);
      Object jval = valEval.getResult(evaluator);
      resultMap = resultMap.plus(jkey, jval);
    }
    return resultMap;
  }

  @Override protected PCollection<Object> getResultDatastructure(InternalGreqlEvaluator evaluator) {
    return null;
  }
}