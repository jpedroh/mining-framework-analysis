package org.dspace.app.statistics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.dspace.core.Context;
import org.dspace.core.LogHelper;
import org.dspace.core.Utils;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.SearchUtils;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * This class performs all the actual analysis of a given set of DSpace log
 * files.  Most input can be configured; use the -help flag for a full list
 * of usage information.
 *
 * The output of this file is plain text and forms an "aggregation" file which
 * can then be used for display purposes using the related ReportGenerator
 * class.
 *
 * @author Richard Jones
 */
public class LogAnalyser {
  /**
     * aggregator for all actions performed in the system
     */
  private static Map<String, Integer> actionAggregator;

  /**
     * aggregator for all searches performed
     */
  private static Map<String, Integer> searchAggregator;

  /**
     * aggregator for user logins
     */
  private static Map<String, Integer> userAggregator;

  /**
     * aggregator for item views
     */
  private static Map<String, Integer> itemAggregator;

  /**
     * aggregator for current archive state statistics
     */
  private static Map<String, Integer> archiveStats;

  /**
     * warning counter
     */
  private static int warnCount = 0;

  /**
     * exception counter
     */
  private static int excCount = 0;

  /**
     * log line counter
     */
  private static int lineCount = 0;

  /**
     * list of actions to be included in the general summary
     */
  private static List<String> generalSummary;

  /**
     * list of words not to be aggregated
     */
  private static List<String> excludeWords;

  /**
     * list of search types to be ignored, such as "author:"
     */
  private static List<String> excludeTypes;

  /**
     * list of characters to be excluded
     */
  private static List<String> excludeChars;

  /**
     * list of item types to be reported on in the current state
     */
  private static List<String> itemTypes;

  /**
     * bottom limit to output for search word analysis
     */
  private static int searchFloor;

  /**
     * bottom limit to output for item view analysis
     */
  private static int itemFloor;

  /**
     * number of items from most popular to be looked up in the database
     */
  private static int itemLookup;

  /**
     * mode to use for user email display
     */
  private static String userEmail;

  /**
     * URL of the service being analysed
     */
  private static String url;

  /**
     * Name of the service being analysed
     */
  private static String name;

  /**
     * Name of the service being analysed
     */
  private static String hostName;

  /**
     * the average number of views per item
     */
  private static int views = 0;

  /**
     * Exclude characters regular expression pattern
     */
  private static Pattern excludeCharRX = null;

  /**
     * handle indicator string regular expression pattern
     */
  private static Pattern handleRX = null;

  /**
     * item id indicator string regular expression pattern
     */
  private static Pattern itemRX = null;

  /**
     * query string indicator regular expression pattern
     */
  private static Pattern queryRX = null;

  /**
     * collection indicator regular expression pattern
     */
  private static Pattern collectionRX = null;

  /**
     * community indicator regular expression pattern
     */
  private static Pattern communityRX = null;

  /**
     * results indicator regular expression pattern
     */
  private static Pattern resultsRX = null;

  /**
     * single character regular expression pattern
     */
  private static Pattern singleRX = null;

  /**
     * a pattern to match a valid version 1.3 log file line
     */
  private static Pattern valid13 = null;

  /**
     * basic log line
     */
  private static Pattern validBase = null;

  /**
     * a pattern to match a valid version 1.4 log file line
     */
  private static Pattern valid14 = null;

  /**
     * pattern to match valid log file names
     */
  private static Pattern logRegex = null;

  /**
     * pattern to match commented out lines from the config file
     */
  private static final Pattern COMMENT = Pattern.compile("^#");

  /**
     * pattern to match genuine lines from the config file
     */
  private static final Pattern REAL = Pattern.compile("^(.+)=(.+)");

  /**
     * pattern to match all search types
     */
  private static Pattern typeRX = null;

  /**
     * pattern to match all search types
     */
  private static Pattern wordRX = null;

  /**
     * process timing clock
     */
  private static Calendar startTime = null;

  /**
     * the log directory to be analysed
     */
  private static String logDir;

  /**
     * the regex to describe the file name format
     */
  private static String fileTemplate = "dspace\\.log.*";

  /**
     * the configuration file from which to configure the analyser
     */
  private static String configFile;

  /**
     * the output file to which to write aggregation data
     */
  private static String outFile;

