package jsprit.core.problem.vehicle;
import java.util.Collection;

/**
 * Factory that creates a finite fleetmanager.
 *
 * @author schroeder
 */
public class FiniteFleetManagerFactory implements VehicleFleetManagerFactory {
  private Collection<Vehicle> vehicles;

  /**
	 * Constucts the factory.
	 *
	 * @param vehicles
	 */
  public FiniteFleetManagerFactory(Collection<Vehicle> vehicles) {
    super();
    this.vehicles = vehicles;
  }

  /**
	 * Creates the finite fleetmanager.
	 */
  @Override public VehicleFleetManager createFleetManager() {
    return new VehicleFleetManagerImpl(vehicles);
  }
}