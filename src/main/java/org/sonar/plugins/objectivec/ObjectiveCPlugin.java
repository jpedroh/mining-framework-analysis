package org.sonar.plugins.objectivec;
import java.util.List;
import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.objectivec.colorizer.ObjectiveCColorizerFormat;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.plugins.objectivec.core.ObjectiveCSourceImporter;
import org.sonar.plugins.objectivec.cpd.ObjectiveCCpdMapping;
import com.google.common.collect.ImmutableList;

@Properties(value = { @Property(key = 
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
OCLintSensor
=======
ObjectiveCCoverageSensor
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
.
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
REPORT_PATH_KEY
=======
REPORT_PATTERN_KEY
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
, defaultValue = 
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
OCLintSensor
=======
ObjectiveCCoverageSensor
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
.
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
DEFAULT_REPORT_PATH
=======
DEFAULT_REPORT_PATTERN
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
, name = 
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
"Path to oclint pmd formatted report"
=======
"Path to unit test coverage report(s)"
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
, description = 
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
"Relative to projects\' root."
=======
"Relative to projects\' root. Ant patterns are accepted"
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
, global = false, project = true) }) public class ObjectiveCPlugin extends SonarPlugin {
  public List<Class<? extends Extension>> getExtensions() {
    return ImmutableList.of(ObjectiveC.class, ObjectiveCSourceImporter.class, ObjectiveCColorizerFormat.class, ObjectiveCCpdMapping.class, ObjectiveCSquidSensor.class, ObjectiveCProfile.class, OCLintRuleRepository.class, OCLintSensor.class, OCLintProfile.class, 
<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/left.java
    OCLintProfileImporter
=======
    ObjectiveCCoverageSensor
>>>>>>> /usr/src/app/output/octo-technology/sonar-objective-c/a1e94950696d8ca2430d435be2221196a7089ec0/src/main/java/org/sonar/plugins/objectivec/ObjectiveCPlugin.java/right.java
    .class);
  }

  public static final String FALSE = "false";

  public static final String FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes";

  public static final String FILE_SUFFIXES_DEFVALUE = "h,m";

  public static final String PROPERTY_PREFIX = "sonar.objectivec";

  public static final String TEST_FRAMEWORK_KEY = PROPERTY_PREFIX + ".testframework";

  public static final String TEST_FRAMEWORK_DEFAULT = "ghunit";
}