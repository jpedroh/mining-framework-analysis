package org.movsim.simulator.roadSection.impl;
import java.util.LinkedList;
import java.util.List;
import org.movsim.input.InputData;
import org.movsim.input.model.SimulationInput;
import org.movsim.input.model.simulation.RampData;
import org.movsim.simulator.Constants;
import org.movsim.simulator.roadSection.FlowConservingBottlenecks;
import org.movsim.simulator.roadSection.UpstreamBoundary;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.VehicleGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractRoadSection.
 */
public abstract class AbstractRoadSection {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(AbstractRoadSection.class);

  /** The road length. */
  protected final double roadLength;

  /** The n lanes. */
  protected final int nLanes;

  /** The dt. */
  protected double dt;

  /** The id. */
  protected final long id;

  protected final boolean instantaneousFileOutput;

  /** The veh generator. */
  protected final VehicleGenerator vehGenerator;

  /** The veh container list (for each lane). */
  protected List<VehicleContainer> vehContainers;

  protected List<Vehicle> stagedVehicles;

  /** The upstream boundary. */
  protected UpstreamBoundary upstreamBoundary;

  /** The flow cons bottlenecks. */
  protected FlowConservingBottlenecks flowConsBottlenecks;

  /**
     * Instantiates a new abstract road section.
     *
     * @param inputData the input data
     * @param vehGenerator the veh generator
     */
  public AbstractRoadSection(final InputData inputData, final VehicleGenerator vehGenerator) {
    this.vehGenerator = vehGenerator;
    final SimulationInput simInput = inputData.getSimulationInput();
    this.dt = simInput.getTimestep();
    this.roadLength = simInput.getSingleRoadInput().getRoadLength();
    this.nLanes = simInput.getSingleRoadInput().getLanes();
    this.id = simInput.getSingleRoadInput().getId();
    this.instantaneousFileOutput = inputData.getProjectMetaData().isInstantaneousFileOutput();
    init();
  }

  /**
     * Instantiates a new abstract road section.
     *
     * @param rampData the ramp data
     * @param vehGenerator the veh generator
     */
  public AbstractRoadSection(final RampData rampData, final VehicleGenerator vehGenerator) {
    this.vehGenerator = vehGenerator;
    this.roadLength = rampData.getRoadLength();
    this.nLanes = 1;
    this.id = rampData.getId();
    this.instantaneousFileOutput = false;
    init();
  }

  /**
     * Instantiates a new abstract road section.
     *
     * @param rampData the ramp data
     */
  public AbstractRoadSection(final RampData rampData) {
    this.vehGenerator = null;
    this.roadLength = rampData.getRoadLength();
    this.nLanes = 1;
    this.id = rampData.getId();
    this.instantaneousFileOutput = false;
    init();
  }

  /**
     * Inits the.
     */
  private void init() {
    stagedVehicles = new LinkedList<Vehicle>();
  }

  /**
     * Gets the road length.
     *
     * @return the road length
     */
  public double getRoadLength() {
    return roadLength;
  }

  /**
     * Gets the id.
     *
     * @return the id
     */
  public long getId() {
    return id;
  }

  /**
     * Gets the timestep.
     *
     * @return the timestep
     */
  public double getTimestep() {
    return dt;
  }

  /**
     * Gets the number of lanes.
     *
     * @return the number of lanes
     */
  public int getNumberOfLanes() {
    return nLanes;
  }

  /**
     * Gets the veh containers.
     *
     * @return the veh containers
     */
  public List<VehicleContainer> getVehContainers() {
    return vehContainers;
  }

  /**
     * Gets the veh container.
     *
     * @param laneIndex the lane index
     * @return the veh container
     */
  public VehicleContainer getVehContainer(int laneIndex) {
    return vehContainers.get(laneIndex);
  }

