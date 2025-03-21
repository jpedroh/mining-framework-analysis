package org.openpnp.machine.reference.solutions;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.LengthConverter;
import org.openpnp.machine.reference.ReferenceCamera;
import org.openpnp.machine.reference.ReferenceHead;
import org.openpnp.machine.reference.ReferenceMachine;
import org.openpnp.machine.reference.ReferenceNozzle;
import org.openpnp.machine.reference.axis.ReferenceControllerAxis;
import org.openpnp.machine.reference.axis.ReferenceControllerAxis.BacklashCompensationMethod;
import org.openpnp.machine.reference.feeder.ReferenceTubeFeeder;
import org.openpnp.model.AxesLocation;
import org.openpnp.model.Length;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.model.Package;
import org.openpnp.model.Part;
import org.openpnp.model.Solutions;
import org.openpnp.model.Solutions.Milestone;
import org.openpnp.model.Solutions.State;
import org.openpnp.spi.Axis.Type;
import org.openpnp.spi.Camera;
import org.openpnp.spi.CoordinateAxis;
import org.openpnp.spi.Head;
import org.openpnp.spi.HeadMountable;
import org.openpnp.spi.MotionPlanner.CompletionType;
import org.openpnp.spi.Nozzle;
import org.openpnp.util.MovableUtils;
import org.openpnp.util.NanosecondTime;
import org.openpnp.util.SimpleGraph;
import org.openpnp.util.UiUtils;
import org.openpnp.util.VisionUtils;
import org.openpnp.vision.pipeline.CvStage.Result.Circle;
import org.pmw.tinylog.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This helper class implements the Issues & Solutions for the Calibration Milestone. 
 */
public class CalibrationSolutions implements Solutions.Subject {
  @Attribute(required = false) private int backlashCalibrationPasses = 4;

  @Attribute(required = false) private double errorDampening = 0.9;

  @Attribute(required = false) private double backlashTestMoveMm = 10;

  @Attribute(required = false) private double backlashTestMoveLargeMm = 90;

  @Attribute(required = false) private double stepTestMm = 1;

  @Attribute(required = false) private double maxSneakUpOffsetMm = 2.5;

  @Attribute(required = false) private double acceptableSneakUpOffsetMm = 0.8;

  @Element(required = false) private double[] backlashProbingSpeeds = new double[] { 0.25, 0.33, 0.5, 0.75, 1 };

  @Attribute(required = false) private double backlashDistanceFactor = Math.pow(2.0, 0.5);

  @Attribute(required = false) private int nozzleOffsetAngles = 6;

  @Attribute(required = false) private long extraVacuumDwellMs = 300;

  @Attribute(required = false) private long machineSettleMs = 500;

  public CalibrationSolutions setMachine(ReferenceMachine machine) {
    this.machine = machine;
    return this;
  }

  private ReferenceMachine machine;

  @Override public void findIssues(Solutions solutions) {
    if (solutions.isTargeting(Milestone.Calibration)) {
      for (Head h : machine.getHeads()) {
        if (h instanceof ReferenceHead) {
          ReferenceHead head = (ReferenceHead) h;
          ReferenceCamera defaultCamera = null;
          try {
            defaultCamera = (ReferenceCamera) head.getDefaultCamera();
          } catch (Exception e) {
          }
          Nozzle defaultNozzle = null;
          try {
            defaultNozzle = head.getDefaultNozzle();
          } catch (Exception e1) {
          }
          if (defaultCamera != null) {
            for (Camera camera : head.getCameras()) {
              if (camera instanceof ReferenceCamera) {
                perDownLookingCameraSolutions(solutions, head, defaultCamera, defaultNozzle, (ReferenceCamera) camera);
              }
            }
            for (Nozzle nozzle : head.getNozzles()) {
              if (nozzle instanceof ReferenceNozzle) {
                perNozzleSolutions(solutions, head, defaultCamera, defaultNozzle, (ReferenceNozzle) nozzle);
              }
            }
            perHeadSolutions(solutions, head, defaultCamera);
          }
        }
      }
      Camera defaultCamera = null;
      Nozzle defaultNozzle = null;
      try {
        defaultCamera = VisionUtils.getBottomVisionCamera();
        Head head = machine.getDefaultHead();
        defaultNozzle = head.getDefaultNozzle();
      } catch (Exception e1) {
      }
      if (defaultCamera != null && defaultNozzle != null) {
        for (Camera camera : machine.getCameras()) {
          if (camera instanceof ReferenceCamera) {
            perUpLookingCameraSolutions(solutions, defaultCamera, defaultNozzle, (ReferenceCamera) camera);
          }
        }
      }
    }
  }

  private void perHeadSolutions(Solutions solutions, ReferenceHead head, ReferenceCamera defaultCamera) {
  }

