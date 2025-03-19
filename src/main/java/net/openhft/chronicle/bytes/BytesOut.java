package net.openhft.chronicle.bytes;
import net.openhft.chronicle.core.io.ClosedIllegalStateException;
import net.openhft.chronicle.core.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Proxy;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import static net.openhft.chronicle.bytes.internal.ReferenceCountedUtil.throwExceptionIfReleased;

@SuppressWarnings(value = { "rawtypes", "unchecked" }) public interface BytesOut<U extends java.lang.Object> extends StreamingDataOutput<Bytes<U>>, ByteStringAppender<Bytes<U>>, BytesPrepender<Bytes<U>>, BytesComment<BytesOut<U>> {
  /**
     * Proxy an interface so each message called is written to a file for replay.
     *
     * @param tClass     primary interface
     * @param additional any additional interfaces
     * @return a proxy which implements the primary interface (additional interfaces have to be
     * cast)
     * @throws NullPointerException if the provided {@code tClass} is {@code null}
     * @throws ClosedIllegalStateException if this BytesOut has been previously released
     */
  @NotNull default <T extends java.lang.Object> T bytesMethodWriter(@NotNull Class<T> tClass, Class... additional) throws IllegalArgumentException {
    throwExceptionIfReleased(this);
    Class[] interfaces = ObjectUtils.addAll(tClass, additional);
    return (T) Proxy.newProxyInstance(tClass.getClassLoader(), interfaces, new BinaryBytesMethodWriterInvocationHandler(MethodEncoderLookup.BY_ANNOTATION, this));
  }

  void writeMarshallableLength16(WriteBytesMarshallable marshallable) throws IllegalArgumentException, BufferOverflowException, IllegalStateException, BufferUnderflowException;

  /**
     * Write a limit set of writeObject types.
     *
     * @param componentType expected.
     * @param obj           of componentType
     */
  default void writeObject(Class componentType, Object obj) throws IllegalArgumentException, BufferOverflowException, ArithmeticException, IllegalStateException, BufferUnderflowException {
    if (!componentType.isInstance(obj)) {
      throw new IllegalArgumentException("Cannot serialize " + obj.getClass() + " as an " + componentType);
    }
    if (obj instanceof BytesMarshallable) {
      ((BytesMarshallable) obj).writeMarshallable(this);
      return;
    }
    if (obj instanceof Enum) {
      writeEnum((Enum) obj);
      return;
    }
    if (obj instanceof BytesStore) {
      BytesStore bs = (BytesStore) obj;
      writeStopBit(bs.readRemaining());
      write(bs);
      return;
    }
    switch (componentType.getName()) {
      case "java.lang.String":
      writeUtf8((String) obj);
      return;
      case "java.lang.Double":
      writeDouble((Double) obj);
      return;
      case "java.lang.Long":
      writeLong((Long) obj);
      return;
      case "java.lang.Integer":
      writeInt((Integer) obj);
      return;
      default:
      throw new UnsupportedOperationException("Not supported " + componentType);
    }
  }
}