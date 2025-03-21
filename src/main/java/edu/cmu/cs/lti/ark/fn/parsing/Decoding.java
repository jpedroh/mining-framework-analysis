package edu.cmu.cs.lti.ark.fn.parsing;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.*;
import edu.cmu.cs.lti.ark.util.FileUtil;
import edu.cmu.cs.lti.ark.util.ds.Range0Based;
import edu.cmu.cs.lti.ark.util.ds.Scored;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;
import java.util.*;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;
import static edu.cmu.cs.lti.ark.util.ds.Scored.scored;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;

/**
 * Predict spans for roles using beam search.
 */
public class Decoding {
  private static final int DEFAULT_BEAM_WIDTH = 100;

  private static final Joiner TAB_JOINER = Joiner.on("\t");

  protected double[] modelWeights;

  public static class RoleAssignments implements Comparable<RoleAssignments> {
    private final static Function<Map.Entry<String, Range0Based>, String> JOIN_ENTRY = new Function<Map.Entry<String, Range0Based>, String>() {
      @Override public String apply(Map.Entry<String, Range0Based> input) {
        return TAB_JOINER.join(input.getKey(), spanToString(input.getValue()));
      }
    };

    private final PMap<String, Range0Based> nonNullAssignments;

    private final PMap<String, Range0Based> nullAssignments;

    public RoleAssignments(PMap<String, Range0Based> nonNullAssignments, PMap<String, Range0Based> nullAssignments) {
      this.nonNullAssignments = nonNullAssignments;
      this.nullAssignments = nullAssignments;
    }

    public RoleAssignments() {
      this(HashTreePMap.<String, Range0Based>empty(), HashTreePMap.<String, Range0Based>empty());
    }

    public RoleAssignments plus(String key, Range0Based value) {
      if (value.isEmpty()) {
        return new RoleAssignments(nonNullAssignments, nullAssignments.plus(key, value));
      } else {
        return new RoleAssignments(nonNullAssignments.plus(key, value), nullAssignments);
      }
    }

    private static String spanToString(Range0Based span) {
      return (span.start == span.end) ? ("" + span.start) : (span.start + ":" + span.end);
    }

    private Map<String, Range0Based> getNonNullAssignments() {
      return nonNullAssignments;
    }

    /** Determines whether the given span overlaps with any of our spans */
    private boolean overlaps(Range0Based otherSpan) {
      if (otherSpan.isEmpty()) {
        return false;
      }
      for (Range0Based span : getNonNullAssignments().values()) {
        if (span.overlaps(otherSpan)) {
          return true;
        }
      }
      return false;
    }

    @Override public String toString() {
      return TAB_JOINER.join(transform(getNonNullAssignments().entrySet(), JOIN_ENTRY));
    }

    @Override public int compareTo(RoleAssignments other) {
      return Ordering.arbitrary().compare(this, other);
    }
  }

  public static class CandidatesForRole extends TreeSet<Scored<Range0Based>> {
  }

  public Decoding(double[] modelWeights) {
    this.modelWeights = modelWeights;
  }

  public static Decoding fromFile(String modelFile, String alphabetFile) {
    return new Decoding(readModel(modelFile, alphabetFile));
  }

  protected static double[] readModel(String modelFile, String alphabetFile) {
    System.out.println("alphabet file: " + alphabetFile);
    final Scanner localsc = FileUtil.openInFile(alphabetFile);
    final int numLocalFeatures;
    try {
      numLocalFeatures = localsc.nextInt() + 1;
    }  finally {
      localsc.close();
    }
    final Scanner scanner = FileUtil.openInFile(modelFile);
    final double[] modelWeights = new double[numLocalFeatures];
    try {
      System.out.println("numLocalFeatures = " + numLocalFeatures);
      for (int i = 0; i < numLocalFeatures; i++) {
        modelWeights[i] = Double.parseDouble(scanner.nextLine());
      }
    }  finally {
      scanner.close();
    }
    return modelWeights;
  }

