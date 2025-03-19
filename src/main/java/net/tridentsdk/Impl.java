package net.tridentsdk;
import net.tridentsdk.base.Substance;
import net.tridentsdk.command.logger.LogHandler;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.config.Config;
import net.tridentsdk.doc.Internal;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.ItemMeta;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.tablist.TabList;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

/**
 * This provides the accessors to implementations provided
 * by the server in order for plugins to use API methods
 * implemented by the server.
 *
 * @author TridentSDK
 * @since 0.4-alpha
 */
@Internal @ThreadSafe public final class Impl {
  /**
     * The latch that is used to guard the thread-safety of
     * the implementation provider
     */
  private static final CountDownLatch IMPL_LATCH = new CountDownLatch(1);

  /**
     * The instance of the implementation provider
     */
  @Policy(value = "Sync writes") private static ImplementationProvider impl;

  /**
     * The lock used for writing the impl field
     */
  private static final Object lock = new Object();

  private Impl() {
  }

  /**
     * Sets the implementation provider which allows plugins
     * to access the parts of the API that implemented by
     * the server.
     *
     * @param i the implementation provider instance
     */
  public static void setImpl(ImplementationProvider i) {
    synchronized (lock) {
      if (Impl.impl == null) {
        Impl.impl = i;
        IMPL_LATCH.countDown();
      }
    }
  }

  @Nonnull public static ImplementationProvider get() {
    try {
      IMPL_LATCH.await();
      return impl;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Internal public interface ImplementationProvider {
    Server getServer();

    Config newCfg(Path p);

    Logger newLogger(String s);

    void attachHandler(Logger logger, LogHandler handler);

    boolean removeHandler(Logger logger, LogHandler handler);

    TabList getGlobalTabList();

    TabList newTabList();

    BossBar newBossBar();

    Inventory newInventory(InventoryType type, int slots);

    Item newItem(Substance substance, int count, byte damage, ItemMeta meta);
  }
}