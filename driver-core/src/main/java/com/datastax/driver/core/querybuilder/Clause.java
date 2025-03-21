package com.datastax.driver.core.querybuilder;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class Clause extends Utils.Appendeable {
  final String name;

  private Clause(String name) {
    this.name = name;
  }



  String name() {
    return name;
  }

  abstract Object firstValue();

  static class SimpleClause extends Clause {
    private final String op;

    private final Object value;

    SimpleClause(String name, String op, Object value) {
      super(name);
      this.op = op;
      this.value = value;
    }

    @Override void appendTo(StringBuilder sb, List<ByteBuffer> variables) {
      Utils.appendName(name, sb).append(op);
      Utils.appendValue(value, sb, variables);
    }

    @Override Object firstValue() {
      return value;
    }

    @Override boolean containsBindMarker() {
      return Utils.containsBindMarker(value);
    }
  }

  static class InClause extends Clause {
    private final List<Object> values;

    InClause(String name, List<Object> values) {
      super(name);
      this.values = values;
      if (values == null) {
        throw new IllegalArgumentException("Missing values for IN clause");
      }
    }

    @Override void appendTo(StringBuilder sb, List<ByteBuffer> variables) {
      if (values.size() == 1 && values.get(0) instanceof BindMarker) {
        Utils.appendName(name, sb).append("IN ").append(values.get(0));
        return;
      }
      Utils.appendName(name, sb).append(" IN (");
      Utils.joinAndAppendValues(sb, ",", values, variables).append(")");
    }

    @Override Object firstValue() {
      return values.isEmpty() ? null : values.get(0);
    }

    @Override boolean containsBindMarker() {
      for (Object value : values) {
        if (Utils.containsBindMarker(value)) {
          return true;
        }
      }
      return false;
    }
  }
}