  /**
     * the starting date of the report
     */
  private static Date startDate = null;

  /**
     * the end date of the report
     */
  private static Date endDate = null;

  /**
     * the starting date of the report as obtained from the log files
     */
  private static Date logStartDate = null;

  /**
     * the end date of the report as obtained from the log files
     */
  private static Date logEndDate = null;

  /**
     * Default constructor
     */
  private LogAnalyser() {
  }

  /**
     * main method to be run from command line.  See usage information for
     * details as to how to use the command line flags (-help)
     *
     * @param argv the command line arguments given
     * @throws Exception    if error
     * @throws SQLException if database error
     */
  public static void main(String[] argv) throws Exception, SQLException {
    startTime = new GregorianCalendar();
    Context context = new Context();
    context.turnOffAuthorisationSystem();
    String myLogDir = null;
    String myFileTemplate = null;
    String myConfigFile = null;
    String myOutFile = null;
    Date myStartDate = null;
    Date myEndDate = null;
    boolean myLookUp = false;
    Options options = new Options();
    Option option;
    option = Option.builder().longOpt("log").hasArg().build();
    options.addOption(option);
    option = Option.builder().longOpt("file").hasArg().build();
    options.addOption(option);
    option = Option.builder().longOpt("cfg").hasArg().build();
    options.addOption(option);
    option = Option.builder().longOpt("out").hasArg().build();
    options.addOption(option);
    option = Option.builder().longOpt("help").build();
    options.addOption(option);
    option = Option.builder().longOpt("start").hasArg().build();
    options.addOption(option);
    option = Option.builder().longOpt("end").hasArg().build();
    options.addOption(option);
    option = Option.builder().longOpt("lookup").build();
    DefaultParser cmdParser = new DefaultParser();
    CommandLine cmd = cmdParser.parse(options, argv);
    if (cmd.hasOption("help")) {
      LogAnalyser.usage();
      System.exit(0);
    }
    if (cmd.hasOption("log")) {
      myLogDir = cmd.getOptionValue("log");
    }
    if (cmd.hasOption("file")) {
      myFileTemplate = cmd.getOptionValue("file");
    }
    if (cmd.hasOption("cfg")) {
      myConfigFile = cmd.getOptionValue("cfg");
    }
    if (cmd.hasOption("out")) {
      myOutFile = cmd.getOptionValue("out");
    }
    if (cmd.hasOption("start")) {
      myStartDate = parseDate(cmd.getOptionValue("start"));
    }
    if (cmd.hasOption("end")) {
      myEndDate = parseDate(cmd.getOptionValue("end"));
    }
    myLookUp = cmd.hasOption("lookup");
    processLogs(context, myLogDir, myFileTemplate, myConfigFile, myOutFile, myStartDate, myEndDate, myLookUp);
  }

