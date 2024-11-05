package com.willwinder.universalgcodesender.model;
import com.willwinder.universalgcodesender.IController;
import com.willwinder.universalgcodesender.types.GcodeCommand;
import com.willwinder.universalgcodesender.utils.Settings;
import com.willwinder.universalgcodesender.model.Utils.Units;
import java.io.File;
import java.io.IOException;

public interface BackendAPI extends BackendAPIReadOnly {
  public void setGcodeFile(File file) throws Exception;

  public void setTempDir(File file) throws IOException;

  public void applySettings(Settings settings) throws Exception;

  public void preprocessAndExportToFile(File f) throws Exception;

  public void connect(String firmware, String port, int baudRate) throws Exception;

  public void disconnect() throws Exception;

  public void autoconnect();

  public void sendGcodeCommand(String commandText) throws Exception;

  public void sendGcodeCommand(GcodeCommand command) throws Exception;

  public void adjustManualLocation(int dirX, int dirY, int dirZ, double stepSize, Units units) throws Exception;

  public void send() throws Exception;

  public void pauseResume() throws Exception;

  public void cancel() throws Exception;

  public void returnToZero() throws Exception;

  public void resetCoordinatesToZero() throws Exception;

  public void resetCoordinateToZero(char coordinate) throws Exception;

  public void killAlarmLock() throws Exception;

  public void performHomingCycle() throws Exception;

  public void toggleCheckMode() throws Exception;

  public void issueSoftReset() throws Exception;

  public void requestParserState() throws Exception;

  public IController getController();

  public void applySettingsToController(Settings settings, IController controller) throws Exception;
}