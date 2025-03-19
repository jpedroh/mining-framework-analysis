package org.btrplace.scheduler.choco.transition;
import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.VMState;
import org.btrplace.model.view.network.Network;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.plan.event.Action;
import org.btrplace.plan.event.MigrateVM;
import org.btrplace.plan.event.SubstitutedVMEvent;
import org.btrplace.scheduler.SchedulerException;
import org.btrplace.scheduler.choco.ReconfigurationProblem;
import org.btrplace.scheduler.choco.Slice;
import org.btrplace.scheduler.choco.SliceBuilder;
import org.btrplace.scheduler.choco.duration.DurationEvaluators;
import org.btrplace.scheduler.choco.extensions.FastIFFEq;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Arithmetic;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LCF;
import org.chocosolver.solver.constraints.Operator;
import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.variables.*;

/**
 * Model an action that allow a running VM to be relocate elsewhere if necessary.
 * The relocation can be performed through a live-migration or a re-instantiation.
 * The re-instantiation consists in forging a new VM having the same characteristics
 * and launching it on the destination node. Once this new VM has been launched, the
 * original VM is shut down. Such a relocation n may be faster than a migration-based
 * one while being less aggressive for the network. However, the VM must be able to
 * be cloned from a template.
 * <p>
 * If the relocation is performed with a live-migration, a {@link MigrateVM} action
 * will be generated. If the relocation is performed through a re-instantiation, a {@link ForgeVM},
 * a {@link BootVM}, and a {@link ShutdownVM} actions are generated.
 * <p>
 * To relocate the VM using a re-instantiation, the VM must first have an attribute {@code clone}
 * set to {@code true}. The re-instantiation duration is then estimated. If it is shorter than
 * the migration duration, then re-instantiation will be preferred.
 * <p>
 *
 * @author Vincent Kherbache
 */
public class RelocatableVM implements KeepRunningVM {
  public static final String PREFIX = "relocatable(";

  public static final String PREFIX_STAY = "stayRunningOn(";

  private final VM vm;

  private Slice cSlice;

  private 
<<<<<<< /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/choco/src/main/java/org/btrplace/scheduler/choco/transition/RelocatableVM.java/left.java
  Slice
=======
  Task
>>>>>>> /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/choco/src/main/java/org/btrplace/scheduler/choco/transition/RelocatableVM.java/right.java
   
<<<<<<< /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/choco/src/main/java/org/btrplace/scheduler/choco/transition/RelocatableVM.java/left.java
  dSlice
=======
  migrationTask
>>>>>>> /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/choco/src/main/java/org/btrplace/scheduler/choco/transition/RelocatableVM.java/right.java
  ;

  private ReconfigurationProblem rp;

  private BoolVar state;

  private BoolVar stay;

  private IntVar duration;

  private IntVar start;

  private IntVar end;

  private IntVar bandwidth;

  private Node src;

  private boolean manageable = true;

  private boolean postCopy = false;

  /**
     * The relocation method. 0 for migration, 1 for relocation.
     */
  private BoolVar doReinstantiation;

