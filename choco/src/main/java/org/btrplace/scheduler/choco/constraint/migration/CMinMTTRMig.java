package org.btrplace.scheduler.choco.constraint.migration;
import org.btrplace.model.Instance;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.constraint.migration.MinMTTRMig;
import org.btrplace.scheduler.SchedulerException;
import org.btrplace.scheduler.choco.Parameters;
import org.btrplace.scheduler.choco.ReconfigurationProblem;
import org.btrplace.scheduler.choco.SliceUtils;
import org.btrplace.scheduler.choco.constraint.mttr.MovementGraph;
import org.btrplace.scheduler.choco.constraint.mttr.MyInputOrder;
import org.btrplace.scheduler.choco.transition.NodeTransition;
import org.btrplace.scheduler.choco.constraint.mttr.OnStableNodeFirst;
import org.btrplace.scheduler.choco.constraint.mttr.StartOnLeafNodes;
import org.btrplace.scheduler.choco.transition.*;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.strategy.ISF;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.search.strategy.strategy.StrategiesSequencer;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * An objective that minimizes the time to repair a non-viable model involving a set of migrations.
 *
 * @author Vincent Kherbache
 */
public class CMinMTTRMig implements org.btrplace.scheduler.choco.constraint.CObjective {
  private ReconfigurationProblem rp;

  private List<Constraint> costConstraints;

  private boolean costActivated = false;

  /**
     * Make a new Objective.
     */
  public CMinMTTRMig(MinMTTRMig m) {
    costConstraints = new ArrayList<>();
  }

  public CMinMTTRMig() {
    this(null);
  }

  @Override public boolean inject(Parameters ps, ReconfigurationProblem rp) throws SchedulerException {
    this.rp = rp;
    List<IntVar> endVars = new ArrayList<>();
    for (VMTransition m : rp.getVMActions()) {
      endVars.add(m.getEnd());
    }
    for (NodeTransition m : rp.getNodeActions()) {
      endVars.add(m.getEnd());
    }
    IntVar[] costs = endVars.toArray(new IntVar[endVars.size()]);
    IntVar cost = VariableFactory.bounded(rp.makeVarLabel("costEndVars"), 0, Integer.MAX_VALUE / 100, rp.getSolver());
    costConstraints.add(IntConstraintFactory.sum(costs, cost));
    rp.setObjective(true, cost);
    injectSchedulingHeuristic(cost);
    postCostConstraints();
    return true;
  }

  /**
     * Inject a specific scheduling heuristic to the solver.
     *
     * @param cost  the global cost variable.
     */
  private void injectSchedulingHeuristic(IntVar cost) {
    List<AbstractStrategy<?>> strategies = new ArrayList<>();
    List<IntVar> endVars = new ArrayList<>();
    for (Node n : rp.getNodes()) {
      if (rp.getNodeAction(n) instanceof BootableNode) {
        endVars.add(rp.getNodeAction(n).getEnd());
      }
    }
    if (!endVars.isEmpty()) {
      strategies.add(ISF.custom(ISF.minDomainSize_var_selector(), ISF.min_value_selector(), ISF.split(), endVars.toArray(new IntVar[endVars.size()])));
    }
    endVars.clear();
    MovementGraph gr = new MovementGraph(rp);
    OnStableNodeFirst schedHeuristic = new OnStableNodeFirst(rp, this);
    strategies.add(new IntStrategy(SliceUtils.extractStarts(TransitionUtils.getDSlices(rp.getVMActions())), new StartOnLeafNodes(rp, gr), new IntDomainMin()));
    strategies.add(new IntStrategy(schedHeuristic.getScope(), schedHeuristic, new IntDomainMin()));
    for (VMTransition a : rp.getVMActions()) {
      endVars.add(a.getEnd());
    }
    if (!endVars.isEmpty()) {
      strategies.add(ISF.custom(ISF.minDomainSize_var_selector(), ISF.min_value_selector(), ISF.split(), endVars.toArray(new IntVar[endVars.size()])));
    }
    endVars.clear();
    for (Node n : rp.getNodes()) {
      if (rp.getNodeAction(n) instanceof ShutdownableNode) {
        endVars.add(rp.getNodeAction(n).getEnd());
      }
    }
    if (!endVars.isEmpty()) {
      strategies.add(ISF.custom(ISF.minDomainSize_var_selector(), ISF.min_value_selector(), ISF.split(), endVars.toArray(new IntVar[endVars.size()])));
    }
    strategies.add(new IntStrategy(new IntVar[] { rp.getEnd(), cost }, new MyInputOrder<>(rp.getSolver(), this), new IntDomainMin()));
    rp.getSolver().getSearchLoop().set(new StrategiesSequencer(rp.getSolver().getEnvironment(), strategies.toArray(new AbstractStrategy[strategies.size()])));
  }

  @Override public void postCostConstraints() {
    if (!costActivated) {
      rp.getLogger().debug("Post the cost-oriented constraints");
      costActivated = true;
      Solver s = rp.getSolver();
      costConstraints.forEach(s::post);
    }
  }

  @Override public Set<VM> getMisPlacedVMs(Instance i) {
    return Collections.emptySet();
  }
}