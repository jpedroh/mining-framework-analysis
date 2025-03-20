package org.openpnp.machine.reference;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.openpnp.ConfigurationListener;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.Icons;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.reference.ReferenceNozzleTip.VacuumMeasurementMethod;
import org.openpnp.machine.reference.axis.ReferenceControllerAxis;
import org.openpnp.machine.reference.driver.GcodeDriver;
import org.openpnp.machine.reference.driver.GcodeDriver.CommandType;
import org.openpnp.machine.reference.wizards.ReferenceNozzleCameraOffsetWizard;
import org.openpnp.machine.reference.wizards.ReferenceNozzleCompatibleNozzleTipsWizard;
import org.openpnp.machine.reference.wizards.ReferenceNozzleConfigurationWizard;
import org.openpnp.machine.reference.wizards.ReferenceNozzleToolChangerWizard;
import org.openpnp.machine.reference.wizards.ReferenceNozzleVacuumWizard;
import org.openpnp.model.Configuration;
import org.openpnp.model.Length;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.model.Part;
import org.openpnp.model.Solutions;
import org.openpnp.model.Solutions.Milestone;
import org.openpnp.model.Solutions.Severity;
import org.openpnp.spi.Actuator;
import org.openpnp.spi.Actuator.ActuatorValueType;
import org.openpnp.spi.Camera;
import org.openpnp.spi.Camera.Looking;
import org.openpnp.spi.CoordinateAxis;
import org.openpnp.spi.Feeder;
import org.openpnp.spi.HeadMountable;
import org.openpnp.spi.MotionPlanner.CompletionType;
import org.openpnp.spi.Nozzle;
import org.openpnp.spi.NozzleTip;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.spi.base.AbstractActuator;
import org.openpnp.spi.base.AbstractNozzle;
import org.openpnp.util.MovableUtils;
import org.openpnp.util.SimpleGraph;
import org.openpnp.util.UiUtils;
import org.pmw.tinylog.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.core.Persist;

public class ReferenceNozzle extends AbstractNozzle implements ReferenceHeadMountable {
  @Element private Location headOffsets = new Location(LengthUnit.Millimeters);

  @Attribute(required = false) private int pickDwellMilliseconds;

  @Attribute(required = false) private int placeDwellMilliseconds;

  @Attribute(required = false) private String currentNozzleTipId;

  @Attribute(required = false) private boolean changerEnabled = false;

  @Attribute(required = false) private boolean nozzleTipChangedOnManualFeed = false;

  @Element(required = false) private Location manualNozzleTipChangeLocation = new Location(LengthUnit.Millimeters);

  @Deprecated @Element(required = false) protected Length safeZ = null;

  @Attribute(required = false) private boolean enableDynamicSafeZ = false;

  @Element(required = false) protected String vacuumSenseActuatorName;

  @Element(required = false) protected String vacuumActuatorName;

  @Element(required = false) protected String blowOffActuatorName;

  @Attribute(required = false) private int version;

  @Deprecated @Attribute(required = false) private boolean limitRotation = true;

  private Actuator vacuumSenseActuator;

  private Actuator vacuumActuator;

  private Actuator blowOffActuator;

  protected ReferenceNozzleTip nozzleTip;

  public ReferenceNozzle() {
    Configuration.get().addListener(new ConfigurationListener.Adapter() {
      @Override public void configurationLoaded(Configuration configuration) throws Exception {
        if (getHead() != null) {
          nozzleTip = (ReferenceNozzleTip) configuration.getMachine().getNozzleTip(currentNozzleTipId);
          if (version < 200) {
            Actuator vacuumActuator = null;
            try {
              vacuumActuator = getVacuumActuator();
            } catch (Exception e) {
              vacuumActuatorName = null;
            }
            try {
              getVacuumSenseActuator();
            } catch (Exception e) {
              vacuumSenseActuatorName = null;
            }
            Actuator blowOffActuator = null;
            try {
              blowOffActuator = getBlowOffActuator();
            } catch (Exception e) {
              blowOffActuatorName = null;
            }
            if (vacuumSenseActuatorName == null) {
              vacuumSenseActuatorName = vacuumActuatorName;
            } else {
              if (vacuumActuatorName == null) {
                vacuumActuatorName = vacuumSenseActuatorName;
              }
            }
            if (blowOffActuator != null) {
              AbstractActuator.suggestValueType(vacuumActuator, ActuatorValueType.Double);
              AbstractActuator.suggestValueType(blowOffActuator, ActuatorValueType.Double);
            }
            version = 200;
          }
          try {
            getVacuumSenseActuator();
          } catch (Exception e) {
          }
          try {
            getVacuumActuator();
          } catch (Exception e) {
          }
          try {
            getBlowOffActuator();
          } catch (Exception e) {
          }
          if (isManualNozzleTipChangeLocationUndefined()) {
            for (Nozzle nozzle : configuration.getMachine().getDefaultHead().getNozzles()) {
              if (nozzle instanceof ReferenceNozzle) {
                if (!((ReferenceNozzle) nozzle).isManualNozzleTipChangeLocationUndefined()) {
                  manualNozzleTipChangeLocation = ((ReferenceNozzle) nozzle).getManualNozzleTipChangeLocation();
                  break;
                }
              }
            }
          }
          if (isManualNozzleTipChangeLocationUndefined()) {
            for (NozzleTip nt : configuration.getMachine().getNozzleTips()) {
              if (nt instanceof ReferenceNozzleTip) {
                manualNozzleTipChangeLocation = ((ReferenceNozzleTip) nt).getChangerEndLocation();
                break;
              }
            }
          }
        }
      }
    });
  }

  public ReferenceNozzle(String id) {
    this();
    this.id = id;
  }

  @Persist private void persist() {
    try {
      setVacuumSenseActuator(getVacuumSenseActuator());
    } catch (Exception e) {
    }
    try {
      setVacuumActuator(getVacuumActuator());
    } catch (Exception e) {
    }
    try {
      setBlowOffActuator(getBlowOffActuator());
    } catch (Exception e) {
    }
  }

  @Deprecated public boolean isLimitRotation() {
    return limitRotation;
  }

  public boolean isEnableDynamicSafeZ() {
    return enableDynamicSafeZ;
  }

  public void setEnableDynamicSafeZ(boolean enableDynamicSafeZ) {
    this.enableDynamicSafeZ = enableDynamicSafeZ;
  }

