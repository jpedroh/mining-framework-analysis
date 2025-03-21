package com.willwinder.universalgcodesender.listeners;
import com.willwinder.universalgcodesender.model.Alarm;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.types.GcodeCommand;

/**
 * Controller listener event interface
 *
 * @author wwinder
 */
public interface ControllerListener {
  /**
     * An event triggered when a stream is stopped
     */
  void streamCanceled();


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * The file streaming has completed.
     */
  void fileStreamComplete(String filename);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/right.java


  /**
     * An event triggered when a stream is started
     */
  void streamStarted();

  /**
     * An event triggered when a started stream is paused
     */
  void streamPaused();

  /**
     * An event triggered when a paused stream is resumed
     */
  void streamResumed();

  /**
     * The file streaming has completed.
     */
  void streamComplete(String filename);

  /**
     * If an alarm is received from the controller
     *
     * @param alarm the alarm received from the controller
     */
  void receivedAlarm(Alarm alarm);

  /**
     * A command in the stream has been skipped.
     */
  void commandSkipped(GcodeCommand command);

  /**
     * A command has successfully been sent to the controller.
     */
  void commandSent(GcodeCommand command);

  /**
     * A command has been processed by the the controller.
     */
  void commandComplete(GcodeCommand command);

  /**
     * Probe coordinates received.
     */
  void probeCoordinates(Position p);

  /**
     * Controller status information.
     */
  void statusStringListener(ControllerStatus status);
}