package hudson.tasks.junit;
import hudson.tasks.test.TestObject;
import hudson.util.io.ParserConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Result of one test suite.
 *
 * <p>
 * The notion of "test suite" is rather arbitrary in JUnit ant task.
 * It's basically one invocation of junit.
 *
 * <p>
 * This object is really only used as a part of the persisted
 * object tree.
 *
 * @author Kohsuke Kawaguchi
 */
@ExportedBean public final class SuiteResult implements Serializable {
  private final String file;

  private final String name;

  private final String stdout;

  private final String stderr;

  private float duration;

  /**
     * The 'timestamp' attribute of  the test suite.
     * AFAICT, this is not a required attribute in XML, so the value may be null.
     */
  private String timestamp;

  /** Optional ID attribute of a test suite. E.g., Eclipse plug-ins tests always have the name 'tests' but a different id. **/
  private String id;

  /** Optional time attribute of a test suite. E.g., Suites can use their own time attribute or the sum of their cases' times as before.**/
  private String time;

  /**
     * All test cases.
     */
  private final List<CaseResult> cases = new ArrayList<CaseResult>();

  private transient Map<String, CaseResult> casesByName;

  private transient hudson.tasks.junit.TestResult parent;

  SuiteResult(String name, String stdout, String stderr) {
    this.name = name;
    this.stderr = stderr;
    this.stdout = stdout;
    this.file = null;
  }

  private synchronized Map<String, CaseResult> casesByName() {
    if (casesByName == null) {
      casesByName = new HashMap<String, CaseResult>();
      for (CaseResult c : cases) {
        casesByName.put(c.getName(), c);
      }
    }
    return casesByName;
  }

  public static class SuiteResultParserConfigurationContext {
    public final File xmlReport;

    SuiteResultParserConfigurationContext(File xmlReport) {
      this.xmlReport = xmlReport;
    }
  }

  /**
     * Parses the JUnit XML file into {@link SuiteResult}s.
     * This method returns a collection, as a single XML may have multiple &lt;testsuite>
     * elements wrapped into the top-level &lt;testsuites>.
     */
  static List<SuiteResult> parse(File xmlReport, boolean keepLongStdio) throws DocumentException, IOException, InterruptedException {
    List<SuiteResult> r = new ArrayList<SuiteResult>();
    SAXReader saxReader = new SAXReader();
    saxReader.setEntityResolver(new XMLEntityResolver());
    FileInputStream xmlReportStream = null;
    try {
      xmlReportStream = new FileInputStream(xmlReport);
      Document result = saxReader.read(xmlReportStream);
      Element root = result.getRootElement();
      parseSuite(xmlReport, keepLongStdio, r, root);
    }  finally {
      if (xmlReportStream != null) {
        xmlReportStream.close();
      }
    }
    return r;
  }

  private static void parseSuite(File xmlReport, boolean keepLongStdio, List<SuiteResult> r, Element root) throws DocumentException, IOException {
    @SuppressWarnings(value = { "unchecked" }) List<Element> testSuites = (List<Element>) root.elements("testsuite");
    for (Element suite : testSuites) {
      parseSuite(xmlReport, keepLongStdio, r, suite);
    }
    if (root.element("testcase") != null || root.element("error") != null) {
      r.add(new SuiteResult(xmlReport, root, keepLongStdio));
    }
  }

