package edu.mit.blocks.workspace;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.renderable.Comment;
import edu.mit.blocks.renderable.RenderableBlock;

/**
 * A MiniMap is a miniturized representation of the
 * current Workspace's block canvas. Each MiniMap may
 * only be associated with only one block canvas.
 * 
 * A MiniMap should display its associated set of
 * RenderableBlocks and Comments at their current position,
 * or render their motion in real time.
 * 
 * The Minimap must tranform the block canvas's width:height ratio
 * such that it would fit within the MiniMap's area.
 * This may warp the blocks to appear thinner/wider
 * than their real size.
 * 
 * The MiniMap should expand and shrink with some delay
 * as the user mouses over or moves the mouse out of focus.
 * 
 * blockCanvas : BlockCanvas 		//the block canvas that this MiniMap renders
 * blocks : Set<RenderableBlocks> 	//the set of blocks that this renders
 * comments : Set<Comment> 			//the set of comments that this renders
 * mapwidth : Integer				//this MiniMap's maximum width
 * mapheight: Integer				//this MiniMap's maximum height
 * ratio : Double					//the aspect ratio that this should maintain
 * 									//		when rendering this.blocks and this.comments
 */
public class MiniMap extends JPanel implements WorkspaceWidget, MouseListener, MouseMotionListener, SearchableContainer, PageChangeListener {
  private static final long serialVersionUID = 328149080271L;

  /**the border width of the this mini map*/
  private static final int BORDER_WIDTH = 5;

  /**the default width of a mini map*/
  private static final int DEFAULT_WIDTH = 150;

  /**the default height of a mini map*/
  private static final int DEFAULT_HEIGHT = 75;

  /**this.width*/
  private int MAPWIDTH = 150;

  /**this.height*/
  private int MAPHEIGHT = 75;

  /**this.blockCanvas*/
  private BlockCanvas blockCanvas;

  private boolean expand = false;

  private final MiniMapEnlargerTimer enlarger;

  /**this.ratio*/
  private double transformX = 1;

  private double transformY = 1;

  private final Workspace workspace;

  /**
     * @effect  constructs a MiniMap, M, such that
     * 			M.blockCanvas = The current blockcanvas in Workspace &&
     * 			M.blocks = M.blockCanvas.getBlocks &&
     * 			M.comments = Set of all M.blockCanvas.getBlocks.getComment &&
     * 			M.mapwidth = width parameter &&
     * 			M.ratio = M.blockCanvas : M.wdith &&
     * 			M.location = ALWAYS 16 pixels away from the upper-right edge corner
     */
  public MiniMap(Workspace workspace) {
    super();
    this.workspace = workspace;
    this.setPreferredSize(new Dimension(MAPHEIGHT, MAPHEIGHT));
    this.setLayout(null);
    this.setOpaque(false);
    this.setFont(new Font("Ariel", Font.PLAIN, 9));
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    this.enlarger = new MiniMapEnlargerTimer();
    PageChangeEventManager.addPageChangeListener(this);
  }

  /**
     * @modies this.preferredSize && this.size && this.isVisible
     * @effect mutates this MiniMap by making it invisible
     */
  public void hideMiniMap() {
    this.setSize(0, 0);
    this.setPreferredSize(new Dimension(0, 0));
    this.setVisible(false);
  }

  public void update() {
    this.repaint();
  }

  /**
     * Specified by codeblocks.workspace.SearchableContainer interface
     */
  public Iterable<RenderableBlock> getSearchableElements() {
    return new ArrayList<RenderableBlock>();
  }

  /**
     * Specified by codeblocks.workspace.SearchableContainer interface
     */
  public void updateContainsSearchResults(boolean containsSearchResults) {
    this.repaint();
  }

  public void repositionMiniMap() {
    if (this.getParent() != null) {
      this.setBounds(this.getParent().getWidth() - MAPWIDTH - 26, 0, MAPWIDTH + 2 * BORDER_WIDTH, MAPHEIGHT + 2 * BORDER_WIDTH);
    }
  }

