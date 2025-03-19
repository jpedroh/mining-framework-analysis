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
  void setSource(ProjectileSource shooter);

  /**
     * The projectile source
     *
     * @return gets the source of the projectile
     */
  ProjectileSource getSource();
}