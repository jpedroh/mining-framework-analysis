package hudson.plugins.logparser;
import hudson.FilePath;
import hudson.console.ConsoleNote;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LogParserParser {
  final private HashMap<String, Integer> statusCount = new HashMap<>();

  final private HashMap<String, BufferedWriter> writers = new HashMap<>();

  final private HashMap<String, String> linkFiles = new HashMap<>();

  final private String[] parsingRulesArray;

  final private Pattern[] compiledPatterns;

  final private CompiledPatterns compiledPatternsPlusError;

  final private List<String> extraTags;

  final private HashMap<String, Integer> statusCountPerSection = new HashMap<>();

  final private ArrayList<String> headerForSection = new ArrayList<>();

  private int sectionCounter = 0;

  final private LogParserDisplayConsts displayConstants = new LogParserDisplayConsts();

  final private VirtualChannel channel;

  final private boolean preformattedHtml;

  public LogParserParser(final FilePath parsingRulesFile, final boolean preformattedHtml, final VirtualChannel channel) throws IOException {
    final Logger logger = Logger.getLogger(getClass().getName());
    this.parsingRulesArray = LogParserUtils.readParsingRules(parsingRulesFile);
    this.compiledPatternsPlusError = LogParserUtils.compilePatterns(this.parsingRulesArray, logger);
    this.compiledPatterns = this.compiledPatternsPlusError.getCompiledPatterns();
    this.extraTags = this.compiledPatternsPlusError.getExtraTags();
    this.preformattedHtml = preformattedHtml;
    this.channel = channel;
    statusCount.put(LogParserConsts.ERROR, 0);
    statusCount.put(LogParserConsts.WARNING, 0);
    statusCount.put(LogParserConsts.INFO, 0);
    statusCount.put(LogParserConsts.DEBUG, 0);
    for (String extraTag : this.extraTags) {
      statusCount.put(extraTag, 0);
    }
  }

  @Deprecated public LogParserResult parseLog(final AbstractBuild<?, ?> build) throws IOException, InterruptedException {
    return this.parseLog((Run<?, ?>) build);
  }

  public LogParserResult parseLog(final Run<?, ?> build) throws IOException, InterruptedException {
    final Logger logger = Logger.getLogger(getClass().getName());
    final InputStream log = build.getLogInputStream();
    final String logDirectory = build.getRootDir().getAbsolutePath();
    final String parsedFilePath = logDirectory + "/log_content.html";
    final String errorLinksFilePath = logDirectory + "/logerrorLinks.html";
    final String warningLinksFilePath = logDirectory + "/logwarningLinks.html";
    final String infoLinksFilePath = logDirectory + "/loginfoLinks.html";
    final String debugLinksFilePath = logDirectory + "/logdebugLinks.html";
    final Map<String, String> linksFilePathByExtraTags = new HashMap<>();
    for (String extraTag : this.extraTags) {
      linksFilePathByExtraTags.put(extraTag, logDirectory + "/log" + extraTag + "Links.html");
    }
    final String buildRefPath = logDirectory + "/log_ref.html";
    final String buildWrapperPath = logDirectory + "/log.html";
    linkFiles.put(LogParserConsts.ERROR, errorLinksFilePath);
    linkFiles.put(LogParserConsts.WARNING, warningLinksFilePath);
    linkFiles.put(LogParserConsts.INFO, infoLinksFilePath);
    linkFiles.put(LogParserConsts.DEBUG, debugLinksFilePath);
    for (String extraTag : this.extraTags) {
      linkFiles.put(extraTag, linksFilePathByExtraTags.get(extraTag));
    }
    final BufferedWriter writer = new BufferedWriter(new FileWriter(parsedFilePath));
    writers.put(LogParserConsts.ERROR, new BufferedWriter(new FileWriter(errorLinksFilePath)));
    writers.put(LogParserConsts.WARNING, new BufferedWriter(new FileWriter(warningLinksFilePath)));
    writers.put(LogParserConsts.INFO, new BufferedWriter(new FileWriter(infoLinksFilePath)));
    writers.put(LogParserConsts.DEBUG, new BufferedWriter(new FileWriter(debugLinksFilePath)));
    for (String extraTag : this.extraTags) {
      writers.put(extraTag, new BufferedWriter(new FileWriter(linksFilePathByExtraTags.get(extraTag))));
    }
    final String shortLink = " <a target=\"content\" href=\"log_content.html\">Beginning of log</a>";
    LogParserWriter.writeHeaderTemplateToAllLinkFiles(writers, sectionCounter);
    headerForSection.add(shortLink);
    writer.write(LogParserConsts.getHtmlOpeningTags());
    final String styles = "<style>\n" + "  body {margin-left:.5em; }\n" + "  pre {font-family: Consolas, \"Courier New\"; word-wrap: break-word; }\n" + "  pre span {word-wrap: break-word; } \n" + "</style>\n";
    writer.write(styles);
    if (this.preformattedHtml) {
      writer.write("<pre>");
    }
    parseLogBody(build, writer, log, logger);
    if (this.preformattedHtml) {
      writer.write("</pre>");
    }
    writer.write(LogParserConsts.getHtmlClosingTags());
    writer.close();
    writers.get(LogParserConsts.ERROR).close();
    writers.get(LogParserConsts.WARNING).close();
    writers.get(LogParserConsts.INFO).close();
    writers.get(LogParserConsts.DEBUG).close();
    for (String extraTag : this.extraTags) {
      writers.get(extraTag).close();
    }
    LogParserWriter.writeReferenceHtml(buildRefPath, headerForSection, statusCountPerSection, displayConstants.getIconTable(), displayConstants.getLinkListDisplay(), displayConstants.getLinkListDisplayPlural(), statusCount, linkFiles, extraTags);
    LogParserWriter.writeWrapperHtml(buildWrapperPath);
    final String buildUrlPath = build.getUrl();
    final String buildActionPath = LogParserAction.getUrlNameStat();
    final String parsedLogURL = buildUrlPath + buildActionPath + "/log.html";
    final LogParserResult result = new LogParserResult();
    result.setHtmlLogFile(parsedFilePath);
    result.setTotalErrors(statusCount.get(LogParserConsts.ERROR));
    result.setTotalWarnings(statusCount.get(LogParserConsts.WARNING));
    result.setTotalInfos(statusCount.get(LogParserConsts.INFO));
    result.setTotalDebugs(statusCount.get(LogParserConsts.DEBUG));
    for (String extraTag : this.extraTags) {
      result.putTotalCountsByExtraTag(extraTag, statusCount.get(extraTag));
    }
    result.setErrorLinksFile(errorLinksFilePath);
    result.setWarningLinksFile(warningLinksFilePath);
    result.setInfoLinksFile(infoLinksFilePath);
    result.setDebugLinksFile(debugLinksFilePath);
    for (String extraTag : this.extraTags) {
      result.putLinksFileByExtraTag(extraTag, linksFilePathByExtraTags.get(extraTag));
    }
    result.setParsedLogURL(parsedLogURL);
    result.setHtmlLogPath(logDirectory);
    result.setBadParsingRulesError(this.compiledPatternsPlusError.getError());
    result.setExtraTags(this.extraTags);
    return result;
  }

  public String parseLine(final String line) throws IOException {
    return parseLine(line, null);
  }

  public String parseLine(final String line, final String status) throws IOException {
    String parsedLine = line;
    String effectiveStatus = status;
    if (status == null) {
      effectiveStatus = LogParserConsts.NONE;
    } else {
      if (status.equals(LogParserConsts.START)) {
        effectiveStatus = LogParserConsts.INFO;
      }
    }
    parsedLine = ConsoleNote.removeNotes(parsedLine);
    parsedLine = parsedLine.replaceAll("<", "&lt;");
    parsedLine = parsedLine.replaceAll(">", "&gt;");
    if (effectiveStatus != null && !effectiveStatus.equals(LogParserConsts.NONE)) {
      incrementCounter(effectiveStatus);
      incrementCounterPerSection(effectiveStatus, sectionCounter);
      final String parsedLineColored = colorLine(parsedLine, effectiveStatus);
      final String parsedLineColoredAndMarked = addMarkerAndLink(parsedLineColored, effectiveStatus, status);
      parsedLine = parsedLineColoredAndMarked;
    }
    final StringBuffer result = new StringBuffer(parsedLine);
    if (!preformattedHtml) {
      result.append("<br/>\n");
    }
    return result.toString();
  }

  public void incrementCounter(final String status) {
    final int currentVal = statusCount.get(status);
    statusCount.put(status, currentVal + 1);
  }

  public void incrementCounterPerSection(final String status, final int sectionNumber) {
    final String key = LogParserUtils.getSectionCountKey(status, sectionNumber);
    Integer currentValInteger = statusCountPerSection.get(key);
    if (currentValInteger == null) {
      currentValInteger = 0;
    }
    final int newVal = currentValInteger + 1;
    statusCountPerSection.put(key, newVal);
  }

  private String colorLine(final String line, final String status) {
    String color = displayConstants.getColorTable().get(status);
    if (color == null) {
      color = LogParserDisplayConsts.DEFAULT_COLOR;
    }
    final StringBuffer result = new StringBuffer("<span class=\"");
    result.append(status.toLowerCase());
    result.append("\" style=\"color: ");
    result.append(color);
    result.append("\">");
    result.append(line);
    result.append("</span>");
    return result.toString();
  }

  private String addMarkerAndLink(final String line, final String effectiveStatus, final String status) throws IOException {
    final String statusCountStr = statusCount.get(effectiveStatus).toString();
    final String marker = effectiveStatus + statusCountStr;
    final StringBuffer shortLink = new StringBuffer(" <a target=\"content\" href=\"log_content.html#");
    shortLink.append(marker);
    shortLink.append("\">");
    shortLink.append(line);
    shortLink.append("</a>");
    final StringBuffer link = new StringBuffer("<li>");
    link.append(statusCountStr);
    link.append(shortLink);
    link.append("</li>");
    final BufferedWriter linkWriter = (BufferedWriter) writers.get(effectiveStatus);
    linkWriter.write(link.toString());
    linkWriter.newLine();
    final StringBuffer markedLine = new StringBuffer("<p><a name=\"");
    markedLine.append(marker);
    markedLine.append("\"></a></p>");
    markedLine.append(line);
    if (status.equals(LogParserConsts.START)) {
      sectionCounter++;
      LogParserWriter.writeHeaderTemplateToAllLinkFiles(writers, sectionCounter);
      final StringBuffer brShortLink = new StringBuffer("<br/>");
      brShortLink.append(shortLink);
      headerForSection.add(brShortLink.toString());
    }
    return markedLine.toString();
  }

  private void parseLogBody(final Run<?, ?> build, final BufferedWriter writer, final InputStream log, final Logger logger) throws IOException, InterruptedException {
    final String signature = build.getParent().getName() + "_build_" + build.getNumber();
    logger.log(Level.INFO, "LogParserParser: Start parsing : " + signature);
    final Calendar calendarStart = Calendar.getInstance();
    final HashMap<String, String> lineStatusMatches = channel.call(new LogParserStatusComputer(log, parsingRulesArray, compiledPatterns, signature));
    final InputStreamReader streamReader = new InputStreamReader(build.getLogInputStream(), build.getCharset());
    final BufferedReader reader = new BufferedReader(streamReader);
    String line;
    String status;
    int line_num = 0;
    while ((line = reader.readLine()) != null) {
      status = lineStatusMatches.get(String.valueOf(line_num));
      final String parsedLine = parseLine(line, status);
      writer.write(parsedLine);
      writer.newLine();
      line_num++;
    }
    reader.close();
    final Calendar calendarEnd = Calendar.getInstance();
    final long diffSeconds = (calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis()) / 1000;
    final long diffMinutes = diffSeconds / 60;
    logger.log(Level.INFO, "LogParserParser: Parsing took " + diffMinutes + " minutes (" + diffSeconds + ") seconds.");
  }
}