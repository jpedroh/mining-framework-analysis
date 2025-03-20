package org.fit.cssbox.layout;
import java.awt.Graphics2D;
import java.util.*;
import org.fit.cssbox.css.HTMLNorm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import cz.vutbr.web.css.*;

/**
 * A box that represents a table.
 * http://www.w3.org/TR/CSS21/tables.html
 * 
 * @author burgetr
 */
public class TableBox extends BlockBox {
  private static Logger log = LoggerFactory.getLogger(TableBox.class);

  private final int DEFAULT_SPACING = 2;

  protected TableBodyBox header;

  protected TableBodyBox footer;

  protected Vector<TableBodyBox> bodies;

  protected Vector<TableColumn> columns;

  /** total number of columns in the table */
  protected int columnCount;

  /** cell spacing */
  protected int spacing = 2;

  /** an anonymous table body (for lines that are not in any other body) */
  private TableBodyBox anonbody;

  /** true if the column width have been already calculated */
  private boolean columnsCalculated = false;

  /**
     * Create a new table
     */
  public TableBox(Element n, Graphics2D g, VisualContext ctx) {
    super(n, g, ctx);
    isblock = true;
  }

  /**
     * Create a new table from an inline box
     */
  public TableBox(InlineBox src) {
    super(src);
    isblock = true;
  }

  /**
     * Create a new table from a block box
     */
  public TableBox(BlockBox src) {
    super(src.el, src.g, src.ctx);
    copyValues(src);
    isblock = true;
  }

  /**
     * Determine the number of columns for the whole table
     * @return the column number
     */
  public int getColumnCount() {
    return columnCount;
  }

  @Override public boolean hasFixedWidth() {
    return wset;
  }

  @Override public void initBox() {
    loadTableStyle();
    organizeContent();
  }

  @Override public boolean doLayout(int widthlimit, boolean force, boolean linestart) {
    setAvailableWidth(widthlimit);
    int wlimit = getAvailableContentWidth();
    int maxw = 0;
    int y = 0;
    calculateColumns();
    if (header != null) {
      header.setSpacing(spacing);
      header.doLayout(wlimit, columns);
      header.setPosition(0, y);
      if (header.getWidth() > maxw) {
        maxw = header.getWidth();
      }
      y += header.getHeight();
    }
    for (Iterator<TableBodyBox> it = bodies.iterator(); it.hasNext(); ) {
      TableBodyBox body = it.next();
      body.setSpacing(spacing);
      body.doLayout(wlimit, columns);
      body.setPosition(0, y);
      if (body.getWidth() > maxw) {
        maxw = body.getWidth();
      }
      y += body.getHeight();
    }
    if (footer != null) {
      footer.setSpacing(spacing);
      footer.doLayout(wlimit, columns);
      footer.setPosition(0, y);
      if (footer.getWidth() > maxw) {
        maxw = footer.getWidth();
      }
      y += footer.getHeight();
    }
    content.width = maxw;
    content.height = y;
    setSize(totalWidth(), totalHeight());
    return true;
  }

  @Override protected void loadSizes(boolean update) {
    if (!update) {
      String width = getElement().getAttribute("width");
      if (!width.equals("")) {
        TermLengthOrPercent wspec = HTMLNorm.createLengthOrPercent(width);
        if (wspec != null) {
          Declaration dec = CSSFactory.getRuleFactory().createDeclaration();
          dec.setProperty("width");
          dec.unlock();
          dec.add(wspec);
          dec.setImportant(true);
          style.push(dec);
        }
      }
    }
    super.loadSizes(update);
  }

  /** 
     * Calculates the widths and margins for the table.
     * @param width the specified width
     * @param exact true if this is the exact width, false when it's a max/min width
     * @param contw containing block width
     * @param update <code>true</code>, if we're just updating the size to a new containing block size
     */
  @Override protected void computeWidthsInFlow(TermLengthOrPercent width, boolean auto, boolean exact, int contw, boolean update) {
    CSSDecoder dec = new CSSDecoder(ctx);
    if (cblock == null && cblock.getContainingBlock() != null) {
      log.debug(toString() + " has no cblock");
      return;
    }
    contw = cblock.getContainingBlock().getContentWidth();
    if (width == null) {
      auto = true;
    }
    if (exact) {
      wset = !auto;
    }
    if (wset && exact && width.isPercentage()) {
      wrelative = true;
    }
    preferredWidth = -1;
    margin.left = margin.right = 0;
    if (!columnsCalculated) {
      update = false;
    }
    if (!wset && !update) {
      content.width = contw - border.left - padding.left - padding.right - border.right;
    } else {
      if (!update) {
        content.width = dec.getLength(width, auto, 0, 0, contw);
      }
      preferredWidth = border.left + padding.left + content.width + padding.right + border.right;
    }
  }

