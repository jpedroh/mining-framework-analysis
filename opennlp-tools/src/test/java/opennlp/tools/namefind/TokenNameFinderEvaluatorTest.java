package opennlp.tools.namefind;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import opennlp.common.namefind.TokenNameFinder;
import opennlp.common.util.Span;
import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is the test class for {@link TokenNameFinderEvaluator}..
 */
public class TokenNameFinderEvaluatorTest {
  /** Return a dummy name finder that always return something expected */
  public TokenNameFinder mockTokenNameFinder(Span[] ret) {
    TokenNameFinder mockInstance = mock(TokenNameFinder.class);
    when(mockInstance.find(any(String[].class))).thenReturn(ret);
    return mockInstance;
  }

  private static String[] sentence = { "U", ".", "S", ".", "President", "Barack", "Obama", "is", "considering", "sending", "additional", "American", "forces", "to", "Afghanistan", "." };

  @Test public void testPositive() {
    OutputStream stream = new ByteArrayOutputStream();
    TokenNameFinderEvaluationMonitor listener = new NameEvaluationErrorListener(stream);
    Span[] pred = createSimpleNameSampleA().getNames();
    TokenNameFinderEvaluator eval = new TokenNameFinderEvaluator(mockTokenNameFinder(pred), listener);
    eval.evaluateSample(createSimpleNameSampleA());
    Assert.assertEquals(1.0, eval.getFMeasure().getFMeasure(), 0.0);
    Assert.assertEquals(0, stream.toString().length());
  }

  private static NameSample createSimpleNameSampleA() {
    Span[] names = { new Span(0, 4, "Location"), new Span(5, 7, "Person"), new Span(14, 15, "Location") };
    NameSample nameSample;
    nameSample = new NameSample(sentence, names, false);
    return nameSample;
  }

  @Test public void testNegative() {
    OutputStream stream = new ByteArrayOutputStream();
    TokenNameFinderEvaluationMonitor listener = new NameEvaluationErrorListener(stream);
    Span[] pred = createSimpleNameSampleB().getNames();
    TokenNameFinderEvaluator eval = new TokenNameFinderEvaluator(mockTokenNameFinder(pred), listener);
    eval.evaluateSample(createSimpleNameSampleA());
    Assert.assertEquals(0.8, eval.getFMeasure().getFMeasure(), 0.0);
    Assert.assertNotSame(0, stream.toString().length());
  }

  private static NameSample createSimpleNameSampleB() {
    Span[] names = { new Span(0, 4, "Location"), new Span(14, 15, "Location") };
    NameSample nameSample;
    nameSample = new NameSample(sentence, names, false);
    return nameSample;
  }
}