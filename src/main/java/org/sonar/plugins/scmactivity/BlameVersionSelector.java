/*
 * SonarQube SCM Activity Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
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

<<<<<<< /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/left.java
  public BlameVersionSelector(ScmConfiguration configuration, Blame blame, Sha1Generator sha1Generator, FileToResource fileToResource, ProjectFileSystem projectFileSystem) {
    this.configuration = configuration;
||||||| /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/base.java
  public BlameVersionSelector(Blame blame, Sha1Generator sha1Generator, FileToResource fileToResource, ProjectFileSystem projectFileSystem) {
=======
  public BlameVersionSelector(Blame blame) {
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/right.java
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
||||||| /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/base.java
    try {
      Resource resource = fileToResource.toResource(inputFile, context);
      Charset charset = projectFileSystem.getSourceCharset();

      String fileContent = FileUtils.readFileToString(file, charset.name());

      String currentSha1 = sha1Generator.find(fileContent);
      if (currentSha1.equals(previousSha1)) {
        return fileNotChanged(file, resource);
      }

      String[] lines = fileContent.split("(\r)?\n|\r", -1);
      return fileChanged(file, resource, currentSha1, lines.length);
    } catch (IOException e) {
      LOG.error("Unable to get scm information: {}", file, e);
      return MeasureUpdate.NONE;
=======
    if (inputFile.status() == InputFile.Status.SAME && hasPreviousMeasures) {
      return fileNotChanged(file, sonarFile);
>>>>>>> /usr/src/app/output/sonarcommunity/sonar-scm-activity/5cc251bf86feb0684541aefacfab932916d461e7/src/main/java/org/sonar/plugins/scmactivity/BlameVersionSelector.java/right.java
    }

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
