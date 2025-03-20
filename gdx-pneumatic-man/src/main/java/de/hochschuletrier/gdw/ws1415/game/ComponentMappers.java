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

  public static final ComponentMapper<MovementComponent, TextureComponent> 
<<<<<<< /usr/src/app/output/lusito/gamedevweek/c4df6bbd7e0d3033a0e0e9873a280a515097e953/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/left.java
  movement = ComponentMapper.getFor(MovementComponent.class)
=======
  texture = ComponentMapper.getFor(TextureComponent.class)
>>>>>>> /usr/src/app/output/lusito/gamedevweek/c4df6bbd7e0d3033a0e0e9873a280a515097e953/gdx-pneumatic-man/src/main/java/de/hochschuletrier/gdw/ws1415/game/ComponentMappers.java/right.java
  ;

  public static final ComponentMapper<KillsPlayerOnContactComponent> enemy = ComponentMapper.getFor(KillsPlayerOnContactComponent.class);

  public static final ComponentMapper<BouncingComponent> bouncing = ComponentMapper.getFor(BouncingComponent.class);

  public static final ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);

  public static final ComponentMapper<BlockComponent> block = ComponentMapper.getFor(BlockComponent.class);

  public static final ComponentMapper<LayerComponent> layer = ComponentMapper.getFor(LayerComponent.class);

  public static final ComponentMapper<JumpComponent> jump = ComponentMapper.getFor(JumpComponent.class);
}