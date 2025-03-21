package org.apache.fop.layoutmgr.inline;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.area.Trait;
import org.apache.fop.area.inline.TextArea;
import org.apache.fop.fo.Constants;
import org.apache.fop.fo.FOText;
import org.apache.fop.fonts.Font;
import org.apache.fop.fonts.FontSelector;
import org.apache.fop.fonts.GlyphMapping;
import org.apache.fop.layoutmgr.InlineKnuthSequence;
import org.apache.fop.layoutmgr.KnuthBox;
import org.apache.fop.layoutmgr.KnuthElement;
import org.apache.fop.layoutmgr.KnuthGlue;
import org.apache.fop.layoutmgr.KnuthPenalty;
import org.apache.fop.layoutmgr.KnuthSequence;
import org.apache.fop.layoutmgr.LayoutContext;
import org.apache.fop.layoutmgr.LeafPosition;
import org.apache.fop.layoutmgr.Position;
import org.apache.fop.layoutmgr.PositionIterator;
import org.apache.fop.layoutmgr.TraitSetter;
import org.apache.fop.text.linebreak.LineBreakStatus;
import org.apache.fop.traits.MinOptMax;
import org.apache.fop.traits.SpaceVal;
import org.apache.fop.util.CharUtilities;
import org.apache.fop.util.ListUtil;

/**
 * LayoutManager for text (a sequence of characters) which generates one
 * or more inline areas.
 */
public class TextLayoutManager extends LeafNodeLayoutManager {
  private static final int SOFT_HYPHEN_PENALTY = 1;

  private final class PendingChange {
    private final GlyphMapping mapping;

    private final int index;

    private PendingChange(final GlyphMapping mapping, final int index) {
      this.mapping = mapping;
      this.index = index;
    }
  }

  /**
     * logging instance
     */
  private static final Log LOG = LogFactory.getLog(TextLayoutManager.class);

  private final List<GlyphMapping> mappings;

  /** Non-space characters on which we can end a line. */
  private static final String BREAK_CHARS = "-/";

  private final FOText foText;

  /**
     * Contains an array of widths to adjust for kerning. The first entry can
     * be used to influence the start position of the first letter. The entry i+1 defines the
     * cursor advancement after the character i. A null entry means no special advancement.
     */
  private final MinOptMax[] letterSpaceAdjustArray;

  /** Font used for the space between words. */
  private Font spaceFont;

  /** Start index of next TextArea */
  private int nextStart;

  /** size of a space character (U+0020) glyph in current font */
  private int spaceCharIPD;

  private MinOptMax wordSpaceIPD;

  private MinOptMax letterSpaceIPD;

  /** size of the hyphen character glyph in current font */
  private int hyphIPD;

  private boolean hasChanged;

  private int[] returnedIndices = { 0, 0 };

  private int changeOffset;

  private int thisStart;

  private int tempStart;

  private List changeList = new LinkedList();

  private AlignmentContext alignmentContext;

  /**
     * The width to be reserved for border and padding at the start of the line.
     */
  private int lineStartBAP;

  /**
     * The width to be reserved for border and padding at the end of the line.
     */
  private int lineEndBAP;

  private boolean keepTogether;

  private final Position auxiliaryPosition = new LeafPosition(this, -1);

  /**
     * Create a Text layout manager.
     *
     * @param node The FOText object to be rendered
     */
  public TextLayoutManager(FOText node) {
    foText = node;
    letterSpaceAdjustArray = new MinOptMax[node.length() + 1];
    mappings = new ArrayList<GlyphMapping>();
  }

  private KnuthPenalty makeZeroWidthPenalty(int penaltyValue) {
    return new KnuthPenalty(0, penaltyValue, false, auxiliaryPosition, true);
  }

  private KnuthBox makeAuxiliaryZeroWidthBox() {
    return new KnuthInlineBox(0, null, notifyPos(new LeafPosition(this, -1)), true);
  }

  /** {@inheritDoc} */
  public void initialize() {
    foText.resetBuffer();
    spaceFont = FontSelector.selectFontForCharacterInText(' ', foText, this);
    spaceCharIPD = spaceFont.getCharWidth(' ');
    hyphIPD = foText.getCommonHyphenation().getHyphIPD(spaceFont);
    SpaceVal letterSpacing = SpaceVal.makeLetterSpacing(foText.getLetterSpacing());
    SpaceVal wordSpacing = SpaceVal.makeWordSpacing(foText.getWordSpacing(), letterSpacing, spaceFont);
    letterSpaceIPD = letterSpacing.getSpace();
    wordSpaceIPD = MinOptMax.getInstance(spaceCharIPD).plus(wordSpacing.getSpace());
    keepTogether = foText.getKeepTogether().getWithinLine().getEnum() == Constants.EN_ALWAYS;
  }

  /**
     * Generate and add areas to parent area.
     * This can either generate an area for each TextArea and each space, or
     * an area containing all text with a parameter controlling the size of
     * the word space. The latter is most efficient for PDF generation.
     * Set size of each area.
     * @param posIter Iterator over Position information returned
     * by this LayoutManager.
     * @param context LayoutContext for adjustments
     */
  public void addAreas(final PositionIterator posIter, final LayoutContext context) {
    GlyphMapping mapping;
    int wordSpaceCount = 0;
    int letterSpaceCount = 0;
    int firstMappingIndex = -1;
    int lastMappingIndex = 0;
    MinOptMax realWidth = MinOptMax.ZERO;
    GlyphMapping lastMapping = null;
    while (posIter.hasNext()) {
      Position nextPos = posIter.next();
      assert (nextPos instanceof LeafPosition);
      final LeafPosition tbpNext = (LeafPosition) nextPos;
      if (tbpNext == null) {
        continue;
      }
      if (tbpNext.getLeafPos() != -1) {
        mapping = mappings.get(tbpNext.getLeafPos());
        if (lastMapping == null || (mapping.font != lastMapping.font) || (mapping.level != lastMapping.level)) {
          if (lastMapping != null) {
            addMappingAreas(lastMapping, wordSpaceCount, letterSpaceCount, firstMappingIndex, lastMappingIndex, realWidth, context);
          }
          firstMappingIndex = tbpNext.getLeafPos();
          wordSpaceCount = 0;
          letterSpaceCount = 0;
          realWidth = MinOptMax.ZERO;
        }
        wordSpaceCount += mapping.wordSpaceCount;
        letterSpaceCount += mapping.letterSpaceCount;
        realWidth = realWidth.plus(mapping.areaIPD);
        lastMappingIndex = tbpNext.getLeafPos();
        lastMapping = mapping;
      }
    }
    if (lastMapping != null) {
      addMappingAreas(lastMapping, wordSpaceCount, letterSpaceCount, firstMappingIndex, lastMappingIndex, realWidth, context);
    }
  }

