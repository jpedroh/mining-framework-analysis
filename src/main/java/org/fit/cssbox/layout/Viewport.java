package org.fit.cssbox.layout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Vector;
import org.fit.cssbox.render.BoxRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import cz.vutbr.web.css.CSSFactory;

/**
 * This class represents a browser viewport which is implemented as a special case of a block
 * box. It differs mainly in the way the sizes are computed. Moreover, it provides the methods
 * for margin collapsing which is done before the layout itself.
 * 
 * @author radek
 */
public class Viewport extends BlockBox {
  private static Logger log = LoggerFactory.getLogger(Viewport.class);

  private int width;

  private int height;

  protected BrowserConfig config;

  /** Visible rectagle -- the position and size of the CSS viewport */
  private Rectangle visibleRect;

  private BoxFactory factory;

  private BoxRenderer renderer;

  private Element root;

  private ElementBox rootBox;

  protected ElementBox lastbox = null;

  protected ElementBox lastparent = null;

  private int maxx;

  private int maxy;

  private boolean recomputeAbs;

  /**
     * Creates a new Viewport with the given initial size. The actual size may be increased during the layout. 
     *  
     * @param e The anonymous element representing the viewport.
     * @param g 
     * @param ctx
     * @param factory The factory used for creating the child boxes.
     * @param root The root element of the rendered document.
     * @param width Preferred (minimal) width.
     * @param height Preferred (minimal) height.
     */
  public Viewport(Element e, Graphics2D g, VisualContext ctx, BoxFactory factory, Element root, int width, int height) {
    super(e, g, ctx);
    ctx.setViewport(this);
    this.factory = factory;
    this.root = root;
    style = CSSFactory.createNodeData();
    nested = new Vector<Box>();
    startChild = 0;
    endChild = 0;
    this.width = width;
    this.height = height;
    isblock = true;
    contblock = true;
    root = null;
    visibleRect = new Rectangle(0, 0, width, height);
  }

  /**
     * Obtains the position of the visible part (CSS viewport) in the canvas.
     * @return the visible rectangle
     */
  public Rectangle getVisibleRect() {
    return visibleRect;
  }

  /**
     * Sets the position of the visible part (CSS viewport) in the canvas.
     * @param visibleRect the visible rectangle to be set
     */
  public void setVisibleRect(Rectangle visibleRect) {
    this.visibleRect = visibleRect;
    this.content = visibleRect.getSize();
  }

  /**
     * Obtains the size of the whole canvas that represents the whole rendered page.
     * @return The canvas size.
     */
  public Dimension getCanvasSize() {
    return new Dimension(width, height);
  }

  /**
     * Obtains the current browser configuration.
     * @return current configuration.
     */
  public BrowserConfig getConfig() {
    return config;
  }

  /**
     * Sets the browser configuration used for rendering.
     * @param config the new configuration.
     */
  public void setConfig(BrowserConfig config) {
    this.config = config;
  }

  @Override public void initSubtree() {
    super.initSubtree();
    loadBackgroundFromContents();
  }

  @Override public String toString() {
    return "Viewport " + width + "x" + height + "[visible " + visibleRect.x + "," + visibleRect.y + "," + visibleRect.width + "," + visibleRect.height + "]";
  }

  public BoxFactory getFactory() {
    return factory;
  }

  public int getMinimalWidthLimit() {
    return width;
  }

  /**
     * Obtains the DOM root element.
     * @return The root element of the document.
     */
  public Element getRootElement() {
    return root;
  }

  /**
     * Obtains the child box the corresponds to the DOM root element.
     * @return the corresponding element box or <code>null</code> if the viewport is empty.
     */
  public ElementBox getRootBox() {
    return rootBox;
  }

  public ElementBox getElementBoxByName(String name, boolean case_sensitive) {
    return recursiveFindElementBoxByName(this, name, case_sensitive);
  }

