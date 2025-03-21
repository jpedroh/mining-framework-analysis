package harvard.robobees.simbeeotic.model;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import harvard.robobees.simbeeotic.SimEngine;
import harvard.robobees.simbeeotic.configuration.ConfigurationAnnotations.GlobalScope;
import harvard.robobees.simbeeotic.model.sensor.PoseSensor;
import harvard.robobees.simbeeotic.model.sensor.PositionSensor;
import harvard.robobees.simbeeotic.util.MathUtil;
import harvard.robobees.simbeeotic.util.PIDController;
import harvard.robobees.simbeeotic.util.MedianPIDController;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.log4j.Logger;
import javax.vecmath.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;

/**
 * A base class that implements a simple movement abstraction on top of the
 * raw helicopter control. The abstraction provides a mechanism to move to an
 * arbitrary point in 3D space, reorient in place, and hover in place.
 *
 * @author bkate
 * @author kar
 */
public abstract class BaseAutoHeliBehavior implements HeliBehavior {
  protected SimEngine simEngine;

  private Timer controlTimer;

  private Vector3f lastPos = new Vector3f();

  private Quat4f lastPose = new Quat4f();

  private long lastTime = 0;

  private MoveState currState = MoveState.IDLE;

  MoveState prevState = MoveState.IDLE;

  private Vector3f currTarget = new Vector3f();

  private double currEpsilon = 0.1;

  private MoveCallback currMoveCallback;

  private List<AbstractHeli> allHelis;

  private Vector3f calcTarget;

  private Vector3f rVec;

  private int myHeliId;

  private Vector3f landingSpot;

  private Vector3f hiveLocation;

  private double hiveRadius = 0.55;

  private BufferedWriter logWriter;

  private HeliControl control;

  private Platform platform;

  private PositionSensor posSensor;

  private PoseSensor orientSensor;

  private MedianPIDController throttlePID;

  private MedianPIDController pitchPID;

  private MedianPIDController rollPID;

  private MedianPIDController yawPID;

  private double[] yawDiffs;

  private int yawHistPtr;

  private double yawSetpoint = 0;

  private double currYaw = 0, prevYaw = 0, idYaw = 0, fHeading = 0;

  private boolean goodOrientation = false;

  private boolean logData = true;

  private String logPath = "./heli_log.txt";

  public enum MoveState {
    IDLE,
    HOVER,
    RUN,
    MOVE,
    LAND
  }

  private static Logger logger = Logger.getLogger(BaseAutoHeliBehavior.class);

  private static final long CONTROL_LOOP_PERIOD = 10;

  private static final float COLLISION_BUFF = 1.0f;

  private static final float COLLISION_BUFF_HIVE = 0.4f;

  private static final float BOUNDARY_BUFF = 0.5f;

  private static final float DESTINATION_EPSILON = 0.3f;

  private static final float SLOWDOWN_DISTANCE = 0.8f;

  private static final float FLYING_ALTITUDE = 0.1f;

  private static final float LANDING_EPSILON = 0.3f;

  private static final float LANDING_ALTITUDE = 0.1f;

  private static final float LANDING_STAGING_ALTITUDE = 0.5f;

  private static final long LANDING_STAGING_TIME = 1;

