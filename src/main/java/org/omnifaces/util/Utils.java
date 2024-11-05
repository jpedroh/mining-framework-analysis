package org.omnifaces.util;
import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.quote;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class Utils {
  public static final Charset UTF_8 = Charset.forName("UTF-8");

  private static final int DEFAULT_STREAM_BUFFER_SIZE = 10240;

  private static final String PATTERN_RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";

  private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");

  private static final int BASE64_SEGMENT_LENGTH = 4;

  private static final int UNICODE_3_BYTES = 0xfff;

  private static final int UNICODE_2_BYTES = 0xff;

  private static final int UNICODE_1_BYTE = 0xf;

  private static final int UNICODE_END_PRINTABLE_ASCII = 0x7f;

  private static final int UNICODE_BEGIN_PRINTABLE_ASCII = 0x20;

  private static final String ERROR_UNSUPPORTED_ENCODING = "UTF-8 is apparently not supported on this platform.";

  private Utils() {
  }

  public static boolean isEmpty(String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  public static boolean isEmpty(Object value) {
    if (value == null) {
      return true;
    } else {
      if (value instanceof String) {
        return ((String) value).isEmpty();
      } else {
        if (value instanceof Collection<?>) {
          return ((Collection<?>) value).isEmpty();
        } else {
          if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).isEmpty();
          } else {
            if (value.getClass().isArray()) {
              return Array.getLength(value) == 0;
            } else {
              return value.toString() == null || value.toString().isEmpty();
            }
          }
        }
      }
    }
  }

  public static boolean isAnyEmpty(Object... values) {
    for (Object value : values) {
      if (isEmpty(value)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isBlank(String string) {
    return isEmpty(string) || string.trim().isEmpty();
  }

  public static boolean isNumber(String string) {
    try {
      Long.parseLong(string);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isDecimal(String string) {
    try {
      Double.parseDouble(string);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static <T extends java.lang.Object> T coalesce(T... objects) {
    for (T object : objects) {
      if (object != null) {
        return object;
      }
    }
    return null;
  }

  public static <T extends java.lang.Object> boolean isOneOf(T object, T... objects) {
    for (Object other : objects) {
      if (object == null ? other == null : object.equals(other)) {
        return true;
      }
    }
    return false;
  }

  public static boolean startsWithOneOf(String string, String... prefixes) {
    for (String prefix : prefixes) {
      if (string.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isOneInstanceOf(Class<?> cls, Class<?>... classes) {
    for (Class<?> other : classes) {
      if (cls == null ? other == null : other.isAssignableFrom(cls)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isOneAnnotationPresent(Class<?> cls, Class<? extends Annotation>... annotations) {
    for (Class<? extends Annotation> annotation : annotations) {
      if (cls.isAnnotationPresent(annotation)) {
        return true;
      }
    }
    return false;
  }

  public static long stream(InputStream input, OutputStream output) throws IOException {
    ReadableByteChannel inputChannel = null;
    WritableByteChannel outputChannel = null;
    try {
      inputChannel = Channels.newChannel(input);
      outputChannel = Channels.newChannel(output);
      ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
      long size = 0;
      while (inputChannel.read(buffer) != -1) {
        buffer.flip();
        size += outputChannel.write(buffer);
        buffer.clear();
      }
      return size;
    }  finally {
      close(outputChannel);
      close(inputChannel);
    }
  }

  public static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    stream(input, output);
    return output.toByteArray();
  }

  public static IOException close(Closeable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (IOException e) {
        return e;
      }
    }
    return null;
  }

  @SuppressWarnings(value = { "unchecked" }) public static <E extends java.lang.Object> Set<E> unmodifiableSet(Object... values) {
    Set<E> set = new HashSet<E>();
    for (Object value : values) {
      if (value instanceof Object[]) {
        for (Object item : (Object[]) value) {
          set.add((E) item);
        }
      } else {
        if (value instanceof Collection<?>) {
          for (Object item : (Collection<?>) value) {
            set.add((E) item);
          }
        } else {
          set.add((E) value);
        }
      }
    }
    return Collections.unmodifiableSet(set);
  }

  public static <E extends java.lang.Object> List<E> iterableToList(Iterable<E> iterable) {
    List<E> list = null;
    if (iterable instanceof List) {
      list = (List<E>) iterable;
    } else {
      if (iterable instanceof Collection) {
        list = new ArrayList<E>((Collection<E>) iterable);
      } else {
        list = new ArrayList<E>();
        Iterator<E> iterator = iterable.iterator();
        while (iterator.hasNext()) {
          list.add(iterator.next());
        }
      }
    }
    return list;
  }

  public static List<String> csvToList(String values) {
    return csvToList(values, ",");
  }

  public static List<String> csvToList(String values, String delimiter) {
    if (isEmpty(values)) {
      return emptyList();
    }
    List<String> list = new ArrayList<String>();
    for (String value : values.split(quote(delimiter))) {
      String trimmedValue = value.trim();
      if (!isEmpty(trimmedValue)) {
        list.add(trimmedValue);
      }
    }
    return list;
  }

  public static <T extends java.lang.Object> Map<T, T> reverse(Map<T, T> source) {
    Map<T, T> target = new HashMap<T, T>();
    for (Entry<T, T> entry : source.entrySet()) {
      target.put(entry.getValue(), entry.getKey());
    }
    return target;
  }

  public static boolean containsByClassName(Collection<?> objects, String className) {
    for (Object object : objects) {
      if (object.getClass().getName().equals(className)) {
        return true;
      }
    }
    return false;
  }

  public static String formatRFC1123(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_RFC1123_DATE, Locale.US);
    sdf.setTimeZone(TIMEZONE_GMT);
    return sdf.format(date);
  }

  public static Date parseRFC1123(String string) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_RFC1123_DATE, Locale.US);
    return sdf.parse(string);
  }

  public static String serializeURLSafe(String string) {
    if (string == null) {
      return null;
    }
    try {
      InputStream raw = new ByteArrayInputStream(string.getBytes(UTF_8));
      ByteArrayOutputStream deflated = new ByteArrayOutputStream();
      stream(raw, new DeflaterOutputStream(deflated, new Deflater(Deflater.BEST_COMPRESSION)));
      String base64 = DatatypeConverter.printBase64Binary(deflated.toByteArray());
      return base64.replace('+', '-').replace('/', '_').replace("=", "");
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String unserializeURLSafe(String string) {
    if (string == null) {
      return null;
    }
    try {
      String base64 = string.replace('-', '+').replace('_', '/') + "===".substring(0, string.length() % BASE64_SEGMENT_LENGTH);
      InputStream deflated = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(base64));
      return new String(toByteArray(new InflaterInputStream(deflated)), UTF_8);
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String encodeURL(String string) {
    if (string == null) {
      return null;
    }
    try {
      return URLEncoder.encode(string, UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(ERROR_UNSUPPORTED_ENCODING, e);
    }
  }

  public static String decodeURL(String string) {
    if (string == null) {
      return null;
    }
    try {
      return URLDecoder.decode(string, UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(ERROR_UNSUPPORTED_ENCODING, e);
    }
  }

  public static String escapeJS(String string, boolean escapeSingleQuote) {
    if (string == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder(string.length());
    for (char c : string.toCharArray()) {
      if (c > UNICODE_3_BYTES) {
        builder.append("\\u").append(Integer.toHexString(c));
      } else {
        if (c > UNICODE_2_BYTES) {
          builder.append("\\u0").append(Integer.toHexString(c));
        } else {
          if (c > UNICODE_END_PRINTABLE_ASCII) {
            builder.append("\\u00").append(Integer.toHexString(c));
          } else {
            if (c < UNICODE_BEGIN_PRINTABLE_ASCII) {
              escapeJSControlCharacter(builder, c);
            } else {
              escapeJSASCIICharacter(builder, c, escapeSingleQuote);
            }
          }
        }
      }
    }
    return builder.toString();
  }

  private static void escapeJSControlCharacter(StringBuilder builder, char c) {
    switch (c) {
      case '\b':
      builder.append('\\').append('b');
      break;
      case '\n':
      builder.append('\\').append('n');
      break;
      case '\t':
      builder.append('\\').append('t');
      break;
      case '\f':
      builder.append('\\').append('f');
      break;
      case '\r':
      builder.append('\\').append('r');
      break;
      default:
      if (c > UNICODE_1_BYTE) {
        builder.append("\\u00").append(Integer.toHexString(c));
      } else {
        builder.append("\\u000").append(Integer.toHexString(c));
      }
      break;
    }
  }

  private static void escapeJSASCIICharacter(StringBuilder builder, char c, boolean escapeSingleQuote) {
    switch (c) {
      case '\'':
      if (escapeSingleQuote) {
        builder.append('\\');
      }
      builder.append('\'');
      break;
      case '\"':
      builder.append('\\').append('\"');
      break;
      case '\\':
      builder.append('\\').append('\\');
      break;
      case '/':
      builder.append('\\').append('/');
      break;
      default:
      builder.append(c);
      break;
    }
  }

  public static long stream(File file, OutputStream output, long start, long length) throws IOException {
    if (start == 0 && length >= file.length()) {
      return stream(new FileInputStream(file), output);
    }
    try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(file.toPath(), StandardOpenOption.READ)) {
      WritableByteChannel outputChannel = Channels.newChannel(output);
      ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
      long size = 0;
      while (fileChannel.read(buffer, start + size) != -1) {
        buffer.flip();
        if (size + buffer.limit() > length) {
          buffer.limit((int) (length - size));
        }
        size += outputChannel.write(buffer);
        if (size >= length) {
          break;
        }
        buffer.clear();
      }
      return size;
    }
  }
}