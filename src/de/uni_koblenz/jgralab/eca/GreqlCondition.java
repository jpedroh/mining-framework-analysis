package de.uni_koblenz.jgralab.eca;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.eca.events.Event;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;

public class GreqlCondition implements Condition {
  /**
	 * Condition as GReQuL Query
	 */
  private String conditionExpression;

  /**
	 * Creates a Condition with the given GReQuL Query as condition Expression
	 * 
	 * @param conditionExpression
	 *            condition as GReQuL Query
	 */
  public GreqlCondition(String conditionExpression) {
    this.conditionExpression = conditionExpression;
  }

  /**
	 * Evaluates the condition
	 * 
	 * @param event
	 *            an Event containing the element to check the condition for
	 * @return if the condition is evaluated to true
	 */
  @Override public boolean evaluate(Event event) {
    AttributedElement element = event.getElement();
    GreqlEvaluator greqlEvaluator = ((ECARuleManager) event.getGraph().getECARuleManager()).getGreqlEvaluator();
    if (conditionExpression.contains("context")) {
      greqlEvaluator.setQuery("using context: " + conditionExpression);
      greqlEvaluator.setVariable("context", element);
    } else {
      greqlEvaluator.setQuery(conditionExpression);
    }
    greqlEvaluator.startEvaluation();
    return (Boolean) greqlEvaluator.getResult();

<<<<<<< /usr/src/app/output/jgralab/jgralab/ba28dc3943bf31e37f3beb96450dac330ebebd47/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/left.java
    if (result.isBoolean()) {
      return result.toBoolean();
    } else {
      System.err.println("Invalid Condition: " + conditionExpression);
      throw new ECAException("Invalid Condition: \"" + conditionExpression + "\" evaluates to JValueType " + result.getType() + " but the result has to be a boolean.");
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }

  /**
	 * @return the conditionExpression
	 */
  public String getConditionExpression() {
    return conditionExpression;
  }

  @Override public String toString() {
    return "Condition: " + conditionExpression;
  }
}