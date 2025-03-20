package com.graphhopper.jsprit.core.problem.constraint;
import com.graphhopper.jsprit.core.algorithm.state.InternalStates;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.AbstractJob;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;

/**
 * Ensures that capacity constraint is met, i.e. that current load plus
 * new job size does not exceeds capacity of new vehicle.
 * <p>
 * <p>If job is neither Pickup, Delivery nor Service, it returns true.
 *
 * @author stefan
 */
public class ServiceLoadRouteLevelConstraint implements HardRouteConstraint {
  private RouteAndActivityStateGetter stateManager;


<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
  private SizeDimension defaultValue;
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public ServiceLoadRouteLevelConstraint(RouteAndActivityStateGetter stateManager) {
    super();
    this.stateManager = stateManager;

<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
    defaultValue = SizeDimension.Builder.newInstance().build();
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }

  @Override public boolean fulfilled(JobInsertionContext insertionContext) {
    SizeDimension maxLoadAtRoute = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.MAXLOAD, SizeDimension.class);
    maxLoadAtRoute = (maxLoadAtRoute != null) ? maxLoadAtRoute : Capacity.EMPTY;
    Capacity capacityOfNewVehicle = insertionContext.getNewVehicle().getType().getCapacityDimensions();
    if (!maxLoadAtRoute.isLessOrEqual(capacityOfNewVehicle)) {
      return false;
    }
    AbstractJob job = (AbstractJob) insertionContext.getJob();
    SizeDimension loadAtDepot = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_BEGINNING, Capacity.class);
    loadAtDepot = (loadAtDepot != null) ? loadAtDepot : Capacity.EMPTY;
    if (!(loadAtDepot.add(job.getSizeAtStart()).isLessOrEqual(capacityOfNewVehicle))) {
      return false;
    }
    Capacity loadAtEnd = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_END, Capacity.class);
    loadAtEnd = (loadAtEnd != null) ? loadAtEnd : Capacity.EMPTY;
    if (!(loadAtEnd.add(job.getSizeAtEnd()).isLessOrEqual(capacityOfNewVehicle))) {

<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
      SizeDimension loadAtDepot = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_BEGINNING, SizeDimension.class);
=======
      return false;
>>>>>>> /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/right.java
    } else {

<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
      if (insertionContext.getJob() instanceof Pickup || insertionContext.getJob() instanceof Service) {
        SizeDimension loadAtEnd = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_END, SizeDimension.class);
        if (loadAtEnd == null) {
          loadAtEnd = defaultValue;
        }
        if (!loadAtEnd.add(insertionContext.getJob().getSize()).isLessOrEqual(capacityDimensions)) {
          return false;
        }
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    }
    return true;
  }
}