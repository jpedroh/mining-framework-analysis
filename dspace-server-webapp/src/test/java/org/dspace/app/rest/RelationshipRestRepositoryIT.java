package org.dspace.app.rest;
import static com.jayway.jsonpath.JsonPath.read;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.dspace.app.rest.matcher.PageMatcher;
import org.dspace.app.rest.matcher.RelationshipMatcher;
import org.dspace.app.rest.model.RelationshipRest;
import org.dspace.app.rest.test.AbstractEntityIntegrationTest;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.EPersonBuilder;
import org.dspace.builder.EntityTypeBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.builder.MetadataFieldBuilder;
import org.dspace.builder.RelationshipBuilder;
import org.dspace.builder.RelationshipTypeBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.EntityType;
import org.dspace.content.Item;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.content.MetadataSchemaEnum;
import org.dspace.content.MetadataValue;
import org.dspace.content.Relationship;
import org.dspace.content.RelationshipType;
import org.dspace.content.service.EntityTypeService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.MetadataFieldService;
import org.dspace.content.service.MetadataSchemaService;
import org.dspace.content.service.RelationshipTypeService;
import org.dspace.core.Constants;
import org.dspace.core.I18nUtil;
import org.dspace.discovery.MockSolrSearchCore;
import org.dspace.eperson.EPerson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class RelationshipRestRepositoryIT extends AbstractEntityIntegrationTest {
  @Autowired protected RelationshipTypeService relationshipTypeService;

  @Autowired protected EntityTypeService entityTypeService;

  @Autowired protected AuthorizeService authorizeService;

  @Autowired protected ItemService itemService;

  @Autowired protected MetadataFieldService metadataFieldService;

  @Autowired protected MetadataSchemaService metadataSchemaService;

  @Autowired MockSolrSearchCore mockSolrSearchCore;

  protected Community parentCommunity;

  protected Community child1;

  protected Collection col1;

  protected Collection col2;

  protected Collection col3;

  protected Collection col4;

  protected Collection col5;

  protected Collection col6;

  protected Collection col7;

  protected Item author1;

  protected Item author2;

  protected Item author3;

  protected Item orgUnit1;

  protected Item orgUnit2;

  protected Item orgUnit3;

  protected Item project1;

  protected Item publication1;

  protected Item publication2;

  protected RelationshipType isAuthorOfPublicationRelationshipType;

  protected RelationshipType isOrgUnitOfPersonRelationshipType;

  protected EPerson user1;

  @Before public void setUp() throws Exception {
    super.setUp();
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").withEntityType("Person").build();
    col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").withEntityType("Publication").build();
    col3 = CollectionBuilder.createCollection(context, child1).withName("OrgUnits").withEntityType("OrgUnit").build();
    col4 = CollectionBuilder.createCollection(context, child1).withName("Projects").withEntityType("Project").build();
    col5 = CollectionBuilder.createCollection(context, child1).withName("Projects").withEntityType("Journal").build();
    col6 = CollectionBuilder.createCollection(context, child1).withName("Projects").withEntityType("JournalVolume").build();
    col7 = CollectionBuilder.createCollection(context, child1).withName("Projects").withEntityType("JournalIssue").build();
    author1 = ItemBuilder.createItem(context, col1).withTitle("Author1").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withPersonIdentifierLastName("Smith").withPersonIdentifierFirstName("Donald").build();
    author2 = ItemBuilder.createItem(context, col1).withTitle("Author2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").build();
    author3 = ItemBuilder.createItem(context, col1).withTitle("Author3").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maybe").withPersonIdentifierLastName("Maybe").build();
    publication1 = ItemBuilder.createItem(context, col2).withTitle("Publication1").withAuthor("Testy, TEst").withIssueDate("2015-01-01").build();
    publication2 = ItemBuilder.createItem(context, col2).withTitle("Publication2").withAuthor("Testy, TEst").withIssueDate("2015-01-01").build();
    orgUnit1 = ItemBuilder.createItem(context, col3).withTitle("OrgUnit1").withAuthor("Testy, TEst").withIssueDate("2015-01-01").build();
    orgUnit2 = ItemBuilder.createItem(context, col3).withTitle("OrgUnit2").withAuthor("Testy, TEst").withIssueDate("2015-01-01").build();
    orgUnit3 = ItemBuilder.createItem(context, col3).withTitle("OrgUnit3").withAuthor("Test, Testy").withIssueDate("2015-02-01").build();
    project1 = ItemBuilder.createItem(context, col4).withTitle("Project1").withAuthor("Testy, TEst").withIssueDate("2015-01-01").build();
    isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    isOrgUnitOfPersonRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Person"), entityTypeService.findByEntityType(context, "OrgUnit"), "isOrgUnitOfPerson", "isPersonOfOrgUnit");
    user1 = EPersonBuilder.createEPerson(context).withNameInMetadata("first", "last").withEmail("testaze@gmail.com").withPassword(password).withLanguage(I18nUtil.getDefaultLocale().getLanguage()).build();
    context.restoreAuthSystemState();
  }

  @Test public void findAllRelationshipTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isOrgUnitOfPersonRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Person"), entityTypeService.findByEntityType(context, "OrgUnit"), "isOrgUnitOfPerson", "isPersonOfOrgUnit");
    RelationshipType isOrgUnitOfProjectRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Project"), entityTypeService.findByEntityType(context, "OrgUnit"), "isOrgUnitOfProject", "isProjectOfOrgUnit");
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship1 = RelationshipBuilder.createRelationshipBuilder(context, author1, orgUnit1, isOrgUnitOfPersonRelationshipType).build();
    Relationship relationship2 = RelationshipBuilder.createRelationshipBuilder(context, project1, orgUnit1, isOrgUnitOfProjectRelationshipType).build();
    Relationship relationship3 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 3)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship1), RelationshipMatcher.matchRelationship(relationship2), RelationshipMatcher.matchRelationship(relationship3))));
    getClient().perform(get("/api/core/relationships").param("size", "2").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 2, 2, 3)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship1), RelationshipMatcher.matchRelationship(relationship2))));
    getClient().perform(get("/api/core/relationships").param("size", "2").param("page", "1").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(1, 2, 2, 3)))).andExpect(jsonPath("$._embedded.relationships", contains(RelationshipMatcher.matchRelationship(relationship3))));
  }

  @Test public void createRelationshipWriteAccessLeftItem() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void createRelationshipWriteAccessRightItem() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void createRelationshipNoWriteAccess() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    String token = getAuthToken(user1.getEmail(), password);
    getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isForbidden());
  }

  @Test public void createRelationshipWithLeftWardValue() throws Exception {
    context.turnOffAuthorisationSystem();
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      String leftwardValue = "Name variant test left";
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).param("leftwardValue", leftwardValue).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", containsString(leftwardValue))).andExpect(jsonPath("$.rightwardValue", is(nullValue())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void createRelationshipWithRightwardValue() throws Exception {
    context.turnOffAuthorisationSystem();
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      String rightwardValue = "Name variant test right";
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).param("rightwardValue", rightwardValue).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", is(nullValue()))).andExpect(jsonPath("$.rightwardValue", containsString(rightwardValue)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void createRelationshipWithRightwardValueAndLeftWardValue() throws Exception {
    context.turnOffAuthorisationSystem();
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      String leftwardValue = "Name variant test left";
      String rightwardValue = "Name variant test right";
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).param("leftwardValue", leftwardValue).param("rightwardValue", rightwardValue).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", containsString(leftwardValue))).andExpect(jsonPath("$.rightwardValue", containsString(rightwardValue)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void createRelationshipAndAddLeftWardValueAfterwards() throws Exception {
    context.turnOffAuthorisationSystem();
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      String leftwardValue = "Name variant test label";
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).param("projection", "full").contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andExpect(jsonPath("$", RelationshipMatcher.matchFullEmbeds())).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", is(nullValue()))).andExpect(jsonPath("$.rightwardValue", is(nullValue())));
      JsonObject contentObj = new JsonObject();
      contentObj.addProperty("leftwardValue", leftwardValue);
      getClient(token).perform(put("/api/core/relationships/" + idRef).contentType("application/json").content(contentObj.toString())).andExpect(status().isOk());
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", containsString(leftwardValue))).andExpect(jsonPath("$.rightwardValue", is(nullValue())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void createRelationshipThenAddLabelsAndRemoveThem() throws Exception {
    context.turnOffAuthorisationSystem();
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      String leftwardValue = "Name variant test left";
      String rightwardValue = "Name variant test right";
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", is(nullValue()))).andExpect(jsonPath("$.rightwardValue", is(nullValue())));
      JsonObject contentObj = new JsonObject();
      contentObj.addProperty("leftwardValue", leftwardValue);
      contentObj.addProperty("rightwardValue", rightwardValue);
      getClient(token).perform(put("/api/core/relationships/" + idRef).contentType("application/json").content(contentObj.toString())).andExpect(status().isOk());
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", containsString(leftwardValue))).andExpect(jsonPath("$.rightwardValue", containsString(rightwardValue)));
      getClient(token).perform(put("/api/core/relationships/" + idRef).contentType("application/json").content("{}")).andExpect(status().isOk());
      getClient().perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(idRef.get()))).andExpect(jsonPath("$.leftwardValue", is(nullValue()))).andExpect(jsonPath("$.rightwardValue", is(nullValue())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * This method will test the addition of a mixture of plain-text metadatavalues and relationships to then
     * verify that the place property is still being handled correctly.
     * @throws Exception
     */
  @Test public void addRelationshipsAndMetadataToValidatePlaceTest() throws Exception {
    context.turnOffAuthorisationSystem();
    Item author1 = ItemBuilder.createItem(context, col1).withTitle("Author1").withIssueDate("2017-10-17").withPersonIdentifierFirstName("Donald").withPersonIdentifierLastName("Smith").build();
    Item author2 = ItemBuilder.createItem(context, col1).withTitle("Author2").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maria").withPersonIdentifierLastName("Smith").build();
    Item author3 = ItemBuilder.createItem(context, col1).withTitle("Author3").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maybe").withPersonIdentifierLastName("Maybe").build();
    Item publication1 = ItemBuilder.createItem(context, col2).withTitle("Publication1").withIssueDate("2015-01-01").build();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    AtomicReference<Integer> idRef3 = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text");
      itemService.update(context, publication1);
      List<MetadataValue> list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      assertEquals(2, list.size());
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text")) {
          assertEquals(1, mdv.getPlace());
        }
      }
      MetadataValue author0MD = list.get(0);
      assertEquals("Smith, Donald", author0MD.getValue());
      MetadataValue author1MD = list.get(1);
      assertEquals("plain text", author1MD.getValue());
      context.restoreAuthSystemState();
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      publication1 = itemService.find(context, publication1.getID());
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      assertEquals(3, list.size());
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef2)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(2)));
      author0MD = list.get(0);
      assertEquals("Smith, Donald", author0MD.getValue());
      author1MD = list.get(1);
      assertEquals("plain text", author1MD.getValue());
      MetadataValue author2MD = list.get(2);
      assertEquals("Smith, Maria", author2MD.getValue());
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text two");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      publication1 = itemService.find(context, publication1.getID());
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      assertEquals(4, list.size());
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text two")) {
          assertEquals(3, mdv.getPlace());
        }
      }
      author0MD = list.get(0);
      assertEquals("Smith, Donald", author0MD.getValue());
      author1MD = list.get(1);
      assertEquals("plain text", author1MD.getValue());
      author2MD = list.get(2);
      assertEquals("Smith, Maria", author2MD.getValue());
      MetadataValue author3MD = list.get(3);
      assertEquals("plain text two", author3MD.getValue());
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author3.getID())).andExpect(status().isCreated()).andDo((result) -> idRef3.set(read(result.getResponse().getContentAsString(), "$.id")));
      publication1 = itemService.find(context, publication1.getID());
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      assertEquals(5, list.size());
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(4)));
      author0MD = list.get(0);
      assertEquals("Smith, Donald", author0MD.getValue());
      author1MD = list.get(1);
      assertEquals("plain text", author1MD.getValue());
      author2MD = list.get(2);
      assertEquals("Smith, Maria", author2MD.getValue());
      author3MD = list.get(3);
      assertEquals("plain text two", author3MD.getValue());
      MetadataValue author4MD = list.get(4);
      assertEquals("Maybe, Maybe", author4MD.getValue());
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text three");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      assertEquals(6, list.size());
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text three")) {
          assertEquals(5, mdv.getPlace());
        }
      }
      author0MD = list.get(0);
      assertEquals("Smith, Donald", author0MD.getValue());
      author1MD = list.get(1);
      assertEquals("plain text", author1MD.getValue());
      author2MD = list.get(2);
      assertEquals("Smith, Maria", author2MD.getValue());
      author3MD = list.get(3);
      assertEquals("plain text two", author3MD.getValue());
      author4MD = list.get(4);
      assertEquals("Maybe, Maybe", author4MD.getValue());
      MetadataValue author5MD = list.get(5);
      assertEquals("plain text three", author5MD.getValue());
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text four");
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text five");
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text six");
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text seven");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      publication1 = itemService.find(context, publication1.getID());
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      assertEquals(10, list.size());
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text four")) {
          assertEquals(6, mdv.getPlace());
        }
        if (StringUtils.equals(mdv.getValue(), "plain text five")) {
          assertEquals(7, mdv.getPlace());
        }
        if (StringUtils.equals(mdv.getValue(), "plain text six")) {
          assertEquals(8, mdv.getPlace());
        }
        if (StringUtils.equals(mdv.getValue(), "plain text seven")) {
          assertEquals(9, mdv.getPlace());
        }
      }
      author0MD = list.get(0);
      assertEquals("Smith, Donald", author0MD.getValue());
      author1MD = list.get(1);
      assertEquals("plain text", author1MD.getValue());
      author2MD = list.get(2);
      assertEquals("Smith, Maria", author2MD.getValue());
      author3MD = list.get(3);
      assertEquals("plain text two", author3MD.getValue());
      author4MD = list.get(4);
      assertEquals("Maybe, Maybe", author4MD.getValue());
      author5MD = list.get(5);
      assertEquals("plain text three", author5MD.getValue());
      MetadataValue author6MD = list.get(6);
      assertEquals("plain text four", author6MD.getValue());
      MetadataValue author7MD = list.get(7);
      assertEquals("plain text five", author7MD.getValue());
      MetadataValue author8MD = list.get(8);
      assertEquals("plain text six", author8MD.getValue());
      MetadataValue author9MD = list.get(9);
      assertEquals("plain text seven", author9MD.getValue());
      list = itemService.getMetadata(publication1, "dc", "contributor", Item.ANY, Item.ANY);
      assertEquals(10, list.size());
      list = itemService.getMetadata(publication1, "dc", Item.ANY, Item.ANY, Item.ANY);
      assertEquals(16, list.size());
      list = itemService.getMetadata(publication1, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
      assertEquals(20, list.size());
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
      RelationshipBuilder.deleteRelationship(idRef3.get());
    }
  }

  /**
     * This method will test the deletion of a plain-text metadatavalue to then
     * verify that the place property is still being handled correctly.
     * @throws Exception
     */
  @Test public void deleteMetadataValueAndValidatePlace() throws Exception {
    context.turnOffAuthorisationSystem();
    Item publication1 = ItemBuilder.createItem(context, col2).withTitle("Publication1").withIssueDate("2015-01-01").build();
    Item author2 = ItemBuilder.createItem(context, col1).withTitle("Author2").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maria").withPersonIdentifierLastName("Smith").build();
    Item author3 = ItemBuilder.createItem(context, col1).withTitle("Author3").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maybe").withPersonIdentifierLastName("Maybe").build();
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    AtomicReference<Integer> idRef3 = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      List<MetadataValue> list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text")) {
          assertEquals(1, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef2)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(2)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text two");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text two")) {
          assertEquals(3, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author3.getID())).andExpect(status().isCreated()).andDo((result) -> idRef3.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(4)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text three");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text three")) {
          assertEquals(5, mdv.getPlace());
        }
      }
      List<MetadataValue> authors = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      List<MetadataValue> listToRemove = new LinkedList<>();
      for (MetadataValue metadataValue : authors) {
        if (StringUtils.equals(metadataValue.getValue(), "plain text two")) {
          listToRemove.add(metadataValue);
        }
      }
      context.turnOffAuthorisationSystem();
      itemService.removeMetadataValues(context, publication1, listToRemove);
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text")) {
          assertEquals(1, mdv.getPlace());
        }
      }
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text three")) {
          assertEquals(4, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef2)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(2)));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(3)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
      RelationshipBuilder.deleteRelationship(idRef3.get());
    }
  }

  /**
     * This method will test the deletion of a Relationship to then
     * verify that the place property is still being handled correctly.
     * @throws Exception
     */
  @Test public void deleteRelationshipsAndValidatePlace() throws Exception {
    context.turnOffAuthorisationSystem();
    Item publication1 = ItemBuilder.createItem(context, col2).withTitle("Publication1").withIssueDate("2015-01-01").build();
    Item author2 = ItemBuilder.createItem(context, col1).withTitle("Author2").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maria").withPersonIdentifierLastName("Smith").build();
    Item author3 = ItemBuilder.createItem(context, col1).withTitle("Author3").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maybe").withPersonIdentifierLastName("Maybe").build();
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    AtomicReference<Integer> idRef3 = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      List<MetadataValue> list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text")) {
          assertEquals(1, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef2)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(2)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text two");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text two")) {
          assertEquals(3, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author3.getID())).andExpect(status().isCreated()).andDo((result) -> idRef3.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(4)));
      context.turnOffAuthorisationSystem();
      publication1 = itemService.find(context, publication1.getID());
      itemService.addMetadata(context, publication1, "dc", "contributor", "author", Item.ANY, "plain text three");
      itemService.update(context, publication1);
      context.restoreAuthSystemState();
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text three")) {
          assertEquals(5, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(delete("/api/core/relationships/" + idRef2));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(0)));
      publication1 = itemService.find(context, publication1.getID());
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text")) {
          assertEquals(1, mdv.getPlace());
        }
      }
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text two")) {
          assertEquals(2, mdv.getPlace());
        }
      }
      list = itemService.getMetadata(publication1, "dc", "contributor", "author", Item.ANY);
      for (MetadataValue mdv : list) {
        if (StringUtils.equals(mdv.getValue(), "plain text three")) {
          assertEquals(4, mdv.getPlace());
        }
      }
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("leftPlace", is(3)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
      RelationshipBuilder.deleteRelationship(idRef3.get());
    }
  }

  /**
     * This method will test the deletion of a Relationship and will then
     * verify that the relation is removed
     * @throws Exception
     */
  @Test public void deleteRelationship() throws Exception {
    context.turnOffAuthorisationSystem();
    Item author2 = ItemBuilder.createItem(context, col1).withTitle("Author2").withIssueDate("2016-02-13").withPersonIdentifierFirstName("Maria").withPersonIdentifierLastName("Smith").build();
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/spring-rest/api/core" + "/items/" + publication1.getID() + "\n" + "https://localhost:8080/spring-rest/api/core" + "/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/items/" + publication1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(1)));
      getClient(adminToken).perform(get("/api/core/items/" + author1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(1)));
      getClient(adminToken).perform(get("/api/core/items/" + author2.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(0)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/spring-rest/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/spring-rest/api/core/items/" + author2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/items/" + publication1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(2)));
      getClient(adminToken).perform(get("/api/core/items/" + author1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(1)));
      getClient(adminToken).perform(get("/api/core/items/" + author2.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(1)));
      getClient(adminToken).perform(delete("/api/core/relationships/" + idRef1));
      getClient(adminToken).perform(get("/api/core/items/" + publication1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(1)));
      getClient(adminToken).perform(get("/api/core/items/" + author1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(0)));
      getClient(adminToken).perform(get("/api/core/items/" + author2.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(1)));
      getClient(adminToken).perform(delete("/api/core/relationships/" + idRef2));
      getClient(adminToken).perform(get("/api/core/items/" + publication1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(0)));
      getClient(adminToken).perform(get("/api/core/items/" + author1.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(0)));
      getClient(adminToken).perform(get("/api/core/items/" + author2.getID() + "/relationships")).andExpect(status().isOk()).andExpect(jsonPath("page.totalElements", is(0)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
    }
  }

  /**
     * This test will simply add Relationships between Items with a useForPlace attribute set to false for the
     * RelationshipType. We want to test that the Relationships that are created will still have their place
     * attributes handled in a correct way
     * @throws Exception
     */
  @Test public void addRelationshipsNotUseForPlace() throws Exception {
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    AtomicReference<Integer> idRef3 = new AtomicReference<>();
    try {
      String adminToken = getAuthToken(admin.getEmail(), password);
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isOrgUnitOfPersonRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(0)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isOrgUnitOfPersonRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author2.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + orgUnit1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef2)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(1)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isOrgUnitOfPersonRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author3.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + orgUnit1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef3.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(2)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
      RelationshipBuilder.deleteRelationship(idRef3.get());
    }
  }

  /**
     * This test will simply add Relationships between Items with a useForPlace attribute set to false for the
     * RelationshipType. We want to test that the Relationships that are created will still have their place
     * attributes handled in a correct way. It will then delete a Relationship and once again ensure that the place
     * attributes are being handled correctly.
     * @throws Exception
     */
  @Test public void addAndDeleteRelationshipsNotUseForPlace() throws Exception {
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    AtomicReference<Integer> idRef3 = new AtomicReference<>();
    try {
      String adminToken = getAuthToken(admin.getEmail(), password);
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isOrgUnitOfPersonRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(0)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isOrgUnitOfPersonRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author2.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + orgUnit1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef2)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(1)));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isOrgUnitOfPersonRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author3.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + orgUnit1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef3.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(2)));
      getClient(adminToken).perform(delete("/api/core/relationships/" + idRef2)).andExpect(status().isNoContent());
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(0)));
      getClient(adminToken).perform(get("/api/core/relationships/" + idRef3)).andExpect(status().isOk()).andExpect(jsonPath("rightPlace", is(1)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
      RelationshipBuilder.deleteRelationship(idRef3.get());
    }
  }

  /**
     * This test will create a relationship with author 1 - publication 1
     * Then modify this relationship by changing the left item to author 2 via PUT > Verify
     * Then modify this relationship by changing the right item to publication 2 via PUT > Verify
     *
     * @throws Exception
     */
  @Test public void putRelationshipAdminAccess() throws Exception {
    AtomicReference<Integer> idRef1 = new AtomicReference<>();
    try {
      String token = getAuthToken(admin.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef1.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(token).perform(put("/api/core/relationships/" + idRef1 + "/leftItem").contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication2.getID())).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication2.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
      getClient(token).perform(put("/api/core/relationships/" + idRef1 + "/rightItem").contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef1)).andExpect(status().isOk()).andExpect(jsonPath("$._links.rightItem.href", containsString(author2.getID().toString()))).andExpect(jsonPath("$._links.leftItem.href", containsString(publication2.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef1.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 1 and author 2
     * Verify this is possible for a user with WRITE permissions on author 1 and author 2
     */
  @Test public void putRelationshipWriteAccessOnAuthors() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, author2, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/rightItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.rightItem.href", containsString(author2.getID().toString()))).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 1 and author 2
     * Verify this is possible for a user with WRITE permissions on publication 1
     */
  @Test public void putRelationshipWriteAccessOnPublication() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/rightItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.rightItem.href", containsString(author2.getID().toString()))).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 2 and author 1
     * Verify this is possible for a user with WRITE permissions on publication 1 and publication 2
     */
  @Test public void putRelationshipWriteAccessOnPublications() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    authorizeService.addPolicy(context, publication2, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/leftItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication2.getID())).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication2.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 2 and author 1
     * Verify this is possible for a user with WRITE permissions on author 1
     */
  @Test public void putRelationshipWriteAccessOnAuthor() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(user1.getEmail(), password);
      getClient(getAuthToken(admin.getEmail(), password)).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/leftItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication2.getID())).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication2.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 1 and author 2
     * Verify this is NOT possible for a user without WRITE permissions
     */
  @Test public void putRelationshipNoAccess() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(admin.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/rightItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isForbidden());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 1 and author 2
     * Verify this is NOT possible for a user with WRITE permissions on author 1
     */
  @Test public void putRelationshipOnlyAccessOnOneAuthor() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, author1, Constants.WRITE, user1);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      String token = getAuthToken(admin.getEmail(), password);
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      token = getAuthToken(user1.getEmail(), password);
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/rightItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + author2.getID())).andExpect(status().isForbidden());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Create a relationship between publication 1 and author 1
     * Change it to a relationship between publication 2 and author 1
     * Verify this is NOT possible for a user with WRITE permissions on publication 1
     */
  @Test public void putRelationshipOnlyAccessOnOnePublication() throws Exception {
    context.turnOffAuthorisationSystem();
    context.setCurrentUser(user1);
    authorizeService.addPolicy(context, publication1, Constants.WRITE, user1);
    String token = getAuthToken(user1.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/leftItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication2.getID())).andExpect(status().isForbidden());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$._links.leftItem.href", containsString(publication1.getID().toString()))).andExpect(jsonPath("$._links.rightItem.href", containsString(author1.getID().toString())));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void findRelationshipByLabelTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isOrgUnitOfPersonRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Person"), entityTypeService.findByEntityType(context, "OrgUnit"), "isOrgUnitOfPerson", "isPersonOfOrgUnit");
    RelationshipType isOrgUnitOfProjectRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Project"), entityTypeService.findByEntityType(context, "OrgUnit"), "isOrgUnitOfProject", "isProjectOfOrgUnit");
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship1 = RelationshipBuilder.createRelationshipBuilder(context, author1, orgUnit1, isOrgUnitOfPersonRelationshipType).build();
    Relationship relationshipOrgunitExtra = RelationshipBuilder.createRelationshipBuilder(context, author1, orgUnit2, isOrgUnitOfPersonRelationshipType).build();
    Relationship relationshipAuthorExtra = RelationshipBuilder.createRelationshipBuilder(context, author2, orgUnit1, isOrgUnitOfPersonRelationshipType).build();
    Relationship relationship2 = RelationshipBuilder.createRelationshipBuilder(context, project1, orgUnit1, isOrgUnitOfProjectRelationshipType).build();
    Relationship relationship3 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships/search/byLabel").param("label", "isOrgUnitOfPerson").param("dso", author1.getID().toString()).param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 2)))).andExpect(jsonPath("$._embedded.relationships", hasItem(RelationshipMatcher.matchRelationship(relationship1)))).andExpect(jsonPath("$._embedded.relationships[0]._links.rightItem.href", containsString(orgUnit1.getID().toString()))).andExpect(jsonPath("$._embedded.relationships[1]._links.rightItem.href", containsString(orgUnit2.getID().toString())));
    getClient().perform(get("/api/core/relationships/search/byLabel").param("label", "isOrgUnitOfPerson").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 3)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship1), RelationshipMatcher.matchRelationship(relationshipAuthorExtra), RelationshipMatcher.matchRelationship(relationshipOrgunitExtra))));
  }

  @Test public void putRelationshipWithNonexistentID() throws Exception {
    context.turnOffAuthorisationSystem();
    String token = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    int nonexistentRelationshipID = 404404404;
    getClient(token).perform(put("/api/core/relationships/" + nonexistentRelationshipID + "/leftItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID())).andExpect(status().isNotFound());
  }

  @Test public void putRelationshipWithInvalidItemIDInBody() throws Exception {
    context.turnOffAuthorisationSystem();
    String token = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      int nonexistentItemID = 404404404;
      getClient(token).perform(put("/api/core/relationships/" + idRef + "/leftItem").contentType(MediaType.parseMediaType("text/uri-list")).content("https://localhost:8080/server/api/core/items/" + nonexistentItemID)).andExpect(status().isUnprocessableEntity());
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  /**
     * Verify when a rightward value is present which has been configured to
     * be used for virtual metadata, that the virtual metadata is populated
     * with the custom value
     */
  @Test public void rightwardValueRelationshipTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship3 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withRightwardValue("RightwardValueTest").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 1)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship3))));
    getClient().perform(get("/api/core/items/" + publication1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata("dc.contributor.author", "RightwardValueTest"), matchMetadata("dc.title", "Publication1"))));
  }

  /**
     * Verify when no rightward value is present, that the virtual metadata is populated
     * with the metadata from the related item
     */
  @Test public void nonRightwardValueRelationshipTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship3 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 1)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship3))));
    getClient().perform(get("/api/core/items/" + publication1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata("dc.contributor.author", "Maybe, Maybe"), matchMetadata("dc.contributor.author", "Testy, TEst"), matchMetadata("dc.title", "Publication1"))));
  }

  /**
     * Verify when a rightward value is present which has been configured to
     * be used for virtual metadata, that the virtual metadata is populated
     * with the custom value
     * Verify that only the relationship containing the rightward value will be updated
     */
  @Test public void mixedRightwardValueAndRegularRelationshipTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Item author1 = ItemBuilder.createItem(context, col1).withTitle("Author1").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withPersonIdentifierFirstName("testingFirstName").withPersonIdentifierLastName("testingLastName").build();
    Relationship relationship3 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    Relationship relationship2 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withRightwardValue("TestingRightwardValue").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 2)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship3), RelationshipMatcher.matchRelationship(relationship2))));
    getClient().perform(get("/api/core/items/" + publication1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata("dc.contributor.author", "Maybe, Maybe"), matchMetadata("dc.contributor.author", "Testy, TEst"), matchMetadata("dc.contributor.author", "TestingRightwardValue"), not(matchMetadata("dc.contributor.author", "testingLastName, testingFirstName")), matchMetadata("dc.title", "Publication1"))));
  }

  /**
     * Verify when a leftward value is present which has NOT been configured to
     * be used for virtual metadata, that the virtual metadata is NOT populated
     * with the custom value
     */
  @Test public void leftwardValueRelationshipTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship3 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftwardValue("leftwardValue").withLeftPlace(1).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$.page", is(PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 1)))).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationship(relationship3))));
    getClient().perform(get("/api/core/items/" + publication1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata("dc.contributor.author", "Maybe, Maybe"), matchMetadata("dc.contributor.author", "Testy, TEst"), matchMetadata("dc.title", "Publication1"))));
  }

  @Test public void putRelationshipWithJson() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    Integer idRef = null;
    try {
      MvcResult mvcResult = getClient(token).perform(post("/api/core/relationships").param("relationshipType", isAuthorOfPublicationRelationshipType.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + publication1.getID() + "\n" + "https://localhost:8080/server/api/core/items/" + author1.getID())).andExpect(status().isCreated()).andReturn();
      ObjectMapper mapper = new ObjectMapper();
      String content = mvcResult.getResponse().getContentAsString();
      Map<String, Object> map = mapper.readValue(content, Map.class);
      String id = String.valueOf(map.get("id"));
      idRef = Integer.parseInt(id);
      RelationshipRest relationshipRest = new RelationshipRest();
      relationshipRest.setLeftPlace(0);
      relationshipRest.setRightPlace(1);
      relationshipRest.setLeftwardValue(null);
      relationshipRest.setRightwardValue(null);
      getClient(token).perform(put("/api/core/relationships/" + idRef).contentType(contentType).content(mapper.writeValueAsBytes(relationshipRest))).andExpect(status().isOk());
      getClient(token).perform(get("/api/core/relationships/" + idRef)).andExpect(status().isOk()).andExpect(jsonPath("$.leftPlace", is(0))).andExpect(jsonPath("$.rightPlace", is(1)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef);
    }
  }

  @Test public void orgUnitAndOrgUnitRelationshipVirtualMetadataTest() throws Exception {
    context.turnOffAuthorisationSystem();
    EntityType orgUnit = entityTypeService.findByEntityType(context, "OrgUnit");
    RelationshipType isParentOrgUnitOf = relationshipTypeService.findbyTypesAndTypeName(context, orgUnit, orgUnit, "isParentOrgUnitOf", "isChildOrgUnitOf");
    MetadataSchema metadataSchema = metadataSchemaService.find(context, "relation");
    MetadataFieldBuilder.createMetadataField(context, metadataSchema, "isParentOrgUnitOf", null, null).build();
    MetadataFieldBuilder.createMetadataField(context, metadataSchema, "isChildOrgUnitOf", null, null).build();
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isParentOrgUnitOf.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + orgUnit1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      itemService.getMetadata(orgUnit1, "*", "*", "*", "*", true);
      getClient(adminToken).perform(get("/api/core/items/" + orgUnit1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata[\'relation.isParentOrgUnitOf\'][0].value", is(String.valueOf(orgUnit2.getID()))));
      getClient(adminToken).perform(get("/api/core/items/" + orgUnit2.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata[\'relation.isChildOrgUnitOf\'][0].value", is(String.valueOf(orgUnit1.getID()))));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void orgUnitFindByLabelParentChildOfCountTest() throws Exception {
    context.turnOffAuthorisationSystem();
    EntityType orgUnit = entityTypeService.findByEntityType(context, "OrgUnit");
    RelationshipType isParentOrgUnitOf = relationshipTypeService.findbyTypesAndTypeName(context, orgUnit, orgUnit, "isParentOrgUnitOf", "isChildOrgUnitOf");
    MetadataSchema metadataSchema = metadataSchemaService.find(context, "relation");
    MetadataFieldBuilder.createMetadataField(context, metadataSchema, "isParentOrgUnitOf", null, null).build();
    MetadataFieldBuilder.createMetadataField(context, metadataSchema, "isChildOrgUnitOf", null, null).build();
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    AtomicReference<Integer> idRef2 = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isParentOrgUnitOf.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + orgUnit1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isParentOrgUnitOf.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + orgUnit2.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit3.getID())).andExpect(status().isCreated()).andDo((result) -> idRef2.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient().perform(get("/api/core/relationships/search/byLabel").param("label", "isChildOrgUnitOf").param("dso", String.valueOf(orgUnit2.getID())).param("page", "0").param("size", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.page", PageMatcher.pageEntryWithTotalPagesAndElements(0, 1, 1, 1)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
      RelationshipBuilder.deleteRelationship(idRef2.get());
    }
  }

  @Test public void orgUnitLeftMaxCardinalityTest() throws Exception {
    context.turnOffAuthorisationSystem();
    EntityType orgUnit = entityTypeService.findByEntityType(context, "OrgUnit");
    RelationshipType isParentOrgUnitOf = relationshipTypeService.findbyTypesAndTypeName(context, orgUnit, orgUnit, "isParentOrgUnitOf", "isChildOrgUnitOf");
    MetadataSchema metadataSchema = metadataSchemaService.find(context, "relation");
    MetadataFieldBuilder.createMetadataField(context, metadataSchema, "isParentOrgUnitOf", null, null).build();
    MetadataFieldBuilder.createMetadataField(context, metadataSchema, "isChildOrgUnitOf", null, null).build();
    String adminToken = getAuthToken(admin.getEmail(), password);
    context.restoreAuthSystemState();
    AtomicReference<Integer> idRef = new AtomicReference<>();
    try {
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isParentOrgUnitOf.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + orgUnit1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit2.getID())).andExpect(status().isCreated()).andDo((result) -> idRef.set(read(result.getResponse().getContentAsString(), "$.id")));
      getClient(adminToken).perform(post("/api/core/relationships").param("relationshipType", isParentOrgUnitOf.getID().toString()).contentType(MediaType.parseMediaType(org.springframework.data.rest.webmvc.RestMediaTypes.TEXT_URI_LIST_VALUE)).content("https://localhost:8080/server/api/core/items/" + orgUnit1.getID() + "\n" + "https://localhost:8080/server/api/core/items" + "/" + orgUnit3.getID())).andExpect(status().isBadRequest());
      getClient().perform(get("/api/core/relationships/search/byLabel").param("label", "isParentOrgUnitOf").param("dso", String.valueOf(orgUnit1.getID())).param("page", "0")).andExpect(status().isOk()).andExpect(jsonPath("$.page", PageMatcher.pageEntryWithTotalPagesAndElements(0, 20, 1, 1)));
    }  finally {
      RelationshipBuilder.deleteRelationship(idRef.get());
    }
  }

  @Test public void testVirtualMdInRESTAndSolrDoc() throws Exception {
    context.turnOffAuthorisationSystem();
    EntityType journalEntityType = entityTypeService.findByEntityType(context, "Journal");
    if (journalEntityType == null) {
      journalEntityType = EntityTypeBuilder.createEntityTypeBuilder(context, "Journal").build();
    }
    EntityType journalVolumeEntityType = entityTypeService.findByEntityType(context, "JournalVolume");
    if (journalVolumeEntityType == null) {
      journalVolumeEntityType = EntityTypeBuilder.createEntityTypeBuilder(context, "JournalVolume").build();
    }
    EntityType journalIssueEntityType = entityTypeService.findByEntityType(context, "JournalIssue");
    if (journalIssueEntityType == null) {
      journalIssueEntityType = EntityTypeBuilder.createEntityTypeBuilder(context, "JournalIssue").build();
    }
    EntityType publicationEntityType = entityTypeService.findByEntityType(context, "Publication");
    if (publicationEntityType == null) {
      publicationEntityType = EntityTypeBuilder.createEntityTypeBuilder(context, "Publication").build();
    }
    RelationshipType isPublicationOfJournalIssue = relationshipTypeService.findbyTypesAndTypeName(context, journalIssueEntityType, publicationEntityType, "isPublicationOfJournalIssue", "isJournalIssueOfPublication");
    if (isPublicationOfJournalIssue == null) {
      isPublicationOfJournalIssue = RelationshipTypeBuilder.createRelationshipTypeBuilder(context, journalIssueEntityType, publicationEntityType, "isPublicationOfJournalIssue", "isJournalIssueOfPublication", null, null, null, null).build();
    }
    RelationshipType isIssueOfJournalVolume = relationshipTypeService.findbyTypesAndTypeName(context, journalVolumeEntityType, journalIssueEntityType, "isIssueOfJournalVolume", "isJournalVolumeOfIssue");
    if (isIssueOfJournalVolume == null) {
      isIssueOfJournalVolume = RelationshipTypeBuilder.createRelationshipTypeBuilder(context, journalVolumeEntityType, journalIssueEntityType, "isIssueOfJournalVolume", "isJournalVolumeOfIssue", null, null, null, null).build();
    } else {
      isIssueOfJournalVolume.setRightMinCardinality(0);
    }
    RelationshipType isVolumeOfJournal = relationshipTypeService.findbyTypesAndTypeName(context, journalEntityType, journalVolumeEntityType, "isVolumeOfJournal", "isJournalOfVolume");
    if (isVolumeOfJournal == null) {
      isVolumeOfJournal = RelationshipTypeBuilder.createRelationshipTypeBuilder(context, journalEntityType, journalVolumeEntityType, "isVolumeOfJournal", "isJournalOfVolume", null, null, null, null).build();
    } else {
      isVolumeOfJournal.setRightMinCardinality(0);
    }
    MetadataSchema journalSchema = metadataSchemaService.find(context, "journal");
    if (journalSchema == null) {
      journalSchema = metadataSchemaService.create(context, "journal", "journal");
    }
    String journalTitleVirtualMdField = "journal.title";
    MetadataField journalTitleField = metadataFieldService.findByString(context, journalTitleVirtualMdField, '.');
    if (journalTitleField == null) {
      metadataFieldService.create(context, journalSchema, "title", null, "Journal Title");
    }
    String journalTitle = "Journal Title Test";
    Item journal = ItemBuilder.createItem(context, col5).withTitle(journalTitle).build();
    Item journalVolume = ItemBuilder.createItem(context, col6).withTitle("JournalVolume").build();
    Item journalIssue = ItemBuilder.createItem(context, col7).withTitle("JournalIssue").build();
    Item publication = ItemBuilder.createItem(context, col2).withTitle("Publication").build();
    RelationshipBuilder.createRelationshipBuilder(context, journalIssue, publication, isPublicationOfJournalIssue).build();
    RelationshipBuilder.createRelationshipBuilder(context, journalVolume, journalIssue, isIssueOfJournalVolume).build();
    mockSolrSearchCore.getSolr().commit(false, false);
    getClient().perform(get("/api/core/items/" + publication.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata." + journalTitleVirtualMdField).doesNotExist());
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery("search.resourceid:" + publication.getID());
    QueryResponse queryResponse = mockSolrSearchCore.getSolr().query(solrQuery);
    assertThat(queryResponse.getResults().size(), equalTo(1));
    assertNull(queryResponse.getResults().get(0).getFieldValues(journalTitleVirtualMdField));
    RelationshipBuilder.createRelationshipBuilder(context, journal, journalVolume, isVolumeOfJournal).build();
    mockSolrSearchCore.getSolr().commit(false, false);
    getClient().perform(get("/api/core/items/" + publication.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata(journalTitleVirtualMdField, journalTitle))));
    queryResponse = mockSolrSearchCore.getSolr().query(solrQuery);
    assertThat(queryResponse.getResults().size(), equalTo(1));
    assertEquals(journalTitle, ((List) queryResponse.getResults().get(0).getFieldValues(journalTitleVirtualMdField)).get(0));
    getClient().perform(get("/api/core/items/" + journalVolume.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata(journalTitleVirtualMdField, journalTitle))));
    solrQuery.setQuery("search.resourceid:" + journalVolume.getID());
    queryResponse = mockSolrSearchCore.getSolr().query(solrQuery);
    assertThat(queryResponse.getResults().size(), equalTo(1));
    assertEquals(journalTitle, ((List) queryResponse.getResults().get(0).getFieldValues(journalTitleVirtualMdField)).get(0));
    getClient().perform(get("/api/core/items/" + journalIssue.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", allOf(matchMetadata(journalTitleVirtualMdField, journalTitle))));
    solrQuery.setQuery("search.resourceid:" + journalIssue.getID());
    queryResponse = mockSolrSearchCore.getSolr().query(solrQuery);
    assertThat(queryResponse.getResults().size(), equalTo(1));
    assertEquals(journalTitle, ((List) queryResponse.getResults().get(0).getFieldValues(journalTitleVirtualMdField)).get(0));
    context.restoreAuthSystemState();
  }

  @Test public void findOneTestWrongUUID() throws Exception {
    getClient().perform(get("/api/core/relationships/" + 1000)).andExpect(status().isNotFound());
  }

  /**
     * Verify whether the relationship metadata appears correctly on both members.
     * {@link #isAuthorOfPublicationRelationshipType} is tested again in
     * {@link LeftTiltedRelationshipRestRepositoryIT} with tilted set to left.
     */
  @Test public void testIsAuthorOfPublicationRelationshipMetadataViaREST() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).build();
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/core/items/{uuid}", author1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", matchMetadata(String.format("%s.isPublicationOfAuthor", MetadataSchemaEnum.RELATION.getName()), publication1.getID().toString())));
    getClient(adminToken).perform(get("/api/core/items/{uuid}", publication1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", matchMetadata(String.format("%s.isAuthorOfPublication", MetadataSchemaEnum.RELATION.getName()), author1.getID().toString())));
  }

  /**
     * Verify whether the relationship metadata appears correctly on both members.
     * {@link #isOrgUnitOfPersonRelationshipType} is tested again in
     * {@link RightTiltedRelationshipRestRepositoryIT} with tilted set to right.
     */
  @Test public void testIsOrgUnitOfPersonRelationshipMetadataViaREST() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipBuilder.createRelationshipBuilder(context, author1, orgUnit1, isOrgUnitOfPersonRelationshipType).build();
    context.restoreAuthSystemState();
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/core/items/{uuid}", author1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", matchMetadata(String.format("%s.isOrgUnitOfPerson", MetadataSchemaEnum.RELATION.getName()), orgUnit1.getID().toString())));
    getClient(adminToken).perform(get("/api/core/items/{uuid}", orgUnit1.getID())).andExpect(status().isOk()).andExpect(jsonPath("$.metadata", matchMetadata(String.format("%s.isPersonOfOrgUnit", MetadataSchemaEnum.RELATION.getName()), author1.getID().toString())));
  }

  @Test public void findByItemsAndTypeTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship1 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    Relationship relationship2 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString()).param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships", containsInAnyOrder(RelationshipMatcher.matchRelationshipValues(relationship1), RelationshipMatcher.matchRelationshipValues(relationship2)))).andExpect(jsonPath("$.page.totalPages", is(1))).andExpect(jsonPath("$.page.totalElements", is(2)));
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "isPublicationOfAuthor").param("focusItem", author1.getID().toString()).param("relatedItem", publication1.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships", contains(RelationshipMatcher.matchRelationshipValues(relationship2)))).andExpect(jsonPath("$.page.totalPages", is(1))).andExpect(jsonPath("$.page.totalElements", is(1)));
  }

  @Test public void findByItemsAndTypeBadRequestTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", "1").param("focusItem", publication1.getID().toString()).param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString()).param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", "1").param("relationshipLabel", "isAuthorOfPublication").param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isBadRequest());
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", "1").param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString())).andExpect(status().isBadRequest());
  }

  @Test public void findByItemsAndTypeUnprocessableEntityTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "wrongLabel").param("focusItem", orgUnit1.getID().toString()).param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isUnprocessableEntity());
  }

  @Test public void findByItemsAndTypeEmptyResponceTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withLeftPlace(1).build();
    context.restoreAuthSystemState();
    Integer typeId = Integer.MAX_VALUE;
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", typeId.toString()).param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString()).param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships").doesNotExist());
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "isAuthorOfPublication").param("focusItem", UUID.randomUUID().toString()).param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships").doesNotExist());
  }

  @Test public void findByItemsAndTypePaginationTest() throws Exception {
    context.turnOffAuthorisationSystem();
    RelationshipType isAuthorOfPublicationRelationshipType = relationshipTypeService.findbyTypesAndTypeName(context, entityTypeService.findByEntityType(context, "Publication"), entityTypeService.findByEntityType(context, "Person"), "isAuthorOfPublication", "isPublicationOfAuthor");
    Relationship relationship1 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author3, isAuthorOfPublicationRelationshipType).withLeftPlace(2).build();
    Relationship relationship2 = RelationshipBuilder.createRelationshipBuilder(context, publication1, author1, isAuthorOfPublicationRelationshipType).withLeftPlace(2).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString()).param("size", "1").param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships", contains(RelationshipMatcher.matchRelationshipValues(relationship1)))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.totalElements", is(2)));
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString()).param("page", "1").param("size", "1").param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships", contains(RelationshipMatcher.matchRelationshipValues(relationship2)))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.totalElements", is(2)));
    getClient().perform(get("/api/core/relationships/search/byItemsAndType").param("typeId", isAuthorOfPublicationRelationshipType.getID().toString()).param("relationshipLabel", "isAuthorOfPublication").param("focusItem", publication1.getID().toString()).param("page", "5").param("relatedItem", author1.getID().toString(), author2.getID().toString(), author3.getID().toString())).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.relationships").doesNotExist()).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.number", is(5))).andExpect(jsonPath("$.page.totalPages", is(1))).andExpect(jsonPath("$.page.totalElements", is(2)));
  }
}