  /**
     * Make a new model.
     *
     * @param p the RP to use as a basis.
     * @param e the VM managed by the action
     * @throws org.btrplace.scheduler.SchedulerException if an error occurred
     */
  public RelocatableVM(ReconfigurationProblem p, VM e) throws SchedulerException {
    vm = e;
    rp = p;
    src = rp.getSourceModel().getMapping().getVMLocation(e);
    Solver s = rp.getSolver();
    Model mo = rp.getSourceModel();
    start = rp.getStart();
    end = rp.getStart();
    duration = VariableFactory.zero(s);
    state = VariableFactory.one(s);
    if (!p.getManageableVMs().contains(e)) {
      stay = VariableFactory.one(s);
      doReinstantiation = VariableFactory.zero(s);
      manageable = false;
      IntVar host = rp.makeCurrentHost(vm, PREFIX_STAY, vm, ").host");
      cSlice = new SliceBuilder(rp, vm, PREFIX_STAY, vm.toString(), ").cSlice").setHoster(host).setEnd(rp.makeUnboundedDuration(PREFIX_STAY, vm, ").cSlice_end")).build();
      dSlice = new SliceBuilder(rp, vm, PREFIX_STAY, vm, ").dSlice").setHoster(host).setStart(cSlice.getEnd()).build();
      s.post(new Arithmetic(cSlice.getEnd(), Operator.LE, rp.getEnd()));
      return;
    }
    stay = VF.bool(vm + "stay", s);
    cSlice = new SliceBuilder(rp, vm, PREFIX, vm, ").cSlice").setHoster(rp.getNode(rp.getSourceModel().getMapping().getVMLocation(vm))).setEnd(rp.makeUnboundedDuration(PREFIX, vm, ").cSlice_end")).build();
    dSlice = new SliceBuilder(rp, vm, PREFIX, vm, ").dSlice").setStart(rp.makeUnboundedDuration(PREFIX, vm, ").dSlice_start")).build();
    s.post(new Arithmetic(cSlice.getEnd(), Operator.LE, rp.getEnd()));
    start = dSlice.getStart();
    end = cSlice.getEnd();
    DurationEvaluators dev = rp.getDurationEvaluators();
    int migrateDuration = dev.evaluate(rp.getSourceModel(), MigrateVM.class, vm);
    int bootDuration = dev.evaluate(rp.getSourceModel(), org.btrplace.plan.event.BootVM.class, vm);
    int forgeD = p.getDurationEvaluators().evaluate(p.getSourceModel(), org.btrplace.plan.event.ForgeVM.class, vm);
    int reInstantiateDuration = bootDuration + forgeD;
    reInstantiateDuration = forgeD;
    Network network = Network.get(mo);
    IntVar migrationDuration;
    if (network != null) {
      postCopy = mo.getAttributes().get(vm, "postCopy", false);
      migrationDuration = p.makeUnboundedDuration(PREFIX, vm, ").duration");
      bandwidth = VF.bounded(PREFIX + vm + ").bandwidth", 0, Integer.MAX_VALUE / 100, s);
    } else {
      migrationDuration = VariableFactory.enumerated(rp.makeVarLabel(PREFIX, vm, ").duration"), new int[] { 0, migrateDuration }, s);
      bandwidth = null;
    }
    if (mo.getAttributes().get(vm, "clone", false) && mo.getAttributes().isSet(vm, "template")) {
      doReinstantiation = VariableFactory.bool(rp.makeVarLabel("relocation_method(", vm, ")"), s);
      duration = VariableFactory.bounded(rp.makeVarLabel(PREFIX, vm, ").duration"), Math.min(migrationDuration.getLB(), reInstantiateDuration), Math.max(migrationDuration.getUB(), reInstantiateDuration), s);
      LCF.ifThenElse(LCF.or(new Arithmetic(doReinstantiation, Operator.EQ, 0), new Arithmetic(migrationDuration, Operator.LT, reInstantiateDuration)), new Arithmetic(duration, Operator.EQ, migrationDuration), new Arithmetic(duration, Operator.EQ, reInstantiateDuration));
      IntVar time = VariableFactory.enumerated(rp.makeVarLabel(doReinstantiation.getName(), " * ", forgeD), 0, forgeD, s);
      s.post(IntConstraintFactory.times(doReinstantiation, forgeD, time));
      s.post(new Arithmetic(start, Operator.GE, time));
      s.post(new FastIFFEq(doReinstantiation, duration, reInstantiateDuration));
    } else {
      doReinstantiation = VariableFactory.zero(s);
      duration = migrationDuration;
    }
    s.post(new FastIFFEq(stay, dSlice.getHoster(), cSlice.getHoster().getValue()));
    s.post(new FastIFFEq(stay, duration, 0));
    migrationTask = VariableFactory.task(start, duration, end);
  }

  public Task getMigrationTask() {
    return migrationTask;
  }

