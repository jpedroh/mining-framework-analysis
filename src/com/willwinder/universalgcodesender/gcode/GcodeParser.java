package com.willwinder.universalgcodesender.gcode;
import com.willwinder.universalgcodesender.types.PointSegment;
import java.text.DecimalFormat;
import java.util.*;
import javax.vecmath.Point3d;

/**
 *
 * @author wwinder
 */
public class GcodeParser {
  private boolean isMetric = true;

  private boolean inAbsoluteMode = true;

  private boolean inAbsoluteIJKMode = false;

  private String lastGcodeCommand = "";

  private Point3d currentPoint = null;

  private int commandNumber = 0;

  private double speedOverride = -1;

  private int truncateDecimalLength = 40;

  private boolean removeAllWhitespace = true;

  private boolean convertArcsToLines = false;

  private double smallArcThreshold = 1.0;

  private double smallArcSegmentLength = 0.3;

  List<PointSegment> points;

  public GcodeParser() {
    this.reset();
  }

  public boolean getConvertArcsToLines() {
    return convertArcsToLines;
  }

  public void setConvertArcsToLines(boolean convertArcsToLines) {
    this.convertArcsToLines = convertArcsToLines;
  }

  public boolean getRemoveAllWhitespace() {
    return removeAllWhitespace;
  }

  public void setRemoveAllWhitespace(boolean removeAllWhitespace) {
    this.removeAllWhitespace = removeAllWhitespace;
  }

  public double getSmallArcSegmentLength() {
    return smallArcSegmentLength;
  }

  public void setSmallArcSegmentLength(double smallArcSegmentLength) {
    this.smallArcSegmentLength = smallArcSegmentLength;
  }

  public double getSmallArcThreshold() {
    return smallArcThreshold;
  }

  public void setSmallArcThreshold(double smallArcThreshold) {
    this.smallArcThreshold = smallArcThreshold;
  }

  public double getSpeedOverride() {
    return speedOverride;
  }

  public void setSpeedOverride(double speedOverride) {
    this.speedOverride = speedOverride;
  }

  public int getTruncateDecimalLength() {
    return truncateDecimalLength;
  }

  public void setTruncateDecimalLength(int truncateDecimalLength) {
    this.truncateDecimalLength = truncateDecimalLength;
  }

  final public void reset() {
    this.currentPoint = new Point3d();
    this.points = new ArrayList<>();
    this.points.add(new PointSegment(this.currentPoint, -1));
  }

  /**
     * Add a command to be processed.
     */
  public PointSegment addCommand(String command) {
    String stripped = GcodePreprocessorUtils.removeComment(command);
    List<String> args = GcodePreprocessorUtils.splitCommand(stripped);
    return this.addCommand(args);
  }

  /**
     * Add a command which has already been broken up into its arguments.
     */
  public PointSegment addCommand(List<String> args) {
    if (args.isEmpty()) {
      return null;
    }
    return processCommand(args);
  }

  /**
     * Warning, this should only be used when modifying live gcode, such as when
     * expanding an arc or canned cycle into line segments.
     */
  private void setLastGcodeCommand(String num) {
    this.lastGcodeCommand = num;
  }

  /**
     * Gets the point at the end of the list.
     */
  public Point3d getCurrentPoint() {
    return currentPoint;
  }

  /**
     * Expands the last point in the list if it is an arc according to the
     * the parsers settings.
     */
  public List<PointSegment> expandArc() {
    PointSegment startSegment = this.points.get(this.points.size() - 2);
    PointSegment lastSegment = this.points.get(this.points.size() - 1);
    if (!lastSegment.isArc()) {
      return null;
    }
    Point3d start = startSegment.point();
    Point3d end = lastSegment.point();
    Point3d center = lastSegment.center();
    double radius = lastSegment.getRadius();
    boolean clockwise = lastSegment.isClockwise();
    List<Point3d> expandedPoints = GcodePreprocessorUtils.generatePointsAlongArcBDring(start, end, center, clockwise, radius, smallArcThreshold, smallArcSegmentLength);
    if (expandedPoints == null) {
      return null;
    }
    this.points.remove(this.points.size() - 1);
    commandNumber--;
    List<PointSegment> psl = new ArrayList<>();
    PointSegment temp;
    Iterator<Point3d> psi = expandedPoints.listIterator(1);
    while (psi.hasNext()) {
      temp = new PointSegment(psi.next(), commandNumber++);
      temp.setIsMetric(lastSegment.isMetric());
      this.points.add(temp);
      psl.add(temp);
    }
    this.currentPoint = this.points.get(this.points.size() - 1).point();
    return psl;
  }

  public List<PointSegment> getPointSegmentList() {
    return this.points;
  }

  private PointSegment processCommand(List<String> args) {
    List<String> gCodes;
    PointSegment ps = null;
    gCodes = GcodePreprocessorUtils.parseCodes(args, 'G');
    if (gCodes.isEmpty() && lastGcodeCommand != null && !lastGcodeCommand.isEmpty()) {
      gCodes.add(lastGcodeCommand);
    }
    for (String i : gCodes) {
      ps = handleGCode(i, args);
    }
    return ps;
  }

