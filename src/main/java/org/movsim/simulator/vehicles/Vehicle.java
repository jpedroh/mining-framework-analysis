package org.movsim.simulator.vehicles;
import org.movsim.consumption.FuelConsumption;
import org.movsim.input.model.VehicleInput;
import org.movsim.simulator.MovsimConstants;
import org.movsim.simulator.roadnetwork.Lane;
import org.movsim.simulator.roadnetwork.LaneSegment;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.movsim.simulator.roadnetwork.TrafficLight;
import org.movsim.simulator.vehicles.lanechanging.LaneChangingModel;
import org.movsim.simulator.vehicles.longmodel.Memory;
import org.movsim.simulator.vehicles.longmodel.TrafficLightApproaching;
import org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Vehicle.
 */
public class Vehicle {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(Vehicle.class);

  protected static final int INITIAL_ID = 1;

  protected static final int INITIAL_TEMPLATE_ID = -1;

  /**
     * 'Not Set' vehicle id value, guaranteed not to be used by any vehicles.
     */
  public static final int ID_NOT_SET = -1;

  /**
     * 'Not Set' road segment id value, guaranteed not to be used by any vehicles.
     */
  public static final int ROAD_SEGMENT_ID_NOT_SET = -1;

  /** in m/s^2 */
  private final static double THRESHOLD_BRAKELIGHT_ON = 0.2;

  /** in m/s^2 */
  private final static double THRESHOLD_BRAKELIGHT_OFF = 0.1;

  /** needs to be > 0 */
  private final static double FINITE_LANE_CHANGE_TIME_S = 5;

  /** The label. */
  private final String label;

  /** The length. */
  private final double length;

  private final double width;

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
  int id;

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
  private AccelerationModel accelerationModel;

  /** The lane-changing model. */
  private LaneChangingModel lcModel;

  /** The memory. */
  private Memory memory = null;

  /** The noise. */
  private Noise noise = null;

  private int color;

  private Object colorObject;

  /** The traffic light approaching. */
  private final TrafficLightApproaching trafficLightApproaching;

  private final FuelConsumption fuelModel;

  private boolean isBrakeLightOn;

  private PhysicalQuantities physQuantities;

  private int roadSegmentId;

  private double roadSegmentLength;

  private int exitRoadSegmentId = ROAD_SEGMENT_ID_NOT_SET;

  private long roadId;

  private static int nextId = INITIAL_ID;

  private static int nextTemplateId = INITIAL_TEMPLATE_ID;

  public static enum IntegrationType {
    EULER,
    KINEMATIC,
    RUNGE_KUTTA
  }

  private static IntegrationType integrationType = IntegrationType.KINEMATIC;

  /**
     * Resets the next id.
     */
  public static void resetNextId() {
    nextId = INITIAL_ID;
    nextTemplateId = INITIAL_TEMPLATE_ID;
  }

  /**
     * Returns the id of the last vehicle created.
     * 
     * @return the id of the last vehicle created
     */
  public static int lastIdSet() {
    return nextId - 1;
  }

  /**
     * Returns the number of vehicles that have been created. Used for instrumentation.
     * 
     * @return the number of vehicles that have been created
     */
  public static int count() {
    return nextId - INITIAL_ID;
  }

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
  public Vehicle(String label, int id, final AccelerationModel longModel, final VehicleInput vehInput, final Object cyclicBuffer, final LaneChangingModel lcModel, final FuelConsumption fuelModel) {
    this.label = label;
    this.id = id;
    this.fuelModel = fuelModel;
    length = vehInput.getLength();
    width = MovsimConstants.VEHICLE_WIDTH;
    reactionTime = vehInput.getReactionTime();
    maxDecel = vehInput.getMaxDeceleration();
    initialize();
    this.accelerationModel = longModel;
    physQuantities = new PhysicalQuantities(this);
    this.lcModel = lcModel;
    lcModel.initialize(this);
    if (vehInput.isWithMemory()) {
      memory = new Memory(vehInput.getMemoryInputData());
    }
    if (vehInput.isWithNoise()) {
      noise = new Noise(vehInput.getNoiseInputData());
    }
    trafficLightApproaching = new TrafficLightApproaching();
    assert FINITE_LANE_CHANGE_TIME_S > 0;
  }