  private void perDownLookingCameraSolutions(Solutions solutions, ReferenceHead head, ReferenceCamera defaultCamera, Nozzle defaultNozzle, ReferenceCamera camera) {
    VisionSolutions visualSolutions = machine.getVisionSolutions();
    if (visualSolutions.isSolvedPrimaryXY(head)) {
      if (camera == defaultCamera) {
        CoordinateAxis rawAxisX = HeadSolutions.getRawAxis(machine, camera.getAxisX());
        CoordinateAxis rawAxisY = HeadSolutions.getRawAxis(machine, camera.getAxisY());
        for (CoordinateAxis rawAxis : new CoordinateAxis[] { rawAxisX, rawAxisY }) {
          if (rawAxis instanceof ReferenceControllerAxis) {
            ReferenceControllerAxis axis = (ReferenceControllerAxis) rawAxis;
            BacklashCompensationMethod oldMethod = axis.getBacklashCompensationMethod();
            Length oldOffset = axis.getBacklashOffset();
            Length oldSneakUp = axis.getSneakUpOffset();
            double oldSpeed = axis.getBacklashSpeedFactor();
            Length oldAcceptableTolerance = axis.getAcceptableTolerance();
            solutions.add(new Solutions.Issue(camera, "Calibrate backlash compensation for axis " + axis.getName() + ".", "Automatically calibrates the backlash compensation for " + axis.getName() + " using the primary calibration fiducial.", Solutions.Severity.Fundamental, "https://github.com/openpnp/openpnp/wiki/Calibration-Solutions#calibrating-backlash-compensation") {
              {
                tolerance = oldAcceptableTolerance;
              }

              private Length tolerance;

              @Override public void activate() throws Exception {
                MainFrame.get().getMachineControls().setSelectedTool(camera);
                camera.ensureCameraVisible();
              }

              @Override public String getExtendedDescription() {
                return "<html>" + "<p>Backlash compensation is used to avoid the effects of any looseness or play in the mechanical " + "linkages of machine axes. More information can be found in the Wiki (press the blue Info button below).</p><br/>" + "<p>Set the acceptable <strong>Tolerance \u00b1</strong> as high as possible to allow for a more efficient backlash " + "compensation method, avoiding extra moves and direction changes.</p><br/>" + "<p><span color=\"red\">CAUTION 1</span>: The camera " + camera.getName() + " will move over the primary fiducial " + "and then perform a calibration motion pattern, moving the axis " + axis.getName() + " over its full soft-limit range.</p><br/>" + "<p><span color=\"red\">CAUTION 2</span>: The machine will also perform a visual homing cycle, once the new backlash " + "compensation method is established. This is done to recalibrate the coordinate system that might be affected by the " + "new method.</p><br/>" + "<p>When ready, press Accept.</p>" + (getState() == State.Solved ? "<br/><h4>Results:</h4>" + "<table>" + "<tr><td align=\"right\">Detected Backlash:</td>" + "<td>" + axis.getBacklashOffset() + "</td></tr>" + "<tr><td align=\"right\">Selected Method:</td>" + "<td>" + axis.getBacklashCompensationMethod().toString() + "</td></tr>" + "<tr><td align=\"right\">Sneak-up Distance:</td>" + "<td>" + axis.getSneakUpOffset() + "</td></tr>" + "<tr><td align=\"right\">Speed Factor:</td>" + "<td>" + axis.getBacklashSpeedFactor() + "</td></tr>" + "<tr><td align=\"right\">Applicable Resolution:</td>" + "<td>" + String.format("%.4f", getAxisCalibrationTolerance(camera, axis, true)) + " mm</td></tr>" + "</table>" : "") + "</html>";
              }

              @Override public Solutions.Issue.CustomProperty[] getProperties() {
                return new Solutions.Issue.CustomProperty[] { new Solutions.Issue.LengthProperty("Tolerance \u00b1", "Set the targe tolerance. By granting a larger tolerance, a more efficient backlash compensation method may be eligible.") {
                  @Override public Length get() {
                    return tolerance;
                  }

                  @Override public void set(Length value) {
                    tolerance = value;
                  }
                } };
              }

              @Override public void setState(Solutions.State state) throws Exception {
                if (state == State.Solved) {
                  if (!visualSolutions.isSolvedPrimaryXY(head)) {
                    throw new Exception("The head " + head.getName() + " primary fiducial location X and Y must be set first.");
                  }
                  final State oldState = getState();
                  UiUtils.submitUiMachineTask(() -> {
                    axis.setAcceptableTolerance(tolerance);
                    calibrateAxisBacklash(head, camera, camera, axis);
                    return true;
                  }, (result) -> {
                    UiUtils.messageBoxOnException(() -> super.setState(state));
                    solutions.setSolutionsIssueSolved(this, true);
                  }, (t) -> {
                    UiUtils.showError(t);
                    UiUtils.messageBoxOnException(() -> setState(oldState));
                  });
                } else {
                  axis.setBacklashCompensationMethod(oldMethod);
                  axis.setBacklashOffset(oldOffset);
                  axis.setSneakUpOffset(oldSneakUp);
                  axis.setBacklashSpeedFactor(oldSpeed);
                  axis.setAcceptableTolerance(oldAcceptableTolerance);
                  solutions.setSolutionsIssueSolved(this, false);
                  super.setState(state);
                }
              }
            });
          }
        }
      }
    }
  }

