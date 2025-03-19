package net.imglib2.img;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SubIntervalIterable;

/**
 * Allows a {@link RandomAccessibleInterval} to be treated as an {@link Img}.
 * 
 * @author Tobias Pietzsch
 * @author Christian Dietz (dietzc85@googlemail.com)
 */
public class ImgView<T extends Type<T>> extends IterableRandomAccessibleInterval<T> implements Img<T>, SubIntervalIterable<T> {
  private final ImgFactory<T> factory;

  private final IterableInterval<T> ii;

  /**
	 * View on {@link Img} which is defined by a given Interval, but still is an
	 * {@link Img}.
	 * 
	 * @param in
	 *            Source interval for the view
	 * @param fac
	 *            <T> Factory to create img
	 */
  @Deprecated public ImgView(final RandomAccessibleInterval<T> in, final ImgFactory<T> fac) {
    super(in);
    factory = fac;
    ii = Views.flatIterable(in);
  }

  @Override public ImgFactory<T> factory() {
    return factory;
  }

  @Override public Img<T> copy() {
    final Img<T> copy = factory.create(this, randomAccess().get().createVariable());
    final Cursor<T> srcCursor = localizingCursor();
    final RandomAccess<T> resAccess = copy.randomAccess();
    while (srcCursor.hasNext()) {
      srcCursor.fwd();
      resAccess.setPosition(srcCursor);
      resAccess.get().set(srcCursor.get());
    }
    return copy;
  }

  @Override public Cursor<T> cursor() {
    return ii.cursor();
  }

  @Override public Cursor<T> localizingCursor() {
    return ii.localizingCursor();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public boolean supportsOptimizedCursor(final Interval interval) {
    if (this.sourceInterval instanceof SubIntervalIterable) {
      return ((SubIntervalIterable<T>) this.sourceInterval).supportsOptimizedCursor(interval);
    } else {
      return false;
    }
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Object subIntervalIterationOrder(final Interval interval) {
    if (this.sourceInterval instanceof SubIntervalIterable) {
      return ((SubIntervalIterable<T>) this.sourceInterval).subIntervalIterationOrder(interval);
    } else {
      return new FlatIterationOrder(interval);
    }
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Cursor<T> cursor(final Interval interval) {
    if (this.sourceInterval instanceof SubIntervalIterable) {
      return ((SubIntervalIterable<T>) this.sourceInterval).cursor(interval);
    } else {
      return Views.interval(this.sourceInterval, interval).cursor();
    }
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public Cursor<T> localizingCursor(final Interval interval) {
    if (this.sourceInterval instanceof SubIntervalIterable) {
      return ((SubIntervalIterable<T>) this.sourceInterval).localizingCursor(interval);
    } else {
      return Views.interval(this.sourceInterval, interval).localizingCursor();
    }
  }

  /**
	 * Represent an arbitrary RandomAccessibleInterval as an Img
	 * 
	 * @param accessible 
	 * 			RandomAccessibleInterval which will be wrapped with an ImgView
	 * @param factory
	 * 			ImgFactory returned by {@link ImgView#factory()}
	 * @return
	 * 			RandomAccessibleInterval represented as an Img
	 */
  public static <T extends Type<T>> Img<T> wrap(RandomAccessibleInterval<T> accessible, final ImgFactory<T> factory) {
    if (accessible instanceof Img) {
      return (Img<T>) accessible;
    } else {
      return new ImgView<T>(accessible, factory);
    }
  }
}