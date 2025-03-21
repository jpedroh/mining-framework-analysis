package com.willwinder.universalgcodesender.utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.willwinder.universalgcodesender.G2CoreController;
import com.willwinder.universalgcodesender.firmware.fluidnc.FluidNCController;
import com.willwinder.universalgcodesender.GrblController;
import com.willwinder.universalgcodesender.GrblEsp32Controller;
import com.willwinder.universalgcodesender.IController;
import com.willwinder.universalgcodesender.LoopBackCommunicator;
import com.willwinder.universalgcodesender.SmoothieController;
import com.willwinder.universalgcodesender.TinyGController;
import com.willwinder.universalgcodesender.XLCDCommunicator;
import com.willwinder.universalgcodesender.gcode.util.CommandProcessorLoader;
import com.willwinder.universalgcodesender.gcode.processors.CommandProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * POJO Object representation of a controller JSON file.
 *
 * @author wwinder
 */
public class ControllerSettings {
  private class ControllerConfig {
    public String name;

    public JsonElement args;
  }

  String Name;

  static public class ProcessorConfig {
    public String name;

    public Boolean enabled = true;

    public Boolean optional = true;

    public JsonObject args = null;

    public ProcessorConfig(String name, Boolean enabled, Boolean optional, JsonObject args) {
      this.name = name;
      this.enabled = enabled;
      this.optional = optional;
      this.args = args;
    }
  }

  Integer Version = 0;

  public class ProcessorConfigGroups {
    public ArrayList<ProcessorConfig> Front;

    public ArrayList<ProcessorConfig> Custom;

    public ArrayList<ProcessorConfig> End;
  }

  ControllerConfig Controller;

  ProcessorConfigGroups GcodeProcessors;

  public enum CONTROLLER {
    GRBL("GRBL"),
    GRBL_ESP32("GRBL ESP32"),
    FLUIDNC("FluidNC"),
    SMOOTHIE("SmoothieBoard"),
    TINYG("TinyG"),
    G2CORE("g2core"),
    XLCD("XLCD"),
    LOOPBACK("Loopback"),
    LOOPBACK_SLOW("Loopback_Slow")
    ;

    final String name;

    CONTROLLER(String name) {
      this.name = name;
    }

    public static CONTROLLER fromString(String name) {
      for (CONTROLLER c : values()) {
        if (c.name.equalsIgnoreCase(name)) {
          return c;
        }
      }
      return null;
    }
  }

  public String getName() {
    return Name;
  }

  public Integer getVersion() {
    return Version;
  }

  /**
     * Parse the "Controller" object in the firmware config json.
     * 
     * "Controller": {
     *     "name": "GRBL",
     *     "args": null
     * }
     */
  public Optional<IController> getController() {
    String controllerName = this.Controller.name;
    CONTROLLER controller = CONTROLLER.fromString(controllerName);
    if (controller == null) {
      return Optional.empty();
    }
    switch (controller) {
      case GRBL:
      return Optional.of(new GrblController());
      case GRBL_ESP32:
      return Optional.of(new GrblEsp32Controller());
      case SMOOTHIE:
      return Optional.of(new SmoothieController());
      case TINYG:
      return Optional.of(new TinyGController());
      case G2CORE:
      return Optional.of(new G2CoreController());
      case XLCD:
      return Optional.of(new GrblController(new XLCDCommunicator()));
      case LOOPBACK:
      return Optional.of(new GrblController(new LoopBackCommunicator()));
      case LOOPBACK_SLOW:
      return Optional.of(new GrblController(new LoopBackCommunicator(100)));
      case FLUIDNC:
      return 
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/84d687330a68c360b5596e4fcc075d6f2a61da00/ugs-core/src/com/willwinder/universalgcodesender/utils/ControllerSettings.java/left.java
      new FluidNCController()
=======
      Optional.empty()
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/84d687330a68c360b5596e4fcc075d6f2a61da00/ugs-core/src/com/willwinder/universalgcodesender/utils/ControllerSettings.java/right.java
      ;
    }
  }

  /**
     * Get the list of processors from the settings in the order they should be
     * applied.
     */
  public List<CommandProcessor> getProcessors() {
    List<CommandProcessor> ret = new ArrayList<>();
    ret.addAll(CommandProcessorLoader.initializeWithProcessors(GcodeProcessors.Front));
    ret.addAll(CommandProcessorLoader.initializeWithProcessors(GcodeProcessors.Custom));
    ret.addAll(CommandProcessorLoader.initializeWithProcessors(GcodeProcessors.End));
    return ret;
  }

  public ProcessorConfigGroups getProcessorConfigs() {
    return this.GcodeProcessors;
  }
}