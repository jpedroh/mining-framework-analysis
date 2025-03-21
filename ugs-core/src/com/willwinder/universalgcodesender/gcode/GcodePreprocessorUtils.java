package com.willwinder.universalgcodesender.gcode;
import com.willwinder.universalgcodesender.gcode.util.Code;
import com.willwinder.universalgcodesender.gcode.util.GcodeParserException;
import com.willwinder.universalgcodesender.gcode.util.PlaneFormatter;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.Position;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.willwinder.universalgcodesender.gcode.util.Code.*;
import static com.willwinder.universalgcodesender.gcode.util.Code.ModalGroup.Motion;

/**
 * Collection of useful command preprocessor methods.
 *
 * @author wwinder
 */
public class GcodePreprocessorUtils {
  public static final Pattern COMMENT = Pattern.compile("\\(.*\\)|\\s*;.*|%.*$");

  private static final String EMPTY = "";

  private static final Pattern COMMENTPARSE = Pattern.compile("(?<=\\()[^()]*|(?<=;).*|%");

  private static int decimalLength = -1;

  private static Pattern decimalPattern;

  private static DecimalFormat decimalFormatter;

  /**
     * Searches the command string for an 'f' and replaces the speed value 
     * between the 'f' and the next space with a percentage of that speed.
     * In that way all speed values become a ratio of the provided speed 
     * and don't get overridden with just a fixed speed.
     */
  static public String overrideSpeed(String command, double speed) {
    String returnString = command;
    Pattern pattern = Pattern.compile("F([0-9.]+)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(command);
    if (matcher.find()) {
      double originalFeedRate = Double.parseDouble(matcher.group(1));
      double newFeedRate = originalFeedRate * speed / 100.0;
      returnString = matcher.replaceAll("F" + newFeedRate);
    }
    return returnString;
  }

  /**
     * Removes any comments within parentheses or beginning with a semi-colon.
     */
  static public String removeComment(String command) {
    return COMMENT.matcher(command).replaceAll(EMPTY);
  }

  /**
     * Searches for a comment in the input string and returns the first match.
     */
  static public String parseComment(String command) {
    String comment = EMPTY;
    Matcher matcher = COMMENTPARSE.matcher(command);
    if (matcher.find()) {
      comment = matcher.group(0);
    }
    return comment;
  }

  static public String truncateDecimals(int length, String command) {
    if (length != decimalLength) {
      updateDecimalFormatter(length);
    }
    Matcher matcher = decimalPattern.matcher(command);
    double d;
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      d = Double.parseDouble(matcher.group());
      matcher.appendReplacement(sb, decimalFormatter.format(d));
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  private static void updateDecimalFormatter(int length) {
    StringBuilder df = new StringBuilder();
    df.append("#");
    if (length != 0) {
      df.append(".");
    }
    for (int i = 0; i < length; i++) {
      df.append('#');
    }
    decimalFormatter = new DecimalFormat(df.toString(), Localization.dfs);
    df = new StringBuilder();
    df.append("\\d+\\.\\d");
    for (int i = 0; i < length; i++) {
      df.append("\\d");
    }
    df.append('+');
    decimalPattern = Pattern.compile(df.toString());
    decimalLength = length;
  }

  static public List<String> parseCodes(List<String> args, char code) {
    List<String> l = new ArrayList<>();
    char address = Character.toUpperCase(code);
    for (String s : args) {
      if (s.length() > 0 && Character.toUpperCase(s.charAt(0)) == address) {
        l.add(s.substring(1));
      }
    }
    return l;
  }

  /**
     * Update a point given the arguments of a command, using a pre-parsed list.
     */
  static public Position updatePointWithCommand(List<String> commandArgs, Position initial, boolean absoluteMode) {
    double x = parseCoord(commandArgs, 'X');
    double y = parseCoord(commandArgs, 'Y');
    double z = parseCoord(commandArgs, 'Z');
    if (Double.isNaN(x) && Double.isNaN(y) && Double.isNaN(z)) {
      return null;
    }
    return updatePointWithCommand(initial, x, y, z, absoluteMode);
  }

  /**
     * Update a point given the new coordinates.
     */
  static public Position updatePointWithCommand(Position initial, double x, double y, double z, boolean absoluteMode) {
    Position newPoint = new Position(initial);
    if (absoluteMode) {
      if (!Double.isNaN(x)) {
        newPoint.x = x;
      }
      if (!Double.isNaN(y)) {
        newPoint.y = y;
      }
      if (!Double.isNaN(z)) {
        newPoint.z = z;
      }
    } else {
      if (!Double.isNaN(x)) {
        newPoint.x += x;
      }
      if (!Double.isNaN(y)) {
        newPoint.y += y;
      }
      if (!Double.isNaN(z)) {
        newPoint.z += z;
      }
    }
    return newPoint;
  }

  static public Position updateCenterWithCommand(List<String> commandArgs, Position initial, Position nextPoint, boolean absoluteIJKMode, boolean clockwise, PlaneFormatter plane) {
    double i = parseCoord(commandArgs, 'I');
    double j = parseCoord(commandArgs, 'J');
    double k = parseCoord(commandArgs, 'K');
    double radius = parseCoord(commandArgs, 'R');
    if (Double.isNaN(i) && Double.isNaN(j) && Double.isNaN(k)) {
      return GcodePreprocessorUtils.convertRToCenter(initial, nextPoint, radius, absoluteIJKMode, clockwise, plane);
    }
    return updatePointWithCommand(initial, i, j, k, absoluteIJKMode);
  }

  static public String generateLineFromPoints(final Code command, final Position start, final Position end, final boolean absoluteMode, DecimalFormat formatter) {
    DecimalFormat df = formatter;
    if (df == null) {
      df = new DecimalFormat("0.####", Localization.dfs);
    }
    StringBuilder sb = new StringBuilder();
    sb.append(command);
    if (absoluteMode) {
      if (!Double.isNaN(end.x)) {
        sb.append("X");
        sb.append(df.format(end.x));
      }
      if (!Double.isNaN(end.y)) {
        sb.append("Y");
        sb.append(df.format(end.y));
      }
      if (!Double.isNaN(end.z)) {
        sb.append("Z");
        sb.append(df.format(end.z));
      }
    } else {
      if (!Double.isNaN(end.x)) {
        sb.append("X");
        sb.append(df.format(end.x - start.x));
      }
      if (!Double.isNaN(end.y)) {
        sb.append("Y");
        sb.append(df.format(end.y - start.x));
      }
      if (!Double.isNaN(end.z)) {
        sb.append("Z");
        sb.append(df.format(end.z - start.x));
      }
    }
    return sb.toString();
  }

  /**
     * Splits a gcode command by each word/argument, doesn't care about spaces.
     * This command is about the same speed as the string.split(" ") command,
     * but might be a little faster using precompiled regex.
     */
  static public List<String> splitCommand(String command) {
    if (command.startsWith("$")) {
      return Collections.singletonList(command);
    }
    List<String> l = new ArrayList<>();
    boolean readNumeric = false;
    boolean readLineComment = false;
    boolean readBlockComment = false;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < command.length(); i++) {
      char c = command.charAt(i);
      if (c == '(' && !readLineComment && !readBlockComment) {
        if (sb.length() > 0) {
          l.add(sb.toString());
          sb = new StringBuilder();
        }
        sb.append(c);
        readBlockComment = true;
        continue;
      } else {
        if (readBlockComment && c == ')') {
          readBlockComment = false;
          sb.append(c);
          l.add(sb.toString());
          sb = new StringBuilder();
          continue;
        } else {
          if (c == ';' && !readLineComment && !readBlockComment) {
            if (sb.length() > 0) {
              l.add(sb.toString());
              sb = new StringBuilder();
            }
            sb.append(c);
            readLineComment = true;
            continue;
          }
        }
      }
      if (readLineComment || readBlockComment) {
        sb.append(c);
      } else {
        if (Character.isWhitespace(c)) {
          continue;
        } else {
          if (readNumeric && !Character.isDigit(c) && c != '.') {
            readNumeric = false;
            l.add(sb.toString());
            sb = new StringBuilder();
            if (Character.isLetter(c)) {
              sb.append(c);
            }
          } else {
            if (Character.isDigit(c) || c == '.' || c == '-') {
              sb.append(c);
              readNumeric = true;
            } else {
              if (Character.isLetter(c)) {
                sb.append(c);
              }
            }
          }
        }
      }
    }
    if (sb.length() > 0) {
      l.add(sb.toString());
    }
    return l;
  }

  static public boolean hasAxisWords(List<String> argList) {
    for (String t : argList) {
      if (t.length() > 1) {
        char c = Character.toUpperCase(t.charAt(0));
        if (c == 'X' || c == 'Y' || c == 'Z') {
          return true;
        }
      }
    }
    return false;
  }

  /**
     * Pulls out a word, like "F100", "S1300", "T0", "X-0.5"
     */
  static public String extractWord(List<String> argList, char c) {
    char address = Character.toUpperCase(c);
    for (String t : argList) {
      if (Character.toUpperCase(t.charAt(0)) == address) {
        return t;
      }
    }
    return null;
  }

  static public double parseCoord(List<String> argList, char c) {
    String word = extractWord(argList, c);
    if (word != null && word.length() > 1) {
      try {
        return Double.parseDouble(word.substring(1));
      } catch (NumberFormatException e) {
        return Double.NaN;
      }
    }
    return Double.NaN;
  }

  /**
     * Generates the points along an arc including the start and end points.
     */
  static public List<Position> generatePointsAlongArcBDring(final Position start, final Position end, final Position center, boolean clockwise, double R, double minArcLength, double arcSegmentLength, PlaneFormatter plane) {
    double radius = R;
    if (radius == 0) {
      radius = Math.sqrt(Math.pow(plane.axis0(start) - plane.axis0(center), 2.0) + Math.pow(plane.axis1(end) - plane.axis1(center), 2.0));
    }
    double startAngle = GcodePreprocessorUtils.getAngle(center, start, plane);
    double endAngle = GcodePreprocessorUtils.getAngle(center, end, plane);
    double sweep = GcodePreprocessorUtils.calculateSweep(startAngle, endAngle, clockwise);
    double arcLength = sweep * radius;
    if (minArcLength > 0 && arcLength < minArcLength) {
      return null;
    }
    int numPoints = 20;
    if (arcSegmentLength <= 0 && minArcLength > 0) {
      arcSegmentLength = (sweep * radius) / minArcLength;
    }
    if (arcSegmentLength > 0) {
      numPoints = (int) Math.ceil(arcLength / arcSegmentLength);
    }
    return GcodePreprocessorUtils.generatePointsAlongArcBDring(start, end, center, clockwise, radius, startAngle, sweep, numPoints, plane);
  }

  /**
     * Generates the points along an arc including the start and end points.
     */
  static private List<Position> generatePointsAlongArcBDring(final Position p1, final Position p2, final Position center, boolean isCw, double radius, double startAngle, double sweep, int numPoints, PlaneFormatter plane) {
    Position lineStart = new Position(p1);
    List<Position> segments = new ArrayList<>();
    double angle;
    if (radius == 0) {
      radius = Math.sqrt(Math.pow(plane.axis0(p1) - plane.axis1(center), 2.0) + Math.pow(plane.axis1(p1) - plane.axis1(center), 2.0));
    }
    double linearIncrement = (plane.linear(p2) - plane.linear(p1)) / numPoints;
    double linearPos = plane.linear(lineStart);
    for (int i = 0; i < numPoints; i++) {
      if (isCw) {
        angle = (startAngle - i * sweep / numPoints);
      } else {
        angle = (startAngle + i * sweep / numPoints);
      }
      if (angle >= Math.PI * 2) {
        angle = angle - Math.PI * 2;
      }
      plane.setAxis0(lineStart, Math.cos(angle) * radius + plane.axis0(center));
      plane.setAxis1(lineStart, Math.sin(angle) * radius + plane.axis1(center));
      plane.setLinear(lineStart, linearPos);
      linearPos += linearIncrement;
      segments.add(new Position(lineStart));
    }
    segments.add(new Position(p2));
    return segments;
  }

  /**
     * Helper method for to convert IJK syntax to center point.
     *
     * @return the center of rotation between two points with IJK codes.
     */
  static private Position convertRToCenter(Position start, Position end, double radius, boolean absoluteIJK, boolean clockwise, PlaneFormatter plane) {
    Position center = new Position(start.getUnits());
    double x = plane.axis0(end) - plane.axis0(start);
    double y = plane.axis1(end) - plane.axis1(start);
    double h_x2_div_d = 4 * radius * radius - x * x - y * y;
    h_x2_div_d = (-Math.sqrt(h_x2_div_d)) / Math.hypot(x, y);
    if (!clockwise) {
      h_x2_div_d = -h_x2_div_d;
    }
    if (radius < 0) {
      h_x2_div_d = -h_x2_div_d;
    }
    double offsetX = 0.5 * (x - (y * h_x2_div_d));
    double offsetY = 0.5 * (y + (x * h_x2_div_d));
    if (!absoluteIJK) {
      plane.setAxis0(center, plane.axis0(start) + offsetX);
      plane.setAxis1(center, plane.axis1(start) + offsetY);
    } else {
      plane.setAxis0(center, offsetX);
      plane.setAxis1(center, offsetY);
    }
    return center;
  }

  /**
     * Helper method for arc calculation
     *
     * @return angle in radians of a line going from start to end.
     */
  static private double getAngle(final Position start, final Position end, PlaneFormatter plane) {
    double deltaX = plane.axis0(end) - plane.axis0(start);
    double deltaY = plane.axis1(end) - plane.axis1(start);
    double angle = 0.0;
    if (deltaX != 0) {
      if (deltaX > 0 && deltaY >= 0) {
        angle = Math.atan(deltaY / deltaX);
      } else {
        if (deltaX < 0 && deltaY >= 0) {
          angle = Math.PI - Math.abs(Math.atan(deltaY / deltaX));
        } else {
          if (deltaX < 0 && deltaY < 0) {
            angle = Math.PI + Math.abs(Math.atan(deltaY / deltaX));
          } else {
            if (deltaX > 0 && deltaY < 0) {
              angle = Math.PI * 2 - Math.abs(Math.atan(deltaY / deltaX));
            }
          }
        }
      }
    } else {
      if (deltaY > 0) {
        angle = Math.PI / 2.0;
      } else {
        angle = Math.PI * 3.0 / 2.0;
      }
    }
    return angle;
  }

  /**
     * Helper method for arc calculation to calculate sweep from two angles.
     * @return sweep in radians.
     */
  static private double calculateSweep(double startAngle, double endAngle, boolean isCw) {
    double sweep;
    if (startAngle == endAngle) {
      sweep = (Math.PI * 2);
    } else {
      if (endAngle == 0) {
        endAngle = Math.PI * 2;
      }
      if (!isCw && endAngle < startAngle) {
        sweep = ((Math.PI * 2 - startAngle) + endAngle);
      } else {
        if (isCw && endAngle > startAngle) {
          sweep = ((Math.PI * 2 - endAngle) + startAngle);
        } else {
          sweep = Math.abs(endAngle - startAngle);
        }
      }
    }
    return sweep;
  }

  static public Set<Code> getMCodes(List<String> args) {
    return getCodes(args, 'M');
  }

  static public Set<Code> getGCodes(List<String> args) {
    return getCodes(args, 'G');
  }

  static public Set<Code> getCodes(List<String> args, Character letter) {
    List<String> gCodeStrings = parseCodes(args, letter);
    return gCodeStrings.stream().map((c) -> letter + c).map(Code::lookupCode).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static class SplitCommand {
    public String extracted;

    public String remainder;
  }

  public static boolean isMotionWord(char character) {
    char c = Character.toUpperCase(character);
    return c == 'X' || c == 'Y' || c == 'Z' || c == 'U' || c == 'V' || c == 'W' || c == 'I' || c == 'J' || c == 'K' || c == 'R';
  }

  /**
     * Return extracted motion words and remainder words.
     *
     * If the code is implicit, like the command "X0Y0", we'll still extract "X0Y0".
     * If the code is G0 or G1 and G53 is found, it will also be extracted:
     * http://linuxcnc.org/docs/html/gcode/g-code.html#gcode:g53
     */
  public static SplitCommand extractMotion(Code code, String command) {
    List<String> args = splitCommand(command);
    if (args.isEmpty()) {
      return null;
    }
    StringBuilder extracted = new StringBuilder();
    StringBuilder remainder = new StringBuilder();
    boolean includeG53 = code == G0 || code == G1;
    for (String arg : args) {
      char c = arg.charAt(0);
      Code lookup = Code.lookupCode(arg);
      if (lookup.getType() == Motion && lookup != code) {
        return null;
      }
      if (lookup == code || isMotionWord(c) || (includeG53 && lookup == G53)) {
        extracted.append(arg);
      } else {
        remainder.append(arg);
      }
    }
    if (extracted.length() == 0) {
      return null;
    }
    SplitCommand sc = new SplitCommand();
    sc.extracted = extracted.toString();
    sc.remainder = remainder.toString();
    return sc;
  }

  /**
     * Normalize a command by adding in implicit state.
     *
     * For example given the following program:
     *     G20
     *     G0 X10 F25
     *     Y10
     *
     * The third command would be normalized to:
     *     G0 Y10 F25
     *
     * @param command a command string to normalize.
     * @param state the machine state before the command.
     * @return normalized command.
     */
  public static String normalizeCommand(String command, GcodeState state) throws GcodeParserException {
    List<String> args = GcodePreprocessorUtils.splitCommand(command);
    Set<Code> gCodes = getGCodes(args);
    Code code = null;
    for (Code c : gCodes) {
      if (c.getType() == Motion) {
        code = c;
      }
    }
    if (code == null) {
      code = state.currentMotionMode;
    }
    SplitCommand split = extractMotion(code, command);
    if (split == null) {
      throw new GcodeParserException("Invalid state attached to command, please notify the developers.");
    }
    StringBuilder result = new StringBuilder();
    result.append("F").append(state.speed);
    result.append("S").append(state.spindleSpeed);
    if (!gCodes.contains(code)) {
      result.append(state.currentMotionMode.toString());
    }
    result.append(split.extracted);
    return result.toString();
  }
}