  private void perNozzleSolutions(Solutions solutions, ReferenceHead head, ReferenceCamera defaultCamera, Nozzle defaultNozzle, ReferenceNozzle nozzle) {
    VisionSolutions visualSolutions = machine.getVisionSolutions();
    if (visualSolutions.isSolvedPrimaryXY(head) && visualSolutions.isSolvedPrimaryZ(head) && (nozzle == defaultNozzle || nozzle.getHeadOffsets().isInitialized())) {
      final Location oldNozzleOffsets = nozzle.getHeadOffsets();
      final Length oldTestObjectDiameter = head.getCalibrationTestObjectDiameter();
      solutions.add(visualSolutions.new VisionFeatureIssue(nozzle, defaultCamera, oldTestObjectDiameter, "Calibrate precise camera \u2194 nozzle " + nozzle.getName() + " offsets.", "Use a test object to perform the precision camera \u2194 nozzle " + nozzle.getName() + " offsets calibration.", Solutions.Severity.Fundamental, "https://github.com/openpnp/openpnp/wiki/Calibration-Solutions#calibrating-precision-camera-to-nozzle-offsets") {
        @Override public String getExtendedDescription() {
          return "<html>" + "<p>To calibrate precision camera \u2194 nozzle offsets, we let the nozzle pick, rotate and place a small " + "test object and then measure the resulting offsets using the camera.</p><br/>" + "<p>Instructions about suitable test objects etc. must be obtained in the OpenPnP " + "Wiki. Press the blue Info button (below) to open the Wiki.</p><br/>" + "<p>Place the calibration test object onto the calibration primary fiducial.</p><br/>" + "<p>Jog camera " + defaultCamera.getName() + " over the test object. Target it with the cross-hairs.</p><br/>" + "<p>Adjust the <strong>Feature diameter</strong> up and down and see if it is detected right in the " + "camera view. A green circle and cross-hairs should appear and hug the test object contour. " + "Zoom the camera using the scroll-wheel.</p><br/>" + "<p><strong color=\"red\">Caution:</strong> The nozzle " + nozzle.getName() + " will move to the test object " + "and perform the calibration pick & place pattern. Make sure to load the right nozzle tip and " + "ready the vacuum system.</p><br/>" + "<p>When ready, press Accept.</p>" + (getState() == State.Solved && !nozzle.getHeadOffsets().equals(oldNozzleOffsets) ? "<br/><h4>Results:</h4>" + "<table>" + "<tr><td align=\"right\">Detected Nozzle Head Offsets:</td>" + "<td>" + nozzle.getHeadOffsets() + "</td></tr>" + "<tr><td align=\"right\">Previous Nozzle Head Offsets:</td>" + "<td>" + oldNozzleOffsets + "</td></tr>" + "<tr><td align=\"right\">Difference:</td>" + "<td>" + nozzle.getHeadOffsets().subtract(oldNozzleOffsets) + "</td></tr>" + "</table>" : "") + "</html>";
        }

        @Override public void setState(Solutions.State state) throws Exception {
          if (state == State.Solved) {
            if (!visualSolutions.isSolvedPrimaryXY(head)) {
              throw new Exception("The head " + head.getName() + " primary fiducial location X and Y must be set first.");
            }
            if (!visualSolutions.isSolvedPrimaryZ(head)) {
              throw new Exception("The head " + head.getName() + " primary fiducial location Z must be set first.");
            }
            if (!(nozzle == defaultNozzle || nozzle.getHeadOffsets().isInitialized())) {
              throw new Exception("The nozzle " + nozzle.getName() + " head offsets must be roughly set first. " + "Use the \"Noozle " + nozzle.getName() + " offset for the primary fiducial\" calibration.");
            }
            final State oldState = getState();
            UiUtils.submitUiMachineTask(() -> {
              Circle testObject = visualSolutions.getSubjectPixelLocation(defaultCamera, null, new Circle(0, 0, featureDiameter), 0, null, null);
              head.setCalibrationTestObjectDiameter(defaultCamera.getUnitsPerPixelPrimary().getLengthX().multiply(testObject.getDiameter()));
              calibrateNozzleOffsets(head, defaultCamera, nozzle);
              return true;
            }, (result) -> {
              UiUtils.messageBoxOnException(() -> super.setState(state));
              solutions.setSolutionsIssueSolved(this, true);
            }, (t) -> {
              UiUtils.showError(t);
              UiUtils.messageBoxOnException(() -> setState(oldState));
            });
          } else {
            nozzle.setHeadOffsets(oldNozzleOffsets);
            head.setCalibrationTestObjectDiameter(oldTestObjectDiameter);
            solutions.setSolutionsIssueSolved(this, false);
            super.setState(state);
          }
        }
      });
    }
  }

  private void perUpLookingCameraSolutions(Solutions solutions, Camera defaultCamera, Nozzle defaultNozzle, ReferenceCamera camera) {
  }

