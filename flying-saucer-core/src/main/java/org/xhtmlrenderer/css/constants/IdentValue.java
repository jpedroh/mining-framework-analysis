package org.xhtmlrenderer.css.constants;
import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.util.XRRuntimeException;

/**
 * An IdentValue represents a string that you can assign to a CSS property,
 * where the string is one of several enumerated values. For example,
 * "whitespace" can take the values "nowrap", "pre" and "normal". There is a
 * static instance for all idents in the CSS 2 spec, which you can retrieve
 * using the {@link #getByIdentString(String)} method. The instance doesn't have
 * any behavior: it's just a marker so that you can retrieve an ident from a
 * DerivedValue or CalculatedStyle, then compare to the instance here. For
 * example: <pre>
 * CalculatedStyle style = ...getstyle from somewhere
 * IdentValue whitespace = style.getIdent(CSSName.WHITESPACE);
 * if ( whitespace == IdentValue.NORMAL ) {
 *      // perform normal spacing
 * } else if ( whitespace == IdentValue.NOWRAP ) {
 *      // space with no wrapping
 * } else if ( whitespace == IdentValue.PRE ) {
 *      // preserve spacing
 * }
 * </pre> All static instances are instantiated automatically, and are
 * Singletons, so you can compare using a simple Object comparison using <code>==</code>
 * .
 *
 * @author Patrick Wright
 */
public class IdentValue implements FSDerivedValue {
  private static int maxAssigned = 0;

  /**
     * Description of the Field
     */
  private final String ident;

  public final int FS_ID;

  public final static IdentValue ABSOLUTE = addValue("absolute");

  public final static IdentValue ALWAYS = addValue("always");

  public final static IdentValue ARMENIAN = addValue("armenian");

  public final static IdentValue AUTO = addValue("auto");

  public final static IdentValue AVOID = addValue("avoid");

  public final static IdentValue BASELINE = addValue("baseline");

  public final static IdentValue BLINK = addValue("blink");

  public final static IdentValue BLOCK = addValue("block");

  public final static IdentValue BOLD = addValue("bold");

  public final static IdentValue BOLDER = addValue("bolder");

  public final static IdentValue BOTH = addValue("both");

  public final static IdentValue BOTTOM = addValue("bottom");

  public final static IdentValue CAPITALIZE = addValue("capitalize");

  public final static IdentValue CENTER = addValue("center");

  public final static IdentValue CIRCLE = addValue("circle");

  public final static IdentValue CJK_IDEOGRAPHIC = addValue("cjk-ideographic");

  public final static IdentValue CLOSE_QUOTE = addValue("close-quote");

  public final static IdentValue COLLAPSE = addValue("collapse");

  public final static IdentValue COMPACT = addValue("compact");

  public final static IdentValue CONTAIN = addValue("contain");

  public final static IdentValue COVER = addValue("cover");

  public final static IdentValue CREATE = addValue("create");

  public final static IdentValue DASHED = addValue("dashed");

  public final static IdentValue DECIMAL = addValue("decimal");

  public final static IdentValue DECIMAL_LEADING_ZERO = addValue("decimal-leading-zero");

  public final static IdentValue DISC = addValue("disc");

  public final static IdentValue DOTTED = addValue("dotted");

  public final static IdentValue DOUBLE = addValue("double");

  public final static IdentValue DYNAMIC = addValue("dynamic");

  public final static IdentValue FIXED = addValue("fixed");

  public final static IdentValue FONT_WEIGHT_100 = addValue("100");

  public final static IdentValue FONT_WEIGHT_200 = addValue("200");

  public final static IdentValue FONT_WEIGHT_300 = addValue("300");

  public final static IdentValue FONT_WEIGHT_400 = addValue("400");

  public final static IdentValue FONT_WEIGHT_500 = addValue("500");

  public final static IdentValue FONT_WEIGHT_600 = addValue("600");

  public final static IdentValue FONT_WEIGHT_700 = addValue("700");

  public final static IdentValue FONT_WEIGHT_800 = addValue("800");

  public final static IdentValue FONT_WEIGHT_900 = addValue("900");

  public final static IdentValue FS_CONTENT_PLACEHOLDER = addValue("-fs-content-placeholder");

  public final static IdentValue FS_INITIAL_VALUE = addValue("-fs-initial-value");

  public final static IdentValue GEORGIAN = addValue("georgian");

  public final static IdentValue GROOVE = addValue("groove");

  public final static IdentValue HEBREW = addValue("hebrew");

  public final static IdentValue HIDDEN = addValue("hidden");

  public final static IdentValue HIDE = addValue("hide");

  public final static IdentValue HIRAGANA = addValue("hiragana");

  public final static IdentValue HIRAGANA_IROHA = addValue("hiragana-iroha");

  public final static IdentValue INHERIT = addValue("inherit");

  public final static IdentValue INLINE = addValue("inline");

  public final static IdentValue INLINE_BLOCK = addValue("inline-block");

