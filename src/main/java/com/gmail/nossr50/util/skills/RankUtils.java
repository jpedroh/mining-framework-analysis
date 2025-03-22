package com.gmail.nossr50.util.skills;
import com.gmail.nossr50.config.RankConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.SkillUnlockNotificationTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;

public class RankUtils {
  private static HashMap<String, HashMap<Integer, Integer>> subSkillRanks;

  private static int count = 0;

  /**
     *
     * @param plugin plugin instance ref
     * @param mcMMOPlayer target player
     * @param primarySkillType the skill to check
     * @param newLevel the new level of this skill
     */
  public static void executeSkillUnlockNotifications(Plugin plugin, McMMOPlayer mcMMOPlayer, PrimarySkillType primarySkillType, int newLevel) {
    for (SubSkillType subSkillType : mcMMO.p.getSkillTools().getSubSkills(primarySkillType)) {
      int playerRankInSkill = getRank(mcMMOPlayer.getPlayer(), subSkillType);
      HashMap<Integer, Integer> innerMap = subSkillRanks.get(subSkillType.toString());
      if (innerMap == null || innerMap.get(playerRankInSkill) == null) {
        continue;
      }
      if (!Permissions.isSubSkillEnabled(mcMMOPlayer.getPlayer(), subSkillType)) {
        continue;
      }
      if (newLevel == innerMap.get(playerRankInSkill)) {
        SkillUnlockNotificationTask skillUnlockNotificationTask = new SkillUnlockNotificationTask(mcMMOPlayer, subSkillType, newLevel);
        mcMMO.p.getFoliaLib().getImpl().runAtEntityLater(mcMMOPlayer.getPlayer(), skillUnlockNotificationTask, (count * 100L));
        count++;
      }
    }
  }

  /**
     * Reset the interval between skill unlock notifications
     */
  public static void resetUnlockDelayTimer() {
    count = 0;
  }

  private static void addRanks(AbstractSubSkill abstractSubSkill) {
    for (int i = 0; i < abstractSubSkill.getNumRanks(); i++) {
      addRank(abstractSubSkill, abstractSubSkill.getNumRanks() - i);
    }
  }

  private static void addRanks(SubSkillType subSkillType) {
    for (int i = 0; i < subSkillType.getNumRanks(); i++) {
      addRank(subSkillType, subSkillType.getNumRanks() - i);
    }
  }

  /**
     * Populates the ranks for every skill we know about
     */
  public static void populateRanks() {
    for (SubSkillType subSkillType : SubSkillType.values()) {
      addRanks(subSkillType);
    }
    for (AbstractSubSkill abstractSubSkill : InteractionManager.getSubSkillList()) {
      addRanks(abstractSubSkill);
    }
  }

  /**
     * Returns whether the player has unlocked the first rank in target subskill
     * @param player the player
     * @param subSkillType the target subskill
     * @return true if the player has at least one rank in the skill
     */
  public static boolean hasUnlockedSubskill(Player player, SubSkillType subSkillType) {
    int curRank = getRank(player, subSkillType);
    return curRank == -1 || curRank >= 1;
  }

  /**
     * Returns whether the player has unlocked the first rank in target subskill
     * @param player the player
     * @param abstractSubSkill the target subskill
     * @return true if the player has at least one rank in the skill
     */
  public static boolean hasUnlockedSubskill(Player player, AbstractSubSkill abstractSubSkill) {
    int curRank = getRank(player, abstractSubSkill);
    return curRank == -1 || curRank >= 1;
  }

  /**
     * Returns whether the player has reached the specified rank in target subskill
     * @param rank the target rank
     * @param player the player
     * @param subSkillType the target subskill
     * @return true if the player is at least that rank in this subskill
     */
  public static boolean hasReachedRank(int rank, Player player, SubSkillType subSkillType) {
    return getRank(player, subSkillType) >= rank;
  }

  /**
     * Returns whether the player has reached the specified rank in target subskill
     * @param rank the target rank
     * @param player the player
     * @param abstractSubSkill the target subskill
     * @return true if the player is at least that rank in this subskill
     */
  public static boolean hasReachedRank(int rank, Player player, AbstractSubSkill abstractSubSkill) {
    return getRank(player, abstractSubSkill) >= rank;
  }

  /**
     * Gets the current rank of the subskill for the player
     * @param mmoPlayer The player in question
     * @param subSkillType Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without ranks.
     */
  public static int getRank(McMMOPlayer mmoPlayer, SubSkillType subSkillType) {
    return getRank(mmoPlayer.getPlayer(), subSkillType);
  }

  /**
     * Gets the current rank of the subskill for the player
     * @param player The player in question
     * @param subSkillType Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without ranks.
     */
  public static int getRank(Player player, SubSkillType subSkillType) {
    String skillName = subSkillType.toString();
    int numRanks = subSkillType.getNumRanks();
    if (subSkillRanks == null) {
      subSkillRanks = new HashMap<>();
    }
    if (numRanks == 0) {
      return -1;
    }
    if (subSkillRanks.get(skillName) == null && numRanks > 0) {
      addRanks(subSkillType);
    }
    HashMap<Integer, Integer> rankMap = subSkillRanks.get(skillName);
    if (UserManager.getPlayer(player) == null) {
      return 0;
    }
    int currentSkillLevel = UserManager.getPlayer(player).getSkillLevel(subSkillType.getParentSkill());
    for (int i = 0; i < numRanks; i++) {
      int rank = numRanks - i;
      int unlockLevel = getRankUnlockLevel(subSkillType, rank);
      if (rank == 0) {
        return 0;
      }
      if (currentSkillLevel >= unlockLevel) {
        return rank;
      }
    }
    return 0;
  }

