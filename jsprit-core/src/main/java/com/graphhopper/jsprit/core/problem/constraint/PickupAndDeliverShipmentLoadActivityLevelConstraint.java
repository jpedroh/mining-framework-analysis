package com.graphhopper.jsprit.core.problem.constraint;
import com.graphhopper.jsprit.core.algorithm.state.InternalStates;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;
import com.graphhopper.jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;

/**
 * Constraint that ensures capacity constraint at each activity.
 * <p>
 * <p>This is critical to consistently calculate pd-problems with capacity constraints. Critical means
 * that is MUST be visited. It also assumes that pd-activities are visited in the order they occur in a tour.
 *
 * @author schroeder
 */
public class PickupAndDeliverShipmentLoadActivityLevelConstraint implements HardActivityConstraint {
  private RouteAndActivityStateGetter stateManager;

  private SizeDimension defaultValue;

  /**
     * Constructs the constraint ensuring capacity constraint at each activity.
     * <p>
     * <p>This is critical to consistently calculate pd-problems with capacity constraints. Critical means
     * that is MUST be visited. It also assumes that pd-activities are visited in the order they occur in a tour.
     *
     * @param stateManager the stateManager
     */
  public PickupAndDeliverShipmentLoadActivityLevelConstraint(RouteAndActivityStateGetter stateManager) {
    super();
    this.stateManager = stateManager;
    defaultValue = SizeDimension.Builder.newInstance().build();
  }

  /**
     * Checks whether there is enough capacity to insert newAct between prevAct and nextAct.
     */
  @Override public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
    if (!(newAct instanceof JobActivity)) {
      return ConstraintsStatus.FULFILLED;
    }
    JobActivity newJobAct = (JobActivity) newAct;
    if (!(newJobAct.getJob() instanceof Shipment)) {
      return ConstraintsStatus.FULFILLED;
    }
    SizeDimension loadAtPrevAct;
    if (prevAct instanceof Start) {
      loadAtPrevAct = stateManager.getRouteState(iFacts.getRoute(), InternalStates.LOAD_AT_BEGINNING, SizeDimension.class);
      if (loadAtPrevAct == null) {
        loadAtPrevAct = defaultValue;
      }
    } else {
      loadAtPrevAct = stateManager.getActivityState(prevAct, InternalStates.LOAD, SizeDimension.class);
      if (loadAtPrevAct == null) {
        loadAtPrevAct = defaultValue;
      }
    }
    SizeDimension vehicleCapacityDimensions = iFacts.getNewVehicle().getType().getCapacityDimensions();
    if (newAct instanceof PickupActivityNEW) {
      SizeDimension newCapacity = loadAtPrevAct.add(newAct.getSize());
      if (!newCapacity.isLessOrEqual(vehicleCapacityDimensions)) {
        return ConstraintsStatus.NOT_FULFILLED;
      }
    }
    if (newAct instanceof DeliveryActivityNEW) {
      SizeDimension newCapacity = loadAtPrevAct.add(newAct.getSize().abs());
      if (!newCapacity.isLessOrEqual(vehicleCapacityDimensions)) {
        return ConstraintsStatus.NOT_FULFILLED_BREAK;
      }
    }
    return ConstraintsStatus.FULFILLED;
  }
}