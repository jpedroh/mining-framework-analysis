package com.github.maven_nar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.util.FileUtils;

/**
 * Layout which expands a nar file into:
 *
 * <pre>
 * nar/noarch/include
 * nar/aol/<aol>-<type>/bin
 * nar/aol/<aol>-<type>/lib
 * </pre>
 *
 * This loayout has a one-to-one relation with the aol-type version of the nar.
 *
 * @author Mark Donszelmann (Mark.Donszelmann@gmail.com)
 */
public class NarLayout21 extends AbstractNarLayout {
  private final NarFileLayout fileLayout;

  public NarLayout21(final Log log) {
    super(log);
    this.fileLayout = new NarFileLayout10();
  }

  @Override public final void attachNars(final File baseDir, final ArchiverManager archiverManager, final MavenProjectHelper projectHelper, final MavenProject project) throws MojoExecutionException {
    if (getNoArchDirectory(baseDir, project.getArtifactId(), project.getVersion()).exists()) {
      attachNar(archiverManager, projectHelper, project, NarConstants.NAR_NO_ARCH, getNoArchDirectory(baseDir, project.getArtifactId(), project.getVersion()), "*/**");
    }
    final String[] subDirs = baseDir.list();
    for (int i = 0; subDirs != null && i < subDirs.length; i++) {
      final String artifactIdVersion = project.getArtifactId() + "-" + project.getVersion();
      if (!subDirs[i].startsWith(artifactIdVersion)) {
        continue;
      }
      final String classifier = subDirs[i].substring(artifactIdVersion.length() + 1);
      if (classifier.equals(NarConstants.NAR_NO_ARCH)) {
        continue;
      }
      final File dir = new File(baseDir, subDirs[i]);
      attachNar(archiverManager, projectHelper, project, classifier, dir, "*/**");
    }
  }

  private File getAolDirectory(final File baseDir, final String artifactId, final String version, final String aol, final String type) {
    return new File(baseDir, artifactId + "-" + version + "-" + aol + "-" + type);
  }

  @Override public final File getBinDirectory(final File baseDir, final String artifactId, final String version, final String aol) {
    File dir = getAolDirectory(baseDir, artifactId, version, aol, Library.EXECUTABLE);
    dir = new File(dir, this.fileLayout.getBinDirectory(aol));
    return dir;
  }

  @Override public final File getIncludeDirectory(final File baseDir, final String artifactId, final String version) {
    return new File(getNoArchDirectory(baseDir, artifactId, version), this.fileLayout.getIncludeDirectory());
  }

  @Override public final File getLibDirectory(final File baseDir, final String artifactId, final String version, final String aol, final String type) throws MojoExecutionException {
    if (type.equals(Library.EXECUTABLE)) {
      throw new MojoExecutionException("NAR: for type EXECUTABLE call getBinDirectory instead of getLibDirectory");
    }
    File dir = getAolDirectory(baseDir, artifactId, version, aol, type);
    dir = new File(dir, this.fileLayout.getLibDirectory(aol, type));
    return dir;
  }

  @Override public File getNarUnpackDirectory(final File baseUnpackDirectory, final File narFile) {
    final File dir = new File(baseUnpackDirectory, FileUtils.basename(narFile.getPath(), "." + NarConstants.NAR_EXTENSION));
    return dir;
  }

  @Override public File getNoArchDirectory(final File baseDir, final String artifactId, final String version) {
    return new File(baseDir, artifactId + "-" + version + "-" + NarConstants.NAR_NO_ARCH);
  }

  @Override public final void prepareNarInfo(final File baseDir, final MavenProject project, final NarInfo narInfo, final AbstractNarMojo mojo) throws MojoExecutionException {
    if (getNoArchDirectory(baseDir, project.getArtifactId(), project.getVersion()).exists()) {
      narInfo.setNar(null, NarConstants.NAR_NO_ARCH, project.getGroupId() + ":" + project.getArtifactId() + ":" + NarConstants.NAR_TYPE + ":" + NarConstants.NAR_NO_ARCH);
    }
    final String artifactIdVersion = project.getArtifactId() + "-" + project.getVersion();
    final String[] subDirs = baseDir.list();
    final ArrayList<String> classifiers = new ArrayList<String>();
    for (int i = 0; subDirs != null && i < subDirs.length; i++) {
      if (!subDirs[i].startsWith(artifactIdVersion)) {
        continue;
      }
      final String classifier = subDirs[i].substring(artifactIdVersion.length() + 1);
      if (classifier.equals(NarConstants.NAR_NO_ARCH)) {
        continue;
      }
      classifiers.add(classifier);
    }
    if (!classifiers.isEmpty()) {
      for (final String classifier : classifiers) {
        final int lastDash = classifier.lastIndexOf('-');
        final String type = classifier.substring(lastDash + 1);
        final AOL aol = new AOL(classifier.substring(0, lastDash));
        if (narInfo.getOutput(aol, null) == null) {
          narInfo.setOutput(aol, mojo.getOutput(!Library.EXECUTABLE.equals(type)));
        }
        if (mojo.getLibsName() != null) {
          narInfo.setLibs(aol, mojo.getLibsName());
        }
        if (type.equals(Library.SHARED)) {
          narInfo.setBinding(aol, type);
          narInfo.setBinding(null, type);
        } else {
          if (narInfo.getBinding(aol, null) == null) {
            narInfo.setBinding(aol, type);
          }
          if (narInfo.getBinding(null, null) == null) {
            narInfo.setBinding(null, type);
          }
        }
        narInfo.setNar(null, type, project.getGroupId() + ":" + project.getArtifactId() + ":" + NarConstants.NAR_TYPE + ":" + "${aol}" + "-" + type);
      }
      if (mojo != null && narInfo.getOutput(null, null) == null) {
        narInfo.setOutput(null, mojo.getOutput(true));
      }
    }
  }

  @Override public void unpackNar(final File unpackDirectory, final ArchiverManager archiverManager, final File file, final String os, final String linkerName, final AOL defaultAOL) throws MojoExecutionException, MojoFailureException {
    final File dir = getNarUnpackDirectory(unpackDirectory, file);
    boolean process = false;
    if (!unpackDirectory.exists()) {
      unpackDirectory.mkdirs();
      process = true;
    } else {
      if (!dir.exists()) {
        process = true;
      } else {
        if (file.lastModified() > dir.lastModified()) {
          NarUtil.deleteDirectory(dir);
          process = true;
        } else {
          if (dir.list().length == 0) {
            process = true;
          }
        }
      }
    }
    if (process) {
      unpackNarAndProcess(archiverManager, file, dir, os, linkerName, defaultAOL);
    }
  }
}