package org.apache.shiro.web.mgt;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.CryptoException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.junit.Test;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link CookieRememberMeManager} implementation.
 *
 * @since 1.0
 */
public class CookieRememberMeManagerTest {
  @Test public void onSuccessfulLogin() {
    HttpServletRequest mockRequest = createNiceMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createNiceMock(HttpServletResponse.class);
    WebSubject mockSubject = createNiceMock(WebSubject.class);
    expect(mockSubject.getServletRequest()).andReturn(mockRequest).anyTimes();
    expect(mockSubject.getServletResponse()).andReturn(mockResponse).anyTimes();
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    org.apache.shiro.web.servlet.Cookie cookie = createMock(org.apache.shiro.web.servlet.Cookie.class);
    mgr.setCookie(cookie);
    cookie.removeFrom(isA(HttpServletRequest.class), isA(HttpServletResponse.class));
    expect(cookie.getName()).andReturn("rememberMe");
    expect(cookie.getValue()).andReturn(null);
    expect(cookie.getComment()).andReturn(null);
    expect(cookie.getDomain()).andReturn(null);
    expect(cookie.getPath()).andReturn(null);
    expect(cookie.getMaxAge()).andReturn(SimpleCookie.DEFAULT_MAX_AGE);
    expect(cookie.getVersion()).andReturn(SimpleCookie.DEFAULT_VERSION);
    expect(cookie.isSecure()).andReturn(false);
    expect(cookie.isHttpOnly()).andReturn(true);
    expect(cookie.getSameSite()).andReturn(org.apache.shiro.web.servlet.Cookie.SameSiteOptions.LAX);
    UsernamePasswordToken token = new UsernamePasswordToken("user", "secret");
    token.setRememberMe(true);
    AuthenticationInfo account = new SimpleAuthenticationInfo("user", "secret", "test");
    replay(mockSubject);
    replay(mockRequest);
    replay(cookie);
    mgr.onSuccessfulLogin(mockSubject, token, account);
    verify(mockRequest);
    verify(mockSubject);
    verify(cookie);
  }

