package org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl;
import org.movsim.input.model.vehicle.longModel.AccelerationModelInputDataKrauss;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModelCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Krauss.
 * 
 * @author Martin Treiber, Ralph Germ
 */
public class Krauss extends AccelerationModelAbstract implements AccelerationModel {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(Krauss.class);

  /**
     * The parameter T is given by the update timestep dt: dt = T = Tr =
     * tau_relax
     */
  private double T, dt;

  /** The v0. */
  private double v0;

  /** The a. */
  private double a;

  /** The b. */
  private double b;

  /** The s0. */
  private double s0;

  /** The dimensionless epsilon has similar effects as the braking
        probability of the Nagel-S CA
        default value 0.4 (PRE) or 1 (EPJB)
    */
  private double epsilon;

  /**
     * Instantiates a new krauss instance.
     * 
     * @param modelName
     *            the model name
     * @param parameters
     *            the parameters
     */
  public Krauss(String modelName, AccelerationModelInputDataKrauss parameters) {
    super(modelName, AccelerationModelCategory.INTERATED_MAP_MODEL, parameters);
    initParameters();
  }

  @Override protected void initParameters() {
    logger.debug("init model parameters");
    this.T = this.dt = ((AccelerationModelInputDataKrauss) parameters).getDt();
    this.v0 = ((AccelerationModelInputDataKrauss) parameters).getV0();
    this.a = ((AccelerationModelInputDataKrauss) parameters).getA();
    this.b = ((AccelerationModelInputDataKrauss) parameters).getB();
    this.s0 = ((AccelerationModelInputDataKrauss) parameters).getS0();
    this.epsilon = ((AccelerationModelInputDataKrauss) parameters).getEpsilon();
  }

  /**
     * Gets the t.
     * 
     * @return the t
     */
  public double getT() {
    return T;
  }

  /**
     * Gets the v0.
     * 
     * @return the v0
     */
  public double getV0() {
    return v0;
  }

  /**
     * Gets the a.
     * 
     * @return the a
     */
  public double getA() {
    return a;
  }

  /**
     * Gets the b.
     * 
     * @return the b
     */
  public double getB() {
    return b;
  }

  /**
     * Gets the s0.
     * 
     * @return the s0
     */
  public double getS0() {
    return s0;
  }

  /**
     * Gets the epsilon.
     * 
     * @return the epsilon
     */
  public double getEpsilon() {
    return epsilon;
  }

  @Override public double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA) {
    final Moveable vehFront = vehContainer.getLeader(me);
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double localV0 = Math.min(alphaV0 * v0, me.getSpeedlimit());
    final double localT = alphaT * T;
    return acc(s, v, dv, localV0, localT);
  }

  @Override public double calcAcc(final Vehicle me, final Vehicle vehFront) {
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double localV0 = Math.min(v0, me.getSpeedlimit());
    final double localT = T;
    return acc(s, v, dv, localV0, localT);
  }

  @Override public double calcAccSimple(double s, double v, double dv) {
    return acc(s, v, dv, v0, T);
  }

  /**
     * Acc.
     * 
     * @param s
     *            the s
     * @param v
     *            the v
     * @param dv
     *            the dv
     * @param v0Local
     *            the v0 local
     * @param TLocal
     *            the local time gap. Notice that inconsistencies may arise for
     *            nontrivial values since then no longer dt=T=tau_relax making
     *            the vSafe formula possibly inconsistent
     * @return the double
     */
  private double acc(double s, double v, double dv, double v0Local, double TLocal) {
    final double vp = v - dv;
    final double vSafe = -b * T + Math.sqrt(b * b * T * T + vp * vp + 2 * b * Math.max(s - s0, 0.));
    final double vUpper = Math.min(vSafe, Math.min(v + a * TLocal, v0Local));
    double vLower = (1 - epsilon) * vUpper + epsilon * Math.max(0, (v - b * TLocal));
    final double r = Math.random();
    final double vNew = vLower + r * (vUpper - vLower);
    final double aWanted = (vNew - v) / TLocal;
    return aWanted;
  }

  @Override public double getDesiredSpeedParameterV0() {
    return v0;
  }

  @Override public double getRequiredUpdateTime() {
    return this.T;
  }

  @Override protected void setDesiredSpeedV0(double v0) {
    this.v0 = v0;
  }
}