  public final static IdentValue INLINE_TABLE = addValue("inline-table");

  public final static IdentValue INSET = addValue("inset");

  public final static IdentValue INSIDE = addValue("inside");

  public final static IdentValue ITALIC = addValue("italic");

  public final static IdentValue JUSTIFY = addValue("justify");

  public final static IdentValue KATAKANA = addValue("katakana");

  public final static IdentValue KATAKANA_IROHA = addValue("katakana-iroha");

  public final static IdentValue KEEP = addValue("keep");

  public final static IdentValue LANDSCAPE = addValue("landscape");

  public final static IdentValue LEFT = addValue("left");

  public final static IdentValue LIGHTER = addValue("lighter");

  public final static IdentValue LINE = addValue("line");

  public final static IdentValue LINEAR_GRADIENT = addValue("linear-gradient");

  public final static IdentValue LINE_THROUGH = addValue("line-through");

  public final static IdentValue LIST_ITEM = addValue("list-item");

  public final static IdentValue LOWER_ALPHA = addValue("lower-alpha");

  public final static IdentValue LOWER_GREEK = addValue("lower-greek");

  public final static IdentValue LOWER_LATIN = addValue("lower-latin");

  public final static IdentValue LOWER_ROMAN = addValue("lower-roman");

  public final static IdentValue LOWERCASE = addValue("lowercase");

  public final static IdentValue LTR = addValue("ltr");

  public final static IdentValue MARKER = addValue("marker");

  public final static IdentValue MIDDLE = addValue("middle");

  public final static IdentValue NO_CLOSE_QUOTE = addValue("no-close-quote");

  public final static IdentValue NO_OPEN_QUOTE = addValue("no-open-quote");

  public final static IdentValue NO_REPEAT = addValue("no-repeat");

  public final static IdentValue NONE = addValue("none");

  public final static IdentValue NORMAL = addValue("normal");

  public final static IdentValue NOWRAP = addValue("nowrap");

  public final static IdentValue BREAK_WORD = addValue("break-word");

  public final static IdentValue OBLIQUE = addValue("oblique");

  public final static IdentValue OPEN_QUOTE = addValue("open-quote");

  public final static IdentValue OUTSET = addValue("outset");

  public final static IdentValue OUTSIDE = addValue("outside");

  public final static IdentValue OVERLINE = addValue("overline");

  public final static IdentValue PAGINATE = addValue("paginate");

  public final static IdentValue POINTER = addValue("pointer");

  public final static IdentValue PORTRAIT = addValue("portrait");

  public final static IdentValue PRE = addValue("pre");

  public final static IdentValue PRE_LINE = addValue("pre-line");

  public final static IdentValue PRE_WRAP = addValue("pre-wrap");

  public final static IdentValue RELATIVE = addValue("relative");

  public final static IdentValue REPEAT = addValue("repeat");

  public final static IdentValue REPEAT_X = addValue("repeat-x");

  public final static IdentValue REPEAT_Y = addValue("repeat-y");

  public final static IdentValue RIDGE = addValue("ridge");

  public final static IdentValue RIGHT = addValue("right");

  public final static IdentValue RUN_IN = addValue("run-in");

  public final static IdentValue SCROLL = addValue("scroll");

  public final static IdentValue SEPARATE = addValue("separate");

  public final static IdentValue SHOW = addValue("show");

  public final static IdentValue SMALL_CAPS = addValue("small-caps");

  public final static IdentValue SOLID = addValue("solid");

  public final static IdentValue SQUARE = addValue("square");

  public final static IdentValue STATIC = addValue("static");

  public final static IdentValue SUB = addValue("sub");

  public final static IdentValue SUPER = addValue("super");

  public final static IdentValue TABLE = addValue("table");

  public final static IdentValue TABLE_CAPTION = addValue("table-caption");

  public final static IdentValue TABLE_CELL = addValue("table-cell");

  public final static IdentValue TABLE_COLUMN = addValue("table-column");

  public final static IdentValue TABLE_COLUMN_GROUP = addValue("table-column-group");

  public final static IdentValue TABLE_FOOTER_GROUP = addValue("table-footer-group");

  public final static IdentValue TABLE_HEADER_GROUP = addValue("table-header-group");

  public final static IdentValue TABLE_ROW = addValue("table-row");

  public final static IdentValue TABLE_ROW_GROUP = addValue("table-row-group");

  public final static IdentValue TEXT_BOTTOM = addValue("text-bottom");

  public final static IdentValue TEXT_TOP = addValue("text-top");

  public final static IdentValue THICK = addValue("thick");

  public final static IdentValue THIN = addValue("thin");

  public final static IdentValue TOP = addValue("top");

  public final static IdentValue TRANSPARENT = addValue("transparent");

  public final static IdentValue UNDERLINE = addValue("underline");

  public final static IdentValue UPPER_ALPHA = addValue("upper-alpha");

  public final static IdentValue UPPER_LATIN = addValue("upper-latin");

