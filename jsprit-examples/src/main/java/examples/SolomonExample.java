package examples;
import java.io.File;
import java.util.Collection;
import readers.SolomonReader;
import algorithms.VehicleRoutingAlgorithms;
import algorithms.selectors.SelectBest;
import analysis.AlgorithmSearchProgressChartListener;
import analysis.SolutionPlotter;
import analysis.SolutionPrinter;
import analysis.SolutionPrinter.Print;
import basics.VehicleRoutingAlgorithm;
import basics.VehicleRoutingProblem;
import basics.VehicleRoutingProblemSolution;

public class SolomonExample {
  public static void main(String[] args) {
    File dir = new File("output");
    if (!dir.exists()) {
      System.out.println("creating directory ./output");
      boolean result = dir.mkdir();
      if (result) {
        System.out.println("./output created");
      }
    }
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    new SolomonReader(vrpBuilder).read("input/C101_solomon.txt");
    VehicleRoutingProblem vrp = vrpBuilder.build();
    SolutionPlotter.plotVrpAsPNG(vrp, "output/solomon_C101.png", "C101");
    VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/algorithmConfig_solomon.xml");
    vra.setPrematureBreak(100);
    Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
    VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);
    SolutionPrinter.print(solution, Print.VERBOSE);
    SolutionPlotter.plotSolutionAsPNG(vrp, solution, "output/solomon_C101_solution.png", "C101");
  }
}