  public int getPickDwellMilliseconds() {
    return pickDwellMilliseconds;
  }

  public void setPickDwellMilliseconds(int pickDwellMilliseconds) {
    this.pickDwellMilliseconds = pickDwellMilliseconds;
  }

  public int getPlaceDwellMilliseconds() {
    return placeDwellMilliseconds;
  }

  public void setPlaceDwellMilliseconds(int placeDwellMilliseconds) {
    this.placeDwellMilliseconds = placeDwellMilliseconds;
  }

  @Override public Location getHeadOffsets() {
    return headOffsets;
  }

  @Override public void setHeadOffsets(Location headOffsets) {
    Location oldValue = this.headOffsets;
    this.headOffsets = headOffsets;
    firePropertyChange("headOffsets", oldValue, headOffsets);
    Location offsetsDiff = headOffsets.subtract(oldValue).convertToUnits(LengthUnit.Millimeters);
    adjustHeadOffsetsDependencies(oldValue, headOffsets);
  }

  /**
     * Adjust any dependent head offsets, e.g. after calibration.
     * 
     * @param headOffsetsOld
     * @param headOffsetsNew
     * @param offsetsDiff
     */
  public void adjustHeadOffsetsDependencies(Location headOffsetsOld, Location headOffsetsNew) {
    Location offsetsDiff = headOffsetsNew.subtract(headOffsetsOld).convertToUnits(LengthUnit.Millimeters);
    if (offsetsDiff.getLinearDistanceTo(Location.origin) > 0.01) {
      ReferenceNozzleTipCalibration.resetAllNozzleTips();
    }
    if (headOffsetsOld.isInitialized() && headOffsetsNew.isInitialized() && head != null) {
      for (HeadMountable hm : head.getHeadMountables()) {
        if (this != hm && (hm instanceof ReferenceHeadMountable) && !(hm instanceof ReferenceNozzle)) {
          ReferenceHeadMountable otherHeadMountable = (ReferenceHeadMountable) hm;
          Location otherHeadOffsets = otherHeadMountable.getHeadOffsets();
          if (otherHeadOffsets.isInitialized() && headOffsetsOld.convertToUnits(LengthUnit.Millimeters).getLinearDistanceTo(otherHeadOffsets) <= 0.01) {
            Location hmOffsets = otherHeadOffsets.derive(headOffsetsNew, true, true, false, false);
            Logger.info("Set " + otherHeadMountable.getClass().getSimpleName() + " " + otherHeadMountable.getName() + " head offsets to " + hmOffsets + " (previously " + otherHeadOffsets + ")");
            otherHeadMountable.setHeadOffsets(hmOffsets);
          }
        }
      }
      try {
        if (this == head.getDefaultNozzle()) {
          for (Camera camera : getMachine().getCameras()) {
            if (camera instanceof ReferenceCamera && camera.getLooking() == Looking.Up) {
              ReferenceHeadMountable upLookingCamera = (ReferenceHeadMountable) camera;
              Location cameraOffsets = upLookingCamera.getHeadOffsets();
              if (cameraOffsets.isInitialized()) {
                cameraOffsets = cameraOffsets.add(offsetsDiff);
                Logger.info("Set camera " + upLookingCamera.getName() + " head offsets to " + cameraOffsets + " (previously " + upLookingCamera.getHeadOffsets() + ")");
                upLookingCamera.setHeadOffsets(cameraOffsets);
              }
            }
          }
        }
      } catch (Exception e) {
        Logger.warn(e);
      }
    }
  }

  @Override public ReferenceNozzleTip getNozzleTip() {
    return nozzleTip;
  }

  @Override public boolean isNozzleTipChangedOnManualFeed() {
    return nozzleTipChangedOnManualFeed;
  }

  public void setNozzleTipChangedOnManualFeed(boolean nozzleTipChangedOnManualFeed) {
    this.nozzleTipChangedOnManualFeed = nozzleTipChangedOnManualFeed;
  }

  public Location getManualNozzleTipChangeLocation() {
    return manualNozzleTipChangeLocation;
  }

  public void setManualNozzleTipChangeLocation(Location manualNozzleTipChangeLocation) {
    Object oldValue = this.manualNozzleTipChangeLocation;
    this.manualNozzleTipChangeLocation = manualNozzleTipChangeLocation;
    firePropertyChange("manualNozzleTipChangeLocation", oldValue, manualNozzleTipChangeLocation);
  }

  @Override public void moveToPickLocation(Feeder feeder) throws Exception {
    Location pickLocation = feeder.getPickLocation();
    if (feeder.isPartHeightAbovePickLocation()) {
      Length partHeight = getSafePartHeight(feeder.getPart());
      pickLocation = pickLocation.add(new Location(partHeight.getUnits(), 0, 0, partHeight.getValue(), 0));
    }
    MovableUtils.moveToLocationAtSafeZ(this, pickLocation);
  }

  @Override public void pick(Part part) throws Exception {
    Logger.debug("{}.pick()", getName());
    if (part == null) {
      throw new Exception("Can\'t pick null part");
    }
    if (nozzleTip == null) {
      throw new Exception("Can\'t pick, no nozzle tip loaded");
    }
    Map<String, Object> globals = new HashMap<>();
    globals.put("nozzle", this);
    globals.put("part", part);
    Configuration.get().getScripting().on("Nozzle.BeforePick", globals);
    setPart(part);
    storeBeforePickVacuumLevel();
    double pickVacuumThreshold = part.getPackage().getPickVacuumLevel();
    if (Double.compare(pickVacuumThreshold, Double.valueOf(0.0)) != 0) {
      actuateVacuumValve(pickVacuumThreshold);
    } else {
      actuateVacuumValve(true);
    }
    establishPickVacuumLevel(this.getPickDwellMilliseconds() + nozzleTip.getPickDwellMilliseconds());
    getMachine().fireMachineHeadActivity(head);
    Configuration.get().getScripting().on("Nozzle.AfterPick", globals);
  }

  @Override public void moveToPlacementLocation(Location placementLocation, Part part) throws Exception {
    if (part != null) {
      placementLocation = placementLocation.add(new Location(part.getHeight().getUnits(), 0, 0, part.getHeight().getValue(), 0));
    }
    MovableUtils.moveToLocationAtSafeZ(this, placementLocation);
  }