  /**
     * using the pre-configuration information passed here, analyse the logs
     * and produce the aggregation file
     *
     * @param context        the DSpace context object this occurs under
     * @param myLogDir       the passed log directory.  Uses default if null
     * @param myFileTemplate the passed file name regex.  Uses default if null
     * @param myConfigFile   the DStat config file.  Uses default if null
     * @param myOutFile      the file to which to output aggregation data.  Uses default if null
     * @param myStartDate    the desired start of the analysis.  Starts from the beginning otherwise
     * @param myEndDate      the desired end of the analysis.  Goes to the end otherwise
     * @param myLookUp       force a lookup of the database
     * @return aggregate output
     * @throws IOException            if IO error
     * @throws SQLException           if database error
     * @throws SearchServiceException if search error
     */
  public static String processLogs(Context context, String myLogDir, String myFileTemplate, String myConfigFile, String myOutFile, Date myStartDate, Date myEndDate, boolean myLookUp) throws IOException, SQLException, SearchServiceException {
    startTime = new GregorianCalendar();
    actionAggregator = new HashMap<>();
    searchAggregator = new HashMap<>();
    userAggregator = new HashMap<>();
    itemAggregator = new HashMap<>();
    archiveStats = new HashMap<>();
    generalSummary = new ArrayList<>();
    excludeWords = new ArrayList<>();
    excludeTypes = new ArrayList<>();
    excludeChars = new ArrayList<>();
    itemTypes = new ArrayList<>();
    setParameters(myLogDir, myFileTemplate, myConfigFile, myOutFile, myStartDate, myEndDate, myLookUp);
    FileReader fr = null;
    BufferedReader br = null;
    readConfig(configFile);
    setRegex(fileTemplate);
    File[] logFiles = getLogFiles(logDir);
    int i = 0;
    for (i = 0; i < logFiles.length; i++) {
      Matcher matchRegex = logRegex.matcher(logFiles[i].getName());
      if (matchRegex.matches()) {
        try {
          fr = new FileReader(logFiles[i].toString());
          br = new BufferedReader(fr);
        } catch (IOException e) {
          System.out.println("Failed to read log file " + logFiles[i].toString());
          System.exit(0);
        }
        String line = null;
        while ((line = br.readLine()) != null) {
          LogLine logLine = getLogLine(line);
          if (logLine != null) {
            if ((startDate != null) && (!logLine.afterDate(startDate))) {
              continue;
            }
            if ((endDate != null) && (!logLine.beforeDate(endDate))) {
              break;
            }
            lineCount++;
            if (startDate == null) {
              if (logStartDate != null) {
                if (logLine.beforeDate(logStartDate)) {
                  logStartDate = logLine.getDate();
                }
              } else {
                logStartDate = logLine.getDate();
              }
            }
            if (endDate == null) {
              if (logEndDate != null) {
                if (logLine.afterDate(logEndDate)) {
                  logEndDate = logLine.getDate();
                }
              } else {
                logEndDate = logLine.getDate();
              }
            }
            if (logLine.isLevel("WARN")) {
              warnCount++;
            }
            if (logLine.isLevel("ERROR")) {
              excCount++;
            }
            if (null == logLine.getAction()) {
              continue;
            }
            if (logLine.isAction("search")) {
              String[] words = analyseQuery(logLine.getParams());
              for (String word : words) {
                searchAggregator.put(word, increment(searchAggregator, word));
              }
            }
            if (logLine.isAction("login") && !userEmail.equals("off")) {
              userAggregator.put(logLine.getUser(), increment(userAggregator, logLine.getUser()));
            }
            if (logLine.isAction("view_item")) {
              String handle = logLine.getParams();
              Matcher matchHandle = handleRX.matcher(handle);
              handle = matchHandle.replaceAll("");
              Matcher matchItem = itemRX.matcher(handle);
              handle = matchItem.replaceAll("").trim();
              itemAggregator.put(handle, increment(itemAggregator, handle));
            }
            actionAggregator.put(logLine.getAction(), increment(actionAggregator, logLine.getAction()));
          }
        }
        br.close();
        fr.close();
      }
    }
    archiveStats.put("All Items", getNumItems(context));
    for (i = 0; i < itemTypes.size(); i++) {
      archiveStats.put(itemTypes.get(i), getNumItems(context, itemTypes.get(i)));
    }
    ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
    hostName = Utils.getHostName(configurationService.getProperty("dspace.ui.url"));
    name = configurationService.getProperty("dspace.name").trim();
    url = configurationService.getProperty("dspace.ui.url").trim();
    if ((url != null) && (!url.endsWith("/"))) {
      url = url + "/";
    }
    if ((archiveStats.get("All Items")) != 0) {
      Double avg = Math.ceil((actionAggregator.get("view_item")).doubleValue() / (archiveStats.get("All Items")).doubleValue());
      views = avg.intValue();
    }
    return createOutput();
  }

  /**
     * set the passed parameters up as global class variables.  This has to
     * be done in a separate method because the API permits for running from
     * the command line with args or calling the processLogs method statically
     * from elsewhere
     *
     * @param myLogDir       the log file directory to be analysed
     * @param myFileTemplate regex for log file names
     * @param myConfigFile   config file to use for dstat
     * @param myOutFile      file to write the aggregation into
     * @param myStartDate    requested log reporting start date
     * @param myEndDate      requested log reporting end date
     * @param myLookUp       requested look up force flag
     */
  public static void setParameters(String myLogDir, String myFileTemplate, String myConfigFile, String myOutFile, Date myStartDate, Date myEndDate, boolean myLookUp) {
    ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
    if (myLogDir != null) {
      logDir = myLogDir;
    } else {
      logDir = configurationService.getProperty("log.report.dir");
    }
    if (myFileTemplate != null) {
      fileTemplate = myFileTemplate;
    }
    if (myConfigFile != null) {
      configFile = myConfigFile;
    } else {
      configFile = configurationService.getProperty("dspace.dir") + File.separator + "config" + File.separator + "dstat.cfg";
    }
    if (myStartDate != null) {
      startDate = new Date(myStartDate.getTime());
    }
    if (myEndDate != null) {
      endDate = new Date(myEndDate.getTime());
    }
    if (myOutFile != null) {
      outFile = myOutFile;
    } else {
      outFile = configurationService.getProperty("log.report.dir") + File.separator + "dstat.dat";
    }
  }