  @Test public void getRememberedSerializedIdentityReturnsNullForDeletedCookie() {
    HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
    WebSubjectContext context = new DefaultWebSubjectContext();
    context.setServletRequest(mockRequest);
    context.setServletResponse(mockResponse);
    expect(mockRequest.getAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY)).andReturn(null);
    Cookie[] cookies = new Cookie[] { new Cookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME, org.apache.shiro.web.servlet.Cookie.DELETED_COOKIE_VALUE) };
    expect(mockRequest.getCookies()).andReturn(cookies);
    replay(mockRequest);
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    assertNull(mgr.getRememberedSerializedIdentity(context));
  }

  @Test public void getRememberedPrincipals() {
    HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
    WebSubjectContext context = new DefaultWebSubjectContext();
    context.setServletRequest(mockRequest);
    context.setServletResponse(mockResponse);
    expect(mockRequest.getAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY)).andReturn(null);
    final String userPCAesBase64 = "0o6DCfePYTjK4q579qzUFEfkeGRvbBOdKHp2y8/nGAltt1Vz8uW0Z8igeO" + "Tq/yBmcw25f3Q0ui/Leg3x0iQZWhw9Bbu0mFHmHsGxEd6mPwtUpSegIjyX5c/kZpqnb7QLdajPWiczX8P" + "Oc2Eku5+8ye1u38Y8uKlklHxcYCPh0pRiDSBxfjPsLaDfOpGbmPjZd4SVg68i/++TvUjqBNJyb+pDix3f" + "PeuPvReWGcE50iovezVZrEfDOAQ0cZYW35ShypMWOmE9yZnb+p8++StDyAUegryyuIa4pjuRzfMh9D+sN" + "F9tm/EnDC1VCer2S/a0AGlWAQiM7jrWt1sNinZcKIrvShaWI21tONJt8WhozNS2H72lk4p92rfLNHeglT" + "xObxIYxLfTI9KiToSe1nYmpQmbBO8x1wWDkWBG//EqRvhgbIfQVqJp12T0fJC1nFuZuVhw/ZanaAZGDk8" + "7aLMiw3T6FBZtWaspgvfH+0TJrTD8Ra386ekNXNN8JW8=";
    Cookie[] cookies = new Cookie[] { new Cookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME, userPCAesBase64) };
    expect(mockRequest.getCookies()).andReturn(cookies);
    replay(mockRequest);
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    mgr.setCipherKey(Base64.decode("kPH+bIxk5D2deZiIxcaaaA=="));
    PrincipalCollection collection = mgr.getRememberedPrincipals(context);
    verify(mockRequest);
    assertTrue(collection != null);
    assertTrue(collection.iterator().next().equals("user"));
  }

  @Test(expected = CryptoException.class) public void getRememberedPrincipalsNoMoreDefaultCipher() {
    HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
    WebSubjectContext context = new DefaultWebSubjectContext();
    context.setServletRequest(mockRequest);
    context.setServletResponse(mockResponse);
    expect(mockRequest.getAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY)).andReturn(null);
    expect(mockRequest.getContextPath()).andReturn("/test");
    final String userPCAesBase64 = "0o6DCfePYTjK4q579qzUFEfkeGRvbBOdKHp2y8/nGAltt1Vz8uW0Z8igeO" + "Tq/yBmcw25f3Q0ui/Leg3x0iQZWhw9Bbu0mFHmHsGxEd6mPwtUpSegIjyX5c/kZpqnb7QLdajPWiczX8P" + "Oc2Eku5+8ye1u38Y8uKlklHxcYCPh0pRiDSBxfjPsLaDfOpGbmPjZd4SVg68i/++TvUjqBNJyb+pDix3f" + "PeuPvReWGcE50iovezVZrEfDOAQ0cZYW35ShypMWOmE9yZnb+p8++StDyAUegryyuIa4pjuRzfMh9D+sN" + "F9tm/EnDC1VCer2S/a0AGlWAQiM7jrWt1sNinZcKIrvShaWI21tONJt8WhozNS2H72lk4p92rfLNHeglT" + "xObxIYxLfTI9KiToSe1nYmpQmbBO8x1wWDkWBG//EqRvhgbIfQVqJp12T0fJC1nFuZuVhw/ZanaAZGDk8" + "7aLMiw3T6FBZtWaspgvfH+0TJrTD8Ra386ekNXNN8JW8=";
    Cookie[] cookies = new Cookie[] { new Cookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME, userPCAesBase64) };
    expect(mockRequest.getCookies()).andReturn(cookies);
    replay(mockRequest);
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    mgr.getRememberedPrincipals(context);
  }

  @Test public void getRememberedPrincipalsDecryptionError() {
    HttpServletRequest mockRequest = createNiceMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createNiceMock(HttpServletResponse.class);
    WebSubjectContext context = new DefaultWebSubjectContext();
    context.setServletRequest(mockRequest);
    context.setServletResponse(mockResponse);
    expect(mockRequest.getAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY)).andReturn(null);
    final String userPCAesBase64 = "garbage";
    Cookie[] cookies = new Cookie[] { new Cookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME, userPCAesBase64) };
    expect(mockRequest.getCookies()).andReturn(cookies).anyTimes();
    replay(mockRequest);
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    try {
      mgr.getRememberedPrincipals(context);
    } catch (IllegalArgumentException expected) {
      return;
    }
    fail("CryptoException was expected to be thrown");
  }

  @Test public void onLogout() {
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    org.apache.shiro.web.servlet.Cookie cookie = createMock(org.apache.shiro.web.servlet.Cookie.class);
    mgr.setCookie(cookie);
    HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
    WebSubject mockSubject = createNiceMock(WebSubject.class);
    expect(mockSubject.getServletRequest()).andReturn(mockRequest).anyTimes();
    expect(mockSubject.getServletResponse()).andReturn(mockResponse).anyTimes();
    expect(mockRequest.getContextPath()).andReturn(null).anyTimes();
    cookie.removeFrom(isA(HttpServletRequest.class), isA(HttpServletResponse.class));
    replay(mockRequest);
    replay(mockResponse);
    replay(mockSubject);
    replay(cookie);
    mgr.onLogout(mockSubject);
    verify(mockSubject);
    verify(mockRequest);
    verify(mockResponse);
    verify(cookie);
  }

  @Test public void shouldIgnoreInvalidCookieValues() {
    HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
    HttpServletResponse mockResponse = createMock(HttpServletResponse.class);
    WebSubjectContext context = new DefaultWebSubjectContext();
    context.setServletRequest(mockRequest);
    context.setServletResponse(mockResponse);
    CookieRememberMeManager mgr = new CookieRememberMeManager();
    Cookie[] cookies = new Cookie[] { new Cookie(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME, UUID.randomUUID().toString() + "%%ldapRealm") };
    expect(mockRequest.getAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY)).andReturn(null);
    expect(mockRequest.getContextPath()).andReturn(null);
    expect(mockRequest.getCookies()).andReturn(cookies);
    replay(mockRequest);
    final byte[] rememberedSerializedIdentity = mgr.getRememberedSerializedIdentity(context);
    assertNull("should ignore invalid cookie values", rememberedSerializedIdentity);
  }
}