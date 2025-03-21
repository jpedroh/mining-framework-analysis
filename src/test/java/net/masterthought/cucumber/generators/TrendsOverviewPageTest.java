package net.masterthought.cucumber.generators;
import mockit.Deencapsulation;
import java.io.File;
import net.masterthought.cucumber.ReportBuilder;
import java.io.IOException;
import net.masterthought.cucumber.ReportResult;
import net.masterthought.cucumber.Trends;
import net.masterthought.cucumber.generators.integrations.PageTest;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TrendsOverviewPageTest extends PageTest {
  private final String TRENDS_FILE = pathToSampleFile("cucumber-trends.json");

  private final String TRENDS_TMP_FILE = TRENDS_FILE + "-tmp";

  @Before public void setUp() throws IOException {
    setUpWithJson(SAMPLE_JSON);
    FileUtils.copyFile(new File(TRENDS_FILE), new File(TRENDS_TMP_FILE));
  }

  @Test public void getWebPage_ReturnsTrendsOverviewFileName() {
    page = new TrendsOverviewPage(reportResult, configuration, null);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo(TrendsOverviewPage.WEB_PAGE);
  }

  @Test public void prepareReport_AddsCustomProperties() {
    configuration.setBuildNumber("myBuild");
    Trends trends = Deencapsulation.invoke(ReportBuilder.class, "loadTrends", new File(TRENDS_TMP_FILE));
    page = new TrendsOverviewPage(reportResult, configuration, trends);
    Deencapsulation.setField(page, "reportResult", new ReportResult(features, configuration));
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(21);
    assertThat(context.get("buildNumbers")).isEqualTo(new String[] { "01_first", "other build", "05last" });
    assertThat(context.get("failedFeatures")).isEqualTo(new int[] { 1, 2, 5 });
    assertThat(context.get("passedFeatures")).isEqualTo(new int[] { 9, 18, 25 });
    assertThat(context.get("failedScenarios")).isEqualTo(new int[] { 10, 20, 20 });
    assertThat(context.get("passedScenarios")).isEqualTo(new int[] { 10, 20, 20 });
    assertThat(context.get("passedSteps")).isEqualTo(new int[] { 1, 3, 5 });
    assertThat(context.get("failedSteps")).isEqualTo(new int[] { 10, 30, 50 });
    assertThat(context.get("skippedSteps")).isEqualTo(new int[] { 100, 300, 500 });
    assertThat(context.get("pendingSteps")).isEqualTo(new int[] { 1000, 3000, 5000 });
    assertThat(context.get("undefinedSteps")).isEqualTo(new int[] { 10000, 30000, 50000 });
    assertThat(context.get("durations")).isEqualTo(new long[] { 3206126182398L, 3206126182399L, 3206126182310L });
  }
}