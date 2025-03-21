package com.willwinder.universalgcodesender.gcode.util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.willwinder.universalgcodesender.gcode.processors.ArcExpander;
import com.willwinder.universalgcodesender.gcode.processors.CommandLengthProcessor;
import com.willwinder.universalgcodesender.gcode.processors.CommandProcessor;
import com.willwinder.universalgcodesender.gcode.processors.CommentProcessor;
import com.willwinder.universalgcodesender.gcode.processors.DecimalProcessor;
import com.willwinder.universalgcodesender.gcode.processors.FeedOverrideProcessor;
import com.willwinder.universalgcodesender.gcode.processors.LineSplitter;
import com.willwinder.universalgcodesender.gcode.processors.M30Processor;
import com.willwinder.universalgcodesender.gcode.processors.PatternRemover;
import com.willwinder.universalgcodesender.gcode.processors.SpindleOnDweller;
import com.willwinder.universalgcodesender.gcode.processors.WhitespaceProcessor;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.utils.ControllerSettings.ProcessorConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author wwinder
 */
public class CommandProcessorLoader {
  private static final Logger LOGGER = Logger.getLogger(CommandProcessorLoader.class.getSimpleName());

  /**
     * Add any ICommandProcessors specified in a JSON string. Processors are
     * initialized using the application settings if they are enabled.
     * <p>
     * JSON Format:
     * [
     * {
     * "name":"ArcExpander",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * "name": "CommandLenghtProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * "name": "CommentProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * "name": "DecimalProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * "name": "FeedOverrideProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * "name": "M30Processor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * name: "WhitespaceProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * },{
     * name: "SpindleOnDweller",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {}
     * }
     * ]
     */
  static private List<ProcessorConfig> getConfigFrom(String jsonConfig) {
    List<ProcessorConfig> list = new ArrayList<>();
    JsonArray json = JsonParser.parseString(jsonConfig).getAsJsonArray();
    for (JsonElement entry : json) {
      JsonObject object = entry.getAsJsonObject();
      boolean optional = true;
      boolean enabled = true;
      JsonObject args = null;
      if (object.has("optional") && !object.get("optional").isJsonNull()) {
        optional = object.get("optional").getAsBoolean();
      }
      if (object.has("enabled") && !object.get("enabled").isJsonNull()) {
        enabled = object.get("enabled").getAsBoolean();
      }
      if (object.has("args") && !object.get("args").isJsonNull()) {
        args = object.get("args").getAsJsonObject();
      }
      String name = object.get("name").getAsString();
      list.add(new ProcessorConfig(name, enabled, optional, args));
    }
    return list;
  }

  /**
     * Add any ICommandProcessors specified in a JSON string. Processors are
     * configured by properties in the JSON file.
     * <p>
     * JSON Format:
     * [   {
     * "name":"ArcExpander",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {
     * "segmentLengthMM": <double>
     * }
     * },{
     * "name": "CommandLenghtProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {
     * "commandLength": <double>
     * }
     * },{
     * "name": "CommentProcessor",
     * "enabled": <enabled>
     * "optional": <optional>,
     * },{
     * "name": "DecimalProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {
     * "decimals": <double>
     * }
     * },{
     * "name": "FeedOverrideProcessor",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {
     * "speed": <double>
     * }
     * },{
     * "name": "M30Processor",
     * "enabled": <enabled>
     * "optional": <optional>,
     * },{
     * "name": "WhitespaceProcessor",
     * "enabled": <enabled>
     * "optional": <optional>,
     * },{
     * "name": "SpindleOnDweller",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {
     * "duraion": <double>
     * }
     * },{
     * "name":"LineSplitter",
     * "enabled": <enabled>,
     * "optional": <optional>,
     * "args": {
     * "segmentLengthMM": <double>
     * }
     * }
     * ]
     */
  static public List<CommandProcessor> initializeWithProcessors(String jsonConfig) {
    return initializeWithProcessors(getConfigFrom(jsonConfig));
  }

  static public List<CommandProcessor> initializeWithProcessors(List<ProcessorConfig> config) {
    List<CommandProcessor> list = new ArrayList<>();
    for (ProcessorConfig pc : config) {
      if (pc.optional && !pc.enabled) {
        continue;
      }
      getProcessor(pc).ifPresent(list::add);
    }
    return list;
  }

  /**
     * Helper to instantiate a processor by name and call the getHelp method.
     *
     * @param pc processor config
     * @return a help text for the given processor
     */
  static public String getHelpForConfig(ProcessorConfig pc) {
    return getProcessor(pc).map(CommandProcessor::getHelp).orElse(Localization.getString("settings.processors.loadError") + ": " + Localization.getString(pc.name));
  }

  private static Optional<CommandProcessor> getProcessor(ProcessorConfig pc) {
    switch (pc.name) {
      case "ArcExpander":
      double length = pc.args.get("segmentLengthMM").getAsDouble();
      return Optional.of(new ArcExpander(true, length));
      case "CommandLengthProcessor":
      int commandLength = pc.args.get("commandLength").getAsInt();
      return Optional.of(new CommandLengthProcessor(commandLength));
      case "CommentProcessor":
      return Optional.of(new CommentProcessor());
      case "DecimalProcessor":
      int decimals = pc.args.get("decimals").getAsInt();
      return Optional.of(new DecimalProcessor(decimals));
      case "FeedOverrideProcessor":
      double override = pc.args.get("speedOverridePercent").getAsDouble();
      return Optional.of(new FeedOverrideProcessor(override));
      case "M30Processor":
      return Optional.of(new M30Processor());
      case "PatternRemover":
      String pattern = pc.args.get("pattern").getAsString();
      return Optional.of(new PatternRemover(pattern));
      case "WhitespaceProcessor":
      return Optional.of(new WhitespaceProcessor());
      case "SpindleOnDweller":
      double duration = pc.args.get("duration").getAsDouble();
      return Optional.of(new SpindleOnDweller(duration));
      case "LineSplitter":
      return Optional.of(new LineSplitter(pc.args.get("segmentLengthMM").getAsDouble()));
      case "EmptyLineRemoverProcessor":
      LOGGER.severe("Unknown processor: " + pc.name);
      return 
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/c93fec43eeeffe10a015ffc20b37d77918d1d7ae/ugs-core/src/com/willwinder/universalgcodesender/gcode/util/CommandProcessorLoader.java/left.java
      Optional.empty()
=======
      new EmptyLineRemoverProcessor()
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/c93fec43eeeffe10a015ffc20b37d77918d1d7ae/ugs-core/src/com/willwinder/universalgcodesender/gcode/util/CommandProcessorLoader.java/right.java
      ;
    }
  }
}