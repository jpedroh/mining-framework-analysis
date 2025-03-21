package net.masterthought.cucumber.generators;
import static org.assertj.core.api.Assertions.assertThat;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.apache.velocity.VelocityContext;
import net.masterthought.cucumber.json.Element;
import org.junit.Before;
import net.masterthought.cucumber.json.Embedding;
import org.junit.Test;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.Step;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class FeatureReportPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getWebPage_ReturnsFeatureFileName() {
    Feature feature = features.get(1);
    page = new FeatureReportPage(reportResult, configuration, feature);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo(feature.getReportFileName());
  }

  @Test public void prepareReport_AddsCustomProperties() {
    Feature feature = features.get(1);
    page = new FeatureReportPage(reportResult, configuration, feature);
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FeatureReportPageTest.java/left.java
    13
=======
    11
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/47f552e3abd1197a640f0548fc194f38efc2eced/src/test/java/net/masterthought/cucumber/generators/FeatureReportPageTest.java/right.java
    );
    assertThat(context.get("feature")).isEqualTo(feature);
  }

  @Test public void getMimeType_OnEmbeddingFromV2CucumberReportFile_SupportsScreenshots() {
    Feature feature = features.get(0);
    Element element = feature.getElements()[0];
    Step step = element.getSteps()[0];
    Embedding[] embeddings = step.getEmbeddings();
    assertThat(embeddings[0].getMimeType()).isEqualTo("image/url");
  }

  @Test public void getMimeType_OnEmbeddingFromV3CucumberReportFile_SupportsScreenshots() {
    Feature feature = features.get(0);
    Element element = feature.getElements()[0];
    Step step = element.getSteps()[0];
    Embedding[] embeddings = step.getEmbeddings();
    assertThat(embeddings[1].getMimeType()).isEqualTo("text/plain");
  }
}