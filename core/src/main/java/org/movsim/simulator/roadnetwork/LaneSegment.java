package org.movsim.simulator.roadnetwork;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.movsim.simulator.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A LaneSegment represents a lane within a RoadSegment.
 * </p>
 * <p>
 * Lanes are of different types including traffic lanes, exit (deceleration) lanes and entrance (acceleration) lanes. Typically vehicle
 * behavior (and especially lane change behavior) is different in each type of lane.
 * </p>
 * <p>
 * The vehicles in a lane segment are stored in a sorted ArrayList. This ArrayList is kept sorted so that the vehicles in front of and
 * behind a given vehicle can be found efficiently.
 * </p>
 * <p>
 * Vehicles are sorted in order of decreasing position:
 * </p>
 * <p>
 * V[n+1].pos < V[n].pos < V[n-1].pos ... < V[1].pos < V[0].pos
 * </p>
 */
public class LaneSegment implements Iterable<Vehicle> {
  final static Logger logger = LoggerFactory.getLogger(LaneSegment.class);

  private static final boolean DEBUG = false;

  private static final int VEHICLES_PER_LANE_INITIAL_SIZE = 50;

  private final RoadSegment roadSegment;

  private LaneSegment sinkLaneSegment;

  private LaneSegment sourceLaneSegment;

  private final int lane;

  private Lanes.Type type;

  final ArrayList<Vehicle> vehicles;

  private int removedVehicleCount;

  /**
     * Constructor.
     * 
     * @param roadSegment
     * @param lane
     *            (not the laneIndex)
     */
  LaneSegment(RoadSegment roadSegment, int lane) {
    this.roadSegment = roadSegment;
    assert lane >= Lanes.MOST_INNER_LANE;
    this.lane = lane;
    vehicles = new ArrayList<>(VEHICLES_PER_LANE_INITIAL_SIZE);
    type = Lanes.Type.TRAFFIC;
  }

  /**
     * Returns the lane.
     * <p>
     * The lane is an identifier of the lane in the physical network starting with a value of 1 for the most inner lane.
     * </p>
     * 
     * @return lane, not the index of the lane in the roadSegment
     */
  public final int lane() {
    return lane;
  }

  /**
     * Sets the type of the lane.
     * 
     * @param type
     */
  public final void setType(Lanes.Type type) {
    this.type = type;
    if (type == Lanes.Type.ENTRANCE) {
      setSinkLaneSegment(null);
    }
  }

  /**
     * Returns the type of the lane.
     * 
     * @return type of lane
     */
  public final Lanes.Type type() {
    return type;
  }

  /**
     * Returns the road segment for the lane.
     * 
     * @return road segment
     */
  public final RoadSegment roadSegment() {
    return roadSegment;
  }

  /**
     * Returns the length of the lane.
     * 
     * @return length of lane
     */
  public final double roadLength() {
    return roadSegment.roadLength();
  }

  public final void setSourceLaneSegment(LaneSegment sourceLaneSegment) {
    this.sourceLaneSegment = sourceLaneSegment;
  }

  public final LaneSegment sourceLaneSegment() {
    return sourceLaneSegment;
  }

  public final void setSinkLaneSegment(LaneSegment sinkLaneSegment) {
    this.sinkLaneSegment = sinkLaneSegment;
  }

  public final LaneSegment sinkLaneSegment() {
    return sinkLaneSegment;
  }

  /**
     * Clears this lane segment of any vehicles.
     */
  public final void clearVehicles() {
    vehicles.clear();
  }

  /**
     * Returns the number of vehicles on this lane segment.
     * 
     * @return the number of vehicles on this lane segment
     */
  public final int vehicleCount() {
    return vehicles.size();
  }

  public int stoppedVehicleCount() {
    int stoppedVehicleCount = 0;
    for (final Vehicle vehicle : vehicles) {
      if (vehicle.type() != Vehicle.Type.OBSTACLE && vehicle.getSpeed() <= 0.01) {
        ++stoppedVehicleCount;
      }
    }
    return stoppedVehicleCount;
  }

  /** Returns the number of real vehicles (without 'obstacles') n this lane segment. */
  public final int vehicleCountWithoutObstacles() {
    return vehicles.size() - obstacleCount();
  }

  /**
     * Returns the number of obstacles on this lane segment.
     * 
     * @return the number of obstacles on this lane segment
     */
  public final int obstacleCount() {
    int obstacleCount = 0;
    for (final Vehicle vehicle : vehicles) {
      if (vehicle.type() == Vehicle.Type.OBSTACLE) {
        ++obstacleCount;
      }
    }
    return obstacleCount;
  }

