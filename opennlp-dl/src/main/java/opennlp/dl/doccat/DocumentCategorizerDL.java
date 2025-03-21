package opennlp.dl.doccat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;
import ai.onnxruntime.OnnxTensor;
import opennlp.dl.InferenceOptions;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import opennlp.dl.Tokens;
import opennlp.dl.doccat.scoring.AverageClassifcationScoringStrategy;
import opennlp.dl.doccat.scoring.ClassificationScoringStrategy;
import opennlp.tools.doccat.DocumentCategorizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WordpieceTokenizer;

/**
 * An implementation of {@link DocumentCategorizer} that performs document classification
 * using ONNX models.
 */
public class DocumentCategorizerDL implements DocumentCategorizer {
  public static final String INPUT_IDS = "input_ids";

  public static final String ATTENTION_MASK = "attention_mask";

  public static final String TOKEN_TYPE_IDS = "token_type_ids";

  protected final OrtEnvironment env;

  protected final OrtSession session;

  private final 
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
  Tokenizer
=======
  Inference
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
   
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
  tokenizer
=======
  inference
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
  ;

  private final Map<Integer, String> categories;

  private final Map<String, Integer> vocabulary;

  private final ClassificationScoringStrategy classificationScoringStrategy;

  private static final int SPLIT_LENGTH = 125;

  /**
   * Creates a new document categorizer using ONNX models. This will calculate document scores
   * by averaging scores for individual document parts using the {@link AverageClassifcationScoringStrategy}.
   * @param model The ONNX model file.
   * @param vocab The model's vocabulary file.
   * @param categories The categories.
   */
  public DocumentCategorizerDL(File model, File vocab, Map<Integer, String> categories) throws IOException, Exception, OrtException {
    this(model, vocab, categories, new 
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
    AverageClassifcationScoringStrategy
=======
    DocumentCategorizerInference
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
    (model, vocab, new InferenceOptions()));
  }

  /**
   * Creates a new document categorizer using ONNX models.
   * @param model The ONNX model file.
   * @param vocab The model's vocabulary file.
   * @param categories The categories.
   * @param inferenceOptions The {@link InferenceOptions} used to customize the inference process.
   */
  public DocumentCategorizerDL(File model, File vocab, Map<Integer, String> categories, InferenceOptions inferenceOptions) throws Exception {
    this(categories, new DocumentCategorizerInference(model, vocab, inferenceOptions));
  }

  /**
   * Creates a new document categorizer using ONNX models.
   * @param model The ONNX model file.
   * @param vocab The model's vocabulary file.
   * @param categories The categories.
   * @param classificationScoringStrategy Implementation of {@link ClassificationScoringStrategy} used
   *                                      to calculate the classification scores given the score of each
   *                                      individual document part.
   */
  public DocumentCategorizerDL(Map<Integer, String> categories, 
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
  ClassificationScoringStrategy classificationScoringStrategy
=======
  Inference inference
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
  ) throws IOException, OrtException {
    this.env = OrtEnvironment.getEnvironment();
    this.session = env.createSession(model.getPath(), new OrtSession.SessionOptions());
    this.vocabulary = loadVocab(vocab);
    this.tokenizer = new WordpieceTokenizer(vocabulary.keySet());
    this.categories = categories;
    this.
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
    classificationScoringStrategy
=======
    inference
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
     = 
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
    classificationScoringStrategy
=======
    inference
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
    ;
  }

  @Override public double[] categorize(String[] strings) {
    try {

<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
      final List<Tokens> tokens = tokenize(strings[0]);
=======
      final Object output = inference.infer(strings[0]);
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java

      final List<double[]> 
<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/left.java
      scores = new LinkedList<>()
=======
      vectors = inference.convertFloatsToDoubles((float[][]) output)
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/doccat/DocumentCategorizerDL.java/right.java
      ;
      for (final Tokens t : tokens) {
        final Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put(INPUT_IDS, OnnxTensor.createTensor(env, LongBuffer.wrap(t.getIds()), new long[] { 1, t.getIds().length }));
        inputs.put(ATTENTION_MASK, OnnxTensor.createTensor(env, LongBuffer.wrap(t.getMask()), new long[] { 1, t.getMask().length }));
        inputs.put(TOKEN_TYPE_IDS, OnnxTensor.createTensor(env, LongBuffer.wrap(t.getTypes()), new long[] { 1, t.getTypes().length }));
        final float[][] v = (float[][]) session.run(inputs).get(0).getValue();
        final double[] categoryScoresForTokens = softmax(v[0]);
        scores.add(categoryScoresForTokens);
      }
      return classificationScoringStrategy.score(scores);
    } catch (Exception ex) {
      System.err.println("Unload to perform document classification inference: " + ex.getMessage());
    }
    return new double[] {  };
  }

