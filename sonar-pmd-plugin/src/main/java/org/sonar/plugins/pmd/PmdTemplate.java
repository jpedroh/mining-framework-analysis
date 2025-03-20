package org.sonar.plugins.pmd;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class PmdTemplate {
  private static final Logger LOG = Loggers.get(PmdTemplate.class);

  private static final Map<String, String> JAVA_VERSIONS = prepareVersions();

  private static Map<String, String> prepareVersions() {
    final Map<String, String> versions = new HashMap<>();
    versions.put("1.1", "1.3");
    versions.put("1.2", "1.3");
    versions.put("5", "1.5");
    versions.put("6", "1.6");
    versions.put("7", "1.7");
    versions.put("8", "1.8");
    versions.put("9", "9");
    versions.put("1.9", "9");
    versions.put("10", "10");
    versions.put("1.10", "10");
    versions.put("11", "11");
    versions.put("1.11", "11");
    return versions;
  }

  private SourceCodeProcessor processor;

  private PMDConfiguration configuration;

  PmdTemplate(PMDConfiguration configuration, SourceCodeProcessor processor) {
    this.configuration = configuration;
    this.processor = processor;
  }

  public static PmdTemplate create(String javaVersion, ClassLoader classloader, Charset charset) {
    PMDConfiguration configuration = new PMDConfiguration();
    configuration.setDefaultLanguageVersion(languageVersion(javaVersion));
    configuration.setClassLoader(classloader);
    configuration.setSourceEncoding(charset.name());
    SourceCodeProcessor processor = new SourceCodeProcessor(configuration);
    return new PmdTemplate(configuration, processor);
  }

  static LanguageVersion languageVersion(String javaVersion) {
    String version = normalize(javaVersion);
    LanguageVersion languageVersion = new JavaLanguageModule().getVersion(version);
    if (languageVersion == null) {
      throw new IllegalArgumentException("Unsupported Java version for PMD: " + version);
    }
    LOG.info("Java version: " + version);
    return languageVersion;
  }

  private static String normalize(String version) {
    return JAVA_VERSIONS.getOrDefault(version, version);
  }

  PMDConfiguration configuration() {
    return configuration;
  }

  public void process(InputFile file, RuleSets rulesets, RuleContext ruleContext) {
    ruleContext.setSourceCodeFile(
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-pmd/2301c0627b5ab5276acb103f933e68d2ab6ca3be/sonar-pmd-plugin/src/main/java/org/sonar/plugins/pmd/PmdTemplate.java/left.java
    new File(file.uri().toString())
=======
    Paths.get(file.uri()).toFile()
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-pmd/2301c0627b5ab5276acb103f933e68d2ab6ca3be/sonar-pmd-plugin/src/main/java/org/sonar/plugins/pmd/PmdTemplate.java/right.java
    );
    try (InputStream inputStream = file.inputStream()) {
      processor.processSourceCode(inputStream, rulesets, ruleContext);
    } catch (RuntimeException | IOException | PMDException e) {
      LOG.error("Fail to execute PMD. Following file is ignored: " + file, e);
    }
  }
}