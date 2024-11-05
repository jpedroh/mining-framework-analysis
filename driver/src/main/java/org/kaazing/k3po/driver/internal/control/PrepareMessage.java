package org.kaazing.k3po.driver.internal.control;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrepareMessage extends ControlMessage {
  private List<String> names;

  private String version;

  public PrepareMessage() {
    this.names = new ArrayList<>(5);
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public List<String> getNames() {
    return names;
  }

  public void setNames(List<String> names) {
    this.names.clear();
    this.names.addAll(names);
  }

  @Override public int hashCode() {
    return Objects.hash(getKind(), names);
  }

  @Override public boolean equals(Object obj) {
    return (this == obj) || (obj instanceof PrepareMessage) && equals((PrepareMessage) obj);
  }

  @Override public Kind getKind() {
    return Kind.PREPARE;
  }

  protected final boolean equals(PrepareMessage that) {
    return super.equalTo(that) && Objects.equals(this.names, that.names);
  }
}