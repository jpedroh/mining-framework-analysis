package com.willwinder.universalgcodesender.gcode.processors;
import com.google.common.collect.Iterables;
import com.willwinder.universalgcodesender.gcode.GcodeParser.GcodeMeta;
import com.willwinder.universalgcodesender.gcode.GcodePreprocessorUtils;
import com.willwinder.universalgcodesender.gcode.GcodePreprocessorUtils.SplitCommand;
import com.willwinder.universalgcodesender.gcode.GcodeState;
import com.willwinder.universalgcodesender.gcode.util.Code;
import static com.willwinder.universalgcodesender.gcode.util.Code.G1;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserException;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserUtils;
import com.willwinder.universalgcodesender.gcode.util.PlaneFormatter;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.types.PointSegment;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Expand an arc into smaller sections. You can configure the length of each
 * section, and whether it is expanded with a bunch of smaller arcs, or with
 * line segments.
 *
 * @author wwinder
 */
public class ArcExpander implements CommandProcessor {
  final private boolean convertToLines;

  final private double length;

  final private DecimalFormat df;

  @Override public String getHelp() {
    return Localization.getString("sender.help.arcs") + "\n" + Localization.getString("sender.arcs.length") + ": " + df.format(length);
  }

  /**
     * @param convertToLines toggles if smaller lines or arcs are returned.
     * @param length the length of each smaller segment.
     */
  public ArcExpander(boolean convertToLines, double length) {
    this.convertToLines = convertToLines;
    this.length = length;
    df = new DecimalFormat("#.#########", Localization.dfs);
  }

  /**
     * @param convertToLines toggles if smaller lines or arcs are returned.
     * @param length the length of each smaller segment.
     */
  public ArcExpander(boolean convertToLines, double length, DecimalFormat df) {
    this.convertToLines = convertToLines;
    this.length = length;
    this.df = df;
  }

  @Override public List<String> processCommand(String command, GcodeState state) throws GcodeParserException {
    if (state.currentPoint == null) {
      throw new GcodeParserException(Localization.getString("parser.processor.arc.start-error"));
    }
    List<String> results = new ArrayList<>();
    List<GcodeMeta> commands = GcodeParserUtils.processCommand(command, 0, state, true);
    Code c = hasArcCommand(commands);
    if (c == null) {
      return Collections.singletonList(command);
    }
    SplitCommand sc = GcodePreprocessorUtils.extractMotion(c, command);
    if (sc.remainder.length() > 0) {
      results.add(sc.remainder);
    }
    GcodeMeta arcMeta = Iterables.getLast(commands);
    PointSegment ps = arcMeta.point;
    Position start = state.currentPoint;
    Position end = arcMeta.point.point();
    List<Position> points = GcodePreprocessorUtils.generatePointsAlongArcBDring(start, end, ps.center(), ps.isClockwise(), ps.getRadius(), 0, this.length, new PlaneFormatter(ps.getPlaneState()));
    points.remove(0);
    if (convertToLines) {
      for (Position point : points) {
        results.add(GcodePreprocessorUtils.generateLineFromPoints(G1, start, point, state.inAbsoluteMode, df));
        start = point;
      }
    } else {
      throw new UnsupportedOperationException("I have not implemented this.");
    }
    return results;
  }

  private static Code hasArcCommand(List<GcodeMeta> commands) {
    if (commands == null) {
      return null;
    }
    for (GcodeMeta meta : commands) {
      if (meta.point != null && meta.point.isArc()) {
        return meta.code;
      }
    }
    return null;
  }
}