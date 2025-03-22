package org.movsim.simulator.roadnetwork.controller;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.movsim.autogen.TrafficLightStatus;
import org.movsim.network.autogen.opendrive.OpenDRIVE;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Controller;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Signals.Signal;
import org.movsim.simulator.roadnetwork.LaneSegment;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.movsim.simulator.roadnetwork.SignalPoint;
import org.movsim.simulator.roadnetwork.regulator.Regulator;
import org.movsim.simulator.vehicles.Vehicle;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Represents a 'traffic light' to which vehicles will react. The visibility range is limited to {@code MAX_LOOK_AHEAD_DISTANCE}, e.g.
 * {@literal 1000m}, or to two {@link RoadSegment}s.
 * 
 */
public class TrafficLight extends RoadObjectController {
  public static final double MAX_LOOK_AHEAD_DISTANCE = 1000;

  /** The status. */
  private TrafficLightStatus status;

  private final String groupId;

  private TriggerCallback triggerCallback;

  private final Set<TrafficLightStatus> possibleStati = new HashSet<>();

  private final Signal signal;

  private final Controller controller;

  private final String signalType;

  private final SignalPoint signalPointEnd;

  private final Map<RoadSegment, SignalPoint> signalPointsBegin = new HashMap<>();

  public TrafficLight(Signal signal, Controller controller, RoadSegment roadSegment) {
    super(RoadObjectType.TRAFFICLIGHT, signal.getS(), roadSegment);
    if (signal.isSetValidity()) {
      throw new IllegalArgumentException("trafficlights always apply to all lanes, cannot use xodr validity information from signal-id=" + signal.getId());
    }
    this.controller = Preconditions.checkNotNull(controller);
    this.signal = Preconditions.checkNotNull(signal);
    Preconditions.checkArgument(signal.isSetId(), "id not set");
    Preconditions.checkArgument(!signal.getId().isEmpty(), "empty id!");
    Preconditions.checkArgument(signal.isSetS(), "signal.s not set");
    this.signalType = Preconditions.checkNotNull(checkTypesAndExtractSignalType());
    this.groupId = controller.getId();
    signalPointEnd = new SignalPoint(position, roadSegment);
  }

  private String checkTypesAndExtractSignalType() {
    String signalType = null;
    for (OpenDRIVE.Controller.Control control : controller.getControl()) {
      if (!control.isSetType()) {
        throw new IllegalArgumentException("controller.control.type must be set in xodr for signal=" + signalId());
      }
      if (control.getSignalId().equals(signalId())) {
        signalType = control.getType();
      }
    }
    return signalType;
  }

  /**
     * Returns the id. This id is defined in the infrastructure configuration file.
     * 
     * @return the label
     */
  public String signalId() {
    return signal.getId();
  }

  /**
     * Returns the signal Name.
     * 
     * @return the label
     */
  public String signalName() {
    return signal.getName();
  }

  public Signal signal() {
    return signal;
  }

  /**
     * Returns the signal type assigned in the controller.control xodr input. This type links from a 'physical' signal to a 'logical'
     * representation of the trafficlight state.
     * 
     * @return the signal-type id
     */
  public String signalType() {
    return signalType;
  }

  public String controllerId() {
    return controller.getId();
  }

  public Controller getController() {
    return controller;
  }

  /**
     * Returns the controllergroup-id which allows for a unique reference to a set of signals in the infrastructure.
     * 
     * @return groupId
     */
  public String groupId() {
    return groupId;
  }

  public TrafficLightStatus status() {
    return status;
  }

  /**
     * sets the {@link TrafficLightStatus}.
     * 
     * <p>
     * The user has to assure that a single traffic light is not simultaneously controlled by the {@link TrafficLightController} and one (or
     * more) {@link Regulator}(s).
     * </p>
     * 
     * @param newStatus
     */
  public void setState(TrafficLightStatus newStatus) {
    this.status = newStatus;
  }

  public void triggerNextPhase() {
    if (hasTriggerCallback()) {
      LOG.debug("mouse click triggers next phase");
      triggerCallback.nextPhase();
    }
  }

