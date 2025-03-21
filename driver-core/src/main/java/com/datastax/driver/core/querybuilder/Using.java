package com.datastax.driver.core.querybuilder;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class Using extends Utils.Appendeable {
  final String optionName;

  private Using(String optionName) {
    this.optionName = optionName;
  }

  static class WithValue extends Using {
    private final long value;

    WithValue(String optionName, long value) {
      super(optionName);
      this.value = value;
    }

    @Override void appendTo(StringBuilder sb, List<Object> variables) {
      sb.append(optionName).append(' ').append(value);
    }

    @Override boolean containsBindMarker() {
      return false;
    }
  }

  static class WithMarker extends Using {
    private final BindMarker marker;

    WithMarker(String optionName, BindMarker marker) {
      super(optionName);
      this.marker = marker;
    }

    @Override void appendTo(StringBuilder sb, List<Object> variables) {
      sb.append(optionName).append(' ').append(marker);
    }

    @Override boolean containsBindMarker() {
      return true;
    }
  }
}