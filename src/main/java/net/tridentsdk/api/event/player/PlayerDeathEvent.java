package net.tridentsdk.api.event.player;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.entity.EntityDeathEvent;

/**
 * Called when a Player dies
 */
public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {
  private EntityDeathEvent.Cause cause;

  public PlayerDeathEvent(Player player, EntityDeathEvent.Cause cause) {
    super(player);
    this.cause = cause;
  }

  public EntityDeathEvent.Cause getCause() {
    return cause;
  }

  public Player getPlayer() {
    return (Player) super.getEntity();
  }
}