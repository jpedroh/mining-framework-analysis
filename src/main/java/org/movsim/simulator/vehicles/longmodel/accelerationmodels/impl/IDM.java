package org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl;
import org.movsim.input.model.vehicle.longModel.AccelerationModelInputDataIDM;
import org.movsim.simulator.Constants;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModelCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IDM.
 * <p>
 * Implementation of the 'intelligent driver model'(IDM). <a
 * href="http://en.wikipedia.org/wiki/Intelligent_Driver_Model">Wikipedia
 * article IDM.</a>
 * </p>
 * <p>
 * Treiber/Kesting: Verkehrsdynamik und -simulation, 2010, chapter 11.3
 * </p>
 * <p>
 * see <a href="http://xxx.uni-augsburg.de/abs/cond-mat/0002177"> M. Treiber, A.
 * Hennecke, and D. Helbing, Congested Traffic States in Empirical Observations
 * and Microscopic Simulations, Phys. Rev. E 62, 1805 (2000)].</a>
 * </p>
 */
public class IDM extends AccelerationModelAbstract implements AccelerationModel {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(IDM.class);

  /** desired velocity (m/s). */
  private double v0;

  /** safe time headway (s). */
  private double T;

  /** bumper-to-bumper vehicle distance in jams or queues; minimun gap. */
  private double s0;

  /** gap parameter (m). */
  private double s1;

  /** acceleration (m/s^2). */
  private double a;

  /** comfortable (desired) deceleration (braking), (m/s^2). */
  private double b;

  /** acceleration exponent. */
  private double delta;

  /**
     * Instantiates a new IDM.
     * 
     * @param modelName
     *            the model name
     * @param parameters
     *            the parameters: v0, T, s0, s1, a, b, delta
     */
  public IDM(String modelName, AccelerationModelInputDataIDM parameters) {
    super(modelName, AccelerationModelCategory.CONTINUOUS_MODEL, parameters);
    initParameters();
  }

  @Override protected void initParameters() {
    logger.debug("init model parameters");
    this.v0 = ((AccelerationModelInputDataIDM) parameters).getV0();
    this.T = ((AccelerationModelInputDataIDM) parameters).getT();
    this.s0 = ((AccelerationModelInputDataIDM) parameters).getS0();
    this.s1 = ((AccelerationModelInputDataIDM) parameters).getS1();
    this.a = ((AccelerationModelInputDataIDM) parameters).getA();
    this.b = ((AccelerationModelInputDataIDM) parameters).getB();
    this.delta = ((AccelerationModelInputDataIDM) parameters).getDelta();
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
     * Gets the t.
     * 
     * @return the t
     */
  public double getT() {
    return T;
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
     * Gets the s1.
     * 
     * @return the s1
     */
  public double getS1() {
    return s1;
  }

  /**
     * Gets the delta.
     * 
     * @return the delta
     */
  public double getDelta() {
    return delta;
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

  @Override public double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA) {
    final Moveable vehFront = vehContainer.getLeader(me);
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double localT = alphaT * T;
    final double localV0 = Math.min(alphaV0 * v0, me.getSpeedlimit());
    final double localA = alphaA * a;
    return acc(s, v, dv, localT, localV0, localA);
  }

  @Override public double calcAcc(final Vehicle me, final Vehicle vehFront) {
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double localT = T;
    ;
    final double localV0 = Math.min(v0, me.getSpeedlimit());
    final double localA = a;
    return acc(s, v, dv, localT, localV0, localA);
  }

  @Override public double calcAccSimple(double s, double v, double dv) {
    return acc(s, v, dv, T, v0, a);
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
     * @param TLocal
     *            the t local
     * @param v0Local
     *            the v0 local
     * @param aLocal
     *            the a local
     * @return the double
     */
  private double acc(double s, double v, double dv, double TLocal, double v0Local, double aLocal) {
    if (v0Local == 0) {
      return 0;
    }
    double sstar = s0 + TLocal * v + s1 * Math.sqrt((v + 0.0001) / v0Local) + (0.5 * v * dv) / Math.sqrt(aLocal * b);
    if (sstar < s0) {
      sstar = s0;
    }
    final double aWanted = aLocal * (1. - Math.pow((v / v0Local), delta) - (sstar / s) * (sstar / s));
    logger.debug("aWanted = {}", aWanted);
    return aWanted;
  }

  @Override public double getDesiredSpeedParameterV0() {
    return v0;
  }

  @Override public double getRequiredUpdateTime() {
    return 0;
  }

  @Override protected void setDesiredSpeedV0(double v0) {
    this.v0 = v0;
  }
}