/*
 * Copyright (C) 2010-2022 Structr GmbH
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
package org.structr.schema.parser;

import org.structr.common.error.ErrorBuffer;
import org.structr.core.property.ArrayProperty;
import org.structr.schema.SchemaHelper.Type;

/**
 *
 *
 */
public class LongArrayPropertyParser extends LongPropertyParser {

	public LongArrayPropertyParser(final ErrorBuffer errorBuffer, final String className, final PropertyDefinition params) {
		super(errorBuffer, className, params);
	}

	@Override
	public String getPropertyType() {
		return ArrayProperty.class.getSimpleName().concat("<Long>");
	}

	@Override
	public String getValueType() {
		return Long[].class.getSimpleName();
	}

	@Override
	public String getUnqualifiedValueType() {
		return "LongArray";
	}

	@Override
	public String getPropertyParameters() {
		return ", Long.class";
	}

	@Override
	public Type getKey() {
		return Type.LongArray;
	}

	@Override
	public String getDefaultValue() {
		return "\"".concat(getSourceDefaultValue()).concat("\"");
	}
}
