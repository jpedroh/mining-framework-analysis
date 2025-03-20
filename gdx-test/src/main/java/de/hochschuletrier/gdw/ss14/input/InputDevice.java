package de.hochschuletrier.gdw.ss14.input;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class InputDevice {
  public static enum DeviceType {
    MOUSE("mouse"),
    KEYBOARD("keyboard"),
    GAMEPAD("gamepad")
    ;

    private String deviceName;

    private DeviceType(String deviceName) {
      this.deviceName = deviceName;
    }

    @Override public String toString() {
      return deviceName;
    }
  }



  private protected 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputDevice.java/left.java
  HashMap
=======
  LinkedList
>>>>>>> /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputDevice.java/right.java
  <String, GameInputAdapter, InputAction> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputDevice.java/left.java
  buttonMap = new HashMap<>()
=======
  listener = new LinkedList<>()
>>>>>>> /usr/src/app/output/lusito/gamedevweek/d60d1e1f9f4b6bbd93fac52529eb2210b68b8634/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputDevice.java/right.java
  ;

  /**
     * 
     * @param gia @ GameInputAdapter to the Listener
     */
  public void addGameInputAdapter(GameInputAdapter gia) {
    listener.add(gia);
  }

  /**
     * 
     * @param gia remove GameInputAdapter gia from the listener
     */
  public void removeGameInputAdapter(GameInputAdapter gia) {
    listener.remove(gia);
  }

  /**
	 *  Laser on / off
	 */
  protected void fireLaserButtonPressed() {
    for (GameInputAdapter inp : listener) {
      inp.laserButtonPressed();
    }
  }

  /**
	 * water pistol on
	 */
  protected void fireWaterPistolButtonDown() {
    for (GameInputAdapter inp : listener) {
      inp.waterPistolButtonDown();
    }
  }

  /**
	 * water pistol off
	 */
  protected void fireWaterPistolButtonUp() {
    for (GameInputAdapter inp : listener) {
      inp.waterPistolButtonUp();
    }
  }

  /**
	 * go to the menu or back
	 */
  protected void fireMenuButtonPressed() {
    for (GameInputAdapter inp : listener) {
      inp.menueButtonPressed();
    }
  }
}