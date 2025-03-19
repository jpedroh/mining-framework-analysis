package net.imglib2.img;
import java.util.Iterator;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class AbstractImg<T extends java.lang.Object> implements Img<T> {
  final protected int n;

  protected long numPixels;

  protected final long[] dimension;

  protected final long[] max;

  public AbstractImg(final long[] size) {
    this.n = size.length;
    this.numPixels = numElements(size);
    this.dimension = size.clone();
    max = new long[size.length];
    for (int i = 0; i < size.length; ++i) {
      max[i] = size[i] - 1;
    }
  }

  @Override public Iterator<T> iterator() {
    return cursor();
  }

  @Override public T firstElement() {
    return cursor().next();
  }

  public static long numElements(final long[] dim) {
    long numPixels = 1;
    for (int i = 0; i < dim.length; ++i) {
      numPixels *= dim[i];
    }
    return numPixels;
  }

  @Override public int numDimensions() {
    return n;
  }

  @Override public void dimensions(final long[] s) {
    for (int i = 0; i < n; ++i) {
      s[i] = dimension[i];
    }
  }

  @Override public long dimension(final int d) {
    try {
      return this.dimension[d];
    } catch (final ArrayIndexOutOfBoundsException e) {
      return 1;
    }
  }

  @Override public long size() {
    return numPixels;
  }

  @Override public String toString() {
    String className = this.getClass().getCanonicalName();
    className = className.substring(className.lastIndexOf(".") + 1, className.length());
    String description = className + " [" + dimension[0];
    for (int i = 1; i < n; ++i) {
      description += "x" + dimension[i];
    }
    description += "]";
    return description;
  }

  @Override public double realMax(final int d) {
    return max[d];
  }

  @Override public void realMax(final double[] m) {
    for (int d = 0; d < n; ++d) {
      m[d] = max[d];
    }
  }

  @Override public void realMax(final RealPositionable m) {
    m.setPosition(max);
  }

  @Override public double realMin(final int d) {
    return 0;
  }

  @Override public void realMin(final double[] m) {
    for (int d = 0; d < n; ++d) {
      m[d] = 0;
    }
  }

  @Override public void realMin(final RealPositionable m) {
    for (int d = 0; d < n; ++d) {
      m.setPosition(0, d);
    }
  }

  @Override public long max(final int d) {
    return max[d];
  }

  @Override public void max(final long[] m) {
    for (int d = 0; d < n; ++d) {
      m[d] = max[d];
    }
  }

  @Override public void max(final Positionable m) {
    m.setPosition(max);
  }

  @Override public void min(final long[] m) {
    for (int d = 0; d < n; ++d) {
      m[d] = 0;
    }
  }

  @Override public long min(final int d) {
    return 0;
  }

  @Override public void min(final Positionable m) {
    for (int d = 0; d < n; ++d) {
      m.setPosition(0, d);
    }
  }

  @Override public RandomAccess<T> randomAccess(final Interval interval) {
    return randomAccess();
  }
}