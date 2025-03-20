package jsprit.examples;
import jsprit.analysis.toolbox.GraphStreamViewer;
import java.util.Collection;
import jsprit.analysis.toolbox.GraphStreamViewer.Label;
import jsprit.analysis.toolbox.Plotter;
import jsprit.analysis.toolbox.SolutionPrinter;
import jsprit.analysis.toolbox.SolutionPrinter.Print;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.VehicleRoutingAlgorithmBuilder;
import jsprit.core.algorithm.state.StateManager;
import jsprit.core.algorithm.termination.IterationWithoutImprovementTermination;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.constraint.ConstraintManager;
import jsprit.core.problem.constraint.HardRouteStateLevelConstraint;
import jsprit.core.problem.job.Shipment;
import jsprit.core.problem.misc.JobInsertionContext;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;
import jsprit.util.Examples;

public class TransportOfDisabledPeople {
  static int WHEELCHAIRSPACE_INDEX = 0;

  static int PASSENGERSEATS_INDEX = 1;

  public static void main(String[] args) {
    Examples.createOutputFolder();
    VehicleTypeImpl.Builder wheelChairTypeBuilder = VehicleTypeImpl.Builder.newInstance("wheelChairBusType").addCapacityDimension(WHEELCHAIRSPACE_INDEX, 2).addCapacityDimension(PASSENGERSEATS_INDEX, 4);
    VehicleType vehicleType_wheelchair = wheelChairTypeBuilder.build();
    VehicleTypeImpl.Builder soleyPassengerTypeBuilder = VehicleTypeImpl.Builder.newInstance("passengerBusType").addCapacityDimension(PASSENGERSEATS_INDEX, 6);
    VehicleType vehicleType_solelypassenger = soleyPassengerTypeBuilder.build();
    Builder vehicleBuilder1 = VehicleImpl.Builder.newInstance("wheelchair_bus");
    vehicleBuilder1.setStartLocationCoordinate(Coordinate.newInstance(10, 10));
    vehicleBuilder1.setType(vehicleType_wheelchair);
    Vehicle vehicle1 = vehicleBuilder1.build();
    Builder vehicleBuilder1_2 = VehicleImpl.Builder.newInstance("wheelchair_bus_2");
    vehicleBuilder1_2.setStartLocationCoordinate(Coordinate.newInstance(10, 10));
    vehicleBuilder1_2.setType(vehicleType_wheelchair);
    Vehicle vehicle1_2 = vehicleBuilder1_2.build();
    Builder vehicleBuilder2 = VehicleImpl.Builder.newInstance("passenger_bus");
    vehicleBuilder2.setStartLocationCoordinate(Coordinate.newInstance(30, 30)).setEndLocationCoordinate(Coordinate.newInstance(30, 19));
    vehicleBuilder2.setType(vehicleType_solelypassenger);
    Vehicle vehicle2 = vehicleBuilder2.build();
    Builder vehicleBuilder2_2 = VehicleImpl.Builder.newInstance("passenger_bus_2");
    vehicleBuilder2_2.setStartLocationCoordinate(Coordinate.newInstance(30, 30)).setEndLocationCoordinate(Coordinate.newInstance(30, 19));
    vehicleBuilder2_2.setType(vehicleType_solelypassenger);
    Vehicle vehicle2_2 = vehicleBuilder2_2.build();
    Shipment shipment1 = Shipment.Builder.newInstance("wheelchair_1").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(5, 7)).setDeliveryCoord(Coordinate.newInstance(6, 9)).build();
    Shipment shipment2 = Shipment.Builder.newInstance("2").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(5, 13)).setDeliveryCoord(Coordinate.newInstance(6, 11)).build();
    Shipment shipment3 = Shipment.Builder.newInstance("wheelchair_2").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(15, 7)).setDeliveryCoord(Coordinate.newInstance(14, 9)).build();
    Shipment shipment4 = Shipment.Builder.newInstance("4").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(15, 13)).setDeliveryCoord(Coordinate.newInstance(14, 11)).build();
    Shipment shipment5 = Shipment.Builder.newInstance("wheelchair_3").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(25, 27)).setDeliveryCoord(Coordinate.newInstance(26, 29)).build();
    Shipment shipment6 = Shipment.Builder.newInstance("6").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(25, 33)).setDeliveryCoord(Coordinate.newInstance(26, 31)).build();
    Shipment shipment7 = Shipment.Builder.newInstance("7").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(35, 27)).setDeliveryCoord(Coordinate.newInstance(34, 29)).build();
    Shipment shipment8 = Shipment.Builder.newInstance("wheelchair_4").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(35, 33)).setDeliveryCoord(Coordinate.newInstance(34, 31)).build();
    Shipment shipment9 = Shipment.Builder.newInstance("9").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(5, 27)).setDeliveryCoord(Coordinate.newInstance(6, 29)).build();
    Shipment shipment10 = Shipment.Builder.newInstance("wheelchair_5").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(5, 33)).setDeliveryCoord(Coordinate.newInstance(6, 31)).build();
    Shipment shipment11 = Shipment.Builder.newInstance("11").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(15, 27)).setDeliveryCoord(Coordinate.newInstance(14, 29)).build();
    Shipment shipment12 = Shipment.Builder.newInstance("wheelchair_6").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(15, 33)).setDeliveryCoord(Coordinate.newInstance(14, 31)).build();
    Shipment shipment13 = Shipment.Builder.newInstance("13").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(25, 7)).setDeliveryCoord(Coordinate.newInstance(26, 9)).build();
    Shipment shipment14 = Shipment.Builder.newInstance("wheelchair_7").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(25, 13)).setDeliveryCoord(Coordinate.newInstance(26, 11)).build();
    Shipment shipment15 = Shipment.Builder.newInstance("15").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(35, 7)).setDeliveryCoord(Coordinate.newInstance(34, 9)).build();
    Shipment shipment16 = Shipment.Builder.newInstance("wheelchair_8").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(35, 13)).setDeliveryCoord(Coordinate.newInstance(34, 11)).build();
    Shipment shipment17 = Shipment.Builder.newInstance("17").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(5, 14)).setDeliveryCoord(Coordinate.newInstance(6, 16)).build();
    Shipment shipment18 = Shipment.Builder.newInstance("wheelchair_9").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(5, 20)).setDeliveryCoord(Coordinate.newInstance(6, 18)).build();
    Shipment shipment19 = Shipment.Builder.newInstance("19").addSizeDimension(PASSENGERSEATS_INDEX, 1).setPickupCoord(Coordinate.newInstance(15, 14)).setDeliveryCoord(Coordinate.newInstance(14, 16)).build();
    Shipment shipment20 = Shipment.Builder.newInstance("wheelchair_10").addSizeDimension(WHEELCHAIRSPACE_INDEX, 1).setPickupCoord(Coordinate.newInstance(15, 20)).setDeliveryCoord(Coordinate.newInstance(14, 18)).build();
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    vrpBuilder.addVehicle(vehicle1).addVehicle(vehicle2).addVehicle(vehicle1_2).addVehicle(vehicle2_2);
    vrpBuilder.addJob(shipment1).addJob(shipment2).addJob(shipment3).addJob(shipment4);
    vrpBuilder.addJob(shipment5).addJob(shipment6).addJob(shipment7).addJob(shipment8);
    vrpBuilder.addJob(shipment9).addJob(shipment10).addJob(shipment11).addJob(shipment12);
    vrpBuilder.addJob(shipment13).addJob(shipment14).addJob(shipment15).addJob(shipment16);
    vrpBuilder.addJob(shipment17).addJob(shipment18).addJob(shipment19).addJob(shipment20);
    vrpBuilder.setFleetSize(FleetSize.FINITE);
    HardRouteStateLevelConstraint wheelchair_bus_passenger_pickup_constraint = new HardRouteStateLevelConstraint() {
      @Override public boolean fulfilled(JobInsertionContext insertionContext) {
        Shipment shipment2insert = ((Shipment) insertionContext.getJob());
        if (insertionContext.getNewVehicle().getId().equals("wheelchair_bus")) {
          if (shipment2insert.getSize().get(PASSENGERSEATS_INDEX) > 0) {
            if (shipment2insert.getPickupCoord().getX() > 15. || shipment2insert.getDeliveryCoord().getX() > 15.) {
              return false;
            }
          }
        }
        return true;
      }
    };
    VehicleRoutingProblem problem = vrpBuilder.build();
    StateManager stateManager = new StateManager(problem);
    ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
    constraintManager.addConstraint(wheelchair_bus_passenger_pickup_constraint);
    VehicleRoutingAlgorithmBuilder algorithmBuilder = new VehicleRoutingAlgorithmBuilder(problem, "input/algorithmConfig_noVehicleSwitch.xml");
    algorithmBuilder.setStateAndConstraintManager(stateManager, constraintManager);
    algorithmBuilder.addCoreConstraints();
    algorithmBuilder.addDefaultCostCalculators();
    VehicleRoutingAlgorithm algorithm = algorithmBuilder.build();
    algorithm.setPrematureAlgorithmTermination(new IterationWithoutImprovementTermination(100));
    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
    SolutionPrinter.print(problem, bestSolution, Print.VERBOSE);
    Plotter problemPlotter = new Plotter(problem);
    problemPlotter.plotShipments(true);
    problemPlotter.setLabel(jsprit.analysis.toolbox.Plotter.Label.SIZE);
    problemPlotter.plot("output/transportOfDisabledPeopleExample_problem.png", "disabled people tp");
    Plotter solutionPlotter = new Plotter(problem, Solutions.bestOf(solutions));
    solutionPlotter.plotShipments(true);
    solutionPlotter.setLabel(jsprit.analysis.toolbox.Plotter.Label.SIZE);
    solutionPlotter.plot("output/transportOfDisabledPeopleExample_solution.png", "disabled people tp");
    new GraphStreamViewer(problem).labelWith(Label.ID).setRenderDelay(100).setRenderShipments(true).display();
    new GraphStreamViewer(problem, Solutions.bestOf(solutions)).labelWith(Label.ACTIVITY).setRenderDelay(100).setRenderShipments(true).display();
  }
}