package org.schemarepo;
import java.util.Iterator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * An abstract JUnit test for thoroughly testing a Repository implementation
 */
public abstract class AbstractTestRepository<R extends Repository> {
  private static final String SUB = "sub";

  private static final String CONF = "conf";

  private static final String NOCONF = "noconf";

  private static final String VALIDATING = "validating";

  private static final String FOO = "foo";

  private static final String BAR = "bar";

  private static final String BAZ = "baz";

  protected R repo;

  protected abstract R createRepository() throws Exception;

  @Before public void setUpRepository() throws Exception {
    repo = createRepository();
  }

  @After public void tearDownRepository() throws Exception {
    repo = null;
  }

  protected final R getRepo() {
    return repo;
  }

  @Test public void testRepository() throws SchemaValidationException {
    Subject none = repo.lookup(SUB);
    Assert.assertNull("non-existent subject lookup should return null", none);
    Iterable<Subject> subjects = repo.subjects();
    Assert.assertNotNull("subjects must not be null");
    Assert.assertFalse("subjects must be empty", subjects.iterator().hasNext());
    Subject sub = repo.register(SUB, null);
    Assert.assertNotNull("failed to register subject: " + SUB, sub);
    Subject sub2 = repo.register(SUB, null);
    Assert.assertNotNull("failed to re-register subject: " + SUB, sub2);
    Assert.assertEquals("registering a subject twice did not produce the same result", sub.getName(), sub2.getName());
    Subject sub3 = repo.lookup(SUB);
    Assert.assertNotNull("subject lookup failed", sub3);
    Assert.assertEquals("subject lookup failed", sub.getName(), sub3.getName());
    Subject none2 = repo.lookup("nothing");
    Assert.assertNull("non-existent subject lookup should return null", none2);
    boolean hasSub = false;
    for (Subject s : repo.subjects()) {
      if (sub.getName().equals(s.getName())) {
        hasSub = true;
        break;
      }
    }
    Assert.assertTrue("subjects() did not contain registered subject: " + sub, hasSub);
    SchemaEntry noLatest = sub.latest();
    Assert.assertNull("latest must be null if it does not exist", noLatest);
    SchemaEntry didNotRegister = sub.registerIfLatest(FOO, new SchemaEntry("not", "there"));
    Assert.assertNull("registerIfLatest must return null if there are no schemas in the " + "subject and the passed in latest is not null. Found: " + didNotRegister, didNotRegister);
    SchemaEntry foo = sub.registerIfLatest(FOO, null);
    validateSchemaEntry(FOO, foo);
    SchemaEntry foo2 = sub.register(FOO);
    Assert.assertEquals("duplicate schema registration must be idempotent", foo, foo2);
    validateSchemaEntry(FOO, foo2);
    SchemaEntry bar = sub.registerIfLatest(BAR, foo2);
    validateSchemaEntry(BAR, bar);
    SchemaEntry none3 = sub.registerIfLatest("none", foo);
    Assert.assertNull("registerIfLatest must return null if latest does not match", none3);
    SchemaEntry none4 = sub.registerIfLatest("none", null);
    Assert.assertNull("registerIfLatest must return null if there is a latest schema in" + " the subject and the passed in value is null", none4);
    SchemaEntry baz = sub.register(BAZ);
    validateSchemaEntry(BAZ, baz);
    Subject subject = repo.lookup(SUB);
    Assert.assertNotNull("lookup of previously registered subject failed", subject);
    Assert.assertEquals("latest schema must match last registered", baz, subject.latest());
    boolean foundfoo = false, foundbar = false;
    for (SchemaEntry s : sub3.allEntries()) {
      if (s.equals(foo)) {
        foundfoo = true;
      }
      if (s.equals(bar)) {
        foundbar = true;
      }
    }
    Assert.assertTrue("AllEntries did not contain schema: " + FOO, foundfoo);
    Assert.assertTrue("AllEntries did not contain schema: " + BAR, foundbar);
    Iterator<SchemaEntry> allEntries = sub3.allEntries().iterator();
    Assert.assertEquals("Latest must be first returned from allEntries()", sub3.latest(), allEntries.next());
    Assert.assertEquals("second-latest must be BAR", bar, allEntries.next());
    Assert.assertEquals("third must be FOO", foo, allEntries.next());
    SchemaEntry resultfoo = subject.lookupBySchema(foo.getSchema());
    Assert.assertEquals("lookup by Schema did not return same result", foo, resultfoo);
    SchemaEntry notThere = subject.lookupBySchema("notThere");
    Assert.assertNull("non existent schema should return null", notThere);
    SchemaEntry resultfooid = subject.lookupById(foo.getId());
    Assert.assertEquals("lookup by ID did not return same result", foo, resultfooid);
    SchemaEntry notTherById = subject.lookupById("notThere");
    Assert.assertNull("non existent schema should return null", notTherById);
    if (subject.integralKeys()) {
      Integer.parseInt(resultfoo.getId());
    }
  }

