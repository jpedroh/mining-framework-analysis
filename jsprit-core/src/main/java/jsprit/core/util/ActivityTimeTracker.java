package jsprit.core.util;
import jsprit.core.problem.cost.ForwardTransportTime;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.*;

public class ActivityTimeTracker implements ActivityVisitor {
  public static enum ActivityPolicy {
    AS_SOON_AS_TIME_WINDOW_OPENS,
    AS_SOON_AS_ARRIVED
  }

  private ForwardTransportTime transportTime;

  private TourActivity prevAct = null;

  private double startAtPrevAct;

  private VehicleRoute route;

  private boolean beginFirst = false;

  private double actArrTime;

  private double actEndTime;

  private ActivityStartStrategy startStrategy;

  public ActivityTimeTracker(ForwardTransportTime transportTime) {
    super();
    this.transportTime = transportTime;
    this.startStrategy = new ActivityStartsAsSoonAsTimeWindowOpens();
  }

  public ActivityTimeTracker(ForwardTransportTime transportTime, ActivityPolicy activityPolicy) {
    super();
    this.transportTime = transportTime;
    if (activityPolicy.equals(ActivityPolicy.AS_SOON_AS_ARRIVED)) {
      this.startStrategy = new ActivityStartAsSoonAsArrived();
    } else {
      this.startStrategy = new ActivityStartsAsSoonAsTimeWindowOpens();
    }
  }

  public ActivityTimeTracker(ForwardTransportTime transportTime, ActivityStartStrategy startStrategy) {
    super();
    this.transportTime = transportTime;
    this.startStrategy = startStrategy;
  }

  public double getActArrTime() {
    return actArrTime;
  }

  public double getActEndTime() {
    return actEndTime;
  }

  @Override public void begin(VehicleRoute route) {
    prevAct = route.getStart();
    startAtPrevAct = prevAct.getEndTime();
    actEndTime = startAtPrevAct;
    this.route = route;
    beginFirst = true;
  }

  @Override public void visit(TourActivity activity) {
    if (!beginFirst) {
      throw new IllegalStateException("never called begin. this however is essential here");
    }
    double transportTime = this.transportTime.getTransportTime(prevAct.getLocation(), activity.getLocation(), startAtPrevAct, route.getDriver(), route.getVehicle());
    double arrivalTimeAtCurrAct = startAtPrevAct + transportTime;
    actArrTime = arrivalTimeAtCurrAct;
    double operationEndTime = startStrategy.getActivityStartTime(activity, arrivalTimeAtCurrAct) + activity.getOperationTime();
    actEndTime = operationEndTime;
    prevAct = activity;
    startAtPrevAct = operationEndTime;
  }

  @Override public void finish() {
    double transportTime = this.transportTime.getTransportTime(prevAct.getLocation(), route.getEnd().getLocation(), startAtPrevAct, route.getDriver(), route.getVehicle());
    double arrivalTimeAtCurrAct = startAtPrevAct + transportTime;
    actArrTime = arrivalTimeAtCurrAct;
    actEndTime = arrivalTimeAtCurrAct;
    beginFirst = false;
  }
}