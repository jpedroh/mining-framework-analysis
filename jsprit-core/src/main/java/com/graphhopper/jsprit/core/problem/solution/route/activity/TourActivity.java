package com.graphhopper.jsprit.core.problem.solution.route.activity;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.HasIndex;
import com.graphhopper.jsprit.core.problem.Location;

/**
 * Basic interface for tour-activities.
 * <p>
 * <p>A tour activity is the basic element of a tour, which is consequently a sequence of tour-activities.
 *
 * @author schroeder
 */
public interface TourActivity extends HasIndex {
  public void setTheoreticalEarliestOperationStartTime(double earliest);

  public void setTheoreticalLatestOperationStartTime(double latest);

  /**
     * Returns the name of this activity.
     *
     * @return name
     */
  public abstract String getName();

  /**
     * Returns location.
     *
     * @return location
     */
  public abstract Location getLocation();

  /**
     * Returns the theoretical earliest operation start time, which is the time that is just allowed
     * (not earlier) to start this activity, that is for example <code>service.getTimeWindow().getStart()</code>.
     *
     * @return earliest start time
     */
  public abstract double getTheoreticalEarliestOperationStartTime();

  /**
     * Returns the theoretical latest operation start time, which is the time that is just allowed
     * (not later) to start this activity, that is for example <code>service.getTimeWindow().getEnd()</code>.
     *
     * @return latest start time
     */
  public abstract double getTheoreticalLatestOperationStartTime();

  /**
     * Returns the operation-time this activity takes.
     * <p>
     * <p>Note that this is not necessarily the duration of this activity, but the
     * service time a pickup/delivery actually takes, that is for example <code>service.getServiceTime()</code>.
     *
     * @return operation time
     */
  public abstract double getOperationTime();

  /**
     * Returns the arrival-time of this activity.
     *
     * @return arrival time
     */
  public abstract double getArrTime();

  /**
     * Returns end-time of this activity.
     *
     * @return end time
     */
  public abstract double getEndTime();

  /**
     * Sets the arrival time of that activity.
     *
     * @param arrTime
     */
  public abstract void setArrTime(double arrTime);

  /**
     * Sets the end-time of this activity.
     *
     * @param endTime
     */
  public abstract void setEndTime(double endTime);

  /**
     * Returns the capacity-demand of that activity, in terms of what needs to be loaded or unloaded at
     * this activity.
     *
     * @return capacity
     */
  public abstract SizeDimension getSize();

  /**
     * Makes a deep copy of this activity.
     *
     * @return copied activity
     */
  public abstract TourActivity duplicate();
}