package org.enderstone.server.entity.goals;
import org.enderstone.server.entity.EnderEntity;

/**
 *
 * @author gyroninja
 */
public interface Goal {
  public abstract boolean start();

  public abstract boolean shouldContinue();

  public abstract EnderEntity getCurrentTarget();

  public abstract void run();

  public abstract void reset();
}