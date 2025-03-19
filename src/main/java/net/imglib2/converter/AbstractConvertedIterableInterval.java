package net.imglib2.converter;
import java.util.Iterator;
import net.imglib2.AbstractWrappedInterval;
import net.imglib2.IterableInterval;

/**
 * TODO
 * 
 */
abstract public class AbstractConvertedIterableInterval<A extends java.lang.Object, B extends java.lang.Object> extends AbstractWrappedInterval<IterableInterval<A>> implements IterableInterval<B> {
  public AbstractConvertedIterableInterval(final IterableInterval<A> source) {
    super(source);
  }

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