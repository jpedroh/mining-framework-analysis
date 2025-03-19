package net.imglib2.type.volatiles;
import net.imglib2.Volatile;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * Abstract base class for {@link VolatileNumericType}s that wrap a
 * {@link NumericType} that is either VALID or INVALID.
 * 
 * @param <N>
 *            wrapped {@link NumericType}.
 * @param <T>
 *            type of derived concrete class.
 * 
 * @author Stephan Saalfeld
 */
abstract public class AbstractVolatileNumericType<N extends NumericType<N>, T extends AbstractVolatileNumericType<N, T>> extends Volatile<N> implements NumericType<T> {
  public AbstractVolatileNumericType(final N t, final boolean valid) {
    super(t, valid);
  }

  public AbstractVolatileNumericType(final N t) {
    this(t, false);
  }

  @Override public void set(final T c) {
    t.set(c.t);
    valid = c.valid;
  }

  @Override public void add(final T c) {
    t.add(c.t);
    valid &= c.valid;
  }

  @Override public void sub(final T c) {
    t.sub(c.t);
    valid &= c.valid;
  }

  @Override public void mul(final T c) {
    t.mul(c.t);
    valid &= c.valid;
  }

  @Override public void div(final T c) {
    t.div(c.t);
    valid &= c.valid;
  }

  @Override public void pow(final T c) {
    t.pow(c.t);
    valid &= c.valid;
  }

  @Override public void pow(final double power) {
    t.pow(power);
  }

  @Override public void setZero() {
    t.setZero();
  }

  @Override public void setOne() {
    t.setOne();
  }

  @Override public void mul(final float c) {
    t.mul(c);
  }

  @Override public void mul(final double c) {
    t.mul(c);
  }

  @Override public boolean valueEquals(T other) {
    return isValid() == other.isValid() && t.valueEquals(other.t);
  }

  @Override public boolean equals(final Object obj) {
    if (!getClass().isInstance(obj)) {
      return false;
    }
    @SuppressWarnings(value = { "unchecked" }) T t = (T) obj;
    return AbstractVolatileNumericType.this.valueEquals(t);
  }

  @Override public int hashCode() {
    return Util.combineHash(Boolean.hashCode(isValid()), t.hashCode());
  }
}