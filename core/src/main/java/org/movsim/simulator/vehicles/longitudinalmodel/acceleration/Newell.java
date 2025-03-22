package org.movsim.simulator.vehicles.longitudinalmodel.acceleration;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.longitudinalmodel.acceleration.parameter.IModelParameterNewell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Newell.
 */
class Newell extends LongitudinalModelBase {
  /** The Constant LOG. */
  private static final Logger LOG = LoggerFactory.getLogger(Newell.class);

  /** The simulation timepstep as parameter */
  private final double dt;

  private final IModelParameterNewell param;

  /**
     * Instantiates a new Newell car-following model.
     * 
     * @param simulationTimestep
     * @param modelParameter
     */
  public Newell(double simulationTimestep, IModelParameterNewell modelParameter) {
    super(ModelName.NEWELL);
    this.dt = simulationTimestep;
    this.param = modelParameter;
  }

  @Override public double calcAcc(Vehicle me, Vehicle frontVehicle, double alphaT, double alphaV0, double alphaA) {
    final double s = me.getNetDistance(frontVehicle);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(frontVehicle);
    final double dtLocal = alphaT * dt;
    final double v0Local = Math.min(alphaV0 * getDesiredSpeed(), me.getEffectiveSpeedlimit());
    return acc(s, v, dv, dtLocal, v0Local);
  }

  @Override public double calcAccSimple(double s, double v, double dv) {
    return acc(s, v, dv, dt, getDesiredSpeed());
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
     * @param dtLocal
     *            the dt local
     * @return the double
     */
  private double acc(double s, double v, double dv, double dtLocal, double v0Local) {
    final double vNew = Math.min(Math.max((s - getMinimumGap()) / dtLocal, 0), v0Local);
    double aWanted = (vNew - v) / dtLocal;
    return aWanted;
  }

  @Override protected IModelParameterNewell getParameter() {
    return param;
  }
}