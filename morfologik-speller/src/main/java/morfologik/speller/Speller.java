package morfologik.speller;
import static morfologik.fsa.MatchResult.EXACT_MATCH;
import static morfologik.fsa.MatchResult.SEQUENCE_IS_A_PREFIX;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.*;
import morfologik.fsa.FSA;
import morfologik.fsa.ByteSequenceIterator;
import morfologik.fsa.FSATraversal;
import morfologik.fsa.MatchResult;
import morfologik.stemming.BufferUtils;
import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.DictionaryMetadata;
import morfologik.stemming.UnmappableInputException;

/**
 * Finds spelling suggestions. Implements K. Oflazer's algorithm as described
 * in: Oflazer, Kemal. 1996.
 * "Error-Tolerant Finite-State Recognition with Applications to Morphological Analysis and Spelling Correction."
 * <i>Computational Linguistics</i> 22 (1): 73–89.
 * 
 * <p>
 * See Jan Daciuk's <code>s_fsa</code> package.
 */
public class Speller {
  /**
   * Maximum length of the word to be checked.
   */
  public static final int MAX_WORD_LENGTH = 120;

  static final int FREQ_RANGES = 'Z' - 'A' + 1;

  static final int FIRST_RANGE_CODE = 'A';

  static final int UPPER_SEARCH_LIMIT = 15;

  private static final int MIN_WORD_LENGTH = 4;

  private static final int MAX_RECURSION_LEVEL = 6;

  private final int editDistance;

  private int effectEditDistance;

  private final HMatrix hMatrix;

  private char[] candidate;

  private int candLen;

  private int wordLen;

  private char[] wordProcessed;

  private Map<Character, List<char[]>> replacementsAnyToOne = new HashMap<Character, List<char[]>>();

  private Map<String, List<char[]>> replacementsAnyToTwo = new HashMap<String, List<char[]>>();

  private Map<String, List<String>> replacementsTheRest = new HashMap<String, List<String>>();

  /**
   * List of candidate strings, including same additional data such as edit
   * distance from the original word.
   */
  private final List<CandidateData> candidates = new ArrayList<CandidateData>();

  private boolean containsSeparators = true;

