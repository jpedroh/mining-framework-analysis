/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.effect;

import net.tridentsdk.entity.living.Player;
import net.tridentsdk.util.Vector;

/**
 * Represents effects that can be executed without an entity
 *
 * @author The TridentSDK Team
 * @since 0.4-alpha
 */
public interface RemoteEffect<T> extends Effect<T> {

    /**
     * Execute the effect at the given location for all nearby players
     *
     * @param vector The vector of the effect
     */
    void apply(Vector vector);

    /**
     * Execute the effect at the given location for specified player
     *
     * @param player The player to send the effect to
     * @param vector The vector of the effect
     */
    void apply(Player player, Vector vector);

    /**
     * Set the position of the effect
     *
     * @param vector The vector of the effect
     */
    void setPosition(Vector vector);

}
