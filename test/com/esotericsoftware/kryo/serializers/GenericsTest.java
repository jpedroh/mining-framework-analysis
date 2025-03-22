package com.esotericsoftware.kryo.serializers;
import java.io.Serializable;
import com.esotericsoftware.kryo.KryoTestCase;
import java.util.ArrayList;
import com.esotericsoftware.kryo.io.Input;
import java.util.Arrays;
import com.esotericsoftware.kryo.io.Output;
import java.util.HashMap;
import com.esotericsoftware.kryo.serializers.GenericsTest.A.DontPassToSuper;
import java.util.HashSet;
import com.esotericsoftware.kryo.serializers.GenericsTest.ClassWithMap.MapKey;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class GenericsTest extends KryoTestCase {
  {
    supportsCopy = true;
  }

  @Override @Before public void setUp() throws Exception {
    super.setUp();
  }

  @Test public void testGenericClassWithGenericFields() {
    kryo.setReferences(true);
    kryo.setRegistrationRequired(false);
    kryo.register(BaseGeneric.class);
    List list = Arrays.asList(new SerializableObjectFoo("one"), new SerializableObjectFoo("two"), new SerializableObjectFoo("three"));
    BaseGeneric<SerializableObjectFoo> bg1 = new BaseGeneric(list);
    roundTrip(117, bg1);
  }

  @Test public void testNonGenericClassWithGenericSuperclass() {
    kryo.setReferences(true);
    kryo.setRegistrationRequired(false);
    kryo.register(BaseGeneric.class);
    kryo.register(ConcreteClass.class);
    List list = Arrays.asList(new SerializableObjectFoo("one"), new SerializableObjectFoo("two"), new SerializableObjectFoo("three"));
    ConcreteClass cc1 = new ConcreteClass(list);
    roundTrip(117, cc1);
  }

  @Test public void testDifferentTypeArguments() {
    LongHolder o1 = new LongHolder(1L);
    LongListHolder o2 = new LongListHolder(Arrays.asList(1L));
    kryo.setRegistrationRequired(false);
    Output buffer = new Output(512, 4048);
    kryo.writeClassAndObject(buffer, o1);
    kryo.writeClassAndObject(buffer, o2);
  }

  @Test public void testSuperGenerics() {
    kryo.register(SuperGenerics.Root.class);
    kryo.register(SuperGenerics.Value.class);
    Output output = new Output(2048, -1);
    SuperGenerics.Root root = new SuperGenerics.Root();
    root.rootSuperField = new SuperGenerics.Value();
    kryo.writeObject(output, root);
    output.flush();
    Input input = new Input(output.getBuffer(), 0, output.position());
    kryo.readObject(input, SuperGenerics.Root.class);
  }

  @Test public void testMapTypeParams() {
    ClassWithMap hasMap = new ClassWithMap();
    MapKey key = new MapKey();
    key.field1 = "foo";
    key.field2 = "bar";
    HashSet set = new HashSet();
    set.add("one");
    set.add("two");
    hasMap.values.put(key, set);
    kryo.register(ClassWithMap.class);
    kryo.register(MapKey.class);
    kryo.register(HashMap.class);
    kryo.register(HashSet.class);
    roundTrip(20, 18, hasMap);
  }

  @Test public void testNotPassingToSuper() {
    kryo.register(DontPassToSuper.class);
    kryo.copy(new DontPassToSuper());
  }

  @Test public void testFieldWithGenericInterface() {
    ClassWithGenericInterfaceField.A o = new ClassWithGenericInterfaceField.A();
    kryo.setRegistrationRequired(false);
    roundTrip(170, o);
  }

  @Test public void testFieldWithGenericArrayType() {
    ClassArrayHolder o = new ClassArrayHolder(new Class[] {  });
    kryo.setRegistrationRequired(false);
    roundTrip(70, o);
  }

  @Test public void testClassWithMultipleGenericTypes() {
    HolderWithAdditionalGenericType<String, Integer> o = new HolderWithAdditionalGenericType<>(1);
    kryo.setRegistrationRequired(false);
    roundTrip(87, o);
  }

  @Test public void testClassHierarchyWithChangingGenericTypeVariables() {
    ClassHierarchyWithChangingTypeVariableNames.A<?> o = new ClassHierarchyWithChangingTypeVariableNames.A<>(Enum.class);
    kryo.setRegistrationRequired(false);
    roundTrip(131, o, false);
  }

  @Test public void testClassHierarchyWithMultipleTypeVariables() {
    ClassHierarchyWithMultipleTypeVariables.A<Integer, ?> o = new ClassHierarchyWithMultipleTypeVariables.A<>(Enum.class);
    kryo.setRegistrationRequired(false);
    roundTrip(110, o);
  }

  @Test public void testClassHierarchyWithConflictingTypeVariables() {
    ClassWithConflictingTypeArguments.A o = new ClassWithConflictingTypeArguments.A(new ClassWithConflictingTypeArguments.B<>(1));
    try {
      kryo.setOptimizedGenerics(false);
      kryo.setRegistrationRequired(false);
      Output buffer = new Output(512, 4048);
      kryo.writeClassAndObject(buffer, o);
    }  finally {
      kryo.setOptimizedGenerics(true);
    }
  }

  private interface Holder<V extends java.lang.Object> {
    V getValue();
  }

  static private abstract class AbstractValueHolder<V extends java.lang.Object> implements Holder<V> {
    private final V value;

    AbstractValueHolder(V value) {
      this.value = value;
    }

    @Override public V getValue() {
      return value;
    }

    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final AbstractValueHolder<?> that = (AbstractValueHolder<?>) o;
      return Objects.deepEquals(value, that.value);
    }
  }

  static private abstract class AbstractValueListHolder<V extends java.lang.Object> extends AbstractValueHolder<List<V>> {
    AbstractValueListHolder(List<V> value) {
      super(value);
    }
  }

  static private class LongHolder extends AbstractValueHolder<Long> {
    LongHolder(Long value) {
      super(value);
    }
  }

  static private class LongListHolder extends AbstractValueListHolder<Long> {
    LongListHolder(java.util.List<Long> value) {
      super(value);
    }
  }

  static class ClassArrayHolder extends AbstractValueHolder<Class<?>[]> {
    /** Kryo Constructor */
    ClassArrayHolder() {
      super(null);
    }

    ClassArrayHolder(Class<?>[] value) {
      super(value);
    }
  }

  static class HolderWithAdditionalGenericType<BT extends java.lang.Object, OT extends java.lang.Object> extends AbstractValueHolder<OT> {
    private BT value;

    /** Kryo Constructor */
    HolderWithAdditionalGenericType() {
      super(null);
    }

    HolderWithAdditionalGenericType(OT value) {
      super(value);
    }

    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      if (!super.equals(o)) {
        return false;
      }
      final HolderWithAdditionalGenericType<?, ?> that = (HolderWithAdditionalGenericType<?, ?>) o;
      return Objects.equals(value, that.value);
    }
  }

  static private class SerializableObjectFoo implements Serializable {
    String name;

    SerializableObjectFoo(String name) {
      this.name = name;
    }

    public SerializableObjectFoo() {
      name = "Default";
    }

    @Override public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      SerializableObjectFoo other = (SerializableObjectFoo) obj;
      if (name == null) {
        if (other.name != null) {
          return false;
        }
      } else {
        if (!name.equals(other.name)) {
          return false;
        }
      }
      return true;
    }
  }

  static private class BaseGeneric<T extends Serializable> {
    private final List<T> listPayload;

    /** Kryo Constructor */
    protected BaseGeneric() {
      super();
      this.listPayload = null;
    }

    protected BaseGeneric(final List<T> listPayload) {
      super();
      this.listPayload = new ArrayList(listPayload);
    }

    public final List<T> getPayload() {
      return this.listPayload;
    }

    @Override public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      BaseGeneric other = (BaseGeneric) obj;
      if (listPayload == null) {
        if (other.listPayload != null) {
          return false;
        }
      } else {
        if (!listPayload.equals(other.listPayload)) {
          return false;
        }
      }
      return true;
    }
  }

  static private class ConcreteClass2 extends BaseGeneric<SerializableObjectFoo> {
    /** Kryo Constructor */
    ConcreteClass2() {
      super();
    }

    public ConcreteClass2(final List listPayload) {
      super(listPayload);
    }
  }

  static private class ConcreteClass1 extends ConcreteClass2 {
    /** Kryo Constructor */
    ConcreteClass1() {
      super();
    }

    public ConcreteClass1(final List listPayload) {
      super(listPayload);
    }
  }

  static private class ConcreteClass extends ConcreteClass1 {
    /** Kryo Constructor */
    ConcreteClass() {
      super();
    }

    public ConcreteClass(final List listPayload) {
      super(listPayload);
    }
  }

  static public class SuperGenerics {
    static public class RootSuper<RS extends java.lang.Object> {
      public ValueSuper<RS> rootSuperField;
    }

    static public class Root extends RootSuper<String> {
    }

    static public class ValueSuper<VS extends java.lang.Object> extends ValueSuperSuper<Integer> {
      VS superField;
    }

    static public class ValueSuperSuper<VSS extends java.lang.Object> {
      VSS superSuperField;
    }

    static public class Value extends ValueSuper<String> {
    }
  }

  static public class ClassWithMap {
    public final Map<MapKey, Set<String>> values = new HashMap();

    @Override public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ClassWithMap other = (ClassWithMap) obj;
      if (values == null) {
        if (other.values != null) {
          return false;
        }
      } else {
        if (!values.toString().equals(other.values.toString())) {
          return false;
        }
      }
      return true;
    }

    static public class MapKey {
      public String field1, field2;

      @Override public String toString() {
        return field1 + ":" + field2;
      }
    }
  }

  static public class A<X extends java.lang.Object> {
    static public class B<Y extends java.lang.Object> extends A {
    }

    static public class DontPassToSuper<Z extends java.lang.Object> extends B {
      B<Z> b;
    }
  }

  static class ClassWithGenericInterfaceField {
    static class A extends B<String> {
      A() {
        super(new C());
      }
    }

    static class B<T extends java.lang.Object> {
      Supplier<T> s;

      B(Supplier<T> s) {
        this.s = s;
      }

      @Override public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }
        final B<?> b = (B<?>) o;
        return Objects.equals(s.get(), b.s.get());
      }

      @Override public int hashCode() {
        return Objects.hash(s);
      }
    }

    static class C implements Supplier<String>, Serializable {
      @Override public String get() {
        return null;
      }
    }
  }

  static class ClassHierarchyWithChangingTypeVariableNames {
    static final class A<T extends java.lang.Object> extends B<T> {
      T d;

      /** Kryo Constructor */
      A() {
      }

      A(T d) {
        this.d = d;
      }

      @Override public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }
        final A<?> a = (A<?>) o;
        return Objects.equals(d, a.d);
      }
    }

    static class B<E extends java.lang.Object> extends C<E> {
    }

    static class C<E extends java.lang.Object> {
    }
  }

  static class ClassHierarchyWithMultipleTypeVariables {
    static class A<T extends java.lang.Object, S extends java.lang.Object> extends B<T> {
      Class<S> s;

      /** Kryo Constructor */
      A() {
      }

      A(Class<S> s) {
        this.s = s;
      }

      @Override public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }
        final A<?, ?> a = (A<?, ?>) o;
        return Objects.equals(s, a.s);
      }
    }

    static class B<T extends java.lang.Object> extends C<T> {
    }

    public static class C<T extends java.lang.Object> {
    }
  }

  static class ClassWithConflictingTypeArguments {
    static final class A {
      C<String> c;

      public A(C<String> c) {
        this.c = c;
      }
    }

    static class B<R extends java.lang.Object, V extends java.lang.Object> implements C<V> {
      R r;

      public B(R r) {
        this.r = r;
      }
    }

    interface C<T extends java.lang.Object> {
    }
  }
}