package org.enderstone.server.entity.goals;
import java.util.List;
import org.enderstone.server.api.Location;
import org.enderstone.server.entity.EntityMob;
import org.enderstone.server.entity.pathfinding.PathFinder;
import org.enderstone.server.entity.pathfinding.PathTile;
import org.enderstone.server.entity.player.EnderPlayer;

/**
 *
 * @author gyroninja
 */
public class GoalAttackEntity implements Goal {
  private final EntityMob mob;

  private int lastUpdate;

  public GoalAttackEntity(EntityMob mob) {
    this.mob = mob;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) @Override public boolean shouldStart() {
    Collection<EnderEntity> entities = (Collection<EnderEntity>) (targetType == EnderPlayer.class ? mob.getWorld().getPlayers() : mob.getWorld().getEntities());
    for (Entity e : entities) {
      if (e.getClass().equals(targetType)) {
        if (mob.getLocation().distanceSquared(e.getLocation()) < (16 * 16)) {
          target = (EnderEntity) e;
          return true;
        }
      }
    }
    return false;
  }
>>>>>>> /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/right.java


  @Override public boolean start() {
    if (mob.getNavigator().getTarget() == null) {
      return false;
    }
    pathfindToTarget(mob.getLocation());
    return mob.getNavigator().getPathfinder().hasPath();
  }

  @Override public boolean shouldContinue() {
    return mob.getNavigator().getPath() != null;
  }

  @Override public void run() {
    lastUpdate++;
    if (lastUpdate > 20) {
      lastUpdate = 0;
      PathTile currentTile = mob.getNavigator().getCurrentTile();
      if (currentTile != null) {
        pathfindToTarget(currentTile.getLocation(mob.getNavigator().getPathfinder().getStartLocation()));
      }
    }
  }



  @Override public void reset() {
    mob.getNavigator().setPath(null, null);
  }

  private void pathfindToTarget(Location start) {
    PathFinder pathfinder = new PathFinder(mob, mob.getLocation(), mob.getNavigator().getTarget().getLocation(), 32);
    List<PathTile> path = pathfinder.calculatePath();
    if (pathfinder.hasPath()) {
      mob.getNavigator().setPath(pathfinder, path);
    } else {
      mob.getNavigator().setPath(null, null);
    }
  }

  @Override public EnderEntity getCurrentTarget() {
    return this.target;
  }
}