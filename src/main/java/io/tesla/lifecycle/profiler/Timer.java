package io.tesla.lifecycle.profiler;



public interface Timer {
  public abstract void stop();

  public abstract long getTime();

  public abstract String format(long elapsedTime);
}