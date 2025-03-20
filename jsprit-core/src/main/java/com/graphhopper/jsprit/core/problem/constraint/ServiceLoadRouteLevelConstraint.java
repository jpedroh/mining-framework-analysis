/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.core.problem.constraint;

import com.graphhopper.jsprit.core.algorithm.state.InternalStates;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.AbstractJob;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;

/**
 * Ensures that capacity constraint is met, i.e. that current load plus
 * new job size does not exceeds capacity of new vehicle.
 * <p>
 * <p>If job is neither Pickup, Delivery nor Service, it returns true.
 *
 * @author stefan
 */
public class ServiceLoadRouteLevelConstraint implements HardRouteConstraint {

    private RouteAndActivityStateGetter stateManager;

<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
    private SizeDimension defaultValue;

||||||| /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/base.java
    private Capacity defaultValue;

=======
>>>>>>> /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/right.java
    public ServiceLoadRouteLevelConstraint(RouteAndActivityStateGetter stateManager) {
        super();
        this.stateManager = stateManager;
<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
        defaultValue = SizeDimension.Builder.newInstance().build();
||||||| /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/base.java
        defaultValue = Capacity.Builder.newInstance().build();
=======
>>>>>>> /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/right.java
    }

    @Override
    public boolean fulfilled(JobInsertionContext insertionContext) {
<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
        SizeDimension maxLoadAtRoute = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.MAXLOAD, SizeDimension.class);
        if (maxLoadAtRoute == null) {
            maxLoadAtRoute = defaultValue;
        }
        SizeDimension capacityDimensions = insertionContext.getNewVehicle().getType().getCapacityDimensions();
        if (!maxLoadAtRoute.isLessOrEqual(capacityDimensions)) {
||||||| /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/base.java
        Capacity maxLoadAtRoute = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.MAXLOAD, Capacity.class);
        if (maxLoadAtRoute == null) {
            maxLoadAtRoute = defaultValue;
        }
        Capacity capacityDimensions = insertionContext.getNewVehicle().getType().getCapacityDimensions();
        if (!maxLoadAtRoute.isLessOrEqual(capacityDimensions)) {
=======
        Capacity maxLoadAtRoute = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.MAXLOAD, Capacity.class);
        maxLoadAtRoute = (maxLoadAtRoute != null) ? maxLoadAtRoute : Capacity.EMPTY;
        Capacity capacityOfNewVehicle = insertionContext.getNewVehicle().getType().getCapacityDimensions();
        if (!maxLoadAtRoute.isLessOrEqual(capacityOfNewVehicle)) {
>>>>>>> /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/right.java
            return false;
        }
<<<<<<< /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/left.java
        if (insertionContext.getJob() instanceof Delivery) {
            SizeDimension loadAtDepot = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_BEGINNING, SizeDimension.class);
            if (loadAtDepot == null) {
                loadAtDepot = defaultValue;
            }
            if (!loadAtDepot.add(insertionContext.getJob().getSize())
                            .isLessOrEqual(capacityDimensions)) {
                return false;
            }
        } else if (insertionContext.getJob() instanceof Pickup || insertionContext.getJob() instanceof Service) {
            SizeDimension loadAtEnd = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_END, SizeDimension.class);
            if (loadAtEnd == null) {
                loadAtEnd = defaultValue;
            }
            if (!loadAtEnd.add(insertionContext.getJob().getSize())
                            .isLessOrEqual(capacityDimensions)) {
                return false;
            }
||||||| /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/base.java
        if (insertionContext.getJob() instanceof Delivery) {
            Capacity loadAtDepot = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_BEGINNING, Capacity.class);
            if (loadAtDepot == null) {
                loadAtDepot = defaultValue;
            }
            if (!loadAtDepot.add(insertionContext.getJob().getSize())
                            .isLessOrEqual(capacityDimensions)) {
                return false;
            }
        } else if (insertionContext.getJob() instanceof Pickup || insertionContext.getJob() instanceof Service) {
            Capacity loadAtEnd = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_END, Capacity.class);
            if (loadAtEnd == null) {
                loadAtEnd = defaultValue;
            }
            if (!loadAtEnd.add(insertionContext.getJob().getSize())
                            .isLessOrEqual(capacityDimensions)) {
                return false;
            }
=======
        AbstractJob job = (AbstractJob) insertionContext.getJob();
        Capacity loadAtDepot = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_BEGINNING, Capacity.class);
        loadAtDepot = (loadAtDepot != null) ? loadAtDepot : Capacity.EMPTY;
        if (!(loadAtDepot.add(job.getSizeAtStart()).isLessOrEqual(capacityOfNewVehicle))) {
            return false;
        }
        Capacity loadAtEnd = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_END, Capacity.class);
        loadAtEnd = (loadAtEnd != null) ? loadAtEnd : Capacity.EMPTY;
        if (!(loadAtEnd.add(job.getSizeAtEnd()).isLessOrEqual(capacityOfNewVehicle))) {
            return false;
>>>>>>> /usr/src/app/output/jsprit/jsprit/bf597eb8ec5d4fe4917d750f18d7339c90890435/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/constraint/ServiceLoadRouteLevelConstraint.java/right.java
        }
        return true;

//        if(loadAtDepot.add())
//        if(insertionContext.getJob().getInitialPickup() < 0){
//
//        }
//        else {
//
//        }
//        if (insertionContext.getJob() instanceof Delivery) {
//            Capacity loadAtDepot = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_BEGINNING, Capacity.class);
//            if (loadAtDepot == null) {
//                loadAtDepot = Capacity.EMPTY;
//            }
//            if (!loadAtDepot.add(insertionContext.getJob().getSize())
//                .isLessOrEqual(capacityOfNewVehicle)) {
//                return false;
//            }
//        } else if (insertionContext.getJob() instanceof Pickup || insertionContext.getJob() instanceof Service) {
//            Capacity loadAtEnd = stateManager.getRouteState(insertionContext.getRoute(), InternalStates.LOAD_AT_END, Capacity.class);
//            if (loadAtEnd == null) {
//                loadAtEnd = Capacity.EMPTY;
//            }
//            if (!loadAtEnd.add(insertionContext.getJob().getSize())
//                .isLessOrEqual(capacityOfNewVehicle)) {
//                return false;
//            }
//        }
//        return true;
    }

}
