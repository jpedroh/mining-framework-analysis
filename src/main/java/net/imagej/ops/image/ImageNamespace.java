package net.imagej.ops.image;
import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.ops.Ops;
import net.imagej.ops.image.cooccurrenceMatrix.MatrixOrientation;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.BooleanType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import org.scijava.plugin.Plugin;

/**
 * The image namespace contains operations relating to images.
 *
 * @author Curtis Rueden
 */
@Plugin(type = Namespace.class) public class ImageNamespace extends AbstractNamespace {
  /** Executes the "ascii" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.ascii.DefaultASCII.class) public <T extends RealType<T>> String ascii(final IterableInterval<T> image) {
    final String result = (String) ops().run(net.imagej.ops.Ops.Image.ASCII.class, image);
    return result;
  }

  /** Executes the "ascii" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.ascii.DefaultASCII.class) public <T extends RealType<T>> String ascii(final IterableInterval<T> image, final T min) {
    final String result = (String) ops().run(net.imagej.ops.Ops.Image.ASCII.class, image, min);
    return result;
  }

  /** Executes the "ascii" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.ascii.DefaultASCII.class) public <T extends RealType<T>> String ascii(final IterableInterval<T> image, final T min, final T max) {
    final String result = (String) ops().run(net.imagej.ops.Ops.Image.ASCII.class, image, min, max);
    return result;
  }

  @OpMethod(ops = { net.imagej.ops.image.cooccurrenceMatrix.CooccurrenceMatrix3D.class, net.imagej.ops.image.cooccurrenceMatrix.CooccurrenceMatrix2D.class }) public <T extends RealType<T>> double[][] cooccurrenceMatrix(final IterableInterval<T> in, final int nrGreyLevels, final int distance, final MatrixOrientation orientation) {
    final double[][] result = (double[][]) ops().run(Ops.Image.CooccurrenceMatrix.class, in, nrGreyLevels, distance, orientation);
    return result;
  }

  /** Executes the "distancetransform" operation on the given arguments. */
  @OpMethod(ops = { net.imagej.ops.image.distancetransform.DefaultDistanceTransform.class, net.imagej.ops.image.distancetransform.DistanceTransform2D.class, net.imagej.ops.image.distancetransform.DistanceTransform3D.class }) public <B extends BooleanType<B>, T extends RealType<T>> RandomAccessibleInterval<T> distancetransform(final RandomAccessibleInterval<B> in, final RandomAccessibleInterval<T> out) {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<T> result = (RandomAccessibleInterval<T>) ops().run(Ops.Image.DistanceTransform.class, in, out);
    return result;
  }

  /** Executes the "distancetransform" operation on the given arguments. */
  @OpMethod(ops = { net.imagej.ops.image.distancetransform.DefaultDistanceTransform.class, net.imagej.ops.image.distancetransform.DistanceTransform2D.class, net.imagej.ops.image.distancetransform.DistanceTransform3D.class }) public <B extends BooleanType<B>, T extends RealType<T>> RandomAccessibleInterval<T> distancetransform(final RandomAccessibleInterval<B> in) {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<T> result = (RandomAccessibleInterval<T>) ops().run(Ops.Image.DistanceTransform.class, in);
    return result;
  }

  /** Executes the "distancetransform" operation on the given arguments. */
  @OpMethod(ops = { net.imagej.ops.image.distancetransform.DefaultDistanceTransformCalibration.class, net.imagej.ops.image.distancetransform.DistanceTransform2DCalibration.class, net.imagej.ops.image.distancetransform.DistanceTransform3DCalibration.class }) public <B extends BooleanType<B>, T extends RealType<T>> RandomAccessibleInterval<T> distancetransform(final RandomAccessibleInterval<T> out, final RandomAccessibleInterval<B> in, final double... calibration) {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<T> result = (RandomAccessibleInterval<T>) ops().run(Ops.Image.DistanceTransform.class, out, in, calibration);
    return result;
  }

