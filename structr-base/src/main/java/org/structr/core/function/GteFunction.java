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
package org.structr.core.function;

import org.structr.common.error.FrameworkException;
import org.structr.schema.action.ActionContext;

public class GteFunction extends CoreFunction {

	public static final String ERROR_MESSAGE_GTE = "Usage: ${gte(value1, value2)}. Example: ${if(gte(this.children, 2), \"Equal to or more than two\", \"Less than two\")}";

	@Override
	public String getName() {
		return "gte";
	}

	@Override
	public String getSignature() {
		return "value1, value2";
	}

	@Override
	public Object apply(final ActionContext ctx, final Object caller, final Object[] sources) throws FrameworkException {

		return gte(sources[0], sources[1]);
	}

	@Override
	public String usage(boolean inJavaScriptContext) {
		return ERROR_MESSAGE_GTE;
	}

	@Override
	public String shortDescription() {
		return "Returns true if the first argument is greater or equal to the second argument";
	}
}
