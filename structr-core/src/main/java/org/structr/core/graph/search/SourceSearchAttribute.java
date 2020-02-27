/**
 * Copyright (C) 2010-2020 Structr GmbH
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
package org.structr.core.graph.search;

import org.structr.api.search.Occurrence;
import org.structr.core.GraphObject;

/**
 *
 *
 */
public class SourceSearchAttribute<T> extends SearchAttribute<T> {

	public SourceSearchAttribute(Occurrence occur) {
		super(occur);
	}

	@Override
	public String toString() {
		return "SourceSearchAttribute()";
	}

	@Override
	public boolean isExactMatch() {
		return true;
	}

	@Override
	public boolean includeInResult(GraphObject entity) {
		return true;
	}

	@Override
	public Class getQueryType() {
		return null;
	}
}
