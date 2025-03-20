package com.joyent.manta.client;
import com.joyent.manta.client.helper.IntegrationTestHelper;
import com.joyent.manta.config.ConfigContext;
import com.joyent.manta.config.IntegrationTestConfigContext;
import com.joyent.test.util.MantaAssert;
import com.joyent.test.util.MantaFunction;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static com.joyent.manta.client.MantaClient.SEPARATOR;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Tests the functionality of signing private Manta URLs for public access.
 *
 * @author Elijah Zupancic
 */
@Test(groups = { "buckets" }) public class MantaClientSigningIT {
  private static final String TEST_DATA = "EPISODEII_IS_BEST_EPISODE";

  private MantaClient mantaClient;

  private String testPathPrefix;

  private ConfigContext config;

  @BeforeClass @Parameters(value = { "testType" }) public void beforeClass(final @Optional String testType) throws IOException {
    config = new IntegrationTestConfigContext();
    final String testName = this.getClass().getSimpleName();
    mantaClient = new MantaClient(config);
    testPathPrefix = IntegrationTestHelper.setupTestPath(config, mantaClient, testName, testType);
    IntegrationTestHelper.createTestBucketOrDirectory(mantaClient, testPathPrefix, testType);
  }

  @AfterClass public void afterClass() throws IOException {
    IntegrationTestHelper.cleanupTestBucketOrDirectory(mantaClient, testPathPrefix);
  }

  private HttpURLConnection openHttpURLConnection(URI uri, MantaClient client) throws IOException {
    boolean tlsInsecure = client.getContext().tlsInsecure();
    HttpURLConnection connection = (HttpsURLConnection) uri.toURL().openConnection();
    if (tlsInsecure && connection instanceof HttpsURLConnection) {
      HttpsURLConnection secureConn = (HttpsURLConnection) connection;
      TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
      } };
      HostnameVerifier hostnameVerifier = (hostname, session) -> true;
      try {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        secureConn.setSSLSocketFactory(sslContext.getSocketFactory());
        secureConn.setHostnameVerifier(hostnameVerifier);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return connection;
  }

  public final void testCanCreateSignedGETUriFromPath() throws IOException {
    if (config.isClientEncryptionEnabled()) {
      throw new SkipException("Signed URLs are not decrypted by the client");
    }
    final String name = UUID.randomUUID().toString();
    final String path = testPathPrefix + name;
    mantaClient.put(path, TEST_DATA);
    mantaClient.head(path);
    Instant expires = Instant.now().plus(1, ChronoUnit.HOURS);
    URI uri = mantaClient.getAsSignedURI(path, "GET", expires);
    HttpURLConnection connection = openHttpURLConnection(uri, mantaClient);
    try (InputStream is = connection.getInputStream()) {
      connection.setReadTimeout(3000);
      connection.connect();
      if (connection.getResponseCode() != 200) {
        Assert.fail(IOUtils.toString(connection.getErrorStream(), Charset.defaultCharset()));
      }
      String actual = IOUtils.toString(is, Charset.defaultCharset());
      Assert.assertEquals(actual, TEST_DATA);
    } finally {
      connection.disconnect();
    }
    mantaClient.delete(path);
    MantaAssert.assertResponseFailureCode(404, (MantaFunction<Object>) () -> mantaClient.get(path));
  }

  public final void testCanCreateSignedHEADUriFromPath() throws IOException {
    if (config.isClientEncryptionEnabled()) {
      throw new SkipException("Signed URLs are not decrypted by the client");
    }
    final String name = UUID.randomUUID().toString();
    final String path = testPathPrefix + name;
    mantaClient.put(path, TEST_DATA);
    mantaClient.head(path);
    Instant expires = Instant.now().plus(1, ChronoUnit.HOURS);
    URI uri = mantaClient.getAsSignedURI(path, "HEAD", expires);
    HttpURLConnection connection = openHttpURLConnection(uri, mantaClient);
    try {
      connection.setReadTimeout(3000);
      connection.setRequestMethod("HEAD");
      connection.connect();
      Map<String, List<String>> headers = connection.getHeaderFields();
      if (connection.getResponseCode() != 200) {
        Assert.fail(IOUtils.toString(connection.getErrorStream(), Charset.defaultCharset()));
      }
      Assert.assertNotNull(headers);
      Assert.assertEquals(TEST_DATA.length(), connection.getContentLength());
    }  finally {
      connection.disconnect();
    }
    mantaClient.delete(path);
    MantaAssert.assertResponseFailureCode(404, (MantaFunction<Object>) () -> mantaClient.get(path));
  }

