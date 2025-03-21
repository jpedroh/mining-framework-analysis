package controllers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import ninja.NinjaTest;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.junit.Test;
import com.google.common.collect.Maps;

public class ApplicationControllerTest extends NinjaTest {
  @Test public void testThatRedirectWorks() {
    Map<String, String> headers = Maps.newHashMap();
    String result = ninjaTestBrowser.makeRequest(getServerAddress() + "/redirect", headers);
    assertTrue(result.contains("And developing large web applications becomes fun again."));
  }

  @Test public void testHtmlEscapingInTeamplateWorks() {
    String expectedContent = "&lt;script&gt;alert(\'Hello\');&lt;/script&gt;";
    Map<String, String> headers = Maps.newHashMap();
    String result = ninjaTestBrowser.makeRequest(getServerAddress() + "htmlEscaping", headers);
    assertTrue(result.contains(expectedContent));
  }

  @Test public void makeSureSessionsGetSentToClient() {
    Map<String, String> headers = Maps.newHashMap();
    HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(getServerAddress() + "session", headers);
    assertEquals(1, ninjaTestBrowser.getCookies().size());
    Cookie cookie = ninjaTestBrowser.getCookieWithName("NINJA_SESSION");
    assertTrue(cookie != null);
    assertTrue(cookie.getValue().contains("___TS"));
    assertTrue(cookie.getValue().contains("username"));
    assertTrue(cookie.getValue().contains("kevin"));
  }

  @Test public void testThatPathParamParsingWorks() {
    Map<String, String> headers = Maps.newHashMap();
    String response = ninjaTestBrowser.makeRequest(getServerAddress() + "user/12345/john@example.com/userDashboard", headers);
    assertTrue(response.contains("john@example.com"));
    assertTrue(response.contains("12345"));
    assertTrue(response.contains("By the way... Reverse url of this rawUrl is: /user/12345/john@example.com/userDashboard"));
  }

  @Test public void testThatValidationWorks() {
    Map<String, String> headers = Maps.newHashMap();
    String response = ninjaTestBrowser.makeRequest(getServerAddress() + "validation?email=john@example.com");
    assertEquals(response, "\"john@example.com\"");
    response = ninjaTestBrowser.makeRequest(getServerAddress() + "validation");
    assertEquals(response.trim(), "[{\"field\":\"email\",\"constraintViolation\":{\"messageKey\":\"validation.required.violation\",\"fieldKey\":\"email\",\"defaultMessage\":\"email is required\",\"messageParams\":[]}}]");
  }

  @Test public void testPostFormParsingWorks() {
    Map<String, String> headers = Maps.newHashMap();
    Map<String, String> formParameters = Maps.newHashMap();
    formParameters.put("description", "test3");
    formParameters.put("email", "test2@email.com");
    formParameters.put("name", "test1");
    String response = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/contactForm", headers, formParameters);
    assertTrue(response.contains("test3"));
    assertTrue(response.contains("test2@email.com"));
    assertTrue(response.contains("test1"));
  }

  @Test public void testFlashSuccessWorks() {
    String response = ninjaTestBrowser.makeRequest(getServerAddress() + "/flash_success");
    System.out.println("repinse: " + response);
    assertTrue(response.contains("This is a flashed success - with placeholder: PLACEHOLDER"));
  }

  @Test public void testFlashErrorWorks() {
    String response = ninjaTestBrowser.makeRequest(getServerAddress() + "/flash_error");
    System.out.println("repinse: " + response);
    assertTrue(response.contains("This is a flashed error - with placeholder: PLACEHOLDER"));
  }

  @Test public void testFlashAnyWorks() {
    String response = ninjaTestBrowser.makeRequest(getServerAddress() + "/flash_any");
    assertTrue(response.contains("This is an arbitrary message as flash message - with placeholder: PLACEHOLDER"));
  }
}