  public final static IdentValue UPPER_ROMAN = addValue("upper-roman");

  public final static IdentValue UPPERCASE = addValue("uppercase");

  public final static IdentValue VISIBLE = addValue("visible");

  public final static IdentValue CROSSHAIR = addValue("crosshair");

  public final static IdentValue DEFAULT = addValue("default");

  public final static IdentValue EMBED = addValue("embed");

  public final static IdentValue E_RESIZE = addValue("e-resize");

  public final static IdentValue HELP = addValue("help");

  public final static IdentValue LARGE = addValue("large");

  public final static IdentValue LARGER = addValue("larger");

  public final static IdentValue MEDIUM = addValue("medium");

  public final static IdentValue MOVE = addValue("move");

  public final static IdentValue N_RESIZE = addValue("n-resize");

  public final static IdentValue NE_RESIZE = addValue("ne-resize");

  public final static IdentValue NW_RESIZE = addValue("nw-resize");

  public final static IdentValue PROGRESS = addValue("progress");

  public final static IdentValue S_RESIZE = addValue("s-resize");

  public final static IdentValue SE_RESIZE = addValue("se-resize");

  public final static IdentValue SMALL = addValue("small");

  public final static IdentValue SMALLER = addValue("smaller");

  public final static IdentValue START = addValue("start");

  public final static IdentValue SW_RESIZE = addValue("sw-resize");

  public final static IdentValue TEXT = addValue("text");

  public final static IdentValue W_RESIZE = addValue("w-resize");

  public final static IdentValue WAIT = addValue("wait");

  public final static IdentValue X_LARGE = addValue("x-large");

  public final static IdentValue X_SMALL = addValue("x-small");

  public final static IdentValue XX_LARGE = addValue("xx-large");

  public final static IdentValue XX_SMALL = addValue("xx-small");

  /**
     * Description of the Field
     */
  private static Map ALL_IDENT_VALUES;

  /**
     * Constructor for the IdentValue object
     *
     * @param ident PARAM
     */
  private IdentValue(String ident) {
    this.ident = ident;
    this.FS_ID = IdentValue.maxAssigned++;
  }

  /**
     * Returns a string representation of the object, in this case, the ident as
     * a string (as it appears in the CSS spec).
     *
     * @return a string representation of the object.
     */
  public String toString() {
    return ident;
  }

  /**
     * Returns the Singleton IdentValue that corresponds to the given string,
     * e.g. for "normal" will return IdentValue.NORMAL. Use this when you have
     * the string but need to look up the Singleton. If the string doesn't match
     * an ident in the CSS spec, a runtime exception is thrown.
     *
     * @param ident The identifier to retrieve the Singleton IdentValue for.
     * @return see desc.
     */
  public static IdentValue getByIdentString(String ident) {
    IdentValue val = (IdentValue) ALL_IDENT_VALUES.get(ident);
    if (val == null) {
      throw new XRRuntimeException("Ident named " + ident + " has no IdentValue instance assigned to it.");
    }
    return val;
  }

  /**
     * TODO: doc
     */
  public static boolean looksLikeIdent(String ident) {
    return (IdentValue) ALL_IDENT_VALUES.get(ident) != null;
  }

  public static IdentValue valueOf(String ident) {
    return (IdentValue) ALL_IDENT_VALUES.get(ident);
  }

  public static int getIdentCount() {
    return ALL_IDENT_VALUES.size();
  }

  /**
     * Adds a feature to the Value attribute of the IdentValue class
     *
     * @param ident The feature to be added to the Value attribute
     * @return Returns
     */
  private final static synchronized IdentValue addValue(String ident) {
    if (ALL_IDENT_VALUES == null) {
      ALL_IDENT_VALUES = new HashMap();
    }
    IdentValue val = new IdentValue(ident);
    ALL_IDENT_VALUES.put(ident, val);
    return val;
  }

  public boolean isDeclaredInherit() {
    return this == INHERIT;
  }

  public FSDerivedValue computedValue() {
    return this;
  }

  public float asFloat() {
    throw new XRRuntimeException("Ident value is never a float; wrong class used for derived value.");
  }

  public FSColor asColor() {
    throw new XRRuntimeException("Ident value is never a color; wrong class used for derived value.");
  }

  public float getFloatProportionalTo(CSSName cssName, float baseValue, CssContext ctx) {
    throw new XRRuntimeException("Ident value (" + toString() + ") is never a length; wrong class used for derived value.");
  }

  public String asString() {
    return toString();
  }

  public String[] asStringArray() {
    throw new XRRuntimeException("Ident value is never a string array; wrong class used for derived value.");
  }

  public IdentValue asIdentValue() {
    return this;
  }

  public boolean hasAbsoluteUnit() {
    throw new XRRuntimeException("Ident value is never an absolute unit; wrong class used for derived value; this " + "ident value is a " + this.asString());
  }

  public boolean isIdent() {
    return true;
  }

  public boolean isDependentOnFontSize() {
    return false;
  }
}