package com.github.mongobee;
import static org.junit.Assert.assertEquals;
import com.github.fakemongo.Fongo;
import static org.mockito.Matchers.any;
import com.github.mongobee.changeset.ChangeEntry;
import static org.mockito.Matchers.anyString;
import com.github.mongobee.dao.ChangeEntryDao;
import static org.mockito.Mockito.doCallRealMethod;
import com.github.mongobee.dao.ChangeEntryIndexDao;
import static org.mockito.Mockito.when;
import com.github.mongobee.resources.EnvironmentMock;
import org.bson.Document;
import com.github.mongobee.test.changelogs.AnotherMongobeeTestResource;
import org.junit.Before;
import com.github.mongobee.test.profiles.def.UnProfiledChangeLog;
import org.junit.Test;
import com.github.mongobee.test.profiles.dev.ProfiledDevChangeLog;
import org.junit.runner.RunWith;
import com.mongodb.DB;
import org.mockito.InjectMocks;
import com.mongodb.MongoClientURI;
import org.mockito.Mock;
import com.mongodb.client.MongoDatabase;
import org.junit.After;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * Tests for Spring profiles integration
 *
 * @author lstolowski
 * @since 2014-09-17
 */
@RunWith(value = MockitoJUnitRunner.class) public class MongobeeProfileTest {
  private static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  public static final int CHANGELOG_COUNT = 13;

  @InjectMocks private Mongobee runner = new Mongobee();

  @Mock private ChangeEntryDao dao;

  @Mock private ChangeEntryIndexDao indexDao;

  private DB fakeDb;

  private MongoDatabase fakeMongoDatabase;

  @Before public void init() throws Exception {
    fakeDb = new Fongo("testServer").getDB("mongobeetest");
    fakeMongoDatabase = new Fongo("testServer").getDatabase("mongobeetest");
    when(dao.connectMongoDb(any(MongoClientURI.class), anyString())).thenReturn(fakeMongoDatabase);
    when(dao.getDb()).thenReturn(fakeDb);
    when(dao.getMongoDatabase()).thenReturn(fakeMongoDatabase);
    when(dao.acquireProcessLock()).thenReturn(true);
    doCallRealMethod().when(dao).save(any(ChangeEntry.class));
    doCallRealMethod().when(dao).setChangelogCollectionName(anyString());
    doCallRealMethod().when(dao).setIndexDao(any(ChangeEntryIndexDao.class));
    dao.setIndexDao(indexDao);
    dao.setChangelogCollectionName(CHANGELOG_COLLECTION_NAME);
    runner.setDbName("mongobeetest");
    runner.setEnabled(true);
  }

  @Test public void shouldRunDevProfileAndNonAnnotated() throws Exception {
    ConfigurableEnvironment env = new StandardEnvironment();
    env.addActiveProfile("dev");
    runner.setSpringEnvironment(env);
    runner.setChangeLogsScanPackage(ProfiledDevChangeLog.class.getPackage().getName());
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.execute();
    long change1 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev1").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
    long change2 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev4").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);
    long change3 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev3").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(0, change3);
  }

  @Test public void shouldRunUnprofiledChangeLog() throws Exception {
    runner.setSpringEnvironment(new EnvironmentMock("test"));
    runner.setChangeLogsScanPackage(UnProfiledChangeLog.class.getPackage().getName());
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.execute();
    long change1 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev1").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
    long change2 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev2").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change2);
    long change3 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev3").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change3);
    long change4 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev4").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(0, change4);
    long change5 = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document().append(ChangeEntry.KEY_CHANGEID, "Pdev5").append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change5);
  }

  @Test public void shouldNotRunAnyChangeSet() throws Exception {
    runner.setSpringEnvironment(new EnvironmentMock("foobar"));
    runner.setChangeLogsScanPackage(ProfiledDevChangeLog.class.getPackage().getName());
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.execute();
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(0, changes);
  }

  @Test public void shouldRunChangeSetsWhenNoEnv() throws Exception {
    runner.setSpringEnvironment(null);
    runner.setChangeLogsScanPackage(AnotherMongobeeTestResource.class.getPackage().getName());
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.execute();
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @Test public void shouldRunChangeSetsWhenEmptyEnv() throws Exception {
    runner.setSpringEnvironment(new EnvironmentMock());
    runner.setChangeLogsScanPackage(AnotherMongobeeTestResource.class.getPackage().getName());
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.execute();
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @Test public void shouldRunAllChangeSets() throws Exception {
    runner.setSpringEnvironment(new EnvironmentMock("dev"));
    runner.setChangeLogsScanPackage(AnotherMongobeeTestResource.class.getPackage().getName());
    when(dao.isNewChange(any(ChangeEntry.class))).thenReturn(true);
    runner.execute();
    long changes = fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).count(new Document());
    assertEquals(CHANGELOG_COUNT, changes);
  }

  @After public void cleanUp() {
    runner.setMongoTemplate(null);
    runner.setJongo(null);
    fakeDb.dropDatabase();
  }
}