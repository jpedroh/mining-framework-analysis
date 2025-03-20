package com.graphhopper.jsprit.core.problem.solution.route.activity;
import java.util.Collection;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.AbstractJob;
import com.graphhopper.jsprit.core.problem.job.Service;

public class ServiceActivityNEW extends JobActivity {
  public ServiceActivityNEW(AbstractJob job, String name, Location location, double operationTime, SizeDimension capacity, Collection<TimeWindow> timeWindows) {
    super(job, name, location, operationTime, capacity, timeWindows);
  }

  public ServiceActivityNEW(ServiceActivityNEW sourceActivity) {
    super(sourceActivity);
  }

  public static ServiceActivityNEW newInstance(Service service) {
    return new ServiceActivityNEW(service, service.getName(), service.getLocation(), service.getServiceDuration(), service.getSize(), service.getTimeWindows());
  }
}