  public void setTriggerCallback(TriggerCallback triggerCallback) {
    this.triggerCallback = Preconditions.checkNotNull(triggerCallback);
  }

  public boolean hasTriggerCallback() {
    return triggerCallback != null;
  }

  @Deprecated void addPossibleState(TrafficLightStatus status) {
    possibleStati.add(status);
  }

  /**
     * Return the number of lights this traffic light has, can be 1, 2 or 3.
     * 
     * @return
     */
  @Deprecated public static int lightCount() {
    return 3;
  }

  @Override public String toString() {
    return "TrafficLight [controllerId = " + controllerId() + ", signalId = " + signalId() + ", status=" + status + ", position=" + position + ", signalType=" + signalType + ", groupId = " + groupId + ", roadSegment.id=" + ((roadSegment == null) ? "null" : roadSegment.userId()) + "]";
  }

  @Override public void createSignalPositions() {
    roadSegment.signalPoints().add(signalPointEnd);
    LOG.info("trafficlight={}", this);
    LOG.info("trafficlight end signal point placed at position={} on roadSegment={}.", position, roadSegment);
    double upstreamPosition = position - MAX_LOOK_AHEAD_DISTANCE;
    if (upstreamPosition >= 0 || !roadSegment.hasUpstreamConnection()) {
      upstreamPosition = Math.max(0, upstreamPosition);
      addSignalPointBegin(upstreamPosition, roadSegment);
    } else {
      Set<RoadSegment> visitedRoadSegments = Sets.newHashSet();
      double dxToGo = MAX_LOOK_AHEAD_DISTANCE - position;
      addSignalPointsToUpstreamRoadSegments(roadSegment, dxToGo, visitedRoadSegments);
    }
    if (signalPointsBegin.isEmpty()) {
      throw new IllegalStateException("did not set any upstream signal points for traffic light=" + toString());
    }
    for (Entry<RoadSegment, SignalPoint> entry : signalPointsBegin.entrySet()) {
      entry.getKey().signalPoints().add(entry.getValue());
    }
  }

  private void addSignalPointBegin(double upstreamPosition, RoadSegment upstreamRoadSegment) {
    SignalPoint signalPoint = new SignalPoint(upstreamPosition, upstreamRoadSegment);
    signalPointsBegin.put(upstreamRoadSegment, signalPoint);
    LOG.info("trafficlight signal start point placed at position={} on roadSegment={}", upstreamPosition, upstreamRoadSegment);
  }

  private void addSignalPointsToUpstreamRoadSegments(RoadSegment startRoadSegment, double dxToGo, Set<RoadSegment> visitedRoadSegments) {
    for (LaneSegment laneSegment : startRoadSegment.laneSegments()) {
      if (laneSegment.hasSourceLaneSegment()) {
        RoadSegment upstreamRoadSegment = laneSegment.sourceLaneSegment().roadSegment();
        if (!visitedRoadSegments.contains(upstreamRoadSegment)) {
          visitedRoadSegments.add(upstreamRoadSegment);
          double posSignalPoint = upstreamRoadSegment.roadLength() - dxToGo;
          if (upstreamRoadSegment.getSizeSinkRoadSegments() > 1) {
            addSignalPointBegin(Math.max(0, posSignalPoint), startRoadSegment);
          } else {
            if (posSignalPoint >= 0 || !upstreamRoadSegment.hasUpstreamConnection()) {
              addSignalPointBegin(Math.max(0, posSignalPoint), upstreamRoadSegment);
            } else {
              addSignalPointsToUpstreamRoadSegments(upstreamRoadSegment, -posSignalPoint, visitedRoadSegments);
            }
          }
        }
      }
    }
  }

  @Override public void timeStep(double dt, double simulationTime, long iterationCount) {
    for (SignalPoint signalPoint : signalPointsBegin.values()) {
      for (Vehicle vehicle : signalPoint.passedVehicles()) {
        vehicle.getTrafficLightApproaching().addTrafficLight(this);
        LOG.debug("vehicle pos={} --> set trafficlight={}", vehicle.getFrontPosition(), this);
      }
    }
  }
}