  /**
     * Returns the total travel time of all vehicles on this lane segment.
     * 
     * @return the total vehicle travel time
     */
  public double totalVehicleTravelTime() {
    double totalVehicleTravelTime = 0;
    for (final Vehicle vehicle : vehicles) {
      totalVehicleTravelTime += vehicle.totalTravelTime();
    }
    return totalVehicleTravelTime;
  }

  /**
     * Returns the total travel distance of all vehicles on this lane segment.
     * 
     * @return the total vehicle travel distance
     */
  public double totalVehicleTravelDistance() {
    double totalVehicleTravelDistance = 0;
    for (final Vehicle vehicle : vehicles) {
      totalVehicleTravelDistance += vehicle.totalTravelDistance();
    }
    return totalVehicleTravelDistance;
  }

  /**
     * Returns the total fuel used by all vehicles on this lane segment.
     * 
     * @return the total vehicle fuel used
     */
  public double totalVehicleFuelUsedLiters() {
    double totalVehicleFuelUsedLiters = 0;
    for (final Vehicle vehicle : vehicles) {
      totalVehicleFuelUsedLiters += vehicle.totalFuelUsedLiters();
    }
    return totalVehicleFuelUsedLiters;
  }

  public double instantaneousFuelUsedLitersPerS() {
    double instFuelUsedLiters = 0;
    for (final Vehicle vehicle : vehicles) {
      instFuelUsedLiters += vehicle.getActualFuelFlowLiterPerS();
    }
    return instFuelUsedLiters;
  }

  /**
     * <p>
     * Returns the vehicle at the given index.
     * </p>
     * 
     * 
     * @param index
     * 
     * @return vehicle at given index
     */
  public Vehicle getVehicle(int index) {
    return vehicles.get(index);
  }

  /**
     * Removes the vehicle at the given index.
     * 
     * @param index
     *            index of vehicle to remove
     */
  public void removeVehicle(int index) {
    vehicles.remove(index);
  }

  /**
     * Removes the given vehicle.
     * 
     * @param vehicleToRemove
     */
  public void removeVehicle(Vehicle vehicleToRemove) {
    final long vehicleId = vehicleToRemove.getId();
    final int count = vehicles.size();
    for (int i = 0; i < count; ++i) {
      final Vehicle vehicle = vehicles.get(i);
      if (vehicle.getId() == vehicleId) {
        vehicles.remove(i);
        return;
      }
    }
  }

  /**
     * Removes the front vehicle on this lane segment.
     */
  public void removeFrontVehicleOnLane() {
    if (vehicles.size() > 0) {
      vehicles.remove(0);
    }
  }

  /**
     * Removes any vehicles that have moved past the end of this road segment.
     * 
     * @return the number of vehicles removed
     */
  public int removeVehiclesPastEnd(TrafficSink sink) {
    int count = 0;
    final double roadLength = roadSegment.roadLength();
    int vehicleCount = vehicles.size();
    while (vehicleCount > 0 && vehicles.get(0).getRearPosition() > roadLength) {
      sink.recordRemovedVehicle(vehicles.get(0));
      vehicles.remove(0);
      ++removedVehicleCount;
      --vehicleCount;
      ++count;
    }
    return count;
  }

  public Collection<? extends Vehicle> getVehiclesPastEnd(TrafficSink sink) {
    ArrayList<Vehicle> vehiclesPastEnd = new ArrayList<>();
    int index = 0;
    while (index < vehicles.size() && vehicles.get(index).getRearPosition() > roadSegment.roadLength()) {
      vehiclesPastEnd.add(vehicles.get(index));
      index++;
    }
    return vehiclesPastEnd;
  }

  /**
     * @return the removedVehicleCount
     */
  public int getRemovedVehicleCount() {
    return removedVehicleCount;
  }

  /**
     * Adds a vehicle to this lane segment.
     * 
     * @param vehicle
     */
  public void addVehicle(Vehicle vehicle) {
    assert vehicle.getSpeed() >= 0.0 : "vehicleSpeed=" + vehicle.getSpeed();
    assert vehicle.lane() == lane;
    assert vehicle.roadSegmentId() == roadSegment.id();
    assert assertInvariant();
    final int index = positionBinarySearch(vehicle.getRearPosition());
    if (index < 0) {
      vehicles.add(-index - 1, vehicle);
    } else {
      if (index == 0) {
        vehicles.add(0, vehicle);
      } else {
        assert false;
      }
    }
    assert laneIsSorted();
    assert assertInvariant();
  }

  public int addVehicleTemp(Vehicle vehicle) {
    assert vehicle.getSpeed() >= 0.0;
    assert vehicle.lane() == lane;
    assert assertInvariant();
    final int index = positionBinarySearch(vehicle.getRearPosition());
    int pos = 0;
    if (index < 0) {
      pos = -index - 1;
      vehicles.add(pos, vehicle);
    } else {
      if (index == 0) {
        vehicles.add(pos, vehicle);
      } else {
        assert false;
      }
    }
    assert laneIsSorted();
    assert assertInvariant();
    return pos;
  }

