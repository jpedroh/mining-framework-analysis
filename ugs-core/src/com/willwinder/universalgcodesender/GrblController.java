package com.willwinder.universalgcodesender;
import com.willwinder.universalgcodesender.firmware.IFirmwareSettings;
import com.willwinder.universalgcodesender.firmware.grbl.GrblFirmwareSettings;
import com.willwinder.universalgcodesender.gcode.GcodeCommandCreator;
import com.willwinder.universalgcodesender.gcode.util.GcodeUtils;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.ControllerStatus;
import com.willwinder.universalgcodesender.listeners.ControllerStatusBuilder;
import com.willwinder.universalgcodesender.listeners.MessageType;
import com.willwinder.universalgcodesender.model.*;
import com.willwinder.universalgcodesender.model.UGSEvent.ControlState;
import com.willwinder.universalgcodesender.model.UnitUtils.Units;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.types.GrblFeedbackMessage;
import com.willwinder.universalgcodesender.types.GrblSettingMessage;
import com.willwinder.universalgcodesender.utils.GrblLookups;
import org.apache.commons.lang3.StringUtils;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.willwinder.universalgcodesender.model.UGSEvent.ControlState.COMM_CHECK;
import static com.willwinder.universalgcodesender.model.UGSEvent.ControlState.COMM_IDLE;

/**
 * GRBL Control layer, coordinates all aspects of control.
 *
 * @author wwinder
 */
public class GrblController extends AbstractController {
  private static final Logger logger = Logger.getLogger(GrblController.class.getName());

  private static final GrblLookups ALARMS = new GrblLookups("alarm_codes");

  private static final GrblLookups ERRORS = new GrblLookups("error_codes");

  private StatusPollTimer positionPollTimer;

  private double grblVersion = 0.0;

  private Character grblVersionLetter = null;

  protected Boolean isReady = false;

  private Capabilities capabilities = new Capabilities();

  private final GrblFirmwareSettings firmwareSettings;

  private ControllerStatus controllerStatus = new ControllerStatus(ControllerState.DISCONNECTED, new Position(0, 0, 0, Units.MM), new Position(0, 0, 0, Units.MM));

  private Boolean isCanceling = false;

  private int attemptsRemaining;

  private Position lastLocation;

  /**
     * For storing a temporary state if using single step mode when entering the state
     * check mode. When leaving check mode the temporary single step mode will be reverted.
     */
  private boolean temporaryCheckSingleStepMode = false;

  public GrblController(AbstractCommunicator comm) {
    super(comm);
    this.commandCreator = new GcodeCommandCreator();
    this.positionPollTimer = new StatusPollTimer(this);
    this.firmwareSettings = new GrblFirmwareSettings(this);
    this.comm.addListener(firmwareSettings);
  }

  public GrblController() {
    this(new GrblCommunicator());
  }

