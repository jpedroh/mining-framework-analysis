/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2015 the original author or authors.
 */
package org.assertj.core.api;

import java.nio.file.Path;

/**
 * Entry point for assertion methods for different data types. Each method in this class is a static factory for the
 * type-specific assertion objects. The purpose of this class is to make test code more readable.
 * <p>
 * For example:
 * <pre><code class='java'> int removed = employees.removeFired();
 * {@link Assertions#assertThat(int) assertThat}(removed).{@link IntegerAssert#isZero isZero}();
 *
 * List&lt;Employee&gt; newEmployees = employees.hired(TODAY);
 * {@link Assertions#assertThat(Iterable) assertThat}(newEmployees).{@link IterableAssert#hasSize(int) hasSize}(6);</code></pre>
 * <p/>
 * </p>
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author David DIDIER
 * @author Ted Young
 * @author Joel Costigliola
 * @author Matthieu Baechler
 * @author Mikhail Mazursky
 * @author Nicolas François
 * @author Julien Meddah
 * @author William Delanoue
 * @author Turbo87
 * @author dorzey
 */
public class Assertions extends Java6Assertions {

  /**
   * Creates a new instance of {@link PathAssert}
   *
   * @param actual the path to test
   * @return the created assertion object
   */
  public static AbstractPathAssert<?> assertThat(Path actual) {
    return new PathAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link FloatAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link FloatAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link IntegerAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link IntegerAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link LongAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link LongAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link ShortAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Creates a new instance of <code>{@link ShortAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  /**
   * Allows to capture and then assert on a {@link Throwable} more easily when used with Java 8 lambdas.
   * 
   * <p>
   * Java 8 example :
   * <pre><code class='java'>  {@literal @}Test
   *  public void testException() {
   *    assertThatThrownBy(() -> { throw new Exception("boom!") }).isInstanceOf(Exception.class)
   *                                                              .hasMessageContaining("boom");
   *  }</code></pre>
   * 
   * <p>
   * Java 7 example :
   * <pre><code class='java'> assertThatThrownBy(new ThrowingCallable() {
   * 
   *   {@literal @}Override
   *   public void call() throws Exception {
   *     throw new Exception("boom!");
   *   }
   *   
   * }).isInstanceOf(Exception.class)
   *   .hasMessageContaining("boom");</code></pre>
   * 
   * If the provided {@link ThrowingCallable} does not raise an exception, an error is immediately raised, 
   * in that case the test description provided with {@link AbstractAssert#as(String, Object...) as(String, Object...)} is not honored. 
   * To use a test description, use {@link #catchThrowable(ThrowingCallable) catchThrowable} as shown below.  
   * <pre><code class='java'> // assertion will fail but "display me" won't appear in the error 
   * assertThatThrownBy(() -> { // do nothing }).as("display me").isInstanceOf(Exception.class);
   * 
   * // assertion will fail AND "display me" will appear in the error
   * Throwable thrown = catchThrowable(() -> { // do nothing });
   * assertThat(thrown).as("display me").isInstanceOf(Exception.class); </code></pre>
   *
   * @param shouldRaiseThrowable The {@link ThrowingCallable} or lambda with the code that should raise the throwable.
   * @return The captured exception or <code>null</code> if none was raised by the callable.
   */
  // -------------------------------------------------------------------------------------------------
  // fail methods : not assertions but here to have a single entry point to all AssertJ features.
  // -------------------------------------------------------------------------------------------------
  // ------------------------------------------------------------------------------------------------------
  // properties methods : not assertions but here to have a single entry point to all AssertJ features.
  // ------------------------------------------------------------------------------------------------------
  // ------------------------------------------------------------------------------------------------------
  // Data utility methods : not assertions but here to have a single entry point to all AssertJ features.
  // ------------------------------------------------------------------------------------------------------
  // ------------------------------------------------------------------------------------------------------
  // Condition methods : not assertions but here to have a single entry point to all AssertJ features.
  // ------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------
  // Filter methods : not assertions but here to have a single entry point to all AssertJ features.
  // --------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------
  // File methods : not assertions but here to have a single entry point to all AssertJ features.
  // --------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------
  // URL/Resource methods : not assertions but here to have a single entry point to all AssertJ features.
  // --------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------
  // Date formatting methods : not assertions but here to have a single entry point to all AssertJ features.
  // --------------------------------------------------------------------------------------------------

  /**
   * Creates a new </code>{@link Assertions}</code>.
   */
  protected Assertions() {}
}
