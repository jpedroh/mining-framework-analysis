/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
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

	@SuppressWarnings("unchecked")
	@Override
<<<<<<< /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/left.java
	public boolean start() {

		if (mob.getNavigator().getTarget() == null) {

			return false;
||||||| /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/base.java
	public boolean shouldStart() {

		Collection<EnderEntity> entities = (Collection<EnderEntity>) (targetType == EnderPlayer.class ? mob.getWorld().getPlayers() : mob.getWorld().getEntities());

		for (Entity e : entities) {

			if (e.getClass().equals(targetType)) {

				if (mob.getLocation().distanceSquared(e.getLocation()) < 1024) {//32 squared = 1024

					target = (EnderEntity) e;

					return true;
				}
			}
=======
	public boolean shouldStart() {
		Collection<EnderEntity> entities = (Collection<EnderEntity>) (targetType == EnderPlayer.class ? mob.getWorld().getPlayers() : mob.getWorld().getEntities());
		for (Entity e : entities) {
			if (e.getClass().equals(targetType)) {
				if (mob.getLocation().distanceSquared(e.getLocation()) < (16 * 16)) { // range of 16 blocks
					target = (EnderEntity) e;
					return true;
				}
			}
>>>>>>> /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/right.java
		}
<<<<<<< /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/left.java

		pathfindToTarget(mob.getLocation());

		return mob.getNavigator().getPathfinder().hasPath();
||||||| /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/base.java

		return false;
=======
		return false;
>>>>>>> /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/right.java
	}

	@Override
	public boolean shouldContinue() {
<<<<<<< /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/left.java

		return mob.getNavigator().getPath() != null;
||||||| /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/base.java

		if (target.isDead() || (target instanceof EnderPlayer && !((EnderPlayer) target).isOnline)) {

			return false;
		}

		return mob.getLocation().distanceSquared(target.getLocation()) < 1024;//32 squared = 1024
	}

	@Override
	public void start() {

		pathfindToTarget(mob.getLocation());
=======
		if (target.isDead() || (target instanceof EnderPlayer && !((EnderPlayer) target).isOnline)) {
			return false;
		}
		return mob.getLocation().distanceSquared(target.getLocation()) < 1024;// 32 squared = 1024
	}

	@Override
	public void start() {
		pathfindToTarget(mob.getLocation());
>>>>>>> /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/right.java
	}

	@Override
	public void run() {
		lastUpdate++;
		if (lastUpdate > 20) {
			lastUpdate = 0;
			PathTile currentTile = mob.getNavigator().getCurrentTile();
			if (currentTile != null) {
				pathfindToTarget(currentTile.getLocation(mob.getNavigator().getPathfinder().getStartLocation()));
			}
		}
	};

	@Override
	public void reset() {
<<<<<<< /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/left.java
||||||| /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/base.java

		target = null;

=======
		target = null;
>>>>>>> /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/right.java
		mob.getNavigator().setPath(null, null);
	}

	private void pathfindToTarget(Location start) {
<<<<<<< /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/left.java

		PathFinder pathfinder = new PathFinder(mob.getLocation(), mob.getNavigator().getTarget().getLocation(), 32);

||||||| /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/base.java

		PathFinder pathfinder = new PathFinder(mob.getLocation(), target.getLocation(), 32);

=======
		PathFinder pathfinder = new PathFinder(mob, mob.getLocation(), target.getLocation(), 32);
>>>>>>> /usr/src/app/output/sandergielisse/enderstone/17ed8520dacfc399023efb73188f3fd9c599bf01/src/org/enderstone/server/entity/goals/GoalAttackEntity.java/right.java
		List<PathTile> path = pathfinder.calculatePath();
		if (pathfinder.hasPath()) {
			mob.getNavigator().setPath(pathfinder, path);
		} else {
			mob.getNavigator().setPath(null, null);
		}
	}

	@Override
	public EnderEntity getCurrentTarget() {
		return this.target;
	}
}
