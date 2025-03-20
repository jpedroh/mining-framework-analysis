<<<<<<< /usr/src/app/output/lusito/gamedevweek/d09b4cd1f960946a232cdd912f0ba8ce6195eb67/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/left.java
package de.hochschuletrier.gdw.ss14.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.game.GameSettings;

public class InputManager {
    private static final Logger logger = LoggerFactory.getLogger(InputManager.class);
    private static InputManager instance;
    
    public static InputManager getInstance () {
        if (instance == null) {
            logger.error("InputManager not initialized!");
        }
        return instance;
    }
    
    private InputManager(){
    }
    
    private InputDevice inputDevice;
    
    public InputDevice getInputDevice() {
        return this.inputDevice;
    }
    
    public void addGameInputAdapter(GameInputAdapter gia) {
        inputDevice.addGameInputAdapter(gia);
    }
    
    public void removeGameInputAdapter(GameInputAdapter gia) {
        inputDevice.addGameInputAdapter(gia);
    }
    
    public void update() {
        inputDevice.update();
    }
    
    public static void init() {
        if (instance != null) {
            logger.info("InputManager already initialized!");
            return;
        }
        
        instance = new InputManager();
        
        switch (GameSettings.getInstance().getInputDevice()) {
            case MOUSE:
                instance.inputDevice = new InputMouse();
                break;
            case KEYBOARD:
                instance.inputDevice = new InputKeyboard();
                break;
            case GAMEPAD:
                instance.inputDevice = new InputGamePad();
                break;
        }
        Main.inputMultiplexer.addProcessor(instance.inputDevice);
    }
    
}
||||||| /usr/src/app/output/lusito/gamedevweek/d09b4cd1f960946a232cdd912f0ba8ce6195eb67/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/base.java
package de.hochschuletrier.gdw.ss14.input;

import de.hochschuletrier.gdw.ss14.sandbox.inputTest.GameInputAdapter;
import de.hochschuletrier.gdw.ss14.sandbox.inputTest.GeneralInputAdapter;

public class InputManager {
    private static InputManager instance;
    
    public static InputManager getInstance () {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
    
    private InputManager(){
    }
    
    private GeneralInputAdapter inputDevice = new GeneralInputAdapter();
    
    public void addGameInputAdapter(GameInputAdapter gia) {
        inputDevice.addGameInputAdapter(gia);
    }
    
    public void removeGameInputAdapter(GameInputAdapter gia) {
        inputDevice.addGameInputAdapter(gia);
    }
    
    public void update() {
        
    }
    
    
}
=======
fatal: path 'gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java' does not exist in 'fa9d2ab60f080ec562eefa3b740f25db672ea45f'
>>>>>>> /usr/src/app/output/lusito/gamedevweek/d09b4cd1f960946a232cdd912f0ba8ce6195eb67/gdx-test/src/main/java/de/hochschuletrier/gdw/ss14/input/InputManager.java/right.java
