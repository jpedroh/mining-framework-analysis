package org.movsim.simulator.roadSection.impl;
import java.util.ArrayList;
import java.util.List;
import org.movsim.input.InputData;
import org.movsim.input.model.RoadInput;
import org.movsim.input.model.SimulationInput;
import org.movsim.input.model.simulation.DetectorInput;
import org.movsim.input.model.simulation.ICMacroData;
import org.movsim.input.model.simulation.ICMicroData;
import org.movsim.input.model.simulation.RampData;
import org.movsim.input.model.simulation.SimpleRampData;
import org.movsim.output.LoopDetector;
import org.movsim.output.impl.LoopDetectors;
import org.movsim.simulator.Constants;
import org.movsim.simulator.impl.MyRandom;
import org.movsim.simulator.roadSection.InitialConditionsMacro;
import org.movsim.simulator.roadSection.OfframpImpl;
import org.movsim.simulator.roadSection.RoadSection;
import org.movsim.simulator.roadSection.SpeedLimits;
import org.movsim.simulator.roadSection.TrafficLight;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.VehicleGenerator;
import org.movsim.simulator.vehicles.VehiclePrototype;
import org.movsim.simulator.vehicles.impl.VehicleContainerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RoadSectionImpl.
 */
public class RoadSectionImpl extends AbstractRoadSection implements RoadSection {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(RoadSectionImpl.class);

  private TrafficLightsImpl trafficLights;

  /** The speedlimits. */
  private SpeedLimits speedlimits;

  /** The detectors. */
  private LoopDetectors detectors = null;

  private int countVehiclesToOfframp;

  /**
     * Instantiates a new road section impl.
     *
     * @param inputData the input data
     * @param vehGenerator the veh generator
     */
  public RoadSectionImpl(final InputData inputData, final VehicleGenerator vehGenerator) {
    super(inputData, vehGenerator);
    logger.info("Cstr. RoadSectionImpl");
    initialize(inputData);
    if (Math.abs(dt - vehGenerator.requiredTimestep()) > Constants.SMALL_VALUE) {
      this.dt = vehGenerator.requiredTimestep();
      logger.info("model requires specific integration timestep. sets to dt={}", dt);
    }
  }

  /**
     * Initialize.
     * 
     * @param inputData
     *            the input data
     */
  private void initialize(InputData inputData) {
    countVehiclesToOfframp = 0;
    vehContainers = new ArrayList<VehicleContainer>();
    for (int laneIndex = 0; laneIndex < nLanes; laneIndex++) {
      vehContainers.add(new VehicleContainerImpl(laneIndex));
    }
    final RoadInput roadInput = inputData.getSimulationInput().getSingleRoadInput();
    upstreamBoundary = new UpstreamBoundaryImpl(vehGenerator, vehContainers, roadInput.getUpstreamBoundaryData(), inputData.getProjectMetaData().getProjectName());
    flowConsBottlenecks = new FlowConservingBottlenecksImpl(roadInput.getFlowConsBottleneckInputData());
    speedlimits = new SpeedLimitsImpl(roadInput.getSpeedLimitInputData());
    trafficLights = new TrafficLightsImpl(inputData.getProjectMetaData().getProjectName(), roadInput.getTrafficLightsInput());
    final DetectorInput detInput = roadInput.getDetectorInput();
    if (detInput.isWithDetectors()) {
      detectors = new LoopDetectors(inputData.getProjectMetaData().getProjectName(), detInput);
    }
    initialConditions(inputData.getSimulationInput());
  }

  /**
     * Initial conditions.
     * 
     * @param simInput
     *            the sim input
     */
  private void initialConditions(SimulationInput simInput) {
    final List<ICMacroData> icMacroData = simInput.getSingleRoadInput().getIcMacroData();
    if (!icMacroData.isEmpty()) {
      logger.debug("choose macro initial conditions: generate vehicles from macro-density ");
      final InitialConditionsMacro icMacro = new InitialConditionsMacroImpl(icMacroData);
      final double xLocalMin = 0;
      double xLocal = roadLength;
      while (xLocal > xLocalMin) {
        final VehiclePrototype vehPrototype = vehGenerator.getVehiclePrototype();
        final double rhoLocal = icMacro.rho(xLocal);
        double speedInit = icMacro.vInit(xLocal);
        if (speedInit == 0) {
          speedInit = vehPrototype.getEquilibriumSpeed(rhoLocal);
        }
        final int laneEnter = Constants.MOST_RIGHT_LANE;
        final Vehicle veh = vehGenerator.createVehicle(vehPrototype);
        vehContainers.get(Constants.MOST_RIGHT_LANE).add(veh, xLocal, speedInit);
        logger.debug("init conditions macro: rhoLoc={}/km, xLoc={}", 1000 * rhoLocal, xLocal);
        xLocal -= 1 / rhoLocal;
      }
    } else {
      logger.debug(("choose micro initial conditions"));
      final List<ICMicroData> icSingle = simInput.getSingleRoadInput().getIcMicroData();
      for (final ICMicroData ic : icSingle) {
        final double posInit = ic.getX();
        final double speedInit = ic.getSpeed();
        final String vehTypeFromFile = ic.getLabel();
        final int laneInit = ic.getInitLane();
        final Vehicle veh = (vehTypeFromFile.isEmpty()) ? vehGenerator.createVehicle() : vehGenerator.createVehicle(vehTypeFromFile);
        vehContainers.get(Constants.MOST_RIGHT_LANE).add(veh, posInit, speedInit);
        logger.info("set vehicle with label = {}", veh.getLabel());
      }
    }
  }

