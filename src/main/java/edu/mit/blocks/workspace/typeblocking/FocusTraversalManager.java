package edu.mit.blocks.workspace.typeblocking;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.BlockCanvas.Canvas;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceListener;

/**
 * The FocusTraversalManager has two function.  First, it 
 * maintains a pointer to the block, if any, that has 
 * focus and the corresponding focus point on that block.  
 * If the focus is not on the block, then it must be set 
 * to some point of the block canvas.
 * 
 * The second primary function of the FocusTraversalManager 
 * is to redirect the focus to the next appropriate block 
 * in a particular stack.  One could "traverse" the stack 
 * by moving the focus to one of the following:
 * 		1. the block after
 * 		2. the block before
 * 		3. the next block
 * 		4. the previous block
 * 
 * The exact definition of what "next", "previous", 
 * "after", and "before" is described in details in 
 * their corresponding method summary.
 * As a focus manager of the entire system, the class 
 * must maintain particular invariants at all time.  
 * Clients of this module may obtain the focus through 
 * three observer (getter) methods.  Clients may also 
 * manualy mutate the focus through three modifier (setter) 
 * methods.
 * 
 * However, BOTH the value returned in the observer 
 * methods and the value passed in the modifier methods 
 * MUST maintain particular invariants described below.
 * 
 * These invariants must hold at all time and check reps 
 * should be imposed to ensure that any changes to the 
 * system still holds these crucial invariants.  Clients 
 * of this module may assume that the invariants mentioned 
 * below will always hold.
 * 
 * INVARIANT I.
 * 	If the canvas has focus, then the block does not. Thus
 * 		1. focusBlock == Block.null
 * 		2. canvasFocusPoint != null
 * 		3. blockFocusPoint == null
 * 
 * INVARIANT II.
 * 	If the block has focus, then the canvas does not. Thus
 * 		1. focusBlock != Block.null
 * 		2. canvasFocusPoint == null
 * 		3. blockFocusPoint != null
 * 
 * @specfield focusBlock : Long //block with focus
 * @specfield canvasFocusPoint : Point //focus point on canvas relative to canvas
 * @specfield blockFocusPoint : Point //focus point on block relative to block
 *
 */
public class FocusTraversalManager implements MouseListener, KeyListener, WorkspaceListener {
  /** this.focuspoint: the point on the block with focus */
  private Point blockFocusPoint = null;

  /** this.focuspoint: the point on the block canvas's last mosue click */
  private Point canvasFocusPoint = new Point(0, 0);

  /** this.focusblock: the Block ID that currently has focus */
  private Long focusBlock = Block.NULL;

  private final Workspace workspace;

  public FocusTraversalManager(Workspace workspace) {
    this.workspace = workspace;
  }

  /**
     * @return the block that has focus, if any.
     *
     * TODO: finish method documentation
     */
  public Long getFocusBlockID() {
    if (invalidBlock(focusBlock)) {
      if (canvasFocusPoint == null) {
        throw new RuntimeException("Focus has not yet been set to block");
      }
      if (blockFocusPoint != null) {
        throw new RuntimeException("Focus should be set to block");
      }
    } else {
      if (canvasFocusPoint != null) {
        throw new RuntimeException("Focus has not yet been set to canvas");
      }
      if (blockFocusPoint == null) {
        throw new RuntimeException("Focus has not been removed from block");
      }
    }
    return focusBlock;
  }

  /**
     * @return point of focus on canvas
     *
     * TODO: finish method documentation
     */
  public Point getCanvasPoint() {
    if (!invalidBlock(focusBlock)) {
      throw new RuntimeException("May not request canvas\'s focus point if " + "canvas does not have focus. Focus at: " + focusBlock);
    }
    if (blockFocusPoint != null) {
      throw new RuntimeException("May not request canvas\'s focus point if " + "canvas does not have focus. Focus at: " + blockFocusPoint);
    }
    if (canvasFocusPoint == null) {
      throw new RuntimeException("May not request canvas\'s focus point if " + "canvas does not have focus. Canvas focus is null.");
    }
    return canvasFocusPoint;
  }

