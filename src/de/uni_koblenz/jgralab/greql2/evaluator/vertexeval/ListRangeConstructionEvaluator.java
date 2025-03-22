package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.ListRangeConstruction;

/**
 * Creates a list of integers. Adds all integer-values to the list, that are
 * between the result of firstElementExpression and lastElementExpression. These
 * borders are also added to the list
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ListRangeConstructionEvaluator extends VertexEvaluator {
  private ListRangeConstruction vertex;

  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }

  /**
	 * Creates a new ListRangeConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ListRangeConstructionEvaluator(ListRangeConstruction vertex, GreqlEvaluator eval) {
    super(eval);
    this.vertex = vertex;
  }

  private VertexEvaluator firstElementEvaluator = null;

  private VertexEvaluator lastElementEvaluator = null;

  private void getEvals() {
    Expression firstElementExpression = (Expression) vertex.getFirstIsFirstValueOfIncidence(EdgeDirection.IN).getAlpha();
    Expression lastElementExpression = (Expression) vertex.getFirstIsLastValueOfIncidence(EdgeDirection.IN).getAlpha();
    firstElementEvaluator = vertexEvalMarker.getMark(firstElementExpression);
    lastElementEvaluator = vertexEvalMarker.getMark(lastElementExpression);
  }

  @Override public PVector<Integer> evaluate() {
    PVector<Integer> resultList = JGraLab.vector();
    if (firstElementEvaluator == null) {
      getEvals();
    }
    Object firstElement = firstElementEvaluator.getResult();
    Object lastElement = lastElementEvaluator.getResult();
    if (firstElement instanceof Integer && lastElement instanceof Integer) {
      if ((Integer) firstElement < (Integer) lastElement) {
        for (int i = (Integer) firstElement; i < (Integer) lastElement + 1; i++) {
          resultList = resultList.plus(i);
        }
      } else {
        for (int i = (Integer) lastElement; i < (Integer) firstElement + 1; i++) {
          resultList = resultList.plus(i);
        }
      }
    }
    return resultList;
  }

  @Override public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
    return this.greqlEvaluator.getCostModel().calculateCostsListRangeConstruction(this, graphSize);
  }

  @Override public long calculateEstimatedCardinality(GraphSize graphSize) {
    return greqlEvaluator.getCostModel().calculateCardinalityListRangeConstruction(this, graphSize);
  }
}