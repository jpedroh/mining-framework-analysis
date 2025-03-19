package org.dyn4j.world;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dyn4j.DataContainer;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.broadphase.AABBExpansionMethod;
import org.dyn4j.collision.broadphase.AABBProducer;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.CollisionBodyBroadphaseFilter;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.StaticValueAABBExpansionMethod;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.ContactUpdateHandler;
import org.dyn4j.dynamics.contact.ForceCollisionTimeOfImpactSolver;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.TimeOfImpactListener;

/**
 * Abstract implementation of the {@link PhysicsWorld} interface.
 * <p>
 * This class builds on top of the {@link AbstractCollisionWorld} class adding a physics
 * pipeline to the collision detection pipeline. This class implements the {@link #processCollisions(Iterator)}
 * method and uses it to build a {@link ConstraintGraph} which is then used to solve
 * {@link ContactConstraint}s and {@link Joint}s.
 * <p>
 * Extenders only need to implement the {@link #createCollisionData(org.dyn4j.collision.CollisionPair)} method
 * to ensure the correct type of collision data is used for tracking.
 * <p>
 * <b>NOTE</b>: This class uses the {@link Body#setOwner(Object)} and 
 * {@link Body#setFixtureModificationHandler(org.dyn4j.collision.FixtureModificationHandler)}
 * methods to handle certain scenarios like fixture removal on a body or bodies added to
 * more than one world. Likewise, the {@link Joint#setOwner(Object)} method is used to handle
 * joints being added to the world. Callers should <b>NOT</b> use the methods.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.0.0
 * @param <T> the {@link PhysicsBody} type
 * @param <V> the {@link ContactCollisionData} type
 */
public abstract class AbstractPhysicsWorld<T extends PhysicsBody, V extends ContactCollisionData<T>> extends AbstractCollisionWorld<T, BodyFixture, V> implements PhysicsWorld<T, V>, Shiftable, DataContainer {
  /** The dynamics settings for this world */
  protected final Settings settings;

  /** The {@link TimeStep} used by the dynamics calculations */
  protected final TimeStep timeStep;

  /** The world gravity vector */
  protected final Vector2 gravity;

  /** The {@link CoefficientMixer} */
  protected CoefficientMixer coefficientMixer;

  /** The {@link ContactConstraintSolver} */
  protected ContactConstraintSolver<T> contactConstraintSolver;

  /** The {@link TimeOfImpactSolver} */
  protected TimeOfImpactSolver<T> timeOfImpactSolver;

  /** The CCD {@link BroadphaseDetector} */
  protected BroadphaseDetector<T> ccdBroadphase;

  /** The {@link Joint} list */
  protected final List<Joint<T>> joints;

  /** The unmodifiable {@link Joint} list */
  protected final List<Joint<T>> jointsUnmodifiable;

  /** The list of {@link ContactListener}s */
  protected final List<ContactListener<T>> contactListeners;

  /** The unmodifiable list of {@link ContactListener}s */
  protected final List<ContactListener<T>> contactListenersUnmodifiable;

  /** The list of {@link DestructionListener}s */
  protected final List<DestructionListener<T>> destructionListeners;

  /** The unmodifiable list of {@link DestructionListener}s */
  protected final List<DestructionListener<T>> destructionListenersUnmodifiable;

  /** The list of {@link TimeOfImpactListener}s */
  protected final List<TimeOfImpactListener<T>> timeOfImpactListeners;

  /** The unmodifiable list of {@link TimeOfImpactListener}s */
  protected final List<TimeOfImpactListener<T>> timeOfImpactListenersUnmodifiable;

  /** The list of {@link StepListener}s */
  protected final List<StepListener<T>> stepListeners;

  /** The unmodifiable list of {@link StepListener}s */
  protected final List<StepListener<T>> stepListenersUnmodifiable;

  /** The accumulated time */
  protected double time;

  /** True if an update to the collision data or interaction graph is needed before a step of the engine */
  protected boolean updateRequired;

  /** The constraint graph between bodies */
  protected final ConstraintGraph<T> constraintGraph;

  /** A temporary list of only the {@link ContactConstraint} collisions from the last detection; cleared and refilled each step */
  protected final List<V> contactCollisions;

  /** The full set of tracked CCD collision data */
  protected final Set<CollisionPair<T>> ccdCollisionData;

  /**
	 * Default constructor.
	 * <p>
	 * Uses the {@link CollisionWorld#DEFAULT_INITIAL_BODY_CAPACITY} and
	 * {@link PhysicsWorld#DEFAULT_INITIAL_JOINT_CAPACITY} as the initial capacity.
	 */
  public AbstractPhysicsWorld() {
    this(DEFAULT_INITIAL_BODY_CAPACITY, DEFAULT_INITIAL_JOINT_CAPACITY);
  }

