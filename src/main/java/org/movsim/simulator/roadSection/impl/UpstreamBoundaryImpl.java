package org.movsim.simulator.roadSection.impl;
import java.io.PrintWriter;
import java.util.List;
import org.movsim.input.model.simulation.UpstreamBoundaryData;
import org.movsim.output.fileoutput.FileUpstreamBoundaryData;
import org.movsim.simulator.Constants;
import org.movsim.simulator.roadSection.InflowTimeSeries;
import org.movsim.simulator.roadSection.UpstreamBoundary;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.VehicleGenerator;
import org.movsim.simulator.vehicles.VehiclePrototype;
import org.movsim.utilities.impl.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class UpstreamBoundaryImpl.
 */
public class UpstreamBoundaryImpl implements UpstreamBoundary {
  private FileUpstreamBoundaryData fileUpstreamBoundary;

  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(UpstreamBoundaryImpl.class);


<<<<<<< /usr/src/app/output/movsim/movsim/a1a04271dd9f9c1984db574e638fc0eb890760d3/src/main/java/org/movsim/simulator/roadSection/impl/UpstreamBoundaryImpl.java/left.java
  private static final String outputHeading = Constants.COMMENT_CHAR + "     t[s], lane,  xEnter[m],    v[km/h],   total qBC[1/h],    count,      queue\n";
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /** The n wait. */
  private double nWait;

  /** The veh generator. */
  private final VehicleGenerator vehGenerator;

  /** The veh container. */
  private final List<VehicleContainer> vehContainers;

  /** The inflow time series. */
  private final InflowTimeSeries inflowTimeSeries;

  /** The entering veh counter. */
  private int enteringVehCounter;

  /** The x enter last. */
  private double xEnterLast;

  /** The v enter last. */
  private double vEnterLast;

  /** The lane enter last. */
  private int laneEnterLast;

  /**
     * Instantiates a new upstream boundary impl.
     * 
     * @param vehGenerator
     *            the vehicle generator
     * @param vehContainer
     *            the vehicle container
     * @param upstreamBoundaryData
     *            the upstream boundary data
     * @param projectName
     *            the project name
     */
  public UpstreamBoundaryImpl(VehicleGenerator vehGenerator, List<VehicleContainer> vehContainers, UpstreamBoundaryData upstreamBoundaryData, String projectName) {
    this.vehGenerator = vehGenerator;
    this.vehContainers = vehContainers;
    nWait = 0;
    enteringVehCounter = 1;
    inflowTimeSeries = new InflowTimeSeriesImpl(upstreamBoundaryData.getInflowTimeSeries());
    if (upstreamBoundaryData.withLogging()) {
      fileUpstreamBoundary = new FileUpstreamBoundaryData(projectName);
    }
  }

  private int getNewLaneIndex(int iLane) {
    return (iLane == vehContainers.size() - 1 ? 0 : iLane + 1);
  }

  public double getTotalInflow(double time) {
    final double qBC = inflowTimeSeries.getFlowPerLane(time);
    final int nLanes = vehContainers.size();
    return nLanes * qBC;
  }

  @Override public void update(int itime, double dt, double time) {
    final double totalInflow = getTotalInflow(time);
    nWait += totalInflow * dt;
    if (nWait >= 1) {
      int iLane = laneEnterLast;
      for (int i = 0, N = vehContainers.size(); i < N; i++) {
        iLane = getNewLaneIndex(iLane);
        final VehicleContainer vehContainerLane = vehContainers.get(iLane);
        final boolean isEntered = tryEnteringNewVehicle(vehContainerLane, iLane, time, totalInflow);
        if (isEntered) {
          nWait--;
          if (fileUpstreamBoundary != null) {

<<<<<<< /usr/src/app/output/movsim/movsim/a1a04271dd9f9c1984db574e638fc0eb890760d3/src/main/java/org/movsim/simulator/roadSection/impl/UpstreamBoundaryImpl.java/left.java
            fstrLogging.printf(outputFormat, time, laneEnterLast, xEnterLast, 3.6 * vEnterLast, 3600 * totalInflow, enteringVehCounter, nWait);
=======
>>>>>>> Unknown file: This is a bug in JDime.

            fileUpstreamBoundary.update(time, laneEnterLast, xEnterLast, 3.6 * vEnterLast, 3600 * qBC, enteringVehCounter, nWait);
          }
          return;
        }
      }
    }
  }

