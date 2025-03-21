/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.php.checks;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.FunctionArgumentCheck;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;


@Rule(key = "S4830")
public class SSLCertificatesVerificationDisabledCheck extends FunctionArgumentCheck {

  private static final String MESSAGE = "Enable server certificate validation on this SSL/TLS connection.";

  private static final String CURL_SETOPT = "curl_setopt";
  private static final String CURLOPT_SSL_VERIFYPEER = "CURLOPT_SSL_VERIFYPEER";
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/left.java
  private static final Set<String> VERIFY_PEER_COMPLIANT_VALUES = ImmutableSet.of("true", "1");
||||||| /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/base.java
  private static final Set<String> VERIFY_PEER_COMPLIANT_VALUES = ImmutableSet.of("true", "1");
=======
  private static final Set<String> VERIFY_PEER_COMPLIANT_VALUES = ImmutableSet.of("false", "0");
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/right.java

  @Override
  public void visitFunctionCall(FunctionCallTree tree) {
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/left.java
    checkArgument(tree, CURL_SETOPT, new ArgumentIndicator(1, CURLOPT_SSL_VERIFYPEER), new ArgumentVerifier(2, VERIFY_PEER_COMPLIANT_VALUES, false));
||||||| /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/base.java
    String functionName = CheckUtils.getLowerCaseFunctionName(tree);
    List<ExpressionTree> arguments = tree.arguments();
=======
    checkArgument(tree, CURL_SETOPT, new ArgumentMatcher(1, CURLOPT_SSL_VERIFYPEER), new ArgumentVerifier(2, VERIFY_PEER_COMPLIANT_VALUES));
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/right.java

    super.visitFunctionCall(tree);
  }

<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/left.java
  protected void createIssue(ExpressionTree expressionTree) {
    context().newIssue(this, expressionTree, MESSAGE);
  }
||||||| /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/base.java
=======
  protected void createIssue(ExpressionTree argument) {
    context().newIssue(this, argument, MESSAGE);
  }
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/926cb8c06d4ab0dadb8f0f0e978cdb64f567510e/php-checks/src/main/java/org/sonar/php/checks/SSLCertificatesVerificationDisabledCheck.java/right.java
}
