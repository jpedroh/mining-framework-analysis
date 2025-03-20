package com.graphhopper.jsprit.core.problem.solution.route.activity;
import java.util.Collection;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.AbstractJob;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Break.Builder;

public class BreakActivity extends InternalJobActivity {
  public static BreakActivity newInstance(Break aBreak, Builder builder) {
    return new BreakActivity(aBreak, "break", builder.getLocation(), builder.getServiceTime(), builder.getCapacity(), builder.getTimeWindows().getTimeWindows());
  }

  public BreakActivity(BreakActivity breakActivity) {
    super(breakActivity);
  }

  private BreakActivity(AbstractJob job, String name, Location location, double operationTime, SizeDimension capacity, Collection<TimeWindow> timeWindows) {
    super(job, name, location, operationTime, capacity, timeWindows);
  }

  @Override public Break getJob() {
    return (Break) super.getJob();
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getJob() == null) ? 0 : getJob().hashCode());
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
    BreakActivity other = (BreakActivity) obj;
    if (getJob() == null) {
      if (other.getJob() != null) {
        return false;
      }
    } else {
      if (!getJob().equals(other.getJob())) {
        return false;
      }
    }
    return true;
  }

  public void setLocation(Location breakLocation) {
    location = breakLocation;
  }
}