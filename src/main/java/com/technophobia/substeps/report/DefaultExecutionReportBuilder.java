package com.technophobia.substeps.report;
import com.technophobia.substeps.execution.ExecutionNode;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.file.FileURLConnection;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author ian
 */
public class DefaultExecutionReportBuilder implements ExecutionReportBuilder {
  private final Logger log = LoggerFactory.getLogger(DefaultExecutionReportBuilder.class);

  private final Properties velocityProperties = new Properties();

  /**
     * @parameter default-value = ${project.build.directory}
     */
  private File outputDirectory;

  public DefaultExecutionReportBuilder() {
    velocityProperties.setProperty("resource.loader", "class");
    velocityProperties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
  }

  public DefaultExecutionReportBuilder(File outputDirectory) {
    this();
    this.outputDirectory = outputDirectory;
  }

  public void buildReport(final ReportData data) {
    log.debug("Build report in: " + outputDirectory.getAbsolutePath());
    final File reportDir = new File(outputDirectory + File.separator + "feature_report");
    try {
      log.debug("trying to create: " + reportDir.getAbsolutePath());
      if (reportDir.exists()) {
        FileUtils.deleteDirectory(reportDir);
      }
      Assert.assertTrue("failed to create directory: " + reportDir, reportDir.mkdir());
      copyStaticResources(reportDir);
      buildMainReport(data, reportDir);
      buildDetailReports(data, reportDir);
      buildTreeJSON(data, reportDir);
    } catch (final IOException ex) {
      log.error("IOException: ", ex);
    } catch (final URISyntaxException ex) {
      log.error("URISyntaxException: ", ex);
    }
  }

  private void buildTreeJSON(final ReportData reportData, final File reportDir) throws IOException {
    log.debug("Building tree json file.");
    File jsonFile = new File(reportDir, "tree.json");
    Writer writer = new BufferedWriter(new FileWriter(jsonFile));
    List<ExecutionNode> nodeList = reportData.getRootNodes();
    try {
      if (!nodeList.isEmpty()) {
        ExecutionNode rootNode = nodeList.get(0);
        buildNodeJSON(rootNode, writer);
      }
    }  finally {
      writer.close();
    }
  }

  private void buildNodeJSON(final ExecutionNode node, Writer writer) throws IOException {
    writer.append("{ ");
    writer.append("\"data\" : { ");
    writer.append("\"title\" : \"");
    writer.append(getDescriptionForNode(node));
    writer.append("\"");
    writer.append(", \"attr\" : { \"id\" : \"");
    writer.append(Long.toString(node.getId()));
    writer.append("\" }");
    writer.append(", \"icon\" : \"");
    writer.append(getNodeImage(node));
    writer.append("\"");
    writer.append("}");
    if (node.hasChildren()) {
      if (node.hasError()) {
        writer.append(", \"state\" : \"open\"");
      }
      writer.append(", \"children\" : [");
      boolean first = true;
      for (ExecutionNode child : node.getChildren()) {
        if (!first) {
          writer.append(", ");
        }
        buildNodeJSON(child, writer);
        first = false;
      }
      writer.append("]");
    }
    writer.append("}");
  }

  private void buildDetailReports(ReportData reporData, File reportDir) throws IOException {
    log.debug("Building detail report partials.");
    for (ExecutionNode node : reporData.getRootNodes()) {
      buildDetailReport(node, reportDir);
    }
  }

