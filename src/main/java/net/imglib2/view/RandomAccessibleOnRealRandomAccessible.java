package net.imglib2.view;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.View;

/**
 * {@link RandomAccessible} on a {@link RealRandomAccessible}. For optimal
 * performance, no integer coordinates are stored in the {@link RandomAccess}
 * but only method calls passed through to an actual {@link RealRandomAccess}.
 * Therefore, localization into integer fields performs a Math.round operation
 * per field and is thus not very efficient. Localization into real fields,
 * however, is passed through and thus performs optimally.
 * 
 * @author ImgLib2 developers
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RandomAccessibleOnRealRandomAccessible<T extends java.lang.Object> extends AbstractEuclideanSpace implements RandomAccessible<T>, View {
  final protected RealRandomAccessible<T> source;

  final protected class RandomAccessOnRealRandomAccessible implements RandomAccess<T> {
    final protected RealRandomAccess<T> sourceAccess;

    public RandomAccessOnRealRandomAccessible(final RealRandomAccess<T> sourceAccess) {
      this.sourceAccess = sourceAccess;
    }

    @Override public void localize(final int[] position) {
      for (int d = 0; d < n; ++d) {
        position[d] = (int) Math.round(sourceAccess.getDoublePosition(d));
      }
    }

    @Override public void localize(final long[] position) {
      for (int d = 0; d < n; ++d) {
        position[d] = Math.round(sourceAccess.getDoublePosition(d));
      }
    }

    @Override public int getIntPosition(final int d) {
      return (int) Math.round(sourceAccess.getDoublePosition(d));
    }

    @Override public long getLongPosition(final int d) {
      return Math.round(sourceAccess.getDoublePosition(d));
    }

    @Override public void localize(final float[] position) {
      sourceAccess.localize(position);
    }

    @Override public void localize(final double[] position) {
      sourceAccess.localize(position);
    }

    @Override public float getFloatPosition(final int d) {
      return sourceAccess.getFloatPosition(d);
    }

    @Override public double getDoublePosition(final int d) {
      return sourceAccess.getDoublePosition(d);
    }

    @Override public void fwd(final int d) {
      sourceAccess.fwd(d);
    }

    @Override public void bck(final int d) {
      sourceAccess.bck(d);
    }

    @Override public void move(final int distance, final int d) {
      sourceAccess.move(distance, d);
    }

    @Override public void move(final long distance, final int d) {
      sourceAccess.move(distance, d);
    }

    @Override public void move(final Localizable localizable) {
      sourceAccess.move(localizable);
    }

    @Override public void move(final int[] distance) {
      sourceAccess.move(distance);
    }

    @Override public void move(final long[] distance) {
      sourceAccess.move(distance);
    }

    @Override public void setPosition(final Localizable localizable) {
      sourceAccess.setPosition(localizable);
    }

    @Override public void setPosition(final int[] position) {
      sourceAccess.setPosition(position);
    }

    @Override public void setPosition(final long[] position) {
      sourceAccess.setPosition(position);
    }

    @Override public void setPosition(final int position, final int d) {
      sourceAccess.setPosition(position, d);
    }

    @Override public void setPosition(final long position, final int d) {
      sourceAccess.setPosition(position, d);
    }

    @Override public T get() {
      return sourceAccess.get();
    }

    @Override public RandomAccessOnRealRandomAccessible copy() {
      return new RandomAccessOnRealRandomAccessible(sourceAccess.copyRealRandomAccess());
    }

    @Override public RandomAccessOnRealRandomAccessible copyRandomAccess() {
      return copy();
    }

    @Override public int numDimensions() {
      return n;
    }
  }

  public RealRandomAccessible<T> getSource() {
    return source;
  }

  public RandomAccessibleOnRealRandomAccessible(final RealRandomAccessible<T> source) {
    super(source.numDimensions());
    this.source = source;
  }

  @Override public RandomAccess<T> randomAccess() {
    return new RandomAccessOnRealRandomAccessible(source.realRandomAccess());
  }

  @Override public RandomAccess<T> randomAccess(final Interval interval) {
    return new RandomAccessOnRealRandomAccessible(source.realRandomAccess(interval));
  }
}