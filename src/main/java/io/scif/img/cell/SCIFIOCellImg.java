package io.scif.img.cell;
import io.scif.FormatException;
import io.scif.Reader;
import io.scif.img.cell.loaders.SCIFIOArrayLoader;
import java.io.IOException;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.cache.Cache;
import net.imglib2.cache.IoSync;
import net.imglib2.cache.img.CachedCellImg;
import net.imglib2.display.ColorTable;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import org.scijava.Disposable;

/**
 * {@link CachedCellImg} implementation backed by a SCIFIO {@link Reader}.
 *
 * @author Mark Hiner
 * @author Tobias Pietzsch
 */
public class SCIFIOCellImg<T extends NativeType<T>, A extends java.lang.Object> extends CachedCellImg<T, A> implements Disposable {
  private final Reader reader;

  private SCIFIOArrayLoader<?> loader;

  private final SCIFIOCellImgFactory<T> factory;

  private final IoSync iosync;

  public SCIFIOCellImg(final SCIFIOCellImgFactory<T> factory, final CellGrid grid, final Fraction entitiesPerPixel, final Cache<Long, Cell<A>> cache, final A accessType, final IoSync iosync) {
    super(grid, entitiesPerPixel, cache, accessType);
    this.factory = factory;
    reader = factory.reader();
    this.iosync = iosync;
  }

  /**
	 * Returns the ColorTable of the specified image and plane index.
	 * <p>
	 * NB: opens the underlying image.
	 * </p>
	 */
  public ColorTable getColorTable(final int imageIndex, final int planeIndex) throws FormatException, IOException {
    if (loader != null) {
      return loader.loadTable(imageIndex, planeIndex);
    }
    final int planarAxisCount = reader.getMetadata().get(imageIndex).getAxesPlanar().size();
    final long[] dims = new long[planarAxisCount];
    for (int i = 0; i < dims.length; i++) {
      dims[i] = 1;
    }
    final Interval bounds = new FinalInterval(dims);
    return reader.openPlane(imageIndex, planeIndex, bounds).getColorTable();
  }

  @Override public SCIFIOCellImgFactory<T> factory() {
    return factory;
  }

  public Reader reader() {
    return reader;
  }

  public void setLoader(final SCIFIOArrayLoader<?> loader) {
    this.loader = loader;
  }

  @Override public SCIFIOCellImg<T, A> copy() {
    @SuppressWarnings(value = { "unchecked" }) final SCIFIOCellImg<T, A> copy = (SCIFIOCellImg<T, A>) factory().create(dimension);
    super.copyDataTo(copy);
    return copy;
  }

  @Override public void dispose() {
    iosync.shutdown();
    try {
      reader.close();
    } catch (final IOException e) {
    }
  }
}