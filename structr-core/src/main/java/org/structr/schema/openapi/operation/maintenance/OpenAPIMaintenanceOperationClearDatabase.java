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
package org.structr.schema.openapi.operation.maintenance;

import org.structr.schema.openapi.common.OpenAPISchemaReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.structr.schema.openapi.operation.OpenAPIOperation;

public class OpenAPIMaintenanceOperationClearDatabase extends LinkedHashMap<String, Object> {

    public OpenAPIMaintenanceOperationClearDatabase() {

        final Map<String, Object> operations = new LinkedHashMap<>();

        put("/maintenance/clearDatabase", operations);

        operations.put("post", new OpenAPIOperation(

                // summary
                "Clears the database",

                // description
                "Clears the database, i.e. removes all nodes and relationships and restores the initial schema as if it were an empty Structr instance.",

                // operation ID
                "clearDatabase",

                // tags
                Set.of("Maintenance commands (admin only)"),

                // parameters
                null,

                // request body
		null,

                // responses
                Map.of(
                        "200", new OpenAPISchemaReference("#/components/responses/ok"),
                        "401", new OpenAPISchemaReference("#/components/responses/unauthorized")
                )
        ));

    }
}
