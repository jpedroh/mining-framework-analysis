package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.Query;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsGoalRestrOf;
import de.uni_koblenz.jgralab.greql2.schema.IsStartRestrOf;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathDescriptionEvaluator.java/left.java
/**
 * This is the base class for all path descriptions. It provides methods to add
 * start- and goalrestrictions to the pathdescription. The subclasses like
 * AlternativePathDescriptionEvaluator etc. don't need to care about this,
 * because the method PathDescriptionEvaluator.getResult(...) automaticly adds
 * start- and goalrestrictions to the pathdescription, if a start or
 * goalrestriction exists.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class PathDescriptionEvaluator<V extends PathDescription> extends VertexEvaluator<V> {
  /**
	 * The NFA which is created out of this PathDescription
	 */
  protected NFA createdNFA;

  /**
	 * Creates a new PathDescriptionEvaluator
	 * 
	 * @param eval
	 */
  public PathDescriptionEvaluator(V vertex, Query query) {
    super(vertex, query);
  }

  /**
	 * returns the nfa
	 */
  public NFA getNFA(InternalGreqlEvaluator evaluator) {
    if (createdNFA == null) {
      getResult(evaluator);
    }
    return createdNFA;
  }

  /**
	 * Returns the created NFA, encapsulated in a JValue The NFA for the path
	 * description doesn't depend on the subgraph, so the getResult-Methode is
	 * overwritten
	 * 
	 * @return the result as jvalue
	 */
  @Override public Object getResult(InternalGreqlEvaluator evaluator) {
    if (createdNFA == null) {
      Object result = evaluate(evaluator);
      createdNFA = (NFA) result;
      evaluator.setLocalEvaluationResult(vertex, result);
      addGoalRestrictions(evaluator);
      addStartRestrictions(evaluator);
    }
    return evaluator.getLocalEvaluationResult(vertex);
  }

  /**
	 * creates the lists of goal type restrictions from all TypeId-Vertices that
	 * belong to this path descritpion and adds the transitions that accepts
	 * them to the nfa
	 */
  protected void addGoalRestrictions(InternalGreqlEvaluator evaluator) {
    PathDescription pathDesc = getVertex();
    VertexEvaluator<? extends Expression> goalRestEval = null;
    IsGoalRestrOf inc = pathDesc.getFirstIsGoalRestrOfIncidence(EdgeDirection.IN);
    if (inc == null) {
      return;
    }
    TypeCollection typeCollection = new TypeCollection();
    while (inc != null) {
      VertexEvaluator<? extends Expression> vertexEval = query.getVertexEvaluator(inc.getAlpha());
      if (vertexEval instanceof TypeIdEvaluator) {
        TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEval;
        typeCollection.addTypes((TypeCollection) typeEval.getResult(evaluator));
      } else {
        goalRestEval = vertexEval;
      }
      inc = inc.getNextIsGoalRestrOfIncidence(EdgeDirection.IN);
    }
    NFA.addGoalTypeRestriction(getNFA(evaluator), typeCollection);
    if (goalRestEval != null) {
      NFA.addGoalBooleanRestriction(getNFA(evaluator), goalRestEval, query);
    }
  }

  /**
	 * creates the lists of start and goal type restrictions from all
	 * TypeId-Vertices that belong to this path descritpion
	 * 
	 * @return the generated list of types
	 */
  protected void addStartRestrictions(InternalGreqlEvaluator evaluator) {
    PathDescription pathDesc = getVertex();
    VertexEvaluator<? extends Expression> startRestEval = null;
    IsStartRestrOf inc = pathDesc.getFirstIsStartRestrOfIncidence(EdgeDirection.IN);
    if (inc == null) {
      return;
    }
    TypeCollection typeCollection = new TypeCollection();
    while (inc != null) {
      VertexEvaluator<? extends Expression> vertexEval = query.getVertexEvaluator(inc.getAlpha());
      if (vertexEval instanceof TypeIdEvaluator) {
        TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEval;
        typeCollection.addTypes((TypeCollection) typeEval.getResult(evaluator));
      } else {
        startRestEval = vertexEval;
      }
      inc = inc.getNextIsStartRestrOfIncidence(EdgeDirection.IN);
    }
    NFA.addStartTypeRestriction(getNFA(evaluator), typeCollection);
    if (startRestEval != null) {
      NFA.addStartBooleanRestriction(getNFA(evaluator), startRestEval, query);
    }
  }
}
=======
/**
 * This is the base class for all path descriptions. It provides methods to add
 * start- and goalrestrictions to the pathdescription. The subclasses like
 * AlternativePathDescriptionEvaluator etc. don't need to care about this,
 * because the method PathDescriptionEvaluator.getResult(...) automaticly adds
 * start- and goalrestrictions to the pathdescription, if a start or
 * goalrestriction exists.
 *
 * @author ist@uni-koblenz.de
 *
 */
