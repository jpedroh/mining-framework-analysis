package com.willwinder.universalgcodesender;
import com.google.gson.JsonObject;
import com.willwinder.universalgcodesender.firmware.IFirmwareSettings;
import com.willwinder.universalgcodesender.firmware.tinyg.TinyGFirmwareSettings;
import com.willwinder.universalgcodesender.gcode.TinyGGcodeCommandCreator;
import com.willwinder.universalgcodesender.gcode.util.GcodeUtils;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.ControllerStatus;
import com.willwinder.universalgcodesender.listeners.ControllerStatusBuilder;
import com.willwinder.universalgcodesender.listeners.MessageType;
import com.willwinder.universalgcodesender.model.*;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.types.TinyGGcodeCommand;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.willwinder.universalgcodesender.model.UGSEvent.ControlState.COMM_CHECK;
import static com.willwinder.universalgcodesender.model.UGSEvent.ControlState.COMM_IDLE;
import static com.willwinder.universalgcodesender.model.UGSEvent.ControlState.COMM_SENDING;
import static com.willwinder.universalgcodesender.model.UGSEvent.ControlState.COMM_SENDING_PAUSED;

/**
 * TinyG Control layer, coordinates all aspects of control.
 *
 * @author wwinder
 * @author Joacim Breiler
 */
public class TinyGController extends AbstractController {
  private static final Logger LOGGER = Logger.getLogger(TinyGController.class.getSimpleName());

  private static final String NOT_SUPPORTED_YET = "Not supported yet.";

  private static final String STATUS_REPORT_CONFIG = "{sr:{posx:t, posy:t, posz:t, mpox:t, mpoy:t, mpoz:t, plan:t, vel:t, unit:t, stat:t, dist:t, frmo:t, coor:t}}";

  private static final double LATEST_TINYG_FIRMWARE_VERSION = 0.97;

  protected final Capabilities capabilities;

  private final TinyGFirmwareSettings firmwareSettings;

  protected ControllerStatus controllerStatus;

  protected String firmwareVersion;

  protected double firmwareVersionNumber;

  public TinyGController() {
    this(new TinyGCommunicator());
  }

  public TinyGController(ICommunicator communicator) {
    super(communicator);
    capabilities = new Capabilities();
    commandCreator = new TinyGGcodeCommandCreator();
    firmwareSettings = new TinyGFirmwareSettings(this);
    communicator.addListener(firmwareSettings);
    controllerStatus = new ControllerStatus(ControllerState.UNKNOWN, new Position(0, 0, 0, UnitUtils.Units.MM), new Position(0, 0, 0, UnitUtils.Units.MM));
    firmwareVersion = "TinyG unknown version";
  }

  @Override public Boolean handlesAllStateChangeEvents() {
    return false;
  }

  @Override public Capabilities getCapabilities() {
    return capabilities;
  }

  @Override public IFirmwareSettings getFirmwareSettings() {
    return firmwareSettings;
  }

  @Override protected void closeCommBeforeEvent() {
  }

  @Override protected void closeCommAfterEvent() {
  }

