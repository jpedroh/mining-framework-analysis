package org.dspace.statistics.export.service;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.dspace.core.Context;
import org.dspace.statistics.export.OpenURLTracker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for the OpenUrlServiceImpl
 */
@RunWith(value = MockitoJUnitRunner.class) public class OpenUrlServiceImplTest {
  private OpenUrlServiceImpl openUrlService;

  @Mock private FailedOpenURLTrackerService failedOpenURLTrackerService;

  @Mock private HttpClient httpClient;

  @Before public void setUp() throws Exception {
    openUrlService = Mockito.spy(OpenUrlServiceImpl.class);
    openUrlService.failedOpenUrlTrackerService = failedOpenURLTrackerService;
    doReturn(httpClient).when(openUrlService).getHttpClient(any());
  }

  /**
     * Create a mock http response with the given status code.
     * @param statusCode the http status code to use in the mock.
     * @return a mocked http response.
     */
  protected HttpResponse createMockHttpResponse(int statusCode) {
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(statusCode);
    HttpResponse httpResponse = mock(HttpResponse.class);
    when(httpResponse.getStatusLine()).thenReturn(statusLine);
    return httpResponse;
  }

  /**
     * Create a mock open url tracker with the given url.
     * @param url the url to use in the mock.
     * @return a mocked open url tracker.
     */
  protected OpenURLTracker createMockTracker(String url) {
    OpenURLTracker tracker = mock(OpenURLTracker.class);
    when(tracker.getUrl()).thenReturn(url);
    return tracker;
  }

  /**
     * Test the processUrl method
     * @throws IOException
     * @throws SQLException
     */
  @Test public void testProcessUrl() throws IOException, SQLException {
    Context context = mock(Context.class);
    doReturn(createMockHttpResponse(HttpURLConnection.HTTP_OK)).when(httpClient).execute(any());
    openUrlService.processUrl(context, "test-url");
    verify(openUrlService, times(0)).logfailed(context, "test-url");
  }

  /**
     * Test the processUrl method when the url connection fails
     * @throws IOException
     * @throws SQLException
     */
  @Test public void testProcessUrlOnFail() throws IOException, SQLException {
    Context context = mock(Context.class);
    doReturn(createMockHttpResponse(HttpURLConnection.HTTP_INTERNAL_ERROR)).when(httpClient).execute(any());
    doNothing().when(openUrlService).logfailed(any(Context.class), anyString());
    openUrlService.processUrl(context, "test-url");
    verify(openUrlService, times(1)).logfailed(context, "test-url");
  }

  /**
     * Test the ReprocessFailedQueue method
     * @throws SQLException
     */
  @Test public void testReprocessFailedQueue() throws IOException, SQLException {
    Context context = mock(Context.class);
    List<OpenURLTracker> trackers = List.of(createMockTracker("tacker1"), createMockTracker("tacker2"), createMockTracker("tacker3"));
    when(failedOpenURLTrackerService.findAll(any(Context.class))).thenReturn(trackers);
    doReturn(createMockHttpResponse(HttpURLConnection.HTTP_INTERNAL_ERROR), createMockHttpResponse(HttpURLConnection.HTTP_NOT_FOUND), createMockHttpResponse(HttpURLConnection.HTTP_OK)).when(httpClient).execute(any());
    openUrlService.reprocessFailedQueue(context);
    verify(openUrlService, times(3)).tryReprocessFailed(any(Context.class), any(OpenURLTracker.class));
    verify(failedOpenURLTrackerService, times(0)).remove(any(Context.class), eq(trackers.get(0)));
    verify(failedOpenURLTrackerService, times(0)).remove(any(Context.class), eq(trackers.get(1)));
    verify(failedOpenURLTrackerService, times(1)).remove(any(Context.class), eq(trackers.get(2)));
  }

  /**
     * Test the method that logs the failed urls in the db
     * @throws SQLException
     */
  @Test public void testLogfailed() throws SQLException {
    Context context = mock(Context.class);
    OpenURLTracker tracker1 = mock(OpenURLTracker.class);
    when(failedOpenURLTrackerService.create(any(Context.class))).thenReturn(tracker1);
    String failedUrl = "failed-url";
    openUrlService.logfailed(context, failedUrl);
    verify(tracker1).setUrl(failedUrl);
    ArgumentCaptor<Date> dateArgCaptor = ArgumentCaptor.forClass(Date.class);
    verify(tracker1).setUploadDate(dateArgCaptor.capture());
    assertThat(new BigDecimal(dateArgCaptor.getValue().getTime()), closeTo(new BigDecimal(new Date().getTime()), new BigDecimal(5000)));
  }

  /**
     * Tests whether the timeout gets set to 10 seconds when processing a url
     * @throws SQLException
     */
  @Test public void testTimeout() throws IOException, SQLException {
    Context context = mock(Context.class);
    String URL = "http://bla.com";
    RequestConfig.Builder requestConfig = mock(RequestConfig.Builder.class);
    doReturn(requestConfig).when(openUrlService).getRequestConfigBuilder();
    doReturn(requestConfig).when(requestConfig).setConnectTimeout(10 * 1000);
    doReturn(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/left.java
    RequestConfig.custom().build()
=======
    createMockHttpResponse(HttpURLConnection.HTTP_OK)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/right.java
    ).when(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/left.java
    requestConfig
=======
    httpClient
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/right.java
    ).
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/left.java
    build()
=======
    execute(any())
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/right.java
    ;
    openUrlService.processUrl(context, 
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/left.java
    URL
=======
    "test-url"
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/right.java
    );
    verify(openUrlService).getHttpClient(any());

<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/left.java
    Mockito.verify(requestConfig).setConnectTimeout(10 * 1000)
=======
    verify(openUrlService).getHttpClientRequestConfig()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-api/src/test/java/org/dspace/statistics/export/service/OpenUrlServiceImplTest.java/right.java
    ;
    assertThat(openUrlService.getHttpClientRequestConfig().getConnectTimeout(), is(10 * 1000));
  }
}