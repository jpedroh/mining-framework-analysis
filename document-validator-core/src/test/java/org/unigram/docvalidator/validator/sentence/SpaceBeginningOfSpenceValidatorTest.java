package org.unigram.docvalidator.validator.sentence;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.ValidationError;

public class SpaceBeginningOfSpenceValidatorTest {
  @Test public void testProcessSetenceWithoutEndSpace() {
    SpaceBeginningOfSentenceValidator spaceValidator = new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("That is true.", 0);
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(1, errors.size());
  }

  @Test public void testProcessEndSpace() {
    SpaceBeginningOfSentenceValidator spaceValidator = new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence(" That is true.", 0);
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test public void testProcessHeadSentenceInAParagraph() {
    SpaceBeginningOfSentenceValidator spaceValidator = new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("That is true.", 0);
    str.isFirstSentence = true;
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }

  @Test public void testProcessZerorLengthSentence() {
    SpaceBeginningOfSentenceValidator spaceValidator = new SpaceBeginningOfSentenceValidator();
    Sentence str = new Sentence("", 0);
    str.isFirstSentence = true;
    List<ValidationError> errors = spaceValidator.check(str);
    assertNotNull(errors);
    assertEquals(0, errors.size());
  }
}