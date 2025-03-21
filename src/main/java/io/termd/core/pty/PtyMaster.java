package io.termd.core.pty;
import io.termd.core.io.BinaryDecoder;
import io.termd.core.util.Helper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * todo : integrate with
 * - https://github.com/traff/pty4j
 * - https://github.com/jawi/JPty
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class PtyMaster extends Thread {
  private Process process;

  private final String line;

  private boolean interrupted;

  private BiConsumer<Status, Status> changeHandler;

  private final Consumer<int[]> stdout;

  private final Consumer<Void> doneHandler;

  private Status status;

  public PtyMaster(String line, Consumer<int[]> stdout, Consumer<Void> doneHandler) {
    this.line = line;
    this.doneHandler = doneHandler;
    this.stdout = stdout;
    this.status = Status.NEW;
  }

  public BiConsumer<Status, Status> getChangeHandler() {
    return changeHandler;
  }

  public void setChangeHandler(BiConsumer<Status, Status> changeHandler) {
    this.changeHandler = changeHandler;
  }

  private class Pipe extends Thread {
    private final Charset charset = StandardCharsets.UTF_8;

    private final InputStream in;

    private final BinaryDecoder decoder = new BinaryDecoder(charset, (codepoints) -> 
<<<<<<< /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyMaster.java/left.java
    conn.schedule(() -> {
      int len = codepoints.length;
      for (int i = 0; i < codepoints.length; i++) {
        if (codepoints[i] == '\n' && (i == 0 || codepoints[i - 1] != '\r')) {
          len++;
        }
      }
      int ptr = 0;
      int[] corrected = new int[len];
      for (int i = 0; i < codepoints.length; i++) {
        if (codepoints[i] == '\n' && (i == 0 || codepoints[i - 1] != '\r')) {
          corrected[ptr++] = '\r';
          corrected[ptr++] = '\n';
        } else {
          corrected[ptr++] = codepoints[i];
        }
      }
      conn.stdoutHandler().accept(corrected);
      if (processOutputConsumer != null) {
        processOutputConsumer.accept(corrected);
      }
    })
=======
    {
      int len = codepoints.length;
      for (int i = 0; i < codepoints.length; i++) {
        if (codepoints[i] == '\n' && (i == 0 || codepoints[i - 1] != '\r')) {
          len++;
        }
      }
      int ptr = 0;
      int[] corrected = new int[len];
      for (int i = 0; i < codepoints.length; i++) {
        if (codepoints[i] == '\n' && (i == 0 || codepoints[i - 1] != '\r')) {
          corrected[ptr++] = '\r';
          corrected[ptr++] = '\n';
        } else {
          corrected[ptr++] = codepoints[i];
        }
      }
      stdout.accept(corrected);
    }
>>>>>>> /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyMaster.java/right.java
    );

    public Pipe(InputStream in) {
      this.in = in;
    }

    @Override public void run() {
      byte[] buffer = new byte[512];
      while (true) {
        try {
          int l = in.read(buffer);
          if (l == -1) {
            break;
          }
          decoder.write(buffer, 0, l);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Process getProcess() {
    return process;
  }

  @Override public void run() {
    ProcessBuilder builder = new ProcessBuilder(line.split("\\s+"));
    try {
      process = builder.start();
      setStatus(Status.RUNNING);
      Pipe stdout = new Pipe(process.getInputStream());
      Pipe stderr = new Pipe(process.getErrorStream());
      stdout.start();
      stderr.start();
      int exitValue = -1;
      try {
        process.waitFor();
        exitValue = process.exitValue();
      } catch (InterruptedException e) {
        setStatus(Status.INTERRUPTED);
        Thread.currentThread().interrupt();
      }
      try {
        stdout.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      try {
        stderr.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      if (exitValue == 0) {
        setStatus(Status.COMPLETED);
      } else {
        setStatus(Status.FAILED);
      }
    } catch (IOException e) {
      stdout.
<<<<<<< /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyMaster.java/left.java
      stdoutHandler().accept(Helper.toCodePoints(e.getMessage() + "\r\n"))
=======
      accept(Helper.toCodePoints(e.getMessage() + "\r\n"))
>>>>>>> /usr/src/app/output/termd/termd/75f5c20e784c95a7d810d61508d3c6be690661dc/src/main/java/io/termd/core/pty/PtyMaster.java/right.java
      ;
    }
    doneHandler.accept(null);
  }

  public Status getStatus() {
    return status;
  }

  public void interruptProcess() {
    if (!interrupted) {
      interrupted = true;
      process.destroy();
    }
  }

  private void setStatus(Status next) {
    Status prev = status;
    status = next;
    if (changeHandler != null) {
      changeHandler.accept(prev, next);
    }
  }
}