  @Override public void addSubBox(Box box) {
    super.addSubBox(box);
    if (box instanceof ElementBox && ((ElementBox) box).getElement() == root) {
      if (rootBox != null) {
        log.debug("Viewport warning: another root box \'" + box + "\' in addition to previous \'" + rootBox + "\'");
      }
      box.makeRoot();
      rootBox = (ElementBox) box;
    }
  }

  @Override public boolean hasFixedHeight() {
    return true;
  }

  @Override public boolean hasFixedWidth() {
    return true;
  }

  @Override public boolean canIncreaseWidth() {
    return true;
  }

  @Override public boolean isVisible() {
    return true;
  }

  @Override public boolean visibleInClip(Box box) {
    if (config.getClipViewport()) {
      return super.visibleInClip(box);
    } else {
      Rectangle bb;
      if (box instanceof ElementBox) {
        bb = ((ElementBox) box).getAbsoluteBorderBounds();
      } else {
        bb = box.getAbsoluteBounds();
      }
      return (bb.x + bb.width > 0) && (bb.y + bb.height > 0);
    }
  }

  @Override protected boolean separatedFromTop(ElementBox box) {
    return true;
  }

  @Override protected boolean separatedFromBottom(ElementBox box) {
    return true;
  }

  @Override public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    content = new Dimension(width, height);
    bounds = new Rectangle(0, 0, totalWidth(), totalHeight());
  }

  @Override public Viewport getViewport() {
    return this;
  }

  @Override protected void loadPosition() {
    position = BlockBox.POS_ABSOLUTE;
    topset = true;
    leftset = true;
    bottomset = false;
    rightset = false;
    coords = new LengthSet(0, 0, 0, 0);
  }

  @Override protected void loadSizes(boolean update) {
    if (!update) {
      margin = new LengthSet();
      emargin = new LengthSet();
      declMargin = new LengthSet();
      border = new LengthSet();
      padding = new LengthSet();
      content = new Dimension(width, height);
      min_size = new Dimension(-1, -1);
      max_size = new Dimension(-1, -1);
      loadPosition();
    }
    bounds = new Rectangle(0, 0, totalWidth(), totalHeight());
  }

  @Override public void setContentWidth(int width) {
    if (width > this.width) {
      super.setContentWidth(width);
    }
  }

  @Override public void setContentHeight(int height) {
    if (height > this.height) {
      super.setContentHeight(height);
    }
  }

  @Override protected void loadBackground() {
    bgcolor = null;
  }

  /**
	 * Use the root box or the body box (for HTML documents) for obtaining the backgrounds.
	 */
  private void loadBackgroundFromContents() {
    if (rootBox != null) {
      ElementBox src = rootBox;
      if (src.getBgcolor() == null && config.getUseHTML()) {
        src = getElementBoxByName("body", false);
      }
      if (src != null) {
        if (src.getBgcolor() != null) {
          bgcolor = src.getBgcolor();
          src.setBgcolor(null);
        }
        if (src.getBackgroundImages() != null && !src.getBackgroundImages().isEmpty()) {
          bgimages = loadBackgroundImages(src.getStyle());
          src.getBackgroundImages().clear();
        }
      } else {
        log.debug("Couldn\'t find the HTML <body> element");
      }
    }
  }

  /**
	 * Calculates the absolute positions and updates the viewport size
	 * in order to enclose all the boxes.
	 * @param min the minimal viewport dimensions
	 */
  public void updateBounds(Dimension min) {
    maxx = min.width;
    maxy = min.height;
    absolutePositionsChildren();
    if (width < maxx) {
      width = maxx;
    }
    if (height < maxy) {
      height = maxy;
    }
    loadSizes();
    loadBackgroundFromContents();
  }

  @Override public boolean doLayout(int availw, boolean force, boolean linestart) {
    clearSplitted();
    int min = getMinimalContentWidth();
    int pref = Math.max(min, width);
    setContentWidth(pref);
    updateChildSizes();
    widthComputed = true;
    setAvailableWidth(totalWidth());
    if (!contblock) {
      layoutInline();
    } else {
      layoutBlocks();
    }
    return true;
  }

  @Override public boolean formsStackingContext() {
    return true;
  }

  @Override public void absolutePositions() {
    if (parent == null) {
      absbounds = new Rectangle(bounds);
    } else {
      absbounds = new Rectangle(parent.getAbsoluteContentBounds());
    }
    if (scontext != null) {
      scontext.clear();
    }
    absolutePositionsChildren();
  }

  /**
	 * Computes the absolute positions of the child boxes.
	 */
  protected void absolutePositionsChildren() {
    recomputeAbs = false;
    for (int i = 0; i < getSubBoxNumber(); i++) {
      getSubBox(i).absolutePositions();
    }
    if (recomputeAbs) {
      for (int i = 0; i < getSubBoxNumber(); i++) {
        getSubBox(i).absolutePositions();
      }
      recomputeAbs = false;
    }
  }

  /**
     * Sets the current renderer and draws the whole subtree using the given renderer.
     * @param renderer The renderer to be used for drawing.
     */
  public void draw(BoxRenderer renderer) {
    this.renderer = renderer;
    drawStackingContext(false);
  }

  /**
     * Obtains the current renderer used for painting the boxes.
     * @return current renderer.
     */
  public BoxRenderer getRenderer() {
    return renderer;
  }

  /**
	 * Updates the maximal viewport size according to the element bounds
	 */
  public void updateBoundsFor(Rectangle bounds) {
    int x = bounds.x + bounds.width - 1;
    if (maxx < x) {
      maxx = x;
    }
    int y = bounds.y + bounds.height - 1;
    if (maxy < y) {
      maxy = y;
    }
  }

  /**
	 * Indicates that the absolute positions need to be recomputed onec again. This happens when
	 * some absolutely positioned box has a 'static' position depending on some in-flow box
	 * and the position of the in-flow box changed.
	 */
  public void requireRecomputePositions() {
    recomputeAbs = true;
  }

  /**
	 * Uses the given block as a clipping block instead of the default Viewport.
	 * @param block the new clipping block
	 */
  public void clipByBlock(BlockBox block) {
    recursivelySetClipBlock(this, block);
  }

  private void recursivelySetClipBlock(Box root, BlockBox clip) {
    if (root == this || root.getClipBlock() == this) {
      root.setClipBlock(clip);
      if (root instanceof ElementBox) {
        ElementBox eb = (ElementBox) root;
        for (int i = eb.getStartChild(); i < eb.getEndChild(); i++) {
          recursivelySetClipBlock(eb.getSubBox(i), clip);
        }
      }
    }
  }

  @Override public int getContentX() {
    return visibleRect.x;
  }

  @Override public int getContentY() {
    return visibleRect.y;
  }

  @Override public int getContentWidth() {
    return visibleRect.width;
  }

  @Override public int getContentHeight() {
    return visibleRect.height;
  }

  @Override public Rectangle getAbsoluteBackgroundBounds() {
    return new Rectangle(visibleRect);
  }

  @Override public Rectangle getAbsoluteBorderBounds() {
    return new Rectangle(visibleRect);
  }

  @Override public Rectangle getClippedBounds() {
    if (config.getClipViewport()) {
      return getAbsoluteBounds();
    } else {
      return new Rectangle(0, 0, width, height);
    }
  }

  @Override public Rectangle getClippedContentBounds() {
    if (config.getClipViewport()) {
      return getAbsoluteBounds();
    } else {
      return new Rectangle(0, 0, width, height);
    }
  }

  private ElementBox recursiveFindElementBoxByName(ElementBox ebox, String name, boolean case_sensitive) {
    boolean eq;
    if (case_sensitive) {
      eq = ebox.getElement().getTagName().equals(name);
    } else {
      eq = ebox.getElement().getTagName().equalsIgnoreCase(name);
    }
    if (eq) {
      return ebox;
    } else {
      ElementBox ret = null;
      for (int i = 0; i < ebox.getSubBoxNumber() && ret == null; i++) {
        Box child = ebox.getSubBox(i);
        if (child instanceof ElementBox) {
          ret = recursiveFindElementBoxByName((ElementBox) child, name, case_sensitive);
        }
      }
      return ret;
    }
  }
}