  private void addMappingAreas(GlyphMapping mapping, int wordSpaceCount, int letterSpaceCount, int firstMappingIndex, int lastMappingIndex, MinOptMax realWidth, LayoutContext context) {
    int textLength = mapping.getWordLength();
    if (mapping.letterSpaceCount == textLength && !mapping.isHyphenated && context.isLastArea()) {
      realWidth = realWidth.minus(letterSpaceIPD);
      letterSpaceCount--;
    }
    for (int i = mapping.startIndex; i < mapping.endIndex; i++) {
      MinOptMax letterSpaceAdjustment = letterSpaceAdjustArray[i + 1];
      if (letterSpaceAdjustment != null && letterSpaceAdjustment.isElastic()) {
        letterSpaceCount++;
      }
    }
    if (context.isLastArea() && mapping.isHyphenated) {
      realWidth = realWidth.plus(hyphIPD);
    }
    double ipdAdjust = context.getIPDAdjust();
    int difference;
    if (ipdAdjust > 0.0) {
      difference = (int) (realWidth.getStretch() * ipdAdjust);
    } else {
      difference = (int) (realWidth.getShrink() * ipdAdjust);
    }
    int letterSpaceDim = letterSpaceIPD.getOpt();
    if (ipdAdjust > 0.0) {
      letterSpaceDim += (int) (letterSpaceIPD.getStretch() * ipdAdjust);
    } else {
      letterSpaceDim += (int) (letterSpaceIPD.getShrink() * ipdAdjust);
    }
    int totalAdjust = (letterSpaceDim - letterSpaceIPD.getOpt()) * letterSpaceCount;
    int wordSpaceDim = wordSpaceIPD.getOpt();
    if (wordSpaceCount > 0) {
      wordSpaceDim += (difference - totalAdjust) / wordSpaceCount;
    }
    totalAdjust += (wordSpaceDim - wordSpaceIPD.getOpt()) * wordSpaceCount;
    if (totalAdjust != difference) {
      TextLayoutManager.LOG.trace("TextLM.addAreas: error in word / letter space adjustment = " + (totalAdjust - difference));
      totalAdjust = difference;
    }
    TextArea textArea = new TextAreaBuilder(realWidth, totalAdjust, context, firstMappingIndex, lastMappingIndex, context.isLastArea(), mapping.font).build();
    textArea.setTextLetterSpaceAdjust(letterSpaceDim);
    textArea.setTextWordSpaceAdjust(wordSpaceDim - spaceCharIPD - 2 * textArea.getTextLetterSpaceAdjust());
    if (context.getIPDAdjust() != 0) {
      textArea.setSpaceDifference(wordSpaceIPD.getOpt() - spaceCharIPD - 2 * textArea.getTextLetterSpaceAdjust());
    }
    parentLayoutManager.addChildArea(textArea);
  }

  private final class TextAreaBuilder {
    private final MinOptMax width;

    private final int adjust;

    private final LayoutContext context;

    private final int firstIndex;

    private final int lastIndex;

    private final boolean isLastArea;

    private final Font font;

    private TextArea textArea;

    private int blockProgressionDimension;

    private GlyphMapping mapping;

    private StringBuffer wordChars;

    private int[] letterSpaceAdjust;

    private int letterSpaceAdjustIndex;

    private int[] wordLevels;

    private int wordLevelsIndex;

    private int wordIPD;

    private int[][] gposAdjustments;

    private int gposAdjustmentsIndex;

    /**
         * Creates a new <code>TextAreaBuilder</code> which itself builds an inline word area. This
         * creates a TextArea and sets up the various attributes.
         *
         * @param width      the MinOptMax width of the content
         * @param adjust     the total ipd adjustment with respect to the optimal width
         * @param context    the layout context
         * @param firstIndex the index of the first GlyphMapping used for the TextArea
         * @param lastIndex  the index of the last GlyphMapping used for the TextArea
         * @param isLastArea is this TextArea the last in a line?
         * @param font       Font to be used in this particular TextArea
         */
    private TextAreaBuilder(MinOptMax width, int adjust, LayoutContext context, int firstIndex, int lastIndex, boolean isLastArea, Font font) {
      this.width = width;
      this.adjust = adjust;
      this.context = context;
      this.firstIndex = firstIndex;
      this.lastIndex = lastIndex;
      this.isLastArea = isLastArea;
      this.font = font;
    }

    private TextArea build() {
      createTextArea();
      setInlineProgressionDimension();
      calcBlockProgressionDimension();
      setBlockProgressionDimension();
      setBaselineOffset();
      setBlockProgressionOffset();
      setText();
      TraitSetter.addFontTraits(textArea, font);
      textArea.addTrait(Trait.COLOR, foText.getColor());
      TraitSetter.addTextDecoration(textArea, foText.getTextDecoration());
      if (!context.treatAsArtifact()) {
        TraitSetter.addStructureTreeElement(textArea, foText.getStructureTreeElement());
      }
      return textArea;
    }

    /**
         * Creates an plain <code>TextArea</code> or a justified <code>TextArea</code> with
         * additional information.
         */
    private void createTextArea() {
      if (context.getIPDAdjust() == 0.0) {
        textArea = new TextArea();
      } else {
        textArea = new TextArea(width.getStretch(), width.getShrink(), adjust);
      }
    }

    private void setInlineProgressionDimension() {
      textArea.setIPD(width.getOpt() + adjust);
    }

