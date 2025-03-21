package ninja.session;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collection;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.utils.Clock;
import ninja.utils.CookieEncryption;
import ninja.utils.Crypto;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.SecretGenerator;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(value = Parameterized.class) public class SessionImplTest {
  @Mock private Context context;

  @Mock private Result result;

  @Captor private ArgumentCaptor<Cookie> cookieCaptor;

  private Crypto crypto;

  private CookieEncryption encryption;

  @Mock NinjaProperties ninjaProperties;

  @Mock Clock clock;

  @Parameter public boolean encrypted;

  /**
     * This method provides parameters for {@code encrypted} field. The first set contains {@code false} so that
     * {@link CookieEncryption} is not initialized and test class is run without session cookie encryption. Second set
     * contains {@code true} so that sessions cookies are encrypted.
     *
     * @return
     */
  @Parameters public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { false }, { true } });
  }

  @Before public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds)).thenReturn(10000);
    when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionSendOnlyIfChanged, true)).thenReturn(true);
    when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true)).thenReturn(true);
    when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionHttpOnly, true)).thenReturn(true);
    when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret)).thenReturn(SecretGenerator.generateSecret());
    when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA");
    when(clock.currentTimeMillis()).thenReturn(System.currentTimeMillis());
    when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false)).thenReturn(encrypted);
    encryption = new CookieEncryption(ninjaProperties);
    crypto = new Crypto(ninjaProperties);
  }

  @Test public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.save(context);
    verify(context, never()).addCookie(Matchers.any(Cookie.class));
  }

  @Test public void testSessionCookieSettingWorks() throws Exception {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("hello", "session!");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    assertEquals("NINJA_SESSION", cookieCaptor.getValue().getName());
    String cookieString = cookieCaptor.getValue().getValue();
    String cookieFromSign = cookieString.substring(cookieString.indexOf("-") + 1);
    String computedSign = crypto.signHmacSha1(cookieFromSign);
    assertEquals(computedSign, cookieString.substring(0, cookieString.indexOf("-")));
    if (encrypted) {
      cookieFromSign = encryption.decrypt(cookieFromSign);
    }
    assertTrue(cookieFromSign.contains(Session.TIMESTAMP_KEY));
  }

  @Test public void testHttpsOnlyWorks() throws Exception {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("hello", "session!");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    assertEquals(true, cookieCaptor.getValue().isSecure());
  }

  @Test public void testNoHttpsOnlyWorks() throws Exception {
    when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true)).thenReturn(false);
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("hello", "session!");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    assertEquals(false, cookieCaptor.getValue().isSecure());
  }

  @Test public void testHttpOnlyWorks() throws Exception {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("hello", "session!");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    assertEquals(true, cookieCaptor.getValue().isHttpOnly());
  }

  @Test public void testNoHttpOnlyWorks() throws Exception {
    when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionHttpOnly, true)).thenReturn(false);
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("hello", "session!");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    assertEquals(false, cookieCaptor.getValue().isHttpOnly());
  }

  @Test public void testThatCookieSavingAndInitingWorks() {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("key1", "value1");
    sessionCookie.put("key2", "value2");
    sessionCookie.put("key3", "value3");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    Cookie newSessionCookie = Cookie.builder(cookieCaptor.getValue().getName(), cookieCaptor.getValue().getValue()).build();
    when(context.getCookie(cookieCaptor.getValue().getName())).thenReturn(newSessionCookie);
    Session sessionCookie2 = createNewSession();
    sessionCookie2.init(context);
    assertEquals("value1", sessionCookie2.get("key1"));
    assertEquals("value2", sessionCookie2.get("key2"));
    assertEquals("value3", sessionCookie2.get("key3"));
  }

  @Test public void testThatCorrectMethodOfNinjaPropertiesIsUsedSoThatStuffBreaksWhenPropertyIsAbsent() {
    when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn(null);
    Session sessionCookie = createNewSession();
    verify(ninjaProperties).getOrDie(NinjaConstant.applicationCookiePrefix);
  }

  @Test public void testSessionCookieDelete() {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    final String key = "mykey";
    final String value = "myvalue";
    sessionCookie.put(key, value);
    assertEquals(value, sessionCookie.get(key));
    assertEquals(value, sessionCookie.remove(key));
    assertNull(sessionCookie.get(key));
  }

  @Test public void testGetAuthenticityTokenWorks() {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    String authenticityToken = sessionCookie.getAuthenticityToken();
    String cookieValueWithoutSign = captureFinalCookie(sessionCookie);
    assertTrue(cookieValueWithoutSign.contains(Session.AUTHENTICITY_KEY + "=" + authenticityToken));
    assertTrue(cookieValueWithoutSign.contains(Session.TIMESTAMP_KEY));
  }

  @Test public void testGetIdTokenWorks() {
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    String idToken = sessionCookie.getId();
    String valueWithoutSign = captureFinalCookie(sessionCookie);
    assertTrue(valueWithoutSign.contains(Session.ID_KEY + "=" + idToken));
    assertTrue(valueWithoutSign.contains(Session.TIMESTAMP_KEY));
  }

  @Test public void testThatCookieUsesContextPath() {
    Mockito.when(context.getContextPath()).thenReturn("/my_context");
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("anykey", "anyvalue");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    Cookie cookie = cookieCaptor.getValue();
    assertThat(cookie.getPath(), CoreMatchers.equalTo("/my_context/"));
  }

  @Test public void testExpiryTime() {
    Session sessionCookie1 = createNewSession();
    sessionCookie1.init(context);
    sessionCookie1.put("a", "2");
    sessionCookie1.setExpiryTime(10 * 1000L);
    assertThat(sessionCookie1.get("a"), CoreMatchers.equalTo("2"));
    sessionCookie1.save(context);
    Session sessionCookie2 = roundTrip(sessionCookie1);
    assertThat(sessionCookie2.get("a"), CoreMatchers.equalTo("2"));
    when(clock.currentTimeMillis()).thenReturn(System.currentTimeMillis() + 11 * 1000L);
    Session sessionCookie3 = roundTrip(sessionCookie2);
    assertNull(sessionCookie3.get("a"));
  }

  @Test public void testExpiryTimeRoundTrip() {
    when(ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds)).thenReturn(null);
    Session sessionCookie1 = createNewSession();
    sessionCookie1.init(context);
    sessionCookie1.put("a", "2");
    sessionCookie1.setExpiryTime(10 * 1000L);
    assertThat(sessionCookie1.get("a"), CoreMatchers.equalTo("2"));
    Session sessionCookie2 = roundTrip(sessionCookie1);
    assertThat(sessionCookie2.get("a"), CoreMatchers.equalTo("2"));
    assertThat(sessionCookie2.get(Session.EXPIRY_TIME_KEY), CoreMatchers.equalTo("10000"));
    sessionCookie2.setExpiryTime(null);
    Session sessionCookie3 = roundTrip(sessionCookie2);
    assertNull(sessionCookie3.get(Session.EXPIRY_TIME_KEY));
  }

  @Test public void testThatCookieDoesNotUseApplicationDomainWhenNotSet() {
    when(ninjaProperties.get(NinjaConstant.applicationCookieDomain)).thenReturn(null);
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("anykey", "anyvalue");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    Cookie cookie = cookieCaptor.getValue();
    assertThat(cookie.getDomain(), CoreMatchers.equalTo(null));
  }

  @Test public void testThatCookieUseApplicationDomain() {
    when(ninjaProperties.get(NinjaConstant.applicationCookieDomain)).thenReturn("domain.com");
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("anykey", "anyvalue");
    sessionCookie.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    Cookie cookie = cookieCaptor.getValue();
    assertThat(cookie.getDomain(), CoreMatchers.equalTo("domain.com"));
  }

  @Test public void testThatCookieClearWorks() {
    String applicationCookieName = ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix) + ninja.utils.NinjaConstant.SESSION_SUFFIX;
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("anykey", "anyvalue");
    Session sessionCookieWithValues = roundTrip(sessionCookie);
    sessionCookieWithValues.clear();
    when(context.hasCookie(applicationCookieName)).thenReturn(true);
    String cookieValue = captureFinalCookie(sessionCookieWithValues);
    assertThat(cookieValue, not(containsString("anykey")));
    assertThat(cookieCaptor.getValue().getDomain(), CoreMatchers.equalTo(null));
    assertThat(cookieCaptor.getValue().getMaxAge(), CoreMatchers.equalTo(0));
  }

  @Test public void testThatCookieClearWorksWithApplicationDomain() {
    String applicationCookieName = ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix) + ninja.utils.NinjaConstant.SESSION_SUFFIX;
    when(ninjaProperties.get(NinjaConstant.applicationCookieDomain)).thenReturn("domain.com");
    Session sessionCookie = createNewSession();
    sessionCookie.init(context);
    sessionCookie.put("anykey", "anyvalue");
    Session sessionCookieWithValues = roundTrip(sessionCookie);
    sessionCookieWithValues.clear();
    when(context.hasCookie(applicationCookieName)).thenReturn(true);
    String cookieValue = captureFinalCookie(sessionCookieWithValues);
    assertThat(cookieValue, not(containsString("anykey")));
    assertThat(cookieCaptor.getValue().getDomain(), CoreMatchers.equalTo("domain.com"));
    assertThat(cookieCaptor.getValue().getMaxAge(), CoreMatchers.equalTo(0));
  }

  @Test public void testSessionEncryptionKeysMismatch() {
    if (!encrypted) {
      assertTrue("N/A for plain session cookies without encryption", true);
      return;
    }
    Session session_1 = createNewSession();
    session_1.init(context);
    session_1.put("key", "value");
    session_1.save(context);
    verify(context).addCookie(cookieCaptor.capture());
    assertEquals("value", session_1.get("key"));
    Cookie cookie = cookieCaptor.getValue();
    Session session_2 = createNewSession();
    when(context.getCookie("NINJA_SESSION")).thenReturn(cookie);
    session_2.init(context);
    assertFalse(session_2.isEmpty());
    assertEquals("value", session_2.get("key"));
    when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret)).thenReturn(SecretGenerator.generateSecret());
    encryption = new CookieEncryption(ninjaProperties);
    Session session_3 = createNewSession();
    session_3.init(context);
    assertTrue(session_3.isEmpty());
  }

  private Session roundTrip(Session sessionCookie1) {
    sessionCookie1.save(context);
    verify(context, atLeastOnce()).addCookie(cookieCaptor.capture());
    when(context.getCookie("NINJA_SESSION")).thenReturn(cookieCaptor.getValue());
    Session sessionCookie2 = createNewSession();
    sessionCookie2.init(context);
    return sessionCookie2;
  }

  private Session createNewSession() {
    return new SessionImpl(crypto, encryption, ninjaProperties, clock);
  }

  private String captureFinalCookie(Session sessionCookie) {
    sessionCookie.save(context);
    verify(context, atLeastOnce()).addCookie(cookieCaptor.capture());
    String cookieValue = cookieCaptor.getValue().getValue();
    String cookieValueWithoutSign = cookieValue.substring(cookieValue.indexOf("-") + 1);
    if (encrypted) {
      cookieValueWithoutSign = encryption.decrypt(cookieValueWithoutSign);
    }
    return cookieValueWithoutSign;
  }
}