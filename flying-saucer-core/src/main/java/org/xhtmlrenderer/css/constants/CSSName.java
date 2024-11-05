package org.xhtmlrenderer.css.constants;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.xhtmlrenderer.css.parser.CSSErrorHandler;
import org.xhtmlrenderer.css.parser.CSSParser;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.BackgroundPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.BorderPropertyBuilders;
import org.xhtmlrenderer.css.parser.property.BorderSpacingPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.ContentPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.CounterPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.FontPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.ListStylePropertyBuilder;
import org.xhtmlrenderer.css.parser.property.OneToFourPropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.parser.property.QuotesPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.SizePropertyBuilder;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.DerivedValueFactory;
import org.xhtmlrenderer.util.XRLog;

public final class CSSName implements Comparable {
  private static final Integer PRIMITIVE = new Integer(0);

  private static final Integer SHORTHAND = new Integer(1);

  private static final Integer INHERITS = new Integer(2);

  private static final Integer NOT_INHERITED = new Integer(3);

  private static int maxAssigned;

  private final String propName;

  private final String initialValue;

  private final boolean propertyInherits;

  private FSDerivedValue initialDerivedValue;

  private final boolean implemented;

  private final PropertyBuilder builder;

  public final int FS_ID;

  private static final CSSName[] ALL_PROPERTIES;

  private static final Map ALL_PROPERTY_NAMES = new TreeMap();

  private static final Map ALL_PRIMITIVE_PROPERTY_NAMES = new TreeMap();

  public final static CSSName COLOR = addProperty("color", PRIMITIVE, "black", INHERITS, new PrimitivePropertyBuilders.Color());

  public final static CSSName BACKGROUND_COLOR = addProperty("background-color", PRIMITIVE, "transparent", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundColor());

  public final static CSSName BACKGROUND_IMAGE = addProperty("background-image", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundImage());

  public final static CSSName BACKGROUND_REPEAT = addProperty("background-repeat", PRIMITIVE, "repeat", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundRepeat());

  public final static CSSName BACKGROUND_ATTACHMENT = addProperty("background-attachment", PRIMITIVE, "scroll", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundAttachment());

  public final static CSSName BACKGROUND_POSITION = addProperty("background-position", PRIMITIVE, "0% 0%", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundPosition());

  public final static CSSName BACKGROUND_SIZE = addProperty("background-size", PRIMITIVE, "auto auto", NOT_INHERITED, new PrimitivePropertyBuilders.BackgroundSize());

  public final static CSSName BORDER_COLLAPSE = addProperty("border-collapse", PRIMITIVE, "separate", INHERITS, new PrimitivePropertyBuilders.BorderCollapse());

  public final static CSSName FS_BORDER_SPACING_HORIZONTAL = addProperty("-fs-border-spacing-horizontal", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.FSBorderSpacingHorizontal());

  public final static CSSName FS_BORDER_SPACING_VERTICAL = addProperty("-fs-border-spacing-vertical", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.FSBorderSpacingVertical());

  public final static CSSName FS_DYNAMIC_AUTO_WIDTH = addProperty("-fs-dynamic-auto-width", PRIMITIVE, "static", NOT_INHERITED, new PrimitivePropertyBuilders.FSDynamicAutoWidth());

  public final static CSSName FS_FONT_METRIC_SRC = addProperty("-fs-font-metric-src", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.FSFontMetricSrc());

  public final static CSSName FS_KEEP_WITH_INLINE = addProperty("-fs-keep-with-inline", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSKeepWithInline());

  public final static CSSName FS_PAGE_WIDTH = addProperty("-fs-page-width", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageWidth());

  public final static CSSName FS_PAGE_HEIGHT = addProperty("-fs-page-height", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageHeight());

  public final static CSSName FS_PAGE_SEQUENCE = addProperty("-fs-page-sequence", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageSequence());

  public final static CSSName FS_PDF_FONT_EMBED = addProperty("-fs-pdf-font-embed", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPDFFontEmbed());

