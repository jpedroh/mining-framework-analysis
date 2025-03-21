import net.masterthought.cucumber.Configuration;
import java.io.File;
import net.masterthought.cucumber.ReportBuilder;
import java.io.IOException;
import net.masterthought.cucumber.presentation.PresentationMode;
import java.util.ArrayList;
import net.masterthought.cucumber.sorting.SortingMethod;
import java.util.List;
import org.junit.Test;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class LiveDemoTest {
  @Test public void generateDemoReport() throws IOException {
    File reportOutputDirectory = new File("target/demo");
    List<String> jsonFiles = new ArrayList<>();
    jsonFiles.add("src/test/resources/json/sample.json");
    String buildNumber = "101";
    String projectName = "Live Demo Project";
    Configuration configuration = new Configuration(reportOutputDirectory, projectName);
    configuration.setBuildNumber(buildNumber);
    configuration.addClassifications("Browser", "Firefox");
    configuration.addClassifications("Branch", "release/1.0");
    configuration.setSortingMethod(SortingMethod.NATURAL);
    configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
    configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
    configuration.setTrendsStatsFile(new File("target/test-classes/demo-trends.json"));
    ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
    reportBuilder.generateReports();
  }
}