package opennlp.dl.doccat;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ai.onnxruntime.OrtException;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import opennlp.dl.AbstactDLTest;
import opennlp.dl.InferenceOptions;

public class DocumentCategorizerDLEval extends AbstactDLTest {
  @Test public void categorize() throws IOException, Exception, OrtException {
    final File model = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.onnx");
    final File vocab = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.vocab");
    final DocumentCategorizerDL documentCategorizerDL = new DocumentCategorizerDL(model, vocab, getCategories());
    final Lorem lorem = LoremIpsum.getInstance();
    final String text = lorem.getParagraphs(100, 200);
    final double[] result = documentCategorizerDL.categorize(new String[] { text });
    final double[] expected = new double[] { 0.007819971069693565, 0.006593209225684404, 0.04995147883892059, 0.3003573715686798, 0.6352779865264893 };
    Assert.assertTrue(Arrays.equals(expected, result));
    Assert.assertEquals(5, result.length);
    final String category = documentCategorizerDL.getBestCategory(result);
    Assert.assertEquals(text, category);
  }

  @Ignore(value = "This test will only run if a GPU device is present.") @Test public void categorizeWithGpu() throws Exception {
    final File model = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.onnx");
    final File vocab = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.vocab");
    final InferenceOptions inferenceOptions = new InferenceOptions();
    inferenceOptions.setGpu(true);
    inferenceOptions.setGpuDeviceId(0);
    final DocumentCategorizerDL documentCategorizerDL = new DocumentCategorizerDL(model, vocab, getCategories(), inferenceOptions);
    final double[] result = documentCategorizerDL.categorize(new String[] { "I am happy" });
    System.out.println(Arrays.toString(result));
    final double[] expected = new double[] { 0.007819971069693565, 0.006593209225684404, 0.04995147883892059, 0.3003573715686798, 0.6352779865264893 };
    Assert.assertTrue(Arrays.equals(expected, result));
    Assert.assertEquals(5, result.length);
    final String category = documentCategorizerDL.getBestCategory(result);
    Assert.assertEquals("very good", category);
  }

  @Test public void categorizeWithInferenceOptions() throws Exception {
    final File model = new File(getOpennlpDataDir(), "onnx/doccat/lvwerra_distilbert-imdb.onnx");
    final File vocab = new File(getOpennlpDataDir(), "onnx/doccat/lvwerra_distilbert-imdb.vocab");
    final InferenceOptions inferenceOptions = new InferenceOptions();
    inferenceOptions.setIncludeTokenTypeIds(false);
    final Map<Integer, String> categories = new HashMap<>();
    categories.put(0, "negative");
    categories.put(1, "positive");
    final DocumentCategorizerDL documentCategorizerDL = new DocumentCategorizerDL(model, vocab, categories, inferenceOptions);
    final double[] result = documentCategorizerDL.categorize(new String[] { "I am angry" });
    System.out.println(Arrays.toString(result));
    final double[] expected = new double[] { 0.8851314783096313, 0.11486853659152985 };
    Assert.assertTrue(Arrays.equals(expected, result));
    Assert.assertEquals(2, result.length);
    final String category = documentCategorizerDL.getBestCategory(result);
    Assert.assertEquals("negative", category);
  }

  @Test public void scoreMap() throws IOException, Exception, OrtException {
    final File model = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.onnx");
    final File vocab = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.vocab");
    final DocumentCategorizerDL documentCategorizerDL = new DocumentCategorizerDL(model, vocab, getCategories());
    final Map<String, Double> result = documentCategorizerDL.scoreMap(new String[] { "I am happy" });
    Assert.assertEquals(0.6352779865264893, result.get("very good").doubleValue(), 0);
    Assert.assertEquals(0.3003573715686798, result.get("good").doubleValue(), 0);
    Assert.assertEquals(0.04995147883892059, result.get("neutral").doubleValue(), 0);
    Assert.assertEquals(0.006593209225684404, result.get("bad").doubleValue(), 0);
    Assert.assertEquals(0.007819971069693565, result.get("very bad").doubleValue(), 0);
  }

  @Test public void sortedScoreMap() throws IOException, Exception, OrtException {
    final File model = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.onnx");
    final File vocab = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.vocab");
    final DocumentCategorizerDL documentCategorizerDL = new DocumentCategorizerDL(model, vocab, getCategories());
    final Map<Double, Set<String>> result = documentCategorizerDL.sortedScoreMap(new String[] { "I am happy" });
    Assert.assertEquals(result.get(0.6352779865264893).size(), 1);
    Assert.assertEquals(result.get(0.3003573715686798).size(), 1);
    Assert.assertEquals(result.get(0.04995147883892059).size(), 1);
    Assert.assertEquals(result.get(0.006593209225684404).size(), 1);
    Assert.assertEquals(result.get(0.007819971069693565).size(), 1);
  }

  @Test public void doccat() throws IOException, Exception, OrtException {
    final File model = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.onnx");
    final File vocab = new File(getOpennlpDataDir(), "onnx/doccat/nlptown_bert-base-multilingual-uncased-sentiment.vocab");
    final DocumentCategorizerDL documentCategorizerDL = new DocumentCategorizerDL(model, vocab, getCategories());
    final int index = documentCategorizerDL.getIndex("bad");
    Assert.assertEquals(1, index);
    final String category = documentCategorizerDL.getCategory(3);
    Assert.assertEquals("good", category);
    final int number = documentCategorizerDL.getNumberOfCategories();
    Assert.assertEquals(5, number);
  }

  private Map<Integer, String> getCategories() {
    final Map<Integer, String> categories = new HashMap<>();
    categories.put(0, "very bad");
    categories.put(1, "bad");
    categories.put(2, "neutral");
    categories.put(3, "good");
    categories.put(4, "very good");
    return categories;
  }
}