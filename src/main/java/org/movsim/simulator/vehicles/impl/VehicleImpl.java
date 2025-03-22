package org.movsim.simulator.vehicles.impl;
import java.util.List;
import org.movsim.input.model.VehicleInput;
import org.movsim.simulator.Constants;
import org.movsim.simulator.roadSection.TrafficLight;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Noise;
import org.movsim.simulator.vehicles.PhysicalQuantities;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.lanechanging.impl.LaneChangingModelImpl;
import org.movsim.simulator.vehicles.longmodel.Memory;
import org.movsim.simulator.vehicles.longmodel.TrafficLightApproaching;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.movsim.simulator.vehicles.longmodel.impl.MemoryImpl;
import org.movsim.simulator.vehicles.longmodel.impl.TrafficLightApproachingImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class VehicleImpl.
 */
public class VehicleImpl implements Vehicle {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(VehicleImpl.class);

  private final static double THRESHOLD_BRAKELIGHT_ON = 0.2;

  private final static double THRESHOLD_BRAKELIGHT_OFF = 0.1;

  private final static double FINITE_LANE_CHANGE_TIME_S = 5;

  /** The label. */
  private final String label;

  /** The length. */
  private final double length;

  /** The position. */
  private double position;

  /** The old position. */
  private double positionOld;

  /** The speed. */
  private double speed;

  /** The acceleration model. */
  private double accModel;

  /** The acceleration. */
  private double acc;

  private double accOld;

  /** The reaction time. */
  private final double reactionTime;

  /** The max deceleration . */
  private final double maxDecel;

  /** The id. */
  private final int id;

  /** The vehicle number. */
  private int vehNumber;

  /** The lane. */
  private int lane;

  private int laneOld;

  /** variable for remembering new target lane when assigning to new vehContainerLane */
  private int targetLane;

  /** finite lane-changing duration */
  private double tLaneChangingDelay;

  /** The speed limit. */
  private double speedlimit;

  /** The long model. */
  private final AccelerationModel accelerationModel;

  /** The lane-changing model. */
  private final LaneChangingModelImpl lcModel;

  /** The memory. */
  private Memory memory = null;

  /** The noise. */
  private Noise noise = null;

  /** The traffic light approaching. */
  private final TrafficLightApproaching trafficLightApproaching;

  /** The cyclic buffer. */
  private final CyclicBufferImpl cyclicBuffer;

  private boolean isBrakeLightOn;

  private PhysicalQuantities physQuantities;

  /**
     * Instantiates a new vehicle impl.
     *
     * @param label the label
     * @param id the id
     * @param longModel the acceleration model. longitudinal ("car-following") model.
     * @param vehInput the veh input
     * @param cyclicBuffer the cyclic buffer
     * @param lcModel the lanechange model
     */
  public VehicleImpl(String label, int id, final AccelerationModel longModel, final VehicleInput vehInput, final CyclicBufferImpl cyclicBuffer, final LaneChangingModelImpl lcModel) {
    this.label = label;
    this.id = id;
    length = vehInput.getLength();
    reactionTime = vehInput.getReactionTime();
    maxDecel = vehInput.getMaxDeceleration();
    this.accelerationModel = longModel;
    physQuantities = new PhysicalQuantities(this);
    this.lcModel = lcModel;
    lcModel.initialize(this);
    this.cyclicBuffer = cyclicBuffer;
    positionOld = 0;
    position = 0;
    speed = 0;
    acc = 0;
    isBrakeLightOn = false;
    speedlimit = Constants.MAX_VEHICLE_SPEED;
    if (vehInput.isWithMemory()) {
      memory = new MemoryImpl(vehInput.getMemoryInputData());
    }
    if (vehInput.isWithNoise()) {
      noise = new NoiseImpl(vehInput.getNoiseInputData());
    }
    trafficLightApproaching = new TrafficLightApproachingImpl();
    assert FINITE_LANE_CHANGE_TIME_S > 0;
  }

  @Override public void init(double pos, double v, int lane) {
    this.laneOld = this.lane;
    this.position = pos;
    this.positionOld = pos;
    this.speed = v;
    this.targetLane = this.lane = lane;
  }

  @Override public String getLabel() {
    return label;
  }

