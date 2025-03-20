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

  public static final ComponentMapper<MovementComponent, KillsPlayerOnContactComponent> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/98e1d9fc53d925c97b2c00438dd36aa657c39f70/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/left.java
  movement = ComponentMapper.getFor(MovementComponent.class)
=======
  enemy = ComponentMapper.getFor(KillsPlayerOnContactComponent.class)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/98e1d9fc53d925c97b2c00438dd36aa657c39f70/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/right.java
  ;

  public static final ComponentMapper<BouncingComponent> bouncing = ComponentMapper.getFor(BouncingComponent.class);
}