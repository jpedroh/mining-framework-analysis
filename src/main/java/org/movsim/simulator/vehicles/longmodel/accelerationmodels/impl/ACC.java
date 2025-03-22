package org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl;
import org.movsim.input.model.vehicle.longModel.AccelerationModelInputDataACC;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModelCategory;
import org.movsim.utilities.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ACC.
 */
public class ACC extends AccelerationModelAbstract implements AccelerationModel, Observer {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(ACC.class);

  /**
     * The v0. desired velocity (m/s)
     */
  private double v0;

  /**
     * The T. time headway (s)
     */
  private double T;

  /**
     * The s0. bumper-to-bumper distance (m)
     */
  private double s0;

  /** The s1. */
  private double s1;

  /**
     * The a. acceleration (m/s^2)
     */
  private double a;

  /**
     * The b. comfortable (desired) deceleration (m/s^2)
     */
  private double b;

  /**
     * The delta. acceleration exponent (1)
     */
  private double delta;

  /**
     * The coolness. coolness=0: acc1=IIDM (without constant-acceleration
     * heuristic, CAH), coolness=1 CAH factor in range [0, 1]
     */
  private double coolness;

  /**
     * Instantiates a new aCC.
     * 
     * @param modelName
     *            the model name
     * @param parameters
     *            the parameters
     */
  public ACC(String modelName, AccelerationModelInputDataACC parameters) {
    super(modelName, AccelerationModelCategory.CONTINUOUS_MODEL, parameters);
    initParameters();
  }

  @Override protected void initParameters() {
    logger.debug("init model parameters");
    this.v0 = ((AccelerationModelInputDataACC) parameters).getV0();
    this.T = ((AccelerationModelInputDataACC) parameters).getT();
    this.s0 = ((AccelerationModelInputDataACC) parameters).getS0();
    this.s1 = ((AccelerationModelInputDataACC) parameters).getS1();
    this.a = ((AccelerationModelInputDataACC) parameters).getA();
    this.b = ((AccelerationModelInputDataACC) parameters).getB();
    this.delta = ((AccelerationModelInputDataACC) parameters).getDelta();
    this.coolness = ((AccelerationModelInputDataACC) parameters).getCoolness();
  }

  @Override public double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA) {
    final Moveable vehFront = vehContainer.getLeader(me);
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double aLead = (vehFront == null) ? me.getAcc() : vehFront.getAcc();
    final double Tloc = alphaT * T;
    final double v0Loc = Math.min(alphaV0 * v0, me.getSpeedlimit());
    final double aLoc = alphaA * a;
    return acc(s, v, dv, aLead, Tloc, v0Loc, aLoc);
  }

  @Override public double calcAcc(final Vehicle me, final Vehicle vehFront) {
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double aLead = (vehFront == null) ? me.getAcc() : vehFront.getAcc();
    final double TLocal = T;
    ;
    final double v0Local = Math.min(v0, me.getSpeedlimit());
    final double aLocal = a;
    return acc(s, v, dv, aLead, TLocal, v0Local, aLocal);
  }

  @Override public double calcAccSimple(double s, double v, double dv) {
    return acc(s, v, dv, 0, T, v0, a);
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
     * @param aLead
     *            the a lead
     * @param TLocal
     *            the t local
     * @param v0Local
     *            the v0 local
     * @param aLocal
     *            the a local
     * @return the double
     */
  private double acc(double s, double v, double dv, double aLead, double TLocal, double v0Local, double aLocal) {
    if (v0Local == 0) {
      return 0;
    }
    final double sstar = s0 + Math.max(TLocal * v + s1 * Math.sqrt((v + 0.00001) / v0Local) + 0.5 * v * dv / Math.sqrt(a * b), 0.);
    final double z = sstar / Math.max(s, 0.01);
    final double accEmpty = (v <= v0) ? a * (1 - Math.pow((v / v0), delta)) : -b * (1 - Math.pow((v0 / v), a * delta / b));
    final double accPos = accEmpty * (1. - Math.pow(z, Math.min(2 * a / accEmpty, 100.)));
    final double accInt = a * (1 - z * z);
    final double accIIDM = (v < v0) ? (z < 1) ? accPos : accInt : (z < 1) ? accEmpty : accInt + accEmpty;
    final double aLeadRestricted = Math.min(aLead, a);
    final double dvp = Math.max(dv, 0.0);
    final double vLead = v - dvp;
    final double denomCAH = vLead * vLead - 2 * s * aLeadRestricted;
    final double accCAH = ((vLead * dvp < -2 * s * aLeadRestricted) && (denomCAH != 0)) ? v * v * aLeadRestricted / denomCAH : aLeadRestricted - 0.5 * dvp * dvp / Math.max(s, 0.0001);
    final double accACC_IIDM = (accIIDM > accCAH) ? accIIDM : (1 - coolness) * accIIDM + coolness * (accCAH + b * Math.tanh((accIIDM - accCAH) / b));
    return accACC_IIDM;
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

  @Override public double getDesiredSpeedParameterV0() {
    return v0;
  }

  /**
     * Gets the coolness.
     * 
     * @return the coolness
     */
  public double getCoolness() {
    return coolness;
  }

  @Override public double getRequiredUpdateTime() {
    return 0;
  }

  @Override protected void setDesiredSpeedV0(double v0) {
    this.v0 = v0;
  }
}