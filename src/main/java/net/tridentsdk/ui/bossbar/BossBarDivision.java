/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.ui.bossbar;

import lombok.Getter;

/**
 * Represents the amount of dividing lines on the
 * boss bar (the amount of lines overlayed onto it).
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
@Getter
public enum BossBarDivision {

    /**
     * No dividing lines
     */
    NO_DIVISION(0, 0),

    /**
     * 6 dividing lines
     */
    NOTCHES_6(1, 6),

    /**
     * 10 dividing lines
     */
    NOTCHES_10(2, 10),

    /**
     * 12 dividing lines
     */
    NOTCHES_12(3, 12),

    /**
     * 20 dividing lines
     */
    NOTCHES_20(4, 20);

    /**
     * The internal ID used by net data.
     */
    private final int id;

    /**
     * The number of dividing lines.
     */
    private final int notches;

    BossBarDivision(int id, int notches) {
        this.id = id;
        this.notches = notches;
    }

}
