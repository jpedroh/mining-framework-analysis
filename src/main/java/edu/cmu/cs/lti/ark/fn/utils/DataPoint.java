package edu.cmu.cs.lti.ark.fn.utils;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import edu.cmu.cs.lti.ark.fn.parsing.CandidateSpanPruner;
import edu.cmu.cs.lti.ark.util.ds.Range;
import edu.cmu.cs.lti.ark.util.ds.Range0Based;
import edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParse;
import gnu.trove.THashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class DataPoint {
  protected DependencyParse parse;

  protected String frameName;

  protected int[] targetTokenIdxs;

  protected int sentNum;

  protected String dataSet;

  /**
	 * Maps token numbers in the sentence to corresponding character indices
	 * @see #processOrgLine(String)
	 * @see #getCharacterIndicesForToken(int)
	 */
  private THashMap<Integer, Range0Based> tokenIndexMap;

  /**
	 * Given a sentence tokenized with space separators, populates tokenIndexMap with mappings 
	 * from token numbers to strings in the format StartCharacterOffset\tEndCharacterOffset
	 */
  public void processOrgLine(String tokenizedSentence) {
    tokenIndexMap = getCharOffsetsOfTokens(tokenizedSentence);
  }

  public static THashMap<Integer, Range0Based> getCharOffsetsOfTokens(String tokenizedSentence) {
    final StringTokenizer st = new StringTokenizer(tokenizedSentence.trim(), " ", true);
    final THashMap<Integer, Range0Based> localTokenIndexMap = new THashMap<>();
    int count = 0;
    int tokNum = 0;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (token.equals(" ")) {
        count++;
        continue;
      }
      token = token.trim();
      int start = count;
      int end = count + token.length() - 1;
      localTokenIndexMap.put(tokNum, new Range0Based(start, end));
      tokNum++;
      count += token.length();
    }
    return localTokenIndexMap;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void processFrameLine(String frameLine) {
    final String[] tokens = frameLine.split("\t");
    frameName = tokens[0].intern();
    sentNum = parseInt(tokens[4]);
    lexicalUnitName = tokens[1].intern();
    String[] tokNums = tokens[2].split("_");
    targetTokenIdxs = new int[tokNums.length];
    for (int j = 0; j < tokNums.length; j++) {
      targetTokenIdxs[j] = parseInt(tokNums[j]);
    }
    Arrays.sort(targetTokenIdxs);
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/utils/DataPoint.java/right.java


  public DependencyParse getParse() {
    return parse;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  protected static Pair<String, Integer> parseFrameNameAndSentenceNum(String frameLine) {
    String[] toks = frameLine.split("\t");
    String frameName = toks[0].intern();
    int sentNum = Integer.parseInt(toks[4]);
    return new Pair<String, Integer>(frameName, sentNum);
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/utils/DataPoint.java/right.java


  public String getFrameName() {
    return frameName;
  }

  public int[] getTargetTokenIdxs() {
    return targetTokenIdxs;
  }

  public int getSentenceNum() {
    return sentNum;
  }

  public static DependencyParse[] buildParsesForLine(String parseLine) {
    StringTokenizer st = new StringTokenizer(parseLine, "\t");
    int numWords = Integer.parseInt(st.nextToken());
    String[] parts = new String[6];
    String nextToken = st.nextToken().trim();
    for (int p = 0; p < 6; p++) {
      parts[p] = "";
      while (true) {
        for (int j = 0; j < numWords; j++) {
          String tkn = (j == 0) ? nextToken : st.nextToken().trim();
          parts[p] += tkn + "\t";
        }
        parts[p] = parts[p].trim();
        if (st.hasMoreElements()) {
          nextToken = st.nextToken().trim();
          if (nextToken.equals("|")) {
            parts[p] += "\t||\t";
            nextToken = st.nextToken().trim();
            continue;
          }
        }
        break;
      }
    }
    DependencyParse[] dependencyParses = DependencyParse.buildParseTrees(parts, 0.0);
    for (DependencyParse parse : dependencyParses) {
      parse.processSentence();
    }
    return dependencyParses;
  }

  public Range getCharacterIndicesForToken(int tokenNum) {
    return tokenIndexMap.get(tokenNum);
  }

  public List<Range0Based> getTokenStartEnds() {
    for (int tknNum : targetTokenIdxs) {
      if (!oCurrent.isPresent()) {
        oCurrent = Optional.of(new Range0Based(tknNum, tknNum));
      } else {
        final Range0Based current = oCurrent.get();
        if (mergeAdjacent && current.start == tknNum - 1) {
          oCurrent = Optional.of(new Range0Based(current.start, tknNum));
        } else {
          result.add(current);
          oCurrent = Optional.of(new Range0Based(tknNum, tknNum));
        }
      }
    }
    return getContiguousSpans(this.targetTokenIdxs);
  }

  public static List<Range0Based> getContiguousSpans(int[] tokenIdxs) {
    final List<Range0Based> result = Lists.newArrayList();
    Optional<Range0Based> oCurrent = Optional.absent();
    for (int tknNum : tokenIdxs) {
      if (!oCurrent.isPresent()) {
        oCurrent = Optional.of(new Range0Based(tknNum, tknNum));
      } else {
        final Range0Based current = oCurrent.get();
        if (current.start == tknNum - 1) {
          oCurrent = Optional.of(new Range0Based(current.start, tknNum));
        } else {
          result.add(current);
          oCurrent = Optional.of(new Range0Based(tknNum, tknNum));
        }
      }
    }
    if (oCurrent.isPresent()) {
      result.add(oCurrent.get());
    }
    return result;
  }

  public List<Range0Based> getCharStartEnds(List<Range0Based> tokenSpans) {
    final List<Range0Based> result = Lists.newArrayList();
    for (Range0Based tokenSpan : tokenSpans) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      final Range0Based charRange = new Range0Based(tokenIndexMap.get(tokenRange.start).start, tokenIndexMap.get(tokenRange.end).end);
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/utils/DataPoint.java/right.java

      result.add(getCharSpan(tokenSpan, tokenIndexMap));
    }
    return result;
  }

  public static Range0Based getCharSpan(Range0Based tokenSpan, THashMap<Integer, Range0Based> tokenIndexMap) {
    return new Range0Based(tokenIndexMap.get(tokenSpan.start).start, tokenIndexMap.get(tokenSpan.end).end);
  }

  public static final String FN13_LEXICON_EXEMPLARS = "exemplars";

  public static final String SEMEVAL07_TRAIN_SET = "train";

  public static final String SEMEVAL07_DEV_SET = "dev";

  public static final String SEMEVAL07_TEST_SET = "test";

  /** Sentence index ranges for documents in the train, dev, and test portions of the SemEval'07 data */
  protected static final Map<String, Map<String, ? extends Range>> DOCUMENT_SENTENCE_RANGES = new THashMap<String, Map<String, ? extends Range>>();

  static {
    {
      Map<String, Range0Based> exemplarMap = new THashMap<String, Range0Based>();
      exemplarMap.put("*", new Range0Based(0, 139439, false));
      DOCUMENT_SENTENCE_RANGES.put(FN13_LEXICON_EXEMPLARS, exemplarMap);
    }
    {
      Map<String, Range0Based> trainMap = new THashMap<String, Range0Based>();
      trainMap.put("ANC/EntrepreneurAsMadonna", new Range0Based(0, 33, false));
      trainMap.put("ANC/HistoryOfJerusalem", new Range0Based(171, 292, false));
      trainMap.put("NTI/BWTutorial_chapter1", new Range0Based(292, 393, false));
      trainMap.put("NTI/Iran_Chemical", new Range0Based(393, 536, false));
      trainMap.put("NTI/Iran_Introduction", new Range0Based(536, 598, false));
      trainMap.put("NTI/Iran_Missile", new Range0Based(598, 778, false));
      trainMap.put("NTI/Iran_Nuclear", new Range0Based(778, 913, false));
      trainMap.put("NTI/Kazakhstan", new Range0Based(913, 942, false));
      trainMap.put("NTI/LibyaCountry1", new Range0Based(942, 983, false));
      trainMap.put("NTI/NorthKorea_ChemicalOverview", new Range0Based(983, 1055, false));
      trainMap.put("NTI/NorthKorea_NuclearCapabilities", new Range0Based(1055, 1085, false));
      trainMap.put("NTI/NorthKorea_NuclearOverview", new Range0Based(1085, 1206, false));
      trainMap.put("NTI/Russia_Introduction", new Range0Based(1206, 1247, false));
      trainMap.put("NTI/SouthAfrica_Introduction", new Range0Based(1247, 1300, false));
      trainMap.put("NTI/Syria_NuclearOverview", new Range0Based(1300, 1356, false));
      trainMap.put("NTI/Taiwan_Introduction", new Range0Based(1356, 1392, false));
      trainMap.put("NTI/WMDNews_062606", new Range0Based(1392, 1476, false));
      trainMap.put("PropBank/PropBankCorpus", new Range0Based(1476, 1801, false));
      DOCUMENT_SENTENCE_RANGES.put(SEMEVAL07_TRAIN_SET, trainMap);
    }
    {
      Map<String, Range0Based> devMap = new THashMap<String, Range0Based>();
      devMap.put("ANC/StephanopoulosCrimes", new Range0Based(146, 178, false));
      devMap.put("NTI/Iran_Biological", new Range0Based(178, 280, false));
      devMap.put("NTI/NorthKorea_Introduction", new Range0Based(280, 329, false));
      devMap.put("NTI/WMDNews_042106", new Range0Based(329, 397, false));
      DOCUMENT_SENTENCE_RANGES.put(SEMEVAL07_DEV_SET, devMap);
    }
    {
      Map<String, Range0Based> testMap = new THashMap<String, Range0Based>();
      testMap.put("ANC/IntroOfDublin", new Range0Based(0, 67, false));
      testMap.put("NTI/chinaOverview", new Range0Based(67, 106, false));
      testMap.put("NTI/workAdvances", new Range0Based(106, 120, false));
      DOCUMENT_SENTENCE_RANGES.put(SEMEVAL07_TEST_SET, testMap);
    }
  }
}