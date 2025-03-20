package jsprit.core.problem.vehicle;
import jsprit.core.problem.Capacity;
import jsprit.core.problem.vehicle.VehicleTypeImpl.VehicleCostParams;

public interface VehicleType {
  public String getTypeId();

  public int getCapacity();

  public Capacity getCapacityDimensions();

  public double getMaxVelocity();

  public VehicleCostParams getVehicleCostParams();
}