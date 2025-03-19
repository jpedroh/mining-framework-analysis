package net.imglib2.img.array;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.view.iteration.SubIntervalIterable;

/**
 * This {@link Img} stores an image in a single linear array of basic types. By
 * that, it provides the fastest possible access to data while limiting the
 * number of basic types stored to {@link Integer#MAX_VALUE}. Keep in mind that
 * this does not necessarily reflect the number of pixels, because a pixel can
 * be stored in less than or more than a basic type entry.
 * 
 * @param <T>
 * @param <A>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class ArrayImg<T extends NativeType<T>, A extends java.lang.Object> extends AbstractNativeImg<T, A> implements SubIntervalIterable<T> {
  final int[] steps, dim;

  final private A data;

  /**
	 * TODO check for the size of numPixels being < Integer.MAX_VALUE? TODO Type
	 * is suddenly not necessary anymore
	 * 
	 * @param factory
	 * @param data
	 * @param dim
	 * @param entitiesPerPixel
	 */
  public ArrayImg(final A data, final long[] dim, final Fraction entitiesPerPixel) {
    super(dim, entitiesPerPixel);
    this.dim = new int[n];
    for (int d = 0; d < n; ++d) {
      this.dim[d] = (int) dim[d];
    }
    this.steps = new int[n];
    IntervalIndexer.createAllocationSteps(this.dim, this.steps);
    this.data = data;
  }

  @Override public A update(final Object o) {
    return data;
  }

  @Override public ArrayCursor<T> cursor() {
    return new ArrayCursor<T>(this);
  }

  @Override public ArrayLocalizingCursor<T> localizingCursor() {
    return new ArrayLocalizingCursor<T>(this);
  }

  @Override public ArrayRandomAccess<T> randomAccess() {
    return new ArrayRandomAccess<T>(this);
  }

  @Override public ArrayRandomAccess<T> randomAccess(final Interval interval) {
    return randomAccess();
  }

  @Override public FlatIterationOrder iterationOrder() {
    return new FlatIterationOrder(this);
  }

  @Override public ArrayImgFactory<T> factory() {
    return new ArrayImgFactory<T>();
  }

  @Override public ArrayImg<T, ?> copy() {
    final ArrayImg<T, ?> copy = factory().create(dimension, firstElement().createVariable());
    final ArrayCursor<T> source = this.cursor();
    final ArrayCursor<T> target = copy.cursor();
    while (source.hasNext()) {
      target.next().set(source.next());
    }
    return copy;
  }

  /**
	 * {@inheritDoc}
	 */
  @Override public Cursor<T> cursor(final Interval interval) {
    final int dimLength = fastCursorAvailable(interval);
    assert dimLength > 0;
    return new ArraySubIntervalCursor<T>(this, (int) offset(interval), (int) size(interval, dimLength));
  }

  private long size(final Interval interval, final int length) {
    long size = interval.dimension(0);
    for (int d = 1; d < length; ++d) {
      size *= interval.dimension(d);
    }
    return size;
  }

  private long offset(final Interval interval) {
    final int maxDim = numDimensions() - 1;
    long i = interval.min(maxDim);
    for (int d = maxDim - 1; d >= 0; --d) {
      i = i * dimension(d) + interval.min(d);
    }
    return i;
  }

  /**
	 * If method returns -1 no fast cursor is available, else the amount of dims
	 * (starting from zero) which can be iterated fast are returned.
	 */
  private int fastCursorAvailable(final Interval interval) {
    if (!Intervals.contains(this, interval)) {
      return -1;
    }
    int dimIdx = 0;
    for ( ; dimIdx < n; ++dimIdx) {
      if (interval.dimension(dimIdx) != dimension(dimIdx)) {
        break;
      }
    }
    if (dimIdx == n) {
      return dimIdx;
    }
    ++dimIdx;
    for (int d = dimIdx; d < n; ++d) {
      if (interval.dimension(d) != 1) {
        return -1;
      }
    }
    return dimIdx;
  }

  /**
	 * {@inheritDoc}
	 */
  @Override public Cursor<T> localizingCursor(final Interval interval) {
    final int dimLength = fastCursorAvailable(interval);
    assert dimLength > 0;
    return new ArrayLocalizingSubIntervalCursor<T>(this, (int) offset(interval), (int) size(interval, dimLength));
  }

  /**
	 * {@inheritDoc}
	 */
  @Override public boolean supportsOptimizedCursor(final Interval interval) {
    return fastCursorAvailable(interval) > 0;
  }

  /**
	 * {@inheritDoc}
	 */
  @Override public Object subIntervalIterationOrder(final Interval interval) {
    return new FlatIterationOrder(interval);
  }
}