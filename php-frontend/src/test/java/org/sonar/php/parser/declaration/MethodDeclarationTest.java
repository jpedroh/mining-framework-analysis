package org.sonar.php.parser.declaration;
import org.junit.Test;
import org.sonar.php.parser.PHPLexicalGrammar;
import static org.sonar.php.utils.Assertions.assertThat;

public class MethodDeclarationTest {
  @Test public void test() throws Exception {
    assertThat(PHPLexicalGrammar.METHOD_DECLARATION).matches("function f ();").matches("function f () {}").matches("function &f () {}").matches("private function f () {}").matches("protected abstract function f () {}").matches("public static function f () {}").matches("final function f () {}").matches("function f () : bool {}").matches("function f () : ?bool {}").matches("function if() {}").matches(
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/61de31ef3667903965e4f26aeb52b329b5dde5a2/php-frontend/src/test/java/org/sonar/php/parser/declaration/MethodDeclarationTest.java/left.java
    "function match() {}"
=======
    "#[A1(4)] public function f() {}"
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/61de31ef3667903965e4f26aeb52b329b5dde5a2/php-frontend/src/test/java/org/sonar/php/parser/declaration/MethodDeclarationTest.java/right.java
    );
  }

  @Test public void optional_semicolon() {
    assertThat(PHPLexicalGrammar.METHOD_DECLARATION).matches("function fun() ?>").notMatches("function fun() ?> <?php {}");
  }
}