package net.openhft.chronicle.bytes.internal;
import net.openhft.chronicle.bytes.*;
import net.openhft.chronicle.bytes.util.DecoratedBufferOverflowException;
import net.openhft.chronicle.bytes.util.DecoratedBufferUnderflowException;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Memory;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.UnsafeMemory;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import static net.openhft.chronicle.core.util.ObjectUtils.requireNonNull;

/**
 * Bytes to wrap memory mapped data.
 * <p>
 * NOTE These Bytes are single Threaded as are all Bytes.
 */
@SuppressWarnings(value = { "rawtypes" }) public class ChunkedMappedBytes extends CommonMappedBytes {
  public ChunkedMappedBytes(@NotNull final MappedFile mappedFile) throws IllegalStateException {
    this(mappedFile, "");
  }

  protected ChunkedMappedBytes(@NotNull final MappedFile mappedFile, final String name) throws IllegalStateException {
    super(mappedFile, name);
  }

  @Override public @NotNull ChunkedMappedBytes write(final long offsetInRDO, final byte[] bytes, int offset, final int length) throws IllegalStateException, BufferOverflowException {
    requireNonNull(bytes);
    throwExceptionIfClosed();
    long wp = offsetInRDO;
    if ((length + offset) > bytes.length) {
      throw new ArrayIndexOutOfBoundsException("bytes.length=" + bytes.length + ", " + "length=" + length + ", offset=" + offset);
    }
    if (length > writeRemaining()) {
      throw new DecoratedBufferOverflowException(String.format("write failed. Length: %d > writeRemaining: %d", length, writeRemaining()));
    }
    int remaining = length;
    MappedBytesStore bytesStore = acquireNextByteStore(wp, false);
    while (remaining > 0) {
      long safeCopySize = copySize(wp);
      if (safeCopySize + mappedFile.overlapSize() >= remaining) {
        bytesStore.write(wp, bytes, offset, remaining);
        return this;
      }
      bytesStore.write(wp, bytes, offset, (int) safeCopySize);
      offset += safeCopySize;
      wp += safeCopySize;
      remaining -= safeCopySize;
      bytesStore = acquireNextByteStore0(wp, false);
    }
    return this;
  }

  @Override public @NotNull ChunkedMappedBytes write(final long writeOffset, @NotNull final RandomDataInput bytes, long readOffset, final long length) throws BufferOverflowException, BufferUnderflowException, IllegalStateException {
    requireNonNull(bytes);
    throwExceptionIfClosed();
    long wp = writeOffset;
    if (length > writeRemaining()) {
      throw new DecoratedBufferOverflowException(String.format("write failed. Length: %d > writeRemaining: %d", length, writeRemaining()));
    }
    long remaining = length;
    MappedBytesStore bytesStore = acquireNextByteStore(wp, false);
    while (remaining > 0) {
      long safeCopySize = copySize(wp);
      if (safeCopySize + mappedFile.overlapSize() >= remaining) {
        bytesStore.write(wp, bytes, readOffset, remaining);
        return this;
      }
      bytesStore.write(wp, bytes, readOffset, safeCopySize);
      readOffset += safeCopySize;
      wp += safeCopySize;
      remaining -= safeCopySize;
      bytesStore = acquireNextByteStore0(wp, false);
    }
    return this;
  }

  private long copySize(final long writePosition) {
    long size = mappedFile.chunkSize();
    return size - writePosition % size;
  }

  @NotNull @Override public Bytes<Void> readPositionRemaining(final long position, final long remaining) throws BufferUnderflowException, IllegalStateException {
    final long limit = position + remaining;
    acquireNextByteStore(position, true);
    try {
      if (writeLimit < limit) {
        writeLimit(limit);
      }
      if (Jvm.isAssertEnabled()) {
        readLimit(limit);
      } else {
        uncheckedWritePosition(limit);
      }
      return readPosition(position);
    } catch (BufferOverflowException e) {
      throw new AssertionError(e);
    }
  }

