package opennlp.tools.namefind;
import java.io.IOException;
import opennlp.tools.ml.model.Event;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ObjectStreamUtils;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is the test class for {@link NameFinderEventStream}.
 */
public class NameFinderEventStreamTest {
  private static final String[] SENTENCE = { "Elise", "Wendel", "appreciated", "the", "hint", "and", "enjoyed", "a", "delicious", "traditional", "meal", "." };

  private static final NameContextGenerator CG = new DefaultNameContextGenerator((AdaptiveFeatureGenerator[]) null);

  /**
   * Tests the correctly generated outcomes for a test sentence.
   */
  @Test public void testOutcomesForSingleTypeSentence() throws IOException {
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2, "person") }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample));
    Assert.assertEquals("person-" + NameFinderME.START, eventStream.read().getOutcome());
    Assert.assertEquals("person-" + NameFinderME.CONTINUE, eventStream.read().getOutcome());
    for (int i = 0; i < 10; i++) {
      Assert.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }
    Assert.assertNull(eventStream.read());
    eventStream.close();
  }

  /**
   * Tests the correctly generated outcomes for a test sentence. If the Span
   * declares its type, passing the type to event stream has no effect
   */
  @Test public void testOutcomesTypeCantOverride() throws IOException {
    String type = "XYZ";
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2, "person") }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), type, CG, null);
    String prefix = "person-";

<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
=======
    assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java


<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
=======
    assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java

    for (int i = 0; i < 10; i++) {
      Assert.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }

<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertNull(eventStream.read());
=======
    assertNull(eventStream.read());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java

    eventStream.close();
  }

  /**
   * Tests the correctly generated outcomes for a test sentence. If the Span
   * does not declare its type and the user passed a type, use the type from
   * user
   */
  @Test public void testOutcomesWithType() throws IOException {
    String type = "XYZ";
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2) }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), type, CG, null);
    String prefix = type + "-";

<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
=======
    assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java


<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
=======
    assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java

    for (int i = 0; i < 10; i++) {
      Assert.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }

<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertNull(eventStream.read());
=======
    assertNull(eventStream.read());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java

    eventStream.close();
  }

  /**
   * Tests the correctly generated outcomes for a test sentence. If the Span
   * does not declare its type and the user did not set a type, it will use
   * "default".
   */
  @Test public void testOutcomesTypeEmpty() throws IOException {
    NameSample nameSample = new NameSample(SENTENCE, new Span[] { new Span(0, 2) }, false);
    ObjectStream<Event> eventStream = new NameFinderEventStream(ObjectStreamUtils.createObjectStream(nameSample), null, CG, null);
    String prefix = "default-";

<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
=======
    assertEquals(prefix + NameFinderME.START, eventStream.read().getOutcome());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java


<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
=======
    assertEquals(prefix + NameFinderME.CONTINUE, eventStream.read().getOutcome());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java

    for (int i = 0; i < 10; i++) {
      Assert.assertEquals(NameFinderME.OTHER, eventStream.read().getOutcome());
    }

<<<<<<< /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/left.java
    Assert.assertNull(eventStream.read());
=======
    assertNull(eventStream.read());
>>>>>>> /usr/src/app/output/apache/opennlp/d7c92974378dbc53ab64d1be823986bb2c371d71/opennlp-tools/src/test/java/opennlp/tools/namefind/NameFinderEventStreamTest.java/right.java

    eventStream.close();
  }
}