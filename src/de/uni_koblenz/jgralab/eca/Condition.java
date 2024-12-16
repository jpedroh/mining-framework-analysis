package de.uni_koblenz.jgralab.eca;

import de.uni_koblenz.jgralab.eca.events.Event;
import de.uni_koblenz.jgralab.greql2.types.Types;


public interface Condition {
	/**
	 * Evaluates the condition
	 * 
	 * @param event
	 *            an Event containing the element to check the condition for
	 * @return if the condition is evaluated to true
	 */
	public abstract boolean evaluate(Event event);
}