  /**
     * Constructor.
     */
  public Vehicle(double rearPosition, double speed, int lane, double length, double width) {
    assert rearPosition >= 0.0;
    assert speed >= 0.0;
    id = nextId++;
    this.length = length;
    setRearPosition(rearPosition);
    this.speed = speed;
    this.lane = lane;
    this.width = width;
    this.color = 0;
    fuelModel = null;
    trafficLightApproaching = null;
    reactionTime = 0.0;
    maxDecel = 0.0;
    lcModel = null;
    accelerationModel = null;
    label = "";
    physQuantities = new PhysicalQuantities(this);
  }

  /**
     * Copy constructor.
     * 
     * @param source
     */
  public Vehicle(Vehicle source) {
    id = source.id;
    type = source.type;
    position = source.position;
    speed = source.speed;
    lane = source.lane;
    length = source.length;
    width = source.width;
    color = source.color;
    fuelModel = source.fuelModel;
    trafficLightApproaching = source.trafficLightApproaching;
    reactionTime = source.reactionTime;
    maxDecel = source.maxDecel;
    lcModel = source.lcModel;
    accelerationModel = source.accelerationModel;
    label = source.label;
  }

  /**
     * Constructor.
     */
  public Vehicle(org.movsim.simulator.vehicles.Vehicle.Type car, AccelerationModel ldm, Object lcm, double length, double width, int i) {
    id = nextId++;
    this.length = length;
    setRearPosition(0.0);
    this.speed = 0.0;
    this.lane = Lane.NONE;
    this.width = width;
    this.color = 0;
    fuelModel = null;
    trafficLightApproaching = null;
    reactionTime = 0.0;
    maxDecel = 0.0;
    lcModel = null;
    accelerationModel = ldm;
    label = "";
  }

  private void initialize() {
    positionOld = 0;
    position = 0;
    speed = 0;
    acc = 0;
    isBrakeLightOn = false;
    speedlimit = MovsimConstants.MAX_VEHICLE_SPEED;
  }

  public void init(double pos, double v, int lane, long roadId) {
    this.laneOld = this.lane;
    this.roadId = roadId;
    this.position = pos;
    this.positionOld = pos;
    this.speed = v;
    this.targetLane = this.lane = lane;
  }

  public String getLabel() {
    return label;
  }

  /**
     * Sets this vehicle's color.
     * 
     * @param color
     *            RGB integer color value
     */
  public final void setColor(int color) {
    this.color = color;
  }

  /**
     * Returns this vehicle's color.
     * 
     * @return vehicle's color, as an RGB integer
     */
  public final int color() {
    return color;
  }

  /**
     * Sets this vehicle's color object cache value. Primarily of use by AWT which rather
     * inefficiently uses objects rather than integers to represent color values. Note that
     * an object is cached so Vehicle.java has no dependency on AWT.
     * 
     * @param colorObject
     */
  public final void setColorObject(Object colorObject) {
    this.colorObject = colorObject;
  }

  /**
     * Returns the previously cached object associated with this vehicle's color.
     * 
     * @return vehicle's previously cached color object
     */
  public final Object colorObject() {
    return colorObject;
  }

  /**
     * Returns this vehicle's length.
     * 
     * @return vehicle's length, in meters
     */
  public double getLength() {
    return length;
  }

  /**
     * Returns this vehicle's width.
     * 
     * @return vehicle's width, in meters
     */
  public double getWidth() {
    return width;
  }

  public double getPosition() {
    return position;
  }

  public double posFrontBumper() {
    return position + 0.5 * length;
  }

  public double posRearBumper() {
    return position - 0.5 * length;
  }

  /**
     * Sets the position of the rear of this vehicle.
     * 
     * @param rearPosition
     *            new rear position
     */
  public final void setRearPosition(double rearPosition) {
    this.position = rearPosition + 0.5 * length;
  }

  public double getPositionOld() {
    return positionOld;
  }

  /**
     * Sets the position.
     * 
     * @param position
     *            the new position
     */
  public void setPosition(double position) {
    this.position = position;
  }

  /**
     * Sets the position of the middle of the vehicle.
     * 
     * @param position
     *            the position of the middle of the vehicle
     */
  public void setMidPosition(double position) {
    this.position = position;
  }