  public final static CSSName FS_PDF_FONT_ENCODING = addProperty("-fs-pdf-font-encoding", PRIMITIVE, "Cp1252", NOT_INHERITED, new PrimitivePropertyBuilders.FSPDFFontEncoding());

  public final static CSSName FS_PAGE_ORIENTATION = addProperty("-fs-page-orientation", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSPageOrientation());

  public final static CSSName FS_TABLE_PAGINATE = addProperty("-fs-table-paginate", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSTablePaginate());

  public final static CSSName FS_TEXT_DECORATION_EXTENT = addProperty("-fs-text-decoration-extent", PRIMITIVE, "line", NOT_INHERITED, new PrimitivePropertyBuilders.FSTextDecorationExtent());

  public final static CSSName FS_FIT_IMAGES_TO_WIDTH = addProperty("-fs-fit-images-to-width", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.FSFitImagesToWidth());

  public final static CSSName FS_NAMED_DESTINATION = addProperty("-fs-named-destination", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.FSNamedDestination());

  public final static CSSName BOTTOM = addProperty("bottom", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Bottom());

  public final static CSSName CAPTION_SIDE = addProperty("caption-side", PRIMITIVE, "top", INHERITS, new PrimitivePropertyBuilders.CaptionSide());

  public final static CSSName CLEAR = addProperty("clear", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.Clear());

  public final static CSSName CLIP = addProperty("clip", PRIMITIVE, "auto", NOT_INHERITED, false, null);

  public final static CSSName CONTENT = addProperty("content", PRIMITIVE, "normal", NOT_INHERITED, new ContentPropertyBuilder());

  public final static CSSName COUNTER_INCREMENT = addProperty("counter-increment", PRIMITIVE, "none", NOT_INHERITED, true, new CounterPropertyBuilder.CounterIncrement());

  public final static CSSName COUNTER_RESET = addProperty("counter-reset", PRIMITIVE, "none", NOT_INHERITED, true, new CounterPropertyBuilder.CounterReset());

  public final static CSSName CURSOR = addProperty("cursor", PRIMITIVE, "auto", INHERITS, true, new PrimitivePropertyBuilders.Cursor());

  public final static CSSName DIRECTION = addProperty("direction", PRIMITIVE, "ltr", INHERITS, false, null);

  public final static CSSName DISPLAY = addProperty("display", PRIMITIVE, "inline", NOT_INHERITED, new PrimitivePropertyBuilders.Display());

  public final static CSSName EMPTY_CELLS = addProperty("empty-cells", PRIMITIVE, "show", INHERITS, new PrimitivePropertyBuilders.EmptyCells());

  public final static CSSName FLOAT = addProperty("float", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.Float());

  public final static CSSName FONT_STYLE = addProperty("font-style", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.FontStyle());

  public final static CSSName FONT_VARIANT = addProperty("font-variant", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.FontVariant());

  public final static CSSName FONT_WEIGHT = addProperty("font-weight", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.FontWeight());

  public final static CSSName FONT_SIZE = addProperty("font-size", PRIMITIVE, "medium", INHERITS, new PrimitivePropertyBuilders.FontSize());

  public final static CSSName LINE_HEIGHT = addProperty("line-height", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.LineHeight());

  public final static CSSName FONT_FAMILY = addProperty("font-family", PRIMITIVE, "serif", INHERITS, new PrimitivePropertyBuilders.FontFamily());

  public final static CSSName FS_COLSPAN = addProperty("-fs-table-cell-colspan", PRIMITIVE, "1", NOT_INHERITED, new PrimitivePropertyBuilders.FSTableCellColspan());

  public final static CSSName FS_ROWSPAN = addProperty("-fs-table-cell-rowspan", PRIMITIVE, "1", NOT_INHERITED, new PrimitivePropertyBuilders.FSTableCellRowspan());

  public final static CSSName HEIGHT = addProperty("height", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Height());

  public final static CSSName LEFT = addProperty("left", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Left());

  public final static CSSName LETTER_SPACING = addProperty("letter-spacing", PRIMITIVE, "normal", INHERITS, true, new PrimitivePropertyBuilders.LetterSpacing());

