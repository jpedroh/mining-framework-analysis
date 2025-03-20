package com.shazam.shazamcrest.matcher;
import static com.shazam.shazamcrest.BeanFinder.findBeanAt;
import static com.shazam.shazamcrest.FieldsIgnorer.MARKER;
import static com.shazam.shazamcrest.CyclicReferenceDetector.getClassesWithCircularReferences;
import static com.shazam.shazamcrest.FieldsIgnorer.findPaths;
import static com.shazam.shazamcrest.matcher.GsonProvider.gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.shazam.shazamcrest.ComparisonDescription;

/**
 * Extends the functionalities of {@link DiagnosingMatcher} with the possibility to specify fields and object types to
 * ignore in the comparison, or fields to be matched with a custom matcher
 */
class DiagnosingCustomisableMatcher<T extends java.lang.Object> extends DiagnosingMatcher<T> implements CustomisableMatcher<T> {
  private final Set<String> pathsToIgnore = new HashSet<String>();

  private final Map<String, Matcher<?>> customMatchers = new HashMap<String, Matcher<?>>();

  protected final List<Class<?>> typesToIgnore = new ArrayList<Class<?>>();

  protected final Set<Class<?>> circularReferenceTypes = new HashSet<Class<?>>();

  protected final T expected;

  public DiagnosingCustomisableMatcher(T expected) {
    this.expected = expected;
    circularReferenceTypes.addAll(getClassesWithCircularReferences(expected));
  }

  @Override public void describeTo(Description description) {
    Gson gson = gson(typesToIgnore, circularReferenceTypes);
    description.appendText(filterJson(gson, expected));
    for (String fieldPath : customMatchers.keySet()) {
      description.appendText("\nand ").appendText(fieldPath).appendText(" ").appendDescriptionOf(customMatchers.get(fieldPath));
    }
  }

  @Override protected boolean matches(Object actual, Description mismatchDescription) {
    circularReferenceTypes.addAll(getClassesWithCircularReferences(actual));
    Gson gson = gson(typesToIgnore, circularReferenceTypes);
    if (!areCustomMatchersMatching(actual, mismatchDescription, gson)) {
      return false;
    }
    String expectedJson = filterJson(gson, expected);
    String actualJson = filterJson(gson, actual);
    return assertEquals(expectedJson, actualJson, mismatchDescription, gson);
  }

  private boolean areCustomMatchersMatching(Object actual, Description mismatchDescription, Gson gson) {
    Map<Object, Matcher<?>> customMatching = new HashMap<Object, Matcher<?>>();
    for (Entry<String, Matcher<?>> entry : customMatchers.entrySet()) {
      Object object = actual == null ? null : findBeanAt(entry.getKey(), actual);
      customMatching.put(object, customMatchers.get(entry.getKey()));
    }
    for (Entry<Object, Matcher<?>> entry : customMatching.entrySet()) {
      Matcher<?> matcher = entry.getValue();
      Object object = entry.getKey();
      if (!matcher.matches(object)) {
        appendFieldPath(matcher, mismatchDescription);
        matcher.describeMismatch(object, mismatchDescription);
        appendFieldJsonSnippet(object, mismatchDescription, gson);
        return false;
      }
    }
    return true;
  }

  private String removeSetMarker(String json) {
    return json.replaceAll(MARKER, "");
  }

  @Override public CustomisableMatcher<T> ignoring(String string) {
    pathsToIgnore.add(string);
    return this;
  }

  @Override public CustomisableMatcher<T> ignoring(Class<?> clazz) {
    typesToIgnore.add(clazz);
    return this;
  }

  @Override public <V extends java.lang.Object> CustomisableMatcher<T> with(String fieldPath, Matcher<V> matcher) {
    customMatchers.put(fieldPath, matcher);
    return this;
  }

  protected boolean appendMismatchDescription(Description mismatchDescription, String expectedJson, String actualJson, String message) {
    if (mismatchDescription instanceof ComparisonDescription) {
      ComparisonDescription shazamMismatchDescription = (ComparisonDescription) mismatchDescription;
      shazamMismatchDescription.setComparisonFailure(true);
      shazamMismatchDescription.setExpected(expectedJson);
      shazamMismatchDescription.setActual(actualJson);
      shazamMismatchDescription.setDifferencesMessage(message);
    }
    mismatchDescription.appendText(message);
    return false;
  }

  private boolean assertEquals(final String expectedJson, String actualJson, Description mismatchDescription, Gson gson) {
    try {
      JSONAssert.assertEquals(expectedJson, actualJson, true);
    } catch (AssertionError e) {
      return appendMismatchDescription(mismatchDescription, expectedJson, actualJson, e.getMessage());
    } catch (JSONException e) {
      return appendMismatchDescription(mismatchDescription, expectedJson, actualJson, e.getMessage());
    }
    return true;
  }

  private void appendFieldJsonSnippet(Object actual, Description mismatchDescription, Gson gson) {
    JsonElement jsonTree = gson.toJsonTree(actual);
    if (!jsonTree.isJsonPrimitive() && !jsonTree.isJsonNull()) {
      mismatchDescription.appendText("\n" + gson.toJson(actual));
    }
  }

  private void appendFieldPath(Matcher<?> matcher, Description mismatchDescription) {
    for (Entry<String, Matcher<?>> entry : customMatchers.entrySet()) {
      if (entry.getValue().equals(matcher)) {
        mismatchDescription.appendText(entry.getKey()).appendText(" ");
      }
    }
  }

  private String filterJson(Gson gson, Object object) {
    Set<String> set = new HashSet<String>();
    set.addAll(pathsToIgnore);
    set.addAll(customMatchers.keySet());
    JsonElement filteredJson = findPaths(gson, object, set);
    return removeSetMarker(gson.toJson(filteredJson));
  }
}