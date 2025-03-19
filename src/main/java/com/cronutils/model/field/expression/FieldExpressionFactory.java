package com.cronutils.model.field.expression;
import java.util.List;
import com.cronutils.model.field.value.IntegerFieldValue;
import com.cronutils.model.field.value.SpecialChar;
import com.cronutils.model.field.value.SpecialCharFieldValue;

public class FieldExpressionFactory {
  private FieldExpressionFactory() {
  }

  public static Always always() {
    return Always.INSTANCE;
  }

  public static Between between(int from, int to) {
    return new Between(new IntegerFieldValue(from), new IntegerFieldValue(to));
  }

  public static Between between(SpecialChar from, int to) {
    return new Between(new SpecialCharFieldValue(from), new IntegerFieldValue(to));
  }

  public static Every every(int time) {
    return new Every(new IntegerFieldValue(time));
  }

  public static Every every(FieldExpression expression, int time) {
    return new Every(expression, new IntegerFieldValue(time));
  }

  public static On on(SpecialChar specialChar) {
    return new On(new SpecialCharFieldValue(specialChar));
  }

  public static On on(int time) {
    return new On(new IntegerFieldValue(time));
  }

  public static On on(int time, SpecialChar specialChar) {
    return new On(new IntegerFieldValue(time), new SpecialCharFieldValue(specialChar));
  }

  public static On on(int time, SpecialChar specialChar, int nth) {
    return new On(new IntegerFieldValue(time), new SpecialCharFieldValue(specialChar), new IntegerFieldValue(nth));
  }

  public static QuestionMark questionMark() {
    return QuestionMark.INSTANCE;
  }

  public static And and(List<FieldExpression> expressions) {
    And and = new And();
    for (FieldExpression expression : expressions) {
      and.and(expression);
    }
    return and;
  }
}