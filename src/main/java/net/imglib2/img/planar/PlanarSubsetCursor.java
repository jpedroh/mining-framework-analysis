package net.imglib2.img.planar;
import net.imglib2.AbstractCursorInt;
import net.imglib2.Interval;
import net.imglib2.type.NativeType;
import net.imglib2.util.Intervals;

/**
 * Basic Iterator for {@link PlanarImg PlanarContainers}
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Christian Dietz
 */
public class PlanarSubsetCursor<T extends NativeType<T>> extends AbstractCursorInt<T> implements PlanarImg.PlanarContainerSampler {
  /**
	 * Access to the type
	 */
  protected final T type;

  /**
	 * Container
	 */
  protected final PlanarImg<T, ?> container;

  /**
	 * Last index in plane
	 */
  protected final int lastIndexPlane;

  /**
	 * Current slice index
	 */
  protected int sliceIndex;

  /**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
  protected int index;

  /**
	 * Total offset in container to interval position
	 */
  protected final int offsetContainer;

  /**
	 * Current index in the container
	 */
  protected int indexContainer;

  /**
	 * Size of one plane
	 */
  protected final int planeSize;

  /**
	 * Last index to be visited in the container
	 */
  protected final int lastIndexContainer;

  protected PlanarSubsetCursor(final PlanarSubsetCursor<T> cursor) {
    super(cursor.numDimensions());
    container = cursor.container;
    this.type = container.createLinkedType();
    lastIndexPlane = cursor.lastIndexPlane;
    sliceIndex = cursor.sliceIndex;
    index = cursor.index;
    offsetContainer = cursor.offsetContainer;
    indexContainer = cursor.indexContainer;
    planeSize = cursor.planeSize;
    lastIndexContainer = cursor.lastIndexContainer;
    type.updateContainer(this);
    type.updateIndex(index);
  }

  public PlanarSubsetCursor(final PlanarImg<T, ?> container, Interval interval) {
    super(container.numDimensions());
    this.type = container.createLinkedType();
    this.container = container;
    this.offsetContainer = (int) offset(interval);
    this.planeSize = ((n > 1) ? (int) container.dimension(1) : 1) * (int) container.dimension(0);
    this.lastIndexPlane = planeSize - 1;
    this.lastIndexContainer = (int) (offsetContainer + Intervals.numElements(interval) - 1);
    reset();
  }

  @Override public int getCurrentSliceIndex() {
    return sliceIndex;
  }

  @Override public T get() {
    return type;
  }

  @Override public PlanarSubsetCursor<T> copy() {
    return new PlanarSubsetCursor<T>(this);
  }

  @Override public PlanarSubsetCursor<T> copyCursor() {
    return copy();
  }

  /**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element
	 */
  @Override public boolean hasNext() {
    return indexContainer < lastIndexContainer;
  }

  @Override public void fwd() {
    indexContainer++;
    if (++index > lastIndexPlane) {
      index = 0;
      ++sliceIndex;
      type.updateContainer(this);
    }
    type.updateIndex(index);
  }

  @Override public void jumpFwd(long steps) {
    long newIndex = index + steps;
    if (newIndex > lastIndexPlane) {
      final long s = newIndex / planeSize;
      newIndex -= s * planeSize;
      sliceIndex += s;
      type.updateContainer(this);
    }
    index = (int) newIndex;
    indexContainer += steps;
    type.updateIndex(index);
  }

  @Override public void reset() {
    sliceIndex = offsetContainer / planeSize;
    index = offsetContainer % planeSize - 1;
    indexContainer = offsetContainer + index;
    type.updateIndex(index);
    type.updateContainer(this);
  }

  @Override public String toString() {
    return type.toString();
  }

  @Override public void localize(final int[] position) {
    container.indexToGlobalPosition(sliceIndex, index, position);
  }

  @Override public int getIntPosition(final int dim) {
    return container.indexToGlobalPosition(sliceIndex, index, dim);
  }

  private long offset(final Interval interval) {
    final int maxDim = numDimensions() - 1;
    long i = interval.min(maxDim);
    for (int d = maxDim - 1; d >= 0; --d) {
      i = i * container.dimension(d) + interval.min(d);
    }
    return i;
  }
}