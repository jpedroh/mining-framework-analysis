package com.cronutils.descriptor;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import com.cronutils.Function;
import com.cronutils.model.field.expression.FieldExpression;
import static com.cronutils.model.field.expression.FieldExpression.always;

/**
 * Description strategy where a cron field number can be mapped to a name.
 * Ex.: days of week or months
 */
class NominalDescriptionStrategy extends DescriptionStrategy {
  private FieldExpression expression;

  private Set<Function<FieldExpression, String>> descriptions;

  /**
     * Constructor
     * @param bundle - locale in which description should be given
     * @param nominalValueFunction - function that maps Integer to String.
     *                             The function should return "" if does not match criteria,
     *                             or the description otherwise.
     * @param expression - CronFieldExpression instance, the expression to be described.
     */
  public NominalDescriptionStrategy(ResourceBundle bundle, Function<Integer, String> nominalValueFunction, FieldExpression expression) {
    super(bundle);
    descriptions = new HashSet<>();
    if (nominalValueFunction != null) {
      this.nominalValueFunction = nominalValueFunction;
    }
    if (expression != null) {
      this.expression = expression;
    } else {
      this.expression = always();
    }
  }

  @Override public String describe() {
    for (Function<FieldExpression, String> function : descriptions) {
      if (!"".equals(function.apply(expression))) {
        return function.apply(expression);
      }
    }
    return describe(expression);
  }

  /**
     * Allows to provide a specific description to handle a CronFieldExpression instance
     *
     * @param desc - function that maps CronFieldExpression to String.
     *             The function should return "" if does not match criteria,
     *             or the description otherwise.
     * @return NominalDescriptionStrategy, this instance
     */
  public NominalDescriptionStrategy addDescription(Function<FieldExpression, String> desc) {
    descriptions.add(desc);
    return this;
  }
}