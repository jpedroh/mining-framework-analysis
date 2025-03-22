package de.uni_koblenz.jgralab.greql2.optimizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.OptimizerException;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsConstraintOf;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.IsVarOf;
import de.uni_koblenz.jgralab.greql2.schema.RecordConstruction;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.greql2.schema.RecordId;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.schema.Attribute;

/**
 * This {@link Optimizer} implements the transformation "Selection as early as
 * possible".
 *
 * @author ist@uni-koblenz.de
 *
 */
public class EarlySelectionOptimizer extends OptimizerBase {
  private static Logger logger = JGraLab.getLogger(EarlySelectionOptimizer.class.getPackage().getName());

  private Greql2 syntaxgraph;

  @Override public boolean isEquivalent(Optimizer optimizer) {
    if (optimizer instanceof EarlySelectionOptimizer) {
      return true;
    }
    return false;
  }

  @Override public boolean optimize(GreqlEvaluator eval, Greql2 syntaxgraph) throws OptimizerException {
    this.syntaxgraph = syntaxgraph;
    int noOfRuns = 1;
    while (runOptimization()) {
      logger.finer(optimizerHeaderString() + "Iteration " + noOfRuns + " finished.  Restarting...");
      noOfRuns++;
    }
    if (noOfRuns > 1) {
      logger.finer(optimizerHeaderString() + "finished after " + noOfRuns + " runs.");
    }
    OptimizerUtility.createMissingSourcePositions(syntaxgraph);
    recreateVertexEvaluators(eval);
    return noOfRuns > 1;
  }

  /**
	 * Do an optimization run.
	 *
	 * @throws OptimizerException
	 */
  private boolean runOptimization() throws OptimizerException {
    HashMap<SimpleDeclaration, Set<Expression>> movableExpressions = new HashMap<SimpleDeclaration, Set<Expression>>();
    for (Declaration decl : syntaxgraph.getDeclarationVertices()) {
      IsConstraintOf isConst = decl.getFirstIsConstraintOfIncidence(EdgeDirection.IN);
      while (isConst != null) {
        Expression exp = isConst.getAlpha();
        for (Entry<SimpleDeclaration, Set<Expression>> e : collectMovableExpressions(exp).entrySet()) {
          if (movableExpressions.containsKey(e.getKey())) {
            movableExpressions.get(e.getKey()).addAll(e.getValue());
          } else {
            movableExpressions.put(e.getKey(), e.getValue());
          }
        }
        isConst = isConst.getNextIsConstraintOfIncidence(EdgeDirection.IN);
      }
    }
    boolean aTransformationWasDone = false;
    List<SimpleDeclaration> simpleDeclsWithMovableExpressions = new ArrayList<SimpleDeclaration>(movableExpressions.keySet());
    Collections.sort(simpleDeclsWithMovableExpressions, new Comparator<SimpleDeclaration>() {
      @Override public int compare(SimpleDeclaration sd1, SimpleDeclaration sd2) {
        Declaration decl1 = sd1.getFirstIsSimpleDeclOfIncidence().getOmega();
        Declaration decl2 = sd2.getFirstIsSimpleDeclOfIncidence().getOmega();
        if (OptimizerUtility.isAbove(decl1, decl2)) {
          return 1;
        }
        if (OptimizerUtility.isAbove(decl2, decl1)) {
          return -1;
        }
        return 0;
      }
    });
    for (SimpleDeclaration sd : simpleDeclsWithMovableExpressions) {
      Declaration parentDecl = sd.getFirstIsSimpleDeclOfIncidence().getOmega();
      Set<Variable> varsDeclaredBySd = OptimizerUtility.collectVariablesDeclaredBy(sd);
      boolean foundPredicateNeedingAllVars = false, foundPredNeedingPartOfVars = false;
      Set<Variable> varsMaybeToSplitOut = new HashSet<Variable>();
      for (Expression pred : movableExpressions.get(sd)) {
        Set<Variable> neededLocalVars = collectNeededLocalVariables(pred);
        if (neededLocalVars.size() < varsDeclaredBySd.size()) {
          foundPredNeedingPartOfVars = true;
          if (varsMaybeToSplitOut.size() < neededLocalVars.size()) {
            varsMaybeToSplitOut = neededLocalVars;
          }
        } else {
          foundPredicateNeedingAllVars = true;
        }
      }
      List<SimpleDeclaration> sdsOfParentDecl = collectSimpleDeclarationsOf(parentDecl);
      if (foundPredNeedingPartOfVars && (!foundPredicateNeedingAllVars || (sdsOfParentDecl.size() == 1))) {
        splitSimpleDeclaration(sd, varsMaybeToSplitOut);
        aTransformationWasDone = true;
      } else {
        if (varsDeclaredBySd.size() == 1) {
          movePredicatesToOneVarSimpleDeclaration(sd, movableExpressions.get(sd), varsDeclaredBySd);
          aTransformationWasDone = true;
        } else {
          if (sdsOfParentDecl.size() > 1) {
            movePredicatesToMultiVarSimpleDeclaration(sd, movableExpressions.get(sd), varsDeclaredBySd);
            aTransformationWasDone = true;
          }
        }
      }
    }
    return aTransformationWasDone;
  }

