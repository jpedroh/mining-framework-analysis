package de.hochschuletrier.gdw.ss14.ecs.components;
import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.ss14.physics.ICatStateListener;
import de.hochschuletrier.gdw.ss14.states.CatStateEnum;
import de.hochschuletrier.gdw.ss14.states.GroundTypeState;

public class CatPropertyComponent implements Component {
  public static final int MAX_LIVES = 9;

  public Vector2 lastCheckPoint;


<<<<<<< /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/components/CatPropertyComponent.java/left.java
  public GroundTypeState groundWalking;
=======
  public static final float CATBOX_COOLDOWN = 2.0f;
>>>>>>> /usr/src/app/output/lusito/gamedevweek/83315ff1e11f8b4e9c3f0c24427fefda9b9f087a/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/components/CatPropertyComponent.java/right.java


  public int amountLives;

  public boolean isHidden;

  public boolean canSeeLaserPointer;

  public boolean isAlive, atePositiveFood;

  private CatStateEnum state;

  public boolean isInfluenced = false;

  public float influencedToLaser = 1;

  public final float TIME_TILL_INFLUENCED = 2.5f;

  public float timeTillInfluencedTimer = 0;

  public final float TIME_TILL_JUMP = 1f;

  public float timeTillJumpTimer = 0;

  public final float PLAYTIME = 1.5f;

  public float playTimeTimer = 0;

  public boolean isCatBoxOnCooldown = false;

  public float catBoxCooldownTimer = CATBOX_COOLDOWN;

  public ArrayList<ICatStateListener> StateListener;

  public CatPropertyComponent() {
    lastCheckPoint = new Vector2();
    canSeeLaserPointer = true;
    amountLives = MAX_LIVES;
    isAlive = true;
    atePositiveFood = false;
    state = CatStateEnum.IDLE;
    isHidden = false;
    StateListener = new ArrayList<>();
  }

  public CatStateEnum getState() {
    return state;
  }

  public void setState(CatStateEnum newState) {
    if (newState == state) {
      return;
    }
    StateListener.forEach((l) -> l.stateChanged(state, newState));
    state = newState;
  }
}