package org.assertj.core.api;

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import static org.assertj.core.util.Arrays.array;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.io.File;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.io.InputStream;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.math.BigDecimal;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.time.LocalDate;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.time.LocalDateTime;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.time.LocalTime;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.time.ZonedDateTime;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.util.Date;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.util.Iterator;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.util.List;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.util.Map;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.util.Optional;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import java.util.concurrent.Callable;

=======
>>>>>>> Unknown file: This is a bug in JDime.

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/dd0b3c1b29a139edec4e06194f2d1d0e7cfacaff/src/main/java/org/assertj/core/api/AbstractSoftAssertions.java/left.java
import net.sf.cglib.proxy.Enhancer;

=======
>>>>>>> Unknown file: This is a bug in JDime.

public class AbstractSoftAssertions {
  protected final SoftProxies proxies;

  public AbstractSoftAssertions() {
    super();
    proxies = new SoftProxies();
  }

  protected <T extends java.lang.Object, V extends java.lang.Object> V proxy(Class<V> assertClass, Class<T> actualClass, T actual) {
    return proxies.create(assertClass, actualClass, actual);
  }

  /**
   * Create assertion for {@link java.util.Optional}.
   *
   * @param actual the actual value.
   * @param <T> the type of the value contained in the {@link java.util.Optional}.
   *
   * @return the created assertion objet.
   */
  @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> OptionalAssert<T> assertThat(Optional<T> actual) {
    return proxy(OptionalAssert.class, Optional.class, actual);
  }

  /**
   * Creates a new instance of <code>{@link LocalDateAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public LocalDateAssert assertThat(LocalDate actual) {
    return proxy(LocalDateAssert.class, LocalDate.class, actual);
  }

  /**
   * Creates a new instance of <code>{@link LocalDateTimeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public LocalDateTimeAssert assertThat(LocalDateTime actual) {
    return proxy(LocalDateTimeAssert.class, LocalDateTime.class, actual);
  }

  /**
   * Creates a new instance of <code>{@link ZonedDateTimeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public ZonedDateTimeAssert assertThat(ZonedDateTime actual) {
    return proxy(ZonedDateTimeAssert.class, ZonedDateTime.class, actual);
  }

  /**
   * Creates a new instance of <code>{@link LocalTimeAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  public LocalTimeAssert assertThat(LocalTime actual) {
    return proxy(LocalTimeAssert.class, LocalTime.class, actual);
  }
}