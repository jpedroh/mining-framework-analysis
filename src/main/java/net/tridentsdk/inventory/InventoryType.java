package net.tridentsdk.inventory;
import javax.annotation.concurrent.Immutable;

/**
 * Represents the types of inventories that are accessible
 * in the Minecraft world.
 *
 * @author TridentSDK
 * @since 0.3-alpha-DP
 */@Immutable public enum InventoryType {
  CONTAINER,
  PLAYER(
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
  "player"
=======
  "Player"
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
  ),
  CHEST,
  CRAFTING_TABLE,
  FURNACE,
  DISPENSER,
  ENCHANTING_TABLE,
  BREWING_STAND,
  VILLAGER,
  BEACON,
  ANVIL,
  HOPPER,
  DROPPER,
  SHULKER_BOX,
  ENTITY_HORSE("EntityHorse"),
  HORSE("EntityHorse")
  ;

  /**
     * The raw name of the inventory as represented by the
     * protocol.
     */
  private final String 
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
  raw
=======
  name
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
  ;

  /**
     * Creates a new inventory type based on the enum name.
     */
  InventoryType() {
    this.
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
    raw
=======
    name
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
     = "minecraft:" + this.name().toLowerCase();
  }

  /**
     * Creates a new inventory type based on the given raw
     * inventory name.
     *
     * @param raw the raw inventory name
     */
  InventoryType(
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
  String raw
=======
  String name
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
  ) {
    this.
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
    raw
=======
    name
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
     = 
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
    raw
=======
    name
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
    ;
  }

  @Override public String toString() {
    return this.
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/left.java
    raw
=======
    name
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/inventory/InventoryType.java/right.java
    ;
  }
}