  public void appendVehicle(Vehicle vehicle) {
    assert vehicle.getFrontPosition() >= 0.0;
    assert vehicle.getSpeed() >= 0.0;
    assert vehicle.lane() == lane;
    assert vehicle.roadSegmentId() == roadSegment.id();
    assert laneIsSorted();
    assert assertInvariant();
    if (DEBUG) {
      if (vehicles.size() > 0) {
        final Vehicle lastVehicle = vehicles.get(vehicles.size() - 1);
        if (lastVehicle.getRearPosition() < vehicle.getRearPosition()) {
          assert false;
        }
      }
    }
    vehicles.add(vehicle);
    assert laneIsSorted();
    assert assertInvariant();
  }

  /**
     * Returns the rear vehicle.
     * 
     * @return the rear vehicle
     */
  public Vehicle rearVehicle() {
    final int count = vehicles.size();
    if (count > 0) {
      return vehicles.get(count - 1);
    }
    return null;
  }

  /**
     * Finds the vehicle immediately at or behind the given position.
     * 
     * @param vehiclePos
     * 
     * @return reference to the rear vehicle
     */
  public Vehicle rearVehicle(double vehiclePos) {
    final int index = positionBinarySearch(vehiclePos);
    final int insertionPoint = -index - 1;
    if (index >= 0) {
      if (index < vehicles.size()) {
        return vehicles.get(index);
      }
    } else {
      if (insertionPoint < vehicles.size()) {
        return vehicles.get(insertionPoint);
      }
    }
    if (sourceLaneSegment != null) {
      final Vehicle sourceFrontVehicle = sourceLaneSegment.frontVehicle();
      if (sourceFrontVehicle != null) {
        final Vehicle rearVehicle = new Vehicle(sourceFrontVehicle);
        rearVehicle.setFrontPosition(rearVehicle.getFrontPosition() - sourceLaneSegment.roadLength());
        return rearVehicle;
      }
    }
    return null;
  }

  public final Vehicle rearVehicle(Vehicle vehicle) {
    return rearVehicle(vehicle.getRearPosition());
  }

  public Vehicle rearVehicleOnSinkLanePosAdjusted() {
    if (sinkLaneSegment == null) {
      return null;
    }
    final Vehicle sinkRearVehicle = sinkLaneSegment.rearVehicle();
    if (sinkRearVehicle == null) {
      return null;
    }
    final Vehicle ret = new Vehicle(sinkRearVehicle);
    ret.setFrontPosition(ret.getFrontPosition() + roadSegment.roadLength());
    return ret;
  }

  Vehicle secondLastVehicleOnSinkLanePosAdjusted() {
    if (sinkLaneSegment == null) {
      return null;
    }
    final int sinkLaneVehicleCount = sinkLaneSegment.vehicleCount();
    if (sinkLaneVehicleCount < 2) {
      return null;
    }
    final Vehicle vehicle = sinkLaneSegment.getVehicle(sinkLaneVehicleCount - 2);
    final Vehicle ret = new Vehicle(vehicle);
    ret.setFrontPosition(ret.getFrontPosition() + roadSegment.roadLength());
    return ret;
  }

  /**
     * Returns the front vehicle which is the most downstream vehicle in the {@link LaneSegment}.
     * 
     * @return the front vehicle
     */
  public Vehicle frontVehicle() {
    if (vehicles.size() > 0) {
      return vehicles.get(0);
    }
    return null;
  }

  /**
     * Finds the vehicle immediately in front of the given position. That is a vehicle such that vehicle.position() >
     * vehicePos (strictly greater than). The vehicle whose position equals vehiclePos is deemed to be in the rear.
     * 
     * @param vehiclePos
     * 
     * @return reference to the front vehicle
     */
  public Vehicle frontVehicle(double vehiclePos) {
    final int index = positionBinarySearch(vehiclePos);
    final int insertionPoint = -index - 1;
    if (index > 0) {
      return vehicles.get(index - 1);
    } else {
      if (insertionPoint > 0) {
        return vehicles.get(insertionPoint - 1);
      }
    }
    if (sinkLaneSegment != null) {
      final Vehicle sinkRearVehicle = sinkLaneSegment.rearVehicle();
      if (sinkRearVehicle != null) {
        final Vehicle frontVehicle = new Vehicle(sinkRearVehicle);
        frontVehicle.setFrontPosition(frontVehicle.getFrontPosition() + roadSegment.roadLength());
        return frontVehicle;
      }
    }
    return null;
  }