  /** Executes the "distancetransform" operation on the given arguments. */
  @OpMethod(ops = { net.imagej.ops.image.distancetransform.DefaultDistanceTransformCalibration.class, net.imagej.ops.image.distancetransform.DistanceTransform2DCalibration.class, net.imagej.ops.image.distancetransform.DistanceTransform3DCalibration.class }) public <B extends BooleanType<B>, T extends RealType<T>> RandomAccessibleInterval<T> distancetransform(final RandomAccessibleInterval<B> in, final double... calibration) {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<T> result = (RandomAccessibleInterval<T>) ops().run(Ops.Image.DistanceTransform.class, in, calibration);
    return result;
  }

  /** Executes the "equation" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.equation.DefaultEquation.class) public <T extends RealType<T>> IterableInterval<T> equation(final String in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Equation.class, in);
    return result;
  }

  /** Executes the "equation" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.equation.DefaultEquation.class) public <T extends RealType<T>> IterableInterval<T> equation(final IterableInterval<T> out, final String in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Equation.class, out, in);
    return result;
  }

  /** Executes the "fill" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.fill.DefaultFill.class) public <T extends Type<T>> Iterable<T> fill(final Iterable<T> out, final T in) {
    @SuppressWarnings(value = { "unchecked" }) final Iterable<T> result = (Iterable<T>) ops().run(net.imagej.ops.Ops.Image.Fill.class, out, in);
    return result;
  }

  /** Executes the "histogram" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.histogram.HistogramCreate.class) public <T extends RealType<T>> Histogram1d<T> histogram(final Iterable<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final Histogram1d<T> result = (Histogram1d<T>) ops().run(net.imagej.ops.Ops.Image.Histogram.class, in);
    return result;
  }

  /** Executes the "histogram" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.histogram.HistogramCreate.class) public <T extends RealType<T>> Histogram1d<T> histogram(final Iterable<T> in, final int numBins) {
    @SuppressWarnings(value = { "unchecked" }) final Histogram1d<T> result = (Histogram1d<T>) ops().run(net.imagej.ops.Ops.Image.Histogram.class, in, numBins);
    return result;
  }

  @SuppressWarnings(value = { "unchecked", "rawtypes" }) @OpMethod(op = net.imagej.ops.image.integral.DefaultIntegralImg.class) public <T extends RealType<T>> RandomAccessibleInterval<RealType> integral(final RandomAccessibleInterval<RealType> out, final RandomAccessibleInterval<T> in) {
    final RandomAccessibleInterval<RealType> result = (RandomAccessibleInterval) ops().run(net.imagej.ops.image.integral.DefaultIntegralImg.class, out, in);
    return result;
  }

  @SuppressWarnings(value = { "unchecked", "rawtypes" }) @OpMethod(ops = { net.imagej.ops.image.integral.DefaultIntegralImg.class, net.imagej.ops.image.integral.WrappedIntegralImg.class }) public <T extends RealType<T>> RandomAccessibleInterval<RealType> integral(final RandomAccessibleInterval<T> in) {
    final RandomAccessibleInterval<RealType> result = (RandomAccessibleInterval) ops().run(Ops.Image.Integral.class, in);
    return result;
  }

  @SuppressWarnings(value = { "unchecked", "rawtypes" }) @OpMethod(op = net.imagej.ops.image.integral.SquareIntegralImg.class) public <T extends RealType<T>> RandomAccessibleInterval<RealType> squareIntegral(final RandomAccessibleInterval<RealType> out, final RandomAccessibleInterval<T> in) {
    final RandomAccessibleInterval<RealType> result = (RandomAccessibleInterval) ops().run(Ops.Image.SquareIntegral.class, out, in);
    return result;
  }

  @SuppressWarnings(value = { "unchecked", "rawtypes" }) @OpMethod(op = net.imagej.ops.image.integral.SquareIntegralImg.class) public <T extends RealType<T>> RandomAccessibleInterval<RealType> squareIntegral(final RandomAccessibleInterval<T> in) {
    final RandomAccessibleInterval<RealType> result = (RandomAccessibleInterval) ops().run(Ops.Image.SquareIntegral.class, in);
    return result;
  }

  /** Executes the "invert" operation on the given arguments. */
  @OpMethod(ops = { net.imagej.ops.image.invert.InvertII.class, net.imagej.ops.image.invert.InvertIIInteger.class }) public <I extends RealType<I>, O extends RealType<O>> IterableInterval<O> invert(final IterableInterval<O> out, final IterableInterval<I> in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<O> result = (IterableInterval<O>) ops().run(net.imagej.ops.Ops.Image.Invert.class, out, in);
    return result;
  }

