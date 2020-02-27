/**
 * Copyright (C) 2010-2020 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.websocket.command;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.Query;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.AbstractNode;
import org.structr.core.property.PropertyKey;
import org.structr.schema.SchemaHelper;
import org.structr.web.entity.File;
import org.structr.web.entity.Folder;
import org.structr.web.entity.Image;
import org.structr.websocket.StructrWebSocket;
import org.structr.websocket.message.MessageBuilder;
import org.structr.websocket.message.WebSocketMessage;

/**
 * Websocket command to retrieve nodes of a given type which are on root level,
 * i.e. not children of another node.
 *
 * To get all nodes of a certain type, see the {@link GetCommand}.
 *
 */
public class ListCommand extends AbstractCommand {

	private static final Logger logger = LoggerFactory.getLogger(ListCommand.class.getName());

	static {

		StructrWebSocket.addCommand(ListCommand.class);
	}

	@Override
	public void processMessage(final WebSocketMessage webSocketData) throws FrameworkException {

		setDoTransactionNotifications(false);

		final SecurityContext securityContext = getWebSocket().getSecurityContext();

		final String rawType                  = webSocketData.getNodeDataStringValue("type");
		final String properties               = webSocketData.getNodeDataStringValue("properties");
		final boolean rootOnly                = webSocketData.getNodeDataBooleanValue("rootOnly");

		Class type = SchemaHelper.getEntityClassForRawType(rawType);

		if (type == null) {
			getWebSocket().send(MessageBuilder.status().code(404).message("Type " + rawType + " not found").build(), true);
			return;
		}

		if (properties != null) {
			securityContext.setCustomView(StringUtils.split(properties, ","));
		}

		final String sortOrder         = webSocketData.getSortOrder();
		final String sortKey           = webSocketData.getSortKey();
		final int pageSize             = webSocketData.getPageSize();
		final int page                 = webSocketData.getPage();
		final PropertyKey sortProperty = StructrApp.key(type, sortKey);
		final Query query              = StructrApp.getInstance(securityContext).nodeQuery(type).sort(sortProperty, "desc".equals(sortOrder)).page(page).pageSize(pageSize);

		if (File.class.isAssignableFrom(type)) {

			if (rootOnly) {
				query.and(StructrApp.key(File.class, "hasParent"), false);
			}

			// inverted as isThumbnail is not necessarily present in all objects inheriting from FileBase
			query.not().and(StructrApp.key(Image.class, "isThumbnail"), true);

		}

		// important
		if (Folder.class.isAssignableFrom(type) && rootOnly) {

			query.and(StructrApp.key(Folder.class, "hasParent"), false);
		}

		try {

			// do search
			final List<AbstractNode> result = query.getAsList();

			// set full result list
			webSocketData.setResult(result);

			// send only over local connection
			getWebSocket().send(webSocketData, true);

		} catch (FrameworkException fex) {

			logger.warn("Exception occured", fex);
			getWebSocket().send(MessageBuilder.status().code(fex.getStatus()).message(fex.getMessage()).build(), true);

		}
	}

	@Override
	public String getCommand() {
		return "LIST";
	}
}
