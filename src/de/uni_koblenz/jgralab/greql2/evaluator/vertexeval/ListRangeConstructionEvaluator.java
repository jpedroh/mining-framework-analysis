package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
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
public class ListRangeConstructionEvaluator extends VertexEvaluator<ListRangeConstruction> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java


  /**
	 * Creates a new ListRangeConstructionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public ListRangeConstructionEvaluator(ListRangeConstruction vertex, Query query) {
    super(vertex, query);
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> firstElementEvaluator = null;
=======
  private VertexEvaluator firstElementEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> lastElementEvaluator = null;
=======
  private VertexEvaluator lastElementEvaluator = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java


  private void getEvals() {
    Expression firstElementExpression = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    vertex.getFirstIsFirstValueOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsFirstValueOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java
    ;
    Expression lastElementExpression = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    vertex.getFirstIsLastValueOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsLastValueOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java
    ;
    firstElementEvaluator = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    getVertexEvaluator(firstElementExpression)
=======
    getMark(firstElementExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java
    ;
    lastElementEvaluator = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    getVertexEvaluator(lastElementExpression)
=======
    getMark(lastElementExpression)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/right.java
    ;
  }

  @Override public PVector<Integer> evaluate(InternalGreqlEvaluator evaluator) {
    PVector<Integer> resultList = JGraLab.vector();
    if (firstElementEvaluator == null) {
      getEvals();
    }
    Object firstElement = firstElementEvaluator.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
    Object lastElement = lastElementEvaluator.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/ListRangeConstructionEvaluator.java/left.java
    evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
    );
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
}