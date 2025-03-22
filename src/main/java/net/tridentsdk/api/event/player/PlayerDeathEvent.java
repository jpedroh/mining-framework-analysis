/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.api.event.player;

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDeathEvent;

/**
 * Called when a Player dies
 */

import net.tridentsdk.api.event.Cancellable;

public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {

<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/dc340947a28c96d0b5ea6ba459a24c63deebb5b2/src/main/java/net/tridentsdk/api/event/player/PlayerDeathEvent.java/left.java
    private EntityDeathEvent.Cause cause;
||||||| /usr/src/app/output/tridentsdk/tridentsdk/dc340947a28c96d0b5ea6ba459a24c63deebb5b2/src/main/java/net/tridentsdk/api/event/player/PlayerDeathEvent.java/base.java
    /**
     * TODO add cause of death
     *
     * @param player the player associated with this event (that died)
     */
=======
    /**
     * @param player the player associated with this event (that died)
     */
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/dc340947a28c96d0b5ea6ba459a24c63deebb5b2/src/main/java/net/tridentsdk/api/event/player/PlayerDeathEvent.java/right.java

    public PlayerDeathEvent(Player player, EntityDeathEvent.Cause cause) {
        super(player);
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/dc340947a28c96d0b5ea6ba459a24c63deebb5b2/src/main/java/net/tridentsdk/api/event/player/PlayerDeathEvent.java/left.java
        this.cause = cause;
    }

    public EntityDeathEvent.Cause getCause() {
        return cause;
||||||| /usr/src/app/output/tridentsdk/tridentsdk/dc340947a28c96d0b5ea6ba459a24c63deebb5b2/src/main/java/net/tridentsdk/api/event/player/PlayerDeathEvent.java/base.java
=======
    }

    public Player getPlayer() {
        return (Player) super.getEntity();
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/dc340947a28c96d0b5ea6ba459a24c63deebb5b2/src/main/java/net/tridentsdk/api/event/player/PlayerDeathEvent.java/right.java
    }
}
