package net.imglib2.view.iteration;
import java.util.Arrays;
import java.util.Iterator;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.transform.integer.BoundingBox;
import net.imglib2.transform.integer.SlicingTransform;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.TransformBuilder;
import net.imglib2.view.Views;

/**
 * Simplifies View cascades to provide the most efficient {@link Cursor}.
 * 
 * @see #getEfficientIterableInterval(Interval, RandomAccessible)
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class IterableTransformBuilder<T extends java.lang.Object> extends TransformBuilder<T> {
  /**
	 * Create an {@link IterableInterval} that iterates an {@link Interval} of a
	 * {@link RandomAccessible}. If possible, this should return an optimized
	 * cursor. If not, falls back to creating an
	 * {@link IterableRandomAccessibleInterval}.
	 * 
	 * @param interval
	 *            the interval of {@code randomAccessible} which should be
	 *            iterated.
	 * @param randomAccessible
	 *            the {@link RandomAccessible} that should be iterated.
	 * @return an {@link IterableInterval} that iterates {@code interval} of
	 *         {@code randomAccessible}.
	 */
  public static <S extends java.lang.Object> IterableInterval<S> getEfficientIterableInterval(final Interval interval, final RandomAccessible<S> randomAccessible) {
    return new IterableTransformBuilder<S>(interval, randomAccessible).buildIterableInterval();
  }

  /**
	 * The interval which should be iterated.
	 * 
	 * <p>
	 * Currently, no transformations are done on this, because the cases where
	 * an optimized {@link IterableInterval} can be returned do not allow for
	 * any transformation except a single slicing. In the future, it may become
	 * necessary, to propagated the interval through the transforms down the
	 * view hierarchy.
	 */
  protected Interval interval;

  /**
	 * Create a new IterableTransformBuilder. This calls the the super
	 * constructor to gather and simplify transformations.
	 * 
	 * @param interval
	 *            the interval of {@code randomAccessible} which should be
	 *            iterated.
	 * @param randomAccessible
	 *            the {@link RandomAccessible} that should be iterated.
	 */
  public IterableTransformBuilder(final Interval interval, final RandomAccessible<T> randomAccessible) {
    super(interval, randomAccessible);
    this.interval = interval;
  }

  private class SubInterval extends AbstractWrappedInterval<Interval> implements IterableInterval<T> {
    final long numElements;

    final SubIntervalIterable<T> iterableSource;

    public SubInterval(final SubIntervalIterable<T> iterableSource) {
      super(interval);
      numElements = Intervals.numElements(interval);
      this.iterableSource = iterableSource;
    }

    @Override public long size() {
      return numElements;
    }

    @Override public T firstElement() {
      return cursor().next();
    }

    @Override public Object iterationOrder() {
      return iterableSource.subIntervalIterationOrder(interval);
    }

    @Override public Iterator<T> iterator() {
      return cursor();
    }

    @Override public Cursor<T> cursor() {
      return iterableSource.cursor(interval);
    }

    @Override public Cursor<T> localizingCursor() {
      return iterableSource.localizingCursor(interval);
    }
  }

  private class Slice extends AbstractWrappedInterval<Interval> implements IterableInterval<T> {
    final long numElements;

    final SubIntervalIterable<T> iterableSource;

    final Interval sourceInterval;

    final SlicingTransform transformToSource;

    final boolean hasFlatIterationOrder;

    public Slice(final SubIntervalIterable<T> iterableSource, final Interval sourceInterval, final SlicingTransform transformToSource, final boolean hasFlatIterationOrder) {
      super(interval);
      numElements = Intervals.numElements(interval);
      this.iterableSource = iterableSource;
      this.sourceInterval = sourceInterval;
      this.transformToSource = transformToSource;
      this.hasFlatIterationOrder = hasFlatIterationOrder;
    }

    @Override public long size() {
      return numElements;
    }

    @Override public T firstElement() {
      return cursor().next();
    }

    @Override public Object iterationOrder() {
      return hasFlatIterationOrder ? new FlatIterationOrder(interval) : this;
    }

    @Override public Iterator<T> iterator() {
      return cursor();
    }

    @Override public Cursor<T> cursor() {
      return new SlicingCursor<T>(iterableSource.cursor(sourceInterval), transformToSource);
    }

    @Override public Cursor<T> localizingCursor() {
      return new SlicingCursor<T>(iterableSource.localizingCursor(sourceInterval), transformToSource);
    }
  }

  /**
	 * Create an {@link IterableInterval} on the {@link Interval} specified in
	 * the constructor of the {@link RandomAccessible} specified in the
	 * constructor.
	 */
  public IterableInterval<T> buildIterableInterval() {
    if (boundingBox != null && SubIntervalIterable.class.isInstance(source)) {
      @SuppressWarnings(value = { "unchecked" }) final SubIntervalIterable<T> iterableSource = (SubIntervalIterable<T>) source;
      if (transforms.isEmpty()) {
        if (iterableSource.supportsOptimizedCursor(interval)) {
          return new SubInterval(iterableSource);
        }
      } else {
        if (transforms.size() == 1 && SlicingTransform.class.isInstance(transforms.get(0))) {
          final SlicingTransform t = (SlicingTransform) transforms.get(0);
          final int m = t.numTargetDimensions();
          final int n = t.numSourceDimensions();
          boolean optimizable = true;
          int firstZeroDim = 0;
          for ( ; firstZeroDim < m && !t.getComponentZero(firstZeroDim); ++firstZeroDim) {
            ;
          }
          for (int d = firstZeroDim + 1; d < m && optimizable; ++d) {
            if (!t.getComponentZero(d)) {
              optimizable = false;
            }
          }
          final int[] sourceComponent = new int[n];
          if (optimizable) {
            Arrays.fill(sourceComponent, -1);
            for (int d = 0; d < m; ++d) {
              if (!t.getComponentZero(d)) {
                sourceComponent[t.getComponentMapping(d)] = d;
              }
            }
            for (int d = 0; d < n && optimizable; ++d) {
              if (sourceComponent[d] < 0) {
                optimizable = false;
              }
            }
          }
          if (optimizable) {
            final Interval sliceInterval = t.transform(new BoundingBox(interval)).getInterval();
            if (iterableSource.supportsOptimizedCursor(sliceInterval)) {
              boolean flat = FlatIterationOrder.class.isInstance(iterableSource.subIntervalIterationOrder(sliceInterval));
              for (int d = 0; d < n - 1; ++d) {
                if (sourceComponent[d + 1] <= sourceComponent[d]) {
                  flat = false;
                }
              }
              return new Slice(iterableSource, sliceInterval, t, flat);
            }
          }
        }
      }
    }
    return new IterableRandomAccessibleInterval<T>(Views.interval(build(), interval));
  }
}