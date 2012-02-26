/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         https://github.com/jgralab/jgralab
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.eca.events;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.eca.ECAException;
import de.uni_koblenz.jgralab.eca.ECARule;
import de.uni_koblenz.jgralab.schema.EdgeClass;

public class DeleteEdgeEventDescription extends EventDescription<EdgeClass> {

	/**
	 * Creates an DeleteEdgeEventDescription with the given parameters
	 * 
	 * @param time
	 *            the EventTime, BEFORE or AFTER
	 * @param type
	 *            the Class of elements, this Event monitors
	 */
	public DeleteEdgeEventDescription(EventTime time, EdgeClass type) {
		super(time, type);
	}

	/**
	 * Creates an DeleteEdgeEventDescription with the given parameters
	 * 
	 * @param time
	 *            the EventTime, BEFORE or AFTER
	 * @param contextExpr
	 *            the contextExpression to get the context
	 */
	public DeleteEdgeEventDescription(EventTime time, String contextExpr) {
		super(time, contextExpr);
		if (time.equals(EventTime.AFTER)) {
			throw new ECAException(
					"Event \"after delete Edge\" can not match a context expression"
							+ " because there is no element.");
		}
	}

	// -------------------------------------------------------------------

	/**
	 * Triggers the rules if this EventDescription matches the Event
	 * 
	 * @param element
	 *            the to be deleted Edge
	 */
	public void fire(Edge element) {
		if (super.checkContext(element)) {
			int nested = getActiveECARules().get(0).getECARuleManager()
					.getNestedTriggerCalls();
			Graph graph = getActiveECARules().get(0).getECARuleManager()
					.getGraph();
			for (ECARule<EdgeClass> rule : activeRules) {
				rule.trigger(new DeleteEdgeEvent(nested, graph, element));
			}
		}
	}

	/**
	 * Triggers the rule if this EventDescription matches the Event
	 * 
	 * @param type
	 *            the type of the Edge that is deleted
	 * @param oldAlpha
	 * 				the old alpha {@link Vertex} of the deleted {@link Edge}
	 * @param oldOmega
	 * 				the old omega {@link Vertex} of the deleted {@link Edge}
	 */
	public void fire(EdgeClass type, Vertex oldAlpha, Vertex oldOmega) {
		if (super.checkContext(type)) {
			int nested = getActiveECARules().get(0).getECARuleManager()
					.getNestedTriggerCalls();
			Graph graph = getActiveECARules().get(0).getECARuleManager()
					.getGraph();
			for (ECARule<EdgeClass> rule : activeRules) {
				rule.trigger(new DeleteEdgeEvent(nested, graph, type,oldAlpha,oldOmega));
			}
		}
	}

}
