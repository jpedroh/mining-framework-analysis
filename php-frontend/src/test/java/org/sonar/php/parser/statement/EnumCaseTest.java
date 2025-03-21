package org.sonar.php.parser.statement;
import org.junit.Test;
import org.sonar.php.parser.PHPLexicalGrammar;
import static org.sonar.php.utils.Assertions.assertThat;

public class EnumCaseTest {
  @Test public void test() {
    assertThat(PHPLexicalGrammar.ENUM_CASE).matches("case A;").matches("#[A1(1)] case A;").
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/eddb02d847090e98cbe8c994fb5b5397f6f4dd5e/php-frontend/src/test/java/org/sonar/php/parser/statement/EnumCaseTest.java/left.java
    matches("case Enum;").notMatches("case A")
=======
    matches("case A = \'A\';").matches("case A = \'A\' . \'B\';").matches("case A = MyClass::CONSTANT;").notMatches("case A")
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/eddb02d847090e98cbe8c994fb5b5397f6f4dd5e/php-frontend/src/test/java/org/sonar/php/parser/statement/EnumCaseTest.java/right.java
    ;
  }
}