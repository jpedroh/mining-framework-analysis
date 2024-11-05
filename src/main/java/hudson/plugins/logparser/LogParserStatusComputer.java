package hudson.plugins.logparser;
import hudson.FilePath;
import hudson.remoting.RemoteInputStream;
import jenkins.security.MasterToSlaveCallable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LogParserStatusComputer extends MasterToSlaveCallable<HashMap<String, String>, RuntimeException> {
  private static final long serialVersionUID = 1L;

  final private String[] parsingRulesArray;

  final private Pattern[] compiledPatterns;

  final private InputStream remoteLog;

  private final String signature;

  public LogParserStatusComputer(final InputStream log, final String[] parsingRulesArray, final Pattern[] compiledPatterns, final String signature) throws IOException, InterruptedException {
    this.parsingRulesArray = parsingRulesArray;
    this.compiledPatterns = compiledPatterns;
    this.remoteLog = new RemoteInputStream(log, RemoteInputStream.Flag.GREEDY);
    this.signature = signature;
  }

  public HashMap<String, String> call() {
    try {
      return computeStatusMatches(remoteLog, signature);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private HashMap<String, String> computeStatusMatches(final InputStream log, final String signature) throws IOException, InterruptedException {
    final Logger logger = Logger.getLogger(this.getClass().getName());
    String tempDir = System.getProperty("java.io.tmpdir");
    if (!tempDir.endsWith(File.separator)) {
      final StringBuffer tempDirBuffer = new StringBuffer(tempDir);
      tempDirBuffer.append(File.separator);
      tempDir = tempDirBuffer.toString();
    }
    final String tempFileLocation = tempDir + "log-parser_" + signature;
    final File tempFile = new File(tempFileLocation);
    final FilePath tempFilePath = new FilePath(tempFile);
    tempFilePath.copyFrom(log);
    logger.log(Level.INFO, "Local temp file:" + tempFileLocation);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(tempFilePath.read()));
    int threadCounter = 0;
    final ArrayList<LogParserThread> runners = new ArrayList<>();
    final LogParserReader logParserReader = new LogParserReader(reader);
    final ExecutorService execSvc = Executors.newCachedThreadPool();
    int linesInLog = LogParserUtils.countLines(tempFileLocation);
    final int threadsNeeded = linesInLog / LogParserUtils.getLinesPerThread() + 1;
    for (int i = 0; i < threadsNeeded; i++) {
      final LogParserThread logParserThread = new LogParserThread(logParserReader, parsingRulesArray, compiledPatterns, threadCounter);
      runners.add(logParserThread);
      execSvc.execute(logParserThread);
      threadCounter++;
    }
    execSvc.shutdown();
    execSvc.awaitTermination(3600, TimeUnit.SECONDS);
    final int runnersSize = runners.size();
    LogParserThread[] sortedRunners = new LogParserThread[runnersSize];
    for (LogParserThread logParserThread : runners) {
      final LogParserLogPart logPart = logParserThread.getLogPart();
      if (logPart != null) {
        final int logPartNum = logPart.getLogPartNum();
        sortedRunners[logPartNum] = logParserThread;
      }
    }
    final HashMap<String, String> result = new HashMap<>();
    HashMap<String, String> moreLineStatusMatches;
    for (int i = 0; i < runnersSize; i++) {
      final LogParserThread logParserThread = sortedRunners[i];
      if (logParserThread != null) {
        moreLineStatusMatches = getLineStatusMatches(logParserThread.getLineStatuses(), i);
        result.putAll(moreLineStatusMatches);
      }
    }
    reader.close();
    tempFilePath.delete();
    return result;
  }

  private HashMap<String, String> getLineStatusMatches(final String[] statuses, final int logPart) {
    final HashMap<String, String> result = new HashMap<>();
    String status;
    int line_num;
    final int linesPerThread = LogParserUtils.getLinesPerThread();
    for (int i = 0; i < statuses.length; i++) {
      status = statuses[i];
      line_num = i + logPart * linesPerThread;
      result.put(String.valueOf(line_num), status);
    }
    return result;
  }
}