package org.movsim.simulator.roadnetwork;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.CheckForNull;
import org.movsim.simulator.SimulationTimeStep;
import org.movsim.simulator.roadnetwork.routing.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterable collection of the road segments in the road network.
 */
public class RoadNetwork implements SimulationTimeStep, Iterable<RoadSegment> {
  /** The Constant LOG. */
  private static final Logger LOG = LoggerFactory.getLogger(RoadNetwork.class);

  private final ArrayList<RoadSegment> roadSegments = new ArrayList<>();

  private String name;

  private boolean isWithCrashExit;

  /**
     * Sets the name of the road network.
     * 
     * @param name
     */
  public final void setName(String name) {
    this.name = name;
  }

  /**
     * Returns the name of the road network.
     * 
     * @return the name of the road network
     */
  public final String name() {
    return name;
  }

  /**
     * Given its id, find a road segment in the road network.
     * 
     * @param id
     * @return the road segment with the given id
     */
  public RoadSegment findById(int id) {
    for (final RoadSegment roadSegment : roadSegments) {
      if (roadSegment.id() == id) {
        return roadSegment;
      }
    }
    return null;
  }

  /**
     * Given its userId, find a road segment in the road network.
     * 
     * @param userId
     * @return the road segment with the given userId
     */
  @CheckForNull public RoadSegment findByUserId(String userId) {
    for (final RoadSegment roadSegment : roadSegments) {
      if (roadSegment.userId() != null && roadSegment.userId().equals(userId)) {
        return roadSegment;
      }
    }
    return null;
  }

  /**
     * Clear the road network so that it is empty and ready to accept new RoadSegments, Vehicles, sources, sinks and
     * junctions.
     */
  public void clear() {
    name = null;
    RoadSegment.resetNextId();
    roadSegments.clear();
  }

  /**
     * Called when the system is running low on memory, and would like actively running process to try to tighten their
     * belts.
     */
  public void onLowMemory() {
    roadSegments.trimToSize();
  }

  /**
     * Returns the number of RoadSegments in the road network.
     * 
     * @return the number of RoadSegments in the road network
     */
  public final int size() {
    return roadSegments.size();
  }

  /**
     * Adds a road segment to the road network.
     * 
     * @param roadSegment
     * @return roadSegment for convenience
     */
  public RoadSegment add(RoadSegment roadSegment) {
    assert roadSegment != null;
    assert roadSegment.eachLaneIsSorted();
    roadSegments.add(roadSegment);
    return roadSegment;
  }

  /**
     * Returns an iterator over all the road segments in the road network.
     * 
     * @return an iterator over all the road segments in the road network
     */
  @Override public Iterator<RoadSegment> iterator() {
    return roadSegments.iterator();
  }

