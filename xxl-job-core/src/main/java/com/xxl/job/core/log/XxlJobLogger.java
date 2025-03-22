package com.xxl.job.core.log;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.springframework.util.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by xuxueli on 17/4/28.
 */
public class XxlJobLogger {
  private static Logger logger = LoggerFactory.getLogger("xxl-job logger");

  /**
     * append log
     *
     * @param callInfo
     * @param appendLog
     */
  private static void logDetail(StackTraceElement callInfo, String appendLog) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(DateUtil.format(new Date())).append(" ").append("[" + callInfo.getClassName() + "#" + callInfo.getMethodName() + "]").append("-").append("[" + callInfo.getLineNumber() + "]").append("-").append("[" + Thread.currentThread().getName() + "]").append(" ").append(appendLog != null ? appendLog : "");
    String formatAppendLog = stringBuffer.toString();
    String logFileName = XxlJobFileAppender.contextHolder.get();
    if (logFileName != null && logFileName.trim().length() > 0) {
      XxlJobFileAppender.appendLog(logFileName, formatAppendLog);
    } else {
      logger.info(">>>>>>>>>>> {}", formatAppendLog);
    }
    String subFileName = XxlJobFileAppender.subLogFile.get();
    if (!StringUtils.isEmpty(subFileName)) {
      XxlJobFileAppender.appendLog(subFileName, formatAppendLog);
    }
  }

  /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
  public static void log(String appendLogPattern, Object... appendLogArguments) {
    FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
    String appendLog = ft.getMessage();
    StackTraceElement callInfo = new Throwable().getStackTrace()[1];
    logDetail(callInfo, appendLog);
  }

  /**
     * append exception stack
     *
     * @param e
     */
  public static void log(Throwable e) {
    StringWriter stringWriter = new StringWriter();
    e.printStackTrace(new PrintWriter(stringWriter));
    String appendLog = stringWriter.toString();
    StackTraceElement callInfo = new Throwable().getStackTrace()[1];
    logDetail(callInfo, appendLog);
  }
}