package de.hochschuletrier.gdw.ss14.ecs.systems;
import com.badlogic.gdx.utils.Array;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.InputComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.MovementComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

public class PlayerMovementSystem extends ECSystem {
  public PlayerMovementSystem(EntityManager entityManager) {
    super(entityManager, 1);
  }

  @Override public void render() {
  }

  @Override public void update(float delta) {
    Array<Integer> compos = entityManager.getAllEntitiesWithComponents(PlayerComponent.class, MovementComponent.class, PhysicsComponent.class, InputComponent.class, CatPropertyComponent.class);
    for (Integer integer : compos) {
      MovementComponent moveCompo = entityManager.getComponent(integer, MovementComponent.class);
      PhysicsComponent phyCompo = entityManager.getComponent(integer, PhysicsComponent.class);
      InputComponent inputCompo = entityManager.getComponent(integer, InputComponent.class);
      CatPropertyComponent catStateCompo = entityManager.getComponent(integer, CatPropertyComponent.class);
      if (!catStateCompo.canSeeLaserPointer) {
        return;
      }
      if (moveCompo.velocity == 0) {
        catStateCompo.state = CatStateEnum.IDLE;
      } else {
        if (moveCompo.velocity > 0 && moveCompo.velocity < moveCompo.MIDDLE_VELOCITY) {
          catStateCompo.state = CatStateEnum.WALK;
        } else {
          if (moveCompo.velocity > moveCompo.MIDDLE_VELOCITY && moveCompo.velocity < moveCompo.MAX_VELOCITY) {
            catStateCompo.state = CatStateEnum.RUN;
          }
        }
      }
      moveCompo.directionVec = inputCompo.whereToGo.sub(phyCompo.getPosition());
      float distance = moveCompo.directionVec.len();
      if (distance <= 30 && (catStateCompo.state == CatStateEnum.IDLE)) {
        catStateCompo.jumpBuffer += delta;
        if (catStateCompo.jumpBuffer >= 500) {
          catStateCompo.state = CatStateEnum.JUMP;
        }
      }
      if (distance >= 200) {
        moveCompo.velocity += moveCompo.ACCELERATION * delta;
        if (moveCompo.velocity >= moveCompo.MAX_VELOCITY) {
          moveCompo.velocity = moveCompo.MAX_VELOCITY;
        }
      } else {
        if (distance >= 100) {
          if (moveCompo.velocity >= moveCompo.MIDDLE_VELOCITY) {
            moveCompo.velocity += moveCompo.DAMPING * delta;
            if (moveCompo.velocity <= moveCompo.MIDDLE_VELOCITY) {
              moveCompo.velocity = moveCompo.MIDDLE_VELOCITY;
            }
          } else {
            if (moveCompo.velocity < moveCompo.MIDDLE_VELOCITY) {
              moveCompo.velocity += moveCompo.ACCELERATION * delta;
              if (moveCompo.velocity >= moveCompo.MIDDLE_VELOCITY) {
                moveCompo.velocity = moveCompo.MIDDLE_VELOCITY;
              }
            }
          }
        } else {
          if (catStateCompo.state == CatStateEnum.JUMP) {
            moveCompo.velocity = 200;
          } else {
            moveCompo.velocity += moveCompo.DAMPING * 1.5f * delta;
            if (moveCompo.velocity <= moveCompo.MIN_VELOCITY) {
              moveCompo.velocity = 0;
            }
          }
        }
      }
      moveCompo.directionVec = moveCompo.directionVec.nor();
      float angle = (float) Math.atan2(-moveCompo.directionVec.x, moveCompo.directionVec.y);
      phyCompo.setRotation(angle);
      phyCompo.setVelocityX(moveCompo.directionVec.x * moveCompo.velocity);
      phyCompo.setVelocityY(moveCompo.directionVec.y * moveCompo.velocity);
    }
  }
}