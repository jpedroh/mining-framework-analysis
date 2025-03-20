package algorithms;
import java.util.Collection;
import basics.Job;
import basics.algo.InsertionListener;
import basics.route.VehicleRoute;

/**
 * 
 * @author stefan schroeder
 * 
 */
public interface InsertionStrategy {
  /**
	 * Assigns the unassigned jobs to service-providers
	 * 
	 * @param vehicleRoutes
	 * @param unassignedJobs
	 */
  public void insertJobs(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs);

  public void addListener(InsertionListener insertionListener);

  public void removeListener(InsertionListener insertionListener);

  public Collection<InsertionListener> getListeners();
}