  /**
     * @param reportDir
     * @throws IOException
     */
  private void copyStaticResources(final File reportDir) throws URISyntaxException, IOException {
    log.debug("Copying static resources to: " + reportDir.getAbsolutePath());
    URL staticURL = getClass().getResource("/static");
    if (staticURL == null) {
      throw new IllegalStateException("Failed to copy static resources for report.  URL for resources is null.");
    }
    copyResourcesRecursively(staticURL, reportDir);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * @param reportDir
	 * @throws IOException
	 */
  private void copyStaticResource(final File reportDir, final String resource, final String subfolder) throws IOException {
    log.debug("copyStaticResource: reportDir: " + reportDir.getAbsolutePath() + " resource: " + resource + " subfolder: " + subfolder);
    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/" + subfolder + resource);
    if (resourceAsStream == null) {
      resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("static/" + subfolder + resource);
    }
    final File newOutput = new File(reportDir, resource);
    Assert.assertTrue("failed to create new file", newOutput.createNewFile());
    Files.copy(new FileInputSupplier(resourceAsStream), newOutput);
  }
>>>>>>> /usr/src/app/output/technophobia/substeps-core/005a00496d02f32b568224e01e2a2fed19f9a4bc/src/main/java/com/technophobia/substeps/report/DefaultExecutionReportBuilder.java/right.java


  /**
     * @param node
     * @param reportDir
     * @throws IOException
     */
  private void buildDetailReport(final ExecutionNode node, final File reportDir) throws IOException {
    final VelocityContext vCtx = new VelocityContext();
    vCtx.put("node", node);
    final String vml = "detail.vm";
    renderAndWriteToFile(reportDir, vCtx, vml, node.getId() + "-details.html");
    if (node.hasChildren()) {
      for (ExecutionNode child : node.getChildren()) {
        buildDetailReport(child, reportDir);
      }
    }
  }

  private String getNodeImage(final ExecutionNode node) {
    return "img/" + node.getResult().getResult() + ".png";
  }

  private String getDescriptionForNode(final ExecutionNode node) {
    final StringBuilder buf = new StringBuilder();
    if (node.getParent() == null) {
      if (node.getLine() != null) {
        buf.append(node.getLine());
      } else {
        buf.append("executionNodeRoot");
      }
    } else {
      buildDescriptionString(null, node, buf);
    }
    return StringEscapeUtils.escapeHtml(buf.toString());
  }

  public static void buildDescriptionString(final String prefix, final ExecutionNode node, final StringBuilder buf) {
    if (prefix != null) {
      buf.append(prefix);
    }
    if (node.getFeature() != null) {
      buf.append(node.getFeature().getName());
    } else {
      if (node.getScenarioName() != null) {
        if (node.isOutlineScenario()) {
          buf.append("Scenario #: ");
        } else {
          buf.append("Scenario: ");
        }
        buf.append(node.getScenarioName());
      }
    }
    if (node.getParent() != null && node.getParent().isOutlineScenario()) {
      buf.append(node.getRowNumber()).append(" ").append(node.getParent().getScenarioName()).append(":");
    }
    if (node.getLine() != null) {
      buf.append(node.getLine());
    }
  }

  private void buildMainReport(final ReportData data, final File reportDir) throws IOException {
    log.debug("Building main report file.");
    final VelocityContext vCtx = new VelocityContext();
    final String vml = "report_frame.vm";
    final ExecutionStats stats = new ExecutionStats();
    stats.buildStats(data);
    vCtx.put("stats", stats);
    renderAndWriteToFile(reportDir, vCtx, vml, "report_frame.html");
  }

  /**
     * @param reportDir
     * @param vCtx
     * @param vm
     * @param targetFilename
     * @throws IOException
     */
  private void renderAndWriteToFile(final File reportDir, final VelocityContext vCtx, final String vm, final String targetFilename) throws IOException {
    Writer writer = new BufferedWriter(new FileWriter(new File(reportDir, targetFilename)));
    final VelocityEngine velocityEngine = new VelocityEngine();
    try {
      velocityEngine.init(velocityProperties);
      velocityEngine.getTemplate("templates/" + vm).merge(vCtx, writer);
    } catch (final ResourceNotFoundException e) {
      throw new RuntimeException(e);
    } catch (final ParseErrorException e) {
      throw new RuntimeException(e);
    } catch (final MethodInvocationException e) {
      throw new RuntimeException(e);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (final IOException e) {
        log.error("IOException: ", e);
      }
    }
  }

  public void copyResourcesRecursively(URL originUrl, File destination) throws IOException {
    URLConnection urlConnection = originUrl.openConnection();
    if (urlConnection instanceof JarURLConnection) {
      copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
    } else {
      if (urlConnection instanceof FileURLConnection) {
        FileUtils.copyDirectory(new File(originUrl.getPath()), destination);
      } else {
        throw new RuntimeException("URLConnection[" + urlConnection.getClass().getSimpleName() + "] is not a recognized/implemented connection type.");
      }
    }
  }

  public void copyJarResourcesRecursively(File destination, JarURLConnection jarConnection) throws IOException {
    JarFile jarFile = jarConnection.getJarFile();
    for (JarEntry entry : Collections.list(jarFile.entries())) {
      if (entry.getName().startsWith(jarConnection.getEntryName())) {
        String fileName = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
        if (!entry.isDirectory()) {
          InputStream entryInputStream = null;
          try {
            entryInputStream = jarFile.getInputStream(entry);
            FileUtils.copyInputStreamToFile(entryInputStream, new File(destination, fileName));
          }  finally {
            IOUtils.closeQuietly(entryInputStream);
          }
        } else {
          new File(destination, fileName).mkdirs();
        }
      }
    }
  }
}