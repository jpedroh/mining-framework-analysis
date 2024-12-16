/*  
 *  Imixs-Workflow 
 *  
 *  Copyright (C) 2001-2020 Imixs Software Solutions GmbH,  
 *  http://www.imixs.com
 *  
 *  This program is free software; you can redistribute it and/or 
 *  modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation; either version 2 
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You can receive a copy of the GNU General Public
 *  License at http://www.gnu.org/licenses/gpl.html
 *  
 *  Project: 
 *      https://www.imixs.org
 *      https://github.com/imixs/imixs-workflow
 *  
 *  Contributors:  
 *      Imixs Software Solutions GmbH - Project Management
 *      Ralph Soika - Software Developer
 */
package org.imixs.workflow.engine.adminp;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RunAs;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.engine.index.UpdateService;
import org.imixs.workflow.engine.jpa.Document;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.InvalidAccessException;
import org.imixs.workflow.exceptions.PluginException;


/**
 * JobHandler to rebuild the lucene fulltext index.
 * 
 * The job starts at 1970/01/01 and reads documents in sequence.
 * 
 * @author rsoika
 *
 */
@DeclareRoles({ "org.imixs.ACCESSLEVEL.MANAGERACCESS" })
@RunAs("org.imixs.ACCESSLEVEL.MANAGERACCESS")
@Stateless
@LocalBean
public class JobHandlerRebuildIndex implements JobHandler {
    private static final String BLOCK_SIZE_DEFAULT = "500";

    private static final String TIMEOUT_DEFAULT = "120";

    @Inject
    @ConfigProperty(name = "lucene.rebuild.block_size", defaultValue = BLOCK_SIZE_DEFAULT)
    int block_size;

    @Inject
    @ConfigProperty(name = "lucene.rebuild.time_out", defaultValue = TIMEOUT_DEFAULT)
    int time_out;

    private static final int READ_AHEAD = 32;

    public final static String ITEM_SYNCPOINT = "syncpoint";

    public final static String ITEM_SYNCDATE = "syncdate";

    public static final String SNAPSHOT_TYPE_PRAFIX = "snapshot-";

    @PersistenceContext(unitName = "org.imixs.workflow.jpa")
    private EntityManager manager;

    @Inject
    UpdateService updateService;

    private static Logger logger = Logger.getLogger(JobHandlerRebuildIndex.class.getName());