  @Override protected void openCommAfterEvent() {
    try {
      this.comm.sendByteImmediately(TinyGUtils.COMMAND_RESET);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override protected void cancelSendBeforeEvent() throws Exception {
    pauseStreaming();
  }

  @Override public void jogMachine(float dirX, float dirY, float dirZ, double stepSize, double feedRate, UnitUtils.Units units) throws Exception {
    UnitUtils.Units targetUnits = UnitUtils.Units.getUnits(getCurrentGcodeState().units);
    double scale = UnitUtils.scaleUnits(units, targetUnits);
    String commandString = GcodeUtils.generateMoveCommand("G91G1", stepSize * scale, feedRate * scale, dirX, dirY, dirZ, targetUnits);
    GcodeCommand command = createCommand(commandString);
    command.setTemporaryParserModalChange(true);
    sendCommandImmediately(command);
    restoreParserModalState();
  }

  @Override public void jogMachineTo(final PartialPosition position, final double feedRate) throws Exception {
    UnitUtils.Units targetUnits = UnitUtils.Units.getUnits(getCurrentGcodeState().units);
    double scale = UnitUtils.scaleUnits(position.getUnits(), targetUnits);
    PartialPosition positionInTargetUnits = position.getPositionIn(targetUnits);
    String commandString = GcodeUtils.generateMoveToCommand("G90G1", positionInTargetUnits, feedRate * scale);
    GcodeCommand command = createCommand(commandString);
    command.setTemporaryParserModalChange(true);
    sendCommandImmediately(command);
    restoreParserModalState();
  }

  @Override protected void cancelSendAfterEvent() throws Exception {
    comm.sendByteImmediately(TinyGUtils.COMMAND_PAUSE);
    comm.sendByteImmediately(TinyGUtils.COMMAND_QUEUE_FLUSH);
    comm.cancelSend();
  }

  @Override protected void pauseStreamingEvent() throws Exception {
    comm.sendByteImmediately(TinyGUtils.COMMAND_PAUSE);
  }

  @Override protected void resumeStreamingEvent() throws Exception {
    comm.sendByteImmediately(TinyGUtils.COMMAND_RESUME);
  }

  @Override protected Boolean isIdleEvent() {
    return getControlState() == COMM_IDLE || getControlState() == COMM_CHECK;
  }

  @Override protected void rawResponseHandler(String response) {
    JsonObject jo;
    try {
      jo = TinyGUtils.jsonToObject(response);
    } catch (Exception ignored) {
      this.dispatchConsoleMessage(MessageType.VERBOSE, response + "\n");
      return;
    }
    if (TinyGUtils.isRestartingResponse(jo)) {
      this.dispatchConsoleMessage(MessageType.INFO, "[restarting] " + response + "\n");
    } else {
      if (TinyGUtils.isReadyResponse(jo)) {
        handleReadyResponse(response, jo);
      } else {
        if (jo.has("ack")) {
          dispatchConsoleMessage(MessageType.INFO, "[ack] " + response + "\n");
          sendInitCommands();
        } else {
          if (TinyGUtils.isStatusResponse(jo)) {
            updateControllerStatus(jo);
            dispatchConsoleMessage(MessageType.INFO, response + "\n");
            checkStreamFinished();
          } else {
            if (TinyGGcodeCommand.isOkErrorResponse(response)) {
              if (jo.get("r").getAsJsonObject().has(TinyGUtils.FIELD_STATUS_REPORT)) {
                updateControllerStatus(jo.get("r").getAsJsonObject());
                checkStreamFinished();
              } else {
                if (rowsRemaining() > 0) {
                  try {
                    commandComplete(response);
                  } catch (Exception e) {
                    this.dispatchConsoleMessage(MessageType.ERROR, Localization.getString("controller.error.response") + " <" + response + ">: " + e.getMessage());
                  }
                }
              }
              this.dispatchConsoleMessage(MessageType.INFO, response + "\n");
            } else {
              if (TinyGGcodeCommand.isQueueReportResponse(response)) {
                LOGGER.log(Level.FINE, "Queue buffer usage: " + jo.get("qr").getAsString());
              } else {
                if (TinyGGcodeCommand.isRecieveQueueReportResponse(response)) {
                  LOGGER.log(Level.FINE, "Receive queue buffer usage: " + jo.get("rx").getAsString());
                } else {
                  this.dispatchConsoleMessage(MessageType.INFO, "[unhandled message] " + response + "\n");
                }
              }
            }
          }
        }
      }
    }
  }

  protected void handleReadyResponse(String response, JsonObject jo) {
    if (TinyGUtils.isTinyGVersion(jo)) {
      firmwareVersionNumber = TinyGUtils.getVersion(jo);
      firmwareVersion = "TinyG " + firmwareVersionNumber;
    }
    if (firmwareVersionNumber > LATEST_TINYG_FIRMWARE_VERSION) {
      dispatchConsoleMessage(MessageType.ERROR, String.format(Localization.getString("tinyg.exception.unknownVersion"), firmwareVersionNumber) + "\n");
      return;
    }
    capabilities.addCapability(CapabilitiesConstants.RETURN_TO_ZERO);
    capabilities.addCapability(CapabilitiesConstants.JOGGING);
    capabilities.removeCapability(CapabilitiesConstants.CONTINUOUS_JOGGING);
    capabilities.addCapability(CapabilitiesConstants.HOMING);
    capabilities.addCapability(CapabilitiesConstants.FIRMWARE_SETTINGS);
    capabilities.removeCapability(CapabilitiesConstants.OVERRIDES);
    capabilities.removeCapability(CapabilitiesConstants.SETUP_WIZARD);
    setCurrentState(COMM_IDLE);
    dispatchConsoleMessage(MessageType.INFO, "[ready] " + response + "\n");
    sendInitCommands();
  }

  private void updateControllerStatus(JsonObject jo) {
    ControllerState previousState = controllerStatus.getState();
    UGSEvent.ControlState previousControlState = getControlState(previousState);
    List<String> gcodeList = TinyGUtils.convertStatusReportToGcode(jo);
    gcodeList.forEach((gcode) -> updateParserModalState(new GcodeCommand(gcode)));
    controllerStatus = TinyGUtils.updateControllerStatus(controllerStatus, jo);
    dispatchStatusString(controllerStatus);
    UGSEvent.ControlState newControlState = getControlState(controllerStatus.getState());
    if (!previousControlState.equals(newControlState)) {
      LOGGER.log(Level.FINE, "Changing state from " + previousControlState + " to " + newControlState);
      setCurrentState(newControlState);
    }
  }

  protected void sendInitCommands() {
    comm.queueCommand(new GcodeCommand("{ej:1}"));
    comm.queueCommand(new GcodeCommand(STATUS_REPORT_CONFIG));
    comm.queueCommand(new GcodeCommand("{jv:4}"));
    comm.queueCommand(new GcodeCommand("{qv:0}"));
    comm.queueCommand(new GcodeCommand("{sv:1}"));
    comm.queueCommand(new GcodeCommand("{sr:n}"));
    comm.streamCommands();
    setStatusUpdateRate(getStatusUpdateRate());
  }

  @Override public void updateParserModalState(GcodeCommand command) {
    if (!command.getCommandString().startsWith("{")) {
      super.updateParserModalState(command);
    }
  }

  @Override public void performHomingCycle() throws Exception {
    sendCommandImmediately(new GcodeCommand("G28.2 Z0 X0 Y0"));
  }

  @Override public void resetCoordinatesToZero() throws Exception {
    String command = TinyGUtils.generateResetCoordinatesToZeroCommand(controllerStatus, getCurrentGcodeState());
    sendCommandImmediately(new GcodeCommand(command));
  }

  @Override public void killAlarmLock() throws Exception {
    sendCommandImmediately(new GcodeCommand(TinyGUtils.COMMAND_KILL_ALARM_LOCK));
  }

  @Override public void toggleCheckMode() {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
  }

  @Override public void viewParserState() throws Exception {
    if (this.isCommOpen()) {
      sendCommandImmediately(new GcodeCommand(TinyGUtils.COMMAND_STATUS_REPORT));
    }
  }

  @Override public void requestStatusReport() throws Exception {
    viewParserState();
  }

  @Override public void softReset() throws Exception {
    comm.cancelSend();
    comm.sendByteImmediately(TinyGUtils.COMMAND_RESET);
    setCurrentState(UGSEvent.ControlState.COMM_DISCONNECTED);
    controllerStatus = ControllerStatusBuilder.newInstance(controllerStatus).setState(ControllerState.DISCONNECTED).build();
    dispatchStatusString(controllerStatus);
  }

  @Override public void setWorkPosition(PartialPosition axisPosition) throws Exception {
    String command = TinyGUtils.generateSetWorkPositionCommand(controllerStatus, getCurrentGcodeState(), axisPosition);
    sendCommandImmediately(new GcodeCommand(command));
  }

  @Override protected void isReadyToStreamCommandsEvent() {
  }

  @Override protected void isReadyToSendCommandsEvent() {
  }

  @Override protected void statusUpdatesEnabledValueChanged() {
  }

  @Override protected void statusUpdatesRateValueChanged() {
    comm.queueCommand(new GcodeCommand("{si:" + getStatusUpdateRate() + "}"));
  }

  @Override public void sendOverrideCommand(Overrides command) throws Exception {
    ControllerStatus.OverridePercents currentOverrides = controllerStatus.getOverrides();
    Optional<GcodeCommand> gcodeCommand = TinyGUtils.createOverrideCommand(currentOverrides, command);
    if (gcodeCommand.isPresent()) {
      sendCommandImmediately(gcodeCommand.get());
    }
  }

  @Override public String getFirmwareVersion() {
    return firmwareVersion;
  }

  @Override public ControllerStatus getControllerStatus() {
    return controllerStatus;
  }

  @Override public UGSEvent.ControlState getControlState() {
    return getControlState(getControllerStatus().getState());
  }

  protected UGSEvent.ControlState getControlState(ControllerState controllerState) {
    switch (controllerState) {
      case JOG:
      case RUN:
      return COMM_SENDING;
      case HOLD:
      case DOOR:
      return COMM_SENDING_PAUSED;
      case IDLE:
      if (isStreaming()) {
        return COMM_SENDING_PAUSED;
      } else {
        return COMM_IDLE;
      }
      case ALARM:
      return COMM_IDLE;
      case CHECK:
      if (isStreaming() && comm.isPaused()) {
        return COMM_SENDING_PAUSED;
      } else {
        if (isStreaming() && !comm.isPaused()) {
          return COMM_SENDING;
        } else {
          return COMM_CHECK;
        }
      }
      default:
      return COMM_IDLE;
    }
  }
}