  @Override public void start(final Platform platform, final HeliControl control, final Boundary bounds) {
    this.platform = platform;
    this.control = control;
    if (logData) {
      try {
        logWriter = new BufferedWriter(new FileWriter(logPath));
      } catch (IOException e) {
        throw new RuntimeException("Could not open log output file: " + logPath, e);
      }
    }
    allHelis = simEngine.findModelsByType(AbstractHeli.class);
    myHeliId = control.getHeliId();
    SimpleHive hive = simEngine.findModelByType(SimpleHive.class);
    if (hive == null) {
      throw new RuntimeException("A hive must be present and initialized before any helicopters.");
    }
    hiveLocation = hive.getTruthPosition();
    hiveRadius = hive.getSize() / 2;
    landingSpot = calcHiveLocation();
    posSensor = platform.getSensor("position-sensor", PositionSensor.class);
    orientSensor = platform.getSensor("pose-sensor", PoseSensor.class);
    if (posSensor == null) {
      throw new RuntimeModelingException("A position sensor is needed for BaseAutoHeliBehavior.");
    }
    if (orientSensor == null) {
      throw new RuntimeModelingException("A pose sensor is needed for the BaseAutoHeliBehavior.");
    }
    throttlePID = new MedianPIDController(1.0, 0.4, 0.2, 0.2);
    pitchPID = new MedianPIDController(0.0, 0.1, 0.1, 0.3, 0.1);
    rollPID = new MedianPIDController(0.0, 0.1, 0.1, 0.3, 0.1);
    yawPID = new MedianPIDController(0.0, 0.2, 0.0, 0.0, 0.1);
    control.setThrust(control.getThrustTrim());
    control.setPitch(control.getPitchTrim());
    control.setRoll(control.getRollTrim());
    control.setYaw(control.getYawTrim());
    control.sendCommands();
    yawDiffs = new double[3];
    yawHistPtr = 0;
    controlTimer = new Timer();
    final long firstTime = System.currentTimeMillis();
    controlTimer.scheduleAtFixedRate(new TimerTask() {
      @Override public void run() {
        Vector3f pos = posSensor.getPosition();
        Quat4f pose = orientSensor.getPose();
        Vector3f euler = MathUtil.quaternionToEulerZYX(pose);
        goodOrientation = ((Math.abs(euler.z) > 0.001) && ((Math.PI - Math.abs(euler.z)) > 0.001));
        while (!goodOrientation) {
          pose = orientSensor.getPose();
          euler = MathUtil.quaternionToEulerZYX(pose);
          goodOrientation = ((Math.abs(euler.z) > 0.001) && ((Math.PI - Math.abs(euler.z)) > 0.001));
          System.out.println("Dropping orientation!");
        }
        long realTime = System.currentTimeMillis();
        if ((realTime - lastTime) == 0) {
          return;
        }
        logModelData(currState, lastPos, pos, lastPose, pose, (realTime - firstTime) / 1000.0f, (realTime - lastTime) / 1000.0f);
        lastPos = pos;
        lastPose = pose;
        lastTime = realTime;
        if (currState != prevState) {
          logger.info("new state: " + currState);
          prevState = currState;
        }
        fHeading = filterHeading(euler.z);
        double targetWX = currTarget.getX() - pos.getX();
        double targetWY = currTarget.getY() - pos.getY();
        double targetBX = 1 * (targetWX * Math.cos(-fHeading) + targetWY * -Math.sin(-fHeading));
        double targetBY = 1 * (targetWX * Math.sin(-fHeading) + targetWY * Math.cos(-fHeading));
        showState();

<<<<<<< /usr/src/app/output/robobees/simbeeotic/d8cc9db915bb7f2fd6a528df8c173d1c08407ec4/simbeeotic-testbed/src/main/java/harvard/robobees/simbeeotic/model/BaseAutoHeliBehavior.java/left.java
        long time = System.nanoTime();
=======
>>>>>>> Unknown file: This is a bug in JDime.

        switch (currState) {
          case IDLE:
          if (control.getThrust() > 0.0) {
            control.setThrust(0.0);
          }
          break;
          case LAND:
          control.setThrust(control.getThrustTrim() - 0.1);
          if (pos.z < LANDING_ALTITUDE) {
            currState = MoveState.IDLE;
            if (currMoveCallback != null) {
              currMoveCallback.reachedDestination();
              currMoveCallback = null;
            }
          } else {
            updateYaw(time, fHeading);
            updatePitch(time, -targetBX);
            updateRoll(time, targetBY);
          }
          break;
          case RUN:
          updateThrottle(time, pos.z);

<<<<<<< /usr/src/app/output/robobees/simbeeotic/d8cc9db915bb7f2fd6a528df8c173d1c08407ec4/simbeeotic-testbed/src/main/java/harvard/robobees/simbeeotic/model/BaseAutoHeliBehavior.java/left.java
          updateYaw(time, fHeading);
=======
          if (pos.z < (TAKEOFF_ALTITUDE - 0.2)) {
            currState = MoveState.IDLE;
            stop();
          }
>>>>>>> /usr/src/app/output/robobees/simbeeotic/d8cc9db915bb7f2fd6a528df8c173d1c08407ec4/simbeeotic-testbed/src/main/java/harvard/robobees/simbeeotic/model/BaseAutoHeliBehavior.java/right.java

          if (dist <= currEpsilon) {
            hover();
            if (currMoveCallback != null) {
              tmp = currMoveCallback;
              currMoveCallback.reachedDestination();
            }
            if ((tmp != null) && (currMoveCallback != null) && (tmp.equals(currMoveCallback))) {
              currMoveCallback = null;
            }
            break;
          }
          double pitchSetPoint = 0.0;
          control.setPitch(control.getPitchTrim() + pitchSetPoint);
          updateRoll(time, targetBY);
          break;
          case MOVE:
          updateThrottle(time, pos.z);
          updateYaw(time, fHeading);
          updatePitch(time, -targetBX);
          updateRoll(time, targetBY);
          break;
          case HOVER:
          updateThrottle(time, pos.z);
          updateYaw(time, fHeading);
          updatePitch(time, -targetBX);

<<<<<<< /usr/src/app/output/robobees/simbeeotic/d8cc9db915bb7f2fd6a528df8c173d1c08407ec4/simbeeotic-testbed/src/main/java/harvard/robobees/simbeeotic/model/BaseAutoHeliBehavior.java/left.java
          updateRoll(time, targetBY);
=======
>>>>>>> Unknown file: This is a bug in JDime.

          break;
          default:
        }
        control.sendCommands();
      }
    }, 0, 10);
  }

