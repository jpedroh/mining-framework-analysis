package jsprit.core.problem.vehicle;
import java.util.Collection;

/**
 * Factory that creates an infinite fleetmanager.
 *
 * @author schroeder
 */
public class InfiniteFleetManagerFactory implements VehicleFleetManagerFactory {
  private Collection<Vehicle> vehicles;

  /**
	 * Constructs the factory.
	 *
	 * @param vehicles
	 */
  public InfiniteFleetManagerFactory(Collection<Vehicle> vehicles) {
    super();
    this.vehicles = vehicles;
  }

  /**
	 * Creates the infinite fleetmanager.
	 */
  @Override public VehicleFleetManager createFleetManager() {
    return new InfiniteVehicles(vehicles);
  }
}