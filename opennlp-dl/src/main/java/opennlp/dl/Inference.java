package opennlp.dl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WordpieceTokenizer;

/**
 * An abstract class used by OpenNLP implementations using ONNX models.
 */
public abstract class Inference {
  public static final String INPUT_IDS = "input_ids";

  public static final String ATTENTION_MASK = "attention_mask";

  public static final String TOKEN_TYPE_IDS = "token_type_ids";

  protected final OrtEnvironment env;

  protected final OrtSession session;

  private final Tokenizer tokenizer;

  private final Map<String, Integer> vocabulary;


<<<<<<< /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/Inference.java/left.java
  private static final int SPLIT_LENGTH = 125;
=======
  protected InferenceOptions inferenceOptions;
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/Inference.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  public abstract Object infer(String text) throws Exception;
>>>>>>> /usr/src/app/output/apache/opennlp/2f90d876fbdf09a6e1453c8bafd4ea4cdedb574e/opennlp-dl/src/main/java/opennlp/dl/Inference.java/right.java


  /**
   * Instantiates a new inference class.
   * @param model The ONNX model file.
   * @param vocab The model's vocabulary file.
   * @throws OrtException Thrown if the ONNX model cannot be loaded.
   * @throws IOException Thrown if the ONNX model or vocabulary files cannot be opened or read.
   */
  public Inference(File model, File vocab, InferenceOptions inferenceOptions) throws OrtException, IOException {
    this.env = OrtEnvironment.getEnvironment();
    final OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
    if (inferenceOptions.isGpu()) {
      sessionOptions.addCUDA(inferenceOptions.getGpuDeviceId());
    }
    this.session = env.createSession(model.getPath(), sessionOptions);
    this.vocabulary = loadVocab(vocab);
    this.tokenizer = new WordpieceTokenizer(vocabulary.keySet());
    this.inferenceOptions = inferenceOptions;
  }

  /**
   * Tokenize the input text using the {@link WordpieceTokenizer}.
   * @param text The input text.
   * @return The input text's {@link Tokens}.
   */
  public List<Tokens> tokenize(final String text) {
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
   * Loads a vocabulary file from disk.
   * @param vocab The vocabulary file.
   * @return A map of vocabulary words to integer IDs.
   * @throws IOException Thrown if the vocabulary file cannot be opened and read.
   */
  public Map<String, Integer> loadVocab(File vocab) throws IOException {
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

  public static int maxIndex(double[] arr) {
    return IntStream.range(0, arr.length).reduce((i, j) -> arr[i] > arr[j] ? i : j).orElse(-1);
  }

  /**
   * Applies softmax to an array of values.
   * @param input An array of values.
   * @return The output array.
   */
  public double[] softmax(final double[] input) {
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

  /**
   * Converts a two-dimensional float array to doubles.
   * @param input The input array.
   * @return The converted array.
   */
  public double[][] convertFloatsToDoubles(float[][] input) {
    final double[][] outputs = new double[input.length][input[0].length];
    for (int i = 0; i < input.length; i++) {
      for (int j = 0; j < input[0].length; j++) {
        outputs[i][j] = (double) input[i][j];
      }
    }
    return outputs;
  }

  /**
   * Converts a three-dimensional float array to doubles.
   * @param input The input array.
   * @return The converted array.
   */
  public double[] convertFloatsToDoubles(float[] input) {
    final double[] output = new double[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = input[i];
    }
    return output;
  }
}