  /**
	 * Optional constructor.
	 * @param initialBodyCapacity the initial body capacity
	 * @param initialJointCapacity the initial joint capacity
	 */
  public AbstractPhysicsWorld(int initialBodyCapacity, int initialJointCapacity) {
    super(initialBodyCapacity);
    if (initialBodyCapacity <= 0) {
      initialBodyCapacity = DEFAULT_INITIAL_BODY_CAPACITY;
    }
    if (initialJointCapacity <= 0) {
      initialJointCapacity = DEFAULT_INITIAL_JOINT_CAPACITY;
    }
    this.settings = new Settings();
    this.timeStep = new TimeStep(this.settings.getStepFrequency());
    this.gravity = PhysicsWorld.EARTH_GRAVITY.copy();
    this.broadphaseFilter = new PhysicsBodyBroadphaseCollisionDataFilter<T>(this);
    this.coefficientMixer = CoefficientMixer.DEFAULT_MIXER;
    this.contactConstraintSolver = new SequentialImpulses<T>();
    this.timeOfImpactSolver = new ForceCollisionTimeOfImpactSolver<T>();
    final BroadphaseFilter<T> broadphaseFilter = new CollisionBodyBroadphaseFilter<T>();
    final AABBProducer<T> aabbProducer = new PhysicsBodySweptAABBProducer<T>();
    final AABBExpansionMethod<T> expansionMethod = new StaticValueAABBExpansionMethod<T>(0.2);
    this.ccdBroadphase = new DynamicAABBTree<T>(broadphaseFilter, aabbProducer, expansionMethod, initialBodyCapacity);
    this.ccdBroadphase.setUpdateTrackingEnabled(true);
    this.joints = new ArrayList<Joint<T>>(initialJointCapacity);
    this.jointsUnmodifiable = Collections.unmodifiableList(this.joints);
    this.contactListeners = new ArrayList<ContactListener<T>>();
    this.destructionListeners = new ArrayList<DestructionListener<T>>();
    this.timeOfImpactListeners = new ArrayList<TimeOfImpactListener<T>>();
    this.stepListeners = new ArrayList<StepListener<T>>();
    this.contactListenersUnmodifiable = Collections.unmodifiableList(this.contactListeners);
    this.destructionListenersUnmodifiable = Collections.unmodifiableList(this.destructionListeners);
    this.timeOfImpactListenersUnmodifiable = Collections.unmodifiableList(this.timeOfImpactListeners);
    this.stepListenersUnmodifiable = Collections.unmodifiableList(this.stepListeners);
    this.time = 0.0;
    int estimatedCollisionPairs = Collisions.getEstimatedCollisionPairs(initialBodyCapacity);
    this.constraintGraph = new ConstraintGraph<T>(initialBodyCapacity, initialJointCapacity);
    this.contactCollisions = new ArrayList<V>(estimatedCollisionPairs);
    this.ccdCollisionData = new LinkedHashSet<CollisionPair<T>>();
    this.updateRequired = true;
  }

  @Override public boolean update(double elapsedTime) {
    return this.update(elapsedTime, -1.0, 1);
  }

  @Override public boolean update(double elapsedTime, int maximumSteps) {
    return this.update(elapsedTime, -1.0, maximumSteps);
  }

  @Override public boolean update(double elapsedTime, double stepElapsedTime) {
    return this.update(elapsedTime, stepElapsedTime, 1);
  }

  @Override public boolean update(double elapsedTime, double stepElapsedTime, int maximumSteps) {
    if (elapsedTime < 0.0) {
      elapsedTime = 0.0;
    }
    this.time += elapsedTime;
    double invhz = this.settings.getStepFrequency();
    int steps = 0;
    while (this.time >= invhz && steps < maximumSteps) {
      this.timeStep.update(stepElapsedTime <= 0 ? invhz : stepElapsedTime);
      this.time = this.time - invhz;
      this.step();
      steps++;
    }
    return steps > 0;
  }

  @Override public void updatev(double elapsedTime) {
    if (elapsedTime <= 0.0) {
      return;
    }
    this.timeStep.update(elapsedTime);
    this.step();
  }

  @Override public void step(int steps) {
    double invhz = this.settings.getStepFrequency();
    this.step(steps, invhz);
  }

  @Override public void step(int steps, double elapsedTime) {
    if (steps <= 0) {
      return;
    }
    if (elapsedTime <= 0.0) {
      return;
    }
    for (int i = 0; i < steps; i++) {
      this.timeStep.update(elapsedTime);
      this.step();
    }
  }

