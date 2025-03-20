package algorithms;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import util.Solutions;
import algorithms.BackwardInTimeListeners.BackwardInTimeListener;
import algorithms.ForwardInTimeListeners.ForwardInTimeListener;
import algorithms.HardConstraints.HardActivityLevelConstraintManager;
import algorithms.StateManager.StateImpl;
import algorithms.StateUpdates.UpdateActivityTimes;
import algorithms.StateUpdates.UpdateCostsAtAllLevels;
import algorithms.StateUpdates.UpdateEarliestStartTimeWindowAtActLocations;
import algorithms.StateUpdates.UpdateLatestOperationStartTimeAtActLocations;
import algorithms.acceptors.AcceptNewIfBetterThanWorst;
import algorithms.selectors.SelectBest;
import basics.Delivery;
import basics.Job;
import basics.Pickup;
import basics.VehicleRoutingAlgorithm;
import basics.VehicleRoutingProblem;
import basics.VehicleRoutingProblemSolution;
import basics.algo.InsertionStartsListener;
import basics.algo.IterationStartsListener;
import basics.algo.JobInsertedListener;
import basics.algo.SearchStrategy;
import basics.algo.SearchStrategyManager;
import basics.io.VrpXMLReader;
import basics.io.VrpXMLWriter;
import basics.route.DeliveryActivity;
import basics.route.End;
import basics.route.PickupActivity;
import basics.route.Start;
import basics.route.TourActivity;
import basics.route.VehicleRoute;

public class BuildPDVRPAlgoFromScratchTest {
  VehicleRoutingProblem vrp;

  VehicleRoutingAlgorithm vra;

  static Logger log = Logger.getLogger(BuildPDVRPAlgoFromScratchTest.class);

