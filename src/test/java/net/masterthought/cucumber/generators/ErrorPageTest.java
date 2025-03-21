package net.masterthought.cucumber.generators;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class ErrorPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void prepareReport_AddsCustomProperties() {
    Exception exception = new Exception();
    page = new ErrorPage(null, configuration, exception, jsonReports);
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/ErrorPageTest.java/left.java
    15
=======
    13
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/ErrorPageTest.java/right.java
    );
    assertThat(context.get("classifications")).isInstanceOf(List.class);
    assertThat(context.get("output_message")).isEqualTo(ExceptionUtils.getStackTrace(exception));
    assertThat(context.get("json_files")).isEqualTo(jsonReports);
    assertThat(context.get("directory_qualifier")).isEqualTo(configuration.getDirectoryQualifier());
  }
}