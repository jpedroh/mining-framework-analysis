package org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl;
import org.movsim.input.model.vehicle.longModel.AccelerationModelInputData;
import org.movsim.simulator.impl.MyRandom;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModelCategory;
import org.movsim.utilities.Observer;
import org.movsim.utilities.impl.ScalingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LongitudinalModel.
 */
public abstract class AccelerationModelAbstract implements Observer {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(AccelerationModelAbstract.class);

  /** The model name. */
  private final String modelName;

  private final double scalingLength;

  /** The model category. */
  private final int modelCategory;

  /** The parameters. */
  public AccelerationModelInputData parameters;

  /**
     * Inits the parameters.
     */
  protected abstract void initParameters();

  /** The id. */
  protected long id;

  /**
     * Instantiates a new longitudinal model impl.
     * 
     * @param modelName
     *            the model name
     * @param modelCategory
     *            the model category
     * @param parameters
     *            the parameters
     */
  public AccelerationModelAbstract(String modelName, int modelCategory, AccelerationModelInputData parameters) {
    this.modelName = modelName;
    this.modelCategory = modelCategory;
    this.parameters = parameters;
    this.id = MyRandom.nextInt();
    this.scalingLength = ScalingHelper.getScalingLength(modelName);
    parameters.registerObserver(this);
  }

  /**
     * Removes the observer.
     */
  public void removeObserver() {
    if (parameters != null) {
      parameters.removeObserver(this);
    }
  }

  /**
     * Model name.
     * 
     * @return the string
     */
  public String modelName() {
    return modelName;
  }

  /**
     * Checks if is cellular automaton.
     * 
     * @return true, if is cA
     */
  public boolean isCA() {
    return (modelCategory == AccelerationModelCategory.CELLULAR_AUTOMATON);
  }

  /**
     * Checks if is iterated map.
     * 
     * @return true, if is iterated map
     */
  public boolean isIteratedMap() {
    return (modelCategory == AccelerationModelCategory.INTERATED_MAP_MODEL);
  }

  /**
     * Gets the model category.
     * 
     * @return the model category
     */
  public int getModelCategory() {
    return modelCategory;
  }

  /**
     * Gets the scaling length.
     *
     * @return the scaling length
     */
  public double getScalingLength() {
    return scalingLength;
  }

  @Override public void notifyObserver() {
    initParameters();
    logger.debug("observer notified");
  }

  /**
     * Sets the relative randomization v0.
     *
     * @param relRandomizationFactor the new relative randomization v0
     */
  public void setRelativeRandomizationV0(double relRandomizationFactor) {
    final double equalRandom = 2 * MyRandom.nextDouble() - 1;
    final double newV0 = getDesiredSpeedParameterV0() * (1 + relRandomizationFactor * equalRandom);
    logger.info("randomization of desired speeds: v0={}, new v0={}", getDesiredSpeedParameterV0(), newV0);
    setDesiredSpeedV0(newV0);
  }

  protected double calcSmoothFraction(double speedMe, double speedFront) {
    final double widthDeltaSpeed = 1;
    double x = 0;
    if (speedFront >= 0) {
      x = 0.5 * (1 + Math.tanh((speedMe - speedFront) / widthDeltaSpeed));
    }
    return x;
  }

  public abstract double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA);

  public double calcAccEur(double vCritEur, Vehicle me, VehicleContainer vehContainer, VehicleContainer vehContainerLeftLane, double alphaT, double alphaV0, double alphaA) {
    final double accInOwnLane = calcAcc(me, vehContainer, alphaT, alphaV0, alphaA);
    if (vehContainerLeftLane == null) {
      return accInOwnLane;
    }
    final Vehicle newFront = vehContainerLeftLane.getLeader(me);
    double speedFront = (newFront != null) ? newFront.getSpeed() : -1;
    double accLeft = (speedFront > vCritEur) ? calcAcc(me, vehContainerLeftLane, alphaT, alphaV0, alphaA) : Double.MAX_VALUE;
    final double frac = calcSmoothFraction(me.getSpeed(), speedFront);
    final double accResult = frac * Math.min(accInOwnLane, accLeft) + (1 - frac) * accInOwnLane;
    return accResult;
  }

  /**
     * Gets the desired speed parameter v0.
     *
     * @return the desired speed parameter v0
     */
  public abstract double getDesiredSpeedParameterV0();

  /**
     * Sets the desired speed v0.
     *
     * @param v0 the new desired speed v0
     */
  protected abstract void setDesiredSpeedV0(double v0);
}