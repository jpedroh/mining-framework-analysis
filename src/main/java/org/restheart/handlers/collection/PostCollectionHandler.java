/*
 * RESTHeart - the data REST API server
 * Copyright (C) 2014 - 2015 SoftInstigate Srl
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.restheart.handlers.collection;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;
import org.restheart.db.DocumentDAO;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.RequestHelper;
import org.restheart.utils.ResponseHelper;


/**
 *
 * @author Andrea Di Cesare
 */
public class PostCollectionHandler extends PutCollectionHandler {
    /**
     * Creates a new instance of PostCollectionHandler
     */
    public PostCollectionHandler() {
    }

    /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
    @Override
    public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
        DBObject content = context.getContent();
        if (content == null) {
            content = new BasicDBObject();
        }
        // cannot POST an array
        if (content instanceof BasicDBList) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "data cannot be an array");
            return;
        }
        ObjectId etag = RequestHelper.getWriteEtag(exchange);
        if (((content.get("_id") != null) && (content.get("_id") instanceof String)) && RequestContext.isReservedResourceDocument(((String) (content.get("_id"))))) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_FORBIDDEN, "reserved resource");
            return;
        }
        int httpCode = new DocumentDAO().upsertDocumentPost(exchange, context.getDBName(), context.getCollectionName(), content, etag);
        // send the warnings if any (and in case no_content change the return code to ok
        if ((context.getWarnings() != null) && (!context.getWarnings().isEmpty())) {
            sendWarnings(httpCode, exchange, context);
        } else {
            exchange.setResponseCode(httpCode);
        }
        exchange.endExchange();
    }
}