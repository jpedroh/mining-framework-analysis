package de.hochschuletrier.gdw.ss14.ecs.systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.utils.Array;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBody;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixEntity;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.CatBoxPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.Component;
import de.hochschuletrier.gdw.ss14.ecs.components.EnemyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.GroundPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent.ToolState;
import de.hochschuletrier.gdw.ss14.ecs.components.StairsPhysicsComponent;
import de.hochschuletrier.gdw.ss14.physics.ICollisionListener;
import de.hochschuletrier.gdw.ss14.physics.RayCastPhysics;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;
import de.hochschuletrier.gdw.ss14.game.Game;

public class CatContactSystem extends ECSystem implements ICollisionListener {
  private static final Logger logger = LoggerFactory.getLogger(CatContactSystem.class);

  private PhysixManager phyManager;

  private RayCastPhysics rcp;

  public CatContactSystem(EntityManager entityManager, PhysixManager physicsManager) {
    super(entityManager);
    phyManager = physicsManager;
  }

  @Override public void fireBeginnCollision(PhysixContact contact) {
    PhysixBody owner = contact.getMyPhysixBody();
    Object o = contact.getOtherPhysixBody().getFixtureList().get(0).getUserData();
    PhysixEntity other = contact.getOtherPhysixBody().getOwner();
    Array<Integer> physicEntities = entityManager.getAllEntitiesWithComponents(PhysicsComponent.class);
    Integer myEntity = null, otherEntity = null;
    PhysicsComponent otherPhysic = null;
    for (Integer i : physicEntities) {
      PhysicsComponent tmp = entityManager.getComponent(i, PhysicsComponent.class);
      if (tmp.physicsBody == contact.getMyPhysixBody()) {
        myEntity = i;
      }
      if (tmp.physicsBody == contact.getOtherPhysixBody()) {
        otherEntity = i;
        otherPhysic = tmp;
      }
    }
    boolean isCatInZone = false, mySightCone = false, otherSightCone = false;
    if (contact.getMyFixture().getUserData() != null) {
      if (contact.getMyFixture().getUserData().equals("masscenter")) {
        isCatInZone = true;
      } else {
        if (contact.getMyFixture().getUserData().equals("sightcone")) {
          mySightCone = true;
        }
      }
    }
    if (contact.getOtherFixture().getUserData() != null) {
      if (contact.getOtherFixture().getUserData().equals("sightcone")) {
        otherSightCone = true;
      }
    }
    if (myEntity == null || otherEntity == null || otherPhysic == null) {
      return;
    }
    Component c = null, d = null;
    if ((c = entityManager.getComponent(otherEntity, EnemyComponent.class)) != null) {
      if (otherPhysic instanceof CatPhysicsComponent) {
        if (mySightCone) {
          return;
        }
        if (otherSightCone) {
        } else {
        }
      }
    } else {
      if ((c = entityManager.getComponent(otherEntity, JumpablePropertyComponent.class)) != null) {
        switch (((JumpablePropertyComponent) c).type) {
          case deadzone:
          if (!isCatInZone) {
            break;
          }
          if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
            ((CatPropertyComponent) d).setState(CatStateEnum.FALL);
          }
          break;
          default:
          break;
        }
      } else {
        if ((c = entityManager.getComponent(otherEntity, StairsPhysicsComponent.class)) != null) {
          if (isCatInZone) {
          }
        } else {
          if (otherPhysic instanceof WoolPhysicsComponent || (c = entityManager.getComponent(otherEntity, WoolPhysicsComponent.class)) != null) {
            if (mySightCone) {
              if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
                ((CatPropertyComponent) d).isInfluenced = true;
              }
              ((WoolPhysicsComponent) otherPhysic).isSeen = true;
            } else {
            }
          } else {
            if ((c = entityManager.getComponent(otherEntity, GroundPropertyComponent.class)) != null) {
              if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
                ((CatPropertyComponent) d).groundWalking = ((GroundPropertyComponent) c).type;
              }
            } else {
              if (otherPhysic instanceof CatBoxPhysicsComponent) {
                if (mySightCone) {
                  return;
                }
                if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
                  CatPropertyComponent catPropertyComponent = (CatPropertyComponent) d;
                  if (!catPropertyComponent.isCatBoxOnCooldown) {
                    if ((d = entityManager.getComponent(myEntity, RenderComponent.class)) != null) {
                      entityManager.removeComponent(myEntity, d);
                    }
                    catPropertyComponent.isHidden = true;
                    catPropertyComponent.isCatBoxOnCooldown = true;
                    catPropertyComponent.catBoxCooldownTimer = CatPropertyComponent.CATBOX_COOLDOWN;
                    Array<Integer> lasers = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);
                    for (Integer entity : lasers) {
                      LaserPointerComponent laserPointerComponent = entityManager.getComponent(entity, LaserPointerComponent.class);
                      laserPointerComponent.toolState = ToolState.WATERPISTOL;
                    }
                  }
                }
              } else {
                if (other instanceof StairsPhysicsComponent) {
                  if (mySightCone) {
                    return;
                  }
                  int entity = ((StairsPhysicsComponent) other).owner;
                  if (entity >= 0) {
                    StairComponent stairComponent = entityManager.getComponent(entity, StairComponent.class);
                    if (stairComponent != null) {
                      Game.mapManager.setFloor(stairComponent.targetFloor);
                    }
                  }
                } else {
                  if (other instanceof FinishPhysicsComponent) {
                    if (mySightCone) {
                      return;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @Override public void update(float delta) {
  }

  @Override public void render() {
  }

  @Override public void fireEndCollision(PhysixContact contact) {
    PhysixBody owner = contact.getMyPhysixBody();
    Object o = contact.getOtherPhysixBody().getFixtureList().get(0).getUserData();
    PhysixEntity other = contact.getOtherPhysixBody().getOwner();
    Array<Integer> physicEntities = entityManager.getAllEntitiesWithComponents(PhysicsComponent.class);
    Integer myEntity = null, otherEntity = null;
    PhysicsComponent otherPhysic = null;
    for (Integer i : physicEntities) {
      PhysicsComponent tmp = entityManager.getComponent(i, PhysicsComponent.class);
      if (tmp.physicsBody == contact.getMyPhysixBody()) {
        myEntity = i;
      }
      if (tmp.physicsBody == contact.getOtherPhysixBody()) {
        otherEntity = i;
        otherPhysic = tmp;
      }
    }
    boolean isCatInZone = false, mySightCone = false, otherSightCone = false;
    if (contact.getMyFixture().getUserData() != null) {
      if (contact.getMyFixture().getUserData().equals("masscenter")) {
        isCatInZone = true;
      } else {
        if (contact.getMyFixture().getUserData().equals("sightcone")) {
          mySightCone = true;
        }
      }
    }
    if (contact.getOtherFixture().getUserData() != null) {
      if (contact.getOtherFixture().getUserData().equals("sightcone")) {
        otherSightCone = true;
      }
    }
    if (myEntity == null || otherEntity == null || otherPhysic == null) {
      return;
    }
    Component c = null, d = null;
    if (otherPhysic instanceof WoolPhysicsComponent || (c = entityManager.getComponent(otherEntity, WoolPhysicsComponent.class)) != null) {
      if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
        ((CatPropertyComponent) d).isInfluenced = false;
      }
      ((WoolPhysicsComponent) otherPhysic).isSeen = false;
    } else {
      if ((c = entityManager.getComponent(otherEntity, EnemyComponent.class)) != null) {
        if (otherSightCone && !mySightCone) {
        }
      }
    }
  }
}