package org.assertj.core.util;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.test.ExpectedException.none;
import static org.assertj.core.util.introspection.Introspection.getPropertyGetter;
import java.lang.reflect.Method;
import org.assertj.core.test.ExpectedException;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class Introspection_getProperty_Test {
  @Rule public ExpectedException thrown = none();

  private Employee judy;

  @Before public void initData() {
    judy = new Employee(100000.0, 31);
  }

  @Test public void get_getter_for_property() {
    Method getter = getPropertyGetter("age", judy);
    assertThat(getter).isNotNull();
  }

  @Test public void should_raise_an_error_because_of_missing_getter() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
    assertThatThrownBy(() -> getPropertyGetter("salary", judy)).isInstanceOf(IntrospectionError.class).hasMessage("No getter for property \'salary\' in org.assertj.core.util.Employee")
=======
    thrown.expect(IntrospectionError.class, "No getter for property \'salary\' in org.assertj.core.util.Employee")
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/right.java
    ;
    getPropertyGetter("salary", judy);
  }


<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
  @Test public void should_raise_an_error_because_of_non_public_getter() {
    assertThatThrownBy(() -> getPropertyGetter("firstJob", judy)).isInstanceOf(IntrospectionError.class).hasMessage("No public getter for property \'firstJob\' in org.assertj.core.util.Employee");
    assertThatThrownBy(() -> getPropertyGetter("company", judy)).isInstanceOf(IntrospectionError.class).hasMessage("No public getter for property \'company\' in org.assertj.core.util.Employee");
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Test public void should_raise_an_error_because_of_non_public_getter_when_getter_does_not_exists() {
    thrown.expect(IntrospectionError.class, "No public getter for property \'company\' in org.assertj.core.util.Employee");
    getPropertyGetter("company", judy);
  }

  @Test public void should_raise_an_error_because_of_non_public_getter_when_getter_is_package_private() {
    thrown.expect(IntrospectionError.class, "No public getter for property \'firstJob\' in org.assertj.core.util.Employee");
    getPropertyGetter("firstJob", judy);
  }

  @Test public void should_raise_an_error_because_of_non_public_getter_when_getter_is_in_superclass() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
    assertThatThrownBy(() -> getPropertyGetter("name", new Example())).isInstanceOf(IntrospectionError.class).hasMessage("No public getter for property \'name\' in org.assertj.core.util.Introspection_getProperty_Test$Example")
=======
    thrown.expect(IntrospectionError.class, "No public getter for property \'name\' in org.assertj.core.util.Introspection_getProperty_Test$Example")
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/327f0860049931d7af945af02e57dab608ae0aed/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/right.java
    ;
    getPropertyGetter("name", new Example());
  }

  public static class Example extends Super {
  }

  public static class Super {
    @SuppressWarnings(value = { "unused" }) private String getName() {
      return "a";
    }
  }
}