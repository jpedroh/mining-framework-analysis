package org.assertj.core.util;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.introspection.Introspection.getPropertyGetter;
import java.lang.reflect.Method;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.Before;
import org.junit.Test;

public class Introspection_getProperty_Test {
  private Employee judy;

  @Before public void initData() {
    judy = new Employee(100000.0, 31);
  }

  @Test public void get_descriptor_for_property() {
    Method getter = getPropertyGetter("age", judy);
    assertThat(getter).isNotNull();
  }

  @Test public void get_descriptor_for_property_from_interface_default_method() {
    PropertyDescriptor propertyDescriptor = getProperty("degree", judy);
    assertThat(propertyDescriptor).isNotNull();
    assertThat(propertyDescriptor.getName()).isEqualTo("degree");
    assertThat(propertyDescriptor.getPropertyType()).isEqualTo(String.class);
  }

  @Test public void should_raise_an_error_because_of_missing_getter() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
    assertThatThrownBy(() -> getProperty("salary", judy)).isInstanceOf(IntrospectionError.class).hasMessage("No getter for property \'salary\' in org.assertj.core.util.Employee");
=======
    try {
      getPropertyGetter("salary", judy);
      fail("IntrospectionError expected");
    } catch (IntrospectionError error) {
      assertThat(error).hasMessage("No getter for property \'salary\' in org.assertj.core.util.Employee");
    }
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/right.java
  }

  @Test public void should_raise_an_error_because_of_non_public_getter() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
    assertThatThrownBy(() -> getProperty("firstJob", judy)).isInstanceOf(IntrospectionError.class).hasMessage("No public getter for property \'firstJob\' in org.assertj.core.util.Employee");
=======
    try {
      getPropertyGetter("company", judy);
      fail("IntrospectionError expected");
    } catch (IntrospectionError error) {
      assertThat(error).hasMessage("No public getter for property \'company\' in org.assertj.core.util.Employee");
    }
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/right.java


<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
    assertThatThrownBy(() -> getProperty("company", judy)).isInstanceOf(IntrospectionError.class).hasMessage("No public getter for property \'company\' in org.assertj.core.util.Employee");
=======
    try {
      getPropertyGetter("firstJob", judy);
      fail("IntrospectionError expected");
    } catch (IntrospectionError error) {
      assertThat(error).hasMessage("No public getter for property \'firstJob\' in org.assertj.core.util.Employee");
    }
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/right.java
  }

  @Test public void should_raise_an_error_because_of_non_public_getter_when_getter_is_in_superclass() {

<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/left.java
    assertThatThrownBy(() -> getProperty("name", new Example())).isInstanceOf(IntrospectionError.class).hasMessage("No public getter for property \'name\' in org.assertj.core.util.Introspection_getProperty_Test$Example");
=======
    try {
      getPropertyGetter("name", new Example());
      fail("IntrospectionError expected");
    } catch (IntrospectionError error) {
      assertThat(error).hasMessage("No public getter for property \'name\' in org.assertj.core.util.Introspection_getProperty_Test$Example");
    }
>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/c85be7b3c81624c9ada99251b31ec677cbd338a6/src/test/java/org/assertj/core/util/Introspection_getProperty_Test.java/right.java
  }

  public static class Example extends Super {
  }

  public static class Super {
    @SuppressWarnings(value = { "unused" }) private String getName() {
      return "a";
    }
  }
}