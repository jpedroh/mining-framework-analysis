package com.ning.billing.recurly.model;
import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRecurlyObject extends TestModelBase {
  @Test(groups = "fast") public void testNull() {
    Assert.assertEquals(null, RecurlyObject.booleanOrNull(null));
    Assert.assertEquals(null, RecurlyObject.dateTimeOrNull(null));
    Assert.assertEquals(null, RecurlyObject.integerOrNull(null));
    Assert.assertEquals(null, RecurlyObject.stringOrNull(null));
    for (final String nil : RecurlyObject.NIL_VAL) {
      final HashMap<String, String> nilMap = new HashMap<String, String>();
      nilMap.put(RecurlyObject.NIL_STR, nil);
      Assert.assertEquals(null, RecurlyObject.booleanOrNull(nilMap));
      Assert.assertEquals(null, RecurlyObject.dateTimeOrNull(nilMap));
      Assert.assertEquals(null, RecurlyObject.integerOrNull(nilMap));
      Assert.assertEquals(null, RecurlyObject.stringOrNull(nilMap));
    }
    final HashMap<String, String> nonNilMap = new HashMap<String, String>();
    nonNilMap.put("foo", "bar");
    Assert.assertNotNull(RecurlyObject.isNull(nonNilMap));
  }
}