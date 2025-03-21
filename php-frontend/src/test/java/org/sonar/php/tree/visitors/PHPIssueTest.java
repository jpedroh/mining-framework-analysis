package org.sonar.php.tree.visitors;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.sonar.php.tree.impl.expression.NameIdentifierTreeImpl;
import org.sonar.php.tree.impl.lexical.InternalSyntaxToken;
import org.sonar.php.utils.DummyCheck;
import org.sonar.plugins.php.api.visitors.PHPCheck;
import java.util.Collections;

public class PHPIssueTest {
  private static final PHPCheck CHECK = new DummyCheck();

  @Test public void test_no_line() throws Exception {
    PHPIssue issue = new PHPIssue(CHECK, "message");
    Assertions.assertThat(issue.check()).isEqualTo(CHECK);
    Assertions.assertThat(issue.message()).isEqualTo("message");
    Assertions.assertThat(issue.line()).isEqualTo(0);
    Assertions.assertThat(issue.cost()).isNull();
  }

  @Test public void test_with_line() throws Exception {
    final int line = 7;
    PHPIssue issue = new PHPIssue(CHECK, "message").line(line);
    Assertions.assertThat(issue.check()).isEqualTo(CHECK);
    Assertions.assertThat(issue.message()).isEqualTo("message");
    Assertions.assertThat(issue.line()).isEqualTo(line);
    Assertions.assertThat(issue.cost()).isNull();
  }

  @Test public void test_with_line_and_cost() throws Exception {
    final int cost = 7;
    PHPIssue issue = new PHPIssue(CHECK, "message").cost(cost);
    Assertions.assertThat(issue.check()).isEqualTo(CHECK);
    Assertions.assertThat(issue.message()).isEqualTo("message");
    Assertions.assertThat(issue.line()).isEqualTo(0);
    Assertions.assertThat(issue.cost()).isEqualTo(cost);
  }

  @Test public void test_setting_line_from_tree() throws Exception {
    final int line = 3;
    NameIdentifierTreeImpl tree = new NameIdentifierTreeImpl(new InternalSyntaxToken(line, 1, "tree", Collections.EMPTY_LIST, 0, false));
    PHPIssue issue = new PHPIssue(CHECK, "message").tree(tree);
    Assertions.assertThat(issue.check()).isEqualTo(CHECK);
    Assertions.assertThat(issue.message()).isEqualTo("message");
    Assertions.assertThat(issue.line()).isEqualTo(line);
    Assertions.assertThat(issue.cost()).isNull();
  }
}