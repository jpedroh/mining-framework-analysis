package org.movsim.simulator.roadSection.impl;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.movsim.input.InputData;
import org.movsim.input.model.simulation.FlowConservingBottleneckDataPoint;
import org.movsim.input.model.simulation.RampData;
import org.movsim.output.LoopDetector;
import org.movsim.simulator.Constants;
import org.movsim.simulator.roadSection.RoadSection;
import org.movsim.simulator.roadSection.TrafficLight;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.VehicleGenerator;
import org.movsim.simulator.vehicles.impl.VehicleContainerImpl;
import org.movsim.utilities.impl.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OnrampImpl.
 */
public class OnrampMobilImpl extends AbstractRoadSection implements RoadSection {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(OnrampImpl.class);

  private static final String extensionFormat = ".S%d_log.csv";

  private static final String outputHeading = Constants.COMMENT_CHAR + "     t[s], lane,  xEnter[m],    v[km/h],   qBC[1/h],  count,  queue\n";

  private static final String outputFormat = "%10.2f, %4d, %10.2f, %10.2f, %10.2f, %6d, %6d%n";

  /** The Constant MINSPACE_MERGE_M. */
  final static double MINSPACE_MERGE_M = 2.0;

  /** The Constant RAMP_VEL_REDUCEFACTOR. */
  final static double RAMP_VEL_REDUCEFACTOR = 0.6;

  final static int N_LANES = 1;

  /** The main veh container. */
  private final VehicleContainer mainVehContainerMostRightLane;

  /** The x center position of the ramp. */
  private final double mergeLength;

  /** The x up ramp marks the start of the ramp. */
  private final double xUpRamp;

  /** The x down ramp marks the end of the ramp. */
  private final double xOffsetMain;

  private final double xToMain;

  /** The n wait. */
  private double nWait;

  /** The fstr logging. */
  PrintWriter fstrLogging;

  /** The x enter last merge. status of last merging vehicle */
  private double xEnterLastMerge;

  /** The v enter last merge. */
  private double vEnterLastMerge;

  /** The merge count. */
  private int mergeCount;

  private final boolean isWithCrashExit = true;

  /**
     * Instantiates a new onramp impl.
     * 
     * @param rampData
     *            the ramp data
     * @param vehGenerator
     *            the veh generator
     * @param mainVehContainerMostRightLane
     *            the main veh container
     * @param projectName
     *            the project name
     * @param rampIndex
     *            the ramp index
     */
  public OnrampMobilImpl(final RampData rampData, final VehicleGenerator vehGenerator, final VehicleContainer mainVehContainerMostRightLane, String projectName, int rampIndex) {
    super(rampData, vehGenerator);
    mergeLength = rampData.getRampMergingLength();
    xUpRamp = roadLength - mergeLength;
    xToMain = rampData.getRampStartPosition();
    xOffsetMain = rampData.getRampStartPosition() - xUpRamp;
    logger.debug("xOffsetMain = {}", xOffsetMain);
    if (xOffsetMain < 0) {
      logger.error("xOffsetMain = {}. negative values not allowed.", xOffsetMain);
    }
    this.mainVehContainerMostRightLane = mainVehContainerMostRightLane;
    vehContainers = new ArrayList<VehicleContainer>();
    vehContainers.add(new VehicleContainerImpl(Constants.MOST_RIGHT_LANE));
    setObstacleAtEndOfLane();
    flowConsBottlenecks = new FlowConservingBottlenecksImpl(new ArrayList<FlowConservingBottleneckDataPoint>());
    upstreamBoundary = new UpstreamBoundaryImpl(vehGenerator, vehContainers, rampData.getUpstreamBoundaryData(), projectName);
    mergeCount = 0;
    if (rampData.withLogging()) {
      final int roadCount = 1;
      final String filename = projectName + String.format(extensionFormat, rampIndex + roadCount);
      fstrLogging = FileUtils.getWriter(filename);
      fstrLogging.printf(outputHeading);
      fstrLogging.flush();
    }
    nWait = 0;
  }

  @Override public void laneChanging(long iterationCount, double dt, double time) {
    stagedVehicles.clear();
    assert vehContainers.size() == 1;
    final VehicleContainer vehContainer = vehContainers.get(0);
    for (Vehicle veh : vehContainer.getVehicles()) {
      if (!veh.getLabel().equals(Constants.OBSTACLE_KEY_NAME) && tryToMergeToMainroad(veh)) {
        stagedVehicles.add(veh);
      }
    }
    for (final Vehicle veh : stagedVehicles) {
      vehContainer.removeVehicle(veh);
      mainVehContainerMostRightLane.addFromToRamp(veh, veh.getPosition(), veh.getSpeed(), Constants.TO_RIGHT);
    }
  }

  /**
     * Try to merge to mainroad.
     *
     * @param veh the veh
     * @return true, if successful
     */
  private boolean tryToMergeToMainroad(final Vehicle veh) {
    final double pos = veh.getPosition();
    if (pos > xUpRamp) {
      final double newPos = pos + xOffsetMain;
      veh.setPosition(newPos);
      logger.debug("mergeToMainroad: veh in ramp region! pos = {}, positionOnMainraod = {}", pos, newPos);
      final boolean isSafeChange = veh.getLaneChangingModel().isMandatoryLaneChangeSafe(dt, mainVehContainerMostRightLane);
      if (isSafeChange) {
        logger.debug("safeChange --> pos = {}, positionOnMainraod = {}", pos, newPos);
        return true;
      } else {
        veh.setPosition(pos);
      }
    }
    return false;
  }

  /**
     * Sets the obstacle at end of lane.
     */
  private void setObstacleAtEndOfLane() {
    final Vehicle obstacle = vehGenerator.createVehicle(Constants.OBSTACLE_KEY_NAME);
    final double posInit = roadLength;
    final double speedInit = 0;
    vehContainers.get(0).add(obstacle, posInit, speedInit);
    logger.debug("set obstacle at pos={} with length={}", posInit, obstacle.getLength());
  }

  @Override public void updateRoadConditions(long iterationCount, double time) {
  }

  @Override public void updateDownstreamBoundary() {
  }

  @Override public List<TrafficLight> getTrafficLights() {
    return null;
  }

  @Override public List<LoopDetector> getLoopDetectors() {
    return null;
  }

  @Override public void updateDetectors(long iterationCount, double dt, double simulationTime) {
  }

  @Override public double getRampMergingLength() {
    return mergeLength;
  }

  @Override public double getRampPositionToMainroad() {
    return xToMain;
  }

  @Override public List<RoadSection> rampFactory(InputData inputData) {
    return null;
  }

  @Override public void laneChangingToOfframps(List<RoadSection> ramps, long iterationCount, double dt, double time) {
  }
}