  @Override public double[] categorize(String[] strings, Map<String, Object> map) {
    return categorize(strings);
  }

  @Override public String getBestCategory(double[] doubles) {
    return categories.get(maxIndex(doubles));
  }

  @Override public int getIndex(String s) {
    return getKey(s);
  }

  @Override public String getCategory(int i) {
    return categories.get(i);
  }

  @Override public int getNumberOfCategories() {
    return categories.size();
  }

  @Override public String getAllResults(double[] doubles) {
    return null;
  }

  @Override public Map<String, Double> scoreMap(String[] strings) {
    final double[] scores = categorize(strings);
    final Map<String, Double> scoreMap = new HashMap<>();
    for (int x : categories.keySet()) {
      scoreMap.put(categories.get(x), scores[x]);
    }
    return scoreMap;
  }

  @Override public SortedMap<Double, Set<String>> sortedScoreMap(String[] strings) {
    final double[] scores = categorize(strings);
    final SortedMap<Double, Set<String>> scoreMap = new TreeMap<>();
    for (int x : categories.keySet()) {
      if (scoreMap.get(scores[x]) == null) {
        scoreMap.put(scores[x], new HashSet<>());
      }
      scoreMap.get(scores[x]).add(categories.get(x));
    }
    return scoreMap;
  }

  private int getKey(String value) {
    for (Map.Entry<Integer, String> entry : categories.entrySet()) {
      if (entry.getValue().equals(value)) {
        return entry.getKey();
      }
    }
    return -1;
  }

  /**
   * Loads a vocabulary file from disk.
   * @param vocab The vocabulary file.
   * @return A map of vocabulary words to integer IDs.
   * @throws IOException Thrown if the vocabulary file cannot be opened and read.
   */
  private Map<String, Integer> loadVocab(File vocab) throws IOException {
    final Map<String, Integer> v = new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(vocab.getPath()));
    String line = br.readLine();
    int x = 0;
    while (line != null) {
      line = br.readLine();
      x++;
      v.put(line, x);
    }
    return v;
  }

  private Tokens oldTokenize(String text) {
    final String[] tokens = tokenizer.tokenize(text);
    final int[] ids = new int[tokens.length];
    for (int x = 0; x < tokens.length; x++) {
      ids[x] = vocabulary.get(tokens[x]);
    }
    final long[] lids = Arrays.stream(ids).mapToLong((i) -> i).toArray();
    final long[] mask = new long[ids.length];
    Arrays.fill(mask, 1);
    final long[] types = new long[ids.length];
    Arrays.fill(types, 0);
    return new Tokens(tokens, lids, mask, types);
  }

  private List<Tokens> tokenize(final String text) {
    final List<Tokens> t = new LinkedList<>();
    final String[] whitespaceTokenized = text.split("\\s+");
    for (int start = 0; start < whitespaceTokenized.length; start = start + SPLIT_LENGTH) {
      int end = start + SPLIT_LENGTH;
      if (end > whitespaceTokenized.length) {
        end = whitespaceTokenized.length;
      }
      final String group = String.join(" ", Arrays.copyOfRange(whitespaceTokenized, start, end));
      start = start - 50;
      final String[] tokens = tokenizer.tokenize(group);
      final int[] ids = new int[tokens.length];
      for (int x = 0; x < tokens.length; x++) {
        ids[x] = vocabulary.get(tokens[x]);
      }
      final long[] lids = Arrays.stream(ids).mapToLong((i) -> i).toArray();
      final long[] mask = new long[ids.length];
      Arrays.fill(mask, 1);
      final long[] types = new long[ids.length];
      Arrays.fill(types, 0);
      t.add(new Tokens(tokens, lids, mask, types));
    }
    return t;
  }

  /**
   * Applies softmax to an array of values.
   * @param input An array of values.
   * @return The output array.
   */
  private double[] softmax(final float[] input) {
    final double[] t = new double[input.length];
    double sum = 0.0;
    for (int x = 0; x < input.length; x++) {
      double val = Math.exp(input[x]);
      sum += val;
      t[x] = val;
    }
    final double[] output = new double[input.length];
    for (int x = 0; x < output.length; x++) {
      output[x] = (float) (t[x] / sum);
    }
    return output;
  }

  private int maxIndex(double[] arr) {
    return IntStream.range(0, arr.length).reduce((i, j) -> arr[i] > arr[j] ? i : j).orElse(-1);
  }
}