  @Override public void place() throws Exception {
    Logger.debug("{}.place()", getName());
    if (nozzleTip == null) {
      throw new Exception("Can\'t place, no nozzle tip loaded");
    }
    Map<String, Object> globals = new HashMap<>();
    globals.put("nozzle", this);
    Configuration.get().getScripting().on("Nozzle.BeforePlace", globals);
    storeBeforePlaceVacuumLevel();
    if (getPart() != null) {
      double placeBlowLevel = getPart().getPackage().getPlaceBlowOffLevel();
      if (Double.compare(placeBlowLevel, Double.valueOf(0.0)) != 0) {
        actuateBlowValve(placeBlowLevel);
      } else {
        actuateVacuumValve(false);
      }
    } else {
      actuateVacuumValve(false);
    }
    establishPlaceVacuumLevel(this.getPlaceDwellMilliseconds() + nozzleTip.getPlaceDwellMilliseconds());
    setPart(null);
    getMachine().fireMachineHeadActivity(head);
    Configuration.get().getScripting().on("Nozzle.AfterPlace", globals);
  }

  protected ReferenceNozzleTip getUnloadedNozzleTipStandin() {
    for (NozzleTip nozzleTip : this.getCompatibleNozzleTips()) {
      if (nozzleTip instanceof ReferenceNozzleTip) {
        ReferenceNozzleTip referenceNozzleTip = (ReferenceNozzleTip) nozzleTip;
        if (referenceNozzleTip.isUnloadedNozzleTipStandin()) {
          return referenceNozzleTip;
        }
      }
    }
    return null;
  }

  public ReferenceNozzleTip getCalibrationNozzleTip() {
    if (nozzleTip != null) {
      ReferenceNozzleTip calibrationNozzleTip = null;
      if (nozzleTip instanceof ReferenceNozzleTip) {
        calibrationNozzleTip = (ReferenceNozzleTip) nozzleTip;
      }
      return calibrationNozzleTip;
    } else {
      return getUnloadedNozzleTipStandin();
    }
  }

  @Override public Location getCameraToolCalibratedOffset(Camera camera) {
    ReferenceNozzleTip calibrationNozzleTip = getCalibrationNozzleTip();
    if (calibrationNozzleTip != null && calibrationNozzleTip.getCalibration().isCalibrated(this)) {
      return calibrationNozzleTip.getCalibration().getCalibratedCameraOffset(this, camera);
    }
    return new Location(camera.getUnitsPerPixel().getUnits());
  }

  @Override public void calibrate() throws Exception {
    ReferenceNozzleTip calibrationNozzleTip = getCalibrationNozzleTip();
    if (calibrationNozzleTip != null) {
      calibrationNozzleTip.getCalibration().calibrate(this);
    }
  }

  @Override public boolean isCalibrated() {
    ReferenceNozzleTip calibrationNozzleTip = getCalibrationNozzleTip();
    if (calibrationNozzleTip != null) {
      return calibrationNozzleTip.getCalibration().isCalibrated(this);
    }
    return true;
  }

  @Override public Location toHeadLocation(Location location, Location currentLocation, LocationOption... options) {
    if (!Arrays.asList(options).contains(LocationOption.SuppressDynamicCompensation)) {
      ReferenceNozzleTip calibrationNozzleTip = getCalibrationNozzleTip();
      if (calibrationNozzleTip != null && calibrationNozzleTip.getCalibration().isCalibrated(this)) {
        Location correctionOffset = calibrationNozzleTip.getCalibration().getCalibratedOffset(this, location.getRotation());
        location = location.subtract(correctionOffset);
        Logger.trace("{}.toHeadLocation({}, ...) runout compensation {}", getName(), location, correctionOffset);
      }
    }
    return super.toHeadLocation(location, currentLocation, options);
  }

  @Override public Location toHeadMountableLocation(Location location, Location currentLocation, LocationOption... options) {
    location = super.toHeadMountableLocation(location, currentLocation, options);
    if (!Arrays.asList(options).contains(LocationOption.SuppressDynamicCompensation)) {
      ReferenceNozzleTip calibrationNozzleTip = getCalibrationNozzleTip();
      if (calibrationNozzleTip != null && calibrationNozzleTip.getCalibration().isCalibrated(this)) {
        Location offset = calibrationNozzleTip.getCalibration().getCalibratedOffset(this, location.getRotation());
        location = location.add(offset);
      }
    }
    return location;
  }

  @Override public Length getSafePartHeight(Part part) {
    if (part != null) {
      if (part.isPartHeightUnknown() && nozzleTip != null) {
        return nozzleTip.getMaxPartHeight();
      } else {
        return part.getHeight();
      }
    }
    return new Length(0, LengthUnit.Millimeters);
  }

  @Override public Length getEffectiveSafeZ() throws Exception {
    Length safeZ = super.getEffectiveSafeZ();
    if (safeZ == null) {
      throw new Exception("Nozzle " + getName() + " has no Z axis with Safe Zone mapped.");
    }
    if (enableDynamicSafeZ) {
      safeZ = safeZ.add(getSafePartHeight());
    }
    return safeZ;
  }

  @Override public void home() throws Exception {
    Logger.debug("{}.home()", getName());
    for (NozzleTip attachedNozzleTip : this.getCompatibleNozzleTips()) {
      if (attachedNozzleTip instanceof ReferenceNozzleTip) {
        ReferenceNozzleTip calibrationNozzleTip = (ReferenceNozzleTip) attachedNozzleTip;
        if (calibrationNozzleTip.getCalibration().isRecalibrateOnHomeNeeded(this)) {
          if (calibrationNozzleTip == this.getCalibrationNozzleTip()) {
            try {
              Logger.debug("{}.home() nozzle tip {} calibration neeeded", getName(), calibrationNozzleTip.getName());
              calibrationNozzleTip.getCalibration().calibrate(this, true, false);
            } catch (Exception e) {
              if (calibrationNozzleTip.getCalibration().isFailHoming()) {
                throw e;
              } else {
                UiUtils.messageBoxOnExceptionLater(() -> {
                  throw e;
                });
              }
            }
          } else {
            Logger.debug("{}.home() nozzle tip {} calibration reset", getName(), calibrationNozzleTip.getName());
            calibrationNozzleTip.getCalibration().reset(this);
          }
        }
      }
    }
  }

