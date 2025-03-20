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
package com.graphhopper.jsprit.core.problem.job;

import com.graphhopper.jsprit.core.problem.solution.route.activity.ServiceActivity;

/**
 * Service implementation of a job.
 * <p>
 * <p>
 * <p>Note that two services are equal if they have the same id.
 *
 * @author schroeder
 */
public class Service extends AbstractSingleActivityJob<ServiceActivity> {


    public static final class Builder
    extends AbstractSingleActivityJob.BuilderBase<Service, Builder> {

        public Builder(String id) {
            super(id);
            setType("pickup");
        }

        public static Builder newInstance(String id) {
            return new Builder(id);
        }

<<<<<<< /usr/src/app/output/jsprit/jsprit/9fe0b3315e0376479ad9be196dc988ad75af06ac/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/job/Service.java/left.java
        @Override
        protected Service createInstance() {
            return new Service(this);
||||||| /usr/src/app/output/jsprit/jsprit/9fe0b3315e0376479ad9be196dc988ad75af06ac/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/job/Service.java/base.java
        private String id;

        protected String locationId;

        private String type = "service";

        protected Coordinate coord;

        protected double serviceTime;

        protected TimeWindow timeWindow = TimeWindow.newInstance(0.0, Double.MAX_VALUE);

        protected Capacity.Builder capacityBuilder = Capacity.Builder.newInstance();

        protected Capacity capacity;

        protected Skills.Builder skillBuilder = Skills.Builder.newInstance();

        protected Skills skills;

        private String name = "no-name";

        protected Location location;

        protected TimeWindowsImpl timeWindows;

    		private boolean twAdded = false;

        private int priority = 2;

    		Builder(String id){
    			this.id = id;
    			timeWindows = new TimeWindowsImpl();
    			timeWindows.add(timeWindow);
    		}

        /**
         * Protected method to set the type-name of the service.
         * <p>
         * <p>Currently there are {@link Service}, {@link Pickup} and {@link Delivery}.
         *
         * @param name the name of service
         * @return the builder
         */
        protected Builder<T> setType(String name) {
            this.type = name;
            return this;
        }

        /**
         * Sets location
         *
         * @param location location
         * @return builder
         */
        public Builder<T> setLocation(Location location) {
            this.location = location;
            return this;
        }

        /**
         * Sets the serviceTime of this service.
         * <p>
         * <p>It is understood as time that a service or its implied activity takes at the service-location, for instance
         * to unload goods.
         *
         * @param serviceTime the service time / duration of service to be set
         * @return builder
         * @throws IllegalArgumentException if serviceTime < 0
         */
        public Builder<T> setServiceTime(double serviceTime) {
            if (serviceTime < 0)
                throw new IllegalArgumentException("serviceTime must be greater than or equal to zero");
            this.serviceTime = serviceTime;
            return this;
        }

        /**
         * Adds capacity dimension.
         *
         * @param dimensionIndex the dimension index of the capacity value
         * @param dimensionValue the capacity value
         * @return the builder
         * @throws IllegalArgumentException if dimensionValue < 0
         */
        public Builder<T> addSizeDimension(int dimensionIndex, int dimensionValue) {
            if (dimensionValue < 0) throw new IllegalArgumentException("capacity value cannot be negative");
            capacityBuilder.addDimension(dimensionIndex, dimensionValue);
            return this;
        }

        public Builder<T> setTimeWindow(TimeWindow tw){
            if(tw == null) throw new IllegalArgumentException("time-window arg must not be null");
            this.timeWindow = tw;
            this.timeWindows = new TimeWindowsImpl();
            timeWindows.add(tw);
            return this;
        }

        public Builder<T> addTimeWindow(TimeWindow timeWindow) {
            if(timeWindow == null) throw new IllegalArgumentException("time-window arg must not be null");
            if(!twAdded){
                timeWindows = new TimeWindowsImpl();
                twAdded = true;
            }
            timeWindows.add(timeWindow);
            return this;
        }

        public Builder<T> addTimeWindow(double earliest, double latest) {
            return addTimeWindow(TimeWindow.newInstance(earliest, latest));
        }

        /**
         * Builds the service.
         *
         * @return {@link Service}
         * @throws IllegalArgumentException if neither locationId nor coordinate is set.
         */
        public T build() {
            if (location == null) throw new IllegalArgumentException("location is missing");
            this.setType("service");
            capacity = capacityBuilder.build();
            skills = skillBuilder.build();
            return (T) new Service(this);
        }

        public Builder<T> addRequiredSkill(String skill) {
            skillBuilder.addSkill(skill);
            return this;
        }

        public Builder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> addAllRequiredSkills(Skills skills){
            for(String s : skills.values()){
                skillBuilder.addSkill(s);
            }
            return this;
        }

        public Builder<T> addAllSizeDimensions(Capacity size){
            for(int i=0;i<size.getNuOfDimensions();i++){
                capacityBuilder.addDimension(i,size.get(i));
            }
            return this;
        }

        /**
         * Set priority to service. Only 1 = high priority, 2 = medium and 3 = low are allowed.
         * <p>
         * Default is 2 = medium.
         *
         * @param priority
         * @return builder
         */
        public Builder<T> setPriority(int priority) {
            if(priority < 1 || priority > 3) throw new IllegalArgumentException("incorrect priority. only 1 = high, 2 = medium and 3 = low is allowed");
            this.priority = priority;
            return this;
=======
        private String id;

        protected String locationId;

        private String type = "service";

        protected Coordinate coord;

        protected double serviceTime;

        protected TimeWindow timeWindow = TimeWindow.newInstance(0.0, Double.MAX_VALUE);

        protected Capacity.Builder capacityBuilder = Capacity.Builder.newInstance();

        protected Capacity capacity;

        protected Skills.Builder skillBuilder = Skills.Builder.newInstance();

        protected Skills skills;

        private String name = "no-name";

        protected Location location;

        protected TimeWindowsImpl timeWindows;

    		private boolean twAdded = false;

        private int priority = 2;

    		Builder(String id){
    			this.id = id;
    			timeWindows = new TimeWindowsImpl();
    			timeWindows.add(timeWindow);
    		}

        /**
         * Protected method to set the type-name of the service.
         * <p>
         * <p>Currently there are {@link Service}, {@link Pickup} and {@link Delivery}.
         *
         * @param name the name of service
         * @return the builder
         */
        protected Builder<T> setType(String name) {
            this.type = name;
            return this;
        }

        /**
         * Sets location
         *
         * @param location location
         * @return builder
         */
        public Builder<T> setLocation(Location location) {
            this.location = location;
            return this;
        }

        /**
         * Sets the serviceTime of this service.
         * <p>
         * <p>It is understood as time that a service or its implied activity takes at the service-location, for instance
         * to unload goods.
         *
         * @param serviceTime the service time / duration of service to be set
         * @return builder
         * @throws IllegalArgumentException if serviceTime < 0
         */
        public Builder<T> setServiceTime(double serviceTime) {
            if (serviceTime < 0)
                throw new IllegalArgumentException("serviceTime must be greater than or equal to zero");
            this.serviceTime = serviceTime;
            return this;
        }

        /**
         * Adds capacity dimension.
         *
         * @param dimensionIndex the dimension index of the capacity value
         * @param dimensionValue the capacity value
         * @return the builder
         * @throws IllegalArgumentException if dimensionValue < 0
         */
        public Builder<T> addSizeDimension(int dimensionIndex, int dimensionValue) {
            if (dimensionValue < 0) throw new IllegalArgumentException("capacity value cannot be negative");
            capacityBuilder.addDimension(dimensionIndex, dimensionValue);
            return this;
        }

        public Builder<T> setTimeWindow(TimeWindow tw){
            if(tw == null) throw new IllegalArgumentException("time-window arg must not be null");
            this.timeWindow = tw;
            this.timeWindows = new TimeWindowsImpl();
            timeWindows.add(tw);
            return this;
        }

        public Builder<T> addTimeWindow(TimeWindow timeWindow) {
            if(timeWindow == null) throw new IllegalArgumentException("time-window arg must not be null");
            if(!twAdded){
                timeWindows = new TimeWindowsImpl();
                twAdded = true;
            }
            timeWindows.add(timeWindow);
            return this;
        }

        public Builder<T> addTimeWindow(double earliest, double latest) {
            return addTimeWindow(TimeWindow.newInstance(earliest, latest));
        }

        /**
         * Builds the service.
         *
         * @return {@link Service}
         * @throws IllegalArgumentException if neither locationId nor coordinate is set.
         */
        public T build() {
            if (location == null) throw new IllegalArgumentException("location is missing");
            this.setType("service");
            capacity = capacityBuilder.build();
            skills = skillBuilder.build();
            return (T) new Service(this);
        }

        public Builder<T> addRequiredSkill(String skill) {
            skillBuilder.addSkill(skill);
            return this;
        }

        public Builder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> addAllRequiredSkills(Skills skills){
            for(String s : skills.values()){
                skillBuilder.addSkill(s);
            }
            return this;
        }

        public Builder<T> addAllSizeDimensions(Capacity size){
            for(int i=0;i<size.getNuOfDimensions();i++){
                capacityBuilder.addDimension(i,size.get(i));
            }
            return this;
        }

        /**
         * Set priority to service. Only 1 (very high) to 10 (very low) are allowed.
         * <p>
         * Default is 2.
         *
         * @param priority
         * @return builder
         */
        public Builder<T> setPriority(int priority) {
            if (priority < 1 || priority > 10)
                throw new IllegalArgumentException("incorrect priority. only priority values from 1 to 10 are allowed where 1 = high and 10 is low");
            this.priority = priority;
            return this;
>>>>>>> /usr/src/app/output/jsprit/jsprit/9fe0b3315e0376479ad9be196dc988ad75af06ac/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/job/Service.java/right.java
        }
    }

    Service(Builder builder) {
        super(builder);
    }

    @Override
    protected ServiceActivity createActivity(
                    AbstractSingleActivityJob.BuilderBase<? extends AbstractSingleActivityJob<?>, ?> builder) {
        return new ServiceActivity(this, builder.type,
                        builder.location, builder.serviceTime, builder.getCapacity(),
                        builder.timeWindows.getTimeWindows());
        // return new PickupActivityNEW(this, builder.type, builder.location,
        // builder.serviceTime,
        // builder.getCapacity(), builder.timeWindows.getTimeWindows());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder getBuilder(String id) {
        return Builder.newInstance(id);
    }

}
