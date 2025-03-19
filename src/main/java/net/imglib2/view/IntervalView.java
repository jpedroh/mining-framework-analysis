package net.imglib2.view;
import java.util.Iterator;
import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.iteration.IterableTransformBuilder;

/**
 * IntervalView is a view that puts {@link Interval} boundaries on its source
 * {@link RandomAccessible}. IntervalView uses {@link TransformBuilder} to
 * create efficient {@link RandomAccess accessors}. Usually an IntervalView is
 * created through the {@link Views#interval(RandomAccessible, Interval)} method
 * instead.
 */
public class IntervalView<T extends java.lang.Object> extends AbstractInterval implements RandomAccessibleInterval<T>, IterableInterval<T> {
  /**
	 * The source {@link RandomAccessible}.
	 */
  protected final RandomAccessible<T> source;

  /**
	 * TODO Javadoc
	 */
  protected RandomAccessible<T> fullViewRandomAccessible;

  /**
	 * TODO Javadoc
	 */
  protected IterableInterval<T> fullViewIterableInterval;

  /**
	 * Create a view that defines an interval on a source. It is the callers
	 * responsibility to ensure that the source is defined in the specified
	 * interval.
	 * 
	 * @see Views#interval(RandomAccessible, Interval)
	 */
  public IntervalView(final RandomAccessible<T> source, final Interval interval) {
    super(interval);
    assert (source.numDimensions() == interval.numDimensions());
    this.source = source;
    this.fullViewRandomAccessible = null;
  }

  /**
	 * Create a view that defines an interval on a source. It is the callers
	 * responsibility to ensure that the source is defined in the specified
	 * interval.
	 * 
	 * @see Views#interval(RandomAccessible, Interval)
	 * 
	 * @param min
	 *            minimum coordinate of the interval.
	 * @param max
	 *            maximum coordinate of the interval.
	 */
  public IntervalView(final RandomAccessible<T> source, final long[] min, final long[] max) {
    super(min, max);
    assert (source.numDimensions() == min.length);
    this.source = source;
    this.fullViewRandomAccessible = null;
  }

  /**
	 * Gets the underlying source {@link RandomAccessible}.
	 * 
	 * @return the source {@link RandomAccessible}.
	 */
  public RandomAccessible<T> getSource() {
    return source;
  }

  @Override public RandomAccess<T> randomAccess(final Interval interval) {
    return TransformBuilder.getEfficientRandomAccessible(interval, this).randomAccess();
  }

  @Override public RandomAccess<T> randomAccess() {
    if (fullViewRandomAccessible == null) {
      fullViewRandomAccessible = TransformBuilder.getEfficientRandomAccessible(this, this);
    }
    return fullViewRandomAccessible.randomAccess();
  }

  protected IterableInterval<T> getFullViewIterableInterval() {
    if (fullViewIterableInterval == null) {
      fullViewIterableInterval = IterableTransformBuilder.getEfficientIterableInterval(this, this);
    }
    return fullViewIterableInterval;
  }

  @Override public long size() {
    return getFullViewIterableInterval().size();
  }

  @Override public T firstElement() {
    return getFullViewIterableInterval().firstElement();
  }

  @Override public Object iterationOrder() {
    return getFullViewIterableInterval().iterationOrder();
  }

  @Override public Iterator<T> iterator() {
    return getFullViewIterableInterval().iterator();
  }

  @Override public Cursor<T> cursor() {
    return getFullViewIterableInterval().cursor();
  }

  @Override public Cursor<T> localizingCursor() {
    return getFullViewIterableInterval().localizingCursor();
  }
}