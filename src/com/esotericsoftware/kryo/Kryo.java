package com.esotericsoftware.kryo;
import static com.esotericsoftware.kryo.util.Util.*;
import static com.esotericsoftware.minlog.Log.*;
import com.esotericsoftware.kryo.SerializerFactory.FieldSerializerFactory;
import com.esotericsoftware.kryo.SerializerFactory.ReflectionSerializerFactory;
import com.esotericsoftware.kryo.SerializerFactory.SingletonSerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import com.esotericsoftware.kryo.serializers.ClosureSerializer.Closure;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.BooleanArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ByteArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.CharArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.DoubleArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.FloatArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.IntArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.LongArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ObjectArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ShortArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.StringArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ArraysAsListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BigDecimalSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BigIntegerSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BitSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BooleanSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ByteSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CalendarSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CharSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CharsetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ClassSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptySetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CurrencySerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.DateSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.DoubleSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.EnumSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.EnumSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.FloatSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.IntSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.KryoSerializableSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.LocaleSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.LongSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.PriorityQueueSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ShortSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringBufferSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringBuilderSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TimeZoneSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.URLSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.VoidSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.esotericsoftware.kryo.serializers.OptionalSerializers;
import com.esotericsoftware.kryo.serializers.TimeSerializers;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.GenericHandler;
import com.esotericsoftware.kryo.util.DefaultGenericHandler;
import com.esotericsoftware.kryo.util.DefaultGenericHandler.GenericType;
import com.esotericsoftware.kryo.util.DefaultGenericHandler.GenericsHierarchy;
import com.esotericsoftware.kryo.util.IdentityMap;
import com.esotericsoftware.kryo.util.IntArray;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.kryo.util.NoGenericsHandler;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.esotericsoftware.kryo.util.Pool.Poolable;
import com.esotericsoftware.kryo.util.Util;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Currency;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.SerializingInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class Kryo implements Poolable {
  static public final byte NULL = 0;

  static public final byte NOT_NULL = 1;

  static private final int REF = -1;

  static private final int NO_REF = -2;

  private SerializerFactory defaultSerializer = new FieldSerializerFactory();

  private final ArrayList<DefaultSerializerEntry> defaultSerializers = new ArrayList(53);

  private final int lowPriorityDefaultSerializerCount;

  private final ClassResolver classResolver;

  private int nextRegisterID;

  private ClassLoader classLoader = getClass().getClassLoader();

  private InstantiatorStrategy strategy = new DefaultInstantiatorStrategy();

  private boolean registrationRequired = true;

  private boolean warnUnregisteredClasses;

  private int depth, maxDepth = Integer.MAX_VALUE;

  private boolean autoReset = true;

  private volatile Thread thread;

  private ObjectMap context, graphContext;

  private ReferenceResolver referenceResolver;

  private final IntArray readReferenceIds = new IntArray(0);

  private boolean references, copyReferences = true;

  private Object readObject;

  private int copyDepth;

  private boolean copyShallow;

  private IdentityMap originalToCopy;

  private Object needsCopyReference;

  private GenericHandler generics = new DefaultGenericHandler(this);

  public Kryo() {
    this(new DefaultClassResolver(), null);
  }

  public Kryo(ReferenceResolver referenceResolver) {
    this(new DefaultClassResolver(), referenceResolver);
  }

  public Kryo(ClassResolver classResolver, ReferenceResolver referenceResolver) {
    if (classResolver == null) {
      throw new IllegalArgumentException("classResolver cannot be null.");
    }
    this.classResolver = classResolver;
    classResolver.setKryo(this);
    this.referenceResolver = referenceResolver;
    if (referenceResolver != null) {
      referenceResolver.setKryo(this);
      references = true;
    }
    addDefaultSerializer(byte[].class, ByteArraySerializer.class);
    addDefaultSerializer(char[].class, CharArraySerializer.class);
    addDefaultSerializer(short[].class, ShortArraySerializer.class);
    addDefaultSerializer(int[].class, IntArraySerializer.class);
    addDefaultSerializer(long[].class, LongArraySerializer.class);
    addDefaultSerializer(float[].class, FloatArraySerializer.class);
    addDefaultSerializer(double[].class, DoubleArraySerializer.class);
    addDefaultSerializer(boolean[].class, BooleanArraySerializer.class);
    addDefaultSerializer(String[].class, StringArraySerializer.class);
    addDefaultSerializer(Object[].class, ObjectArraySerializer.class);
    addDefaultSerializer(KryoSerializable.class, KryoSerializableSerializer.class);
    addDefaultSerializer(BigInteger.class, BigIntegerSerializer.class);
    addDefaultSerializer(BigDecimal.class, BigDecimalSerializer.class);
    addDefaultSerializer(Class.class, ClassSerializer.class);
    addDefaultSerializer(Date.class, DateSerializer.class);
    addDefaultSerializer(Enum.class, EnumSerializer.class);
    addDefaultSerializer(EnumSet.class, EnumSetSerializer.class);
    addDefaultSerializer(Currency.class, CurrencySerializer.class);
    addDefaultSerializer(StringBuffer.class, StringBufferSerializer.class);
    addDefaultSerializer(StringBuilder.class, StringBuilderSerializer.class);
    addDefaultSerializer(Collections.EMPTY_LIST.getClass(), CollectionsEmptyListSerializer.class);
    addDefaultSerializer(Collections.EMPTY_MAP.getClass(), CollectionsEmptyMapSerializer.class);
    addDefaultSerializer(Collections.EMPTY_SET.getClass(), CollectionsEmptySetSerializer.class);
    addDefaultSerializer(Collections.singletonList(null).getClass(), CollectionsSingletonListSerializer.class);
    addDefaultSerializer(Collections.singletonMap(null, null).getClass(), CollectionsSingletonMapSerializer.class);
    addDefaultSerializer(Collections.singleton(null).getClass(), CollectionsSingletonSetSerializer.class);
    addDefaultSerializer(TreeSet.class, TreeSetSerializer.class);
    addDefaultSerializer(Collection.class, CollectionSerializer.class);
    addDefaultSerializer(TreeMap.class, TreeMapSerializer.class);
    addDefaultSerializer(Map.class, MapSerializer.class);
    addDefaultSerializer(TimeZone.class, TimeZoneSerializer.class);
    addDefaultSerializer(Calendar.class, CalendarSerializer.class);
    addDefaultSerializer(Locale.class, LocaleSerializer.class);
    addDefaultSerializer(Charset.class, CharsetSerializer.class);
    addDefaultSerializer(URL.class, URLSerializer.class);
    addDefaultSerializer(Arrays.asList().getClass(), ArraysAsListSerializer.class);
    addDefaultSerializer(void.class, new VoidSerializer());
    addDefaultSerializer(PriorityQueue.class, new PriorityQueueSerializer());
    addDefaultSerializer(BitSet.class, new BitSetSerializer());
    OptionalSerializers.addDefaultSerializers(this);
    TimeSerializers.addDefaultSerializers(this);
    lowPriorityDefaultSerializerCount = defaultSerializers.size();
    register(int.class, new IntSerializer());
    register(String.class, new StringSerializer());
    register(float.class, new FloatSerializer());
    register(boolean.class, new BooleanSerializer());
    register(byte.class, new ByteSerializer());
    register(char.class, new CharSerializer());
    register(short.class, new ShortSerializer());
    register(long.class, new LongSerializer());
    register(double.class, new DoubleSerializer());
  }

  public void setDefaultSerializer(SerializerFactory serializer) {
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    defaultSerializer = serializer;
  }

  public void setDefaultSerializer(Class<? extends Serializer> serializer) {
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    defaultSerializer = new ReflectionSerializerFactory(serializer);
  }

  public void addDefaultSerializer(Class type, Serializer serializer) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    insertDefaultSerializer(type, new SingletonSerializerFactory(serializer));
  }

  public void addDefaultSerializer(Class type, SerializerFactory serializerFactory) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (serializerFactory == null) {
      throw new IllegalArgumentException("serializerFactory cannot be null.");
    }
    insertDefaultSerializer(type, serializerFactory);
  }

  public void addDefaultSerializer(Class type, Class<? extends Serializer> serializerClass) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (serializerClass == null) {
      throw new IllegalArgumentException("serializerClass cannot be null.");
    }
    insertDefaultSerializer(type, new ReflectionSerializerFactory(serializerClass));
  }

  private int insertDefaultSerializer(Class type, SerializerFactory factory) {
    int lowest = 0;
    for (int i = 0, n = defaultSerializers.size() - lowPriorityDefaultSerializerCount; i < n; i++) {
      if (type.isAssignableFrom(defaultSerializers.get(i).type)) {
        lowest = i + 1;
      }
    }
    defaultSerializers.add(lowest, new DefaultSerializerEntry(type, factory));
    return lowest;
  }

  public Serializer getDefaultSerializer(Class type) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    Serializer serializerForAnnotation = getDefaultSerializerForAnnotatedType(type);
    if (serializerForAnnotation != null) {
      return serializerForAnnotation;
    }
    for (int i = 0, n = defaultSerializers.size(); i < n; i++) {
      DefaultSerializerEntry entry = defaultSerializers.get(i);
      if (entry.type.isAssignableFrom(type) && entry.serializerFactory.isSupported(type)) {
        return entry.serializerFactory.newSerializer(this, type);
      }
    }
    return newDefaultSerializer(type);
  }

  protected Serializer getDefaultSerializerForAnnotatedType(Class type) {
    if (type.isAnnotationPresent(DefaultSerializer.class)) {
      DefaultSerializer annotation = (DefaultSerializer) type.getAnnotation(DefaultSerializer.class);
      return newFactory(annotation.serializerFactory(), annotation.value()).newSerializer(this, type);
    }
    return null;
  }

  protected Serializer newDefaultSerializer(Class type) {
    return defaultSerializer.newSerializer(this, type);
  }

  public Registration register(Class type) {
    Registration registration = classResolver.getRegistration(type);
    if (registration != null) {
      return registration;
    }
    return register(type, getDefaultSerializer(type));
  }

  public Registration register(Class type, int id) {
    Registration registration = classResolver.getRegistration(type);
    if (registration != null) {
      return registration;
    }
    return register(type, getDefaultSerializer(type), id);
  }

  public Registration register(Class type, Serializer serializer) {
    Registration registration = classResolver.getRegistration(type);
    if (registration != null) {
      registration.setSerializer(serializer);
      return registration;
    }
    return classResolver.register(new Registration(type, serializer, getNextRegistrationId()));
  }

  public Registration register(Class type, Serializer serializer, int id) {
    if (id < 0) {
      throw new IllegalArgumentException("id must be >= 0: " + id);
    }
    return register(new Registration(type, serializer, id));
  }

  public Registration register(Registration registration) {
    int id = registration.getId();
    if (id < 0) {
      throw new IllegalArgumentException("id must be > 0: " + id);
    }
    Registration existing = classResolver.unregister(id);
    if (DEBUG && existing != null && existing.getType() != registration.getType()) {
      debug("kryo", "Registration overwritten: " + existing + " -> " + registration);
    }
    return classResolver.register(registration);
  }

  public int getNextRegistrationId() {
    while (nextRegisterID != -2) {
      if (classResolver.getRegistration(nextRegisterID) == null) {
        return nextRegisterID;
      }
      nextRegisterID++;
    }
    throw new KryoException("No registration IDs are available.");
  }

  public Registration getRegistration(Class type) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    Registration registration = classResolver.getRegistration(type);
    if (registration == null) {
      if (Proxy.isProxyClass(type)) {
        registration = getRegistration(InvocationHandler.class);
      } else {
        if (!type.isEnum() && Enum.class.isAssignableFrom(type) && type != Enum.class) {
          while (true) {
            type = type.getSuperclass();
            if (type == null) {
              break;
            }
            if (type.isEnum()) {
              registration = classResolver.getRegistration(type);
              break;
            }
          }
        } else {
          if (EnumSet.class.isAssignableFrom(type)) {
            registration = classResolver.getRegistration(EnumSet.class);
          } else {
            if (isClosure(type)) {
              registration = classResolver.getRegistration(ClosureSerializer.Closure.class);
            }
          }
        }
      }
      if (registration == null) {
        if (registrationRequired) {
          throw new IllegalArgumentException(unregisteredClassMessage(type));
        }
        if (WARN && warnUnregisteredClasses) {
          warn(unregisteredClassMessage(type));
        }
        registration = classResolver.registerImplicit(type);
      }
    }
    return registration;
  }

  protected String unregisteredClassMessage(Class type) {
    return "Class is not registered: " + className(type) + "\nNote: To register this class use: kryo.register(" + className(type) + ".class);";
  }

  public Registration getRegistration(int classID) {
    return classResolver.getRegistration(classID);
  }

  public Serializer getSerializer(Class type) {
    return getRegistration(type).getSerializer();
  }

  public Registration writeClass(Output output, Class type) {
    if (output == null) {
      throw new IllegalArgumentException("output cannot be null.");
    }
    try {
      return classResolver.writeClass(output, type);
    }  finally {
      if (depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public void writeObject(Output output, Object object) {
    if (output == null) {
      throw new IllegalArgumentException("output cannot be null.");
    }
    if (object == null) {
      throw new IllegalArgumentException("object cannot be null.");
    }
    beginObject();
    try {
      if (references && writeReferenceOrNull(output, object, false)) {
        return;
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Write", object, output.position());
      }
      getRegistration(object.getClass()).getSerializer().write(this, output, object);
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public void writeObject(Output output, Object object, Serializer serializer) {
    if (output == null) {
      throw new IllegalArgumentException("output cannot be null.");
    }
    if (object == null) {
      throw new IllegalArgumentException("object cannot be null.");
    }
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    beginObject();
    try {
      if (references && writeReferenceOrNull(output, object, false)) {
        return;
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Write", object, output.position());
      }
      serializer.write(this, output, object);
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public void writeObjectOrNull(Output output, Object object, Class type) {
    if (output == null) {
      throw new IllegalArgumentException("output cannot be null.");
    }
    beginObject();
    try {
      Serializer serializer = getRegistration(type).getSerializer();
      if (references) {
        if (writeReferenceOrNull(output, object, true)) {
          return;
        }
      } else {
        if (!serializer.getAcceptsNull()) {
          if (object == null) {
            if (TRACE || (DEBUG && depth == 1)) {
              log("Write", object, output.position());
            }
            output.writeByte(NULL);
            return;
          }
          if (TRACE) {
            trace("kryo", "Write: <not null>" + pos(output.position()));
          }
          output.writeByte(NOT_NULL);
        }
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Write", object, output.position());
      }
      serializer.write(this, output, object);
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public void writeObjectOrNull(Output output, Object object, Serializer serializer) {
    if (output == null) {
      throw new IllegalArgumentException("output cannot be null.");
    }
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    beginObject();
    try {
      if (references) {
        if (writeReferenceOrNull(output, object, true)) {
          return;
        }
      } else {
        if (!serializer.getAcceptsNull()) {
          if (object == null) {
            if (TRACE || (DEBUG && depth == 1)) {
              log("Write", null, output.position());
            }
            output.writeByte(NULL);
            return;
          }
          if (TRACE) {
            trace("kryo", "Write: <not null>" + pos(output.position()));
          }
          output.writeByte(NOT_NULL);
        }
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Write", object, output.position());
      }
      serializer.write(this, output, object);
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public void writeClassAndObject(Output output, Object object) {
    if (output == null) {
      throw new IllegalArgumentException("output cannot be null.");
    }
    beginObject();
    try {
      if (object == null) {
        writeClass(output, null);
        return;
      }
      Registration registration = writeClass(output, object.getClass());
      if (references && writeReferenceOrNull(output, object, false)) {
        return;
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Write", object, output.position());
      }
      registration.getSerializer().write(this, output, object);
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  boolean writeReferenceOrNull(Output output, Object object, boolean mayBeNull) {
    if (object == null) {
      if (TRACE || (DEBUG && depth == 1)) {
        log("Write", null, output.position());
      }
      output.writeByte(NULL);
      return true;
    }
    if (!referenceResolver.useReferences(object.getClass())) {
      if (mayBeNull) {
        if (TRACE) {
          trace("kryo", "Write: <not null>" + pos(output.position()));
        }
        output.writeByte(NOT_NULL);
      }
      return false;
    }
    int id = referenceResolver.getWrittenId(object);
    if (id != -1) {
      if (DEBUG) {
        debug("kryo", "Write reference " + id + ": " + string(object) + pos(output.position()));
      }
      output.writeVarInt(id + 2, true);
      return true;
    }
    id = referenceResolver.addWrittenObject(object);
    if (TRACE) {
      trace("kryo", "Write: <not null>" + pos(output.position()));
    }
    output.writeByte(NOT_NULL);
    if (TRACE) {
      trace("kryo", "Write initial reference " + id + ": " + string(object) + pos(output.position()));
    }
    return false;
  }

  public Registration readClass(Input input) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null.");
    }
    try {
      return classResolver.readClass(input);
    }  finally {
      if (depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T readObject(Input input, Class<T> type) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null.");
    }
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    beginObject();
    try {
      T object;
      if (references) {
        int stackSize = readReferenceOrNull(input, type, false);
        if (stackSize == REF) {
          return (T) readObject;
        }
        object = (T) getRegistration(type).getSerializer().read(this, input, type);
        if (stackSize == readReferenceIds.size) {
          reference(object);
        }
      } else {
        object = (T) getRegistration(type).getSerializer().read(this, input, type);
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Read", object, input.position());
      }
      return object;
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T readObject(Input input, Class<T> type, Serializer serializer) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null.");
    }
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    beginObject();
    try {
      T object;
      if (references) {
        int stackSize = readReferenceOrNull(input, type, false);
        if (stackSize == REF) {
          return (T) readObject;
        }
        object = (T) serializer.read(this, input, type);
        if (stackSize == readReferenceIds.size) {
          reference(object);
        }
      } else {
        object = (T) serializer.read(this, input, type);
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Read", object, input.position());
      }
      return object;
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T readObjectOrNull(Input input, Class<T> type) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null.");
    }
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    beginObject();
    try {
      T object;
      if (references) {
        int stackSize = readReferenceOrNull(input, type, true);
        if (stackSize == REF) {
          return (T) readObject;
        }
        object = (T) getRegistration(type).getSerializer().read(this, input, type);
        if (stackSize == readReferenceIds.size) {
          reference(object);
        }
      } else {
        Serializer serializer = getRegistration(type).getSerializer();
        if (!serializer.getAcceptsNull() && input.readByte() == NULL) {
          if (TRACE || (DEBUG && depth == 1)) {
            log("Read", null, input.position());
          }
          return null;
        }
        object = (T) serializer.read(this, input, type);
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Read", object, input.position());
      }
      return object;
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T readObjectOrNull(Input input, Class<T> type, Serializer serializer) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null.");
    }
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (serializer == null) {
      throw new IllegalArgumentException("serializer cannot be null.");
    }
    beginObject();
    try {
      T object;
      if (references) {
        int stackSize = readReferenceOrNull(input, type, true);
        if (stackSize == REF) {
          return (T) readObject;
        }
        object = (T) serializer.read(this, input, type);
        if (stackSize == readReferenceIds.size) {
          reference(object);
        }
      } else {
        if (!serializer.getAcceptsNull() && input.readByte() == NULL) {
          if (TRACE || (DEBUG && depth == 1)) {
            log("Read", null, input.position());
          }
          return null;
        }
        object = (T) serializer.read(this, input, type);
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Read", object, input.position());
      }
      return object;
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  public Object readClassAndObject(Input input) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null.");
    }
    beginObject();
    try {
      Registration registration = readClass(input);
      if (registration == null) {
        return null;
      }
      Class type = registration.getType();
      Object object;
      if (references) {
        int stackSize = readReferenceOrNull(input, type, false);
        if (stackSize == REF) {
          return readObject;
        }
        object = registration.getSerializer().read(this, input, type);
        if (stackSize == readReferenceIds.size) {
          reference(object);
        }
      } else {
        object = registration.getSerializer().read(this, input, type);
      }
      if (TRACE || (DEBUG && depth == 1)) {
        log("Read", object, input.position());
      }
      return object;
    }  finally {
      if (--depth == 0 && autoReset) {
        reset();
      }
    }
  }

  int readReferenceOrNull(Input input, Class type, boolean mayBeNull) {
    if (type.isPrimitive()) {
      type = getWrapperClass(type);
    }
    boolean referencesSupported = referenceResolver.useReferences(type);
    int id;
    if (mayBeNull) {
      id = input.readVarInt(true);
      if (id == NULL) {
        if (TRACE || (DEBUG && depth == 1)) {
          log("Read", null, input.position());
        }
        readObject = null;
        return REF;
      }
      if (!referencesSupported) {
        readReferenceIds.add(NO_REF);
        return readReferenceIds.size;
      }
    } else {
      if (!referencesSupported) {
        readReferenceIds.add(NO_REF);
        return readReferenceIds.size;
      }
      id = input.readVarInt(true);
    }
    if (id == NOT_NULL) {
      if (TRACE) {
        trace("kryo", "Read: <not null>" + pos(input.position()));
      }
      id = referenceResolver.nextReadId(type);
      if (TRACE) {
        trace("kryo", "Read initial reference " + id + ": " + className(type) + pos(input.position()));
      }
      readReferenceIds.add(id);
      return readReferenceIds.size;
    }
    id -= 2;
    readObject = referenceResolver.getReadObject(type, id);
    if (DEBUG) {
      debug("kryo", "Read reference " + id + ": " + string(readObject) + pos(input.position()));
    }
    return REF;
  }

  public void reference(Object object) {
    if (copyDepth > 0) {
      if (needsCopyReference != null) {
        if (object == null) {
          throw new IllegalArgumentException("object cannot be null.");
        }
        originalToCopy.put(needsCopyReference, object);
        needsCopyReference = null;
      }
    } else {
      if (references && object != null) {
        int id = readReferenceIds.pop();
        if (id != NO_REF) {
          referenceResolver.setReadObject(id, object);
        }
      }
    }
  }

  public void reset() {
    depth = 0;
    if (graphContext != null) {
      graphContext.clear(2048);
    }
    classResolver.reset();
    if (references) {
      referenceResolver.reset();
      readObject = null;
    }
    copyDepth = 0;
    if (originalToCopy != null) {
      originalToCopy.clear(2048);
    }
    if (TRACE) {
      trace("kryo", "Object graph complete.");
    }
  }

  public <T extends java.lang.Object> T copy(T object) {
    if (object == null) {
      return null;
    }
    if (copyShallow) {
      return object;
    }
    copyDepth++;
    try {
      if (originalToCopy == null) {
        originalToCopy = new IdentityMap();
      }
      Object existingCopy = originalToCopy.get(object);
      if (existingCopy != null) {
        return (T) existingCopy;
      }
      if (copyReferences) {
        needsCopyReference = object;
      }
      Object copy;
      if (object instanceof KryoCopyable) {
        copy = ((KryoCopyable) object).copy(this);
      } else {
        copy = getSerializer(object.getClass()).copy(this, object);
      }
      if (needsCopyReference != null) {
        reference(copy);
      }
      if (TRACE || (DEBUG && copyDepth == 1)) {
        log("Copy", copy, -1);
      }
      return (T) copy;
    }  finally {
      if (--copyDepth == 0) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T copy(T object, Serializer serializer) {
    if (object == null) {
      return null;
    }
    if (copyShallow) {
      return object;
    }
    copyDepth++;
    try {
      if (originalToCopy == null) {
        originalToCopy = new IdentityMap();
      }
      Object existingCopy = originalToCopy.get(object);
      if (existingCopy != null) {
        return (T) existingCopy;
      }
      if (copyReferences) {
        needsCopyReference = object;
      }
      Object copy;
      if (object instanceof KryoCopyable) {
        copy = ((KryoCopyable) object).copy(this);
      } else {
        copy = serializer.copy(this, object);
      }
      if (needsCopyReference != null) {
        reference(copy);
      }
      if (TRACE || (DEBUG && copyDepth == 1)) {
        log("Copy", copy, -1);
      }
      return (T) copy;
    }  finally {
      if (--copyDepth == 0) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T copyShallow(T object) {
    if (object == null) {
      return null;
    }
    copyDepth++;
    copyShallow = true;
    try {
      if (originalToCopy == null) {
        originalToCopy = new IdentityMap();
      }
      Object existingCopy = originalToCopy.get(object);
      if (existingCopy != null) {
        return (T) existingCopy;
      }
      if (copyReferences) {
        needsCopyReference = object;
      }
      Object copy;
      if (object instanceof KryoCopyable) {
        copy = ((KryoCopyable) object).copy(this);
      } else {
        copy = getSerializer(object.getClass()).copy(this, object);
      }
      if (needsCopyReference != null) {
        reference(copy);
      }
      if (TRACE || (DEBUG && copyDepth == 1)) {
        log("Shallow copy", copy, -1);
      }
      return (T) copy;
    }  finally {
      copyShallow = false;
      if (--copyDepth == 0) {
        reset();
      }
    }
  }

  public <T extends java.lang.Object> T copyShallow(T object, Serializer serializer) {
    if (object == null) {
      return null;
    }
    copyDepth++;
    copyShallow = true;
    try {
      if (originalToCopy == null) {
        originalToCopy = new IdentityMap();
      }
      Object existingCopy = originalToCopy.get(object);
      if (existingCopy != null) {
        return (T) existingCopy;
      }
      if (copyReferences) {
        needsCopyReference = object;
      }
      Object copy;
      if (object instanceof KryoCopyable) {
        copy = ((KryoCopyable) object).copy(this);
      } else {
        copy = serializer.copy(this, object);
      }
      if (needsCopyReference != null) {
        reference(copy);
      }
      if (TRACE || (DEBUG && copyDepth == 1)) {
        log("Shallow copy", copy, -1);
      }
      return (T) copy;
    }  finally {
      copyShallow = false;
      if (--copyDepth == 0) {
        reset();
      }
    }
  }

  private void beginObject() {
    if (DEBUG) {
      if (depth == 0) {
        thread = Thread.currentThread();
      } else {
        if (thread != Thread.currentThread()) {
          throw new ConcurrentModificationException("Kryo must not be accessed concurrently by multiple threads.");
        }
      }
    }
    if (depth == maxDepth) {
      throw new KryoException("Max depth exceeded: " + depth);
    }
    depth++;
  }

  public ClassResolver getClassResolver() {
    return classResolver;
  }

  public ReferenceResolver getReferenceResolver() {
    return referenceResolver;
  }

  public void setClassLoader(ClassLoader classLoader) {
    if (classLoader == null) {
      throw new IllegalArgumentException("classLoader cannot be null.");
    }
    this.classLoader = classLoader;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public void setRegistrationRequired(boolean registrationRequired) {
    this.registrationRequired = registrationRequired;
    if (TRACE) {
      trace("kryo", "Registration required: " + registrationRequired);
    }
  }

  public boolean isRegistrationRequired() {
    return registrationRequired;
  }

  public void setWarnUnregisteredClasses(boolean warnUnregisteredClasses) {
    this.warnUnregisteredClasses = warnUnregisteredClasses;
    if (TRACE) {
      trace("kryo", "Warn unregistered classes: " + warnUnregisteredClasses);
    }
  }

  public boolean getWarnUnregisteredClasses() {
    return warnUnregisteredClasses;
  }

  public boolean setReferences(boolean references) {
    boolean old = this.references;
    if (references == old) {
      return references;
    }
    if (old) {
      referenceResolver.reset();
      readObject = null;
    }
    this.references = references;
    if (references && referenceResolver == null) {
      referenceResolver = new MapReferenceResolver();
    }
    if (TRACE) {
      trace("kryo", "References: " + references);
    }
    return !references;
  }

  public void setCopyReferences(boolean copyReferences) {
    this.copyReferences = copyReferences;
  }

  public void setReferenceResolver(ReferenceResolver referenceResolver) {
    if (referenceResolver == null) {
      throw new IllegalArgumentException("referenceResolver cannot be null.");
    }
    this.references = true;
    this.referenceResolver = referenceResolver;
    if (TRACE) {
      trace("kryo", "Reference resolver: " + referenceResolver.getClass().getName());
    }
  }

  public boolean getReferences() {
    return references;
  }

  public void setInstantiatorStrategy(InstantiatorStrategy strategy) {
    this.strategy = strategy;
  }

  public InstantiatorStrategy getInstantiatorStrategy() {
    return strategy;
  }

  protected ObjectInstantiator newInstantiator(Class type) {
    return strategy.newInstantiatorOf(type);
  }

  public <T extends java.lang.Object> T newInstance(Class<T> type) {
    Registration registration = getRegistration(type);
    ObjectInstantiator instantiator = registration.getInstantiator();
    if (instantiator == null) {
      instantiator = newInstantiator(type);
      registration.setInstantiator(instantiator);
    }
    return (T) instantiator.newInstance();
  }

  public ObjectMap getContext() {
    if (context == null) {
      context = new ObjectMap();
    }
    return context;
  }

  public ObjectMap getGraphContext() {
    if (graphContext == null) {
      graphContext = new ObjectMap();
    }
    return graphContext;
  }

  public int getDepth() {
    return depth;
  }

  public IdentityMap getOriginalToCopyMap() {
    return originalToCopy;
  }

  public void setAutoReset(boolean autoReset) {
    this.autoReset = autoReset;
  }

  public void setMaxDepth(int maxDepth) {
    if (maxDepth <= 0) {
      throw new IllegalArgumentException("maxDepth must be > 0.");
    }
    this.maxDepth = maxDepth;
  }

  public boolean isFinal(Class type) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    if (type.isArray()) {
      return Modifier.isFinal(Util.getElementClass(type).getModifiers());
    }
    return Modifier.isFinal(type.getModifiers());
  }

  public boolean isClosure(Class type) {
    if (type == null) {
      throw new IllegalArgumentException("type cannot be null.");
    }
    return type.getName().indexOf('/') >= 0;
  }

  public GenericHandler getGenerics() {
    return generics;
  }

  public void setGenerics(GenericHandler newHandler) {
    generics = newHandler;
  }

  static final class DefaultSerializerEntry {
    final Class type;

    final SerializerFactory serializerFactory;

    DefaultSerializerEntry(Class type, SerializerFactory serializerFactory) {
      this.type = type;
      this.serializerFactory = serializerFactory;
    }
  }
}