package fr.moribus.imageonmap.map;
import fr.moribus.imageonmap.ui.MapItemManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ImageMap implements ConfigurationSerializable {
  static public enum Type {
    SINGLE,
    POSTER
    ;


<<<<<<< Unknown file: This is a bug in JDime.
=======
    static public Type fromString(String string) {
      switch (string.toLowerCase()) {
        case "poster":
        case "multi":
        return POSTER;
        case "single":
        return SINGLE;
        default:
        return null;
      }
    }
>>>>>>> /usr/src/app/output/coutume/imageonmap/3ace5f5edf302738f9c7d705ce9c81f718c800af/src/main/java/fr/moribus/imageonmap/map/ImageMap.java/right.java

  }



  static public final int WIDTH = 128;

  static public final int HEIGHT = 128;

  static public final String DEFAULT_NAME = "Map";

  private String id;

  private final UUID userUUID;

  private final Type mapType;

  private String name;

  protected ImageMap(UUID userUUID, Type mapType) {
    this(userUUID, mapType, null, null);
  }

  protected ImageMap(UUID userUUID, Type mapType, String id, String name) {
    this.userUUID = userUUID;
    this.mapType = mapType;
    this.id = id;
    this.name = name;
    if (this.id == null) {
      if (this.name == null) {
        this.name = DEFAULT_NAME;
      }
      this.id = MapManager.getNextAvailableMapID(this.name, userUUID);
    }
  }

  public abstract short[] getMapsIDs();

  public abstract boolean managesMap(short mapID);

  public abstract int getMapCount();

  public boolean managesMap(ItemStack item) {
    if (item == null) {
      return false;
    }
    if (item.getType() != Material.MAP) {
      return false;
    }
    return managesMap(item.getDurability());
  }

  public boolean give(Player player) {
    return MapItemManager.give(player, this);
  }

  static public ImageMap fromConfig(Map<String, Object> map, UUID userUUID) throws InvalidConfigurationException {
    Type mapType;
    try {
      mapType = Type.valueOf((String) map.get("type"));
    } catch (ClassCastException ex) {
      throw new InvalidConfigurationException(ex);
    }
    switch (mapType) {
      case SINGLE:
      return new SingleMap(map, userUUID);
      case POSTER:
      return new PosterMap(map, userUUID);
      default:
      throw new IllegalArgumentException("Unhandled map type given");
    }
  }

  protected ImageMap(Map<String, Object> map, UUID userUUID, Type mapType) throws InvalidConfigurationException {
    this(userUUID, mapType, (String) getNullableFieldValue(map, "id"), (String) getNullableFieldValue(map, "name"));
  }

  protected abstract void postSerialize(Map<String, Object> map);

  @Override public Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", getId());
    map.put("type", mapType.toString());
    map.put("name", getName());
    this.postSerialize(map);
    return map;
  }

  static protected <T extends java.lang.Object> T getFieldValue(Map<String, Object> map, String fieldName) throws InvalidConfigurationException {
    T value = getNullableFieldValue(map, fieldName);
    if (value == null) {
      throw new InvalidConfigurationException("Field value not found for \"" + fieldName + "\"");
    }
    return value;
  }

  static protected <T extends java.lang.Object> T getNullableFieldValue(Map<String, Object> map, String fieldName) throws InvalidConfigurationException {
    try {
      return (T) map.get(fieldName);
    } catch (ClassCastException ex) {
      throw new InvalidConfigurationException("Invalid field \"" + fieldName + "\"", ex);
    }
  }

  public UUID getUserUUID() {
    return userUUID;
  }

  public synchronized String getName() {
    return name;
  }

  public synchronized String getId() {
    return id;
  }

  public synchronized void rename(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public void rename(String name) {
    if (getName().equals(name)) {
      return;
    }
    rename(MapManager.getNextAvailableMapID(name, getUserUUID()), name);
  }
}