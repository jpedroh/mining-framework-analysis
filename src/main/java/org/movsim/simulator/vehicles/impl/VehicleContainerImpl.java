package org.movsim.simulator.vehicles.impl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.movsim.simulator.vehicles.Moveable;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class VehicleContainerImpl.
 */
public class VehicleContainerImpl implements VehicleContainer {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(VehicleContainerImpl.class);

  /** The vehicles. */
  private final List<Vehicle> vehicles;

  /** The veh counter. */
  private int vehCounter;

  private final int laneIndex;

  /**
     * Instantiates a new vehicle container impl.
     *
     * @param laneIndex the lane index
     */
  public VehicleContainerImpl(int laneIndex) {
    this.laneIndex = laneIndex;
    vehicles = new ArrayList<Vehicle>();
    vehCounter = 0;
  }

  @Override public int getLaneIndex() {
    return laneIndex;
  }

  @Override public List<Vehicle> getVehicles() {
    return vehicles;
  }

  @Override public Vehicle get(int index) {
    return vehicles.get(index);
  }

  @Override public int size() {
    return vehicles.size();
  }

  @Override public Vehicle getMostDownstream() {
    if (vehicles.isEmpty()) {
      return null;
    }
    return vehicles.get(0);
  }

  @Override public Vehicle getMostUpstream() {
    if (vehicles.isEmpty()) {
      return null;
    }
    return vehicles.get(vehicles.size() - 1);
  }

  @Override public void add(final Vehicle veh, double xInit, double vInit) {
    add(veh, xInit, vInit, laneIndex, false);
  }

  @Override public void add(final Vehicle veh) {
    add(veh, veh.getPosition(), veh.getSpeed(), laneIndex, false);
  }

  @Override public void addFromToRamp(final Vehicle veh, double xInit, double vInit, int oldLane) {
    add(veh, xInit, vInit, laneIndex, false);
    veh.initLaneChangeFromRamp(oldLane);
  }

  /**
     * Adds the.
     *
     * @param veh the veh
     * @param xInit the x init
     * @param vInit the v init
     * @param laneInit the lane init
     */
  private void add(final Vehicle veh, double xInit, double vInit, int laneInit, boolean isTestwise) {
    if (!isTestwise) {
      vehCounter++;
      veh.setVehNumber(vehCounter);
      veh.init(xInit, vInit, laneInit);
    }
    if (vehicles.isEmpty()) {
      vehicles.add(veh);
    } else {
      if (veh.getPosition() < getMostUpstream().getPosition()) {
        vehicles.add(veh);
      } else {
        if (veh.getPosition() > getMostDownstream().getPosition()) {
          vehicles.add(0, veh);
        } else {
          vehicles.add(0, veh);
          sort();
        }
      }
    }
    logger.debug("vehicleContainerImpl: vehicle added: x={}, v={}", veh.getPosition(), veh.getSpeed());
  }

  @Override public void removeVehiclesDownstream(double roadLength) {
    while (!vehicles.isEmpty() && getMostDownstream().getPosition() > roadLength) {
      vehicles.get(0).removeObservers();
      vehicles.remove(0);
      logger.debug(" remove veh ... size = {}", vehicles.size());
    }
  }

  @Override public void removeVehicleMostDownstream() {
    if (!vehicles.isEmpty()) {
      vehicles.remove(0);
    }
  }

  @Override public void removeVehicle(final Vehicle veh) {
    if (!vehicles.isEmpty()) {
      vehicles.remove(veh);
    }
  }

  @Override public Vehicle getLeader(final Moveable veh) {
    final int index = vehicles.indexOf(veh);
    if (index == 0) {
      return null;
    } else {
      if (index == -1) {
        return findVirtualLeader(veh);
      }
    }
    return vehicles.get(index - 1);
  }

  @Override public Vehicle getFollower(final Moveable veh) {
    final int index = vehicles.indexOf(veh);
    if (index == vehicles.size() - 1) {
      return null;
    } else {
      if (index == -1) {
        return findVirtualFollower(veh);
      }
    }
    return vehicles.get(index + 1);
  }

  /**
     * Sort.
     */
  private void sort() {
    Collections.sort(vehicles, new Comparator<Vehicle>() {
      @Override public int compare(Vehicle o1, Vehicle o2) {
        final Double pos1 = new Double((o1).getPosition());
        final Double pos2 = new Double((o2).getPosition());
        return pos2.compareTo(pos1);
      }
    });
  }

  @Override public List<Moveable> getMoveables() {
    List<Moveable> moveables = new ArrayList<Moveable>();
    for (final Vehicle veh : vehicles) {
      moveables.add(veh);
    }
    return moveables;
  }

  @Override public Moveable getMoveable(int index) {
    return vehicles.get(index);
  }

  /**
     * Find virtual leader.
     *
     * @param veh the veh
     * @return the vehicle
     */
  private Vehicle findVirtualLeader(final Moveable veh) {
    final double position = veh.getPosition();
    for (int i = vehicles.size() - 1; i >= 0; i--) {
      final Vehicle vehOnLane = vehicles.get(i);
      if (vehOnLane.getPosition() >= position) {
        return vehOnLane;
      }
    }
    return null;
  }

  /**
     * Find virtual follower.
     *
     * @param veh the veh
     * @return the vehicle
     */
  private Vehicle findVirtualFollower(final Moveable veh) {
    final double position = veh.getPosition();
    for (int i = 0, N = vehicles.size(); i < N; i++) {
      final Vehicle vehOnLane = vehicles.get(i);
      if (vehOnLane.getPosition() <= position) {
        return vehOnLane;
      }
    }
    return null;
  }

  @Override public void addTestwise(final Vehicle veh) {
    if (veh != null) {
      add(veh, veh.getPosition(), veh.getSpeed(), laneIndex, true);
    }
  }
}