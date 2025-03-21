package org.skyscreamer.jsonassert.comparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.allJSONObjects;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.allSimpleValues;

/**
 * This class is the default json comparator implementation. <p/>
 * Comparison is performed according to  {@link JSONCompareMode} that is passed as constructor's argument.
 */
public class DefaultComparator extends AbstractComparator {
  JSONCompareMode mode;

  String wildcard;

  public DefaultComparator(JSONCompareMode mode) {
    this.mode = mode;
  }

  public DefaultComparator(JSONCompareMode mode, String wildcard) {
    this.mode = mode;
    this.wildcard = wildcard;
  }

  @Override public void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result) throws JSONException {
    checkJsonObjectKeysExpectedInActual(prefix, expected, actual, result);
    if (!mode.isExtensible()) {
      checkJsonObjectKeysActualInExpected(prefix, expected, actual, result);
    }
  }

  @Override public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {

<<<<<<< /usr/src/app/output/skyscreamer/jsonassert/3f737f33c689d3144367457ca37d32fb93b749d6/src/main/java/org/skyscreamer/jsonassert/comparator/DefaultComparator.java/left.java
    if (wildcard == null || !wildcard.equals(expectedValue)) {
      if (expectedValue instanceof Number && actualValue instanceof Number) {
        if (((Number) expectedValue).doubleValue() != ((Number) actualValue).doubleValue()) {
          result.fail(prefix, expectedValue, actualValue);
        }
      } else {
        if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
          if (expectedValue instanceof JSONArray) {
            compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);
          } else {
            if (expectedValue instanceof JSONObject) {
              compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
            } else {
              if (!expectedValue.equals(actualValue)) {
                result.fail(prefix, expectedValue, actualValue);
              }
            }
          }
        } else {
          result.fail(prefix, expectedValue, actualValue);
        }
      }
    }
=======
    if (areNumbers(expectedValue, actualValue)) {
      if (areNotSameDoubles(expectedValue, actualValue)) {
        result.fail(prefix, expectedValue, actualValue);
      }
    } else {
      if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
        if (expectedValue instanceof JSONArray) {
          compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);
        } else {
          if (expectedValue instanceof JSONObject) {
            compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
          } else {
            if (!expectedValue.equals(actualValue)) {
              result.fail(prefix, expectedValue, actualValue);
            }
          }
        }
      } else {
        result.fail(prefix, expectedValue, actualValue);
      }
    }
>>>>>>> /usr/src/app/output/skyscreamer/jsonassert/3f737f33c689d3144367457ca37d32fb93b749d6/src/main/java/org/skyscreamer/jsonassert/comparator/DefaultComparator.java/right.java
  }

  @Override public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
    if (expected.length() != actual.length()) {
      result.fail(prefix + "[]: Expected " + expected.length() + " values but got " + actual.length());
      return;
    } else {
      if (expected.length() == 0) {
        return;
      }
    }
    if (mode.hasStrictOrder()) {
      compareJSONArrayWithStrictOrder(prefix, expected, actual, result);
    } else {
      if (allSimpleValues(expected)) {
        compareJSONArrayOfSimpleValues(prefix, expected, actual, result);
      } else {
        if (allJSONObjects(expected)) {
          compareJSONArrayOfJsonObjects(prefix, expected, actual, result);
        } else {
          recursivelyCompareJSONArray(prefix, expected, actual, result);
        }
      }
    }
  }

  protected boolean areNumbers(Object expectedValue, Object actualValue) {
    return expectedValue instanceof Number && actualValue instanceof Number;
  }

  protected boolean areNotSameDoubles(Object expectedValue, Object actualValue) {
    return ((Number) expectedValue).doubleValue() != ((Number) actualValue).doubleValue();
  }
}