  @Override public void addBody(T body) {
    super.addBody(body);
    this.constraintGraph.addBody(body);
    this.ccdBroadphase.add(body);
  }

  @Override public void addJoint(Joint<T> joint) {
    if (joint == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.addNullJoint"));
    }
    if (joint.getOwner() == this) {
      throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingJoint"));
    }
    if (joint.getOwner() != null) {
      throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldJoint"));
    }
    T body1 = joint.getBody1();
    T body2 = joint.getBody2();
    if (!this.constraintGraph.containsBody(body1) || !this.constraintGraph.containsBody(body2)) {
      throw new IllegalArgumentException("dynamics.world.addJointWithoutBodies");
    }
    this.joints.add(joint);
    joint.setOwner(this);
    this.constraintGraph.addJoint(joint);
  }

  @Override public boolean containsJoint(Joint<T> joint) {
    return this.joints.contains(joint);
  }

  @Override public boolean removeJoint(int index) {
    Joint<T> joint = this.joints.get(index);
    return removeJoint(joint);
  }

  @Override public void removeAllBodies() {
    this.removeAllBodiesAndJoints(false);
  }

  @Override public boolean removeBody(int index) {
    return this.removeBody(index, false);
  }

  @Override public boolean removeBody(int index, boolean notify) {
    T body = this.bodies.get(index);
    return this.removeBody(body, notify);
  }

  @Override public boolean removeBody(T body) {
    return this.removeBody(body, false);
  }

  @Override public void removeAllBodiesAndJoints() {
    this.removeAllBodiesAndJoints(false);
  }

  @Override public void removeAllJoints() {
    this.removeAllJoints(false);
  }

  @Override public void removeAllBodiesAndJoints(boolean notify) {
    this.removeAllBodies(notify);
  }

  /**
	 * Destroys all joints associated with the given constraint graph node.
	 * @param node the node
	 * @param notify true if destruction should emit notifications
	 */
  protected void destroyJoints(ConstraintGraphNode<T> node, boolean notify) {
    T body = node.body;
    int jSize = node.joints.size();
    for (int j = 0; j < jSize; j++) {
      Joint<T> joint = node.joints.get(j);
      joint.setOwner(null);
      T other = joint.getOtherBody(body);
      other.setAtRest(false);
      if (notify) {
        for (DestructionListener<T> dl : this.destructionListeners) {
          dl.destroyed(joint);
        }
      }
      this.joints.remove(joint);
      ConstraintGraphNode<T> otherNode = this.constraintGraph.getNode(other);
      if (otherNode != null) {
        otherNode.joints.remove(joint);
      }
    }
    node.joints.clear();
  }

  /**
	 * Destroys the contacts for the given graph node.
	 * @param node the node
	 * @param fixture the fixture of the contacts to destroy; null means to destroy all
	 * @param notify true if destruction should emit notifications
	 */
  protected void destroyContacts(ConstraintGraphNode<T> node, BodyFixture fixture, boolean notify) {
    T body = node.body;
    Iterator<ContactConstraint<T>> it = node.contactConstraints.iterator();
    while (it.hasNext()) {
      ContactConstraint<T> contactConstraint = it.next();
      if (fixture != null && contactConstraint.getFixture1() != fixture && contactConstraint.getFixture2() != fixture) {
        continue;
      }
      T other = contactConstraint.getOtherBody(body);
      other.setAtRest(false);
      V data = this.collisionData.remove(contactConstraint.getCollisionPair());
      if (notify) {
        List<? extends SolvedContact> contacts = contactConstraint.getContacts();
        int cSize = contacts.size();
        for (int k = 0; k < cSize; k++) {
          Contact contact = contacts.get(k);
          for (ContactListener<T> cl : this.contactListeners) {
            cl.destroyed(data, contact);
          }
        }
        for (DestructionListener<T> dl : this.destructionListeners) {
          dl.destroyed(contactConstraint);
        }
      }
      if (fixture != null) {
        it.remove();
      }
      ConstraintGraphNode<T> otherNode = this.constraintGraph.getNode(other);
      if (otherNode != null) {
        otherNode.contactConstraints.remove(contactConstraint);
      }
    }
    if (fixture == null) {
      node.contactConstraints.clear();
    }
  }

