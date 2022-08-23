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
package org.structr.core.property;

import org.structr.api.Predicate;
import org.structr.api.search.SortType;
import org.structr.common.SecurityContext;
import org.structr.core.GraphObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * A read-only property that returns the concatenated values of two other properties.
 *
 *
 */
public class ConcatProperty extends AbstractReadOnlyProperty<String> {

	private PropertyKey<String>[] propertyKeys = null;
	private String separator = null;

	public ConcatProperty(String name, String separator, PropertyKey<String>... propertyKeys) {

		super(name);

		this.propertyKeys = propertyKeys;
		this.separator = separator;
	}

	@Override
	public String getProperty(SecurityContext securityContext, GraphObject obj, boolean applyConverter) {
		return getProperty(securityContext, obj, applyConverter, null);
	}

	@Override
	public String getProperty(SecurityContext securityContext, GraphObject obj, boolean applyConverter, final Predicate<GraphObject> predicate) {

		StringBuilder combinedPropertyValue = new StringBuilder();
		int len = propertyKeys.length;

		for(int i=0; i<len; i++) {

			combinedPropertyValue.append(obj.getProperty(propertyKeys[i]));
			if(i < len-1) {
				combinedPropertyValue.append(separator);
			}
		}

		return combinedPropertyValue.toString();
	}

	@Override
	public Class relatedType() {
		return null;
	}

	@Override
	public Class valueType() {
		return String.class;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public SortType getSortType() {
		return SortType.Default;
	}

	// ----- OpenAPI -----
	@Override
	public Object getExampleValue(final String type, final String viewName) {
		return "concatenated string";
	}

	@Override
	public Map<String, Object> describeOpenAPIOutputSchema(String type, String viewName) {
		return null;
	}

	// ----- OpenAPI -----
	@Override
	public Map<String, Object> describeOpenAPIOutputType(final String type, final String viewName, final int level) {

		final Map<String, Object> map = new TreeMap<>();
		final Class valueType         = valueType();

		if (valueType != null) {

			map.put("type",    valueType.getSimpleName().toLowerCase());
			map.put("example", getExampleValue(type, viewName));

			if (this.readOnly) {
				map.put("readOnly", true);
			}
		}

		return map;
	}

	@Override
	public Map<String, Object> describeOpenAPIInputType(final String type, final String viewName, final int level) {

		final Map<String, Object> map = new TreeMap<>();
		final Class valueType         = valueType();

		if (valueType != null) {

			map.put("type", valueType.getSimpleName().toLowerCase());
			map.put("readOnly",    true);
			map.put("example", getExampleValue(type, viewName));
		}

		return map;
	}
}
