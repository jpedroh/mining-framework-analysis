package com.gmail.nossr50.skills.woodcutting;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Woodcutting {
  public static int leafBlowerUnlockLevel = AdvancedConfig.getInstance().getLeafBlowUnlockLevel();

  public static int treeFellerThreshold = Config.getInstance().getTreeFellerThreshold();

  protected static boolean treeFellerReachedThreshold = false;

  protected enum ExperienceGainMethod {
    DEFAULT,
    TREE_FELLER
  }

  private Woodcutting() {
  }

  /**
     * Retrieves the experience reward from a log
     *
     * @param blockState Log being broken
     * @param experienceGainMethod How the log is being broken
     * @return Amount of experience
     */
  protected static int getExperienceFromLog(BlockState blockState, ExperienceGainMethod experienceGainMethod) {
    if (mcMMO.getModManager().isCustomLog(blockState)) {
      return mcMMO.getModManager().getBlock(blockState).getXpGain();
    }
    return ExperienceConfig.getInstance().getXp(SkillType.WOODCUTTING, blockState.getBlockData());
  }

  /**
     * Checks for double drops
     *
     * @param blockState Block being broken
     */
  protected static void checkForDoubleDrop(BlockState blockState) {
    if (mcMMO.getModManager().isCustomLog(blockState) && mcMMO.getModManager().getBlock(blockState).isDoubleDropEnabled()) {
      Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
    } else {
      if (Config.getInstance().getWoodcuttingDoubleDropsEnabled(blockState.
<<<<<<< /usr/src/app/output/mcmmo-dev/mcmmo/d4bd886522cc786603bfbfeca927401c27335458/src/main/java/com/gmail/nossr50/skills/woodcutting/Woodcutting.java/left.java
      getBlockData()
=======
      getType()
>>>>>>> /usr/src/app/output/mcmmo-dev/mcmmo/d4bd886522cc786603bfbfeca927401c27335458/src/main/java/com/gmail/nossr50/skills/woodcutting/Woodcutting.java/right.java
      )) {
        Misc.dropItems(Misc.getBlockCenter(blockState), blockState.getBlock().getDrops());
      }
    }
  }

  /**
     * The x/y differences to the blocks in a flat cylinder around the center
     * block, which is excluded.
     */
  private static final int[][] directions = { new int[] { -2, -1 }, new int[] { -2, 0 }, new int[] { -2, 1 }, new int[] { -1, -2 }, new int[] { -1, -1 }, new int[] { -1, 0 }, new int[] { -1, 1 }, new int[] { -1, 2 }, new int[] { 0, -2 }, new int[] { 0, -1 }, new int[] { 0, 1 }, new int[] { 0, 2 }, new int[] { 1, -2 }, new int[] { 1, -1 }, new int[] { 1, 0 }, new int[] { 1, 1 }, new int[] { 1, 2 }, new int[] { 2, -1 }, new int[] { 2, 0 }, new int[] { 2, 1 } };

  /**
     * Processes Tree Feller in a recursive manner
     *
     * @param blockState Block being checked
     * @param treeFellerBlocks List of blocks to be removed
     */
  protected static void processTree(BlockState blockState, Set<BlockState> treeFellerBlocks) {
    List<BlockState> futureCenterBlocks = new ArrayList<BlockState>();
    if (handleBlock(blockState.getBlock().getRelative(BlockFace.UP).getState(), futureCenterBlocks, treeFellerBlocks)) {
      for (int[] dir : directions) {
        handleBlock(blockState.getBlock().getRelative(dir[0], 0, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);
        if (treeFellerReachedThreshold) {
          return;
        }
      }
    } else {
      handleBlock(blockState.getBlock().getRelative(BlockFace.DOWN).getState(), futureCenterBlocks, treeFellerBlocks);
      for (int y = -1; y <= 1; y++) {
        for (int[] dir : directions) {
          handleBlock(blockState.getBlock().getRelative(dir[0], y, dir[1]).getState(), futureCenterBlocks, treeFellerBlocks);
          if (treeFellerReachedThreshold) {
            return;
          }
        }
      }
    }
    for (BlockState futureCenterBlock : futureCenterBlocks) {
      if (treeFellerReachedThreshold) {
        return;
      }
      processTree(futureCenterBlock, treeFellerBlocks);
    }
  }

  /**
     * Handles the durability loss
     *
     * @param treeFellerBlocks List of blocks to be removed
     * @param inHand tool being used
     * @return True if the tool can sustain the durability loss
     */
  protected static boolean handleDurabilityLoss(Set<BlockState> treeFellerBlocks, ItemStack inHand) {
    short durabilityLoss = 0;
    Material type = inHand.getType();
    for (BlockState blockState : treeFellerBlocks) {
      if (BlockUtils.isLog(blockState)) {
        durabilityLoss += Config.getInstance().getAbilityToolDamage();
      }
    }
    SkillUtils.handleDurabilityChange(inHand, durabilityLoss);
    return (inHand.getDurability() < (mcMMO.getRepairableManager().isRepairable(type) ? mcMMO.getRepairableManager().getRepairable(type).getMaximumDurability() : type.getMaxDurability()));
  }

  /**
     * Handle a block addition to the list of blocks to be removed and to the
     * list of blocks used for future recursive calls of
     * 'processTree()'
     *
     * @param blockState Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call
     *     'processTree()'
     * @param treeFellerBlocks List of blocks to be removed
     * @return true if and only if the given blockState was a Log not already
     *     in treeFellerBlocks.
     */
  private static boolean handleBlock(BlockState blockState, List<BlockState> futureCenterBlocks, Set<BlockState> treeFellerBlocks) {
    if (treeFellerBlocks.contains(blockState) || mcMMO.getPlaceStore().isTrue(blockState)) {
      return false;
    }
    if (treeFellerBlocks.size() > treeFellerThreshold) {
      treeFellerReachedThreshold = true;
    }
    if (BlockUtils.isLog(blockState)) {
      treeFellerBlocks.add(blockState);
      futureCenterBlocks.add(blockState);
      return true;
    } else {
      if (BlockUtils.isLeaves(blockState)) {
        treeFellerBlocks.add(blockState);
        return false;
      }
    }
    return false;
  }
}