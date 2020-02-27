/**
 * Copyright (C) 2010-2020 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.flow.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.flow.api.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.structr.api.util.Iterables;
import org.structr.core.GraphObject;
import org.structr.flow.impl.FlowBaseNode;
import org.structr.flow.impl.FlowContainer;
import org.structr.flow.impl.FlowExceptionHandler;

public class FlowEngine {
	private final Map<FlowType, FlowHandler> handlers 	= new EnumMap<>(FlowType.class);
	private Context context                           	= null;

	public FlowEngine() {
		this((GraphObject)null);
	}

	public FlowEngine(final GraphObject thisObject) {
		this(new Context(thisObject));
	}

	public FlowEngine(final Context context) {

		init();

		this.context = context;
	}

	public FlowResult execute(final FlowElement step) {
		return this.execute(this.context,step);
	}

	public FlowResult execute(final Context context, final FlowElement step) {

		FlowElement current = step;

		while (current != null) {

			final FlowHandler handler = handlers.get(current.getFlowType());
			if (handler != null) {

				FlowElement next = null;

				try {

					next = handler.handle(context, current);

				} catch (FlowException ex) {

					return handleException(context, ex, current);

				}

				if (next != null) {

					if (next.equals(current)) {

						context.error(new FlowError("FlowElement is connected to itself. Cancelling execution to prevent unlimited recursion."));

					}

				}

				current = next;

			} else {

				System.out.println("No handler registered for type " + current.getFlowType() + ", aborting.");

			}

			// check for return or error values and break early
			if (context.hasResult() || context.hasError()) {
				return new FlowResult(context);
			}
		}

		return new FlowResult(context);
	}

	// ----- private methods -----
	private void init() {

		handlers.put(FlowType.Action,   	new ActionHandler());
		handlers.put(FlowType.Decision, 	new DecisionHandler());
		handlers.put(FlowType.Return,   	new ReturnHandler());
		handlers.put(FlowType.ForEach,  	new ForEachHandler());
		handlers.put(FlowType.Store, 		new StoreHandler());
		handlers.put(FlowType.Aggregation,  new AggregationHandler());
		handlers.put(FlowType.Exception, 	new ExceptionHandler());
		handlers.put(FlowType.Filter,		new FilterHandler());
		handlers.put(FlowType.Fork,			new ForkHandler());
	}

	private FlowResult handleException(final Context context, final FlowException exception, final FlowElement current) {
		// Check if current element has a linked FlowExceptionHandler or if there is a global one
		if (current instanceof ThrowingElement) {

			FlowExceptionHandler exceptionHandler = ((ThrowingElement)current).getExceptionHandler(context);

			if (exceptionHandler != null) {

				context.setData(exceptionHandler.getUuid(), exception);
				return this.execute(context, exceptionHandler);

			}

		}

		// No linked FlowExceptionHandler was found, try to find an eligible global one
		final Logger logger = LoggerFactory.getLogger(FlowEngine.class);
		try {
			FlowContainer container = current.getFlowContainer();

			Iterable<FlowBaseNode> flowNodes = container.getProperty(FlowContainer.flowNodes);

			if (flowNodes != null) {

				for (FlowBaseNode node : flowNodes) {

					if (node instanceof FlowExceptionHandler) {

						final FlowExceptionHandler exceptionHandler = (FlowExceptionHandler) node;
						final List<FlowBaseNode> handledNodes = Iterables.toList(exceptionHandler.getProperty(FlowExceptionHandler.handledNodes));

						if (handledNodes == null || handledNodes.size() == 0) {

							context.setData(exceptionHandler.getUuid(), exception);
							return this.execute(context, exceptionHandler);

						}

					}

				}

			}
		} catch (NullPointerException ex) {

			logger.warn("Exception while processing FlowException.", ex);
		}

		// In case no handler is present at all, print the stack trace and return the intermediate result
		FlowContainer container = current.getFlowContainer();
		FlowBaseNode currentFlowNode = (FlowBaseNode) current;
		logger.error((container.getName() != null ? ("[" + container.getProperty(FlowContainer.effectiveName) + "]") : "") + ("([" + currentFlowNode.getType() + "]" + currentFlowNode.getUuid() + ") Exception: "), exception);
		context.error(new FlowError(exception.getMessage()));
		return new FlowResult(context);
	}
}
