package com.graphhopper.jsprit.core.problem.constraint;
import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.TransportTime;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingActivityCosts;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliveryActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.End;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import java.util.Collections;
import java.util.Map;

/**
 * Created by schroeder on 15/09/16.
 */
public class MaxTimeInVehicleConstraint implements HardActivityConstraint {
  private final VehicleRoutingProblem vrp;

  private final TransportTime transportTime;

  private final VehicleRoutingActivityCosts activityCosts;

  private final StateId minSlackId;

  private final StateId openJobsId;

  private final StateManager stateManager;

  public MaxTimeInVehicleConstraint(TransportTime transportTime, VehicleRoutingActivityCosts activityCosts, StateId minSlackId, StateManager stateManager, VehicleRoutingProblem vrp, StateId openJobsId) {
    this.transportTime = transportTime;
    this.minSlackId = minSlackId;
    this.stateManager = stateManager;
    this.activityCosts = activityCosts;
    this.vrp = vrp;
    this.openJobsId = openJobsId;
  }

  @Override public ConstraintsStatus fulfilled(final JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
    boolean newActIsPickup = newAct instanceof PickupActivity;
    boolean newActIsDelivery = newAct instanceof DeliveryActivity;
    double newActArrival = prevActDepTime + transportTime.getTransportTime(prevAct.getLocation(), newAct.getLocation(), prevActDepTime, iFacts.getNewDriver(), iFacts.getNewVehicle());
    double newActStart = Math.max(newActArrival, newAct.getTheoreticalEarliestOperationStartTime());
    double newActDeparture = newActStart + activityCosts.getActivityDuration(prevAct, newAct, newActArrival, iFacts.getNewDriver(), iFacts.getNewVehicle());
    double nextActArrival = newActDeparture + transportTime.getTransportTime(newAct.getLocation(), nextAct.getLocation(), newActDeparture, iFacts.getNewDriver(), iFacts.getNewVehicle());
    double nextActStart = Math.max(nextActArrival, nextAct.getTheoreticalEarliestOperationStartTime());
    if (newAct instanceof DeliveryActivity) {
      double pickupEnd;
      if (iFacts.getAssociatedActivities().size() == 1) {
        pickupEnd = iFacts.getNewDepTime();
      } else {
        pickupEnd = iFacts.getRelatedActivityContext().getEndTime();
      }
      double timeInVehicle = newActStart - pickupEnd;
      double maxTimeInVehicle = ((TourActivity.JobActivity) newAct).getJob().getMaxTimeInVehicle();
      if (timeInVehicle > maxTimeInVehicle) {
        return ConstraintsStatus.NOT_FULFILLED;
      }
    } else {
      if (newActIsPickup) {
        if (iFacts.getAssociatedActivities().size() == 1) {
          double maxTimeInVehicle = ((TourActivity.JobActivity) newAct).getJob().getMaxTimeInVehicle();
          double nextActDeparture = nextActStart + activityCosts.getActivityDuration(prevAct, nextAct, nextActArrival, iFacts.getNewDriver(), iFacts.getNewVehicle());
          double timeToEnd = 0;
          if (timeToEnd > maxTimeInVehicle) {
            return ConstraintsStatus.NOT_FULFILLED;
          }
        }
      }
    }
    double minSlack = Double.MAX_VALUE;
    if (!(nextAct instanceof End)) {
      minSlack = stateManager.getActivityState(nextAct, iFacts.getNewVehicle(), minSlackId, Double.class);
    }
    double directArrTimeNextAct = prevActDepTime + transportTime.getTransportTime(prevAct.getLocation(), nextAct.getLocation(), prevActDepTime, iFacts.getNewDriver(), iFacts.getNewVehicle());
    double directNextActStart = Math.max(directArrTimeNextAct, nextAct.getTheoreticalEarliestOperationStartTime());
    double additionalTimeOfNewAct = (nextActStart - prevActDepTime) - (directNextActStart - prevActDepTime);
    if (additionalTimeOfNewAct > minSlack) {
      if (newActIsPickup) {
        return ConstraintsStatus.NOT_FULFILLED;
      } else {
        return ConstraintsStatus.NOT_FULFILLED;
      }
    }
    if (newActIsDelivery) {
      Map<Job, Double> openJobsAtNext;
      if (nextAct instanceof End) {
        openJobsAtNext = stateManager.getRouteState(iFacts.getRoute(), iFacts.getNewVehicle(), openJobsId, Map.class);
      } else {
        openJobsAtNext = stateManager.getActivityState(nextAct, iFacts.getNewVehicle(), openJobsId, Map.class);
      }
      if (openJobsAtNext == null) {
        openJobsAtNext = Collections.emptyMap();
      }
      for (Job openJob : openJobsAtNext.keySet()) {
        double slack = openJobsAtNext.get(openJob);
        double additionalTimeOfNewJob = additionalTimeOfNewAct;
        if (openJob instanceof Shipment) {
          Map<Job, Double> openJobsAtNextOfPickup = Collections.emptyMap();
          TourActivity nextAfterPickup;
          if (iFacts.getAssociatedActivities().size() == 1 && !iFacts.getRoute().isEmpty()) {
            nextAfterPickup = iFacts.getRoute().getActivities().get(0);
          } else {
            nextAfterPickup = iFacts.getRoute().getActivities().get(iFacts.getRelatedActivityContext().getInsertionIndex());
          }
          if (nextAfterPickup != null) {
            openJobsAtNextOfPickup = stateManager.getActivityState(nextAfterPickup, iFacts.getNewVehicle(), openJobsId, Map.class);
          }
          if (openJobsAtNextOfPickup.containsKey(openJob)) {
            TourActivity pickupAct = iFacts.getAssociatedActivities().get(0);
            double pickupActArrTime = iFacts.getRelatedActivityContext().getArrivalTime();
            double pickupActEndTime = startOf(pickupAct, pickupActArrTime) + activityCosts.getActivityDuration(pickupAct, pickupActArrTime, iFacts.getNewDriver(), iFacts.getNewVehicle());
            double nextAfterPickupArr = pickupActEndTime + transportTime.getTransportTime(pickupAct.getLocation(), nextAfterPickup.getLocation(), pickupActArrTime, iFacts.getNewDriver(), iFacts.getNewVehicle());
            additionalTimeOfNewJob += startOf(nextAfterPickup, nextAfterPickupArr) - startOf(nextAfterPickup, nextAfterPickup.getArrTime());
          }
        }
        if (additionalTimeOfNewJob > slack) {
          return ConstraintsStatus.NOT_FULFILLED;
        }
      }
    }
    return ConstraintsStatus.FULFILLED;
  }

  private double startOf(TourActivity act, double arrTime) {
    return Math.max(arrTime, act.getTheoreticalEarliestOperationStartTime());
  }
}