  private PointSegment addLinearPointSegment(Point3d nextPoint, boolean fastTraverse) {
    PointSegment ps = new PointSegment(nextPoint, commandNumber++);
    boolean zOnly = false;
    if ((this.currentPoint.x == nextPoint.x) && (this.currentPoint.y == nextPoint.y) && (this.currentPoint.z != nextPoint.z)) {
      zOnly = true;
    }
    ps.setIsMetric(this.isMetric);
    ps.setIsZMovement(zOnly);
    ps.setIsFastTraverse(fastTraverse);
    this.points.add(ps);
    this.currentPoint = nextPoint;
    return ps;
  }

  private PointSegment addArcPointSegment(Point3d nextPoint, boolean clockwise, List<String> args) {
    PointSegment ps = new PointSegment(nextPoint, commandNumber++);
    Point3d center = GcodePreprocessorUtils.updateCenterWithCommand(args, this.currentPoint, nextPoint, this.inAbsoluteIJKMode, clockwise);
    double radius = GcodePreprocessorUtils.parseCoord(args, 'R');
    if (Double.isNaN(radius)) {
      radius = Math.sqrt(Math.pow(this.currentPoint.x - center.x, 2.0) + Math.pow(this.currentPoint.y - center.y, 2.0));
    }
    ps.setIsMetric(this.isMetric);
    ps.setArcCenter(center);
    ps.setIsArc(true);
    ps.setRadius(radius);
    ps.setIsClockwise(clockwise);
    this.points.add(ps);
    this.currentPoint = nextPoint;
    return ps;
  }

  private PointSegment handleGCode(String code, List<String> args) {
    PointSegment ps = null;
    Point3d nextPoint = GcodePreprocessorUtils.updatePointWithCommand(args, this.currentPoint, this.inAbsoluteMode);
    if (code.length() > 1 && code.startsWith("0")) {
      code = code.substring(1);
    }
    switch (code) {
      case "0":
      ps = addLinearPointSegment(nextPoint, true);
      break;
      case "1":
      ps = addLinearPointSegment(nextPoint, false);
      break;
      case "2":
      ps = addArcPointSegment(nextPoint, true, args);
      break;
      case "3":
      ps = addArcPointSegment(nextPoint, false, args);
      break;
      case "20":
      this.isMetric = false;
      break;
      case "21":
      this.isMetric = true;
      break;
      case "90":
      this.inAbsoluteMode = true;
      break;
      case "90.1":
      this.inAbsoluteIJKMode = true;
      break;
      case "91":
      this.inAbsoluteMode = false;
      break;
      case "91.1":
      this.inAbsoluteIJKMode = false;
      break;
    }
    this.lastGcodeCommand = code;
    return ps;
  }

  public List<String> preprocessCommands(Collection<String> commands) {
    int count = commands.size();
    int interval = count / 1000;
    List<String> result = new ArrayList<>(count);
    int i = 0;
    double row = 0;
    for (String command : commands) {
      i++;
      row++;
      if (i >= interval) {
        System.out.println("row " + (int) row + " of " + count);
        i = 0;
      }
      result.addAll(preprocessCommand(command));
    }
    return result;
  }

  public List<String> preprocessCommand(String command) {
    List<String> result = new ArrayList<>();
    boolean hasComment = false;
    String newCommand = GcodePreprocessorUtils.removeComment(command);
    String rawCommand = newCommand;
    hasComment = (newCommand.length() != command.length());
    if (removeAllWhitespace) {
      newCommand = GcodePreprocessorUtils.removeAllWhitespace(newCommand);
    }
    newCommand = GcodePreprocessorUtils.removeM30(newCommand);
    if (newCommand.length() > 0) {
      if (speedOverride > 0) {
        newCommand = GcodePreprocessorUtils.overrideSpeed(newCommand, speedOverride);
      }
      if (truncateDecimalLength > 0) {
        newCommand = GcodePreprocessorUtils.truncateDecimals(truncateDecimalLength, newCommand);
      }
      if (convertArcsToLines) {
        List<String> arcLines = convertArcsToLines(newCommand);
        if (arcLines != null) {
          result.addAll(arcLines);
        } else {
          result.add(newCommand);
        }
      } else {
        if (hasComment) {
          result.add(command.replace(rawCommand, newCommand));
        } else {
          result.add(newCommand);
        }
      }
    } else {
      if (hasComment) {
        result.add(command);
      }
    }
    return result;
  }

  public List<String> convertArcsToLines(String command) {
    List<String> result = null;
    Point3d start = new Point3d(this.currentPoint);
    PointSegment ps = addCommand(command);
    if (ps == null || !ps.isArc()) {
      return result;
    }
    List<PointSegment> psl = expandArc();
    if (psl == null) {
      return result;
    }
    int index;
    StringBuilder sb;
    result = new ArrayList<>(psl.size());
    sb = new StringBuilder("#.");
    for (index = 0; index < truncateDecimalLength; index++) {
      sb.append("#");
    }
    DecimalFormat df = new DecimalFormat(sb.toString());
    index = 0;
    for (PointSegment segment : psl) {
      Point3d end = segment.point();
      result.add(GcodePreprocessorUtils.generateG1FromPoints(start, end, this.inAbsoluteMode, df));
      start = segment.point();
    }
    return result;
  }
}