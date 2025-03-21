package net.masterthought.cucumber.generators;
import net.masterthought.cucumber.generators.integrations.PageTest;
import java.util.ArrayList;
import net.masterthought.cucumber.json.Element;
import java.util.List;
import net.masterthought.cucumber.json.Feature;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(context.getKeys()).hasSize(11);
    List<Element> elements = (List<Element>) context.get("failures");
    assertThat(elements).hasSameElementsAs(failures);
  }
}