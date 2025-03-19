package org.btrplace.examples;
import org.btrplace.model.DefaultModel;
import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.constraint.MaxOnline;
import org.btrplace.model.constraint.Offline;
import org.btrplace.model.constraint.SatConstraint;
import org.btrplace.model.view.ShareableResource;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.scheduler.SchedulerException;
import org.btrplace.scheduler.choco.ChocoScheduler;
import org.btrplace.scheduler.choco.DefaultChocoScheduler;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class Decommissionning implements Example {
  @Override public boolean run() {
    int ratio = 1;
    int nbPCPUs = 4;
    int nbNodes = 2;
    Model mo = new DefaultModel();
    for (int i = 0; i < nbNodes; i++) {
      Node n = mo.newNode();
      mo.getMapping().addOnlineNode(n);
      for (int j = 0; j < ratio * nbPCPUs; j++) {
        VM v = mo.newVM();
        mo.getMapping().addRunningVM(v, n);
      }
    }
    ShareableResource rc = new ShareableResource("cpu", 8, 1);
    mo.attach(rc);
    for (int i = 0; i < nbNodes; i++) {
      Node n = mo.newNode();
      mo.getMapping().addOfflineNode(n);
      rc.setCapacity(n, 10);
    }
    List<SatConstraint> cstrs = new ArrayList<>();
    cstrs.addAll(Offline.newOffline(mo.getMapping().getOnlineNodes()));
    MaxOnline m = new MaxOnline(mo.getMapping().getAllNodes(), nbNodes + 1, true);
    cstrs.add(m);
    ChocoScheduler cra = new DefaultChocoScheduler();
    cra.setMaxEnd(3);
    try {
      cra.setVerbosity(1);
      ReconfigurationPlan p = cra.solve(mo, cstrs);
      System.out.println(p);
      System.out.println(cra.getStatistics());
    } catch (SchedulerException ex) {
      System.err.println(ex.getMessage());
      return false;
    }
    return true;
  }
}