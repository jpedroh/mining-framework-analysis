package net.imglib2.util;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.view.Views;

public class Localizables {
  public static long[] asLongArray(final Localizable localizable) {
    final long[] result = new long[localizable.numDimensions()];
    localizable.localize(result);
    return result;
  }

  public static RandomAccessible<Localizable> randomAccessible(final int n) {
    return new LocationRandomAccessible(n);
  }

  public static RandomAccessibleInterval<Localizable> randomAccessibleInterval(final Interval interval) {
    return Views.interval(randomAccessible(interval.numDimensions()), interval);
  }

  /**
	 * Return true if both {@link Localizable} refer to the same position.
	 */
  public static boolean equals(final Localizable a, final Localizable b) {
    final int n = a.numDimensions();
    if (n != b.numDimensions()) {
      return false;
    }
    for (int d = 0; d < n; d++) {
      if (a.getLongPosition(d) != b.getLongPosition(d)) {
        return false;
      }
    }
    return true;
  }

  /**
	 * Return true if the two {@link RealLocalizable}s refer to the same
	 * position.
	 */
  public static boolean equals(final RealLocalizable a, final RealLocalizable b) {
    final int n = a.numDimensions();
    if (n != b.numDimensions()) {
      return false;
    }
    for (int d = 0; d < n; d++) {
      if (a.getDoublePosition(d) != b.getDoublePosition(d)) {
        return false;
      }
    }
    return true;
  }

  /**
	 * Return true if the two {@link RealLocalizable}s refer to the same
	 * position up to a given tolerance.
	 */
  public static boolean equals(final RealLocalizable a, final RealLocalizable b, final double tolerance) {
    final int n = a.numDimensions();
    if (n != b.numDimensions()) {
      return false;
    }
    for (int d = 0; d < n; d++) {
      if (Math.abs(a.getDoublePosition(d) - b.getDoublePosition(d)) > tolerance) {
        return false;
      }
    }
    return true;
  }

  /**
	 * Return the current position as string.
	 */
  public static String toString(final Localizable value) {
    final StringBuilder sb = new StringBuilder();
    char c = '(';
    for (int i = 0; i < value.numDimensions(); i++) {
      sb.append(c);
      sb.append(value.getLongPosition(i));
      c = ',';
    }
    sb.append(")");
    return sb.toString();
  }

  /**
	 * Return the current position as string.
	 */
  public static String toString(final RealLocalizable value) {
    final StringBuilder sb = new StringBuilder();
    char c = '(';
    for (int i = 0; i < value.numDimensions(); i++) {
      sb.append(c);
      sb.append(value.getDoublePosition(i));
      c = ',';
    }
    sb.append(")");
    return sb.toString();
  }

  private static class LocationRandomAccessible extends AbstractEuclideanSpace implements RandomAccessible<Localizable> {
    public LocationRandomAccessible(final int n) {
      super(n);
    }

    @Override public RandomAccess<Localizable> randomAccess() {
      return new LocationRandomAccess(n);
    }

    @Override public RandomAccess<Localizable> randomAccess(final Interval interval) {
      return randomAccess();
    }
  }

  private static class LocationRandomAccess extends Point implements RandomAccess<Localizable> {
    public LocationRandomAccess(final int n) {
      super(n);
    }

    public LocationRandomAccess(final Localizable initialPosition) {
      super(initialPosition);
    }

    @Override public RandomAccess<Localizable> copyRandomAccess() {
      return new LocationRandomAccess(this);
    }

    @Override public Localizable get() {
      return this;
    }

    @Override public Sampler<Localizable> copy() {
      return copyRandomAccess();
    }
  }
}