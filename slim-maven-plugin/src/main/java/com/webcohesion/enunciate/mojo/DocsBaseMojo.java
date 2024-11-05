package com.webcohesion.enunciate.mojo;
import com.webcohesion.enunciate.Enunciate;
import com.webcohesion.enunciate.module.DocumentationProviderModule;
import com.webcohesion.enunciate.module.EnunciateModule;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

@Mojo(name = "docs", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true) public class DocsBaseMojo extends ConfigMojo implements MavenReport {
  @Parameter(defaultValue = "${project.reporting.outputDirectory}", property = "enunciate.docsDir", required = true) protected String docsDir;

  @Parameter protected String docsSubdir = "apidocs";

  @Parameter(defaultValue = "${project.build.directory}/enunciate-docs-staging", required = true) protected String docsStagingDir;

  @Parameter protected String indexPageName;

  @Parameter(defaultValue = "Web Service API") protected String reportName;

  @Parameter(defaultValue = "Web Service API Documentation") protected String reportDescription;

  private Exception siteError = null;

  @Override protected void applyAdditionalConfiguration(EnunciateModule module) {
    super.applyAdditionalConfiguration(module);
    if (module instanceof DocumentationProviderModule) {
      DocumentationProviderModule docsProvider = (DocumentationProviderModule) module;
      docsProvider.setDefaultDocsDir(new File(this.docsStagingDir));
      if (this.docsSubdir != null) {
        docsProvider.setDefaultDocsSubdir(this.docsSubdir);
      }
    }
  }

  @Override public void execute() throws MojoExecutionException, MojoFailureException {
    this.docsStagingDir = docsDir;
    super.execute();
  }

  public void generate(org.codehaus.doxia.sink.Sink sink, Locale locale) throws MavenReportException {
    generate(locale);
  }

  public void generate(Sink sink, Locale locale) throws MavenReportException {
    generate(locale);
  }

  void generate(Locale locale) throws MavenReportException {
    if (this.siteError != null) {
      throw new MavenReportException("Unable to generate Enunciate documentation.", this.siteError);
    }
    new File(getReportOutputDirectory(), this.indexPageName == null ? "index.html" : this.indexPageName).delete();
    Enunciate enunciate = (Enunciate) getPluginContext().get(ConfigMojo.ENUNCIATE_PROPERTY);
    try {
      enunciate.copyDir(getReportStagingDirectory(), getReportOutputDirectory());
    } catch (IOException e) {
      throw new MavenReportException("Unable to copy Enunciate documentation from the staging area to the report directory.", e);
    }
  }

  public String getOutputName() {
    String indexName = "index";
    if (this.indexPageName != null) {
      if (this.indexPageName.indexOf('.') > 0) {
        indexName = this.indexPageName.substring(0, this.indexPageName.indexOf('.'));
      } else {
        indexName = this.indexPageName;
      }
    }
    return this.docsSubdir == null ? indexName : (this.docsSubdir + "/" + indexName);
  }

  public String getName(Locale locale) {
    return this.reportName;
  }

  public String getCategoryName() {
    return CATEGORY_PROJECT_REPORTS;
  }

  public String getDescription(Locale locale) {
    return this.reportDescription;
  }

  public void setReportOutputDirectory(File outputDirectory) {
    this.docsDir = outputDirectory.getAbsolutePath();
  }

  public File getReportOutputDirectory() {
    File outputDir = new File(this.docsDir);
    if (this.docsSubdir != null) {
      outputDir = new File(outputDir, this.docsSubdir);
    }
    return outputDir;
  }

  public File getReportStagingDirectory() {
    File outputDir = new File(this.docsStagingDir);
    if (this.docsSubdir != null) {
      outputDir = new File(outputDir, this.docsSubdir);
    }
    return outputDir;
  }

  public boolean isExternalReport() {
    return true;
  }

  public boolean canGenerateReport() {
    if (this.skipEnunciate) {
      return false;
    }
    ClassLoader old = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
      super.execute();
    } catch (Exception e) {
      this.siteError = e;
      return false;
    } finally {
      Thread.currentThread().setContextClassLoader(old);
    }
    return new File(getReportStagingDirectory(), this.indexPageName == null ? "index.html" : this.indexPageName).exists();
  }

  public void generate(org.apache.maven.doxia.sink.Sink sink, java.util.Locale locale) throws MavenReportException {
    generate();
  }

  private void generate() throws MavenReportException {
    if (this.siteError != null) {
      throw new MavenReportException("Unable to generate Enunciate documentation.", this.siteError);
    }
    new File(getReportOutputDirectory(), this.indexPageName == null ? "index.html" : this.indexPageName).delete();
    Enunciate enunciate = (Enunciate) getPluginContext().get(ConfigMojo.ENUNCIATE_PROPERTY);
    try {
      enunciate.copyDir(getReportStagingDirectory(), getReportOutputDirectory());
    } catch (IOException e) {
      throw new MavenReportException("Unable to copy Enunciate documentation from the staging area to the report directory.", e);
    }
  }
}