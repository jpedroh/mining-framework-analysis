package de.hochschuletrier.gdw.ws1415.game;
import com.badlogic.ashley.core.ComponentMapper;
import de.hochschuletrier.gdw.commons.gdx.physix.components.*;
import de.hochschuletrier.gdw.ws1415.game.components.*;

public class ComponentMappers {
  public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);

  public static final ComponentMapper<TriggerComponent> trigger = ComponentMapper.getFor(TriggerComponent.class);

  public static final ComponentMapper<PhysixBodyComponent> physixBody = ComponentMapper.getFor(PhysixBodyComponent.class);

  public static final ComponentMapper<PhysixModifierComponent> physixModifier = ComponentMapper.getFor(PhysixModifierComponent.class);

  public static final ComponentMapper<ImpactSoundComponent> impactSound = ComponentMapper.getFor(ImpactSoundComponent.class);

  public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);

  public static final ComponentMapper<MovementComponent, LayerComponent> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/a95278a62906a85fd6483170221f110ee5880d94/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/left.java
  movement = ComponentMapper.getFor(MovementComponent.class)
=======
  layer = ComponentMapper.getFor(LayerComponent.class)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/a95278a62906a85fd6483170221f110ee5880d94/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/right.java
  ;

  public static final ComponentMapper<KillsPlayerOnContactComponent> enemy = ComponentMapper.getFor(KillsPlayerOnContactComponent.class);

  public static final ComponentMapper<BouncingComponent, HealthComponent> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/a95278a62906a85fd6483170221f110ee5880d94/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/left.java
  bouncing = ComponentMapper.getFor(BouncingComponent.class)
=======
  health = ComponentMapper.getFor(HealthComponent.class)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/a95278a62906a85fd6483170221f110ee5880d94/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/right.java
  ;

  public static final ComponentMapper<JumpComponent, BlockComponent> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/a95278a62906a85fd6483170221f110ee5880d94/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/left.java
  jump = ComponentMapper.getFor(JumpComponent.class)
=======
  block = ComponentMapper.getFor(BlockComponent.class)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/a95278a62906a85fd6483170221f110ee5880d94/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/right.java
  ;
}