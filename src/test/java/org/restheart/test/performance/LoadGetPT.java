package org.restheart.test.performance;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.restheart.ConfigurationException;
import org.restheart.db.CollectionDAO;
import org.restheart.db.DBCursorPool;
import org.restheart.db.MongoDBClientSingleton;
import org.restheart.utils.FileUtils;
import org.restheart.utils.HttpStatus;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class LoadGetPT {
  private String url;

  private String id;

  private String pwd;

  private boolean printData = false;

  private String db;

  private String coll;

  private String filter = null;

  private int page = 1;

  private int pagesize = 5;

  private final Path CONF_FILE = new File("./etc/restheart-perftest.yml").toPath();

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
      @Override protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(id, pwd.toCharArray());
      }
    });
    try {
      MongoDBClientSingleton.init(FileUtils.getConfiguration(CONF_FILE, false));
    } catch (ConfigurationException ex) {
      System.out.println(ex.getMessage() + ", exiting...");
      System.exit(-1);
    }
    httpExecutor = Executor.newInstance();
  }

  /**
     *
     * @throws IOException
     */
  public void get() throws IOException {
    URLConnection connection = new URL(url).openConnection();
    InputStream stream = connection.getInputStream();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
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
     */
  public void dbdirect() {
    final CollectionDAO collectionDAO = new CollectionDAO();
    DBCollection dbcoll = collectionDAO.getCollection(db, coll);
    Deque<String> _filter;
    if (filter == null) {
      _filter = null;
    } else {
      _filter = new ArrayDeque<>();
      _filter.add(filter);
    }
    ArrayList<DBObject> data;
    try {
      data = collectionDAO.getCollectionData(dbcoll, page, pagesize, null, _filter, DBCursorPool.EAGER_CURSOR_ALLOCATION_POLICY.NONE);
    } catch (Exception e) {
      System.out.println("error: " + e.getMessage());
      return;
    }
    assertNotNull(data);
    assertFalse(data.isEmpty());
    if (printData) {
      System.out.println(data);
    }
  }

  public void getPagesLinearly() throws Exception {
    Integer _page = threadPages.get(Thread.currentThread().getId());
    if (_page == null) {
      threadPages.put(Thread.currentThread().getId(), page);
      _page = page;
    }
    String pagedUrl = url + "?page=" + (_page % 10000);
    _page++;
    threadPages.put(Thread.currentThread().getId(), _page);
    if (printData) {
      System.out.println(Thread.currentThread().getId() + " -> " + pagedUrl);
    }
    Response resp = httpExecutor.execute(Request.Get(new URI(pagedUrl)));
    HttpResponse httpResp = resp.returnResponse();
    assertNotNull(httpResp);
    HttpEntity entity = httpResp.getEntity();
    assertNotNull(entity);
    StatusLine statusLine = httpResp.getStatusLine();
    assertNotNull(statusLine);
    assertEquals("check status code", HttpStatus.SC_OK, statusLine.getStatusCode());
  }

  public void getPagesRandomly() throws Exception {
    long rpage = Math.round(Math.random() * 10000);
    String pagedUrl = url + "?page=" + rpage;
    Response resp = httpExecutor.execute(Request.Get(new URI(pagedUrl)));
    HttpResponse httpResp = resp.returnResponse();
    assertNotNull(httpResp);
    HttpEntity entity = httpResp.getEntity();
    assertNotNull(entity);
    StatusLine statusLine = httpResp.getStatusLine();
    assertNotNull(statusLine);
    assertEquals("check status code", HttpStatus.SC_OK, statusLine.getStatusCode());
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

  /**
     * @param filter the filter to set
     */
  public void setFilter(String filter) {
    this.filter = filter;
  }

  /**
     * @param page the page to set
     */
  public void setPage(int page) {
    this.page = page;
  }

  /**
     * @param pagesize the pagesize to set
     */
  public void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }
}