  /**
     * Returns the vehicle in front of the given vehicle.
     * 
     * @param vehicle
     * @return the next downstream vehicle
     */
  public final Vehicle frontVehicle(Vehicle vehicle) {
    return frontVehicle(vehicle.getRearPosition());
  }

  private int positionBinarySearch(double vehiclePos) {
    int low = 0;
    int high = vehicles.size() - 1;
    while (low <= high) {
      final int mid = (low + high) >> 1;
      final double rearPos = vehicles.get(mid).getRearPosition();
      final int compare = Double.compare(vehiclePos, rearPos);
      if (compare < 0) {
        low = mid + 1;
      } else {
        if (compare > 0) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    }
    return -(low + 1);
  }

  /**
     * If there is a traffic sink, use it to perform any traffic outflow.
     * 
     * @param dt
     *            simulation time interval
     * @param simulationTime
     * @param iterationCount
     */
  public void outFlow(double dt, double simulationTime, long iterationCount) {
    assert laneIsSorted();
    assert assertInvariant();
    final double roadLength = roadSegment.roadLength();
    if (sinkLaneSegment != null) {
      int count = vehicles.size();
      while (count > 0) {
        final Vehicle vehicle = vehicles.get(0);
        if (vehicle.getRearPosition() < roadLength) {
          break;
        }
        final double rearPositionOnNewRoadSegment = vehicle.getRearPosition() - roadLength;
        double exitEndPos = Vehicle.EXIT_POSITION_NOT_SET;
        if (sinkLaneSegment.type() == Lanes.Type.TRAFFIC) {
          final int exitRoadSegmentId = vehicle.exitRoadSegmentId();
          if (exitRoadSegmentId == sinkLaneSegment.roadSegment.id()) {
            exitEndPos = sinkLaneSegment.roadLength();
          } else {
            final RoadSegment sinkSinkRoad = sinkLaneSegment.roadSegment();
            if (sinkSinkRoad != null && sinkSinkRoad.id() == exitRoadSegmentId) {
              exitEndPos = sinkLaneSegment.roadLength() + sinkSinkRoad.roadLength();
            }
          }
        }
        final int laneOnNewRoadSegment = sinkLaneSegment.lane();
        vehicle.moveToNewRoadSegment(sinkLaneSegment.roadSegment(), laneOnNewRoadSegment, rearPositionOnNewRoadSegment, exitEndPos);
        vehicles.remove(0);
        --count;
        ++removedVehicleCount;
        sinkLaneSegment.appendVehicle(vehicle);
      }
    }
    assert assertInvariant();
  }

  /**
     * Returns true if the vehicle array is sorted.
     * 
     * @return true if the vehicle array is sorted
     */
  public boolean laneIsSorted() {
    final int count = vehicles.size();
    if (count > 1) {
      Vehicle frontVehicle = vehicles.get(0);
      for (int i = 1; i < count; ++i) {
        final Vehicle vehicle = vehicles.get(i);
        if (frontVehicle.getRearPosition() < vehicle.getRearPosition()) {
          return false;
        }
        frontVehicle = vehicle;
      }
    }
    return true;
  }

  /**
     * Simple bubble sort of the vehicles. Useful for debugging.
     */
  void sortVehicles() {
    final int count = vehicles.size();
    boolean sorted = false;
    while (!sorted) {
      sorted = true;
      for (int i = 1; i < count; ++i) {
        final Vehicle front = vehicles.get(i - 1);
        final Vehicle rear = vehicles.get(i);
        if (rear.getRearPosition() > front.getRearPosition()) {
          sorted = false;
          vehicles.set(i - 1, rear);
          vehicles.set(i, front);
        }
      }
    }
  }

  /**
     * Returns an iterator over all the vehicles in this lane segment.
     * 
     * @return an iterator over all the vehicles in this lane segment
     */
  @Override public final Iterator<Vehicle> iterator() {
    return vehicles.iterator();
  }

  /**
     * Asserts the lane segment's class invariant. Used for debugging.
     */
  public boolean assertInvariant() {
    final int roadSegmentId = roadSegment.id();
    for (final Vehicle vehicle : vehicles) {
      assert vehicle.roadSegmentId() == roadSegmentId;
      if (vehicle.lane() != lane) {
        logger.info("vehicle lane={}, lane={}", vehicle.lane(), lane);
      }
      assert vehicle.lane() == lane;
    }
    return true;
  }

  public void clearVehicleRemovedCount() {
    removedVehicleCount = 0;
  }

  @Override public String toString() {
    return "LaneSegment [sinkLaneSegment=" + sinkLaneSegment + ", sourceLaneSegment=" + sourceLaneSegment + ", lane=" + lane + ", type=" + type + ", removedVehicleCount=" + removedVehicleCount + "]";
  }
}