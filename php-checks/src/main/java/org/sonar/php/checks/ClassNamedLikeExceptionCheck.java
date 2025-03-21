package org.sonar.php.checks;
import java.util.Locale;
import org.sonar.check.Rule;
import org.sonar.php.symbols.Symbols;
import org.sonar.plugins.php.api.symbols.QualifiedName;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.declaration.ClassDeclarationTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

@Rule(key = "S2166") public class ClassNamedLikeExceptionCheck extends PHPVisitorCheck {
  private static final String MESSAGE = "Rename this class to remove \"Exception\" or correct its inheritance.";

  private static final QualifiedName EXCEPTION_FQN = QualifiedName.qualifiedName("Exception");

  @Override public void visitClassDeclaration(ClassDeclarationTree tree) {
    if (tree.is(Tree.Kind.CLASS_DECLARATION) && tree.name().text().toLowerCase(Locale.ENGLISH).endsWith("exception") && Symbols.get(tree).isOrSubClassOf(EXCEPTION_FQN).isFalse()) {
      context().newIssue(this, tree.name(), MESSAGE);
    }
    super.visitClassDeclaration(tree);
  }
}