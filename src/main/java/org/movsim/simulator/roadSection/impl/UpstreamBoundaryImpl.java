/**
 * Copyright (C) 2010, 2011 by Arne Kesting, Martin Treiber,
 *                             Ralph Germ, Martin Budden
 *                             <info@movsim.org>
 * ----------------------------------------------------------------------
 * 
 *  This file is part of 
 *  
 *  MovSim - the multi-model open-source vehicular-traffic simulator 
 *
 *  MovSim is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MovSim is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MovSim.  If not, see <http://www.gnu.org/licenses/> or
 *  <http://www.movsim.org>.
 *  
 * ----------------------------------------------------------------------
 */
package org.movsim.simulator.roadSection.impl;

import java.io.PrintWriter;
import java.util.List;
import org.movsim.input.model.simulation.UpstreamBoundaryData;
import org.movsim.output.fileoutput.FileUpstreamBoundaryData;
import org.movsim.simulator.Constants;
import org.movsim.simulator.roadSection.InflowTimeSeries;
import org.movsim.simulator.roadSection.UpstreamBoundary;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.simulator.vehicles.VehicleContainer;
import org.movsim.simulator.vehicles.VehicleGenerator;
import org.movsim.simulator.vehicles.VehiclePrototype;
import org.movsim.utilities.impl.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class UpstreamBoundaryImpl.
 */
public class UpstreamBoundaryImpl implements UpstreamBoundary {
    /** The Constant logger. */
    final static Logger logger = LoggerFactory.getLogger(UpstreamBoundaryImpl.class);

    /** The n wait. */
    private double nWait;

    /** The veh generator. */
    private final VehicleGenerator vehGenerator;

    /** The veh container. */
    private final List<VehicleContainer> vehContainers;

    /** The inflow time series. */
    private final InflowTimeSeries inflowTimeSeries;

    /** The entering veh counter. */
    private int enteringVehCounter;

    // status of last merging vehicle for logging to file
    // status of last merging vehicle for logging to file
    /** The x enter last. */
    private double xEnterLast;

    /** The v enter last. */
    private double vEnterLast;

    /** The lane enter last. */
    private int laneEnterLast;

    private FileUpstreamBoundaryData fileUpstreamBoundary;

    /**
     * Instantiates a new upstream boundary impl.
     * 
     * @param vehGenerator
     *            the vehicle generator
     * @param vehContainer
     *            the vehicle container
     * @param upstreamBoundaryData
     *            the upstream boundary data
     * @param projectName
     *            the project name
     */
    public UpstreamBoundaryImpl(VehicleGenerator vehGenerator, List<VehicleContainer> vehContainers, UpstreamBoundaryData upstreamBoundaryData, String projectName) {
        this.vehGenerator = vehGenerator;
        this.vehContainers = vehContainers;
        nWait = 0;
        enteringVehCounter = 1;
        inflowTimeSeries = new InflowTimeSeriesImpl(upstreamBoundaryData.getInflowTimeSeries());
        if (upstreamBoundaryData.withLogging()) {
            fileUpstreamBoundary = new FileUpstreamBoundaryData(projectName);
        }
    }

    private int getNewLaneIndex(int iLane){
        return (iLane==vehContainers.size()-1 ? 0 : iLane+1);
    }

    public double getTotalInflow(double time) {
        // inflow over all lanes
        final double qBC = inflowTimeSeries.getFlowPerLane(time);
        final int nLanes = vehContainers.size();
        return nLanes * qBC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.simulator.roadSection.UpstreamBoundary#update(int,
     * double, double)
     */
    @Override
    public void update(int itime, double dt, double time) {
        // integrate inflow demand
        final double totalInflow = getTotalInflow(time);
        nWait += totalInflow * dt;
        if (nWait >= 1) {
            // try to insert new vehicle at inflow
            // iterate periodically over n lanes
            int iLane = laneEnterLast;
            for (int i = 0, N = vehContainers.size(); i < N; i++) {
                iLane = getNewLaneIndex(iLane);
                final VehicleContainer vehContainerLane = vehContainers.get(iLane);
                // lane index is identical to vehicle's lane number 
                final boolean isEntered = tryEnteringNewVehicle(vehContainerLane, iLane, time, totalInflow);
                if (isEntered) {
                    nWait--;
                    if (fileUpstreamBoundary != null) {
                        fileUpstreamBoundary.update(time, laneEnterLast, xEnterLast, 3.6 * vEnterLast, 3600 * totalInflow, enteringVehCounter, nWait);
                    }
                    return;// only one insert per simulation update

                }
            }
        }
    }

