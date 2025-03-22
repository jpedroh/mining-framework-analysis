package net.tridentsdk.meta;
import net.tridentsdk.meta.nbt.TagCompound;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A class representing an item's extra metadata such as
 * enchantments, attributes, item-specific data such as
 * potion and skull metas, etc...
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe public class ItemMeta {
  /**
     * The NBT data which contains modifications made to
     * this item.
     */
  private final TagCompound nbt = new TagCompound();

  @Nullable public TagCompound toNbt() {
    if (this.nbt.isEmpty()) {
      return null;
    }
    return 
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/meta/ItemMeta.java/left.java
    null
=======
    this.nbt
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/5f8547d9368c73ccbc2ba422b1efa3775af4ac61/src/main/java/net/tridentsdk/meta/ItemMeta.java/right.java
    ;
  }
}