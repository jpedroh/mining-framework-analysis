package io.tesla.lifecycle.profiler;

public interface Timer {

<<<<<<< /usr/src/app/output/takari/maven-profiler/2d7f5eba094953230771d27e6d178cfe31373c6d/src/main/java/io/tesla/lifecycle/profiler/Timer.java/left.java
  void stop();
  long getTime();
  String format(long elapsedTime);
||||||| /usr/src/app/output/takari/maven-profiler/2d7f5eba094953230771d27e6d178cfe31373c6d/src/main/java/io/tesla/lifecycle/profiler/Timer.java/base.java
  public Timer() {
    start = System.currentTimeMillis();
  }

  public void stop() {
    time = elapsedTime();
  }

  public long getTime() {
    return time;
  }

  private long elapsedTime() {
    return System.currentTimeMillis() - start;
  }

  public static String formatTime(long ms) {
    long secs = ms / MS_PER_SEC;
    long mins = secs / SEC_PER_MIN;
    secs = secs % SEC_PER_MIN;    
    long fractionOfASecond = ms - (secs * 1000);
//    System.out.println("mins " + mins);
//    System.out.println("secs " + secs);
//    System.out.println(fractionOfASecond);
//    System.out.println(">> " + fractionOfASecond);
    
    String msg = mins + "m " + secs + "." + fractionOfASecond;

    if (msg.length() == 3) {
      msg += "00s";
    } else if (msg.length() == 4) {
      msg += "0s";
    } else {
      msg += "s";
    }

    return msg;
  }
=======
  public Timer() {
    start = System.currentTimeMillis();
  }

  public void stop() {
    time = elapsedTime();
  }

  public long getTime() {
    return time;
  }

  private long elapsedTime() {
    return System.currentTimeMillis() - start;
  }

  public static String formatTime(long ms) {
    long secs = ms / MS_PER_SEC;
    long mins = secs / SEC_PER_MIN;
    secs = secs % SEC_PER_MIN;
    long fractionOfASecond = ms - (secs * 1000);

      StringBuilder msg = new StringBuilder();
      if (mins > 0)
      {
          msg.append(mins);
          msg.append("m ");
      }
      if (secs > 0)
      {
          msg.append(secs);
          msg.append("s");
      }

      if ( mins == 0)
      {
          if (msg.length() > 0 ) msg.append(" ");
          msg.append(fractionOfASecond);
          msg.append("ms");
      }

      return msg.toString();
  }
>>>>>>> /usr/src/app/output/takari/maven-profiler/2d7f5eba094953230771d27e6d178cfe31373c6d/src/main/java/io/tesla/lifecycle/profiler/Timer.java/right.java
}
