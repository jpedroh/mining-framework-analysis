/*
 * RESTHeart - the Web API for MongoDB
 * Copyright (C) SoftInstigate Srl
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
package org.restheart.handlers.files;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.restheart.representation.Resource;
import static org.restheart.hal.Representation.HAL_JSON_MEDIA_TYPE;
import static org.restheart.utils.HttpStatus.SC_CREATED;
import static org.restheart.utils.HttpStatus.SC_OK;

/**
 *
 * @author Maurizio Turatti {@literal <maurizio@softinstigate.com>}
 */
public class PutFileHandlerIT extends FileHandlerAbstractIT {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void init() throws Exception {
        Thread.sleep(1000); // Sleep 1 second to avoid NoHttpResponseException
        createBucket();
    }

    private void createBucket() throws IOException {
        // create db
        Response resp = adminExecutor.execute(Request.Put(dbTmpUri)
                                                  .addHeader(Headers.CONTENT_TYPE_STRING, Resource.HAL_JSON_MEDIA_TYPE));

        HttpResponse httpResp = resp.returnResponse();
        assertNotNull(httpResp);
        StatusLine statusLine = httpResp.getStatusLine();

        assertNotNull(statusLine);
        assertEquals("check status code", SC_CREATED, statusLine.getStatusCode());

        // create bucket
        String bucketUrl = dbTmpUri + "/" + BUCKET + ".files/";
        resp = adminExecutor.execute(Request.Put(bucketUrl)
                                         .addHeader(Headers.CONTENT_TYPE_STRING, Resource.HAL_JSON_MEDIA_TYPE));

        httpResp = resp.returnResponse();
        assertNotNull(httpResp);
        statusLine = httpResp.getStatusLine();

        assertNotNull(statusLine);
        assertEquals("check status code", SC_CREATED, statusLine.getStatusCode());
    }

    private HttpResponse createFilePut(String id) throws UnknownHostException, IOException {
        String bucketUrl = dbTmpUri + "/" + BUCKET + ".files/" + id;

        HttpEntity entity = buildMultipartResource();

        Response resp = adminExecutor.execute(Request.Put(bucketUrl)
                .body(entity));

        HttpResponse httpResp = resp.returnResponse();
        assertNotNull(httpResp);
        StatusLine statusLine = httpResp.getStatusLine();
        assertNotNull(statusLine);

        assertTrue("check status code", Arrays.asList(SC_CREATED, SC_OK).contains(statusLine.getStatusCode()));

        return httpResp;
    }

    @Test
    public void testPutNonExistingFile() throws IOException {
        String id = "nonexistingfile" + UUID.randomUUID().toString();

        final HttpResponse httpResponse = createFilePut(id);
        assertEquals(SC_CREATED, httpResponse.getStatusLine().getStatusCode());

        // test that GET /db/bucket.files includes the file
        final String fileUrl = dbTmpUri + "/" + BUCKET + ".files/" + id;
        Response resp = adminExecutor.execute(Request.Get(fileUrl));

        HttpResponse httpResp = resp.returnResponse();
        assertNotNull(httpResp);
        HttpEntity entity = httpResp.getEntity();
        assertNotNull(entity);
        StatusLine statusLine = httpResp.getStatusLine();
        assertNotNull(statusLine);

        assertEquals("check status code", SC_OK, statusLine.getStatusCode());
        assertNotNull("content type not null", entity.getContentType());
        assertEquals("check content type", Resource.HAL_JSON_MEDIA_TYPE, entity.getContentType().getValue());

        String content = EntityUtils.toString(entity);

        JsonObject json = null;

        try {
            json = Json.parse(content).asObject();
        } catch (Throwable t) {
            fail("parsing received json");
        }

        assertNotNull(json.get("_id"));
        assertNotNull(json.get("metadata"));
    }

    @Test
    public void testPutAndOverwriteExistingFile() throws IOException {
        String id = "nonexistingfile" + UUID.randomUUID().toString();

        HttpResponse httpResponse = createFilePut(id);
        assertEquals(SC_CREATED, httpResponse.getStatusLine().getStatusCode());
        //now run the put again to see that it has been overwritten
        httpResponse = createFilePut(id);
        assertEquals(SC_OK, httpResponse.getStatusLine().getStatusCode());

        // test that GET /db/bucket.files includes the file
        final String fileUrl = dbTmpUri + "/" + BUCKET + ".files/" + id;
        Response resp = adminExecutor.execute(Request.Get(fileUrl));

        HttpResponse httpResp = resp.returnResponse();
        assertNotNull(httpResp);
        HttpEntity entity = httpResp.getEntity();
        assertNotNull(entity);
        StatusLine statusLine = httpResp.getStatusLine();
        assertNotNull(statusLine);

        assertEquals("check status code", SC_OK, statusLine.getStatusCode());
        assertNotNull("content type not null", entity.getContentType());
        assertEquals("check content type", Resource.HAL_JSON_MEDIA_TYPE, entity.getContentType().getValue());

        String content = EntityUtils.toString(entity);

        JsonObject json = null;

        try {
            json = Json.parse(content).asObject();
        } catch (Throwable t) {
            fail("parsing received json");
        }

        assertNotNull(json.get("_id"));
        assertNotNull(json.get("metadata"));
    }
}
