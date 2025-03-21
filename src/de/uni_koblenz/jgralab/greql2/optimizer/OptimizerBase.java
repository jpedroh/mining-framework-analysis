package de.uni_koblenz.jgralab.greql2.optimizer;
import java.util.HashSet;
import java.util.Set;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.OptimizerException;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.impl.InternalEdge;

/**
 * Base class for all {@link Optimizer}s which defines some useful methods that
 * are needed in derived Classes.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class OptimizerBase implements Optimizer {
  protected String optimizerHeaderString() {
    return "*** " + this.getClass().getSimpleName() + ": ";
  }

  protected void recreateVertexEvaluators(GreqlEvaluator eval) {
    eval.createVertexEvaluators();
  }

  /**
	 * Put all edges going in or coming out of vertex <code>from</code> to
	 * vertex <code>to</code>. If there's already an edge of exactly that type
	 * between <code>from</code>'s that-vertex and <code>to</code>, then don't
	 * create a duplicate edge, unless <code>allowDuplicateEdges</code> is true.
	 * 
	 * @param from
	 *            the old vertex
	 * @param to
	 *            the new vertex
	 * @param allowDuplicateEdges
	 */
  protected void relink(Vertex from, Vertex to) {
    assert (from != null) && (to != null) : "Relinking null!";
    assert from != to : "Relinking from itself!";
    assert from.getM1Class() == to.getM1Class() : "Relinking different classes! from is " + from + ", to is " + to;
    assert from.isValid() && to.isValid() : "Relinking invalid vertices!";
    InternalEdge e = (InternalEdge) from.getFirstIncidence(EdgeDirection.IN);
    while (e != null) {
      InternalEdge newE = (InternalEdge) e.getNextIncidence(EdgeDirection.IN);
      e.setOmega(to);
      e = newE;
    }
    e = (InternalEdge) from.getFirstIncidence(EdgeDirection.OUT);
    while (e != null) {
      InternalEdge newE = (InternalEdge) e.getNextIncidence(EdgeDirection.OUT);
      e.setAlpha(to);
      e = newE;
    }
  }

  /**
	 * Check if <code>var1</code> is declared before <code>var2</code>. A
	 * {@link Variable} is declared before another variable, if it's declared in
	 * an outer {@link Declaration}, or if it's declared in the same
	 * {@link Declaration} but in a {@link SimpleDeclaration} that comes before
	 * the other {@link Variable}'s {@link SimpleDeclaration}, or if it's
	 * declared in the same {@link SimpleDeclaration} but is connected to that
	 * earlier (meaning its {@link IsDeclaredVarOf} edge comes before the
	 * other's).
	 * 
	 * Note that a {@link Variable} is never declared before itself.
	 * 
	 * @param var1
	 *            a {@link Variable}
	 * @param var2
	 *            a {@link Variable}
	 * @return <code>true</code> if <code>var1</code> is declared before
	 *         <code>var2</code>, <code>false</code> otherwise.
	 */
  protected boolean isDeclaredBefore(Variable var1, Variable var2) {
    if (var1 == var2) {
      return false;
    }
    IsBoundVarOf ibvo1 = var1.getFirstIsBoundVarOfIncidence();
    IsBoundVarOf ibvo2 = var2.getFirstIsBoundVarOfIncidence();
    if (ibvo1 != null) {
      if (ibvo2 == null) {
        return true;
      }
      Greql2Expression root = (Greql2Expression) ibvo1.getOmega();
      for (IsBoundVarOf ibvo : root.getIsBoundVarOfIncidences()) {
        ibvo = (IsBoundVarOf) ibvo.getNormalEdge();
        if (ibvo == ibvo1) {
          return true;
        } else {
          if (ibvo == ibvo2) {
            return false;
          }
        }
      }
      throw new OptimizerException("You must never come here...");
    } else {
      if (ibvo2 != null) {
        return false;
      }
    }
    SimpleDeclaration sd1 = (SimpleDeclaration) var1.getFirstIsDeclaredVarOfIncidence(EdgeDirection.OUT).getOmega();
    Declaration decl1 = (Declaration) sd1.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT).getOmega();
    SimpleDeclaration sd2 = (SimpleDeclaration) var2.getFirstIsDeclaredVarOfIncidence(EdgeDirection.OUT).getOmega();
    Declaration decl2 = (Declaration) sd2.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT).getOmega();
    if (decl1 == decl2) {
      if (sd1 == sd2) {
        IsDeclaredVarOf inc = sd1.getFirstIsDeclaredVarOfIncidence(EdgeDirection.IN);
        while (inc != null) {
          if (inc.getAlpha() == var1) {
            return true;
          }
          if (inc.getAlpha() == var2) {
            return false;
          }
          inc = inc.getNextIsDeclaredVarOfIncidence(EdgeDirection.IN);
        }
      } else {
        IsSimpleDeclOf inc = decl1.getFirstIsSimpleDeclOfIncidence(EdgeDirection.IN);
        while (inc != null) {
          if (inc.getAlpha() == sd1) {
            return true;
          }
          if (inc.getAlpha() == sd2) {
            return false;
          }
          inc = inc.getNextIsSimpleDeclOfIncidence(EdgeDirection.IN);
        }
      }
    } else {
      Vertex declParent1 = decl1.getFirstIncidence(EdgeDirection.OUT).getOmega();
      Vertex declParent2 = decl2.getFirstIncidence(EdgeDirection.OUT).getOmega();
      if (OptimizerUtility.isAbove(declParent1, declParent2)) {
        return true;
      } else {
        return false;
      }
    }
    throw new OptimizerException("No case matched in isDeclaredBefore(Variable, Variable)." + " That must not happen!");
  }

  /**
	 * Find the nearest {@link Declaration} above <code>vertex</code>.
	 * 
	 * @param vertex
	 *            a {@link Vertex}
	 * @return nearest {@link Declaration} above <code>vertex</code>
	 */
  protected Declaration findNearestDeclarationAbove(Vertex vertex) {
    if (vertex instanceof Declaration) {
      return (Declaration) vertex;
    }
    Declaration result = null;
    Edge inc = vertex.getFirstIncidence(EdgeDirection.OUT);
    while (inc != null) {
      result = findNearestDeclarationAbove(inc.getOmega());
      if (result != null) {
        return result;
      }
      inc = inc.getNextIncidence(EdgeDirection.OUT);
    }
    return null;
  }

  /**
	 * Split the given {@link SimpleDeclaration} so that there's one
	 * {@link SimpleDeclaration} that declares the {@link Variable}s in
	 * <code>varsToBeSplit</code> and one for the rest.
	 * 
	 * @param sd
	 *            the {@link SimpleDeclaration} to be split
	 * @param varsToBeSplit
	 *            a {@link Set} of {@link Variable}s that should have their own
	 *            {@link SimpleDeclaration}
	 * @return the newly created {@link SimpleDeclaration} declaring all
	 *         <code>varsToBeSplit</code>
	 */
  protected SimpleDeclaration splitSimpleDeclaration(SimpleDeclaration sd, Set<Variable> varsToBeSplit) {
    Greql2 syntaxgraph = (Greql2) sd.getGraph();
    Set<Variable> varsDeclaredBySD = OptimizerUtility.collectVariablesDeclaredBy(sd);
    if (varsDeclaredBySD.size() == varsToBeSplit.size()) {
      return sd;
    }
    Declaration parentDecl = (Declaration) sd.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT).getOmega();
    IsSimpleDeclOf oldEdge = sd.getFirstIsSimpleDeclOfIncidence();
    SimpleDeclaration newSD = syntaxgraph.createSimpleDeclaration();
    IsSimpleDeclOf newEdge = syntaxgraph.createIsSimpleDeclOf(newSD, parentDecl);
    syntaxgraph.createIsTypeExprOfDeclaration((Expression) sd.getFirstIsTypeExprOfDeclarationIncidence(EdgeDirection.IN).getAlpha(), newSD);
    newEdge.getReversedEdge().putIncidenceAfter(oldEdge.getReversedEdge());
    for (Variable var : varsToBeSplit) {
      IsDeclaredVarOf inc = sd.getFirstIsDeclaredVarOfIncidence(EdgeDirection.IN);
      HashSet<IsDeclaredVarOf> relinkIncs = new HashSet<IsDeclaredVarOf>();
      while (inc != null) {
        if (inc.getAlpha() == var) {
          relinkIncs.add(inc);
        }
        inc = inc.getNextIsDeclaredVarOfIncidence(EdgeDirection.IN);
      }
      for (IsDeclaredVarOf relinkEdge : relinkIncs) {
        ((InternalEdge) relinkEdge).setOmega(newSD);
      }
    }
    return newSD;
  }
}