  @NotNull @Override public Bytes<Void> readPosition(final long position) throws BufferUnderflowException, IllegalStateException {
    if (bytesStore.inside(position)) {
      return super.readPosition(position);
    } else {
      acquireNextByteStore0(position, true);
      return this;
    }
  }

  /**
     * This single-argument version of the call returns an address which is guarateed safe for a contiguous
     * read up to the overlap size.
     * <p>
     * NOTE: If called with an offset which is already in the overlap region this call with therefore
     * prompt a remapping to the new segment, which in turn may unmap the current segment.
     * Any other handles using data in the current segment may therefore result in a memory violation
     * when next used.
     * <p>
     * If manipulating offsets which may reside in the overlap region, always use the 2-argument version below
     */
  @Override public long addressForRead(final long offset) throws BufferUnderflowException, IllegalStateException {
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset)) {
      bytesStore = acquireNextByteStore0(offset, true);
    }
    return bytesStore.addressForRead(offset);
  }

  /**
     * This two-argument version of the call returns an address which is guaranteed safe for a contiguous
     * read up to the requested buffer size.
     * <p>
     * NOTE: In contrast to the single-argument version this call will not prompt a remapping if
     * called within the overlap region (provided the full extent remains in the overlap region)
     * <p>
     * This version is therefore safe to use cooperatively with other handles in a defined sequence
     * of bytes (eg returned from a DocumentContext) regardless of whether the handles span the
     * overlap region
     */
  @Override public long addressForRead(final long offset, final int buffer) throws UnsupportedOperationException, BufferUnderflowException, IllegalStateException {
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, buffer)) {
      bytesStore = acquireNextByteStore0(offset, true);
    }
    return bytesStore.addressForRead(offset);
  }

  @Override public long addressForWrite(final long offset) throws UnsupportedOperationException, BufferOverflowException, IllegalStateException {
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset)) {
      bytesStore = acquireNextByteStore0(offset, true);
    }
    return bytesStore.addressForWrite(offset);
  }

  @Override protected void readCheckOffset(final long offset, final long adding, final boolean given) throws BufferUnderflowException, IllegalStateException {
    final long check = adding >= 0 ? offset : offset + adding;
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(check, adding)) {
      acquireNextByteStore0(offset, false);
    }
    super.readCheckOffset(offset, adding, given);
  }

  @Override protected void writeCheckOffset(final long offset, final long adding) throws BufferOverflowException, IllegalStateException {
    throwExceptionIfClosed();
    if (offset < 0 || offset > mappedFile.capacity() - adding) {
      throw writeBufferOverflowException0(offset);
    }
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, checkSize0(adding))) {
      acquireNextByteStore0(offset, false);
      if (!this.bytesStore.inside(offset, checkSize0(adding))) {
        throw new DecoratedBufferUnderflowException(String.format("Acquired the next BytesStore, but still not room to add %d when realCapacity %d", adding, this.bytesStore.realCapacity()));
      }
    }
  }

  private long checkSize0(long adding) {
    if (adding < 0 || adding > MAX_CAPACITY) {
      throw new IllegalArgumentException("Invalid size " + adding);
    }
    return adding;
  }

  @Override public void ensureCapacity(final long desiredCapacity) throws IllegalArgumentException, IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(writePosition(), checkSize0(desiredCapacity))) {
      acquireNextByteStore0(writePosition(), false);
    }
  }

  @Override public @NotNull Bytes<Void> writeSkip(long bytesToSkip) throws BufferOverflowException, IllegalStateException {
    writeCheckOffset(writePosition(), Math.min(128, bytesToSkip));
    uncheckedWritePosition(writePosition() + bytesToSkip);
    return this;
  }

  @NotNull private BufferOverflowException writeBufferOverflowException0(final long offset) {
    BufferOverflowException exception = new BufferOverflowException();
    exception.initCause(new IllegalArgumentException("Offset out of bound " + offset));
    return exception;
  }

  private @NotNull MappedBytesStore acquireNextByteStore(final long offset, final boolean set) throws IllegalStateException {
    BytesStore bytesStore = this.bytesStore;
    if (bytesStore.inside(offset)) {
      return (MappedBytesStore) bytesStore;
    }
    throwExceptionIfReleased();
    return acquireNextByteStore0(offset, set);
  }

  private synchronized @NotNull MappedBytesStore acquireNextByteStore0(final long offset, final boolean set) throws IllegalStateException {
    throwExceptionIfClosed();
    @Nullable final BytesStore oldBS = this.bytesStore;
    @NotNull final MappedBytesStore newBS;
    try {
      newBS = mappedFile.acquireByteStore(this, offset, oldBS);
      if (newBS != oldBS) {
        this.bytesStore(newBS);
        if (oldBS != null) {
          oldBS.release(this);
        }
        if (lastActualSize < newBS.maximumLimit) {
          lastActualSize = newBS.maximumLimit;
        }
      }
      assert newBS.reservedBy(this);
    } catch (@NotNull IOException e) {
      throw new IORuntimeException(e);
    }
    if (set) {
      try {
        if (writeLimit() < readPosition) {
          writeLimit(readPosition);
        }
        if (readLimit() < readPosition) {
          readLimit(readPosition);
        }
      } catch (BufferUnderflowException | BufferOverflowException e) {
        throw new AssertionError(e);
      }
      readPosition = offset;
    }
    return newBS;
  }

  @NotNull @Override public Bytes<Void> readSkip(final long bytesToSkip) throws BufferUnderflowException, IllegalStateException {
    if (readPosition + bytesToSkip > readLimit()) {
      throw new BufferUnderflowException();
    }
    long check = bytesToSkip >= 0 ? this.readPosition : this.readPosition + bytesToSkip;
    BytesStore bytesStore = this.bytesStore;
    if (bytesToSkip != (int) bytesToSkip || !bytesStore.inside(readPosition, (int) bytesToSkip)) {
      acquireNextByteStore0(check, false);
    }
    this.readPosition += bytesToSkip;
    return this;
  }

  @NotNull @Override public Bytes<Void> clear() throws IllegalStateException {
    long start = 0L;
    readPosition = start;
    uncheckedWritePosition(start);
    writeLimit = mappedFile.capacity();
    return this;
  }

  @NotNull @Override public Bytes<Void> writeByte(final byte i8) throws BufferOverflowException, IllegalStateException {
    throwExceptionIfClosed();
    final long oldPosition = writePosition();
    if (writePosition() < 0 || writePosition() > capacity() - 1) {
      throw writeBufferOverflowException0(writePosition());
    }
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(writePosition(), 1)) {
      bytesStore = acquireNextByteStore0(writePosition(), false);
    }
    uncheckedWritePosition(writePosition() + 1);
    bytesStore.writeByte(oldPosition, i8);
    return this;
  }

  @Override public boolean isElastic() {
    return true;
  }

  @NotNull @Override public Bytes<Void> write(@NotNull final BytesStore bytes, final long offset, final long length) throws BufferUnderflowException, BufferOverflowException, IllegalStateException {
    requireNonNull(bytes);
    throwExceptionIfClosed();
    if (length == 8) {
      writeLong(bytes.readLong(offset));
    } else {
      if (length > 0) {
        if (bytes.isDirectMemory()) {
          long fromAddress = bytes.addressForRead(offset);
          if (length <= bytes.bytesStore().realCapacity() - offset) {
            this.acquireNextByteStore(writePosition(), false);
            if (bytesStore.realCapacity() - writePosition() >= length) {
              rawCopy(length, fromAddress);
              return this;
            }
          }
        }
        BytesInternal.writeFully(bytes, offset, length, this);
      }
    }
    return this;
  }

  void rawCopy(final long length, final long fromAddress) throws BufferOverflowException, IllegalStateException {
    this.throwExceptionIfReleased();
    OS.memory().copyMemory(fromAddress, addressForWritePosition(), length);
    uncheckedWritePosition(writePosition() + length);
  }

  @Override @NotNull public Bytes<Void> writeOrderedInt(long offset, int i) throws BufferOverflowException, IllegalStateException {
    throwExceptionIfClosed();
    writeCheckOffset(offset, 4);
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, 4)) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    bytesStore.writeOrderedInt(offset, i);
    return this;
  }

  @Override public byte readVolatileByte(long offset) throws BufferUnderflowException, IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, 1)) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    return bytesStore.readVolatileByte(offset);
  }

  @Override public short readVolatileShort(long offset) throws BufferUnderflowException, IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, 2)) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    return bytesStore.readVolatileShort(offset);
  }

  @Override public int readVolatileInt(long offset) throws BufferUnderflowException, IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, 4)) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    return bytesStore.readVolatileInt(offset);
  }

  @Override public long readVolatileLong(long offset) throws BufferUnderflowException, IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, 8)) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    return bytesStore.readVolatileLong(offset);
  }

  @Override public int peekUnsignedByte() throws IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(readPosition, 1)) {
      bytesStore = acquireNextByteStore0(readPosition, false);
    }
    try {
      return readPosition >= writePosition() ? -1 : bytesStore.readUnsignedByte(readPosition);
    } catch (BufferUnderflowException e) {
      return -1;
    }
  }

  @Override public int peekUnsignedByte(final long offset) throws BufferUnderflowException, IllegalStateException {
    throwExceptionIfClosed();
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(offset, 1)) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    return offset >= readLimit() ? -1 : bytesStore.peekUnsignedByte(offset);
  }

  @SuppressWarnings(value = { "restriction" }) @Override public int peekVolatileInt() throws IllegalStateException {
    BytesStore bytesStore = this.bytesStore;
    if (!bytesStore.inside(readPosition, 4)) {
      bytesStore = acquireNextByteStore0(readPosition, true);
    }
    MappedBytesStore mbs = (MappedBytesStore) bytesStore;
    long address = mbs.address + mbs.translate(readPosition);
    @Nullable Memory memory = mbs.memory;
    if ((address & 63) <= 60) {
      ObjectUtils.requireNonNull(memory);
      UnsafeMemory.unsafeLoadFence();
      return UnsafeMemory.unsafeGetInt(address);
    } else {
      return memory.readVolatileInt(address);
    }
  }

  @NotNull @Override public Bytes<Void> appendUtf8(@NotNull char[] chars, int offset, int length) throws BufferOverflowException, IllegalArgumentException, IllegalStateException {
    requireNonNull(chars);
    throwExceptionIfClosed();
    if (writePosition() < 0 || writePosition() > capacity() - 1L + length) {
      throw writeBufferOverflowException0(writePosition());
    }
    int i;
    ascii:
    {
      for (i = 0; i < length; i++) {
        char c = chars[offset + i];
        if (c > 0x007F) {
          break ascii;
        }
        long oldPosition = writePosition();
        BytesStore bytesStore = this.bytesStore;
        if ((writePosition() & 0xff) == 0 && !bytesStore.inside(writePosition(), (length - i) * 3L)) {
          bytesStore = acquireNextByteStore0(writePosition(), false);
        }
        uncheckedWritePosition(writePosition() + 1);
        bytesStore.writeByte(oldPosition, (byte) c);
      }
      return this;
    }
    for ( ; i < length; i++) {
      char c = chars[offset + i];
      BytesInternal.appendUtf8Char(this, c);
    }
    return this;
  }

  @Override public boolean compareAndSwapLong(long offset, long expected, long value) throws BufferOverflowException, IllegalStateException {
    throwExceptionIfClosed();
    if (offset < 0 || offset > mappedFile.capacity() - 8L) {
      throw writeBufferOverflowException0(offset);
    }
    BytesStore bytesStore = this.bytesStore;
    if (bytesStore.start() > offset || offset + 8L > bytesStore.safeLimit()) {
      bytesStore = acquireNextByteStore0(offset, false);
    }
    return bytesStore.compareAndSwapLong(offset, expected, value);
  }
}