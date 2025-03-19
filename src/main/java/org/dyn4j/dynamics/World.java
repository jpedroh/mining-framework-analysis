package org.dyn4j.dynamics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.dyn4j.DataContainer;
import org.dyn4j.Listener;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.BoundsListener;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.BroadphaseItem;
import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.LinkPostProcessor;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphasePostProcessor;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactManager;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.dynamics.contact.WarmStartingContactManager;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Manages the logic of collision detection, resolution, and reporting.
 * <p>
 * Interfacing with dyn4j starts with this class.  Create a new instance of this class
 * and add bodies and joints.  Then call one of the update or step methods in your game
 * loop to move the physics engine forward in time.
 * <p>
 * Via the {@link #addListener(Listener)} method, a {@link World} instance can have multiple listeners for all the listener types.
 * Some listener types return a boolean to indicate continuing or allowing something, like {@link CollisionListener}.  If, for example,
 * there are multiple {@link CollisionListener}s and <b>any</b> one of them returns false for an event, the collision is skipped.  However,
 * all listeners will still be called no matter if the first returned false.
 * @author William Bittle
 * @version 3.3.0
 * @since 1.0.0
 */
public class World implements Shiftable, DataContainer {
  /** Earths gravity constant */
  public static final Vector2 EARTH_GRAVITY = new Vector2(0.0, -9.8);

  /** Zero gravity constant */
  public static final Vector2 ZERO_GRAVITY = new Vector2(0.0, 0.0);

  /** The world id */
  protected final UUID id = UUID.randomUUID();

  /** The dynamics settings for this world */
  protected Settings settings;

  /** The {@link Step} used by the dynamics calculations */
  protected Step step;

  /** The world gravity vector */
  protected Vector2 gravity;

  /** The world {@link Bounds} */
  protected Bounds bounds;

  /** The {@link BroadphaseDetector} */
  protected BroadphaseDetector<Body, BodyFixture> broadphaseDetector;

  /** The {@link BroadphaseFilter} for detection */
  protected BroadphaseFilter<Body, BodyFixture> detectBroadphaseFilter;

  /** The {@link NarrowphaseDetector} */
  protected NarrowphaseDetector narrowphaseDetector;

  /** The {@link NarrowphasePostProcessor} */
  protected NarrowphasePostProcessor narrowphasePostProcessor;

  /** The {@link ManifoldSolver} */
  protected ManifoldSolver manifoldSolver;

  /** The {@link TimeOfImpactDetector} */
  protected TimeOfImpactDetector timeOfImpactDetector;

  /** The {@link RaycastDetector} */
  protected RaycastDetector raycastDetector;

  /** The {@link ContactManager} */
  protected ContactManager contactManager;

  /** The {@link CoefficientMixer} */
  protected CoefficientMixer coefficientMixer;

  /** The {@link ContactConstraintSolver} */
  protected ContactConstraintSolver contactConstraintSolver;

  /** The {@link TimeOfImpactSolver} */
  protected TimeOfImpactSolver timeOfImpactSolver;

  /** The application data associated */
  protected Object userData;

  /** The list of listeners for this world */
  private final List<Listener> listeners;

  /** The {@link Body} list */
  private final List<Body> bodies;

  /** The {@link Joint} list */
  private final List<Joint> joints;

  /** The reusable island */
  private Island island;

  /** The accumulated time */
  private double time;

  /** Flag to find new contacts */
  private boolean updateRequired;

  /**
	 * Default constructor.
	 * <p>
	 * Builds a simulation {@link World} without bounds.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * <p>
	 * Uses the {@link Capacity#DEFAULT_CAPACITY} capacity object for initialization.
	 */
  public World() {
    this(Capacity.DEFAULT_CAPACITY, null);
  }

  /**
	 * Optional constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * <p>
	 * The initial capacity specifies the estimated number of bodies that the simulation
	 * will have at any one time.  This is used to size internal structures to improve
	 * performance.  The internal structures can grow past the initial capacity.
	 * @param initialCapacity the initial capacity settings
	 * @since 3.1.1
	 */
  public World(Capacity initialCapacity) {
    this(initialCapacity, null);
  }

  /**
	 * Optional constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * @param bounds the bounds of the {@link World}; can be null
	 */
  public World(Bounds bounds) {
    this(Capacity.DEFAULT_CAPACITY, bounds);
  }

  /**
	 * Full constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * <p>
	 * The initial capacity specifies the estimated number of bodies that the simulation
	 * will have at any one time.  This is used to size internal structures to improve
	 * performance.  The internal structures can grow past the initial capacity.
	 * @param initialCapacity the initial capacity settings
	 * @param bounds the bounds of the {@link World}; can be null
	 * @throws NullPointerException if initialCapacity is null
	 * @since 3.1.1
	 */
  public World(Capacity initialCapacity, Bounds bounds) {
    if (initialCapacity == null) {
      initialCapacity = new Capacity();
    }
    this.settings = new Settings();
    this.step = new Step(this.settings.getStepFrequency());
    this.gravity = World.EARTH_GRAVITY;
    this.bounds = bounds;
    this.broadphaseDetector = new DynamicAABBTree<Body, BodyFixture>(initialCapacity.getBodyCount());
    this.detectBroadphaseFilter = new DetectBroadphaseFilter();
    this.narrowphaseDetector = new Gjk();
    this.narrowphasePostProcessor = new LinkPostProcessor();
    this.manifoldSolver = new ClippingManifoldSolver();
    this.timeOfImpactDetector = new ConservativeAdvancement();
    this.raycastDetector = new Gjk();
    this.coefficientMixer = CoefficientMixer.DEFAULT_MIXER;
    this.contactManager = new WarmStartingContactManager(initialCapacity);
    this.contactConstraintSolver = new SequentialImpulses();
    this.timeOfImpactSolver = new TimeOfImpactSolver();
    this.bodies = new ArrayList<Body>(initialCapacity.getBodyCount());
    this.joints = new ArrayList<Joint>(initialCapacity.getJointCount());
    this.listeners = new ArrayList<Listener>(initialCapacity.getListenerCount());
    this.island = new Island(initialCapacity);
    this.time = 0.0;
    this.updateRequired = true;
  }

  /**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.  In addition, this method will
	 * return true if a step was performed.
	 * <p>
	 * This method performs, at maximum, one simulation step.  Any remaining time from 
	 * the previous call of this method is added to the given elapsed time to determine
	 * if a step needs to be performed.  If the given elapsed time is usually greater 
	 * than the step frequency, consider using the {@link #update(double, int)} method
	 * instead.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double, int)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @return boolean true if the {@link World} performed a simulation step
	 */
  public boolean update(double elapsedTime) {
    return this.update(elapsedTime, -1.0, 1);
  }

  /**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.
	 * <p>
	 * Unlike the {@link #update(double)} method, this method will perform more than one
	 * step based on the given elapsed time.  For example, if the given elapsed time + the
	 * remaining time from the last call of this method is 2 * step frequency, then 2 steps 
	 * will be performed.  Use the maximumSteps parameter to put an upper bound on the 
	 * number of steps performed.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @param maximumSteps the maximum number of steps to perform
	 * @return boolean true if the {@link World} performed at least one simulation step
	 * @since 3.1.10
	 */
  public boolean update(double elapsedTime, int maximumSteps) {
    return this.update(elapsedTime, -1.0, maximumSteps);
  }

  /**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.  In addition, this method will
	 * return true if a step was performed.
	 * <p>
	 * This method performs, at maximum, one simulation step.  Any remaining time from 
	 * the previous call of this method is added to the given elapsed time to determine
	 * if a step needs to be performed.  If the given elapsed time is usually greater 
	 * than the step frequency, consider using the {@link #update(double, int)} method
	 * instead.
	 * <p>
	 * The stepElapsedTime parameter provides a way for the {@link World} to continue to 
	 * update at the frequency defined in the {@link Settings} object, but advance the
	 * simulation by the given time.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @param stepElapsedTime the time, in seconds, that the simulation should be advanced
	 * @return boolean true if the {@link World} performed at least one simulation step
	 * @since 3.2.4
	 */
  public boolean update(double elapsedTime, double stepElapsedTime) {
    return this.update(elapsedTime, stepElapsedTime, 1);
  }

  /**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.
	 * <p>
	 * Unlike the {@link #update(double)} method, this method will perform more than one
	 * step based on the given elapsed time.  For example, if the given elapsed time + the
	 * remaining time from the last call of this method is 2 * step frequency, then 2 steps 
	 * will be performed.  Use the maximumSteps parameter to put an upper bound on the 
	 * number of steps performed.
	 * <p>
	 * The stepElapsedTime parameter provides a way for the {@link World} to continue to 
	 * update at the frequency defined in the {@link Settings} object, but advance the
	 * simulation by the given time.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @param stepElapsedTime the time, in seconds, that the simulation should be advanced for each step; if less than or equal to zero {@link Settings#getStepFrequency()} will be used
	 * @param maximumSteps the maximum number of steps to perform
	 * @return boolean true if the {@link World} performed at least one simulation step
	 * @since 3.2.4
	 */
  public boolean update(double elapsedTime, double stepElapsedTime, int maximumSteps) {
    if (elapsedTime < 0.0) {
      elapsedTime = 0.0;
    }
    this.time += elapsedTime;
    double invhz = this.settings.getStepFrequency();
    int steps = 0;
    while (this.time >= invhz && steps < maximumSteps) {
      this.step.update(stepElapsedTime <= 0 ? invhz : stepElapsedTime);
      this.time = this.time - invhz;
      this.step();
      steps++;
    }
    return steps > 0;
  }

  /**
	 * Updates the {@link World}.
	 * <p>
	 * This method will update the world on every call.  Unlike the {@link #update(double)}
	 * method, this method uses the given elapsed time and does not attempt to update the world
	 * on a set interval.
	 * <p>
	 * This method immediately returns if the given elapsedTime is less than or equal to
	 * zero.
	 * @see #update(double)
	 * @see #update(double, int)
	 * @param elapsedTime the elapsed time in seconds
	 */
  public void updatev(double elapsedTime) {
    if (elapsedTime <= 0.0) {
      return;
    }
    this.step.update(elapsedTime);
    this.step();
  }

  /**
	 * Performs the given number of simulation steps using the step frequency in {@link Settings}.
	 * <p>
	 * This method immediately returns if the given step count is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 */
  public void step(int steps) {
    double invhz = this.settings.getStepFrequency();
    this.step(steps, invhz);
  }

  /**
	 * Performs the given number of simulation steps using the given elapsed time for each step.
	 * <p>
	 * This method immediately returns if the given elapsedTime or step count is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 * @param elapsedTime the elapsed time for each step
	 */
  public void step(int steps, double elapsedTime) {
    if (steps <= 0) {
      return;
    }
    if (elapsedTime <= 0.0) {
      return;
    }
    for (int i = 0; i < steps; i++) {
      this.step.update(elapsedTime);
      this.step();
    }
  }

  /**
	 * Performs one time step of the {@link World} using the current {@link Step}.
	 * <p>
	 * This method advances the world by the elapsed time in the {@link Step} object
	 * and performs collision resolution and constraint solving.
	 * <p>
	 * This method will perform a collision detection sweep at the end to ensure that
	 * callers of the world have the latest collision information. If the {@link #isUpdateRequired()}
	 * method returns true, a collision detection sweep will be performed before doing
	 * collision resolution.  See the {@link #setUpdateRequired(boolean)} method for details
	 * on when this flag should be set.
	 * <p>
	 * Use the various listeners to listen for events during the execution of
	 * this method.
	 * <p>
	 * If possible use the {@link StepListener#postSolve(Step, World)} method to update any
	 * bodies or joints to increase performance.
	 * <p>
	 * Most {@link Listener}s do not allow modification of the world, bodies, joints, etc in
	 * there methods. It's recommended that any of modification be performed in a {@link StepListener}
	 * or after this method has returned.
	 */
  protected void step() {
    List<StepListener> stepListeners = this.getListeners(StepListener.class);
    List<ContactListener> contactListeners = this.getListeners(ContactListener.class);
    int sSize = stepListeners.size();
    for (int i = 0; i < sSize; i++) {
      StepListener sl = stepListeners.get(i);
      sl.begin(this.step, this);
    }
    if (this.updateRequired) {
      this.detect();
      for (int i = 0; i < sSize; i++) {
        StepListener sl = stepListeners.get(i);
        sl.updatePerformed(this.step, this);
      }
      this.updateRequired = false;
    }
    this.contactManager.preSolveNotify(contactListeners);
    ContinuousDetectionMode continuousDetectionMode = this.settings.getContinuousDetectionMode();
    int size = this.bodies.size();
    for (int i = 0; i < size; i++) {
      Body body = this.bodies.get(i);
      body.setOnIsland(false);
      if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
        body.transform0.set(body.getTransform());
      }
    }
    int jSize = this.joints.size();
    for (int i = 0; i < jSize; i++) {
      Constraint joint = this.joints.get(i);
      joint.setOnIsland(false);
    }
    Deque<Body> stack = new ArrayDeque<Body>(size);
    Joint joint;
    ContactConstraint contactConstraint;
    Constraint constraint;
    for (int i = 0; i < size; i++) {
      Body seed = this.bodies.get(i);
      if (seed.isOnIsland() || seed.isAsleep() || !seed.isActive() || seed.isStatic()) {
        continue;
      }
      Island island = this.island;
      island.clear();
      stack.clear();
      stack.push(seed);
      while (stack.size() > 0) {
        Body body = stack.pop();
        island.add(body);
        body.setOnIsland(true);
        body.setAsleep(false);
        if (body.isStatic()) {
          continue;
        }
        int ceSize = body.contacts.size();
        for (int j = 0; j < ceSize; j++) {
          ContactEdge contactEdge = body.contacts.get(j);
          constraint = contactConstraint = contactEdge.interaction;
          if (!contactConstraint.isEnabled() || contactConstraint.isSensor() || constraint.isOnIsland()) {
            continue;
          }
          Body other = contactEdge.other;
          island.add(contactConstraint);
          constraint.setOnIsland(true);
          if (!other.isOnIsland()) {
            stack.push(other);
            other.setOnIsland(true);
          }
        }
        int jeSize = body.joints.size();
        for (int j = 0; j < jeSize; j++) {
          JointEdge jointEdge = body.joints.get(j);
          constraint = joint = jointEdge.interaction;
          if (!joint.isActive() || constraint.isOnIsland()) {
            continue;
          }
          Body other = jointEdge.other;
          if (!other.isActive()) {
            continue;
          }
          island.add(joint);
          constraint.setOnIsland(true);
          if (!other.isOnIsland()) {
            stack.push(other);
            other.setOnIsland(true);
          }
        }
      }
      island.solve(this.contactConstraintSolver, this.gravity, this.step, this.settings);
      for (int j = 0; j < size; j++) {
        Body body = this.bodies.get(j);
        if (body.isStatic()) {
          body.setOnIsland(false);
        }
      }
    }
    stack.clear();
    this.island.clear();
    this.contactManager.postSolveNotify(contactListeners);
    if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
      this.solveTOI(continuousDetectionMode);
    }
    for (int i = 0; i < sSize; i++) {
      StepListener sl = stepListeners.get(i);
      sl.postSolve(this.step, this);
    }
    this.detect();
    this.updateRequired = false;
    for (int i = 0; i < sSize; i++) {
      StepListener sl = stepListeners.get(i);
      sl.end(this.step, this);
    }
  }

  /**
	 * Finds new contacts for all bodies in this world.
	 * <p>
	 * This method performs the following:
	 * <ol>
	 * 	<li>Checks for out of bound bodies</li>
	 * 	<li>Updates the broad-phase using the current body positions</li>
	 * 	<li>Performs broad-phase collision detection</li>
	 * 	<li>Performs narrow-phase collision detection</li>
	 * 	<li>Performs manifold solving</li>
	 * 	<li>Adds contacts to the contact manager</li>
	 * 	<li>Warm starts the contacts</li>
	 * </ol>
	 * <p>
	 * This method will notify all bounds and collision listeners.  If any {@link CollisionListener}
	 * returns false, the collision is ignored.
	 * <p>
	 * This method also notifies any {@link ContactListener}s.
	 * @since 3.0.0
	 */
  protected void detect() {
    List<BoundsListener> boundsListeners = this.getListeners(BoundsListener.class);
    List<CollisionListener> collisionListeners = this.getListeners(CollisionListener.class);
    int size = this.bodies.size();
    int blSize = boundsListeners.size();
    int clSize = collisionListeners.size();
    for (int i = 0; i < size; i++) {
      Body body = this.bodies.get(i);
      if (!body.isActive()) {
        continue;
      }
      body.contacts.clear();
      if (this.bounds != null && this.bounds.isOutside(body)) {
        body.setActive(false);
        for (int j = 0; j < blSize; j++) {
          BoundsListener bl = boundsListeners.get(j);
          bl.outside(body);
        }
      }
      this.broadphaseDetector.update(body);
    }
    if (size > 0) {
      List<BroadphasePair<Body, BodyFixture>> pairs = this.broadphaseDetector.detect(this.detectBroadphaseFilter);
      int pSize = pairs.size();
      boolean allow = true;
      for (int i = 0; i < pSize; i++) {
        BroadphasePair<Body, BodyFixture> pair = pairs.get(i);
        Body body1 = pair.getCollidable1();
        Body body2 = pair.getCollidable2();
        BodyFixture fixture1 = pair.getFixture1();
        BodyFixture fixture2 = pair.getFixture2();
        allow = true;
        for (int j = 0; j < clSize; j++) {
          CollisionListener cl = collisionListeners.get(j);
          if (!cl.collision(body1, fixture1, body2, fixture2)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        Transform transform1 = body1.getTransform();
        Transform transform2 = body2.getTransform();
        Convex convex2 = fixture2.getShape();
        Convex convex1 = fixture1.getShape();
        Penetration penetration = new Penetration();
        if (this.narrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration)) {
          if (penetration.getDepth() == 0.0) {
            continue;
          }
          if (this.narrowphasePostProcessor != null) {
            this.narrowphasePostProcessor.process(convex1, transform1, convex2, transform2, penetration);
          }
          allow = true;
          for (int j = 0; j < clSize; j++) {
            CollisionListener cl = collisionListeners.get(j);
            if (!cl.collision(body1, fixture1, body2, fixture2, penetration)) {
              allow = false;
            }
          }
          if (!allow) {
            continue;
          }
          Manifold manifold = new Manifold();
          if (this.manifoldSolver.getManifold(penetration, convex1, transform1, convex2, transform2, manifold)) {
            if (manifold.getPoints().size() == 0) {
              continue;
            }
            allow = true;
            for (int j = 0; j < clSize; j++) {
              CollisionListener cl = collisionListeners.get(j);
              if (!cl.collision(body1, fixture1, body2, fixture2, manifold)) {
                allow = false;
              }
            }
            if (!allow) {
              continue;
            }
            ContactConstraint contactConstraint = new ContactConstraint(body1, fixture1, body2, fixture2, manifold, this.coefficientMixer.mixFriction(fixture1.getFriction(), fixture2.getFriction()), this.coefficientMixer.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution()));
            allow = true;
            for (int j = 0; j < clSize; j++) {
              CollisionListener cl = collisionListeners.get(j);
              if (!cl.collision(contactConstraint)) {
                allow = false;
              }
            }
            if (!allow) {
              continue;
            }
            ContactEdge contactEdge1 = new ContactEdge(body2, contactConstraint);
            ContactEdge contactEdge2 = new ContactEdge(body1, contactConstraint);
            body1.contacts.add(contactEdge1);
            body2.contacts.add(contactEdge2);
            this.contactManager.queue(contactConstraint);
          }
        }
      }
    }
    this.contactManager.updateAndNotify(this.getListeners(ContactListener.class), this.settings);
  }

  /**
	 * Solves the time of impact for all the {@link Body}s in this {@link World}.
	 * <p>
	 * This method solves for the time of impact for each {@link Body} iteratively
	 * and pairwise.
	 * <p>
	 * The cases considered are dependent on the given collision detection mode.
	 * <p>
	 * Cases skipped (including the converse of the above):
	 * <ul>
	 * <li>Inactive, asleep, or non-moving bodies</li>
	 * <li>Bodies connected via a joint with the collision flag set to false</li>
	 * <li>Bodies already in contact</li>
	 * <li>Fixtures whose filters return false</li>
	 * <li>Sensor fixtures</li>
	 * </ul>
	 * @param mode the continuous collision detection mode
	 * @see ContinuousDetectionMode
	 * @since 1.2.0
	 */
  protected void solveTOI(ContinuousDetectionMode mode) {
    List<TimeOfImpactListener> listeners = this.getListeners(TimeOfImpactListener.class);
    int size = this.bodies.size();
    boolean bulletsOnly = (mode == ContinuousDetectionMode.BULLETS_ONLY);
    for (int i = 0; i < size; i++) {
      Body body = this.bodies.get(i);
      if (bulletsOnly && !body.isBullet()) {
        continue;
      }
      if (body.mass.isInfinite()) {
        continue;
      }
      if (!body.isOnIsland() || body.isAsleep()) {
        continue;
      }
      this.solveTOI(body, listeners);
    }
  }

  /**
	 * Solves the time of impact for the given {@link Body}.
	 * <p>
	 * This method will find the first {@link Body} that the given {@link Body}
	 * collides with unless ignored via the {@link TimeOfImpactListener}.
	 * <p>
	 * If any {@link TimeOfImpactListener} doesn't allow the collision then the collision
	 * is ignored.
	 * <p>
	 * After the first {@link Body} is found the two {@link Body}s are interpolated
	 * to the time of impact.
	 * <p>
	 * Then the {@link Body}s are position solved using the {@link TimeOfImpactSolver}
	 * to force the {@link Body}s into collision.  This causes the discrete collision
	 * detector to detect the collision on the next time step.
	 * @param body1 the {@link Body}
	 * @param listeners the list of {@link TimeOfImpactListener}s
	 * @since 3.1.0
	 */
  protected void solveTOI(Body body1, List<TimeOfImpactListener> listeners) {
    int size = this.bodies.size();
    AABB aabb1 = body1.createSweptAABB();
    boolean bullet = body1.isBullet();
    double t1 = 0.0;
    double t2 = 1.0;
    TimeOfImpact minToi = null;
    Body minBody = null;
    for (int i = 0; i < size; i++) {
      Body body2 = this.bodies.get(i);
      if (body1 == body2) {
        continue;
      }
      if (!body2.isActive()) {
        continue;
      }
      if (body2.isDynamic() && !bullet) {
        continue;
      }
      if (body1.isConnected(body2, false)) {
        continue;
      }
      if (body1.isInContact(body2)) {
        continue;
      }
      AABB aabb2 = body2.createSweptAABB();
      if (!aabb1.overlaps(aabb2)) {
        continue;
      }
      TimeOfImpact toi = new TimeOfImpact();
      int fc1 = body1.getFixtureCount();
      int fc2 = body2.getFixtureCount();
      double dt = this.step.getDeltaTime();
      Vector2 v1 = body1.getLinearVelocity().product(dt);
      Vector2 v2 = body2.getLinearVelocity().product(dt);
      double av1 = body1.getAngularVelocity() * dt;
      double av2 = body2.getAngularVelocity() * dt;
      Transform tx1 = body1.getInitialTransform();
      Transform tx2 = body2.getInitialTransform();
      for (int j = 0; j < fc1; j++) {
        BodyFixture f1 = body1.getFixture(j);
        if (f1.isSensor()) {
          continue;
        }
        for (int k = 0; k < fc2; k++) {
          BodyFixture f2 = body2.getFixture(k);
          if (f2.isSensor()) {
            continue;
          }
          Filter filter1 = f1.getFilter();
          Filter filter2 = f2.getFilter();
          if (!filter1.isAllowed(filter2)) {
            continue;
          }
          Convex c1 = f1.getShape();
          Convex c2 = f2.getShape();
          if (this.timeOfImpactDetector.getTimeOfImpact(c1, tx1, v1, av1, c2, tx2, v2, av2, t1, t2, toi)) {
            double t = toi.getTime();
            if (t < t2) {
              boolean allow = true;
              for (TimeOfImpactListener tl : listeners) {
                if (!tl.collision(body1, f1, body2, f2, toi)) {
                  allow = false;
                }
              }
              if (allow) {
                t2 = t;
                minToi = toi;
                minBody = body2;
              }
            }
          }
        }
      }
    }
    if (minToi != null) {
      double t = minToi.getTime();
      body1.transform0.lerp(body1.getTransform(), t, body1.getTransform());
      if (minBody.isDynamic()) {
        minBody.transform0.lerp(minBody.getTransform(), t, minBody.getTransform());
      }
      this.timeOfImpactSolver.solve(body1, minBody, minToi, this.settings);
    }
  }

  /**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param start the start point
	 * @param end the end point
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the {@link Ray}
	 * @throws NullPointerException if start, end, or results is null
	 * @see #raycast(Ray, double, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
  public boolean raycast(Vector2 start, Vector2 end, boolean ignoreSensors, boolean all, List<RaycastResult> results) {
    return this.raycast(start, end, null, ignoreSensors, true, all, results);
  }

  /**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param start the start point
	 * @param end the end point
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the {@link Ray}
	 * @throws NullPointerException if start, end, or results is null
	 * @see #raycast(Ray, double, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
  public boolean raycast(Vector2 start, Vector2 end, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
    return this.raycast(start, end, null, ignoreSensors, ignoreInactive, all, results);
  }

  /**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param start the start point
	 * @param end the end point
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the {@link Ray}
	 * @throws NullPointerException if start, end, or results is null
	 * @see #raycast(Ray, double, Filter, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
  public boolean raycast(Vector2 start, Vector2 end, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
    Vector2 d = start.to(end);
    double maxLength = d.normalize();
    Ray ray = new Ray(start, d);
    return this.raycast(ray, maxLength, filter, ignoreSensors, ignoreInactive, all, results);
  }

  /**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the given {@link Ray}
	 * @throws NullPointerException if ray or results is null
	 * @see #raycast(Vector2, Vector2, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
  public boolean raycast(Ray ray, double maxLength, boolean ignoreSensors, boolean all, List<RaycastResult> results) {
    return this.raycast(ray, maxLength, null, ignoreSensors, true, all, results);
  }

  /**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the given {@link Ray}
	 * @throws NullPointerException if ray or results is null
	 * @see #raycast(Vector2, Vector2, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
  public boolean raycast(Ray ray, double maxLength, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
    return this.raycast(ray, maxLength, null, ignoreSensors, ignoreInactive, all, results);
  }

  /**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the given {@link Ray}
	 * @throws NullPointerException if ray or results is null
	 * @see #raycast(Vector2, Vector2, Filter, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
  public boolean raycast(Ray ray, double maxLength, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
    List<RaycastListener> listeners = this.getListeners(RaycastListener.class);
    int rlSize = listeners.size();
    double max = 0.0;
    if (maxLength > 0.0) {
      max = maxLength;
    }
    RaycastResult result = null;
    RaycastBroadphaseFilter bpFilter = new RaycastBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
    List<BroadphaseItem<Body, BodyFixture>> items = this.broadphaseDetector.raycast(ray, maxLength, bpFilter);
    int size = items.size();
    boolean found = false;
    boolean allow = true;
    for (int i = 0; i < size; i++) {
      BroadphaseItem<Body, BodyFixture> item = items.get(i);
      Body body = item.getCollidable();
      BodyFixture fixture = item.getFixture();
      Transform transform = body.getTransform();
      Raycast raycast = new Raycast();
      allow = true;
      for (int j = 0; j < rlSize; j++) {
        RaycastListener rl = listeners.get(j);
        if (!rl.allow(ray, body, fixture)) {
          allow = false;
        }
      }
      if (!allow) {
        continue;
      }
      Convex convex = fixture.getShape();
      if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
        allow = true;
        for (int j = 0; j < rlSize; j++) {
          RaycastListener rl = listeners.get(j);
          if (!rl.allow(ray, body, fixture, raycast)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        if (!all) {
          if (result == null) {
            result = new RaycastResult(body, fixture, raycast);
            results.add(result);
            found = true;
          } else {
            result.body = body;
            result.fixture = fixture;
            result.raycast = raycast;
          }
          max = result.raycast.getDistance();
        } else {
          results.add(new RaycastResult(body, fixture, raycast));
          found = true;
        }
      }
    }
    return found;
  }

  /**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body. 
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param start the start point
	 * @param end the end point
	 * @param body the {@link Body} to test
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if start, end, body, or result is null
	 * @see #raycast(Ray, Body, double, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
  public boolean raycast(Vector2 start, Vector2 end, Body body, boolean ignoreSensors, RaycastResult result) {
    return this.raycast(start, end, body, null, ignoreSensors, result);
  }

  /**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body. 
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param start the start point
	 * @param end the end point
	 * @param body the {@link Body} to test
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if start, end, body, or result is null
	 * @see #raycast(Ray, Body, double, Filter, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
  public boolean raycast(Vector2 start, Vector2 end, Body body, Filter filter, boolean ignoreSensors, RaycastResult result) {
    Vector2 d = start.to(end);
    double maxLength = d.normalize();
    Ray ray = new Ray(start, d);
    return this.raycast(ray, body, maxLength, filter, ignoreSensors, result);
  }

  /**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body.
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param ray the {@link Ray} to cast
	 * @param body the {@link Body} to test
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if ray, body, or result is null
	 * @see #raycast(Vector2, Vector2, Body, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
  public boolean raycast(Ray ray, Body body, double maxLength, boolean ignoreSensors, RaycastResult result) {
    return this.raycast(ray, body, maxLength, null, ignoreSensors, result);
  }

  /**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body.
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param ray the {@link Ray} to cast
	 * @param body the {@link Body} to test
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if ray, body, or result is null
	 * @see #raycast(Vector2, Vector2, Body, Filter, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
  public boolean raycast(Ray ray, Body body, double maxLength, Filter filter, boolean ignoreSensors, RaycastResult result) {
    List<RaycastListener> listeners = this.getListeners(RaycastListener.class);
    int rlSize = listeners.size();
    boolean allow = true;
    int size = body.getFixtureCount();
    Transform transform = body.getTransform();
    double max = 0.0;
    if (maxLength > 0.0) {
      max = maxLength;
    }
    Raycast raycast = new Raycast();
    boolean found = false;
    for (int i = 0; i < size; i++) {
      BodyFixture fixture = body.getFixture(i);
      if (ignoreSensors && fixture.isSensor()) {
        continue;
      }
      if (filter != null && !filter.isAllowed(fixture.getFilter())) {
        continue;
      }
      allow = true;
      for (int j = 0; j < rlSize; j++) {
        RaycastListener rl = listeners.get(j);
        if (!rl.allow(ray, body, fixture)) {
          allow = false;
        }
      }
      if (!allow) {
        continue;
      }
      Convex convex = fixture.getShape();
      if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
        allow = true;
        for (int j = 0; j < rlSize; j++) {
          RaycastListener rl = listeners.get(j);
          if (!rl.allow(ray, body, fixture, raycast)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        max = raycast.getDistance();
        result.fixture = fixture;
        found = true;
      }
    }
    if (found) {
      result.body = body;
      result.raycast = raycast;
    }
    return found;
  }

  /**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.5
	 * @see #convexCast(Convex, Transform, Vector2, double, boolean, boolean, boolean, List)
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, boolean ignoreSensors, boolean all, List<ConvexCastResult> results) {
    return this.convexCast(convex, transform, deltaPosition, 0.0, null, ignoreSensors, true, all, results);
  }

  /**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param ignoreInactive true if inactive bodies should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 * @see #convexCast(Convex, Transform, Vector2, double, boolean, boolean, boolean, List)
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<ConvexCastResult> results) {
    return this.convexCast(convex, transform, deltaPosition, 0.0, null, ignoreSensors, ignoreInactive, all, results);
  }

  /**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @see #convexCast(Convex, Transform, Vector2, double, boolean, boolean, boolean, List)
	 * @since 3.1.5
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, boolean ignoreSensors, boolean all, List<ConvexCastResult> results) {
    return this.convexCast(convex, transform, deltaPosition, deltaAngle, null, ignoreSensors, true, all, results);
  }

  /**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param ignoreInactive true if inactive bodies should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<ConvexCastResult> results) {
    return this.convexCast(convex, transform, deltaPosition, deltaAngle, null, ignoreSensors, ignoreInactive, all, results);
  }

  /**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param ignoreInactive true if inactive bodies should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<ConvexCastResult> results) {
    List<ConvexCastListener> listeners = this.getListeners(ConvexCastListener.class);
    int clSize = listeners.size();
    double radius = convex.getRadius();
    Vector2 startWorldCenter = transform.getTransformed(convex.getCenter());
    AABB startAABB = new AABB(startWorldCenter, radius);
    Transform finalTransform = transform.lerped(deltaPosition, deltaAngle, 1.0);
    Vector2 endWorldCenter = finalTransform.getTransformed(convex.getCenter());
    AABB endAABB = new AABB(endWorldCenter, radius);
    AABB aabb = startAABB.getUnion(endAABB);
    ConvexCastResult min = null;
    final Vector2 dp2 = new Vector2();
    double t2 = 1.0;
    boolean found = false;
    boolean allow = true;
    AABBBroadphaseFilter bpFilter = new AABBBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
    List<BroadphaseItem<Body, BodyFixture>> items = this.broadphaseDetector.detect(aabb, bpFilter);
    for (BroadphaseItem<Body, BodyFixture> item : items) {
      Body body = item.getCollidable();
      BodyFixture fixture = item.getFixture();
      double ft2 = t2;
      TimeOfImpact bodyMinToi = null;
      BodyFixture bodyMinFixture = null;
      Transform bodyTransform = body.getTransform();
      allow = true;
      for (int j = 0; j < clSize; j++) {
        ConvexCastListener ccl = listeners.get(j);
        if (!ccl.allow(convex, body, fixture)) {
          allow = false;
        }
      }
      if (!allow) {
        continue;
      }
      Convex c = fixture.getShape();
      TimeOfImpact timeOfImpact = new TimeOfImpact();
      if (this.timeOfImpactDetector.getTimeOfImpact(convex, transform, deltaPosition, deltaAngle, c, bodyTransform, dp2, 0.0, 0.0, ft2, timeOfImpact)) {
        allow = true;
        for (int j = 0; j < clSize; j++) {
          ConvexCastListener ccl = listeners.get(j);
          if (!ccl.allow(convex, body, fixture, timeOfImpact)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        if (bodyMinToi == null || timeOfImpact.getTime() < bodyMinToi.getTime()) {
          ft2 = timeOfImpact.getTime();
          bodyMinToi = timeOfImpact;
          bodyMinFixture = fixture;
        }
      }
      if (bodyMinToi != null) {
        if (!all) {
          t2 = bodyMinToi.getTime();
          if (min == null || bodyMinToi.getTime() < min.timeOfImpact.getTime()) {
            min = new ConvexCastResult(body, bodyMinFixture, bodyMinToi);
          }
        } else {
          ConvexCastResult result = new ConvexCastResult(body, fixture, timeOfImpact);
          results.add(result);
        }
        found = true;
      }
    }
    if (min != null) {
      results.add(min);
    }
    return found;
  }

  /**
	 * Performs a linear convex cast on the given body, placing a detected collision into the given result object.
	 * <p>
	 * This method does a static test of the body (in other words, does not take into account the body's linear
	 * or angular velocity, but rather assumes it is stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, the fixture that has the minimum time of impact will be the result.
	 * <p>
	 * Returns false if the given body and convex are in collision at the beginning of the cast.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param body the body to cast against
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param result the convex cast result
	 * @return boolean true if a collision was found
	 * @since 3.1.5
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, Body body, boolean ignoreSensors, ConvexCastResult result) {
    return this.convexCast(convex, transform, deltaPosition, 0, body, null, ignoreSensors, result);
  }

  /**
	 * Performs a linear convex cast on the given body, placing a detected collision into the given result object.
	 * <p>
	 * This method does a static test of the body (in other words, does not take into account the body's linear
	 * or angular velocity, but rather assumes it is stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, the fixture that has the minimum time of impact will be the result.
	 * <p>
	 * Returns false if the given body and convex are in collision at the beginning of the cast.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param body the body to cast against
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param result the convex cast result
	 * @return boolean true if a collision was found
	 * @since 3.1.5
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, Body body, boolean ignoreSensors, ConvexCastResult result) {
    return this.convexCast(convex, transform, deltaPosition, deltaAngle, body, null, ignoreSensors, result);
  }

  /**
	 * Performs a linear convex cast on the given body, placing a detected collision into the given result object.
	 * <p>
	 * This method does a static test of the body (in other words, does not take into account the body's linear
	 * or angular velocity, but rather assumes it is stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, the fixture that has the minimum time of impact will be the result.
	 * <p>
	 * Returns false if the given body and convex are in collision at the beginning of the cast.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param body the body to cast against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param result the convex cast result
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 */
  public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, Body body, Filter filter, boolean ignoreSensors, ConvexCastResult result) {
    List<ConvexCastListener> listeners = this.getListeners(ConvexCastListener.class);
    int clSize = listeners.size();
    boolean allow = true;
    boolean found = false;
    final Vector2 dp2 = new Vector2();
    double t2 = 1.0;
    int bSize = body.getFixtureCount();
    Transform bodyTransform = body.getTransform();
    for (int i = 0; i < bSize; i++) {
      BodyFixture bodyFixture = body.getFixture(i);
      if (ignoreSensors && bodyFixture.isSensor()) {
        continue;
      }
      if (filter != null && !filter.isAllowed(bodyFixture.getFilter())) {
        continue;
      }
      allow = true;
      for (int j = 0; j < clSize; j++) {
        ConvexCastListener ccl = listeners.get(j);
        if (!ccl.allow(convex, body, bodyFixture)) {
          allow = false;
        }
      }
      if (!allow) {
        return false;
      }
      Convex c = bodyFixture.getShape();
      TimeOfImpact toi = new TimeOfImpact();
      if (this.timeOfImpactDetector.getTimeOfImpact(convex, transform, deltaPosition, deltaAngle, c, bodyTransform, dp2, 0.0, 0.0, t2, toi)) {
        allow = true;
        for (int j = 0; j < clSize; j++) {
          ConvexCastListener ccl = listeners.get(j);
          if (!ccl.allow(convex, body, bodyFixture, toi)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        t2 = toi.getTime();
        result.fixture = bodyFixture;
        result.timeOfImpact = toi;
        result.body = body;
        found = true;
      }
    }
    return found;
  }

  /**
	 * Returns true if the given AABB overlaps a {@link Body} in this {@link World}.
	 * <p>
	 * If any part of a body is overlaping the AABB, the body is added to the list.
	 * <p>
	 * This performs a static collision test of the world using the {@link BroadphaseDetector}.
	 * <p>
	 * This may return bodies who only have sensor fixtures overlapping.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param aabb the world space {@link AABB}
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any body
	 * @since 3.1.9
	 */
  public boolean detect(AABB aabb, List<DetectResult> results) {
    return this.detect(aabb, null, false, true, results);
  }

  /**
	 * Returns true if the given AABB overlaps a {@link Body} {@link Fixture} in this {@link World}.
	 * <p>
	 * If any part of a body is overlaping the AABB, the body and that respective fixture is added 
	 * to the returned list.
	 * <p>
	 * This performs a static collision test of the world using the {@link BroadphaseDetector}.
	 * <p>
	 * This may return bodies who only have sensor fixtures overlapping.
	 * @param aabb the world space {@link AABB}
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any body
	 * @since 3.1.9
	 */
  public boolean detect(AABB aabb, boolean ignoreInactive, List<DetectResult> results) {
    return this.detect(aabb, null, false, ignoreInactive, results);
  }

  /**
	 * Returns true if the given AABB overlaps a {@link Body} in this {@link World}.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * @param aabb the world space {@link AABB}
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any fixture
	 * @since 3.1.9
	 */
  public boolean detect(AABB aabb, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
    return this.detect(aabb, null, ignoreSensors, ignoreInactive, results);
  }

  /**
	 * Returns true if the given AABB overlaps a {@link Body} in this {@link World}.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * @param aabb the world space {@link AABB}
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any fixture
	 * @since 3.1.9
	 */
  public boolean detect(AABB aabb, Filter filter, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
    List<DetectListener> listeners = this.getListeners(DetectListener.class);
    int dlSize = listeners.size();
    AABBBroadphaseFilter bpFilter = new AABBBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
    List<BroadphaseItem<Body, BodyFixture>> collisions = this.broadphaseDetector.detect(aabb, bpFilter);
    boolean found = false;
    int bSize = collisions.size();
    boolean allow;
    for (int i = 0; i < bSize; i++) {
      BroadphaseItem<Body, BodyFixture> item = collisions.get(i);
      Body body = item.getCollidable();
      BodyFixture fixture = item.getFixture();
      Transform transform = body.getTransform();
      allow = true;
      for (int j = 0; j < dlSize; j++) {
        DetectListener dl = listeners.get(j);
        if (!dl.allow(aabb, body, fixture)) {
          allow = false;
        }
      }
      if (!allow) {
        continue;
      }
      AABB faabb = fixture.getShape().createAABB(transform);
      if (aabb.overlaps(faabb)) {
        DetectResult result = new DetectResult(body, fixture);
        results.add(result);
        found = true;
      }
    }
    return found;
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The returned results may include sensor fixutres.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, boolean, List)
	 */
  public boolean detect(Convex convex, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, null, false, true, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, boolean, boolean, List)
	 */
  public boolean detect(Convex convex, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, null, ignoreSensors, true, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, List)
	 */
  public boolean detect(Convex convex, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, null, ignoreSensors, ignoreInactive, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, boolean, List)
	 */
  public boolean detect(Convex convex, Filter filter, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, filter, ignoreSensors, ignoreInactive, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information will have a performance impact.
	 * @param convex the convex shape in world coordinates
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean includeCollisionData, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, filter, ignoreSensors, ignoreInactive, includeCollisionData, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The returned results may include sensor fixutres.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, boolean, List)
	 */
  public boolean detect(Convex convex, Transform transform, List<DetectResult> results) {
    return this.detect(convex, transform, null, false, true, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, boolean, boolean, List)
	 */
  public boolean detect(Convex convex, Transform transform, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(convex, transform, null, ignoreSensors, true, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, List)
	 */
  public boolean detect(Convex convex, Transform transform, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
    return this.detect(convex, transform, null, ignoreSensors, ignoreInactive, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, boolean, List)
	 */
  public boolean detect(Convex convex, Transform transform, Filter filter, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
    return this.detect(convex, transform, filter, ignoreSensors, ignoreInactive, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information will have a performance impact.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Transform transform, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean includeCollisionData, List<DetectResult> results) {
    List<DetectListener> listeners = this.getListeners(DetectListener.class);
    int dlSize = listeners.size();
    boolean allow = true;
    AABB aabb = convex.createAABB(transform);
    AABBBroadphaseFilter bpFilter = new AABBBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
    List<BroadphaseItem<Body, BodyFixture>> items = this.broadphaseDetector.detect(aabb, bpFilter);
    int bSize = items.size();
    boolean found = false;
    for (int i = 0; i < bSize; i++) {
      BroadphaseItem<Body, BodyFixture> item = items.get(i);
      Body body = item.getCollidable();
      BodyFixture fixture = item.getFixture();
      Transform bt = body.getTransform();
      allow = true;
      for (int j = 0; j < dlSize; j++) {
        DetectListener dl = listeners.get(j);
        if (!dl.allow(convex, transform, body, fixture)) {
          allow = false;
        }
      }
      if (!allow) {
        continue;
      }
      Convex bc = fixture.getShape();
      boolean collision = false;
      Penetration penetration = (includeCollisionData ? new Penetration() : null);
      if (includeCollisionData) {
        collision = this.narrowphaseDetector.detect(convex, transform, bc, bt, penetration);
      } else {
        collision = this.narrowphaseDetector.detect(convex, transform, bc, bt);
      }
      if (collision) {
        DetectResult result = new DetectResult(body, fixture, penetration);
        results.add(result);
        found = true;
      }
    }
    return found;
  }

  /**
	 * Returns true if the given {@link AABB} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the AABB overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * @param aabb the {@link AABB} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(AABB aabb, Body body, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(aabb, body, null, ignoreSensors, results);
  }

  /**
	 * Returns true if the given {@link AABB} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the AABB overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * @param aabb the {@link AABB} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(AABB aabb, Body body, Filter filter, boolean ignoreSensors, List<DetectResult> results) {
    List<DetectListener> listeners = this.getListeners(DetectListener.class);
    int dlSize = listeners.size();
    boolean allow = true;
    boolean found = false;
    AABB baabb = this.broadphaseDetector.getAABB(body);
    if (baabb == null) {
      baabb = body.createAABB();
    }
    if (aabb.overlaps(baabb)) {
      Transform transform = body.getTransform();
      int fSize = body.getFixtureCount();
      for (int j = 0; j < fSize; j++) {
        BodyFixture fixture = body.getFixture(j);
        if (ignoreSensors && fixture.isSensor()) {
          continue;
        }
        if (filter != null && !filter.isAllowed(fixture.getFilter())) {
          continue;
        }
        allow = true;
        for (int k = 0; k < dlSize; k++) {
          DetectListener dl = listeners.get(k);
          if (!dl.allow(aabb, body, fixture)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        AABB faabb = fixture.getShape().createAABB(transform);
        if (aabb.overlaps(faabb)) {
          DetectResult result = new DetectResult(body, fixture);
          results.add(result);
          found = true;
        }
      }
    }
    return found;
  }

  /**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Body body, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, body, null, ignoreSensors, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Body body, Filter filter, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, body, filter, ignoreSensors, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information negatively impacts performance.
	 * @param convex the {@link Convex} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Body body, Filter filter, boolean ignoreSensors, boolean includeCollisionData, List<DetectResult> results) {
    return this.detect(convex, Transform.IDENTITY, body, filter, ignoreSensors, includeCollisionData, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in local coordinates
	 * @param transform the convex shape's world {@link Transform}
	 * @param body the {@link Body} to test against
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Transform transform, Body body, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(convex, transform, body, null, ignoreSensors, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in local coordinates
	 * @param transform the convex shape's world {@link Transform}
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Transform transform, Body body, Filter filter, boolean ignoreSensors, List<DetectResult> results) {
    return this.detect(convex, transform, body, filter, ignoreSensors, false, results);
  }

  /**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information negatively impacts performance.
	 * @param convex the {@link Convex} in local coordinates
	 * @param transform the convex shape's world {@link Transform}
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
  public boolean detect(Convex convex, Transform transform, Body body, Filter filter, boolean ignoreSensors, boolean includeCollisionData, List<DetectResult> results) {
    List<DetectListener> listeners = this.getListeners(DetectListener.class);
    int dlSize = listeners.size();
    boolean allow = true;
    for (int i = 0; i < dlSize; i++) {
      DetectListener dl = listeners.get(i);
      if (!dl.allow(convex, transform, body)) {
        allow = false;
      }
    }
    if (!allow) {
      return false;
    }
    AABB aabb = convex.createAABB(transform);
    AABB baabb = this.broadphaseDetector.getAABB(body);
    if (baabb == null) {
      baabb = body.createAABB();
    }
    boolean found = false;
    if (aabb.overlaps(baabb)) {
      Transform bt = body.getTransform();
      int fSize = body.getFixtureCount();
      for (int i = 0; i < fSize; i++) {
        BodyFixture fixture = body.getFixture(i);
        if (ignoreSensors && fixture.isSensor()) {
          continue;
        }
        Filter ff = fixture.getFilter();
        if (filter != null && !ff.isAllowed(filter)) {
          continue;
        }
        allow = true;
        for (int j = 0; j < dlSize; j++) {
          DetectListener dl = listeners.get(j);
          if (!dl.allow(convex, transform, body, fixture)) {
            allow = false;
          }
        }
        if (!allow) {
          continue;
        }
        Convex bc = fixture.getShape();
        boolean collision = false;
        Penetration penetration = (includeCollisionData ? new Penetration() : null);
        if (includeCollisionData) {
          collision = this.narrowphaseDetector.detect(convex, transform, bc, bt, penetration);
        } else {
          collision = this.narrowphaseDetector.detect(convex, transform, bc, bt);
        }
        if (collision) {
          DetectResult result = new DetectResult(body, fixture, penetration);
          results.add(result);
          found = true;
        }
      }
    }
    return found;
  }

  /**
	 * Shifts the coordinates of the entire world by the given amount.
	 * <pre>
	 * NewPosition = OldPosition + shift
	 * </pre>
	 * This method is useful in situations where the world is very large
	 * causing very large numbers to be used in the computations.  Shifting
	 * the coordinate system allows the computations to be localized and 
	 * retain accuracy.
	 * <p>
	 * This method modifies the coordinates of every body and joint in the world.
	 * <p>
	 * Adding joints or bodies after this method is called should consider that
	 * everything has been shifted.
	 * <p>
	 * This method does <b>NOT</b> require a call to {@link #setUpdateRequired(boolean)}.
	 * @param shift the distance to shift along the x and y axes
	 * @since 3.2.0
	 */
  public void shift(Vector2 shift) {
    int bSize = this.bodies.size();
    for (int i = 0; i < bSize; i++) {
      Body body = this.bodies.get(i);
      body.shift(shift);
    }
    int jSize = this.joints.size();
    for (int i = 0; i < jSize; i++) {
      Joint joint = this.joints.get(i);
      joint.shift(shift);
    }
    this.broadphaseDetector.shift(shift);
    if (this.bounds != null) {
      this.bounds.shift(shift);
    }
    this.contactManager.shift(shift);
  }

  /**
	 * Adds the given {@link Body} to the {@link World}.
	 * @param body the {@link Body} to add
	 * @throws NullPointerException if body is null
	 * @throws IllegalArgumentException if body has already been added to this world or if its a member of another world instance
	 * @since 3.1.1
	 */
  public void addBody(Body body) {
    if (body == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.addNullBody"));
    }
    if (body.world == this) {
      throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingBody"));
    }
    if (body.world != null) {
      throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldBody"));
    }
    this.bodies.add(body);
    body.world = this;
    this.broadphaseDetector.add(body);
  }

  /**
	 * Adds the given {@link Joint} to the {@link World}.
	 * @param joint the {@link Joint} to add
	 * @throws NullPointerException if joint is null
	 * @throws IllegalArgumentException if joint has already been added to this world or if its a member of another world instance
	 * @since 3.1.1
	 */
  public void addJoint(Joint joint) {
    if (joint == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.addNullJoint"));
    }
    Constraint constraint = joint;
    if (constraint.world == this) {
      throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingBody"));
    }
    if (constraint.world != null) {
      throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldBody"));
    }
    this.joints.add(joint);
    constraint.world = this;
    Body body1 = joint.getBody1();
    Body body2 = joint.getBody2();
    JointEdge jointEdge1 = new JointEdge(body2, joint);
    body1.joints.add(jointEdge1);
    JointEdge jointEdge2 = new JointEdge(body1, joint);
    body2.joints.add(jointEdge2);
  }

  /**
	 * Returns true if this world contains the given body.
	 * @param body the {@link Body} to test for
	 * @return boolean true if the body is contained in this world
	 * @since 3.1.1
	 */
  public boolean containsBody(Body body) {
    return this.bodies.contains(body);
  }

  /**
	 * Returns true if this world contains the given joint.
	 * @param joint the {@link Joint} to test for
	 * @return boolean true if the joint is contained in this world
	 * @since 3.1.1
	 */
  public boolean containsJoint(Joint joint) {
    return this.joints.contains(joint);
  }

  /**
	 * Removes the {@link Body} at the given index from this {@link World}.
	 * <p>
	 * Use the {@link #removeBody(int, boolean)} method to enable implicit
	 * destruction notification.
	 * @param index the index of the body to remove.
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
  public boolean removeBody(int index) {
    return removeBody(index, false);
  }

  /**
	 * Removes the {@link Body} at the given index from this {@link World}.
	 * <p>
	 * When a body is removed, joints and contacts may be implicitly destroyed.
	 * Pass true to the notify parameter to be notified of the destruction of these objects
	 * via the {@link DestructionListener}s.
	 * <p>
	 * This method does not trigger {@link ContactListener#end(ContactPoint)} events
	 * for the contacts that are being removed.
	 * @param index the index of the body to remove.
	 * @param notify true if implicit destruction should be notified
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
  public boolean removeBody(int index, boolean notify) {
    Body body = this.bodies.get(index);
    return removeBody(body, notify);
  }

  /**
	 * Removes the given {@link Body} from this {@link World}.
	 * <p>
	 * Use the {@link #removeBody(Body, boolean)} method to enable implicit
	 * destruction notification.
	 * @param body the {@link Body} to remove.
	 * @return boolean true if the body was removed
	 */
  public boolean removeBody(Body body) {
    return removeBody(body, false);
  }

  /**
	 * Removes the given {@link Body} from this {@link World}.
	 * <p>
	 * When a body is removed, joints and contacts may be implicitly destroyed.
	 * Pass true to the notify parameter to be notified of the destruction of these objects
	 * via the {@link DestructionListener}s.
	 * <p>
	 * This method does not trigger {@link ContactListener#end(ContactPoint)} events
	 * for the contacts that are being removed.
	 * @param body the {@link Body} to remove
	 * @param notify true if implicit destruction should be notified
	 * @return boolean true if the body was removed
	 * @since 3.1.1
	 */
  public boolean removeBody(Body body, boolean notify) {
    List<DestructionListener> listeners = null;
    if (notify) {
      listeners = this.getListeners(DestructionListener.class);
    }
    if (body == null) {
      return false;
    }
    boolean removed = this.bodies.remove(body);
    if (removed) {
      body.world = null;
      this.broadphaseDetector.remove(body);
      Iterator<JointEdge> aIterator = body.joints.iterator();
      while (aIterator.hasNext()) {
        JointEdge jointEdge = aIterator.next();
        aIterator.remove();
        Joint joint = jointEdge.interaction;
        Constraint constraint = joint;
        constraint.world = null;
        Body other = jointEdge.other;
        other.setAsleep(false);
        Iterator<JointEdge> bIterator = other.joints.iterator();
        while (bIterator.hasNext()) {
          JointEdge otherJointEdge = bIterator.next();
          Joint otherJoint = otherJointEdge.interaction;
          if (otherJoint == joint) {
            bIterator.remove();
            break;
          }
        }
        if (notify) {
          for (DestructionListener dl : listeners) {
            dl.destroyed(joint);
          }
        }
        this.joints.remove(joint);
      }
      Iterator<ContactEdge> acIterator = body.contacts.iterator();
      while (acIterator.hasNext()) {
        ContactEdge contactEdge = acIterator.next();
        acIterator.remove();
        ContactConstraint contactConstraint = contactEdge.interaction;
        Body other = contactEdge.other;
        other.setAsleep(false);
        Iterator<ContactEdge> iterator = other.contacts.iterator();
        while (iterator.hasNext()) {
          ContactEdge otherContactEdge = iterator.next();
          ContactConstraint otherContactConstraint = otherContactEdge.interaction;
          if (otherContactConstraint == contactConstraint) {
            iterator.remove();
            break;
          }
        }
        this.contactManager.end(contactConstraint);
        List<Contact> contacts = contactConstraint.getContacts();
        int size = contacts.size();
        for (int j = 0; j < size; j++) {
          Contact contact = contacts.get(j);
          ContactPoint contactPoint = new ContactPoint(contactConstraint, contact);
          if (notify) {
            for (DestructionListener dl : listeners) {
              dl.destroyed(contactPoint);
            }
          }
        }
      }
    }
    return removed;
  }

  /**
	 * Removes the {@link Joint} at the given index from this {@link World}.
	 * <p>
	 * No other objects are implicitly destroyed with joints are removed.
	 * @param index the index of the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 * @since 3.2.0
	 */
  public boolean removeJoint(int index) {
    Joint joint = this.joints.get(index);
    return removeJoint(joint);
  }

  /**
	 * Removes the given {@link Joint} from this {@link World}.
	 * <p>
	 * No other objects are implicitly destroyed with joints are removed.
	 * @param joint the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 */
  public boolean removeJoint(Joint joint) {
    if (joint == null) {
      return false;
    }
    boolean removed = this.joints.remove(joint);
    if (removed) {
      Constraint constraint = joint;
      constraint.world = null;
      Body body1 = joint.getBody1();
      Body body2 = joint.getBody2();
      Iterator<JointEdge> iterator = body1.joints.iterator();
      while (iterator.hasNext()) {
        JointEdge jointEdge = iterator.next();
        if (jointEdge.interaction == joint) {
          iterator.remove();
          break;
        }
      }
      iterator = body2.joints.iterator();
      while (iterator.hasNext()) {
        JointEdge jointEdge = iterator.next();
        if (jointEdge.interaction == joint) {
          iterator.remove();
          break;
        }
      }
      body1.setAsleep(false);
      body2.setAsleep(false);
    }
    return removed;
  }

  /**
	 * Removes all the joints and bodies from this world.
	 * <p>
	 * This method does <b>not</b> notify of destroyed objects.
	 * @see #removeAllBodiesAndJoints(boolean)
	 * @since 3.1.1
	 */
  public void removeAllBodiesAndJoints() {
    this.removeAllBodiesAndJoints(false);
  }

  /**
	 * Removes all the joints and bodies from this world.
	 * @param notify true if destruction of joints and contacts should be notified of by the {@link DestructionListener}
	 * @since 3.1.1
	 */
  public void removeAllBodiesAndJoints(boolean notify) {
    List<DestructionListener> listeners = null;
    if (notify) {
      listeners = this.getListeners(DestructionListener.class);
    }
    int bsize = this.bodies.size();
    for (int i = 0; i < bsize; i++) {
      Body body = this.bodies.get(i);
      body.joints.clear();
      if (notify) {
        Iterator<ContactEdge> aIterator = body.contacts.iterator();
        while (aIterator.hasNext()) {
          ContactEdge contactEdge = aIterator.next();
          Body other = contactEdge.other;
          ContactConstraint contactConstraint = contactEdge.interaction;
          Iterator<ContactEdge> bIterator = other.contacts.iterator();
          while (bIterator.hasNext()) {
            ContactEdge otherContactEdge = bIterator.next();
            ContactConstraint otherContactConstraint = otherContactEdge.interaction;
            if (otherContactConstraint == contactConstraint) {
              bIterator.remove();
              break;
            }
          }
          List<Contact> contacts = contactConstraint.getContacts();
          int csize = contacts.size();
          for (int j = 0; j < csize; j++) {
            Contact contact = contacts.get(j);
            ContactPoint contactPoint = new ContactPoint(contactConstraint, contact);
            for (DestructionListener dl : listeners) {
              dl.destroyed(contactPoint);
            }
          }
        }
        for (DestructionListener dl : listeners) {
          dl.destroyed(body);
        }
      }
      body.contacts.clear();
      body.world = null;
    }
    if (notify) {
      int jsize = this.joints.size();
      for (int i = 0; i < jsize; i++) {
        Joint joint = this.joints.get(i);
        Constraint constraint = joint;
        constraint.world = null;
        for (DestructionListener dl : listeners) {
          dl.destroyed(joint);
        }
      }
    }
    this.broadphaseDetector.clear();
    this.joints.clear();
    this.bodies.clear();
    this.contactManager.clear();
  }

  /**
	 * This is a convenience method for the {@link #removeAllBodiesAndJoints()} method since all joints will be removed
	 * when all bodies are removed anyway.
	 * <p>
	 * This method does not notify of the destroyed contacts, joints, etc.
	 * @see #removeAllBodies(boolean)
	 * @since 3.0.1
	 */
  public void removeAllBodies() {
    this.removeAllBodiesAndJoints(false);
  }

  /**
	 * This is a convenience method for the {@link #removeAllBodiesAndJoints(boolean)} method since all joints will be removed
	 * when all bodies are removed anyway.
	 * @param notify true if destruction of joints and contacts should be notified of by the {@link DestructionListener}
	 * @since 3.0.1
	 */
  public void removeAllBodies(boolean notify) {
    this.removeAllBodiesAndJoints(notify);
  }

  /**
	 * Removes all {@link Joint}s from this {@link World}.
	 * <p>
	 * This method does not notify of the joints removed.
	 * @see #removeAllJoints(boolean)
	 * @since 3.0.1
	 */
  public void removeAllJoints() {
    this.removeAllJoints(false);
  }

  /**
	 * Removes all {@link Joint}s from this {@link World}.
	 * @param notify true if destruction of joints should be notified of by the {@link DestructionListener}
	 * @since 3.0.1
	 */
  public void removeAllJoints(boolean notify) {
    List<DestructionListener> listeners = null;
    if (notify) {
      listeners = this.getListeners(DestructionListener.class);
    }
    int jSize = this.joints.size();
    for (int i = 0; i < jSize; i++) {
      Joint joint = this.joints.get(i);
      Constraint constraint = joint;
      constraint.world = null;
      Body body1 = joint.getBody1();
      Body body2 = joint.getBody2();
      Iterator<JointEdge> iterator = body1.joints.iterator();
      while (iterator.hasNext()) {
        JointEdge jointEdge = iterator.next();
        if (jointEdge.interaction == joint) {
          iterator.remove();
          break;
        }
      }
      iterator = body2.joints.iterator();
      while (iterator.hasNext()) {
        JointEdge jointEdge = iterator.next();
        if (jointEdge.interaction == joint) {
          iterator.remove();
          break;
        }
      }
      body1.setAsleep(false);
      body2.setAsleep(false);
      if (notify) {
        for (DestructionListener dl : listeners) {
          dl.destroyed(joint);
        }
      }
    }
    this.joints.clear();
  }

  /**
	 * Returns true if upon the next time step the contacts must be updated.
	 * <p>
	 * This is typically set via user code when something about the simulation changes
	 * that can affect collision detection.
	 * @return boolean
	 * @see #setUpdateRequired(boolean)
	 */
  public boolean isUpdateRequired() {
    return this.updateRequired;
  }

  /**
	 * Sets the update required flag.
	 * <p>
	 * Set this flag to true if any of the following conditions have been met:
	 * <ul>
	 * 	<li>If a Body has been added or removed from the World</li>
	 * 	<li>If a Body has been translated or rotated</li>
	 * 	<li>If a Body's state has been manually changed via the Body.setActive(boolean) method</li>
	 * 	<li>If a BodyFixture has been added or removed from a Body</li>
	 * 	<li>If a BodyFixture's sensor flag has been manually changed via the BodyFixture.setSensor(boolean) method</li>
	 * 	<li>If a BodyFixture's filter has been manually changed via the BodyFixture.setFilter(boolean) method</li>
	 * 	<li>If a BodyFixture's restitution or friction coefficient has changed</li>
	 * 	<li>If a BodyFixture's Shape has been translated or rotated</li>
	 * 	<li>If a BodyFixture's Shape has been changed (vertices, radius, etc.)</li>
	 * 	<li>If a Body's type has changed to or from Static (this is caused by the using setMassType(Mass.INFINITE/Mass.NORMAL) method)</li>
	 * 	<li>If a Joint has been added or removed from the World in which the joined bodies should not be allowed to collide</li>
	 * 	<li>If the World's CoefficientMixer has been changed</li>
	 * </ul>
	 * @param flag the flag
	 */
  public void setUpdateRequired(boolean flag) {
    this.updateRequired = flag;
  }

  /**
	 * Returns the world id.
	 * @return UUID
	 * @since 3.2.0
	 */
  public UUID getId() {
    return this.id;
  }

  /**
	 * Returns the settings for this world.
	 * @return {@link Settings}
	 * @since 3.0.3
	 */
  public Settings getSettings() {
    return this.settings;
  }

  /**
	 * Sets the dynamics settings for this world.
	 * @param settings the desired settings
	 * @throws NullPointerException if the given settings is null
	 * @since 3.0.3
	 */
  public void setSettings(Settings settings) {
    if (settings == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullSettings"));
    }
    this.settings = settings;
  }

  /**
	 * Sets the acceleration due to gravity.
	 * @param gravity the gravity in meters/second<sup>2</sup>
	 * @throws NullPointerException if gravity is null
	 */
  public void setGravity(Vector2 gravity) {
    if (gravity == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullGravity"));
    }
    this.gravity = gravity;
  }

  /**
	 * Returns the acceleration due to gravity.
	 * @return {@link Vector2} the gravity in meters/second<sup>2</sup>
	 */
  public Vector2 getGravity() {
    return this.gravity;
  }

  /**
	 * Sets the bounds of this {@link World}.
	 * @param bounds the bounds; can be null
	 */
  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  /**
	 * Returns the bounds of this world.
	 * <p>
	 * This will return null if no bounds were initially set
	 * or if it was set to null via the {@link #setBounds(Bounds)}
	 * method.
	 * @return {@link Bounds} the bounds or null
	 */
  public Bounds getBounds() {
    return this.bounds;
  }

  /**
	 * Returns the listeners that are of the given type (or sub types)
	 * of the given type.
	 * <p>
	 * Returns an empty list if no listeners for the given type are found.
	 * <p>
	 * Returns null if clazz is null.
	 * <p>
	 * Example usage:
	 * <pre>
	 * world.getListeners(ContactListener.class);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the type of listener to get
	 * @return List&lt;T&gt;
	 * @since 3.1.0
	 */
  public <T extends Listener> List<T> getListeners(Class<T> clazz) {
    if (clazz == null) {
      return null;
    }
    List<T> listeners = new ArrayList<T>();
    int lSize = this.listeners.size();
    for (int i = 0; i < lSize; i++) {
      Listener listener = this.listeners.get(i);
      if (clazz.isInstance(listener)) {
        listeners.add(clazz.cast(listener));
      }
    }
    return listeners;
  }

  /**
	 * Returns the listeners of the given type (or sub types) in the given list.
	 * <p>
	 * This method does <b>not</b> clear the given listeners list before
	 * adding the listeners.
	 * <p>
	 * If clazz or listeners is null, this method immediately returns.
	 * <p>
	 * Example usage:
	 * <pre>
	 * List&lt;ContactListener&gt; list = ...;
	 * world.getListeners(ContactListener.class, list);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the type of listener to get
	 * @param listeners the list to add the listeners to
	 * @since 3.1.1
	 */
  public <T extends Listener> void getListeners(Class<T> clazz, List<T> listeners) {
    if (clazz == null || listeners == null) {
      return;
    }
    int lSize = this.listeners.size();
    for (int i = 0; i < lSize; i++) {
      Listener listener = this.listeners.get(i);
      if (clazz.isInstance(listener)) {
        listeners.add(clazz.cast(listener));
      }
    }
  }

  /**
	 * Adds the given listener to the list of listeners.
	 * @param listener the listener
	 * @throws NullPointerException if the given listener is null
	 * @throws IllegalArgumentException if the given listener has already been added to this world
	 * @since 3.1.0
	 */
  public void addListener(Listener listener) {
    if (listener == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullListener"));
    }
    if (this.listeners.contains(listener)) {
      throw new IllegalArgumentException("dynamics.world.addExistingListener");
    }
    this.listeners.add(listener);
  }

  /**
	 * Returns true if the given listener is already attached to this world.
	 * @param listener the listener
	 * @return boolean
	 * @since 3.1.1
	 */
  public boolean containsListener(Listener listener) {
    return this.listeners.contains(listener);
  }

  /**
	 * Removes the given listener from this world.
	 * @param listener the listener to remove
	 * @return boolean true if the listener was removed
	 * @since 3.1.0
	 */
  public boolean removeListener(Listener listener) {
    return this.listeners.remove(listener);
  }

  /**
	 * Removes all the listeners.
	 * @return int the number of listeners removed
	 * @since 3.1.1
	 */
  public int removeAllListeners() {
    int count = this.listeners.size();
    this.listeners.clear();
    return count;
  }

  /**
	 * Removes all the listeners of the specified type (or sub types).
	 * <p>
	 * Returns zero if the given type is null or there are zero listeners
	 * attached.
	 * <p>
	 * Example usage:
	 * <pre>
	 * world.removeAllListeners(ContactListener.class);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the listener type
	 * @return int the number of listeners removed
	 * @since 3.1.1
	 */
  public <T extends Listener> int removeAllListeners(Class<T> clazz) {
    if (clazz == null) {
      return 0;
    }
    if (this.listeners.isEmpty()) {
      return 0;
    }
    int count = 0;
    Iterator<Listener> listenerIterator = this.listeners.iterator();
    while (listenerIterator.hasNext()) {
      Listener listener = listenerIterator.next();
      if (clazz.isInstance(listener)) {
        listenerIterator.remove();
        count++;
      }
    }
    return count;
  }

  /**
	 * Returns the total number of listeners attached to this world.
	 * @return int
	 * @since 3.1.1
	 */
  public int getListenerCount() {
    return this.listeners.size();
  }

  /**
	 * Returns the total number of listeners of the given type (or sub types) 
	 * attached to this world.
	 * <p>
	 * Returns zero if the given class type is null.
	 * <p>
	 * Example usage:
	 * <pre>
	 * world.getListenerCount(BoundsListener.class);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the listener type
	 * @return int
	 * @since 3.1.1
	 */
  public <T extends Listener> int getListenerCount(Class<T> clazz) {
    if (clazz == null) {
      return 0;
    }
    int count = 0;
    int lSize = this.listeners.size();
    for (int i = 0; i < lSize; i++) {
      Listener listener = this.listeners.get(i);
      if (clazz.isInstance(listener)) {
        count++;
      }
    }
    return count;
  }

  /**
	 * Sets the broad-phase collision detection algorithm.
	 * @param broadphaseDetector the broad-phase collision detection algorithm
	 * @throws NullPointerException if broadphaseDetector is null
	 */
  public void setBroadphaseDetector(BroadphaseDetector<Body, BodyFixture> broadphaseDetector) {
    if (broadphaseDetector == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullBroadphaseDetector"));
    }
    this.broadphaseDetector = broadphaseDetector;
    int size = this.bodies.size();
    for (int i = 0; i < size; i++) {
      this.broadphaseDetector.add(this.bodies.get(i));
    }
  }

  /**
	 * Returns the broad-phase collision detection algorithm.
	 * @return {@link BroadphaseDetector} the broad-phase collision detection algorithm
	 */
  public BroadphaseDetector<Body, BodyFixture> getBroadphaseDetector() {
    return this.broadphaseDetector;
  }

  /**
	 * Sets the {@link BroadphaseFilter} used when detecting collisions for each time step.
	 * <p>
	 * This should always be an instance of a class that extends the {@link DetectBroadphaseFilter}
	 * so that the standard filters are retained.
	 * @param filter the filter
	 * @since 3.2.2
	 */
  public void setDetectBroadphaseFilter(BroadphaseFilter<Body, BodyFixture> filter) {
    if (filter == null) {
      this.detectBroadphaseFilter = new DetectBroadphaseFilter();
    } else {
      this.detectBroadphaseFilter = filter;
    }
  }

  /**
	 * Returns the {@link BroadphaseFilter} used when detecting collisions for each time step.
	 * @return {@link BroadphaseFilter}
	 * @since 3.2.2
	 */
  public BroadphaseFilter<Body, BodyFixture> getDetectBroadphaseFilter() {
    return this.detectBroadphaseFilter;
  }

  /**
	 * Sets the narrow-phase collision detection algorithm.
	 * @param narrowphaseDetector the narrow-phase collision detection algorithm
	 * @throws NullPointerException if narrowphaseDetector is null
	 */
  public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector) {
    if (narrowphaseDetector == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullNarrowphaseDetector"));
    }
    this.narrowphaseDetector = narrowphaseDetector;
  }

  /**
	 * Returns the narrow-phase collision detection algorithm.
	 * @return {@link NarrowphaseDetector} the narrow-phase collision detection algorithm
	 */
  public NarrowphaseDetector getNarrowphaseDetector() {
    return this.narrowphaseDetector;
  }

  /**
	 * Sets the manifold solver.
	 * @param manifoldSolver the manifold solver
	 * @throws NullPointerException if manifoldSolver is null
	 */
  public void setManifoldSolver(ManifoldSolver manifoldSolver) {
    if (manifoldSolver == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullManifoldSolver"));
    }
    this.manifoldSolver = manifoldSolver;
  }

  /**
	 * Returns the manifold solver.
	 * @return {@link ManifoldSolver} the manifold solver
	 */
  public ManifoldSolver getManifoldSolver() {
    return this.manifoldSolver;
  }

  /**
	 * Sets the time of impact detector.
	 * @param timeOfImpactDetector the time of impact detector
	 * @throws NullPointerException if timeOfImpactDetector is null
	 * @since 1.2.0
	 */
  public void setTimeOfImpactDetector(TimeOfImpactDetector timeOfImpactDetector) {
    if (timeOfImpactDetector == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullTimeOfImpactDetector"));
    }
    this.timeOfImpactDetector = timeOfImpactDetector;
  }

  /**
	 * Returns the time of impact detector.
	 * @return {@link TimeOfImpactDetector} the time of impact detector
	 * @since 1.2.0
	 */
  public TimeOfImpactDetector getTimeOfImpactDetector() {
    return this.timeOfImpactDetector;
  }

  /**
	 * Sets the raycast detector.
	 * @param raycastDetector the raycast detector
	 * @throws NullPointerException if raycastDetector is null
	 * @since 2.0.0
	 */
  public void setRaycastDetector(RaycastDetector raycastDetector) {
    if (raycastDetector == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullRaycastDetector"));
    }
    this.raycastDetector = raycastDetector;
  }

  /**
	 * Returns the raycast detector.
	 * @return {@link RaycastDetector} the raycast detector
	 * @since 2.0.0
	 */
  public RaycastDetector getRaycastDetector() {
    return this.raycastDetector;
  }

  /**
	 * Returns the {@link CoefficientMixer}.
	 * @return {@link CoefficientMixer}
	 * @see #setCoefficientMixer(CoefficientMixer)
	 */
  public CoefficientMixer getCoefficientMixer() {
    return this.coefficientMixer;
  }

  /**
	 * Sets the {@link CoefficientMixer}.
	 * <p>
	 * A {@link CoefficientMixer} is an implementation of mixing functions for various
	 * coefficients used in contact solving.  Common coefficients are restitution and 
	 * friction.  Since each {@link BodyFixture} can have it's own value for these 
	 * coefficients, the {@link CoefficientMixer} is used to mathematically combine them
	 * into one coefficient to be used in contact resolution.
	 * <p>
	 * {@link CoefficientMixer#DEFAULT_MIXER} is the default.
	 * @param coefficientMixer the coefficient mixer
	 * @throws NullPointerException if coefficientMixer is null
	 * @see CoefficientMixer
	 */
  public void setCoefficientMixer(CoefficientMixer coefficientMixer) {
    if (coefficientMixer == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullCoefficientMixer"));
    }
    this.coefficientMixer = coefficientMixer;
  }

  /**
	 * Sets the {@link ContactManager}.
	 * <p>
	 * A {@link ContactManager} manages the contacts detected in the {@link World#detect()} method
	 * and performs notification of {@link ContactListener}s.  {@link ContactManager}s can also contain
	 * specialized logic for improving performance and simulation quality.
	 * <p>
	 * Changing the contact manager requires an update to be performed on the next update of this
	 * world and any cached information will be lost.
	 * <p>
	 * The default is the {@link WarmStartingContactManager}.
	 * @param contactManager the contact manager
	 * @throws NullPointerException if contactManager is null
	 * @see ContactManager
	 * @since 3.2.0
	 */
  public void setContactManager(ContactManager contactManager) {
    if (contactManager == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullContactManager"));
    }
    this.contactManager = contactManager;
    this.updateRequired = true;
  }

  /**
	 * Returns the {@link ContactManager}.
	 * @return {@link ContactManager}
	 * @since 1.0.2
	 * @see #setContactManager(ContactManager)
	 */
  public ContactManager getContactManager() {
    return this.contactManager;
  }

  /**
	 * Sets the {@link ContactConstraintSolver} for this world.
	 * @param constraintSolver the contact constraint solver
	 * @throws NullPointerException if contactManager is null
	 * @see ContactConstraintSolver
	 * @since 3.2.0
	 */
  public void setContactConstraintSolver(ContactConstraintSolver constraintSolver) {
    if (constraintSolver == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullContactConstraintSolver"));
    }
    this.contactConstraintSolver = constraintSolver;
  }

  /**
	 * Returns the {@link ContactConstraintSolver}.
	 * @return {@link ContactConstraintSolver}
	 * @since 3.2.0
	 * @see #setContactConstraintSolver(ContactConstraintSolver)
	 */
  public ContactConstraintSolver getContactConstraintSolver() {
    return this.contactConstraintSolver;
  }

  public Object getUserData() {
    return this.userData;
  }

  public void setUserData(Object userData) {
    this.userData = userData;
  }

  /**
	 * Returns the number of {@link Body}s in this {@link World}.
	 * @return int the number of bodies
	 */
  public int getBodyCount() {
    return this.bodies.size();
  }

  /**
	 * Returns the {@link Body} at the given index.
	 * @param index the index
	 * @return {@link Body}
	 */
  public Body getBody(int index) {
    return this.bodies.get(index);
  }

  /**
	 * Returns an unmodifiable list containing all the bodies in this world.
	 * <p>
	 * The returned list is backed by the internal list, therefore adding or removing bodies while 
	 * iterating through the returned list is not permitted.  Use the {@link #getBodyIterator()}
	 * method instead.
	 * @return List&lt;{@link Body}&gt;
	 * @since 3.1.5
	 * @see #getBodyIterator()
	 */
  public List<Body> getBodies() {
    return Collections.unmodifiableList(this.bodies);
  }

  /**
	 * Returns an iterator for iterating over the bodies in this world.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;{@link Body}&gt;
	 * @since 3.2.0
	 */
  public Iterator<Body> getBodyIterator() {
    return new BodyIterator(this);
  }

  /**
	 * Returns the number of {@link Joint}s in this {@link World}.
	 * @return int the number of joints
	 */
  public int getJointCount() {
    return this.joints.size();
  }

  /**
	 * Returns the {@link Joint} at the given index.
	 * @param index the index
	 * @return {@link Joint}
	 */
  public Joint getJoint(int index) {
    return this.joints.get(index);
  }

  /**
	 * Returns an unmodifiable list containing all the joints in this world.
	 * <p>
	 * The returned list is backed by the internal list, therefore adding or removing joints while 
	 * iterating through the returned list is not permitted.  Use the {@link #getJointIterator()}
	 * method instead.
	 * @return List&lt;{@link Joint}&gt;
	 * @since 3.1.5
	 * @see #getJointIterator()
	 */
  public List<Joint> getJoints() {
    return Collections.unmodifiableList(this.joints);
  }

  /**
	 * Returns an iterator for iterating over the joints in this world.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;{@link Joint}&gt;
	 * @since 3.2.0
	 */
  public Iterator<Joint> getJointIterator() {
    return new JointIterator(this);
  }

  /**
	 * Returns the {@link Step} object used to advance
	 * the simulation.
	 * <p>
	 * The returned object contains the step information (elapsed time)
	 * for the last and the previous time step.
	 * @return {@link Step} the current step object
	 */
  public Step getStep() {
    return this.step;
  }

  /**
	 * Returns true if this world doesn't contain any
	 * bodies or joints.
	 * @return boolean
	 * @since 3.0.1
	 */
  public boolean isEmpty() {
    int bSize = this.bodies.size();
    int jSize = this.joints.size();
    return bSize == 0 && jSize == 0;
  }

  /**
	 * Returns the current accumulated time.
	 * <p>
	 * This is the time that has elapsed since the last step
	 * of the engine.
	 * <p>
	 * This time is used and/or accumulated on each call of the 
	 * {@link #update(double)} and {@link #update(double, int)} methods.
	 * <p>
	 * This time is reduced by the step frequency for each step
	 * of the engine.
	 * @return double
	 * @since 3.1.10
	 */
  public double getAccumulatedTime() {
    return this.time;
  }

  /**
	 * Sets the current accumulated time.
	 * <p>
	 * A typical use case would be to throw away any remaining time
	 * that the {@link #update(double)} or {@link #update(double, int)}
	 * methods didn't use:
	 * <pre>
	 * boolean updated = world.update(elapsedTime);
	 * // the check if the world actually updated is crutial in this example
	 * if (updated) {
	 * 	// throw away any remaining time we didnt use
	 * 	world.setAccumulatedTime(0);
	 * }
	 * </pre>
	 * Or, in the case of reusing the same World object, you could use this
	 * method to clear any accumulated time.
	 * <p>
	 * If elapsedTime is less than zero, this method immediately returns.
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the desired elapsed time
	 * @since 3.1.10
	 */
  public void setAccumulatedTime(double elapsedTime) {
    if (elapsedTime < 0.0) {
      return;
    }
    this.time = elapsedTime;
  }
}