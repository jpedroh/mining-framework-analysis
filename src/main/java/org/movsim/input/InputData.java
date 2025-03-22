package org.movsim.input;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.movsim.input.model.SimulationInput;
import org.movsim.input.model.VehicleInput;
import org.movsim.input.model.consumption.FuelConsumptionInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputData {
  /** The Constant logger. */
  final static Logger logger = LoggerFactory.getLogger(InputData.class);

  /** The vehicle input data. */
  private List<VehicleInput> vehicleInputData;

  /** The simulation input. */
  private SimulationInput simulationInput;

  /** The fuel consumption input. */
  private FuelConsumptionInput fuelConsumptionInput;

  private ProjectMetaData projectMetaData;

  /**
     * Instantiates a new input data.
     */
  public InputData() {
    projectMetaData = ProjectMetaData.getInstance();
  }

  /**
     * Sets the project name.
     * 
     * @param projectname
     *            the new project name
     */
  public void setProjectName(String projectname) {
    this.projectMetaData.setProjectName(projectname);
  }

  /**
     * Sets the vehicle input data.
     * 
     * @param vehicleInputData
     *            the new vehicle input data
     */
  public void setVehicleInputData(List<VehicleInput> vehicleInputData) {
    this.vehicleInputData = vehicleInputData;
  }

  public List<VehicleInput> getVehicleInputData() {
    return vehicleInputData;
  }

  public static Map<String, VehicleInput> createVehicleInputDataMap(List<VehicleInput> vehicleInputData) {
    final HashMap<String, VehicleInput> map = new HashMap<String, VehicleInput>();
    for (final VehicleInput vehInput : vehicleInputData) {
      final String keyName = vehInput.getLabel();
      map.put(keyName, vehInput);
    }
    return map;
  }

  public SimulationInput getSimulationInput() {
    return simulationInput;
  }

  /**
     * Sets the simulation input.
     * 
     * @param simulationInput
     *            the new simulation input
     */
  public void setSimulationInput(SimulationInput simulationInput) {
    this.simulationInput = simulationInput;
  }

  public ProjectMetaData getProjectMetaData() {
    return projectMetaData;
  }

  public void setFuelConsumptionInput(FuelConsumptionInput fuelConsumptionInput) {
    this.fuelConsumptionInput = fuelConsumptionInput;
  }

  public FuelConsumptionInput getFuelConsumptionInput() {
    return fuelConsumptionInput;
  }
}