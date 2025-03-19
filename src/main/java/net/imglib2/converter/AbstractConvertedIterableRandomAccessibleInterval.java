package net.imglib2.converter;
import java.util.Iterator;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;

/**
 * TODO
 * 
 */
abstract public class AbstractConvertedIterableRandomAccessibleInterval<A extends java.lang.Object, B extends java.lang.Object, S extends RandomAccessible<A> & IterableInterval<A>> extends AbstractWrappedInterval<S> implements IterableInterval<B>, RandomAccessibleInterval<B> {
  public AbstractConvertedIterableRandomAccessibleInterval(final S source) {
    super(source);
  }

  @Override abstract public AbstractConvertedRandomAccess<A, B> randomAccess();

  @Override abstract public AbstractConvertedRandomAccess<A, B> randomAccess(final Interval interval);

  @Override public long size() {
    return sourceInterval.size();
  }

  @Override public Object iterationOrder() {
    return sourceInterval.iterationOrder();
  }

  @Override public Iterator<B> iterator() {
    return cursor();
  }

  @Override public B firstElement() {
    return cursor().next();
  }

  @Override abstract public AbstractConvertedCursor<A, B> cursor();

  @Override abstract public AbstractConvertedCursor<A, B> localizingCursor();
}