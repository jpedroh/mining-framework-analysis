package net.masterthought.cucumber.generators;
import net.masterthought.cucumber.generators.integrations.PageTest;
import java.util.List;
import net.masterthought.cucumber.json.support.TagObject;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagsOverviewPageTest extends PageTest {
  @Before public void setUp() {
    setUpWithJson(SAMPLE_JSON);
  }

  @Test public void getWebPage_ReturnsTagOverviewReportFileName() {
    page = new TagsOverviewPage(reportResult, configuration);
    String fileName = page.getWebPage();
    assertThat(fileName).isEqualTo(TagsOverviewPage.WEB_PAGE);
  }

  @Test public void prepareReport_AddsCustomProperties() {
    page = new TagsOverviewPage(reportResult, configuration);
    page.prepareReport();
    VelocityContext context = page.context;
    assertThat(context.getKeys()).hasSize(14);
    assertThat(context.get("all_tags")).isEqualTo(tags);
    assertThat(context.get("report_summary")).isEqualTo(reportResult.getTagReport());
    assertThat(context.get("chart_categories")).isEqualTo(TagsOverviewPage.generateTagLabels(tags));
    assertThat(context.get("chart_data")).isEqualTo(TagsOverviewPage.generateTagValues(tags));
  }

  @Test public void prepareReport_setTagsToExcludeFromChart_ReturnsFilteredTags() {
    page = new TagsOverviewPage(reportResult, configuration);
    configuration.setTagsToExcludeFromChart("@checkout", "@feature.*");
    page.prepareReport();
    VelocityContext context = page.context;
    List<TagObject> tags = (List<TagObject>) context.get("all_tags");
    assertThat(tags).hasSize(1);
    assertThat(tags.get(0).getName()).isEqualTo("@fast");
    assertThat(context.get("chart_categories")).isEqualTo(new String[] { "@fast" });
    assertThat(context.get("chart_data")).isEqualTo(new String[][] { { "100.00" }, { "0.00" }, { "0.00" }, { "0.00" }, { "0.00" } });
  }

  @Test public void generateTagLabels_ReturnsTags() {
    List<TagObject> allTags = this.tags;
    String[] labels = TagsOverviewPage.generateTagLabels(allTags);
    assertThat(labels).isEqualTo(new String[] { "@checkout", "@fast", "@featureTag" });
  }

  @Test public void generateTagValues_ReturnsTagValues() {
    List<TagObject> allTags = this.tags;
    String[][] labels = TagsOverviewPage.generateTagValues(allTags);
    assertThat(labels).isEqualTo(new String[][] { { "62.50", "100.00", "100.00" }, { "6.25", "0.00", "0.00" }, { "12.50", "0.00", "0.00" }, { "6.25", "0.00", "0.00" }, { "12.50", "0.00", "0.00" } });
  }
}