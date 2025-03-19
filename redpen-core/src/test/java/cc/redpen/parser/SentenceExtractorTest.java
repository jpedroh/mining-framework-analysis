package cc.redpen.parser;
import cc.redpen.model.Sentence;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class SentenceExtractorTest {
  @Test public void testSimple() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen.", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testMultipleSentences() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. that is a paper.", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" that is a paper.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testTwoSentencesWithDifferentStopCharacters() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("is this a pen? that is a paper.", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("is this a pen?", outputSentences.get(0).content);
    assertEquals(" that is a paper.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testMultipleSentencesWithoutPeriodInTheEnd() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. that is a paper", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" that is a paper", remain);
  }

  @Test public void testEndWithDoubleQuotation() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \"pen.\"", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \"pen.\"", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testEndWithSingleQuotation() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \'pen.\'", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \'pen.\'", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testEndWithDoubleQuotationEnglishVersion() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \"pen\".", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \"pen\".", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testEndWithSingleQuotationEnglishVersion() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \'pen\'.", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a \'pen\'.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testMultipleSentencesOneOfThemIsEndWithDoubleQuotation() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a \"pen.\" Another one is not a pen.", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("this is a \"pen.\"", outputSentences.get(0).content);
    assertEquals(" Another one is not a pen.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testMultipleSentencesWithPartialSplit() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. Another\n" + "one is not a pen.", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" Another\none is not a pen.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testMultipleSentencesWithPartialSentence() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("this is a pen. Another\n", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("this is a pen.", outputSentences.get(0).content);
    assertEquals(" Another\n", remain);
  }

  @Test public void testJapaneseSimple() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("\u3002");
    stopChars.add("\uff1f");
    SentenceExtractor extractor = new SentenceExtractor(stopChars);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("\u3053\u308c\u306f\u57fc\u7389\u3067\u3059\u304b\uff1f\u3044\u3044\u3048\u7fa4\u99ac\u3067\u3059\u3002", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("\u3053\u308c\u306f\u57fc\u7389\u3067\u3059\u304b\uff1f", outputSentences.get(0).content);
    assertEquals("\u3044\u3044\u3048\u7fa4\u99ac\u3067\u3059\u3002", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testJapaneseSimpleWithSpace() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("\u3002");
    stopChars.add("\uff1f");
    SentenceExtractor extractor = new SentenceExtractor(stopChars);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("\u3053\u308c\u306f\u57fc\u7389\u3067\u3059\u304b\uff1f \u3044\u3044\u3048\u7fa4\u99ac\u3067\u3059\u3002", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("\u3053\u308c\u306f\u57fc\u7389\u3067\u3059\u304b\uff1f", outputSentences.get(0).content);
    assertEquals(" \u3044\u3044\u3048\u7fa4\u99ac\u3067\u3059\u3002", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testJapaneseSimpleWithEndQuotations() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("\u3002");
    stopChars.add("\uff1f");
    List<String> rightQuotations = new ArrayList<>();
    stopChars.add("\u2019");
    stopChars.add("\u201d");
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("\u3053\u308c\u306f\u201c\u7fa4\u99ac\u3002\u201d", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("\u3053\u308c\u306f\u201c\u7fa4\u99ac\u3002\u201d", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testJapaneseMultipleSentencesWithEndQuotations() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("\u3002");
    stopChars.add("\uff1f");
    List<String> rightQuotations = new ArrayList<>();
    stopChars.add("\u2019");
    stopChars.add("\u201d");
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("\u3053\u308c\u306f\u201c\u7fa4\u99ac\u3002\u201d\u3042\u308c\u306f\u7fa4\u99ac\u3067\u306f\u306a\u3044\u3002", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("\u3053\u308c\u306f\u201c\u7fa4\u99ac\u3002\u201d", outputSentences.get(0).content);
    assertEquals("\u3042\u308c\u306f\u7fa4\u99ac\u3067\u306f\u306a\u3044\u3002", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testJapaneseMultipleSentencesWithPartialSplit() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("\uff0e");
    stopChars.add("\uff1f");
    List<String> rightQuotations = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("\u305d\u308c\u306f\u7570\u306a\u308b\uff0e\u305f\u3068\u3048\u3070\uff0c\n" + "\u4ee5\u4e0b\u306e\u3068\u304a\u308a\u3067\u3042\u308b\uff0e", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("\u305d\u308c\u306f\u7570\u306a\u308b\uff0e", outputSentences.get(0).content);
    assertEquals("\u305f\u3068\u3048\u3070\uff0c\n\u4ee5\u4e0b\u306e\u3068\u304a\u308a\u3067\u3042\u308b\uff0e", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testJapanesSentenceWithEndWithNonFullStop() {
    List<String> stopChars = new ArrayList<>();
    stopChars.add("\uff0e");
    List<String> rightQuotations = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(stopChars, rightQuotations);
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("\u305d\u308c\u306f\u7570\u306a\u308b\uff0e\u305f\u3068\u3048\u3070\uff0c", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("\u305d\u308c\u306f\u7570\u306a\u308b\uff0e", outputSentences.get(0).content);
    assertEquals("\u305f\u3068\u3048\u3070\uff0c", remain);
  }

  @Test public void testSentenceWithWhiteWord() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("He is a Dr. candidate.", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("He is a Dr. candidate.", outputSentences.get(0).content);
    assertEquals("", remain);
  }

  @Test public void testMultipleSentencesWithWhiteWord() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("Is he a Dr. candidate? Yes, he is.", outputSentences, 0);
    assertEquals(2, outputSentences.size());
    assertEquals("Is he a Dr. candidate?", outputSentences.get(0).content);
    assertEquals(" Yes, he is.", outputSentences.get(1).content);
    assertEquals("", remain);
  }

  @Test public void testVoidLine() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract("", outputSentences, 0);
    assertEquals(0, outputSentences.size());
    assertEquals(remain, "");
  }

  @Test public void testJustPeriodLine() {
    SentenceExtractor extractor = new SentenceExtractor();
    List<Sentence> outputSentences = new ArrayList<>();
    String remain = extractor.extract(".", outputSentences, 0);
    assertEquals(1, outputSentences.size());
    assertEquals("", remain);
  }

  @Test public void testConstructPatternString() {
    List<String> endCharacters = new ArrayList<>();
    endCharacters.add("\\.");
    endCharacters.add("?");
    endCharacters.add("!");
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    assertEquals("\\.\'|\\?\'|\\!\'|\\.\"|\\?\"|\\!\"|\\.|\\?|\\!", extractor.constructEndSentencePattern());
  }

  @Test public void testConstructPatternStringWithoutEscape() {
    List<String> endCharacters = new ArrayList<>();
    endCharacters.add(".");
    endCharacters.add("?");
    endCharacters.add("!");
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    assertEquals("\\.\'|\\?\'|\\!\'|\\.\"|\\?\"|\\!\"|\\.|\\?|\\!", extractor.constructEndSentencePattern());
  }

  @Test public void testConstructPatternStringForSingleCharacter() {
    List<String> endCharacters = new ArrayList<>();
    endCharacters.add("\\.");
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    assertEquals("\\.\'|\\.\"|\\.", extractor.constructEndSentencePattern());
  }

  @Test(expected = IllegalArgumentException.class) public void testThrowExceptionGivenVoidList() {
    List<String> endCharacters = new ArrayList<>();
    SentenceExtractor extractor = new SentenceExtractor(endCharacters);
    extractor.constructEndSentencePattern();
  }

  @Test public void testThrowExceptionGivenNull() {
    SentenceExtractor extractor = new SentenceExtractor();
    extractor.constructEndSentencePattern();
  }
}