  /**
     * @modifies this.bounds && this.blockCanvas && this.blocks && this.comments
     * @effects 1] Point this.blockCanvas to whatever current block
     * 			   canvas is in Workspace
     * 			2] Reset this.bounds to maintain aspect ratio and be
     * 			   16 pixels away from upper-right edge corner &&
     * 			3] Rerender this.blocks and this.comment toreflect
     * 			   real-time relative positions and dimension
     */
  public void paint(Graphics g) {
    super.paint(g);
    for (int i = 0; i < BORDER_WIDTH; i++) {
      g.setColor(new Color(200, 200, 150, 50 * (i + 1)));
      g.drawRect(i, i, this.getWidth() - 1 - 2 * i, this.getHeight() - 1 - 2 * i);
    }
    this.blockCanvas = workspace.getBlockCanvas();
    this.transformX = (double) (MAPWIDTH) / this.getCanvas().getWidth();
    this.transformY = (double) (MAPHEIGHT) / this.getCanvas().getHeight();
    g.translate(5, 5);
    for (Page page : this.blockCanvas.getPages()) {
      Color pageColor = page.getPageColor();
      g.setColor(new Color(pageColor.getRed(), pageColor.getGreen(), pageColor.getBlue(), 200));
      Rectangle pageRect = rescaleRect(page.getJComponent().getBounds());
      g.fillRect(pageRect.x, pageRect.y, pageRect.width, pageRect.height);
      g.setColor(Color.white);
      g.clipRect(pageRect.x, pageRect.y, pageRect.width, pageRect.height);
      g.drawString(page.getPageName(), pageRect.x + 1, pageRect.height - 3);
      if (page.getIcon() != null && expand) {
        g.drawImage(page.getIcon(), pageRect.x + 1, pageRect.height - 28, 15, 15, null);
      }
      g.setClip(null);
      for (Component component : page.getJComponent().getComponents()) {
        if (component instanceof RenderableBlock && component != null && component.isVisible()) {
          if (((RenderableBlock) component).isSearchResult()) {
            g.setColor(Color.yellow);
          } else {
            g.setColor(((RenderableBlock) component).getBLockColor());
          }
          drawBoundingBox(g, component);
        } else {
          if (component instanceof Comment && component.isVisible()) {
            g.setColor(Color.yellow);
            drawBoundingBox(g, component);
          }
        }
      }
    }
    for (Component component : this.getCanvas().getComponents()) {
      if (component instanceof PageDivider) {
        g.setColor(Color.GRAY);
        Rectangle dividerRect = rescaleRect(component.getBounds());
        g.fillRect(dividerRect.x, dividerRect.y, dividerRect.width + 1, dividerRect.height);
      }
    }
    for (Component component : workspace.getComponentsInLayer(Workspace.DRAGGED_BLOCK_LAYER)) {
      if (component instanceof RenderableBlock && component != null && component.isVisible()) {
        g.setColor(((RenderableBlock) component).getBLockColor());
        drawBoundingBox(g, component);
      } else {
        if (component instanceof Comment && component.isVisible()) {
          g.setColor(Color.yellow);
          drawBoundingBox(g, component);
        }
      }
    }
    g.setColor(Color.red);
    g.drawRect(rescaleX(blockCanvas.getHorizontalModel().getValue()), rescaleY(blockCanvas.getVerticalModel().getValue()), rescaleX(blockCanvas.getWidth()), rescaleY(blockCanvas.getHeight()));
  }

  /**
     * @effect Renders a JComponent by drawing a rectangle around
     * 		   its bounding box (rescaled to fit MiniMap) using
     * 		   the given graphics context
     */
  private void drawBoundingBox(Graphics g, Component block) {
    Rectangle blockRect = block.getBounds();
    blockRect.setLocation(SwingUtilities.convertPoint(block.getParent(), blockRect.getLocation(), getCanvas()));
    blockRect = rescaleRect(blockRect);
    g.fillRect((blockRect.x), (blockRect.y), (blockRect.width), (blockRect.height));
    g.setColor(Color.white);
    g.drawRect((blockRect.x), (blockRect.y), (blockRect.width), (blockRect.height));
  }

  /**
     * @return viewPort of this.blockCanvas
     */
  private JComponent getCanvas() {
    return blockCanvas.getCanvas();
  }

  /**
     * Scales i to current aspect ratio.
     * @param p
     *
     * @requires p != null
     * @return new Point that is a copy of "p" tranformed
     * 			 by (this.transformX, this.transformY)
     */
  private int rescaleX(int x) {
    return (int) (x * this.transformX);
  }

  private int rescaleY(int y) {
    return (int) (y * this.transformY);
  }