  /**
     * Returns this vehicle's speed.
     * 
     * @return this vehicle's speed, in m/s
     */
  public double getSpeed() {
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

  public double getSpeedlimit() {
    return speedlimit;
  }

  public void setSpeedlimit(double speedlimit) {
    this.speedlimit = speedlimit;
  }

  public double getAcc() {
    return acc;
  }

  public double accModel() {
    return accModel;
  }

  public double getDistanceToTrafficlight() {
    return trafficLightApproaching.getDistanceToTrafficlight();
  }

  /**
     * Returns this vehicle's id.
     * 
     * @return vehicle's id
     * 
     */
  public int getId() {
    return id;
  }

  public long getRoadId() {
    return roadId;
  }

  public boolean isFromOnramp() {
    return (vehNumber < 0);
  }

  public int getVehNumber() {
    return vehNumber;
  }

  public void setVehNumber(int vehNumber) {
    this.vehNumber = vehNumber;
  }

  public double getNetDistance(final Vehicle vehFront) {
    if (vehFront == null) {
      return MovsimConstants.GAP_INFINITY;
    }
    final double netGap = vehFront.getPosition() - position - 0.5 * (getLength() + vehFront.getLength());
    return netGap;
  }

  public double getRelSpeed(Vehicle vehFront) {
    if (vehFront == null) {
      return 0;
    }
    return (speed - vehFront.getSpeed());
  }

  public void calcAcceleration(double dt, final LaneSegment vehContainer, final LaneSegment vehContainerLeftLane, double alphaT, double alphaV0) {
    accOld = acc;
    double accError = 0;
    if (noise != null) {
      noise.update(dt);
      accError = noise.getAccError();
      final Vehicle vehFront = vehContainer.frontVehicle(this);
      if (getNetDistance(vehFront) < MovsimConstants.CRITICAL_GAP) {
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
    if (trafficLightApproaching != null && trafficLightApproaching.considerTrafficLight()) {
      acc = Math.min(accModel, trafficLightApproaching.accApproaching());
    } else {
      acc = accModel;
    }
    acc = Math.max(acc + accError, -maxDecel);
  }

  public double calcAccModel(final LaneSegment vehContainer, final LaneSegment vehContainerLeftLane) {
    return calcAccModel(vehContainer, vehContainerLeftLane, 1, 1, 1);
  }

  private double calcAccModel(final LaneSegment vehContainer, final LaneSegment vehContainerLeftLane, double alphaTLocal, double alphaV0Local, double alphaALocal) {
    if (accelerationModel == null) {
      return 0.0;
    }
    final double acc;
    if (lcModel != null && lcModel.isInitialized() && lcModel.withEuropeanRules()) {
      acc = accelerationModel.calcAccEur(lcModel.vCritEurRules(), this, vehContainer, vehContainerLeftLane, alphaTLocal, alphaV0Local, alphaALocal);
    } else {
      acc = accelerationModel.calcAcc(this, vehContainer, alphaTLocal, alphaV0Local, alphaALocal);
    }
    return acc;
  }

  public void updatePositionAndSpeed(double dt) {
    positionOld = position;
    if (accelerationModel != null && accelerationModel.isCA()) {
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

  public int getLane() {
    return lane;
  }

  public void setLane(int lane) {
    this.lane = lane;
  }

  public boolean hasReactionTime() {
    return (reactionTime + MovsimConstants.SMALL_VALUE > 0);
  }

  public void updateTrafficLight(double time, TrafficLight trafficLight) {
    trafficLightApproaching.update(this, time, trafficLight, accelerationModel);
  }

  public void removeObservers() {
    accelerationModel.removeObserver();
  }

  public LaneChangingModel getLaneChangingModel() {
    return lcModel;
  }

  public void setLaneChangingModel(LaneChangingModel lcModel) {
    this.lcModel = lcModel;
  }

  public AccelerationModel getAccelerationModel() {
    return accelerationModel;
  }

  public void setAccelerationModel(AccelerationModel AccelerationModel) {
    this.accelerationModel = AccelerationModel;
  }

  public boolean considerLaneChanging(double dt, RoadSegment roadSegment) {
    if (lcModel == null || !lcModel.isInitialized()) {
      return false;
    }
    if (roadSegment.laneCount() < 2) {
      return false;
    }
    if (inProcessOfLaneChanging()) {
      updateLaneChangingDelay(dt);
      return false;
    }
    final int laneChangingDirection = lcModel.determineLaneChangingDirection(roadSegment);
    if (laneChangingDirection != MovsimConstants.NO_CHANGE) {
      setTargetLane(lane + laneChangingDirection);
      resetDelay();
      updateLaneChangingDelay(dt);
      logger.info("veh id={}", id);
      logger.debug("do lane change to={} into target lane={}", laneChangingDirection, targetLane);
      logger.info("do lane change to={} into target lane={}", laneChangingDirection, targetLane);
      return true;
    }
    return false;
  }

  public void initLaneChangeFromRamp(int oldLane) {
    laneOld = oldLane;
    resetDelay();
    final double delayInit = 0.2;
    updateLaneChangingDelay(delayInit);
    logger.debug("do lane change from ramp: virtual old lane (origin)={}, contLane={}", lane, getContinousLane());
    if (oldLane == MovsimConstants.TO_LEFT) {
      logger.debug("do lane change from ramp: virtual old lane (origin)={}, contLane={}", lane, getContinousLane());
    }
  }

  public int getTargetLane() {
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
  private void updateLaneChangingDelay(double dt) {
    tLaneChangingDelay += dt;
  }

  public double getContinousLane() {
    if (inProcessOfLaneChanging()) {
      final double fractionTimeLaneChange = Math.min(1, tLaneChangingDelay / FINITE_LANE_CHANGE_TIME_S);
      return fractionTimeLaneChange * lane + (1 - fractionTimeLaneChange) * laneOld;
    }
    return getLane();
  }

  public boolean isBrakeLightOn() {
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

  public PhysicalQuantities physicalQuantities() {
    return physQuantities;
  }

  public double getActualFuelFlowLiterPerS() {
    if (fuelModel == null) {
      return 0;
    }
    return fuelModel.getFuelFlowInLiterPerS(speed, acc);
  }

  /**
     * 'Not Set' road exit position value, guaranteed not to be used by any vehicles.
     */
  public static final double EXIT_POSITION_NOT_SET = -1.0;

  public static enum Type {
    NONE,
    OBSTACLE,
    CAR,
    TRUCK,
    TEST_CAR
  }

  private Type type;

  /**
     * Returns this vehicle's type.
     * 
     * @return vehicle's type
     * 
     */
  public final Vehicle.Type type() {
    return type;
  }

  /**
     * Sets this vehicle's type.
     * @param type 
     * 
     */
  public final void setType(Vehicle.Type type) {
    this.type = type;
  }

  /**
     * <p>
     * Called when vehicle changes road segments (and possibly also lanes) at a link or junction.
     * </p>
     * <p>
     * Although the change of lanes is immediate, <code>lane</code>, <code>prevLane</code> and
     * <code>timeAtWhichLastChangedLanes</code> are used to interpolate this vehicle's lateral
     * position and so give the appearance of a smooth lane change.
     * </p>
     * 
     * @param newLane
     * @param newPos
     * @param exitPos
     */
  public void moveToNewRoadSegment(int newLane, double newRearPos, double exitPos) {
    final int delta = laneOld - lane;
    lane = newLane;
    laneOld = lane + delta;
    setRearPosition(newRearPos);
  }

  /**
     * Sets the road segment properties for this vehicle. Invoked after a vehicle has moved onto a
     * new road segment.
     * 
     * @param roadSegmentId
     * @param roadSegmentLength
     * 
     */
  public final void setRoadSegment(int roadSegmentId, double roadSegmentLength) {
    this.roadSegmentId = roadSegmentId;
    this.roadSegmentLength = roadSegmentLength;
  }

  /**
     * Returns the id of the road segment currently occupied by this vehicle.
     * 
     * @return id of the road segment currently occupied by this vehicle
     */
  public final int roadSegmentId() {
    return roadSegmentId;
  }

  /**
     * Returns the id of the road segment in which this vehicle wishes to exit.
     * 
     * @return id of exit road segment
     */
  public final int exitRoadSegmentId() {
    return exitRoadSegmentId;
  }
}