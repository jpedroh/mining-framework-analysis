package jsprit.core.problem.job;
import jsprit.core.problem.Capacity;
import jsprit.core.problem.HasId;
import jsprit.core.problem.Skills;
import jsprit.core.problem.HasIndex;

/**
 * Basic interface for all jobs.
 * 
 * @author schroeder
 *
 */
public interface Job extends HasId, HasIndex {
  /**
	 * Returns the unique identifier (id) of a job.
	 * 
	 * @return id
	 */
  public String getId();

  /**
	 * Returns size, i.e. capacity-demand, of this job which can consist of an arbitrary number of capacity dimensions.
	 * 
	 * @return Capacity
	 */
  public Capacity getSize();

  /**
     * Returns required skills.
     *
     * @return
     */
  public Skills getRequiredSkills();
}