  private Rectangle rescaleRect(Rectangle rec) {
    return new Rectangle((int) (rec.x * this.transformX), (int) (rec.y * this.transformY), (int) (rec.width * this.transformX), (int) (rec.height * this.transformY));
  }

  /**
     * Scales i to World using curent aspect ratio
     * @param p
     *
     * @requires p != null && this.transformX != 0 && this.transformY != 0
     * @return new Point that is a copy of "p" tranformed
     * 			 by (1/this.transformX, 1/this.transformY)
     */
  private Point rescaleToWorld(Point p) {
    Point point = new Point((int) (p.x / this.transformX), (int) (p.y / this.transformY));
    return point;
  }

  /**
     * Set this.blockCanvas.viewport to be centered around p if posible
     * @param p
     */
  private void scrollToPoint(Point p) {
    Point transform = rescaleToWorld(p);
    blockCanvas.getHorizontalModel().setValue((int) (transform.x - 0.5 * blockCanvas.getWidth()));
    blockCanvas.getVerticalModel().setValue((int) (transform.y - 0.5 * blockCanvas.getHeight()));
    this.repaint();
  }

  /**
     * When dragging along miniMap, zoom to new point
     */
  public void mouseDragged(MouseEvent e) {
    scrollToPoint(e.getPoint());
  }

  /**
     * When releasing a mouse in a MiniMap, scroll to point
     */
  public void mouseReleased(MouseEvent e) {
    scrollToPoint(e.getPoint());
  }

  /**MouseEvent methods not interested by this WorkspceWidget**/
  public void mouseMoved(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
    expand = true;
    enlarger.expand();
  }

  public void mouseExited(MouseEvent e) {
    expand = false;
    enlarger.shrink();
  }

  public void mouseClicked(MouseEvent e) {
  }

  /**
     * @param block
     * @param WorkspacePoint
     * @modifies block
     * @effects  change block.location to canvas point
     * 			 represented by WorkspacePoint.  In other words,
     * 			 find what point WorkspacePoint represents on the
     * 			 MiniMap adn find its corresponding point on the
     * 			 blockCanvas.  Then set block.location to that point
     * 			 on the blockCanvas.
     */
  public void blockDragged(RenderableBlock block, Point WorkspacePoint) {
    Point mapPoint = SwingUtilities.convertPoint(block, WorkspacePoint, this);
    mapPoint.translate(-6, -6);
    Point worldPoint = SwingUtilities.convertPoint(this.getCanvas(), rescaleToWorld(mapPoint), workspace);
    int width = block.getStackBounds().width + 3;
    int height = block.getStackBounds().height + 3;
    int canvasWidth = this.getCanvas().getWidth() - workspace.getCanvasOffset().width;
    int canvasHeight = this.getCanvas().getHeight() - workspace.getCanvasOffset().height;
    if (worldPoint.y + height > canvasHeight) {
      worldPoint.setLocation(worldPoint.x, canvasHeight - height);
    }
    if (worldPoint.x + width > canvasWidth) {
      worldPoint.setLocation(canvasWidth - width, worldPoint.y);
    }
    block.setLocation(worldPoint);
    block.getDragHandler().myLoc.setLocation(worldPoint);
    block.moveConnectedBlocks();
    this.repaint();
  }

  /**
     * @param block
     * @modifies block
     * @effects  Set block to whatever page it is on is one exist.
     * 			 Otherwise, set it to whatever Widget is currently underneath
     * 			 it.
     */
  public void blockDropped(RenderableBlock block) {
    Point location = block.getLocation();
    if (location.getY() <= 0) {
      location.setLocation(location.getX(), 1);
    }
    WorkspaceWidget w = workspace.getWidgetAt(location);
    for (Page page : this.blockCanvas.getPages()) {
      if (page.contains(SwingUtilities.convertPoint(block.getParent(), location, page.getJComponent()))) {
        w = page;
      }
    }
    RenderableBlock socketBlock;
    for (BlockConnector con : (workspace.getEnv().getBlock(block.getBlockID()).getSockets())) {
      socketBlock = block.getWorkspace().getEnv().getRenderableBlock(con.getBlockID());
      if (socketBlock != null) {
        w = socketBlock.getParentWidget();
      }
    }
    w.blockDropped(block);
    this.repaint();
  }