  public final void testCanCreateSignedPUTUriFromPath() throws IOException, InterruptedException {
    if (config.isClientEncryptionEnabled()) {
      throw new SkipException("Signed URLs are not decrypted by the client");
    }
    final String name = UUID.randomUUID().toString();
    final String path = testPathPrefix + name;
    Instant expires = Instant.now().plus(1, ChronoUnit.HOURS);
    URI uri = mantaClient.getAsSignedURI(path, "PUT", expires);
    HttpURLConnection connection = openHttpURLConnection(uri, mantaClient);
    connection.setReadTimeout(3000);
    connection.setRequestMethod("PUT");
    connection.setDoOutput(true);
    connection.setChunkedStreamingMode(10);
    connection.connect();
    try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), UTF_8)) {
      out.write(TEST_DATA);
    } finally {
      connection.disconnect();
    }
    for (int i = 0; i < 10; i++) {
      Thread.sleep(500);
      if (mantaClient.existsAndIsAccessible(path)) {
        break;
      }
    }
    String actual = mantaClient.getAsString(path);
    Assert.assertEquals(actual, TEST_DATA);
    mantaClient.delete(path);
    MantaAssert.assertResponseFailureCode(404, (MantaFunction<Object>) () -> mantaClient.get(path));
  }

  public final void testCanCreateSignedOPTIONSUriFromPath() throws IOException {
    if (config.isClientEncryptionEnabled()) {
      throw new SkipException("Signed URLs are not decrypted by the client");
    }
    final String name = UUID.randomUUID().toString();
    final String path = testPathPrefix + name;
    mantaClient.put(path, TEST_DATA);
    mantaClient.head(path);
    Assert.assertEquals(mantaClient.getAsString(path), TEST_DATA);
    Instant expires = Instant.now().plus(1, ChronoUnit.HOURS);
    URI uri = mantaClient.getAsSignedURI(path, "OPTIONS", expires);
    HttpURLConnection connection = openHttpURLConnection(uri, mantaClient);
    try {
      connection.setReadTimeout(3000);
      connection.setRequestMethod("OPTIONS");
      connection.connect();
      if (connection.getResponseCode() != 200) {
        String errorText = IOUtils.toString(connection.getErrorStream(), Charset.defaultCharset());
        if (config.getMantaUser().contains(SEPARATOR)) {
          String msg = String.format("This fails due to an outstanding bug: MANTA-2839.\n%s", errorText);
          throw new SkipException(msg);
        }
        if (testPathPrefix.contains(config.getMantaBucketsDirectory())) {
          final String msg = "{\"code\":\"MethodNotAllowedError\",\"message\":\"OPTIONS is not allowed\"}";
          Assert.fail(msg);
        } else {
          Assert.fail(errorText);
        }
      }
      String server = StringUtils.substringBefore(connection.getHeaderField("Server"), "/");
      Assert.
<<<<<<< Unknown file: This is a bug in JDime.
=======
      assertEquals(server, "Manta")
>>>>>>> /usr/src/app/output/joyent/java-manta/5fb0fe7ba2f3107ca1ed1b26c8e6f2d30262db6d/java-manta-it/src/test/java/com/joyent/manta/client/MantaClientSigningIT.java/right.java
      ;
    }  finally {
      connection.disconnect();
    }
    mantaClient.delete(path);
    MantaAssert.assertResponseFailureCode(404, (MantaFunction<Object>) () -> mantaClient.get(path));
  }

  public final void testCanCreateSignedURIWithEncodedCharacters() throws IOException {
    if (config.isClientEncryptionEnabled()) {
      throw new SkipException("Signed URLs are not decrypted by the client");
    }
    final String path = testPathPrefix + "\u26f0 quack \ud83e\udd86";
    mantaClient.put(path, TEST_DATA, UTF_8);
    Assert.assertEquals(mantaClient.getAsString(path), TEST_DATA);
    final URI uri = mantaClient.getAsSignedURI(path, "GET", Instant.now().plus(Duration.ofHours(1)));
    HttpURLConnection conn = openHttpURLConnection(uri, mantaClient);
    try (InputStream is = conn.getInputStream()) {
      conn.setReadTimeout(3000);
      conn.connect();
      Assert.assertEquals(conn.getResponseCode(), 200);
      Assert.assertEquals(IOUtils.toString(is, UTF_8), TEST_DATA);
    } finally {
      conn.disconnect();
    }
    mantaClient.delete(path);
    MantaAssert.assertResponseFailureCode(404, (MantaFunction<Object>) () -> mantaClient.get(path));
  }
}