public abstract class PathDescriptionEvaluator extends VertexEvaluator {
  /**
	 * The NFA which is created out of this PathDescription
	 */
  protected NFA createdNFA;

  /**
	 * Creates a new PathDescriptionEvaluator
	 *
	 * @param eval
	 */
  public PathDescriptionEvaluator(GreqlEvaluator eval) {
    super(eval);
  }

  /**
	 * returns the nfa
	 */
  public NFA getNFA() {
    if (createdNFA == null) {
      getResult();
    }
    return createdNFA;
  }

  /**
	 * Returns the created NFA, encapsulated in a JValue The NFA for the path
	 * description doesn't depend on the subgraph, so the getResult-Methode is
	 * overwritten
	 *
	 * @return the result as jvalue
	 */
  @Override public Object getResult() {
    if (createdNFA == null) {
      result = evaluate();
      createdNFA = (NFA) result;
      addGoalRestrictions();
      addStartRestrictions();
    }
    return result;
  }

  /**
	 * creates the lists of goal type restrictions from all TypeId-Vertices that
	 * belong to this path descritpion and adds the transitions that accepts
	 * them to the nfa
	 */
  protected void addGoalRestrictions() {
    PathDescription pathDesc = (PathDescription) getVertex();
    VertexEvaluator goalRestEval = null;
    IsGoalRestrOf inc = pathDesc.getFirstIsGoalRestrOfIncidence(EdgeDirection.IN);
    if (inc == null) {
      return;
    }
    TypeCollection typeCollection = new TypeCollection();
    while (inc != null) {
      VertexEvaluator vertexEval = vertexEvalMarker.getMark(inc.getAlpha());
      if (vertexEval instanceof TypeIdEvaluator) {
        TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEval;
        typeCollection.addTypes((TypeCollection) typeEval.getResult());
      } else {
        goalRestEval = vertexEval;
      }
      inc = inc.getNextIsGoalRestrOfIncidence(EdgeDirection.IN);
    }
    NFA.addGoalTypeRestriction(getNFA(), typeCollection);
    if (goalRestEval != null) {
      NFA.addGoalBooleanRestriction(getNFA(), goalRestEval, vertexEvalMarker);
    }
  }

  /**
	 * creates the lists of start and goal type restrictions from all
	 * TypeId-Vertices that belong to this path descritpion
	 *
	 * @return the generated list of types
	 */
  protected void addStartRestrictions() {
    PathDescription pathDesc = (PathDescription) getVertex();
    VertexEvaluator startRestEval = null;
    IsStartRestrOf inc = pathDesc.getFirstIsStartRestrOfIncidence(EdgeDirection.IN);
    if (inc == null) {
      return;
    }
    TypeCollection typeCollection = new TypeCollection();
    while (inc != null) {
      VertexEvaluator vertexEval = vertexEvalMarker.getMark(inc.getAlpha());
      if (vertexEval instanceof TypeIdEvaluator) {
        TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEval;
        typeCollection.addTypes((TypeCollection) typeEval.getResult());
      } else {
        startRestEval = vertexEval;
      }
      inc = inc.getNextIsStartRestrOfIncidence(EdgeDirection.IN);
    }
    NFA.addStartTypeRestriction(getNFA(), typeCollection);
    if (startRestEval != null) {
      NFA.addStartBooleanRestriction(getNFA(), startRestEval, vertexEvalMarker);
    }
  }
}
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/greql2/evaluator/vertexeval/PathDescriptionEvaluator.java/right.java