  private void movePredicatesToMultiVarSimpleDeclaration(SimpleDeclaration origSD, Set<Expression> predicates, Set<Variable> varsDeclaredByOrigSD) throws OptimizerException {
    logger.finer(optimizerHeaderString() + "(Mn) Performing early selection transformation for " + origSD + " declaring ");
    int varsSize = varsDeclaredByOrigSD.size();
    int i = 1;
    StringBuilder sb = new StringBuilder();
    for (Variable var : varsDeclaredByOrigSD) {
      sb.append(var + " (" + var.get_name() + ")");
      if (i < varsSize) {
        sb.append(", ");
      }
      i++;
    }
    logger.finer(sb.toString() + " with predicates " + predicates + ".");
    Declaration parentDeclOfOrigSD = origSD.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT).getOmega();
    assert parentDeclOfOrigSD.getDegree(EdgeDirection.OUT) == 1;
    HashMap<Variable, Set<Edge>> varEdgeMap = new HashMap<Variable, Set<Edge>>();
    for (Variable var : varsDeclaredByOrigSD) {
      varEdgeMap.put(var, collectVariableAccessEdges(var));
    }
    RecordConstruction newOuterRecord = syntaxgraph.createRecordConstruction();
    StringBuilder newOuterVarName = new StringBuilder();
    for (Variable var : varsDeclaredByOrigSD) {
      newOuterVarName.append(var.get_name());
      RecordElement recElem = syntaxgraph.createRecordElement();
      syntaxgraph.createIsRecordElementOf(recElem, newOuterRecord);
      RecordId recId = syntaxgraph.createRecordId();
      recId.set_name("_" + var.get_name());
      syntaxgraph.createIsRecordIdOf(recId, recElem);
      syntaxgraph.createIsRecordExprOf(var, recElem);
    }
    Variable newOuterRecordVar = syntaxgraph.createVariable();
    newOuterRecordVar.set_name(newOuterVarName.toString());
    SimpleDeclaration newOuterSD = syntaxgraph.createSimpleDeclaration();
    syntaxgraph.createIsSimpleDeclOf(newOuterSD, parentDeclOfOrigSD);
    syntaxgraph.createIsDeclaredVarOf(newOuterRecordVar, newOuterSD);
    SetComprehension newInnerCompr = syntaxgraph.createSetComprehension();
    syntaxgraph.createIsTypeExprOfDeclaration(newInnerCompr, newOuterSD);
    Declaration newInnerDecl = syntaxgraph.createDeclaration();
    syntaxgraph.createIsCompDeclOf(newInnerDecl, newInnerCompr);
    syntaxgraph.createIsCompResultDefOf(newOuterRecord, newInnerCompr);
    origSD.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT).setOmega(newInnerDecl);
    Expression newCombinedConstraint = createConjunction(new ArrayList<Expression>(predicates), new HashSet<Variable>());
    syntaxgraph.createIsConstraintOf(newCombinedConstraint, newInnerDecl);
    for (Expression pred : predicates) {
      removeExpressionFromOriginalConstraint(pred, parentDeclOfOrigSD);
    }
    for (Entry<Variable, Set<Edge>> e : varEdgeMap.entrySet()) {
      FunctionApplication funApp = syntaxgraph.createFunctionApplication();
      FunctionId funId = OptimizerUtility.findOrCreateFunctionId("getValue", syntaxgraph);
      syntaxgraph.createIsFunctionIdOf(funId, funApp);
      Identifier identifier = syntaxgraph.createIdentifier();
      identifier.set_name("_" + e.getKey().get_name());
      syntaxgraph.createIsArgumentOf(newOuterRecordVar, funApp);
      syntaxgraph.createIsArgumentOf(identifier, funApp);
      for (Edge edge : e.getValue()) {
        if (edge.isValid()) {
          edge.setAlpha(funApp);
          assert edge.getAlpha() == funApp;
        }
      }
    }
  }

  private void movePredicatesToOneVarSimpleDeclaration(SimpleDeclaration origSD, Set<Expression> predicates, Set<Variable> varsDeclaredByOrigSD) throws OptimizerException {
    Variable var = varsDeclaredByOrigSD.iterator().next();
    logger.finer(optimizerHeaderString() + "Performing early selection transformation for " + origSD + " declaring variable " + var + " (" + var.get_name() + ") with predicates " + predicates);
    Expression newCombinedConstraint = createConjunction(new ArrayList<Expression>(predicates), varsDeclaredByOrigSD);
    SetComprehension newSetComp = syntaxgraph.createSetComprehension();
    Declaration newDecl = syntaxgraph.createDeclaration();
    SimpleDeclaration newInnerSD = syntaxgraph.createSimpleDeclaration();
    Set<Variable> undeclaredVars = collectUndeclaredVariablesBelow(newCombinedConstraint);
    if (undeclaredVars.size() != 1) {
      OptimizerException ex = new OptimizerException("undeclaredVars = " + undeclaredVars + " has size different form 1.");
      logger.throwing(getClass().getName(), "movePredicatesToOneVarSimpleDeclaration", ex);
      throw ex;
    }
    Variable newInnerVar = undeclaredVars.iterator().next();
    origSD.getFirstIsTypeExprOfIncidence(EdgeDirection.IN).setOmega(newInnerSD);
    syntaxgraph.createIsTypeExprOfDeclaration(newSetComp, origSD);
    syntaxgraph.createIsCompDeclOf(newDecl, newSetComp);
    syntaxgraph.createIsSimpleDeclOf(newInnerSD, newDecl);
    syntaxgraph.createIsDeclaredVarOf(newInnerVar, newInnerSD);
    syntaxgraph.createIsConstraintOf(newCombinedConstraint, newDecl);
    syntaxgraph.createIsCompResultDefOf(newInnerVar, newSetComp);
    for (Expression exp : predicates) {
      removeExpressionFromOriginalConstraint(exp, origSD.getFirstIsSimpleDeclOfIncidence().getOmega());
    }
  }

  private void removeExpressionFromOriginalConstraint(Expression exp, Declaration origDecl) throws OptimizerException {
    if (exp.getFirstIsConstraintOfIncidence(EdgeDirection.OUT) != null) {
      exp.getFirstIsConstraintOfIncidence(EdgeDirection.OUT).delete();
      OptimizerUtility.deleteOrphanedVerticesBelow(exp, new HashSet<Vertex>());
      return;
    }
    ArrayList<Edge> upEdges = new ArrayList<Edge>();
    for (Edge e : exp.incidences(EdgeDirection.OUT)) {
      if ((e.getOmega() instanceof FunctionApplication) && existsForwardPathExcludingOtherTargetClassVertices(e, origDecl)) {
        FunctionApplication father = (FunctionApplication) e.getOmega();
        if (OptimizerUtility.isAnd(father)) {
          upEdges.add(e);
        }
      }
    }
    for (Edge upEdge : upEdges) {
      if (!upEdge.isValid()) {
        continue;
      }
      FunctionApplication funApp = (FunctionApplication) upEdge.getOmega();
      if (funApp == null) {
        throw new OptimizerException("Something\'s pretty wrong. upEdge.getOmega() returned null!! upEdge = " + upEdge + ".");
      }
      Expression otherArg = null;
      for (IsArgumentOf inc : funApp.getIsArgumentOfIncidences(EdgeDirection.IN)) {
        if (inc.getNormalEdge() != upEdge.getNormalEdge()) {
          otherArg = inc.getAlpha();
        }
      }
      ArrayList<Edge> funAppEdges = new ArrayList<Edge>();
      for (Edge funAppEdge : funApp.incidences(EdgeDirection.OUT)) {
        funAppEdges.add(funAppEdge);
      }
      for (Edge fae : funAppEdges) {
        fae.setAlpha(otherArg);
      }
      OptimizerUtility.deleteOrphanedVerticesBelow(funApp, new HashSet<Vertex>());
    }
  }

  /**
	 * Collects all edges running out of the given Variable, which represent
	 * accesses. Basically, those are all outgoing edges except IsDeclaredVarOf,
	 * IsBoundVarOf and IsVarOf edges.
	 *
	 * @param var
	 *            a Variable
	 * @return all edges running out of the given Variable representing variable
	 *         accesses.
	 */
  private Set<Edge> collectVariableAccessEdges(Variable var) {
    HashSet<Edge> edges = new HashSet<Edge>();
    for (Edge e : var.incidences(EdgeDirection.OUT)) {
      if ((e instanceof IsDeclaredVarOf) || (e instanceof IsBoundVarOf) || (e instanceof IsVarOf)) {
        continue;
      }
      edges.add(e);
    }
    return edges;
  }

  private Expression createConjunction(List<Expression> predicates, Set<Variable> varsToBeCopied, HashMap<Variable, Variable> copiedVars) {
    if (predicates.size() == 1) {
      return (Expression) copySubgraph(predicates.get(0), syntaxgraph, varsToBeCopied, copiedVars);
    }
    FunctionApplication funApp = syntaxgraph.createFunctionApplication();
    FunctionId funId = OptimizerUtility.findOrCreateFunctionId("and", syntaxgraph);
    syntaxgraph.createIsFunctionIdOf(funId, funApp);
    syntaxgraph.createIsArgumentOf((Expression) copySubgraph(predicates.get(0), syntaxgraph, varsToBeCopied, copiedVars), funApp);
    syntaxgraph.createIsArgumentOf(createConjunction(predicates.subList(1, predicates.size()), varsToBeCopied, copiedVars), funApp);
    return funApp;
  }

  /**
	 * Given a {@link List} of {@link Expression}s build a new conjunction (AND-
	 * {@link FunctionApplication}) that combines all the predicate
	 * {@link Expression} in the list. This will be done by copying the vertices
	 * with exceptions for {@link FunctionId}s (never copied) and
	 * {@link Variable}s (only those in <code>varsToBeCopied</code> will be
	 * copied ONCE).
	 *
	 * @param predicates
	 *            a {@link List} of {@link Expression}s
	 * @param varsToBeCopied
	 *            a {@link Set} of {@link Variable}s that shall be copied ONCE.
	 *            (After a variable was copied, the one and only copy will be
	 *            reused instead of the original {@link Variable}.)
	 * @return a AND-{@link FunctionApplication} that combines all
	 *         {@link Expression}s in <code>predicates</code>, or a copy of the
	 *         {@link Expression} in <code>predicates</code> if that contains
	 *         only one {@link Expression}
	 */
  private Expression createConjunction(List<Expression> predicates, Set<Variable> varsToBeCopied) {
    return createConjunction(predicates, varsToBeCopied, new HashMap<Variable, Variable>());
  }

  /**
	 * Find all {@link Expression}s below <code>exp</code> that can be moved and
	 * return them.
	 *
	 * An {@link Expression} is considered movable if it needs only
	 * {@link Variable}s that are locally declared in one
	 * {@link SimpleDeclaration} and this {@link SimpleDeclaration} is not the
	 * only one in the parent {@link Declaration}.
	 *
	 * @param exp
	 *            the {@link Expression} below which to look for movable
	 *            {@link Expression}s
	 */
  private HashMap<SimpleDeclaration, Set<Expression>> collectMovableExpressions(Expression exp) {
    HashMap<SimpleDeclaration, Set<Expression>> movableExpressions = new HashMap<SimpleDeclaration, Set<Expression>>();
    if ((exp instanceof FunctionApplication) && OptimizerUtility.isAnd((FunctionApplication) exp)) {
      FunctionApplication funApp = (FunctionApplication) exp;
      IsArgumentOf isArg = funApp.getFirstIsArgumentOfIncidence(EdgeDirection.IN);
      while (isArg != null) {
        for (Entry<SimpleDeclaration, Set<Expression>> entry : collectMovableExpressions(isArg.getAlpha()).entrySet()) {
          if (movableExpressions.containsKey(entry.getKey())) {
            movableExpressions.get(entry.getKey()).addAll(entry.getValue());
          } else {
            movableExpressions.put(entry.getKey(), entry.getValue());
          }
        }
        isArg = isArg.getNextIsArgumentOfIncidence(EdgeDirection.IN);
      }
      return movableExpressions;
    }
    SimpleDeclaration sd = findSimpleDeclarationThatDeclaresAllNeededLocalVariables(exp);
    if (sd != null) {
      Declaration parent = sd.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT).getOmega();
      if ((collectSimpleDeclarationsOf(parent).size() > 1) || (OptimizerUtility.collectVariablesDeclaredBy(sd).size() > 1)) {
        if (movableExpressions.containsKey(sd)) {
          movableExpressions.get(sd).add(exp);
        } else {
          HashSet<Expression> predicates = new HashSet<Expression>();
          predicates.add(exp);
          movableExpressions.put(sd, predicates);
        }
      }
    }
    return movableExpressions;
  }

  /**
	 * Find the {@link SimpleDeclaration} that declares all local
	 * {@link Variable}s the {@link Expression} <code>exp</code> needs. If
	 * <code>exp</code> doesn't need any variables or such an
	 * {@link SimpleDeclaration} doesn't exist, return <code>null</code>.
	 *
	 * @param exp
	 *            an {@link Expression}
	 * @return the {@link SimpleDeclaration} that declares all local
	 *         {@link Variable}s the {@link Expression} <code>exp</code> needs
	 *         or <code>null</code>, if such a {@link SimpleDeclaration} doesn't
	 *         exist.
	 */
  private SimpleDeclaration findSimpleDeclarationThatDeclaresAllNeededLocalVariables(Expression exp) {
    Set<Variable> neededVars = collectNeededLocalVariables(exp);
    SimpleDeclaration sd = null, oldSd = null;
    for (Variable var : neededVars) {
      sd = var.getFirstIsDeclaredVarOfIncidence().getOmega();
      if ((oldSd != null) && (sd != oldSd)) {
        return null;
      }
      oldSd = sd;
    }
    return sd;
  }

  /**
	 * @param exp
	 *            the {@link Expression} you want to calculate the needed local
	 *            {@link Variable}s for. A {@link Variable} is local if it's
	 *            declared by one of the {@link SimpleDeclaration}s of the
	 *            nearest {@link Declaration} above <code>exp</code>.
	 * @return a {@link Set} of {@link Variable}s the {@link Expression}
	 *         <code>exp</code> needs and that are declared by the nearest
	 *         {@link Declaration} above <code>exp</code>.
	 */
  private Set<Variable> collectNeededLocalVariables(Expression exp) {
    Set<Variable> neededVars = OptimizerUtility.collectInternallyDeclaredVariablesBelow(exp);
    Set<Variable> neededLocalVars = new HashSet<Variable>();
    Declaration localDecl = findNearestDeclarationAbove(exp);
    for (SimpleDeclaration sd : collectSimpleDeclarationsOf(localDecl)) {
      for (Variable var : neededVars) {
        IsDeclaredVarOf inc = sd.getFirstIsDeclaredVarOfIncidence();
        while (inc != null) {
          if (inc.getAlpha() == var) {
            neededLocalVars.add(var);
          }
          inc = inc.getNextIsDeclaredVarOfIncidence();
        }
      }
    }
    return neededLocalVars;
  }

  /**
	 * @param edge
	 *            the start {@link Edge}
	 * @param target
	 *            the target {@link Vertex}
	 * @return <code>true</code> if there's a forward directed path from
	 *         <code>edge</code> to <code>target</code> with no other vertices
	 *         of <code>target</code>'s class in between, <code>false</code>
	 *         otherwise
	 */
  private boolean existsForwardPathExcludingOtherTargetClassVertices(Edge edge, Vertex target) {
    Vertex omega = edge.getOmega();
    if (omega == target) {
      return true;
    }
    if (omega.getAttributedElementClass().getSchemaClass() == target.getAttributedElementClass().getSchemaClass()) {
      return false;
    }
    for (Edge e : omega.incidences(EdgeDirection.OUT)) {
      if (existsForwardPathExcludingOtherTargetClassVertices(e, target)) {
        return true;
      }
    }
    return false;
  }

  /**
	 * Collect all {@link SimpleDeclaration}s of <code>decl</code> in a
	 * {@link List}.
	 *
	 * @param decl
	 *            a {@link Declaration}
	 * @return a {@link List} of all {@link SimpleDeclaration}s that are part of
	 *         <code>decl</code>
	 */
  private List<SimpleDeclaration> collectSimpleDeclarationsOf(Declaration decl) {
    ArrayList<SimpleDeclaration> sds = new ArrayList<SimpleDeclaration>();
    for (IsSimpleDeclOf inc : decl.getIsSimpleDeclOfIncidences(EdgeDirection.IN)) {
      sds.add(inc.getAlpha());
    }
    return sds;
  }

  /**
	 * Collect the {@link Variable}s that have no outgoing
	 * {@link IsDeclaredVarOf} edges and are located below <code>v</code>.
	 *
	 * @param vertex
	 *            the root {@link Vertex} below which to look for undeclared
	 *            {@link Variable}s
	 * @return a {@link Set} of {@link Variable}s that have no outgoing
	 *         {@link IsDeclaredVarOf} edges and are located below
	 *         <code>v</code>
	 */
  private Set<Variable> collectUndeclaredVariablesBelow(Vertex vertex) {
    HashSet<Variable> undeclaredVars = new HashSet<Variable>();
    for (Variable var : OptimizerUtility.collectInternallyDeclaredVariablesBelow(vertex)) {
      if (var.getFirstIsDeclaredVarOfIncidence(EdgeDirection.OUT) == null) {
        undeclaredVars.add(var);
      }
    }
    return undeclaredVars;
  }

  /**
	 * Makes a deep copy of the subgraph given by <code>origVertex</code>. For
	 * each {@link Vertex} in that subgraph a new {@link Vertex} of the same
	 * type will be created, likewise for the {@link Edge}s. As an exception to
	 * that rule, {@link Identifier}s other than {@link Variable}s won't be
	 * copied. For {@link Variable}s it's quite complicated. If a
	 * {@link Variable} is in <code>variablesToBeCopied</code> it will be copied
	 * ONCE. After that the one and only copy is used instead of creating a new
	 * copy. That's what <code>copiedVarMap</code> is for. So normally you'd
	 * provide an empty {@link HashMap}.
	 *
	 * @param origVertex
	 *            the root {@link Vertex} of the subgraph to be copied
	 * @param graph
	 *            the {@link Graph} where <code>origVertex</code> is part of
	 * @param variablesToBeCopied
	 *            a set of {@link Variable}s that should be copied ONCE
	 * @param copiedVarMap
	 *            a {@link HashMap} form the original {@link Variable} to its
	 *            one and only copy
	 * @return the root {@link Vertex} of the copy
	 */
  private Vertex copySubgraph(Vertex origVertex, Greql2 graph, Set<Variable> variablesToBeCopied, HashMap<Variable, Variable> copiedVarMap) {
    if ((origVertex instanceof Identifier) && !(origVertex instanceof Variable)) {
      return origVertex;
    }
    if (origVertex instanceof Variable) {
      if (copiedVarMap.containsKey(origVertex)) {
        return copiedVarMap.get(origVertex);
      }
      if (!variablesToBeCopied.contains(origVertex)) {
        return origVertex;
      }
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/1599d6b3faa82c01a998c961c15a55304d634e59/src/de/uni_koblenz/jgralab/greql2/optimizer/EarlySelectionOptimizer.java/left.java
    Class<? extends Vertex> vertexClass = origVertex.getAttributedElementClass().getSchemaClass();
=======
>>>>>>> Unknown file: This is a bug in JDime.

    Vertex topVertex = graph.createVertex(origVertex.getAttributedElementClass());
    copyAttributes(origVertex, topVertex);
    if (topVertex instanceof Variable) {
      Variable newVar = (Variable) topVertex;
      newVar.set_name("_" + newVar.get_name());
      copiedVarMap.put((Variable) origVertex, newVar);
    }
    Edge origEdge = origVertex.getFirstIncidence(EdgeDirection.IN);
    Vertex subVertex;
    while (origEdge != null) {
      subVertex = copySubgraph(origEdge.getAlpha(), graph, variablesToBeCopied, copiedVarMap);

<<<<<<< /usr/src/app/output/jgralab/jgralab/1599d6b3faa82c01a998c961c15a55304d634e59/src/de/uni_koblenz/jgralab/greql2/optimizer/EarlySelectionOptimizer.java/left.java
      Class<? extends Edge> edgeClass = origEdge.getAttributedElementClass().getSchemaClass();
=======
>>>>>>> Unknown file: This is a bug in JDime.

      graph.createEdge(origEdge.getAttributedElementClass(), subVertex, topVertex);
      origEdge = origEdge.getNextIncidence(EdgeDirection.IN);
    }
    return topVertex;
  }

  /**
	 * Copy the attribute values of <code>from</code> to <code>to</code>. The
	 * types of the given {@link AttributedElement}s have to be equal.
	 *
	 * @param from
	 *            an {@link AttributedElement}
	 * @param to
	 *            another {@link AttributedElement} whose runtime type equals
	 *            <code>from</code>'s type.
	 */
  private void copyAttributes(AttributedElement<?, ?> from, AttributedElement<?, ?> to) {
    for (Attribute attr : from.getAttributedElementClass().getAttributeList()) {
      to.setAttribute(attr.getName(), from.getAttribute(attr.getName()));
    }
  }
}