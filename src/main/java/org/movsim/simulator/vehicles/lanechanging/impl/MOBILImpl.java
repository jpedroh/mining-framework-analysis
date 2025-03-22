package org.movsim.simulator.vehicles.lanechanging.impl;
import java.util.List;
import org.movsim.input.model.vehicle.laneChanging.LaneChangingMobilData;
import org.movsim.simulator.Constants;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.impl.VehicleContainerImpl;

public class MOBILImpl {
  private double politeness;

  private double threshold;

  private double bSafe;

  private double gapMin;

  private double biasRight;

  private double thresholdRef;

  private double biasRightRef;

  private double bSafeRef;

  private double pRef;

  private final Vehicle me;

  public MOBILImpl(final Vehicle vehicle) {
    this.me = vehicle;
  }

  public MOBILImpl(final Vehicle vehicle, LaneChangingMobilData lcMobilData) {
    this.me = vehicle;
    bSafeRef = bSafe = lcMobilData.getSafeDeceleration();
    biasRightRef = biasRight = lcMobilData.getRightBiasAcceleration();
    gapMin = lcMobilData.getMinimumGap();
    thresholdRef = threshold = lcMobilData.getThresholdAcceleration();
    pRef = politeness = lcMobilData.getPoliteness();
  }

  private boolean neigborsInProcessOfLaneChanging(final Vehicle v1, final Vehicle v2, final Vehicle v3) {
    final boolean oldFrontVehIsLaneChanging = (v1 == null) ? false : v1.inProcessOfLaneChanging();
    final boolean newFrontVehIsLaneChanging = (v2 == null) ? false : v2.inProcessOfLaneChanging();
    final boolean newBackVehIsLaneChanging = (v3 == null) ? false : v3.inProcessOfLaneChanging();
    return (oldFrontVehIsLaneChanging || newFrontVehIsLaneChanging || newBackVehIsLaneChanging);
  }

  private boolean safetyCheckGaps(double gapFront, double gapBack) {
    return ((gapFront < gapMin) || (gapBack < gapMin));
  }

  private boolean safetyCheckAcceleration(double acc) {
    return acc <= -bSafe;
  }

  public double calcAccelerationBalance(final int direction, final List<VehicleContainer> lanes) {
    final int currentLane = me.getLane();
    final VehicleContainer ownLane = lanes.get(currentLane);
    final VehicleContainer newLane = lanes.get(currentLane + direction);
    double prospectiveBalance = -Double.MAX_VALUE;
    final Vehicle newFront = newLane.getLeader(me);
    final Vehicle oldFront = ownLane.getLeader(me);
    final Vehicle newBack = newLane.getFollower(me);
    if (neigborsInProcessOfLaneChanging(oldFront, newFront, newBack)) {
      return prospectiveBalance;
    }
    final double gapFront = me.getNetDistance(newFront);
    final double gapBack = (newBack == null) ? Constants.GAP_INFINITY : newBack.getNetDistance(me);
    if (safetyCheckGaps(gapFront, gapBack)) {
      return prospectiveBalance;
    }
    final VehicleContainer newSituationNewBack = new VehicleContainerImpl(0);
    newSituationNewBack.addTestwise(newBack);
    newSituationNewBack.addTestwise(me);
    final VehicleContainer leftLaneNewBack = (direction == Constants.TO_RIGHT || currentLane + direction + Constants.TO_LEFT >= lanes.size()) ? null : lanes.get(currentLane + direction + Constants.TO_LEFT);
    final double newBackNewAcc = (newBack == null) ? 0 : newBack.calcAccModel(newSituationNewBack, leftLaneNewBack);
    if (safetyCheckAcceleration(newBackNewAcc)) {
      return prospectiveBalance;
    }
    final VehicleContainer leftLaneMeOld = (currentLane + Constants.TO_LEFT) >= lanes.size() ? null : lanes.get(currentLane + Constants.TO_LEFT);
    final double meOldAcc = me.calcAccModel(ownLane, leftLaneMeOld);
    final Vehicle oldBack = ownLane.getFollower(me);
    final double oldBackOldAcc = (oldBack != null) ? oldBack.calcAccModel(ownLane, leftLaneMeOld) : 0;
    final VehicleContainer leftLaneNewBackOldAcc = (currentLane + direction + Constants.TO_LEFT >= lanes.size()) ? null : lanes.get(currentLane + direction + Constants.TO_LEFT);
    final double newBackOldAcc = (newBack != null) ? newBack.calcAccModel(newLane, leftLaneNewBackOldAcc) : 0;
    final VehicleContainer newSituationMe = new VehicleContainerImpl(0);
    newSituationMe.addTestwise(me);
    newSituationMe.addTestwise(newFront);
    final VehicleContainer leftLaneNewMe;
    if (direction == Constants.TO_LEFT) {
      leftLaneNewMe = leftLaneNewBack;
    } else {
      leftLaneNewMe = new VehicleContainerImpl(0);
      leftLaneNewMe.addTestwise(oldFront);
    }
    final double meNewAcc = me.calcAccModel(newSituationMe, leftLaneNewBack);
    final VehicleContainer newSituationOldBack = new VehicleContainerImpl(0);
    newSituationOldBack.addTestwise(oldFront);
    newSituationOldBack.addTestwise(oldBack);
    final VehicleContainer leftLaneNewSituationOldBack;
    if (direction == Constants.TO_LEFT) {
      leftLaneNewSituationOldBack = new VehicleContainerImpl(0);
      leftLaneNewSituationOldBack.addTestwise(me);
    } else {
      leftLaneNewSituationOldBack = leftLaneMeOld;
    }
    final double oldBackNewAcc = (oldBack != null) ? oldBack.calcAccModel(newSituationOldBack, null) : 0;
    final double oldBackDiffAcc = oldBackNewAcc - oldBackOldAcc;
    final double newBackDiffAcc = newBackNewAcc - newBackOldAcc;
    final double meDiffAcc = meNewAcc - meOldAcc;
    final int changeTo = newLane.getLaneIndex() - ownLane.getLaneIndex();
    final double biasSign = (changeTo == Constants.TO_LEFT) ? 1 : -1;
    prospectiveBalance = meDiffAcc + politeness * (oldBackDiffAcc + newBackDiffAcc) - threshold - biasSign * biasRight;
    return prospectiveBalance;
  }

  public double getMinimumGap() {
    return gapMin;
  }

  public double getSafeDeceleration() {
    return bSafe;
  }
}