  @Override public void loadNozzleTip(NozzleTip nozzleTip) throws Exception {
    if (this.nozzleTip == nozzleTip) {
      return;
    }
    ReferenceNozzleTip nt = (ReferenceNozzleTip) nozzleTip;
    Actuator tcPostOneActuator = getMachine().getActuatorByName(nt.getChangerActuatorPostStepOne());
    Actuator tcPostTwoActuator = getMachine().getActuatorByName(nt.getChangerActuatorPostStepTwo());
    Actuator tcPostThreeActuator = getMachine().getActuatorByName(nt.getChangerActuatorPostStepThree());
    if (!getCompatibleNozzleTips().contains(nt)) {
      throw new Exception("Can\'t load incompatible nozzle tip.");
    }
    if (nt.getNozzleAttachedTo() != null) {
      nt.getNozzleAttachedTo().unloadNozzleTip();
    }
    unloadNozzleTip();
    double speed = getHead().getMachine().getSpeed();
    if (!nt.isUnloadedNozzleTipStandin()) {
      if (changerEnabled) {
        Logger.debug("{}.loadNozzleTip({}): Start", getName(), nozzleTip.getName());
        Map<String, Object> globals = new HashMap<>();
        globals.put("head", getHead());
        globals.put("nozzle", this);
        globals.put("nozzleTip", nt);
        Configuration.get().getScripting().on("NozzleTip.BeforeLoad", globals);
        ensureZCalibrated(true);
        Logger.debug("{}.loadNozzleTip({}): moveTo Start Location", new Object[] { getName(), nozzleTip.getName() });
        MovableUtils.moveToLocationAtSafeZ(this, nt.getChangerStartLocationCalibrated(true), speed);
        if (tcPostOneActuator != null) {
          tcPostOneActuator.actuate(true);
        }
        Logger.debug("{}.loadNozzleTip({}): moveTo Mid Location", new Object[] { getName(), nozzleTip.getName() });
        moveTo(nt.getChangerMidLocationCalibrated(false), nt.getChangerStartToMidSpeed() * speed);
        if (tcPostTwoActuator != null) {
          tcPostTwoActuator.actuate(true);
        }
        Logger.debug("{}.loadNozzleTip({}): moveTo Mid Location 2", new Object[] { getName(), nozzleTip.getName() });
        moveTo(nt.getChangerMidLocation2Calibrated(false), nt.getChangerMidToMid2Speed() * speed);
        if (tcPostThreeActuator != null) {
          tcPostThreeActuator.actuate(true);
        }
        Logger.debug("{}.loadNozzleTip({}): moveTo End Location", new Object[] { getName(), nozzleTip.getName() });
        moveTo(nt.getChangerEndLocationCalibrated(false), nt.getChangerMid2ToEndSpeed() * speed);
        moveToSafeZ(getHead().getMachine().getSpeed());
        Logger.debug("{}.loadNozzleTip({}): Finished", new Object[] { getName(), nozzleTip.getName() });
        Configuration.get().getScripting().on("NozzleTip.Loaded", globals);
      } else {
        Logger.debug("{}.loadNozzleTip({}): moveTo manual Location", new Object[] { getName(), nozzleTip.getName() });
        assertManualChangeLocation();
        MovableUtils.moveToLocationAtSafeZ(this, getManualNozzleTipChangeLocation());
      }
    }
    this.nozzleTip = nt;
    currentNozzleTipId = nozzleTip.getId();
    firePropertyChange("nozzleTip", null, getNozzleTip());
    ((ReferenceMachine) head.getMachine()).fireMachineHeadActivity(head);
    ensureZCalibrated(true);
    if (!nt.isUnloadedNozzleTipStandin()) {
      if (!changerEnabled) {
        if (this.nozzleTip.getCalibration().isRecalibrateOnNozzleTipChangeNeeded(this) || this.nozzleTip.getCalibration().isRecalibrateOnNozzleTipChangeInJobNeeded(this)) {
          Logger.debug("{}.loadNozzleTip() nozzle tip {} calibration reset", getName(), this.nozzleTip.getName());
          this.nozzleTip.getCalibration().reset(this);
        }
        waitForCompletion(CompletionType.WaitForStillstand);
        throw new Exception("Manual NozzleTip " + nt.getName() + " load on Nozzle " + getName() + " required!");
      }
    }
    if (this.nozzleTip.getCalibration().isRecalibrateOnNozzleTipChangeNeeded(this)) {
      Logger.debug("{}.loadNozzleTip() nozzle tip {} calibration needed", getName(), this.nozzleTip.getName());
      this.nozzleTip.getCalibration().calibrate(this);
    } else {
      if (this.nozzleTip.getCalibration().isRecalibrateOnNozzleTipChangeInJobNeeded(this)) {
        Logger.debug("{}.loadNozzleTip() nozzle tip {} calibration reset", getName(), this.nozzleTip.getName());
        this.nozzleTip.getCalibration().reset(this);
      }
    }
  }

