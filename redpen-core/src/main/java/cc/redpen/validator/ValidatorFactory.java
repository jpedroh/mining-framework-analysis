package cc.redpen.validator;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.validator.section.*;
import cc.redpen.validator.sentence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Factory class of validators.
 */
public class ValidatorFactory {
  private static final String validatorPackage = Validator.class.getPackage().getName();

  private static final List<String> VALIDATOR_PACKAGES = asList(validatorPackage, validatorPackage + ".sentence", validatorPackage + ".section");

  static final Map<String, Validator> validators = new LinkedHashMap<>();

  public static void registerValidator(Class<? extends Validator> clazz) {
    validators.put(clazz.getSimpleName().replace("Validator", ""), createValidator(clazz));
  }

  static {
    registerValidator(DuplicatedSectionValidator.class);
    registerValidator(FrequentSentenceStartValidator.class);
    registerValidator(ParagraphNumberValidator.class);
    registerValidator(ParagraphStartWithValidator.class);
    registerValidator(SectionLengthValidator.class);
    registerValidator(UnexpandedAcronymValidator.class);
    registerValidator(WordFrequencyValidator.class);
    registerValidator(SectionLevelValidator.class);
    registerValidator(CommaNumberValidator.class);
    registerValidator(ContractionValidator.class);
    registerValidator(DoubledJoshiValidator.class);
    registerValidator(DoubledWordValidator.class);
    registerValidator(DoubleNegativeValidator.class);
    registerValidator(EndOfSentenceValidator.class);
    registerValidator(HankakuKanaValidator.class);
    registerValidator(HyphenationValidator.class);
    registerValidator(InvalidExpressionValidator.class);
    registerValidator(InvalidSymbolValidator.class);
    registerValidator(InvalidWordValidator.class);
    registerValidator(JapaneseStyleValidator.class);
    registerValidator(KatakanaEndHyphenValidator.class);
    registerValidator(KatakanaSpellCheckValidator.class);
    registerValidator(NumberFormatValidator.class);
    registerValidator(OkuriganaValidator.class);
    registerValidator(ParenthesizedSentenceValidator.class);
    registerValidator(QuotationValidator.class);
    registerValidator(SentenceLengthValidator.class);
    registerValidator(SpaceBeginningOfSentenceValidator.class);
    registerValidator(SpaceBetweenAlphabeticalWordValidator.class);
    registerValidator(SpellingValidator.class);
    registerValidator(StartWithCapitalLetterValidator.class);
    registerValidator(SuccessiveWordValidator.class);
    registerValidator(SuggestExpressionValidator.class);
    registerValidator(SymbolWithSpaceValidator.class);
    registerValidator(WeakExpressionValidator.class);
    registerValidator(WordNumberValidator.class);
    registerValidator(
<<<<<<< /usr/src/app/output/recruit-tech/redpen/6070b11414a006e8f3c05e457543200a9f558809/redpen-core/src/main/java/cc/redpen/validator/ValidatorFactory.java/left.java
    LongKanjiChainValidator
=======
    JapaneseAmbiguousNounConjunctionValidator
>>>>>>> /usr/src/app/output/recruit-tech/redpen/6070b11414a006e8f3c05e457543200a9f558809/redpen-core/src/main/java/cc/redpen/validator/ValidatorFactory.java/right.java
    .class);
    registerValidator(JavaScriptValidator.class);
  }

  public static List<ValidatorConfiguration> getConfigurations(String lang) {
    return validators.entrySet().stream().filter((e) -> {
      List<String> supportedLanguages = e.getValue().getSupportedLanguages();
      return supportedLanguages.isEmpty() || supportedLanguages.contains(lang);
    }).map((e) -> new ValidatorConfiguration(e.getKey(), toStrings(e.getValue().getProperties()))).collect(toList());
  }

  @SuppressWarnings(value = { "unchecked" }) static Map<String, String> toStrings(Map<String, Object> properties) {
    Map<String, String> result = new LinkedHashMap<>();
    for (Map.Entry<String, Object> e : properties.entrySet()) {
      if (e.getValue() instanceof Iterable) {
        result.put(e.getKey(), join((Iterable) e.getValue(), ','));
      } else {
        result.put(e.getKey(), e.getValue().toString());
      }
    }
    return result;
  }

  public static Validator getInstance(String validatorName) throws RedPenException {
    Configuration conf = Configuration.builder().addValidatorConfig(new ValidatorConfiguration(validatorName)).build();
    return getInstance(conf.getValidatorConfigs().get(0), conf);
  }

  public static Validator getInstance(ValidatorConfiguration config, Configuration globalConfig) throws RedPenException {
    Validator prototype = validators.get(config.getConfigurationName());
    Class<? extends Validator> validatorClass = prototype != null ? prototype.getClass() : loadPlugin(config.getConfigurationName());
    Validator validator = createValidator(validatorClass);
    validator.preInit(config, globalConfig);
    return validator;
  }

  @SuppressWarnings(value = { "unchecked" }) private static Class<? extends Validator> loadPlugin(String name) throws RedPenException {
    for (String p : VALIDATOR_PACKAGES) {
      try {
        Class<? extends Validator> validatorClass = (Class) Class.forName(p + "." + name + "Validator");
        registerValidator(validatorClass);
        return validatorClass;
      } catch (ClassNotFoundException ignore) {
      }
    }
    throw new RedPenException("There is no such validator: " + name);
  }

  private static Validator createValidator(Class<? extends Validator> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Cannot create instance of " + clazz + " using default constructor");
    }
  }
}