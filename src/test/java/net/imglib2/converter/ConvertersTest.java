package net.imglib2.converter;
import java.util.Random;
import org.junit.Assert;
import net.imglib2.img.Img;
import org.junit.Test;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

/**
 *
 *
 * @author Stephan Saalfeld
 */
public class ConvertersTest {
  final Random rnd = new Random();

  final static byte[] testValues = new byte[20 * 30 * 4];

  {
    rnd.nextBytes(testValues);
  }

  final static int[] data = new int[20 * 30];

  {
    for (int i = 0; i < data.length; ++i) {
      data[i] = rnd.nextInt();
    }
  }

  @Test public void testArgbChannels() {
    final ArrayImg<ARGBType, ?> test = ArrayImgs.argbs(data, 20, 30);
    final ArrayImg<ARGBType, ?> source = ArrayImgs.argbs(data.clone(), 20, 30);
    final RandomAccessibleInterval<UnsignedByteType> composite = Converters.argbChannels(source);
    final Cursor<UnsignedByteType> compositeCursorA = Views.flatIterable(Views.hyperSlice(composite, 2, 0)).cursor();
    final Cursor<UnsignedByteType> compositeCursorR = Views.flatIterable(Views.hyperSlice(composite, 2, 1)).cursor();
    final Cursor<UnsignedByteType> compositeCursorG = Views.flatIterable(Views.hyperSlice(composite, 2, 2)).cursor();
    final Cursor<UnsignedByteType> compositeCursorB = Views.flatIterable(Views.hyperSlice(composite, 2, 3)).cursor();
    final Cursor<ARGBType> testCursor = test.cursor();
    final Cursor<ARGBType> sourceCursor = source.cursor();
    int i = 0;
    while (compositeCursorA.hasNext()) {
      final ARGBType argbSource = sourceCursor.next();
      final ARGBType argbTest = testCursor.next();
      final UnsignedByteType a = compositeCursorA.next();
      final UnsignedByteType r = compositeCursorR.next();
      final UnsignedByteType g = compositeCursorG.next();
      final UnsignedByteType b = compositeCursorB.next();
      Assert.assertTrue(argbSource.valueEquals(argbTest));
      Assert.assertEquals(ARGBType.alpha(argbTest.get()), a.get());
      Assert.assertEquals(ARGBType.red(argbTest.get()), r.get());
      Assert.assertEquals(ARGBType.green(argbTest.get()), g.get());
      Assert.assertEquals(ARGBType.blue(argbTest.get()), b.get());
      a.set(testValues[i] & 0xff);
      Assert.assertEquals(ARGBType.alpha(argbSource.get()), testValues[i] & 0xff);
      Assert.assertEquals(a.get(), testValues[i] & 0xff);
      ++i;
      r.set(testValues[i] & 0xff);
      Assert.assertEquals(ARGBType.red(argbSource.get()), testValues[i] & 0xff);
      Assert.assertEquals(r.get(), testValues[i] & 0xff);
      ++i;
      g.set(testValues[i] & 0xff);
      Assert.assertEquals(ARGBType.green(argbSource.get()), testValues[i] & 0xff);
      Assert.assertEquals(g.get(), testValues[i] & 0xff);
      ++i;
      b.set(testValues[i] & 0xff);
      Assert.assertEquals(ARGBType.blue(argbSource.get()), testValues[i] & 0xff);
      Assert.assertEquals(b.get(), testValues[i] & 0xff);
      ++i;
    }
  }

  @Test public void testMergeARBGReading() {
    final Img<UnsignedByteType> image = ArrayImgs.unsignedBytes(new byte[] { 1, 2, 3, 4 }, 4);
    final RandomAccessibleInterval<ARGBType> argb = Converters.mergeARGB(image, ColorChannelOrder.ARGB);
    Assert.assertEquals(0x01020304, argb.randomAccess().get().get());
  }

  @Test public void testMergeARBGWriting() {
    final byte[] pixels = new byte[4];
    final Img<UnsignedByteType> image = ArrayImgs.unsignedBytes(pixels, 4);
    final RandomAccessibleInterval<ARGBType> arbg = Converters.mergeARGB(image, ColorChannelOrder.ARGB);
    arbg.randomAccess().get().set(new ARGBType(0x01020304));
    Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, pixels);
  }

  @Test public void testMergeRGBReading() {
    final Img<UnsignedByteType> image = ArrayImgs.unsignedBytes(new byte[] { 1, 2, 3 }, 4);
    final RandomAccessibleInterval<ARGBType> argb = Converters.mergeARGB(image, ColorChannelOrder.RGB);
    Assert.assertEquals(0xff010203, argb.randomAccess().get().get());
  }

  @Test public void testMergbeRGBWriting() {
    final byte[] pixels = new byte[3];
    final Img<UnsignedByteType> image = ArrayImgs.unsignedBytes(pixels, 3);
    final RandomAccessibleInterval<ARGBType> argb = Converters.mergeARGB(image, ColorChannelOrder.RGB);
    argb.randomAccess().get().set(new ARGBType(0x00010203));
    Assert.assertArrayEquals(new byte[] { 1, 2, 3 }, pixels);
  }
}