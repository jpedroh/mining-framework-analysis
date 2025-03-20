package controllers;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import models.User;
import ninja.NinjaTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.cookie.Cookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.Maps;

public class ApplicationHandlerTest extends NinjaTest {
  Map<String, String> headers = Maps.newHashMap();

  Map<String, String> formParams = Maps.newHashMap();

  String result;

  @Before public void setUp() {
    formParams.clear();
    headers.clear();
    headers.put("Accept-Language", "en-US");
  }

  @After public void tearDown() {
  }

  @Test public void testRegistrationPart() {
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "register");
    assertTrue(result.contains("form action=\"/register\""));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    assertNotNull(User.getUsrByMail("admin@localhost.de"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.put("firstName", "Nhoj");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    User user = User.getUsrByMail("admin@localhost.de");
    assertTrue(user.getForename().equals("John"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "");
    formParams.put("surName", "");
    formParams.put("mail", "");
    formParams.put("password", "");
    formParams.put("passwordNew1", "");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testRegistrationMails() {
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin.this.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "@this.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "@");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "blubb@");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testRegistrationPasswords() {
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "4321");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "Cats");
    formParams.put("passwordNew1", "cats");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "dogs");
    formParams.put("passwordNew1", "Dogs");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testVerification() {
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    User user = User.getUsrByMail("admin@localhost.de");
    assertNotNull(user);
    assertFalse(user.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    String random = RandomStringUtils.randomAlphanumeric(5);
    while (user.getConfirmation().equals(random)) {
      random = RandomStringUtils.randomAlphanumeric(5);
    }
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "verify/" + user.getId() + "/" + random);
    String expected = ninjaTestBrowser.makeRequest(getServerAddress() + "/login");
    assertTrue(result.equals(expected));
    User updateduser = User.getById(user.getId());
    assertFalse(updateduser.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "verify/" + user.getId() + "/" + user.getConfirmation());
    assertTrue(result.contains("class=\"alert alert-success\">"));
    updateduser = User.getById(user.getId());
    assertTrue(updateduser.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testGetLostPw() {
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    User user = User.getUsrByMail("admin@localhost.de");
    assertFalse(user.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "lostpw/" + user.getId() + "/" + user.getConfirmation());
    assertTrue(result.contains("form action=\"/lostpw"));
    String random = RandomStringUtils.randomAlphanumeric(5);
    while (user.getConfirmation().equals(random)) {
      random = RandomStringUtils.randomAlphanumeric(20);
    }
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "lostpw/" + user.getId() + "/" + random);
    String expected = ninjaTestBrowser.makeRequest(getServerAddress() + "/");
    assertTrue(result.equals(expected));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    User updatedUser = User.getById(user.getId());
    assertFalse(updatedUser.isActive());
  }

  @Test public void testLostPwForm() {
    formParams.clear();
    formParams.put("firstName", "John");
    formParams.put("surName", "Doe");
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    formParams.put("passwordNew1", "1234");
    formParams.put("language", "en");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "register", headers, formParams);
    User user = User.getUsrByMail("admin@localhost.de");
    assertFalse(user.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("password2", "abc");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "lostpw/" + user.getId() + "/" + user.getConfirmation(), headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertTrue(result.contains("form action=\"/lostpw"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("password", "123");
    formParams.put("password2", "abc");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "lostpw/" + user.getId() + "/" + user.getConfirmation(), headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertTrue(result.contains("form action=\"/lostpw"));
    User updateuser = User.getById(user.getId());
    assertFalse(updateuser.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("password", "1234");
    formParams.put("password2", "1234");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "lostpw/" + user.getId() + "/" + user.getConfirmation(), headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    updateuser = User.getById(user.getId());
    assertTrue(updateuser.isActive());
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "lostpw/" + user.getId() + "/" + user.getConfirmation(), headers, formParams);
    String expected = ninjaTestBrowser.makeRequest(getServerAddress());
    assertTrue(expected.equals(result));
    assertFalse(result.contains("FreeMarker template error"));
  }

  @Test public void testLogin() {
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "/login");
    assertTrue(result.contains("form action=\"/login\""));
    User user = new User("John", "Doe", "admin@localhost.de", "1234", "en");
    user.save();
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("mail", "admin00@this.de");
    formParams.put("password", "1234");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    Cookie flash = ninjaTestBrowser.getCookieWithName("XCMailr_FLASH");
    assertNotNull(flash);
    assertTrue(flash.getValue().contains("error"));
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("mail", "");
    formParams.put("password", "");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    flash = ninjaTestBrowser.getCookieWithName("XCMailr_FLASH");
    assertNotNull(flash);
    assertTrue(flash.getValue().contains("error"));
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "baum");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    flash = ninjaTestBrowser.getCookieWithName("XCMailr_FLASH");
    assertNotNull(flash);
    assertTrue(flash.getValue().contains("error"));
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    user = User.getById(user.getId());
    user.setActive(true);
    user.update();
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    Cookie cookie = ninjaTestBrowser.getCookieWithName("XCMailr_SESSION");
    assertTrue(cookie != null);
    assertTrue(cookie.getValue().contains("___TS"));
    assertTrue(cookie.getValue().contains("username"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testWrongLoginAccountDeactivation() {
    User user = new User("John", "Doe", "admin@localhost.de", "1234", "en");
    user.setActive(true);
    user.save();
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "12");
    for (int i = 0; i < 7; i++) {
      result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    }
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertTrue(result.contains("form action=\"/pwresend\""));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testLogout() {
    User user = new User("John", "Doe", "admin@localhost.de", "1234", "en");
    user.setActive(true);
    user.save();
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    Cookie cookie = ninjaTestBrowser.getCookieWithName("XCMailr_SESSION");
    assertTrue(cookie != null);
    assertTrue(cookie.getValue().contains("___TS"));
    assertTrue(cookie.getValue().contains("username"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "/logout", headers);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    cookie = ninjaTestBrowser.getCookieWithName("XCMailr_SESSION");
    assertTrue(cookie == null);
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testIndexPage() {
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "/");
    assertTrue(result.contains("<a href=\"/register\" class=\"createAccount\">"));
    User user = new User("John", "Doe", "admin@localhost.de", "1234", "en");
    user.setActive(true);
    user.save();
    formParams.clear();
    formParams.put("mail", "admin@localhost.de");
    formParams.put("password", "1234");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "/login", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    Cookie cookie = ninjaTestBrowser.getCookieWithName("XCMailr_SESSION");
    assertTrue(cookie != null);
    assertTrue(cookie.getValue().contains("___TS"));
    assertTrue(cookie.getValue().contains("username"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "/");
    assertTrue(result.contains("<a href=\"/logout\" class=\"logoutAccount\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }

  @Test public void testPwResend() {
    result = ninjaTestBrowser.makeRequest(getServerAddress() + "pwresend");
    assertTrue(result.contains("form action=\"/pwresend\""));
    formParams.clear();
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "pwresend", headers, formParams);
    assertTrue(result.contains("form action=\"/pwresend\""));
    assertTrue(result.contains("class=\"alert alert-danger\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    User user = new User("firstName", "surname", "admin@localhost.de", "1234", "en");
    user.setActive(true);
    user.save();
    formParams.put("mail", "admin@localhost.de");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "pwresend", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
    formParams.put("mail", "admin@xcmlr123456x.t2113ee");
    result = ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress() + "pwresend", headers, formParams);
    assertTrue(result.contains("class=\"alert alert-success\">"));
    assertFalse(result.contains("FreeMarker template error"));
    assertFalse(result.contains("<title>404 - not found</title>"));
  }
}