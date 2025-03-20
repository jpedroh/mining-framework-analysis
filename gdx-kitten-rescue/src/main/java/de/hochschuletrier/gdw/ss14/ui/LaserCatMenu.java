package de.hochschuletrier.gdw.ss14.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Table.Debug;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.hochschuletrier.gdw.commons.gdx.assets.AssetManagerX;
import de.hochschuletrier.gdw.ss14.Main;
import de.hochschuletrier.gdw.ss14.sound.SoundManager;

public abstract class LaserCatMenu {
  private static Image menuCatImage, titleTextImage;

  private ShapeRenderer shapeRenderer;

  protected static SoundListener soundListener;

  protected static Table widgetFrame;

  protected static Table table;

  protected static Skin catSkin;

  protected static Stage stage;

  protected static float heightOfWidgetFrame;

  protected static float widthOfWidgetFrame;

  protected UIButton button[];

  protected Label label[];

  protected String name[];

  protected int numberOfButtons;

  public void init(AssetManagerX assetManager) {
    heightOfWidgetFrame = 0.25f;
    widthOfWidgetFrame = 0.65f;
    stage = new Stage();
    table = new Table();
    stage.addActor(table);
    table.setFillParent(true);
    catSkin = new Skin(Gdx.files.internal("data/skins/MainMenuSkin.json"));
    Main.inputMultiplexer.addProcessor(stage);
    table.setBackground(catSkin.getDrawable("main-menu-background"));
    titleTextImage = new Image(catSkin.getDrawable("game-title"));
    table.add(titleTextImage).top().size(Value.percentWidth(0.8f, table), Value.percentHeight(0.25f, table)).expandX();
    table.row();
    widgetFrame = new Table();
    table.add(widgetFrame).bottom().size(Value.percentWidth(widthOfWidgetFrame, table), Value.percentHeight(heightOfWidgetFrame, table));
    table.row();
    menuCatImage = new Image(catSkin.getDrawable("main-menu-cat"));
    table.add(menuCatImage).bottom().expandY();
    shapeRenderer = new ShapeRenderer();
    table.debug(Debug.all);
    LaserCatMenu.soundListener = new SoundListener();
  }

  public void dispose() {
    stage.dispose();
    shapeRenderer.dispose();
  }

  public void render() {
    stage.draw();
  }

  public void update(float delta) {
    stage.act(Gdx.graphics.getDeltaTime());
  }

  protected class SoundListener extends ClickListener {
    public void clicked(InputEvent event, float x, float y) {
      if (event.getListenerActor().getName().equals("bell")) {
        SoundManager.performAction(UIActions.BELLCLICKED);
      } else {
        SoundManager.performAction(UIActions.BUTTONCLICKED);
      }
    }

    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
      if (this.isPressed()) {
        return;
      }
      if (event.getListenerActor().getName().equals("bell")) {
        SoundManager.performAction(UIActions.BELLOVER);
        animateRingingBell(event.getListenerActor());
      } else {
        SoundManager.performAction(UIActions.BUTTONOVER);
      }
    }
  }

  private void animateRingingBell(Actor b) {
    UIButton button = (UIButton) b;
  }
}