  /**
     * Gets the current rank of the subskill for the player
     * @param player The player in question
     * @param abstractSubSkill Target subskill
     * @return The rank the player currently has achieved in this skill. -1 for skills without ranks.
     */
  public static int getRank(Player player, AbstractSubSkill abstractSubSkill) {
    String skillName = abstractSubSkill.getConfigKeyName();
    int numRanks = abstractSubSkill.getNumRanks();
    if (subSkillRanks == null) {
      subSkillRanks = new HashMap<>();
    }
    if (numRanks == 0) {
      return -1;
    }
    if (subSkillRanks.get(skillName) == null && numRanks > 0) {
      addRanks(abstractSubSkill);
    }
    HashMap<Integer, Integer> rankMap = subSkillRanks.get(skillName);
    if (UserManager.getPlayer(player) == null) {
      return 0;
    }
    int currentSkillLevel = UserManager.getPlayer(player).getSkillLevel(abstractSubSkill.getPrimarySkill());
    for (int i = 0; i < numRanks; i++) {
      int rank = numRanks - i;
      int unlockLevel = getRankUnlockLevel(abstractSubSkill, rank);
      if (rank == 0) {
        return 0;
      }
      if (currentSkillLevel >= unlockLevel) {
        return rank;
      }
    }
    return 0;
  }

  /**
     * Adds ranks to our map
     * @param abstractSubSkill The subskill to add ranks for
     * @param rank The rank to add
     */
  private static void addRank(AbstractSubSkill abstractSubSkill, int rank) {
    initMaps(abstractSubSkill.getConfigKeyName());
    HashMap<Integer, Integer> rankMap = subSkillRanks.get(abstractSubSkill.getConfigKeyName());
    rankMap.put(rank, getRankUnlockLevel(abstractSubSkill, rank));
  }

  @Deprecated private static void addRank(SubSkillType subSkillType, int rank) {
    initMaps(subSkillType.toString());
    HashMap<Integer, Integer> rankMap = subSkillRanks.get(subSkillType.toString());
    rankMap.put(rank, getRankUnlockLevel(subSkillType, rank));
  }

  private static void initMaps(String s) {
    if (subSkillRanks == null) {
      subSkillRanks = new HashMap<>();
    }
    subSkillRanks.computeIfAbsent(s, (k) -> new HashMap<>());
  }

  /**
     * Gets the unlock level for a specific rank in a subskill
     * @param subSkillType The target subskill
     * @param rank The target rank
     * @return The level at which this rank unlocks
     */
  public static int getRankUnlockLevel(SubSkillType subSkillType, int rank) {
    return RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, rank);
  }

  public static int getRankUnlockLevel(AbstractSubSkill abstractSubSkill, int rank) {
    return RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, rank);
  }

  /**
     * Get the level at which a skill is unlocked for a player (this is the first rank of a skill)
     * @param subSkillType target subskill
     * @return The unlock requirements for rank 1 in this skill
     */
  public static int getUnlockLevel(SubSkillType subSkillType) {
    return RankConfig.getInstance().getSubSkillUnlockLevel(subSkillType, 1);
  }

  /**
     * Get the level at which a skill is unlocked for a player (this is the first rank of a skill)
     * @param abstractSubSkill target subskill
     * @return The unlock requirements for rank 1 in this skill
     */
  public static int getUnlockLevel(AbstractSubSkill abstractSubSkill) {
    return RankConfig.getInstance().getSubSkillUnlockLevel(abstractSubSkill, 1);
  }

  /**
     * Get the highest rank of a subskill
     * @param subSkillType target subskill
     * @return the last rank of a subskill
     */
  public static int getHighestRank(SubSkillType subSkillType) {
    return subSkillType.getNumRanks();
  }

  public static String getHighestRankStr(SubSkillType subSkillType) {
    return String.valueOf(subSkillType.getNumRanks());
  }

  /**
     * Get the highest rank of a subskill
     * @param abstractSubSkill target subskill
     * @return the last rank of a subskill
     */
  public static int getHighestRank(AbstractSubSkill abstractSubSkill) {
    return abstractSubSkill.getNumRanks();
  }

  public static int getSuperAbilityUnlockRequirement(SuperAbilityType superAbilityType) {
    return getRankUnlockLevel(superAbilityType.getSubSkillTypeDefinition(), 1);
  }

  public static boolean isPlayerMaxRankInSubSkill(Player player, SubSkillType subSkillType) {
    int playerRank = getRank(player, subSkillType);
    int highestRank = getHighestRank(subSkillType);
    return playerRank == highestRank;
  }
}