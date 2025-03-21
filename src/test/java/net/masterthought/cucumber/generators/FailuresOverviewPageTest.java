package net.masterthought.cucumber.generators;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.apache.velocity.VelocityContext;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.junit.Before;
import net.masterthought.cucumber.json.Element;
import org.junit.Test;
import net.masterthought.cucumber.json.Feature;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class FailuresOverviewPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getWebPage_ReturnsFailureReportFileName() {
    page = new FailuresOverviewPage(reportResult, configuration);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo(FailuresOverviewPage.WEB_PAGE);
  }

  @Test public void prepareReport_AddsCustomProperties() {
    page = new FailuresOverviewPage(reportResult, configuration);
    List<Element> failures = new ArrayList<>();
    for (Feature feature : features) {
      if (feature.getStatus().isPassed()) {
        continue;
      }
      for (Element element : feature.getElements()) {
        if (element.getStepsStatus().isPassed()) {
          continue;
        }
        failures.add(element);
      }
    }
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FailuresOverviewPageTest.java/left.java
    13
=======
    11
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FailuresOverviewPageTest.java/right.java
    );
    List<Element> elements = (List<Element>) context.get("failures");
    assertThat(elements).hasSameElementsAs(failures);
  }
}