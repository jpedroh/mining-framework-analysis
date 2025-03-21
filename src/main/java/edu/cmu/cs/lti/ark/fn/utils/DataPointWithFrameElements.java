package edu.cmu.cs.lti.ark.fn.utils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.Sentence;
import edu.cmu.cs.lti.ark.fn.parsing.RankedScoredRoleAssignment;
import edu.cmu.cs.lti.ark.util.ds.Range0Based;
import edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParse;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class DataPointWithFrameElements extends DataPoint {
  private final List<FrameElementAndSpan> frameElementsAndSpans;

  public final int rank;

  public final double score;

  public static class FrameElementAndSpan {
    public final String name;

    public final Range0Based span;

    public FrameElementAndSpan(String name, Range0Based span) {
      this.name = name;
      this.span = span;
    }
  }

  public DataPointWithFrameElements(Sentence sentence, String frameElementsLine) {
    this(sentence.toDependencyParse(), frameElementsLine.trim(), null);
  }

  public DataPointWithFrameElements(DependencyParse parse, String frameElementsLine, String dataSet) {
    this(parse, RankedScoredRoleAssignment.fromLine(frameElementsLine), dataSet);
  }

  public DataPointWithFrameElements(DependencyParse parse, RankedScoredRoleAssignment roleAssignment, String dataSet) {
    this.parse = parse;
    this.dataSet = dataSet;
    this.rank = roleAssignment.rank();
    this.score = roleAssignment.score();
    this.frameName = roleAssignment.frame();
    this.sentNum = roleAssignment.sentenceIdx();
    this.targetTokenIdxs = new int[roleAssignment.targetSpan().length()];
    for (int i = 0; i < roleAssignment.targetSpan().length(); i++) {
      targetTokenIdxs[i] = roleAssignment.targetSpan().start + i;
    }
    this.frameElementsAndSpans = ImmutableList.copyOf(roleAssignment.fesAndSpans());
  }

  /**
	 * @return An array listing, in the order they were annotated in the XML file, the frame element names 
	 * (of this frame) corresponding to annotated filler spans in the sentence. The same element name may be 
	 * listed multiple times. Elements filled by null instantiations are not included.
	 */
  public List<String> getOvertFrameElementNames() {
    return Lists.transform(frameElementsAndSpans, new Function<FrameElementAndSpan, String>() {
      @Nullable @Override public String apply(FrameElementAndSpan input) {
        return input.name;
      }
    });
  }

  /**
	 * @return A list of 0-based word token index ranges (startIndex, endIndex) (inclusive) delimiting spans which are 
	 * frame element fillers. This list is parallel to the list of frame element names returned by
	 * {@link #getOvertFrameElementNames()}.
	 */
  public List<Range0Based> getOvertFrameElementSpans() {
    return Lists.transform(frameElementsAndSpans, new Function<FrameElementAndSpan, Range0Based>() {
      @Nullable @Override public Range0Based apply(FrameElementAndSpan input) {
        return input.span;
      }
    });
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * Produces a frame elements line representation of a specified frame annotation. 
	 * Result does not end in a newline.
	 * 
	 * @param arguments Map from role names to filler argument token ranges
	 * @param frameName
	 * @param lexicalUnit
	 * @param tokenNums Token numbers for the target
	 * @param target The target word(s), separated by spaces
	 * @param sentNum
	 * @return
	 */
  public static String makeFrameElementsLine(Map<String, Range0Based> arguments, String frameName, String lexicalUnit, int[] tokenNums, String target, int sentNum) {
    String s = makeFrameLine(frameName, lexicalUnit, tokenNums, target, sentNum) + "\t";
    int numNonemptySpans = 0;
    for (Map.Entry<String, Range0Based> argument : arguments.entrySet()) {
      Range0Based span = argument.getValue();
      if (CandidateFrameElementFilters.isEmptySpan(span)) {
        continue;
      }
      String rangeS = "" + span.start;
      if (span.length() > 1) {
        rangeS += ":" + (span.start + span.length());
      }
      s += argument.getKey() + "\t" + rangeS + "\t";
      numNonemptySpans++;
    }
    return (numNonemptySpans + 1) + "\t" + s.trim();
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/utils/DataPointWithFrameElements.java/right.java


  public List<FrameElementAndSpan> getFrameElementsAndSpans() {
    return frameElementsAndSpans;
  }

  public static String getTokens(String sentence, int[] intNums) {
    StringTokenizer st = new StringTokenizer(sentence, " ", true);
    int count = 0;
    String result = "";
    Arrays.sort(intNums);
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim().intern();
      if (token.equals("")) {
        continue;
      }
      if (Arrays.binarySearch(intNums, count) >= 0) {
        result += token + " ";
      }
      count++;
    }
    return result.trim();
  }
}