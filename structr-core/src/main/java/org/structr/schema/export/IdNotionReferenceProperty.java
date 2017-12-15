/**
 * Copyright (C) 2010-2017 Structr GmbH
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
package org.structr.schema.export;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.App;
import org.structr.core.entity.AbstractSchemaNode;
import org.structr.core.entity.SchemaProperty;
import org.structr.schema.SchemaHelper;
import org.structr.schema.json.JsonSchema;
import org.structr.schema.json.JsonType;

/**
 *
 *
 */
class IdNotionReferenceProperty extends StructrReferenceProperty {

	private String referenceName = null;
	private String reference     = null;
	private String type          = null;

	IdNotionReferenceProperty(final JsonType parent, final String name, final String reference, final String type, final String referenceName) {

		super(parent, name);

		this.referenceName = referenceName;
		this.reference     = reference;
		this.type          = type;
	}

	@Override
	public URI getId() {
		return null;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	SchemaProperty createDatabaseSchema(final App app, final AbstractSchemaNode schemaNode) throws FrameworkException {

		final SchemaProperty property = super.createDatabaseSchema(app, schemaNode);

		property.setProperty(SchemaProperty.format, referenceName + ", " + StringUtils.join(properties, ", "));
		property.setProperty(SchemaProperty.propertyType, SchemaHelper.Type.IdNotion.name());

		return property;
	}

	@Override
	void deserialize(final Map<String, Object> source) {

		super.deserialize(source);

		final String type = (String)source.get(JsonSchema.KEY_TYPE);
		switch (type) {

			case "object":
				reference = (String)source.get(JsonSchema.KEY_REFERENCE);
				break;

			case "array":
				final Object itemsValue = source.get(JsonSchema.KEY_ITEMS);
				if (itemsValue != null && itemsValue instanceof Map) {

					final Map<String, Object> items = (Map)itemsValue;
					reference = (String)items.get(JsonSchema.KEY_REFERENCE);
				}
				break;
		}
	}

	@Override
	void deserialize(final SchemaProperty schemaProperty) {
		super.deserialize(schemaProperty);
	}

	@Override
	Map<String, Object> serialize() {

		final Map<String, Object> map = super.serialize();
		final String type             = getType();

		switch (type) {

			case "object":
				map.put(JsonSchema.KEY_REFERENCE, reference);
				break;

			case "array":
				final Map<String, Object> items = new TreeMap<>();
				map.put(JsonSchema.KEY_ITEMS, items);
				items.put(JsonSchema.KEY_REFERENCE, reference);
				break;
		}

		return map;
	}
}
