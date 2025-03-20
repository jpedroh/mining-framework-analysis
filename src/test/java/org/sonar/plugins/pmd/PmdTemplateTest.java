/*
 * SonarQube PMD Plugin
 * Copyright (C) 2012-2018 SonarSource SA
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
package org.sonar.plugins.pmd;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.java.JavaLanguageHandler;
import org.mockito.stubbing.Answer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

 class PmdTemplateTest {

    private final RuleSets rulesets = mock(RuleSets.class);
  private final RuleContext ruleContext = mock(RuleContext.class);
  private final PMDConfiguration configuration = mock(PMDConfiguration.class);
  private final SourceCodeProcessor processor = mock(SourceCodeProcessor.class);
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-pmd/933775819973c32cc8a831ea756e9a6c7a10f113/src/test/java/org/sonar/plugins/pmd/PmdTemplateTest.java/left.java
  @Test
  public void should_process_input_file() throws Exception {
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        InputStream inputStreamArg = (InputStream) invocation.getArguments()[0];
        List<String> inputStreamLines = CharStreams.readLines(new InputStreamReader(inputStreamArg));
        assertThat(inputStreamLines).containsExactly("Example source");
        return null;
      }
    }).when(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));
    
    new PmdTemplate(configuration, processor).process(inputFile, rulesets, ruleContext);

    verify(ruleContext).setSourceCodeFilename(inputFile.getAbsolutePath());
    verify(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));
  }
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        InputStream inputStreamArg = (InputStream) invocation.getArguments()[0];
        List<String> inputStreamLines = CharStreams.readLines(new InputStreamReader(inputStreamArg));
        assertThat(inputStreamLines).containsExactly("Example source");
        return null;
      }
||||||| /usr/src/app/output/sonarcommunity/sonar-pmd/933775819973c32cc8a831ea756e9a6c7a10f113/src/test/java/org/sonar/plugins/pmd/PmdTemplateTest.java/base.java
  @Test
  public void should_process_input_file() throws Exception {
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        InputStream inputStreamArg = (InputStream) invocation.getArguments()[0];
        List<String> inputStreamLines = CharStreams.readLines(new InputStreamReader(inputStreamArg));
        assertThat(inputStreamLines).containsExactly("Example source");
        return null;
      }
    }).when(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));
    
    new PmdTemplate(configuration, processor).process(inputFile, rulesets, ruleContext);

    verify(ruleContext).setSourceCodeFilename(inputFile.getAbsolutePath());
    verify(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));
  }
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        InputStream inputStreamArg = (InputStream) invocation.getArguments()[0];
        List<String> inputStreamLines = CharStreams.readLines(new InputStreamReader(inputStreamArg));
        assertThat(inputStreamLines).containsExactly("Example source");
        return null;
      }
=======
  @Test
    void should_process_input_file() throws Exception {
        doAnswer((Answer<Void>) invocation -> {
            final InputStream inputStreamArg = (InputStream) invocation.getArguments()[0];
            final List<String> inputStreamLines =
                    new BufferedReader(new InputStreamReader(inputStreamArg))
                            .lines()
                            .collect(Collectors.toList());
            assertThat(inputStreamLines).containsExactly("Example source");
            return null;
        }).when(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));

        new PmdTemplate(configuration, processor).process(inputFile, rulesets, ruleContext);

        verify(ruleContext).setSourceCodeFilename(inputFile.uri().toString());
        verify(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));
    }
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-pmd/933775819973c32cc8a831ea756e9a6c7a10f113/src/test/java/org/sonar/plugins/pmd/PmdTemplateTest.java/right.java
  @Test
  void should_ignore_PMD_error() throws PMDException {
      doThrow(new PMDException("BUG"))
              .when(processor).processSourceCode(any(InputStream.class), any(RuleSets.class), any(RuleContext.class));

      new PmdTemplate(configuration, processor).process(inputFile, rulesets, ruleContext);
  }
  @Test
  void java12_version() {
      assertThat(PmdTemplate.languageVersion("1.2").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  void java5_version() {
      assertThat(PmdTemplate.languageVersion("5").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  void java6_version() {
      assertThat(PmdTemplate.languageVersion("6").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  void java7_version() {
      assertThat(PmdTemplate.languageVersion("7").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  void java8_version() {
      assertThat(PmdTemplate.languageVersion("8").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  public void java9_version() {
    assertThat(PmdTemplate.languageVersion("9").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  public void java10_version() {
    assertThat(PmdTemplate.languageVersion("10").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
  @Test
  public void java11_version() {
    assertThat(PmdTemplate.languageVersion("11").getLanguageVersionHandler()).isInstanceOf(JavaLanguageHandler.class);
  }
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-pmd/933775819973c32cc8a831ea756e9a6c7a10f113/src/test/java/org/sonar/plugins/pmd/PmdTemplateTest.java/left.java
  @Test(expected = IllegalArgumentException.class)
  public void should_fail_on_invalid_java_version() {
    PmdTemplate.create("12.2", mock(ClassLoader.class), Charsets.UTF_8);
  }
||||||| /usr/src/app/output/sonarcommunity/sonar-pmd/933775819973c32cc8a831ea756e9a6c7a10f113/src/test/java/org/sonar/plugins/pmd/PmdTemplateTest.java/base.java
  @Test(expected = IllegalArgumentException.class)
  public void should_fail_on_invalid_java_version() {
    PmdTemplate.create("12.2", mock(ClassLoader.class), Charsets.UTF_8);
  }
=======
  @Test
    void should_fail_on_invalid_java_version() {
        final Throwable thrown = catchThrowable(() -> PmdTemplate.create("12.2", mock(ClassLoader.class), StandardCharsets.UTF_8));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-pmd/933775819973c32cc8a831ea756e9a6c7a10f113/src/test/java/org/sonar/plugins/pmd/PmdTemplateTest.java/right.java
    private final InputFile inputFile = TestInputFileBuilder.create(
            "src",
            "test/resources/org/sonar/plugins/pmd/source.txt"
    ).build();
    @Test
    void shouldnt_fail_on_valid_java_version() {
        PmdTemplate.create("6", mock(ClassLoader.class), StandardCharsets.UTF_8);
    }

    /**
     * SONARPLUGINS-3318
     */
    @Test
    void should_set_classloader() {
        ClassLoader classloader = mock(ClassLoader.class);
        PmdTemplate pmdTemplate = PmdTemplate.create("6", classloader, StandardCharsets.UTF_8);
        assertThat(pmdTemplate.configuration().getClassLoader()).isEqualTo(classloader);
    }

    @Test
    void should_set_encoding() {
        PmdTemplate pmdTemplate = PmdTemplate.create("6", mock(ClassLoader.class), StandardCharsets.UTF_16BE);
        assertThat(pmdTemplate.configuration().getSourceEncoding().toString()).isEqualTo("UTF-16BE");
    }

}
