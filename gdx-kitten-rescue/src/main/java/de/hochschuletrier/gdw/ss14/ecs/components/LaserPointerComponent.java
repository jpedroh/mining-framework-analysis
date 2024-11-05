package de.hochschuletrier.gdw.ss14.ecs.components;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

public class LaserPointerComponent implements Component {
  public static final float WATER_CONSUMPTION_SPEED = 0.1f;

  public static final float WATER_REFILL_SPEED = 0.05f;

  public Vector2 position;

  public final float MAX_SPEED = 150f;

  public float speed;

  public final float ACCELERATION = 100f;

  public float currentWaterlevel;

  public ToolState toolState;

  public boolean waterpistolIsUsed;

  public enum ToolState {
    LASER,
    WATERPISTOL
  }

  public LaserPointerComponent(Vector2 position) {
    toolState = ToolState.LASER;
    waterpistolIsUsed = false;
    currentWaterlevel = 1.0f;
    this.position = position;
  }
}