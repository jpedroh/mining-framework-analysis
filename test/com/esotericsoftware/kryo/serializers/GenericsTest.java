package com.esotericsoftware.kryo.serializers;
import static org.junit.Assert.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoTestCase;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.GenericsTest.A.DontPassToSuper;
import com.esotericsoftware.kryo.serializers.GenericsTest.ClassWithMap.MapKey;
import com.esotericsoftware.kryo.util.NoGenericsHandler;
import java.io.Serializable;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import java.lang.invoke.SerializedLambda;
import java.util.*;
import org.junit.Ignore;

public class GenericsTest extends KryoTestCase {
  {
    supportsCopy = true;
  }

  @Before public void setUp() throws Exception {
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
    roundTrip(18, hasMap);
  }

  @Test public void testNotPassingToSuper() {
    kryo.register(DontPassToSuper.class);
    kryo.copy(new DontPassToSuper());
  }

  @Test public void testComplicatedGenerics() {
    final Kryo kryo = new Kryo();
    kryo.setRegistrationRequired(false);
    kryo.setGenerics(NoGenericsHandler.INSTANCE);
    final Output output = new Output(1024);
    kryo.writeClassAndObject(output, new StringSupplierContainer());
    Object result = kryo.readClassAndObject(new Input(output.getBuffer()));
    assertTrue(result instanceof StringSupplierContainer);
    assertTrue(((StringSupplierContainer) result).input instanceof EmptyStringSupplier);
  }

  static class EmptyStringSupplier implements Supplier<String>, Serializable {
    public String get() {
      return "";
    }
  }

  static class StringSupplierContainer extends SupplierContainer<String> {
    StringSupplierContainer() {
      super(new EmptyStringSupplier());
    }
  }

  static class SupplierContainer<T extends java.lang.Object> {
    public final Supplier<T> input;

    SupplierContainer(Supplier<T> input) {
      this.input = input;
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

    public V getValue() {
      return value;
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

  static private class SerializableObjectFoo implements Serializable {
    String name;

    SerializableObjectFoo(String name) {
      this.name = name;
    }

    public SerializableObjectFoo() {
      name = "Default";
    }

    public boolean equals(Object obj) {
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

    public boolean equals(Object obj) {
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
    ConcreteClass2() {
      super();
    }

    public ConcreteClass2(final List listPayload) {
      super(listPayload);
    }
  }

  static private class ConcreteClass1 extends ConcreteClass2 {
    ConcreteClass1() {
      super();
    }

    public ConcreteClass1(final List listPayload) {
      super(listPayload);
    }
  }

  static private class ConcreteClass extends ConcreteClass1 {
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

    public boolean equals(Object obj) {
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

      public String toString() {
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

  @Test public void testFieldWithGenericInterface() {
    ClassWithGenericInterfaceField o = new ClassWithGenericInterfaceField();
    kryo.setRegistrationRequired(false);
    kryo.register(SerializedLambda.class);
    kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
    Output buffer = new Output(512, 4048);
    kryo.writeClassAndObject(buffer, o);
    buffer.flush();
    Input input = new Input(buffer.getBuffer(), 0, buffer.position());
    kryo.readObject(input, ClassWithGenericInterfaceField.class);
  }

  @Test public void testFieldWithGenericArrayType() {
    final ClassArrayHolder o = new ClassArrayHolder(new Class[] {  });
    kryo.setRegistrationRequired(false);
    Output buffer = new Output(512, 4048);
    kryo.writeClassAndObject(buffer, o);
  }

  @Test public void testClassWithMultipleGenericTypes() {
    final HolderWithAdditionalGenericType<String, Integer> o = new HolderWithAdditionalGenericType<>(1);
    kryo.setRegistrationRequired(false);
    Output buffer = new Output(512, 4048);
    kryo.writeClassAndObject(buffer, o);
  }

  static class ClassArrayHolder extends AbstractValueHolder<Class<?>[]> {
    public ClassArrayHolder(Class<?>[] value) {
      super(value);
    }
  }

  static class HolderWithAdditionalGenericType<BT extends java.lang.Object, OT extends java.lang.Object> extends AbstractValueHolder<OT> {
    private BT value;

    HolderWithAdditionalGenericType(OT value) {
      super(value);
    }
  }

  static class ClassWithGenericInterfaceField {
    private final Holder<?> input = (Holder<?> & Serializable) () -> null;
  }
}