package com.graphhopper.jsprit.core.problem.solution.route.activity;
import java.util.Collection;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.AbstractJob;

public class DeliveryActivityNEW extends JobActivity {
  public DeliveryActivityNEW(AbstractJob job, String name, Location location, double operationTime, SizeDimension capacity, Collection<TimeWindow> timeWindows) {
    super(job, name, location, operationTime, capacity, timeWindows);
  }

  public DeliveryActivityNEW(DeliveryActivityNEW sourceActivity) {
    super(sourceActivity);
  }
}