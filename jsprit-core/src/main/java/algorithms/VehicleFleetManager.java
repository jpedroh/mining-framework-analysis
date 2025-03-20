package algorithms;
import java.util.Collection;
import basics.route.Vehicle;

public interface VehicleFleetManager {
  public abstract void lock(Vehicle vehicle);

  public abstract void unlock(Vehicle vehicle);

  public abstract boolean isLocked(Vehicle vehicle);

  public abstract void unlockAll();

  public abstract Collection<Vehicle> getAvailableVehicles();

  public Collection<Vehicle> getAvailableVehicles(String withoutThisType, String locationId);
}