  @Override protected void computeHeightsInFlow(TermLengthOrPercent height, boolean auto, boolean exact, int contw, int conth, boolean update) {
    CSSDecoder dec = new CSSDecoder(ctx);
    if (cblock == null && cblock.getContainingBlock() != null) {
      log.debug(toString() + " has no cblock");
      return;
    }
    contw = cblock.getContainingBlock().getContentWidth();
    conth = cblock.getContainingBlock().getContentHeight();
    if (height == null) {
      auto = true;
    }
    margin.top = margin.bottom = 0;
    if (cblock != null && cblock.hset) {
      hset = (exact && !auto && height != null);
      if (!update) {
        content.height = dec.getLength(height, auto, 0, 0, conth);
      }
    } else {
      hset = (exact && !auto && height != null && !height.isPercentage());
      if (!update) {
        content.height = dec.getLength(height, auto, 0, 0, 0);
      }
    }
  }

  @Override protected int getMaximalContentWidth() {
    int ret = 0;
    if (header != null) {
      int m = header.getMaximalWidth();
      if (m > ret) {
        ret = m;
      }
    }
    if (footer != null) {
      int m = footer.getMaximalWidth();
      if (m > ret) {
        ret = m;
      }
    }
    for (Iterator<TableBodyBox> it = bodies.iterator(); it.hasNext(); ) {
      int m = it.next().getMaximalWidth();
      if (m > ret) {
        ret = m;
      }
    }
    return ret;
  }

  @Override protected int getMinimalContentWidth() {
    int ret = 0;
    if (header != null) {
      int m = header.getMinimalWidth();
      if (m > ret) {
        ret = m;
      }
    }
    if (footer != null) {
      int m = footer.getMinimalWidth();
      if (m > ret) {
        ret = m;
      }
    }
    for (Iterator<TableBodyBox> it = bodies.iterator(); it.hasNext(); ) {
      int m = it.next().getMinimalWidth();
      if (m > ret) {
        ret = m;
      }
    }
    return ret;
  }

  @Override protected void drawChildren(DrawStage turn) {
    if (header != null) {
      header.draw(turn);
    }
    for (TableBodyBox body : bodies) {
      body.draw(turn);
    }
    if (footer != null) {
      footer.draw(turn);
    }
  }

  /**
     * Determine the number of columns for the whole table
     */
  public void determineColumnCount() {
    int ret = 0;
    if (header != null) {
      int c = header.getColumnCount();
      if (c > ret) {
        ret = c;
      }
    }
    if (footer != null) {
      int c = footer.getColumnCount();
      if (c > ret) {
        ret = c;
      }
    }
    for (Iterator<TableBodyBox> it = bodies.iterator(); it.hasNext(); ) {
      int c = it.next().getColumnCount();
      if (c > ret) {
        ret = c;
      }
    }
    columnCount = ret;
  }

  /**
     * Analyzes the cells in the body and updates the stored column parametres 
     */
  private void updateColumns(TableBodyBox body) {
    for (int i = 0; i < columns.size(); i++) {
      if (i < body.getColumnCount()) {
        body.updateColumn(i, columns.elementAt(i));
      }
    }
  }

