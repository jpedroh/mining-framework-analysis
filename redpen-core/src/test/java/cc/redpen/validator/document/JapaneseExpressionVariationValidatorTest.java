package cc.redpen.validator.document;
import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class JapaneseExpressionVariationValidatorTest extends BaseValidatorTest {
  JapaneseExpressionVariationValidatorTest() {
    super("JapaneseExpressionVariation");
  }

  @Test void detectSameReadingsInJapaneseCharacters() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("\u4e4b\u306f\u5c71\u3067\u3059\u3002\u3053\u308c\u306f\u5ddd\u3067\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectSameReadingsInJapaneseCharactersInDefaultDictionary() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("node\u306f\u82f1\u8a9e\u3067\u3059\u3002\u30ce\u30fc\u30c9\u306f\u30ab\u30bf\u30ab\u30ca\u3067\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectSameReadingsInJapaneseCharactersInDefaultDictionaryWithUpperCase() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("Node\u306f\u82f1\u8a9e\u3067\u3059\u3002\u30ce\u30fc\u30c9\u306f\u30ab\u30bf\u30ab\u30ca\u3067\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectSameAlphabecicalReadings() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("\u3053\u306eExcel\u306f\u3042\u306e\u30a8\u30af\u30bb\u30eb\u3068\u306f\u9055\u3044\u307e\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectSameAlphabecicalReadingsInUserDictionary() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName).addProperty("map", "{svm,\u30b5\u30dd\u30fc\u30c8\u30d9\u30af\u30bf\u30de\u30b7\u30f3}")).build();
    Document document = prepareSimpleDocument("\u3053\u306eSVM\u306f\u3042\u306e\u30b5\u30dd\u30fc\u30c8\u30d9\u30af\u30bf\u30de\u30b7\u30f3\u3068\u306f\u9055\u3044\u307e\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectNormalizedReadings() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("\u3053\u306e\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u306f\u3042\u306e\u30a4\u30f3\u30c7\u30af\u30b9\u3068\u306f\u9055\u3044\u307e\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectNormalizedReadings2() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("\u3053\u306e\u30f4\u30a7\u30c8\u30ca\u30e0\u306f\u3042\u306e\u30d9\u30c8\u30ca\u30e0\u3068\u306f\u9055\u3044\u307e\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectSameReadingsInConcatinatedJapaneseWord() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("\u8eab\u5206\u8a3c\u660e\u66f8\u306f\u7d19\u3067\u3059\u3002\u8eab\u5206\u8a3c\u660e\u6240\u306f\u9593\u9055\u3044\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }

  @Test void detectMultipleSameReadings() throws RedPenException {
    config = Configuration.builder("ja").addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    Document document = prepareSimpleDocument("\u3053\u306eExcel\u306f\u3042\u306e\u30a8\u30af\u30bb\u30eb\u3068\u3082\u3053\u306e\u30a8\u30af\u30bb\u30eb\u3068\u3082\u9055\u3044\u307e\u3059\u3002");
    RedPen redPen = new RedPen(config);
    Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
    assertEquals(1, errors.get(document).size());
  }
}