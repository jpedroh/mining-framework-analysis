package com.datastax.driver.core.querybuilder;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class Clause extends Utils.Appendeable {
  abstract String name();

  abstract Object firstValue();

  private static abstract class AbstractClause extends Clause {
    final String name;

    private AbstractClause(String name) {
      this.name = name;
    }

    @Override String name() {
      return name;
    }
  }

  static class SimpleClause extends AbstractClause {
    private final String op;

    private final Object value;

    SimpleClause(String name, String op, Object value) {
      super(name);
      this.op = op;
      this.value = value;
    }

    @Override void appendTo(StringBuilder sb, List<Object> variables) {
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

  static class InClause extends AbstractClause {
    private final List<Object> values;

    InClause(String name, List<Object> values) {
      super(name);
      this.values = values;
      if (values == null) {
        throw new IllegalArgumentException("Missing values for IN clause");
      }
    }

    @Override void appendTo(StringBuilder sb, List<Object> variables) {
      if (values.size() == 1 && values.get(0) instanceof BindMarker) {
        Utils.appendName(name, sb).append(" IN ").append(values.get(0));
        return;
      }
      Utils.appendName(name, sb).append(" IN (");
      Utils.joinAndAppendValues(sb, ",", values, variables).append(')');
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

  static class CompoundClause extends Clause {
    private String op;

    private final List<String> names;

    private final List<Object> values;

    CompoundClause(List<String> names, String op, List<Object> values) {
      assert names.size() == values.size();
      this.op = op;
      this.names = names;
      this.values = values;
    }

    @Override String name() {
      return null;
    }

    @Override Object firstValue() {
      return null;
    }

    @Override boolean containsBindMarker() {
      for (int i = 0; i < values.size(); i++) {
        if (Utils.containsBindMarker(values.get(i))) {
          return true;
        }
      }
      return false;
    }

    @Override void appendTo(StringBuilder sb, List<Object> variables) {
      sb.append("(");
      for (int i = 0; i < names.size(); i++) {
        if (i > 0) {
          sb.append(",");
        }
        Utils.appendName(names.get(i), sb);
      }
      sb.append(")").append(op).append("(");
      for (int i = 0; i < values.size(); i++) {
        if (i > 0) {
          sb.append(",");
        }
        Utils.appendValue(values.get(i), sb, variables);
      }
      sb.append(")");
    }
  }
}