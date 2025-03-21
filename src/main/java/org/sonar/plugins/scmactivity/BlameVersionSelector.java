package org.sonar.plugins.scmactivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.resources.Resource;
import java.io.File;

public class BlameVersionSelector implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(BlameVersionSelector.class);

  private final ScmConfiguration configuration;

  private final Blame blame;

  public BlameVersionSelector(ScmConfiguration configuration, Blame blame) {
    this.configuration = configuration;
    this.blame = blame;
  }

  public MeasureUpdate detect(Resource sonarFile, InputFile inputFile, SensorContext context, boolean hasPreviousMeasures) {
    File file = inputFile.file();

<<<<<<< /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/left.java
    try {
      Resource resource = fileToResource.toResource(inputFile, context);
      Charset charset = projectFileSystem.getSourceCharset();
      String fileContent = FileUtils.readFileToString(file, charset.name());
      String currentSha1 = sha1Generator.find(fileContent);
      if (currentSha1.equals(previousSha1) && !configuration.isReloadBlameEnabled()) {
        return fileNotChanged(file, resource);
      }
      String[] lines = fileContent.split("(\r)?\n|\r", -1);
      return fileChanged(file, resource, currentSha1, lines.length);
    } catch (IOException e) {
      LOG.error("Unable to get scm information: {}", file, e);
      return MeasureUpdate.NONE;
    }
=======
    if (inputFile.status() == InputFile.Status.SAME && hasPreviousMeasures) {
      return fileNotChanged(file, sonarFile);
    }
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/right.java

    return fileChanged(file, sonarFile, inputFile.lines());
  }

  private MeasureUpdate fileNotChanged(File file, Resource resource) {
    LOG.debug("File not changed since previous analysis: {}", file);
    return new CopyPreviousMeasures(resource);
  }

  private MeasureUpdate fileChanged(File file, Resource resource, int lineCount) {
    LOG.debug("File changed since previous analysis: {}", file);
    return blame.save(file, resource, lineCount);
  }
}