  public List<String> decodeAll(List<FrameFeatures> frameFeaturesList, List<String> frameLines, int offset, int kBestOutput) {
    final ArrayList<String> results = new ArrayList<>();
    for (int i = 0; i < frameFeaturesList.size(); i++) {
      final FrameFeatures frameFeatures = frameFeaturesList.get(i);
      final String initialDecisionLine = getInitialDecisionLine(frameLines.get(i), offset);
      final List<Scored<RoleAssignments>> predictions = getPredictions(frameFeatures, kBestOutput);
      final List<String> predictionLines = Lists.newArrayList();
      for (int j = 0; j < predictions.size(); j++) {
        final Scored<RoleAssignments> prediction = predictions.get(j);
        predictionLines.add(formatPrediction(j, initialDecisionLine, prediction.value, prediction.score));
      }
      results.add(Joiner.on("\n").join(predictionLines));
    }
    return results;
  }

  private String formatPrediction(int rank, String initialDecisionLine, RoleAssignments assignments, double score) {
    return TAB_JOINER.join(rank, score, assignments.getNonNullAssignments().size() + 1, initialDecisionLine, assignments.toString());
  }

  /**
	 * Calculates the sum of the weights of firing features.
	 *
	 * @param feats indexes of firing features
	 * @param weights an array of weights into which feats indexes
	 * @return the sum of the weights of firing features
	 */
  public static double getWeightSum(int[] feats, double[] weights) {
    double weightSum = weights[0];
    for (int feat : feats) {
      if (feat != 0) {
        weightSum += weights[feat];
      }
    }
    return weightSum;
  }

  /** Adds 'offset' to the sentence field and discards the 1st two fields. */
  protected String getInitialDecisionLine(String frameLine, int offset) {
    String[] frameTokens = frameLine.split("\t");
    frameTokens[7] = "" + (parseInt(frameTokens[7]) + offset);
    return TAB_JOINER.join(copyOf(frameTokens).subList(3, frameTokens.length)).trim();
  }

  private static <T extends java.lang.Object> List<T> safeTruncate(List<T> list, int beamWidth) {
    return list.subList(0, min(list.size(), beamWidth));
  }

  /**
	 * Decode, respecting the constraint that arguments do not overlap.
	 * Find the k (approximately) best configurations of non-overlapping role-filling spans using beam search.
	 *
	 * @param frameFeatures features for the given frame
	 * @param kBestOutput the number of top configurations we should return
	 * @return a list of Strings encoding the best k configurations of spans for all roles of the given frame
	 */
  public List<Scored<RoleAssignments>> getPredictions(FrameFeatures frameFeatures, int kBestOutput) {
    final Map<String, CandidatesForRole> candidatesAndScoresByRole = scoreCandidatesForRoles(frameFeatures.fElements, frameFeatures.fElementSpansAndFeatures);
    List<Scored<RoleAssignments>> currentBeam = Lists.newArrayList(scored(new RoleAssignments(), 0.0));
    for (String roleName : candidatesAndScoresByRole.keySet()) {
      final PriorityQueue<Scored<RoleAssignments>> newBeam = Queues.newPriorityQueue();
      for (Scored<Range0Based> candidate : candidatesAndScoresByRole.get(roleName)) {
        for (Scored<RoleAssignments> partialAssignment : currentBeam) {
          final double newScore = partialAssignment.score + candidate.score;
          if (newBeam.size() >= DEFAULT_BEAM_WIDTH && newScore <= newBeam.peek().score) {
            break;
          }
          if (!partialAssignment.value.overlaps(candidate.value)) {
            final RoleAssignments newAssignment = partialAssignment.value.plus(roleName, candidate.value);
            newBeam.add(scored(newAssignment, newScore));
          }
          if (newBeam.size() > DEFAULT_BEAM_WIDTH) {
            newBeam.poll();
          }
        }
      }
      currentBeam = copyOf(newBeam);
    }
    return safeTruncate(currentBeam, kBestOutput);
  }

  private Map<String, CandidatesForRole> scoreCandidatesForRoles(List<String> roleNames, List<SpanAndFeatures[]> featuresList) {
    final Map<String, CandidatesForRole> results = Maps.newHashMap();
    for (int i = 0; i < featuresList.size(); i++) {
      final String roleName = roleNames.get(i);
      final CandidatesForRole candidatesForRole = new CandidatesForRole();
      for (SpanAndFeatures spanAndFeatures : featuresList.get(i)) {
        final Range0Based span = spanAndFeatures.span();
        final double logScore = getWeightSum(spanAndFeatures.features(), modelWeights);
        candidatesForRole.add(scored(span, logScore));
      }
      results.put(roleName, candidatesForRole);
    }
    return results;
  }
}