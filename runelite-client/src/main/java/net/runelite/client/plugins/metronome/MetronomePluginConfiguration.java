package net.runelite.client.plugins.metronome;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.api.SoundEffectVolume;

@ConfigGroup(value = "metronome") public interface MetronomePluginConfiguration extends Config {
  int VOLUME_MAX = SoundEffectVolume.HIGH;

  @ConfigItem(keyName = "tickCount", name = "Tick count", description = "Configures the tick on which a sound will be played.") default int tickCount() {
    return 1;
  }

  @Range(max = VOLUME_MAX) @ConfigItem(keyName = "tickVolume", name = "Tick volume", description = "Configures the volume of the tick sound. A value of 0 will disable tick sounds.") default int tickVolume() {
    return SoundEffectVolume.MEDIUM_HIGH;
  }

  @ConfigItem(keyName = "enableVisualCue", name = "Enable visual cue overlay", description = "Toggles whether to display visual cue around your player model", position = 4) default boolean enableVisualCue() {
    return false;
  }

  @Range(max = VOLUME_MAX) @ConfigItem(keyName = "tockVolume", name = "Tock volume", description = "Configures the volume of the tock sound. A value of 0 will disable tock sounds.") default int tockVolume() {
    return SoundEffectVolume.MUTED;
  }
}