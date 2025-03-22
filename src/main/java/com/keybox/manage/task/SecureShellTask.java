package com.keybox.manage.task;
import com.keybox.common.util.AppConfig;
import com.keybox.manage.util.SSHUtil;
import com.keybox.manage.util.SessionOutputUtil;
import com.keybox.manage.model.SessionOutput;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Task to watch for output read from the ssh session stream
 */
public class SecureShellTask implements Runnable {
  InputStream outFromChannel;

  SessionOutput sessionOutput;

  public SecureShellTask(SessionOutput sessionOutput, InputStream outFromChannel) {
    this.sessionOutput = sessionOutput;
    this.outFromChannel = outFromChannel;
  }

  public void run() {
    InputStreamReader isr = new InputStreamReader(outFromChannel);
    BufferedReader br = new BufferedReader(isr);
    try {
      SessionOutputUtil.addOutput(sessionOutput.getSessionId(), sessionOutput.getHostSystemId(), sessionOutput);
      char[] buff = new char[
<<<<<<< /usr/src/app/output/bastillion-io/bastillion/83ad29e2011c4a71d9004d22939820a7e3318f9b/src/main/java/com/keybox/manage/task/SecureShellTask.java/left.java
      SSHUtil.KEY_LENGTH
=======
      Integer.parseInt(AppConfig.getProperty("KeyStrengh"))
>>>>>>> /usr/src/app/output/bastillion-io/bastillion/83ad29e2011c4a71d9004d22939820a7e3318f9b/src/main/java/com/keybox/manage/task/SecureShellTask.java/right.java
      ];
      int read;
      while ((read = br.read(buff)) != -1) {
        SessionOutputUtil.addToOutput(sessionOutput.getSessionId(), sessionOutput.getInstanceId(), buff, 0, read);
        Thread.sleep(50);
      }
      SessionOutputUtil.removeOutput(sessionOutput.getSessionId(), sessionOutput.getInstanceId());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}