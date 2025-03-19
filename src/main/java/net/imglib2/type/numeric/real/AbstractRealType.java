package net.imglib2.type.numeric.real;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.AbstractComplexType;

/**
 * TODO
 * 
 */
public abstract class AbstractRealType<T extends AbstractRealType<T>> extends AbstractComplexType<T> implements RealType<T> {
  @Override public float getImaginaryFloat() {
    return 0;
  }

  @Override public double getImaginaryDouble() {
    return 0;
  }

  @Override public void setImaginary(final float complex) {
  }

  @Override public void setImaginary(final double complex) {
  }

  @Override public void inc() {
    setReal(getRealDouble() + 1);
  }

  @Override public void dec() {
    setReal(getRealDouble() - 1);
  }

  @Override public void set(final T c) {
    setReal(c.getRealDouble());
  }

  @Override public void mul(final float c) {
    setReal(getRealDouble() * c);
  }

  @Override public void mul(final double c) {
    setReal(getRealDouble() * c);
  }

  @Override public void add(final T c) {
    setReal(getRealDouble() + c.getRealDouble());
  }

  @Override public void div(final T c) {
    setReal(getRealDouble() / c.getRealDouble());
  }

  @Override public void mul(final T c) {
    setReal(getRealDouble() * c.getRealDouble());
  }

  @Override public void sub(final T c) {
    setReal(getRealDouble() - c.getRealDouble());
  }

  @Override public void pow(final T c) {
    setReal(Math.pow(getRealDouble(), c.getRealDouble()));
  }

  @Override public void pow(final double power) {
    setReal(Math.pow(getRealDouble(), power));
  }

  @Override public void setZero() {
    setReal(0);
  }

  @Override public void setOne() {
    setReal(1);
  }

  @Override public int compareTo(final T other) {
    return Double.compare(getRealDouble(), other.getRealDouble());
  }

  @Override public boolean valueEquals(T other) {
    return DoubleType.equals(getRealDouble(), other.getRealDouble());
  }

  @Override public boolean equals(final Object obj) {
    if (!getClass().isInstance(obj)) {
      return false;
    }
    @SuppressWarnings(value = { "unchecked" }) final T t = (T) obj;
    return AbstractRealType.this.valueEquals(t);
  }

  @Override public int hashCode() {
    return Double.hashCode(getRealDouble());
  }

  @Override public float getPowerFloat() {
    return getRealFloat();
  }

  @Override public double getPowerDouble() {
    return getRealDouble();
  }

  @Override public float getPhaseFloat() {
    return 0;
  }

  @Override public double getPhaseDouble() {
    return 0;
  }

  @Override public String toString() {
    return "" + getRealDouble();
  }
}