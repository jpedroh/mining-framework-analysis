package org.enderstone.server.inventory;
import org.enderstone.server.api.Location;
import org.enderstone.server.api.messages.CachedMessage;
import org.enderstone.server.api.messages.SimpleMessage;
import org.enderstone.server.util.MergedList;

/**
 *
 * @author Fernando
 */
public class CraftingInventory extends DefaultHalfInventory {
  private final Location location;

  private static final CachedMessage TITLE = CachedMessage.wrap(new SimpleMessage("Crafting Table"));

  public CraftingInventory(Location location) {
    super(InventoryType.CRAFTING_TABLE, 10, TITLE, new CraftingInventoryListener(3, 3, 0, 1, DefaultCraftingRecipes.getRecipes()));
    this.location = location;
  }

  @Override protected void close0() {
    for (ItemStack item : this.items) {
      if (item != null) {
        location.getWorld().dropItem(location, item, 10);
      }
    }
  }

  @Override public Inventory openFully(PlayerInventory inventory) {
    Inventory inv = super.openFully(inventory);
    inv.addListener(new InventoryListener() {
      @Override public void onSlotChange(Inventory inv, int slot, ItemStack oldStack, ItemStack newStack) {
      }

      @Override public void onPropertyChange(Inventory inv, short property, short oldValue, short newValue) {
      }

      @Override public void closeInventory(Inventory inv) {
        close();
      }
    });
    return inv;
  }

  @SuppressWarnings(value = { "unchecked" }) @Override protected MergedList<ItemStack> combineItems(PlayerInventory inventory) {
    return new MergedList.Builder<ItemStack>().addList(0, this.items, 0, 10).addList(10, inventory.getRawItems(), 9, 36).build();
  }
}