  @Override public boolean removeBody(T body, boolean notify) {
    if (body == null) {
      return false;
    }
    boolean removed = this.bodies.remove(body);
    if (removed) {
      body.setFixtureModificationHandler(null);
      body.setOwner(null);
      body.setAtRest(false);
      body.setEnabled(true);
      this.broadphaseDetector.remove(body);
      this.ccdBroadphase.remove(body);
      ConstraintGraphNode<T> node = this.constraintGraph.removeBody(body);
      this.destroyJoints(node, notify);
      this.destroyContacts(node, null, notify);
    }
    return removed;
  }

  @Override public boolean removeJoint(Joint<T> joint) {
    boolean removed = this.joints.remove(joint);
    if (removed) {
      joint.setOwner(null);
      T b1 = joint.getBody1();
      T b2 = joint.getBody2();
      b1.setAtRest(false);
      b2.setAtRest(false);
      this.constraintGraph.removeJoint(joint);
    }
    return removed;
  }

  @Override public void removeAllBodies(boolean notify) {
    int bsize = this.bodies.size();
    int jsize = this.joints.size();
    if (!notify) {
      for (int i = 0; i < bsize; i++) {
        T body = this.bodies.get(i);
        body.setFixtureModificationHandler(null);
        body.setOwner(null);
        body.setAtRest(false);
        body.setEnabled(true);
      }
      for (int i = 0; i < jsize; i++) {
        Joint<T> joint = this.joints.get(i);
        joint.setOwner(null);
      }
      this.clear();
      return;
    }
    for (int i = 0; i < bsize; i++) {
      T body = this.bodies.get(i);
      body.setFixtureModificationHandler(null);
      body.setOwner(null);
      body.setAtRest(false);
      body.setEnabled(true);
      ConstraintGraphNode<T> node = this.constraintGraph.removeBody(body);
      this.destroyJoints(node, notify);
      this.destroyContacts(node, null, notify);
      for (DestructionListener<T> dl : this.destructionListeners) {
        dl.destroyed(body);
      }
    }
    this.clear();
  }

  /**
	 * Helper method to clear the world of bodies and joints.
	 */
  protected void clear() {
    this.bodies.clear();
    this.broadphaseDetector.clear();
    this.ccdBroadphase.clear();
    this.collisionData.clear();
    this.constraintGraph.clear();
    this.contactCollisions.clear();
    this.joints.clear();
  }

  @Override public void removeAllJoints(boolean notify) {
    int size = this.joints.size();
    for (int i = 0; i < size; i++) {
      Joint<T> joint = this.joints.get(i);
      joint.setOwner(null);
      T b1 = joint.getBody1();
      T b2 = joint.getBody2();
      b1.setAtRest(false);
      b2.setAtRest(false);
      if (notify) {
        for (DestructionListener<T> dl : this.destructionListeners) {
          dl.destroyed(joint);
        }
      }
    }
    this.constraintGraph.removeAllJoints();
    this.joints.clear();
  }

  @Override protected void handleFixtureRemoved(T body, BodyFixture fixture) {
    super.handleFixtureRemoved(body, fixture);
    ConstraintGraphNode<T> node = this.constraintGraph.getNode(body);
    if (node != null) {
      this.destroyContacts(node, fixture, true);
    }
  }

  @Override protected void handleAllFixturesRemoved(T body) {
    super.handleAllFixturesRemoved(body);
    ConstraintGraphNode<T> node = this.constraintGraph.getNode(body);
    if (node != null) {
      this.destroyContacts(node, null, true);
    }
  }

  @Override public Settings getSettings() {
    return this.settings;
  }

  @Override public void setSettings(Settings settings) {
    if (settings == null) {
      return;
    }
    this.settings.copy(settings);
  }

  @Override public void setGravity(Vector2 gravity) {
    if (gravity == null) {
      return;
    }
    this.gravity.x = gravity.x;
    this.gravity.y = gravity.y;
  }

  @Override public void setGravity(double x, double y) {
    this.gravity.x = x;
    this.gravity.y = y;
  }

  @Override public Vector2 getGravity() {
    return this.gravity;
  }

  @Override public CoefficientMixer getCoefficientMixer() {
    return this.coefficientMixer;
  }

  @Override public void setCoefficientMixer(CoefficientMixer coefficientMixer) {
    if (coefficientMixer == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullCoefficientMixer"));
    }
    this.coefficientMixer = coefficientMixer;
  }

  @Override public void setContactConstraintSolver(ContactConstraintSolver<T> constraintSolver) {
    if (constraintSolver == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullContactConstraintSolver"));
    }
    this.contactConstraintSolver = constraintSolver;
  }

  @Override public ContactConstraintSolver<T> getContactConstraintSolver() {
    return this.contactConstraintSolver;
  }

  @Override public BroadphaseDetector<T> getContinuousCollisionDetectionBroadphaseDetector() {
    return this.ccdBroadphase;
  }

