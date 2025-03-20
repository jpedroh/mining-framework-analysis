package algorithms;
import org.junit.Test;
import basics.Service;
import basics.costs.VehicleRoutingTransportCosts;
import basics.route.Driver;
import basics.route.Vehicle;

public class TestJobDistanceAvgCosts {
  public static void main(String[] args) {
    VehicleRoutingTransportCosts costs = new VehicleRoutingTransportCosts() {
      @Override public double getBackwardTransportTime(String fromId, String toId, double arrivalTime, Driver driver, Vehicle vehicle) {
        return 0;
      }

      @Override public double getBackwardTransportCost(String fromId, String toId, double arrivalTime, Driver driver, Vehicle vehicle) {
        return 0;
      }

      @Override public double getTransportCost(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
        String vehicleId = vehicle.getId();
        return 0;
      }

      @Override public double getTransportTime(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
        return 0;
      }
    };
    JobDistanceAvgCosts c = new JobDistanceAvgCosts(costs);
    c.getDistance(Service.Builder.newInstance("1", 1).setLocationId("foo").build(), Service.Builder.newInstance("2", 2).setLocationId("foo").build());
  }

  @Test(expected = NullPointerException.class) public void whenVehicleAndDriverIsNull_And_CostsDoesNotProvideAMethodForThis_throwException() {
    VehicleRoutingTransportCosts costs = new VehicleRoutingTransportCosts() {
      @Override public double getBackwardTransportTime(String fromId, String toId, double arrivalTime, Driver driver, Vehicle vehicle) {
        return 0;
      }

      @Override public double getBackwardTransportCost(String fromId, String toId, double arrivalTime, Driver driver, Vehicle vehicle) {
        return 0;
      }

      @Override public double getTransportCost(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
        String vehicleId = vehicle.getId();
        return 0;
      }

      @Override public double getTransportTime(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
        return 0;
      }
    };
    JobDistanceAvgCosts c = new JobDistanceAvgCosts(costs);
    c.getDistance(Service.Builder.newInstance("1", 1).setLocationId("loc").build(), Service.Builder.newInstance("2", 2).setLocationId("loc").build());
  }
}