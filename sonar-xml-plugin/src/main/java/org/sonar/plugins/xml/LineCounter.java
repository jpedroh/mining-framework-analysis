package org.sonar.plugins.xml;
import java.io.IOException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.xml.checks.XmlFile;
import org.sonar.plugins.xml.compat.CompatibleInputFile;
import org.sonar.plugins.xml.parsers.LineCountParser;
import org.xml.sax.SAXException;

/**
 * Count lines of code in XML files.
 *
 * @author Matthijs Galesloot
 */
public final class LineCounter {
  private static final Logger LOG = LoggerFactory.getLogger(LineCounter.class);

  private LineCounter() {
  }

  private static void saveMeasures(XmlFile xmlFile, LineCountData data, FileLinesContext fileLinesContext, SensorContext context) throws IOException, SAXException {
    data.updateAccordingTo(xmlFile.getLineDelta());
    for (int line = 1; line <= data.linesNumber(); line++) {
      fileLinesContext.setIntValue(CoreMetrics.NCLOC_DATA_KEY, line, data.linesOfCodeLines().contains(line) ? 1 : 0);
      fileLinesContext.setIntValue(CoreMetrics.COMMENT_LINES_DATA_KEY, line, data.effectiveCommentLines().contains(line) ? 1 : 0);
    }
    fileLinesContext.save();
    saveMeasure(context, xmlFile.getInputFile(), CoreMetrics.LINES, data.linesNumber());
    saveMeasure(context, xmlFile.getInputFile(), CoreMetrics.COMMENT_LINES, data.effectiveCommentLines().size());
    saveMeasure(context, xmlFile.getInputFile(), CoreMetrics.NCLOC, data.linesOfCodeLines().size());
  }

  private static <T extends Serializable> void saveMeasure(SensorContext context, 
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-xml/0e1fee2967a660d9ad773b8279e8ffe5ab4d0908/sonar-xml-plugin/src/main/java/org/sonar/plugins/xml/LineCounter.java/left.java
  InputFile
=======
  CompatibleInputFile
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-xml/0e1fee2967a660d9ad773b8279e8ffe5ab4d0908/sonar-xml-plugin/src/main/java/org/sonar/plugins/xml/LineCounter.java/right.java
   inputFile, Metric<T> metric, T value) {
    context.<T>newMeasure().withValue(value).forMetric(metric).on(
<<<<<<< /usr/src/app/output/sonarcommunity/sonar-xml/0e1fee2967a660d9ad773b8279e8ffe5ab4d0908/sonar-xml-plugin/src/main/java/org/sonar/plugins/xml/LineCounter.java/left.java
    inputFile
=======
    inputFile.wrapped()
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-xml/0e1fee2967a660d9ad773b8279e8ffe5ab4d0908/sonar-xml-plugin/src/main/java/org/sonar/plugins/xml/LineCounter.java/right.java
    ).save();
  }

  public static void analyse(SensorContext context, FileLinesContextFactory fileLinesContextFactory, XmlFile xmlFile) {
    LOG.debug("Count lines in " + xmlFile.getAbsolutePath());
    try {
      saveMeasures(xmlFile, new LineCountParser(xmlFile.getContents(), xmlFile.getCharset()).getLineCountData(), fileLinesContextFactory.createFor(xmlFile.getInputFile().wrapped()), context);
    } catch (Exception e) {
      LOG.warn("Unable to count lines for file: " + xmlFile.getAbsolutePath());
      LOG.warn("Cause: ", e);
    }
  }
}