package net.tridentsdk.api.event.entity;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.LivingEntity;
import net.tridentsdk.api.entity.living.Player;

/**
 * Called when an Entity dies
 */
public class EntityDeathEvent extends EntityEvent {
  private Cause cause;

  public EntityDeathEvent(LivingEntity entity, Cause cause) {
    super(entity);
    this.cause = cause;
  }

  public Cause getCause() {
    return cause;
  }

  public EntityDamageEvent getDeathCause() {
    return getEntity().getLastDamageCause();
  }

  public enum Cause {
    STARVATION,
    FIRE,
    FALL,
    EXPLOSION,
    HIT,
    ENDER_PEARL,
    PROJECTILE,
    LIGHTNING,
    DROWNING,
    SUFFOCATION,
    ANVIL,
    CONTACT,
    LAVA,
    POISON,
    WITHER,
    VOID
  }

  /**
     * Returns a Player if a player was involved in the killing of this entity, else null
     * @return
     */
  public Player killedByPlayer() {
    return getEntity().hurtByPlayer();
  }

  /**
     * Returns whether or not a player was involved in the killing of this entity
     * @return
     */
  public boolean wasKilledByPlayer() {
    return killedByPlayer() == null;
  }

  @Override public LivingEntity getEntity() {
    return (LivingEntity) super.getEntity();
  }
}