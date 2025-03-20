package algorithms;
import java.util.Collection;
import basics.Job;
import basics.algo.JobInsertedListener;
import basics.algo.RuinListener;
import basics.costs.VehicleRoutingActivityCosts;
import basics.costs.VehicleRoutingTransportCosts;
import basics.route.VehicleRoute;

class StateUpdates {
  static class UpdateStates implements JobInsertedListener, RuinListener {
    private RouteActivityVisitor routeActivityVisitor;

    private ReverseRouteActivityVisitor revRouteActivityVisitor;

    public UpdateStates(StateManagerImpl states, VehicleRoutingTransportCosts routingCosts, VehicleRoutingActivityCosts activityCosts) {
      routeActivityVisitor = new RouteActivityVisitor();
      routeActivityVisitor.addActivityVisitor(new UpdateActivityTimes(routingCosts));
      routeActivityVisitor.addActivityVisitor(new UpdateCostsAtAllLevels(activityCosts, routingCosts, states));
      routeActivityVisitor.addActivityVisitor(new UpdateLoadAtAllLevels(states));
      revRouteActivityVisitor = new ReverseRouteActivityVisitor();
      revRouteActivityVisitor.addActivityVisitor(new UpdateLatestOperationStartTimeAtActLocations(states, routingCosts));
    }

    public void update(VehicleRoute route) {
      routeActivityVisitor.visit(route);
      revRouteActivityVisitor.visit(route);
    }

    @Override public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
      routeActivityVisitor.visit(inRoute);
      revRouteActivityVisitor.visit(inRoute);
    }

    @Override public void ruinStarts(Collection<VehicleRoute> routes) {
    }

    @Override public void ruinEnds(Collection<VehicleRoute> routes, Collection<Job> unassignedJobs) {
      for (VehicleRoute route : routes) {
        routeActivityVisitor.visit(route);
        revRouteActivityVisitor.visit(route);
      }
    }

    @Override public void removed(Job job, VehicleRoute fromRoute) {
    }
  }
}