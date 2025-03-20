package org.sonar.objectivec;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import org.junit.Test;
import org.sonar.objectivec.api.ObjectiveCMetric;
import org.sonar.squidbridge.api.SourceFile;

public class ObjectiveCAstScannerTest {

<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/9e1766fa33eb3709a94ff91b3b140964cf1416d1/src/test/java/org/sonar/objectivec/ObjectiveCAstScannerTest.java/left.java
  @Test public void lines() {
    SourceFile file = ObjectiveCAstScanner.scanSingleFile(new File("src/test/resources/objcSample.h"));
    assertThat(file.getInt(ObjectiveCMetric.LINES), is(18));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/9e1766fa33eb3709a94ff91b3b140964cf1416d1/src/test/java/org/sonar/objectivec/ObjectiveCAstScannerTest.java/left.java
  @Test public void lines_of_code() {
    SourceFile file = ObjectiveCAstScanner.scanSingleFile(new File("src/test/resources/objcSample.h"));
    assertThat(file.getInt(ObjectiveCMetric.LINES_OF_CODE), is(5));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/octo-technology/sonar-objective-c/9e1766fa33eb3709a94ff91b3b140964cf1416d1/src/test/java/org/sonar/objectivec/ObjectiveCAstScannerTest.java/left.java
  @Test public void comments() {
    SourceFile file = ObjectiveCAstScanner.scanSingleFile(new File("src/test/resources/objcSample.h"));
    assertThat(file.getInt(ObjectiveCMetric.COMMENT_LINES), is(4));
    assertThat(file.getNoSonarTagLines(), hasItem(10));
    assertThat(file.getNoSonarTagLines().size(), is(1));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}