    /**
     * Try entering new vehicle.
     * 
     * @param time
     *            the time
     * @param qBC
     *            the q bc
     * @return true, if successful
     */
    private boolean tryEnteringNewVehicle(final VehicleContainer vehContainer, int lane, double time, double qBC) {

        // type of new vehicle
        final VehiclePrototype vehPrototype = vehGenerator.getVehiclePrototype(); 
        final Vehicle leader = vehContainer.getMostUpstream();

        // (1) empty road
        if (leader == null) {
            enterVehicleOnEmptyRoad(vehContainer, lane, time, vehPrototype);
            return true;
        }
        // (2) check if gap to leader is sufficiently large
        // origin of road section is assumed to be zero
        final double netGapToLeader = leader.getPosition() - leader.getLength();
        double gapAtQMax = 1. / vehPrototype.getRhoQMax();
        if (vehPrototype.getLongModel().modelName().equalsIgnoreCase("")) {
            final double tau = 1;
            gapAtQMax = leader.getSpeed() * tau;
        }
        // minimal distance set to 80% of 1/rho at flow maximum in fundamental
        // diagram
        double minRequiredGap = 0.8 * gapAtQMax;
        if (vehPrototype.getLongModel().isCA()) {
            final double tau = 1;
            minRequiredGap = leader.getSpeed() * tau;
        }
        if (netGapToLeader > minRequiredGap) {
            enterVehicle(vehContainer, lane, time, minRequiredGap, vehPrototype, leader);
            return true;
        }
        // no entering possible
        return false;
    }

    /**
     * Enter vehicle on empty road.
     * 
     * @param time
     *            the time
     * @param vehPrototype
     *            the veh prototype
     */
    private void enterVehicleOnEmptyRoad(final VehicleContainer vehContainer, int lane, double time, VehiclePrototype vehPrototype) {
        final double xEnter = 0;
        final double vEnter = inflowTimeSeries.getSpeed(time);
        addVehicle(vehContainer, lane, vehPrototype, xEnter, vEnter);
    //        logger.debug("add vehicle from upstream boundary to empty road: xEnter={}, vEnter={}", xEnter, vEnter);
    }

    /**
     * Enter vehicle.
     * 
     * @param time
     *            the time
     * @param sFreeMin
     *            the s free min
     * @param vehPrototype
     *            the veh prototype
     * @param leader
     *            the leader
     */
    private void enterVehicle(final VehicleContainer vehContainer, int lane, double time, double sFreeMin, VehiclePrototype vehPrototype, Vehicle leader) {
        final double sFree = leader.getPosition() - leader.getLength();
        final double xLast = leader.getPosition();
        final double vLast = leader.getSpeed();
        final double aLast = leader.getAcc();
        final double speedDefault = inflowTimeSeries.getSpeed(time);
        final double vEnterTest = Math.min(speedDefault, 1.5 * vLast);
        final double lengthLast = leader.getLength();
        final double qBC = inflowTimeSeries.getFlowPerLane(time);
        final double xEnter = Math.min((vEnterTest * nWait) / Math.max(qBC, 0.001), (xLast - sFreeMin) - lengthLast);
        final double rhoEnter = 1.0 / (xLast - xEnter);
        final double vMaxEq = vehPrototype.getEquilibriumSpeed(0.5 * rhoEnter);
        final double bMax = 4;// max. kinematic deceleration at boundary

        final double bEff = Math.max(0.1, bMax + aLast);
        final double vMaxKin = vLast + Math.sqrt((2 * sFree) * bEff);
        final double vEnter = Math.min(Math.min(vEnterTest, vMaxEq), vMaxKin);
        // final int laneEnter = Constants.MOST_RIGHT_LANE;
        addVehicle(vehContainer, lane, vehPrototype, xEnter, vEnter);
//        logger.debug("add vehicle from upstream boundary: xEnter={}, vEnter={}",
//         xEnter, vEnter);
//        System.out.printf("add vehicle from upstream boundary: xLast=%.2f, vLast=%.2f, xEnter=%.2f, vEnter=%.2f, lane=%d, rhoEnter=%.2f, vMaxEq=%.2f, vMaxKin=%.2f %n",
//          xLast, vLast, xEnter, vEnter, lane, rhoEnter, vMaxEq, vMaxKin );
    }

    /**
     * Adds the vehicle.
     * 
     * @param vehPrototype
     *            the veh prototype
     * @param xEnter
     *            the x enter
     * @param vEnter
     *            the v enter
     * @param laneEnter
     *            the lane enter
     */
    private void addVehicle(final VehicleContainer vehContainer, int laneEnter, final VehiclePrototype vehPrototype, double xEnter, double vEnter) {
        final Vehicle veh = vehGenerator.createVehicle(vehPrototype);
        vehContainer.add(veh, xEnter, vEnter, laneEnter);
        // status variables of entering vehicle for logging
        enteringVehCounter++;
        xEnterLast = xEnter;
        vEnterLast = vEnter;
        laneEnterLast = laneEnter;
    }
}