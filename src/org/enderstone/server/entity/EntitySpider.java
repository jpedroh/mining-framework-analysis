package org.enderstone.server.entity;
import org.enderstone.server.Main;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.Vector;
import org.enderstone.server.entity.goals.Goal;
import org.enderstone.server.entity.goals.GoalAttackEntity;
import org.enderstone.server.entity.targets.TargetEntityInRange;
import org.enderstone.server.entity.player.EnderPlayer;
import org.enderstone.server.regions.EnderWorld;

public class EntitySpider extends EntityMob {
  private static final byte APPEARANCE_ID = (byte) 52;

  public EntitySpider(EnderWorld world, Location location) {
    super(APPEARANCE_ID, world, location);
    this.getNavigator().addGoal(new GoalAttackEntity(this));
    this.getNavigator().addTarget(new TargetEntityInRange(this, EnderPlayer.class));
  }

  @Override protected String getDamageSound() {
    return "mob.spider.say";
  }

  @Override protected String getDeadSound() {
    return "mob.spider.death";
  }

  @Override protected String getRandomSound() {
    if (Main.random.nextBoolean()) {
      return "mob.spider.say";
    } else {
      return "mob.spider.step";
    }
  }

  @Override public float getMovementSpeed() {
    return 4;
  }

  @Override public boolean onCollision(EnderPlayer withPlayer) {
    for (Goal pathfinder : this.getNavigator().getGoals()) {
      if (pathfinder instanceof GoalAttackEntity) {
        GoalAttackEntity entityAttack = (GoalAttackEntity) pathfinder;
        if (entityAttack.getCurrentTarget() != null && entityAttack.getCurrentTarget() instanceof EnderPlayer) {
          if (entityAttack.getCurrentTarget().equals(withPlayer)) {
            withPlayer.damage(2F, Vector.substract(this.getLocation(), withPlayer.getLocation()).multiply(0.2F).add(0, 0.2F, 0));
          }
        }
      }
    }
    return super.onCollision(withPlayer);
  }
}