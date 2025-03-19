package net.tridentsdk.inventory;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class InventoryTypeTest {
  @Test public void testTypes() {
    String types = "minecraft:container\n" + "minecraft:chest\n" + "minecraft:crafting_table\n" + "minecraft:furnace\n" + "minecraft:dispenser\n" + "minecraft:enchanting_table\n" + "minecraft:brewing_stand\n" + "minecraft:villager\n" + "minecraft:beacon\n" + "minecraft:anvil\n" + "minecraft:hopper\n" + "minecraft:dropper\n" + "minecraft:shulker_box\n" + "EntityHorse\n" + "player";
    for (InventoryType type : InventoryType.values()) {
      assertEquals(types.split("\n")[type.ordinal()], type.toString());
    }
  }
}