  /**
     * @return point of focus on block
     *
     * TODO: finish method documentation
     */
  public Point getBlockPoint() {
    if (invalidBlock(focusBlock)) {
      throw new RuntimeException("May not request block\'s focus point if " + "block does not have focus. Focus at: " + focusBlock);
    }
    if (blockFocusPoint == null) {
      throw new RuntimeException("May not request block\'s focus point if " + "block does not have focus. Focus at: " + blockFocusPoint);
    }
    if (canvasFocusPoint != null) {
      throw new RuntimeException("Canvas focus is still valid. May not request" + "block\'s focus point if block does not have focus.");
    }
    return blockFocusPoint;
  }

  /**
     * Sets focus to block
     * @parem block
     *
     * TODO: finish method documentation
     */
  public void setFocus(Block block) {
    if (block == null) {
      throw new RuntimeException("Invariant Violated:" + "may not set focus to a null Block instance");
    } else {
      setFocus(block.getBlockID());
    }
  }

  public void setFocus(Long blockID) {
    if (blockID == null || blockID == Block.NULL || blockID == -1 || workspace.getEnv().getBlock(blockID) == null) {
      throw new RuntimeException("Invariant Violated:" + "may not set focus to a null Block instance");
    }
    if (!invalidBlock(this.focusBlock)) {
      getBlock(this.focusBlock).setFocus(false);
      workspace.getEnv().getRenderableBlock(this.focusBlock).repaintBlock();
    }
    getBlock(blockID).setFocus(true);
    workspace.getEnv().getRenderableBlock(blockID).requestFocus();
    workspace.getEnv().getRenderableBlock(blockID).repaintBlock();
    this.canvasFocusPoint = null;
    this.blockFocusPoint = new Point(0, 0);
    this.focusBlock = blockID;
  }

  /**
     * Set Focus to canvas at canvasPoint.  THE BLOCKID MUST BE BLOCK.NULL!!!
     * @param canvasPoint
     * @param blockID
     *
     * TODO: finish method documentation
     */
  public void setFocus(Point canvasPoint, Long blockID) {
    if (blockID == null || blockID == Block.NULL || blockID == -1 || workspace.getEnv().getBlock(blockID) == null) {
      if (!invalidBlock(this.focusBlock)) {
        getBlock(this.focusBlock).setFocus(false);
        workspace.getEnv().getRenderableBlock(this.focusBlock).repaintBlock();
      }
      this.focusBlock = Block.NULL;
      this.canvasFocusPoint = canvasPoint;
      this.blockFocusPoint = null;
    } else {
      throw new RuntimeException("Invariant Violated:" + "may not set new focus point if focus is on a block");
    }
  }

  void setFocus(Point location) {
    throw new RuntimeException("The use of this method is FORBIDDEN");
  }

  /**
     * Reassigns the focus to the "next block" of the current focusBlock.
     * If the current focusblock is at location n of the flatten linear vector
     * of the block tree structure, then the "next block" is located at n+1.
     * In other words, the previous block is the parent block of the next
     * socket of the parent block of the focusblock.
     *
     * @requires 	pointFocusOwner != null &&
     * 				focusblock.getSockets() != null &&
     * 				focusblock.getSockets() is not empty
     * @modifies this.focusblock
     * @effects this.focusblock now points to the "next block"
     * 			as described in method overview;
     * @returns true if the new focus is on a block that isn't null
     */
  public boolean focusNextBlock() {
    if (invalidBlock(focusBlock) || !workspace.getEnv().getRenderableBlock(focusBlock).isVisible()) {
      setFocus(canvasFocusPoint, Block.NULL);
      return false;
    }
    Block currentBlock = getBlock(focusBlock);
    for (BlockConnector socket : currentBlock.getSockets()) {
      if (socket != null && !invalidBlock(socket.getBlockID())) {
        setFocus(socket.getBlockID());
        return true;
      }
    }
    Long afterBlock = currentBlock.getAfterBlockID();
    if (!invalidBlock(afterBlock)) {
      setFocus(afterBlock);
      return true;
    }
    Block nextBlock = this.getNextNode(currentBlock);
    if (nextBlock == null) {
      throw new RuntimeException("Invariant Violated: return value of getNextNode() may not be null");
    }
    setFocus(nextBlock.getBlockID());
    return true;
  }

