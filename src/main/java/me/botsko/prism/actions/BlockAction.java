package me.botsko.prism.actions;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.appliers.ChangeResult;
import me.botsko.prism.appliers.ChangeResultType;
import me.botsko.prism.appliers.PrismProcessType;
import me.botsko.prism.commandlibs.Flag;
import me.botsko.prism.events.BlockStateChange;
import me.botsko.prism.utils.EntityUtils;
import me.botsko.prism.utils.MaterialTag;
import me.botsko.prism.utils.TypeUtils;
import me.botsko.prism.utils.block.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Nameable;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.CHEST;
import static org.bukkit.Material.COMMAND_BLOCK;
import static org.bukkit.Material.FARMLAND;
import static org.bukkit.Material.FIRE;
import static org.bukkit.Material.JUKEBOX;
import static org.bukkit.Material.NETHER_PORTAL;
import static org.bukkit.Material.OBSIDIAN;
import static org.bukkit.Material.PLAYER_HEAD;
import static org.bukkit.Material.PLAYER_WALL_HEAD;
import static org.bukkit.Material.SPAWNER;
import static org.bukkit.Material.TRAPPED_CHEST;
import static org.bukkit.Material.WATER;
import org.bukkit.ChatColor;

public class BlockAction extends GenericAction {
  private BlockActionData actionData;

  public void setBlock(Block block) {
    if (block != null) {
      setBlock(block.getState());
    }
  }

  public void setBlock(BlockState state) {
    if (state != null) {
      setMaterial(state.getType());
      setBlockData(state.getBlockData());
      createActionData(state);
      setLoc(state.getLocation());
    }
  }

  private void createActionData(BlockState state) {
    switch (state.getType()) {
      case SPAWNER:
      final SpawnerActionData spawnerActionData = new SpawnerActionData();
      final CreatureSpawner spawner = (CreatureSpawner) state;
      spawnerActionData.entityType = spawner.getSpawnedType().name().toLowerCase();
      spawnerActionData.delay = spawner.getDelay();
      actionData = spawnerActionData;
      break;
      case PLAYER_WALL_HEAD:
      case PLAYER_HEAD:
      SkullActionData headActionData = new SkullActionData();
      if (state instanceof Skull) {
        Skull skull = ((Skull) state);
        if (skull.getOwningPlayer() != null) {
          headActionData.owner = skull.getOwningPlayer().getUniqueId().toString();
        }
      }
      setBlockRotation(state, headActionData);
      actionData = headActionData;
      break;
      case SKELETON_SKULL:
      case SKELETON_WALL_SKULL:
      case WITHER_SKELETON_SKULL:
      case WITHER_SKELETON_WALL_SKULL:
      SkullActionData skullActionData = new SkullActionData();
      setBlockRotation(state, skullActionData);
      actionData = skullActionData;
      break;
      case COMMAND_BLOCK:
      final CommandBlock cmdBlock = (CommandBlock) state;
      final CommandActionData commandActionData = new CommandActionData();
      commandActionData.command = cmdBlock.getCommand();
      actionData = commandActionData;
      break;
      default:
      if (Tag.SIGNS.isTagged(state.getType())) {
        final SignActionData signActionData = new SignActionData();
        final Sign sign = (Sign) state;
        signActionData.lines = sign.getLines();
        actionData = signActionData;
      }
      break;
    }
    if (state instanceof Nameable && ((Nameable) state).getCustomName() != null) {
      if (actionData == null) {
        actionData = new BlockActionData();
      }
      actionData.customName = ((Nameable) state).getCustomName();
    }
  }

  private void setBlockRotation(BlockState block, SkullActionData skullActionData) {
    if (block.getBlockData() instanceof Rotatable) {
      final Rotatable r = (Rotatable) block.getBlockData();
      skullActionData.rotation = r.getRotation().toString();
    } else {
      final Directional d = (Directional) block.getBlockData();
      skullActionData.rotation = d.getFacing().name().toLowerCase();
    }
  }

  @Override public boolean hasExtraData() {
    return actionData != null;
  }

  @Override public String serialize() {
    return gson().toJson(actionData);
  }