  @Override public double getLength() {
    return length;
  }

  @Override public double getWidth() {
    return Constants.VEHICLE_WIDTH;
  }

  @Override public double getPosition() {
    return position;
  }

  @Override public double posFrontBumper() {
    return position + 0.5 * length;
  }

  @Override public double posRearBumper() {
    return position - 0.5 * length;
  }

  @Override public double getPositionOld() {
    return positionOld;
  }

  /**
     * Sets the position.
     * 
     * @param position
     *            the new position
     */
  @Override public void setPosition(double position) {
    this.position = position;
  }

  @Override public double getSpeed() {
    return speed;
  }

  /**
     * Sets the speed.
     * 
     * @param speed
     *            the new speed
     */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  @Override public double getSpeedlimit() {
    return speedlimit;
  }

  @Override public void setSpeedlimit(double speedlimit) {
    this.speedlimit = speedlimit;
  }

  @Override public double getAcc() {
    return acc;
  }

  @Override public double accModel() {
    return accModel;
  }

  @Override public double getDistanceToTrafficlight() {
    return trafficLightApproaching.getDistanceToTrafficlight();
  }

  @Override public int getId() {
    return id;
  }

  @Override public boolean isFromOnramp() {
    return (vehNumber < 0);
  }

  @Override public int getVehNumber() {
    return vehNumber;
  }

  @Override public void setVehNumber(int vehNumber) {
    this.vehNumber = vehNumber;
  }

  @Override public double getNetDistance(final Moveable vehFront) {
    if (vehFront == null) {
      return Constants.GAP_INFINITY;
    }
    return (vehFront.getPosition() - position - 0.5 * (getLength() + vehFront.getLength()));
  }

  @Override public double getRelSpeed(Moveable vehFront) {
    if (vehFront == null) {
      return 0;
    }
    return (speed - vehFront.getSpeed());
  }

  @Override public void calcAcceleration(double dt, final VehicleContainer vehContainer, final VehicleContainer vehContainerLeftLane, double alphaT, double alphaV0) {
    accOld = acc;
    double accError = 0;
    if (noise != null) {
      noise.update(dt);
      accError = noise.getAccError();
      final Moveable vehFront = vehContainer.getLeader(this);
      if (getNetDistance(vehFront) < Constants.CRITICAL_GAP) {
        accError = Math.min(accError, 0.);
      }
    }
    double alphaTLocal = alphaT;
    double alphaV0Local = alphaV0;
    double alphaALocal = 1;
    if (memory != null) {
      final double v0 = accelerationModel.getDesiredSpeedParameterV0();
      memory.update(dt, speed, v0);
      alphaTLocal *= memory.alphaT();
      alphaV0Local *= memory.alphaV0();
      alphaALocal *= memory.alphaA();
    }
    accModel = calcAccModel(vehContainer, vehContainerLeftLane, alphaTLocal, alphaV0Local, alphaALocal);
    if (trafficLightApproaching.considerTrafficLight()) {
      acc = Math.min(accModel, trafficLightApproaching.accApproaching());
    } else {
      acc = accModel;
    }
    acc = Math.max(acc + accError, -maxDecel);
  }

  @Override public double calcAccModel(final VehicleContainer vehContainer, final VehicleContainer vehContainerLeftLane) {
    return calcAccModel(vehContainer, vehContainerLeftLane, 1, 1, 1);
  }

  private double calcAccModel(final VehicleContainer vehContainer, final VehicleContainer vehContainerLeftLane, double alphaTLocal, double alphaV0Local, double alphaALocal) {
    double acc;
    if (lcModel.isInitialized() && lcModel.withEuropeanRules()) {
      acc = accelerationModel.calcAccEur(lcModel.vCritEurRules(), this, vehContainer, vehContainerLeftLane, alphaTLocal, alphaV0Local, alphaALocal);
    } else {
      acc = accelerationModel.calcAcc(this, vehContainer, alphaTLocal, alphaV0Local, alphaALocal);
    }
    return acc;
  }

