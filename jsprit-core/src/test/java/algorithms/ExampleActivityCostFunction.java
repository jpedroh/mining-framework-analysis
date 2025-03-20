package algorithms;
import basics.costs.VehicleRoutingActivityCosts;
import basics.route.Driver;
import basics.route.TourActivity;
import basics.route.Vehicle;
import basics.route.TourActivity.JobActivity;

public class ExampleActivityCostFunction implements VehicleRoutingActivityCosts {
  public ExampleActivityCostFunction() {
    super();
  }

  public double parameter_timeAtAct;

  public double parameter_penaltyTooLate;

  @Override public double getActivityCost(TourActivity tourAct, double arrivalTime, Driver driver, Vehicle vehicle) {
    if (arrivalTime == Time.TOURSTART || arrivalTime == Time.UNDEFINED) {
      return 0.0;
    } else {
      double endTime = Math.max(arrivalTime, tourAct.getTheoreticalEarliestOperationStartTime()) + tourAct.getOperationTime();
      double timeAtAct = endTime - arrivalTime;
      double totalCost = timeAtAct * parameter_timeAtAct;
      if (tourAct instanceof JobActivity) {
        if (arrivalTime > tourAct.getTheoreticalLatestOperationStartTime()) {
          double penTime = arrivalTime - tourAct.getTheoreticalLatestOperationStartTime();
          totalCost += penTime * parameter_penaltyTooLate;
        }
      }
      return totalCost;
    }
  }
}