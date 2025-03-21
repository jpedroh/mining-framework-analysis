/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2021 SonarSource SA
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
package org.sonar.php.parser.statement;

import org.junit.Test;
import org.sonar.php.parser.PHPLexicalGrammar;

import static org.sonar.php.utils.Assertions.assertThat;

public class EnumCaseTest {

  @Test
  public void test() {
    assertThat(PHPLexicalGrammar.ENUM_CASE)
      .matches("case A;")
      .matches("#[A1(1)] case A;")
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-php/eddb02d847090e98cbe8c994fb5b5397f6f4dd5e/php-frontend/src/test/java/org/sonar/php/parser/statement/EnumCaseTest.java/left.java
      .matches("case Enum;")
||||||| /usr/src/app/output/sonarcommunity/sonar-php/eddb02d847090e98cbe8c994fb5b5397f6f4dd5e/php-frontend/src/test/java/org/sonar/php/parser/statement/EnumCaseTest.java/base.java
=======
      .matches("case A = 'A';")
      .matches("case A = 'A' . 'B';")
      .matches("case A = MyClass::CONSTANT;")
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-php/eddb02d847090e98cbe8c994fb5b5397f6f4dd5e/php-frontend/src/test/java/org/sonar/php/parser/statement/EnumCaseTest.java/right.java
      .notMatches("case A")
    ;
  }
}