    private void calcBlockProgressionDimension() {
      blockProgressionDimension = font.getAscender() - font.getDescender();
    }

    private void setBlockProgressionDimension() {
      textArea.setBPD(blockProgressionDimension);
    }

    private void setBaselineOffset() {
      textArea.setBaselineOffset(font.getAscender());
    }

    private void setBlockProgressionOffset() {
      if (blockProgressionDimension == alignmentContext.getHeight()) {
        textArea.setBlockProgressionOffset(0);
      } else {
        textArea.setBlockProgressionOffset(alignmentContext.getOffset());
      }
    }

    /**
         * Sets the text of the TextArea, split into words and spaces.
         */
    private void setText() {
      int mappingIndex = -1;
      int wordCharLength = 0;
      for (int wordIndex = firstIndex; wordIndex <= lastIndex; wordIndex++) {
        mapping = getGlyphMapping(wordIndex);
        textArea.updateLevel(mapping.level);
        if (mapping.isSpace) {
          addSpaces();
        } else {
          if (mappingIndex == -1) {
            mappingIndex = wordIndex;
            wordCharLength = 0;
          }
          wordCharLength += mapping.getWordLength();
          if (isWordEnd(wordIndex)) {
            addWord(mappingIndex, wordIndex, wordCharLength);
            mappingIndex = -1;
          }
        }
      }
    }

    private boolean isWordEnd(int mappingIndex) {
      return mappingIndex == lastIndex || getGlyphMapping(mappingIndex + 1).isSpace;
    }

    /**
         * Add word with fragments from STARTINDEX to ENDINDEX, where
         * total length of (possibly mapped) word is CHARLENGTH.
         * A word is composed from one or more word fragments, where each
         * fragment corresponds to distinct instance in a sequence of
         * area info instances starting at STARTINDEX continuing through (and
         * including)  ENDINDEX.
         * @param startIndex index of first area info of word to add
         * @param endIndex index of last area info of word to add
         * @param wordLength number of (mapped) characters in word
         */
    private void addWord(int startIndex, int endIndex, int wordLength) {
      int blockProgressionOffset = 0;
      boolean gposAdjusted = false;
      if (isHyphenated(endIndex)) {
        wordLength++;
      }
      initWord(wordLength);
      for (int i = startIndex; i <= endIndex; i++) {
        GlyphMapping wordMapping = getGlyphMapping(i);
        addWordChars(wordMapping);
        addLetterAdjust(wordMapping);
        if (addGlyphPositionAdjustments(wordMapping)) {
          gposAdjusted = true;
        }
      }
      if (isHyphenated(endIndex)) {
        addHyphenationChar();
      }
      if (!gposAdjusted) {
        gposAdjustments = null;
      }
      textArea.addWord(wordChars.toString(), wordIPD, letterSpaceAdjust, getNonEmptyLevels(), gposAdjustments, blockProgressionOffset);
    }

    private int[] getNonEmptyLevels() {
      if (wordLevels != null) {
        assert wordLevelsIndex <= wordLevels.length;
        boolean empty = true;
        for (int i = 0, n = wordLevelsIndex; i < n; i++) {
          if (wordLevels[i] >= 0) {
            empty = false;
            break;
          }
        }
        return empty ? null : wordLevels;
      } else {
        return null;
      }
    }

    /**
         * Fully allocate word character buffer, letter space adjustments
         * array, bidi levels array, and glyph position adjustments array.
         * based on full word length, including all (possibly mapped) fragments.
         * @param wordLength length of word including all (possibly mapped) fragments
         */
    private void initWord(int wordLength) {
      wordChars = new StringBuffer(wordLength);
      letterSpaceAdjust = new int[wordLength];
      letterSpaceAdjustIndex = 0;
      wordLevels = new int[wordLength];
      wordLevelsIndex = 0;
      Arrays.fill(wordLevels, -1);
      gposAdjustments = new int[wordLength][4];
      gposAdjustmentsIndex = 0;
      wordIPD = 0;
    }

    private boolean isHyphenated(int endIndex) {
      return isLastArea && endIndex == lastIndex && mapping.isHyphenated;
    }

    private void addHyphenationChar() {
      wordChars.append(foText.getCommonHyphenation().getHyphChar(font));
      textArea.setHyphenated();
    }

    /**
         * Given a word area info associated with a word fragment,
         * (1) concatenate (possibly mapped) word characters to word character buffer;
         * (2) concatenante (possibly mapped) word bidi levels to levels buffer;
         * (3) update word's IPD with optimal IPD of fragment.
         * @param wordMapping fragment info
         */
    private void addWordChars(GlyphMapping wordMapping) {
      int s = wordMapping.startIndex;
      int e = wordMapping.endIndex;
      if (wordMapping.mapping != null) {
        wordChars.append(wordMapping.mapping);
        addWordLevels(getMappingBidiLevels(wordMapping));
      } else {
        for (int i = s; i < e; i++) {
          wordChars.append(foText.charAt(i));
        }
        addWordLevels(foText.getBidiLevels(s, e));
      }
      wordIPD += wordMapping.areaIPD.getOpt();
    }

    /**
         * Obtain bidirectional levels of mapping of characters over specific interval.
         * @param start index in character buffer
         * @param end index in character buffer
         * @return a (possibly empty) array of bidi levels or null
         * in case no bidi levels have been assigned
         */
    private int[] getMappingBidiLevels(GlyphMapping mapping) {
      if (mapping.mapping != null) {
        int nc = mapping.endIndex - mapping.startIndex;
        int nm = mapping.mapping.length();
        int[] la = foText.getBidiLevels(mapping.startIndex, mapping.endIndex);
        if (la == null) {
          return null;
        } else {
          if (nm == nc) {
            return la;
          } else {
            if (nm > nc) {
              int[] ma = new int[nm];
              System.arraycopy(la, 0, ma, 0, la.length);
              for (int i = la.length, n = ma.length, l = (i > 0) ? la[i - 1] : 0; i < n; i++) {
                ma[i] = l;
              }
              return ma;
            } else {
              int[] ma = new int[nm];
              System.arraycopy(la, 0, ma, 0, ma.length);
              return ma;
            }
          }
        }
      } else {
        return foText.getBidiLevels(mapping.startIndex, mapping.endIndex);
      }
    }

