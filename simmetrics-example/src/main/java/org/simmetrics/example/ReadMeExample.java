package org.simmetrics.example;
import java.util.Locale;
import org.simmetrics.StringDistance;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringDistanceBuilder;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.EuclideanDistance;
import org.simmetrics.metrics.StringMetrics;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

/**
 * Examples from README.md
 */
@SuppressWarnings(value = { "javadoc" }) public final class ReadMeExample {
  public static float example01() {
    String str1 = "This is a sentence. It is made of words";
    String str2 = "This sentence is similar. It has almost the same words";
    StringMetric metric = StringMetrics.cosineSimilarity();
    float result = metric.compare(str1, str2);
    return result;
  }

  public static float example02() {
    String str1 = "This is a sentence. It is made of words";
    String str2 = "This sentence is similar. It has almost the same words";
    StringMetric metric = StringMetricBuilder.with(new CosineSimilarity<String>()).simplify(Simplifiers.toLowerCase(Locale.ENGLISH)).simplify(Simplifiers.replaceNonWord()).tokenize(Tokenizers.whitespace()).build();
    float result = metric.compare(str1, str2);
    return result;
  }

  public static float example03() {
    String str1 = "This is a sentence. It is made of words";
    String str2 = "This sentence is similar. It has almost the same words";
    StringDistance metric = StringDistanceBuilder.with(new EuclideanDistance<String>()).simplify(Simplifiers.toLowerCase(Locale.ENGLISH)).simplify(Simplifiers.replaceNonWord()).tokenize(Tokenizers.whitespace()).build();
    float result = metric.distance(str1, str2);
    return result;
  }
}