  @Override public void stop() {
    control.setThrust(0.0);
    if (logData) {
      try {
        logWriter.close();
      } catch (IOException e) {
      }
    }
    controlTimer.cancel();
  }

  /**
     * Moves the helicopter to a point in space.
     *
     * @param x The coordinate in the global X axis (m).
     * @param y The coordinate in the global Y axis (m).
     * @param z The coordinate in the global Z axis (m).
     * @param epsilon The radius around the desired point that is considered acceptable (m).
     */
  protected void moveToPoint(double x, double y, double z, double epsilon) {
    moveToPoint(x, y, z, epsilon, null);
  }

  /**
     * Moves the helicopter to a point in space.
     *
     * @param x The coordinate in the global X axis (m).
     * @param y The coordinate in the global Y axis (m).
     * @param z The coordinate in the global Z axis (m).
     * @param epsilon The radius around the desired point that is considered acceptable (m).
     * @param callback An optional callback to be executed once the helicopter reaches the specified point.
     */
  protected void moveToPoint(double x, double y, double z, double epsilon, MoveCallback callback) {
    if (posSensor.getPosition().getZ() < LANDING_ALTITUDE) {
      currState = MoveState.TAKEOFF;
    } else {
      currState = MoveState.MOVE;
    }
    currTarget = new Vector3f((float) x, (float) y, (float) z);
    currEpsilon = epsilon;
    currMoveCallback = callback;
    throttlePID.setSetpoint(z);
  }

  /**
     * A convenience method for taking off.
     *
     * @param z The altitude to reach after takeoff.
     *
     */
  protected void takeoff(double z) {
    control.setRoll(control.getRollTrim());
    control.setPitch(control.getPitchTrim());
    control.setYaw(control.getYawTrim());
    Vector3f pos = posSensor.getPosition();
    moveToPoint(pos.x, pos.y, z, DESTINATION_EPSILON * 2);
  }

  /**
     * A convenience method for taking off.
     *
     * @param z The altitude to reach after takeoff.
     * @param callback The clalback to invoke when the altitude is reached.
     */
  protected void takeoff(double z, MoveCallback callback) {
    control.setRoll(control.getRollTrim());
    control.setPitch(control.getPitchTrim());
    control.setYaw(control.getYawTrim());
    Vector3f pos = posSensor.getPosition();
    moveToPoint(pos.x, pos.y, z, DESTINATION_EPSILON * 2, callback);
  }

  /**
     * Lands the helicopter at the current position.
     */
  protected void land() {
    currTarget = posSensor.getPosition();
    yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
    currState = MoveState.LAND;
  }

  /**
     * Lands the helicopter at the current position.
     */
  protected void landAtPoint(Vector3f target) {
    target.z = posSensor.getPosition().z;
    currTarget = target;
    yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
    currState = MoveState.LAND;
  }