  public final static CSSName LIST_STYLE_TYPE = addProperty("list-style-type", PRIMITIVE, "disc", INHERITS, new PrimitivePropertyBuilders.ListStyleType());

  public final static CSSName LIST_STYLE_POSITION = addProperty("list-style-position", PRIMITIVE, "outside", INHERITS, new PrimitivePropertyBuilders.ListStylePosition());

  public final static CSSName LIST_STYLE_IMAGE = addProperty("list-style-image", PRIMITIVE, "none", INHERITS, new PrimitivePropertyBuilders.ListStyleImage());

  public final static CSSName MAX_HEIGHT = addProperty("max-height", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.MaxHeight());

  public final static CSSName MAX_WIDTH = addProperty("max-width", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.MaxWidth());

  public final static CSSName MIN_HEIGHT = addProperty("min-height", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MinHeight());

  public final static CSSName MIN_WIDTH = addProperty("min-width", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MinWidth());

  public final static CSSName ORPHANS = addProperty("orphans", PRIMITIVE, "2", INHERITS, true, new PrimitivePropertyBuilders.Orphans());

  public final static CSSName OUTLINE_COLOR = addProperty("outline-color", PRIMITIVE, "black", NOT_INHERITED, false, null);

  public final static CSSName OUTLINE_STYLE = addProperty("outline-style", PRIMITIVE, "none", NOT_INHERITED, false, null);

  public final static CSSName OUTLINE_WIDTH = addProperty("outline-width", PRIMITIVE, "medium", NOT_INHERITED, false, null);

  public final static CSSName OVERFLOW = addProperty("overflow", PRIMITIVE, "visible", NOT_INHERITED, new PrimitivePropertyBuilders.Overflow());

  public final static CSSName PAGE = addProperty("page", PRIMITIVE, "auto", INHERITS, new PrimitivePropertyBuilders.Page());

  public final static CSSName PAGE_BREAK_AFTER = addProperty("page-break-after", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.PageBreakAfter());

  public final static CSSName PAGE_BREAK_BEFORE = addProperty("page-break-before", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.PageBreakBefore());

  public final static CSSName PAGE_BREAK_INSIDE = addProperty("page-break-inside", PRIMITIVE, "auto", INHERITS, new PrimitivePropertyBuilders.PageBreakInside());

  public final static CSSName POSITION = addProperty("position", PRIMITIVE, "static", NOT_INHERITED, new PrimitivePropertyBuilders.Position());

  public final static CSSName QUOTES = addProperty("quotes", PRIMITIVE, "none", INHERITS, new QuotesPropertyBuilder());

  public final static CSSName RIGHT = addProperty("right", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Right());

  public final static CSSName SRC = addProperty("src", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.Src());

  public final static CSSName TAB_SIZE = addProperty("tab-size", PRIMITIVE, "8", INHERITS, new PrimitivePropertyBuilders.TabSize());

  public final static CSSName TABLE_LAYOUT = addProperty("table-layout", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.TableLayout());

  public final static CSSName TEXT_ALIGN = addProperty("text-align", PRIMITIVE, "left", INHERITS, new PrimitivePropertyBuilders.TextAlign());

  public final static CSSName TEXT_DECORATION = addProperty("text-decoration", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.TextDecoration());

  public final static CSSName TEXT_INDENT = addProperty("text-indent", PRIMITIVE, "0", INHERITS, new PrimitivePropertyBuilders.TextIndent());

  public final static CSSName TEXT_TRANSFORM = addProperty("text-transform", PRIMITIVE, "none", INHERITS, new PrimitivePropertyBuilders.TextTransform());

  public final static CSSName TOP = addProperty("top", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Top());

  public final static CSSName UNICODE_BIDI = addProperty("unicode-bidi", PRIMITIVE, "normal", NOT_INHERITED, false, null);

  public final static CSSName VERTICAL_ALIGN = addProperty("vertical-align", PRIMITIVE, "baseline", NOT_INHERITED, new PrimitivePropertyBuilders.VerticalAlign());

  public final static CSSName VISIBILITY = addProperty("visibility", PRIMITIVE, "visible", INHERITS, new PrimitivePropertyBuilders.Visibility());