  /**
     * Accelerate.
     * 
     * @param iterationCount
     *            the i time
     * @param dt
     *            the dt
     * @param time
     *            the time
     */
  public void accelerate(long iterationCount, double dt, double time) {
    for (VehicleContainer vehContainerLane : vehContainers) {
      final int leftLaneIndex = vehContainerLane.getLaneIndex() + Constants.TO_LEFT;
      final VehicleContainer vehContainerLeftLane = (leftLaneIndex < vehContainers.size()) ? vehContainers.get(leftLaneIndex) : null;
      final List<Vehicle> vehiclesOnLane = vehContainerLane.getVehicles();
      for (final Vehicle veh : vehiclesOnLane) {
        final double x = veh.getPosition();
        final double alphaT = (flowConsBottlenecks == null) ? 1 : flowConsBottlenecks.alphaT(x);
        final double alphaV0 = (flowConsBottlenecks == null) ? 1 : flowConsBottlenecks.alphaV0(x);
        veh.calcAcceleration(dt, vehContainerLane, vehContainerLeftLane, alphaT, alphaV0);
      }
    }
  }

  /**
     * Update position and speed.
     *
     * @param iterationCount the iteration count
     * @param dt the dt
     * @param time the time
     */
  public void updatePositionAndSpeed(long iterationCount, double dt, double time) {
    for (VehicleContainer vehContainerLane : vehContainers) {
      for (final Vehicle veh : vehContainerLane.getVehicles()) {
        veh.updatePostionAndSpeed(dt);
      }
    }
  }

  /**
     * Lane changing.
     *
     * @param iterationCount the iteration count
     * @param dt the dt
     * @param time the time
     */
  public void laneChanging(long iterationCount, double dt, double time) {
    for (final VehicleContainer vehContainerLane : vehContainers) {
      stagedVehicles.clear();
      final List<Vehicle> vehiclesOnLane = vehContainerLane.getVehicles();
      for (final Vehicle veh : vehiclesOnLane) {
        if (veh.considerLaneChanging(dt, vehContainers)) {
          stagedVehicles.add(veh);
        }
      }
      for (final Vehicle veh : stagedVehicles) {
        vehContainers.get(veh.getLane()).removeVehicle(veh);
        vehContainers.get(veh.getTargetLane()).add(veh);
      }
    }
  }

  /**
     * Update upstream boundary.
     *
     * @param iterationCount the iteration count
     * @param dt the dt
     * @param time the time
     */
  public void updateUpstreamBoundary(long iterationCount, double dt, double time) {
    upstreamBoundary.update(iterationCount, dt, time);
  }

  /**
     * Check for inconsistencies.
     *
     * @param iterationCount the iteration count
     * @param time the time
     * @param isWithCrashExit the is with crash exit
     */
  public void checkForInconsistencies(long iterationCount, double time, boolean isWithCrashExit) {
    for (int laneIndex = 0, laneIndexMax = vehContainers.size(); laneIndex < laneIndexMax; laneIndex++) {
      final VehicleContainer vehContainerLane = vehContainers.get(laneIndex);
      final List<Vehicle> vehiclesOnLane = vehContainerLane.getVehicles();
      for (int i = 0, N = vehiclesOnLane.size(); i < N; i++) {
        final Moveable egoVeh = vehiclesOnLane.get(i);
        final Moveable vehFront = vehContainerLane.getLeader(egoVeh);
        final double netDistance = egoVeh.getNetDistance(vehFront);
        if (netDistance < 0) {
          logger.error("#########################################################");
          logger.error("Crash of Vehicle i = {} at x = {}m", i, egoVeh.getPosition());
          if (vehFront != null) {
            logger.error("with veh in front at x = {} on lane = {}", vehFront.getPosition(), egoVeh.getLane());
          }
          logger.error("roadID = {}", getId());
          logger.error("net distance  = {}", netDistance);
          logger.error("lane index    = {}", laneIndex);
          logger.error("container.size = {}", vehiclesOnLane.size());
          final StringBuilder msg = new StringBuilder("\n");
          for (int j = Math.max(0, i - 8), M = vehiclesOnLane.size(); j <= Math.min(i + 8, M - 1); j++) {
            final Moveable veh = vehiclesOnLane.get(j);
            msg.append(String.format("veh=%d, pos=%6.2f, speed=%4.2f, accModel=%4.3f, length=%3.1f, lane=%d, id=%d%n", j, veh.getPosition(), veh.getSpeed(), veh.accModel(), veh.getLength(), veh.getLane(), veh.getId()));
          }
          logger.error(msg.toString());
          if (isWithCrashExit) {
            logger.error(" !!! exit after crash !!! ");
            System.exit(-99);
          }
        }
      }
    }
  }
}