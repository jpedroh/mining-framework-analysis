/*
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
package org.structr.core.property;

import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.structr.api.Predicate;
import org.structr.api.search.SortType;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.common.error.ReadOnlyPropertyToken;
import org.structr.core.GraphObject;
import org.structr.core.converter.PropertyConverter;

/**
* A property that returns a constant Boolean value.
 */
public class ConstantBooleanProperty extends AbstractPrimitiveProperty<Boolean>	 {

	private boolean constantValue;

	public ConstantBooleanProperty(final String name, final boolean constantValue) {

		super(name);
		systemInternal();
		readOnly();

		this.constantValue = constantValue;
	}

	public ConstantBooleanProperty(final String jsonName, final String dbName, final boolean constantValue) {
		super(jsonName, dbName);
	}

	@Override
	public Boolean getProperty(final SecurityContext securityContext, final GraphObject obj, final boolean applyConverter) {
		return getProperty(securityContext, obj, applyConverter, null);
	}

	@Override
	public Boolean getProperty(final SecurityContext securityContext, final GraphObject obj, final boolean applyConverter, final Predicate<GraphObject> predicate) {

		if (declaringClass.isAssignableFrom(obj.getClass())) {
			return this.constantValue;
		}

		return false; // null = false
	}

	@Override
	public Object setProperty(final SecurityContext securityContext, final GraphObject obj, final Boolean value) throws FrameworkException {
		throw new FrameworkException(422, "Unable to change value of a constant property", new ReadOnlyPropertyToken(obj.getType(), this));
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isSystemInternal() {
		return true;
	}

	@Override
	public boolean isIndexed() {
		return false;
	}

	@Override
	public Object fixDatabaseProperty(Object value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String typeName() {
		return "Boolean";
	}

	@Override
	public Class valueType() {
		return Boolean.class;
	}

	@Override
	public PropertyConverter<Boolean, ?> databaseConverter(SecurityContext securityContext) {
		return null;
	}

	@Override
	public PropertyConverter<Boolean, ?> databaseConverter(SecurityContext securityContext, GraphObject entity) {
		return null;
	}

	@Override
	public PropertyConverter<?, Boolean> inputConverter(SecurityContext securityContext) {
		return null;
	}

	@Override
	public SortType getSortType() {
		return SortType.Default;
	}

	// ----- CMIS support -----
	@Override
	public PropertyType getDataType() {
		return PropertyType.BOOLEAN;
	}

	// ----- OpenAPI -----
	@Override
	public Object getExampleValue(final String type, final String viewName) {
		return constantValue;
	}
}