  @Override public void unloadNozzleTip() throws Exception {
    if (nozzleTip == null) {
      return;
    }
    ReferenceNozzleTip nt = (ReferenceNozzleTip) nozzleTip;
    Actuator tcPostOneActuator = getMachine().getActuatorByName(nt.getChangerActuatorPostStepOne());
    Actuator tcPostTwoActuator = getMachine().getActuatorByName(nt.getChangerActuatorPostStepTwo());
    Actuator tcPostThreeActuator = getMachine().getActuatorByName(nt.getChangerActuatorPostStepThree());
    if (!nt.isUnloadedNozzleTipStandin()) {
      Logger.debug("{}.unloadNozzleTip(): Start", getName());
      double speed = getHead().getMachine().getSpeed();
      if (changerEnabled) {
        Map<String, Object> globals = new HashMap<>();
        globals.put("head", getHead());
        globals.put("nozzle", this);
        globals.put("nozzleTip", nt);
        Configuration.get().getScripting().on("NozzleTip.BeforeUnload", globals);
        ensureZCalibrated(false);
        Logger.debug("{}.unloadNozzleTip(): moveTo End Location", getName());
        MovableUtils.moveToLocationAtSafeZ(this, nt.getChangerEndLocationCalibrated(true), speed);
        if (tcPostThreeActuator != null) {
          tcPostThreeActuator.actuate(true);
        }
        Logger.debug("{}.unloadNozzleTip(): moveTo Mid Location 2", getName());
        moveTo(nt.getChangerMidLocation2Calibrated(false), nt.getChangerMid2ToEndSpeed() * speed);
        if (tcPostTwoActuator != null) {
          tcPostTwoActuator.actuate(true);
        }
        Logger.debug("{}.unloadNozzleTip(): moveTo Mid Location", getName());
        moveTo(nt.getChangerMidLocationCalibrated(false), nt.getChangerMidToMid2Speed() * speed);
        if (tcPostOneActuator != null) {
          tcPostOneActuator.actuate(true);
        }
        Logger.debug("{}.unloadNozzleTip(): moveTo Start Location", getName());
        moveTo(nt.getChangerStartLocationCalibrated(false), nt.getChangerStartToMidSpeed() * speed);
        moveToSafeZ(getHead().getMachine().getSpeed());
        Logger.debug("{}.unloadNozzleTip(): Finished", getName());
        Configuration.get().getScripting().on("NozzleTip.Unloaded", globals);
      } else {
        Logger.debug("{}.unloadNozzleTip({}): moveTo manual Location", new Object[] { getName(), nozzleTip.getName() });
        assertManualChangeLocation();
        MovableUtils.moveToLocationAtSafeZ(this, getManualNozzleTipChangeLocation());
      }
    }
    nozzleTip = null;
    currentNozzleTipId = null;
    firePropertyChange("nozzleTip", null, getNozzleTip());
    ((ReferenceMachine) head.getMachine()).fireMachineHeadActivity(head);
    if (!changerEnabled) {
      waitForCompletion(CompletionType.WaitForStillstand);
      throw new Exception("Manual NozzleTip " + nt.getName() + " unload from Nozzle " + getName() + " required!");
    }
    ensureZCalibrated(true);
    ReferenceNozzleTip calibrationNozzleTip = this.getCalibrationNozzleTip();
    if (calibrationNozzleTip != null && calibrationNozzleTip.getCalibration().isRecalibrateOnNozzleTipChangeNeeded(this)) {
      Logger.debug("{}.unloadNozzleTip() nozzle tip {} calibration needed", getName(), calibrationNozzleTip.getName());
      calibrationNozzleTip.getCalibration().calibrate(this);
    }
  }

  protected void assertManualChangeLocation() throws Exception {
    if (isManualNozzleTipChangeLocationUndefined()) {
      throw new Exception("Nozzle " + getName() + " Manual Change Location is not configured!");
    }
  }

  protected boolean isManualNozzleTipChangeLocationUndefined() {
    return manualNozzleTipChangeLocation.equals(new Location(LengthUnit.Millimeters));
  }

  public boolean isChangerEnabled() {
    return changerEnabled;
  }

  public void setChangerEnabled(boolean changerEnabled) {
    this.changerEnabled = changerEnabled;
  }

  protected void ensureZCalibrated(boolean assumeNozzleTipLoaded) throws Exception {
  }

  @Override public Wizard getConfigurationWizard() {
    return new ReferenceNozzleConfigurationWizard(getMachine(), this);
  }

  @Override public String getPropertySheetHolderTitle() {
    return getClass().getSimpleName() + " " + getName();
  }

  @Override public PropertySheetHolder[] getChildPropertySheetHolders() {
    return null;
  }

  @Override public PropertySheet[] getPropertySheets() {
    return new PropertySheet[] { new PropertySheetWizardAdapter(getConfigurationWizard()), new PropertySheetWizardAdapter(new ReferenceNozzleCompatibleNozzleTipsWizard(this), "Nozzle Tips"), new PropertySheetWizardAdapter(new ReferenceNozzleVacuumWizard(this), "Vacuum"), new PropertySheetWizardAdapter(new ReferenceNozzleToolChangerWizard(this), "Tool Changer"), new PropertySheetWizardAdapter(new ReferenceNozzleCameraOffsetWizard(this), "Offset Wizard") };
  }

  @Override public Action[] getPropertySheetHolderActions() {
    return new Action[] { deleteAction };
  }

  public Action deleteAction = new AbstractAction("Delete Nozzle") {
    {
      putValue(SMALL_ICON, Icons.nozzleRemove);
      putValue(NAME, "Delete Nozzle");
      putValue(SHORT_DESCRIPTION, "Delete the currently selected nozzle.");
    }

    @Override public void actionPerformed(ActionEvent arg0) {
      if (getHead().getNozzles().size() == 1) {
        MessageBoxes.errorBox(null, "Error: Nozzle Not Deleted", "Can\'t delete last nozzle. There must be at least one nozzle.");
        return;
      }
      int ret = JOptionPane.showConfirmDialog(MainFrame.get(), "Are you sure you want to delete " + getName() + "?", "Delete " + getName() + "?", JOptionPane.YES_NO_OPTION);
      if (ret == JOptionPane.YES_OPTION) {
        getHead().removeNozzle(ReferenceNozzle.this);
      }
    }
  };

  @Override public String toString() {
    return getName() + " " + getId();
  }

  protected ReferenceMachine getMachine() {
    return (ReferenceMachine) Configuration.get().getMachine();
  }

  protected boolean isVaccumSenseActuatorEnabled() {
    return vacuumSenseActuatorName != null && !vacuumSenseActuatorName.isEmpty();
  }

  protected boolean isVaccumActuatorEnabled() {
    return vacuumActuatorName != null && !vacuumActuatorName.isEmpty();
  }

  @Override public boolean isPartOnEnabled(Nozzle.PartOnStep step) {
    if ((step == PartOnStep.AfterPick && getNozzleTip().isPartOnCheckAfterPick()) || (step == PartOnStep.Align && getNozzleTip().isPartOnCheckAlign()) || (step == PartOnStep.BeforePlace && getNozzleTip().isPartOnCheckBeforePlace())) {
      return isVaccumSenseActuatorEnabled() && (getNozzleTip().getMethodPartOn() != VacuumMeasurementMethod.None);
    }
    return false;
  }

  @Override public boolean isPartOffEnabled(Nozzle.PartOffStep step) {
    if ((step == PartOffStep.AfterPlace && getNozzleTip().isPartOffCheckAfterPlace()) || (step == PartOffStep.BeforePick && getNozzleTip().isPartOffCheckBeforePick())) {
      return isVaccumSenseActuatorEnabled() && (getNozzleTip().getMethodPartOff() != VacuumMeasurementMethod.None);
    }
    return false;
  }