  public void calibrateAxisBacklash(ReferenceHead head, ReferenceCamera camera, HeadMountable movable, ReferenceControllerAxis axis) throws Exception {
    if (!(axis.isSoftLimitLowEnabled() && axis.isSoftLimitHighEnabled())) {
      throw new Exception("Axis " + axis.getName() + " must have soft limits enabled for backlash calibration.");
    }
    if (!head.getCalibrationPrimaryFiducialLocation().isInitialized()) {
      throw new Exception("Head " + head.getName() + " primary fiducial location must be set for backlash calibration.");
    }
    if (!head.getCalibrationPrimaryFiducialDiameter().isInitialized()) {
      throw new Exception("Head " + head.getName() + " primary fiducial diameter must be set for backlash calibration.");
    }
    axis.setBacklashCompensationMethod(BacklashCompensationMethod.None);
    Location location = head.getCalibrationPrimaryFiducialLocation();
    Length fiducialDiameter = head.getCalibrationPrimaryFiducialDiameter();
    MovableUtils.moveToLocationAtSafeZ(movable, location);
    location = machine.getVisionSolutions().centerInOnSubjectLocation(camera, movable, fiducialDiameter, "Backlash Calibration Start Location", false);
    Location unit = new Location(LengthUnit.Millimeters, (axis.getType() == Type.X ? 1 : 0), (axis.getType() == Type.Y ? 1 : 0), 0, 0);
    String signPositive = axis.getType() == Type.X ? " \u25ba" : " \u25b2";
    String signNegative = axis.getType() == Type.X ? " \u25c4" : " \u25bc";
    AxesLocation axesLocation0 = movable.toRaw(location);
    AxesLocation axesLocation1 = movable.toRaw(location.add(unit));
    double mmAxis = axesLocation1.getCoordinate(axis) - axesLocation0.getCoordinate(axis);
    double minimumSpeed = backlashProbingSpeeds[0];
    double unitsPerMm = new Length(1, LengthUnit.Millimeters).convertToUnits(axis.getUnits()).getValue();
    double stepMm = getAxisCalibrationTolerance(camera, axis, false);
    double toleranceMm = getAxisCalibrationTolerance(camera, axis, false);
    Length acceptableTolerance = axis.getAcceptableTolerance();
    if (acceptableTolerance != null) {
      toleranceMm = Math.max(1, Math.round(acceptableTolerance.convertToUnits(LengthUnit.Millimeters).getValue() / toleranceMm)) * toleranceMm;
    }
    acceptableTolerance = new Length(toleranceMm, LengthUnit.Millimeters);
    axis.setAcceptableTolerance(acceptableTolerance);
    double toleranceUnits = acceptableTolerance.convertToUnits(axis.getUnits()).getValue();
    final String ERROR = "E";
    final String ABSOLUTE = "A";
    final String ABSOLUTE_RANDOM = "AR";
    final String RELATIVE = "R";
    final String SCALE = "S";
    final String TIME = "T";
    final String VELOCITY = "V";
    final String BACKLASH = "B";
    final String OVERSHOOT = "O";
    final String LIMIT = "L";
    SimpleGraph stepTestGraph = new SimpleGraph();
    stepTestGraph.setRelativePaddingLeft(0.05);
    SimpleGraph.DataScale errorScale = stepTestGraph.getScale(ERROR);
    errorScale.setRelativePaddingBottom(0.2);
    errorScale.setSymmetricIfSigned(true);
    errorScale.setColor(SimpleGraph.getDefaultGridColor());
    stepTestGraph.getRow(ERROR, ABSOLUTE).setColor(new Color(0xFF, 0, 0));
    stepTestGraph.getRow(ERROR, RELATIVE).setColor(new Color(0, 0x5B, 0xD9));
    stepTestGraph.getRow(ERROR, RELATIVE).setMarkerShown(true);
    stepTestGraph.getRow(ERROR, RELATIVE).setLineShown(false);
    stepTestGraph.getRow(ERROR, ABSOLUTE_RANDOM).setColor(new Color(0xBB, 0x77, 0));
    stepTestGraph.getRow(ERROR, ABSOLUTE_RANDOM).setMarkerShown(true);
    stepTestGraph.getRow(ERROR, ABSOLUTE_RANDOM).setLineShown(false);
    stepTestGraph.getRow(ERROR, LIMIT + 0).setColor(new Color(0, 0, 0x77));
    stepTestGraph.getRow(ERROR, LIMIT + 1).setColor(new Color(0, 0, 0x77));
    SimpleGraph distanceGraph = new SimpleGraph();
    distanceGraph.setLogarithmic(true);
    distanceGraph.setRelativePaddingLeft(0.05);
    SimpleGraph.DataScale distScale = distanceGraph.getScale(SCALE);
    distScale.setRelativePaddingBottom(0.55);
    distScale.setColor(SimpleGraph.getDefaultGridColor());
    distanceGraph.getRow(SCALE, BACKLASH + 0).setColor(new Color(00, 0x5B, 0xD9));
    distanceGraph.getRow(SCALE, OVERSHOOT + 0).setColor(new Color(0xFF, 0, 0));
    distanceGraph.getRow(SCALE, BACKLASH + 1).setColor(new Color(00, 0x5B, 0xD9, 128));
    distanceGraph.getRow(SCALE, OVERSHOOT + 1).setColor(new Color(0xFF, 0, 0, 128));
    distanceGraph.getRow(SCALE, LIMIT + 0).setColor(new Color(0, 0, 0x77));
    distanceGraph.getRow(SCALE, LIMIT + 1).setColor(new Color(0, 0, 0x77));
    distanceGraph.getRow(SCALE, ABSOLUTE_RANDOM).setColor(new Color(0xBB, 0x77, 0));
    distanceGraph.getRow(SCALE, ABSOLUTE_RANDOM).setMarkerShown(true);
    distanceGraph.getRow(SCALE, ABSOLUTE_RANDOM).setLineShown(false);
    SimpleGraph.DataScale timeScale = distanceGraph.getScale(TIME);
    timeScale.setLogarithmic(true);
    timeScale.setRelativePaddingTop(0.55);
    timeScale.setRelativePaddingBottom(0.1);
    timeScale.setColor(SimpleGraph.getDefaultGridColor());
    distanceGraph.getRow(TIME, TIME + 0).setColor(new Color(0, 0x80, 0));
    distanceGraph.getRow(TIME, TIME + 1).setColor(new Color(0, 0x80, 0, 128));
    SimpleGraph speedGraph = new SimpleGraph();
    speedGraph.setRelativePaddingLeft(0.05);
    SimpleGraph.DataScale speedScale = speedGraph.getScale(SCALE);
    speedScale.setRelativePaddingBottom(0.2);
    speedScale.setColor(SimpleGraph.getDefaultGridColor());
    speedGraph.getRow(SCALE, BACKLASH).setColor(new Color(0xFF, 0, 0));
    speedGraph.getRow(SCALE, BACKLASH).setMarkerShown(true);
    SimpleGraph.DataScale velocityScale = speedGraph.getScale(VELOCITY);
    velocityScale.setRelativePaddingTop(0.1);
    velocityScale.setRelativePaddingBottom(0.2);
    speedGraph.getRow(VELOCITY, VELOCITY + 0).setColor(new Color(0, 0x80, 0));
    speedGraph.getRow(VELOCITY, VELOCITY + 0).setMarkerShown(true);
    speedGraph.getRow(VELOCITY, VELOCITY + 1).setColor(new Color(0, 0x80, 0, 128));
    speedGraph.getRow(VELOCITY, VELOCITY + 1).setMarkerShown(true);
    MovableUtils.moveToLocationAtSafeZ(movable, location);
    movable.waitForCompletion(CompletionType.WaitForStillstand);
    Location timedLocation = displacedAxisLocation(movable, axis, location, -backlashTestMoveMm * mmAxis, false);
    double dtBaseline;
    {
      Thread.sleep(machineSettleMs);
      double t0 = NanosecondTime.getRuntimeSeconds();
      movable.moveTo(timedLocation, 1.0);
      movable.waitForCompletion(CompletionType.WaitForStillstand);
      movable.moveTo(location, 1.0);
      movable.waitForCompletion(CompletionType.WaitForStillstand);
      double t1 = NanosecondTime.getRuntimeSeconds();
      dtBaseline = (t1 - t0) / 2;
    }
    for (double speed : backlashProbingSpeeds) {
      {
        Thread.sleep(machineSettleMs);
        double t0 = NanosecondTime.getRuntimeSeconds();
        movable.moveTo(timedLocation, speed);
        movable.waitForCompletion(CompletionType.WaitForStillstand);
        double t1 = NanosecondTime.getRuntimeSeconds();
        speedGraph.getRow(VELOCITY, VELOCITY + 1).recordDataPoint(speed, dtBaseline / (t1 - t0));
      }
      {
        Thread.sleep(machineSettleMs);
        double t0 = NanosecondTime.getRuntimeSeconds();
        movable.moveTo(location, speed);
        movable.waitForCompletion(CompletionType.WaitForStillstand);
        double t1 = NanosecondTime.getRuntimeSeconds();
        double effSpeed = dtBaseline / (t1 - t0);
        speedGraph.getRow(VELOCITY, VELOCITY + 0).recordDataPoint(speed, effSpeed);
        if (speed == minimumSpeed) {
          if (effSpeed > Math.sqrt(speed)) {
            throw new Exception("Speed factor seems not to be effective. " + "Should move at " + (int) (speed * 100) + "%, moved at " + (int) (effSpeed * 100) + "%. " + "Check your driver motion control and axis configuration.");
          }
        }
      }
    }
    MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, -backlashTestMoveLargeMm * mmAxis, false));
    int step = 0;
    Location referenceLocation = location;
    Location stepLocation0 = null;
    for (double stepPos = -stepTestMm / 2; stepPos < stepTestMm / 2; stepPos += stepMm) {
      step++;
      Location startMoveLocation = displacedAxisLocation(movable, axis, location, (stepPos - stepTestMm) * mmAxis, false);
      movable.moveTo(startMoveLocation);
      Location nominalStepLocation = displacedAxisLocation(movable, axis, location, stepPos * mmAxis, false);
      movable.moveTo(nominalStepLocation, minimumSpeed);
      Location stepLocation1 = machine.getVisionSolutions().getDetectedLocation(camera, movable, location, fiducialDiameter, "Accuracy Test Step " + step, false);
      if (stepLocation0 != null) {
        Length absoluteErr = stepLocation1.subtract(referenceLocation).dotProduct(unit);
        double absoluteErrUnits = absoluteErr.convertToUnits(axis.getUnits()).getValue();
        stepTestGraph.getRow(ERROR, ABSOLUTE).recordDataPoint(step, absoluteErrUnits);
        Length relativeErr = stepLocation1.subtract(stepLocation0).dotProduct(unit);
        double relativeErrorUnits = relativeErr.convertToUnits(axis.getUnits()).getValue();
        stepTestGraph.getRow(ERROR, RELATIVE).recordDataPoint(step, relativeErrorUnits);
        stepTestGraph.getRow(ERROR, LIMIT + 0).recordDataPoint(step, -toleranceUnits);
        stepTestGraph.getRow(ERROR, LIMIT + 1).recordDataPoint(step, toleranceUnits);
      } else {
        referenceLocation = location.add(stepLocation1.subtract(referenceLocation));
      }
      stepLocation0 = stepLocation1;
    }
    MovableUtils.moveToLocationAtSafeZ(movable, location, minimumSpeed);
    axis.setBacklashCompensationMethod(BacklashCompensationMethod.None);
    ArrayList<Double> backlashProbingDistances = new ArrayList<>();
    double distance0 = Double.NEGATIVE_INFINITY;
    for (double distanceMm = stepMm * 2; distanceMm <= backlashTestMoveMm; distanceMm *= backlashDistanceFactor) {
      if (distanceMm - distance0 >= stepMm) {
        backlashProbingDistances.add(distanceMm);
        distance0 = distanceMm;
      }
    }
    backlashProbingDistances.add(backlashTestMoveLargeMm);
    Collections.reverse(backlashProbingDistances);
    double[] backlashOffsetByDistance = new double[backlashProbingDistances.size()];
    int iDistance = 0;
    double maxBacklash = Double.NEGATIVE_INFINITY;
    double maxBacklashDistance = 0;
    boolean maxBacklashOpen = true;
    double minBacklash = Double.POSITIVE_INFINITY;
    double minBacklashDistance = 0;
    LengthConverter lengthConverter = new LengthConverter();
    for (int pass = 0; pass < 2; pass++) {
      for (double distance : backlashProbingDistances) {
        for (int reverse = 1; reverse >= 0; reverse--) {
          if (reverse == 1 && distance > backlashTestMoveMm) {
            continue;
          }
          Location displacedAxisLocation = displacedAxisLocation(movable, axis, location, -distance * mmAxis, distance > backlashTestMoveMm);
          Length effectiveDistance0 = location.getLinearLengthTo(displacedAxisLocation);
          if (pass == 0) {
            if (reverse == 0) {
              MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, -backlashTestMoveLargeMm * mmAxis, distance > backlashTestMoveMm));
            }
            MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation);
            movable.moveTo(location, minimumSpeed);
          } else {
            if (reverse == 0) {
              MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, -backlashTestMoveMm * mmAxis, distance > backlashTestMoveMm), minimumSpeed);
            }
            MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation, minimumSpeed);
            movable.waitForCompletion(CompletionType.WaitForStillstand);
            Thread.sleep(machineSettleMs);
            double t0 = NanosecondTime.getRuntimeSeconds();
            movable.moveTo(location);
            movable.waitForCompletion(CompletionType.WaitForStillstand);
            double t1 = NanosecondTime.getRuntimeSeconds();
            if (reverse == 0) {
              distanceGraph.getRow(TIME, TIME + 0).recordDataPoint(effectiveDistance0.convertToUnits(axis.getUnits()).getValue(), t1 - t0);
            }
          }
          String passTitle = pass == 0 ? "Backlash at Sneak-up Distance " : "Overshoot at Distance ";
          String distanceOutput = lengthConverter.convertForward(effectiveDistance0);
          Location effective0 = machine.getVisionSolutions().getDetectedLocation(camera, movable, location, fiducialDiameter, passTitle + distanceOutput + signPositive, false);
          displacedAxisLocation = displacedAxisLocation(movable, axis, location, distance * mmAxis, distance > backlashTestMoveMm);
          Length effectiveDistance1 = location.getLinearLengthTo(displacedAxisLocation);
          if (pass == 0) {
            if (reverse == 0) {
              MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, backlashTestMoveLargeMm * mmAxis, distance > backlashTestMoveMm));
            }
            MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation);
            movable.moveTo(location, minimumSpeed);
          } else {
            if (reverse == 0) {
              MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, backlashTestMoveMm * mmAxis, distance > backlashTestMoveMm), minimumSpeed);
            }
            MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation, minimumSpeed);
            movable.waitForCompletion(CompletionType.WaitForStillstand);
            Thread.sleep(machineSettleMs);
            double t0 = NanosecondTime.getRuntimeSeconds();
            movable.moveTo(location);
            movable.waitForCompletion(CompletionType.WaitForStillstand);
            double t1 = NanosecondTime.getRuntimeSeconds();
            if (reverse == 0) {
              distanceGraph.getRow(TIME, TIME + 1).recordDataPoint(effectiveDistance1.convertToUnits(axis.getUnits()).getValue(), t1 - t0);
            }
          }
          distanceOutput = lengthConverter.convertForward(effectiveDistance1);
          Location effective1 = machine.getVisionSolutions().getDetectedLocation(camera, movable, location, fiducialDiameter, passTitle + distanceOutput + signNegative, false);
          double mmError = effective1.subtract(effective0).dotProduct(unit).getValue();
          if (movable == camera) {
            mmError = -mmError;
          }
          if (pass == 0 && reverse == 0) {
            backlashOffsetByDistance[iDistance++] = mmError;
          }
          double errorUnits = mmError * unitsPerMm;
          double effectiveDistance = effectiveDistance0.add(effectiveDistance1).multiply(0.5).convertToUnits(axis.getUnits()).getValue();
          if (pass == 0) {
            distanceGraph.getRow(SCALE, BACKLASH + reverse).recordDataPoint(effectiveDistance, errorUnits);
          } else {
            distanceGraph.getRow(SCALE, OVERSHOOT + reverse).recordDataPoint(effectiveDistance, (maxBacklash - errorUnits) / 2);
          }
          if (pass == 0 && distance > maxBacklash && distance <= maxSneakUpOffsetMm) {
            if (maxBacklashOpen && mmError >= maxBacklash - 2 * toleranceMm) {
              if (mmError > maxBacklash) {
                maxBacklash = mmError;
              }
              maxBacklashDistance = distance;
              if (mmError < minBacklash) {
                minBacklash = mmError;
                minBacklashDistance = distance;
              }
            } else {
              if (maxBacklashOpen) {
                double minBacklashByTolerance = maxBacklash - 2 * toleranceMm;
                distanceGraph.getRow(SCALE, LIMIT + 0).recordDataPoint(maxBacklashDistance * unitsPerMm, 0);
                distanceGraph.getRow(SCALE, LIMIT + 0).recordDataPoint(maxBacklashDistance * unitsPerMm + 1e-8, minBacklashByTolerance * unitsPerMm);
                distanceGraph.getRow(SCALE, LIMIT + 0).recordDataPoint(maxSneakUpOffsetMm * unitsPerMm, minBacklashByTolerance * unitsPerMm);
                distanceGraph.getRow(SCALE, LIMIT + 1).recordDataPoint(maxBacklashDistance * unitsPerMm, minBacklashByTolerance * unitsPerMm);
                distanceGraph.getRow(SCALE, LIMIT + 1).recordDataPoint(maxBacklashDistance * unitsPerMm + 1e-8, maxBacklash * unitsPerMm);
                distanceGraph.getRow(SCALE, LIMIT + 1).recordDataPoint(maxSneakUpOffsetMm * unitsPerMm, maxBacklash * unitsPerMm);
                maxBacklashOpen = false;
              }
            }
          }
        }
      }
    }
    double sneakUpOffset = Math.max(maxBacklash, maxBacklashDistance);
    double[] backlashOffsetBySpeed = new double[backlashProbingSpeeds.length];
    int iSpeed = 0;
    for (double speed : backlashProbingSpeeds) {
      axis.setBacklashCompensationMethod(speed < 1.0 ? BacklashCompensationMethod.DirectionalSneakUp : BacklashCompensationMethod.DirectionalCompensation);
      axis.setBacklashOffset(new Length(0, LengthUnit.Millimeters));
      axis.setSneakUpOffset(new Length(speed < 1.0 ? sneakUpOffset : 0, LengthUnit.Millimeters));
      axis.setBacklashSpeedFactor(speed);
      double offsetMm = 0;
      for (int pass = 0; pass < backlashCalibrationPasses; pass++) {
        MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, -backlashTestMoveLargeMm * mmAxis, false));
        movable.moveTo(location);
        Location effective0 = machine.getVisionSolutions().getDetectedLocation(camera, movable, location, fiducialDiameter, "Backlash at Speed " + speed + "\u00d7" + signPositive, false);
        MovableUtils.moveToLocationAtSafeZ(movable, displacedAxisLocation(movable, axis, location, backlashTestMoveLargeMm * mmAxis, false));
        movable.moveTo(location);
        Location effective1 = machine.getVisionSolutions().getDetectedLocation(camera, movable, location, fiducialDiameter, "Backlash at Speed " + speed + "\u00d7" + signNegative, false);
        double mmError = effective1.subtract(effective0).dotProduct(unit).getValue();
        if (movable == camera) {
          mmError = -mmError;
        }
        offsetMm += mmError * mmAxis * errorDampening;
        if (pass == 0 && mmError <= -toleranceMm) {
          break;
        }
        axis.setBacklashOffset(new Length(offsetMm, LengthUnit.Millimeters).convertToUnits(axis.getDriver().getUnits()));
        if (Math.abs(mmError) < toleranceMm) {
          break;
        }
      }
      backlashOffsetBySpeed[iSpeed++] = offsetMm;
      double offsetUnits = new Length(offsetMm, LengthUnit.Millimeters).convertToUnits(axis.getUnits()).getValue();
      speedGraph.getRow(SCALE, BACKLASH).recordDataPoint(speed, offsetUnits);
    }
    int consistent = 0;
    double offsetMmSum = 0;
    for (double offsetMm : backlashOffsetBySpeed) {
      if (offsetMm <= -toleranceMm || Math.abs(offsetMm - backlashOffsetBySpeed[0]) > toleranceMm) {
        break;
      }
      offsetMmSum += offsetMm;
      consistent++;
    }
    double offsetMmAvg = offsetMmSum / consistent;
    double offsetMmMax = 0;
    iSpeed = 0;
    for (double offsetMm : backlashOffsetBySpeed) {
      offsetMmMax = Math.max(offsetMmMax, Math.abs(offsetMm));
      Logger.debug("Axis " + axis.getName() + " backlash offsets at speed factor " + backlashProbingSpeeds[iSpeed++] + " is " + offsetMm);
    }
    Logger.debug("Axis " + axis.getName() + " backlash offsets analysis, consistent: " + consistent + ", avg offset: " + offsetMmAvg + ", max offset: " + offsetMmMax);
    if (sneakUpOffset > acceptableSneakUpOffsetMm) {
      axis.setBacklashCompensationMethod(BacklashCompensationMethod.OneSidedPositioning);
      axis.setBacklashOffset(new Length(maxBacklash, LengthUnit.Millimeters));
      axis.setSneakUpOffset(new Length(0, LengthUnit.Millimeters));
      axis.setBacklashSpeedFactor(backlashProbingSpeeds[0]);
      Logger.debug("Axis " + axis.getName() + " backlash offsets analysis, sneakUpOffset: " + sneakUpOffset + " unacceptable (> " + acceptableSneakUpOffsetMm + ")");
    } else {
      if (offsetMmAvg < sneakUpOffset - toleranceMm) {
        axis.setBacklashCompensationMethod(BacklashCompensationMethod.DirectionalSneakUp);
        axis.setBacklashOffset(new Length((minBacklash + maxBacklash) / 2, LengthUnit.Millimeters));
        axis.setSneakUpOffset(new Length(sneakUpOffset, LengthUnit.Millimeters));
        axis.setBacklashSpeedFactor(backlashProbingSpeeds[0]);
      } else {
        if (consistent == backlashProbingSpeeds.length) {
          if (offsetMmAvg < toleranceMm) {
            axis.setBacklashCompensationMethod(BacklashCompensationMethod.None);
          } else {
            axis.setBacklashCompensationMethod(BacklashCompensationMethod.DirectionalCompensation);
          }
          axis.setBacklashOffset(new Length(offsetMmAvg, LengthUnit.Millimeters));
          axis.setSneakUpOffset(new Length(0, LengthUnit.Millimeters));
          axis.setBacklashSpeedFactor(backlashProbingSpeeds[consistent - 1]);
        } else {
          if (consistent > 0) {
            axis.setBacklashCompensationMethod(BacklashCompensationMethod.DirectionalSneakUp);
            axis.setBacklashOffset(new Length(offsetMmAvg, LengthUnit.Millimeters));
            axis.setSneakUpOffset(new Length(sneakUpOffset, LengthUnit.Millimeters));
            axis.setBacklashSpeedFactor(backlashProbingSpeeds[consistent - 1]);
          } else {
            throw new Exception("Axis " + axis.getName() + " seems to overshoot, even at the lowest speed factor. " + "Make sure OpenPnP has effective acceleration/jerk control. " + "Automatic compensation not possible.");
          }
        }
      }
    }
    head.visualHome(machine, true);
    MovableUtils.moveToLocationAtSafeZ(movable, location);
    referenceLocation = machine.getVisionSolutions().centerInOnSubjectLocation(camera, movable, fiducialDiameter, "Backlash Compensation Test Location", false);
    final int fraction = 2;
    final double minLog = Math.log(stepMm);
    final double maxLog = Math.log(backlashTestMoveLargeMm);
    final double rangeLog = maxLog - minLog;
    step = 0;
    for (double stepPos = -stepTestMm / 2; stepPos < stepTestMm / 2; stepPos += stepMm * fraction) {
      step++;
      double randomDistance = Math.signum(Math.random() - 0.5) * Math.exp(Math.random() * rangeLog + minLog);
      Location startMoveLocation = displacedAxisLocation(movable, axis, location, randomDistance * mmAxis, false);
      movable.moveTo(startMoveLocation);
      Location nominalStepLocation = displacedAxisLocation(movable, axis, location, stepPos * mmAxis, false);
      movable.moveTo(nominalStepLocation);
      Location stepLocation1 = machine.getVisionSolutions().getDetectedLocation(camera, movable, location, fiducialDiameter, "Random Move Accuracy Test Step " + step, false);
      Length absoluteErr = stepLocation1.subtract(referenceLocation).dotProduct(unit);
      double absoluteErrUnits = absoluteErr.convertToUnits(axis.getUnits()).getValue();
      stepTestGraph.getRow(ERROR, ABSOLUTE_RANDOM).recordDataPoint(2 + (step - 1) * fraction, absoluteErrUnits);
      distanceGraph.getRow(SCALE, ABSOLUTE_RANDOM).recordDataPoint(Math.abs(randomDistance * mmAxis), absoluteErrUnits);
    }
    axis.setStepTestGraph(stepTestGraph);
    axis.setBacklashSpeedTestGraph(speedGraph);
    axis.setBacklashDistanceTestGraph(distanceGraph);
  }

  private double getAxisCalibrationTolerance(ReferenceCamera camera, ReferenceControllerAxis axis, boolean tolerance) {
    Length resolution = new Length(axis.getResolution(), axis.getDriver().getUnits());
    Length finestResolution = new Length(0.01, LengthUnit.Millimeters);
    resolution = resolution.multiply(Math.ceil(finestResolution.divide(resolution)));
    Length subPixelUnit = (axis.getType() == Type.X ? camera.getUnitsPerPixelPrimary().getLengthX() : camera.getUnitsPerPixelPrimary().getLengthY()).multiply(1.0 / machine.getVisionSolutions().getSuperSampling());
    if (tolerance) {
      resolution = subPixelUnit.multiply(Math.ceil(resolution.divide(subPixelUnit))).convertToUnits(LengthUnit.Millimeters);
      resolution = resolution.multiply(1.01);
    } else {
      if (resolution.compareTo(subPixelUnit) < 0) {
        resolution = subPixelUnit;
      }
    }
    return resolution.getValue();
  }

  private Location displacedAxisLocation(HeadMountable movable, ReferenceControllerAxis axis, Location location, double displacement, boolean fullRange) throws Exception {
    location = movable.toHeadLocation(location);
    AxesLocation axesLocation = movable.toRaw(location);
    if (fullRange) {
      axesLocation = axesLocation.put(displacement < 0 ? new AxesLocation(axis, axis.getSoftLimitLow()) : new AxesLocation(axis, axis.getSoftLimitHigh()));
    } else {
      axesLocation = axesLocation.add(new AxesLocation(axis, displacement));
      if (axesLocation.getLengthCoordinate(axis).compareTo(axis.getSoftLimitLow()) < 0) {
        axesLocation = new AxesLocation(axis, axis.getSoftLimitLow());
      }
      if (axesLocation.getLengthCoordinate(axis).compareTo(axis.getSoftLimitHigh()) > 0) {
        axesLocation = new AxesLocation(axis, axis.getSoftLimitHigh());
      }
    }
    Location newLocation = movable.toTransformed(axesLocation);
    newLocation = movable.toHeadMountableLocation(newLocation);
    return newLocation;
  }

  private void calibrateNozzleOffsets(ReferenceHead head, ReferenceCamera defaultCamera, ReferenceNozzle nozzle) throws Exception {
    try {
      Part testPart = new Part("TEST-OBJECT");
      testPart.setHeight(new Length(0.01, LengthUnit.Millimeters));
      Package packag = new Package("TEST-OBJECT-PACKAGE");
      testPart.setPackage(packag);
      ReferenceTubeFeeder feeder = new ReferenceTubeFeeder();
      feeder.setPart(testPart);
      MovableUtils.moveToLocationAtSafeZ(defaultCamera, head.getCalibrationPrimaryFiducialLocation());
      Location location = machine.getVisionSolutions().centerInOnSubjectLocation(defaultCamera, defaultCamera, head.getCalibrationTestObjectDiameter(), "Nozzle Offset Calibration", false);
      int accumulated = 0;
      Location offsetsDiff = new Location(LengthUnit.Millimeters);
      double da = 360.0 / nozzleOffsetAngles;
      for (double angle = -180 + da / 2; angle < 180; angle += da) {
        offsetsDiff = offsetsDiff.subtract(location);
        location = location.derive(head.getCalibrationPrimaryFiducialLocation(), false, false, true, false);
        feeder.setLocation(location.derive(null, null, null, angle));
        nozzle.moveToPickLocation(feeder);
        nozzle.pick(testPart);
        Thread.sleep(extraVacuumDwellMs);
        Location placementLocation = location.derive(null, null, null, angle + 180.0);
        nozzle.moveToPlacementLocation(placementLocation, testPart);
        nozzle.place();
        Thread.sleep(extraVacuumDwellMs);
        MovableUtils.moveToLocationAtSafeZ(defaultCamera, location);
        Location newlocation = machine.getVisionSolutions().centerInOnSubjectLocation(defaultCamera, defaultCamera, head.getCalibrationTestObjectDiameter(), "Nozzle Offset Calibration " + angle + "\u00b0", false);
        offsetsDiff = offsetsDiff.add(newlocation);
        accumulated += 2;
        Logger.debug("Nozzle " + nozzle.getName() + " has placed at offsets " + newlocation.subtract(location) + " at angle " + angle);
        location = newlocation;
      }
      offsetsDiff = offsetsDiff.multiply(1.0 / accumulated).multiply(1, 1, 0, 0);
      Location headOffsets = nozzle.getHeadOffsets().add(offsetsDiff);
      Logger.info("Set nozzle " + nozzle.getName() + " head offsets to " + headOffsets + " (previously " + nozzle.getHeadOffsets() + ")");
      nozzle.setHeadOffsets(headOffsets);
    }  finally {
      if (nozzle.getPart() != null) {
        nozzle.place();
      }
      MovableUtils.moveToLocationAtSafeZ(nozzle, nozzle.getLocation().deriveLengths(null, null, nozzle.getSafeZ(), 0.0));
    }
  }
}