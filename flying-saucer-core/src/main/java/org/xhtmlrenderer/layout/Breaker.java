package org.xhtmlrenderer.layout;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.render.FSFont;

/**
 * A utility class that scans the text of a single inline box, looking for the 
 * next break point.
 * @author Torbjoern Gannholm
 */
public class Breaker {
  public static void breakFirstLetter(LayoutContext c, LineBreakContext context, int avail, CalculatedStyle style) {
    FSFont font = style.getFSFont(c);
    context.setEnd(getFirstLetterEnd(context.getMaster(), context.getStart()));
    context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
    if (context.getWidth() > avail) {
      context.setNeedsNewLine(true);
      context.setUnbreakable(true);
    }
  }

  private static int getFirstLetterEnd(String text, int start) {
    boolean letterFound = false;
    int end = text.length();
    char currentChar;
    for (int i = start; i < end; i++) {
      currentChar = text.charAt(i);
      if (!TextUtil.isFirstLetterSeparatorChar(currentChar)) {
        if (letterFound) {
          return i;
        } else {
          letterFound = true;
        }
      }
    }
    return end;
  }

  public static void breakText(LayoutContext c, LineBreakContext context, int avail, CalculatedStyle style) {
    FSFont font = style.getFSFont(c);
    IdentValue whitespace = style.getWhitespace();
    IdentValue wordwrap = style.getWordWrap();
    boolean breakWord = (wordwrap == IdentValue.BREAK_WORD);
    if (whitespace == IdentValue.NOWRAP) {
      context.setEnd(context.getLast());
      context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
      return;
    }
    if (whitespace == IdentValue.PRE || whitespace == IdentValue.PRE_WRAP || whitespace == IdentValue.PRE_LINE) {
      int n = context.getStartSubstring().indexOf(WhitespaceStripper.EOL);
      if (n > -1) {
        context.setEnd(context.getStart() + n + 1);
        context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
        context.setNeedsNewLine(true);
        context.setEndsOnNL(true);
      } else {
        if (whitespace == IdentValue.PRE) {
          context.setEnd(context.getLast());
          context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
        }
      }
    }
    if (whitespace == IdentValue.PRE || (context.isNeedsNewLine() && context.getWidth() <= avail)) {
      return;
    }
    context.setEndsOnNL(false);
    doBreakText(c, context, avail, style, breakWord);
  }

  private static void doBreakText(LayoutContext c, LineBreakContext context, int avail, CalculatedStyle style, boolean tryToBreakAnywhere) {
    FSFont font = style.getFSFont(c);
    String currentString = context.getStartSubstring();
    int left = 0;
    int right = tryToBreakAnywhere ? 1 : currentString.indexOf(WhitespaceStripper.SPACE, left + 1);
    int lastWrap = 0;
    int graphicsLength = 0;
    int lastGraphicsLength = 0;
    while (right > 0 && graphicsLength <= avail) {
      lastGraphicsLength = graphicsLength;
      graphicsLength += c.getTextRenderer().getWidth(c.getFontContext(), font, currentString.substring(left, right));
      lastWrap = left;
      left = right;
      if (tryToBreakAnywhere) {
        right = (right + 1) % currentString.length();
      } else {
        right = currentString.indexOf(WhitespaceStripper.SPACE, left + 1);
      }
    }
    if (graphicsLength <= avail) {
      lastWrap = left;
      lastGraphicsLength = graphicsLength;
      graphicsLength += c.getTextRenderer().getWidth(c.getFontContext(), font, currentString.substring(left));
    }
    if (graphicsLength <= avail) {
      context.setWidth(graphicsLength);
      context.setEnd(context.getMaster().length());
      return;
    }
    context.setNeedsNewLine(true);
    if (lastWrap == 0 && style.getWordWrap() == IdentValue.BREAK_WORD) {
      if (!tryToBreakAnywhere) {
        doBreakText(c, context, avail, style, true);
        return;
      }
    }
    if (lastWrap != 0) {
      context.setEnd(context.getStart() + lastWrap);
      context.setWidth(lastGraphicsLength);
    } else {
      if (left == 0) {
        left = currentString.length();
      }
      context.setEnd(context.getStart() + left);
      context.setUnbreakable(true);
      if (left == currentString.length()) {
        context.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), font, context.getCalculatedSubstring()));
      } else {
        context.setWidth(graphicsLength);
      }
    }
    return;
  }
}