  /**
     * Reassigns the focus to the "previous block" of the current focusBlock.
     * If the current focusblock is at location n of the flatten linear vector
     * of the block tree structure, then the "previous block" is located at n-1.
     * In other words, the previous block is the innermost block of the previous
     * socket of the parent block of the focusblock.
     *
     * @requires 	pointFocusOwner != null &&
     * 				focusblock.getSockets() != null &&
     * 				focusblock.getSockets() is not empty
     * @modifies this.focusblock
     * @effects this.focusblock now points to the "previous block"
     * 			as described in method overview;
     * @returns true if the new focus is on a block that isn't null
     */
  public boolean focusPrevBlock() {
    if (invalidBlock(focusBlock) || !workspace.getEnv().getRenderableBlock(focusBlock).isVisible()) {
      setFocus(canvasFocusPoint, Block.NULL);
      return false;
    }
    Block currentBlock = getBlock(focusBlock);
    Block previousBlock = getPlugBlock(currentBlock);
    if (previousBlock == null) {
      previousBlock = getBeforeBlock(currentBlock);
    }
    if (previousBlock == null) {
      previousBlock = getBottomRightBlock(currentBlock);
    } else {
      Block beforeBlock = previousBlock;
      List<BlockConnector> connections = new ArrayList<BlockConnector>();
      for (BlockConnector socket : previousBlock.getSockets()) {
        connections.add(socket);
      }
      connections.add(previousBlock.getAfterConnector());
      for (BlockConnector connector : connections) {
        if (connector == null || connector.getBlockID() == Block.NULL || getBlock(connector.getBlockID()) == null) {
          continue;
        }
        if (connector.getBlockID().equals(currentBlock.getBlockID())) {
          if (!beforeBlock.getBlockID().equals(previousBlock.getBlockID())) {
            previousBlock = getBottomRightBlock(previousBlock);
          }
          setFocus(previousBlock.getBlockID());
          return true;
        }
        previousBlock = getBlock(connector.getBlockID());
      }
      previousBlock = getBottomRightBlock(previousBlock);
    }
    setFocus(previousBlock.getBlockID());
    return true;
  }

  /**
     * Gives focus to the first after block down the tree,
     * that is, the next control block in the stack.
     * If next control block does not exist, then give
     * focus to current focusblock.  Otherwise, give
     * focus to block canvas.
     * @requires focusblock.isMinimized() == false
     * @modifies this.focusblock
     * @effects sets this.focusblock to be the first
     * 			after block if possible.  Otherwise, keep
     * 			the focus on the current focusblock.
     * 			If focus block is an invalid block,
     * 			return focus to the default (block canvas)
     * @returns true if and only if focus was set to new after block
     * @expects no wrapping to TopOfStack block, do not use this method for infix blocks
     */
  public boolean focusAfterBlock() {
    if (invalidBlock(focusBlock) || !workspace.getEnv().getRenderableBlock(focusBlock).isVisible()) {
      setFocus(canvasFocusPoint, Block.NULL);
      return false;
    }
    Block currentBlock = getBlock(focusBlock);
    while (currentBlock != null) {
      if (getAfterBlock(currentBlock) != null) {
        setFocus(getAfterBlock(currentBlock));
        return true;
      }
      currentBlock = getPlugBlock(currentBlock);
      if (currentBlock == null) {
        setFocus(focusBlock);
        return true;
      }
    }
    return true;
  }

