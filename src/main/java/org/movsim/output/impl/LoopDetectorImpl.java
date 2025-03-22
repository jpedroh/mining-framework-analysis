package org.movsim.output.impl;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.movsim.output.LoopDetector;
import org.movsim.output.LoopDetectorObservable;
import org.movsim.output.LoopDetectorObserver;
import org.movsim.simulator.Constants;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.utilities.FileUtils;

/**
 * The Class LoopDetectorImpl.
 */
public class LoopDetectorImpl implements LoopDetector, LoopDetectorObservable {
  /** The dt sample. */
  private final double dtSample;

  /** The det position. */
  private final double detPosition;

  /** The time when calculating the average */
  private double timeAtAverage;

  /** The time offset. */
  private double timeOffset;

  /** The print writer. */
  private PrintWriter printWriter = null;

  /** The veh count. */
  private int vehCount;

  /** The v sum. */
  private double vSum;

  /** The occ time. */
  private double occTime;

  /** The sum inv v. */
  private double sumInvV;

  /** The sum inv q. */
  private double sumInvQ;

  /** The mean speed. */
  private double meanSpeed;

  /** The rho arithmetic. */
  private double rhoArithmetic;

  /** The flow. */
  private double flow;

  /** The occupancy. */
  private double occupancy;

  /** The veh count output. */
  private int vehCountOutput;

  /** The mean harmonic speed. */
  private double meanHarmSpeed;

  /** The harmonic mean timegap. */
  private double meanHarmTimegap;

  /** The write output. */
  private final boolean writeOutput;

  private ArrayList<LoopDetectorObserver> observers;

  /**
     * Instantiates a new loop detector impl.
     * 
     * @param projectName
     *            the project name
     * @param writeOutput
     *            the write output
     * @param detPosition
     *            the det position
     * @param dtSample
     *            the dt sample
     */
  public LoopDetectorImpl(String projectName, boolean writeOutput, double detPosition, double dtSample) {
    this.writeOutput = writeOutput;
    this.detPosition = detPosition;
    this.dtSample = dtSample;
    timeOffset = 0;
    reset();
    if (writeOutput) {
      final int xDetectorInt = (int) detPosition;
      final String filename = projectName + ".x" + xDetectorInt + "_det.csv";
      printWriter = initFile(filename);
      writeAggregatedData(0);
    }
  }

  /**
     * Reset.
     */
  private void reset() {
    vehCount = 0;
    vSum = 0;
    occTime = 0;
    sumInvQ = 0;
    sumInvV = 0;
  }

  @Override public void update(double time, VehicleContainer vehicleContainer) {
    for (final Vehicle veh : vehicleContainer.getVehicles()) {
      if ((veh.oldPosition() < detPosition) && (veh.position() >= detPosition)) {
        vehCount++;
        final double speedVeh = veh.speed();
        vSum += speedVeh;
        occTime += veh.length() / speedVeh;
        sumInvV += (speedVeh > 0) ? 1. / speedVeh : 0;
        final Vehicle vehFront = vehicleContainer.getLeader(veh);
        final double brutTimegap = (vehFront == null) ? 0 : (vehFront.position() - veh.position()) / vehFront.speed();
        sumInvQ += (brutTimegap > 0) ? 1. / brutTimegap : 0;
      }
    }
    if ((time - timeOffset + Constants.SMALL_VALUE) >= dtSample) {
      timeAtAverage = time;
      calculateAverages();
      if (writeOutput) {
        writeAggregatedData(time);
      }
      timeOffset = time;
    }
  }

  /**
     * Calculate averages.
     */
  private void calculateAverages() {
    flow = vehCount / dtSample;
    meanSpeed = (vehCount == 0) ? 0 : vSum / vehCount;
    rhoArithmetic = (vehCount == 0) ? 0 : flow / meanSpeed;
    occupancy = occTime / dtSample;
    vehCountOutput = vehCount;
    meanHarmSpeed = (vehCount == 0) ? 0 : 1. / (sumInvV / vehCount);
    meanHarmTimegap = (vehCount == 0) ? 0 : sumInvQ / vehCount;
    reset();
  }

  /**
     * Inits the file.
     * 
     * @param filename
     *            the filename
     * @return the prints the writer
     */
  private PrintWriter initFile(String filename) {
    printWriter = FileUtils.getWriter(filename);
    printWriter.printf(Constants.COMMENT_CHAR + " dtSample in s = %-8.4f%n", dtSample);
    printWriter.printf(Constants.COMMENT_CHAR + " position xDet = %-8.4f%n", detPosition);
    printWriter.printf(Constants.COMMENT_CHAR + " arithmetic average for density rho%n");
    printWriter.printf(Constants.COMMENT_CHAR + "   t[s],  v[km/h],rho[1/km],   Q[1/h],  nVeh,  Occup[1],1/<1/v>(km/h),<1/Tbrutto>(1/s)%n");
    printWriter.flush();
    return printWriter;
  }

  /**
     * Write aggregated data.
     * 
     * @param time
     *            the time
     */
  private void writeAggregatedData(double time) {
    if (printWriter == null) {
      return;
    }
    printWriter.printf("%8.1f, %8.3f, %8.3f, %8.1f, %5d, %8.7f, %8.3f, %8.5f%n", time, 3.6 * meanSpeed, 1000 * rhoArithmetic, 3600 * flow, vehCountOutput, occupancy, 3.6 * meanHarmSpeed, meanHarmTimegap);
    printWriter.flush();
  }

  @Override public void closeFiles() {
    if (printWriter != null) {
      printWriter.close();
    }
  }

  @Override public double position() {
    return detPosition;
  }

  @Override public double flow() {
    return flow;
  }

  @Override public double meanSpeed() {
    return meanSpeed;
  }

  @Override public double occupancy() {
    return occupancy;
  }

  @Override public double rhoArithmetic() {
    return rhoArithmetic;
  }

  public void registerObserver(LoopDetectorObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(LoopDetectorObserver observer) {
    int i = observers.indexOf(observer);
    if (i >= 0) {
      observers.remove(i);
    }
  }

  public void notifyObservers() {
    for (LoopDetectorObserver obs : observers) {
      obs.update(timeAtAverage, flow(), meanSpeed(), rhoArithmetic());
    }
  }
}