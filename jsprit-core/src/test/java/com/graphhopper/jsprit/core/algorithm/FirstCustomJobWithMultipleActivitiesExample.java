package com.graphhopper.jsprit.core.algorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.CustomPickupJob;
import com.graphhopper.jsprit.core.util.Solutions;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by schroeder on 11/11/16.
 */
public class FirstCustomJobWithMultipleActivitiesExample {

<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/test/java/com/graphhopper/jsprit/core/algorithm/FirstCustomJobWithMultipleActivitiesExample.java/left.java
  static class CustomJob extends AbstractJob {
    public static abstract class BuilderBase<T extends CustomJob, B extends BuilderBase<T, B>> extends JobBuilder<T, B> {
      List<Location> locs = new ArrayList<>();

      List<SizeDimension> cap = new ArrayList<>();

      public BuilderBase(String id) {
        super(id);
      }

      public BuilderBase<T, B> addPickup(Location location, SizeDimension capacity) {
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

    public static final class Builder extends BuilderBase<CustomJob, Builder> {
      public static Builder newInstance(String id) {
        return new Builder(id);
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
      Builder builder = (Builder) jobBuilder;
      JobActivityList list = new SequentialJobActivityList(this);
      for (int i = 0; i < builder.getLocs().size(); i++) {
        list.addActivity(new PickupActivityNEW(this, "pick", builder.getLocs().get(i), 0, builder.getCaps().get(i), Arrays.asList(TimeWindow.ETERNITY)));
      }
      setActivities(list);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Test public void shouldRunOK() {
    CustomPickupJob cj = CustomPickupJob.Builder.newInstance("job").addPickup(Location.newInstance(10, 0), Capacity.Builder.newInstance().addDimension(0, 1).build()).addPickup(Location.newInstance(5, 0), Capacity.Builder.newInstance().addDimension(0, 2).build()).addPickup(Location.newInstance(20, 0), Capacity.Builder.newInstance().addDimension(0, 1).build()).build();
    VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 4).build();
    Vehicle v = VehicleImpl.Builder.newInstance("v").setType(type).setStartLocation(Location.newInstance(0, 0)).build();
    VehicleRoutingProblem vrp = VehicleRoutingProblem.Builder.newInstance().addJob(cj).addVehicle(v).build();
    VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
    vra.setMaxIterations(10);
    VehicleRoutingProblemSolution solution = Solutions.bestOf(vra.searchSolutions());
    SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
    Assert.assertTrue(solution.getUnassignedJobs().isEmpty());
  }


<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/test/java/com/graphhopper/jsprit/core/algorithm/FirstCustomJobWithMultipleActivitiesExample.java/left.java
  @Test public void test() {
    CustomJob cj = CustomJob.Builder.newInstance("job").addPickup(Location.newInstance(10, 0), SizeDimension.Builder.newInstance().addDimension(0, 1).build()).addPickup(Location.newInstance(5, 0), SizeDimension.Builder.newInstance().addDimension(0, 2).build()).addPickup(Location.newInstance(20, 0), SizeDimension.Builder.newInstance().addDimension(0, 1).build()).build();
    Vehicle v = VehicleImpl.Builder.newInstance("v").setStartLocation(Location.newInstance(0, 0)).build();
    VehicleRoutingProblem vrp = VehicleRoutingProblem.Builder.newInstance().addJob(cj).addVehicle(v).build();
    VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
    vra.setMaxIterations(0);
    VehicleRoutingProblemSolution solution = Solutions.bestOf(vra.searchSolutions());
    SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
    Assert.assertTrue(solution.getUnassignedJobs().isEmpty());
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Test public void shouldNotIgnoresCapacity() {
    CustomPickupJob cj = CustomPickupJob.Builder.newInstance("job").addPickup(Location.newInstance(10, 0), Capacity.Builder.newInstance().addDimension(0, 1).build()).addPickup(Location.newInstance(5, 0), Capacity.Builder.newInstance().addDimension(0, 2).build()).addPickup(Location.newInstance(20, 0), Capacity.Builder.newInstance().addDimension(0, 1).build()).build();
    VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 2).build();
    Vehicle v = VehicleImpl.Builder.newInstance("v").setType(type).setStartLocation(Location.newInstance(0, 0)).build();
    VehicleRoutingProblem vrp = VehicleRoutingProblem.Builder.newInstance().addJob(cj).addVehicle(v).build();
    VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
    vra.setMaxIterations(10);
    VehicleRoutingProblemSolution solution = Solutions.bestOf(vra.searchSolutions());
    SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
    Assert.assertFalse(solution.getUnassignedJobs().isEmpty());
  }
}