package org.gephi.branding.desktop.reporter;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.MissingResourceException;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInfo;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;

/**
 * @author Mathieu Bastian
 */
public class ReportController {
  private static final String POST_URL = "https://d007fbbdeb6241b5b2c542a6bc548cf3@o43889.ingest.sentry.io/85815";

  public ReportController() {
    Sentry.init((options) -> {
      options.setDsn(POST_URL);
    });
  }

  public void sendReport(final Report report) {
    Thread thread = new Thread(new Runnable() {
      @Override public void run() {
        ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(ReportController.class, "ReportController.status.sending"));
        try {
          handle.start();
          sendSentryReport(report);
          handle.finish();
          DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ReportController.class, "ReportController.status.sent"), NotifyDescriptor.INFORMATION_MESSAGE));
          return;
        } catch (Exception e) {
          Exceptions.printStackTrace(e);
        }
        handle.finish();
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ReportController.class, "ReportController.status.failed"), NotifyDescriptor.WARNING_MESSAGE));
      }
    }, "Exception Reporter");
    thread.start();
  }

  private void sendSentryReport(Report report) {
    SentryEvent event = new SentryEvent();
    event.setLevel(SentryLevel.ERROR);
    event.setRelease(report.getVersion());
    event.setServerName("Gephi Desktop");
    event.setExtra("OS", report.getOs());
    event.setExtra("Heap memory usage", report.getHeapMemoryUsage());
    event.setExtra("Non heap memory usage", report.getNonHeapMemoryUsage());
    event.setExtra("Processors", report.getNumberOfProcessors());
    event.setExtra("Screen devices", report.getScreenDevices());
    event.setExtra("Screen size", report.getScreenSize());
    event.setExtra("VM", report.getVm());
    event.setExtra("OpenGL Vendor", report.getGlVendor());
    event.setExtra("OpenGL Renderer", report.getGlRenderer());
    event.setExtra("OpenGL Version", report.getGlVersion());
    event.setExtra("User email", report.getUserEmail());
    event.setExtra("Log", anonymizeLog(report.getLog()));
    event.setThrowable(report.getThrowable());
    Sentry.captureEvent(event);
  }

  public Document buildReportDocument(Report report) {
    logMessageLog(report);
    logVersion(report);
    logScreenSize(report);
    logCPU(report);
    logMemoryInfo(report);
    logJavaInfo(report);
    logGLInfo(report);
    return buildXMLDocument(report);
  }

  private Document buildXMLDocument(Report report) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.newDocument();
      document.setXmlVersion("1.0");
      document.setXmlStandalone(true);
      report.writeXml(document);
      return document;
    } catch (Exception e) {
      Exceptions.printStackTrace(e);
    }
    return null;
  }

  private void logScreenSize(Report report) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    report.setScreenSize(screenSize);
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    report.setScreenDevices(ge.getScreenDevices().length);
  }

  private void logCPU(Report report) {
    OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
    report.setNumberOfProcessors(bean.getAvailableProcessors());
    String unknown = "unknown";
    String str = System.getProperty("os.name", unknown) + ", " + System.getProperty("os.version", unknown) + ", " + System.getProperty("os.arch", unknown);
    report.setOs(str);
  }

  private void logMemoryInfo(Report report) {
    MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
    report.setHeapMemoryUsage(bean.getHeapMemoryUsage().toString());
    report.setNonHeapMemoryUsage(bean.getNonHeapMemoryUsage().toString());
  }

  private void logJavaInfo(Report report) {
    String str = System.getProperty("java.vm.name", "unknown") + ", " + System.getProperty("java.vm.version", "") + ", " + System.getProperty("java.runtime.name", "unknown") + ", " + System.getProperty("java.runtime.version", "");
    report.setVm(str);
  }

  private void logVersion(Report report) {
    String str = "";
    try {
      str = MessageFormat.format(NbBundle.getBundle("org.netbeans.core.startup.Bundle").getString("currentVersion"), System.getProperty("netbeans.buildnumber"));
      report.setVersion(str);
    } catch (MissingResourceException ex) {
    }
  }

  private void logGLInfo(Report report) {
    String output = report.getLog();
    try {
      LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(output));
      String line;
      while ((line = lineNumberReader.readLine()) != null) {
        if (line.contains("GL_VENDOR:")) {
          report.setGlVendor(line.replaceFirst(".*GL_VENDOR:", ""));
        } else {
          if (line.contains("GL_RENDERER:")) {
            report.setGlRenderer(line.replaceFirst(".*GL_RENDERER:", ""));
          } else {
            if (line.contains("GL_VERSION:")) {
              report.setGlVersion(line.replaceFirst(".*GL_VERSION:", ""));
              break;
            }
          }
        }
      }
      lineNumberReader.close();
    } catch (Exception e) {
    }
  }

  private void logModules(Report report) {
    for (ModuleInfo m : Lookup.getDefault().lookupAll(ModuleInfo.class)) {
      String moduleStr = "";
      SpecificationVersion specVersion = m.getSpecificationVersion();
      if (specVersion != null) {
        moduleStr = m.getCodeName() + " [" + specVersion + "]";
      } else {
        moduleStr = m.getCodeName();
      }
      if (m.isEnabled()) {
        report.addEnabledModule(moduleStr);
      } else {
        report.addDisabledModule(moduleStr);
      }
    }
  }

  private void logMessageLog(Report report) {
    System.out.flush();
    String ud = System.getProperty("netbeans.user");
    if (ud == null || "memory".equals(ud)) {
      return;
    }
    Handler[] handlers = Logger.getLogger("").getHandlers();
    handlers[0].flush();
    File userDir = new File(ud);
    File directory = new File(new File(userDir, "var"), "log");
    File messagesLog = new File(directory, "messages.log");
    String log = "";
    try {
      byte[] buffer = new byte[(int) messagesLog.length()];
      BufferedInputStream f = new BufferedInputStream(new FileInputStream(messagesLog));
      f.read(buffer);
      log = new String(buffer);
    } catch (Exception e) {
    }
    report.setLog(log);
  }

  /**
     * Removes usernames from log files
     */
  protected static String anonymizeLog(String log) {
    return log.replaceAll("(/((home)|(Users))/[^/\n]*)|(\\\\Users\\\\[^\\\\\n]*)", "/ANONYMIZED_HOME_DIR");
  }
}