    /**
         * Given a (possibly null) bidi levels array associated with a word fragment,
         * concatenante (possibly mapped) word bidi levels to levels buffer.
         * @param levels bidi levels array or null
         */
    private void addWordLevels(int[] levels) {
      int numLevels = (levels != null) ? levels.length : 0;
      if (numLevels > 0) {
        int need = wordLevelsIndex + numLevels;
        if (need <= wordLevels.length) {
          System.arraycopy(levels, 0, wordLevels, wordLevelsIndex, numLevels);
        } else {
          throw new IllegalStateException("word levels array too short: expect at least " + need + " entries, but has only " + wordLevels.length + " entries");
        }
      }
      wordLevelsIndex += numLevels;
    }

    /**
         * Given a word area info associated with a word fragment,
         * concatenate letter space adjustments for each (possibly mapped) character.
         * @param wordMapping fragment info
         */
    private void addLetterAdjust(GlyphMapping wordMapping) {
      int letterSpaceCount = wordMapping.letterSpaceCount;
      int wordLength = wordMapping.getWordLength();
      int taAdjust = textArea.getTextLetterSpaceAdjust();
      for (int i = 0, n = wordLength; i < n; i++) {
        int j = letterSpaceAdjustIndex + i;
        if (j > 0) {
          int k = wordMapping.startIndex + i;
          MinOptMax adj = (k < letterSpaceAdjustArray.length) ? letterSpaceAdjustArray[k] : null;
          letterSpaceAdjust[j] = (adj == null) ? 0 : adj.getOpt();
        }
        if (letterSpaceCount > 0) {
          letterSpaceAdjust[j] += taAdjust;
          letterSpaceCount--;
        }
      }
      letterSpaceAdjustIndex += wordLength;
    }

    /**
         * Given a word area info associated with a word fragment,
         * concatenate glyph position adjustments for each (possibly mapped) character.
         * @param wordMapping fragment info
         * @return true if an adjustment was non-zero
         */
    private boolean addGlyphPositionAdjustments(GlyphMapping wordMapping) {
      boolean adjusted = false;
      int[][] gpa = wordMapping.gposAdjustments;
      int numAdjusts = (gpa != null) ? gpa.length : 0;
      int wordLength = wordMapping.getWordLength();
      if (numAdjusts > 0) {
        int need = gposAdjustmentsIndex + numAdjusts;
        if (need <= gposAdjustments.length) {
          for (int i = 0, n = wordLength, j = 0; i < n; i++) {
            if (i < numAdjusts) {
              int[] wpa1 = gposAdjustments[gposAdjustmentsIndex + i];
              int[] wpa2 = gpa[j++];
              for (int k = 0; k < 4; k++) {
                int a = wpa2[k];
                if (a != 0) {
                  wpa1[k] += a;
                  adjusted = true;
                }
              }
            }
          }
        } else {
          throw new IllegalStateException("gpos adjustments array too short: expect at least " + need + " entries, but has only " + gposAdjustments.length + " entries");
        }
      }
      gposAdjustmentsIndex += wordLength;
      return adjusted;
    }

    /**
         * The <code>GlyphMapping</code> stores information about spaces.
         * <p/>
         * Add the spaces - except zero-width spaces - to the TextArea.
         */
    private void addSpaces() {
      int blockProgressionOffset = 0;
      int numZeroWidthSpaces = 0;
      for (int i = mapping.startIndex; i < mapping.endIndex; i++) {
        char spaceChar = foText.charAt(i);
        if (CharUtilities.isZeroWidthSpace(spaceChar)) {
          numZeroWidthSpaces++;
        }
      }
      int numSpaces = mapping.endIndex - mapping.startIndex - numZeroWidthSpaces;
      int spaceIPD = mapping.areaIPD.getOpt() / ((numSpaces > 0) ? numSpaces : 1);
      for (int i = mapping.startIndex; i < mapping.endIndex; i++) {
        char spaceChar = foText.charAt(i);
        int level = foText.bidiLevelAt(i);
        if (!CharUtilities.isZeroWidthSpace(spaceChar)) {
          textArea.addSpace(spaceChar, spaceIPD, CharUtilities.isAdjustableSpace(spaceChar), blockProgressionOffset, level);
        }
      }
    }
  }

  private void addGlyphMapping(GlyphMapping mapping) {
    addGlyphMapping(mappings.size(), mapping);
  }

  private void addGlyphMapping(int index, GlyphMapping mapping) {
    mappings.add(index, mapping);
  }

  private void removeGlyphMapping(int index) {
    mappings.remove(index);
  }

  private GlyphMapping getGlyphMapping(int index) {
    return mappings.get(index);
  }

