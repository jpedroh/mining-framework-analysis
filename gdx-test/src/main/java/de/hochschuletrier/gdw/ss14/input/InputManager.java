package de.hochschuletrier.gdw.ss14.input;

<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/left.java
import org.slf4j.Logger;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/left.java
import org.slf4j.LoggerFactory;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/left.java
import de.hochschuletrier.gdw.ss14.game.GameSettings;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/left.java
import de.hochschuletrier.gdw.ss14.sandbox.inputTest.GameInputAdapter;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/left.java
import de.hochschuletrier.gdw.ss14.sandbox.inputTest.GeneralInputAdapter;

=======
>>>>>>> Unknown file: This is a bug in JDime.

public class InputManager {
  private static final Logger logger = LoggerFactory.getLogger(InputManager.class);

  private static InputManager instance;

  public static InputManager getInstance() {
    if (instance == null) {
      logger.error("InputManager not initialized!");
    }
    return instance;
  }

  private InputManager() {
    switch (GameSettings.getInstance().getInputDevice()) {
      case MOUSE:
      break;
      case KEYBOARD:
      break;
      case GAMEPAD:
      break;
    }
  }

  private InputDevice inputDevice = new InputMouse();

  public void addGameInputAdapter(GameInputAdapter gia) {
    inputDevice.addGameInputAdapter(gia);
  }

  public void removeGameInputAdapter(GameInputAdapter gia) {
    inputDevice.addGameInputAdapter(gia);
  }

  public void update() {
  }

  public static void init() {
    if (instance != null) {
      logger.info("InputManager already initialized!");
      return;
    }
    instance = new InputManager();
  }
}