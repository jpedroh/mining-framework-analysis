package org.movsim.simulator.vehicles;
import java.util.List;

/**
 * The Interface VehicleContainer.
 */
public interface VehicleContainer extends MoveableContainer {
  /**
     * Gets the lane index.
     *
     * @return the lane index
     */
  int getLaneIndex();

  /**
     * Gets the vehicles.
     * 
     * @return the vehicles
     */
  List<Vehicle> getVehicles();

  /**
     * Size.
     * 
     * @return the int
     */
  @Override int size();

  /**
     * Gets the.
     * 
     * @param index
     *            the index
     * @return the vehicle
     */
  Vehicle get(int index);

  /**
     * Gets the most upstream.
     * 
     * @return the most upstream
     */
  Vehicle getMostUpstream();

  /**
     * Gets the most downstream.
     * 
     * @return the most downstream
     */
  Vehicle getMostDownstream();

  /**
     * Adds the.
     * 
     * @param veh
     *            the veh
     * @param xInit
     *            the x init
     * @param vInit
     *            the v init
     */
  void add(final Vehicle veh, double xInit, double vInit);

  /**
     * Adds the.
     *
     * @param veh the veh
     */
  void add(Vehicle veh);

  /**
     * Removes the vehicles downstream.
     * 
     * @param roadLength
     *            the road length
     */
  void removeVehiclesDownstream(double roadLength);

  /**
     * Removes the vehicle most downstream.
     */
  void removeVehicleMostDownstream();

  /**
     * Removes the vehicle.
     *
     * @param veh the veh
     */
  void removeVehicle(final Vehicle veh);

  /**
     * Gets the leader.
     *
     * @param veh the veh
     * @return the leader
     */
  Vehicle getLeader(final Moveable veh);

  /**
     * Gets the follower.
     *
     * @param veh the veh
     * @return the follower
     */
  Vehicle getFollower(final Moveable veh);

  /**
     * Adds the from to ramp.
     *
     * @param veh the veh
     * @param xInit the x init
     * @param vInit the v init
     * @param oldLane the old lane
     */
  void addFromToRamp(Vehicle veh, double xInit, double vInit, int oldLane);

  void addTestwise(final Vehicle veh);
}