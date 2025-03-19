package net.imglib2.type.numeric.integer;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class GenericIntType<T extends GenericIntType<T>> extends AbstractIntegerType<T> implements NativeType<T> {
  int i = 0;

  final protected NativeImg<?, ? extends IntAccess> img;

  protected IntAccess dataAccess;

  public GenericIntType(final NativeImg<?, ? extends IntAccess> intStorage) {
    img = intStorage;
  }

  public GenericIntType(final int value) {
    img = null;
    dataAccess = new IntArray(1);
    setInt(value);
  }

  public GenericIntType(final IntAccess access) {
    img = null;
    dataAccess = access;
  }

  public GenericIntType() {
    this(0);
  }

  @Override public Fraction getEntitiesPerPixel() {
    return new Fraction();
  }

  @Override public void updateContainer(final Object c) {
    dataAccess = img.update(c);
  }

  @Override public abstract NativeTypeFactory<T, IntAccess> getNativeTypeFactory();

  /**
	 * @deprecated Use {@link #getInt()} instead.
	 */
  @Deprecated protected int getValue() {
    return dataAccess.getValue(i);
  }

  /**
	 * @deprecated Use {@link #setInt(int)} instead.
	 */
  @Deprecated protected void setValue(final int f) {
    dataAccess.setValue(i, f);
  }

  /**
	 * Returns the primitive int value that is used to store this type.
	 *
	 * @return primitive int value
	 */
  public int getInt() {
    return dataAccess.getValue(i);
  }

  /**
	 * Sets the primitive int value that is used to store this type.
	 */
  public void setInt(final int f) {
    dataAccess.setValue(i, f);
  }

  @Override public void mul(final float c) {
    final int a = getInt();
    setInt(Util.round(a * c));
  }

  @Override public void mul(final double c) {
    final int a = getInt();
    setInt((int) Util.round(a * c));
  }

  @Override public void add(final T c) {
    final int a = getInt();
    setInt(a + c.getInt());
  }

  @Override public void div(final T c) {
    final int a = getInt();
    setInt(a / c.getInt());
  }

  @Override public void mul(final T c) {
    final int a = getInt();
    setInt(a * c.getInt());
  }

  @Override public void sub(final T c) {
    final int a = getInt();
    setInt(a - c.getInt());
  }

  @Override public void pow(final T c) {
    final int a = getInt();
    setReal(Math.pow(a, c.getInt()));
  }

  @Override public void pow(final double power) {
    final int a = getInt();
    setReal(Math.pow(a, power));
  }

  @Override public void set(final T c) {
    setInt(c.getInt());
  }

  @Override public void setOne() {
    setInt(1);
  }

  @Override public void setZero() {
    setInt(0);
  }

  @Override public void inc() {
    int a = getInt();
    setInt(++a);
  }

  @Override public void dec() {
    int a = getInt();
    setInt(--a);
  }

  @Override public String toString() {
    return "" + getInt();
  }

  @Override public void updateIndex(final int index) {
    i = index;
  }

  @Override public int getIndex() {
    return i;
  }

  @Override public void incIndex() {
    ++i;
  }

  @Override public void incIndex(final int increment) {
    i += increment;
  }

  @Override public void decIndex() {
    --i;
  }

  @Override public void decIndex(final int decrement) {
    i -= decrement;
  }

  @Override public int getBitsPerPixel() {
    return 32;
  }

  @Override public int compareTo(final T other) {
    return Integer.compare(getInt(), other.getInt());
  }

  @Override public boolean valueEquals(final T t) {
    return getInt() == t.getInt();
  }

  @Override public boolean equals(final Object obj) {
    if (!getClass().isInstance(obj)) {
      return false;
    }
    @SuppressWarnings(value = { "unchecked" }) final T t = (T) obj;
    return GenericIntType.this.valueEquals(t);
  }

  @Override public int hashCode() {
    return Integer.hashCode(getInt());
  }
}