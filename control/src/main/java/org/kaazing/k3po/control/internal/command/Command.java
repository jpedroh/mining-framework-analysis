package org.kaazing.k3po.control.internal.command;
import java.util.Objects;

public abstract class Command {
  public enum Kind {
    PREPARE,
    START,
    ABORT,
    AWAIT,
    NOTIFY
  }

  public abstract Kind getKind();

  @Override public abstract int hashCode();

  @Override public boolean equals(Object o) {
    return o == this || o instanceof Command && equalTo((Command) o);
  }

  protected final boolean equalTo(Command that) {
    return Objects.equals(this.getKind(), that.getKind());
  }
}