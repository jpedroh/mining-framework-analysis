package com.esotericsoftware.kryo.serializers;
import static com.esotericsoftware.kryo.Kryo.*;
import static com.esotericsoftware.kryo.util.Util.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

/** Contains many serializer classes that are provided by {@link Kryo#addDefaultSerializer(Class, Class) default}.
 * @author Nathan Sweet */
public class DefaultSerializers {
  public static class VoidSerializer extends ImmutableSerializer {
    @Override public void write(Kryo kryo, Output output, Object object) {
    }

    @Override public Object read(Kryo kryo, Input input, Class type) {
      return null;
    }
  }

  public static class BooleanSerializer extends ImmutableSerializer<Boolean> {
    @Override public void write(Kryo kryo, Output output, Boolean object) {
      output.writeBoolean(object);
    }

    @Override public Boolean read(Kryo kryo, Input input, Class<? extends Boolean> type) {
      return input.readBoolean();
    }
  }

  public static class ByteSerializer extends ImmutableSerializer<Byte> {
    @Override public void write(Kryo kryo, Output output, Byte object) {
      output.writeByte(object);
    }

    @Override public Byte read(Kryo kryo, Input input, Class<? extends Byte> type) {
      return input.readByte();
    }
  }

  public static class CharSerializer extends ImmutableSerializer<Character> {
    @Override public void write(Kryo kryo, Output output, Character object) {
      output.writeChar(object);
    }

    @Override public Character read(Kryo kryo, Input input, Class<? extends Character> type) {
      return input.readChar();
    }
  }

  public static class ShortSerializer extends ImmutableSerializer<Short> {
    @Override public void write(Kryo kryo, Output output, Short object) {
      output.writeShort(object);
    }

    @Override public Short read(Kryo kryo, Input input, Class<? extends Short> type) {
      return input.readShort();
    }
  }

  public static class IntSerializer extends ImmutableSerializer<Integer> {
    @Override public void write(Kryo kryo, Output output, Integer object) {
      output.writeInt(object, false);
    }

    @Override public Integer read(Kryo kryo, Input input, Class<? extends Integer> type) {
      return input.readInt(false);
    }
  }

  public static class LongSerializer extends ImmutableSerializer<Long> {
    @Override public void write(Kryo kryo, Output output, Long object) {
      output.writeVarLong(object, false);
    }

    @Override public Long read(Kryo kryo, Input input, Class<? extends Long> type) {
      return input.readVarLong(false);
    }
  }

  public static class FloatSerializer extends ImmutableSerializer<Float> {
    @Override public void write(Kryo kryo, Output output, Float object) {
      output.writeFloat(object);
    }

    @Override public Float read(Kryo kryo, Input input, Class<? extends Float> type) {
      return input.readFloat();
    }
  }

  public static class DoubleSerializer extends ImmutableSerializer<Double> {
    @Override public void write(Kryo kryo, Output output, Double object) {
      output.writeDouble(object);
    }

    @Override public Double read(Kryo kryo, Input input, Class<? extends Double> type) {
      return input.readDouble();
    }
  }

  public static class StringSerializer extends ImmutableSerializer<String> {
    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, String object) {
      output.writeString(object);
    }

