package com.graphhopper.jsprit.core.problem.constraint;
import com.graphhopper.jsprit.core.algorithm.state.InternalStates;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.SizeDimension.SizeDimensionSign;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.Start;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;

/**
 * Ensures load constraint for inserting ServiceActivity.
 * <p>
 * <p>When using this, you need to use<br>
 *
 * @author schroeder
 */
public class ServiceLoadActivityLevelConstraint implements HardActivityConstraint {
  private RouteAndActivityStateGetter stateManager;

  private SizeDimension defaultValue;

  public ServiceLoadActivityLevelConstraint(RouteAndActivityStateGetter stateManager) {
    super();
    this.stateManager = stateManager;
    defaultValue = SizeDimension.Builder.newInstance().build();
  }

  @Override public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
    SizeDimension futureMaxLoad;
    SizeDimension prevMaxLoad;
    if (prevAct instanceof Start) {
      futureMaxLoad = stateManager.getRouteState(iFacts.getRoute(), InternalStates.MAXLOAD, SizeDimension.class);
      if (futureMaxLoad == null) {
        futureMaxLoad = defaultValue;
      }
      prevMaxLoad = stateManager.getRouteState(iFacts.getRoute(), InternalStates.LOAD_AT_BEGINNING, SizeDimension.class);
      if (prevMaxLoad == null) {
        prevMaxLoad = defaultValue;
      }
    } else {
      futureMaxLoad = stateManager.getActivityState(prevAct, InternalStates.FUTURE_MAXLOAD, SizeDimension.class);
      if (futureMaxLoad == null) {
        futureMaxLoad = defaultValue;
      }
      prevMaxLoad = stateManager.getActivityState(prevAct, InternalStates.PAST_MAXLOAD, SizeDimension.class);
      if (prevMaxLoad == null) {
        prevMaxLoad = defaultValue;
      }
    }
    if (newAct.getSize().sign() == SizeDimensionSign.POSITIVE) {
      if (!newAct.getSize().add(futureMaxLoad).isLessOrEqual(iFacts.getNewVehicle().getType().getCapacityDimensions())) {
        return ConstraintsStatus.NOT_FULFILLED;
      }
    }
    if (newAct.getSize().sign() != SizeDimensionSign.POSITIVE) {
      if (!newAct.getSize().abs().add(prevMaxLoad).isLessOrEqual(iFacts.getNewVehicle().getType().getCapacityDimensions())) {
        return ConstraintsStatus.NOT_FULFILLED_BREAK;
      }
    }
    return ConstraintsStatus.FULFILLED;
  }
}