  private static String prettyMethod(IntVar method) {
    if (method.isInstantiatedTo(0)) {
      return "migration";
    } else {
      if (method.isInstantiatedTo(1)) {
        return "re-instantiation";
      }
    }
    return "(migration || re-instantiation)";
  }

  @Override public boolean isManaged() {
    return manageable;
  }

  @Override public boolean insertActions(Solution s, ReconfigurationPlan plan) {
    DurationEvaluators dev = rp.getDurationEvaluators();
    if (!s.getIntVal(cSlice.getHoster()).equals(s.getIntVal(dSlice.getHoster()))) {
      assert s.getIntVal(stay) == 0;
      Action a;
      Node dst = rp.getNode(s.getIntVal(dSlice.getHoster()));
      if (s.getIntVal(doReinstantiation) == 0) {
        int st = s.getIntVal(getStart());
        int ed = s.getIntVal(getEnd());
        if (getBandwidth() != null) {
          a = new MigrateVM(vm, src, dst, st, ed, s.getIntVal(getBandwidth()));
        } else {
          a = new MigrateVM(vm, src, dst, st, ed);
        }
        boolean b = plan.add(a);
        assert b;
      } else {
        VM newVM = rp.cloneVM(vm);
        if (newVM == null) {
          rp.getLogger().error("Unable to get a new int to plan the re-instantiate of VM {}", vm);
          return false;
        }
        org.btrplace.plan.event.ForgeVM fvm = new org.btrplace.plan.event.ForgeVM(newVM, s.getIntVal(dSlice.getStart()) - dev.evaluate(rp.getSourceModel(), org.btrplace.plan.event.ForgeVM.class, vm), s.getIntVal(dSlice.getStart()));
        boolean b = plan.add(fvm);
        assert b;
        int endForging = fvm.getEnd();
        org.btrplace.plan.event.BootVM boot = new org.btrplace.plan.event.BootVM(newVM, dst, endForging, endForging + dev.evaluate(rp.getSourceModel(), org.btrplace.plan.event.BootVM.class, newVM));
        boot.addEvent(Action.Hook.PRE, new SubstitutedVMEvent(vm, newVM));
        return plan.add(boot) && plan.add(new org.btrplace.plan.event.ShutdownVM(vm, src, boot.getEnd(), s.getIntVal(cSlice.getEnd())));
      }
    }
    return true;
  }

  public IntVar getBandwidth() {
    return bandwidth;
  }

  public boolean usesPostCopy() {
    return postCopy;
  }

  @Override public VM getVM() {
    return vm;
  }

  @Override public IntVar getStart() {
    return start;
  }

  @Override public IntVar getEnd() {
    return end;
  }

  @Override public IntVar getDuration() {
    return duration;
  }

  @Override public Slice getCSlice() {
    return cSlice;
  }

  @Override public Slice getDSlice() {
    return dSlice;
  }

  @Override public BoolVar getState() {
    return state;
  }

  @Override public BoolVar isStaying() {
    return stay;
  }

  /**
     * Tells if the VM can be migrated or re-instantiated.
     *
     * @return a variable instantiated to {@code 0} for a migration based relocation or {@code 1}
     * for a re-instantiation based relocation
     */
  public IntVar getRelocationMethod() {
    return doReinstantiation;
  }

  @Override public String toString() {
    return "relocate(doReinstantiation=" + prettyMethod(doReinstantiation) + " ,vm=" + vm + " ,from=" + src + "(" + rp.getNode(src) + ")" + " ,to=" + (dSlice.getHoster().isInstantiated() ? rp.getNode(dSlice.getHoster().getValue()) : dSlice.getHoster().toString()) + ")";
  }

  @Override public VMState getSourceState() {
    return VMState.RUNNING;
  }

  @Override public VMState getFutureState() {
    return VMState.RUNNING;
  }

  public static class Builder extends VMTransitionBuilder {
    /**
         * New builder
         */
    public Builder() {
      super("relocatable", VMState.RUNNING, VMState.RUNNING);
    }

    @Override public VMTransition build(ReconfigurationProblem r, VM v) throws SchedulerException {
      return new RelocatableVM(r, v);
    }
  }
}