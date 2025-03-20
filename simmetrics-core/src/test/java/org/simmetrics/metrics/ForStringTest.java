package org.simmetrics.metrics;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.simmetrics.Metric;
import org.simmetrics.StringMetricTest;
import org.simmetrics.metrics.Identity;
import org.simmetrics.metrics.StringMetrics.ForString;

@SuppressWarnings(value = { "javadoc" }) public class ForStringTest extends StringMetricTest {
  private final Metric<String> metric = new Identity<>();

  @Override protected ForString getMetric() {
    return new ForString(metric);
  }

  @Override protected T[] getTests() {
    return new T[] { new T(0.0f, "To repeat repeat is to repeat", ""), new T(1.0f, "To repeat repeat is to repeat", "To repeat repeat is to repeat") };
  }

  @Override protected boolean toStringIncludesSimpleClassName() {
    return false;
  }

  @Test public void shouldReturnMetric() {
    assertSame(metric, getMetric().getMetric());
  }
}