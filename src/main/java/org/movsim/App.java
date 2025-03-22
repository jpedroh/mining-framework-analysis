package org.movsim;
import org.movsim.input.impl.SimCommandLine;
import org.movsim.simulator.Simulator;
import org.movsim.simulator.impl.SimulatorImpl;
import org.movsim.ui.controller.Controller;
import org.movsim.ui.controller.impl.SimulatorController;

/**
 * The Class App.
 */
public class App {
  /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
  public static void main(String[] args) {
    final SimCommandLine cmdline = new SimCommandLine(args);
    final Simulator simulator = new SimulatorImpl();
    final Controller controller = new SimulatorController(simulator);
  }
}