  @Override public void updatePostionAndSpeed(double dt) {
    positionOld = position;
    if (accelerationModel.isCA()) {
      speed = (int) (speed + dt * acc + 0.5);
      position = (int) (position + dt * speed + 0.5);
    } else {
      if (speed < 0) {
        speed = 0;
      }
      final double advance = (acc * dt >= -speed) ? speed * dt + 0.5 * acc * dt * dt : -0.5 * speed * speed / acc;
      position += advance;
      speed += dt * acc;
      if (speed < 0) {
        speed = 0;
        acc = 0;
      }
    }
  }

  @Override public int getLane() {
    return lane;
  }

  @Override public boolean hasReactionTime() {
    return (reactionTime + Constants.SMALL_VALUE > 0);
  }

  @Override public void updateTrafficLight(double time, TrafficLight trafficLight) {
    trafficLightApproaching.update(this, time, trafficLight, accelerationModel);
  }

  @Override public void removeObservers() {
    accelerationModel.removeObserver();
  }

  @Override public LaneChangingModelImpl getLaneChangingModel() {
    return lcModel;
  }

  @Override public AccelerationModel getAccelerationModel() {
    return accelerationModel;
  }

  @Override public boolean considerLaneChanging(double dt, final List<VehicleContainer> vehContainers) {
    if (!lcModel.isInitialized()) {
      return false;
    }
    if (inProcessOfLaneChanging()) {
      updateLaneChangingDelay(dt);
      return false;
    }
    if (vehContainers.size() < 2) {
      return false;
    }
    final int saveLane = lane;
    final int laneChangingDirection = lcModel.determineLaneChangingDirection(vehContainers);
    assert saveLane != lane;
    if (saveLane != lane) {
      System.err.println("vehicle\'s lane changed: saveLane=" + saveLane + ", lane=" + lane);
    }
    if (laneChangingDirection != Constants.NO_CHANGE) {
      setTargetLane(lane + laneChangingDirection);
      resetDelay();
      updateLaneChangingDelay(dt);
      logger.info("do lane change to={} into target lane={}", laneChangingDirection, targetLane);
      return true;
    }
    return false;
  }

  @Override public void initLaneChangeFromRamp(int oldLane) {
    laneOld = oldLane;
    resetDelay();
    final double delayInit = 0.2;
    updateLaneChangingDelay(delayInit);
    logger.info("do lane change from ramp: virtual old lane (origin)={}, contLane={}", lane, getContinousLane());
    if (oldLane == Constants.TO_LEFT) {
      System.out.printf(".......... do lane change from ramp: virtual old lane (origin)=%d, contLane=%.4f", lane, getContinousLane());
    }
  }

  @Override public int getTargetLane() {
    return targetLane;
  }

  /**
     * Sets the target lane.
     *
     * @param targetLane the new target lane
     */
  private void setTargetLane(int targetLane) {
    assert targetLane >= 0;
    this.targetLane = targetLane;
  }

  public boolean inProcessOfLaneChanging() {
    return (tLaneChangingDelay > 0 && tLaneChangingDelay < FINITE_LANE_CHANGE_TIME_S);
  }

  /**
     * Reset delay.
     */
  private void resetDelay() {
    tLaneChangingDelay = 0;
  }

  /**
     * Update lane changing delay.
     *
     * @param dt the dt
     */
  public void updateLaneChangingDelay(double dt) {
    tLaneChangingDelay += dt;
  }

  @Override public double getContinousLane() {
    if (inProcessOfLaneChanging()) {
      final double fractionTimeLaneChange = Math.min(1, tLaneChangingDelay / FINITE_LANE_CHANGE_TIME_S);
      return fractionTimeLaneChange * lane + (1 - fractionTimeLaneChange) * laneOld;
    }
    return getLane();
  }

  @Override public boolean isBrakeLightOn() {
    updateBrakeLightStatus();
    return isBrakeLightOn;
  }

  /**
     * Update brake light status.
     */
  private void updateBrakeLightStatus() {
    if (isBrakeLightOn) {
      if (acc > -THRESHOLD_BRAKELIGHT_OFF || speed <= 0.0001) {
        isBrakeLightOn = false;
      }
    } else {
      if (accOld > -THRESHOLD_BRAKELIGHT_ON && acc < -THRESHOLD_BRAKELIGHT_ON) {
        isBrakeLightOn = true;
      }
    }
  }

  @Override public PhysicalQuantities physicalQuantities() {
    return physQuantities;
  }
}