  /**
     * The main timestep of the simulation. Updates the vehicle accelerations, movements, lane-changing decisions and the boundary
     * conditions.
     * 
     * <p>
     * Each update step is applied in parallel to all vehicles <i>of the entire network</i>. Otherwise, inconsistencies would occur. In
     * particular, the complete old state (positions, lanes, speeds ...) is made available during the complete update step of one timestep.
     * Then the inflow is performed for each road segment, adding any new vehicles supplied by any traffic sources. Then the outflow is
     * performed for each road segment, moving vehicles onto the next road segment (or removing them entirely from the road network) when
     * required. Finally the 'signal points' are updated.
     * </p>
     * 
     * <p>
     * The steps themselves are grouped into two main blocks and an auxillary block:
     * <ol type="a">
     * <li>Longitudinal update:</li>
     * <ol type="i">
     * <li>Calculate accelerations</li>
     * <li>update speeds
     * <li>update positions
     * </ol>
     * <li>Discrete Decision update:</li>
     * <ol type="i">
     * <li>Determine decisions (whether to change lanes, decide to cruise/stop at a traffic light, etc.)</li>
     * <li>perform decisions (do the lane changes, cruising/stopping at traffic light, etc.)</li>
     * </ol>
     * 
     * <li>Do the related bookkeeping (update of inflow and outflow at boundaries) and update virtual detectors</li>
     * </ol>
     * </p>
     * 
     * <p>
     * The blocks can be swapped as long as each block is done serially for the whole network in exactly the above order (i),(ii),(iii).
     * </p>
     * 
     * @param dt
     *            simulation time interval, seconds.
     * @param simulationTime
     *            the current logical time in the simulation
     * @param iterationCount
     *            the counter of performed update steps
     */
  @Override public void timeStep(double dt, double simulationTime, long iterationCount) {
    LOG.debug("called timeStep: time={}, timestep={}", simulationTime, dt);
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.updateRoadConditions(dt, simulationTime, iterationCount);
    }
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.makeLaneChanges(dt, simulationTime, iterationCount);
    }
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.updateVehicleAccelerations(dt, simulationTime, iterationCount);
    }
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.updateVehiclePositionsAndSpeeds(dt, simulationTime, iterationCount);
    }
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.checkForInconsistencies(simulationTime, iterationCount, isWithCrashExit);
    }
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.outFlow(dt, simulationTime, iterationCount);
    }
    for (final RoadSegment roadSegment : roadSegments) {
      roadSegment.inFlow(dt, simulationTime, iterationCount);
      roadSegment.updateSignalPointsAfterOutflowAndInflow(simulationTime);
    }
  }

  public void setWithCrashExit(boolean isWithCrashExit) {
    this.isWithCrashExit = isWithCrashExit;
  }

  /**
     * Returns the number of vehicles on this road network.
     * 
     * @return the number of vehicles on this road network
     */
  public int vehicleCount() {
    int vehicleCount = 0;
    for (final RoadSegment roadSegment : roadSegments) {
      vehicleCount += roadSegment.getVehicleCount();
    }
    return vehicleCount;
  }

  public int getObstacleCount() {
    int obstacleCount = 0;
    for (final RoadSegment roadSegment : roadSegments) {
      obstacleCount += roadSegment.getObstacleCount();
    }
    return obstacleCount;
  }

  public int getStoppedVehicleCount() {
    int stoppedVehicleCount = 0;
    for (final RoadSegment roadSegment : roadSegments) {
      stoppedVehicleCount += roadSegment.getStoppedVehicleCount();
    }
    return stoppedVehicleCount;
  }

  public double vehiclesMeanSpeed() {
    double averageSpeed = 0;
    for (final RoadSegment roadSegment : roadSegments) {
      averageSpeed += roadSegment.meanSpeed();
    }
    return averageSpeed / roadSegments.size();
  }

  /**
     * Returns the number of obstacles on this road network.
     * 
     * @return the number of obstacles on this road network
     */
  public int obstacleCount() {
    int obstacleCount = 0;
    for (final RoadSegment roadSegment : roadSegments) {
      obstacleCount += roadSegment.obstacleCount();
    }
    return obstacleCount;
  }

  /**
     * Returns the number of obstacles for the given route.
     * 
     * @return the number of obstacles on the given route
     */
  public int obstacleCount(Route route) {
    int obstacleCount = 0;
    for (final RoadSegment roadSegment : roadSegments) {
      obstacleCount += roadSegment.obstacleCount();
    }
    return obstacleCount;
  }

  /**
     * Asserts the road network's class invariant. Used for debugging.
     */
  public boolean assertInvariant() {
    for (final RoadSegment roadSegment : roadSegments) {
      assert roadSegment.assertInvariant();
    }
    return true;
  }

  /**
     * Returns the number of vehicles on route.
     * 
     * @return the number of vehicles on given route.
     */
  public static int vehicleCount(Route route) {
    int vehicleCount = 0;
    for (final RoadSegment roadSegment : route) {
      vehicleCount += roadSegment.getVehicleCount();
    }
    return vehicleCount;
  }

  /**
     * Returns the total travel time of all vehicles on this road network, including those that have exited.
     * 
     * @return the total vehicle travel time
     */
  public double totalVehicleTravelTime() {
    double totalVehicleTravelTime = 0.0;
    for (RoadSegment roadSegment : roadSegments) {
      totalVehicleTravelTime += roadSegment.totalVehicleTravelTime();
      if (roadSegment.sink() != null) {
        totalVehicleTravelTime += roadSegment.sink().totalVehicleTravelTime();
      }
    }
    return totalVehicleTravelTime;
  }

  public static double instantaneousTravelTime(Route route) {
    double instantaneousTravelTime = 0;
    for (final RoadSegment roadSegment : route) {
      instantaneousTravelTime += roadSegment.instantaneousTravelTime();
    }
    return instantaneousTravelTime;
  }

  /**
     * Returns the total travel distance of all vehicles on this road network, including those that have exited.
     * 
     * @return the total vehicle travel distance
     */
  public double totalVehicleTravelDistance() {
    double totalVehicleTravelDistance = 0.0;
    for (RoadSegment roadSegment : roadSegments) {
      totalVehicleTravelDistance += roadSegment.totalVehicleTravelDistance();
      if (roadSegment.sink() != null) {
        totalVehicleTravelDistance += roadSegment.sink().totalVehicleTravelDistance();
      }
    }
    return totalVehicleTravelDistance;
  }

  public static double totalVehicleTravelDistance(Route route) {
    double totalVehicleTravelDistance = 0.0;
    for (final RoadSegment roadSegment : route) {
      totalVehicleTravelDistance += roadSegment.totalVehicleTravelDistance();
      if (roadSegment.sink() != null) {
        totalVehicleTravelDistance += roadSegment.sink().totalVehicleTravelDistance();
      }
    }
    return totalVehicleTravelDistance;
  }

  /**
     * Returns the total fuel used by all vehicles on this road network, including those that have exited.
     * 
     * @return the total vehicle fuel used
     */
  public double totalVehicleFuelUsedLiters() {
    double totalVehicleFuelUsedLiters = 0.0;
    for (RoadSegment roadSegment : roadSegments) {
      totalVehicleFuelUsedLiters += roadSegment.totalVehicleFuelUsedLiters();
      if (roadSegment.sink() != null) {
        totalVehicleFuelUsedLiters += roadSegment.sink().totalFuelUsedLiters();
      }
    }
    return totalVehicleFuelUsedLiters;
  }

  public static double instantaneousFuelUsedLiters(Route route) {
    double instantaneousConsumption = 0;
    for (final RoadSegment roadSegment : route) {
      instantaneousConsumption += roadSegment.instantaneousConsumptionLitersPerSecond();
    }
    return instantaneousConsumption;
  }
}