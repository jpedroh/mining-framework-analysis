package org.movsim.simulator.vehicles;
import java.util.List;
import org.movsim.simulator.roadSection.TrafficLight;
import org.movsim.simulator.vehicles.lanechanging.impl.LaneChangingModelImpl;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;

/**
 * The Interface Vehicle.
 */
public interface Vehicle extends Moveable {
  /**
     * Sets the speedlimit.
     * 
     * @param speedlimit
     *            the new speedlimit
     */
  void setSpeedlimit(double speedlimit);

  /**
     * Sets the veh number.
     * 
     * @param vehNumber
     *            the new veh number
     */
  void setVehNumber(int vehNumber);

  /**
     * Inits the.
     * 
     * @param pos
     *            the pos
     * @param v
     *            the v
     * @param lane
     *            the lane
     */
  void init(double pos, double v, int lane);

  /**
     * Update postion and speed.
     * 
     * @param dt
     *            the dt
     */
  void updatePostionAndSpeed(double dt);

  /**
     * Calc acceleration.
     * 
     * @param dt
     *            the dt
     * @param vehContainer
     *            the veh container
     * @param alphaT
     *            the alpha t
     * @param alphaV0
     *            the alpha v0
     */
  void calcAcceleration(double dt, VehicleContainer vehContainer, VehicleContainer vehContainerLeftLane, double alphaT, double alphaV0);

  /**
     * Update traffic light.
     * 
     * @param time
     *            the time
     * @param trafficLight
     *            the traffic light
     */
  void updateTrafficLight(double time, TrafficLight trafficLight);

  /**
     * Removes the observers.
     */
  void removeObservers();

  /**
     * Gets the lane changing model.
     *
     * @return the lane changing model
     */
  LaneChangingModelImpl getLaneChangingModel();

  /**
     * Gets the acceleration model.
     *
     * @return the acceleration model
     */
  AccelerationModel getAccelerationModel();

  /**
     * Sets the position.
     *
     * @param newPos the new position
     */
  void setPosition(double newPos);

  /**
     * Consider lane changing.
     *
     * @param dt the dt
     * @param vehContainers the veh containers
     * @return true, if successful
     */
  boolean considerLaneChanging(double dt, final List<VehicleContainer> vehContainers);

  /**
     * Gets the target lane.
     *
     * @return the target lane
     */
  int getTargetLane();

  /**
     * In process of lane changing.
     *
     * @return true, if successful
     */
  boolean inProcessOfLaneChanging();

  /**
     * Inits the lane change from ramp.
     *
     * @param oldLane the old lane
     */
  void initLaneChangeFromRamp(int oldLane);

  double calcAccModel(final VehicleContainer vehContainer, final VehicleContainer vehContainerLeftLane);
}