package org.dspace.app.rest;
import static org.dspace.app.rest.matcher.MetadataMatcher.matchMetadata;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.dspace.app.rest.matcher.BrowseEntryResourceMatcher;
import org.dspace.app.rest.matcher.BrowseIndexMatcher;
import org.dspace.app.rest.matcher.ItemMatcher;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.builder.CollectionBuilder;
import org.dspace.builder.CommunityBuilder;
import org.dspace.builder.GroupBuilder;
import org.dspace.builder.ItemBuilder;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.authority.service.MetadataAuthorityService;
import org.dspace.eperson.Group;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * Integration test to test the /api/discover/browses endpoint
 * (Class has to start or end with IT to be picked up by the failsafe plugin)
 *
 * @author Frederic Van Reet (frederic dot vanreet at atmire dot com)
 * @author Tom Desair (tom dot desair at atmire dot com)
 */
public class BrowsesResourceControllerIT extends AbstractControllerIntegrationTest {
  @Autowired ConfigurationService configurationService;

  @Autowired MetadataAuthorityService metadataAuthorityService;

  @Test public void findAll() throws Exception {
    getClient().perform(get("/api/discover/browses")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(4))).andExpect(jsonPath("$.page.totalPages", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.browses", hasSize(4))).andExpect(jsonPath("$._embedded.browses", containsInAnyOrder(BrowseIndexMatcher.dateIssuedBrowseIndex("asc"), BrowseIndexMatcher.contributorBrowseIndex("asc"), BrowseIndexMatcher.titleBrowseIndex("asc"), BrowseIndexMatcher.subjectBrowseIndex("asc"))));
  }

  @Test public void findBrowseByTitle() throws Exception {
    getClient().perform(get("/api/discover/browses/title")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", BrowseIndexMatcher.titleBrowseIndex("asc")));
  }

  @Test public void findBrowseByDateIssued() throws Exception {
    getClient().perform(get("/api/discover/browses/dateissued")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", BrowseIndexMatcher.dateIssuedBrowseIndex("asc")));
  }

  @Test public void findBrowseByContributor() throws Exception {
    getClient().perform(get("/api/discover/browses/author")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", BrowseIndexMatcher.contributorBrowseIndex("asc")));
  }

  @Test public void findBrowseBySubject() throws Exception {
    getClient().perform(get("/api/discover/browses/subject")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", BrowseIndexMatcher.subjectBrowseIndex("asc")));
  }

  @Test public void findBrowseBySubjectEntries() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item publicItem1 = ItemBuilder.createItem(context, col1).withTitle("Public item 1").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withAuthor("Doe, John").withSubject("ExtraEntry").build();
    Item publicItem2 = ItemBuilder.createItem(context, col2).withTitle("Public item 2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("TestingForMore").withSubject("ExtraEntry").build();
    Item publicItem3 = ItemBuilder.createItem(context, col2).withTitle("Public item 2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("AnotherTest").withSubject("TestingForMore").withSubject("ExtraEntry").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/subject/entries").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("AnotherTest", 1), BrowseEntryResourceMatcher.matchBrowseEntry("ExtraEntry", 3), BrowseEntryResourceMatcher.matchBrowseEntry("TestingForMore", 2))));
    getClient().perform(get("/api/discover/browses/subject/entries").param("sort", "value,desc")).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("TestingForMore", 2), BrowseEntryResourceMatcher.matchBrowseEntry("ExtraEntry", 3), BrowseEntryResourceMatcher.matchBrowseEntry("AnotherTest", 1))));
  }

  @Test public void findBrowseBySubjectEntriesWithAuthority() throws Exception {
    configurationService.setProperty("choices.plugin.dc.subject", "SolrSubjectAuthority");
    configurationService.setProperty("authority.controlled.dc.subject", "true");
    metadataAuthorityService.clearCache();
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item publicItem1 = ItemBuilder.createItem(context, col1).withTitle("Public item 1").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withAuthor("Doe, John").withSubject("History of religion", "VR110102", 600).build();
    Item publicItem2 = ItemBuilder.createItem(context, col2).withTitle("Public item 2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("Church studies", "VR110103", 600).withSubject("History of religion", "VR110102", 600).build();
    Item publicItem3 = ItemBuilder.createItem(context, col2).withTitle("Public item 2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("Missionary studies", "VR110104", 600).withSubject("Church studies", "VR110103", 600).withSubject("History of religion", "VR110102", 600).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/subject/entries").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Church studies", "VR110103", 2), BrowseEntryResourceMatcher.matchBrowseEntry("History of religion", "VR110102", 3), BrowseEntryResourceMatcher.matchBrowseEntry("Missionary studies", "VR110104", 1))));
    getClient().perform(get("/api/discover/browses/subject/entries").param("sort", "value,desc")).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Missionary studies", "VR110104", 1), BrowseEntryResourceMatcher.matchBrowseEntry("History of religion", "VR110102", 3), BrowseEntryResourceMatcher.matchBrowseEntry("Church studies", "VR110103", 2))));
    DSpaceServicesFactory.getInstance().getConfigurationService().reloadConfig();
    metadataAuthorityService.clearCache();
  }

  @Test public void findBrowseBySubjectItems() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item publicItem1 = ItemBuilder.createItem(context, col1).withTitle("zPublic item more").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withAuthor("Doe, John").withSubject("ExtraEntry").withSubject("AnotherTest").build();
    Item publicItem2 = ItemBuilder.createItem(context, col2).withTitle("Public item 2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("AnotherTest").build();
    Item publicItem3 = ItemBuilder.createItem(context, col2).withTitle("Public item 3").withIssueDate("2016-02-14").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("AnotherTest").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/subject/items").param("filterValue", "ExtraEntry")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(publicItem1, "zPublic item more", "2017-10-17"))));
    getClient().perform(get("/api/discover/browses/subject/items").param("filterValue", "AnotherTest")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(publicItem2, "Public item 2", "2016-02-13"), ItemMatcher.matchItemWithTitleAndDateIssued(publicItem3, "Public item 3", "2016-02-14"), ItemMatcher.matchItemWithTitleAndDateIssued(publicItem1, "zPublic item more", "2017-10-17"))));
  }

  @Test public void findBrowseByTitleItems() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item publicItem1 = ItemBuilder.createItem(context, col1).withTitle("Public item 1").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withAuthor("Doe, John").withSubject("Java").withSubject("Unit Testing").build();
    Item publicItem2 = ItemBuilder.createItem(context, col2).withTitle("Public item 2").withIssueDate("2016-02-13").withAuthor("Smith, Maria").withAuthor("Doe, Jane").withSubject("Angular").withSubject("Unit Testing").build();
    Item privateItem = ItemBuilder.createItem(context, col1).withTitle("This is a private item").withIssueDate("2015-03-12").withAuthor("Duck, Donald").withSubject("Cartoons").withSubject("Ducks").makeUnDiscoverable().build();
    Item embargoedItem = ItemBuilder.createItem(context, col2).withTitle("An embargoed publication").withIssueDate("2017-08-10").withAuthor("Mouse, Mickey").withSubject("Cartoons").withSubject("Mice").withEmbargoPeriod("12 months").build();
    Group internalGroup = GroupBuilder.createGroup(context).withName("Internal Group").build();
    Item internalItem = ItemBuilder.createItem(context, col2).withTitle("Internal publication").withIssueDate("2016-09-19").withAuthor("Doe, John").withSubject("Unknown").withReaderGroup(internalGroup).build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/title/items").param("sort", "title,desc")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.totalPages", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(publicItem2, "Public item 2", "2016-02-13"), ItemMatcher.matchItemWithTitleAndDateIssued(publicItem1, "Public item 1", "2017-10-17")))).andExpect(jsonPath("$._embedded.items[*].metadata", Matchers.allOf(not(matchMetadata("dc.title", "This is a private item")), not(matchMetadata("dc.title", "Internal publication")))));
  }

  @Test public void browsePaginationWithoutExplicitParams() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, parentCommunity).withName("Collection 1").build();
    for (int i = 0; i <= 20; i++) {
      ItemBuilder.createItem(context, col1).withTitle("Public item " + String.format("%02d", i)).withIssueDate("2017-10-17").withAuthor("Test, Author" + String.format("%02d", i)).withSubject("Java").withSubject("Unit Testing").build();
    }
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/title/items")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(21))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.next.href", Matchers.containsString("/api/discover/browses/title/items?"))).andExpect(jsonPath("$._links.last.href", Matchers.containsString("/api/discover/browses/title/items?"))).andExpect(jsonPath("$._links.self.href", Matchers.endsWith("/api/discover/browses/title/items")));
    getClient().perform(get("/api/discover/browses/author/entries")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(21))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.next.href", Matchers.containsString("/api/discover/browses/author/entries?"))).andExpect(jsonPath("$._links.last.href", Matchers.containsString("/api/discover/browses/author/entries?"))).andExpect(jsonPath("$._links.self.href", Matchers.endsWith("/api/discover/browses/author/entries")));
  }

  @Test public void testPaginationBrowseByDateIssuedItems() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("Item 1").withIssueDate("2017-10-17").build();
    Item item2 = ItemBuilder.createItem(context, col2).withTitle("Item 2").withIssueDate("2016-02-13").build();
    Item item3 = ItemBuilder.createItem(context, col1).withTitle("Item 3").withIssueDate("2016-02-12").build();
    Item item4 = ItemBuilder.createItem(context, col2).withTitle("Item 4").withIssueDate("2016-02-11").build();
    Item item5 = ItemBuilder.createItem(context, col1).withTitle("Item 5").withIssueDate("2016-02-10").build();
    Item item6 = ItemBuilder.createItem(context, col2).withTitle("Item 6").withIssueDate("2016-01-13").build();
    Item item7 = ItemBuilder.createItem(context, col1).withTitle("Item 7").withIssueDate("2016-01-12").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/dateissued/items").param("sort", "title,asc").param("size", "5")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(5))).andExpect(jsonPath("$.page.totalElements", is(7))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item1, "Item 1", "2017-10-17"), ItemMatcher.matchItemWithTitleAndDateIssued(item2, "Item 2", "2016-02-13"), ItemMatcher.matchItemWithTitleAndDateIssued(item3, "Item 3", "2016-02-12"), ItemMatcher.matchItemWithTitleAndDateIssued(item4, "Item 4", "2016-02-11"), ItemMatcher.matchItemWithTitleAndDateIssued(item5, "Item 5", "2016-02-10"))));
    getClient().perform(get("/api/discover/browses/dateissued/items").param("sort", "title,asc").param("size", "5").param("page", "1")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(5))).andExpect(jsonPath("$.page.totalElements", is(7))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item6, "Item 6", "2016-01-13"), ItemMatcher.matchItemWithTitleAndDateIssued(item7, "Item 7", "2016-01-12"))));
  }

  @Test public void testBrowseByEntriesStartsWith() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("Alan Turing").withAuthor("Turing, Alan Mathison").withIssueDate("1912-06-23").withSubject("Computing").build();
    Item item2 = ItemBuilder.createItem(context, col1).withTitle("Blade Runner").withAuthor("Scott, Ridley").withIssueDate("1982-06-25").withSubject("Science Fiction").build();
    Item item3 = ItemBuilder.createItem(context, col1).withTitle("Python").withAuthor("Van Rossum, Guido").withIssueDate("1990").withSubject("Computing").build();
    Item item4 = ItemBuilder.createItem(context, col2).withTitle("Java").withAuthor("Gosling, James").withIssueDate("1995-05-23").withSubject("Computing").build();
    Item item5 = ItemBuilder.createItem(context, col2).withTitle("Zeta Reticuli").withAuthor("Universe").withIssueDate("2018-01-01").withSubject("Astronomy").build();
    Item item6 = ItemBuilder.createItem(context, col2).withTitle("Moon").withAuthor("Universe").withIssueDate("2018-01-02").withSubject("Astronomy").build();
    Item item7 = ItemBuilder.createItem(context, col2).withTitle("T-800").withAuthor("Cameron, James").withIssueDate("2029").withSubject("Science Fiction").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/author/entries?startsWith=U").param("size", "2").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Universe", 2)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=U")));
    getClient().perform(get("/api/discover/browses/author/entries?startsWith=T").param("scope", col1.getID().toString())).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Turing, Alan Mathison", 1)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=T")));
    getClient().perform(get("/api/discover/browses/subject/entries?startsWith=C")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Computing", 3)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=C")));
  }



  @Test public void testBrowseByEntriesStartsWithAndDiacritics() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("Item1").withAuthor("\u00c1lvarez, Nombre").withIssueDate("1912-06-23").withSubject("Tel\u00e9fono").build();
    Item item2 = ItemBuilder.createItem(context, col1).withTitle("Item2").withAuthor("\u00d6gren, Name").withIssueDate("1982-06-25").withSubject("Televisor").build();
    Item item3 = ItemBuilder.createItem(context, col2).withTitle("Item3").withAuthor("Azuaga, Nombre").withIssueDate("1990").withSubject("Telecomunicaciones").build();
    Item item4 = ItemBuilder.createItem(context, col2).withTitle("Item4").withAuthor("Alonso, Nombre").withAuthor("Ortiz, Nombre").withIssueDate("1995-05-23").withSubject("Guion").build();
    getClient().perform(get("/api/discover/browses/author/entries?startsWith=A").param("size", "4")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Alonso, Nombre", 1), BrowseEntryResourceMatcher.matchBrowseEntry("\u00c1lvarez, Nombre", 1), BrowseEntryResourceMatcher.matchBrowseEntry("Azuaga, Nombre", 1)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=A")));
    getClient().perform(get("/api/discover/browses/author/entries?startsWith=\u00d3")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("\u00d6gren, Name", 1), BrowseEntryResourceMatcher.matchBrowseEntry("Ortiz, Nombre", 1)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=\u00d3")));
    getClient().perform(get("/api/discover/browses/subject/entries?startsWith=Tele")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(3))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Telecomunicaciones", 1), BrowseEntryResourceMatcher.matchBrowseEntry("Tel\u00e9fono", 1), BrowseEntryResourceMatcher.matchBrowseEntry("Televisor", 1)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=Tele")));
    getClient().perform(get("/api/discover/browses/subject/entries?startsWith=Gui\u00f3n")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._embedded.entries", contains(BrowseEntryResourceMatcher.matchBrowseEntry("Guion", 1)))).andExpect(jsonPath("$._links.self.href", containsString("?startsWith=Gui\u00f3n")));
  }



  @Test public void testBrowseByItemsStartsWith() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("Alan Turing").withAuthor("Turing, Alan Mathison").withIssueDate("1912-06-23").withSubject("Computing").build();
    Item item2 = ItemBuilder.createItem(context, col1).withTitle("Blade Runner").withAuthor("Scott, Ridley").withIssueDate("1982-06-25").withSubject("Science Fiction").build();
    Item item3 = ItemBuilder.createItem(context, col1).withTitle("Python").withAuthor("Van Rossum, Guido").withIssueDate("1990").withSubject("Computing").build();
    Item item4 = ItemBuilder.createItem(context, col2).withTitle("Java").withAuthor("Gosling, James").withIssueDate("1995-05-23").withSubject("Computing").build();
    Item item5 = ItemBuilder.createItem(context, col2).withTitle("Zeta Reticuli").withAuthor("Universe").withIssueDate("2018-01-01").withSubject("Astronomy").build();
    Item item6 = ItemBuilder.createItem(context, col2).withTitle("Moon").withAuthor("Universe").withIssueDate("2018-01-02").withSubject("Astronomy").build();
    Item item7 = ItemBuilder.createItem(context, col2).withTitle("T-800").withAuthor("Cameron, James").withIssueDate("2029").withSubject("Science Fiction").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/dateissued/items?startsWith=199").param("size", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$.page.size", is(2))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item3, "Python", "1990"), ItemMatcher.matchItemWithTitleAndDateIssued(item4, "Java", "1995-05-23"))));
    getClient().perform(get("/api/discover/browses/title/items?startsWith=T").param("size", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.self.href", containsString("startsWith=T"))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item7, "T-800", "2029"))));
    getClient().perform(get("/api/discover/browses/title/items?startsWith=Blade").param("scope", col1.getID().toString()).param("size", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.self.href", containsString("startsWith=Blade"))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item2, "Blade Runner", "1982-06-25"))));
  }

  @Test public void testBrowseByStartsWithAndPage() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("Alan Turing").withAuthor("Turing, Alan Mathison").withIssueDate("1912-06-23").withSubject("Computing").build();
    Item item2 = ItemBuilder.createItem(context, col1).withTitle("Blade Runner").withAuthor("Scott, Ridley").withIssueDate("1982-06-25").withSubject("Science Fiction").build();
    Item item3 = ItemBuilder.createItem(context, col2).withTitle("Java").withAuthor("Gosling, James").withIssueDate("1995-05-23").withSubject("Computing").build();
    Item item4 = ItemBuilder.createItem(context, col2).withTitle("Moon").withAuthor("Universe").withIssueDate("2018-01-02").withSubject("Astronomy").build();
    Item item5 = ItemBuilder.createItem(context, col1).withTitle("Python").withAuthor("Van Rossum, Guido").withIssueDate("1990").withSubject("Computing").build();
    Item item6 = ItemBuilder.createItem(context, col2).withTitle("T-800").withAuthor("Cameron, James").withIssueDate("2029").withSubject("Science Fiction").build();
    Item item7 = ItemBuilder.createItem(context, col2).withTitle("Zeta Reticuli").withAuthor("Universe").withIssueDate("2018-01-01").withSubject("Astronomy").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/dateissued/items?startsWith=199").param("size", "1").param("page", "1")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.number", is(1))).andExpect(jsonPath("$.page.size", is(1))).andExpect(jsonPath("$._links.self.href", containsString("startsWith=199"))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item3, "Java", "1995-05-23"))));
  }

  @Test public void testBrowseByTitleStartsWithAndDiacritics() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, parentCommunity).withName("Collection 1").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("N\u00famero 1").withAuthor("Surname, Name").withIssueDate("2020").build();
    Item item2 = ItemBuilder.createItem(context, col1).withTitle("Numero 2").withAuthor("Surname, Name").withIssueDate("2010").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/title/items?startsWith=Num").param("size", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.self.href", containsString("startsWith=Num"))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item1, "N\u00famero 1", "2020"), ItemMatcher.matchItemWithTitleAndDateIssued(item2, "Numero 2", "2010"))));
    getClient().perform(get("/api/discover/browses/title/items?startsWith=N\u00fam").param("size", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.totalElements", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.self.href", containsString("startsWith=N\u00fam"))).andExpect(jsonPath("$._embedded.items", contains(ItemMatcher.matchItemWithTitleAndDateIssued(item1, "N\u00famero 1", "2020"), ItemMatcher.matchItemWithTitleAndDateIssued(item2, "Numero 2", "2010"))));
  }

  @Test public void findBrowseByTitleItemsFullProjectionTest() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item publicItem1 = ItemBuilder.createItem(context, col1).withTitle("Public item 1").withIssueDate("2017-10-17").withAuthor("Smith, Donald").withAuthor("Doe, John").withSubject("Java").withSubject("Unit Testing").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/title/items").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.items[0]._embedded.owningCollection._embedded.adminGroup").doesNotExist());
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/discover/browses/title/items").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.items[0]._embedded.owningCollection._embedded.adminGroup", nullValue()));
  }

  @Test public void browseByAuthorFullProjectionTest() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, parentCommunity).withName("Collection 1").build();
    for (int i = 0; i <= 20; i++) {
      ItemBuilder.createItem(context, col1).withTitle("Public item " + String.format("%02d", i)).withIssueDate("2017-10-17").withAuthor("Test, Author" + String.format("%02d", i)).withSubject("Java").withSubject("Unit Testing").build();
    }
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/author/entries").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(21))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.next.href", Matchers.containsString("/api/discover/browses/author/entries"))).andExpect(jsonPath("$._links.last.href", Matchers.containsString("/api/discover/browses/author/entries"))).andExpect(jsonPath("$._links.self.href", Matchers.endsWith("/api/discover/browses/author/entries")));
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/discover/browses/author/entries").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(21))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.next.href", Matchers.containsString("/api/discover/browses/author/entries"))).andExpect(jsonPath("$._links.last.href", Matchers.containsString("/api/discover/browses/author/entries"))).andExpect(jsonPath("$._links.self.href", Matchers.endsWith("/api/discover/browses/author/entries")));
    getClient().perform(get("/api/discover/browses/author/entries")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", is(21))).andExpect(jsonPath("$.page.totalPages", is(2))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.next.href", Matchers.containsString("/api/discover/browses/author/entries"))).andExpect(jsonPath("$._links.last.href", Matchers.containsString("/api/discover/browses/author/entries"))).andExpect(jsonPath("$._links.self.href", Matchers.endsWith("/api/discover/browses/author/entries")));
  }

  @Test public void testBrowseByDateIssuedItemsFullProjectionTest() throws Exception {
    context.turnOffAuthorisationSystem();
    parentCommunity = CommunityBuilder.createCommunity(context).withName("Parent Community").build();
    Community child1 = CommunityBuilder.createSubCommunity(context, parentCommunity).withName("Sub Community").build();
    Collection col1 = CollectionBuilder.createCollection(context, child1).withName("Collection 1").build();
    Collection col2 = CollectionBuilder.createCollection(context, child1).withName("Collection 2").build();
    Item item1 = ItemBuilder.createItem(context, col1).withTitle("Item 1").withIssueDate("2017-10-17").build();
    context.restoreAuthSystemState();
    getClient().perform(get("/api/discover/browses/dateissued/items").param("projection", "full")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.items[0]._embedded.owningCollection._embedded.adminGroup").doesNotExist());
    String adminToken = getAuthToken(admin.getEmail(), password);
    getClient(adminToken).perform(get("/api/discover/browses/dateissued/items").param("projection", "full")).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.items[0]._embedded.owningCollection._embedded.adminGroup", nullValue()));
  }
}