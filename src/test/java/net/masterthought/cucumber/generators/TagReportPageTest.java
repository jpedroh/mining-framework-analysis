package net.masterthought.cucumber.generators;
import static org.assertj.core.api.Assertions.assertThat;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.apache.velocity.VelocityContext;
import net.masterthought.cucumber.json.support.TagObject;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagReportPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getWebPage_ReturnsTagReportFileName() {
    TagObject tag = tags.get(0);
    page = new TagReportPage(reportResult, configuration, tag);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo(tag.getReportFileName());
  }

  @Test public void prepareReport_AddsCustomProperties() {
    TagObject tag = tags.get(1);
    page = new TagReportPage(reportResult, configuration, tag);
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/TagReportPageTest.java/left.java
    13
=======
    11
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/TagReportPageTest.java/right.java
    );
    assertThat(context.get("tag")).isEqualTo(tag);
  }
}