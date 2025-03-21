package org.sonar.php.parser.statement;
import org.junit.Test;
import org.sonar.php.parser.PHPLexicalGrammar;
import static org.sonar.php.utils.Assertions.assertThat;

public class StatementTest {
  @Test public void test() {
    assertThat(PHPLexicalGrammar.STATEMENT).matches("{}").matches("label:").matches("if ($a): endif;").matches("while($a) {}").matches("for ($i = 1; $i <= 10; $i++) {}").matches("switch ($a) {}").matches("break;").matches("continue;").matches("return;").matches(";").matches("yield $a;").matches("[$a, &$b] = $array;").matches("list($a, &$b) = $array;").matches("foreach ($array as list(&$a, $b)) { $a = 7; }").matches("global $a;").matches("echo \"Hi\";").matches("$a = b\'hello\';").matches("unset($a);").matches("yield yield;").matches("die(yield $foo);").matches("yield from [yield];").matches("list($value) = yield;").matches("var_dump(yield * -1);").matches("var_dump([yield \"k\" => \"a\" . \"b\"]);").matches("$$varName = yield;").matches("$gen = yield;").matches("$var = function () {};").matches("foo();").matches("Foo::bar();").
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/2b1240c2482c763e1523e38661bc8f5c5194763c/php-frontend/src/test/java/org/sonar/php/parser/statement/StatementTest.java/left.java
    matches("\'Foo::bar\'();").matches("[\'Foo\',\'bar\']();").matches("[A::class, $method_name]();").matches("null();")
=======
    matches("\'Foo::bar\'();").matches("[\'Foo\',\'bar\']();")
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/2b1240c2482c763e1523e38661bc8f5c5194763c/php-frontend/src/test/java/org/sonar/php/parser/statement/StatementTest.java/right.java
    ;
  }

  @Test public void optional_semicolon() {
    assertThat(PHPLexicalGrammar.STATEMENT).matches("continue ?>");
  }

  @Test public void top_statement() {
    assertThat(PHPLexicalGrammar.TOP_STATEMENT).matches("__halt_compiler();");
  }
}