package org.dspace.app.rest;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Locale;
import org.dspace.app.rest.matcher.SubmissionFormFieldMatcher;
import org.dspace.app.rest.repository.SubmissionFormRestRepository;
import org.dspace.app.rest.test.AbstractControllerIntegrationTest;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.builder.EPersonBuilder;
import org.dspace.content.authority.DCInputAuthority;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.core.service.PluginService;
import org.dspace.eperson.EPerson;
import org.dspace.services.ConfigurationService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test to test the /api/config/submissionforms endpoint
 * (Class has to start or end with IT to be picked up by the failsafe plugin)
 */
public class SubmissionFormsControllerIT extends AbstractControllerIntegrationTest {
  @Autowired private ConfigurationService configurationService;

  @Autowired private SubmissionFormRestRepository submissionFormRestRepository;

  @Autowired private PluginService pluginService;

  @Autowired private ChoiceAuthorityService cas;

  @Test public void findAll() throws Exception {
    getClient().perform(get("/api/config/submissionforms")).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", equalTo(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms"))).andExpect(jsonPath("$._embedded.submissionforms", hasSize(equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))));
  }

  @Test public void findAllWithNewlyCreatedAccountTest() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.page.size", is(20))).andExpect(jsonPath("$.page.totalElements", equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", equalTo(1))).andExpect(jsonPath("$.page.number", is(0))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms"))).andExpect(jsonPath("$._embedded.submissionforms", hasSize(equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))));
  }

  @Test public void findTraditionalPageOne() throws Exception {
    getClient().perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isUnauthorized());
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("traditionalpageone"))).andExpect(jsonPath("$.name", is("traditionalpageone"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/traditionalpageone"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Author", null, null, true, "Add an author", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Title", null, "You must enter a main title for this item.", false, "Enter the main title of the item.", "dc.title")))).andExpect(jsonPath("$.rows[3].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("date", "Date of Issue", null, "You must enter at least the year.", false, "Please give the date", "col-sm-4", "dc.date.issued"), SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Publisher", null, null, false, "Enter the name of", "col-sm-8", "dc.publisher"))));
  }

  @Test public void findTraditionalPageOneWithNewlyCreatedAccountTest() throws Exception {
    String token = getAuthToken(eperson.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("traditionalpageone"))).andExpect(jsonPath("$.name", is("traditionalpageone"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/traditionalpageone"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Author", null, null, true, "Add an author", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Title", null, "You must enter a main title for this item.", false, "Enter the main title of the item.", "dc.title")))).andExpect(jsonPath("$.rows[3].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("date", "Date of Issue", null, "You must enter at least the year.", false, "Please give the date", "col-sm-4", "dc.date.issued"), SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Publisher", null, null, false, "Enter the name of", "col-sm-8", "dc.publisher"))));
  }

  @Test public void findFieldWithAuthorityConfig() throws Exception {
    configurationService.setProperty("plugin.named.org.dspace.content.authority.ChoiceAuthority", new String[] { "org.dspace.content.authority.SolrAuthority = SolrAuthorAuthority", "org.dspace.content.authority.SolrAuthority = SolrEditorAuthority", "org.dspace.content.authority.SolrAuthority = SolrSubjectAuthority" });
    configurationService.setProperty("solr.authority.server", "${solr.server}/authority");
    configurationService.setProperty("choices.plugin.dc.contributor.author", "SolrAuthorAuthority");
    configurationService.setProperty("choices.presentation.dc.contributor.author", "suggest");
    configurationService.setProperty("authority.controlled.dc.contributor.author", "true");
    configurationService.setProperty("authority.author.indexer.field.1", "dc.contributor.author");
    configurationService.setProperty("choices.plugin.dc.contributor.editor", "SolrEditorAuthority");
    configurationService.setProperty("choices.presentation.dc.contributor.editor", "authorLookup");
    configurationService.setProperty("authority.controlled.dc.contributor.editor", "true");
    configurationService.setProperty("authority.author.indexer.field.2", "dc.contributor.editor");
    configurationService.setProperty("choices.plugin.dc.subject", "SolrSubjectAuthority");
    configurationService.setProperty("choices.presentation.dc.subject", "lookup");
    configurationService.setProperty("authority.controlled.dc.subject", "true");
    configurationService.setProperty("authority.author.indexer.field.3", "dc.subject");
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/sampleauthority")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("sampleauthority"))).andExpect(jsonPath("$.name", is("sampleauthority"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/sampleauthority"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Author", null, null, true, "Author field that can be associated with an authority providing suggestion", null, "dc.contributor.author", "SolrAuthorAuthority")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("lookup-name", "Editor", null, null, false, "Editor field that can be associated with an authority " + "providing the special name lookup", null, "dc.contributor.editor", "SolrEditorAuthority")))).andExpect(jsonPath("$.rows[2].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("lookup", "Subject", null, null, true, "Subject field that can be associated with an authority providing lookup", null, "dc.subject", "SolrSubjectAuthority"))));
    configurationService.reloadConfig();
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
  }

  @Test public void findFieldWithValuePairsConfig() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("traditionalpageone"))).andExpect(jsonPath("$.name", is("traditionalpageone"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/traditionalpageone"))).andExpect(jsonPath("$.rows[7].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("dropdown", "Type", null, null, true, "Select the type(s) of content of the item. To select more than one value in the " + "list, you may have to hold down the \"CTRL\" or \"Shift\" key.", null, "dc.type", "common_types"))));
  }

  @Test public void findFieldWithTypeBindConfig() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("traditionalpageone"))).andExpect(jsonPath("$.name", is("traditionalpageone"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/traditionalpageone"))).andExpect(jsonPath("$.rows[5].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("series", "Series/Report No.", "Technical Report", null, true, "Enter the series and number assigned to this item by your community.", "dc.relation.ispartofseries")))).andExpect(((jsonPath("$.rows[5].fields", not(contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("series", "Series/Report No.", "Article", null, true, "Enter the series and number assigned to this item by your community.", "dc.relation.ispartofseries")))))));
  }

  @Test public void findOpenRelationshipConfig() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("traditionalpageone"))).andExpect(jsonPath("$.name", is("traditionalpageone"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/traditionalpageone"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormOpenRelationshipFieldDefinition("name", "Author", null, true, "Add an author", "dc.contributor.author", "isAuthorOfPublication", null, "person", true))));
  }

  @Test public void findClosedRelationshipConfig() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/journalVolumeStep")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("journalVolumeStep"))).andExpect(jsonPath("$.name", is("journalVolumeStep"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/journalVolumeStep"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormClosedRelationshipFieldDefinition("Journal", null, false, "Select the journal related to this volume.", "isJournalOfVolume", "creativework.publisher:somepublishername", "periodical", false))));
  }

  @Test public void languageSupportTest() throws Exception {
    context.turnOffAuthorisationSystem();
    String[] supportedLanguage = { "it", "uk" };
    configurationService.setProperty("default.locale", "it");
    configurationService.setProperty("webui.supported.locales", supportedLanguage);
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
    Locale uk = new Locale("uk");
    Locale it = new Locale("it");
    context.restoreAuthSystemState();
    String tokenEperson = getAuthToken(eperson.getEmail(), password);
    getClient(tokenEperson).perform(get("/api/config/submissionforms/languagetest").locale(it)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Autore", null, "\u00c8" + " richiesto almeno un autore", true, "Aggiungi un autore", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Titolo", null, "\u00c8" + " necessario inserire un titolo principale per questo item", false, "Inserisci titolo principale di questo item", "dc.title")))).andExpect(jsonPath("$.rows[2].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("dropdown", "Lingua", null, null, false, "Selezionare la lingua del contenuto principale dell\'item." + " Se la lingua non compare nell\'elenco, selezionare (Altro)." + " Se il contenuto non ha davvero una lingua" + " (ad esempio, se \u00e8 un set di dati o un\'immagine) selezionare (N/A)", null, "dc.language.iso", "common_iso_languages"))));
    getClient(tokenEperson).perform(get("/api/config/submissionforms/languagetest").locale(uk)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "\u0410\u0432\u0442\u043e\u0440", null, "\u041f\u043e\u0442\u0440\u0456\u0431\u043d\u043e \u0432\u0432\u0435\u0441\u0442\u0438 \u0445\u043e\u0447\u0430\u0431 \u043e\u0434\u043d\u043e\u0433\u043e \u0430\u0432\u0442\u043e\u0440\u0430!", true, "\u0414\u043e\u0434\u0430\u0442\u0438 \u0430\u0432\u0442\u043e\u0440\u0430", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "\u0417\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a", null, "\u0417\u0430\u0433\u043e\u0432\u043e\u043b\u043e\u043a \u0444\u0430\u0439\u043b\u0430 \u043e\u0431\u043e\u0432\'\u044f\u0437\u043a\u043e\u0432\u0438\u0439 !", false, "\u0412\u0432\u0435\u0441\u0442\u0438 \u043e\u0441\u043d\u043e\u0432\u043d\u0438\u0439 \u0437\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a \u0444\u0430\u0439\u043b\u0430", "dc.title")))).andExpect(jsonPath("$.rows[2].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("dropdown", "\u041c\u043e\u0432\u0430", null, null, false, "\u0412\u0438\u0431\u0435\u0440i\u0442\u044c \u043c\u043e\u0432\u0443 \u0433\u043e\u043b\u043e\u0432\u043d\u043e\u0433\u043e \u0437\u043ci\u0441\u0442\u0443 \u0444\u0430\u0439\u043b\u0443, \u044f\u043a \u0449\u043e \u043c\u043e\u0432\u0438 \u043d\u0435\u043c\u0430\u0454 \u0443 \u0441\u043f\u0438\u0441\u043a\u0443, \u0432\u0438\u0431\u0440\u0430\u0442\u0438 (I\u043d\u0448\u0430)." + " \u042f\u043a \u0449\u043e \u0432\u043ci\u0441\u0442 \u0432\u0430\u0439\u043b\u0443 \u043d\u0435 \u0454 \u0442\u0435\u043a\u0441\u0442\u043e\u0432\u0438\u043c, \u043d\u0430\u043f\u0440\u0438\u043a\u043b\u0430\u0434 \u0454 \u0444\u043e\u0442\u043e\u0433\u0440\u0430\u0444i\u0454\u044e, \u0442\u043e\u0434i \u0432\u0438\u0431\u0440\u0430\u0442\u0438 (N/A)", null, "dc.language.iso", "common_iso_languages"))));
    resetLocalesConfiguration();
  }

  @Test public void preferLanguageTest() throws Exception {
    context.turnOffAuthorisationSystem();
    String[] supportedLanguage = { "it", "uk" };
    configurationService.setProperty("default.locale", "it");
    configurationService.setProperty("webui.supported.locales", supportedLanguage);
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
    EPerson epersonIT = EPersonBuilder.createEPerson(context).withEmail("epersonIT@example.com").withPassword(password).withLanguage("it").build();
    EPerson epersonUK = EPersonBuilder.createEPerson(context).withEmail("epersonUK@example.com").withPassword(password).withLanguage("uk").build();
    context.restoreAuthSystemState();
    String tokenEpersonIT = getAuthToken(epersonIT.getEmail(), password);
    String tokenEpersonUK = getAuthToken(epersonUK.getEmail(), password);
    getClient(tokenEpersonIT).perform(get("/api/config/submissionforms/languagetest")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Autore", null, "\u00c8" + " richiesto almeno un autore", true, "Aggiungi un autore", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Titolo", null, "\u00c8" + " necessario inserire un titolo principale per questo item", false, "Inserisci titolo principale di questo item", "dc.title")))).andExpect(jsonPath("$.rows[2].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("dropdown", "Lingua", null, null, false, "Selezionare la lingua del contenuto principale dell\'item." + " Se la lingua non compare nell\'elenco, selezionare (Altro)." + " Se il contenuto non ha davvero una lingua" + " (ad esempio, se \u00e8 un set di dati o un\'immagine) selezionare (N/A)", null, "dc.language.iso", "common_iso_languages"))));
    getClient(tokenEpersonUK).perform(get("/api/config/submissionforms/languagetest")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "\u0410\u0432\u0442\u043e\u0440", null, "\u041f\u043e\u0442\u0440\u0456\u0431\u043d\u043e \u0432\u0432\u0435\u0441\u0442\u0438 \u0445\u043e\u0447\u0430\u0431 \u043e\u0434\u043d\u043e\u0433\u043e \u0430\u0432\u0442\u043e\u0440\u0430!", true, "\u0414\u043e\u0434\u0430\u0442\u0438 \u0430\u0432\u0442\u043e\u0440\u0430", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "\u0417\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a", null, "\u0417\u0430\u0433\u043e\u0432\u043e\u043b\u043e\u043a \u0444\u0430\u0439\u043b\u0430 \u043e\u0431\u043e\u0432\'\u044f\u0437\u043a\u043e\u0432\u0438\u0439 !", false, "\u0412\u0432\u0435\u0441\u0442\u0438 \u043e\u0441\u043d\u043e\u0432\u043d\u0438\u0439 \u0437\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a \u0444\u0430\u0439\u043b\u0430", "dc.title")))).andExpect(jsonPath("$.rows[2].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("dropdown", "\u041c\u043e\u0432\u0430", null, null, false, "\u0412\u0438\u0431\u0435\u0440i\u0442\u044c \u043c\u043e\u0432\u0443 \u0433\u043e\u043b\u043e\u0432\u043d\u043e\u0433\u043e \u0437\u043ci\u0441\u0442\u0443 \u0444\u0430\u0439\u043b\u0443, \u044f\u043a \u0449\u043e \u043c\u043e\u0432\u0438 \u043d\u0435\u043c\u0430\u0454 \u0443 \u0441\u043f\u0438\u0441\u043a\u0443, \u0432\u0438\u0431\u0440\u0430\u0442\u0438 (I\u043d\u0448\u0430)." + " \u042f\u043a \u0449\u043e \u0432\u043ci\u0441\u0442 \u0432\u0430\u0439\u043b\u0443 \u043d\u0435 \u0454 \u0442\u0435\u043a\u0441\u0442\u043e\u0432\u0438\u043c, \u043d\u0430\u043f\u0440\u0438\u043a\u043b\u0430\u0434 \u0454 \u0444\u043e\u0442\u043e\u0433\u0440\u0430\u0444i\u0454\u044e, \u0442\u043e\u0434i \u0432\u0438\u0431\u0440\u0430\u0442\u0438 (N/A)", null, "dc.language.iso", "common_iso_languages"))));
    resetLocalesConfiguration();
  }

  @Test public void userChoiceAnotherLanguageTest() throws Exception {
    context.turnOffAuthorisationSystem();
    String[] supportedLanguage = { "it", "uk" };
    configurationService.setProperty("default.locale", "it");
    configurationService.setProperty("webui.supported.locales", supportedLanguage);
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
    Locale it = new Locale("it");
    EPerson epersonUK = EPersonBuilder.createEPerson(context).withEmail("epersonUK@example.com").withPassword(password).withLanguage("uk").build();
    context.restoreAuthSystemState();
    String tokenEpersonUK = getAuthToken(epersonUK.getEmail(), password);
    getClient(tokenEpersonUK).perform(get("/api/config/submissionforms/languagetest").locale(it)).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Autore", null, "\u00c8" + " richiesto almeno un autore", true, "Aggiungi un autore", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Titolo", null, "\u00c8" + " necessario inserire un titolo principale per questo item", false, "Inserisci titolo principale di questo item", "dc.title")))).andExpect(jsonPath("$.rows[2].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("dropdown", "Lingua", null, null, false, "Selezionare la lingua del contenuto principale dell\'item." + " Se la lingua non compare nell\'elenco, selezionare (Altro)." + " Se il contenuto non ha davvero una lingua" + " (ad esempio, se \u00e8 un set di dati o un\'immagine) selezionare (N/A)", null, "dc.language.iso", "common_iso_languages"))));
    resetLocalesConfiguration();
  }

  @Test public void defaultLanguageTest() throws Exception {
    context.turnOffAuthorisationSystem();
    String[] supportedLanguage = { "it", "uk" };
    configurationService.setProperty("default.locale", "it");
    configurationService.setProperty("webui.supported.locales", supportedLanguage);
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
    context.restoreAuthSystemState();
    String tokenEperson = getAuthToken(eperson.getEmail(), password);
    getClient(tokenEperson).perform(get("/api/config/submissionforms/languagetest")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Autore", null, "\u00c8 richiesto almeno un autore", true, "Aggiungi un autore", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Titolo", null, "\u00c8 necessario inserire un titolo principale per questo item", false, "Inserisci titolo principale di questo item", "dc.title"))));
    resetLocalesConfiguration();
  }

  @Test public void supportLanguageUsingMultipleLocaleTest() throws Exception {
    context.turnOffAuthorisationSystem();
    String[] supportedLanguage = { "it", "uk", "en" };
    configurationService.setProperty("default.locale", "en");
    configurationService.setProperty("webui.supported.locales", supportedLanguage);
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
    context.restoreAuthSystemState();
    String tokenEperson = getAuthToken(eperson.getEmail(), password);
    getClient(tokenEperson).perform(get("/api/config/submissionforms/languagetest").header("Accept-Language", "fr;q=1, it;q=0.9")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("languagetest"))).andExpect(jsonPath("$.name", is("languagetest"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/languagetest"))).andExpect(jsonPath("$.rows[0].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("name", "Autore", null, "\u00c8 richiesto almeno un autore", true, "Aggiungi un autore", "dc.contributor.author")))).andExpect(jsonPath("$.rows[1].fields", contains(SubmissionFormFieldMatcher.matchFormFieldDefinition("onebox", "Titolo", null, "\u00c8 necessario inserire un titolo principale per questo item", false, "Inserisci titolo principale di questo item", "dc.title"))));
    resetLocalesConfiguration();
  }

  @Test public void multipleExternalSourcesTest() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/traditionalpageone")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("traditionalpageone"))).andExpect(jsonPath("$.name", is("traditionalpageone"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/traditionalpageone"))).andExpect(jsonPath("$.rows[0].fields[0].selectableRelationship.externalSources", contains(is("orcid"), is("my_staff_db"))));
  }

  @Test public void noExternalSourcesTest() throws Exception {
    String token = getAuthToken(admin.getEmail(), password);
    getClient(token).perform(get("/api/config/submissionforms/journalVolumeStep")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$.id", is("journalVolumeStep"))).andExpect(jsonPath("$.name", is("journalVolumeStep"))).andExpect(jsonPath("$.type", is("submissionform"))).andExpect(jsonPath("$._links.self.href", Matchers.startsWith(REST_SERVER_URL + "config/submissionforms/journalVolumeStep"))).andExpect(jsonPath("$.rows[0].fields[0].selectableRelationship.externalSources", nullValue()));
  }

  private void resetLocalesConfiguration() throws DCInputsReaderException {
    configurationService.setProperty("default.locale", "en");
    configurationService.setProperty("webui.supported.locales", null);
    submissionFormRestRepository.reload();
    DCInputAuthority.reset();
    pluginService.clearNamedPluginClasses();
    cas.clearCache();
  }

  @Test public void findAllPaginationTest() throws Exception {
    String tokenAdmin = getAuthToken(admin.getEmail(), password);
    getClient(tokenAdmin).perform(get("/api/config/submissionforms").param("size", "2").param("page", "0")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissionforms[0].id", is("bitstream-metadata"))).andExpect(jsonPath("$._embedded.submissionforms[1].id", is("journalVolumeStep"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=0"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=0"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=1"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=3"), Matchers.containsString("size=2")))).andExpect(jsonPath("$.page.size", is(2))).andExpect(jsonPath("$.page.totalElements", equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", equalTo(4))).andExpect(jsonPath("$.page.number", is(0)));
    getClient(tokenAdmin).perform(get("/api/config/submissionforms").param("size", "2").param("page", "1")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissionforms[0].id", is("languagetest"))).andExpect(jsonPath("$._embedded.submissionforms[1].id", is("qualdroptest"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=0"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=0"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=1"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.next.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=2"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=3"), Matchers.containsString("size=2")))).andExpect(jsonPath("$.page.size", is(2))).andExpect(jsonPath("$.page.totalElements", equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", equalTo(4))).andExpect(jsonPath("$.page.number", is(1)));
    getClient(tokenAdmin).perform(get("/api/config/submissionforms").param("size", "2").param("page", "2")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissionforms[0].id", is("traditionalpagetwo"))).andExpect(jsonPath("$._embedded.submissionforms[1].id", is("sampleauthority"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=0"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=1"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=2"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=3"), Matchers.containsString("size=2")))).andExpect(jsonPath("$.page.size", is(2))).andExpect(jsonPath("$.page.totalElements", equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", equalTo(4))).andExpect(jsonPath("$.page.number", is(2)));
    getClient(tokenAdmin).perform(get("/api/config/submissionforms").param("size", "2").param("page", "3")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$._embedded.submissionforms[0].id", is("traditionalpageone"))).andExpect(jsonPath("$._links.first.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=0"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.prev.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=2"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.self.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=3"), Matchers.containsString("size=2")))).andExpect(jsonPath("$._links.last.href", Matchers.allOf(Matchers.containsString("/api/config/submissionforms?"), Matchers.containsString("page=3"), Matchers.containsString("size=2")))).andExpect(jsonPath("$.page.size", is(2))).andExpect(jsonPath("$.page.totalElements", equalTo(
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/left.java
    7
=======
    8
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/test/java/org/dspace/app/rest/SubmissionFormsControllerIT.java/right.java
    ))).andExpect(jsonPath("$.page.totalPages", equalTo(4))).andExpect(jsonPath("$.page.number", is(3)));
  }
}