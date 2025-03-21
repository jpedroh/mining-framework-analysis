package opennlp.tools.namefind;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import opennlp.tools.ml.model.Event;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ObjectStreamUtils;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

/**
 * This is the test class for {@link NameFinderEventStream}.
 */
public class NameFinderEventStreamTest {
  private static final String[] SENTENCE = { "Elise", "Wendel", "appreciated", "the", "hint", "and", "enjoyed", "a", "delicious", "traditional", "meal", "." };

  private static final NameContextGenerator CG = new DefaultNameContextGenerator((AdaptiveFeatureGenerator[]) null);

  /**
   * Tests the correctly generated outcomes for a test sentence.
   */
  @Test void testOutcomesForSingleTypeSentence() throws IOException {
    NameContextGenerator CG = new DefaultNameContextGenerator((AdaptiveFeatureGenerator[]) null);
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2, "person") }, false);
    try (ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), "person", CG, null)) {
      Assertions.assertEquals("person-" + NameFinderME.START, eventStream.read().getOutcome());
      Assertions.assertEquals("person-" + NameFinderME.CONTINUE, eventStream.read().getOutcome());
      for (int i = 0; i < 10; i++) {
        Assertions.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
      }
      Assertions.assertNull(eventStream.read());
    }
  }

  /**
   * Tests the correctly generated outcomes for a test sentence. If the Span
   * declares its type, passing the type to event stream has no effect
   */
  @Test void testOutcomesTypeCantOverride() throws IOException {
    String type = "XYZ";
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2, "person") }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), type, CG, null);
    String prefix = type + "-";
    Assertions.assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
    Assertions.assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
    for (int i = 0; i < 10; i++) {
      Assertions.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }
    Assertions.assertNull(eventStream.read());
    eventStream.close();
  }

  /**
   * Tests the correctly generated outcomes for a test sentence. If the Span
   * does not declare its type and the user passed a type, use the type from
   * user
   */
  @Test void testOutcomesWithType() throws IOException {
    String type = "XYZ";
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2) }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), type, CG, null);
    String prefix = type + "-";
    Assertions.assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
    Assertions.assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
    for (int i = 0; i < 10; i++) {
      Assertions.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }
    Assertions.assertNull(eventStream.read());
    eventStream.close();
  }

  /**
   * Tests the correctly generated outcomes for a test sentence. If the Span
   * does not declare its type and the user did not set a type, it will use
   * "default".
   */
  @Test void testOutcomesTypeEmpty() throws IOException {
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2) }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), null, CG, null);
    String prefix = "default-";
    Assertions.assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
    Assertions.assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
    for (int i = 0; i < 10; i++) {
      Assertions.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }
    Assertions.assertNull(eventStream.read());
    eventStream.close();
  }
}