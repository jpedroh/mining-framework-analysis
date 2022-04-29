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
package org.structr.memgraph;

import java.util.LinkedHashMap;
import java.util.Map;
import org.structr.api.NativeQuery;

/**
 */
public abstract class AbstractNativeQuery<T> implements NativeQuery<T> {

	protected final Map<String, Object> parameters = new LinkedHashMap<>();
	protected String query                         = null;

	public AbstractNativeQuery(final String query) {
		this.query = query;
	}

	abstract T execute(final SessionTransaction tx);

	public String getQuery() {
		return query;
	}

	@Override
	public void configure(final Map<String, Object> config) {

		if (config != null) {
			parameters.putAll(config);
		}
	}
}
