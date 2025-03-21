package com.willwinder.universalgcodesender.gcode;
import com.willwinder.universalgcodesender.gcode.processors.CommandProcessor;
import com.willwinder.universalgcodesender.gcode.processors.CommandProcessorList;
import com.willwinder.universalgcodesender.gcode.processors.Stats;
import com.willwinder.universalgcodesender.gcode.util.Code;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserException;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserUtils;
import com.willwinder.universalgcodesender.types.PointSegment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Object to parse gcode one command at a time in a way that can be used by any
 * other class which needs to know about the current state at a given command.
 *
 * This object can be extended by adding in any number of ICommandProcessor
 * objects which are applied to each command in the order they were inserted
 * into the parser. These processors can be as simple as removing whitespace to
 * as complex as expanding a canned cycle or applying an leveling plane.
 *
 * @author wwinder
 */
public class GcodeParser implements IGcodeParser {
  private GcodeState state;

  private final CommandProcessorList processors = new CommandProcessorList();

  private Stats statsProcessor;

  public static class GcodeMeta {
    /**
         * The original command represented by this meta object.
         */
    public String command;

    /**
         * Gcode command in line.
         */
    public Code code;

    /**
         * Gcode state after processing the command.
         */
    public GcodeState state;

    /**
         * PointSegments represent the endpoint of a given command.
         */
    public PointSegment point;
  }

  /**
     * Constructor.
     */
  public GcodeParser() {
    this.state = new GcodeState();
    this.reset();
  }

  /**
     * @return the number of command processors that have been added.
     */
  @Override public int numCommandProcessors() {
    return this.processors.size();
  }

  /**
     * Add a preprocessor to use with the preprocessCommand method.
     */
  @Override public void addCommandProcessor(CommandProcessor p) {
    this.processors.add(p);
  }

  @Override public void removeCommandProcessor(CommandProcessor p) {
    this.processors.remove(p);
  }

  /**
     * Clear out any processors that have been added.
     */
  @Override public void clearCommandProcessors() {
    this.processors.clear();
    this.statsProcessor = new Stats();
  }

  /**
     * Resets the current state.
     */
  public void reset() {
    this.statsProcessor = new Stats();
    this.state = new GcodeState();
    this.state.commandNumber = -1;
  }

  /**
     * Add a command to be processed with no line number association.
     */
  @Override public List<GcodeMeta> addCommand(String command) throws GcodeParserException {
    return addCommand(command, ++this.state.commandNumber);
  }

  /**
     * Add a command to be processed with a line number.
     * @throws GcodeParserException If the command is too long throw an exception
     */
  @Override public List<GcodeMeta> addCommand(String command, int line) throws GcodeParserException {
    statsProcessor.processCommand(command, state);
    List<GcodeMeta> results = new ArrayList<>();
    Collection<GcodeMeta> metaObjects = GcodeParserUtils.processCommand(command, line, state, true);
    if (metaObjects != null) {
      for (GcodeMeta c : metaObjects) {
        if (c.point != null) {
          results.add(c);
        }
        if (c.state != null) {
          this.state = c.state;
          statsProcessor.processCommand(command, state);
        }
      }
    }
    return results;
  }

  /**
     * Gets the point at the end of the list.
     */
  @Override public GcodeState getCurrentState() {
    return this.state;
  }

  @Override public GcodeStats getCurrentStats() {
    return statsProcessor;
  }

  /**
     * Applies all command processors to a given command and returns the resulting GCode. Does not change the parser state.
     *
     * @param command a command to run through a list of processors to create a list of commands
     * @param initialState the state before command was applied
     */
  @Override public List<String> preprocessCommand(String command, final GcodeState initialState) throws GcodeParserException {
    return processors.processCommand(command, initialState);
  }
}