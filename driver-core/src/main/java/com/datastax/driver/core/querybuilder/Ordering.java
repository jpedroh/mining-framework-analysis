package com.datastax.driver.core.querybuilder;
import java.nio.ByteBuffer;
import java.util.List;

public class Ordering extends Utils.Appendeable {
  private final String name;

  private final boolean isDesc;

  Ordering(String name, boolean isDesc) {
    this.name = name;
    this.isDesc = isDesc;
  }

  @Override void appendTo(StringBuilder sb, List<Object> variables) {
    Utils.appendName(name, sb);
    sb.append(isDesc ? " DESC" : " ASC");
  }

  @Override boolean containsBindMarker() {
    return false;
  }
}