  @Override public void setContinuousCollisionDetectionBroadphaseDetector(BroadphaseDetector<T> broadphaseDetector) {
    if (broadphaseDetector == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullBroadphaseDetector"));
    }
    this.ccdBroadphase = broadphaseDetector;
    int size = this.bodies.size();
    for (int i = 0; i < size; i++) {
      T body = this.bodies.get(i);
      this.ccdBroadphase.add(body);
    }
  }

  @Override public void setTimeOfImpactSolver(TimeOfImpactSolver<T> timeOfImpactSolver) {
    if (timeOfImpactSolver == null) {
      throw new NullPointerException(Messages.getString("dynamics.world.nullTimeOfImpactSolver"));
    }
    this.timeOfImpactSolver = timeOfImpactSolver;
  }

  @Override public TimeOfImpactSolver<T> getTimeOfImpactSolver() {
    return this.timeOfImpactSolver;
  }

  @Override public int getJointCount() {
    return this.joints.size();
  }

  @Override public Joint<T> getJoint(int index) {
    return this.joints.get(index);
  }

  @Override public List<Joint<T>> getJoints() {
    return this.jointsUnmodifiable;
  }

  @Override public Iterator<Joint<T>> getJointIterator() {
    return new JointIterator();
  }

  @Override public TimeStep getTimeStep() {
    return this.timeStep;
  }

  @Override public double getAccumulatedTime() {
    return this.time;
  }

  @Override public void setAccumulatedTime(double elapsedTime) {
    if (elapsedTime < 0.0) {
      return;
    }
    this.time = elapsedTime;
  }

  @Override public void setUpdateRequired(boolean flag) {
    this.updateRequired = flag;
  }

  @Override public boolean isUpdateRequired() {
    return this.updateRequired;
  }

  @Override public boolean isEmpty() {
    int nb = this.bodies.size();
    int nj = this.joints.size();
    return nb <= 0 && nj <= 0;
  }

  @Override public void shift(Vector2 shift) {
    super.shift(shift);
    int jSize = this.joints.size();
    for (int i = 0; i < jSize; i++) {
      Joint<T> joint = this.joints.get(i);
      joint.shift(shift);
    }
  }

  @Override public boolean isInContact(T body1, T body2) {
    return this.constraintGraph.isInContact(body1, body2);
  }

  @Override public List<ContactConstraint<T>> getContacts(T body) {
    return this.constraintGraph.getContacts(body);
  }

  @Override public List<T> getInContactBodies(T body, boolean includeSensedContact) {
    return this.constraintGraph.getInContactBodies(body, includeSensedContact);
  }

  @Override public boolean isJointCollisionAllowed(T body1, T body2) {
    return this.constraintGraph.isJointCollisionAllowed(body1, body2);
  }

  @Override public boolean isJoined(T body1, T body2) {
    return this.constraintGraph.isJoined(body1, body2);
  }

  @Override public List<Joint<T>> getJoints(T body) {
    return this.constraintGraph.getJoints(body);
  }

  @Override public List<T> getJoinedBodies(T body) {
    return this.constraintGraph.getJoinedBodies(body);
  }

  @Override public List<ContactListener<T>> getContactListeners() {
    return this.contactListenersUnmodifiable;
  }

  @Override public List<DestructionListener<T>> getDestructionListeners() {
    return this.destructionListenersUnmodifiable;
  }

  @Override public List<StepListener<T>> getStepListeners() {
    return this.stepListenersUnmodifiable;
  }

  @Override public List<TimeOfImpactListener<T>> getTimeOfImpactListeners() {
    return this.timeOfImpactListenersUnmodifiable;
  }

  @Override public void removeAllListeners() {
    super.removeAllListeners();
    this.stepListeners.clear();
    this.contactListeners.clear();
    this.destructionListeners.clear();
    this.timeOfImpactListeners.clear();
  }

  @Override public void removeAllContactListeners() {
    this.contactListeners.clear();
  }

  @Override public void removeAllDestructionListeners() {
    this.destructionListeners.clear();
  }

  @Override public void removeAllStepListeners() {
    this.stepListeners.clear();
  }

  @Override public void removeAllTimeOfImpactListeners() {
    this.timeOfImpactListeners.clear();
  }

  @Override public boolean removeContactListener(ContactListener<T> listener) {
    return this.contactListeners.remove(listener);
  }

  @Override public boolean removeDestructionListener(DestructionListener<T> listener) {
    return this.destructionListeners.remove(listener);
  }

  @Override public boolean removeStepListener(StepListener<T> listener) {
    return this.stepListeners.remove(listener);
  }