  /** {@inheritDoc} */
  public List getNextKnuthElements(final LayoutContext context, final int alignment) {
    lineStartBAP = context.getLineStartBorderAndPaddingWidth();
    lineEndBAP = context.getLineEndBorderAndPaddingWidth();
    alignmentContext = context.getAlignmentContext();
    final List returnList = new LinkedList();
    KnuthSequence sequence = new InlineKnuthSequence();
    GlyphMapping mapping = null;
    GlyphMapping prevMapping = null;
    returnList.add(sequence);
    if (LOG.isDebugEnabled()) {
      LOG.debug("GK: [" + nextStart + "," + foText.length() + "]");
    }
    LineBreakStatus lineBreakStatus = new LineBreakStatus();
    thisStart = nextStart;
    boolean inWord = false;
    boolean inWhitespace = false;
    char ch = 0;
    int level = -1;
    int prevLevel = -1;
    boolean retainControls = false;
    while (nextStart < foText.length()) {
      ch = foText.charAt(nextStart);
      level = foText.bidiLevelAt(nextStart);
      boolean breakOpportunity = false;
      byte breakAction = keepTogether ? LineBreakStatus.PROHIBITED_BREAK : lineBreakStatus.nextChar(ch);
      switch (breakAction) {
        case LineBreakStatus.COMBINING_PROHIBITED_BREAK:
        case LineBreakStatus.PROHIBITED_BREAK:
        break;
        case LineBreakStatus.EXPLICIT_BREAK:
        break;
        case LineBreakStatus.COMBINING_INDIRECT_BREAK:
        case LineBreakStatus.DIRECT_BREAK:
        case LineBreakStatus.INDIRECT_BREAK:
        breakOpportunity = true;
        break;
        default:
        TextLayoutManager.LOG.error("Unexpected breakAction: " + breakAction);
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("GK: {" + " index = " + nextStart + ", char = " + CharUtilities.charToNCRef(ch) + ", level = " + level + ", levelPrev = " + prevLevel + ", inWord = " + inWord + ", inSpace = " + inWhitespace + "}");
      }
      if (inWord) {
        if (breakOpportunity || GlyphMapping.isSpace(ch) || CharUtilities.isExplicitBreak(ch) || ((prevLevel != -1) && (level != prevLevel))) {
          prevMapping = processWord(alignment, sequence, prevMapping, ch, breakOpportunity, true, prevLevel, retainControls);
        }
      } else {
        if (inWhitespace) {
          if (ch != CharUtilities.SPACE || breakOpportunity) {
            prevMapping = processWhitespace(alignment, sequence, breakOpportunity, prevLevel);
          }
        } else {
          if (mapping != null) {
            prevMapping = mapping;
            processLeftoverGlyphMapping(alignment, sequence, mapping, ch == CharUtilities.SPACE || breakOpportunity);
            mapping = null;
          }
          if (breakAction == LineBreakStatus.EXPLICIT_BREAK) {
            sequence = processLinebreak(returnList, sequence);
          }
        }
      }
      if (ch == CharUtilities.SPACE && foText.getWhitespaceTreatment() == Constants.EN_PRESERVE || ch == CharUtilities.NBSPACE) {
        final Font font = FontSelector.selectFontForCharacterInText(ch, this.foText, this);
        font.mapChar(ch);
        mapping = new GlyphMapping(nextStart, nextStart + 1, 1, 0, wordSpaceIPD, false, true, breakOpportunity, spaceFont, level, null);
        thisStart = nextStart + 1;
      } else {
        if (CharUtilities.isFixedWidthSpace(ch) || CharUtilities.isZeroWidthSpace(ch)) {
          Font font = FontSelector.selectFontForCharacterInText(ch, foText, this);
          MinOptMax ipd = MinOptMax.getInstance(font.getCharWidth(ch));
          mapping = new GlyphMapping(nextStart, nextStart + 1, 0, 0, ipd, false, true, breakOpportunity, font, level, null);
          thisStart = nextStart + 1;
        } else {
          if (CharUtilities.isExplicitBreak(ch)) {
            thisStart = nextStart + 1;
          }
        }
      }
      inWord = !GlyphMapping.isSpace(ch) && !CharUtilities.isExplicitBreak(ch);
      inWhitespace = ch == CharUtilities.SPACE && foText.getWhitespaceTreatment() != Constants.EN_PRESERVE;
      prevLevel = level;
      nextStart++;
    }
    if (inWord) {
      processWord(alignment, sequence, prevMapping, ch, false, false, prevLevel, retainControls);
    } else {
      if (inWhitespace) {
        processWhitespace(alignment, sequence, !keepTogether, prevLevel);
      } else {
        if (mapping != null) {
          processLeftoverGlyphMapping(alignment, sequence, mapping, ch == CharUtilities.ZERO_WIDTH_SPACE);
        } else {
          if (CharUtilities.isExplicitBreak(ch)) {
            this.processLinebreak(returnList, sequence);
          }
        }
      }
    }
    if (((List) ListUtil.getLast(returnList)).isEmpty()) {
      ListUtil.removeLast(returnList);
    }
    setFinished(true);
    if (returnList.isEmpty()) {
      return null;
    } else {
      return returnList;
    }
  }

  private KnuthSequence processLinebreak(List returnList, KnuthSequence sequence) {
    if (lineEndBAP != 0) {
      sequence.add(new KnuthGlue(lineEndBAP, 0, 0, auxiliaryPosition, true));
    }
    sequence.endSequence();
    sequence = new InlineKnuthSequence();
    returnList.add(sequence);
    return sequence;
  }

  private void processLeftoverGlyphMapping(int alignment, KnuthSequence sequence, GlyphMapping mapping, boolean breakOpportunityAfter) {
    addGlyphMapping(mapping);
    mapping.breakOppAfter = breakOpportunityAfter;
    addElementsForASpace(sequence, alignment, mapping, mappings.size() - 1);
  }

