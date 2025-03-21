package org.assertj.core.error;
import static java.lang.String.format;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.error.ShouldSatisfy.shouldSatisfy;
import static org.assertj.core.presentation.StandardRepresentation.STANDARD_REPRESENTATION;
import static org.assertj.core.util.Lists.newArrayList;
import org.assertj.core.api.TestCondition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


<<<<<<< Unknown file: This is a bug in JDime.
=======
@DisplayName(value = "ShouldSatisfy create")
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/0a68d1e394ac0936322110de62f8739e4694c3fe/src/test/java/org/assertj/core/error/ShouldSatisfy_create_Test.java/right.java
 
<<<<<<< Unknown file: This is a bug in JDime.
=======
public
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/0a68d1e394ac0936322110de62f8739e4694c3fe/src/test/java/org/assertj/core/error/ShouldSatisfy_create_Test.java/right.java
 class ShouldSatisfy_create_Test {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/0a68d1e394ac0936322110de62f8739e4694c3fe/src/test/java/org/assertj/core/error/ShouldSatisfy_create_Test.java/left.java
  @Test void should_create_error_message() {
    ErrorMessageFactory factory = shouldSatisfy("Yoda", new TestCondition<>("green lightsaber bearer"));
    String message = factory.create(new TextDescription("Test"), STANDARD_REPRESENTATION);
    then(message).isEqualTo(format("[Test] %n" + "Expecting:%n" + "  <\"Yoda\">%n" + "to satisfy:%n" + "  <green lightsaber bearer>"));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Test public void should_create_error_message_if_condition_is_not_satisfied() {
    ErrorMessageFactory factory = shouldSatisfy("Yoda", new TestCondition<>("green lightsaber bearer"));
    String message = factory.create(new TextDescription("Test"), STANDARD_REPRESENTATION);
    then(message).isEqualTo(format("[Test] %n" + "Expecting:%n" + "  <\"Yoda\">%n" + "to satisfy:%n" + "  <green lightsaber bearer>"));
  }

  @Test public void should_create_error_message_if_consumers_are_not_all_satisfied() {
    ErrorMessageFactory factory = shouldSatisfy(newArrayList("Luke", "Leia", "Yoda"));
    String message = factory.create(new TextDescription("Test"), STANDARD_REPRESENTATION);
    then(message).isEqualTo(format("[Test] %n" + "Expecting:%n" + "  <[\"Luke\", \"Leia\", \"Yoda\"]>%n" + "to satisfy all the consumers."));
  }
}