    /**
     * This method runs the RebuildLuceneIndexJob. The job starts at creation date
     * 1970/01/01 and reads single documents in sequence.
     * <p>
     * After the run method is finished, the properties numIndex, numUpdates and
     * numProcessed are updated.
     * <p>
     * The method runs in an isolated new transaction because the method flushes the
     * local persistence manager.
     * 
     * @param adminp
     * @return true when finished
     * @throws AccessDeniedException
     * @throws PluginException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ItemCollection run(ItemCollection adminp) throws AdminPException {
        long lProfiler = System.currentTimeMillis();
        long syncPoint = adminp.getItemValueLong("_syncpoint");
        int totalCount = adminp.getItemValueInteger("numUpdates");
        int blockCount = 0;
        // test if the job document provide a blocksize otherwise we take the defaults.
        int jobBlockSize = adminp.getItemValueInteger("blocksize");
        if (jobBlockSize > 0) {
            // overwrite default blocksize
            block_size = jobBlockSize;
        }
        // test if the job document provide a time_out otherwise we take the defaults.
        int jobTimeOut = adminp.getItemValueInteger("numinterval");
        if (jobTimeOut > 0) {
            // overwrite default time_out
            time_out = jobTimeOut;
        }
        // read blocksize and timeout....
        logger.info((((("...Job " + AdminPService.JOB_REBUILD_INDEX) + " (") + adminp.getUniqueID()) + ") - lucene.rebuild.block_size=") + block_size);
        logger.info((((("...Job " + AdminPService.JOB_REBUILD_INDEX) + " (") + adminp.getUniqueID()) + ") - lucene.rebuild.time_out=") + time_out);
        try {
            while (true) {
                List<ItemCollection> resultList = new ArrayList<ItemCollection>();
                List<Document> documents = findNextDocumentsBySyncPoint(syncPoint);
                if ((documents != null) && (documents.size() > 0)) {
                    for (Document doc : documents) {
                        // update syncpoint
                        syncPoint = doc.getCreated().getTimeInMillis();
                        try {
                            resultList.add(new ItemCollection(doc.getData()));
                        } catch (InvalidAccessException e) {
                            logger.warning((("...unable to index document '" + doc.getId()) + "' ") + e.getMessage());
                        }
                        // detach object!
                        manager.detach(doc);
                    }
                    // update the index
                    updateService.updateIndex(resultList);
                    manager.flush();
                    // update count
                    totalCount += resultList.size();
                    blockCount += resultList.size();
                    if (blockCount >= block_size) {
                        long time = (System.currentTimeMillis() - lProfiler) / 1000;
                        if (time == 0) {
                            time = 1;
                        }
                        logger.info(((((((("...Job " + AdminPService.JOB_REBUILD_INDEX) + " (") + adminp.getUniqueID()) + ") - ...") + totalCount) + " documents indexed in ") + time) + " sec. ... ");
                        blockCount = 0;
                    }
                } else {
                    // no more documents
                    manager.flush();
                    break;
                }
                // suspend job?
                long time = (System.currentTimeMillis() - lProfiler) / 1000;
                if (time == 0) {
                    time = 1;
                }
                if (time > time_out) {
                    // suspend after 2 mintues (default 120)....
                    logger.info(((((((("...Job " + AdminPService.JOB_REBUILD_INDEX) + " (") + adminp.getUniqueID()) + ") - suspended: ") + totalCount) + " documents indexed in ") + time) + " sec. ");
                    adminp.replaceItemValue("_syncpoint", syncPoint);
                    adminp.replaceItemValue(JobHandler.ISCOMPLETED, false);
                    adminp.replaceItemValue("numUpdates", totalCount);
                    adminp.replaceItemValue("numProcessed", totalCount);
                    adminp.replaceItemValue("numLastCount", 0);
                    return adminp;
                }
            } 
        } catch (java.lang.Exception e) {
            // print exception and stop job
            logger.severe(((((((((("...Job " + AdminPService.JOB_REBUILD_INDEX) + " (") + adminp.getUniqueID()) + ") - failed - ") + e.getMessage()) + " last syncpoint  ") + syncPoint) + " - ") + totalCount) + "  documents reindexed....");
            e.printStackTrace();
            adminp.replaceItemValue(JobHandler.ISCOMPLETED, false);
            // update syncpoint
            Date syncDate = new Date(syncPoint);
            adminp.replaceItemValue("error", e.getMessage());
            adminp.replaceItemValue(ITEM_SYNCPOINT, syncPoint);
            adminp.replaceItemValue(ITEM_SYNCDATE, syncDate);
            adminp.replaceItemValue("numUpdates", totalCount);
            adminp.replaceItemValue("numProcessed", totalCount);
            adminp.replaceItemValue("numLastCount", 0);
            return adminp;
        }
        // completed
        long time = (System.currentTimeMillis() - lProfiler) / 1000;
        if (time == 0) {
            time = 1;
        }
        logger.info(((((((("...Job " + AdminPService.JOB_REBUILD_INDEX) + " (") + adminp.getUniqueID()) + ") - Finished: ") + totalCount) + " documents indexed in ") + time) + " sec. ");
        adminp.replaceItemValue(JobHandler.ISCOMPLETED, true);
        adminp.replaceItemValue("numUpdates", totalCount);
        adminp.replaceItemValue("numProcessed", totalCount);
        adminp.replaceItemValue("numLastCount", 0);
        return adminp;
    }

    /**
     * Loads the next documents by a given symcpoint (timestamp in milis) compared
     * with the created timestamp of a document entity.
     * <p>
     * It is possible that more than one document entities have the same created
     * timestamp. For that reason the method returns all documents with the same
     * timestamp in a collection.
     * 
     * @param lSyncpoint
     * @return a list of documents with the same creation timestamp after the given
     *         syncpoint. Returns null in case no more documents were found.
     */
    @SuppressWarnings("unchecked")
    private List<Document> findNextDocumentsBySyncPoint(long lSyncpoint) {
        Date syncpoint = new Date(lSyncpoint);
        // ISO date time format: '2016-08-25 01:23:46.0',
        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String query = "SELECT document FROM Document AS document ";
        query += (" WHERE document.created > '" + isoFormat.format(syncpoint)) + "'";
        query += (" AND NOT document.type LIKE '" + SNAPSHOT_TYPE_PRAFIX) + "%' ";
        query += " AND NOT document.type LIKE 'workitemlob%' ";
        query += " AND document.type != 'event' ";
        query += " ORDER BY document.created ASC";
        Query q = manager.createQuery(query);
        q.setFirstResult(0);
        q.setMaxResults(READ_AHEAD);
        List<Document> documentList = q.getResultList();
        if ((documentList != null) && (documentList.size() > 0)) {
            Document lastDocument = null;
            Document nextToLastDocument = null;
            // test if we have two documents with the same creation date (in seldom cases
            // possible)
            if (documentList.size() == READ_AHEAD) {
                lastDocument = documentList.get(READ_AHEAD - 1);
                nextToLastDocument = documentList.get(READ_AHEAD - 2);
                // now test if we have more than one document with the same timestamp at the end
                // of the list
                if (((lastDocument != null) && (nextToLastDocument != null)) && lastDocument.getCreated().equals(nextToLastDocument.getCreated())) {
                    logger.finest("......there are more than one document with the same creation timestamp!");
                    // lets build a new collection with the duplicated creation timestamp
                    syncpoint = new Date(lastDocument.getCreated().getTimeInMillis());
                    query = "SELECT document FROM Document AS document ";
                    query += (" WHERE document.created = '" + isoFormat.format(syncpoint)) + "'";
                    query += (" AND NOT document.type LIKE '" + SNAPSHOT_TYPE_PRAFIX) + "%' ";
                    query += " AND NOT document.type LIKE 'workitemlob%' ";
                    query += " AND document.type != 'event' ";
                    query += " ORDER BY document.created ASC";
                    q = manager.createQuery(query);
                    q.setFirstResult(0);
                    q.setMaxResults(block_size);
                    documentList.addAll(q.getResultList());
                    return documentList;
                } else {
                    // we found exactly READ_AHEAD documents and the last two ones are not equal
                    // so we drop the last one of the result to avoid overlapping duplicates in the
                    // next block.
                    documentList.remove(lastDocument);
                    manager.detach(lastDocument);
                    return documentList;
                }
            } else {
                // we are at the end of the list
                return documentList;
            }
        }
        return null;
    }
}