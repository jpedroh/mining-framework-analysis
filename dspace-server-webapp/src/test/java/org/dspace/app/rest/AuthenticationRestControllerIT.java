package org.dspace.app.rest;
import static java.lang.Thread.sleep;
import static org.dspace.app.rest.utils.RegexUtils.REGEX_UUID;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Base64;
import java.util.Map;
import javax.servlet.http.Cookie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.dspace.app.orcid.client.OrcidClient;
import org.dspace.app.orcid.client.OrcidConfiguration;
import org.dspace.app.orcid.model.OrcidTokenResponseDTO;
import org.dspace.app.rest.authorization.Authorization;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureService;
import org.dspace.app.rest.authorization.impl.CanChangePasswordFeature;
import org.dspace.app.rest.converter.EPersonConverter;
import org.dspace.app.rest.matcher.AuthenticationStatusMatcher;
import org.dspace.app.rest.matcher.AuthorizationMatcher;
import org.dspace.app.rest.matcher.EPersonMatcher;
import org.dspace.app.rest.matcher.GroupMatcher;
import org.dspace.app.rest.matcher.HalMatcher;
import org.dspace.app.rest.model.AuthnRest;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.projection.DefaultProjection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authenticate.OrcidAuthenticationBean;
import org.dspace.builder.BitstreamBuilder;
import org.dspace.builder.BundleBuilder;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * Integration test that covers various authentication scenarios
 *
 * @author Frederic Van Reet (frederic dot vanreet at atmire dot com)
 * @author Tom Desair (tom dot desair at atmire dot com)
 * @author Giuseppe Digilio (giuseppe dot digilio at 4science dot it)
 */
public class AuthenticationRestControllerIT extends AbstractControllerIntegrationTest {
  @Autowired ConfigurationService configurationService;

  @Autowired private EPersonConverter ePersonConverter;

  @Autowired private AuthorizationFeatureService authorizationFeatureService;

  @Autowired private OrcidConfiguration orcidConfiguration;

  @Autowired private OrcidAuthenticationBean orcidAuthentication;

  @Autowired private Utils utils;

  public static final String[] PASS_ONLY = { "org.dspace.authenticate.PasswordAuthentication" };

  public static final String[] SHIB_ONLY = { "org.dspace.authenticate.ShibAuthentication" };

  public static final String[] 
<<<<<<< /usr/src/app/output/dspace/dspace/7780bf3bd84f89cdfef3537c2f9edaba781d0a87/dspace-server-webapp/src/test/java/org/dspace/app/rest/AuthenticationRestControllerIT.java/left.java
  ORCID_ONLY = { "org.dspace.authenticate.OrcidAuthentication" }
=======
  PASS_AND_IP = { "org.dspace.authenticate.PasswordAuthentication", "org.dspace.authenticate.IPAuthentication" }
>>>>>>> /usr/src/app/output/dspace/dspace/7780bf3bd84f89cdfef3537c2f9edaba781d0a87/dspace-server-webapp/src/test/java/org/dspace/app/rest/AuthenticationRestControllerIT.java/right.java
  ;

  public static final String[] SHIB_AND_PASS = { "org.dspace.authenticate.ShibAuthentication", "org.dspace.authenticate.PasswordAuthentication" };

  public static final String[] SHIB_AND_IP = { "org.dspace.authenticate.IPAuthentication", "org.dspace.authenticate.ShibAuthentication" };

  public static final String TRUSTED_IP = "7.7.7.7";

  public static final String UNTRUSTED_IP = "8.8.8.8";

  private Authorization authorization;

  private EPersonRest ePersonRest;

  private final String feature = CanChangePasswordFeature.NAME;

  @Before public void setup() throws Exception {
    super.setUp();
    AuthorizationFeature canChangePasswordFeature = authorizationFeatureService.find(CanChangePasswordFeature.NAME);
    ePersonRest = ePersonConverter.convert(eperson, DefaultProjection.DEFAULT);
    authorization = new Authorization(eperson, canChangePasswordFeature, ePersonRest);
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", PASS_ONLY);
  }