  @Test public void testAllEntriesMultiLineSchema() throws Exception {
    String endOfLine = System.getProperty("line.separator");
    String multiLineSchema1 = "first line" + endOfLine + "second line";
    String multiLineSchema2 = "first line" + endOfLine + "second line" + endOfLine;
    repo.register("sub1", null).register(multiLineSchema1);
    repo.register("sub1", null).register(multiLineSchema2);
    Subject s1 = repo.lookup("sub1");
    Assert.assertNotNull(s1);
    Iterable<SchemaEntry> allEntries = s1.allEntries();
    boolean foundSub1 = false, foundSub2 = false;
    for (SchemaEntry entry : allEntries) {
      if (entry.getSchema().equals(multiLineSchema1)) {
        foundSub1 = true;
      } else {
        if (entry.getSchema().equals(multiLineSchema2)) {
          foundSub2 = true;
        }
      }
    }
    Assert.assertTrue("Check that allEntries() returns proper multi-line schemas", foundSub1 && foundSub2);
  }

  @Test public void testSubjectConfigs() {
    String testKey = "test.key";
    String testVal = "test.val";
    String testVal2 = "test.val2";
    SubjectConfig conf = new SubjectConfig.Builder().set(testKey, testVal).build();
    SubjectConfig conf2 = new SubjectConfig.Builder().set(testKey, testVal2).build();
    Subject none = repo.lookup(CONF);
    Assert.assertNull("non-existent subject lookup should return null", none);
    Subject sub = repo.register(CONF, conf);
    Assert.assertNotNull("failed to register subject: " + CONF, sub);
    Subject sub2 = repo.register(CONF, conf2);
    Assert.assertNotNull("failed to re-register subject: " + SUB, sub2);
    validateSubject(sub, sub2);
    Subject sub3 = repo.lookup(CONF);
    validateSubject(sub, sub3);
    Subject sub4 = repo.lookup(NOCONF);
    Assert.assertNull("subject should not exist", sub4);
  }

  @Test public void testValidation() {
    SubjectConfig conf = new SubjectConfig.Builder().addValidator(ValidatorFactory.REJECT_VALIDATOR).build();
    Subject none = repo.lookup(VALIDATING);
    Assert.assertNull("non-existent subject lookup should return null", none);
    Subject sub = repo.register(VALIDATING, conf);
    Assert.assertNotNull("failed to register subject: " + VALIDATING, sub);
    boolean threw = false;
    try {
      sub.register("stuff");
    } catch (SchemaValidationException e) {
      threw = true;
    }
    Assert.assertTrue("must throw a SchemaValidationException", threw);
  }

  private void validateSubject(Subject sub, Subject sub3) {
    Assert.assertEquals("subject names do not match", sub.getName(), sub3.getName());
    Assert.assertEquals("subject configurations do not match", sub.getConfig(), sub3.getConfig());
  }

  private void validateSchemaEntry(String expectedSchema, SchemaEntry foo) {
    Assert.assertNotNull("Failed to create SchemaEntry with schema: " + expectedSchema, foo);
    Assert.assertEquals("SchemaEntry does not have expected schema value", expectedSchema, foo.getSchema());
    Assert.assertNotNull("SchemaEntry does not have a valid id", foo.getId());
    Assert.assertFalse("SchemaEntry has an empty id", foo.getId().isEmpty());
  }
}