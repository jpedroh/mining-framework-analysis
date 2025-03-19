package com.cronutils.descriptor;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import com.cronutils.Function;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.Between;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.field.expression.On;
import com.cronutils.model.field.value.IntegerFieldValue;
import com.cronutils.utils.Preconditions;

/**
 * Strategy to provide a human readable description to hh:mm:ss variations
 */
class TimeDescriptionStrategy extends DescriptionStrategy {
  private FieldExpression hours;

  private FieldExpression minutes;

  private FieldExpression seconds;

  private Set<Function<TimeFields, String>> descriptions;

  private int defaultSeconds = 0;

  /**
     * Constructor
     * @param bundle - locale considered when creating the description
     * @param hours - CronFieldExpression for hours. If no instance is provided, an Always instance is created.
     * @param minutes - CronFieldExpression for minutes. If no instance is provided, an Always instance is created.
     * @param seconds - CronFieldExpression for seconds. If no instance is provided, an On instance is created.
     */
  TimeDescriptionStrategy(ResourceBundle bundle, FieldExpression hours, FieldExpression minutes, FieldExpression seconds) {
    super(bundle);
    this.hours = ensureInstance(hours, new Always());
    this.minutes = ensureInstance(minutes, new Always());
    this.seconds = ensureInstance(seconds, new On(new IntegerFieldValue(defaultSeconds)));
    descriptions = new HashSet<>();
    registerFunctions();
  }

  /**
     * Give an expression instance, will return it if is not null. Otherwise will return the defaultExpression;
     * @param expression - CronFieldExpression instance; may be null
     * @param defaultExpression - CronFieldExpression, never null;
     * @return
     */
  private FieldExpression ensureInstance(FieldExpression expression, FieldExpression defaultExpression) {
    Preconditions.checkNotNull(defaultExpression, "Default expression must not be null");
    if (expression != null) {
      return expression;
    } else {
      return defaultExpression;
    }
  }

  @Override public String describe() {
    TimeFields fields = new TimeFields(hours, minutes, seconds);
    for (Function<TimeFields, String> function : descriptions) {
      if (!"".equals(function.apply(fields))) {
        return function.apply(fields);
      }
    }
    String secondsDesc = "";
    String minutesDesc = "";
    String hoursDesc = addTimeExpressions(describe(hours), bundle.getString("hour"), bundle.getString("hours"));
    if (!(seconds instanceof On && isDefault((On) seconds))) {
      secondsDesc = addTimeExpressions(describe(seconds), bundle.getString("second"), bundle.getString("seconds"));
    }
    if (!(minutes instanceof On && isDefault((On) minutes))) {
      minutesDesc = addTimeExpressions(describe(minutes), bundle.getString("minute"), bundle.getString("minutes"));
    }
    return String.format("%s %s %s", secondsDesc, minutesDesc, hoursDesc);
  }

  private String addTimeExpressions(String description, String singular, String plural) {
    return description.replaceAll("%s", singular).replaceAll("%p", plural);
  }

