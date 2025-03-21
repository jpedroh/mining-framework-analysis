package edu.mit.blocks.workspace.typeblocking;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockLink;
import edu.mit.blocks.codeblocks.BlockLinkChecker;
import edu.mit.blocks.workspace.Workspace;

/**
 * Utilities class to find links between two blocks
 */
public class LinkFinderUtil {
  /**
     * Handles the Connecting of blocks once they are dropped
     * onto the canvas and are waiting to be linked if posible.
     * @param workspace The workspace in use
     * @param child
     * @param parent
     */
  protected static BlockLink connectBlocks(Workspace workspace, Block child, Block parent) {
    if (invalidBlock(child)) {
      return null;
    }
    if (invalidBlock(parent)) {
      return null;
    }
    if (parent.hasPlug()) {
      for (BlockConnector socket : child.getSockets()) {
        if (invalidConnector(socket)) {
          continue;
        }
        BlockLink link = BlockLinkChecker.canLink(workspace, child, parent, socket, parent.getPlug());
        if (link == null) {
          continue;
        }
        return link;
      }
    }
    if (child.hasPlug()) {
      for (BlockConnector socket : parent.getSockets()) {
        if (socket == null) {
          continue;
        }
        if (child.isInfix()) {
          if (parent.isInfix()) {
            continue;
          }
          BlockLink link = BlockLinkChecker.canLink(workspace, parent, child, socket, child.getPlug());
          if (link == null) {
            continue;
          }
          return link;
        } else {
          BlockLink link = BlockLinkChecker.canLink(workspace, parent, child, socket, child.getPlug());
          if (link == null) {
            continue;
          }
          return link;
        }
      }
    }
    if (child.hasBeforeConnector()) {
      for (BlockConnector socket : parent.getSockets()) {
        if (invalidConnector(socket)) {
          continue;
        }
        BlockLink link = BlockLinkChecker.canLink(workspace, parent, child, socket, child.getBeforeConnector());
        if (link == null) {
          continue;
        }
        return link;
      }
    }
    if (child.hasBeforeConnector()) {
      if (parent.hasAfterConnector()) {
        BlockLink link = BlockLinkChecker.canLink(workspace, parent, child, parent.getAfterConnector(), child.getBeforeConnector());
        if (link == null) {
        } else {
          return link;
        }
      }
    }
    if (child.hasAfterConnector()) {
      if (parent.hasBeforeConnector()) {
        BlockLink link = BlockLinkChecker.canLink(workspace, child, parent, child.getAfterConnector(), parent.getBeforeConnector());
        if (link == null) {
        } else {
          return link;
        }
      }
    }
    if (parent.hasPlug()) {
      return LinkFinderUtil.connectBlocks(workspace, child, workspace.getEnv().getBlock(parent.getPlugBlockID()));
    }
    return null;
  }

  /**
     * @param block
     * @return true iff block is invalid (points to a null instance of Block)
     */
  private static boolean invalidBlock(Block block) {
    if (block == null) {
      return true;
    } else {
      return invalidBlock(block.getBlockID());
    }
  }

  /**
     * @param block
     * @return true iff block is invalid (points to a null instance of Block)
     */
  private static boolean invalidBlock(Long blockID) {
    if (blockID == null) {
      return true;
    }
    if (blockID == Block.NULL) {
      return true;
    } else {
      return false;
    }
  }

  /**
     * An invalid connector is defined as one that
     * is either
     * 		(1) NULL,
     * 		(2) belongs to an instance of a null block,
     * 		(3) or already has a block connected to it.
     * If a connector is invalid, this returns true.
     * Otherwise, return false.
     * @param connector
     * @return true if connector is invalid
     */
  private static boolean invalidConnector(BlockConnector connector) {
    if (connector == null) {
      return true;
    }
    if (connector.hasBlock()) {
      return true;
    }
    return false;
  }
}