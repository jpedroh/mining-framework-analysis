package org.movsim.output.impl;
import java.io.PrintWriter;
import java.util.List;
import org.movsim.input.model.output.MacroInput;
import org.movsim.output.Macro3D;
import org.movsim.simulator.Constants;
import org.movsim.simulator.roadSection.RoadSection;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.utilities.FileUtils;
import org.movsim.utilities.Tables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Macro3DImpl.
 */
public class Macro3DImpl implements Macro3D {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(Macro3DImpl.class);

  /** The dt out. */
  private final double dtOut;

  /** The dx out. */
  private final double dxOut;

  /** The rho inv km. */
  private double[] rhoInvKm;

  /** The v kmh. */
  private double[] vKmh;

  /** The q inv h. */
  private double[] qInvH;

  /** The writer. */
  private PrintWriter writer;

  /** The roadlength. */
  private final double roadlength;

  /** The time offset. */
  private double timeOffset;

  /** The write output. */
  private final boolean writeOutput;

  /**
     * Instantiates a new macro3 d impl.
     * 
     * @param projectName
     *            the project name
     * @param writeOutput
     *            the write output
     * @param input
     *            the input
     * @param roadSection
     *            the road section
     */
  public Macro3DImpl(String projectName, boolean writeOutput, MacroInput input, RoadSection roadSection) {
    this.writeOutput = writeOutput;
    dtOut = input.getDt();
    dxOut = input.getDx();
    roadlength = roadSection.roadLength();
    initialize();
    if (writeOutput) {
      final String filename = projectName + ".dat.csv";
      writer = FileUtils.getWriter(filename);
      writer.printf(Constants.COMMENT_CHAR + "     s[m],       t[s],  rho[1/km],    v[km/h],     Q[1/h]%n");
      writer.flush();
    }
  }

  /**
     * Initialize.
     */
  private void initialize() {
    timeOffset = 0;
    final int nxOut = (int) (roadlength / dxOut);
    rhoInvKm = new double[nxOut + 1];
    vKmh = new double[nxOut + 1];
    qInvH = new double[nxOut + 1];
  }

  @Override public void update(int it, double time, RoadSection roadSection) {
    if ((time - timeOffset) >= dtOut) {
      timeOffset = time;
      calcData(time, roadSection.vehContainer());
      if (writeOutput) {
        writeOutput(time);
      }
    }
  }

  /**
     * Calc data.
     * 
     * @param time
     *            the time
     * @param vehContainer
     *            the veh container
     */
  private void calcData(double time, VehicleContainer vehContainer) {
    final List<Vehicle> vehicles = vehContainer.getVehicles();
    final int size = vehicles.size();
    final double[] rho = new double[size];
    final double[] vMicro = new double[size];
    final double[] xMicro = new double[size];
    for (int i = 0; i < size; i++) {
      vMicro[i] = vehicles.get(i).speed();
      xMicro[i] = vehicles.get(i).position();
    }
    rho[0] = 0;
    for (int i = 1; i < size; i++) {
      final double dist = xMicro[i - 1] - xMicro[i];
      final double length = vehicles.get(i - 1).length();
      rho[i] = (dist > length) ? 1 / dist : 1 / length;
    }
    for (int j = 0; j < rhoInvKm.length; j++) {
      final double x = j * dxOut;
      rhoInvKm[j] = Tables.intpextp(xMicro, rho, x, true) * 1000.;
      vKmh[j] = Tables.intpextp(xMicro, vMicro, x, true) * 3.6;
      qInvH[j] = rhoInvKm[j] * vKmh[j];
    }
  }

  /**
     * Write output.
     * 
     * @param time
     *            the time
     */
  private void writeOutput(double time) {
    for (int j = 0; j < rhoInvKm.length; j++) {
      final double x = j * dxOut;
      writer.printf("%10.1f, %10.2f, %10.4f, %10.4f, %10.4f%n", x, time, rhoInvKm[j], vKmh[j], qInvH[j]);
    }
    writer.printf("%n");
    writer.flush();
  }
}