package com.sdl.selenium.web;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.utils.internationalization.InternationalizationUtils;
import com.sdl.selenium.web.utils.internationalization.InternationalizedTextRetriever;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Created by fratiu on 8/3/2016.
 */
public class InternationalizedWebLocatorTest {
  private WebLocator locator;

  @BeforeClass public void setupInternationalizedTextRetriever() {
    Map<String, String> record1Translations = new HashMap<>();
    record1Translations.put("English", "car");
    record1Translations.put("French", "voiture");
    record1Translations.put("Romanian", "ma\u0219in\u0103");
    record1Translations.put("Japanese", "\u8eca");
    record1Translations.put("Arabic", "\u0633\u064a\u0627\u0631\u0629");
    Map<String, String> record2Translations = new HashMap<>();
    record2Translations.put("English", "negative");
    record2Translations.put("French", "n\u00e9gatif");
    record2Translations.put("Romanian", null);
    record2Translations.put("Japanese", "\u8ca0");
    record2Translations.put("Arabic", "\u0627\u0644\u0633\u0644\u0628\u0649");
    Map<String, String> record3Translations = new HashMap<>();
    record3Translations.put("English", "bird");
    record3Translations.put("French", "oiseau");
    record3Translations.put("Romanian", "pas\u0103re");
    record3Translations.put("Japanese", "\u9ce5");
    record3Translations.put("Arabic", "\u0627\u0646\u0641\u0644\u0648\u0646\u0632\u0627");
    Map<String, String> record4Translations = new HashMap<>();
    record4Translations.put("English", "bird with variables {0}, {1}, {2} and html");
    record4Translations.put("French", "oiseau with variables {0}, {1}, {2} and html");
    record4Translations.put("Romanian", "pas\u0103re with variables {0}, {1}, {2} and html");
    record4Translations.put("Japanese", "\u9ce5 with variables {0}, {1}, {2} and html");
    record4Translations.put("Arabic", "with variables {0}, {1}, {2} and html \u0627\u0646\u0641\u0644\u0648\u0646\u0632\u0627");
    Map<String, String> record5Translations = new HashMap<>();
    record5Translations.put("English", "car with variables %s, %s, %s and html");
    record5Translations.put("French", "voiture with variables %s, %s, %s and html");
    record5Translations.put("Romanian", "ma\u0219in\u0103 with variables %s, %s, %s and html");
    record5Translations.put("Japanese", "\u8eca with variables %s, %s, %s and html");
    record5Translations.put("Arabic", "with variables %s, %s, %s and html \u0633\u064a\u0627\u0631\u0629");
    InternationalizationUtils.setInternationalizedTextRetriever(new InternationalizedTextRetriever(true, Arrays.asList(record1Translations, record2Translations, record3Translations, record4Translations, record5Translations), "English"));
  }

  @Test public void setTextForBaseLanguage() {
    InternationalizationUtils.setCurrentLanguage("English");
    locator = new WebLocator().withText("car");
    assertThat(locator.getXPath(), containsString("car"));
  }

  @Test public void setTextForDefinedLanguage() {
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withText("car");
    assertThat(locator.getXPath(), containsString("voiture"));
  }

  @Test public void setTextForDefinedLanguagesWithSpecialCharacters() {
    InternationalizationUtils.setCurrentLanguage("Romanian");
    locator = new WebLocator().withText("car");
    assertThat(locator.getXPath(), containsString("ma\u0219in\u0103"));
    InternationalizationUtils.setCurrentLanguage("Japanese");
    locator = new WebLocator().withText("car");
    assertThat(locator.getXPath(), containsString("\u8eca"));
    InternationalizationUtils.setCurrentLanguage("Arabic");
    locator = new WebLocator().withText("car");
    assertThat(locator.getXPath(), containsString("\u0633\u064a\u0627\u0631\u0629"));
  }

  @Test public void setTextUsingOtherCsvRecordThanTheFirstOne() {
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withText("negative");
    assertThat(locator.getXPath(), containsString("n\u00e9gatif"));
  }

  @Test public void setTextForLanguageWithNullValue() {
    InternationalizationUtils.setCurrentLanguage("Romanian");
    locator = new WebLocator().withText("negative");
    assertThat(locator.getXPath(), containsString("Missing Romanian text for: negative"));
  }