  /**
     * Lands the helicopter at the current position.
     */
  protected void landAtPoint(Vector3f target, MoveCallback callback) {
    currMoveCallback = callback;
    landAtPoint(target);
  }

  /**
     * Lands the helicopter at the hive.
     */
  protected void landAtHive() {
    moveToPoint(landingSpot.x, landingSpot.y, landingSpot.z, LANDING_EPSILON, new MoveCallback() {
      @Override public void reachedDestination() {
        hoverAtPoint(landingSpot);
        java.util.Timer landTimer = new java.util.Timer();
        landTimer.scheduleAtFixedRate(new TimerTask() {
          @Override public void run() {
            landAtPoint(landingSpot);
          }
        }, 0, 10);
      }
    });
  }

  /**
     * Lands the helicopter at the hive and informs the caller when the maneuver is complete.
     *
     * @param callback The callback to be invoked when the helicopter has landed.
     */
  protected void landAtHive(final MoveCallback callback) {
    moveToPoint(landingSpot.x, landingSpot.y, landingSpot.z, LANDING_EPSILON, new MoveCallback() {
      @Override public void reachedDestination() {
        hoverAtPoint(landingSpot);
        java.util.Timer landTimer = new java.util.Timer();
        landTimer.scheduleAtFixedRate(new TimerTask() {
          @Override public void run() {
            landAtPoint(landingSpot);
          }
        }, 0, 10);
      }
    });
  }

  /**
     * Turns the helicopter counter-clockwise about the body Z axis (yaw).
     *
     * @param angle The angle to turn (in radians).
     */
  protected void turn(double angle) {
    yawSetpoint += angle;
  }

  /**
     * Indicates that the helicopter should hover at the current altitude setpoint.
     */
  protected void hover() {
    currTarget = posSensor.getPosition();
    yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
    throttlePID.setSetpoint(currTarget.z);
    currState = MoveState.HOVER;
  }

  /**
     * Indicates that the helicopter should hover at the current altitude setpoint.
     */
  protected void hoverAtPoint(Vector3f target) {
    currTarget = target;
    yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
    throttlePID.setSetpoint(currTarget.z);
    currState = MoveState.HOVER;
  }

  /**
     * Indicates that the helicopter should hover at the given altitude.
     *
     * @param altitude The hover altitude (m).
     */
  protected void hover(double altitude) {
    hover();
    currTarget.z = (float) altitude;
    yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
    throttlePID.setSetpoint(altitude);
  }

  /**
     * Indicates that the helicopter should hover about a given target point.
     *
     * @param target The point at which the heli should hover.
     */
  protected void hover(Vector3f target) {
    hover();
    currTarget = target;
    yawSetpoint = MathUtil.quaternionToEulerZYX(orientSensor.getPose()).z;
    throttlePID.setSetpoint(target.z);
  }

  /**
     * Indicates that the helicopter should land and idle until given another command.
     */
  protected void idle() {
    currState = MoveState.IDLE;
    control.setThrust(0.0);
  }

  private void updateThrottle(long time, double alt) {
    Double throttleDelta = throttlePID.update(time, alt);
    if (throttleDelta == null) {
      throttleDelta = 0.0;
    }
    control.setThrust(control.getThrustTrim() + throttleDelta);
  }

  private void updatePitch(long time, double xDisp) {
    Double pitchDelta = pitchPID.update(time, xDisp);
    if (pitchDelta == null) {
      pitchDelta = 0.0;
    }
    control.setPitch(control.getPitchTrim() + pitchDelta);
  }

  private void updateRoll(long time, double yDisp) {
    Double rollDelta = rollPID.update(time, yDisp);
    if (rollDelta == null) {
      rollDelta = 0.0;
    }
    control.setRoll(control.getRollTrim() + rollDelta);
  }

