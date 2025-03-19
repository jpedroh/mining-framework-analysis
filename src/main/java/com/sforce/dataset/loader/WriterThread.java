package com.sforce.dataset.loader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import com.sforce.dataset.DatasetUtilConstants;

public class WriterThread implements Runnable {
  private static final int max_error_threshhold = 10000;

  private final BlockingQueue<String[]> queue;

  @SuppressWarnings(value = { "deprecation" }) private final EbinFormatWriter ebinWriter;

  private final ErrorWriter errorwriter;

  private final PrintStream logger;

  private volatile AtomicBoolean done = new AtomicBoolean(false);

  private volatile int errorRowCount = 0;

  private volatile int totalRowCount = 0;

  @SuppressWarnings(value = { "deprecation" }) WriterThread(BlockingQueue<String[]> q, EbinFormatWriter w, ErrorWriter ew, PrintStream logger) {
    if (q == null || w == null || ew == null) {
      throw new IllegalArgumentException("Constructor input cannot be null");
    }
    queue = q;
    this.ebinWriter = w;
    this.errorwriter = ew;
    this.logger = logger;
  }

  /**
 * Converts nulls to empty strings inside a string array so 
 * the csv encoder doesn't freak out when we run into a null
 * 
 * @param input A String array
 * @return a String array with no nulls 
 */
  private String[] convertNullToEmptyString(String[] input) {
    ArrayList<String> returnValue = new ArrayList<String>();
    for (String s : input) {
      returnValue.add(s == null ? "" : s);
    }
    return returnValue.toArray(new String[returnValue.size()]);
  }

  @SuppressWarnings(value = { "deprecation" }) public void run() {
    logger.println("Start: " + Thread.currentThread().getName());
    try {
      String[] row = convertNullToEmptyString(queue.take());
      while (row != null && row.length != 0) {
        try {
          totalRowCount++;
          ebinWriter.addrow(row);
        } catch (Throwable t) {
          if (errorRowCount == 0) {
            logger.println();
          }
          logger.println("Row {" + totalRowCount + "} has error {" + t + "}");
          if (row != null) {
            if (DatasetUtilConstants.debug) {
              t.printStackTrace();
            }
            errorwriter.addError(row, t.getMessage() != null ? t.getMessage() : t.toString());
            errorRowCount++;
            if (errorRowCount >= max_error_threshhold) {
              logger.println("Max error threshold reached. Aborting processing");
              break;
            }
          }
        }
        row = convertNullToEmptyString(queue.take());
      }
    } catch (Throwable t) {
      logger.println(Thread.currentThread().getName() + " " + t.getMessage());
    }
    try {
      ebinWriter.finish();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      errorwriter.finish();
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.println("END: " + Thread.currentThread().getName());
    done.set(true);
  }

  public boolean isDone() {
    return done.get();
  }

  public int getErrorRowCount() {
    return errorRowCount;
  }

  public int getTotalRowCount() {
    return totalRowCount;
  }
}