  /**
     * @return The actuator used to sense the vacuum on the Nozzle.
     * @throws Exception when the actuator name cannot be resolved (dangling reference).
     */
  public Actuator getVacuumSenseActuator() throws Exception {
    if (vacuumSenseActuator == null && vacuumSenseActuatorName != null && !vacuumSenseActuatorName.isEmpty()) {
      vacuumSenseActuator = getHead().getActuatorByName(vacuumSenseActuatorName);
      if (vacuumSenseActuator == null) {
        throw new Exception(String.format("Can\'t find vacuum sense actuator %s", vacuumSenseActuatorName));
      }
    }
    return vacuumSenseActuator;
  }

  /**
     * @return The actuator used to sense the vacuum on the Nozzle.
     * @throws Exception when the actuator is not configured.
     */
  public Actuator getExpectedVacuumSenseActuator() throws Exception {
    Actuator actuator = getVacuumSenseActuator();
    if (actuator == null) {
      throw new Exception("Nozzle " + getName() + " has no vacuum sense actuator assigned.");
    }
    return actuator;
  }

  /**
     * Set the actuator used to sense the vacuum on the Nozzle.
     * @param actuator
     */
  public void setVacuumSenseActuator(Actuator actuator) {
    vacuumSenseActuator = actuator;
    vacuumSenseActuatorName = (actuator != null ? actuator.getName() : null);
  }

  /**
     * @return The actuator used to switch the vacuum valve on the Nozzle. 
     * @throws Exception when the actuator name cannot be resolved (dangling reference).
     */
  public Actuator getVacuumActuator() throws Exception {
    if (vacuumActuator == null && vacuumActuatorName != null && !vacuumActuatorName.isEmpty()) {
      vacuumActuator = getHead().getActuatorByName(vacuumActuatorName);
      if (vacuumActuator == null) {
        throw new Exception(String.format("Can\'t find vacuum valve actuator %s", vacuumActuatorName));
      }
    }
    return vacuumActuator;
  }

  /**
     * @return The actuator used to switch the vacuum valve on the Nozzle. 
     * @throws Exception when the actuator is not configured.
     */
  public Actuator getExpectedVacuumActuator() throws Exception {
    Actuator actuator = getVacuumActuator();
    if (actuator == null) {
      throw new Exception("Nozzle " + getName() + " has no vacuum actuator assigned.");
    }
    return actuator;
  }

  /**
     * Set the actuator used to switch the vacuum valve on the Nozzle. 
     * @param actuator
     */
  public void setVacuumActuator(Actuator actuator) {
    vacuumActuator = actuator;
    vacuumActuatorName = (actuator != null ? actuator.getName() : null);
  }

  /**
     * @return The actuator used to blow off parts on the Nozzle. 
     * @throws Exception when the actuator name cannot be resolved (dangling reference).
     */
  public Actuator getBlowOffActuator() throws Exception {
    if (blowOffActuator == null && blowOffActuatorName != null && !blowOffActuatorName.isEmpty()) {
      blowOffActuator = getHead().getActuatorByName(blowOffActuatorName);
      if (blowOffActuator == null) {
        throw new Exception(String.format("Can\'t find blow off actuator %s", blowOffActuatorName));
      }
    }
    return blowOffActuator;
  }

  /**
     * @return The actuator used to blow off parts on the Nozzle. 
     * @throws Exception when the actuator is not configured.
     */
  public Actuator getExpectedBlowOffActuator() throws Exception {
    Actuator actuator = getBlowOffActuator();
    if (actuator == null) {
      throw new Exception("Nozzle " + getName() + " has no blow off actuator assigned.");
    }
    return actuator;
  }

  /**
     * Set the actuator used to blow off parts on the Nozzle. 
     * @param actuator
     */
  public void setBlowOffActuator(Actuator actuator) {
    blowOffActuator = actuator;
    blowOffActuatorName = (actuator != null ? actuator.getName() : null);
  }

  protected boolean hasPartOnAnyOtherNozzle() {
    for (Nozzle nozzle : getHead().getNozzles()) {
      if (nozzle != this) {
        if (nozzle.getPart() != null) {
          return true;
        }
      }
    }
    return false;
  }

  protected void actuatePump(boolean on) throws Exception {
    Actuator pump = getHead().getPump();
    if (pump != null && !hasPartOnAnyOtherNozzle()) {
      pump.actuate(on);
    }
  }

  protected void actuateVacuumValve(boolean on) throws Exception {
    if (on) {
      actuatePump(true);
    }
    getExpectedVacuumActuator().actuate(on);
    if (!on) {
      actuatePump(false);
    }
  }

  protected void actuateVacuumValve(double value) throws Exception {
    actuatePump(true);
    getExpectedVacuumActuator().actuate(value);
  }

  protected void actuateBlowValve(double value) throws Exception {
    getExpectedBlowOffActuator().actuate(value);
    actuatePump(false);
  }

  public double readVacuumLevel() throws Exception {
    return Double.parseDouble(getExpectedVacuumSenseActuator().read());
  }

  protected boolean isPartOnGraphEnabled() {
    ReferenceNozzleTip nt = getNozzleTip();
    return nt.getMethodPartOn() != VacuumMeasurementMethod.None && (nt.getMethodPartOn().isDifferenceMethod() || nt.isEstablishPartOnLevel());
  }

  protected boolean isPartOffGraphEnabled() {
    ReferenceNozzleTip nt = getNozzleTip();
    return nt.getMethodPartOff() != VacuumMeasurementMethod.None && (nt.getMethodPartOff().isDifferenceMethod() || nt.isEstablishPartOffLevel());
  }

