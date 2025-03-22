/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.api.entity;

import net.tridentsdk.api.entity.living.ProjectileSource;

/**
 * Represents a Projectile
 *
 * @author TridentSDK Team
 */
public interface Projectile extends Entity {
    /**
     * Returns the block/entity that was impaled by the projectile
     *
     * @return the impaled object by the projectile
     */
    Impalable getImpaled();

    /**
     * Returns the shooter of the Projectile
     *
     * @param shooter the ProjectileSource of the Projectile
     */
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/3cb7c26d4f0ac4ec978f8b430a299dc0c48e3f09/src/main/java/net/tridentsdk/api/entity/Projectile.java/left.java
    void setShooter(ProjectileSource shooter);
    /**
     * Represents the current tile (Block) that this Projectile is located in
     *
     * @return the current tile this Projectile is in
     */
    Block getCurrentTile();
||||||| /usr/src/app/output/tridentsdk/tridentsdk/3cb7c26d4f0ac4ec978f8b430a299dc0c48e3f09/src/main/java/net/tridentsdk/api/entity/Projectile.java/base.java
    void setShooter(ProjectileSource shooter);

    /**
     * Returns if the Projectile can bounce
     *
     * @return true if the Projectile can bounce, false if it cannot
     */
    boolean doesBounce();

    /**
     * Sets whether the Projectile can bounce
     *
     * @param bounce Boolean whether the Projectile can bounce
     */
    void setBounce(boolean bounce);

    /**
     * Represents the current tile (Block) that this Projectile is located in
     *
     * @return the current tile this Projectile is in
     */
    Block getCurrentTile();
=======
    void setSource(ProjectileSource shooter);
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/3cb7c26d4f0ac4ec978f8b430a299dc0c48e3f09/src/main/java/net/tridentsdk/api/entity/Projectile.java/right.java

    /**
     * The projectile source
     *
     * @return gets the source of the projectile
     */
    ProjectileSource getSource();
}
