package de.hochschuletrier.gdw.ss14.ecs.components;
import de.hochschuletrier.gdw.ss14.states.*;

public class CatPropertyComponent implements Component {
  public int amountLives;

  public boolean isAlive;

  public 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/5798d449c99505d4ff784cb64692af7c89120741/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/components/CatPropertyComponent.java/left.java
  float
=======
  boolean
>>>>>>> /usr/src/app/output/lusito/gamedevweek/5798d449c99505d4ff784cb64692af7c89120741/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/components/CatPropertyComponent.java/right.java
   
<<<<<<< /usr/src/app/output/lusito/gamedevweek/5798d449c99505d4ff784cb64692af7c89120741/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/components/CatPropertyComponent.java/left.java
  jumpBuffer = 0
=======
  canSeeLaserPointer
>>>>>>> /usr/src/app/output/lusito/gamedevweek/5798d449c99505d4ff784cb64692af7c89120741/gdx-kitten-rescue/src/main/java/de/hochschuletrier/gdw/ss14/ecs/components/CatPropertyComponent.java/right.java
  ;

  public CatStateEnum state;

  public CatPropertyComponent() {
    canSeeLaserPointer = true;
    amountLives = 9;
    isAlive = true;
    state = CatStateEnum.IDLE;
  }
}