  /**
     * @param xmlReport
     *      A JUnit XML report file whose top level element is 'testsuite'.
     * @param suite
     *      The parsed result of {@code xmlReport}
     */
  private SuiteResult(File xmlReport, Element suite, boolean keepLongStdio) throws DocumentException, IOException {
    this.file = xmlReport.getAbsolutePath();
    String name = suite.attributeValue("name");
    if (name == null) {
      name = '(' + xmlReport.getName() + ')';
    } else {
      String pkg = suite.attributeValue("package");
      if (pkg != null && pkg.length() > 0) {
        name = pkg + '.' + name;
      }
    }
    this.name = TestObject.safe(name);
    this.timestamp = suite.attributeValue("timestamp");
    this.id = suite.attributeValue("id");
    if ((this.time = suite.attributeValue("time")) != null) {
      duration = new TimeToFloat(this.time).parse();
    }
    Element ex = suite.element("error");
    if (ex != null) {
      addCase(new CaseResult(this, suite, "<init>", keepLongStdio));
    }
    @SuppressWarnings(value = { "unchecked" }) List<Element> testCases = (List<Element>) suite.elements("testcase");
    for (Element e : testCases) {
      String classname = e.attributeValue("classname");
      if (classname == null) {
        classname = suite.attributeValue("name");
      }
      addCase(new CaseResult(this, e, classname, keepLongStdio));
    }
    String stdout = CaseResult.possiblyTrimStdio(cases, keepLongStdio, suite.elementText("system-out"));
    String stderr = CaseResult.possiblyTrimStdio(cases, keepLongStdio, suite.elementText("system-err"));
    if (stdout == null && stderr == null) {
      Matcher m = SUREFIRE_FILENAME.matcher(xmlReport.getName());
      if (m.matches()) {
        File mavenOutputFile = new File(xmlReport.getParentFile(), m.group(1) + "-output.txt");
        if (mavenOutputFile.exists()) {
          try {
            stdout = CaseResult.possiblyTrimStdio(cases, keepLongStdio, mavenOutputFile);
          } catch (IOException e) {
            throw new IOException("Failed to read " + mavenOutputFile, e);
          }
        }
      }
    }
    this.stdout = stdout;
    this.stderr = stderr;
  }

  void addCase(CaseResult cr) {
    cases.add(cr);
    casesByName().put(cr.getName(), cr);
    if (this.time == null) {
      duration += cr.getDuration();
    }
  }

  @Exported(visibility = 9) public String getName() {
    return name;
  }

  @Exported(visibility = 9) public float getDuration() {
    return duration;
  }

  /**
     * The stdout of this test.
     *
     * @return the stdout of this test.
     * @since 1.281
     * @see CaseResult#getStdout()
     */
  @Exported public String getStdout() {
    return stdout;
  }

  /**
     * The stderr of this test.
     *
     * @return the stderr of this test.
     * @since 1.281
     * @see CaseResult#getStderr()
     */
  @Exported public String getStderr() {
    return stderr;
  }

  /**
     * The absolute path to the original test report. OS-dependent.
     *
     * @return the sabsolute path to the original test report.
     */
  public String getFile() {
    return file;
  }

  public hudson.tasks.junit.TestResult getParent() {
    return parent;
  }

  @Exported(visibility = 9) public String getTimestamp() {
    return timestamp;
  }

  @Exported(visibility = 9) public String getId() {
    return id;
  }

  @Exported(inline = true, visibility = 9) public List<CaseResult> getCases() {
    return cases;
  }

  public SuiteResult getPreviousResult() {
    hudson.tasks.test.TestResult pr = parent.getPreviousResult();
    if (pr == null) {
      return null;
    }
    if (pr instanceof hudson.tasks.junit.TestResult) {
      return ((hudson.tasks.junit.TestResult) pr).getSuite(name);
    }
    return null;
  }

  /**
     * Returns the {@link CaseResult} whose {@link CaseResult#getName()}
     * is the same as the given string.
     * <p>
     * Note that test name needs not be unique.
     * </p>
     *
     * @param name The case name.
     *
     * @return the {@link CaseResult} with the provided name.
     */
  public CaseResult getCase(String name) {
    return casesByName().get(name);
  }

  public Set<String> getClassNames() {
    Set<String> result = new HashSet<String>();
    for (CaseResult c : cases) {
      result.add(c.getClassName());
    }
    return result;
  }

  /** KLUGE. We have to call this to prevent freeze()
     * from calling c.freeze() on all its children,
     * because that in turn calls c.getOwner(),
     * which requires a non-null parent.
     * @param parent
     */
  void setParent(hudson.tasks.junit.TestResult parent) {
    this.parent = parent;
  }

  boolean freeze(hudson.tasks.junit.TestResult owner) {
    if (this.parent != null) {
      return false;
    }
    this.parent = owner;
    for (CaseResult c : cases) {
      c.freeze(this);
    }
    return true;
  }

  private static final long serialVersionUID = 1L;

  private static final Pattern SUREFIRE_FILENAME = Pattern.compile("TEST-(.+)\\.xml");
}