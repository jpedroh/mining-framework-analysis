package jsprit.core.util;
import jsprit.core.problem.solution.route.activity.TourActivity;

public class CalculationUtils {
  /**
     * Calculates actEndTime assuming that activity can at earliest start at act.getTheoreticalEarliestOperationStartTime().
     *
     * @param actArrTime
     * @param act
     * @return
     */
  public static double getActivityEndTime(double actArrTime, TourActivity act) {
    return Math.max(actArrTime, act.getTheoreticalEarliestOperationStartTime()) + act.getOperationTime();
  }
}