  public List<RoadSection> rampFactory(final InputData inputData) {
    List<RoadSection> ramps = new ArrayList<RoadSection>();
    final String projectName = inputData.getProjectMetaData().getProjectName();
    final List<SimpleRampData> simpleOnrampData = inputData.getSimulationInput().getSingleRoadInput().getSimpleRamps();
    int rampIndex = 1;
    for (final SimpleRampData rmpSimpl : simpleOnrampData) {
      ramps.add(new OnrampImpl(rmpSimpl, vehGenerator, vehContainers.get(Constants.MOST_RIGHT_LANE), projectName, rampIndex));
      rampIndex++;
    }
    final List<RampData> rampData = inputData.getSimulationInput().getSingleRoadInput().getRamps();
    for (final RampData rmp : rampData) {
      if ((rmp.getId() > 0)) {
        ramps.add(new OnrampMobilImpl(rmp, vehGenerator, vehContainers.get(Constants.MOST_RIGHT_LANE), projectName, rampIndex));
        rampIndex++;
      }
      if ((rmp.getId() < 0)) {
        ramps.add(new OfframpImpl(rmp));
        rampIndex++;
      }
    }
    return ramps;
  }

  /**
     * Update downstream boundary.
     */
  public void updateDownstreamBoundary() {
    for (VehicleContainer vehContainerLane : vehContainers) {
      vehContainerLane.removeVehiclesDownstream(roadLength);
    }
  }

  public void laneChangingToOfframps(List<RoadSection> ramps, long iterationCount, double dt, double time) {
    final double fractionToOfframp = 0.1;
    for (final RoadSection rmp : ramps) {
      if (rmp instanceof OfframpImpl) {
        stagedVehicles.clear();
        final VehicleContainer vehContainerRightLane = vehContainers.get(Constants.MOST_RIGHT_LANE);
        final VehicleContainer rmpContainer = rmp.getVehContainer(rmp.getNumberOfLanes() - 1);
        for (final Vehicle veh : vehContainerRightLane.getVehicles()) {
          final double pos = veh.getPosition();
          final double mergingZone = 0.4;
          if (pos > rmp.getRampPositionToMainroad() && pos < rmp.getRampPositionToMainroad() + mergingZone * rmp.getRampMergingLength()) {
            final double oldPos = veh.getPosition();
            final double newPos = veh.getPosition() - rmp.getRampPositionToMainroad();
            veh.setPosition(newPos);
            final boolean isSafeChange = veh.getLaneChangingModel().isMandatoryLaneChangeSafe(dt, rmpContainer);
            veh.setPosition(oldPos);
            final double fractionOfLeavingVehicles = upstreamBoundary.getEnteringVehCounter() == 0 ? 0 : countVehiclesToOfframp / (double) upstreamBoundary.getEnteringVehCounter();
            final boolean isDesired = fractionOfLeavingVehicles < fractionToOfframp;
            logger.debug("fraction of leaving vehicles={}, upstreamCounter={}", fractionOfLeavingVehicles, upstreamBoundary.getEnteringVehCounter());
            if (isSafeChange && isDesired) {
              stagedVehicles.add(veh);
              countVehiclesToOfframp++;
            }
          }
        }
        for (final Vehicle veh : stagedVehicles) {
          final double xInit = veh.getPosition() - rmp.getRampPositionToMainroad();
          final double vInit = veh.getSpeed();
          vehContainers.get(Constants.MOST_RIGHT_LANE).removeVehicle(veh);
          rmpContainer.addFromToRamp(veh, xInit, vInit, Constants.TO_LEFT);
        }
      }
    }
    stagedVehicles.clear();
  }

  /**
     * Update position and speed.
     *
     * @param iterationCount the iteration count
     * @param dt the dt
     * @param time the time
     */
  public void updatePositionAndSpeed(int iterationCount, double dt, double time) {
    for (VehicleContainer vehContainerLane : vehContainers) {
      for (final Vehicle veh : vehContainerLane.getVehicles()) {
        veh.updatePostionAndSpeed(dt);
      }
    }
  }

  /**
     * Update road conditions.
     *
     * @param iterationCount the iteration count
     * @param time the time
     */
  public void updateRoadConditions(long iterationCount, double time) {
    trafficLights.update(iterationCount, time, vehContainers);
    updateSpeedLimits(vehContainers);
  }

  /**
     * Update speed limits.
     *
     * @param vehContainers the veh containers
     */
  private void updateSpeedLimits(List<VehicleContainer> vehContainers) {
    if (!speedlimits.isEmpty()) {
      for (VehicleContainer vehContainerLane : vehContainers) {
        for (final Vehicle veh : vehContainerLane.getVehicles()) {
          final double pos = veh.getPosition();
          veh.setSpeedlimit(speedlimits.calcSpeedLimit(pos));
        }
      }
    }
  }

  /**
     * Update onramps.
     *
     * @return the traffic lights
     */
  @Override public List<TrafficLight> getTrafficLights() {
    return trafficLights.getTrafficLights();
  }

  @Override public List<LoopDetector> getLoopDetectors() {
    return detectors.getDetectors();
  }

  @Override public void updateDetectors(long iterationCount, double dt, double time) {
    detectors.update(iterationCount, time, dt, vehContainers);
  }

  @Override public double getRampMergingLength() {
    return 0;
  }

  @Override public double getRampPositionToMainroad() {
    return 0;
  }
}