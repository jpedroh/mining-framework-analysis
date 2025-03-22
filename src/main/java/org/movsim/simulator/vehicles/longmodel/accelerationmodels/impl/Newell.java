package org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl;
import org.movsim.input.model.vehicle.longModel.AccelerationModelInputDataNewell;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModelCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Newell.
 */
public class Newell extends AccelerationModelAbstract implements AccelerationModel {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(Newell.class);

  /** The dt. */
  private double dt;

  /** The v0. */
  private double v0;

  /** The s0. */
  private double s0;

  /**
     * Instantiates a new newell.
     * 
     * @param modelName
     *            the model name
     * @param parameters
     *            the parameters
     */
  public Newell(String modelName, AccelerationModelInputDataNewell parameters) {
    super(modelName, AccelerationModelCategory.INTERATED_MAP_MODEL, parameters);
    initParameters();
  }

  @Override protected void initParameters() {
    logger.debug("init model parameters");
    this.v0 = ((AccelerationModelInputDataNewell) parameters).getV0();
    this.dt = ((AccelerationModelInputDataNewell) parameters).getDt();
    this.v0 = ((AccelerationModelInputDataNewell) parameters).getV0();
    this.s0 = ((AccelerationModelInputDataNewell) parameters).getS0();
  }

  @Override public double calcAcc(Vehicle me, VehicleContainer vehContainer, double alphaT, double alphaV0, double alphaA) {
    final Moveable vehFront = vehContainer.getLeader(me);
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double v0Local = Math.min(alphaV0 * v0, me.getSpeedlimit());
    final double tLocal = alphaT * dt;
    return acc(s, v, dv, v0Local, tLocal);
  }

  @Override public double calcAcc(final Vehicle me, final Vehicle vehFront) {
    final double s = me.getNetDistance(vehFront);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(vehFront);
    final double v0Local = Math.min(v0, me.getSpeedlimit());
    final double dtLocal = dt;
    return acc(s, v, dv, v0Local, dtLocal);
  }

  @Override public double calcAccSimple(double s, double v, double dv) {
    return acc(s, v, dv, v0, dt);
  }

  /**
     * Acc.
     *
     * @param s the s
     * @param v the v
     * @param dv the dv
     * @param v0Local the v0 local
     * @param dtLocal the dt local
     * @return the double
     */
  private double acc(double s, double v, double dv, double v0Local, double dtLocal) {
    final double vNew = Math.min(Math.max((s - s0) / dtLocal, 0), v0Local);
    double aWanted = (vNew - v) / dtLocal;
    if (s / v < dt) {
      aWanted = -10000000;
    }
    return aWanted;
  }

  @Override public double getDesiredSpeedParameterV0() {
    return v0;
  }

  @Override public double getRequiredUpdateTime() {
    return dt;
  }

  @Override protected void setDesiredSpeedV0(double v0) {
    this.v0 = v0;
  }
}