  @Override public void deserialize(String data) {
    if (data != null && data.startsWith("{")) {
      if (getMaterial() == PLAYER_HEAD || getMaterial() == PLAYER_WALL_HEAD) {
        actionData = gson().fromJson(data, SkullActionData.class);
      } else {
        if (getMaterial() == SPAWNER) {
          actionData = gson().fromJson(data, SpawnerActionData.class);
        } else {
          if (Tag.SIGNS.isTagged(getMaterial())) {
            actionData = gson().fromJson(data, SignActionData.class);
          } else {
            if (getMaterial() == COMMAND_BLOCK) {
              actionData = new CommandActionData();
              ((CommandActionData) actionData).command = data;
            } else {
              actionData = gson().fromJson(data, BlockActionData.class);
            }
          }
        }
      }
    }
  }

  private BlockActionData getActionData() {
    return actionData;
  }

  @Override public String getNiceName() {
    String name = "";
    BlockActionData blockActionData = getActionData();
    if (blockActionData != null) {
      if (blockActionData instanceof SkullActionData) {
        final SkullActionData ad = (SkullActionData) blockActionData;
        name += ad.skullType + " ";
      } else {
        if (blockActionData instanceof SpawnerActionData) {
          final SpawnerActionData ad = (SpawnerActionData) blockActionData;
          name += ad.entityType + " ";
        }
      }
    }
    name += Prism.getItems().getAlias(getMaterial(), getBlockData());
    if (blockActionData == null) {
      return name;
    }
    if (blockActionData instanceof SignActionData) {
      final SignActionData ad = (SignActionData) blockActionData;
      if (ad.lines != null && ad.lines.length > 0) {
        name += " (" + TypeUtils.join(ad.lines, ", ") + ")";
      }
    } else {
      if (blockActionData instanceof CommandActionData) {
        final CommandActionData ad = (CommandActionData) blockActionData;
        name += " (" + ad.command + ")";
      }
    }
    if (blockActionData.customName != null) {
      name += ChatColor.RESET + " (" + blockActionData.customName + ChatColor.RESET + ") ";
    }
    if (getActionType().getName().equals("crop-trample") && getMaterial() == AIR) {
      return "empty soil";
    }
    return name;
  }

  @Override public String getCustomDesc() {
    if (getActionType().getName().equals("water-bucket") && getBlockData() instanceof Waterlogged) {
      return "waterlogged";
    }
    return null;
  }

  @Override public ChangeResult applyRollback(Player player, QueryParameters parameters, boolean isPreview) {
    final Block block = getWorld().getBlockAt(getLoc());
    if (getActionType().doesCreateBlock()) {
      return removeBlock(player, parameters, isPreview, block);
    } else {
      return placeBlock(player, parameters, isPreview, block, false);
    }
  }

  @Override public ChangeResult applyRestore(Player player, QueryParameters parameters, boolean isPreview) {
    final Block block = getWorld().getBlockAt(getLoc());
    if (getActionType().doesCreateBlock()) {
      return placeBlock(player, parameters, isPreview, block, false);
    } else {
      return removeBlock(player, parameters, isPreview, block);
    }
  }

  @Override public ChangeResult applyUndo(Player player, QueryParameters parameters, boolean isPreview) {
    final Block block = getWorld().getBlockAt(getLoc());
    return placeBlock(player, parameters, isPreview, block, false);
  }

  @Override public ChangeResult applyDeferred(Player player, QueryParameters parameters, boolean isPreview) {
    final Block block = getWorld().getBlockAt(getLoc());
    return placeBlock(player, parameters, isPreview, block, true);
  }

