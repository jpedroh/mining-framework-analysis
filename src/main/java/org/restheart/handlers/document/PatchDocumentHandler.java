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
package org.restheart.handlers.document;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;
import org.restheart.db.DocumentDAO;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.RequestHelper;
import org.restheart.utils.ResponseHelper;


/**
 *
 * @author Andrea Di Cesare
 */
public class PatchDocumentHandler extends PipedHttpHandler {
    /**
     * Creates a new instance of PatchDocumentHandler
     */
    public PatchDocumentHandler() {
        super(null);
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
        // cannot PATCH with no data
        if (content == null) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "data is empty");
            return;
        }
        // cannot PATCH an array
        if (content instanceof BasicDBList) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "data cannot be an array");
            return;
        }
        String id = context.getDocumentId();
        if (content.get("_id") == null) {
            content.put("_id", getId(id));
        } else if (!content.get("_id").equals(id)) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "_id in json data cannot be different than id in URL");
            return;
        }
        ObjectId etag = RequestHelper.getWriteEtag(exchange);
        if (etag == null) {
            ResponseHelper.endExchange(exchange, HttpStatus.SC_CONFLICT);
            return;
        }
        DocumentDAO documentDAO = new DocumentDAO();
        int httpCode = documentDAO.upsertDocument(context.getDBName(), context.getCollectionName(), context.getDocumentId(), content, etag, true);
        // send the warnings if any (and in case no_content change the return code to ok
        if ((context.getWarnings() != null) && (!context.getWarnings().isEmpty())) {
            sendWarnings(httpCode, exchange, context);
        } else {
            exchange.setResponseCode(httpCode);
        }
        exchange.endExchange();
    }
}