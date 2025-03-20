package com.github.theholywaffle.teamspeak3.log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
  private static final DateFormat format = DateFormat.getInstance();

  private final boolean debugToFile;

  private File log;

  public LogHandler(boolean debugToFile) {
    this.debugToFile = debugToFile;
    if (this.debugToFile) {
      log = new File("teamspeak.log");
      if (!log.exists()) {
        try {
          log.createNewFile();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override public void close() throws SecurityException {
  }

  @Override public void flush() {
  }

  @Override public void publish(LogRecord record) {
    if (record.getLevel().intValue() < Level.WARNING.intValue()) {
      System.out.println("[DEBUG] " + record.getMessage());
    } else {
      System.err.println("[DEBUG] [" + record.getLevel() + "] " + record.getMessage());
    }
    if (debugToFile) {
      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(log, true)))) {
        out.println("[" + format.format(new Date()) + "][" + record.getLevel() + "] " + record.getMessage());
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }
}