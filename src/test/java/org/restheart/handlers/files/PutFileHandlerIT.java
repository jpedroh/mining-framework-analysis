package org.restheart.handlers.files;
import org.apache.http.HttpEntity;
import java.io.IOException;
import org.apache.http.HttpResponse;
import java.net.UnknownHostException;
import org.apache.http.StatusLine;
import java.util.Arrays;
import org.apache.http.client.fluent.Request;
import java.util.UUID;
import org.apache.http.client.fluent.Response;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.junit.rules.TemporaryFolder;
import org.restheart.representation.Resource;
import static org.restheart.hal.Representation.HAL_JSON_MEDIA_TYPE;
import static org.restheart.utils.HttpStatus.SC_CREATED;
import static org.restheart.utils.HttpStatus.SC_OK;

/**
 * TODO: fillme
 */
public class PutFileHandlerIT extends FileHandlerAbstractIT {
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before public void init() throws Exception {
    Thread.sleep(1000);
    createBucket();
  }


<<<<<<< /usr/src/app/output/softinstigate/restheart/ef75e2ee223cd17f1f8d37bf30a3d9e193e77eac/src/test/java/org/restheart/handlers/files/PutFileHandlerIT.java/left.java
  private void createBucket() throws IOException {
    Response resp = adminExecutor.execute(Request.Put(dbTmpUri).addHeader(Headers.CONTENT_TYPE_STRING, Resource.HAL_JSON_MEDIA_TYPE));
    HttpResponse httpResp = resp.returnResponse();
    assertNotNull(httpResp);
    StatusLine statusLine = httpResp.getStatusLine();
    assertNotNull(statusLine);
    assertEquals("check status code", SC_CREATED, statusLine.getStatusCode());
    String bucketUrl = dbTmpUri + "/" + BUCKET + ".files/";
    resp = adminExecutor.execute(Request.Put(bucketUrl).addHeader(Headers.CONTENT_TYPE_STRING, Resource.HAL_JSON_MEDIA_TYPE));
    httpResp = resp.returnResponse();
    assertNotNull(httpResp);
    statusLine = httpResp.getStatusLine();
    assertNotNull(statusLine);
    assertEquals("check status code", SC_CREATED, statusLine.getStatusCode());
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  private HttpResponse createFilePut(String id) throws UnknownHostException, IOException {
    String bucketUrl = dbTmpUri + "/" + BUCKET + ".files/" + id;
    HttpEntity entity = buildMultipartResource();
    Response resp = adminExecutor.execute(Request.Put(bucketUrl).body(entity));
    HttpResponse httpResp = resp.returnResponse();
    assertNotNull(httpResp);
    StatusLine statusLine = httpResp.getStatusLine();
    assertNotNull(statusLine);
    assertTrue("check status code", Arrays.asList(SC_CREATED, SC_OK).contains(statusLine.getStatusCode()));
    return httpResp;
  }

  @Test public void testPutNonExistingFile() throws IOException {
    String id = "nonexistingfile" + UUID.randomUUID().toString();
    final HttpResponse httpResponse = createFilePut(id);
    assertEquals(SC_CREATED, httpResponse.getStatusLine().getStatusCode());
    final String fileUrl = dbTmpUri + "/" + BUCKET + ".files/" + id;
    Response resp = adminExecutor.execute(Request.Get(fileUrl));
    HttpResponse httpResp = this.check("Response is 200 OK", resp, SC_OK);
    HttpEntity entity = checkContentType(httpResp, HAL_JSON_MEDIA_TYPE);

<<<<<<< /usr/src/app/output/softinstigate/restheart/ef75e2ee223cd17f1f8d37bf30a3d9e193e77eac/src/test/java/org/restheart/handlers/files/PutFileHandlerIT.java/left.java
    assertEquals("check content type", Resource.HAL_JSON_MEDIA_TYPE, entity.getContentType().getValue());
=======
>>>>>>> Unknown file: This is a bug in JDime.

    checkNotNullMetadata(entity);
  }

  @Test public void testPutAndOverwriteExistingFile() throws IOException {
    String id = "nonexistingfile" + UUID.randomUUID().toString();
    HttpResponse httpResponse = createFilePut(id);
    assertEquals(SC_CREATED, httpResponse.getStatusLine().getStatusCode());
    httpResponse = createFilePut(id);
    assertEquals(SC_OK, httpResponse.getStatusLine().getStatusCode());
    final String fileUrl = dbTmpUri + "/" + BUCKET + ".files/" + id;
    Response resp = adminExecutor.execute(Request.Get(fileUrl));
    HttpResponse httpResp = this.check("Response is 200 OK", resp, SC_OK);
    HttpEntity entity = checkContentType(httpResp, HAL_JSON_MEDIA_TYPE);

<<<<<<< /usr/src/app/output/softinstigate/restheart/ef75e2ee223cd17f1f8d37bf30a3d9e193e77eac/src/test/java/org/restheart/handlers/files/PutFileHandlerIT.java/left.java
    assertEquals("check content type", Resource.HAL_JSON_MEDIA_TYPE, entity.getContentType().getValue());
=======
>>>>>>> Unknown file: This is a bug in JDime.

    checkNotNullMetadata(entity);
  }
}