  ChangeResult placeBlock(Player player, QueryParameters parameters, boolean isPreview, Block block, boolean isDeferred) {
    BlockStateChange stateChange;
    final boolean cancelIfBadPlace = !getActionType().requiresHandler(BlockChangeAction.class) && !getActionType().requiresHandler(PrismRollbackAction.class) && !parameters.hasFlag(Flag.OVERWRITE);
    if (cancelIfBadPlace && !Utilities.isAcceptableForBlockPlace(block.getType())) {
      Prism.debug("Block skipped due to being unacceptable for block place.: " + block.getType().name());
      return new ChangeResult(ChangeResultType.SKIPPED, null);
    }
    if (Prism.getIllegalBlocks().contains(getMaterial()) && !parameters.getProcessType().equals(PrismProcessType.UNDO)) {
      Prism.debug("Block skipped because it\'s not allowed to be placed unless its an UNDO." + block.getType().name());
      return new ChangeResult(ChangeResultType.SKIPPED, null);
    }
    final BlockState originalBlock = block.getState();
    if (!isPreview) {
      return handleApply(block, originalBlock, parameters, cancelIfBadPlace);
    } else {
      stateChange = new BlockStateChange(originalBlock, originalBlock);
      EntityUtils.sendBlockChange(player, block.getLocation(), getBlockData());
      for (final CommandSender sharedPlayer : parameters.getSharedPlayers()) {
        if (sharedPlayer instanceof Player) {
          EntityUtils.sendBlockChange((Player) sharedPlayer, block.getLocation(), getBlockData());
        }
      }
      return new ChangeResult(ChangeResultType.APPLIED, stateChange);
    }
  }

  private @NotNull ChangeResult handleApply(final Block block, final BlockState originalBlock, final QueryParameters parameters, final boolean cancelIfBadPlace) {
    BlockState state = block.getState();
    switch (getMaterial()) {
      case LILY_PAD:
      final Block below = block.getRelative(BlockFace.DOWN);
      if (below.getType().equals(WATER) || below.getType().equals(AIR)) {
        below.setType(WATER);
      } else {
        return new ChangeResult(ChangeResultType.SKIPPED, null);
      }
      break;
      case NETHER_PORTAL:
      final Block obsidian = Utilities.getFirstBlockOfMaterialBelow(OBSIDIAN, block.getLocation());
      if (obsidian != null) {
        final Block above = obsidian.getRelative(BlockFace.UP);
        if (!(above.getType() == NETHER_PORTAL)) {
          above.setType(FIRE);
          return new ChangeResult(ChangeResultType.APPLIED, null);
        }
      }
      break;
      case JUKEBOX:
      setBlockData(Bukkit.createBlockData(JUKEBOX));
      break;
      default:
      break;
    }
    state.setType(getMaterial());
    state.setBlockData(getBlockData());
    state.update(true);
    BlockState newState = block.getState();
    BlockActionData blockActionData = getActionData();
    if ((getMaterial() == PLAYER_HEAD || getMaterial() == PLAYER_WALL_HEAD) && blockActionData instanceof SkullActionData) {
      return handleSkulls(block, blockActionData, originalBlock);
    }
    if (getMaterial() == SPAWNER && blockActionData instanceof SpawnerActionData) {
      final SpawnerActionData s = (SpawnerActionData) blockActionData;
      ((CreatureSpawner) newState).setDelay(s.getDelay());
      ((CreatureSpawner) newState).setSpawnedType(s.getEntityType());
    }
    if (getMaterial() == COMMAND_BLOCK && blockActionData instanceof CommandActionData) {
      final CommandActionData c = (CommandActionData) blockActionData;
      ((CommandBlock) newState).setCommand(c.command);
    }
    if (newState instanceof Nameable && actionData.customName != null) {
      ((Nameable) newState).setCustomName(actionData.customName);
    }
    if (parameters.getProcessType() == PrismProcessType.ROLLBACK && Tag.SIGNS.isTagged(getMaterial()) && blockActionData instanceof SignActionData) {
      final SignActionData s = (SignActionData) blockActionData;
      if (newState instanceof Sign) {
        if (s.lines != null) {
          for (int i = 0; i < s.lines.length; ++i) {
            ((Sign) newState).setLine(i, s.lines[i]);
          }
        }
      }
    }
    BlockState sibling = null;
    if (Utilities.materialRequiresSoil(getMaterial())) {
      sibling = block.getRelative(BlockFace.DOWN).getState();
      if (cancelIfBadPlace && !MaterialTag.SOIL_CANDIDATES.isTagged(sibling.getType())) {
        Prism.debug(parameters.getProcessType().name() + " skipped due to lack of soil for " + getMaterial().name());
        return new ChangeResult(ChangeResultType.SKIPPED, null);
      }
      sibling.setType(FARMLAND);
    }
    if (newState.getType() != CHEST && newState.getType() != TRAPPED_CHEST) {
      final Block s = Utilities.getSiblingForDoubleLengthBlock(state);
      if (s != null) {
        sibling = s.getState();
        if (cancelIfBadPlace && !Utilities.isAcceptableForBlockPlace(sibling.getType())) {
          Prism.debug(parameters.getProcessType().name() + " skipped due to lack of wrong sibling type for " + getMaterial().name());
          return new ChangeResult(ChangeResultType.SKIPPED, null);
        }
        sibling.setType(block.getType());
        BlockData siblingData = getBlockData().clone();
        if (siblingData instanceof Bed) {
          ((Bed) siblingData).setPart(Part.HEAD);
        } else {
          if (siblingData instanceof Bisected) {
            ((Bisected) siblingData).setHalf(Half.TOP);
          }
        }
        sibling.setBlockData(siblingData);
      }
    }
    boolean physics = !parameters.hasFlag(Flag.NO_PHYS);
    newState.update(true, physics);
    if (sibling != null) {
      sibling.update(true, physics);
    }
    return new ChangeResult(ChangeResultType.APPLIED, new BlockStateChange(originalBlock, state));
  }

