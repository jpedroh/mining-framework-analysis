/*
 * SonarQube Findbugs Plugin
 * Copyright (C) 2012 SonarSource
 * sonarqube@googlegroups.com
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.findbugs;

import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import org.sonar.api.utils.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FindbugsPluginTest {

  @ParameterizedTest
  @CsvSource({
    "9.7,24",
    // We expect one more extension (the "sonar.findbugs.analyzeTests" property) when the version is >= 9.8
    "9.8,25"
  })
  void testGetExtensions(String version, int expectedExtensionsCount) {

    SonarRuntime runtime = mock(SonarRuntime.class);
    when(runtime.getApiVersion()).thenReturn(Version.parse(version));
    Plugin.Context ctx = new Plugin.Context(runtime);

    FindbugsPlugin plugin = new FindbugsPlugin();
    plugin.define(ctx);

<<<<<<< /usr/src/app/output/sonarsource/sonar-findbugs/185c5314e370b844d643c036c0bebada30bb7de6/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/left.java
    assertEquals(14, ctx.getExtensions().size(), "extensions count");
||||||| /usr/src/app/output/sonarsource/sonar-findbugs/185c5314e370b844d643c036c0bebada30bb7de6/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/base.java
    assertEquals(24, ctx.getExtensions().size(), "extensions count");
=======
    assertEquals(expectedExtensionsCount, ctx.getExtensions().size(), "extensions count");
>>>>>>> /usr/src/app/output/sonarsource/sonar-findbugs/185c5314e370b844d643c036c0bebada30bb7de6/src/test/java/org/sonar/plugins/findbugs/FindbugsPluginTest.java/right.java
  }
}
