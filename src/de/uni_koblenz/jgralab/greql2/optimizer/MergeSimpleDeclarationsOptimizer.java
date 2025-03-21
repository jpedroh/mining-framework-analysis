package de.uni_koblenz.jgralab.greql2.optimizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.CostModel;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Graph;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTargetExprOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOfDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.impl.InternalEdge;

/**
 * This {@link MergeSimpleDeclarationsOptimizer} finds and merges all
 * {@link SimpleDeclaration}s in the given syntaxgraph that are below the same
 * {@link Declaration} and share the same {@link Expression} on their
 * {@link IsTargetExprOf} edge. The {@link SimpleDeclaration} with the lowest ID
 * survives, all others are deleted and their {@link IsDeclaredVarOf} edges are
 * relocated to the surviving {@link SimpleDeclaration}.
 * 
 * @author ist@uni-koblenz.de
 */
public class MergeSimpleDeclarationsOptimizer extends Optimizer {
  private static Logger logger = JGraLab.getLogger(MergeSimpleDeclarationsOptimizer.class.getPackage().getName());

  private boolean anOptimizationWasDone = false;

  @Override public boolean isEquivalent(Optimizer optimizer) {
    if (optimizer instanceof MergeSimpleDeclarationsOptimizer) {
      return true;
    }
    return false;
  }

  @Override protected boolean optimize(Greql2Graph syntaxgraph, CostModel costModel) {
    anOptimizationWasDone = false;
    findAndMergeSimpleDeclarations(syntaxgraph);
    return anOptimizationWasDone;
  }

  /**
	 * Finds and merges all {@link SimpleDeclaration}s in the given syntaxgraph
	 * that are below the same {@link Declaration} and share the same
	 * {@link Expression} on their {@link IsTargetExprOf} edge. The
	 * {@link SimpleDeclaration} with the lowest ID survives, all others are
	 * deleted and their {@link IsDeclaredVarOf} edges are relocated to the
	 * surviving {@link SimpleDeclaration}.
	 * 
	 * @param syntaxgraph
	 *            a {@link Greql2} graph
	 */
  private void findAndMergeSimpleDeclarations(Greql2Graph syntaxgraph) {
    HashMap<String, ArrayList<SimpleDeclaration>> mergableSDMap = new HashMap<String, ArrayList<SimpleDeclaration>>();
    Declaration decl = syntaxgraph.getFirstDeclaration();
    while (decl != null) {
      IsSimpleDeclOf isSimpleDeclOf = decl.getFirstIsSimpleDeclOfIncidence(EdgeDirection.IN);
      while (isSimpleDeclOf != null) {
        SimpleDeclaration sDecl = (SimpleDeclaration) isSimpleDeclOf.getAlpha();
        String key = decl.getId() + "-" + sDecl.getFirstIsTypeExprOfIncidence(EdgeDirection.IN).getAlpha().getId();
        if (mergableSDMap.containsKey(key)) {
          mergableSDMap.get(key).add(sDecl);
        } else {
          ArrayList<SimpleDeclaration> simpleDecls = new ArrayList<SimpleDeclaration>();
          simpleDecls.add(sDecl);
          mergableSDMap.put(key, simpleDecls);
        }
        isSimpleDeclOf = isSimpleDeclOf.getNextIsSimpleDeclOfIncidence();
      }
      decl = decl.getNextDeclaration();
    }
    mergeSimpleDeclarations(mergableSDMap);
  }

  /**
	 * Merges the {@link SimpleDeclaration} given as the values of
	 * <code>mergableSDMap</code> if the order of variable declarations isn't
	 * changed by the merge.
	 * 
	 * @param mergableSDMap
	 */
  private void mergeSimpleDeclarations(HashMap<String, ArrayList<SimpleDeclaration>> mergableSDMap) {
    for (Entry<String, ArrayList<SimpleDeclaration>> e : mergableSDMap.entrySet()) {
      SimpleDeclaration survivor = e.getValue().get(0);
      Declaration decl = (Declaration) survivor.getFirstIsSimpleDeclOfIncidence().getOmega();
      IsSimpleDeclOf isSDOfSurvivor = survivor.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT);
      IsTypeExprOfDeclaration isTEODSurvivor = survivor.getFirstIsTypeExprOfDeclarationIncidence(EdgeDirection.IN);
      for (SimpleDeclaration s : e.getValue()) {
        IsSimpleDeclOf isSDOfS = s.getFirstIsSimpleDeclOfIncidence(EdgeDirection.OUT);
        if (isNextInIncidenceList(decl, isSDOfSurvivor, isSDOfS)) {
          logger.finer(optimizerHeaderString() + "Merging all variables of " + s + " into " + survivor + ".");
          while (s.getFirstIsDeclaredVarOfIncidence() != null) {
            ((InternalEdge) s.getFirstIsDeclaredVarOfIncidence()).setOmega(survivor);
          }
          OptimizerUtility.mergeSourcePositions(isSDOfS, isSDOfSurvivor);
          IsTypeExprOfDeclaration isTEODS = s.getFirstIsTypeExprOfDeclarationIncidence(EdgeDirection.IN);
          OptimizerUtility.mergeSourcePositions(isTEODS, isTEODSurvivor);
          s.delete();
          anOptimizationWasDone = true;
        } else {
          survivor = s;
        }
      }
    }
  }

  /**
	 * @param decl
	 * @param isSDOfSurvivor
	 * @param isSDOfS
	 * @return <code>true</code> if <code>isSDOfS</code> follows directly
	 *         <code>isSDOfSurvivor</code> in the incidence list of
	 *         <code>decl</code>, <code>false</code> otherwise
	 */
  private boolean isNextInIncidenceList(Declaration decl, IsSimpleDeclOf isSDOfSurvivor, IsSimpleDeclOf isSDOfS) {
    IsSimpleDeclOf edge = decl.getFirstIsSimpleDeclOfIncidence();
    while (edge != null) {
      if (edge.getNormalEdge() != isSDOfSurvivor) {
        edge = edge.getNextIsSimpleDeclOfIncidence();
        continue;
      }
      IsSimpleDeclOf nextEdge = edge.getNextIsSimpleDeclOfIncidence();
      if ((nextEdge != null) && (nextEdge.getNormalEdge() == isSDOfS)) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }
}