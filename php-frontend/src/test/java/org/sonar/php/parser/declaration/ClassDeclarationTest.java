package org.sonar.php.parser.declaration;
import org.junit.Test;
import org.sonar.php.parser.PHPLexicalGrammar;
import static org.sonar.php.utils.Assertions.assertThat;

public class ClassDeclarationTest {
  @Test public void test() {
    assertThat(PHPLexicalGrammar.CLASS_DECLARATION).matches("class C {}").matches("class match {}").matches("abstract class C {}").matches("final class C {}").matches("class C extends A {}").matches("class C implements B {}").matches(
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/61de31ef3667903965e4f26aeb52b329b5dde5a2/php-frontend/src/test/java/org/sonar/php/parser/declaration/ClassDeclarationTest.java/left.java
    "class C extends A implements B {}"
=======
    "#[A1(1)] class C {}"
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/61de31ef3667903965e4f26aeb52b329b5dde5a2/php-frontend/src/test/java/org/sonar/php/parser/declaration/ClassDeclarationTest.java/right.java
    ).notMatches("class A extends B, C {}");
  }
}