  @Override public Boolean handlesAllStateChangeEvents() {
    return capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME);
  }

  @Override public Capabilities getCapabilities() {
    return capabilities;
  }

  @Override public IFirmwareSettings getFirmwareSettings() {
    return firmwareSettings;
  }

  /***********************
     * API Implementation. *
     ***********************/
  private static String lookupCode(String input, boolean shortString) {
    if (input.contains(":")) {
      String inputParts[] = input.split(":");
      if (inputParts.length == 2) {
        String code = inputParts[1].trim();
        if (StringUtils.isNumeric(code)) {
          String[] lookupParts = null;
          switch (inputParts[0].toLowerCase()) {
            case "error":
            lookupParts = ERRORS.lookup(code);
            break;
            case "alarm":
            lookupParts = ALARMS.lookup(code);
            break;
            default:
            return input;
          }
          if (lookupParts == null) {
            return "(" + input + ") An unknown error has occurred";
          } else {
            if (shortString) {
              return input + " (" + lookupParts[1] + ")";
            } else {
              return "(" + input + ") " + lookupParts[2];
            }
          }
        }
      }
    }
    return input;
  }

  @Override protected void rawResponseHandler(String response) {
    String processed = response;
    try {
      boolean verbose = false;
      if (GrblUtils.isOkResponse(response)) {
        this.commandComplete(processed);
      } else {
        if (GrblUtils.isOkErrorAlarmResponse(response)) {
          if (GrblUtils.isAlarmResponse(response)) {
            controllerStatus = ControllerStatusBuilder.newInstance(controllerStatus).setState(ControllerState.ALARM).build();
            Alarm alarm = GrblUtils.parseAlarmResponse(response);
            dispatchAlarm(alarm);
            dispatchStatusString(controllerStatus);
            dispatchStateChange(COMM_IDLE);
          }
          Optional<GcodeCommand> activeCommand = this.getActiveCommand();
          if (activeCommand.isPresent()) {
            processed = String.format(Localization.getString("controller.exception.sendError"), activeCommand.get().getCommandString(), lookupCode(response, false)).replaceAll("\\.\\.", "\\.");
            this.dispatchConsoleMessage(MessageType.ERROR, processed + "\n");
            this.commandComplete(processed);
          } else {
            processed = String.format(Localization.getString("controller.exception.unexpectedError"), lookupCode(response, false)).replaceAll("\\.\\.", "\\.");
            dispatchConsoleMessage(MessageType.INFO, processed + "\n");
          }
          checkStreamFinished();
          processed = "";
        } else {
          if (GrblUtils.isGrblVersionString(response)) {
            this.isReady = true;
            resetBuffers();
            if (getControlState() != COMM_CHECK) {
              this.controllerStatus = null;
            }
            positionPollTimer.stop();
            positionPollTimer.start();
            if (this.isStreaming()) {
              checkStreamFinished();
            }
            this.grblVersion = GrblUtils.getVersionDouble(response);
            this.grblVersionLetter = GrblUtils.getVersionLetter(response);
            this.capabilities = GrblUtils.getGrblStatusCapabilities(this.grblVersion, this.grblVersionLetter);
            try {
              this.sendCommandImmediately(createCommand(GrblUtils.GRBL_VIEW_SETTINGS_COMMAND));
              this.sendCommandImmediately(createCommand(GrblUtils.GRBL_VIEW_PARSER_STATE_COMMAND));
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
            Logger.getLogger(GrblController.class.getName()).log(Level.CONFIG, "{0} = {1}{2}", new Object[] { Localization.getString("controller.log.version"), this.grblVersion, this.grblVersionLetter });
            Logger.getLogger(GrblController.class.getName()).log(Level.CONFIG, "{0} = {1}", new Object[] { Localization.getString("controller.log.realtime"), this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME) });
          } else {
            if (GrblUtils.isGrblProbeMessage(response)) {
              Position p = GrblUtils.parseProbePosition(response, getFirmwareSettings().getReportingUnits());
              if (p != null) {
                dispatchProbeCoordinates(p);
              }
            } else {
              if (GrblUtils.isGrblStatusString(response)) {
                positionPollTimer.receivedStatus();
                verbose = true;
                this.handleStatusString(response);
                this.checkStreamFinished();
              } else {
                if (GrblUtils.isGrblFeedbackMessage(response, capabilities)) {
                  GrblFeedbackMessage grblFeedbackMessage = new GrblFeedbackMessage(response);
                  this.updateParserModalState(new GcodeCommand(GrblUtils.parseFeedbackMessage(response, capabilities)));
                  this.dispatchConsoleMessage(MessageType.VERBOSE, grblFeedbackMessage.toString() + "\n");
                  setDistanceModeCode(grblFeedbackMessage.getDistanceMode());
                  setUnitsCode(grblFeedbackMessage.getUnits());
                  dispatchStateChange(COMM_IDLE);
                } else {
                  if (GrblUtils.isGrblSettingMessage(response)) {
                    GrblSettingMessage message = new GrblSettingMessage(response);
                    processed = message.toString();
                  }
                }
              }
            }
          }
        }
      }
      if (StringUtils.isNotBlank(processed)) {
        if (verbose) {
          this.dispatchConsoleMessage(MessageType.VERBOSE, processed + "\n");
        } else {
          this.dispatchConsoleMessage(MessageType.INFO, processed + "\n");
        }
      }
    } catch (Exception e) {
      String message = "";
      if (e.getMessage() != null) {
        message = ": " + e.getMessage();
      }
      message = Localization.getString("controller.error.response") + " <" + processed + ">" + message;
      logger.log(Level.SEVERE, message, e);
      this.dispatchConsoleMessage(MessageType.ERROR, message + "\n");
    }
  }

  @Override protected void pauseStreamingEvent() throws Exception {
    if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
      this.comm.sendByteImmediately(GrblUtils.GRBL_PAUSE_COMMAND);
    }
  }

  @Override protected void resumeStreamingEvent() throws Exception {
    if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
      this.comm.sendByteImmediately(GrblUtils.GRBL_RESUME_COMMAND);
    }
  }

  @Override protected void closeCommBeforeEvent() {
    positionPollTimer.stop();
  }

  @Override protected void closeCommAfterEvent() {
    this.grblVersion = 0.0;
    this.grblVersionLetter = null;
  }

  @Override protected void openCommAfterEvent() throws Exception {
    this.comm.sendByteImmediately(GrblUtils.GRBL_RESET_COMMAND);
  }

  @Override protected void isReadyToStreamCommandsEvent() throws Exception {
    isReadyToSendCommandsEvent();
    if (this.controllerStatus != null && this.controllerStatus.getState() == ControllerState.ALARM) {
      throw new Exception(Localization.getString("grbl.exception.Alarm"));
    }
  }

  @Override protected void isReadyToSendCommandsEvent() throws Exception {
    if (!this.isReady) {
      throw new Exception(Localization.getString("controller.exception.booting"));
    }
  }

  @Override protected void cancelSendBeforeEvent() throws Exception {
    boolean paused = isPaused();
    if (paused && !this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
      throw new Exception("Cannot cancel while paused with this version of GRBL. Reconnect to reset GRBL.");
    }
    if (capabilities.hasJogging() && controllerStatus != null && controllerStatus.getState() == ControllerState.JOG) {
      this.comm.sendByteImmediately(GrblUtils.GRBL_JOG_CANCEL_COMMAND);
    } else {
      if (!paused && this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
        try {
          this.pauseStreaming();
          this.dispatchStateChange(ControlState.COMM_SENDING_PAUSED);
        } catch (Exception e) {
          System.out.println("Exception while trying to issue a soft reset: " + e.getMessage());
        }
      }
    }
  }

  @Override protected void cancelSendAfterEvent() {
    if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME) && this.getStatusUpdatesEnabled()) {
      this.attemptsRemaining = 50;
      this.isCanceling = true;
      this.lastLocation = null;
    }
  }

  @Override protected Boolean isIdleEvent() {
    if (this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
      return getControlState() == COMM_IDLE || getControlState() == COMM_CHECK;
    }
    return true;
  }

  @Override public ControlState getControlState() {
    if (!this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
      return super.getControlState();
    }
    ControllerState state = this.controllerStatus == null ? ControllerState.UNKNOWN : this.controllerStatus.getState();
    switch (state) {
      case JOG:
      case RUN:
      return ControlState.COMM_SENDING;
      case HOLD:
      case DOOR:
      return ControlState.COMM_SENDING_PAUSED;
      case IDLE:
      if (isStreaming()) {
        return ControlState.COMM_SENDING_PAUSED;
      } else {
        return ControlState.COMM_IDLE;
      }
      case ALARM:
      return ControlState.COMM_IDLE;
      case CHECK:
      if (isStreaming() && comm.isPaused()) {
        return ControlState.COMM_SENDING_PAUSED;
      } else {
        if (isStreaming() && !comm.isPaused()) {
          return ControlState.COMM_SENDING;
        } else {
          return COMM_CHECK;
        }
      }
      default:
      return ControlState.COMM_IDLE;
    }
  }

  /**
     * Sends the version specific homing cycle to the machine.
     */
  @Override public void performHomingCycle() throws Exception {
    if (this.isCommOpen()) {
      String gcode = GrblUtils.getHomingCommand(this.grblVersion, this.grblVersionLetter);
      if (!"".equals(gcode)) {
        GcodeCommand command = createCommand(gcode);
        sendCommandImmediately(command);
        controllerStatus = ControllerStatusBuilder.newInstance(controllerStatus).setState(ControllerState.HOME).build();
        dispatchStatusString(controllerStatus);
        return;
      }
    }
    super.performHomingCycle();
  }

  @Override public void resetCoordinatesToZero() throws Exception {
    if (this.isCommOpen()) {
      String gcode = GrblUtils.getResetCoordsToZeroCommand(this.grblVersion, this.grblVersionLetter);
      if (!"".equals(gcode)) {
        GcodeCommand command = createCommand(gcode);
        this.sendCommandImmediately(command);
        return;
      }
    }
    super.resetCoordinatesToZero();
  }

  @Override public void resetCoordinateToZero(final Axis axis) throws Exception {
    if (this.isCommOpen()) {
      String gcode = GrblUtils.getResetCoordToZeroCommand(axis, this.grblVersion, this.grblVersionLetter);
      if (!"".equals(gcode)) {
        GcodeCommand command = createCommand(gcode);
        this.sendCommandImmediately(command);
        return;
      }
    }
    super.resetCoordinatesToZero();
  }

  @Override public void setWorkPosition(PartialPosition axisPosition) throws Exception {
    if (!this.isCommOpen()) {
      throw new Exception("Must be connected to set work position");
    }
    String gcode = GrblUtils.getSetCoordCommand(axisPosition, this.grblVersion, this.grblVersionLetter);
    if (StringUtils.isNotEmpty(gcode)) {
      GcodeCommand command = createCommand(gcode);
      this.sendCommandImmediately(command);
    }
  }

  @Override public void killAlarmLock() throws Exception {
    if (this.isCommOpen()) {
      String gcode = GrblUtils.getKillAlarmLockCommand(this.grblVersion, this.grblVersionLetter);
      if (!"".equals(gcode)) {
        GcodeCommand command = createCommand(gcode);
        this.sendCommandImmediately(command);
        return;
      }
    }
    super.killAlarmLock();
  }

  @Override public void toggleCheckMode() throws Exception {
    if (this.isCommOpen()) {
      String gcode = GrblUtils.getToggleCheckModeCommand(this.grblVersion, this.grblVersionLetter);
      if (!"".equals(gcode)) {
        GcodeCommand command = createCommand(gcode);
        this.sendCommandImmediately(command);
        return;
      }
    }
    super.toggleCheckMode();
  }

  @Override public void viewParserState() throws Exception {
    if (this.isCommOpen()) {
      String gcode = GrblUtils.getViewParserStateCommand(this.grblVersion, this.grblVersionLetter);
      if (!"".equals(gcode)) {
        GcodeCommand command = createCommand(gcode);
        this.sendCommandImmediately(command);
        return;
      }
    }
    super.viewParserState();
  }

  @Override public void requestStatusReport() throws Exception {
    if (!this.isCommOpen()) {
      throw new RuntimeException("Not connected to the controller");
    }
    comm.sendByteImmediately(GrblUtils.GRBL_STATUS_COMMAND);
  }

  /**
     * If it is supported, a soft reset real-time command will be issued.
     */
  @Override public void softReset() throws Exception {
    if (this.isCommOpen() && this.capabilities.hasCapability(GrblCapabilitiesConstants.REAL_TIME)) {
      this.comm.sendByteImmediately(GrblUtils.GRBL_RESET_COMMAND);
      this.comm.cancelSend();
    }
  }

  @Override public void jogMachine(float dirX, float dirY, float dirZ, double stepSize, double feedRate, Units units) throws Exception {
    if (capabilities.hasCapability(GrblCapabilitiesConstants.HARDWARE_JOGGING)) {
      String commandString = GcodeUtils.generateMoveCommand("G91", stepSize, feedRate, dirX, dirY, dirZ, units);
      GcodeCommand command = createCommand("$J=" + commandString);
      sendCommandImmediately(command);
    } else {
      super.jogMachine(dirX, dirY, dirZ, stepSize, feedRate, units);
    }
  }

  @Override public void jogMachineTo(PartialPosition position, double feedRate) throws Exception {
    if (capabilities.hasCapability(GrblCapabilitiesConstants.HARDWARE_JOGGING)) {
      String commandString = GcodeUtils.generateMoveToCommand("G90", position, feedRate);
      GcodeCommand command = createCommand("$J=" + commandString);
      sendCommandImmediately(command);
    } else {
      super.jogMachineTo(position, feedRate);
    }
  }

  /************
     * Helpers.
     ************/
  public String getGrblVersion() {
    if (this.isCommOpen()) {
      StringBuilder str = new StringBuilder();
      str.append("Grbl ");
      if (this.grblVersion > 0.0) {
        str.append(this.grblVersion);
      }
      if (this.grblVersionLetter != null) {
        str.append(this.grblVersionLetter);
      }
      if (this.grblVersion <= 0.0 && this.grblVersionLetter == null) {
        str.append("<").append(Localization.getString("unknown")).append(">");
      }
      return str.toString();
    }
    return "<" + Localization.getString("controller.log.notconnected") + ">";
  }

  @Override public String getFirmwareVersion() {
    return getGrblVersion();
  }

  @Override public ControllerStatus getControllerStatus() {
    return controllerStatus;
  }

  private void handleStatusString(final String string) {
    if (this.capabilities == null) {
      return;
    }
    ControlState before = getControlState();
    ControllerState beforeState = controllerStatus == null ? ControllerState.UNKNOWN : controllerStatus.getState();
    controllerStatus = GrblUtils.getStatusFromStatusString(controllerStatus, string, capabilities, getFirmwareSettings().getReportingUnits());
    if (before != getControlState()) {
      this.dispatchStateChange(getControlState());
    }
    if (beforeState == ControllerState.JOG && controllerStatus.getState() == ControllerState.IDLE) {
      this.comm.cancelSend();
    }
    if (before == COMM_CHECK && getControlState() != COMM_CHECK) {
      setSingleStepMode(temporaryCheckSingleStepMode);
    } else {
      if (before != COMM_CHECK && getControlState() == COMM_CHECK) {
        temporaryCheckSingleStepMode = getSingleStepMode();
        setSingleStepMode(true);
      }
    }
    if (isCanceling) {
      if (attemptsRemaining > 0 && lastLocation != null) {
        attemptsRemaining--;
        if (controllerStatus.getState() == ControllerState.IDLE || controllerStatus.getState() == ControllerState.CHECK) {
          isCanceling = false;
          this.dispatchStateChange(getControlState());
        } else {
          if (controllerStatus.getState() == ControllerState.HOLD && lastLocation.equals(this.controllerStatus.getMachineCoord())) {
            try {
              this.issueSoftReset();
            } catch (Exception e) {
              this.dispatchConsoleMessage(MessageType.ERROR, e.getMessage() + "\n");
            }
            isCanceling = false;
          }
        }
        if (isCanceling && attemptsRemaining == 0) {
          this.dispatchConsoleMessage(MessageType.ERROR, Localization.getString("grbl.exception.cancelReset") + "\n");
        }
      }
      lastLocation = new Position(this.controllerStatus.getMachineCoord());
    }
    dispatchStatusString(controllerStatus);
  }

  @Override protected void statusUpdatesEnabledValueChanged() {
    if (getStatusUpdatesEnabled()) {
      positionPollTimer.stop();
      positionPollTimer.start();
    } else {
      positionPollTimer.stop();
    }
  }

  @Override protected void statusUpdatesRateValueChanged() {
    positionPollTimer.stop();
    positionPollTimer.start();
  }

  @Override public void sendOverrideCommand(Overrides command) throws Exception {
    Byte realTimeCommand = GrblUtils.getOverrideForEnum(command, capabilities);
    if (realTimeCommand != null) {
      this.dispatchConsoleMessage(MessageType.INFO, String.format(">>> 0x%02x\n", realTimeCommand));
      this.comm.sendByteImmediately(realTimeCommand);
    }
  }
}