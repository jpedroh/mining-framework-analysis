package net.imglib2.type.numeric.integer;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.Type;
import net.imglib2.util.Fraction;

/**
 * A 12-bit {@link Type} whose data is stored in a {@link LongAccess}.
 *
 * @author Albert Cardona
 * @author Stephan Preibisch
 *
 */
public class Unsigned12BitType extends AbstractIntegerBitType<Unsigned12BitType> {
  private final long mask;

  public Unsigned12BitType(final NativeImg<?, ? extends LongAccess> bitStorage) {
    super(bitStorage, 12);
    this.mask = 4095;
  }

  public Unsigned12BitType(final long value) {
    this((NativeImg<?, ? extends LongAccess>) null);
    dataAccess = new LongArray(1);
    set(value);
  }

  public Unsigned12BitType(final LongAccess access) {
    this((NativeImg<?, ? extends LongAccess>) null);
    dataAccess = access;
  }

  public Unsigned12BitType() {
    this(0);
  }

  @Override public NativeImg<Unsigned12BitType, ? extends LongAccess> createSuitableNativeImg(final NativeImgFactory<Unsigned12BitType> storageFactory, final long[] dim) {
    final NativeImg<Unsigned12BitType, ? extends LongAccess> container = storageFactory.createLongInstance(dim, new Fraction(getBitsPerPixel(), 64));
    final Unsigned12BitType linkedType = new Unsigned12BitType(container);
    container.setLinkedType(linkedType);
    return container;
  }

  @Override public Unsigned12BitType duplicateTypeOnSameNativeImg() {
    return new Unsigned12BitType(img);
  }

  @Override public long get() {
    final long k = i * 12;
    final int i1 = (int) (k >>> 6);
    final long shift = k & 63;
    final long v = dataAccess.getValue(i1);
    final long antiShift = 64 - shift;
    if (antiShift < 12) {
      final long v1 = (v >>> shift) & (mask >>> (12 - antiShift));
      final long v2 = (dataAccess.getValue(i1 + 1) & (mask >>> antiShift)) << antiShift;
      return v1 | v2;
    } else {
      return (v >>> shift) & mask;
    }
  }

  @Override public void set(final long value) {
    final long k = i * 12;
    final int i1 = (int) (k >>> 6);
    final long shift = k & 63;
    final long safeValue = value & mask;
    final long antiShift = 64 - shift;
    synchronized (dataAccess) {
      final long v = dataAccess.getValue(i1);
      if (antiShift < 12) {
        final long v1 = (v & (0xffffffffffffffffL >>> antiShift)) | ((safeValue & (mask >>> (12 - antiShift))) << shift);
        dataAccess.setValue(i1, v1);
        final long v2 = (dataAccess.getValue(i1 + 1) & (0xffffffffffffffffL << (12 - antiShift))) | (safeValue >>> antiShift);
        dataAccess.setValue(i1 + 1, v2);
      } else {
        if (0 == v) {
          dataAccess.setValue(i1, safeValue << shift);
        } else {
          dataAccess.setValue(i1, (v & ~(mask << shift)) | (safeValue << shift));
        }
      }
    }
  }

  @Override public Unsigned12BitType createVariable() {
    return new Unsigned12BitType(0);
  }

  @Override public Unsigned12BitType copy() {
    return new Unsigned12BitType(get());
  }
}