    @Override public String read(Kryo kryo, Input input, Class<? extends String> type) {
      return input.readString();
    }
  }

  public static class BigIntegerSerializer extends ImmutableSerializer<BigInteger> {
    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, BigInteger object) {
      if (object == null) {
        output.writeByte(NULL);
        return;
      }
      if (object == BigInteger.ZERO) {
        output.writeByte(2);
        output.writeByte(0);
        return;
      }
      byte[] bytes = object.toByteArray();
      output.writeVarInt(bytes.length + 1, true);
      output.writeBytes(bytes);
    }

    @Override public BigInteger read(Kryo kryo, Input input, Class<? extends BigInteger> type) {
      int length = input.readVarInt(true);
      if (length == NULL) {
        return null;
      }
      byte[] bytes = input.readBytes(length - 1);
      if (type != BigInteger.class && type != null) {
        try {
          Constructor<? extends BigInteger> constructor = type.getConstructor(byte[].class);
          if (!constructor.isAccessible()) {
            try {
              constructor.setAccessible(true);
            } catch (SecurityException ignored) {
            }
          }
          return constructor.newInstance(bytes);
        } catch (Exception ex) {
          throw new KryoException(ex);
        }
      }
      if (length == 2) {
        switch (bytes[0]) {
          case 0:
          return BigInteger.ZERO;
          case 1:
          return BigInteger.ONE;
          case 10:
          return BigInteger.TEN;
        }
      }
      return new BigInteger(bytes);
    }
  }

  public static class BigDecimalSerializer extends ImmutableSerializer<BigDecimal> {
    private final BigIntegerSerializer bigIntegerSerializer = new BigIntegerSerializer();

    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, BigDecimal object) {
      if (object == null) {
        output.writeByte(NULL);
        return;
      }
      if (object == BigDecimal.ZERO) {
        bigIntegerSerializer.write(kryo, output, BigInteger.ZERO);
        output.writeInt(0, false);
        return;
      }
      bigIntegerSerializer.write(kryo, output, object.unscaledValue());
      output.writeInt(object.scale(), false);
    }

    @Override public BigDecimal read(Kryo kryo, Input input, Class<? extends BigDecimal> type) {
      BigInteger unscaledValue = bigIntegerSerializer.read(kryo, input, BigInteger.class);
      if (unscaledValue == null) {
        return null;
      }
      int scale = input.readInt(false);
      if (type != BigDecimal.class && type != null) {
        try {
          Constructor<? extends BigDecimal> constructor = type.getConstructor(BigInteger.class, int.class);
          if (!constructor.isAccessible()) {
            try {
              constructor.setAccessible(true);
            } catch (SecurityException ignored) {
            }
          }
          return constructor.newInstance(unscaledValue, scale);
        } catch (Exception ex) {
          throw new KryoException(ex);
        }
      }
      if (unscaledValue == BigInteger.ZERO && scale == 0) {
        return BigDecimal.ZERO;
      }
      return new BigDecimal(unscaledValue, scale);
    }
  }

  public static class ClassSerializer extends ImmutableSerializer<Class> {
    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, Class type) {
      kryo.writeClass(output, type);
      if (type != null && (type.isPrimitive() || isWrapperClass(type))) {
        output.writeBoolean(type.isPrimitive());
      }
    }

    @Override public Class read(Kryo kryo, Input input, Class<? extends Class> ignored) {
      Registration registration = kryo.readClass(input);
      if (registration == null) {
        return null;
      }
      Class type = registration.getType();
      if (!type.isPrimitive() || input.readBoolean()) {
        return type;
      }
      return getWrapperClass(type);
    }
  }

  public static class DateSerializer extends Serializer<Date> {
    private Date create(Kryo kryo, Class<? extends Date> type, long time) throws KryoException {
      if (type == Date.class || type == null) {
        return new Date(time);
      }
      if (type == Timestamp.class) {
        return new Timestamp(time);
      }
      if (type == java.sql.Date.class) {
        return new java.sql.Date(time);
      }
      if (type == Time.class) {
        return new Time(time);
      }
      try {
        Constructor<? extends Date> constructor = type.getConstructor(long.class);
        if (!constructor.isAccessible()) {
          try {
            constructor.setAccessible(true);
          } catch (SecurityException ignored) {
          }
        }
        return constructor.newInstance(time);
      } catch (Exception ex) {
        Date d = kryo.newInstance(type);
        d.setTime(time);
        return d;
      }
    }

    @Override public void write(Kryo kryo, Output output, Date object) {
      output.writeVarLong(object.getTime(), true);
    }

    @Override public Date read(Kryo kryo, Input input, Class<? extends Date> type) {
      return create(kryo, type, input.readVarLong(true));
    }

    @Override public Date copy(Kryo kryo, Date original) {
      return create(kryo, original.getClass(), original.getTime());
    }
  }

  public static class EnumSerializer extends ImmutableSerializer<Enum> {
    {
      setAcceptsNull(true);
    }

    private Object[] enumConstants;

    public EnumSerializer(Class<? extends Enum> type) {
      enumConstants = type.getEnumConstants();
      if (enumConstants == null && !Enum.class.equals(type)) {
        throw new IllegalArgumentException("The type must be an enum: " + type);
      }
    }

    @Override public void write(Kryo kryo, Output output, Enum object) {
      if (object == null) {
        output.writeVarInt(NULL, true);
        return;
      }
      output.writeVarInt(object.ordinal() + 1, true);
    }

    @Override public Enum read(Kryo kryo, Input input, Class<? extends Enum> type) {
      int ordinal = input.readVarInt(true);
      if (ordinal == NULL) {
        return null;
      }
      ordinal--;
      if (ordinal < 0 || ordinal > enumConstants.length - 1) {
        throw new KryoException("Invalid ordinal for enum \"" + type.getName() + "\": " + ordinal);
      }
      Object constant = enumConstants[ordinal];
      return (Enum) constant;
    }
  }

  public static class EnumSetSerializer extends Serializer<EnumSet> {
    @Override public void write(Kryo kryo, Output output, EnumSet object) {
      Serializer serializer;
      if (object.isEmpty()) {
        EnumSet tmp = EnumSet.complementOf(object);
        if (tmp.isEmpty()) {
          throw new KryoException("An EnumSet must have a defined Enum to be serialized.");
        }
        serializer = kryo.writeClass(output, tmp.iterator().next().getClass()).getSerializer();
      } else {
        serializer = kryo.writeClass(output, object.iterator().next().getClass()).getSerializer();
      }
      output.writeVarInt(object.size(), true);
      for (Object element : object) {
        serializer.write(kryo, output, element);
      }
    }

    @Override public EnumSet read(Kryo kryo, Input input, Class<? extends EnumSet> type) {
      Registration registration = kryo.readClass(input);
      EnumSet object = EnumSet.noneOf(registration.getType());
      Serializer serializer = registration.getSerializer();
      int length = input.readVarInt(true);
      for (int i = 0; i < length; i++) {
        object.add(serializer.read(kryo, input, null));
      }
      return object;
    }

    @Override public EnumSet copy(Kryo kryo, EnumSet original) {
      return EnumSet.copyOf(original);
    }
  }

  public static class CurrencySerializer extends ImmutableSerializer<Currency> {
    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, Currency object) {
      output.writeString(object == null ? null : object.getCurrencyCode());
    }

    @Override public Currency read(Kryo kryo, Input input, Class<? extends Currency> type) {
      String currencyCode = input.readString();
      if (currencyCode == null) {
        return null;
      }
      return Currency.getInstance(currencyCode);
    }
  }

  public static class StringBufferSerializer extends Serializer<StringBuffer> {
    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, StringBuffer object) {
      output.writeString(object == null ? null : object.toString());
    }

    @Override public StringBuffer read(Kryo kryo, Input input, Class<? extends StringBuffer> type) {
      String value = input.readString();
      if (value == null) {
        return null;
      }
      return new StringBuffer(value);
    }

    @Override public StringBuffer copy(Kryo kryo, StringBuffer original) {
      return new StringBuffer(original);
    }
  }

  public static class StringBuilderSerializer extends Serializer<StringBuilder> {
    {
      setAcceptsNull(true);
    }

    @Override public void write(Kryo kryo, Output output, StringBuilder object) {
      output.writeString(object == null ? null : object.toString());
    }

    @Override public StringBuilder read(Kryo kryo, Input input, Class<? extends StringBuilder> type) {
      return input.readStringBuilder();
    }

    @Override public StringBuilder copy(Kryo kryo, StringBuilder original) {
      return new StringBuilder(original);
    }
  }

  public static class KryoSerializableSerializer extends Serializer<KryoSerializable> {
    @Override public void write(Kryo kryo, Output output, KryoSerializable object) {
      object.write(kryo, output);
    }

    @Override public KryoSerializable read(Kryo kryo, Input input, Class<? extends KryoSerializable> type) {
      KryoSerializable object = kryo.newInstance(type);
      kryo.reference(object);
      object.read(kryo, input);
      return object;
    }
  }

  public static class CollectionsEmptyListSerializer extends ImmutableSerializer<Collection> {
    @Override public void write(Kryo kryo, Output output, Collection object) {
    }

    @Override public Collection read(Kryo kryo, Input input, Class<? extends Collection> type) {
      return Collections.EMPTY_LIST;
    }
  }

  public static class CollectionsEmptyMapSerializer extends ImmutableSerializer<Map> {
    @Override public void write(Kryo kryo, Output output, Map object) {
    }

    @Override public Map read(Kryo kryo, Input input, Class<? extends Map> type) {
      return Collections.EMPTY_MAP;
    }
  }

  public static class CollectionsEmptySetSerializer extends ImmutableSerializer<Set> {
    @Override public void write(Kryo kryo, Output output, Set object) {
    }

    @Override public Set read(Kryo kryo, Input input, Class<? extends Set> type) {
      return Collections.EMPTY_SET;
    }
  }

  public static class CollectionsSingletonListSerializer extends Serializer<List> {
    @Override public void write(Kryo kryo, Output output, List object) {
      kryo.writeClassAndObject(output, object.get(0));
    }

    @Override public List read(Kryo kryo, Input input, Class<? extends List> type) {
      return Collections.singletonList(kryo.readClassAndObject(input));
    }

    @Override public List copy(Kryo kryo, List original) {
      return Collections.singletonList(kryo.copy(original.get(0)));
    }
  }

  public static class CollectionsSingletonMapSerializer extends Serializer<Map> {
    @Override public void write(Kryo kryo, Output output, Map object) {
      Entry entry = (Entry) object.entrySet().iterator().next();
      kryo.writeClassAndObject(output, entry.getKey());
      kryo.writeClassAndObject(output, entry.getValue());
    }

    @Override public Map read(Kryo kryo, Input input, Class<? extends Map> type) {
      Object key = kryo.readClassAndObject(input);
      Object value = kryo.readClassAndObject(input);
      return Collections.singletonMap(key, value);
    }

    @Override public Map copy(Kryo kryo, Map original) {
      Entry entry = (Entry) original.entrySet().iterator().next();
      return Collections.singletonMap(kryo.copy(entry.getKey()), kryo.copy(entry.getValue()));
    }
  }

  public static class CollectionsSingletonSetSerializer extends Serializer<Set> {
    @Override public void write(Kryo kryo, Output output, Set object) {
      kryo.writeClassAndObject(output, object.iterator().next());
    }

    @Override public Set read(Kryo kryo, Input input, Class<? extends Set> type) {
      return Collections.singleton(kryo.readClassAndObject(input));
    }

    @Override public Set copy(Kryo kryo, Set original) {
      return Collections.singleton(kryo.copy(original.iterator().next()));
    }
  }

  public static class TimeZoneSerializer extends ImmutableSerializer<TimeZone> {
    @Override public void write(Kryo kryo, Output output, TimeZone object) {
      output.writeString(object.getID());
    }

    @Override public TimeZone read(Kryo kryo, Input input, Class<? extends TimeZone> type) {
      return TimeZone.getTimeZone(input.readString());
    }
  }

  public static class CalendarSerializer extends Serializer<Calendar> {
    private static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;

    TimeZoneSerializer timeZoneSerializer = new TimeZoneSerializer();

    @Override public void write(Kryo kryo, Output output, Calendar object) {
      timeZoneSerializer.write(kryo, output, object.getTimeZone());
      output.writeVarLong(object.getTimeInMillis(), true);
      output.writeBoolean(object.isLenient());
      output.writeInt(object.getFirstDayOfWeek(), true);
      output.writeInt(object.getMinimalDaysInFirstWeek(), true);
      if (object instanceof GregorianCalendar) {
        output.writeVarLong(((GregorianCalendar) object).getGregorianChange().getTime(), false);
      } else {
        output.writeVarLong(DEFAULT_GREGORIAN_CUTOVER, false);
      }
    }

    @Override public Calendar read(Kryo kryo, Input input, Class<? extends Calendar> type) {
      Calendar result = Calendar.getInstance(timeZoneSerializer.read(kryo, input, TimeZone.class));
      result.setTimeInMillis(input.readVarLong(true));
      result.setLenient(input.readBoolean());
      result.setFirstDayOfWeek(input.readInt(true));
      result.setMinimalDaysInFirstWeek(input.readInt(true));
      long gregorianChange = input.readVarLong(false);
      if (gregorianChange != DEFAULT_GREGORIAN_CUTOVER) {
        if (result instanceof GregorianCalendar) {
          ((GregorianCalendar) result).setGregorianChange(new Date(gregorianChange));
        }
      }
      return result;
    }

    @Override public Calendar copy(Kryo kryo, Calendar original) {
      return (Calendar) original.clone();
    }
  }

  public static class TreeMapSerializer extends MapSerializer<TreeMap> {
    @Override protected void writeHeader(Kryo kryo, Output output, TreeMap treeSet) {
      kryo.writeClassAndObject(output, treeSet.comparator());
    }

    @Override protected TreeMap create(Kryo kryo, Input input, Class<? extends TreeMap> type, int size) {
      return createTreeMap(type, (Comparator) kryo.readClassAndObject(input));
    }

    @Override protected TreeMap createCopy(Kryo kryo, TreeMap original) {
      return createTreeMap(original.getClass(), original.comparator());
    }

    private TreeMap createTreeMap(Class<? extends TreeMap> type, Comparator comparator) {
      if (type == TreeMap.class || type == null) {
        return new TreeMap(comparator);
      }
      try {
        Constructor constructor = type.getConstructor(Comparator.class);
        if (!constructor.isAccessible()) {
          try {
            constructor.setAccessible(true);
          } catch (SecurityException ignored) {
          }
        }
        return (TreeMap) constructor.newInstance(comparator);
      } catch (Exception ex) {
        throw new KryoException(ex);
      }
    }
  }

  public static class ConcurrentSkipListMapSerializer extends MapSerializer<ConcurrentSkipListMap> {
    @Override protected void writeHeader(Kryo kryo, Output output, ConcurrentSkipListMap concurrentSkipListMap) {
      kryo.writeClassAndObject(output, concurrentSkipListMap.comparator());
    }

    @Override protected ConcurrentSkipListMap create(Kryo kryo, Input input, Class<? extends ConcurrentSkipListMap> type, int size) {
      return createConcurrentSkipListMap(type, (Comparator) kryo.readClassAndObject(input));
    }

    @Override protected ConcurrentSkipListMap createCopy(Kryo kryo, ConcurrentSkipListMap original) {
      return createConcurrentSkipListMap(original.getClass(), original.comparator());
    }

    private ConcurrentSkipListMap createConcurrentSkipListMap(Class<? extends ConcurrentSkipListMap> type, Comparator comparator) {
      if (type == ConcurrentSkipListMap.class || type == null) {
        return new ConcurrentSkipListMap(comparator);
      }
      try {
        Constructor constructor = type.getConstructor(Comparator.class);
        if (!constructor.isAccessible()) {
          try {
            constructor.setAccessible(true);
          } catch (SecurityException ignored) {
          }
        }
        return (ConcurrentSkipListMap) constructor.newInstance(comparator);
      } catch (Exception ex) {
        throw new KryoException(ex);
      }
    }
  }

  public static class TreeSetSerializer extends CollectionSerializer<TreeSet> {
    @Override protected void writeHeader(Kryo kryo, Output output, TreeSet treeSet) {
      kryo.writeClassAndObject(output, treeSet.comparator());
    }

    @Override protected TreeSet create(Kryo kryo, Input input, Class<? extends TreeSet> type, int size) {
      return createTreeSet(type, (Comparator) kryo.readClassAndObject(input));
    }

    @Override protected TreeSet createCopy(Kryo kryo, TreeSet original) {
      return createTreeSet(original.getClass(), original.comparator());
    }

    private TreeSet createTreeSet(Class<? extends Collection> type, Comparator comparator) {
      if (type == TreeSet.class || type == null) {
        return new TreeSet(comparator);
      }
      try {
        Constructor constructor = type.getConstructor(Comparator.class);
        if (!constructor.isAccessible()) {
          try {
            constructor.setAccessible(true);
          } catch (SecurityException ignored) {
          }
        }
        return (TreeSet) constructor.newInstance(comparator);
      } catch (Exception ex) {
        throw new KryoException(ex);
      }
    }
  }

  public static class PriorityQueueSerializer extends CollectionSerializer<PriorityQueue> {
    @Override protected void writeHeader(Kryo kryo, Output output, PriorityQueue queue) {
      kryo.writeClassAndObject(output, queue.comparator());
    }

    @Override protected PriorityQueue create(Kryo kryo, Input input, Class<? extends PriorityQueue> type, int size) {
      return createPriorityQueue(type, size, (Comparator) kryo.readClassAndObject(input));
    }

    @Override protected PriorityQueue createCopy(Kryo kryo, PriorityQueue original) {
      return createPriorityQueue(original.getClass(), original.size(), original.comparator());
    }

    private PriorityQueue createPriorityQueue(Class<? extends Collection> type, int size, Comparator comparator) {
      if (type == PriorityQueue.class || type == null) {
        return new PriorityQueue(size, comparator);
      }
      try {
        Constructor constructor = type.getConstructor(int.class, Comparator.class);
        if (!constructor.isAccessible()) {
          try {
            constructor.setAccessible(true);
          } catch (SecurityException ignored) {
          }
        }
        return (PriorityQueue) constructor.newInstance(comparator);
      } catch (Exception ex) {
        throw new KryoException(ex);
      }
    }
  }

  public static class LocaleSerializer extends ImmutableSerializer<Locale> {
    public static final Locale SPANISH = new Locale("es", "", "");

    public static final Locale SPAIN = new Locale("es", "ES", "");

    protected Locale create(String language, String country, String variant) {
      Locale defaultLocale = Locale.getDefault();
      if (isSameLocale(defaultLocale, language, country, variant)) {
        return defaultLocale;
      }
      if (defaultLocale != Locale.US && isSameLocale(Locale.US, language, country, variant)) {
        return Locale.US;
      }
      if (isSameLocale(Locale.ENGLISH, language, country, variant)) {
        return Locale.ENGLISH;
      }
      if (isSameLocale(Locale.GERMAN, language, country, variant)) {
        return Locale.GERMAN;
      }
      if (isSameLocale(SPANISH, language, country, variant)) {
        return SPANISH;
      }
      if (isSameLocale(Locale.FRENCH, language, country, variant)) {
        return Locale.FRENCH;
      }
      if (isSameLocale(Locale.ITALIAN, language, country, variant)) {
        return Locale.ITALIAN;
      }
      if (isSameLocale(Locale.JAPANESE, language, country, variant)) {
        return Locale.JAPANESE;
      }
      if (isSameLocale(Locale.KOREAN, language, country, variant)) {
        return Locale.KOREAN;
      }
      if (isSameLocale(Locale.SIMPLIFIED_CHINESE, language, country, variant)) {
        return Locale.SIMPLIFIED_CHINESE;
      }
      if (isSameLocale(Locale.CHINESE, language, country, variant)) {
        return Locale.CHINESE;
      }
      if (isSameLocale(Locale.TRADITIONAL_CHINESE, language, country, variant)) {
        return Locale.TRADITIONAL_CHINESE;
      }
      if (isSameLocale(Locale.UK, language, country, variant)) {
        return Locale.UK;
      }
      if (isSameLocale(Locale.GERMANY, language, country, variant)) {
        return Locale.GERMANY;
      }
      if (isSameLocale(SPAIN, language, country, variant)) {
        return SPAIN;
      }
      if (isSameLocale(Locale.FRANCE, language, country, variant)) {
        return Locale.FRANCE;
      }
      if (isSameLocale(Locale.ITALY, language, country, variant)) {
        return Locale.ITALY;
      }
      if (isSameLocale(Locale.JAPAN, language, country, variant)) {
        return Locale.JAPAN;
      }
      if (isSameLocale(Locale.KOREA, language, country, variant)) {
        return Locale.KOREA;
      }
      if (isSameLocale(Locale.CANADA, language, country, variant)) {
        return Locale.CANADA;
      }
      if (isSameLocale(Locale.CANADA_FRENCH, language, country, variant)) {
        return Locale.CANADA_FRENCH;
      }
      return new Locale(language, country, variant);
    }

    @Override public void write(Kryo kryo, Output output, Locale l) {
      output.writeAscii(l.getLanguage());
      output.writeAscii(l.getCountry());
      output.writeString(l.getVariant());
    }

    @Override public Locale read(Kryo kryo, Input input, Class<? extends Locale> type) {
      String language = input.readString();
      String country = input.readString();
      String variant = input.readString();
      return create(language, country, variant);
    }

    protected static boolean isSameLocale(Locale locale, String language, String country, String variant) {
      return (locale.getLanguage().equals(language) && locale.getCountry().equals(country) && locale.getVariant().equals(variant));
    }
  }

  public static class CharsetSerializer extends ImmutableSerializer<Charset> {
    @Override public void write(Kryo kryo, Output output, Charset object) {
      output.writeString(object.name());
    }

    @Override public Charset read(Kryo kryo, Input input, Class<? extends Charset> type) {
      return Charset.forName(input.readString());
    }
  }

  public static class URLSerializer extends ImmutableSerializer<URL> {
    @Override public void write(Kryo kryo, Output output, URL object) {
      output.writeString(object.toExternalForm());
    }

    @Override public URL read(Kryo kryo, Input input, Class<? extends URL> type) {
      try {
        return new java.net.URL(input.readString());
      } catch (MalformedURLException ex) {
        throw new KryoException(ex);
      }
    }
  }

  public static class ArraysAsListSerializer extends CollectionSerializer<List> {
    @Override protected List create(Kryo kryo, Input input, Class type, int size) {
      return new ArrayList(size);
    }

    @Override public List read(Kryo kryo, Input input, Class type) {
      List list = super.read(kryo, input, type);
      if (list == null) {
        return null;
      }
      return Arrays.asList(list.toArray());
    }

    @Override public List copy(Kryo kryo, List original) {
      Object[] copyArr = new Object[original.size()];
      List<Object> copy = Arrays.asList(copyArr);
      kryo.reference(copy);
      for (int i = 0; i < original.size(); i++) {
        copyArr[i] = kryo.copy(original.get(i));
      }
      return copy;
    }
  }

  public static class BitSetSerializer extends Serializer<BitSet> {
    @Override public void write(Kryo kryo, Output output, BitSet set) {
      long[] values = set.toLongArray();
      output.writeVarInt(values.length, true);
      output.writeLongs(values, 0, values.length);
    }

    @Override public BitSet read(Kryo kryo, Input input, Class type) {
      int length = input.readVarInt(true);
      long[] values = input.readLongs(length);
      BitSet set = BitSet.valueOf(values);
      return set;
    }

    @Override public BitSet copy(Kryo kryo, BitSet original) {
      return BitSet.valueOf(original.toLongArray());
    }
  }
}