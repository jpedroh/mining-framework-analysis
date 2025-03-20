package jsprit.core.problem.solution.route.activity;
import jsprit.core.problem.AbstractActivity;
import jsprit.core.problem.Capacity;
import jsprit.core.problem.Location;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;
import java.util.ArrayList;
import java.util.List;

public class ServiceActivity extends AbstractActivity implements JobActivity {
  @Deprecated public static int counter = 0;

  public double arrTime;

  public double endTime;

  private double theoreticalEarliest;

  private double theoreticalLatest;

  /**
     * @return the arrTime
     */
  public double getArrTime() {
    return arrTime;
  }

  /**
     * @param arrTime the arrTime to set
     */
  public void setArrTime(double arrTime) {
    this.arrTime = arrTime;
  }

  /**
     * @return the endTime
     */
  public double getEndTime() {
    return endTime;
  }

  /**
     * @param endTime the endTime to set
     */
  public void setEndTime(double endTime) {
    this.endTime = endTime;
  }

  public static ServiceActivity copyOf(ServiceActivity serviceActivity) {
    return new ServiceActivity(serviceActivity);
  }

  public static ServiceActivity newInstance(Service service) {
    return new ServiceActivity(service);
  }

  private final Service service;

  private List<TimeWindow> timeWindows;

  protected ServiceActivity(Service service) {
    counter++;
    this.service = service;
    timeWindows = new ArrayList<TimeWindow>(service.getTimeWindows());
  }

  protected ServiceActivity(ServiceActivity serviceActivity) {
    counter++;
    this.service = serviceActivity.getJob();
    this.arrTime = serviceActivity.getArrTime();
    this.endTime = serviceActivity.getEndTime();
    setIndex(serviceActivity.getIndex());
    this.theoreticalEarliest = serviceActivity.getTheoreticalEarliestOperationStartTime();
    this.theoreticalLatest = serviceActivity.getTheoreticalLatestOperationStartTime();
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((service == null) ? 0 : service.hashCode());
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ServiceActivity other = (ServiceActivity) obj;
    if (service == null) {
      if (other.service != null) {
        return false;
      }
    } else {
      if (!service.equals(other.service)) {
        return false;
      }
    }
    return true;
  }

  public double getTheoreticalEarliestOperationStartTime() {
    return theoreticalEarliest;
  }

  public double getTheoreticalLatestOperationStartTime() {
    return theoreticalLatest;
  }

  @Override public void setTheoreticalEarliestOperationStartTime(double earliest) {
    theoreticalEarliest = earliest;
  }

  @Override public void setTheoreticalLatestOperationStartTime(double latest) {
    theoreticalLatest = latest;
  }

  @Override public double getOperationTime() {
    return service.getServiceDuration();
  }

  @Override public String getLocationId() {
    return service.getLocation().getId();
  }

  @Override public Location getLocation() {
    return service.getLocation();
  }

  @Override public Service getJob() {
    return service;
  }

  @Override public String toString() {
    return "[type=" + getName() + "][locationId=" + getLocationId() + "][size=" + getSize().toString() + "][twStart=" + Activities.round(getTheoreticalEarliestOperationStartTime()) + "][twEnd=" + Activities.round(getTheoreticalLatestOperationStartTime()) + "]";
  }

  @Override public String getName() {
    return service.getType();
  }

  @Override public TourActivity duplicate() {
    return new ServiceActivity(this);
  }

  @Override public Capacity getSize() {
    return service.getSize();
  }
}