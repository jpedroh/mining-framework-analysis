package org.movsim.simulator.vehicles.longitudinalmodel.acceleration;
import org.movsim.autogen.NoiseParameter;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.longitudinalmodel.Noise;
import org.movsim.simulator.vehicles.longitudinalmodel.acceleration.parameter.IModelParameter;
import org.movsim.simulator.vehicles.longitudinalmodel.acceleration.parameter.IModelParameterPTM;
import org.movsim.utilities.LinearInterpolatedFunction;
import org.movsim.utilities.ProbabilityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PTM extends LongitudinalModelBase {
  /** The Constant LOG. */
  private static final Logger LOG = LoggerFactory.getLogger(PTM.class);

  private final IModelParameterPTM param;

  private double delta;

  private double dw;

  private double dt;

  private Noise wienerProcess;

  private static final int NTABMAX = 100;

  /** tabulated d(U_PT)/da function */
  private LinearInterpolatedFunction uPTaFunction;

  /** tabulated d^2(U_PT)/da^2 */
  private LinearInterpolatedFunction uPaaFunction;

  PTM(double simulationTimestep, IModelParameterPTM parameters) {
    super(ModelName.PTM);
    this.param = parameters;
    this.dt = simulationTimestep;
    init();
    initNoise();
    initTables();
  }

  private void initNoise() {
    NoiseParameter noiseParameter = new NoiseParameter();
    noiseParameter.setFluctStrength(1);
    noiseParameter.setTau(param.getTauCorrelation());
    wienerProcess = new Noise(noiseParameter);
  }

  private void init() {
    delta = 0.5 * (1 - param.getGamma());
    dw = 1 - param.getWeightMinus();
  }

  @Override protected IModelParameter getParameter() {
    return param;
  }

  private double get_uPTa(double a) {
    double wm = param.getWeightMinus();
    double bMax = param.getBMax();
    double uPT = Math.min(bMax, Math.max(-bMax, uPTaFunction.value(a)));
    return (a <= -bMax) ? wm * (1 - 2 * delta) * Math.pow(a / param.getA0(), -2 * delta) : (a < bMax) ? uPT : (wm + dw) * (1 - 2 * delta) * Math.pow(a / param.getA0(), -2 * delta);
  }

  private double get_uPTaa(double a) {
    double bmax = param.getBMax();
    double aPT = Math.min(bmax, Math.max(-bmax, uPaaFunction.value(a)));
    return (a <= -bmax) ? -param.getWeightMinus() * 2 * delta * (1 - 2 * delta) * Math.pow(a / param.getA0(), -2 * delta - 1) : (a < bmax) ? aPT : -(param.getWeightMinus() + dw) * 2 * delta * (1 - 2 * delta) * Math.pow(a / param.getA0(), -2 * delta - 1);
  }

  @Override public double calcAcc(Vehicle me, Vehicle frontVehicle, double alphaT, double alphaV0, double alphaA) {
    final double s = me.getNetDistance(frontVehicle);
    final double v = me.getSpeed();
    final double dv = me.getRelSpeed(frontVehicle);
    final double localV0;
    if (me.getEffectiveSpeedlimit() != 0.0) {
      localV0 = Math.min(alphaV0 * getDesiredSpeed(), me.getEffectiveSpeedlimit());
    } else {
      localV0 = alphaV0 * getDesiredSpeed();
    }
    wienerProcess.update(dt);
    return acc(s, v, dv, alphaT, localV0, 1);
  }

  /**
     * acceleration of the PTmodel.
     * Argument parameters:
     * s=net distance (m),
     * v=own velocity (m/s)
     * dv=approaching rate (v-v_front) to the front vehicle (m/s)
     * alpha_v0, alpha_T = multiplicators of v0 and T (flowc bottl)
     */
  @Override public double calcAccSimple(double s, double v, double dv) {
    return acc(s, v, dv, 1, param.getV0(), 1);
  }

  private double acc(double s, double v, double dv, double alphaT, double v0Local, double aLocal) {
    double alphaloc = alphaT * param.getAlpha();
    double afree = (v0Local - v) / param.getTau();
    double sloc = Math.max(s - param.getS0(), 0.01);
    double vloc = Math.max(v, 0.01);
    double bcomf = 2;
    double taumaxmod = Math.max(param.getTauMax(), 0.5 * dv / bcomf);
    double tau = (dv > sloc / taumaxmod) ? sloc / dv : taumaxmod;
    double za = 0.5 * tau / (alphaloc * vloc);
    double logval = Math.log(param.getA0() * param.getWeightCrash() * za / Math.sqrt(2 * Math.PI));
    if (logval <= 0) {
      System.err.println("PTmodel.accSimple: zstar<0 or no solution => prob of approaching nearer than s0 or crash >1/2!");
      return (-param.getBMax());
    }
    double zstar = (logval > 0) ? -Math.sqrt(2 * logval) : 0;
    double astar = 2 / tau * (sloc / tau - dv + alphaloc * v * zstar);
    if (astar < -param.getBMax()) {
      LOG.error("PTmodel.accSimple: initial guess acc<-bmax; returning -bmax");
      return (-param.getBMax());
    }
    double gaussDensity = ProbabilityUtils.getGaussDensity(zstar);
    double ua = get_uPTa(astar) - param.getWeightCrash() * gaussDensity * za;
    double uaa = get_uPTaa(astar) + param.getWeightCrash() * gaussDensity * zstar * za * za;
    astar = (uaa < 0) ? astar - ua / uaa : astar;
    if (uaa >= 0) {
      System.err.println("PTmodel.accSimple: Warning: U\'\'(a)>0 => Newton wants to go to utility minimum instead maximum");
    }
    for (int k = 1; k < 2; k++) {
      zstar = (dv + 0.5 * astar * tau - sloc / tau) / (alphaloc * v);
      gaussDensity = ProbabilityUtils.getGaussDensity(zstar);
      ua = get_uPTa(astar) - param.getWeightCrash() * gaussDensity * za;
      uaa = get_uPTaa(astar) + param.getWeightCrash() * gaussDensity * zstar * za * za;
      astar = (uaa < 0) ? astar - ua / uaa : astar;
    }
    double vara = -1 / (param.getBetaLogit() * uaa);
    double stddeva = (vara > 0) ? Math.sqrt(vara) : 0;
    if (vara <= 0) {
      LOG.error("PTmodel:accSimple:Warning: variance-1/(beta*U\'\'(a))={} negative", vara);
    }
    double aPT = astar + stddeva * wienerProcess.getAccError();
    double aVeryNear = -0. / Math.sqrt(sloc);
    double aWanted = Math.min(afree, aPT + aVeryNear);
    return Math.max(aWanted, -param.getBMax());
  }

  private void initTables() {
    double[] uPTatab = new double[NTABMAX];
    double[] uPTaatab = new double[NTABMAX];
    double[] acc = new double[NTABMAX];
    for (int i = 0; i < NTABMAX; i++) {
      final double a = param.getBMax() * (-1 + 2 * i / ((double) (NTABMAX - 1)));
      double x = a / param.getA0();
      double lorenz = 1 / (1 + x * x);
      double g = x * Math.pow(lorenz, delta);
      double gx = Math.pow(lorenz, delta) - 2 * delta * x * x * Math.pow(lorenz, delta + 1);
      double gxx = -6 * delta * x * Math.pow(lorenz, delta + 1) + 4 * delta * (delta + 1) * Math.pow(x, 3) * Math.pow(lorenz, delta + 2);
      double prefactor = param.getWeightMinus() + 0.5 * dw * (1 + Math.tanh(x));
      double cosh2 = Math.pow(Math.cosh(x), 2);
      acc[i] = a;
      uPTatab[i] = 1 / param.getA0() * (prefactor * gx + 0.5 * dw * g / cosh2);
      uPTaatab[i] = 1 / (param.getA0() * param.getA0()) * (prefactor * gxx + dw / cosh2 * (gx - Math.tanh(x) * g));
    }
    this.uPTaFunction = new LinearInterpolatedFunction(acc, uPTatab);
    this.uPaaFunction = new LinearInterpolatedFunction(acc, uPTaatab);
  }
}