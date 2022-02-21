/*
 * Copyright (C) 2010-2021 Structr GmbH
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
package org.structr.schema.openapi.operation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.structr.schema.export.StructrMethodDefinition;
import org.structr.schema.export.StructrTypeDefinition;
import org.structr.schema.openapi.common.OpenAPIAnyOf;
import org.structr.schema.openapi.common.OpenAPISchemaReference;
import org.structr.schema.openapi.parameter.OpenAPIPathParameter;

public class OpenAPIStaticMethodOperation extends OpenAPIOperation {

	public OpenAPIStaticMethodOperation(final StructrMethodDefinition method, final StructrTypeDefinition parentType) {
		super(
			// summary
			StringUtils.isBlank(method.getSummary()) ? "Executes the static method " + method.getName() + "()." : method.getSummary(),

			// description
			StringUtils.isBlank(method.getDescription()) ? "Executes the static method " + method.getName() + "()." : method.getDescription(),

			// operationId
			"execute" + method.getParent().getName() + "." + method.getName(),

			// tags
			Set.of(method.getParent().getName()),

			null,

			// request body
			method.getOpenAPIRequestBody(),

			// responses
			Map.of(
				"200", new OpenAPISchemaReference("#/components/responses/" + parentType.getName() + "." + method.getName() + "MethodResponse"),
				"401", new OpenAPISchemaReference("#/components/responses/unauthorized"),
				"422", new OpenAPISchemaReference("#/components/responses/validationError")
			)
		);
	}

	public OpenAPIStaticMethodOperation(final StructrMethodDefinition method, final StructrTypeDefinition parentType, Set<String> viewNames) {
		super(
				// summary
				StringUtils.isBlank(method.getSummary()) ? "Executes the static method " + method.getName() + "()." : method.getSummary(),

				// description
				StringUtils.isBlank(method.getDescription()) ? "Executes the static method " + method.getName() + "()." : method.getDescription(),

				// operationId
				"execute" + method.getParent().getName() + "." + method.getName(),

				// tags
				Set.of(method.getParent().getName()),

				List.of(
						new OpenAPIPathParameter("view", "Changes the response schema to the selected views schema", Map.of("type", "string", "enum", viewNames), true)
				),

				// request body
				method.getOpenAPIRequestBody(),

				// responses
				Map.of(
						"200", new OpenAPISchemaReference("#/components/responses/" + parentType.getName() + "." + method.getName() + "MethodResponse"),
						"401", new OpenAPISchemaReference("#/components/responses/unauthorized"),
						"422", new OpenAPISchemaReference("#/components/responses/validationError")
				)
		);
	}
}