  /**
     * Try entering new vehicle.
     * 
     * @param time
     *            the time
     * @param qBC
     *            the q bc
     * @return true, if successful
     */
  private boolean tryEnteringNewVehicle(final VehicleContainer vehContainer, int lane, double time, double qBC) {
    final VehiclePrototype vehPrototype = vehGenerator.getVehiclePrototype();
    final Vehicle leader = vehContainer.getMostUpstream();
    if (leader == null) {
      enterVehicleOnEmptyRoad(vehContainer, lane, time, vehPrototype);
      return true;
    }
    final double netGapToLeader = leader.getPosition() - leader.getLength();
    double gapAtQMax = 1. / vehPrototype.getRhoQMax();
    if (vehPrototype.getLongModel().modelName().equalsIgnoreCase("")) {
      final double tau = 1;
      gapAtQMax = leader.getSpeed() * tau;
    }
    double minRequiredGap = 0.8 * gapAtQMax;
    if (vehPrototype.getLongModel().isCA()) {
      final double tau = 1;
      minRequiredGap = leader.getSpeed() * tau;
    }
    if (netGapToLeader > minRequiredGap) {
      enterVehicle(vehContainer, lane, time, minRequiredGap, vehPrototype, leader);
      return true;
    }
    return false;
  }

  /**
     * Enter vehicle on empty road.
     * 
     * @param time
     *            the time
     * @param vehPrototype
     *            the veh prototype
     */
  private void enterVehicleOnEmptyRoad(final VehicleContainer vehContainer, int lane, double time, VehiclePrototype vehPrototype) {
    final double xEnter = 0;
    final double vEnter = inflowTimeSeries.getSpeed(time);
    addVehicle(vehContainer, lane, vehPrototype, xEnter, vEnter);
  }

  /**
     * Enter vehicle.
     * 
     * @param time
     *            the time
     * @param sFreeMin
     *            the s free min
     * @param vehPrototype
     *            the veh prototype
     * @param leader
     *            the leader
     */
  private void enterVehicle(final VehicleContainer vehContainer, int lane, double time, double sFreeMin, VehiclePrototype vehPrototype, Vehicle leader) {
    final double sFree = leader.getPosition() - leader.getLength();
    final double xLast = leader.getPosition();
    final double vLast = leader.getSpeed();
    final double aLast = leader.getAcc();
    final double speedDefault = inflowTimeSeries.getSpeed(time);
    final double vEnterTest = Math.min(speedDefault, 1.5 * vLast);
    final double lengthLast = leader.getLength();
    final double qBC = inflowTimeSeries.getFlowPerLane(time);
    final double xEnter = Math.min(vEnterTest * nWait / Math.max(qBC, 0.001), xLast - sFreeMin - lengthLast);
    final double rhoEnter = 1. / (xLast - xEnter);
    final double vMaxEq = vehPrototype.getEquilibriumSpeed(0.5 * rhoEnter);
    final double bMax = 4;
    final double bEff = Math.max(0.1, bMax + aLast);
    final double vMaxKin = vLast + Math.sqrt(2 * sFree * bEff);
    final double vEnter = Math.min(Math.min(vEnterTest, vMaxEq), vMaxKin);
    addVehicle(vehContainer, lane, vehPrototype, xEnter, vEnter);
  }

  /**
     * Adds the vehicle.
     * 
     * @param vehPrototype
     *            the veh prototype
     * @param xEnter
     *            the x enter
     * @param vEnter
     *            the v enter
     * @param laneEnter
     *            the lane enter
     */
  private void addVehicle(final VehicleContainer vehContainer, int laneEnter, final VehiclePrototype vehPrototype, double xEnter, double vEnter) {
    final Vehicle veh = vehGenerator.createVehicle(vehPrototype);
    vehContainer.add(veh, xEnter, vEnter, laneEnter);
    enteringVehCounter++;
    xEnterLast = xEnter;
    vEnterLast = vEnter;
    laneEnterLast = laneEnter;
  }
}