  /**Block action methods not interested by MiniMap.  Does nothing*/
  public void blockEntered(RenderableBlock block) {
  }

  public void blockExited(RenderableBlock block) {
  }

  public void blockDragged(RenderableBlock block) {
  }

  public void addBlock(RenderableBlock block) {
  }

  public void addBlocks(Collection<RenderableBlock> blocks) {
  }

  public void removeBlock(RenderableBlock block) {
  }

  /**JComponent representation of this*/
  public JComponent getJComponent() {
    return this;
  }

  /**defined by JComponent.contains()*/
  public boolean contains(int x, int y) {
    return new Rectangle(7, 7, this.getWidth() - 15, this.getHeight() - 15).contains(x, y);
  }

  public Collection<RenderableBlock> getBlocks() {
    return new ArrayList<RenderableBlock>();
  }

  /**
     * Animate A fly to block's location on the MiniMap and BlockCanvas
     * @param block
     */
  public void animateAutoCenter(RenderableBlock block) {
    MiniMapAutoCenterTimer timer = new MiniMapAutoCenterTimer(block);
    timer.start();
  }

  private class MiniMapAutoCenterTimer implements ActionListener {
    /**Internal Timer*/
    private javax.swing.Timer timer;

    /**Number of repititions*/
    private int count;

    /**Change in x per repitition*/
    private int dx;

    /**Change in y per repitition*/
    private int dy;

    /**Constructs this*/
    public MiniMapAutoCenterTimer(RenderableBlock block) {
      timer = new javax.swing.Timer(5, this);
      count = 25;
      Point blockPosition = SwingUtilities.convertPoint(block, new Point((int) (block.getStackBounds().getWidth() / 2), (int) (block.getStackBounds().getHeight() / 2)), getCanvas());
      dx = (blockPosition.x - (blockCanvas.getHorizontalModel().getValue() + blockCanvas.getWidth() / 2)) / count;
      dy = (blockPosition.y - (blockCanvas.getVerticalModel().getValue() + blockCanvas.getHeight() / 2)) / count;
    }

    /**starts internal Timer*/
    public void start() {
      timer.start();
    }

    /**stops internal Timer*/
    public void stop() {
      timer.stop();
    }

    /**Repositions view in blockCanvas iff count>=0; Otherwise stop internal Timer*/
    public void actionPerformed(ActionEvent e) {
      if (count < 0) {
        timer.stop();
      } else {
        blockCanvas.getHorizontalModel().setValue(blockCanvas.getHorizontalModel().getValue() + dx);
        blockCanvas.getVerticalModel().setValue(blockCanvas.getVerticalModel().getValue() + dy);
        count--;
      }
    }
  }

  private class MiniMapEnlargerTimer implements ActionListener {
    /**Growth count*/
    private int count;

    /**Internal Timer*/
    private javax.swing.Timer timer;

    /**absolute value of width growth*/
    private int dx = DEFAULT_WIDTH / 10;



    /**absolute value of height Growth*/
    private int dy = DEFAULT_HEIGHT / 10;



    /**Indicates whether the MiniMap is/was expanding (true)
         * or skrinking (false)*/
    private boolean expand;

    private javax.swing.Timer delayTimer;

    /**
         * Constuctors an animator that can enlarge or skrink the miniMap
         */
    public MiniMapEnlargerTimer() {
      count = 0;
      this.expand = true;
      timer = new Timer(10, this);
      delayTimer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          delayTimer.stop();
          timer.start();
        }
      });
    }

    /**
         * expands/shrinks the miniMap untill count is 0 or 15.
         * At 0, the map is smallest as possible and at 15, the
         * map is largest as possible
         */
    public void actionPerformed(ActionEvent e) {
      if (count <= 0 || count > 15) {
        timer.stop();
      } else {
        if (expand) {
          count = count + 1;
        } else {
          count = count - 1;
        }
        MAPWIDTH = DEFAULT_WIDTH + count * dx;
        MAPHEIGHT = DEFAULT_HEIGHT + count * dy;
        repositionMiniMap();
        repaint();
      }
    }

    /**
         * enlargest this minimap
         */
    public void expand() {
      this.expand = true;
      count++;
      this.timer.start();
    }

    /**
         * shrinks this minimap
         */
    public void shrink() {
      count--;
      this.expand = false;
      this.delayTimer.start();
    }
  }
}