  /**
     * generate the analyser's output to the specified out file
     *
     * @return output
     */
  public static String createOutput() {
    StringBuilder summary = new StringBuilder();
    Iterator<String> keys = null;
    summary.append("log_lines=").append(Integer.toString(lineCount)).append("\n");
    summary.append("warnings=").append(Integer.toString(warnCount)).append("\n");
    summary.append("exceptions=").append(Integer.toString(excCount)).append("\n");
    for (int i = 0; i < generalSummary.size(); i++) {
      summary.append("general_summary=").append(generalSummary.get(i)).append("\n");
    }
    summary.append("server_name=").append(hostName).append("\n");
    summary.append("service_name=").append(name).append("\n");
    SimpleDateFormat sdf = new SimpleDateFormat("dd\'/\'MM\'/\'yyyy");
    if (startDate != null) {
      summary.append("start_date=").append(sdf.format(startDate)).append("\n");
    } else {
      if (logStartDate != null) {
        summary.append("start_date=").append(sdf.format(logStartDate)).append("\n");
      }
    }
    if (endDate != null) {
      summary.append("end_date=").append(sdf.format(endDate)).append("\n");
    } else {
      if (logEndDate != null) {
        summary.append("end_date=").append(sdf.format(logEndDate)).append("\n");
      }
    }
    keys = archiveStats.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      summary.append("archive.").append(key).append("=").append(archiveStats.get(key)).append("\n");
    }
    keys = actionAggregator.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      summary.append("action.").append(key).append("=").append(actionAggregator.get(key)).append("\n");
    }
    summary.append("user_email=").append(userEmail).append("\n");
    int address = 1;
    keys = userAggregator.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      summary.append("user.");
      if (userEmail.equals("on")) {
        summary.append(key).append("=").append(userAggregator.get(key)).append("\n");
      } else {
        if (userEmail.equals("alias")) {
          summary.append("Address ").append(Integer.toString(address++)).append("=").append(userAggregator.get(key)).append("\n");
        }
      }
    }
    summary.append("search_floor=").append(searchFloor).append("\n");
    keys = searchAggregator.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      if ((searchAggregator.get(key)) >= searchFloor) {
        summary.append("search.").append(key).append("=").append(searchAggregator.get(key)).append("\n");
      }
    }
    summary.append("item_floor=").append(itemFloor).append("\n");
    summary.append("host_url=").append(url).append("\n");
    summary.append("item_lookup=").append(itemLookup).append("\n");
    keys = itemAggregator.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      if ((itemAggregator.get(key)) >= itemFloor) {
        summary.append("item.").append(key).append("=").append(itemAggregator.get(key)).append("\n");
      }
    }
    if (views > 0) {
      summary.append("avg_item_views=").append(views).append("\n");
    }
    Calendar endTime = new GregorianCalendar();
    long timeInMillis = (endTime.getTimeInMillis() - startTime.getTimeInMillis());
    summary.append("analysis_process_time=").append(Long.toString(timeInMillis / 1000)).append("\n");
    try (BufferedWriter out = new BufferedWriter(new FileWriter(outFile))) {
      out.write(summary.toString());
      out.flush();
    } catch (IOException e) {
      System.out.println("Unable to write to output file " + outFile);
      System.exit(0);
    }
    return summary.toString();
  }

  /**
     * get an array of file objects representing the passed log directory
     *
     * @param logDir the log directory in which to pick up files
     * @return an array of file objects representing the given logDir
     */
  public static File[] getLogFiles(String logDir) {
    File logs = new File(logDir);
    if (!logs.isDirectory()) {
      System.out.println("Passed log directory is not a directory");
      System.exit(0);
    }
    return logs.listFiles();
  }

  /**
     * set up the regular expressions to be used by this analyser.  Mostly this
     * exists to provide a degree of segregation and readability to the code
     * and to ensure that you only need to set up the regular expressions to
     * be used once
     *
     * @param fileTemplate the regex to be used to identify dspace log files
     */
  public static void setRegex(String fileTemplate) {
    StringBuilder charRegEx = new StringBuilder();
    charRegEx.append("[");
    for (int i = 0; i < excludeChars.size(); i++) {
      charRegEx.append("\\").append(excludeChars.get(i));
    }
    charRegEx.append("]");
    excludeCharRX = Pattern.compile(charRegEx.toString());
    handleRX = Pattern.compile("handle=");
    itemRX = Pattern.compile(",item_id=.*$");
    queryRX = Pattern.compile("query=");
    collectionRX = Pattern.compile("collection_id=[0-9]*,");
    communityRX = Pattern.compile("community_id=[0-9]*,");
    resultsRX = Pattern.compile(",results=(.*)");
    singleRX = Pattern.compile("( . |^. | .$)");
    String logLineBase = "^(\\d\\d\\d\\d-\\d\\d\\-\\d\\d) \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d (\\w+)\\s+\\S+ @ (.*)";
    String logLine13 = "^(\\d\\d\\d\\d-\\d\\d\\-\\d\\d) \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d (\\w+)\\s+\\S+ @ ([^:]+)" + ":[^:]+:([^:]+):(.*)";
    String logLine14 = "^(\\d\\d\\d\\d-\\d\\d\\-\\d\\d) \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d (\\w+)\\s+\\S+ @ ([^:]+)" + ":[^:]+:[^:]+:([^:]+):(.*)";
    valid13 = Pattern.compile(logLine13);
    valid14 = Pattern.compile(logLine14);
    validBase = Pattern.compile(logLineBase);
    logRegex = Pattern.compile(fileTemplate);
    StringBuilder typeRXString = new StringBuilder();
    typeRXString.append("(");
    for (int i = 0; i < excludeTypes.size(); i++) {
      if (i > 0) {
        typeRXString.append("|");
      }
      typeRXString.append(excludeTypes.get(i));
    }
    typeRXString.append(")");
    typeRX = Pattern.compile(typeRXString.toString());
    StringBuilder wordRXString = new StringBuilder();
    wordRXString.append("(");
    for (int i = 0; i < excludeWords.size(); i++) {
      if (i > 0) {
        wordRXString.append("|");
      }
      wordRXString.append(" ").append(excludeWords.get(i)).append(" ");
      wordRXString.append("|");
      wordRXString.append("^").append(excludeWords.get(i)).append(" ");
      wordRXString.append("|");
      wordRXString.append(" ").append(excludeWords.get(i)).append("$");
    }
    wordRXString.append(")");
    wordRX = Pattern.compile(wordRXString.toString());
  }

  /**
     * get the current config file name
     *
     * @return The name of the config file
     */
  public static String getConfigFile() {
    return configFile;
  }

  /**
     * Read in the current config file and populate the class globals.
     *
     * @throws IOException if IO error
     */
  public static void readConfig() throws IOException {
    readConfig(configFile);
  }

  /**
     * Read in the given config file and populate the class globals.
     *
     * @param configFile the config file to read in
     * @throws IOException if IO error
     */
  public static void readConfig(String configFile) throws IOException {
    actionAggregator = new HashMap<>();
    searchAggregator = new HashMap<>();
    userAggregator = new HashMap<>();
    itemAggregator = new HashMap<>();
    archiveStats = new HashMap<>();
    generalSummary = new ArrayList<>();
    excludeWords = new ArrayList<>();
    excludeTypes = new ArrayList<>();
    excludeChars = new ArrayList<>();
    itemTypes = new ArrayList<>();
    FileReader fr = null;
    BufferedReader br = null;
    String record = null;
    try {
      fr = new FileReader(configFile);
      br = new BufferedReader(fr);
    } catch (IOException e) {
      System.out.println("Failed to read config file: " + configFile);
      System.exit(0);
    }
    while ((record = br.readLine()) != null) {
      Matcher matchComment = COMMENT.matcher(record);
      Matcher matchReal = REAL.matcher(record);
      if (!matchComment.matches() && matchReal.matches()) {
        String key = matchReal.group(1).trim();
        String value = matchReal.group(2).trim();
        if (key.equals("general.summary")) {
          actionAggregator.put(value, 0);
          generalSummary.add(value);
        }
        if (key.equals("exclude.word")) {
          excludeWords.add(value);
        }
        if (key.equals("exclude.type")) {
          excludeTypes.add(value);
        }
        if (key.equals("exclude.character")) {
          excludeChars.add(value);
        }
        if (key.equals("item.type")) {
          itemTypes.add(value);
        }
        if (key.equals("item.floor")) {
          itemFloor = Integer.parseInt(value);
        }
        if (key.equals("search.floor")) {
          searchFloor = Integer.parseInt(value);
        }
        if (key.equals("item.lookup")) {
          itemLookup = Integer.parseInt(value);
        }
        if (key.equals("user.email")) {
          userEmail = value;
        }
      }
    }
    br.close();
    fr.close();
  }

  /**
     * increment the value of the given map at the given key by one.
     *
     * @param map the map whose value we want to increase
     * @param key the key of the map whose value to increase
     * @return an integer object containing the new value
     */
  public static Integer increment(Map<String, Integer> map, String key) {
    Integer newValue = null;
    if (map.containsKey(key)) {
      newValue = (map.get(key)) + 1;
    } else {
      newValue = 1;
    }
    return newValue;
  }

  /**
     * Take the standard date string requested at the command line and convert
     * it into a Date object.  Throws and error and exits if the date does
     * not parse
     *
     * @param date the string representation of the date
     * @return a date object containing the date, with the time set to
     * 00:00:00
     */
  public static Date parseDate(String date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy\'-\'MM\'-\'dd");
    Date parsedDate = null;
    try {
      parsedDate = sdf.parse(date);
    } catch (ParseException e) {
      System.out.println("The date is not in the correct format");
      System.exit(0);
    }
    return parsedDate;
  }

  /**
     * Take the date object and convert it into a string of the form YYYY-MM-DD
     *
     * @param date the date to be converted
     * @return A string of the form YYYY-MM-DD
     */
  public static String unParseDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy\'-\'MM\'-\'dd\'T\'hh:mm:ss\'Z\'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }

  /**
     * Take a search query string and pull out all of the meaningful information
     * from it, giving the results in the form of a String array, a single word
     * to each element
     *
     * @param query the search query to be analysed
     * @return the string array containing meaningful search terms
     */
  public static String[] analyseQuery(String query) {
    int i = 0;
    query = query.toLowerCase();
    Matcher matchQuery = queryRX.matcher(query);
    query = matchQuery.replaceAll(" ");
    Matcher matchCollection = collectionRX.matcher(query);
    query = matchCollection.replaceAll(" ");
    Matcher matchCommunity = communityRX.matcher(query);
    query = matchCommunity.replaceAll(" ");
    Matcher matchResults = resultsRX.matcher(query);
    query = matchResults.replaceAll(" ");
    Matcher matchTypes = typeRX.matcher(query);
    query = matchTypes.replaceAll(" ");
    Matcher matchChars = excludeCharRX.matcher(query);
    query = matchChars.replaceAll(" ");
    Matcher matchWords = wordRX.matcher(query);
    query = matchWords.replaceAll(" ");
    Matcher single = singleRX.matcher(query);
    query = single.replaceAll(" ");
    StringTokenizer st = new StringTokenizer(query);
    String[] words = new String[st.countTokens()];
    for (i = 0; i < words.length; i++) {
      words[i] = st.nextToken().trim();
    }
    return words;
  }

  /**
     * split the given line into it's relevant segments if applicable (i.e. the
     * line matches the required regular expression.
     *
     * @param line the line to be segmented
     * @return a Log Line object for the given line
     */
  public static LogLine getLogLine(String line) {
    Matcher match;
    if (line.indexOf(":ip_addr") > 0) {
      match = valid14.matcher(line);
    } else {
      match = valid13.matcher(line);
    }
    if (match.matches()) {
      LogLine logLine = new LogLine(parseDate(match.group(1).trim()), LogHelper.unescapeLogField(match.group(2)).trim(), LogHelper.unescapeLogField(match.group(3)).trim(), LogHelper.unescapeLogField(match.group(4)).trim(), LogHelper.unescapeLogField(match.group(5)).trim());
      return logLine;
    } else {
      match = validBase.matcher(line);
      if (match.matches()) {
        LogLine logLine = new LogLine(parseDate(match.group(1).trim()), LogHelper.unescapeLogField(match.group(2)).trim(), null, null, null);
        return logLine;
      }
      return null;
    }
  }

  /**
     * get the number of items in the archive which were accessioned between
     * the provided start and end dates, with the given value for the DC field
     * 'type' (unqualified)
     *
     * @param context the DSpace context for the action
     * @param type    value for DC field 'type' (unqualified)
     * @return an integer containing the relevant count
     * @throws SQLException           if database error
     * @throws SearchServiceException if search error
     */
  public static Integer getNumItems(Context context, String type) throws SQLException, SearchServiceException {
    DiscoverQuery discoverQuery = new DiscoverQuery();
    if (StringUtils.isNotBlank(type)) {
      discoverQuery.addFilterQueries("dc.type=" + type + "*");
    }
    StringBuilder accessionedQuery = new StringBuilder();
    accessionedQuery.append("dc.date.accessioned_dt:[");
    if (startDate != null) {
      accessionedQuery.append(unParseDate(startDate));
    } else {
      accessionedQuery.append("*");
    }
    accessionedQuery.append(" TO ");
    if (endDate != null) {
      accessionedQuery.append(unParseDate(endDate));
    } else {
      accessionedQuery.append("*");
    }
    accessionedQuery.append("]");
    discoverQuery.addFilterQueries(accessionedQuery.toString());
    discoverQuery.addFilterQueries("withdrawn: false");
    discoverQuery.addFilterQueries("archived: true");
    return (int) SearchUtils.getSearchService().search(context, discoverQuery).getTotalSearchResults();
  }

  /**
     * get the total number of items in the archive at time of execution,
     * ignoring all other constraints
     *
     * @param context the DSpace context the action is being performed in
     * @return an Integer containing the number of items in the
     * archive
     * @throws SQLException           if database error
     * @throws SearchServiceException if search error
     */
  public static Integer getNumItems(Context context) throws SQLException, SearchServiceException {
    return getNumItems(context, null);
  }

  /**
     * print out the usage information for this class to the standard out
     */
  public static void usage() {
    String usage = "Usage Information:\n" + "LogAnalyser [options [parameters]]\n" + "-log [log directory]\n" + "\tOptional\n" + "\tSpecify a directory containing log files\n" + "\tDefault uses [dspace.dir]/log from dspace.cfg\n" + "-file [file name regex]\n" + "\tOptional\n" + "\tSpecify a regular expression as the file name template.\n" + "\tCurrently this needs to be correctly escaped for Java string handling (FIXME)\n" + "\tDefault uses dspace.log*\n" + "-cfg [config file path]\n" + "\tOptional\n" + "\tSpecify a config file to be used\n" + "\tDefault uses dstat.cfg in dspace config directory\n" + "-out [output file path]\n" + "\tOptional\n" + "\tSpecify an output file to write results into\n" + "\tDefault uses dstat.dat in dspace log directory\n" + "-start [YYYY-MM-DD]\n" + "\tOptional\n" + "\tSpecify the start date of the analysis\n" + "\tIf a start date is specified then no attempt to gather \n" + "\tcurrent database statistics will be made unless -lookup is\n" + "\talso passed\n" + "\tDefault is to start from the earliest date records exist for\n" + "-end [YYYY-MM-DD]\n" + "\tOptional\n" + "\tSpecify the end date of the analysis\n" + "\tIf an end date is specified then no attempt to gather \n" + "\tcurrent database statistics will be made unless -lookup is\n" + "\talso passed\n" + "\tDefault is to work up to the last date records exist for\n" + "-lookup\n" + "\tOptional\n" + "\tForce a lookup of the current database statistics\n" + "\tOnly needs to be used if date constraints are also in place\n" + "-help\n" + "\tdisplay this usage information\n";
    System.out.println(usage);
  }
}