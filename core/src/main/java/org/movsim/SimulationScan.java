package org.movsim;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.xml.bind.JAXBException;
import org.movsim.autogen.Movsim;
import org.movsim.simulator.Simulator;
import org.xml.sax.SAXException;

public final class SimulationScan {
  private SimulationScan() {
    throw new IllegalStateException("do not instanciate");
  }

  public static void invokeSimulationScan(final Movsim inputData) throws JAXBException, SAXException {
    int uncertaintyMin = 0;
    int uncertaintyMax = 20;
    int uncertaintyStep = 2;
    int fractionMin = 0;
    int fractionMax = 100;
    int fractionStep = 10;
    StringBuilder sb = new StringBuilder();
    for (int fraction = fractionMin; fraction <= fractionMax; fraction = fraction + fractionStep) {
      for (int uncertainty = uncertaintyMin; uncertainty <= uncertaintyMax; uncertainty = uncertainty + uncertaintyStep) {
        inputData.getScenario().getSimulation().getTrafficComposition().getVehicleType().get(0).setFraction(fraction / 100.0);
        inputData.getScenario().getSimulation().getTrafficComposition().getVehicleType().get(1).setFraction((1 - fraction / 100.0));
        inputData.getVehiclePrototypes().getVehiclePrototypeConfiguration().get(0).getPersonalNavigationDevice().setUncertainty(uncertainty * 6);
        Simulator simRun = MovsimCoreMain.invokeSingleSimulation(inputData);
        sb.append(fraction / 100.0).append(" ").append(uncertainty / 10.0).append(" ").append(simRun.getRoadNetwork().totalVehicleTravelTime()).append("\n");
      }
    }
    writeFile(sb.toString(), "totalVehicleTravelTime.dat");
  }

  public static void writeFile(String text, String outputFile) {
    FileWriter outFile;
    try {
      outFile = new FileWriter(outputFile);
      PrintWriter out = new PrintWriter(outFile);
      out.println(text);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}