  @Test public void testStatusAuthenticatedAsAdmin() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchFullEmbeds())).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchLinks())).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("password"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonWithGroups(admin.getEmail(), "Administrator")));
    getClient(token).perform(get("/api/authz/authorizations/" + authorization.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authorization))));
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$", HalMatcher.matchNoEmbeds()));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  /**
     * This test verifies:
     * - that a logged in via password user finds the expected specialGroupPwd in _embedded.specialGroups;
     * - that a logged in via password and specific IP user finds the expected specialGroupPwd and specialGroupIP
     *   in _embedded.specialGroups;
     * - that a not logged in user with a specific IP finds the expected specialGroupIP in _embedded.specialGroups;
     * @throws Exception
     */
  @Test public void testStatusGetSpecialGroups() throws Exception {
    context.turnOffAuthorisationSystem();
    Group specialGroupPwd = GroupBuilder.createGroup(context).withName("specialGroupPwd").build();
    Group specialGroupIP = GroupBuilder.createGroup(context).withName("specialGroupIP").build();
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", PASS_AND_IP);
    configurationService.setProperty("authentication-password.login.specialgroup", "specialGroupPwd");
    configurationService.setProperty("authentication-ip.specialGroupIP", "123.123.123.123");
    context.restoreAuthSystemState();
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchFullEmbeds())).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchLinks())).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("password"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.specialGroups.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.specialGroups._embedded.specialGroups", Matchers.containsInAnyOrder(GroupMatcher.matchGroupWithName("specialGroupPwd"))));
    getClient(token).perform(get("/api/authn/status/specialGroups").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.specialGroups", Matchers.containsInAnyOrder(GroupMatcher.matchGroupWithName("specialGroupPwd"))));
    getClient(token).perform(get("/api/authn/status").param("projection", "full").with(ip("123.123.123.123"))).andExpect(status().isOk()).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchFullEmbeds())).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchLinks())).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("password"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.specialGroups.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.specialGroups._embedded.specialGroups", Matchers.containsInAnyOrder(GroupMatcher.matchGroupWithName("specialGroupPwd"), GroupMatcher.matchGroupWithName("specialGroupIP"))));
    getClient(token).perform(get("/api/authn/status/specialGroups").param("projection", "full").with(ip("123.123.123.123"))).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.specialGroups", Matchers.containsInAnyOrder(GroupMatcher.matchGroupWithName("specialGroupPwd"), GroupMatcher.matchGroupWithName("specialGroupIP"))));
    getClient().perform(get("/api/authn/status").param("projection", "full").with(ip("123.123.123.123"))).andExpect(status().isOk()).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchFullEmbeds())).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$._embedded.specialGroups._embedded.specialGroups", Matchers.containsInAnyOrder(GroupMatcher.matchGroupWithName("specialGroupIP"))));
    getClient().perform(get("/api/authn/status/specialGroups").param("projection", "full").with(ip("123.123.123.123"))).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.specialGroups", Matchers.containsInAnyOrder(GroupMatcher.matchGroupWithName("specialGroupIP"))));
  }

  @Test @Ignore public void testStatusAuthenticatedAsNormalUser() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchFullEmbeds())).andExpect(jsonPath("$", AuthenticationStatusMatcher.matchLinks())).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("password"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonWithGroups(eperson.getEmail(), "Anonymous")));
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$", HalMatcher.matchNoEmbeds()));
    getClient(token).perform(get("/api/authz/authorizations/" + authorization.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authorization))));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testStatusNotAuthenticated() throws Exception {
    getClient().perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.authenticationMethod").doesNotExist()).andExpect(jsonPath("$.type", is("status"))).andExpect(header().string("WWW-Authenticate", "password realm=\"DSpace REST API\""));
    getClient().perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
  }

  @Test public void testStatusShibAuthenticatedWithCookie() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    String uiURL = configurationService.getProperty("dspace.ui.url");
    Cookie authCookie = getClient().perform(get("/api/authn/shibboleth").header("Referer", "https://myshib.example.com").param("redirectUrl", uiURL).requestAttr("SHIB-MAIL", eperson.getEmail()).requestAttr("SHIB-SCOPED-AFFILIATION", "faculty;staff")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(uiURL)).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN")).andExpect(cookie().exists(AUTHORIZATION_COOKIE)).andReturn().getResponse().getCookie(AUTHORIZATION_COOKIE);
    assertNotNull(authCookie);
    String token = authCookie.getValue();
    getClient().perform(get("/api/authn/status").header("Origin", uiURL).secure(true).cookie(authCookie)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("shibboleth"))).andExpect(jsonPath("$.type", is("status"))).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN"));
    getClient(token).perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
    String headerToken = getClient().perform(post("/api/authn/login").header("Origin", uiURL).secure(true).cookie(authCookie)).andExpect(status().isOk()).andExpect(cookie().value(AUTHORIZATION_COOKIE, "")).andExpect(header().exists(AUTHORIZATION_HEADER)).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN")).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    assertTrue("Check tokens " + token + " and " + headerToken + " have same claims", tokenClaimsEqual(token, headerToken));
    getClient(headerToken).perform(get("/api/authn/status").header("Origin", uiURL)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("shibboleth"))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
    getClient(headerToken).perform(post("/api/authn/logout").header("Origin", uiURL)).andExpect(status().isNoContent());
  }

  @Test public void testShibbolethEndpointCannotBeUsedWithShibDisabled() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", PASS_ONLY);
    String uiURL = configurationService.getProperty("dspace.ui.url");
    String token = getClient().perform(get("/api/authn/shibboleth").header("Referer", "https://myshib.example.com").param("redirectUrl", uiURL).requestAttr("SHIB-MAIL", eperson.getEmail()).requestAttr("SHIB-SCOPED-AFFILIATION", "faculty;staff")).andExpect(status().isUnauthorized()).andReturn().getResponse().getHeader("Authorization");
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.authenticationMethod").doesNotExist());
    getClient(token).perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
  }

  @Test public void testStatusPasswordAuthenticatedWithCookie() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    Cookie authCookie = new Cookie(AUTHORIZATION_COOKIE, token);
    getClient().perform(get("/api/authn/status").secure(true).cookie(authCookie)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status"))).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN"));
    String headerToken = getClient().perform(post("/api/authn/login").secure(true).cookie(authCookie)).andExpect(status().isOk()).andExpect(cookie().value(AUTHORIZATION_COOKIE, "")).andExpect(header().exists(AUTHORIZATION_HEADER)).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN")).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    assertTrue("Check tokens " + token + " and " + headerToken + " have same claims", tokenClaimsEqual(token, headerToken));
    getClient(headerToken).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(headerToken).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testTwoAuthenticationTokens() throws Exception {
    String token1 = getAuthToken(eperson.getEmail(), password);
    sleep(1200);
    String token2 = getAuthToken(eperson.getEmail(), password);
    assertNotEquals(token1, token2);
    assertTrue("Check tokens " + token1 + " and " + token2 + " have same claims", tokenClaimsEqual(token1, token2));
    getClient(token1).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonOnEmail(eperson.getEmail())));
    getClient(token2).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonOnEmail(eperson.getEmail())));
    getClient(token1).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testTamperingWithToken() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    context.turnOffAuthorisationSystem();
    Group internalGroup = GroupBuilder.createGroup(context).withName("Internal Group").build();
    context.restoreAuthSystemState();
    String[] jwtSplit = token.split("\\.");
    String tampered = new String(Base64.getUrlEncoder().encode(new String(Base64.getUrlDecoder().decode(token.split("\\.")[1])).replaceAll("\\[]", "[\"" + internalGroup.getID() + "\"]").getBytes()));
    String tamperedToken = jwtSplit[0] + "." + tampered + "." + jwtSplit[2];
    getClient(tamperedToken).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testLogout() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authn/logout")).andExpect(status().isMethodNotAllowed()).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN"));
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent()).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN"));
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.authenticationMethod").doesNotExist()).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
  }

  @Test public void testLogoutInvalidatesAllTokens() throws Exception {
    String token1 = getAuthToken(eperson.getEmail(), password);
    sleep(1200);
    String token2 = getAuthToken(eperson.getEmail(), password);
    assertNotEquals(token1, token2);
    getClient(token1).perform(post("/api/authn/logout"));
    getClient(token1).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.type", is("status")));
    getClient(token2).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.type", is("status")));
  }

  @Test public void testRefreshToken() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    sleep(1200);
    String newToken = getClient(token).perform(post("/api/authn/login")).andExpect(status().isOk()).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN")).andExpect(cookie().value("DSPACE-XSRF-COOKIE", "")).andExpect(header().string("DSPACE-XSRF-TOKEN", matchesPattern(REGEX_UUID))).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    assertNotEquals(token, newToken);
    assertTrue("Check tokens " + token + " and " + newToken + " have same claims", tokenClaimsEqual(token, newToken));
    getClient(newToken).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("password"))).andExpect(jsonPath("$.type", is("status")));
    getClient(newToken).perform(get("/api/authz/authorizations/" + authorization.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authorization))));
    getClient(newToken).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testRefreshTokenWithInvalidCSRF() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    Cookie[] cookies = new Cookie[1];
    cookies[0] = new Cookie(AUTHORIZATION_COOKIE, token);
    getClient().perform(post("/api/authn/login").with(csrf().useInvalidToken().asHeader()).secure(true).cookie(cookies)).andExpect(status().isForbidden()).andExpect(status().reason(containsString("Invalid CSRF token"))).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN"));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testLoginChangesCSRFToken() throws Exception {
    String token = getClient().perform(post("/api/authn/login").param("user", eperson.getEmail()).param("password", password)).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN")).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testCannotReuseTokenFromUntrustedOrigin() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authn/status").header("Origin", "https://example.org")).andExpect(status().isForbidden());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testCannotAuthenticateFromUntrustedOrigin() throws Exception {
    getClient().perform(post("/api/authn/login").header("Origin", "https://example.org").param("user", eperson.getEmail()).param("password", password)).andExpect(status().isForbidden());
  }

  @Test public void testReuseTokenWithDifferentIP() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authn/status").header("X-FORWARDED-FOR", "1.1.1.1")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authn/status").with(ip("1.1.1.1"))).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testFailedLoginResponseCode() throws Exception {
    getClient().perform(post("/api/authn/login").param("user", eperson.getEmail()).param("password", "fakePassword")).andExpect(status().isUnauthorized());
  }

  @Test public void testLoginLogoutStatusLink() throws Exception {
    getClient().perform(get("/api/authn")).andExpect(status().isOk()).andExpect(jsonPath("$._links.login.href", endsWith("login"))).andExpect(jsonPath("$._links.logout.href", endsWith("logout"))).andExpect(jsonPath("$._links.status.href", endsWith("status")));
  }

  /**
     * Check if we can just request a new token after we logged out
     *
     * @throws Exception
     */
  @Test public void testLoginAgainAfterLogout() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.type", is("status")));
    token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testLoginEmptyRequest() throws Exception {
    getClient().perform(post("/api/authn/login")).andExpect(status().isUnauthorized()).andExpect(status().reason(containsString("Authentication failed")));
  }

  @Test public void testLoginGetRequest() throws Exception {
    getClient().perform(get("/api/authn/login").param("user", eperson.getEmail()).param("password", password)).andExpect(status().isMethodNotAllowed());
  }

  @Test public void testShibbolethLoginURLWithDefaultLazyURL() throws Exception {
    context.turnOffAuthorisationSystem();
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    Group reviewersGroup = GroupBuilder.createGroup(context).withName("Reviewers").build();
    configurationService.setProperty("authentication-shibboleth.role.faculty", "Reviewers");
    context.restoreAuthSystemState();
    getClient().perform(post("/api/authn/login").header("Referer", "http://my.uni.edu")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"https://localhost/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
  }

  @Test public void testShibbolethLoginURLWithServerURLContainingPort() throws Exception {
    context.turnOffAuthorisationSystem();
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    configurationService.setProperty("dspace.server.url", "http://localhost:8080/server");
    configurationService.setProperty("authentication-shibboleth.lazysession.secure", false);
    Group reviewersGroup = GroupBuilder.createGroup(context).withName("Reviewers").build();
    configurationService.setProperty("authentication-shibboleth.role.faculty", "Reviewers");
    context.restoreAuthSystemState();
    getClient().perform(post("/api/authn/login").header("Referer", "http://my.uni.edu")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"http://localhost:8080/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%3A8080%2Fserver%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
  }

  @Test public void testShibbolethLoginURLWithConfiguredLazyURL() throws Exception {
    context.turnOffAuthorisationSystem();
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    configurationService.setProperty("authentication-shibboleth.lazysession.loginurl", "http://shibboleth.org/Shibboleth.sso/Login");
    Group reviewersGroup = GroupBuilder.createGroup(context).withName("Reviewers").build();
    configurationService.setProperty("authentication-shibboleth.role.faculty", "Reviewers");
    context.restoreAuthSystemState();
    getClient().perform(post("/api/authn/login").header("Referer", "http://my.uni.edu")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"http://shibboleth.org/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
  }

  @Test public void testShibbolethLoginURLWithConfiguredLazyURLWithPort() throws Exception {
    context.turnOffAuthorisationSystem();
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    configurationService.setProperty("authentication-shibboleth.lazysession.loginurl", "http://shibboleth.org:8080/Shibboleth.sso/Login");
    Group reviewersGroup = GroupBuilder.createGroup(context).withName("Reviewers").build();
    configurationService.setProperty("authentication-shibboleth.role.faculty", "Reviewers");
    context.restoreAuthSystemState();
    getClient().perform(post("/api/authn/login").header("Referer", "http://my.uni.edu")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"http://shibboleth.org:8080/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
  }

  @Test @Ignore public void testShibbolethLoginRequestAttribute() throws Exception {
    context.turnOffAuthorisationSystem();
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    Group reviewersGroup = GroupBuilder.createGroup(context).withName("Reviewers").build();
    configurationService.setProperty("authentication-shibboleth.role.faculty", "Reviewers");
    context.restoreAuthSystemState();
    getClient().perform(post("/api/authn/login").header("Referer", "http://my.uni.edu")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"https://localhost/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
    String token = getClient().perform(post("/api/authn/login").requestAttr("SHIB-MAIL", eperson.getEmail()).requestAttr("SHIB-SCOPED-AFFILIATION", "faculty;staff")).andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    getClient(token).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("shibboleth"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonWithGroups(eperson.getEmail(), "Anonymous", "Reviewers")));
    getClient(token).perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test @Ignore public void testShibbolethLoginRequestHeaderWithIpAuthentication() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_AND_IP);
    configurationService.setProperty("authentication-ip.Administrator", "123.123.123.123");
    getClient().perform(post("/api/authn/login").header("Referer", "http://my.uni.edu").with(ip("123.123.123.123"))).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "ip realm=\"DSpace REST API\", shibboleth realm=\"DSpace REST API\", " + "location=\"https://localhost/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
    String token = getClient().perform(post("/api/authn/login").with(ip("123.123.123.123")).header("SHIB-MAIL", eperson.getEmail())).andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    getClient(token).perform(get("/api/authn/status").param("projection", "full").with(ip("123.123.123.123"))).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("shibboleth"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonWithGroups(eperson.getEmail(), "Anonymous", "Administrator")));
    token = getClient().perform(post("/api/authn/login").with(ip("234.234.234.234")).header("SHIB-MAIL", eperson.getEmail())).andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    getClient(token).perform(get("/api/authn/status").param("projection", "full").with(ip("234.234.234.234"))).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("shibboleth"))).andExpect(jsonPath("$.type", is("status"))).andExpect(jsonPath("$._links.eperson.href", startsWith(REST_SERVER_URL))).andExpect(jsonPath("$._embedded.eperson", EPersonMatcher.matchEPersonWithGroups(eperson.getEmail(), "Anonymous")));
    getClient(token).perform(get("/api/authz/authorizations/search/object").param("embed", "feature").param("feature", feature).param("uri", utils.linkToSingleResource(ePersonRest, "self").getHref())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0))).andExpect(jsonPath("$._embedded").doesNotExist());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShibbolethAndPasswordAuthentication() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_AND_PASS);
    getClient().perform(get("/api/authn/status").header("Referer", "http://my.uni.edu")).andExpect(status().isOk()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"https://localhost/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\"" + ", password realm=\"DSpace REST API\""));
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("password"))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(get("/api/authz/authorizations/" + authorization.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authorization))));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.authenticationMethod").doesNotExist()).andExpect(jsonPath("$.type", is("status")));
    token = getClient().perform(post("/api/authn/login").requestAttr("SHIB-MAIL", eperson.getEmail()).requestAttr("SHIB-SCOPED-AFFILIATION", "faculty;staff")).andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("shibboleth"))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.authenticationMethod").doesNotExist()).andExpect(jsonPath("$.type", is("status")));
  }

  @Test public void testOnlyPasswordAuthenticationWorks() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", PASS_ONLY);
    getClient().perform(get("/api/authn/status").header("Referer", "http://my.uni.edu")).andExpect(status().isOk()).andExpect(header().string("WWW-Authenticate", "password realm=\"DSpace REST API\""));
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.type", is("status")));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.type", is("status")));
  }

  @Test public void testShibbolethAuthenticationDoesNotWorkWithPassOnly() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", PASS_ONLY);
    getClient().perform(get("/api/authn/status").header("Referer", "http://my.uni.edu")).andExpect(status().isOk()).andExpect(header().string("WWW-Authenticate", "password realm=\"DSpace REST API\""));
    getClient().perform(post("/api/authn/login").requestAttr("SHIB-MAIL", eperson.getEmail()).requestAttr("SHIB-SCOPED-AFFILIATION", "faculty;staff")).andExpect(status().isUnauthorized());
  }

  @Test public void testOnlyShibbolethAuthenticationWorks() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    getClient().perform(get("/api/authn/status").header("Referer", "http://my.uni.edu")).andExpect(status().isOk()).andExpect(header().string("WWW-Authenticate", "shibboleth realm=\"DSpace REST API\", " + "location=\"https://localhost/Shibboleth.sso/Login?" + "target=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Fshibboleth%3F" + "redirectUrl%3Dhttp%3A%2F%2Fmy.uni.edu\""));
    String token = getClient().perform(post("/api/authn/login").requestAttr("SHIB-MAIL", eperson.getEmail()).requestAttr("SHIB-SCOPED-AFFILIATION", "faculty;staff")).andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
    getClient(token).perform(get("/api/authn/status")).andExpect(status().isOk()).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(false))).andExpect(jsonPath("$.type", is("status")));
  }

  @Test public void testPasswordAuthenticationDoesNotWorkWithShibOnly() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", SHIB_ONLY);
    getClient().perform(post("/api/authn/login").param("user", eperson.getEmail()).param("password", password)).andExpect(status().isUnauthorized());
  }

  @Test public void testShortLivedToken() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    String salt = eperson.getSessionSalt();
    getClient(token).perform(post("/api/authn/shortlivedtokens")).andExpect(jsonPath("$.token", notNullValue())).andExpect(jsonPath("$.type", is("shortlivedtoken"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authn/shortlivedtokens"))).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN"));
    assertEquals(salt, eperson.getSessionSalt());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedTokenUsingGet() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    String salt = eperson.getSessionSalt();
    getClient(token).perform(get("/api/authn/shortlivedtokens").with(ip(TRUSTED_IP))).andExpect(status().isOk()).andExpect(jsonPath("$.token", notNullValue())).andExpect(jsonPath("$.type", is("shortlivedtoken"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authn/shortlivedtokens"))).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN"));
    assertEquals(salt, eperson.getSessionSalt());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedTokenUsingGetFromUntrustedIpShould403() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/shortlivedtokens").with(ip(UNTRUSTED_IP))).andExpect(status().isForbidden());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedTokenUsingGetFromUntrustedIpWithForwardHeaderShould403() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/authn/shortlivedtokens").with(ip(UNTRUSTED_IP)).header("X-Forwarded-For", TRUSTED_IP)).andExpect(status().isForbidden());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedTokenWithCSRFSentViaParam() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(post("/api/authn/shortlivedtokens").with(csrf())).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN"));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedTokenNotAuthenticated() throws Exception {
    getClient().perform(post("/api/authn/shortlivedtokens")).andExpect(status().isUnauthorized());
  }

  @Test public void testShortLivedTokenNotAuthenticatedUsingGet() throws Exception {
    getClient().perform(get("/api/authn/shortlivedtokens").with(ip(TRUSTED_IP))).andExpect(status().isUnauthorized());
  }

  @Test public void testShortLivedTokenToDownloadBitstream() throws Exception {
    Bitstream bitstream = createPrivateBitstream();
    String token = getAuthToken(eperson.getEmail(), password);
    String shortLivedToken = getShortLivedToken(token);
    getClient().perform(get("/api/core/bitstreams/" + bitstream.getID() + "/content?authentication-token=" + shortLivedToken)).andExpect(status().isOk());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedTokenToDownloadBitstreamUnauthorized() throws Exception {
    Bitstream bitstream = createPrivateBitstream();
    context.turnOffAuthorisationSystem();
    EPerson testEPerson = EPersonBuilder.createEPerson(context).withNameInMetadata("John", "Doe").withEmail("UnauthorizedUser@example.com").withPassword(password).build();
    context.restoreAuthSystemState();
    String token = getAuthToken(testEPerson.getEmail(), password);
    String shortLivedToken = getShortLivedToken(token);
    getClient().perform(get("/api/core/bitstreams/" + bitstream.getID() + "/content?authentication-token=" + shortLivedToken)).andExpect(status().isForbidden());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testLoginTokenToDownloadBitstream() throws Exception {
    Bitstream bitstream = createPrivateBitstream();
    String loginToken = getAuthToken(eperson.getEmail(), password);
    getClient().perform(get("/api/core/bitstreams/" + bitstream.getID() + "/content?authentication-token=" + loginToken)).andExpect(status().isUnauthorized());
    getClient(loginToken).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testExpiredShortLivedTokenToDownloadBitstream() throws Exception {
    Bitstream bitstream = createPrivateBitstream();
    configurationService.setProperty("jwt.shortLived.token.expiration", "1");
    String token = getAuthToken(eperson.getEmail(), password);
    String shortLivedToken = getShortLivedToken(token);
    Thread.sleep(1);
    getClient().perform(get("/api/core/bitstreams/" + bitstream.getID() + "/content?authentication-token=" + shortLivedToken)).andExpect(status().isUnauthorized());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testShortLivedAndLoginTokenSeparation() throws Exception {
    configurationService.setProperty("jwt.shortLived.token.expiration", "1");
    String token = getAuthToken(eperson.getEmail(), password);
    Thread.sleep(2);
    getClient(token).perform(get("/api/authn/status").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.authenticated", is(true)));
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test(expected = Exception.class) public void testLoginWithShortLivedToken() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    String shortLivedToken = getShortLivedToken(token);
    getClient().perform(post("/api/authn/login?authentication-token=" + shortLivedToken)).andExpect(status().isInternalServerError());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testGenerateShortLivedTokenWithShortLivedToken() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    String shortLivedToken = getShortLivedToken(token);
    getClient().perform(post("/api/authn/shortlivedtokens?authentication-token=" + shortLivedToken)).andExpect(status().isForbidden());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testGenerateShortLivedTokenWithShortLivedTokenUsingGet() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    String shortLivedToken = getShortLivedToken(token);
    getClient().perform(get("/api/authn/shortlivedtokens?authentication-token=" + shortLivedToken).with(ip(TRUSTED_IP))).andExpect(status().isForbidden());
    getClient(token).perform(post("/api/authn/logout")).andExpect(status().isNoContent());
  }

  @Test public void testStatusOrcidAuthenticatedWithCookie() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", ORCID_ONLY);
    String uiURL = configurationService.getProperty("dspace.ui.url");
    context.turnOffAuthorisationSystem();
    String orcid = "0000-1111-2222-3333";
    String code = "123456";
    String orcidAccessToken = "c41e37e5-c2de-4177-91d6-ed9e9d1f31bf";
    EPersonBuilder.createEPerson(context).withEmail("test@email.it").withNetId(orcid).withNameInMetadata("Test", "User").withCanLogin(true).build();
    context.restoreAuthSystemState();
    OrcidClient orcidClientMock = mock(OrcidClient.class);
    when(orcidClientMock.getAccessToken(code)).thenReturn(buildOrcidTokenResponse(orcid, orcidAccessToken));
    OrcidClient originalOrcidClient = orcidAuthentication.getOrcidClient();
    orcidAuthentication.setOrcidClient(orcidClientMock);
    Cookie authCookie = null;
    try {
      authCookie = getClient().perform(get("/api/" + AuthnRest.CATEGORY + "/orcid").param("redirectUrl", uiURL).param("code", code)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(uiURL)).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN")).andExpect(cookie().exists(AUTHORIZATION_COOKIE)).andReturn().getResponse().getCookie(AUTHORIZATION_COOKIE);
    }  finally {
      orcidAuthentication.setOrcidClient(originalOrcidClient);
    }
    assertNotNull(authCookie);
    String token = authCookie.getValue();
    getClient().perform(get("/api/authn/status").header("Origin", uiURL).secure(true).cookie(authCookie)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("orcid"))).andExpect(jsonPath("$.type", is("status"))).andExpect(cookie().doesNotExist("DSPACE-XSRF-COOKIE")).andExpect(header().doesNotExist("DSPACE-XSRF-TOKEN"));
    String headerToken = getClient().perform(post("/api/authn/login").header("Origin", uiURL).secure(true).cookie(authCookie)).andExpect(status().isOk()).andExpect(cookie().value(AUTHORIZATION_COOKIE, "")).andExpect(header().exists(AUTHORIZATION_HEADER)).andExpect(cookie().exists("DSPACE-XSRF-COOKIE")).andExpect(header().exists("DSPACE-XSRF-TOKEN")).andReturn().getResponse().getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_TYPE, "");
    assertTrue("Check tokens " + token + " and " + headerToken + " have same claims", tokenClaimsEqual(token, headerToken));
    getClient(headerToken).perform(get("/api/authn/status").header("Origin", uiURL)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.okay", is(true))).andExpect(jsonPath("$.authenticated", is(true))).andExpect(jsonPath("$.authenticationMethod", is("orcid"))).andExpect(jsonPath("$.type", is("status")));
    getClient(headerToken).perform(post("/api/authn/logout").header("Origin", uiURL)).andExpect(status().isNoContent());
  }

  @Test public void testOrcidLoginURL() throws Exception {
    configurationService.setProperty("plugin.sequence.org.dspace.authenticate.AuthenticationMethod", ORCID_ONLY);
    String originalClientId = orcidConfiguration.getClientId();
    orcidConfiguration.setClientId("CLIENT-ID");
    try {
      getClient().perform(post("/api/authn/login")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "orcid realm=\"DSpace REST API\", " + "location=\"https://sandbox.orcid.org/oauth/authorize?client_id=CLIENT-ID&response_type=code" + "&scope=/authenticate+/read-limited+/activities/update+/person/update&redirect_uri" + "=http%3A%2F%2Flocalhost%2Fapi%2Fauthn%2Forcid\""));
    }  finally {
      orcidConfiguration.setClientId(originalClientId);
    }
  }

  private String getShortLivedToken(String loginToken) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    MvcResult mvcResult = getClient(loginToken).perform(post("/api/authn/shortlivedtokens")).andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    Map<String, Object> map = mapper.readValue(content, Map.class);
    return String.valueOf(map.get("token"));
  }

  private Bitstream createPrivateBitstream() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, parentCommunity).withName("Collection 1").build();
    Item publicItem1 = ItemBuilder.createItem(context, col1).withTitle("Test").withIssueDate("2010-10-17").withAuthor("Smith, Donald").withSubject("ExtraEntry").build();
    Bundle bundle1 = BundleBuilder.createBundle(context, publicItem1).withName("TEST BUNDLE").build();
    Group staffGroup = GroupBuilder.createGroup(context).withName("Staff").addMember(eperson).build();
    String bitstreamContent = "ThisIsSomeDummyText";
    Bitstream bitstream = null;
    try (InputStream is = IOUtils.toInputStream(bitstreamContent, CharEncoding.UTF_8)) {
      bitstream = BitstreamBuilder.createBitstream(context, bundle1, is).withName("Bitstream").withDescription("description").withMimeType("text/plain").withReaderGroup(staffGroup).build();
    }
    context.restoreAuthSystemState();
    return bitstream;
  }

  /**
     * Check if the claims (except for expiration date) are equal between two JWTs.
     * Expiration date (exp) claim is ignored as it includes a timestamp and therefore changes every second.
     * So, this method checks to ensure token equality by comparing all other claims.
     *
     * @param token1 first token
     * @param token2 second token
     * @return True if tokens are identical or have the same claims (ignoring "exp"). False otherwise.
     */
  private boolean tokenClaimsEqual(String token1, String token2) {
    if (token1.equals(token2)) {
      return true;
    }
    try {
      SignedJWT jwt1 = SignedJWT.parse(token1);
      SignedJWT jwt2 = SignedJWT.parse(token2);
      JWTClaimsSet jwt1ClaimsSet = jwt1.getJWTClaimsSet();
      JWTClaimsSet jwt2ClaimsSet = jwt2.getJWTClaimsSet();
      Map<String, Object> jwt1Claims = jwt1ClaimsSet.getClaims();
      for (String claim : jwt1Claims.keySet()) {
        if (claim.equals("exp")) {
          continue;
        }
        if (!jwt1ClaimsSet.getClaim(claim).equals(jwt2ClaimsSet.getClaim(claim))) {
          return false;
        }
      }
      return true;
    } catch (ParseException e) {
      return false;
    }
  }

  private OrcidTokenResponseDTO buildOrcidTokenResponse(String orcid, String accessToken) {
    OrcidTokenResponseDTO token = new OrcidTokenResponseDTO();
    token.setAccessToken(accessToken);
    token.setOrcid(orcid);
    token.setTokenType("Bearer");
    token.setName("Test User");
    token.setScope(String.join(" ", new String[] { "FirstScope", "SecondScope" }));
    return token;
  }
}