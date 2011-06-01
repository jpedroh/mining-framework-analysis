package de.uni_koblenz.jgralab.eca.events;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.eca.EventManager;

public class DeleteEdgeEvent extends Event {

	/**
	 * Creates an DeleteEdgeEvent with the given parameters
	 * 
	 * @param manager
	 *            the EventManager that manages this Event
	 * @param time
	 *            the EventTime, BEFORE or AFTER
	 * @param type
	 *            the Class of elements, this Event monitors
	 */
	public DeleteEdgeEvent(EventManager manager,EventTime time, Class <? extends AttributedElement> type) {
		super(manager,time, type);
		if(time.equals(EventTime.BEFORE)){
			manager.getBeforeDeleteEdgeEvents().add(this);
		}else{
			manager.getAfterDeleteEdgeEvents().add(this);
		}
	}

	/**
	 * Creates an DeleteEdgeEvent with the given parameters
	 * 
	 * @param manager
	 *            the EventManager that manages this Event
	 * @param time
	 *            the EventTime, BEFORE or AFTER
	 * @param contextExpr
	 *            the contextExpression to get the context
	 */
	public DeleteEdgeEvent(EventManager manager,EventTime time, String contextExpr) {
		super(manager,time, contextExpr);
		if(time.equals(EventTime.BEFORE)){
			manager.getBeforeDeleteEdgeEvents().add(this);
		}else{
			manager.getAfterDeleteEdgeEvents().add(this);
		}
	}


}