  /**
   * Internal reusable buffer for encoding words into byte arrays using
   * {@link #encoder}.
   */
  private ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_WORD_LENGTH);

  /**
   * Internal reusable buffer for encoding words into byte arrays using
   * {@link #encoder}.
   */
  private CharBuffer charBuffer = CharBuffer.allocate(MAX_WORD_LENGTH);

  /**
   * Reusable match result.
   */
  private final MatchResult matchResult = new MatchResult();

  /**
   * Features of the compiled dictionary.
   * 
   * @see DictionaryMetadata
   */
  private final DictionaryMetadata dictionaryMetadata;

  /**
   * Charset encoder for the FSA.
   */
  private final CharsetEncoder encoder;

  /**
   * Charset decoder for the FSA.
   */
  private final CharsetDecoder decoder;

  /** An FSA used for lookups. */
  private final FSATraversal matcher;

  /** FSA's root node. */
  private final int rootNode;

  /**
   * The FSA we are using.
   */
  private final FSA fsa;

  /** An iterator for walking along the final states of {@link #fsa}. */
  private final ByteSequenceIterator finalStatesIterator;

  public Speller(final Dictionary dictionary) {
    this(dictionary, 1);
  }

  public Speller(final Dictionary dictionary, final int editDistance) {
    this.editDistance = editDistance;
    this.hMatrix = new HMatrix(editDistance, MAX_WORD_LENGTH);
    this.dictionaryMetadata = dictionary.metadata;
    this.rootNode = dictionary.fsa.getRootNode();
    this.fsa = dictionary.fsa;
    this.matcher = new FSATraversal(fsa);
    this.finalStatesIterator = new ByteSequenceIterator(fsa, rootNode);
    if (rootNode == 0) {
      throw new IllegalArgumentException("Dictionary must have at least the root node.");
    }
    if (dictionaryMetadata == null) {
      throw new IllegalArgumentException("Dictionary metadata must not be null.");
    }
    encoder = dictionaryMetadata.getEncoder();
    decoder = dictionaryMetadata.getDecoder();
    dictionaryMetadata.getSeparatorAsChar();
    this.createReplacementsMaps();
  }

  private void createReplacementsMaps() {
    for (Map.Entry<String, List<String>> entry : dictionaryMetadata.getReplacementPairs().entrySet()) {
      for (String s : entry.getValue()) {
        if (s.length() == 1) {
          if (!replacementsAnyToOne.containsKey(s.charAt(0))) {
            List<char[]> charList = new ArrayList<char[]>();
            charList.add(entry.getKey().toCharArray());
            replacementsAnyToOne.put(s.charAt(0), charList);
          } else {
            replacementsAnyToOne.get(s.charAt(0)).add(entry.getKey().toCharArray());
          }
        } else {
          if (s.length() == 2) {
            if (!replacementsAnyToTwo.containsKey(s)) {
              List<char[]> charList = new ArrayList<char[]>();
              charList.add(entry.getKey().toCharArray());
              replacementsAnyToTwo.put(s, charList);
            } else {
              replacementsAnyToTwo.get(s).add(entry.getKey().toCharArray());
            }
          } else {
            if (!replacementsTheRest.containsKey(entry.getKey())) {
              List<String> charList = new ArrayList<String>();
              charList.add(s);
              replacementsTheRest.put(entry.getKey(), charList);
            } else {
              replacementsTheRest.get(entry.getKey()).add(s);
            }
          }
        }
      }
    }
  }

  private ByteBuffer charSequenceToBytes(final CharSequence word) throws UnmappableInputException {
    charBuffer = BufferUtils.clearAndEnsureCapacity(charBuffer, word.length());
    for (int i = 0; i < word.length(); i++) {
      final char chr = word.charAt(i);
      charBuffer.put(chr);
    }
    charBuffer.flip();
    return BufferUtils.charsToBytes(encoder, charBuffer, byteBuffer);
  }

  /**
   * Checks whether the word is misspelled, by performing a series of checks
   * according to properties of the dictionary.
   *
   * If the flag <code>fsa.dict.speller.ignore-punctuation</code> is set, then
   * all non-alphabetic characters are considered to be correctly spelled.
   *
   * If the flag <code>fsa.dict.speller.ignore-numbers</code> is set, then all
   * words containing decimal digits are considered to be correctly spelled.
   *
   * If the flag <code>fsa.dict.speller.ignore-camel-case</code> is set, then
   * all CamelCase words are considered to be correctly spelled.
   *
   * If the flag <code>fsa.dict.speller.ignore-all-uppercase</code> is set, then
   * all alphabetic words composed of only uppercase characters are considered
   * to be correctly spelled.
   *
   * Otherwise, the word is checked in the dictionary. If the test fails, and
   * the dictionary does not perform any case conversions (as set by
   * <code>fsa.dict.speller.convert-case</code> flag), then the method returns
   * false. In case of case conversions, it is checked whether a non-mixed case
   * word is found in its lowercase version in the dictionary, and for
   * all-uppercase words, whether the word is found in the dictionary with the
   * initial uppercase letter.
   *
   * @param word
   *          - the word to be checked
   * @return true if the word is misspelled
   **/
  public boolean isMisspelled(final String word) {
    String wordToCheck = word;
    if (!dictionaryMetadata.getInputConversionPairs().isEmpty()) {
      wordToCheck = DictionaryLookup.applyReplacements(word, dictionaryMetadata.getInputConversionPairs());
    }
    boolean isAlphabetic = wordToCheck.length() != 1 || isAlphabetic(wordToCheck.charAt(0));
    return wordToCheck.length() > 0 && (!dictionaryMetadata.isIgnoringPunctuation() || isAlphabetic) && (!dictionaryMetadata.isIgnoringNumbers() || containsNoDigit(wordToCheck)) && !(dictionaryMetadata.isIgnoringCamelCase() && isCamelCase(wordToCheck)) && !(dictionaryMetadata.isIgnoringAllUppercase() && isAlphabetic && isAllUppercase(wordToCheck)) && !isInDictionary(wordToCheck) && (!dictionaryMetadata.isConvertingCase() || !(!isMixedCase(wordToCheck) && (isInDictionary(wordToCheck.toLowerCase(dictionaryMetadata.getLocale())) || isAllUppercase(wordToCheck) && isInDictionary(initialUppercase(wordToCheck)))));
  }

  private CharSequence initialUppercase(final String wordToCheck) {
    return wordToCheck.substring(0, 1) + wordToCheck.substring(1).toLowerCase(dictionaryMetadata.getLocale());
  }

  /**
   * Test whether the word is found in the dictionary.
   * 
   * @param word
   *          the word to be tested
   * @return True if it is found.
   */
  public boolean isInDictionary(final CharSequence word) {
    try {
      byteBuffer = charSequenceToBytes(word);
    } catch (UnmappableInputException e) {
      return false;
    }
    final MatchResult match = matcher.match(matchResult, byteBuffer.array(), 0, byteBuffer.remaining(), rootNode);
    if (containsSeparators && match.kind == EXACT_MATCH) {
      containsSeparators = false;
      for (int i = 0; i < word.length(); i++) {
        if (word.charAt(i) == dictionaryMetadata.getSeparator()) {
          containsSeparators = true;
          break;
        }
      }
    }
    if (match.kind == EXACT_MATCH && !containsSeparators) {
      return true;
    }
    return containsSeparators && match.kind == SEQUENCE_IS_A_PREFIX && byteBuffer.remaining() > 0 && fsa.getArc(match.node, dictionaryMetadata.getSeparator()) != 0;
  }

  /**
   * Get the frequency value for a word form. It is taken from the first entry
   * with this word form.
   * 
   * @param word
   *          the word to be tested
   * @return frequency value in range: 0..FREQ_RANGE-1 (0: less frequent).
   */
  public int getFrequency(final CharSequence word) {
    if (!dictionaryMetadata.isFrequencyIncluded()) {
      return 0;
    }
    final byte separator = dictionaryMetadata.getSeparator();
    try {
      byteBuffer = charSequenceToBytes(word);
    } catch (UnmappableInputException e) {
      return 0;
    }
    final MatchResult match = matcher.match(matchResult, byteBuffer.array(), 0, byteBuffer.remaining(), rootNode);
    if (match.kind == SEQUENCE_IS_A_PREFIX) {
      final int arc = fsa.getArc(match.node, separator);
      if (arc != 0 && !fsa.isArcFinal(arc)) {
        finalStatesIterator.restartFrom(fsa.getEndNode(arc));
        if (finalStatesIterator.hasNext()) {
          final ByteBuffer bb = finalStatesIterator.next();
          final byte[] ba = bb.array();
          final int bbSize = bb.remaining();
          return ba[bbSize - 1] - FIRST_RANGE_CODE;
        }
      }
    }
    return 0;
  }

  /**
   * Propose suggestions for misspelled run-on words. This algorithm is inspired
   * by spell.cc in s_fsa package by Jan Daciuk.
   * 
   * @param original
   *          The original misspelled word.
   * @return The list of suggested pairs, as space-concatenated strings.
   */
  public List<String> replaceRunOnWords(final String original) {
    final List<String> candidates = new ArrayList<String>();
    String wordToCheck = original;
    if (!dictionaryMetadata.getInputConversionPairs().isEmpty()) {
      wordToCheck = DictionaryLookup.applyReplacements(original, dictionaryMetadata.getInputConversionPairs());
    }
    if (!isInDictionary(wordToCheck) && dictionaryMetadata.isSupportingRunOnWords()) {
      for (int i = 1; i < wordToCheck.length(); i++) {
        final CharSequence firstCh = wordToCheck.subSequence(0, i);
        if (isInDictionary(firstCh) && isInDictionary(wordToCheck.subSequence(i, wordToCheck.length()))) {
          if (dictionaryMetadata.getOutputConversionPairs().isEmpty()) {
            candidates.add(firstCh + " " + wordToCheck.subSequence(i, wordToCheck.length()));
          } else {
            candidates.add(DictionaryLookup.applyReplacements(firstCh + " " + wordToCheck.subSequence(i, wordToCheck.length()), dictionaryMetadata.getOutputConversionPairs()).toString());
          }
        }
      }
    }
    return candidates;
  }

  /**
   * Find suggestions by using K. Oflazer's algorithm. See Jan Daciuk's s_fsa
   * package, spell.cc for further explanation.
   * 
   * @param w The original misspelled word.
   * @return A list of suggested replacements.
   */
  public List<String> findReplacements(final String w) {
    String word = w;
    if (!dictionaryMetadata.getInputConversionPairs().isEmpty()) {
      word = DictionaryLookup.applyReplacements(w, dictionaryMetadata.getInputConversionPairs());
    }
    candidates.clear();
    if (word.length() > 0 && word.length() < MAX_WORD_LENGTH && !isInDictionary(word)) {
      List<String> wordsToCheck = new ArrayList<String>();
      if (replacementsTheRest != null && word.length() > 1) {
        for (final String wordChecked : getAllReplacements(word, 0, 0)) {
          boolean found = false;
          if (isInDictionary(wordChecked)) {
            candidates.add(new CandidateData(wordChecked, 0));
            found = true;
          } else {
            String lowerWord = wordChecked.toLowerCase(dictionaryMetadata.getLocale());
            String upperWord = wordChecked.toUpperCase(dictionaryMetadata.getLocale());
            if (isInDictionary(lowerWord)) {
              candidates.add(new CandidateData(lowerWord, 0));
              found = true;
            }
            if (isInDictionary(upperWord)) {
              candidates.add(new CandidateData(upperWord, 0));
              found = true;
            }
            if (lowerWord.length() > 1) {
              String firstupperWord = Character.toUpperCase(lowerWord.charAt(0)) + lowerWord.substring(1);
              if (isInDictionary(firstupperWord)) {
                candidates.add(new CandidateData(firstupperWord, 0));
                found = true;
              }
            }
          }
          if (!found) {
            wordsToCheck.add(wordChecked);
          }
        }
      } else {
        wordsToCheck.add(word);
      }
      if (candidates.isEmpty()) {
        int i = 1;
        for (final String wordChecked : wordsToCheck) {
          i++;
          if (i > UPPER_SEARCH_LIMIT) {
            break;
          }
          wordProcessed = wordChecked.toCharArray();
          wordLen = wordProcessed.length;
          if (wordLen < MIN_WORD_LENGTH && i > 2) {
            break;
          }
          candidate = new char[MAX_WORD_LENGTH];
          candLen = candidate.length;
          effectEditDistance = wordLen <= editDistance ? wordLen - 1 : editDistance;
          charBuffer = BufferUtils.clearAndEnsureCapacity(charBuffer, MAX_WORD_LENGTH);
          byteBuffer = BufferUtils.clearAndEnsureCapacity(byteBuffer, MAX_WORD_LENGTH);
          final byte[] prevBytes = new byte[0];
          findRepl(0, fsa.getRootNode(), prevBytes, 0, 0);
        }
      }
    }
    Collections.sort(candidates);
    final Set<String> candStringSet = new LinkedHashSet<String>();
    for (final CandidateData cd : candidates) {
      candStringSet.add(DictionaryLookup.applyReplacements(cd.getWord(), dictionaryMetadata.getOutputConversionPairs()).toString());
    }
    final List<String> candStringList = new ArrayList<String>(candStringSet.size());
    candStringList.addAll(candStringSet);
    return candStringList;
  }

  private void findRepl(final int depth, final int node, final byte[] prevBytes, final int wordIndex, final int candIndex) {
    int dist = 0;
    for (int arc = fsa.getFirstArc(node); arc != 0; arc = fsa.getNextArc(arc)) {
      byteBuffer = BufferUtils.clearAndEnsureCapacity(byteBuffer, prevBytes.length + 1);
      byteBuffer.put(prevBytes);
      byteBuffer.put(fsa.getArcLabel(arc));
      final int bufPos = byteBuffer.position();
      byteBuffer.flip();
      decoder.reset();
      final CoderResult c = decoder.decode(byteBuffer, charBuffer, true);
      if (c.isMalformed()) {
        final byte[] prev = new byte[bufPos];
        byteBuffer.position(0);
        byteBuffer.get(prev);
        if (!fsa.isArcTerminal(arc)) {
          findRepl(depth, fsa.getEndNode(arc), prev, wordIndex, candIndex);
        }
        byteBuffer.clear();
      } else {
        if (!c.isError()) {
          charBuffer.flip();
          candidate[candIndex] = charBuffer.get();
          charBuffer.clear();
          byteBuffer.clear();
          int lengthReplacement;
          if ((lengthReplacement = matchAnyToTwo(wordIndex, candIndex)) > 0) {
            if (isEndOfCandidate(arc, wordIndex) && (dist = hMatrix.get(depth - 1, depth - 1)) <= effectEditDistance) {
              if (Math.abs(wordLen - 1 - (wordIndex + lengthReplacement - 2)) > 0) {
                dist = dist + Math.abs(wordLen - 1 - (wordIndex + lengthReplacement - 2));
              }
              if (dist <= effectEditDistance) {
                addCandidate(candIndex, dist);
              }
            }
            if (isArcNotTerminal(arc, candIndex)) {
              int x = hMatrix.get(depth, depth);
              hMatrix.set(depth, depth, hMatrix.get(depth - 1, depth - 1));
              findRepl(Math.max(0, depth), fsa.getEndNode(arc), new byte[0], wordIndex + lengthReplacement - 1, candIndex + 1);
              hMatrix.set(depth, depth, x);
            }
          }
          if ((lengthReplacement = matchAnyToOne(wordIndex, candIndex)) > 0) {
            if (isEndOfCandidate(arc, wordIndex) && (dist = hMatrix.get(depth, depth)) <= effectEditDistance) {
              if (Math.abs(wordLen - 1 - (wordIndex + lengthReplacement - 1)) > 0) {
                dist = dist + Math.abs(wordLen - 1 - (wordIndex + lengthReplacement - 1));
              }
              if (dist <= effectEditDistance) {
                addCandidate(candIndex, dist);
              }
            }
            if (isArcNotTerminal(arc, candIndex)) {
              findRepl(depth, fsa.getEndNode(arc), new byte[0], wordIndex + lengthReplacement, candIndex + 1);
            }
          }
          if (cuted(depth, wordIndex, candIndex) <= effectEditDistance) {
            if ((isEndOfCandidate(arc, wordIndex)) && (dist = ed(wordLen - 1 - (wordIndex - depth), depth, wordLen - 1, candIndex)) <= effectEditDistance) {
              addCandidate(candIndex, dist);
            }
            if (isArcNotTerminal(arc, candIndex)) {
              findRepl(depth + 1, fsa.getEndNode(arc), new byte[0], wordIndex + 1, candIndex + 1);
            }
          }
        }
      }
    }
  }

  private boolean isArcNotTerminal(final int arc, final int candIndex) {
    return !fsa.isArcTerminal(arc) && !(containsSeparators && candidate[candIndex] == dictionaryMetadata.getSeparatorAsChar());
  }

  private boolean isEndOfCandidate(final int arc, final int wordIndex) {
    return (fsa.isArcFinal(arc) || isBeforeSeparator(arc)) && (Math.abs(wordLen - 1 - (wordIndex)) <= effectEditDistance);
  }

  private boolean isBeforeSeparator(final int arc) {
    if (containsSeparators) {
      final int arc1 = fsa.getArc(fsa.getEndNode(arc), dictionaryMetadata.getSeparator());
      return arc1 != 0 && !fsa.isArcTerminal(arc1);
    }
    return false;
  }

  private void addCandidate(final int depth, final int dist) {
    candidates.add(new CandidateData(String.valueOf(candidate, 0, depth + 1), dist));
  }

  /**
   * Calculates edit distance.
   * 
   * @param i length of first word (here: misspelled) - 1;
   * @param j length of second word (here: candidate) - 1.
   * @param wordIndex (TODO: javadoc?)
   * @param candIndex (TODO: javadoc?)
   * @return Edit distance between the two words. Remarks: See Oflazer.
   */
  public int ed(final int i, final int j, final int wordIndex, final int candIndex) {
    int result;
    int a, b, c;
    if (areEqual(wordProcessed[wordIndex], candidate[candIndex])) {
      result = hMatrix.get(i, j);
    } else {
      if (wordIndex > 0 && candIndex > 0 && wordProcessed[wordIndex] == candidate[candIndex - 1] && wordProcessed[wordIndex - 1] == candidate[candIndex]) {
        a = hMatrix.get(i - 1, j - 1);
        b = hMatrix.get(i + 1, j);
        c = hMatrix.get(i, j + 1);
        result = 1 + min(a, b, c);
      } else {
        a = hMatrix.get(i, j);
        b = hMatrix.get(i + 1, j);
        c = hMatrix.get(i, j + 1);
        result = 1 + min(a, b, c);
      }
    }
    hMatrix.set(i + 1, j + 1, result);
    return result;
  }

  private boolean areEqual(final char x, final char y) {
    if (x == y) {
      return true;
    }
    if (dictionaryMetadata.getEquivalentChars() != null) {
      List<Character> chars = dictionaryMetadata.getEquivalentChars().get(x);
      if (chars != null && chars.contains(y)) {
        return true;
      }
    }
    if (dictionaryMetadata.isIgnoringDiacritics()) {
      String xn = Normalizer.normalize(Character.toString(x), Form.NFD);
      String yn = Normalizer.normalize(Character.toString(y), Form.NFD);
      if (xn.charAt(0) == yn.charAt(0)) {
        return true;
      }
      if (dictionaryMetadata.isConvertingCase()) {
        if (Character.isLetter(xn.charAt(0))) {
          boolean testNeeded = Character.isLowerCase(xn.charAt(0)) != Character.isLowerCase(yn.charAt(0));
          if (testNeeded) {
            return Character.toLowerCase(xn.charAt(0)) == Character.toLowerCase(yn.charAt(0));
          }
        }
      }
      return xn.charAt(0) == yn.charAt(0);
    }
    return false;
  }

  /**
   * Calculates cut-off edit distance.
   * 
   * @param depth current length of candidates.
   * @param wordIndex (TODO: javadoc?)
   * @param candIndex (TODO: javadoc?)
   * @return Cut-off edit distance. Remarks: See Oflazer.
   */
  public int cuted(final int depth, final int wordIndex, final int candIndex) {
    final int l = Math.max(0, depth - effectEditDistance);
    final int u = Math.min(wordLen - 1 - (wordIndex - depth), depth + effectEditDistance);
    int minEd = effectEditDistance + 1;
    int wi = wordIndex + l - depth;
    int d;
    for (int i = l; i <= u; i++, wi++) {
      if ((d = ed(i, depth, wi, candIndex)) < minEd) {
        minEd = d;
      }
    }
    return minEd;
  }

  private int matchAnyToOne(final int wordIndex, final int candIndex) {
    if (replacementsAnyToOne.containsKey(candidate[candIndex])) {
      for (final char[] rep : replacementsAnyToOne.get(candidate[candIndex])) {
        int i = 0;
        while (i < rep.length && (wordIndex + i) < wordLen && rep[i] == wordProcessed[wordIndex + i]) {
          i++;
        }
        if (i == rep.length) {
          return i;
        }
      }
    }
    return 0;
  }

  private int matchAnyToTwo(final int wordIndex, final int candIndex) {
    if (candIndex > 0 && candIndex < candidate.length && wordIndex > 0) {
      char[] twoChar = { candidate[candIndex - 1], candidate[candIndex] };
      String sTwoChar = new String(twoChar);
      if (replacementsAnyToTwo.containsKey(sTwoChar)) {
        for (final char[] rep : replacementsAnyToTwo.get(sTwoChar)) {
          if (rep.length == 2 && wordIndex < wordLen && candidate[candIndex - 1] == wordProcessed[wordIndex - 1] && candidate[candIndex] == wordProcessed[wordIndex]) {
            return 0;
          }
          int i = 0;
          while (i < rep.length && (wordIndex - 1 + i) < wordLen && rep[i] == wordProcessed[wordIndex - 1 + i]) {
            i++;
          }
          if (i == rep.length) {
            return i;
          }
        }
      }
    }
    return 0;
  }

  private static int min(final int a, final int b, final int c) {
    return Math.min(a, Math.min(b, c));
  }

  /**
   * Copy-paste of Character.isAlphabetic() (needed as we require only 1.6)
   * 
   * @param codePoint
   *          The input character.
   * @return True if the character is a Unicode alphabetic character.
   */
  static boolean isAlphabetic(final int codePoint) {
    return ((1 << Character.UPPERCASE_LETTER | 1 << Character.LOWERCASE_LETTER | 1 << Character.TITLECASE_LETTER | 1 << Character.MODIFIER_LETTER | 1 << Character.OTHER_LETTER | 1 << Character.LETTER_NUMBER) >> Character.getType(codePoint) & 1) != 0;
  }

  /**
   * Checks whether a string contains a digit. Used for ignoring words with
   * numbers
   * 
   * @param s
   *          Word to be checked.
   * @return True if there is a digit inside the word.
   */
  static boolean containsNoDigit(final String s) {
    for (int k = 0; k < s.length(); k++) {
      if (Character.isDigit(s.charAt(k))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if <code>str</code> is made up of all-uppercase characters
   * (ignoring characters for which no upper-/lowercase distinction exists).
   */
  boolean isAllUppercase(final String str) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (Character.isLetter(c) && Character.isLowerCase(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if <code>str</code> is made up of all-lowercase characters
   * (ignoring characters for which no upper-/lowercase distinction exists).
   */
  boolean isNotAllLowercase(final String str) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (Character.isLetter(c) && !Character.isLowerCase(c)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param str
   *          input string
   */
  boolean isNotCapitalizedWord(final String str) {
    if (isNotEmpty(str) && Character.isUpperCase(str.charAt(0))) {
      for (int i = 1; i < str.length(); i++) {
        char c = str.charAt(i);
        if (Character.isLetter(c) && !Character.isLowerCase(c)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  /**
   * Helper method to replace calls to "".equals().
   * 
   * @param str
   *          String to check
   * @return true if string is empty OR null
   */
  static boolean isNotEmpty(final String str) {
    return str != null && str.length() != 0;
  }

  /**
   * @param str
   *          input str
   * @return Returns true if str is MixedCase.
   */
  boolean isMixedCase(final String str) {
    return !isAllUppercase(str) && isNotCapitalizedWord(str) && isNotAllLowercase(str);
  }

  /**
   * @param str The string to check.
   * @return Returns true if str is CamelCase. Note that German compounds with a dash
   *         (like "Waschmaschinen-Test") are also considered camel case by this method.
   */
  public boolean isCamelCase(final String str) {
    return isNotEmpty(str) && !isAllUppercase(str) && isNotCapitalizedWord(str) && Character.isUpperCase(str.charAt(0)) && (!(str.length() > 1) || Character.isLowerCase(str.charAt(1))) && isNotAllLowercase(str);
  }

  /**
   * Used to determine whether the dictionary supports case conversions.
   * 
   * @return boolean value that answers this question in a deep and meaningful
   *         way.
   *
   * @since 1.9
   *
   */
  public boolean convertsCase() {
    return dictionaryMetadata.isConvertingCase();
  }

  /**
   * @param str
   *          The string to find the replacements for.
   * @param fromIndex
   *          The index from which replacements are found.
   * @param level
   *          The recursion level. The search stops if level is &gt; MAX_RECURSION_LEVEL.
   * @return A list of all possible replacements of a {#link str} given string
   */
  public List<String> getAllReplacements(final String str, final int fromIndex, final int level) {
    List<String> replaced = new ArrayList<>();
    if (level > MAX_RECURSION_LEVEL) {
      replaced.add(str);
      return replaced;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(str);
    int index = MAX_WORD_LENGTH;
    String key = "";
    int keyLength = 0;
    boolean found = false;
    for (final String auxKey : replacementsTheRest.keySet()) {
      int auxIndex = sb.indexOf(auxKey, fromIndex);
      if (auxIndex > -1 && (auxIndex < index || (auxIndex == index && !(auxKey.length() < keyLength)))) {
        index = auxIndex;
        key = auxKey;
        keyLength = auxKey.length();
      }
    }
    if (index < MAX_WORD_LENGTH) {
      for (final String rep : replacementsTheRest.get(key)) {
        if (!found) {
          replaced.addAll(getAllReplacements(str, index + key.length(), level + 1));
          found = true;
        }
        int ind = sb.indexOf(rep, fromIndex - rep.length() + 1);
        if (rep.length() > key.length() && ind > -1 && (ind == index || ind == index - rep.length() + 1)) {
          continue;
        }
        sb.replace(index, index + key.length(), rep);
        replaced.addAll(getAllReplacements(sb.toString(), index + rep.length(), level + 1));
        sb.setLength(0);
        sb.append(str);
      }
    }
    if (!found) {
      replaced.add(sb.toString());
    }
    return replaced;
  }

  /**
   * Sets up the word and candidate. Used only to test the edit distance in
   * JUnit tests.
   * 
   * @param word
   *          the first word
   * @param candidate
   *          the second word used for edit distance calculation
   */
  void setWordAndCandidate(final String word, final String candidate) {
    wordProcessed = word.toCharArray();
    wordLen = wordProcessed.length;
    this.candidate = candidate.toCharArray();
    candLen = this.candidate.length;
    effectEditDistance = wordLen <= editDistance ? wordLen - 1 : editDistance;
  }

  public final int getWordLen() {
    return wordLen;
  }

  public final int getCandLen() {
    return candLen;
  }

  public final int getEffectiveED() {
    return effectEditDistance;
  }

  private class CandidateData implements Comparable<CandidateData> {
    private final String word;

    private final int distance;

    CandidateData(final String word, final int distance) {
      this.word = word;
      this.distance = distance * FREQ_RANGES + FREQ_RANGES - getFrequency(word) - 1;
    }

    final String getWord() {
      return word;
    }

    final int getDistance() {
      return distance;
    }

    @Override public int compareTo(final CandidateData cd) {
      return cd.getDistance() > this.distance ? -1 : cd.getDistance() == this.distance ? 0 : 1;
    }
  }
}