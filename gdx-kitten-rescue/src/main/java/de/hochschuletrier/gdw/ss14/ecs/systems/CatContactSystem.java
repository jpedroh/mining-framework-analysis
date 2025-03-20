package de.hochschuletrier.gdw.ss14.ecs.systems;
import javax.security.auth.callback.Callback;
import javax.swing.text.html.parser.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.*;
import de.hochschuletrier.gdw.ss14.ecs.components.*;
import de.hochschuletrier.gdw.ss14.states.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixBody;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixContact;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixEntity;
import de.hochschuletrier.gdw.commons.gdx.physix.PhysixManager;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.physics.ICollisionListener;
import de.hochschuletrier.gdw.ss14.physics.RayCastPhysics;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpablePhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpablePropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.WoolPhysicsComponent;

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
    boolean isCatInZone = false;
    if (contact.getMyFixture().getUserData() != null && contact.getMyFixture().getUserData().equals("masscenter")) {
      isCatInZone = true;
    }
    if (myEntity == null || otherEntity == null || otherPhysic == null) {
      return;
    }
    Component c = null, d = null;
    if (
<<<<<<< /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/left.java
    (c = entityManager.getComponent(otherEntity, EnemyComponent.class)) != null
=======
    other instanceof RectPhysicsComponent
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java
    ) {
      if (otherPhysic instanceof ConePhysicsComponent) {
        phyManager.getWorld().rayCast(rcp, other.getPosition(), owner.getPosition());
      } else {
        if (otherPhysic instanceof CatPhysicsComponent) {
        }
      }
    } else {
      if (
<<<<<<< /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/left.java
      (c = entityManager.getComponent(otherEntity, JumpablePropertyComponent.class)) != null
=======
      other instanceof CatPhysicsComponent
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java
      ) {
        switch (((JumpablePropertyComponent) c).type) {
          case deadzone:
          if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
            ((CatPropertyComponent) d).setState(CatStateEnum.FALL);
          }
          break;
          default:
          break;
        }

<<<<<<< Unknown file: This is a bug in JDime.
=======
        if (rcp.m_hit && rcp.m_fraction <= ((CatPhysicsComponent) other).coneRadius) {
          for (Fixture f : other.physicsBody.getFixtureList()) {
            if (rcp.m_fixture == f) {
              EnemyComponent.seeCat = true;
              logger.debug("Katze sichtbar f\u00fcr Hund");
            }
          }
        } else {
        }
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java
      } else {
        if ((c = entityManager.getComponent(otherEntity, GroundPropertyComponent.class)) != null) {
          if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
            ((CatPropertyComponent) d).groundWalking = ((GroundPropertyComponent) c).type;
          }
          ((WoolPhysicsComponent) other).isSeen = true;
          logger.debug("WOOOOOOOOOOOOOOOLL");
        } else {
          if (otherPhysic instanceof CatBoxPhysicsComponent) {
            if ((d = entityManager.getComponent(myEntity, CatPhysicsComponent.class)) != null) {
              entityManager.removeComponent(myEntity, d);
            }
            if ((d = entityManager.getComponent(myEntity, CatPropertyComponent.class)) != null) {
              ((CatPropertyComponent) d).isHidden = true;
            }
            Array<Integer> lasers = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);
            for (Integer entity : lasers) {
              LaserPointerComponent laserPointerComponent = entityManager.getComponent(entity, LaserPointerComponent.class);

<<<<<<< /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/left.java
              laserPointerComponent.isVisible = false;
=======
              if (puddlecompo == other) {
                if (property.type == JumpableState.deadzone) {
                  boolean isCatInZone = false;
                  if (contact.getMyFixture().getUserData() == null) {
                    return;
                  }
                  if (contact.getMyFixture().getUserData().equals("masscenter")) {
                    isCatInZone = true;
                  }
                  if (isCatInZone) {
                    Array<Integer> entities = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, PhysicsComponent.class);
                    if (entities.size > 0) {
                      int player = entities.first();
                      CatPropertyComponent catPropertyComponent = entityManager.getComponent(player, CatPropertyComponent.class);
                      catPropertyComponent.setState(CatStateEnum.FALL);
                    }
                    if (entities.size > 0) {
                      int player = entities.first();
                      CatPropertyComponent catPropertyComponent = entityManager.getComponent(player, CatPropertyComponent.class);
                      catPropertyComponent.setState(CatStateEnum.FALL);
                    }
                  }
                } else {
                  if (property.type == JumpableState.waterpuddle || property.type == JumpableState.bloodpuddle) {
                    boolean isCatInZone = false;
                    if (contact.getMyFixture().getUserData() == null) {
                      return;
                    }
                    if (contact.getMyFixture().getUserData().equals("masscenter")) {
                      isCatInZone = true;
                    }
                    if (isCatInZone) {
                      Array<Integer> entities = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, PhysicsComponent.class);
                      if (entities.size > 0) {
                        int player = entities.first();
                        CatPropertyComponent catPropertyComponent = entityManager.getComponent(player, CatPropertyComponent.class);
                        catPropertyComponent.isAlive = false;
                      }
                    }
                  }
                }
              }
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java
            }
          } else {

<<<<<<< Unknown file: This is a bug in JDime.
=======
            if (other == null) {
              if (!(o instanceof String)) {
                return;
              }
            } else {
              if (other instanceof CatBoxPhysicsComponent) {
                Array<Integer> entities = entityManager.getAllEntitiesWithComponents(CatPropertyComponent.class, RenderComponent.class);
                if (entities.size > 0) {
                  int player = entities.first();
                  RenderComponent renderComponent = entityManager.getComponent(player, RenderComponent.class);
                  CatPropertyComponent catPropertyComponent = entityManager.getComponent(player, CatPropertyComponent.class);
                  if (!catPropertyComponent.isCatBoxOnCooldown) {
                    catPropertyComponent.isCatBoxOnCooldown = true;
                    catPropertyComponent.catBoxCooldownTimer = catPropertyComponent.CATBOX_COOLDOWN;
                    entityManager.removeComponent(player, renderComponent);
                    catPropertyComponent.isHidden = true;
                  } else {
                    return;
                  }
                }
                Array<Integer> lasers = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);
                for (Integer entity : lasers) {
                  LaserPointerComponent laserPointerComponent = entityManager.getComponent(entity, LaserPointerComponent.class);
                  laserPointerComponent.isVisible = false;
                }
              } else {
                if (other instanceof StairsPhysicsComponent) {
                }
              }
            }
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java

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

<<<<<<< Unknown file: This is a bug in JDime.
=======
    PhysixBody owner = contact.getMyPhysixBody();
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    Object o = contact.getOtherPhysixBody().getFixtureList().get(0).getUserData();
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    PhysixEntity other = contact.getOtherPhysixBody().getOwner();
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (other instanceof WoolPhysicsComponent) {
      Array<Integer> compos = entityManager.getAllEntitiesWithComponents(PlayerComponent.class);
      CatPropertyComponent player = entityManager.getComponent(compos.get(0), CatPropertyComponent.class);
      ((WoolPhysicsComponent) other).isSeen = false;
      player.isInfluenced = false;
    }
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (other instanceof CatPhysicsComponent) {
      EnemyComponent.seeCat = false;
    }
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/systems/CatContactSystem.java/right.java
  }
}