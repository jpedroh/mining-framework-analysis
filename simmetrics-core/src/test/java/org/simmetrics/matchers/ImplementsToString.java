package org.simmetrics.matchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

@SuppressWarnings(value = { "javadoc" }) public class ImplementsToString<T extends java.lang.Object> extends TypeSafeDiagnosingMatcher<T> {
  @Override public void describeTo(Description description) {
    description.appendText("implements toString");
  }

  @Override protected boolean matchesSafely(T item, Description mismatchDescription) {
    mismatchDescription.appendText("was ");
    mismatchDescription.appendValue(item.getClass().getSimpleName());
    mismatchDescription.appendText(".toString()=");
    mismatchDescription.appendValue(item.toString());
    String defaultToString = item.getClass().getName() + "@" + Integer.toHexString(item.hashCode());
    return !defaultToString.equals(item.toString());
  }

  public static <T extends java.lang.Object> Matcher<T> implementsToString() {
    return new ImplementsToString<>();
  }
}