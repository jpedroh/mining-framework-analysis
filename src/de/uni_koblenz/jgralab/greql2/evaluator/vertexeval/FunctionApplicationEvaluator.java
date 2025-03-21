package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import java.util.ArrayList;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib.FunctionInfo;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOf;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Evaluates a FunctionApplication vertex in the GReQL-2 Syntaxgraph
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class FunctionApplicationEvaluator extends VertexEvaluator<FunctionApplication> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  protected FunctionApplication vertex;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/FunctionApplicationEvaluator.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * returns the vertex this VertexEvaluator evaluates
	 */
  @Override public Greql2Vertex getVertex() {
    return vertex;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/FunctionApplicationEvaluator.java/right.java


  protected ArrayList<VertexEvaluator> parameterEvaluators = null;

  protected TypeCollection typeArgument = null;

  protected Object[] parameters = null;

  protected int paramEvalCount = 0;

  protected boolean listCreated = false;

  /**
	 * The name of this function
	 */
  private String functionName = null;

  private FunctionInfo fi = null;

  /**
	 * Returns the name of the Greql2Function
	 */
  public String getFunctionName() {
    if (functionName == null) {
      FunctionId id = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/FunctionApplicationEvaluator.java/left.java
      vertex.getFirstIsFunctionIdOfIncidence(EdgeDirection.IN).getAlpha()
=======
      (FunctionId) vertex.getFirstIsFunctionIdOfIncidence(EdgeDirection.IN).getAlpha()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/FunctionApplicationEvaluator.java/right.java
      ;
      functionName = id.get_name();
    }
    return functionName;
  }

  public FunctionInfo getFunctionInfo() {
    if (fi == null) {
      fi = FunLib.getFunctionInfo(getFunctionName());
      if (fi == null) {
        throw new GreqlException("Call to unknown function \'" + getFunctionName() + "\'");
      }
    }
    return fi;
  }

  public Function getFunction() {
    return getFunctionInfo().getFunction();
  }

  @Override public String getLoggingName() {
    return getFunctionName();
  }

  /**
	 * @param eval
	 *            the GreqlEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
  public FunctionApplicationEvaluator(FunctionApplication vertex, Query query) {
    super(vertex, query);
  }

  /**
	 * creates the list of parameter evaluators so that it would not be
	 * necessary to build it up each time the function gets evaluated
	 */
  protected ArrayList<VertexEvaluator> createVertexEvaluatorList() {
    ArrayList<VertexEvaluator> vertexEvalList = new ArrayList<VertexEvaluator>();
    IsArgumentOf inc = vertex.getFirstIsArgumentOfIncidence(EdgeDirection.IN);
    while (inc != null) {
      Expression currentParameterExpr = inc.getAlpha();
      VertexEvaluator paramEval = vertexEvalMarker.getMark(currentParameterExpr);
      vertexEvalList.add(paramEval);
      inc = inc.getNextIsArgumentOfIncidence(EdgeDirection.IN);
    }
    return vertexEvalList;
  }

  /**
	 * creates the type-argument
	 */
  private TypeCollection createTypeArgument() {
    TypeId typeId;
    IsTypeExprOf typeEdge = vertex.getFirstIsTypeExprOfIncidence(EdgeDirection.IN);
    TypeCollection typeCollection = null;
    if (typeEdge != null) {
      typeCollection = new TypeCollection();
      while (typeEdge != null) {
        typeId = (TypeId) typeEdge.getAlpha();
        TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEvalMarker.getMark(typeId);
        typeCollection.addTypes((TypeCollection) typeEval.getResult(graph));
        typeEdge = typeEdge.getNextIsTypeExprOfIncidence(EdgeDirection.IN);
      }
    }
    return typeCollection;
  }

  /**
	 * evaluates the function, calls the right function of the function libary
	 */
  @Override public Object evaluate(InternalGreqlEvaluator evaluator) {
    FunctionInfo fi = getFunctionInfo();
    if (!listCreated) {
      typeArgument = createTypeArgument();
      parameterEvaluators = createVertexEvaluatorList();
      int parameterCount = parameterEvaluators.size();
      if (fi.needsGraphArgument()) {
        parameterCount++;
      }
      if (typeArgument != null) {
        parameterCount++;
      }
      parameters = new Object[parameterCount];
      paramEvalCount = parameterEvaluators.size();
      listCreated = true;
    }
    int p = 0;
    if (fi.needsGraphArgument()) {
      parameters[p++] = graph;
    }
    for (int i = 0; i < paramEvalCount; i++) {
      parameters[p++] = parameterEvaluators.get(i).getResult(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/FunctionApplicationEvaluator.java/left.java
      graph
=======
>>>>>>> Unknown file: This is a bug in JDime.
      );
    }
    if (typeArgument != null) {
      parameters[p] = typeArgument;
    }
    return FunLib.apply(fi, parameters);
  }
}