  /**
     * Calculates the column widths.
     */
  private void calculateColumns() {
    int wlimit = getAvailableContentWidth();
    determineColumnCount();
    while (columns.size() < columnCount) {
      columns.add(new TableColumn(TableColumn.createAnonymousColumn(getParent().getElement().getOwnerDocument()), g, ctx));
    }
    if (header != null) {
      updateColumns(header);
    }
    if (footer != null) {
      updateColumns(footer);
    }
    for (Iterator<TableBodyBox> it = bodies.iterator(); it.hasNext(); ) {
      updateColumns(it.next());
    }
    int sumabs = 0;
    int sumperc = 0;
    int mintotalw = 0;
    int sumnonemin = 0;
    int sumnonemax = 0;
    int totalwperc = 0;
    for (TableColumn col : columns) {
      mintotalw += col.getMinimalWidth();
      if (col.wrelative) {
        sumperc += col.percent;
        int maxw = col.getMaximalWidth();
        int newtotal = maxw * 100 / col.percent;
        if (newtotal > totalwperc) {
          totalwperc = newtotal;
        }
      } else {
        if (col.wset) {
          sumabs += Math.max(col.abswidth, col.getMinimalWidth());
        } else {
          sumnonemin += col.getWidth();
          sumnonemax += col.getMaximalWidth();
        }
      }
    }
    if (totalwperc > wlimit) {
      totalwperc = wlimit;
    }
    int totalwabs = 0;
    if (sumabs + sumnonemax > 0) {
      int abspart = 100 - sumperc;
      totalwabs = (abspart == 0) ? wlimit : (sumabs + sumnonemax) * 100 / abspart;
    }
    int totalw = Math.max(totalwperc, totalwabs);
    if (wset) {
      totalw = content.width - (columns.size() + 1) * spacing;
    } else {
      if (totalw > wlimit) {
        totalw = wlimit;
      }
    }
    if (totalw < mintotalw) {
      totalw = mintotalw;
    }
    int remain = totalw - mintotalw;
    if (remain > 0 && sumperc > 0) {
      for (TableColumn col : columns) {
        if (col.wrelative) {
          int mincw = col.getMinimalWidth();
          int neww = col.percent * totalw / 100;
          if (neww < mincw) {
            neww = mincw;
          }
          col.setColumnWidth(neww);
          remain -= (neww - mincw);
        }
      }
    }
    if (remain > 0 && sumabs > 0) {
      for (TableColumn col : columns) {
        if (col.wset && !col.wrelative) {
          int mincw = col.getMinimalWidth();
          int neww = col.abswidth;
          if (neww < mincw) {
            neww = mincw;
          }
          col.setColumnWidth(neww);
          remain -= (neww - mincw);
        }
      }
    }
    if (remain > 0 && sumnonemin > 0 && sumnonemax > 0) {
      int remainmax = sumnonemax;
      remain += sumnonemin;
      for (TableColumn col : columns) {
        if (!col.wset) {
          int mincw = col.getMinimalWidth();
          int neww = remain * col.getMaximalWidth() / remainmax;
          if (neww < mincw) {
            neww = mincw;
          }
          col.setColumnWidth(neww);
          remain -= neww;
          remainmax -= col.getMaximalWidth();
          if (remainmax <= 0 || remain <= 0) {
            break;
          }
        }
      }
    }
    if (remain > 0 && sumabs > 0) {
      int remainabs = sumabs;
      for (TableColumn col : columns) {
        if (col.wset && !col.wrelative) {
          int addw = remain * col.getMaximalWidth() / remainabs;
          col.setColumnWidth(col.getWidth() + addw);
          remain -= addw;
          remainabs -= col.getMaximalWidth();
        }
      }
    }
    if (remain > 0 && sumperc > 0 && sumperc < 100) {
      int remainperc = sumperc;
      for (TableColumn col : columns) {
        if (col.wrelative) {
          int addw = remain * col.percent / remainperc;
          col.setColumnWidth(col.getWidth() + addw);
          remain -= addw;
          remainperc -= col.getMaximalWidth();
          if (remainperc <= 0 || remain <= 0) {
            break;
          }
        }
      }
    }
    if (remain > 0) {
      int remaincols = columns.size();
      for (int i = columns.size() - 1; i >= 0; i--) {
        TableColumn col = columns.elementAt(i);
        int addw = remain / remaincols;
        col.setColumnWidth(col.getWidth() + addw);
        remain -= addw;
        remaincols--;
      }
    }
    if (remain < 0) {
      if (remain < 0 && sumnonemin > 0) {
        int totaldif = 0;
        for (TableColumn col : columns) {
          if (!col.wset) {
            totaldif += col.getWidth() - col.getMinimalWidth();
          }
        }
        for (int i = columns.size() - 1; i >= 0 && totaldif > 0; i--) {
          TableColumn col = columns.elementAt(i);
          if (!col.wset) {
            int dif = col.getWidth() - col.getMinimalWidth();
            int addw = remain * dif / totaldif;
            col.setColumnWidth(col.getWidth() + addw);
            remain -= addw;
            totaldif -= dif;
            if (remain >= 0) {
              break;
            }
          }
        }
      }
      if (remain < 0 && sumabs > 0) {
        int totaldif = 0;
        for (TableColumn col : columns) {
          if (col.wset && !col.wrelative) {
            totaldif += col.getWidth() - col.getMinimalWidth();
          }
        }
        for (int i = columns.size() - 1; i >= 0 && totaldif > 0; i--) {
          TableColumn col = columns.elementAt(i);
          if (col.wset && !col.wrelative) {
            int dif = col.getWidth() - col.getMinimalWidth();
            int addw = remain * dif / totaldif;
            col.setColumnWidth(col.getWidth() + addw);
            remain -= addw;
            totaldif -= dif;
            if (remain >= 0) {
              break;
            }
          }
        }
      }
      if (remain < 0 && sumperc > 0) {
        int totaldif = 0;
        for (TableColumn col : columns) {
          if (col.wrelative) {
            totaldif += col.getWidth() - col.getMinimalWidth();
          }
        }
        for (int i = columns.size() - 1; i >= 0 && totaldif > 0; i--) {
          TableColumn col = columns.elementAt(i);
          if (col.wrelative) {
            int dif = col.getWidth() - col.getMinimalWidth();
            int addw = remain * dif / totaldif;
            col.setColumnWidth(col.getWidth() + addw);
            remain -= addw;
            totaldif -= dif;
            if (remain >= 0) {
              break;
            }
          }
        }
      }
    }
    columnsCalculated = true;
  }

