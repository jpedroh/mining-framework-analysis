package de.hochschuletrier.gdw.ss14.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import de.hochschuletrier.gdw.commons.gdx.assets.AnimationExtended;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.commons.gdx.input.InputInterceptor;
import de.hochschuletrier.gdw.commons.gdx.sound.SoundEmitter;
import de.hochschuletrier.gdw.commons.gdx.state.GameState;
import de.hochschuletrier.gdw.commons.gdx.state.transition.SplitHorizontalTransition;
import de.hochschuletrier.gdw.commons.gdx.utils.DrawUtil;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.sound.LocalMusic;
import de.hochschuletrier.gdw.ss14.ui.MainMenu;
import de.hochschuletrier.gdw.ss14.sound.SoundManager;
import de.hochschuletrier.gdw.ss14.ui.UIActions;

/**
 * Menu state
 *
 * @author Santo Pfingsten
 */
public class MainMenuState extends GameState implements InputProcessor {

    
    private MainMenu mainMenu;
    InputInterceptor inputProcessor;

    public MainMenuState() {
    }

    @Override
    public void init(AssetManagerX assetManager) {
        super.init(assetManager);


        inputProcessor = new InputInterceptor(this) {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Keys.ESCAPE:
                        if (GameStates.GAMEPLAY.isActive()) {
                            GameStates.MAINMENU.activate(new SplitHorizontalTransition(500).reverse(), null);
                        } else {
                            GameStates.GAMEPLAY.activate(new SplitHorizontalTransition(500), null);
                        }
                        return true;
                }
                return isActive && mainProcessor.keyUp(keycode);
            }
        };
        Main.inputMultiplexer.addProcessor(inputProcessor);
    }

    @Override
    public void render() {
        Main.getInstance().screenCamera.bind();
        mainMenu.render();
}

    @Override
    public void update(float delta) {
    	mainMenu.update(delta);
    }

    @Override
    public void onEnter() {
<<<<<<< /usr/src/app/output/lusito/gamedevweek/bb4703104114664a8800674df2159429666c1863/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/states/MainMenuState.java/left.java
        mainMenu = new MainMenu();
        mainMenu.init(assetManager);
||||||| /usr/src/app/output/lusito/gamedevweek/bb4703104114664a8800674df2159429666c1863/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/states/MainMenuState.java/base.java
    		if (this.music.isMusicPlaying()) {
    			this.music.setFade('i', 4000);
    		} else {
    			this.music.play("menu");
    		}
    	
=======
    		if (this.music.isMusicPlaying()) {
    			this.music.setFade('i', 2000);
    		} else {
    			this.music.play("menu");
    		}
    		SoundManager.performAction(UIActions.BELLCLICKED);
    	
>>>>>>> /usr/src/app/output/lusito/gamedevweek/bb4703104114664a8800674df2159429666c1863/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/states/MainMenuState.java/right.java
        inputProcessor.setActive(true);
        inputProcessor.setBlocking(true);
    }

    @Override
    public void onLeave() {
<<<<<<< /usr/src/app/output/lusito/gamedevweek/bb4703104114664a8800674df2159429666c1863/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/states/MainMenuState.java/left.java
    	mainMenu.dispose();
||||||| /usr/src/app/output/lusito/gamedevweek/bb4703104114664a8800674df2159429666c1863/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/states/MainMenuState.java/base.java
    		if (this.music.isMusicPlaying()) {
    		this.music.setFade('o', 4000);
    		}
    		
=======
    		if (this.music.isMusicPlaying()) {
    		this.music.setFade('o', 2000);
    		}
    		
>>>>>>> /usr/src/app/output/lusito/gamedevweek/bb4703104114664a8800674df2159429666c1863/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/states/MainMenuState.java/right.java
        inputProcessor.setActive(false);
        inputProcessor.setBlocking(false);
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
