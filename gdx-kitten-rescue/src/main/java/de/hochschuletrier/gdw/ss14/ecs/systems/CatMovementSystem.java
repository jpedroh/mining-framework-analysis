package de.hochschuletrier.gdw.ss14.ecs.systems;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.InputComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.JumpDataComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.MovementComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

/**
 * Created by Daniel Dreher on 03.10.2014.
 */
public class CatMovementSystem extends ECSystem {
  public float maxVelocity = 0, acceleration = 0;

  public CatMovementSystem(EntityManager entityManager) {
    super(entityManager, 1);
  }

  @Override public void update(float delta) {
    Array<Integer> entities = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, MovementComponent.class, PhysicsComponent.class, InputComponent.class, CatPropertyComponent.class, JumpDataComponent.class);
    Array<Integer> laser = entityManager.getAllEntitiesWithComponents(LaserPointerComponent.class);
    LaserPointerComponent laserPointerComponent = null;
    if (laser.size > 0) {
      laserPointerComponent = entityManager.getComponent(laser.first(), LaserPointerComponent.class);
    }
    for (Integer entity : entities) {
      MovementComponent movementComponent = entityManager.getComponent(entity, MovementComponent.class);
      PhysicsComponent physicsComponent = entityManager.getComponent(entity, PhysicsComponent.class);
      InputComponent inputComponent = entityManager.getComponent(entity, InputComponent.class);
      CatPropertyComponent catPropertyComponent = entityManager.getComponent(entity, CatPropertyComponent.class);
      JumpDataComponent jumpDataComponent = entityManager.getComponent(entity, JumpDataComponent.class);
      if (catPropertyComponent.getState() == CatStateEnum.IDLE || catPropertyComponent.getState() == CatStateEnum.WALK || catPropertyComponent.getState() == CatStateEnum.RUN) {
        Vector2 tmp = new Vector2(inputComponent.whereToGo.x - physicsComponent.getPosition().x, inputComponent.whereToGo.y - physicsComponent.getPosition().y);
        float distance = tmp.len();
        if (!(distance <= 5)) {
          movementComponent.directionVec.x = tmp.x;
          movementComponent.directionVec.y = tmp.y;
        }
        if (maxVelocity == 0) {
          maxVelocity = movementComponent.maxVelocity;
          acceleration = movementComponent.acceleration;
        }
        movementComponent.maxVelocity = maxVelocity;
        movementComponent.acceleration = acceleration;
        if (catPropertyComponent.atePositiveFood) {
          movementComponent.maxVelocity *= 5;
          movementComponent.acceleration *= 5;
        }
        if (movementComponent.oldPositionVec == null) {
          movementComponent.oldPositionVec = movementComponent.directionVec;
        }
        if (movementComponent.positionVec == null) {
          movementComponent.positionVec = movementComponent.directionVec;
        }
        movementComponent.positionVec.x = movementComponent.directionVec.x * movementComponent.percentOfNewCatVelocity + movementComponent.oldPositionVec.x * (1 - movementComponent.percentOfNewCatVelocity);
        movementComponent.positionVec.y = movementComponent.directionVec.y * movementComponent.percentOfNewCatVelocity + movementComponent.oldPositionVec.y * (1 - movementComponent.percentOfNewCatVelocity);
        movementComponent.oldPositionVec = movementComponent.positionVec;
        movementComponent.positionVec.nor();
        if (distance >= 200) {
          movementComponent.velocity += movementComponent.acceleration * delta;
          if (movementComponent.velocity >= movementComponent.maxVelocity) {
            movementComponent.velocity = movementComponent.maxVelocity;
          }
        } else {
          if (distance >= 100) {
            if (movementComponent.velocity >= movementComponent.middleVelocity) {
              movementComponent.velocity += movementComponent.damping * delta;
              if (movementComponent.velocity <= movementComponent.middleVelocity) {
                movementComponent.velocity = movementComponent.middleVelocity;
              }
            } else {
              if (movementComponent.velocity < movementComponent.middleVelocity) {
                movementComponent.velocity += movementComponent.acceleration * delta;
                if (movementComponent.velocity >= movementComponent.middleVelocity) {
                  movementComponent.velocity = movementComponent.middleVelocity;
                }
              }
            }
          } else {
            movementComponent.velocity += movementComponent.damping * 1.5f * delta;
            if (movementComponent.velocity <= movementComponent.minVelocity) {
              movementComponent.velocity = 0;
            }
          }
        }
        if (distance <= 70 && distance >= 30) {
          if (catPropertyComponent.getState() == CatStateEnum.IDLE) {
            if (laserPointerComponent != null) {
              if (laserPointerComponent.isVisible) {
                catPropertyComponent.timeTillJumpTimer = catPropertyComponent.timeTillJumpTimer + delta;
                if (catPropertyComponent.timeTillJumpTimer >= catPropertyComponent.TIME_TILL_JUMP) {
                  catPropertyComponent.setState(CatStateEnum.JUMP);
                  jumpDataComponent.jumpDirection = movementComponent.directionVec.nor();
                }
              }
            }
          }
        } else {
          catPropertyComponent.timeTillJumpTimer = 0.0f;
        }
        movementComponent.directionVec = movementComponent.directionVec.nor();
        float angle = (float) Math.atan2(-movementComponent.directionVec.x, movementComponent.directionVec.y);
        if (laserPointerComponent != null) {
          if (!laserPointerComponent.isVisible) {
            movementComponent.velocity = 0.0f;
          }
          if (!catPropertyComponent.isHidden && laserPointerComponent.isVisible) {
            physicsComponent.setRotation(angle);
          }
        }
      } else {
        if (catPropertyComponent.getState() == CatStateEnum.JUMP) {
          movementComponent.velocity = jumpDataComponent.jumpVelocity;
        } else {
          if (catPropertyComponent.getState() == CatStateEnum.FALL) {
            movementComponent.velocity = 0.0f;
          }
        }
      }
      physicsComponent.setVelocityX(movementComponent.positionVec.x * movementComponent.velocity);
      physicsComponent.setVelocityY(movementComponent.positionVec.y * movementComponent.velocity);
    }
  }

  @Override public void render() {
  }
}