  @Override protected void loadBlockStyle() {
    super.loadBlockStyle();
    position = POS_STATIC;
    floating = FLOAT_NONE;
  }

  /**
     * Loads the table-specific features from the style
     */
  private void loadTableStyle() {
    CSSDecoder dec = new CSSDecoder(ctx);
    TermList spc = style.getValue(TermList.class, "border-spacing");
    if (spc != null) {
      spacing = dec.getLength((TermLength) spc.get(0), false, DEFAULT_SPACING, 0, 0);
    } else {
      spacing = dec.getLength(getLengthValue("border-spacing"), false, DEFAULT_SPACING, 0, 0);
    }
  }

  /**
     * Goes through the list of child boxes and organizes them into captions, header,
     * footer, etc.
     */
  private void organizeContent() {
    bodies = new Vector<TableBodyBox>();
    columns = new Vector<TableColumn>();
    anonbody = null;
    for (Iterator<Box> it = nested.iterator(); it.hasNext(); ) {
      Box box = it.next();
      if (box instanceof ElementBox) {
        ElementBox subbox = (ElementBox) box;
        if (subbox.getDisplay() == ElementBox.DISPLAY_TABLE_HEADER_GROUP) {
          header = (TableBodyBox) subbox;
          header.setOwnerTable(this);
        } else {
          if (subbox.getDisplay() == ElementBox.DISPLAY_TABLE_FOOTER_GROUP) {
            footer = (TableBodyBox) subbox;
            footer.setOwnerTable(this);
          } else {
            if (subbox.getDisplay() == ElementBox.DISPLAY_TABLE_ROW_GROUP) {
              bodies.add((TableBodyBox) subbox);
              ((TableBodyBox) subbox).setOwnerTable(this);
            } else {
              if (subbox.getDisplay() == ElementBox.DISPLAY_TABLE_COLUMN) {
                for (int i = 0; i < ((TableColumn) subbox).getSpan(); i++) {
                  if (i == 0) {
                    columns.add((TableColumn) subbox);
                  } else {
                    columns.add(((TableColumn) subbox).copyBox());
                  }
                }
              } else {
                if (subbox.getDisplay() == ElementBox.DISPLAY_TABLE_COLUMN_GROUP) {
                  for (int i = 0; i < ((TableColumnGroup) subbox).getSpan(); i++) {
                    columns.add(((TableColumnGroup) subbox).getColumn(i));
                  }
                } else {
                  if (anonbody == null) {
                    Element anonelem = viewport.getFactory().createAnonymousElement(getParent().getElement().getOwnerDocument(), "tbody", "table-row-group");
                    anonbody = new TableBodyBox(anonelem, g, ctx);
                    anonbody.adoptParent(this);
                    anonbody.setStyle(viewport.getFactory().createAnonymousStyle("table-row-group"));
                    anonbody.setOwnerTable(this);
                    bodies.add(anonbody);
                  }
                  anonbody.addSubBox(subbox);
                  anonbody.isempty = false;
                  subbox.setContainingBlock(anonbody);
                  subbox.setParent(anonbody);
                  it.remove();
                  endChild--;
                }
              }
            }
          }
        }
      }
    }
    if (anonbody != null) {
      anonbody.endChild = anonbody.nested.size();
      addSubBox(anonbody);
    }
  }
}