  /**
     * Registers functions that map TimeFields to a human readable description.
     */
  private void registerFunctions() {
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof Always && timeFields.minutes instanceof Always) {
        if (timeFields.seconds instanceof Always) {
          return String.format("%s %s ", bundle.getString("every"), bundle.getString("second"));
        }
        if (timeFields.seconds instanceof On) {
          if (isDefault((On) timeFields.seconds)) {
            return String.format("%s %s ", bundle.getString("every"), bundle.getString("minute"));
          } else {
            return String.format("%s %s %s %s %02d", bundle.getString("every"), bundle.getString("minute"), bundle.getString("at"), bundle.getString("second"), ((On) timeFields.seconds).getTime().getValue());
          }
        }
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof Always && timeFields.minutes instanceof On && timeFields.seconds instanceof On) {
        if (isDefault((On) timeFields.seconds)) {
          if (isDefault((On) timeFields.minutes)) {
            return String.format("%s %s ", bundle.getString("every"), bundle.getString("hour"));
          }
          return String.format("%s %s %s %s %s", bundle.getString("every"), bundle.getString("hour"), bundle.getString("at"), bundle.getString("minute"), ((On) timeFields.minutes).getTime().getValue());
        } else {
          return String.format("%s %s %s %s %s %s %s %s", bundle.getString("every"), bundle.getString("hour"), bundle.getString("at"), bundle.getString("minute"), ((On) timeFields.minutes).getTime().getValue(), bundle.getString("and"), bundle.getString("second"), ((On) timeFields.seconds).getTime().getValue());
        }
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof On && timeFields.minutes instanceof On && timeFields.seconds instanceof Always) {
        return String.format("%s %s %s %02d:%02d", bundle.getString("every"), bundle.getString("second"), bundle.getString("at"), ((On) hours).getTime().getValue(), ((On) minutes).getTime().getValue());
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof On && timeFields.minutes instanceof On && timeFields.seconds instanceof On) {
        if (isDefault((On) timeFields.seconds)) {
          return String.format("%s %02d:%02d", bundle.getString("at"), ((On) hours).getTime().getValue(), ((On) minutes).getTime().getValue());
        } else {
          return String.format("%s %02d:%02d:%02d", bundle.getString("at"), ((On) hours).getTime().getValue(), ((On) minutes).getTime().getValue(), ((On) seconds).getTime().getValue());
        }
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof On && timeFields.minutes instanceof Always && timeFields.seconds instanceof Always) {
        return String.format("%s %02d:00", bundle.getString("at"), ((On) hours).getTime().getValue());
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof On && timeFields.minutes instanceof Between) {
        if (timeFields.seconds instanceof On) {
          return String.format("%s %s %s %02d:%02d %s %02d:%02d", bundle.getString("every"), bundle.getString("minute"), bundle.getString("between"), ((On) timeFields.hours).getTime().getValue(), ((Between) timeFields.minutes).getFrom().getValue(), bundle.getString("and"), ((On) timeFields.hours).getTime().getValue(), ((Between) timeFields.minutes).getTo().getValue());
        }
        if (timeFields.seconds instanceof Always) {
          return String.format("%s %s %s %02d:%02d %s %02d:%02d", bundle.getString("every"), bundle.getString("second"), bundle.getString("between"), ((On) timeFields.hours).getTime().getValue(), ((Between) timeFields.minutes).getFrom().getValue(), bundle.getString("and"), ((On) timeFields.hours).getTime().getValue(), ((Between) timeFields.minutes).getTo().getValue());
        }
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof Always && timeFields.minutes instanceof Every && timeFields.seconds instanceof On) {
        Every minute = (Every) timeFields.minutes;
        String desc;
        if (minute.getPeriod().getValue() == 1 && isDefault((On) timeFields.seconds)) {
          desc = String.format("%s %s", bundle.getString("every"), bundle.getString("minute"));
        } else {
          desc = String.format("%s %s %s ", bundle.getString("every"), minute.getPeriod().getValue(), bundle.getString("minutes"));
        }
        if (minute.getExpression() instanceof Between) {
          return "";
        }
        return desc;
      }
      return "";
    });
    descriptions.add((timeFields) -> {
      if (timeFields.hours instanceof Every && timeFields.minutes instanceof On && timeFields.seconds instanceof On) {
        if (((On) timeFields.minutes).getTime().getValue() == 0 && ((On) timeFields.seconds).getTime().getValue() == 0) {
          Integer period = ((Every) timeFields.hours).getPeriod().getValue();
          if (period == null || period == 1) {
            return String.format("%s %s", bundle.getString("every"), bundle.getString("hour"));
          }
        }
        String result = String.format("%s %s %s %s %s %s ", bundle.getString("every"), ((Every) hours).getPeriod().getValue(), bundle.getString("hours"), bundle.getString("at"), bundle.getString("minute"), ((On) minutes).getTime().getValue());
        if (isDefault((On) timeFields.seconds)) {
          return result;
        } else {
          return String.format("%s %s %s", bundle.getString("and"), bundle.getString("second"), ((On) seconds).getTime().getValue());
        }
      }
      return "";
    });
  }

  class TimeFields {
    public FieldExpression seconds;

    public FieldExpression minutes;

    public FieldExpression hours;

    public TimeFields(FieldExpression hours, FieldExpression minutes, FieldExpression seconds) {
      this.hours = hours;
      this.minutes = minutes;
      this.seconds = seconds;
    }
  }

  /**
     * Checks if On instance has a default value.
     * @param on - On instance
     * @return boolean - true if time value matches a default; false otherwise.
     */
  private boolean isDefault(On on) {
    return on.getTime().getValue() == defaultSeconds;
  }
}