  /**
     * Gives focus to the first beforeblock up the tree,
     * that is, the previous control block in the stack.
     * If no previous control block exists, then give
     * focus to current focusblock.  Otherwise, give
     * focus to block canvas.
     * @requires focusblock.isMinimized() == false
     * @modifies this.focusblock
     * @effects sets this.focusblock to be the first
     * 			before block if possible.  Otherwise, keep
     * 			the focus on the current focusblock.
     * 			If focus block is an invalid block,
     * 			return focus to the default (block canvas)
     * @returns true if and only if focus was set to new before block
     * @expects no wrapping to bottom block, do not use this method for infix blocks
     */
  public boolean focusBeforeBlock() {
    if (invalidBlock(focusBlock) || !workspace.getEnv().getRenderableBlock(focusBlock).isVisible()) {
      setFocus(canvasFocusPoint, Block.NULL);
      return false;
    }
    Block currentBlock = getBlock(focusBlock);
    while (currentBlock != null) {
      if (getBeforeBlock(currentBlock) != null) {
        setFocus(getBeforeBlock(currentBlock));
        return true;
      }
      currentBlock = getPlugBlock(currentBlock);
      if (currentBlock == null) {
        setFocus(focusBlock);
        return false;
      }
    }
    return false;
  }

  /**
     * @requires currentBlock != null
     * @param currentBlock
     * @return currentBlock or NON-NULL block that is the next node of currentBlock
     */
  private Block getNextNode(Block currentBlock) {
    if (invalidBlock(currentBlock)) {
      throw new RuntimeException("Invariant Violated: may not resurve over a null instance of currentBlock");
    }
    Block parentBlock = getBlock(currentBlock.getPlugBlockID());
    if (invalidBlock(parentBlock)) {
      parentBlock = getBlock(currentBlock.getBeforeBlockID());
    }
    if (invalidBlock(parentBlock)) {
      return currentBlock;
    }
    int i = parentBlock.getSocketIndex(parentBlock.getConnectorTo(currentBlock.getBlockID()));
    if (i != -1 && i >= 0) {
      for (BlockConnector parentSocket : parentBlock.getSockets()) {
        if (parentSocket == null || invalidBlock(parentSocket.getBlockID()) || parentBlock.getSocketIndex(parentSocket) <= i) {
          continue;
        } else {
          return getBlock(parentSocket.getBlockID());
        }
      }
    }
    if (invalidBlock(parentBlock.getAfterBlockID())) {
      return getNextNode(parentBlock);
    }
    if (parentBlock.getAfterBlockID().equals(currentBlock.getBlockID())) {
      return getNextNode(parentBlock);
    }
    return getBlock(parentBlock.getAfterBlockID());
  }

  /**
     * For a given block, returns the outermost (top-leftmost)
     * block in the stack.
     * @requires block represented by blockID != null
     * @param blockID any block in a stack.
     * @return  the outermost block (or Top-of-Stack)
     * 			such that the outermost  block != null
     */
  Long getTopOfStack(Long blockID) {
    if (blockID == null || blockID == Block.NULL || workspace.getEnv().getBlock(blockID) == null) {
      throw new RuntimeException("Invariant Violated: may not" + "iterate for outermost block over a null instance of Block");
    }
    Block parentBlock = null;
    parentBlock = getBeforeBlock(blockID);
    if (parentBlock != null) {
      return getTopOfStack(parentBlock.getBlockID());
    }
    parentBlock = getPlugBlock(blockID);
    if (parentBlock != null) {
      return getTopOfStack(parentBlock.getBlockID());
    }
    if (parentBlock != null) {
      throw new RuntimeException("Invariant Violated: may not " + "return a null instance of block as the outermost block");
    }
    return blockID;
  }

  /**
     * For a given block, returns the innermost (bottom-rightmost)
     * block in the substack.
     * @requires block !=null block.getBlockID != Block.NULL
     * @param block the top block of the substack.
     * @return the innermost block in the substack.
     * 		   such that the innermost block != null
     */
  private Block getBottomRightBlock(Block block) {
    if (block == null || block.getBlockID() == Block.NULL) {
      throw new RuntimeException("Invariant Violated: may not" + "iterate for innermost block over a null instance of Block");
    }
    Block returnBlock = null;
    returnBlock = getAfterBlock(block);
    if (returnBlock != null) {
      return getBottomRightBlock(returnBlock);
    }
    for (BlockConnector socket : block.getSockets()) {
      Block socketBlock = getBlock(socket.getBlockID());
      if (socketBlock != null) {
        returnBlock = socketBlock;
      }
    }
    if (returnBlock != null) {
      return getBottomRightBlock(returnBlock);
    }
    if (returnBlock != null) {
      throw new RuntimeException("Invariant Violated: may not " + "return a null instance of block as the innermost block");
    }
    return block;
  }

