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

  public static final ComponentMapper<KillsPlayerOnContactComponent> enemy = ComponentMapper.getFor(KillsPlayerOnContactComponent.class);

  public static final ComponentMapper<AIComponent, HealthComponent> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/d6b031e2569face0946671177f28d977474ef5c2/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/left.java
  AI = ComponentMapper.getFor(AIComponent.class)
=======
  health = ComponentMapper.getFor(HealthComponent.class)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/d6b031e2569face0946671177f28d977474ef5c2/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/right.java
  ;

  public static final ComponentMapper<BlockComponent> block = ComponentMapper.getFor(BlockComponent.class);

  public static final ComponentMapper<LayerComponent> layer = ComponentMapper.getFor(LayerComponent.class);
}