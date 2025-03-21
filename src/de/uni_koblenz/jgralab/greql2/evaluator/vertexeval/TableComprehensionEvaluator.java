package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsTableHeaderOf;
import de.uni_koblenz.jgralab.greql2.schema.TableComprehension;
import de.uni_koblenz.jgralab.greql2.types.Table;
import de.uni_koblenz.jgralab.greql2.types.Tuple;

/**
 * Evaluates a TableComprehensionvertex in the GReQL-2 Syntaxgraph. A
 * TableComprehension vertex is constructed using the notation reportTable
 * columHeader, rowHeader, cellContent
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TableComprehensionEvaluator extends VertexEvaluator<TableComprehension> {
  private VariableDeclarationLayer declarationLayer;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> columnHeaderEval = null;
=======
  private VertexEvaluator columnHeaderEval = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> rowHeaderEval = null;
=======
  private VertexEvaluator rowHeaderEval = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
  private VertexEvaluator<? extends Expression> resultDefEval = null;
=======
  private VertexEvaluator resultDefEval = null;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java


  private boolean initialized = false;

  private void initialize(InternalGreqlEvaluator evaluator) {
    Declaration d = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    vertex.getFirstIsCompDeclOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Declaration) vertex.getFirstIsCompDeclOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    DeclarationEvaluator declEval = (DeclarationEvaluator) 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    getVertexEvaluator(d)
=======
    getMark(d)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    declarationLayer = (VariableDeclarationLayer) declEval.getResult(evaluator);
    Expression columnHeader = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    vertex.getFirstIsColumnHeaderExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsColumnHeaderExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    columnHeaderEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    getVertexEvaluator(columnHeader)
=======
    getMark(columnHeader)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    Expression rowHeader = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    vertex.getFirstIsRowHeaderExprOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsRowHeaderExprOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    rowHeaderEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    getVertexEvaluator(rowHeader)
=======
    getMark(rowHeader)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    Expression resultDef = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    vertex.getFirstIsCompResultDefOfIncidence(EdgeDirection.IN).getAlpha()
=======
    (Expression) vertex.getFirstIsCompResultDefOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    resultDefEval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    query
=======
    vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    getVertexEvaluator(resultDef)
=======
    getMark(resultDef)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
    ;
    initialized = true;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java


  /**
	 * Creates a new TableComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
  public TableComprehensionEvaluator(TableComprehension vertex, Query query) {
    super(vertex, query);
  }

  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    if (!initialized) {
      initialize(evaluator);
    }
    TreeMap<Object, HashMap<Object, Object>> tableMap = new TreeMap<Object, HashMap<Object, Object>>();
    Set<Object> completeColumnHeaderTuple = new HashSet<Object>();
    TreeSet<Object> rowHeaderSet = new TreeSet<Object>();
    declarationLayer.reset();
    while (declarationLayer.iterate(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
    null
=======
>>>>>>> Unknown file: This is a bug in JDime.
    )) {
      Object columnHeaderEntry = columnHeaderEval.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
      evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
      );
      completeColumnHeaderTuple.add(columnHeaderEntry);
      Object rowHeaderEntry = rowHeaderEval.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
      evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
      );
      Object localResult = resultDefEval.getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
      evaluator
=======
>>>>>>> Unknown file: This is a bug in JDime.
      );
      HashMap<Object, Object> row = tableMap.get(rowHeaderEntry);
      if (row == null) {
        row = new HashMap<Object, Object>();
        tableMap.put(rowHeaderEntry, row);
        rowHeaderSet.add(rowHeaderEntry);
      }
      row.put(columnHeaderEntry, localResult);
    }
    Table<Object> resultTable = Table.empty();
    PVector<String> headerTuple = resultTable.getTitles();
    TreeSet<Object> completeColumnHeaderTreeSet = new TreeSet<Object>();
    for (Object jValueImpl : completeColumnHeaderTuple) {
      completeColumnHeaderTreeSet.add(jValueImpl);
    }
    Iterator<Object> colIter = completeColumnHeaderTreeSet.iterator();
    IsTableHeaderOf tHeader = vertex.getFirstIsTableHeaderOfIncidence(EdgeDirection.IN);
    if (tHeader != null) {
      VertexEvaluator<? extends Expression> theval = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
      query
=======
      vertexEvalMarker
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
      .
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/left.java
      getVertexEvaluator(tHeader.getAlpha())
=======
      getMark(tHeader.getAlpha())
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/TableComprehensionEvaluator.java/right.java
      ;
      headerTuple = headerTuple.plus((String) theval.getResult(evaluator));
    } else {
      headerTuple.plus("");
    }
    while (colIter.hasNext()) {
      headerTuple = headerTuple.plus(colIter.next().toString());
    }
    resultTable = resultTable.withTitles(headerTuple);
    Iterator<Entry<Object, HashMap<Object, Object>>> rowIter = tableMap.entrySet().iterator();
    while (rowIter.hasNext()) {
      Entry<Object, HashMap<Object, Object>> currentEntry = rowIter.next();
      Object currentRowHeader = currentEntry.getKey();
      HashMap<Object, Object> currentRow = currentEntry.getValue();
      colIter = completeColumnHeaderTreeSet.iterator();
      Tuple rowTuple = Tuple.empty();
      rowTuple = rowTuple.plus(currentRowHeader);
      while (colIter.hasNext()) {
        Object cellEntry = currentRow.get(colIter.next());
        rowTuple = rowTuple.plus(cellEntry);
      }
      resultTable = resultTable.plus(rowTuple);
    }
    return resultTable;
  }
}