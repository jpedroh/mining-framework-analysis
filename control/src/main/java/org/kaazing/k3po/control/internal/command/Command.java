package org.kaazing.k3po.control.internal.command;
import java.util.Objects;

/**
 * Abstract class for a Command to the robot.
 *
 */
public abstract class Command {
  public enum Kind {
    PREPARE,
    START,
    ABORT,
    AWAIT,
    NOTIFY
  }

  /**
     * @return Kind
     */
  public abstract Kind getKind();

  @Override public abstract int hashCode();

  @Override public boolean equals(Object o) {
    return o == this || o instanceof Command && equalTo((Command) o);
  }

  protected final boolean equalTo(Command that) {
    return Objects.equals(this.getKind(), that.getKind());
  }
}