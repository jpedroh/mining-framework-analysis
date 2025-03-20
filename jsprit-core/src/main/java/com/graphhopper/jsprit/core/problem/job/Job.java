package com.graphhopper.jsprit.core.problem.job;
import com.graphhopper.jsprit.core.problem.*;
import java.util.List;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import java.util.Set;
import com.graphhopper.jsprit.core.problem.SizeDimension;

/**
 * Basic interface for all jobs.
 *
 * @author schroeder
 */
public interface Job extends HasId, HasIndex {
  /**
     * Returns the unique identifier (id) of a job.
     *
     * @return id
     */
  @Override public String getId();

  /**
     * Returns size, i.e. capacity-demand, of this job which can consist of an
     * arbitrary number of capacity dimensions.
     *
     * @return SizeDimension
     */
  public SizeDimension getSize();

  public Skills getRequiredSkills();

  /**
     * Returns name.
     *
     * @return name
     */
  public String getName();

  /**
     * Get priority of job. Only 1 = high priority, 2 = medium and 3 = low are
     * allowed.
     * <p>
     * Default is 2 = medium.
     *
     * @return priority
     */
  public int getPriority();

  /**
     * @return All involved locations
     */
  public List<Location> getAllLocations();

  /**
     * @return All activities
     */
  public JobActivityList getActivityList();

  /**
     * @return All operation time windows
     */
  public Set<TimeWindow> getTimeWindows();
}