  public final static CSSName WHITE_SPACE = addProperty("white-space", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.WhiteSpace());

  public final static CSSName WORD_WRAP = addProperty("word-wrap", PRIMITIVE, "normal", INHERITS, new PrimitivePropertyBuilders.WordWrap());

  public final static CSSName WIDOWS = addProperty("widows", PRIMITIVE, "2", INHERITS, true, new PrimitivePropertyBuilders.Widows());

  public final static CSSName WIDTH = addProperty("width", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.Width());

  public final static CSSName WORD_SPACING = addProperty("word-spacing", PRIMITIVE, "normal", INHERITS, true, new PrimitivePropertyBuilders.WordSpacing());

  public final static CSSName Z_INDEX = addProperty("z-index", PRIMITIVE, "auto", NOT_INHERITED, new PrimitivePropertyBuilders.ZIndex());

  public final static CSSName BORDER_TOP_COLOR = addProperty("border-top-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderTopColor());

  public final static CSSName BORDER_RIGHT_COLOR = addProperty("border-right-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftColor());

  public final static CSSName BORDER_BOTTOM_COLOR = addProperty("border-bottom-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderBottomColor());

  public final static CSSName BORDER_LEFT_COLOR = addProperty("border-left-color", PRIMITIVE, "=color", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftColor());

  public final static CSSName BORDER_TOP_STYLE = addProperty("border-top-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderTopStyle());

  public final static CSSName BORDER_RIGHT_STYLE = addProperty("border-right-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderRightStyle());

  public final static CSSName BORDER_BOTTOM_STYLE = addProperty("border-bottom-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderBottomStyle());

  public final static CSSName BORDER_LEFT_STYLE = addProperty("border-left-style", PRIMITIVE, "none", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftStyle());

  public final static CSSName BORDER_TOP_WIDTH = addProperty("border-top-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderTopWidth());

  public final static CSSName BORDER_RIGHT_WIDTH = addProperty("border-right-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderRightWidth());

  public final static CSSName BORDER_BOTTOM_WIDTH = addProperty("border-bottom-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderBottomWidth());

  public final static CSSName BORDER_LEFT_WIDTH = addProperty("border-left-width", PRIMITIVE, "medium", NOT_INHERITED, new PrimitivePropertyBuilders.BorderLeftWidth());

  public final static CSSName MARGIN_TOP = addProperty("margin-top", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginTop());

  public final static CSSName MARGIN_RIGHT = addProperty("margin-right", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginRight());

  public final static CSSName MARGIN_BOTTOM = addProperty("margin-bottom", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginBottom());

  public final static CSSName MARGIN_LEFT = addProperty("margin-left", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.MarginLeft());

  public final static CSSName PADDING_TOP = addProperty("padding-top", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingTop());

  public final static CSSName PADDING_RIGHT = addProperty("padding-right", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingRight());

  public final static CSSName PADDING_BOTTOM = addProperty("padding-bottom", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingBottom());

  public final static CSSName PADDING_LEFT = addProperty("padding-left", PRIMITIVE, "0", NOT_INHERITED, new PrimitivePropertyBuilders.PaddingLeft());

  public final static CSSName BACKGROUND_SHORTHAND = addProperty("background", SHORTHAND, "transparent none repeat scroll 0% 0%", NOT_INHERITED, new BackgroundPropertyBuilder());

  public final static CSSName BORDER_WIDTH_SHORTHAND = addProperty("border-width", SHORTHAND, "medium", NOT_INHERITED, new OneToFourPropertyBuilders.BorderWidth());

  public final static CSSName BORDER_STYLE_SHORTHAND = addProperty("border-style", SHORTHAND, "none", NOT_INHERITED, new OneToFourPropertyBuilders.BorderStyle());

  public final static CSSName BORDER_SHORTHAND = addProperty("border", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.Border());

  public final static CSSName BORDER_TOP_SHORTHAND = addProperty("border-top", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderTop());

  public final static CSSName BORDER_RIGHT_SHORTHAND = addProperty("border-right", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderRight());

  public final static CSSName BORDER_BOTTOM_SHORTHAND = addProperty("border-bottom", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderBottom());

  public final static CSSName BORDER_LEFT_SHORTHAND = addProperty("border-left", SHORTHAND, "medium none black", NOT_INHERITED, new BorderPropertyBuilders.BorderLeft());

  public final static CSSName BORDER_COLOR_SHORTHAND = addProperty("border-color", SHORTHAND, "black", NOT_INHERITED, new OneToFourPropertyBuilders.BorderColor());

  public final static CSSName BORDER_SPACING = addProperty("border-spacing", SHORTHAND, "0", INHERITS, new BorderSpacingPropertyBuilder());

  public final static CSSName FONT_SHORTHAND = addProperty("font", SHORTHAND, "", INHERITS, new FontPropertyBuilder());

  public final static CSSName LIST_STYLE_SHORTHAND = addProperty("list-style", SHORTHAND, "disc outside none", INHERITS, new ListStylePropertyBuilder());

  public final static CSSName MARGIN_SHORTHAND = addProperty("margin", SHORTHAND, "0", NOT_INHERITED, new OneToFourPropertyBuilders.Margin());

  public final static CSSName OUTLINE_SHORTHAND = addProperty("outline", SHORTHAND, "invert none medium", NOT_INHERITED, false, null);

  public final static CSSName PADDING_SHORTHAND = addProperty("padding", SHORTHAND, "0", NOT_INHERITED, new OneToFourPropertyBuilders.Padding());

  public final static CSSName SIZE_SHORTHAND = addProperty("size", SHORTHAND, "auto", NOT_INHERITED, new SizePropertyBuilder());

  public final static CSSSideProperties MARGIN_SIDE_PROPERTIES = new CSSSideProperties(CSSName.MARGIN_TOP, CSSName.MARGIN_RIGHT, CSSName.MARGIN_BOTTOM, CSSName.MARGIN_LEFT);

  public final static CSSSideProperties PADDING_SIDE_PROPERTIES = new CSSSideProperties(CSSName.PADDING_TOP, CSSName.PADDING_RIGHT, CSSName.PADDING_BOTTOM, CSSName.PADDING_LEFT);

  public final static CSSSideProperties BORDER_SIDE_PROPERTIES = new CSSSideProperties(CSSName.BORDER_TOP_WIDTH, CSSName.BORDER_RIGHT_WIDTH, CSSName.BORDER_BOTTOM_WIDTH, CSSName.BORDER_LEFT_WIDTH);

  public final static CSSSideProperties BORDER_STYLE_PROPERTIES = new CSSSideProperties(CSSName.BORDER_TOP_STYLE, CSSName.BORDER_RIGHT_STYLE, CSSName.BORDER_BOTTOM_STYLE, CSSName.BORDER_LEFT_STYLE);

  public final static CSSSideProperties BORDER_COLOR_PROPERTIES = new CSSSideProperties(CSSName.BORDER_TOP_COLOR, CSSName.BORDER_RIGHT_COLOR, CSSName.BORDER_BOTTOM_COLOR, CSSName.BORDER_LEFT_COLOR);

  private CSSName(String propName, String initialValue, boolean inherits, boolean implemented, PropertyBuilder builder) {
    this.propName = propName;
    this.FS_ID = CSSName.maxAssigned++;
    this.initialValue = initialValue;
    this.propertyInherits = inherits;
    this.implemented = implemented;
    this.builder = builder;
  }

  public String toString() {
    return this.propName;
  }

  public static int countCSSNames() {
    return CSSName.maxAssigned;
  }

  public static int countCSSPrimitiveNames() {
    return ALL_PRIMITIVE_PROPERTY_NAMES.size();
  }

  public static Iterator allCSS2PropertyNames() {
    return ALL_PROPERTY_NAMES.keySet().iterator();
  }

  public static Iterator allCSS2PrimitivePropertyNames() {
    return ALL_PRIMITIVE_PROPERTY_NAMES.keySet().iterator();
  }

  public static boolean propertyInherits(CSSName cssName) {
    return cssName.propertyInherits;
  }

  public static String initialValue(CSSName cssName) {
    return cssName.initialValue;
  }

  public static FSDerivedValue initialDerivedValue(CSSName cssName) {
    return cssName.initialDerivedValue;
  }

  public static boolean isImplemented(CSSName cssName) {
    return cssName.implemented;
  }

  public static PropertyBuilder getPropertyBuilder(CSSName cssName) {
    return cssName.builder;
  }

  public static CSSName getByPropertyName(String propName) {
    return (CSSName) ALL_PROPERTY_NAMES.get(propName);
  }

  public static CSSName getByID(int id) {
    return ALL_PROPERTIES[id];
  }

  private static synchronized CSSName addProperty(String propName, Object type, String initialValue, Object inherit, PropertyBuilder builder) {
    return addProperty(propName, type, initialValue, inherit, true, builder);
  }

  private static synchronized CSSName addProperty(String propName, Object type, String initialValue, Object inherit, boolean implemented, PropertyBuilder builder) {
    CSSName cssName = new CSSName(propName, initialValue, (inherit == INHERITS), implemented, builder);
    ALL_PROPERTY_NAMES.put(propName, cssName);
    if (type == PRIMITIVE) {
      ALL_PRIMITIVE_PROPERTY_NAMES.put(propName, cssName);
    }
    return cssName;
  }

  static {
    Iterator iter = ALL_PROPERTY_NAMES.values().iterator();
    ALL_PROPERTIES = new CSSName[ALL_PROPERTY_NAMES.size()];
    while (iter.hasNext()) {
      CSSName name = (CSSName) iter.next();
      ALL_PROPERTIES[name.FS_ID] = name;
    }
  }

  static {
    CSSParser parser = new CSSParser(new CSSErrorHandler() {
      public void error(String uri, String message) {
        XRLog.cssParse("(" + uri + ") " + message);
      }
    });
    for (Iterator i = ALL_PRIMITIVE_PROPERTY_NAMES.values().iterator(); i.hasNext(); ) {
      CSSName cssName = (CSSName) i.next();
      if (cssName.initialValue.charAt(0) != '=' && cssName.implemented) {
        PropertyValue value = parser.parsePropertyValue(cssName, StylesheetInfo.USER_AGENT, cssName.initialValue);
        if (value == null) {
          XRLog.exception("Unable to derive initial value for " + cssName);
        } else {
          cssName.initialDerivedValue = DerivedValueFactory.newDerivedValue(null, cssName, value);
        }
      }
    }
  }

  public int compareTo(Object object) {
    if (object == null) {
      throw new NullPointerException();
    }
    return FS_ID - ((CSSName) object).FS_ID;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CSSName)) {
      return false;
    }
    CSSName cssName = (CSSName) o;
    return FS_ID == cssName.FS_ID;
  }

  public int hashCode() {
    return FS_ID;
  }

  public static class CSSSideProperties {
    public final CSSName top;

    public final CSSName right;

    public final CSSName bottom;

    public final CSSName left;

    public CSSSideProperties(CSSName top, CSSName right, CSSName bottom, CSSName left) {
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      this.left = left;
    }
  }

  public final static CSSName BORDER_TOP_LEFT_RADIUS = addProperty("border-top-left-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderTopLeftRadius());

  public final static CSSName BORDER_TOP_RIGHT_RADIUS = addProperty("border-top-right-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderTopRightRadius());

  public final static CSSName BORDER_BOTTOM_RIGHT_RADIUS = addProperty("border-bottom-right-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderBottomRightRadius());

  public final static CSSName BORDER_BOTTOM_LEFT_RADIUS = addProperty("border-bottom-left-radius", PRIMITIVE, "0 0", NOT_INHERITED, true, new PrimitivePropertyBuilders.BorderBottomLeftRadius());

  public final static CSSName BORDER_RADIUS_SHORTHAND = addProperty("border-radius", SHORTHAND, "0px", NOT_INHERITED, true, new OneToFourPropertyBuilders.BorderRadius());
}