package com.graphhopper.jsprit.core.problem.job;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupActivityNEW;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by schroeder on 16/11/16.
 */
public class CustomJobTest {
  static class CustomJob extends AbstractJob {
    public static abstract class BuilderBase<T extends CustomJob, B extends CustomJob.BuilderBase<T, B>> extends JobBuilder<T, B> {
      List<Location> locs = new ArrayList<>();

      List<SizeDimension> cap = new ArrayList<>();

      public BuilderBase(String id) {
        super(id);
      }

      public CustomJob.BuilderBase<T, B> addPickup(Location location, SizeDimension capacity) {
        locs.add(location);
        cap.add(capacity);
        return this;
      }

      public List<Location> getLocs() {
        return locs;
      }

      public List<SizeDimension> getCaps() {
        return cap;
      }

      protected void validate() {
      }
    }

    public static final class Builder extends CustomJob.BuilderBase<CustomJob, CustomJob.Builder> {
      public static CustomJob.Builder newInstance(String id) {
        return new CustomJob.Builder(id);
      }

      public Builder(String id) {
        super(id);
      }

      @Override protected CustomJob createInstance() {
        return new CustomJob(this);
      }
    }

    /**
         * Builder based constructor.
         *
         * @param builder The builder instance.
         * @see JobBuilder
         */
    protected CustomJob(JobBuilder<?, ?> builder) {
      super(builder);
    }

    @Override public SizeDimension getSize() {
      return SizeDimension.EMPTY;
    }

    @Override protected void createActivities(JobBuilder<? extends AbstractJob, ?> jobBuilder) {
      CustomJob.Builder builder = (CustomJob.Builder) jobBuilder;
      JobActivityList list = new SequentialJobActivityList(this);
      for (int i = 0; i < builder.getLocs().size(); i++) {
        list.addActivity(new PickupActivityNEW(this, "pick", builder.getLocs().get(i), 0, builder.getCaps().get(i), Arrays.asList(TimeWindow.ETERNITY)));
      }
      setActivities(list);
    }
  }

  @Test public void whenCreatingANewJobWithThreeDistinctActivities_jobShouldContainActivities() {
    CustomJob cj = CustomJob.Builder.newInstance("job").addPickup(Location.newInstance(10, 0), SizeDimension.Builder.newInstance().addDimension(0, 1).build()).addPickup(Location.newInstance(5, 0), SizeDimension.Builder.newInstance().addDimension(0, 2).build()).addPickup(Location.newInstance(20, 0), SizeDimension.Builder.newInstance().addDimension(0, 1).build()).build();
    Assert.assertEquals(3, cj.getActivityList().size());
  }
}