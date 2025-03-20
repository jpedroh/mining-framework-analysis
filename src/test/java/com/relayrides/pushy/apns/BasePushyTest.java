package com.relayrides.pushy.apns;
import static org.junit.Assert.fail;
import io.netty.channel.nio.NioEventLoopGroup;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

public abstract class BasePushyTest {
  public static final ApnsEnvironment TEST_ENVIRONMENT = new ApnsEnvironment("127.0.0.1", 2195, "127.0.0.1", 2196);

  private static final long LATCH_TIMEOUT_VALUE = 2;

  private static final TimeUnit LATCH_TIMEOUT_UNIT = TimeUnit.SECONDS;

  private static NioEventLoopGroup eventLoopGroup;

  private PushManager<SimpleApnsPushNotification> pushManager;

  private MockApnsServer apnsServer;

  private MockFeedbackServer feedbackServer;

  @BeforeClass public static void setUpBeforeClass() {
    BasePushyTest.eventLoopGroup = new NioEventLoopGroup();
  }

  @Rule public Timeout globalTimeout = new Timeout(10000);

  @Before public void setUp() throws InterruptedException, UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    this.apnsServer = new MockApnsServer(TEST_ENVIRONMENT.getApnsGatewayPort(), BasePushyTest.eventLoopGroup);
    this.apnsServer.start();
    this.feedbackServer = new MockFeedbackServer(TEST_ENVIRONMENT.getFeedbackPort(), BasePushyTest.eventLoopGroup);
    this.feedbackServer.start();
    final PushManagerFactory<SimpleApnsPushNotification> pushManagerFactory = new PushManagerFactory<SimpleApnsPushNotification>(TEST_ENVIRONMENT, SSLTestUtil.createSSLContextForTestClient());
    pushManagerFactory.setEventLoopGroup(BasePushyTest.eventLoopGroup);
    this.pushManager = pushManagerFactory.buildPushManager();
  }

  @After public void tearDown() throws InterruptedException {
    this.apnsServer.shutdown();
    this.feedbackServer.shutdown();
  }

  @AfterClass public static void tearDownAfterClass() throws InterruptedException {
    BasePushyTest.eventLoopGroup.shutdownGracefully().await();
  }

  public NioEventLoopGroup getEventLoopGroup() {
    return BasePushyTest.eventLoopGroup;
  }

  public PushManager<SimpleApnsPushNotification> getPushManager() {
    return this.pushManager;
  }

  public MockApnsServer getApnsServer() {
    return this.apnsServer;
  }

  public MockFeedbackServer getFeedbackServer() {
    return this.feedbackServer;
  }

  public SimpleApnsPushNotification createTestNotification() {
    final byte[] token = new byte[MockApnsServer.EXPECTED_TOKEN_SIZE];
    new Random().nextBytes(token);
    return new SimpleApnsPushNotification(token, "{\"aps\":{\"alert\":\"Hello\"}}");
  }

  public void waitForLatch(final CountDownLatch latch) throws InterruptedException {
    while (latch.getCount() > 0) {
      if (!latch.await(LATCH_TIMEOUT_VALUE, LATCH_TIMEOUT_UNIT)) {
        fail(String.format("Timed out waiting for latch. Remaining count: %d", latch.getCount()));
      }
    }
  }
}