  @Test public void setLabelForDefinedLanguagesWithSpecialCharacters() {
    InternationalizationUtils.setCurrentLanguage("Romanian");
    locator = new WebLocator().withLabel("car");
    assertThat(locator.getXPath(), containsString("ma\u0219in\u0103"));
    InternationalizationUtils.setCurrentLanguage("Japanese");
    locator = new WebLocator().withLabel("car");
    assertThat(locator.getXPath(), containsString("\u8eca"));
    InternationalizationUtils.setCurrentLanguage("Arabic");
    locator = new WebLocator().withLabel("car");
    assertThat(locator.getXPath(), containsString("\u0633\u064a\u0627\u0631\u0629"));
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withLabel("negative");
    assertThat(locator.getXPath(), containsString("n\u00e9gatif"));
  }

  @Test public void setLabelAndTextForDefinedLanguagesWithSpecialCharacters() {
    InternationalizationUtils.setCurrentLanguage("Romanian");
    locator = new WebLocator().withLabel("car").withText("bird");
    assertThat(locator.getXPath(), containsString("ma\u0219in\u0103"));
    assertThat(locator.getXPath(), containsString("pas\u0103re"));
    InternationalizationUtils.setCurrentLanguage("Japanese");
    locator = new WebLocator().withLabel("car").withText("bird");
    assertThat(locator.getXPath(), containsString("\u8eca"));
    assertThat(locator.getXPath(), containsString("\u9ce5"));
    InternationalizationUtils.setCurrentLanguage("Arabic");
    locator = new WebLocator().withLabel("car").withText("bird");
    assertThat(locator.getXPath(), containsString("\u0633\u064a\u0627\u0631\u0629"));
    assertThat(locator.getXPath(), containsString("\u0627\u0646\u0641\u0644\u0648\u0646\u0632\u0627"));
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withLabel("negative").withText("bird");
    assertThat(locator.getXPath(), containsString("n\u00e9gatif"));
    assertThat(locator.getXPath(), containsString("oiseau"));
  }

  @Test public void setLabelAndTextForDefinedLanguagesWithVariables() {
    InternationalizationUtils.setCurrentLanguage("Romanian");
    locator = new WebLocator().withLabel("car with variables 10, 20, StringValue and html");
    assertThat(locator.getXPath(), containsString("ma\u0219in\u0103 with variables 10, 20, StringValue and html"));
    InternationalizationUtils.setCurrentLanguage("Japanese");
    locator = new WebLocator().withLabel("bird with variables 10, 20, StringValue and html");
    assertThat(locator.getXPath(), containsString("\u9ce5 with variables 10, 20, StringValue and html"));
    InternationalizationUtils.setCurrentLanguage("Arabic");
    locator = new WebLocator().withLabel("car with variables 10, 20, StringValue and html");
    assertThat(locator.getXPath(), containsString("with variables 10, 20, StringValue and html \u0633\u064a\u0627\u0631\u0629"));
  }

  @Test public void overrideTextInternationalizationFlagAtWebLocatorLevel() {
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withText("car");
    assertThat(locator.getXPath(), containsString("voiture"));
    locator = new WebLocator().withText("car", SearchType.NOT_INTERNATIONALIZED);
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
    locator = new WebLocator().withText("car").setSearchTextType(SearchType.NOT_INTERNATIONALIZED);
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
    locator = new WebLocator().setSearchTextType(SearchType.NOT_INTERNATIONALIZED).withText("car");
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
    locator = new InputButton(new WebLocator(), "car").setSearchTextType(SearchType.NOT_INTERNATIONALIZED);
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
  }

  @Test public void overrideTitleInternationalizationFlagAtWebLocatorLevel() {
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withTitle("car");
    assertThat(locator.getXPath(), containsString("voiture"));
    locator = new WebLocator().withTitle("car", SearchType.NOT_INTERNATIONALIZED);
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
    locator = new WebLocator().withTitle("car").setSearchTitleType(SearchType.NOT_INTERNATIONALIZED);
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
    locator = new WebLocator().setSearchTitleType(SearchType.NOT_INTERNATIONALIZED).withTitle("car");
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
  }

  @Test public void overrideLabelInternationalizationFlagAtWebLocatorLevel() {
    InternationalizationUtils.setCurrentLanguage("French");
    locator = new WebLocator().withLabel("car");
    assertThat(locator.getXPath(), containsString("voiture"));
    locator = new WebLocator().withLabel("car", SearchType.NOT_INTERNATIONALIZED);
    assertThat(locator.getXPath(), containsString("car"));
    assertThat(locator.getXPath(), not(containsString("voiture")));
  }
}