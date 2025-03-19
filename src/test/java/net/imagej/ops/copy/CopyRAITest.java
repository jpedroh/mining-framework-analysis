package net.imagej.ops.copy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.imagej.ops.AbstractOpTest;
import net.imagej.ops.special.hybrid.Hybrids;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.Cursor;
import net.imglib2.FinalDimensions;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import org.junit.Before;
import org.junit.Test;
import org.scijava.util.MersenneTwisterFast;

/**
 * Test {@link CopyRAI}.
 *
 * @author Tim-Oliver Buchholz (University of Konstanz)
 */
public class CopyRAITest extends AbstractOpTest {
  private Img<UnsignedByteType> input;

  Img<UnsignedByteType> input2;

  Img<UnsignedByteType> inputPlanar;

  IntervalView<UnsignedByteType> view;

  IntervalView<UnsignedByteType> viewPlanar;

  int[] size1 = new int[] { 64, 64, 64 };

  int[] size2 = new int[] { 32, 32, 32 };

  double delta = 0.0000001;

  @Before public void createData() {
    input = new ArrayImgFactory<UnsignedByteType>().create(new int[] { 120, 100 }, new UnsignedByteType());
    final MersenneTwisterFast r = new MersenneTwisterFast(System.currentTimeMillis());
    final Cursor<UnsignedByteType> inc = input.cursor();
    while (inc.hasNext()) {
      inc.next().setReal(r.nextDouble() * 255);
    }
    final long[] start = new long[] { 16, 16, 16 };
    final long[] end = new long[] { 47, 47, 47 };
    input2 = ops.create().img(new FinalDimensions(size1), new UnsignedByteType());
    inputPlanar = ops.create().img(new FinalDimensions(size1), new UnsignedByteType(), new PlanarImgFactory<UnsignedByteType>());
    view = Views.interval(input2, new FinalInterval(start, end));
    viewPlanar = Views.interval(inputPlanar, new FinalInterval(start, end));
    final Cursor<UnsignedByteType> cursor = view.cursor();
    final Cursor<UnsignedByteType> cursorPlanar = viewPlanar.cursor();
    while (cursor.hasNext()) {
      cursor.fwd();
      cursorPlanar.fwd();
      cursor.get().setReal(100.0);
      cursorPlanar.get().setReal(100.0);
    }
  }

  @Test public void copyRAINoOutputTest() {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<UnsignedByteType> output = (RandomAccessibleInterval<UnsignedByteType>) ops.run(CopyRAI.class, input);
    final Cursor<UnsignedByteType> inc = input.localizingCursor();
    final RandomAccess<UnsignedByteType> outRA = output.randomAccess();
    while (inc.hasNext()) {
      inc.fwd();
      outRA.setPosition(inc);
      assertEquals(inc.get().get(), outRA.get().get());
    }
  }

  @Test public void copyRAIWithOutputTest() {
    final Img<UnsignedByteType> output = input.factory().create(input, input.firstElement());
    ops.run(CopyRAI.class, output, input);
    final Cursor<UnsignedByteType> inc = input.cursor();
    final Cursor<UnsignedByteType> outc = output.cursor();
    while (inc.hasNext()) {
      assertEquals(inc.next().get(), outc.next().get());
    }
  }

  @Test @SuppressWarnings(value = { "unchecked", "rawtypes" }) public void copyRAIDifferentSizeTest() {
    final UnaryHybridCF<IntervalView<UnsignedByteType>, RandomAccessibleInterval<UnsignedByteType>> copy = (UnaryHybridCF) Hybrids.unaryCF(ops, CopyRAI.class, RandomAccessibleInterval.class, IntervalView.class);
    assertNotNull(copy);
    final Img<UnsignedByteType> out = ops.create().img(new FinalDimensions(size2), new UnsignedByteType());
    copy.compute(view, out);
    assertEquals(ops.stats().mean(out).getRealDouble(), 100.0, delta);
    final Img<UnsignedByteType> outFromPlanar = ops.create().img(new FinalDimensions(size2), new UnsignedByteType());
    copy.compute(viewPlanar, outFromPlanar);
    assertEquals(ops.stats().mean(outFromPlanar).getRealDouble(), 100.0, delta);
  }

  @Test public void copyBooleanTypesTest() {
    final Img<NativeBoolType> in = ops.create().img(new FinalDimensions(size2), new NativeBoolType());
    Cursor<NativeBoolType> cursor = in.cursor();
    while (cursor.hasNext()) {
      cursor.next().set(true);
    }
    final Img<BitType> out = ops.create().img(new FinalDimensions(size2), new BitType());
    ops.run("copy.rai", out, in);
    assertTrue(out.firstElement().get());
  }
}