  private double filterHeading(double heading) {
    if (goodOrientation) {
      prevYaw = currYaw;
      currYaw = heading;
      double yawDiff = currYaw - prevYaw;
      if (yawDiff >= Math.PI) {
        yawDiff -= 2.0 * Math.PI;
      } else {
        if (yawDiff < -Math.PI) {
          yawDiff += 2.0 * Math.PI;
        }
      }
      if (yawDiff > 2.0) {
        yawDiff = 2.0;
      }
      if (yawDiff < -2.0) {
        yawDiff = -2.0;
      }
      yawDiffs[yawHistPtr] = yawDiff;
      yawHistPtr = (yawHistPtr + 1) % 3;
    }
    double mn = yawDiffs[0], md = yawDiffs[1], mx = yawDiffs[2];
    if (mn > md) {
      md = mn;
      mn = yawDiffs[1];
    }
    if (md > mx) {
      mx = md;
      md = yawDiffs[2];
    }
    if (mn > md) {
      double tmp = md;
      md = mn;
      mn = tmp;
    }
    double dYaw = md;
    idYaw += dYaw;
    if (goodOrientation) {
      double yawErr = currYaw - idYaw;
      if (yawErr >= Math.PI) {
        yawErr -= 2.0 * Math.PI;
      } else {
        if (yawErr < -Math.PI) {
          yawErr += 2.0 * Math.PI;
        }
      }
      double yawCorrection = 0.05 * yawErr;
      if (yawCorrection > 0.02) {
        yawCorrection = 0.02;
      }
      if (yawCorrection < -0.02) {
        yawCorrection = -0.02;
      }
      idYaw += yawCorrection;
    }
    if (idYaw >= Math.PI) {
      idYaw -= 2 * Math.PI;
    } else {
      if (idYaw < -Math.PI) {
        idYaw += 2 * Math.PI;
      }
    }
    return idYaw;
  }

  private void updateYaw(long time, double heading) {
    double yawDiff = yawSetpoint - idYaw;
    if (currState == MoveState.MOVE) {
      yawSetpoint = Math.atan2((calcTarget.y - pos.y), (calcTarget.x - pos.x));
    } else {
      if (currState == MoveState.HOVER) {
        yawSetpoint = 0.0;
      }
    }
    if (yawDiff >= Math.PI) {
      yawDiff -= 2 * Math.PI;
    } else {
      if (yawDiff < -Math.PI) {
        yawDiff += 2 * Math.PI;
      }
    }
    Double yawDelta = yawPID.update(time, yawDiff);
    if (yawDelta == null) {
      yawDelta = 0.0;
    }
    control.setYaw(control.getYawTrim() + yawDelta);
  }

  private void showState() {
    if (logger.isDebugEnabled()) {
      logger.debug("State: " + currState + " Pos: " + posSensor.getPosition() + " Target: " + currTarget + " Dist: " + getDistfromPosition3d(currTarget));
    }
  }

  private void logModelData(MoveState s, Vector3f pos1, Vector3f pos2, Quat4f pose1, Quat4f pose2, float time, float dt) {
    if (logData) {
      Vector3f vel = new Vector3f();
      vel.sub(pos2, pos1);
      vel.scale(1 / dt);
      Transform orient = new Transform();
      orient.setIdentity();
      orient.setRotation(pose2);
      Vector3f x = new Vector3f(1, 0, 0);
      Vector3f y = new Vector3f(0, 1, 0);
      Vector3f z = new Vector3f(0, 0, 1);
      orient.transform(x);
      orient.transform(y);
      orient.transform(z);
      double vel_x = vel.dot(x);
      double vel_y = vel.dot(y);
      double vel_z = vel.dot(z);
      Quat4f dQ = new Quat4f();
      dQ.mulInverse(pose2, pose1);
      Vector3f dEuler = MathUtil.quaternionToEulerZYX(dQ);
      dEuler.scale(1 / dt);
      Vector3f pose = MathUtil.quaternionToEulerZYX(pose2);
      Vector3f dEulerApprox = new Vector3f(dQ.x, dQ.y, dQ.z);
      dEulerApprox.scale(2 / dt);
      int thrust = AutoHeliBee.rawCommand(control.getThrust());
      int roll = AutoHeliBee.rawCommand(control.getRoll());
      int pitch = AutoHeliBee.rawCommand(control.getPitch());
      int yaw = AutoHeliBee.rawCommand(control.getYaw());
      try {
        logger.info(time + " " + dt + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + pose.getX() + " " + pose.getY() + " " + pose.getZ() + " " + thrust + " " + roll + " " + pitch + " " + yaw);
        logWriter.write(
<<<<<<< /usr/src/app/output/robobees/simbeeotic/d8cc9db915bb7f2fd6a528df8c173d1c08407ec4/simbeeotic-testbed/src/main/java/harvard/robobees/simbeeotic/model/BaseAutoHeliBehavior.java/left.java
        time + " " + dt + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + pose.getX() + " " + pose.getY() + " " + pose.getZ() + " " + thrust + " " + roll + " " + pitch + " " + yaw + " " + " " + throttlePID.getIErr() + " " + throttlePID.getDErr() + " " + rollPID.getIErr() + " " + rollPID.getDErr() + " " + pitchPID.getIErr() + " " + pitchPID.getDErr() + " " + yawPID.getIErr() + " " + yawPID.getDErr() + " " + fHeading
=======
        System.currentTimeMillis() + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + euler.getX() + " " + euler.getY() + " " + euler.getZ() + " " + control.getThrust() + " " + control.getYaw() + " " + control.getPitch() + " " + control.getRoll()
>>>>>>> /usr/src/app/output/robobees/simbeeotic/d8cc9db915bb7f2fd6a528df8c173d1c08407ec4/simbeeotic-testbed/src/main/java/harvard/robobees/simbeeotic/model/BaseAutoHeliBehavior.java/right.java
         + "\n");
        logger.debug(System.currentTimeMillis() + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + euler.getX() + " " + euler.getY() + " " + euler.getZ() + " " + control.getThrust() + " " + control.getYaw() + " " + control.getPitch() + " " + control.getRoll());
      } catch (IOException e) {
      }
    }
  }

