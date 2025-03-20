package algorithms;
import basics.VehicleRoutingProblem;
import basics.VehicleRoutingProblemSolution;

public interface InitialSolutionFactory {
  public VehicleRoutingProblemSolution createSolution(VehicleRoutingProblem vrp);
}