  protected void storeBeforePickVacuumLevel() throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    if (isPartOnGraphEnabled()) {
      double vacuumLevel = readVacuumLevel();
      SimpleGraph vacuumGraph = nt.startNewVacuumGraph(vacuumLevel, true);
      nt.setVacuumPartOnGraph(vacuumGraph);
    } else {
      nt.setVacuumPartOnGraph(null);
    }
  }

  protected void storeBeforePlaceVacuumLevel() throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    if (isPartOffGraphEnabled()) {
      double vacuumLevel = readVacuumLevel();
      SimpleGraph vacuumGraph = nt.startNewVacuumGraph(vacuumLevel, false);
      nt.setVacuumPartOffGraph(vacuumGraph);
    } else {
      nt.setVacuumPartOffGraph(null);
    }
  }

  protected void establishPickVacuumLevel(int milliseconds) throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    SimpleGraph vacuumGraph = nt.getVacuumPartOnGraph();
    if (vacuumGraph != null) {
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 1);
      long timeout = System.currentTimeMillis() + milliseconds;
      SimpleGraph.DataRow vacuumData = vacuumGraph.getRow(ReferenceNozzleTip.PRESSURE, ReferenceNozzleTip.VACUUM);
      double vacuumLevel;
      do {
        vacuumLevel = readVacuumLevel();
        vacuumData.recordDataPoint(vacuumGraph.getT(), vacuumLevel);
        if (nt.isEstablishPartOnLevel() && vacuumLevel >= nt.getVacuumLevelPartOnLow() && vacuumLevel <= nt.getVacuumLevelPartOnHigh()) {
          break;
        }
      } while(System.currentTimeMillis() < timeout);
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 1);
      nt.setVacuumPartOnGraph(vacuumGraph);
      if (nt.getMethodPartOn().isDifferenceMethod()) {
        nt.setVacuumLevelPartOnReading(vacuumLevel);
      }
    } else {
      Thread.sleep(milliseconds);
    }
  }

  protected void establishPlaceVacuumLevel(int milliseconds) throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    SimpleGraph vacuumGraph = nt.getVacuumPartOffGraph();
    if (vacuumGraph != null) {
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 0);
      long timeout = System.currentTimeMillis() + milliseconds;
      SimpleGraph.DataRow vacuumData = vacuumGraph.getRow(ReferenceNozzleTip.PRESSURE, ReferenceNozzleTip.VACUUM);
      double vacuumLevel;
      do {
        vacuumLevel = readVacuumLevel();
        vacuumData.recordDataPoint(vacuumGraph.getT(), vacuumLevel);
        if (nt.isEstablishPartOffLevel() && vacuumLevel >= nt.getVacuumLevelPartOffLow() && vacuumLevel <= nt.getVacuumLevelPartOffHigh()) {
          break;
        }
      } while(System.currentTimeMillis() < timeout);
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 0);
      nt.setVacuumPartOffGraph(vacuumGraph);
      if (nt.getMethodPartOff().isDifferenceMethod()) {
        nt.setVacuumLevelPartOffReading(vacuumLevel);
      }
    } else {
      Thread.sleep(milliseconds);
    }
  }

  protected double probePartOffVacuumLevel(int probingMilliseconds, int dwellMilliseconds) throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    SimpleGraph vacuumGraph = null;
    double returnedVacuumLevel = Double.NaN;
    if (isPartOnGraphEnabled()) {
      vacuumGraph = nt.getVacuumPartOffGraph();
      if (vacuumGraph == null || vacuumGraph.getT() > 1000.0) {
        vacuumGraph = nt.startNewVacuumGraph(readVacuumLevel(), true);
        nt.setVacuumPartOffGraph(vacuumGraph);
      }
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 0);
    }
    if (nt.getMethodPartOff().isDifferenceMethod()) {
      double vacuumLevel = readVacuumLevel();
      if (vacuumGraph != null) {
        vacuumGraph.getRow(ReferenceNozzleTip.PRESSURE, ReferenceNozzleTip.VACUUM).recordDataPoint(vacuumGraph.getT(), vacuumLevel);
      }
      nt.setVacuumLevelPartOffReading(vacuumLevel);
    }
    try {
      actuateVacuumValve(true);
      if (vacuumGraph != null) {
        vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 1);
        long timeout = System.currentTimeMillis() + probingMilliseconds;
        SimpleGraph.DataRow vacuumData = vacuumGraph.getRow(ReferenceNozzleTip.PRESSURE, ReferenceNozzleTip.VACUUM);
        double vacuumLevel;
        do {
          vacuumLevel = readVacuumLevel();
          vacuumData.recordDataPoint(vacuumGraph.getT(), vacuumLevel);
        } while(System.currentTimeMillis() < timeout);
        vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 1);
        if (dwellMilliseconds <= 0) {
          returnedVacuumLevel = vacuumLevel;
        }
      } else {
        Thread.sleep(probingMilliseconds);
        if (dwellMilliseconds <= 0) {
          returnedVacuumLevel = readVacuumLevel();
        }
      }
    }  finally {
      actuateVacuumValve(false);
    }
    if (vacuumGraph != null) {
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 0);
      long timeout = System.currentTimeMillis() + dwellMilliseconds;
      SimpleGraph.DataRow vacuumData = vacuumGraph.getRow(ReferenceNozzleTip.PRESSURE, ReferenceNozzleTip.VACUUM);
      double vacuumLevel;
      do {
        vacuumLevel = readVacuumLevel();
        vacuumData.recordDataPoint(vacuumGraph.getT(), vacuumLevel);
      } while(System.currentTimeMillis() < timeout);
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 0);
      nt.setVacuumPartOffGraph(vacuumGraph);
      if (dwellMilliseconds > 0) {
        returnedVacuumLevel = vacuumLevel;
      }
      return returnedVacuumLevel;
    } else {
      if (dwellMilliseconds > 0) {
        Thread.sleep(dwellMilliseconds);
        returnedVacuumLevel = readVacuumLevel();
      }
      return returnedVacuumLevel;
    }
  }

  @Override public boolean isPartOn() throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    double vacuumLevel = readVacuumLevel();
    SimpleGraph vacuumGraph = nt.getVacuumPartOnGraph();
    if (vacuumGraph != null) {
      vacuumGraph.getRow(ReferenceNozzleTip.PRESSURE, ReferenceNozzleTip.VACUUM).recordDataPoint(vacuumGraph.getT(), vacuumLevel);
      vacuumGraph.getRow(ReferenceNozzleTip.BOOLEAN, ReferenceNozzleTip.VALVE_ON).recordDataPoint(vacuumGraph.getT(), 1);
    }
    if (nt.getMethodPartOn().isDifferenceMethod()) {
      double vacuumBaselineLevel = nt.getVacuumLevelPartOnReading();
      double vacuumDifference = vacuumLevel - vacuumBaselineLevel;
      nt.setVacuumDifferencePartOnReading(vacuumDifference);
      if (vacuumBaselineLevel < nt.getVacuumLevelPartOnLow() || vacuumBaselineLevel > nt.getVacuumLevelPartOnHigh()) {
        Logger.debug("Nozzle tip {} baseline vacuum level {} outside PartOn range {} .. {}", nt.getName(), vacuumBaselineLevel, nt.getVacuumLevelPartOnLow(), nt.getVacuumLevelPartOnHigh());
        return false;
      }
      if (vacuumDifference < nt.getVacuumDifferencePartOnLow() || vacuumDifference > nt.getVacuumDifferencePartOnHigh()) {
        Logger.debug("Nozzle tip {} vacuum level difference {} outside PartOn range {} .. {}", nt.getName(), vacuumDifference, nt.getVacuumDifferencePartOnLow(), nt.getVacuumDifferencePartOnHigh());
        return false;
      }
    } else {
      nt.setVacuumLevelPartOnReading(vacuumLevel);
      nt.setVacuumDifferencePartOnReading(null);
      if (vacuumLevel < nt.getVacuumLevelPartOnLow() || vacuumLevel > nt.getVacuumLevelPartOnHigh()) {
        Logger.debug("Nozzle tip {} absolute vacuum level {} outside PartOn range {} .. {}", nt.getName(), vacuumLevel, nt.getVacuumLevelPartOnLow(), nt.getVacuumLevelPartOnHigh());
        return false;
      }
    }
    return true;
  }

  @Override public boolean isPartOff() throws Exception {
    ReferenceNozzleTip nt = getNozzleTip();
    double vacuumLevel = probePartOffVacuumLevel(nt.getPartOffProbingMilliseconds(), nt.getPartOffDwellMilliseconds());
    if (nt.getMethodPartOff().isDifferenceMethod()) {
      double vacuumBaselineLevel = nt.getVacuumLevelPartOffReading();
      double vacuumDifference = vacuumLevel - vacuumBaselineLevel;
      nt.setVacuumDifferencePartOffReading(vacuumDifference);
      if (vacuumBaselineLevel < nt.getVacuumLevelPartOffLow() || vacuumBaselineLevel > nt.getVacuumLevelPartOffHigh()) {
        Logger.debug("Nozzle tip {} baseline vacuum level {} outside PartOff range {} .. {}", nt.getName(), vacuumBaselineLevel, nt.getVacuumLevelPartOffLow(), nt.getVacuumLevelPartOffHigh());
        return false;
      }
      if (vacuumDifference < nt.getVacuumDifferencePartOffLow() || vacuumDifference > nt.getVacuumDifferencePartOffHigh()) {
        Logger.debug("Nozzle tip {} vacuum level difference {} outside PartOff range {} .. {}", nt.getName(), vacuumDifference, nt.getVacuumDifferencePartOffLow(), nt.getVacuumDifferencePartOffHigh());
        return false;
      }
    } else {
      nt.setVacuumLevelPartOffReading(vacuumLevel);
      nt.setVacuumDifferencePartOffReading(null);
      if (vacuumLevel < nt.getVacuumLevelPartOffLow() || vacuumLevel > nt.getVacuumLevelPartOffHigh()) {
        Logger.debug("Nozzle tip {} absolute vacuum level {} outside PartOff range {} .. {}", nt.getName(), vacuumLevel, nt.getVacuumLevelPartOffLow(), nt.getVacuumLevelPartOffHigh());
        return false;
      }
    }
    return true;
  }

  @Override public void findIssues(Solutions solutions) {
    super.findIssues(solutions);
    try {
      if (solutions.isTargeting(Milestone.Basics)) {
        findActuatorIssues(solutions, getVacuumActuator(), "vacuum valve", new CommandType[] { CommandType.ACTUATE_BOOLEAN_COMMAND });
        findActuatorIssues(solutions, getVacuumSenseActuator(), "vacuum sensing", new CommandType[] { CommandType.ACTUATOR_READ_COMMAND, CommandType.ACTUATOR_READ_REGEX });
      }
    } catch (Exception e) {
      Logger.warn(e);
    }
    ContactProbeNozzle.addConversionIssue(solutions, this);
  }

  protected void findActuatorIssues(Solutions solutions, Actuator actuator, String qualifier, CommandType[] commandTypes) {
    if (actuator == null) {
      solutions.add(new Solutions.PlainIssue(this, "Nozzle is missing a " + qualifier + " actuator.", "Create and assign a " + qualifier + " actuator as described in the Wiki.", Severity.Warning, "https://github.com/openpnp/openpnp/wiki/Setup-and-Calibration%3A-Vacuum-Setup"));
    } else {
      if (actuator.getDriver() == null) {
        if (!actuator.isDriverless()) {
          solutions.add(new Solutions.PlainIssue(actuator, "The " + qualifier + " actuator " + actuator.getName() + " has no driver assigned.", "Assign a driver as described in the Wiki.", Severity.Warning, "https://github.com/openpnp/openpnp/wiki/Setup-and-Calibration%3A-Actuators#driver-assignment"));
        }
      } else {
        if (actuator.getDriver() instanceof GcodeDriver) {
          GcodeDriver driver = (GcodeDriver) actuator.getDriver();
          for (CommandType commandType : commandTypes) {
            if (driver.getCommand(actuator, commandType) == null) {
              solutions.add(new Solutions.PlainIssue(driver, "The " + qualifier + " actuator " + actuator.getName() + " has no " + commandType + " assigned.", "Assign the command to driver " + driver.getName() + " as described in the Wiki.", Severity.Warning, "https://github.com/openpnp/openpnp/wiki/Setup-and-Calibration%3A-Actuators#assigning-commands"));
            }
          }
        }
      }
    }
  }

  @Deprecated public void migrateSafeZ() {
    if (safeZ == null) {
      safeZ = new Length(0, LengthUnit.Millimeters);
    }
    CoordinateAxis coordAxis = getCoordinateAxisZ();
    if (coordAxis instanceof ReferenceControllerAxis) {
      ReferenceControllerAxis rawAxis = (ReferenceControllerAxis) coordAxis;
      try {
        Length rawZ = headMountableToRawZ(rawAxis, safeZ);
        rawAxis.setSafeZoneLow(rawZ);
        rawAxis.setSafeZoneLowEnabled(true);
        rawAxis.setSafeZoneHigh(rawZ);
        rawAxis.setSafeZoneHighEnabled(true);
        safeZ = null;
      } catch (Exception e) {
        Logger.error(e);
      }
    } else {
      if (coordAxis != null) {
        coordAxis.setHomeCoordinate(safeZ);
      }
    }
  }
}