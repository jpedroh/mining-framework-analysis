package org.movsim.simulator;
import java.util.List;
import java.util.Map;
import org.movsim.input.InputData;
import org.movsim.input.ProjectMetaData;
import org.movsim.input.XmlReaderSimInput;
import org.movsim.input.file.opendrive.OpenDriveReader;
import org.movsim.input.model.RoadInput;
import org.movsim.input.model.SimulationInput;
import org.movsim.input.model.simulation.TrafficCompositionInputData;
import org.movsim.input.model.simulation.TrafficSourceData;
import org.movsim.output.LoopDetectors;
import org.movsim.output.SimObservables;
import org.movsim.output.SimOutput;
import org.movsim.roadmappings.RoadMappingPolyS;
import org.movsim.simulator.roadnetwork.RoadMapping;
import org.movsim.simulator.roadnetwork.RoadNetwork;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.movsim.simulator.roadnetwork.SpeedLimits;
import org.movsim.simulator.roadnetwork.TrafficLights;
import org.movsim.simulator.roadnetwork.UpstreamBoundary;
import org.movsim.simulator.vehicles.VehicleGenerator;
import org.movsim.utilities.MyRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Interface Simulator.
 */
public class Simulator implements Runnable {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(Simulator.class);

  private static Simulator instance = new Simulator();

  private double time;

  private long iterationCount;

  private double timestep;

  /** The duration of the simulation. */
  private double tMax;

  /** The sim output. */
  private SimOutput simOutput;

  /** The sim input. */
  private InputData inputData;

  /** The vehicle generator. */
  private VehicleGenerator vehGenerator;

  private String projectName;

  private long startTimeMillis;

  private RoadNetwork roadNetwork;

  /**
     * Instantiates a new simulator impl.
     */
  private Simulator() {
    inputData = new InputData();
    roadNetwork = new RoadNetwork();
  }

  public static Simulator getInstance() {
    return instance;
  }

  public void initialize() {
    logger.info("Copyright \'\u00a9\' by Arne Kesting, Martin Treiber, Ralph Germ and Martin Budden (2011)");
    final ProjectMetaData projectMetaData = inputData.getProjectMetaData();
    projectName = projectMetaData.getProjectName();
    final String path = projectMetaData.getPathToProjectXmlFile();
    final String xodrFileName = projectMetaData.getXodrFilename();
    final String xodrPath = projectMetaData.getXodrPath();
    final String xmlFileName = xodrPath + xodrFileName;
    logger.info("try to load ", xmlFileName);
    final boolean loaded = OpenDriveReader.loadRoadNetwork(roadNetwork, xmlFileName);
    if (loaded == false) {
      logger.error("failed to load ", xmlFileName);
    }
    logger.info("done with road network parsing");
    final XmlReaderSimInput xmlReader = new XmlReaderSimInput(inputData);
    final SimulationInput simInput = inputData.getSimulationInput();
    this.timestep = simInput.getTimestep();
    this.tMax = simInput.getMaxSimTime();
    MyRandom.initialize(simInput.isWithFixedSeed(), simInput.getRandomSeed());
    final List<TrafficCompositionInputData> heterogenInputData = simInput.getTrafficCompositionInputData();
    final boolean isWithFundDiagramOutput = simInput.isWithWriteFundamentalDiagrams();
    vehGenerator = new VehicleGenerator(projectMetaData, inputData, heterogenInputData, isWithFundDiagramOutput);
    final boolean isWithCrashExit = simInput.isWithCrashExit();
    roadNetwork.setWithCrashExit(isWithCrashExit);
    final Map<Long, RoadInput> roadInputMap = inputData.getSimulationInput().getRoadInput();
    if (loaded == false && roadInputMap.size() == 1) {
      final RoadInput roadinput = roadInputMap.values().iterator().next();
      final int laneCount = 1;
      final double roadLength = 1500;
      final RoadMapping roadMapping = new RoadMappingPolyS(laneCount, 10, 50, 50, 100.0 / Math.PI, roadLength);
      final RoadSegment roadSegment = new RoadSegment(roadMapping);
      addInputToRoadSegment(roadSegment, roadinput);
      roadSegment.setUserId("1");
      roadSegment.addDefaultSink();
      roadNetwork.add(roadSegment);
    } else {
      for (final RoadInput roadinput : roadInputMap.values()) {
        RoadSegment roadSegment = roadNetwork.findById((int) roadinput.getId());
        if (roadSegment != null) {
          addInputToRoadSegment(roadSegment, roadinput);
        }
      }
    }
    reset();
  }

  /**
     * Reset.
     */
  public void reset() {
    time = 0;
    iterationCount = 0;
    simOutput = new SimOutput(inputData, roadNetwork);
  }

  /**
     * Add input data to road segment.
     * 
     * Note by rules of encapsulation this function is NOT a member of RoadSegment, since RoadSegment
     * should not be aware of form of XML file or RoadInput data structure.
     * @param roadSegment
     * @param roadinput
     */
  private void addInputToRoadSegment(RoadSegment roadSegment, RoadInput roadinput) {
    final TrafficSourceData trafficSourceData = roadinput.getTrafficSourceData();
    final UpstreamBoundary upstreamBoundary = new UpstreamBoundary(roadSegment.id(), vehGenerator, roadSegment, trafficSourceData, inputData.getProjectMetaData().getProjectName());
    roadSegment.setUpstreamBoundary(upstreamBoundary);
    final TrafficLights trafficLights = new TrafficLights(projectName, roadinput.getTrafficLightsInput());
    roadSegment.setTrafficLights(trafficLights);
    final SpeedLimits speedLimits = new SpeedLimits(roadinput.getSpeedLimitInputData());
    roadSegment.setSpeedLimits(speedLimits);
    final LoopDetectors loopDetectors = new LoopDetectors(roadSegment.id(), projectName, roadinput.getDetectorInput());
    roadSegment.setLoopDetectors(loopDetectors);
  }

  @Override public void run() {
    logger.info("Simulator.run: start simulation at {} seconds of simulation project={}", time, projectName);
    startTimeMillis = System.currentTimeMillis();
    simOutput.update(iterationCount, time, timestep);
    while (!isSimulationRunFinished()) {
      updateTimestep();
    }
    logger.info(String.format("Simulator.run: stop after time = %.2fs = %.2fh of simulation project=%s", time, time / 3600, projectName));
    final double elapsedTime = 0.001 * (System.currentTimeMillis() - startTimeMillis);
    logger.info(String.format("time elapsed = %.3fs --> simulation time warp = %.2f, time per 1000 update steps=%.3fs", elapsedTime, time / elapsedTime, 1000 * elapsedTime / iterationCount));
  }

  /**
     * Stop this run.
     * 
     * @return true, if successful
     */
  public boolean isSimulationRunFinished() {
    return (time > tMax);
  }

  public void updateTimestep() {
    time += timestep;
    iterationCount++;
    if (iterationCount % 100 == 0) {
      logger.info(String.format("Simulator.update :time = %.2fs = %.2fh, dt = %.2fs, projectName=%s", time, time / 3600, timestep, projectName));
    }
    roadNetwork.timeStep(timestep, time, iterationCount);
    simOutput.update(iterationCount, time, timestep);
  }

  public long iterationCount() {
    return iterationCount;
  }

  public double time() {
    return time;
  }

  public double timestep() {
    return timestep;
  }

  public InputData getSimInput() {
    return inputData;
  }

  public SimObservables getSimObservables() {
    return simOutput;
  }

  public RoadNetwork getRoadNetwork() {
    return roadNetwork;
  }
}