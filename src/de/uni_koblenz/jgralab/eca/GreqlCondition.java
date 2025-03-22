package de.uni_koblenz.jgralab.eca;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.eca.events.Event;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;

public class GreqlCondition implements Condition {
	/**
	 * Condition as GReQuL Query
	 */
	private String conditionExpression;

	// +++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Creates a Condition with the given GReQuL Query as condition Expression
	 *
	 * @param conditionExpression
	 *            condition as GReQuL Query
	 */
	public GreqlCondition(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Evaluates the condition
	 *
	 * @param event
	 *            an Event containing the element to check the condition for
	 * @return if the condition is evaluated to true
	 */
	@Override
	public boolean evaluate(Event event) {
		AttributedElement element = event.getElement();
<<<<<<< /usr/src/app/output/jgralab/jgralab/07337134fba547de8a5275744759c83da0e62431/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/left.java
		GreqlEvaluator greqlEvaluator = ((ECARuleManager) event.getGraph()
				.getECARuleManager()).getGreqlEvaluator();
		if (conditionExpression.contains("context")) {
||||||| /usr/src/app/output/jgralab/jgralab/07337134fba547de8a5275744759c83da0e62431/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/base.java
		GreqlEvaluator greqlEvaluator = event.getGraph().getECARuleManager()
				.getGreqlEvaluator();
		if (this.conditionExpression.contains("context")) {
=======
		GreqlEvaluator greqlEvaluator = ((ECARuleManager) event.getGraph()
				.getECARuleManager()).getGreqlEvaluator();
		if (this.conditionExpression.contains("context")) {
>>>>>>> /usr/src/app/output/jgralab/jgralab/07337134fba547de8a5275744759c83da0e62431/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/right.java
			greqlEvaluator.setQuery("using context: " + conditionExpression);
			greqlEvaluator.setVariable("context", element);
		} else {
			greqlEvaluator.setQuery(conditionExpression);
		}
		greqlEvaluator.startEvaluation();
<<<<<<< /usr/src/app/output/jgralab/jgralab/07337134fba547de8a5275744759c83da0e62431/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/left.java
		JValue result = greqlEvaluator.getEvaluationResult();
		if (result.isBoolean()) {
			return result.toBoolean();
		} else {
			System.err
					.println("Invalid Condition: " + conditionExpression);
			throw new ECAException("Invalid Condition: \""
					+ conditionExpression + "\" evaluates to JValueType "
					+ result.getType() + " but the result has to be a boolean.");
		}
||||||| /usr/src/app/output/jgralab/jgralab/07337134fba547de8a5275744759c83da0e62431/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/base.java
		JValue result = greqlEvaluator.getEvaluationResult();
		if (result.isBoolean()) {
			return result.toBoolean();
		} else {
			System.err
					.println("Invalid Condition: " + this.conditionExpression);
			throw new ECAException("Invalid Condition: \""
					+ this.conditionExpression + "\" evaluates to JValueType "
					+ result.getType() + " but the result has to be a boolean.");
		}
=======
		return (Boolean) greqlEvaluator.getResult();
>>>>>>> /usr/src/app/output/jgralab/jgralab/07337134fba547de8a5275744759c83da0e62431/src/de/uni_koblenz/jgralab/eca/GreqlCondition.java/right.java
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * @return the conditionExpression
	 */
	public String getConditionExpression() {
		return conditionExpression;
	}

	@Override
	public String toString() {
		return "Condition: " + conditionExpression;
	}

}
