package org.simmetrics.example;
import static org.simmetrics.builders.StringMetricBuilder.with;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.StringMetrics;
import org.simmetrics.tokenizers.Tokenizers;

/**
 * The {@link StringMetrics} utility class contains a predefined list of well
 * known metrics.
 */
@SuppressWarnings(value = { "javadoc" }) public final class StringMetricsExample {
  /**
	 * Two strings can be compared using a predefined string metric.
	 */
  public static float example01() {
    String str1 = "This is a sentence. It is made of words";
    String str2 = "This sentence is similar. It has almost the same words";
    StringMetric metric = StringMetrics.jaro();
    return metric.compare(str1, str2);
  }

  /**
	 * A tokenizer is included when the metric is a string or list metric. In
	 * the case of cosine similarity, it is a whitespace tokenizer.
	 */
  public static float example02() {
    String str1 = "A quirky thing it is. This is a sentence.";
    String str2 = "This sentence is similar. A quirky thing it is.";
    StringMetric metric = StringMetrics.cosineSimilarity();
    return metric.compare(str1, str2);
  }

  /**
	 * Using the string {@link StringMetricBuilder} metrics can be
	 * customized. Instead of a whitespace tokenizer a qgram tokenizer is used.
	 *
	 * For more examples see {@link StringMetricBuilderExample}.
	 */
  public static float example03() {
    String str1 = "A quirky thing it is. This is a sentence.";
    String str2 = "This sentence is similar. A quirky thing it is.";
    StringMetric metric = with(new CosineSimilarity<String>()).tokenize(Tokenizers.qGram(3)).build();
    return metric.compare(str1, str2);
  }
}