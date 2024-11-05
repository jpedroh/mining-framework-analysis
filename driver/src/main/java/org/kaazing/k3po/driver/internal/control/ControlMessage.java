package org.kaazing.k3po.driver.internal.control;

public abstract class ControlMessage {
  public enum Kind {
    PREPARE,
    PREPARED,
    START,
    STARTED,
    ERROR,
    ABORT,
    FINISHED,
    AWAIT,
    NOTIFY,
    NOTIFIED
  }

  public abstract Kind getKind();

  public abstract int hashCode();

  public abstract boolean equals(Object obj);

  protected final boolean equalTo(ControlMessage that) {
    return this.getKind() == that.getKind();
  }

  @Override public String toString() {
    return String.format("%s", getKind());
  }
}