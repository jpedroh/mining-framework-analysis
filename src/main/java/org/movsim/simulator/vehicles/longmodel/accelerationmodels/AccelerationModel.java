package org.movsim.simulator.vehicles.longmodel.accelerationmodels;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;

/**
 * The Interface AccelerationModel.
 */
public interface AccelerationModel {
  /**
     * Model name.
     * 
     * @return the string
     */
  String modelName();

  /**
     * Checks if is cellular automaton.
     * 
     * @return true, if is cellular automaton
     */
  boolean isCA();

  /**
     * Checks if is iterated map.
     * 
     * @return true, if is iterated map
     */
  boolean isIteratedMap();

  /**
     * Gets the model category.
     * 
     * @return the model category
     */
  int getModelCategory();

  /**
     * Gets the required update time.
     * 
     * @return the required update time
     */
  double getRequiredUpdateTime();

  /**
     * Gets the desired speed parameter v0.
     *
     * @return the desired speed parameter v0
     */
  double getDesiredSpeedParameterV0();

  /**
     * Sets the relative randomization v0.
     *
     * @param relRandomizationFactor the new relative randomization v0
     */
  void setRelativeRandomizationV0(double relRandomizationFactor);

  /**
     * Calc acc.
     *
     * @param me the me
     * @param vehContainer the veh container
     * @param alphaT the alpha t
     * @param alphaV0 the alpha v0
     * @param alphaA the alpha a
     * @return the double
     */
  double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA);

  double calcAccEur(double vCritEur, Vehicle me, VehicleContainer vehContainer, VehicleContainer vehContainerLeftLane, double alphaT, double alphaV0, double alphaA);

  /**
     * Calc acc.
     *
     * @param me the me
     * @param vehFront the veh front
     * @return the double
     */
  double calcAcc(final Vehicle me, final Vehicle vehFront);

  /**
     * Calc acc simple.
     *
     * @param s the s
     * @param v the v
     * @param dv the dv
     * @return the double
     */
  double calcAccSimple(double s, double v, double dv);

  /**
     * Removes the observer.
     */
  void removeObserver();

  /**
     * Gets the scaling length.
     *
     * @return the scaling length
     */
  double getScalingLength();
}