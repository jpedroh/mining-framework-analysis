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
package org.restheart.handlers.indexes;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.undertow.server.HttpServerExchange;
import org.restheart.db.IndexDAO;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.handlers.document.DocumentRepresentationFactory;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.ResponseHelper;


/**
 *
 * @author Andrea Di Cesare
 */
public class PutIndexHandler extends PipedHttpHandler {
    private static final BasicDBObject fieldsToReturn;

    static {
        fieldsToReturn = new BasicDBObject();
        fieldsToReturn.put("_id", 1);
        fieldsToReturn.put("_created_on", 1);
    }

    /**
     * Creates a new instance of PutIndexHandler
     */
    public PutIndexHandler() {
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
        String db = context.getDBName();
        String co = context.getCollectionName();
        String id = context.getIndexId();
        if (id.startsWith("_")) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "index name cannot start with _");
            return;
        }
        DBObject content = context.getContent();
        DBObject keys = ((DBObject) (content.get("keys")));
        DBObject ops = ((DBObject) (content.get("ops")));
        if (keys == null) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "wrong request, content must include 'keys' object", null);
            return;
        }
        if (ops == null) {
            ops = new BasicDBObject();
        }
        ops.put("name", id);
        try {
            final IndexDAO indexDAO = new IndexDAO();
            indexDAO.createIndex(db, co, keys, ops);
        } catch (java.lang.Throwable t) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_ACCEPTABLE, "error creating the index", t);
            return;
        }
        exchange.setResponseCode(HttpStatus.SC_CREATED);
        // send the warnings if any
        if ((context.getWarnings() != null) && (!context.getWarnings().isEmpty())) {
            DocumentRepresentationFactory.sendDocument(exchange.getRequestPath(), exchange, context, new BasicDBObject());
        }
        exchange.endExchange();
    }
}