/*
 * RESTHeart - the data REST API server
 * Copyright (C) 2014 SoftInstigate Srl
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
package com.softinstigate.restheart.perftest;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.softinstigate.restheart.db.CollectionDAO;
import com.softinstigate.restheart.db.DBCursorPool;
import com.softinstigate.restheart.db.MongoDBClientSingleton;
import com.softinstigate.restheart.utils.FileUtils;
import com.softinstigate.restheart.utils.HttpStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;


/**
 *
 * @author Andrea Di Cesare
 */
public class LoadGetPT {
    private String url;

    private String id;

    private String pwd;

    private boolean printData = false;

    private String db;

    private String coll;

    private final Path CONF_FILE = new File("./etc/restheart-integrationtest.yml").toPath();

    private Executor httpExecutor;

    private final ConcurrentHashMap<Long, Integer> threadPages = new ConcurrentHashMap<>();

    /**
     *
     * @param url
     * @throws MalformedURLException
     */
    public void setUrl(String url) throws MalformedURLException {
        this.url = url;
    }

    /**
     *
     */
    public void prepare() {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(id, pwd.toCharArray());
            }
        });
        MongoDBClientSingleton.init(FileUtils.getConfiguration(CONF_FILE));
        httpExecutor = Executor.newInstance().authPreemptive(new HttpHost("127.0.0.1", 8080, "http")).auth(new HttpHost("127.0.0.1"), id, pwd);
    }

    /**
     *
     * @throws IOException
     */
    public void get() throws IOException {
        URLConnection connection = new URL(url).openConnection();
        // connection.setRequestProperty("Accept-Encoding", "gzip");
        InputStream stream = connection.getInputStream();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String data = in.readLine();
            while (data != null) {
                if (printData) {
                    System.out.println(data);
                }
                data = in.readLine();
            } 
        }
    }

    /**
     *
     * @throws IOException
     */
    public void dbdirect() throws IOException {
        DBCollection dbcoll = CollectionDAO.getCollection(db, coll);
        //CollectionDAO.getCollectionSize(coll, null);
        //CollectionDAO.getCollectionProps(coll);
        ArrayList<DBObject> data = CollectionDAO.getCollectionData(dbcoll, 5000, 100, null, null, DBCursorPool.EAGER_CURSOR_ALLOCATION_POLICY.NONE);
        if (printData) {
            System.out.println(data);
        }
    }

    public void getPagesLinearly() throws Exception {
        Integer page = threadPages.get(Thread.currentThread().getId());

        if (page == null) {
             threadPages.put(Thread.currentThread().getId(), 5000);
             page = 5000;
        }
        
        String pagedUrl = url + "?page=" + (page % 10000);
        
        page++;
        threadPages.put(Thread.currentThread().getId(), page);
        
        if (printData)
            System.out.println(Thread.currentThread().getId() + " -> " + pagedUrl);
        
        Response resp = httpExecutor.execute(Request.Get(new URI(pagedUrl)));

        HttpResponse httpResp = resp.returnResponse();
        Assert.assertNotNull(httpResp);
        HttpEntity entity = httpResp.getEntity();
        Assert.assertNotNull(entity);
        StatusLine statusLine = httpResp.getStatusLine();
        Assert.assertNotNull(statusLine);

        Assert.assertEquals("check status code", HttpStatus.SC_OK, statusLine.getStatusCode());
    }

    public void getPagesRandomly() throws Exception {

        long rpage = Math.round(Math.random()*10000);
        
        String pagedUrl = url + "?page=" + rpage;
        
        //System.out.println(pagedUrl);
        
        Response resp = httpExecutor.execute(Request.Get(new URI(pagedUrl)));

        HttpResponse httpResp = resp.returnResponse();
        Assert.assertNotNull(httpResp);
        HttpEntity entity = httpResp.getEntity();
        Assert.assertNotNull(entity);
        StatusLine statusLine = httpResp.getStatusLine();
        Assert.assertNotNull(statusLine);

        Assert.assertEquals("check status code", HttpStatus.SC_OK, statusLine.getStatusCode());
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @param printData the printData to set
     */
    public void setPrintData(String printData) {
        this.printData = Boolean.valueOf(printData);
    }

    /**
     * @param db the db to set
     */
    public void setDb(String db) {
        this.db = db;
    }

    /**
     * @param coll the coll to set
     */
    public void setColl(String coll) {
        this.coll = coll;
    }
}