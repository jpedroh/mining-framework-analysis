/*
    Copyright 2013-2022 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
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
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/left.java
     * An event triggered when a stream is stopped
     */
    void streamCanceled();

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
||||||| /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/base.java
     * The controller has modified the state by itself, such as pausing a job on
     * an error.
     */
    void controlStateChange(CommunicatorState state);

    /**
=======
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/right.java
     * The file streaming has completed.
     */
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/left.java
    void streamComplete(String filename);
||||||| /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/base.java
    void fileStreamComplete(String filename, boolean success);
=======
    void fileStreamComplete(String filename);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/ecb4e57f1fd34179074d7194438f5e864f84b619/ugs-core/src/com/willwinder/universalgcodesender/listeners/ControllerListener.java/right.java

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