  private Vector3f calcHiveLocation() {
    int numHelis = allHelis.size();
    Vector3f hive = null;
    if (numHelis < 1) {
      return null;
    } else {
      if (numHelis == 1) {
        hive = new Vector3f(hiveLocation.x, hiveLocation.y, LANDING_STAGING_ALTITUDE);
      } else {
        double angle = 0;
        float x, y;
        for (AbstractHeli h : allHelis) {
          if (h.getHeliId() == myHeliId) {
            x = hiveLocation.x + (float) (hiveRadius * Math.cos(angle));
            y = hiveLocation.y + (float) (hiveRadius * Math.sin(angle));
            hive = new Vector3f(x, y, LANDING_STAGING_ALTITUDE);
          } else {
            angle += 2 * Math.PI / numHelis;
          }
        }
      }
    }
    return hive;
  }

  private AbstractHeli findClosestHeli(List<AbstractHeli> helis, float threshold, float thresholdHive) {
    AbstractHeli closestHeli = null;
    Vector3f otherPos;
    float dist;
    float minDist = Float.MAX_VALUE;
    float thresh;
    for (AbstractHeli h : helis) {
      if (h.getHeliId() != myHeliId) {
        otherPos = h.getTruthPosition();
        dist = getDistfromPosition2d(otherPos);
        if (dist < minDist) {
          thresh = threshold;
          if (otherPos.z <= LANDING_ALTITUDE) {
            thresh = thresholdHive;
          }
          if (dist <= thresh) {
            closestHeli = h;
            minDist = dist;
          }
        }
      }
    }
    return closestHeli;
  }

  private float getDistfromPosition2d(Vector3f value) {
    Vector3f pos = posSensor.getPosition();
    Vector3f temp = new Vector3f(value);
    temp.sub(pos);
    return ((float) Math.sqrt(temp.x * temp.x + temp.y * temp.y));
  }

  private float getDistfromPosition3d(Vector3f value) {
    Vector3f pos = posSensor.getPosition();
    Vector3f temp = new Vector3f(value);
    temp.sub(pos);
    return (temp.length());
  }

  public MoveState getState() {
    return currState;
  }

  @Inject(optional = true) public final void setLogging(@Named(value = "logging") final boolean logData) {
    this.logData = logData;
  }

  @Inject(optional = true) public final void setLogPath(@Named(value = "log-path") final String logPath) {
    this.logPath = logPath;
  }

  @Inject public final void setSimEngine(@GlobalScope final SimEngine engine) {
    this.simEngine = engine;
  }

  protected static interface MoveCallback {
    public void reachedDestination();
  }
}