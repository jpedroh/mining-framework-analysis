package algorithms;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import basics.Job;
import basics.VehicleRoutingProblem;
import basics.VehicleRoutingProblemSolution;
import basics.algo.SolutionCostCalculator;
import basics.route.DriverImpl;
import basics.route.TourActivities;
import basics.route.Vehicle;
import basics.route.VehicleRoute;

final class CreateInitialSolution implements InitialSolutionFactory {
  private static final Logger logger = Logger.getLogger(CreateInitialSolution.class);

  private final InsertionStrategy insertion;

  private SolutionCostCalculator solutionCostCalculator;

  private boolean generateAsMuchAsRoutesAsVehiclesExist = false;

  public void setGenerateAsMuchAsRoutesAsVehiclesExist(boolean generateAsMuchAsRoutesAsVehiclesExist) {
    this.generateAsMuchAsRoutesAsVehiclesExist = generateAsMuchAsRoutesAsVehiclesExist;
  }

  public CreateInitialSolution(InsertionStrategy insertionStrategy, SolutionCostCalculator solutionCostCalculator) {
    super();
    this.insertion = insertionStrategy;
    this.solutionCostCalculator = solutionCostCalculator;
  }

  @Override public VehicleRoutingProblemSolution createSolution(final VehicleRoutingProblem vrp) {
    logger.info("create initial solution.");
    List<VehicleRoute> vehicleRoutes = new ArrayList<VehicleRoute>();
    if (generateAsMuchAsRoutesAsVehiclesExist) {
      for (Vehicle vehicle : vrp.getVehicles()) {
        vehicleRoutes.add(VehicleRoute.newInstance(TourActivities.emptyTour(), DriverImpl.noDriver(), vehicle));
      }
    }
    insertion.insertJobs(vehicleRoutes, getUnassignedJobs(vrp));
    logger.info("creation done");
    VehicleRoutingProblemSolution vehicleRoutingProblemSolution = new VehicleRoutingProblemSolution(vehicleRoutes, 0.0);
    solutionCostCalculator.calculateCosts(vehicleRoutingProblemSolution);
    return vehicleRoutingProblemSolution;
  }

  private List<Job> getUnassignedJobs(VehicleRoutingProblem vrp) {
    List<Job> jobs = new ArrayList<Job>(vrp.getJobs().values());
    return jobs;
  }
}