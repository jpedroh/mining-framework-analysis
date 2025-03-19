package org.unigram.docvalidator.validator.sentence;
import static org.junit.Assert.*;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.CharacterTableLoader;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.validator.sentence.InvalidCharacterValidator;

class InvalidCharacterValidatorForTest extends InvalidCharacterValidator {
  void loadCharacterTable(CharacterTable characterTable) {
    this.setCharacterTable(characterTable);
  }
}

public class InvalidCharacterValidatorTest {
  @Test public void testWithInvalidCharacter() {
    InvalidCharacterValidatorForTest validator = new InvalidCharacterValidatorForTest();
    String sampleCharTable = new String("<?xml version=\"1.0\"?>" + "<character-table>" + "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"\uff01\"/>" + "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("\u308f\u305f\u3057\u306f\u30ab\u30e9\u30aa\u30b1\u304c\u5927\u597d\u304d\uff01", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(1, errors.size());
  }

  @Test public void testWithoutInvalidCharacter() {
    InvalidCharacterValidatorForTest validator = new InvalidCharacterValidatorForTest();
    String sampleCharTable = new String("<?xml version=\"1.0\"?>" + "<character-table>" + "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"\uff01\"/>" + "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("I like karaoke!", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(0, errors.size());
  }

  @Test public void testWithoutMultipleInvalidCharacter() {
    InvalidCharacterValidatorForTest validator = new InvalidCharacterValidatorForTest();
    String sampleCharTable = new String("<?xml version=\"1.0\"?>" + "<character-table>" + "<character name=\"EXCLAMATION_MARK\" value=\"!\" invalid-chars=\"\uff01\"/>" + "<character name=\"COMMA\" value=\",\" invalid-chars=\"\u3001\"/>" + "</character-table>");
    InputStream stream = IOUtils.toInputStream(sampleCharTable);
    CharacterTable characterTable = CharacterTableLoader.load(stream);
    validator.loadCharacterTable(characterTable);
    Sentence str = new Sentence("\u308f\u305f\u3057\u306f\u3001\u30ab\u30e9\u30aa\u30b1\u304c\u597d\u304d\uff01", 0);
    List<ValidationError> errors = validator.check(str);
    assertEquals(2, errors.size());
  }
}