  /**
     * @param block
     * @return 	true if and only if block ==null ||
     * 			block.getBlockID == null &&
     * 			block.getBLockID == Block.NULL
     */
  private boolean invalidBlock(Block block) {
    if (block == null) {
      return true;
    }
    if (block.getBlockID() == null) {
      return true;
    }
    if (block.getBlockID() == Block.NULL) {
      return true;
    }
    return false;
  }

  private boolean invalidBlock(Long blockID) {
    if (blockID == null) {
      return true;
    }
    if (blockID == Block.NULL) {
      return true;
    }
    if (getBlock(blockID) == null) {
      return true;
    }
    return false;
  }

  /**
     * All the private methods below follow a similar
     * specification. They all require that the block
     * referanced by blockID (or block.getBlockID) is
     * non-null.  If getting a socket block, they
     * additionally require that 0<socket< # of sockets in block.
     * All the methods before return a block located
     * at a block connector corresponding to the name
     * of the obserser method.
     *
     * @requires blockID != Block.Null && block !=null
     * @returns Block instance located at corresponding
     * 			connection or null if non exists
     */
  private Block getBlock(Long blockID) {
    return workspace.getEnv().getBlock(blockID);
  }

  private Block getBeforeBlock(Long blockID) {
    return getBeforeBlock(getBlock(blockID));
  }

  private Block getBeforeBlock(Block block) {
    return getBlock(block.getBeforeBlockID());
  }

  private Block getAfterBlock(Block block) {
    return getBlock(block.getAfterBlockID());
  }

  private Block getPlugBlock(Long blockID) {
    return getPlugBlock(getBlock(blockID));
  }

  private Block getPlugBlock(Block block) {
    return getBlock(block.getPlugBlockID());
  }

  /**
     * Action: removes the focus current focused block
     * 		   and places new focus on e.getSource
     * @requires e != null
     * @modifies this.blockFocusOwner && e.getSource
     * @effects removes focus from this.blockFocusOwner
     * 			adds focus to e.getSource iff e.getSource
     * 			is instance of BlockCanvas and RenderableBlock
     */
  private void grabFocus(MouseEvent e) {
    if (e.getSource() instanceof Canvas) {
      Point canvasPoint = e.getPoint();
      setFocus(canvasPoint, Block.NULL);
      ((Canvas) e.getSource()).grabFocus();
    } else {
      if (e.getSource() instanceof RenderableBlock) {
        setFocus(((RenderableBlock) e.getSource()).getBlockID());
        ((RenderableBlock) e.getSource()).grabFocus();
      }
    }
  }

  public void mousePressed(MouseEvent e) {
    grabFocus(e);
  }

  public void mouseReleased(MouseEvent e) {
    grabFocus(e);
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    KeyInputMap.processKeyChar(workspace, e);
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }

  /**
     * Subscription: BLOCK_ADDED events.
     * Action: add this.mouselistener to the block referanced by event
     * @requires block reference in event is not null
     * @modifies this.blockFocusOwner && event.block
     * @effects Add this.mouselistener to this.blockFocusOwner
     * 			removes focus from this.blockFocusOwner
     * 			adds focus to e.getSource iff e.getSource
     * 			is instance of BlockCanvas and RenderableBlock
     */
  public void workspaceEventOccurred(WorkspaceEvent event) {
    switch (event.getEventType()) {
      case WorkspaceEvent.BLOCK_ADDED:
      if (!(event.getSourceWidget() instanceof Page)) {
        break;
      }
      RenderableBlock rb = workspace.getEnv().getRenderableBlock(event.getSourceBlockID());
      if (rb == null) {
        break;
      }
      for (MouseListener l : rb.getMouseListeners()) {
        if (l.equals(this)) {
          return;
        }
      }
      rb.addMouseListener(this);
      rb.addKeyListener(this);
      setFocus(event.getSourceBlockID());
      rb.grabFocus();
      break;
    }
  }

  public String toString() {
    return "FocusManager: " + blockFocusPoint + " of " + workspace.getEnv().getBlock(focusBlock);
  }
}