  private @NotNull ChangeResult handleSkulls(final Block block, BlockActionData blockActionData, final BlockState originalBlock) {
    block.setType(getMaterial());
    BlockState state = block.getState();
    final SkullActionData s = (SkullActionData) blockActionData;
    if (state.getBlockData() instanceof Rotatable) {
      final Rotatable r = (Rotatable) state.getBlockData();
      r.setRotation(s.getRotation());
      state.setBlockData(r);
    } else {
      final Directional d = (Directional) state.getBlockData();
      d.setFacing(s.getRotation());
      state.setBlockData(d);
    }
    state = block.getState();
    if (!s.owner.isEmpty()) {
      final Skull skull = (Skull) state;
      skull.setOwningPlayer(Bukkit.getOfflinePlayer(EntityUtils.uuidOf((s.owner))));
    }
    BlockStateChange stateChange = new BlockStateChange(originalBlock, state);
    return new ChangeResult(ChangeResultType.APPLIED, stateChange);
  }

  private ChangeResult removeBlock(Player player, QueryParameters parameters, boolean isPreview, Block block) {
    BlockStateChange stateChange;
    if (!block.getType().equals(AIR)) {
      if (!Utilities.isAcceptableForBlockPlace(block.getType()) && !Utilities.areBlockIdsSameCoreItem(block.getType(), getMaterial()) && !parameters.hasFlag(Flag.OVERWRITE)) {
        return new ChangeResult(ChangeResultType.SKIPPED, null);
      }
      final BlockState originalBlock = block.getState();
      if (!isPreview) {
        block.setType(AIR);
        final BlockState newBlock = block.getState();
        stateChange = new BlockStateChange(originalBlock, newBlock);
      } else {
        stateChange = new BlockStateChange(originalBlock, originalBlock);
        EntityUtils.sendBlockChange(player, block.getLocation(), Bukkit.createBlockData(AIR));
        for (final CommandSender sharedPlayer : parameters.getSharedPlayers()) {
          if (sharedPlayer instanceof Player) {
            EntityUtils.sendBlockChange((Player) sharedPlayer, block.getLocation(), Bukkit.createBlockData(AIR));
          }
        }
      }
      return new ChangeResult(ChangeResultType.APPLIED, stateChange);
    }
    return new ChangeResult(ChangeResultType.SKIPPED, null);
  }

  static class BlockActionData {
    String customName;
  }

  public static class CommandActionData extends BlockActionData {
    String command;
  }

  public static class SpawnerActionData extends BlockActionData {
    String entityType;

    int delay;

    EntityType getEntityType() {
      return EntityType.valueOf(entityType.toUpperCase());
    }

    int getDelay() {
      return delay;
    }
  }

  public static class SkullActionData extends BlockActionData {
    String rotation;

    String owner;

    String skullType;

    BlockFace getRotation() {
      if (rotation != null) {
        return BlockFace.valueOf(rotation.toUpperCase());
      }
      return null;
    }
  }

  public static class SignActionData extends BlockActionData {
    String[] lines;
  }
}