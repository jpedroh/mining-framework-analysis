package org.movsim.output.route;
import org.movsim.autogen.TravelTimes;
import org.movsim.simulator.roadnetwork.RoadNetwork;
import org.movsim.simulator.roadnetwork.routing.Route;
import org.movsim.utilities.ExponentialMovingAverage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TravelTimeOnRoute extends OutputOnRouteBase {
  /** The Constant LOG. */
  final static Logger logger = LoggerFactory.getLogger(TravelTimeOnRoute.class);

  private final double tauEMA;

  private final double beta;

  private final FileTravelTimeOnRoute fileWriter;

  private double instantaneousTravelTime;

  private double totalTravelTime;

  private double meanSpeed;

  private double instTravelTimeEMA;

  private int numberOfVehicles;

  public TravelTimeOnRoute(double simulationTimestep, TravelTimes travelTimeInput, RoadNetwork roadNetwork, Route route, boolean writeOutput) {
    super(roadNetwork, route);
    this.tauEMA = travelTimeInput.getTauEMA();
    this.beta = Math.exp(-simulationTimestep / tauEMA);
    fileWriter = writeOutput ? new FileTravelTimeOnRoute(travelTimeInput.getDt(), route) : null;
    totalTravelTime = 0;
  }

  @Override public void timeStep(double dt, double simulationTime, long iterationCount) {
    numberOfVehicles = Math.max(0, RoadNetwork.vehicleCount(route) - roadNetwork.obstacleCount(route));
    instantaneousTravelTime = RoadNetwork.instantaneousTravelTime(route);
    totalTravelTime += numberOfVehicles * instantaneousTravelTime;
    meanSpeed = route.getLength() / instantaneousTravelTime;
    instTravelTimeEMA = (simulationTime == 0) ? instantaneousTravelTime : ExponentialMovingAverage.calc(instantaneousTravelTime, instTravelTimeEMA, beta);
    if (fileWriter != null) {
      fileWriter.write(simulationTime, this);
    }
  }

  public double getInstantaneousTravelTime() {
    return instantaneousTravelTime;
  }

  public double getMeanSpeed() {
    return meanSpeed;
  }

  public double getInstantaneousTravelTimeEMA() {
    return instTravelTimeEMA;
  }

  public double getTotalTravelTime() {
    return totalTravelTime;
  }

  public int getNumberOfVehicles() {
    return numberOfVehicles;
  }
}