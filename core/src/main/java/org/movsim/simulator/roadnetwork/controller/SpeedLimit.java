package org.movsim.simulator.roadnetwork.controller;
import org.movsim.network.autogen.opendrive.OpenDRIVE;
import org.movsim.network.autogen.opendrive.UserData;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.movsim.simulator.roadnetwork.SignalPoint;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.utilities.Units;
import com.google.common.base.Preconditions;

/**
 * Sets the externally given maximum speed for vehicles passing the position of the 'speedlimit'.
 * 
 * <p>
 * The position, the speedlimit value and the validity length are given in the xodr network input file. Note that a second 'speedlimit'
 * cancelling the first speedlimit will be created if a 'validity length' is defined.
 * 
 * <br>
 * created: May 20, 2013<br>
 * 
 */
public class SpeedLimit extends RoadObjectController {
  private final double speedLimitValue;

  private final SignalPoint signalPoint;

  public SpeedLimit(double position, double speedLimitValue, RoadSegment roadSegment) {
    super(RoadObjectType.SPEEDLIMIT, position, roadSegment);
    this.speedLimitValue = speedLimitValue;
    signalPoint = new SignalPoint(position, roadSegment);
  }

  public SpeedLimit(OpenDRIVE.Road.Objects.Object roadObject, RoadSegment roadSegment) {
    super(RoadObjectType.SPEEDLIMIT, roadObject.getS(), roadSegment);
    Preconditions.checkArgument(roadObject.isSetUserData());
    UserData userData = roadObject.getUserData().get(0);
    Preconditions.checkArgument(userData.getCode().equals("valueKMH"));
    this.speedLimitValue = Double.parseDouble(roadObject.getUserData().get(0).getValue()) * Units.KMH_TO_MS;
    signalPoint = new SignalPoint(position, roadSegment);
  }

  public long getSpeedLimitKmh() {
    return Math.round(speedLimitValue * Units.MS_TO_KMH);
  }

  public double getSpeedLimit() {
    return speedLimitValue;
  }

  @Override public void timeStep(double dt, double simulationTime, long iterationCount) {
    for (Vehicle vehicle : signalPoint.passedVehicles()) {
      vehicle.setSpeedlimit(speedLimitValue);
      LOG.debug("pos={} --> speedlimit in km/h={}", position, Units.MS_TO_KMH * speedLimitValue);
    }
  }

  @Override public String toString() {
    return "SpeedLimit [speedLimitValue=" + speedLimitValue + ", " + super.toString() + "]";
  }

  @Override public void createSignalPositions() {
    roadSegment.signalPoints().add(signalPoint);
  }
}