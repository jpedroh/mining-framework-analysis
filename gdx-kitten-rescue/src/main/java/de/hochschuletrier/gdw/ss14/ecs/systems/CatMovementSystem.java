package de.hochschuletrier.gdw.ss14.ecs.systems;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.*;
import de.hochschuletrier.gdw.ss14.ecs.components.LaserPointerComponent.ToolState;
import de.hochschuletrier.gdw.ss14.sound.SoundManager;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;

/**
 * Created by Daniel Dreher on 03.10.2014.
 */
public class CatMovementSystem extends ECSystem {
  private float maxVelocity = 0.0f, acceleration = 0.0f, foodBuffer = 0.0f;

  private static final float MaxAngularVelocity = (float) (4 * Math.PI);

  public CatMovementSystem(EntityManager entityManager) {
    super(entityManager, 1);
  }

  @Override public void render() {
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
        if (foodBuffer <= 3.0f) {
          if (catPropertyComponent.atePositiveFood) {
            foodBuffer += delta;
            movementComponent.maxVelocity *= 5;
            movementComponent.acceleration *= 5;
          }
        } else {
          catPropertyComponent.atePositiveFood = false;
          foodBuffer = 0;
        }
        if (movementComponent.positionVec == null) {
          movementComponent.positionVec = new Vector2(movementComponent.directionVec.x, movementComponent.directionVec.y);
        }
        if (movementComponent.oldPositionVec == null) {
          movementComponent.oldPositionVec = new Vector2(movementComponent.positionVec.x, movementComponent.positionVec.y);
        }
        catPropertyComponent.canSeeLaserPointer = false;
        if (distance < 500 && laserPointerComponent.toolState == ToolState.LASER) {
          float slpAngle = (float) Math.atan2(-movementComponent.directionVec.x, movementComponent.directionVec.y);
          float slpCurrentRot = physicsComponent.getRotation();
          if (slpCurrentRot < 0f) {
            slpCurrentRot += (float) (2 * Math.PI);
          }
          if (slpAngle < 0f) {
            slpAngle += (float) (2 * Math.PI);
          }
          float slpSpinningAngle = slpAngle - slpCurrentRot;
          if (slpSpinningAngle < 0) {
            slpSpinningAngle *= -1;
          }
          if (slpSpinningAngle > Math.PI) {
            slpSpinningAngle -= (float) (2 * Math.PI);
            slpSpinningAngle *= -1;
          }
          catPropertyComponent.canSeeLaserPointer = slpSpinningAngle < (float) Math.PI * .25f;
        } else {
          catPropertyComponent.canSeeLaserPointer = false;
        }
        System.out.println(movementComponent.damping * delta);
        if (distance >= 200 && catPropertyComponent.canSeeLaserPointer) {
          movementComponent.velocity += movementComponent.acceleration * delta;
          if (movementComponent.velocity >= movementComponent.maxVelocity) {
            movementComponent.velocity = movementComponent.maxVelocity;
          }
        } else {
          if (distance >= 100 && catPropertyComponent.canSeeLaserPointer) {
            if (movementComponent.velocity < movementComponent.middleVelocity) {
              movementComponent.velocity += movementComponent.acceleration * delta;
              if (movementComponent.velocity >= movementComponent.middleVelocity) {
                movementComponent.velocity = movementComponent.middleVelocity;
              }
            } else {
              if (movementComponent.velocity >= movementComponent.middleVelocity) {
                movementComponent.velocity += movementComponent.damping * delta;
                if (movementComponent.velocity <= movementComponent.middleVelocity) {
                  movementComponent.velocity = movementComponent.middleVelocity;
                }
              }
            }
          } else {
            if (catPropertyComponent.canSeeLaserPointer) {
              if (catPropertyComponent.isInfluenced) {
                movementComponent.velocity += movementComponent.acceleration * 1.5f * delta;
                if (movementComponent.velocity <= movementComponent.minVelocity) {
                  movementComponent.velocity = 0;
                }
              } else {
                movementComponent.velocity += movementComponent.damping * 1.5f * delta;
                if (movementComponent.velocity <= movementComponent.minVelocity) {
                  movementComponent.velocity = 0;
                }
              }
            }
          }
        }
        if (distance <= 70 && distance >= 30) {
          if (catPropertyComponent.getState() == CatStateEnum.IDLE) {
            if (laserPointerComponent != null) {
              if (laserPointerComponent.toolState == ToolState.LASER) {
                catPropertyComponent.timeTillJumpTimer = catPropertyComponent.timeTillJumpTimer + delta;
                if (catPropertyComponent.timeTillJumpTimer >= catPropertyComponent.TIME_TILL_JUMP) {
                  catPropertyComponent.setState(CatStateEnum.JUMP);
                  SoundManager.performAction(CatStateEnum.JUMP);
                  jumpDataComponent.jumpDirection = movementComponent.directionVec.nor();
                }
              }
            }
          }
        } else {
          catPropertyComponent.timeTillJumpTimer = 0.0f;
        }
        float percentNewCatVelo = .95f;
        if (!catPropertyComponent.canSeeLaserPointer) {
          movementComponent.velocity = movementComponent.velocity * (1 - delta);
          percentNewCatVelo = .0001f;
        } else {
          if (distance < 5) {
            percentNewCatVelo = .0001f;
          } else {
            if (movementComponent.velocity <= movementComponent.middleVelocity) {
              percentNewCatVelo = .95f;
            } else {
              if (movementComponent.velocity <= movementComponent.maxVelocity) {
                float phyAngle = (float) Math.atan2(-movementComponent.directionVec.x, movementComponent.directionVec.y);
                float phyCurrentRot = physicsComponent.getRotation();
                if (phyCurrentRot < 0f) {
                  phyCurrentRot += (float) (2 * Math.PI);
                }
                if (phyAngle < 0f) {
                  phyAngle += (float) (2 * Math.PI);
                }
                float phySpinningAngle = phyAngle - phyCurrentRot;
                if (phySpinningAngle < 0) {
                  phySpinningAngle *= -1;
                }
                if (phySpinningAngle > Math.PI) {
                  phySpinningAngle -= (float) (2 * Math.PI);
                  phySpinningAngle *= -1;
                }
                if (phySpinningAngle < .2) {
                  percentNewCatVelo = .95f;
                } else {
                  if (phySpinningAngle < .4f) {
                    percentNewCatVelo = .1f;
                  } else {
                    if (phySpinningAngle < .7f) {
                      percentNewCatVelo = .05f;
                    } else {
                      percentNewCatVelo = .01f;
                      movementComponent.slidingLock = true;
                    }
                  }
                }
              } else {
                if (movementComponent.velocity > movementComponent.maxVelocity) {
                  percentNewCatVelo = .01f;
                  movementComponent.slidingLock = true;
                }
              }
            }
          }
        }
        movementComponent.positionVec.x = movementComponent.directionVec.x * percentNewCatVelo + movementComponent.oldPositionVec.x * (1 - percentNewCatVelo);
        movementComponent.positionVec.y = movementComponent.directionVec.y * percentNewCatVelo + movementComponent.oldPositionVec.y * (1 - percentNewCatVelo);
        movementComponent.oldPositionVec.x = movementComponent.positionVec.x;
        movementComponent.oldPositionVec.y = movementComponent.positionVec.y;
        movementComponent.positionVec.nor();
        if (movementComponent.velocity < 10f) {
          movementComponent.slidingLock = false;
        }
        movementComponent.directionVec = movementComponent.directionVec.nor();
        float angle = (float) Math.atan2(-movementComponent.directionVec.x, movementComponent.directionVec.y);
        if (laserPointerComponent != null) {
          if (!catPropertyComponent.canSeeLaserPointer) {
            if (movementComponent.velocity < 10f) {
              movementComponent.velocity = .0f;
            } else {
              if (movementComponent.slidingLock) {
                movementComponent.velocity = movementComponent.velocity - 5 * delta;
              } else {
                if (!movementComponent.slidingLock) {
                  movementComponent.velocity = movementComponent.velocity - 50 * delta;
                }
              }
            }
          }
          if (!catPropertyComponent.isHidden && catPropertyComponent.canSeeLaserPointer) {
            float currentRot = physicsComponent.getRotation();
            if (currentRot < 0f) {
              currentRot += (float) (2 * Math.PI);
            }
            if (angle < 0f) {
              angle += (float) (2 * Math.PI);
            }
            float spinningAngle = angle - currentRot;
            if (Math.abs(spinningAngle) > Math.PI) {
              spinningAngle = Math.signum(spinningAngle) * (Math.abs(spinningAngle) - (float) (2 * Math.PI));
            }
            if (Math.abs(spinningAngle) > MaxAngularVelocity * delta) {
              spinningAngle = Math.signum(spinningAngle) * MaxAngularVelocity * delta;
            }
            float newRotation = currentRot + spinningAngle;
            newRotation -= Math.signum(newRotation) * (float) (2 * Math.PI) * (int) (newRotation / (2 * Math.PI));
            physicsComponent.setRotation(newRotation);
          }
        }
      } else {
        if (catPropertyComponent.getState() == CatStateEnum.JUMP) {
          movementComponent.velocity = jumpDataComponent.jumpVelocity;
        } else {
          if (catPropertyComponent.getState() == CatStateEnum.FALL || catPropertyComponent.getState() == CatStateEnum.DIE) {
            movementComponent.velocity = 0.0f;
          } else {
            if (catPropertyComponent.getState() == CatStateEnum.HIDDEN) {
              movementComponent.velocity = 0.0f;
            } else {
              if (catPropertyComponent.getState() == CatStateEnum.PLAYS_WITH_WOOL) {
                movementComponent.velocity = 0.0f;
                if (catPropertyComponent.playTimeTimer < catPropertyComponent.PLAYTIME) {
                  catPropertyComponent.playTimeTimer += delta;
                } else {
                  catPropertyComponent.playTimeTimer = 0;
                  catPropertyComponent.setState(CatStateEnum.IDLE);
                }
              } else {
                if (catPropertyComponent.getState() == CatStateEnum.JUMPING_IN_BOX) {
                  catPropertyComponent.jumpInBoxTimer += delta;
                  if (catPropertyComponent.jumpInBoxTimer >= catPropertyComponent.JUMP_IN_BOX) {
                    catPropertyComponent.jumpInBoxTimer = 0;
                  }
                }
              }
            }
          }
        }
      }
      physicsComponent.setVelocityX(movementComponent.positionVec.x * movementComponent.velocity);
      physicsComponent.setVelocityY(movementComponent.positionVec.y * movementComponent.velocity);
    }
  }
}