  private GlyphMapping processWhitespace(final int alignment, final KnuthSequence sequence, final boolean breakOpportunity, int level) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("PS: [" + thisStart + "," + nextStart + "]");
    }
    assert nextStart >= thisStart;
    GlyphMapping mapping = new GlyphMapping(thisStart, nextStart, nextStart - thisStart, 0, wordSpaceIPD.mult(nextStart - thisStart), false, true, breakOpportunity, spaceFont, level, null);
    addGlyphMapping(mapping);
    addElementsForASpace(sequence, alignment, mapping, mappings.size() - 1);
    thisStart = nextStart;
    return mapping;
  }

  private GlyphMapping processWord(final int alignment, final KnuthSequence sequence, GlyphMapping prevMapping, final char ch, final boolean breakOpportunity, final boolean checkEndsWithHyphen, int level, boolean retainControls) {
    int lastIndex = nextStart;
    while (lastIndex > 0 && foText.charAt(lastIndex - 1) == CharUtilities.SOFT_HYPHEN) {
      lastIndex--;
    }
    final boolean endsWithHyphen = checkEndsWithHyphen && foText.charAt(lastIndex) == CharUtilities.SOFT_HYPHEN;
    Font font = FontSelector.selectFontForCharactersInText(foText, thisStart, lastIndex, foText, this);
    char breakOpportunityChar = breakOpportunity ? ch : 0;
    char precedingChar = prevMapping != null && !prevMapping.isSpace && prevMapping.endIndex > 0 ? foText.charAt(prevMapping.endIndex - 1) : 0;
    GlyphMapping mapping = GlyphMapping.doGlyphMapping(foText, thisStart, lastIndex, font, letterSpaceIPD, letterSpaceAdjustArray, precedingChar, breakOpportunityChar, endsWithHyphen, level, false, false, retainControls);
    prevMapping = mapping;
    addGlyphMapping(mapping);
    tempStart = nextStart;
    addElementsForAWordFragment(sequence, alignment, mapping, mappings.size() - 1);
    thisStart = nextStart;
    return prevMapping;
  }

  /** {@inheritDoc} */
  public List addALetterSpaceTo(List oldList) {
    return addALetterSpaceTo(oldList, 0);
  }

  /** {@inheritDoc} */
  public List addALetterSpaceTo(final List oldList, int depth) {
    ListIterator oldListIterator = oldList.listIterator();
    KnuthElement knuthElement = (KnuthElement) oldListIterator.next();
    Position pos = knuthElement.getPosition();
    Position innerPosition = pos.getPosition(depth);
    assert (innerPosition instanceof LeafPosition);
    LeafPosition leafPos = (LeafPosition) innerPosition;
    int index = leafPos.getLeafPos();
    if (index > -1) {
      GlyphMapping mapping = getGlyphMapping(index);
      mapping.letterSpaceCount++;
      mapping.addToAreaIPD(letterSpaceIPD);
      if (TextLayoutManager.BREAK_CHARS.indexOf(foText.charAt(tempStart - 1)) >= 0) {
        oldListIterator = oldList.listIterator(oldList.size());
        oldListIterator.add(new KnuthPenalty(0, KnuthPenalty.FLAGGED_PENALTY, true, auxiliaryPosition, false));
        oldListIterator.add(new KnuthGlue(letterSpaceIPD, auxiliaryPosition, false));
      } else {
        if (letterSpaceIPD.isStiff()) {
          oldListIterator.set(new KnuthInlineBox(mapping.areaIPD.getOpt(), alignmentContext, pos, false));
        } else {
          oldListIterator.next();
          oldListIterator.next();
          oldListIterator.set(new KnuthGlue(letterSpaceIPD.mult(mapping.letterSpaceCount), auxiliaryPosition, true));
        }
      }
    }
    return oldList;
  }

  /** {@inheritDoc} */
  public void hyphenate(Position pos, HyphContext hyphContext) {
    GlyphMapping mapping = getGlyphMapping(((LeafPosition) pos).getLeafPos() + changeOffset);
    int startIndex = mapping.startIndex;
    int stopIndex;
    boolean nothingChanged = true;
    Font font = mapping.font;
    while (startIndex < mapping.endIndex) {
      MinOptMax newIPD = MinOptMax.ZERO;
      boolean hyphenFollows;
      stopIndex = startIndex + hyphContext.getNextHyphPoint();
      if (hyphContext.hasMoreHyphPoints() && stopIndex <= mapping.endIndex) {
        hyphenFollows = true;
      } else {
        hyphenFollows = false;
        stopIndex = mapping.endIndex;
      }
      hyphContext.updateOffset(stopIndex - startIndex);
      for (int i = startIndex; i < stopIndex; i++) {
        int cp = Character.codePointAt(foText, i);
        i += Character.charCount(cp) - 1;
        newIPD = newIPD.plus(font.getCharWidth(cp));
        if (i < stopIndex) {
          MinOptMax letterSpaceAdjust = letterSpaceAdjustArray[i + 1];
          if (i == stopIndex - 1 && hyphenFollows) {
            letterSpaceAdjust = null;
          }
          if (letterSpaceAdjust != null) {
            newIPD = newIPD.plus(letterSpaceAdjust);
          }
        }
      }
      boolean isWordEnd = (stopIndex == mapping.endIndex) && (mapping.letterSpaceCount < mapping.getWordLength());
      int letterSpaceCount = isWordEnd ? stopIndex - startIndex - 1 : stopIndex - startIndex;
      assert letterSpaceCount >= 0;
      newIPD = newIPD.plus(letterSpaceIPD.mult(letterSpaceCount));
      if (!(nothingChanged && stopIndex == mapping.endIndex && !hyphenFollows)) {
        changeList.add(new PendingChange(new GlyphMapping(startIndex, stopIndex, 0, letterSpaceCount, newIPD, hyphenFollows, false, false, font, -1, null), ((LeafPosition) pos).getLeafPos() + changeOffset));
        nothingChanged = false;
      }
      startIndex = stopIndex;
    }
    hasChanged |= !nothingChanged;
  }

  /** {@inheritDoc} */
  public boolean applyChanges(final List oldList) {
    return applyChanges(oldList, 0);
  }

  /** {@inheritDoc} */
  public boolean applyChanges(final List oldList, int depth) {
    setFinished(false);
    if (oldList.isEmpty()) {
      return false;
    }
    LeafPosition startPos = null;
    LeafPosition endPos = null;
    ListIterator oldListIter;
    for (oldListIter = oldList.listIterator(); oldListIter.hasNext(); ) {
      Position pos = ((KnuthElement) oldListIter.next()).getPosition();
      Position innerPosition = pos.getPosition(depth);
      assert (innerPosition == null || innerPosition instanceof LeafPosition);
      startPos = (LeafPosition) innerPosition;
      if (startPos != null && startPos.getLeafPos() != -1) {
        break;
      }
    }
    for (oldListIter = oldList.listIterator(oldList.size()); oldListIter.hasPrevious(); ) {
      Position pos = ((KnuthElement) oldListIter.previous()).getPosition();
      Position innerPosition = pos.getPosition(depth);
      assert (innerPosition instanceof LeafPosition);
      endPos = (LeafPosition) innerPosition;
      if (endPos != null && endPos.getLeafPos() != -1) {
        break;
      }
    }
    returnedIndices[0] = (startPos != null ? startPos.getLeafPos() : -1) + changeOffset;
    returnedIndices[1] = (endPos != null ? endPos.getLeafPos() : -1) + changeOffset;
    int mappingsAdded = 0;
    int mappingsRemoved = 0;
    if (!changeList.isEmpty()) {
      int oldIndex = -1;
      int changeIndex;
      PendingChange currChange;
      for (Object aChangeList : changeList) {
        currChange = (PendingChange) aChangeList;
        if (currChange.index == oldIndex) {
          mappingsAdded++;
          changeIndex = currChange.index + mappingsAdded - mappingsRemoved;
        } else {
          mappingsRemoved++;
          mappingsAdded++;
          oldIndex = currChange.index;
          changeIndex = currChange.index + mappingsAdded - mappingsRemoved;
          removeGlyphMapping(changeIndex);
        }
        addGlyphMapping(changeIndex, currChange.mapping);
      }
      changeList.clear();
    }
    returnedIndices[1] += (mappingsAdded - mappingsRemoved);
    changeOffset += (mappingsAdded - mappingsRemoved);
    return hasChanged;
  }

  /** {@inheritDoc} */
  public List getChangedKnuthElements(final List oldList, final int alignment) {
    if (isFinished()) {
      return null;
    }
    final LinkedList returnList = new LinkedList();
    for ( ; returnedIndices[0] <= returnedIndices[1]; returnedIndices[0]++) {
      GlyphMapping mapping = getGlyphMapping(returnedIndices[0]);
      if (mapping.wordSpaceCount == 0) {
        addElementsForAWordFragment(returnList, alignment, mapping, returnedIndices[0]);
      } else {
        addElementsForASpace(returnList, alignment, mapping, returnedIndices[0]);
      }
    }
    setFinished(returnedIndices[0] == mappings.size() - 1);
    return returnList;
  }

  /** {@inheritDoc} */
  public String getWordChars(Position pos) {
    int leafValue = ((LeafPosition) pos).getLeafPos() + changeOffset;
    if (leafValue != -1) {
      GlyphMapping mapping = getGlyphMapping(leafValue);
      StringBuffer buffer = new StringBuffer(mapping.getWordLength());
      for (int i = mapping.startIndex; i < mapping.endIndex; i++) {
        buffer.append(foText.charAt(i));
      }
      return buffer.toString();
    } else {
      return "";
    }
  }

  private void addElementsForASpace(List baseList, int alignment, GlyphMapping mapping, int leafValue) {
    LeafPosition mainPosition = new LeafPosition(this, leafValue);
    if (!mapping.breakOppAfter) {
      if (alignment == Constants.EN_JUSTIFY) {
        baseList.add(makeAuxiliaryZeroWidthBox());
        baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        baseList.add(new KnuthGlue(mapping.areaIPD, mainPosition, false));
      } else {
        baseList.add(new KnuthInlineBox(mapping.areaIPD.getOpt(), null, mainPosition, true));
      }
    } else {
      if (foText.charAt(mapping.startIndex) != CharUtilities.SPACE || foText.getWhitespaceTreatment() == Constants.EN_PRESERVE) {
        baseList.addAll(getElementsForBreakingSpace(alignment, mapping, auxiliaryPosition, 0, mainPosition, mapping.areaIPD.getOpt(), true));
      } else {
        baseList.addAll(getElementsForBreakingSpace(alignment, mapping, mainPosition, mapping.areaIPD.getOpt(), auxiliaryPosition, 0, false));
      }
    }
  }

  private List getElementsForBreakingSpace(int alignment, GlyphMapping mapping, Position pos2, int p2WidthOffset, Position pos3, int p3WidthOffset, boolean skipZeroCheck) {
    List elements = new ArrayList();
    switch (alignment) {
      case EN_CENTER:
      elements.add(new KnuthGlue(lineEndBAP, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false));
      elements.add(makeZeroWidthPenalty(0));
      elements.add(new KnuthGlue(p2WidthOffset - (lineStartBAP + lineEndBAP), -6 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, pos2, false));
      elements.add(makeAuxiliaryZeroWidthBox());
      elements.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
      elements.add(new KnuthGlue(lineStartBAP + p3WidthOffset, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, pos3, false));
      break;
      case EN_START:
      case EN_END:
      KnuthGlue g;
      if (skipZeroCheck || lineStartBAP != 0 || lineEndBAP != 0) {
        g = new KnuthGlue(lineEndBAP, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false);
        elements.add(g);
        elements.add(makeZeroWidthPenalty(0));
        g = new KnuthGlue(p2WidthOffset - (lineStartBAP + lineEndBAP), -3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, pos2, false);
        elements.add(g);
        elements.add(makeAuxiliaryZeroWidthBox());
        elements.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        g = new KnuthGlue(lineStartBAP + p3WidthOffset, 0, 0, pos3, false);
        elements.add(g);
      } else {
        g = new KnuthGlue(0, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false);
        elements.add(g);
        elements.add(makeZeroWidthPenalty(0));
        g = new KnuthGlue(mapping.areaIPD.getOpt(), -3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, pos2, false);
        elements.add(g);
      }
      break;
      case EN_JUSTIFY:
      elements.addAll(getElementsForJustifiedText(mapping, pos2, p2WidthOffset, pos3, p3WidthOffset, skipZeroCheck, mapping.areaIPD.getShrink()));
      break;
      default:
      elements.addAll(getElementsForJustifiedText(mapping, pos2, p2WidthOffset, pos3, p3WidthOffset, skipZeroCheck, 0));
    }
    return elements;
  }

  private List getElementsForJustifiedText(GlyphMapping mapping, Position pos2, int p2WidthOffset, Position pos3, int p3WidthOffset, boolean skipZeroCheck, int shrinkability) {
    int stretchability = mapping.areaIPD.getStretch();
    List elements = new ArrayList();
    if (skipZeroCheck || lineStartBAP != 0 || lineEndBAP != 0) {
      elements.add(new KnuthGlue(lineEndBAP, 0, 0, auxiliaryPosition, false));
      elements.add(makeZeroWidthPenalty(0));
      elements.add(new KnuthGlue(p2WidthOffset - (lineStartBAP + lineEndBAP), stretchability, shrinkability, pos2, false));
      elements.add(makeAuxiliaryZeroWidthBox());
      elements.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
      elements.add(new KnuthGlue(lineStartBAP + p3WidthOffset, 0, 0, pos3, false));
    } else {
      elements.add(new KnuthGlue(mapping.areaIPD.getOpt(), stretchability, shrinkability, pos2, false));
    }
    return elements;
  }

  private void addElementsForAWordFragment(List baseList, int alignment, GlyphMapping mapping, int leafValue) {
    LeafPosition mainPosition = new LeafPosition(this, leafValue);
    boolean suppressibleLetterSpace = mapping.breakOppAfter && !mapping.isHyphenated;
    if (letterSpaceIPD.isStiff()) {
      baseList.add(new KnuthInlineBox(suppressibleLetterSpace ? mapping.areaIPD.getOpt() - letterSpaceIPD.getOpt() : mapping.areaIPD.getOpt(), alignmentContext, notifyPos(mainPosition), false));
    } else {
      int unsuppressibleLetterSpaces = suppressibleLetterSpace ? mapping.letterSpaceCount - 1 : mapping.letterSpaceCount;
      baseList.add(new KnuthInlineBox(mapping.areaIPD.getOpt() - mapping.letterSpaceCount * letterSpaceIPD.getOpt(), alignmentContext, notifyPos(mainPosition), false));
      baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
      baseList.add(new KnuthGlue(letterSpaceIPD.mult(unsuppressibleLetterSpaces), auxiliaryPosition, true));
      baseList.add(makeAuxiliaryZeroWidthBox());
    }
    if (mapping.isHyphenated) {
      MinOptMax widthIfNoBreakOccurs = null;
      if (mapping.endIndex < foText.length()) {
        widthIfNoBreakOccurs = letterSpaceAdjustArray[mapping.endIndex];
      }
      addElementsForAHyphen(baseList, alignment, hyphIPD, widthIfNoBreakOccurs, mapping.breakOppAfter && mapping.isHyphenated);
    } else {
      if (suppressibleLetterSpace) {
        addElementsForAHyphen(baseList, alignment, 0, letterSpaceIPD, true);
      }
    }
  }

  private void addElementsForAHyphen(List baseList, int alignment, int widthIfBreakOccurs, MinOptMax widthIfNoBreakOccurs, boolean unflagged) {
    if (widthIfNoBreakOccurs == null) {
      widthIfNoBreakOccurs = MinOptMax.ZERO;
    }
    switch (alignment) {
      case EN_CENTER:
      baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
      baseList.add(new KnuthGlue(lineEndBAP, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, true));
      baseList.add(new KnuthPenalty(hyphIPD, unflagged ? TextLayoutManager.SOFT_HYPHEN_PENALTY : KnuthPenalty.FLAGGED_PENALTY, !unflagged, auxiliaryPosition, false));
      baseList.add(new KnuthGlue(-(lineEndBAP + lineStartBAP), -6 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false));
      baseList.add(makeAuxiliaryZeroWidthBox());
      baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
      baseList.add(new KnuthGlue(lineStartBAP, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, true));
      break;
      case EN_START:
      case EN_END:
      if (lineStartBAP != 0 || lineEndBAP != 0) {
        baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        baseList.add(new KnuthGlue(lineEndBAP, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false));
        baseList.add(new KnuthPenalty(widthIfBreakOccurs, unflagged ? TextLayoutManager.SOFT_HYPHEN_PENALTY : KnuthPenalty.FLAGGED_PENALTY, !unflagged, auxiliaryPosition, false));
        baseList.add(new KnuthGlue(widthIfNoBreakOccurs.getOpt() - (lineStartBAP + lineEndBAP), -3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false));
        baseList.add(makeAuxiliaryZeroWidthBox());
        baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        baseList.add(new KnuthGlue(lineStartBAP, 0, 0, auxiliaryPosition, false));
      } else {
        baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        baseList.add(new KnuthGlue(0, 3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false));
        baseList.add(new KnuthPenalty(widthIfBreakOccurs, unflagged ? TextLayoutManager.SOFT_HYPHEN_PENALTY : KnuthPenalty.FLAGGED_PENALTY, !unflagged, auxiliaryPosition, false));
        baseList.add(new KnuthGlue(widthIfNoBreakOccurs.getOpt(), -3 * LineLayoutManager.DEFAULT_SPACE_WIDTH, 0, auxiliaryPosition, false));
      }
      break;
      default:
      if (lineStartBAP != 0 || lineEndBAP != 0) {
        baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        baseList.add(new KnuthGlue(lineEndBAP, 0, 0, auxiliaryPosition, false));
        baseList.add(new KnuthPenalty(widthIfBreakOccurs, unflagged ? TextLayoutManager.SOFT_HYPHEN_PENALTY : KnuthPenalty.FLAGGED_PENALTY, !unflagged, auxiliaryPosition, false));
        if (widthIfNoBreakOccurs.isNonZero()) {
          baseList.add(new KnuthGlue(widthIfNoBreakOccurs.getOpt() - (lineStartBAP + lineEndBAP), widthIfNoBreakOccurs.getStretch(), widthIfNoBreakOccurs.getShrink(), auxiliaryPosition, false));
        } else {
          baseList.add(new KnuthGlue(-(lineStartBAP + lineEndBAP), 0, 0, auxiliaryPosition, false));
        }
        baseList.add(makeAuxiliaryZeroWidthBox());
        baseList.add(makeZeroWidthPenalty(KnuthElement.INFINITE));
        baseList.add(new KnuthGlue(lineStartBAP, 0, 0, auxiliaryPosition, false));
      } else {
        baseList.add(new KnuthPenalty(widthIfBreakOccurs, unflagged ? TextLayoutManager.SOFT_HYPHEN_PENALTY : KnuthPenalty.FLAGGED_PENALTY, !unflagged, auxiliaryPosition, false));
        if (widthIfNoBreakOccurs.isNonZero()) {
          baseList.add(new KnuthGlue(widthIfNoBreakOccurs, auxiliaryPosition, false));
        }
      }
    }
  }

  /** {@inheritDoc} */
  public String toString() {
    return super.toString() + "{" + "chars = \'" + CharUtilities.toNCRefs(foText.getCharSequence().toString()) + "\'" + ", len = " + foText.length() + "}";
  }
}