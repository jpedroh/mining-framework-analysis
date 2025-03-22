package org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl;
import org.movsim.input.model.vehicle.longModel.AccelerationModelInputDataGipps;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModelCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Gipps.
 */
public class Gipps extends AccelerationModelAbstract implements AccelerationModel {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(Gipps.class);

  /**
     * The T. results from update timestep dt dt = T = Tr = tau_relax
     */
  private double T;

  /** The v0. */
  private double v0;

  /** The a. */
  private double a;

  /** The b. */
  private double b;

  /** The s0. */
  private double s0;

  /**
     * Instantiates a new gipps.
     * 
     * @param modelName
     *            the model name
     * @param parameters
     *            the parameters
     */
  public Gipps(String modelName, AccelerationModelInputDataGipps parameters) {
    super(modelName, AccelerationModelCategory.INTERATED_MAP_MODEL, parameters);
    initParameters();
  }

  @Override protected void initParameters() {
    logger.debug("init model parameters");
    this.T = ((AccelerationModelInputDataGipps) parameters).getDt();
    this.v0 = ((AccelerationModelInputDataGipps) parameters).getV0();
    this.a = ((AccelerationModelInputDataGipps) parameters).getA();
    this.b = ((AccelerationModelInputDataGipps) parameters).getB();
    this.s0 = ((AccelerationModelInputDataGipps) parameters).getS0();
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

  @Override public double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA) {
    final Moveable vehFront = vehContainer.getLeader(me);
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = (vehFront == null) ? 0 : v - vehFront.getSpeed();
    final double v0Local = Math.min(alphaV0 * v0, me.getSpeedlimit());
    final double TLocal = alphaT * T;
    return acc(s, v, dv, v0Local, TLocal);
  }

  @Override public double calcAcc(final Vehicle me, final Vehicle vehFront) {
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double TLocal = T;
    final double v0Local = Math.min(v0, me.getSpeedlimit());
    return acc(s, v, dv, v0Local, TLocal);
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
     *            the t local
     * @return the double
     */
  private double acc(double s, double v, double dv, double v0Local, double TLocal) {
    final double vp = v - dv;
    final double vSafe = -b * T + Math.sqrt(b * b * T * T + vp * vp + 2 * b * Math.max(s - s0, 0.));
    final double vNew = Math.min(vSafe, Math.min(v + a * TLocal, v0Local));
    final double aWanted = (vNew - v) / T;
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