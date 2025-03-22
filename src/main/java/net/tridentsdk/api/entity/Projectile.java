package net.tridentsdk.api.entity;
import net.tridentsdk.api.Block;
import net.tridentsdk.api.entity.living.ProjectileSource;

/**
 * Represents a Projectile
 *
 * @author TridentSDK Team
 */
public interface Projectile extends Entity {
  /**
     * Represents the shooter of this Projectile, if applicable
     *
     * @return the shooter of this Projectile
     */
  ProjectileSource getShooter();

  /**
     * Returns the shooter of the Projectile
     *
     * @param shooter the ProjectileSource of the Projectile
     */
  void setShooter(ProjectileSource shooter);

  boolean doesBounce();

  void setBounce(boolean bouncy);

  /**
     * Represents the current tile (Block) that this Projectile is located in
     *
     * @return the current tile this Projectile is in
     */
  Block getCurrentTile();

  /**
     * @return
     */
  ProjectileSource getProjectileSource();
}