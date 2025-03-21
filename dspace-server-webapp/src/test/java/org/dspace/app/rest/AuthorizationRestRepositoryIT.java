package org.dspace.app.rest;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.Serializable;
import java.util.UUID;
import java.util.function.Supplier;
import com.jayway.jsonpath.matchers.JsonPathMatchers;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.authorization.AlwaysFalseFeature;
import org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature;
import org.dspace.app.rest.authorization.AlwaysTrueFeature;
import org.dspace.app.rest.authorization.Authorization;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureService;
import org.dspace.app.rest.authorization.TrueForAdminsFeature;
import org.dspace.app.rest.authorization.TrueForLoggedUsersFeature;
import org.dspace.app.rest.authorization.TrueForTestUsersFeature;
import org.dspace.app.rest.authorization.TrueForUsersInGroupTestFeature;
import org.dspace.app.rest.converter.CommunityConverter;
import org.dspace.app.rest.converter.EPersonConverter;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.converter.SiteConverter;
import org.dspace.app.rest.matcher.AuthorizationMatcher;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.CommunityRest;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.SiteRest;
import org.dspace.app.rest.projection.DefaultProjection;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.Site;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.SiteService;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Test suite for the Authorization endpoint
 * 
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 *
 */
public class AuthorizationRestRepositoryIT extends AbstractControllerIntegrationTest {
  private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AuthorizationRestRepositoryIT.class);

  @Autowired private AuthorizationFeatureService authorizationFeatureService;

  @Autowired private SiteConverter siteConverter;

  @Autowired private EPersonConverter ePersonConverter;

  @Autowired private CommunityConverter communityConverter;

  @Autowired private ConfigurationService configurationService;

  @Autowired private ItemConverter itemConverter;

  @Autowired private Utils utils;

  private SiteService siteService;

  /** 
     * this hold a reference to the test feature {@link AlwaysTrueFeature}
     */
  private AuthorizationFeature alwaysTrue;

  /** 
     * this hold a reference to the test feature {@link AlwaysFalseFeature}
     */
  private AuthorizationFeature alwaysFalse;

  /** 
     * this hold a reference to the test feature {@link AlwaysThrowExceptionFeature}
     */
  private AuthorizationFeature alwaysException;

  /** 
     * this hold a reference to the test feature {@link TrueForAdminsFeature}
     */
  private AuthorizationFeature trueForAdmins;

  /** 
     * this hold a reference to the test feature {@link TrueForLoggedUsersFeature}
     */
  private AuthorizationFeature trueForLoggedUsers;

  /** 
     * this hold a reference to the test feature {@link TrueForTestFeature}
     */
  private AuthorizationFeature trueForTestUsers;

  /** 
     * this hold a reference to the test feature {@link TrueForUsersInGroupTestFeature}
     */
  private AuthorizationFeature trueForUsersInGroupTest;

  @Override @Before public void setUp() throws Exception {
    super.setUp();
    siteService = ContentServiceFactory.getInstance().getSiteService();
    alwaysTrue = authorizationFeatureService.find(AlwaysTrueFeature.NAME);
    alwaysFalse = authorizationFeatureService.find(AlwaysFalseFeature.NAME);
    alwaysException = authorizationFeatureService.find(AlwaysThrowExceptionFeature.NAME);
    trueForAdmins = authorizationFeatureService.find(TrueForAdminsFeature.NAME);
    trueForLoggedUsers = authorizationFeatureService.find(TrueForLoggedUsersFeature.NAME);
    trueForTestUsers = authorizationFeatureService.find(TrueForTestUsersFeature.NAME);
    trueForUsersInGroupTest = authorizationFeatureService.find(TrueForUsersInGroupTestFeature.NAME);
    configurationService.setProperty("webui.user.assumelogin", true);
  }

  @Test public void findAllTest() throws Exception {
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations")).andExpect(status().isMethodNotAllowed());
    getClient().perform(get("/api/authz/authorizations")).andExpect(status().isMethodNotAllowed());
  }

  @Test public void findOneTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    Authorization authAdminSite = new Authorization(admin, trueForAdmins, siteRest);
    Authorization authNormalUserSite = new Authorization(eperson, trueForLoggedUsers, siteRest);
    Authorization authAnonymousUserSite = new Authorization(null, alwaysTrue, siteRest);
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authAdminSite))));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authNormalUserSite))));
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authNormalUserSite))));
    getClient().perform(get("/api/authz/authorizations/" + authAnonymousUserSite.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.is(AuthorizationMatcher.matchAuthorization(authAnonymousUserSite))));
  }

  @Test public void findOneUnauthorizedTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    Authorization authAdminSite = new Authorization(admin, alwaysTrue, siteRest);
    Authorization authNormalUserSite = new Authorization(eperson, alwaysTrue, siteRest);
    getClient().perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isUnauthorized());
  }

  @Test public void findOneForbiddenTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    EPerson testEPerson = EPersonBuilder.createEPerson(context).withEmail("test-authorization@example.com").withPassword(password).build();
    context.restoreAuthSystemState();
    Authorization authAdminSite = new Authorization(admin, alwaysTrue, siteRest);
    Authorization authNormalUserSite = new Authorization(eperson, alwaysTrue, siteRest);
    String testToken = getAuthToken(testEPerson.getEmail(), password);
    getClient(testToken).perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isForbidden());
    getClient(testToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isForbidden());
    Authorization noTestAuthForNormalUserSite = new Authorization(eperson, trueForTestUsers, siteRest);
    getClient(testToken).perform(get("/api/authz/authorizations/" + noTestAuthForNormalUserSite.getID())).andExpect(status().isForbidden());
  }

  @Test public void findOneNotFoundTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    EPersonRest epersonRest = ePersonConverter.convert(eperson, DefaultProjection.DEFAULT);
    context.restoreAuthSystemState();
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    String adminToken = getAuthToken(admin.getEmail(), password);
    Authorization authAdminSite = new Authorization(admin, alwaysFalse, siteRest);
    Authorization authNormalUserSite = new Authorization(eperson, alwaysFalse, siteRest);
    Authorization authAnonymousUserSite = new Authorization(null, alwaysFalse, siteRest);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isNotFound());
    getClient(epersonToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isNotFound());
    getClient(epersonToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isNotFound());
    getClient().perform(get("/api/authz/authorizations/" + authAnonymousUserSite.getID())).andExpect(status().isNotFound());
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authAnonymousUserSite.getID())).andExpect(status().isNotFound());
    String authInvalidType = getAuthorizationID(admin, trueForAdmins, epersonRest);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authInvalidType)).andExpect(status().isNotFound());
    String authNotExistingObject = getAuthorizationID(admin, alwaysTrue, ItemRest.CATEGORY + "." + ItemRest.NAME, UUID.randomUUID());
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authNotExistingObject)).andExpect(status().isNotFound());
    String authNotExistingEPerson = getAuthorizationID(UUID.randomUUID(), alwaysTrue, siteRest);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authNotExistingEPerson)).andExpect(status().isNotFound());
    String authNotExistingFeature = getAuthorizationID(admin, "notexistingfeature", siteRest);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authNotExistingFeature)).andExpect(status().isNotFound());
    Authorization noAdminAuthForNormalUserSite = new Authorization(eperson, trueForAdmins, siteRest);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + noAdminAuthForNormalUserSite.getID())).andExpect(status().isNotFound());
    String notValidID = "notvalidID";
    getClient(adminToken).perform(get("/api/authz/authorizations/" + notValidID)).andExpect(status().isNotFound());
    String notValidIDWithWrongEpersonPart = getAuthorizationID("1", alwaysTrue.getName(), SiteRest.CATEGORY + "." + SiteRest.NAME, site.getID().toString());
    getClient(adminToken).perform(get("/api/authz/authorizations/" + notValidIDWithWrongEpersonPart)).andExpect(status().isNotFound());
    String notValidIDWithWrongObjectTypePart = getAuthorizationID(eperson.getID().toString(), alwaysTrue.getName(), "SITE", site.getID().toString());
    getClient(epersonToken).perform(get("/api/authz/authorizations/" + notValidIDWithWrongObjectTypePart)).andExpect(status().isNotFound());
    String notValidIDWithUnknownObjectTypePart = getAuthorizationID(eperson.getID().toString(), alwaysTrue.getName(), "core.unknown", "1");
    getClient(epersonToken).perform(get("/api/authz/authorizations/" + notValidIDWithUnknownObjectTypePart)).andExpect(status().isNotFound());
  }

  @Test public void findOneInternalServerErrorTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    Authorization authAdminSite = new Authorization(admin, alwaysException, siteRest);
    Authorization authNormalUserSite = new Authorization(eperson, alwaysException, siteRest);
    String adminToken = getAuthToken(admin.getEmail(), password);
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isInternalServerError());
    getClient(epersonToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isInternalServerError());
  }

  @Test public void findByObjectTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri).param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(3)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()), is(trueForAdmins.getName()), is(trueForLoggedUsers.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(admin.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(3)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri)).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(3)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()), is(trueForAdmins.getName()), is(trueForLoggedUsers.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(admin.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(3)));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(2)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()), is(trueForLoggedUsers.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName()), is(trueForAdmins.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(eperson.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(2)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri)).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(2)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()), is(trueForLoggedUsers.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName()), is(trueForAdmins.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(eperson.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(2)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(2)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()), is(trueForLoggedUsers.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName()), is(trueForAdmins.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(eperson.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(2)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(2)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()), is(trueForLoggedUsers.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName()), is(trueForAdmins.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(eperson.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(2)));
    getClient().perform(get("/api/authz/authorizations/search/object").param("projection", "full").param("uri", siteUri)).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.authorizations", Matchers.hasSize(greaterThanOrEqualTo(1)))).andExpect(jsonPath("$._embedded.authorizations", Matchers.everyItem(Matchers.anyOf(JsonPathMatchers.hasJsonPath("$.type", is("authorization")), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.allOf(is(alwaysTrue.getName()))), JsonPathMatchers.hasJsonPath("$._embedded.feature", Matchers.not(Matchers.anyOf(is(alwaysFalse.getName()), is(alwaysException.getName()), is(trueForTestUsers.getName()), is(trueForAdmins.getName())))), JsonPathMatchers.hasJsonPath("$._embedded.feature.resourcetypes", Matchers.hasItem(is("authorization"))), JsonPathMatchers.hasJsonPath("$.id", Matchers.anyOf(Matchers.startsWith(eperson.getID().toString()), Matchers.endsWith(siteRest.getUniqueType() + "_" + siteRest.getId()))))))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(1)));
  }

  @Test public void findByNotExistingObjectTest() throws Exception {
    String wrongSiteUri = "http://localhost/api/core/sites/" + UUID.randomUUID();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri)).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri)).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri)).andExpect(status().isOk()).andExpect(jsonPath("$", JsonPathMatchers.hasNoJsonPath("$._embedded.authorizations"))).andExpect(jsonPath("$._links.self.href", Matchers.containsString("/api/authz/authorizations/search/object"))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(0)));
  }

  @Test public void findByObjectBadRequestTest() throws Exception {
    String[] invalidUris = new String[] { "invalid-uri", "", "http://localhost/api/wrongcategory/wrongmodel/1", "http://localhost/api/core/sites/this-is-not-an-uuid" };
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    for (String invalidUri : invalidUris) {
      log.debug("findByObjectBadRequestTest - Testing the URI: " + invalidUri);
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("eperson", admin.getID().toString())).andExpect(status().isBadRequest());
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri)).andExpect(status().isBadRequest());
      getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
      getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri)).andExpect(status().isBadRequest());
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isBadRequest());
      getClient().perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri)).andExpect(status().isBadRequest());
    }
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("eperson", admin.getID().toString())).andExpect(status().isBadRequest());
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/authz/authorizations/search/object")).andExpect(status().isBadRequest());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object")).andExpect(status().isBadRequest());
  }

  @Test public void findByObjectUnauthorizedTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("eperson", admin.getID().toString())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).header("X-On-Behalf-Of", admin.getID())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("eperson", eperson.getID().toString())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isUnauthorized());
  }

  @Test public void findByObjectForbiddenTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    context.turnOffAuthorisationSystem();
    EPerson anotherEperson = EPersonBuilder.createEPerson(context).withEmail("another@example.com").withPassword(password).build();
    context.restoreAuthSystemState();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String anotherToken = getAuthToken(anotherEperson.getEmail(), password);
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("eperson", admin.getID().toString())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).header("X-On-Behalf-Of", admin.getID())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("eperson", eperson.getID().toString())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isForbidden());
  }

  @Test public void findByObjectInternalServerErrorTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("size", "100").param("eperson", admin.getID().toString())).andExpect(status().isInternalServerError());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("size", "100")).andExpect(status().isInternalServerError());
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("size", "100").param("eperson", eperson.getID().toString())).andExpect(status().isInternalServerError());
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("size", "100")).andExpect(status().isInternalServerError());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("size", "100")).andExpect(status().isInternalServerError());
  }

  @Test public void findByObjectAndFeatureTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Community com = CommunityBuilder.createCommunity(context).withName("A test community").build();
    CommunityRest comRest = communityConverter.convert(com, DefaultProjection.DEFAULT);
    String comUri = utils.linkToSingleResource(comRest, "self").getHref();
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))))));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))))));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson", nullValue())))));
  }

  @Test public void findByObjectAndFeatureNotGrantedTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysFalse.getName()).param("eperson", admin.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysFalse.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForAdmins.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForAdmins.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForAdmins.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForAdmins.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForLoggedUsers.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
  }

  @Test public void findByNotExistingObjectAndFeatureTest() throws Exception {
    String wrongSiteUri = "http://localhost/api/core/sites/" + UUID.randomUUID();
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature").param("eperson", admin.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature")).andExpect(jsonPath("$.page.totalElements", is(0)));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature").param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature")).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature").param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature").header("X-On-Behalf-Of", eperson.getID())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", wrongSiteUri).param("feature", alwaysTrue.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", "not-existing-feature")).andExpect(jsonPath("$.page.totalElements", is(0)));
  }

  @Test public void findByObjectAndFeatureBadRequestTest() throws Exception {
    String[] invalidUris = new String[] { "invalid-uri", "", "http://localhost/api/wrongcategory/wrongmodel/1", "http://localhost/api/core/sites/this-is-not-an-uuid" };
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    for (String invalidUri : invalidUris) {
      log.debug("findByObjectAndFeatureBadRequestTest - Testing the URI: " + invalidUri);
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isBadRequest());
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
      getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
      getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
      getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isBadRequest());
      getClient().perform(get("/api/authz/authorizations/search/object").param("uri", invalidUri).param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
    }
  }

  @Test public void findByObjectAndFeatureUnauthorizedTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", admin.getID())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isUnauthorized());
  }

  @Test public void findByObjectAndFeatureForbiddenTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    context.turnOffAuthorisationSystem();
    EPerson anotherEperson = EPersonBuilder.createEPerson(context).withEmail("another@example.com").withPassword(password).build();
    context.restoreAuthSystemState();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String anotherToken = getAuthToken(anotherEperson.getEmail(), password);
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", admin.getID())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isForbidden());
  }

  @Test public void findByObjectAndFeatureInternalServerErrorTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysException.getName()).param("eperson", admin.getID().toString())).andExpect(status().isInternalServerError());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysException.getName())).andExpect(status().isInternalServerError());
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysException.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isInternalServerError());
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysException.getName())).andExpect(status().isInternalServerError());
    getClient().perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", alwaysException.getName())).andExpect(status().isInternalServerError());
  }

  @Test public void findByMultipleObjectsAndFeaturesTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Community com = CommunityBuilder.createCommunity(context).withName("A test community").build();
    String comId = com.getID().toString();
    CommunityRest comRest = communityConverter.convert(com, DefaultProjection.DEFAULT);
    Community secondCom = CommunityBuilder.createCommunity(context).withName("Another test community").build();
    String secondComId = secondCom.getID().toString();
    CommunityRest secondComRest = communityConverter.convert(secondCom, DefaultProjection.DEFAULT);
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    Supplier<MockHttpServletRequestBuilder> baseFeatureRequest = () -> get("/api/authz/authorizations/search/objects").param("type", "core.community").param("uuid", comId).param("uuid", secondComId).param("projection", "level").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName()).param("feature", alwaysFalse.getName()).param("feature", trueForLoggedUsers.getName()).param("feature", trueForAdmins.getName());
    getClient(adminToken).perform(baseFeatureRequest.get().param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(6))).andExpect(jsonPath("$._embedded.authorizations", containsInAnyOrder(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForAdmins.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForAdmins.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForAdmins.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForAdmins.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))))));
    getClient(adminToken).perform(baseFeatureRequest.get()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(6))).andExpect(jsonPath("$._embedded.authorizations", containsInAnyOrder(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForAdmins.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForAdmins.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForAdmins.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForAdmins.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))), allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))))));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(baseFeatureRequest.get().param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$._embedded.authorizations", containsInAnyOrder(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(epersonToken).perform(baseFeatureRequest.get()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$._embedded.authorizations", containsInAnyOrder(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(adminToken).perform(baseFeatureRequest.get().param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$._embedded.authorizations", containsInAnyOrder(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(adminToken).perform(baseFeatureRequest.get().header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))), allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient().perform(baseFeatureRequest.get()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$._embedded.authorizations", containsInAnyOrder(allOf(hasJsonPath("$.id", is(alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson", nullValue())), allOf(hasJsonPath("$.id", is(alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson", nullValue())))));
  }

  @Test public void findByMultipleObjectsAndFeaturesPaginationTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Community com = CommunityBuilder.createCommunity(context).withName("A test community").build();
    String comId = com.getID().toString();
    CommunityRest comRest = communityConverter.convert(com, DefaultProjection.DEFAULT);
    Community secondCom = CommunityBuilder.createCommunity(context).withName("Another test community").build();
    String secondComId = secondCom.getID().toString();
    CommunityRest secondComRest = communityConverter.convert(secondCom, DefaultProjection.DEFAULT);
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    Supplier<MockHttpServletRequestBuilder> baseFeatureRequest = () -> get("/api/authz/authorizations/search/objects").param("type", "core.community").param("uuid", comId).param("uuid", secondComId).param("projection", "level").param("page", "1").param("size", "1").param("embedLevelDepth", "1").param("feature", alwaysTrue.getName()).param("feature", alwaysFalse.getName()).param("feature", trueForLoggedUsers.getName()).param("feature", trueForAdmins.getName());
    getClient(adminToken).perform(baseFeatureRequest.get().param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(6))).andExpect(jsonPath("$.page.totalPages", is(6))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href", containsString("page=2"))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))))));
    getClient(adminToken).perform(baseFeatureRequest.get()).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(6))).andExpect(jsonPath("$.page.totalPages", is(6))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href", containsString("page=2"))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString()))))));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(baseFeatureRequest.get().param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$.page.totalPages", is(4))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href", containsString("page=2"))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(epersonToken).perform(baseFeatureRequest.get()).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$.page.totalPages", is(4))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href", containsString("page=2"))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(adminToken).perform(baseFeatureRequest.get().param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$.page.totalPages", is(4))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href", containsString("page=2"))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient(adminToken).perform(baseFeatureRequest.get().header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$.page.totalPages", is(4))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href", containsString("page=2"))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + trueForLoggedUsers.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(trueForLoggedUsers.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString()))))));
    getClient().perform(baseFeatureRequest.get()).andExpect(status().isOk()).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$._links.prev.href", containsString("page=0"))).andExpect(jsonPath("$._links.next.href").doesNotExist()).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(alwaysTrue.getName() + "_" + secondComRest.getUniqueType() + "_" + secondComRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson", nullValue())))));
  }

  @Test public void findByMultipleObjectsAndFeatureNotGrantedTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Community community = CommunityBuilder.createCommunity(context).build();
    Collection collection = CollectionBuilder.createCollection(context, community).build();
    Item item = ItemBuilder.createItem(context, collection).build();
    String itemId = item.getID().toString();
    ItemRest itemRest = itemConverter.convert(item, DefaultProjection.DEFAULT);
    Item secondItem = ItemBuilder.createItem(context, collection).build();
    String secondItemId = secondItem.getID().toString();
    ItemRest secondItemRest = itemConverter.convert(secondItem, DefaultProjection.DEFAULT);
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("eperson", admin.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("feature", alwaysFalse.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("feature", alwaysFalse.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("feature", trueForAdmins.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("feature", trueForAdmins.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("feature", trueForAdmins.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", itemId).param("uuid", secondItemId).param("feature", alwaysFalse.getName()).param("feature", trueForLoggedUsers.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
  }

  @Test public void findByNotExistingMultipleObjectsAndFeatureTest() throws Exception {
    String wrongSiteId = UUID.randomUUID().toString();
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-feature").param("eperson", admin.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", alwaysTrue.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", "not-existing-feature")).andExpect(jsonPath("$.page.totalElements", is(0)));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", "not-existing-feature").param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", alwaysTrue.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", "not-existing-feature")).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", "not-existing-feature").param("eperson", eperson.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", "not-existing-feature").header("X-On-Behalf-Of", eperson.getID())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", alwaysTrue.getName())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", wrongSiteId).param("feature", "not-existing-one").param("feature", "not-existing-feature")).andExpect(jsonPath("$.page.totalElements", is(0)));
  }

  @Test public void findByMultipleObjectsAndFeatureBadRequestTest() throws Exception {
    String[] invalidUris = new String[] { "foo", "", "boo-invalid", "this-is-not-an-uuid" };
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String adminToken = getAuthToken(admin.getEmail(), password);
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isBadRequest());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isBadRequest());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("uuid", "foo").param("uuid", "").param("uuid", "invalid").param("uuid", "this-is-not-an-uuid").param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "INVALID").param("uuid", UUID.randomUUID().toString()).param("uuid", UUID.randomUUID().toString()).param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("uuid", UUID.randomUUID().toString()).param("uuid", UUID.randomUUID().toString()).param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.item").param("feature", alwaysTrue.getName())).andExpect(status().isBadRequest());
  }

  @Test public void findByMultipleObjectsAndFeatureUnauthorizedTest() throws Exception {
    Site site = siteService.findSite(context);
    String siteId = site.getID().toString();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", admin.getID())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isUnauthorized());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isUnauthorized());
  }

  @Test public void findByMultipleObjectsAndFeatureForbiddenTest() throws Exception {
    Site site = siteService.findSite(context);
    String siteId = site.getID().toString();
    context.turnOffAuthorisationSystem();
    EPerson anotherEperson = EPersonBuilder.createEPerson(context).withEmail("another@example.com").withPassword(password).build();
    context.restoreAuthSystemState();
    configurationService.setProperty("org.dspace.app.rest.authorization.AlwaysThrowExceptionFeature.turnoff", true);
    String anotherToken = getAuthToken(anotherEperson.getEmail(), password);
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysFalse.getName()).param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysFalse.getName()).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", admin.getID())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysFalse.getName()).param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isForbidden());
    getClient(anotherToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysFalse.getName()).param("feature", alwaysTrue.getName()).header("X-On-Behalf-Of", eperson.getID())).andExpect(status().isForbidden());
  }

  @Test public void findByMultipleObjectsAndFeatureInternalServerErrorTest() throws Exception {
    Site site = siteService.findSite(context);
    String siteId = site.getID().toString();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysException.getName()).param("eperson", admin.getID().toString())).andExpect(status().isInternalServerError());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysException.getName())).andExpect(status().isInternalServerError());
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysException.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isInternalServerError());
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysException.getName())).andExpect(status().isInternalServerError());
    getClient().perform(get("/api/authz/authorizations/search/objects").param("type", "core.site").param("uuid", siteId).param("feature", alwaysException.getName())).andExpect(status().isInternalServerError());
  }

  @Test public void verifySpecialGroupMembershipTest() throws Exception {
    Site site = siteService.findSite(context);
    SiteRest siteRest = siteConverter.convert(site, DefaultProjection.DEFAULT);
    String siteUri = utils.linkToSingleResource(siteRest, "self").getHref();
    context.turnOffAuthorisationSystem();
    EPerson memberOfTestGroup = EPersonBuilder.createEPerson(context).withEmail("memberGroupTest@example.com").withPassword(password).build();
    EPerson normalUser = EPersonBuilder.createEPerson(context).withEmail("normal@example.com").withPassword(password).build();
    Group testGroup = GroupBuilder.createGroup(context).withName(TrueForUsersInGroupTestFeature.GROUP_NAME).addMember(memberOfTestGroup).build();
    context.restoreAuthSystemState();
    Authorization authAdminSite = new Authorization(admin, trueForUsersInGroupTest, siteRest);
    Authorization authMemberSite = new Authorization(memberOfTestGroup, trueForUsersInGroupTest, siteRest);
    Authorization authNormalUserSite = new Authorization(normalUser, trueForUsersInGroupTest, siteRest);
    String adminToken = getAuthToken(admin.getEmail(), password);
    String normalUserToken = getAuthToken(normalUser.getEmail(), password);
    String memberToken = getAuthToken(memberOfTestGroup.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isNotFound());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", admin.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isNotFound());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", normalUser.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(normalUserToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isNotFound());
    getClient(normalUserToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", normalUser.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authMemberSite.getID())).andExpect(status().isOk());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", memberOfTestGroup.getID().toString())).andExpect(status().isOk());
    getClient(memberToken).perform(get("/api/authz/authorizations/" + authMemberSite.getID())).andExpect(status().isOk());
    getClient(memberToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", memberOfTestGroup.getID().toString())).andExpect(status().isOk());
    configurationService.setProperty("authentication-password.login.specialgroup", TrueForUsersInGroupTestFeature.GROUP_NAME);
    adminToken = getAuthToken(admin.getEmail(), password);
    normalUserToken = getAuthToken(normalUser.getEmail(), password);
    memberToken = getAuthToken(memberOfTestGroup.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authAdminSite.getID())).andExpect(status().isOk());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", admin.getID().toString())).andExpect(status().isOk());
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isNotFound());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", normalUser.getID().toString())).andExpect(jsonPath("$.page.totalElements", is(0)));
    getClient(normalUserToken).perform(get("/api/authz/authorizations/" + authNormalUserSite.getID())).andExpect(status().isOk());
    getClient(normalUserToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", normalUser.getID().toString())).andExpect(status().isOk());
    getClient(adminToken).perform(get("/api/authz/authorizations/" + authMemberSite.getID())).andExpect(status().isOk());
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", memberOfTestGroup.getID().toString())).andExpect(status().isOk());
    getClient(memberToken).perform(get("/api/authz/authorizations/" + authMemberSite.getID())).andExpect(status().isOk());
    getClient(memberToken).perform(get("/api/authz/authorizations/search/object").param("uri", siteUri).param("feature", trueForUsersInGroupTest.getName()).param("eperson", memberOfTestGroup.getID().toString())).andExpect(status().isOk());
  }

  @Test public void findByObjectAndFeatureFullProjectionTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Community com = CommunityBuilder.createCommunity(context).withName("A test community").build();
    CommunityRest comRest = communityConverter.convert(com, DefaultProjection.DEFAULT);
    String comUri = utils.linkToSingleResource(comRest, "self").getHref();
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "full").param("feature", alwaysTrue.getName()).param("eperson", admin.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(admin.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(admin.getID().toString())), hasJsonPath("$._embedded.object.id", is(com.getID().toString())))))).andExpect(jsonPath("$._embedded.authorizations[0]._embedded.object._embedded.adminGroup", nullValue()));
    String epersonToken = getAuthToken(eperson.getEmail(), password);
    getClient(epersonToken).perform(get("/api/authz/authorizations/search/object").param("uri", comUri).param("projection", "full").param("feature", alwaysTrue.getName()).param("eperson", eperson.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$._embedded.authorizations", contains(allOf(hasJsonPath("$.id", is(eperson.getID().toString() + "_" + alwaysTrue.getName() + "_" + comRest.getUniqueType() + "_" + comRest.getId())), hasJsonPath("$.type", is("authorization")), hasJsonPath("$._embedded.feature.id", is(alwaysTrue.getName())), hasJsonPath("$._embedded.eperson.id", is(eperson.getID().toString())), hasJsonPath("$._embedded.object.id", is(com.getID().toString())))))).andExpect(jsonPath("$._embedded.authorizations[0]._embedded.object._embedded.adminGroup").doesNotExist());
  }

  private String getAuthorizationID(EPerson eperson, AuthorizationFeature feature, BaseObjectRest obj) {
    return getAuthorizationID(eperson != null ? eperson.getID().toString() : null, feature.getName(), obj.getUniqueType(), obj.getId());
  }

  private String getAuthorizationID(UUID epersonUuid, AuthorizationFeature feature, BaseObjectRest obj) {
    return getAuthorizationID(epersonUuid != null ? epersonUuid.toString() : null, feature.getName(), obj.getUniqueType(), obj.getId());
  }

  private String getAuthorizationID(EPerson eperson, String featureName, BaseObjectRest obj) {
    return getAuthorizationID(eperson != null ? eperson.getID().toString() : null, featureName, obj.getUniqueType(), obj.getId());
  }

  private String getAuthorizationID(EPerson eperson, AuthorizationFeature feature, String objUniqueType, Serializable objID) {
    return getAuthorizationID(eperson != null ? eperson.getID().toString() : null, feature.getName(), objUniqueType, objID);
  }

  private String getAuthorizationID(String epersonUuid, String featureName, String type, Serializable id) {
    return (epersonUuid != null ? epersonUuid + "_" : "") + featureName + "_" + type + "_" + id.toString();
  }
}