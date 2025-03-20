package org.simmetrics.metrics;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.simmetrics.Metric;
import org.simmetrics.StringMetricTest;
import org.simmetrics.metrics.Identity;
import org.simmetrics.metrics.StringMetrics.ForStringWithSimplifier;
import org.simmetrics.simplifiers.Simplifier;
import org.simmetrics.simplifiers.Simplifiers;

@SuppressWarnings(value = { "javadoc" }) public class ForStringWithSimplifierTest extends StringMetricTest {
  private final Metric<String> metric = new Identity<>();

  private final Simplifier simplifier = Simplifiers.toLowerCase();

  @Override protected ForStringWithSimplifier getMetric() {
    return new ForStringWithSimplifier(metric, simplifier);
  }

  @Override protected T[] getTests() {
    return new T[] { new T(0.0f, "To repeat repeat is to repeat", ""), new T(1.0f, "To repeat repeat is to repeat", "to repeat repeat is to repeat") };
  }

  @Override protected boolean satisfiesCoincidence() {
    return false;
  }

  @Override protected boolean toStringIncludesSimpleClassName() {
    return false;
  }

  @Test public void shouldReturnMetric() {
    assertSame(metric, getMetric().getMetric());
  }

  @Test public void shouldReturnSimplifier() {
    assertSame(simplifier, getMetric().getSimplifier());
  }
}