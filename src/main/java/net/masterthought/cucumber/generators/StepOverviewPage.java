package net.masterthought.cucumber.generators;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.json.support.StepObject;
import net.masterthought.cucumber.util.Util;

/**
 * Presents details about how long steps are executed (adds the same steps and presents sum).
 * 
 * @author Damian Szczepanik (damianszczepanik@github)
 *
 */
public class StepOverviewPage extends AbstractPage {
  public StepOverviewPage(ReportBuilder reportBuilder) {
    super(reportBuilder, "stepOverview.vm");
  }

  @Override public void generatePage() throws IOException {
    super.generatePage();
    contextMap.put("steps", sortStepsByDate());
    int allOccurrences = 0;
    long allDurations = 0;
    for (StepObject stepObject : reportInformation.getAllSteps().values()) {
      allOccurrences += stepObject.getTotalOccurrences();
      allDurations += stepObject.getTotalDuration();
    }
    contextMap.put("all_occurrences", allOccurrences);
    contextMap.put("all_durations", allDurations);
    contextMap.put("all_formatted_durations", Util.formatDuration(allDurations));
    super.generateReport("step-overview.html");
  }

  private StepObject[] sortStepsByDate() {
    Map<String, StepObject> steps = reportInformation.getStepObject();
    StepObject[] array = new StepObject[steps.
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/9fa019efbb3e9219643a69303c549bdb7b6515a2/src/main/java/net/masterthought/cucumber/generators/StepOverviewPage.java/left.java
    size()
=======
    getAllSteps().size()
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/9fa019efbb3e9219643a69303c549bdb7b6515a2/src/main/java/net/masterthought/cucumber/generators/StepOverviewPage.java/right.java
    ];
    Arrays.sort(steps.
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/9fa019efbb3e9219643a69303c549bdb7b6515a2/src/main/java/net/masterthought/cucumber/generators/StepOverviewPage.java/left.java
    values().toArray(array)
=======
    getAllSteps().values().toArray(array)
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/9fa019efbb3e9219643a69303c549bdb7b6515a2/src/main/java/net/masterthought/cucumber/generators/StepOverviewPage.java/right.java
    , new DurationCompator());
    return array;
  }

  private static class DurationCompator implements Comparator<StepObject> {
    @Override public int compare(StepObject o1, StepObject o2) {
      return Long.signum(o2.getTotalDuration() - o1.getTotalDuration());
    }
  }
}