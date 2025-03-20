package jsprit.core.problem.vehicle;
import jsprit.core.problem.HasId;
import jsprit.core.problem.HasIndex;
import jsprit.core.problem.Location;
import jsprit.core.problem.Skills;
import jsprit.core.problem.job.Break;

/**
 * Basic interface for vehicle-data.
 * 
 * @author schroeder
 *
 */
public interface Vehicle extends HasId, HasIndex {
  /**
	 * Returns the earliest departure of vehicle which should be the lower bound of this vehicle's departure times. 
	 * 
	 * @return earliest departure time
	 */
  public abstract double getEarliestDeparture();

  /**
	 * Returns the latest arrival time at this vehicle's end-location which should be the upper bound of this vehicle's arrival times at end-location.
	 * 
	 * @return latest arrival time of this vehicle
	 * 
	 */
  public abstract double getLatestArrival();

  /**
	 * Returns the {@link VehicleType} of this vehicle.
	 * 
	 * @return {@link VehicleType} of this vehicle
	 */
  public abstract VehicleType getType();

  /**
	 * Returns the id of this vehicle.
	 * 
	 * @return id
	 */
  public abstract String getId();

  /**
	 * Returns true if vehicle returns to depot, false otherwise.
	 * 
	 * @return true if isReturnToDepot
	 */
  public abstract boolean isReturnToDepot();

  public abstract Location getStartLocation();

  public abstract Location getEndLocation();

  public abstract VehicleTypeKey getVehicleTypeIdentifier();

  public abstract Skills getSkills();

  public abstract Break getBreak();
}