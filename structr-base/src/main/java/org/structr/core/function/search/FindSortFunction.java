/*
 * Copyright (C) 2010-2023 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.function.search;

import org.structr.common.error.FrameworkException;
import org.structr.core.function.AdvancedScriptingFunction;
import org.structr.schema.action.ActionContext;

public class FindSortFunction extends AdvancedScriptingFunction {

	public static final String ERROR_MESSAGE_SORT = "Usage: ${sort(key [, descending]). Example: ${find('Group', sort('name'))}";

	@Override
	public String getName() {
		return "find.sort";
	}

	@Override
	public Object apply(final ActionContext ctx, final Object caller, final Object[] sources) throws FrameworkException {

		// use String here because the actual type of the query is not known yet
		String sortKey         = "name";
		boolean sortDescending = false;

		try {

			assertArrayHasMinLengthAndAllElementsNotNull(sources, 1);

			switch (sources.length) {

				case 2: sortDescending = "true".equals(sources[1].toString().toLowerCase()); // no break here
				case 1: sortKey        = sources[0].toString();
			}

			if (sortKey.contains(".")) {

				return new SortPathPredicate(sortKey, sortDescending);
			}

			return new SortPredicate(sortKey, sortDescending);

		} catch (final IllegalArgumentException e) {

			logParameterError(caller, sources, ctx.isJavaScriptContext());

			return usage(ctx.isJavaScriptContext());
		}
	}

	@Override
	public String usage(boolean inJavaScriptContext) {
		return ERROR_MESSAGE_SORT;
	}

	@Override
	public String shortDescription() {
		return "Returns a query predicate that can be used with find() or search().";
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public String getSignature() {
		return null;
	}
}
