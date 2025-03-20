package de.hochschuletrier.gdw.ss14.ecs.systems;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import de.hochschuletrier.gdw.ss14.ecs.*;
import de.hochschuletrier.gdw.ss14.ecs.components.*;
import de.hochschuletrier.gdw.ss14.input.*;
import org.slf4j.*;

public class InputSystem extends ECSystem implements GameInputAdapter {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InputSystem.class);

  public InputSystem(EntityManager entityManager) {
    super(entityManager, 1);
    InputManager.getInstance().addGameInputAdapter(this);
  }

  @Override public void update(float delta) {
    Array<Integer> compos = entityManager.getAllEntitiesWithComponents(InputComponent.class, CameraComponent.class, PlayerComponent.class);
    for (Integer integer : compos) {
      InputComponent inputCompo = entityManager.getComponent(integer, InputComponent.class);
      CameraComponent camComp = entityManager.getComponent(integer, CameraComponent.class);
      LaserPointerComponent laser = entityManager.getComponent(integer, LaserPointerComponent.class);
      inputCompo.whereToGo = laser.position;
      Vector3 vec = new Vector3(inputCompo.whereToGo.x, inputCompo.whereToGo.y, 1);
      vec = camComp.smoothCamera.getOrthographicCamera().unproject(vec);
      inputCompo.whereToGo = new Vector2(vec.x, vec.y);
    }
  }

  @Override public void render() {
  }

  @Override public void move(int screenX, int screenY) {
  }

  @Override public void moveUp(float scale) {
  }

  @Override public void moveDown(float scale) {
  }

  @Override public void moveLeft(float scale) {
  }

  @Override public void moveRight(float scale) {
  }

  @Override public void laserButtonPressed() {
    Array<Integer> entities = entityManager.getAllEntitiesWithComponents(CatPropertyComponent.class);
    for (Integer entity : entities) {
      CatPropertyComponent catPropertyComponent = entityManager.getComponent(entity, CatPropertyComponent.class);
      if (catPropertyComponent.canSeeLaserPointer == true) {
        catPropertyComponent.canSeeLaserPointer = false;
      } else {
        catPropertyComponent.canSeeLaserPointer = true;
      }
    }
  }

  @Override public void waterPistolButtonDown() {
  }

  @Override public void waterPistolButtonUp() {
  }

  @Override public void menueButtonPressed() {
  }
}