  @Override public boolean removeTimeOfImpactListener(TimeOfImpactListener<T> listener) {
    return this.timeOfImpactListeners.remove(listener);
  }

  @Override public boolean addContactListener(ContactListener<T> listener) {
    return this.contactListeners.add(listener);
  }

  @Override public boolean addDestructionListener(DestructionListener<T> listener) {
    return this.destructionListeners.add(listener);
  }

  @Override public boolean addStepListener(StepListener<T> listener) {
    return this.stepListeners.add(listener);
  }

  @Override public boolean addTimeOfImpactListener(TimeOfImpactListener<T> listener) {
    return this.timeOfImpactListeners.add(listener);
  }

  /**
	 * Performs a full step of the engine.
	 */
  protected void step() {
    List<StepListener<T>> stepListeners = this.stepListeners;
    List<ContactListener<T>> contactListeners = this.contactListeners;
    int sSize = stepListeners.size();
    for (int i = 0; i < sSize; i++) {
      StepListener<T> sl = stepListeners.get(i);
      sl.begin(this.timeStep, this);
    }
    if (this.updateRequired) {
      this.detect();
      for (int i = 0; i < sSize; i++) {
        StepListener<T> sl = stepListeners.get(i);
        sl.updatePerformed(this.timeStep, this);
      }
      this.updateRequired = false;
    }
    if (contactListeners.size() > 0) {
      for (ContactCollisionData<T> data : this.contactCollisions) {
        ContactConstraint<T> cc = data.getContactConstraint();
        for (Contact contact : cc.getContacts()) {
          for (ContactListener<T> listener : contactListeners) {
            listener.preSolve(data, contact);
          }
        }
      }
    }
    ContinuousDetectionMode continuousDetectionMode = this.settings.getContinuousDetectionMode();
    int size = this.bodies.size();
    for (int i = 0; i < size; i++) {
      T body = this.bodies.get(i);
      body.getPreviousTransform().set(body.getTransform());
    }
    this.constraintGraph.solve(this.contactConstraintSolver, this.gravity, this.timeStep, this.settings);
    if (contactListeners.size() > 0) {
      for (ContactCollisionData<T> data : this.contactCollisions) {
        ContactConstraint<T> cc = data.getContactConstraint();
        for (SolvedContact contact : cc.getContacts()) {
          for (ContactListener<T> listener : contactListeners) {
            listener.postSolve(data, contact);
          }
        }
      }
    }
    for (int i = 0; i < sSize; i++) {
      StepListener<T> sl = stepListeners.get(i);
      sl.postSolve(this.timeStep, this);
    }
    if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
      this.ccdBroadphase.update();
      this.solveTOI(continuousDetectionMode);
      this.ccdBroadphase.clearUpdates();
    }
    this.detect();
    this.updateRequired = false;
    for (int i = 0; i < sSize; i++) {
      StepListener<T> sl = stepListeners.get(i);
      sl.end(this.timeStep, this);
    }
  }

  @Override protected void processCollisions(Iterator<V> iterator) {
    this.constraintGraph.removeAllContactConstraints();
    this.contactCollisions.clear();
    WarmStartHandler wsh = new WarmStartHandler();
    while (iterator.hasNext()) {
      V collision = iterator.next();
      ContactConstraint<T> contactConstraint = collision.getContactConstraint();
      if (!collision.isManifoldCollision() && contactConstraint.getContacts().size() == 0) {
        continue;
      }
      wsh.data = collision;
      contactConstraint.update(collision.getManifold(), this.settings, wsh);
      if (collision.isManifoldCollision()) {
        collision.setContactConstraintCollision(true);
        this.constraintGraph.addContactConstraint(contactConstraint);
        for (ContactListener<T> listener : this.contactListeners) {
          listener.collision(collision, contactConstraint);
        }
        if (contactConstraint.isEnabled() && !contactConstraint.isSensor()) {
          this.contactCollisions.add(collision);
        }
      }
    }
  }

  /**
	 * Solves any Time-of-Impact events (collision events that were missed by the
	 * discrete collision detection algorithms).
	 * <p>
	 * Returns true if any TOI event was resolved. When true, the bodies involved
	 * had their transforms modified an another discrete collision detection is required.
	 * @param mode the mode
	 * @return boolean 
	 */
  protected boolean solveTOI(ContinuousDetectionMode mode) {
    List<TimeOfImpactListener<T>> listeners = this.timeOfImpactListeners;
    boolean bulletsOnly = (mode == ContinuousDetectionMode.BULLETS_ONLY);
    Iterator<CollisionPair<T>> pairIterator = this.ccdBroadphase.detectIterator();
    while (pairIterator.hasNext()) {
      CollisionPair<T> pair = pairIterator.next().copy();
      this.ccdCollisionData.add(pair);
    }
    Map<T, List<T>> pairMapping = new HashMap<T, List<T>>();
    Iterator<CollisionPair<T>> iterator = this.ccdCollisionData.iterator();
    while (iterator.hasNext()) {
      CollisionPair<T> pair = iterator.next();
      T body1 = pair.getFirst();
      T body2 = pair.getSecond();
      if (!this.ccdBroadphase.contains(body1) || !this.ccdBroadphase.contains(body2)) {
        iterator.remove();
      }
      if (this.ccdBroadphase.isUpdated(body1) || this.ccdBroadphase.isUpdated(body2)) {
        boolean overlaps = this.ccdBroadphase.detect(body1, body2);
        if (!overlaps) {
          iterator.remove();
        }
      }
      if (bulletsOnly && !body1.isBullet() && !body2.isBullet()) {
        continue;
      }
      if (!body1.isEnabled() || !body2.isEnabled()) {
        continue;
      }
      if (body1.isDynamic() && body2.isDynamic()) {
        if (!body1.isBullet() && !body2.isBullet()) {
          continue;
        }
      }
      if (body1.getMass().isInfinite() && body2.getMass().isInfinite()) {
        continue;
      }
      if (body1.isAtRest() && body2.isAtRest()) {
        continue;
      }
      if (!this.isJointCollisionAllowed(body1, body2)) {
        continue;
      }
      if (this.isInContact(body1, body2)) {
        continue;
      }
      boolean allow = true;
      for (TimeOfImpactListener<T> tl : listeners) {
        if (!tl.collision(body1, body2)) {
          allow = false;
        }
      }
      if (!allow) {
        continue;
      }
      if (body1.isDynamic()) {
        List<T> list = pairMapping.get(body1);
        if (list != null) {
          list.add(body2);
        } else {
          list = new ArrayList<T>();
          list.add(body2);
          pairMapping.put(body1, list);
        }
      } else {
        if (body2.isDynamic()) {
          List<T> list = pairMapping.get(body2);
          if (list != null) {
            list.add(body1);
          } else {
            list = new ArrayList<T>();
            list.add(body1);
            pairMapping.put(body2, list);
          }
        }
      }
    }
    boolean solved = false;
    for (T body1 : pairMapping.keySet()) {
      List<T> others = pairMapping.get(body1);
      boolean ss = this.solveTOI(body1, others, listeners);
      solved |= ss;
    }
    return solved;
  }

  /**
	 * Solves the time of impact for the given {@link PhysicsBody}.
	 * <p>
	 * This method will find the first {@link PhysicsBody} that the given {@link PhysicsBody}
	 * collides with unless ignored via the {@link TimeOfImpactListener}.
	 * <p>
	 * If any {@link TimeOfImpactListener} doesn't allow the collision then the collision
	 * is ignored.
	 * <p>
	 * After the first {@link PhysicsBody} is found the two {@link PhysicsBody}s are interpolated
	 * to the time of impact.
	 * <p>
	 * Then the {@link PhysicsBody}s are position solved using the {@link TimeOfImpactSolver}
	 * to force the {@link PhysicsBody}s into collision.  This causes the discrete collision
	 * detector to detect the collision on the next time step.
	 * @param body1 the {@link PhysicsBody}
	 * @param others the other bodies to test against
	 * @param listeners the list of {@link TimeOfImpactListener}s
	 * @return boolean true if a time of impact event was resolved
	 * @since 3.1.0
	 */
  protected boolean solveTOI(T body1, List<T> others, List<TimeOfImpactListener<T>> listeners) {
    int size = others.size();
    double t1 = 0.0;
    double t2 = 1.0;
    TimeOfImpact minToi = null;
    T minBody = null;
    CollisionItemAdapter<T, BodyFixture> reusableItem = new CollisionItemAdapter<T, BodyFixture>();
    for (int i = 0; i < size; i++) {
      T body2 = others.get(i);
      TimeOfImpact toi = new TimeOfImpact();
      int fc1 = body1.getFixtureCount();
      int fc2 = body2.getFixtureCount();
      boolean b2IsStaticAndMultiFixture = body2.isStatic() && fc2 > 1;
      double dt = this.timeStep.getDeltaTime();
      Vector2 v1 = body1.getLinearVelocity().product(dt);
      Vector2 v2 = body2.getLinearVelocity().product(dt);
      double av1 = body1.getAngularVelocity() * dt;
      double av2 = body2.getAngularVelocity() * dt;
      Transform tx1 = body1.getPreviousTransform();
      Transform tx2 = body2.getPreviousTransform();
      for (int k = 0; k < fc2; k++) {
        BodyFixture f2 = body2.getFixture(k);
        if (b2IsStaticAndMultiFixture) {
          AABB b1SweptAABB = this.ccdBroadphase.getAABB(body1);
          reusableItem.set(body2, f2);
          AABB b2StaticAABB = this.broadphaseDetector.getAABB(reusableItem);
          if (!b1SweptAABB.overlaps(b2StaticAABB)) {
            continue;
          }
        }
        if (f2.isSensor()) {
          continue;
        }
        for (int j = 0; j < fc1; j++) {
          BodyFixture f1 = body1.getFixture(j);
          if (f2.isSensor()) {
            continue;
          }
          Filter filter1 = f1.getFilter();
          Filter filter2 = f2.getFilter();
          if (!filter1.isAllowed(filter2)) {
            continue;
          }
          boolean allow = true;
          for (TimeOfImpactListener<T> tl : listeners) {
            if (!tl.collision(body1, f1, body2, f2)) {
              allow = false;
            }
          }
          if (!allow) {
            continue;
          }
          Convex c1 = f1.getShape();
          Convex c2 = f2.getShape();
          if (this.timeOfImpactDetector.getTimeOfImpact(c1, tx1, v1, av1, c2, tx2, v2, av2, t1, t2, toi)) {
            double t = toi.getTime();
            if (t == 0.0) {
              return false;
            }
            if (t < t2) {
              allow = true;
              for (TimeOfImpactListener<T> tl : listeners) {
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
      body1.getPreviousTransform().lerp(body1.getTransform(), t, body1.getTransform());
      if (minBody.isDynamic()) {
        minBody.getPreviousTransform().lerp(minBody.getTransform(), t, minBody.getTransform());
      }
      if (minToi.getSeparation().getDistance() <= 0.0) {
        return true;
      }
      this.timeOfImpactSolver.solve(body1, minBody, minToi, this.settings);
      return true;
    }
    return false;
  }

  private final class WarmStartHandler implements ContactUpdateHandler {
    private ContactCollisionData<T> data;

    private final List<ContactListener<T>> listeners;

    public WarmStartHandler() {
      this.listeners = AbstractPhysicsWorld.this.contactListeners;
    }

    @Override public double getFriction(BodyFixture fixture1, BodyFixture fixture2) {
      return AbstractPhysicsWorld.this.coefficientMixer.mixFriction(fixture1.getFriction(), fixture2.getFriction());
    }

    @Override public double getRestitution(BodyFixture fixture1, BodyFixture fixture2) {
      return AbstractPhysicsWorld.this.coefficientMixer.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution());
    }

    @Override public void begin(Contact contact) {
      int size = this.listeners.size();
      for (int i = 0; i < size; i++) {
        ContactListener<T> listener = this.listeners.get(i);
        listener.begin(this.data, contact);
      }
    }

    @Override public void persist(Contact oldContact, Contact newContact) {
      int size = this.listeners.size();
      for (int i = 0; i < size; i++) {
        ContactListener<T> listener = this.listeners.get(i);
        listener.persist(this.data, oldContact, newContact);
      }
    }

    @Override public void end(Contact contact) {
      int size = this.listeners.size();
      for (int i = 0; i < size; i++) {
        ContactListener<T> listener = this.listeners.get(i);
        listener.end(this.data, contact);
      }
    }
  }

  private final class JointIterator implements Iterator<Joint<T>> {
    /** The current index */
    private int index;

    /** True if the current element has been removed */
    private boolean removed;

    /**
		 * Minimal constructor.
		 */
    public JointIterator() {
      this.index = -1;
      this.removed = false;
    }

    @Override public boolean hasNext() {
      return this.index + 1 < AbstractPhysicsWorld.this.joints.size();
    }

    @Override public Joint<T> next() {
      if (this.index + 1 >= AbstractPhysicsWorld.this.joints.size()) {
        throw new IndexOutOfBoundsException();
      }
      try {
        this.index++;
        this.removed = false;
        Joint<T> joint = AbstractPhysicsWorld.this.joints.get(this.index);
        return joint;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    @Override public void remove() {
      if (this.index < 0 || this.removed) {
        throw new IllegalStateException();
      }
      if (this.index >= AbstractPhysicsWorld.this.joints.size()) {
        throw new IndexOutOfBoundsException();
      }
      try {
        AbstractPhysicsWorld.this.removeJoint(this.index);
        this.index--;
        this.removed = true;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }
  }
}