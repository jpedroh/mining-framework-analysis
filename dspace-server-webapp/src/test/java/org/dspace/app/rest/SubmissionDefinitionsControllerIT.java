package org.dspace.app.rest;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.dspace.app.rest.matcher.SubmissionDefinitionsMatcher;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.content.Collection;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * Integration test to test the /api/config/submissiondefinitions endpoint
 * (Class has to start or end with IT to be picked up by the failsafe plugin)
 */
public class SubmissionDefinitionsControllerIT extends AbstractControllerIntegrationTest {
  @Test public void findAll() throws Exception {
    getClient().perform(get("/api/config/submissiondefinitions")).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(1))).andExpect(jsonPath("$.page.totalPages", greaterThanOrEqualTo(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.search.href", is(REST_SERVER_URL + "config/submissiondefinitions/search"))).andExpect(jsonPath("$._embedded.submissiondefinitions", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test public void findAllWithNewlyCreatedAccountTest() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(1))).andExpect(jsonPath("$.page.totalPages", greaterThanOrEqualTo(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.search.href", is(REST_SERVER_URL + "config/submissiondefinitions/search"))).andExpect(jsonPath("$._embedded.submissiondefinitions", hasSize(greaterThanOrEqualTo(1))));
  }

  @Test public void findDefault() throws Exception {
    getClient().perform(get("/api/config/submissiondefinitions/traditional")).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions/traditional").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$", SubmissionDefinitionsMatcher.matchFullEmbeds())).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", SubmissionDefinitionsMatcher.matchSubmissionDefinition(true, "traditional", "traditional")));
  }

  @Test public void findOneWithNewlyCreatedAccountTest() throws Exception {
    String tokenEPerson = getAuthToken(eperson.getEmail(), password);
    getClient(tokenEPerson).perform(get("/api/config/submissiondefinitions/traditional")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", allOf(hasJsonPath("$.isDefault", is(true)), hasJsonPath("$.name", is("traditional")), hasJsonPath("$.id", is("traditional")), hasJsonPath("$.type", is("submissiondefinition")))));
  }

  @Test public void findByCollection() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, parentCommunity).withName("Collection 1").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/config/submissiondefinitions/search/findByCollection").param("uuid", col1.getID().toString())).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions/search/findByCollection").param("uuid", col1.getID().toString())).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", SubmissionDefinitionsMatcher.matchSubmissionDefinition(true, "traditional", "traditional")));
  }

  @Test public void findByCollectionWithNewlyCreatedAccountTest() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, parentCommunity).withName("Collection 1").build();
    context.restoreAuthSystemState();
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions/search/findByCollection").param("uuid", col1.getID().toString())).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", SubmissionDefinitionsMatcher.matchSubmissionDefinition(true, "traditional", "traditional")));
  }

  @Test public void findCollections() throws Exception {
    getClient().perform(get("/api/config/submissiondefinitions/traditional/collections")).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions/traditional/collections").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", is(0)));
  }

  @Test public void findSections() throws Exception {
    getClient().perform(get("/api/config/submissiondefinitions/traditional/sections")).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissiondefinitions/traditional/sections").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissionsections", hasSize(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$._embedded.submissionsections", Matchers.hasItem(allOf(hasJsonPath("$.id", is("traditionalpageone")), hasJsonPath("$.sectionType", is("submission-form")), hasJsonPath("$.type", is("submissionsection")), hasJsonPath("$._links.config.href", is(REST_SERVER_URL + "config/submissionforms/traditionalpageone")), hasJsonPath("$._links.self.href", is(REST_SERVER_URL + "config/submissionsections/traditionalpageone"))))));
    getClient(token).perform(get("/api/config/submissiondefinitions/extractiontestprocess/sections").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissionsections", hasSize(6))).andExpect(jsonPath("$._embedded.submissionsections", Matchers.not(Matchers.hasItem(hasJsonPath("$.id", is("extractionstep"))))));
  }

  @Test public void findAllPaginationTest() throws Exception {
    String tokenAdmin = getAuthToken(admin.getEmail(), password);
    getClient(tokenAdmin).perform(get("/api/config/submissiondefinitions").param("size", "1").param("page", "0")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissiondefinitions[0].id", is("traditional"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=1"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    "page=4"
=======
    "page=5"
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ), Matchers.containsString("size=1")))).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.number", is(0)));
    getClient(tokenAdmin).perform(get("/api/config/submissiondefinitions").param("size", "1").param("page", "1")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissiondefinitions[0].id", is("accessConditionNotDiscoverable"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=2"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=1"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    "page=4"
=======
    "page="
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ), Matchers.containsString("size=1")))).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.number", is(1)));
    getClient(tokenAdmin).perform(get("/api/config/submissiondefinitions").param("size", "1").param("page", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissiondefinitions[0].id", is("languagetestprocess"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=1"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=3"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=2"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    "page=4"
=======
    "page=5"
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ), Matchers.containsString("size=1")))).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.number", is(2)));
    getClient(tokenAdmin).perform(get("/api/config/submissiondefinitions").param("size", "1").param("page", "3")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissiondefinitions[0].id", is("qualdroptest"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=2"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=4"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=3"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    "page=4"
=======
    "page=5"
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ), Matchers.containsString("size=1")))).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.number", is(3)));
    getClient(tokenAdmin).perform(get("/api/config/submissiondefinitions").param("size", "1").param("page", "4")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissiondefinitions[0].id", is("extractiontestprocess"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=0"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=3"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=5"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString("page=4"), Matchers.containsString("size=1")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissiondefinitions?"), Matchers.containsString(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    "page=4"
=======
    "page=5"
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ), Matchers.containsString("size=1")))).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$.page.totalElements", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", is(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/left.java
    5
=======
    6
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionDefinitionsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.number", is(4)));
  }
}