  /** Executes the "invert" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.invert.InvertII.class) public <I extends RealType<I>, O extends RealType<O>> IterableInterval<O> invert(final IterableInterval<O> out, final IterableInterval<I> in, final RealType<I> min) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<O> result = (IterableInterval<O>) ops().run(net.imagej.ops.Ops.Image.Invert.class, out, in);
    return result;
  }

  /** Executes the "invert" operation on the given arguments, given a {@link IntegerType} minimum. */
  @OpMethod(op = net.imagej.ops.image.invert.InvertIIInteger.class) public <I extends RealType<I>, O extends RealType<O>> IterableInterval<O> invert(final IterableInterval<O> out, final IterableInterval<I> in, final IntegerType min) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<O> result = (IterableInterval<O>) ops().run(net.imagej.ops.Ops.Image.Invert.class, out, in);
    return result;
  }

  /** Executes the "invert" operation on the given arguments, given a {@link RealType} minimum. */
  @OpMethod(op = net.imagej.ops.image.invert.InvertII.class) public <I extends RealType<I>, O extends RealType<O>> IterableInterval<O> invert(final IterableInterval<O> out, final IterableInterval<I> in, final RealType<I> min, final RealType<I> max) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<O> result = (IterableInterval<O>) ops().run(net.imagej.ops.Ops.Image.Invert.class, out, in);
    return result;
  }

  /** Executes the "invert" operation on the given arguments, given a {@link IntegerType} minimum. */
  @OpMethod(op = net.imagej.ops.image.invert.InvertIIInteger.class) public <I extends RealType<I>, O extends RealType<O>> IterableInterval<O> invert(final IterableInterval<O> out, final IterableInterval<I> in, final IntegerType min, final IntegerType max) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<O> result = (IterableInterval<O>) ops().run(net.imagej.ops.Ops.Image.Invert.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIComputer.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> out, final IterableInterval<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIComputer.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> out, final IterableInterval<T> in, final T sourceMin) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, out, in, sourceMin);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIComputer.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> out, final IterableInterval<T> in, final T sourceMin, final T sourceMax) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, out, in, sourceMin, sourceMax);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIComputer.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> out, final IterableInterval<T> in, final T sourceMin, final T sourceMax, final T targetMin) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, out, in, sourceMin, sourceMax, targetMin);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIComputer.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> out, final IterableInterval<T> in, final T sourceMin, final T sourceMax, final T targetMin, final T targetMax) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, out, in, sourceMin, sourceMax, targetMin, targetMax);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIFunction.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIFunction.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> in, final T sourceMin) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, in, sourceMin);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIFunction.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> in, final T sourceMin, final T sourceMax) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, in, sourceMin, sourceMax);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIFunction.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> in, final T sourceMin, final T sourceMax, final T targetMin) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, in, sourceMin, sourceMax, targetMin);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIFunction.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> in, final T sourceMin, final T sourceMax, final T targetMin, final T targetMax) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, in, sourceMin, sourceMax, targetMin, targetMax);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.normalize.NormalizeIIFunction.class) public <T extends RealType<T>> IterableInterval<T> normalize(final IterableInterval<T> in, final T sourceMin, final T sourceMax, final T targetMin, final T targetMax, final boolean isLazy) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Image.Normalize.class, in, sourceMin, sourceMax, targetMin, targetMax, isLazy);
    return result;
  }

  /** Executes the "watershed" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.watershed.Watershed.class) public <T extends RealType<T>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<T> in, final boolean eightConnectivity, final boolean drawWatersheds) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.Watershed.class, out, in, eightConnectivity, drawWatersheds);
    return result;
  }

  /** Executes the "watershed" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.watershed.Watershed.class) public <T extends RealType<T>> ImgLabeling<Integer, IntType> watershed(final RandomAccessibleInterval<T> in, final boolean eightConnectivity, final boolean drawWatersheds) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.Watershed.class, in, eightConnectivity, drawWatersheds);
    return result;
  }

  /** Executes the "watershed" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.watershed.Watershed.class) public <B extends BooleanType<B>, T extends RealType<T>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<T> in, final boolean eightConnectivity, final boolean drawWatersheds, final RandomAccessibleInterval<B> mask) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.Watershed.class, out, in, eightConnectivity, drawWatersheds, mask);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.watershed.WatershedBinary.class) public <B extends BooleanType<B>> ImgLabeling<Integer, IntType> watershed(final RandomAccessibleInterval<B> in, final boolean eightConnectivity, final boolean drawWatersheds, final double... sigma) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedBinary.class, in, eightConnectivity, drawWatersheds, sigma);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.watershed.WatershedBinary.class) public <B extends BooleanType<B>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<B> in, final boolean eightConnectivity, final boolean drawWatersheds, final double... sigma) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedBinary.class, out, in, eightConnectivity, drawWatersheds, sigma);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.watershed.WatershedBinary.class) public <B extends BooleanType<B>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<B> in, final boolean useEightConnectivity, final boolean drawWatersheds, final double[] sigma, final RandomAccessibleInterval<B> mask) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedBinary.class, out, in, useEightConnectivity, drawWatersheds, sigma, mask);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.watershed.WatershedBinarySingleSigma.class) public <T extends BooleanType<T>> ImgLabeling<Integer, IntType> watershed(final RandomAccessibleInterval<T> in, final boolean useEightConnectivity, final boolean drawWatersheds, final double sigma) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedBinarySingleSigma.class, in, useEightConnectivity, drawWatersheds, sigma);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.watershed.WatershedBinarySingleSigma.class) public <T extends BooleanType<T>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<T> in, final boolean useEightConnectivity, final boolean drawWatersheds, final double sigma) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedBinarySingleSigma.class, out, in, useEightConnectivity, drawWatersheds, sigma);
    return result;
  }

  @OpMethod(op = net.imagej.ops.image.watershed.WatershedBinarySingleSigma.class) public <T extends BooleanType<T>, B extends BooleanType<B>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<T> in, final boolean useEightConnectivity, final boolean drawWatersheds, final double sigma, final RandomAccessibleInterval<B> mask) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedBinarySingleSigma.class, out, in, useEightConnectivity, drawWatersheds, sigma, mask);
    return result;
  }

  /** Executes the "watershedSeeded" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.watershed.WatershedSeeded.class) public <T extends RealType<T>> ImgLabeling<Integer, IntType> watershed(final RandomAccessibleInterval<T> in, final ImgLabeling<Integer, IntType> seeds, final boolean eightConnectivity, final boolean drawWatersheds) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = ((ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedSeeded.class, in, seeds, eightConnectivity, drawWatersheds));
    return result;
  }

  /** Executes the "watershedSeeded" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.watershed.WatershedSeeded.class) public <T extends RealType<T>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<T> in, final ImgLabeling<Integer, IntType> seeds, final boolean eightConnectivity, final boolean drawWatersheds) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedSeeded.class, out, in, seeds, eightConnectivity, drawWatersheds);
    return result;
  }

  /** Executes the "watershedSeeded" operation on the given arguments. */
  @OpMethod(op = net.imagej.ops.image.watershed.WatershedSeeded.class) public <B extends BooleanType<B>, T extends RealType<T>> ImgLabeling<Integer, IntType> watershed(final ImgLabeling<Integer, IntType> out, final RandomAccessibleInterval<T> in, final ImgLabeling<Integer, IntType> seeds, final boolean eightConnectivity, final boolean drawWatersheds, final RandomAccessibleInterval<B> mask) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<Integer, IntType> result = (ImgLabeling<Integer, IntType>) ops().run(net.imagej.ops.image.watershed.WatershedSeeded.class, out, in, seeds, eightConnectivity, drawWatersheds, mask);
    return result;
  }

  @Override public String getName() {
    return "image";
  }
}