  @Before public void setup() {
    VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
    new VrpXMLReader(builder).read("src/test/resources/pd_solomon_r101.xml");
    vrp = builder.build();
    final StateManagerImpl stateManager = new StateManagerImpl();
    HardActivityLevelConstraintManager actLevelConstraintAccumulator = new HardActivityLevelConstraintManager();
    actLevelConstraintAccumulator.addConstraint(new HardConstraints.HardPickupAndDeliveryActivityLevelConstraint(stateManager));
    actLevelConstraintAccumulator.addConstraint(new HardConstraints.HardTimeWindowActivityLevelConstraint(stateManager, vrp.getTransportCosts()));
    ActivityInsertionCostCalculator marginalCalculus = new MarginalsCalculusTriangleInequality(vrp.getTransportCosts(), vrp.getActivityCosts(), actLevelConstraintAccumulator);
    CalculatesServiceInsertion serviceInsertion = new CalculatesServiceInsertion(vrp.getTransportCosts(), marginalCalculus, new HardConstraints.HardPickupAndDeliveryLoadConstraint(stateManager));
    VehicleFleetManager fleetManager = new InfiniteVehicles(vrp.getVehicles());
    JobInsertionCalculator finalServiceInsertion = new CalculatesVehTypeDepServiceInsertion(fleetManager, serviceInsertion);
    BestInsertion bestInsertion = new BestInsertion(finalServiceInsertion);
    RuinRadial radial = new RuinRadial(vrp, 0.15, new JobDistanceAvgCosts(vrp.getTransportCosts()));
    RuinRandom random = new RuinRandom(vrp, 0.25);
    SearchStrategy randomStrategy = new SearchStrategy(new SelectBest(), new AcceptNewIfBetterThanWorst(1));
    RuinAndRecreateModule randomModule = new RuinAndRecreateModule("randomRuin_bestInsertion", bestInsertion, random);
    randomStrategy.addModule(randomModule);
    SearchStrategy radialStrategy = new SearchStrategy(new SelectBest(), new AcceptNewIfBetterThanWorst(1));
    RuinAndRecreateModule radialModule = new RuinAndRecreateModule("radialRuin_bestInsertion", bestInsertion, radial);
    radialStrategy.addModule(radialModule);
    SearchStrategyManager strategyManager = new SearchStrategyManager();
    strategyManager.addStrategy(radialStrategy, 0.5);
    strategyManager.addStrategy(randomStrategy, 0.5);
    vra = new VehicleRoutingAlgorithm(vrp, strategyManager);
    vra.getAlgorithmListeners().addListener(new StateUpdates.ResetStateManager(stateManager));
    final RouteActivityVisitor iterateForward = new RouteActivityVisitor();
    iterateForward.addActivityVisitor(new UpdateActivityTimes(vrp.getTransportCosts()));
    iterateForward.addActivityVisitor(new UpdateEarliestStartTimeWindowAtActLocations(stateManager, vrp.getTransportCosts()));
    iterateForward.addActivityVisitor(new UpdateCostsAtAllLevels(vrp.getActivityCosts(), vrp.getTransportCosts(), stateManager));
    iterateForward.addActivityVisitor(new StateUpdates.UpdateOccuredDeliveriesAtActivityLevel(stateManager));
    iterateForward.addActivityVisitor(new StateUpdates.UpdateLoadAtActivityLevel(stateManager));
    final ReverseRouteActivityVisitor iterateBackward = new ReverseRouteActivityVisitor();
    iterateBackward.addActivityVisitor(new UpdateLatestOperationStartTimeAtActLocations(stateManager, vrp.getTransportCosts()));
    iterateBackward.addActivityVisitor(new StateUpdates.UpdateFuturePickupsAtActivityLevel(stateManager));
    InsertionStartsListener loadVehicleInDepot = new InsertionStartsListener() {
      @Override public void informInsertionStarts(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
        for (VehicleRoute route : vehicleRoutes) {
          int loadAtDepot = 0;
          int loadAtEnd = 0;
          for (Job j : route.getTourActivities().getJobs()) {
            if (j instanceof Delivery) {
              loadAtDepot += j.getCapacityDemand();
            }
            if (j instanceof Pickup) {
              loadAtEnd += j.getCapacityDemand();
            }
          }
          stateManager.putRouteState(route, StateTypes.LOAD_AT_DEPOT, new StateImpl(loadAtDepot));
          stateManager.putRouteState(route, StateTypes.LOAD, new StateImpl(loadAtEnd));
          iterateForward.visit(route);
          iterateBackward.visit(route);
        }
      }
    };
    vra.getSearchStrategyManager().addSearchStrategyModuleListener(new RemoveEmptyVehicles(fleetManager));
    JobInsertedListener updateLoadAfterJobHasBeenInserted = new JobInsertedListener() {
      @Override public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
        if (job2insert instanceof Delivery) {
          int loadAtDepot = (int) stateManager.getRouteState(inRoute, StateTypes.LOAD_AT_DEPOT).toDouble();
          stateManager.putRouteState(inRoute, StateTypes.LOAD_AT_DEPOT, new StateImpl(loadAtDepot + job2insert.getCapacityDemand()));
        }
        if (job2insert instanceof Pickup) {
          int loadAtEnd = (int) stateManager.getRouteState(inRoute, StateTypes.LOAD).toDouble();
          stateManager.putRouteState(inRoute, StateTypes.LOAD, new StateImpl(loadAtEnd + job2insert.getCapacityDemand()));
        }
        iterateForward.visit(inRoute);
        iterateBackward.visit(inRoute);
      }
    };
    bestInsertion.addListener(loadVehicleInDepot);
    bestInsertion.addListener(updateLoadAfterJobHasBeenInserted);
    VehicleRoutingProblemSolution iniSolution = new CreateInitialSolution(bestInsertion).createInitialSolution(vrp);
    vra.addInitialSolution(iniSolution);
    vra.setNuOfIterations(10000);
    vra.setPrematureBreak(1000);
  }

  @Test public void test() {
    Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
  }
}