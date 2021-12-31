/*
    Copyright 2021 Will Winder

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
package com.willwinder.universalgcodesender.model.events;

import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.model.UGSEvent;

/**
 * An event that will be dispatched when the controller state has changed.
 *
 * @author Joacim Breiler
 */
public class ControllerStateEvent implements UGSEvent {

    private final ControllerState state;
    private final ControllerState previousState;

    public ControllerStateEvent(ControllerState state, ControllerState previousState) {
        this.state = state;
        this.previousState = previousState;
    }

    public ControllerState getState() {
        return state;
    }

    public ControllerState getPreviousState() {
        return previousState;
    }
}
