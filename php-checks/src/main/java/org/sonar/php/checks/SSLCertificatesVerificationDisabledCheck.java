package org.sonar.php.checks;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.FunctionArgumentCheck;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;

@Rule(key = "S4830") public class SSLCertificatesVerificationDisabledCheck extends FunctionArgumentCheck {
  private static final String MESSAGE = "Enable server certificate validation on this SSL/TLS connection.";

  private static final String CURL_SETOPT = "curl_setopt";

  private static final String CURLOPT_SSL_VERIFYPEER = "CURLOPT_SSL_VERIFYPEER";

  private static final Set<String> VERIFY_PEER_COMPLIANT_VALUES = ImmutableSet.of("false", "0");

  @Override public void visitFunctionCall(FunctionCallTree tree) {
    checkArgument(tree, CURL_SETOPT, new 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/left.java
    ArgumentIndicator
=======
    ArgumentMatcher
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/right.java
    (1, CURLOPT_SSL_VERIFYPEER), new ArgumentVerifier(2, VERIFY_PEER_COMPLIANT_VALUES, false));
    super.visitFunctionCall(tree);
  }

  protected void createIssue(
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/left.java
  ExpressionTree expressionTree
=======
  ExpressionTree argument
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/right.java
  ) {
    context().newIssue(this, 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/left.java
    expressionTree
=======
    argument
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/right.java
    , MESSAGE);
  }
}