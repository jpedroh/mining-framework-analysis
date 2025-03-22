package org.fluentlenium.core.wait;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.fluentlenium.core.Fluent;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.search.Search;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FluentWait implements org.openqa.selenium.support.ui.Wait<Fluent> {
  private final org.openqa.selenium.support.ui.FluentWait<Fluent> wait;

  private final Search search;

  private final WebDriver driver;

  private boolean useDefaultException;

  private boolean useCustomMessage;

  public org.openqa.selenium.support.ui.FluentWait getWait() {
    return wait;
  }

  public FluentWait(Fluent fluent, Search search) {
    wait = new org.openqa.selenium.support.ui.FluentWait<Fluent>(fluent);
    this.search = search;
    driver = fluent.getDriver();
    useDefaultException = true;
  }

  public FluentWait atMost(long duration, java.util.concurrent.TimeUnit unit) {
    wait.withTimeout(duration, unit);
    return this;
  }

  /**
     * @param timeInMillis time In Millis
     * @return fluent wait
     */
  public FluentWait atMost(long timeInMillis) {
    wait.withTimeout(timeInMillis, TimeUnit.MILLISECONDS);
    return this;
  }

  public FluentWait pollingEvery(long duration, java.util.concurrent.TimeUnit unit) {
    wait.pollingEvery(duration, unit);
    return this;
  }

  public FluentWait ignoreAll(java.util.Collection<java.lang.Class<? extends Throwable>> types) {
    wait.ignoreAll(types);
    return this;
  }

  public FluentWait ignoring(java.lang.Class<? extends java.lang.RuntimeException> exceptionType) {
    wait.ignoring(exceptionType);
    return this;
  }

  /**
     * Ignoring the two exceptions passed as params
     *
     * @param firstType  first type of exception which extends java.lang.RuntimeException
     * @param secondType second type of exception which extends java.lang.RuntimeException
     * @return
     */
  public FluentWait ignoring(java.lang.Class<? extends java.lang.RuntimeException> firstType, java.lang.Class<? extends java.lang.RuntimeException> secondType) {
    wait.ignoring(firstType, secondType);
    return this;
  }

  /**
     * @param isTrue predicate condition for wait
     * @return fluent wait
     * @deprecated This method is java8 lambda ambigous with {@link #until(Function)} and will be removed.
     */
  @Deprecated public void until(Predicate<Fluent> isTrue) {
    untilPredicate(isTrue);
  }

  /**
     * @param isTrue predicate condition for wait
     * @return fluent wait
     */
  public void untilPredicate(Predicate<Fluent> predicate) {
    updateWaitWithDefaultExceptions();
    wait.until(predicate);
  }

  /**
     * @param message - the failing message
     * @return fluent wait
     */
  public FluentWait withMessage(String message) {
    wait.withMessage(message);
    useCustomMessage = true;
    return this;
  }

  /**
     * Use this methods only to avoid ignoring StateElementReferenceException
     *
     * @return fluent wait
     */
  public FluentWait withNoDefaultsException() {
    useDefaultException = false;
    return this;
  }

  /**
     * @param string - CSS selector
     * @return fluent wait matcher
     */
  public FluentWaitSelectorMatcher until(String selector) {
    updateWaitWithDefaultExceptions();
    return new FluentWaitSelectorMatcher(search, this, selector);
  }

  @SuppressWarnings(value = { "unchecked" }) public 
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/left.java
  FluentWaitMatcher
=======
  FluentWaitElementMatcher
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/right.java
   until(
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/left.java
  By locator
=======
  FluentWebElement element
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/right.java
  ) {
    updateWaitWithDefaultExceptions();
    return new 
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/left.java
    FluentWaitMatcher
=======
    FluentWaitElementMatcher
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/right.java
    (search, this, 
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/left.java
    locator
=======
    element
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentWait.java/right.java
    );
  }

  @SuppressWarnings(value = { "unchecked" }) public FluentWaitWindowMatcher untilWindow(String windowName) {
    return new FluentWaitWindowMatcher(this, windowName);
  }

  /**
     * Return a matcher configured to wait for particular condition for given elements.
     *
     * @param elements Elements to wait for.
     * @return fluent wait matcher
     */
  public FluentWaitElementListMatcher until(List<? extends FluentWebElement> elements) {
    updateWaitWithDefaultExceptions();
    return new FluentWaitElementListMatcher(search, this, elements);
  }

  /**
     * Return a matcher configured to wait for particular condition for elements matching then given functional supplier.
     *
     * @param selector Supplier of the element to wait for.
     * @return fluent wait matcher
     */
  public FluentWaitSupplierMatcher untilElement(Supplier<? extends FluentWebElement> selector) {
    updateWaitWithDefaultExceptions();
    return new FluentWaitSupplierMatcher(search, this, selector);
  }

  /**
     * Return a matcher configured to wait for particular condition for elements matching then given functional supplier.
     *
     * @param selector Supplier of the element to wait for.
     * @return fluent wait matcher
     */
  public FluentWaitSupplierListMatcher untilElements(Supplier<? extends FluentList<? extends FluentWebElement>> selector) {
    updateWaitWithDefaultExceptions();
    return new FluentWaitSupplierListMatcher(search, this, selector);
  }

  /**
     * @return fluent wait page matcher
     */
  public FluentWaitPageMatcher untilPage() {
    updateWaitWithDefaultExceptions();
    return new FluentWaitPageMatcher(this, driver);
  }

  /**
     * @param page - the page to work with
     * @return fluent wait page matcher
     */
  public FluentWaitPageMatcher untilPage(FluentPage page) {
    updateWaitWithDefaultExceptions();
    return new FluentWaitPageMatcher(this, driver, page);
  }

  /**
     * Waits unconditionally for explicit amount of time. The method should be used only as a last resort. In most
     * cases you should wait for some condition, e.g. visibility of particular element on the page.
     *
     * @param amount   amount of time
     * @param timeUnit unit of time
     * @return {@code this} to allow chaining method invocations
     */
  public FluentWait explicitlyFor(long amount, TimeUnit timeUnit) {
    try {
      timeUnit.sleep(amount);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public void until(final Supplier<Boolean> isTrue) {
    updateWaitWithDefaultExceptions();
    wait.until(new Function<Object, Boolean>() {
      public Boolean apply(Object input) {
        return isTrue.get();
      }

      public String toString() {
        return isTrue.toString();
      }
    });
  }

  @Override public <T extends java.lang.Object> T until(Function<? super Fluent, T> isTrue) {
    updateWaitWithDefaultExceptions();
    return wait.until(isTrue);
  }

  private void updateWaitWithDefaultExceptions() {
    if (useDefaultException) {
      wait.ignoring(StaleElementReferenceException.class);
    }
  }

  public boolean useCustomMessage() {
    return useCustomMessage;
  }
}