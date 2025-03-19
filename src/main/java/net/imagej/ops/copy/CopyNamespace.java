package net.imagej.ops.copy;
import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.type.BooleanType;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import org.scijava.plugin.Plugin;

/**
 * The copy namespace contains ops that copy data.
 *
 * @author Christian Dietz (University of Konstanz)
 */
@Plugin(type = Namespace.class) public class CopyNamespace extends AbstractNamespace {
  @Override public String getName() {
    return "copy";
  }

  @OpMethod(op = net.imagej.ops.copy.CopyImg.class) public <T extends NativeType<T>> Img<T> img(final Img<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final Img<T> result = (Img<T>) ops().run(net.imagej.ops.copy.CopyImg.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyImg.class) public <T extends NativeType<T>> Img<T> img(final Img<T> out, final Img<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final Img<T> result = (Img<T>) ops().run(net.imagej.ops.copy.CopyImg.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyArrayImg.class) public <T extends NativeType<T>, A extends ArrayDataAccess<A>> ArrayImg<T, A> img(final ArrayImg<T, A> in) {
    @SuppressWarnings(value = { "unchecked" }) final ArrayImg<T, A> result = (ArrayImg<T, A>) ops().run(net.imagej.ops.copy.CopyArrayImg.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyArrayImg.class) public <T extends NativeType<T>, A extends ArrayDataAccess<A>> ArrayImg<T, A> img(final ArrayImg<T, A> out, final ArrayImg<T, A> in) {
    @SuppressWarnings(value = { "unchecked" }) final ArrayImg<T, A> result = (ArrayImg<T, A>) ops().run(net.imagej.ops.copy.CopyArrayImg.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyImgLabeling.class) public <L extends java.lang.Object, I extends IntegerType<I>> ImgLabeling<L, I> imgLabeling(final ImgLabeling<L, I> in) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<L, I> result = (ImgLabeling<L, I>) ops().run(net.imagej.ops.copy.CopyImgLabeling.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyImgLabeling.class) public <L extends java.lang.Object, I extends IntegerType<I>> ImgLabeling<L, I> imgLabeling(final ImgLabeling<L, I> out, final ImgLabeling<L, I> in) {
    @SuppressWarnings(value = { "unchecked" }) final ImgLabeling<L, I> result = (ImgLabeling<L, I>) ops().run(net.imagej.ops.copy.CopyImgLabeling.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyII.class) public <T extends java.lang.Object> IterableInterval<T> iterableInterval(final IterableInterval<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Copy.IterableInterval.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyII.class) public <T extends java.lang.Object> IterableInterval<T> iterableInterval(final IterableInterval<T> out, final IterableInterval<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final IterableInterval<T> result = (IterableInterval<T>) ops().run(net.imagej.ops.Ops.Copy.IterableInterval.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyLabelingMapping.class) public <L extends java.lang.Object> LabelingMapping<L> labelingMapping(final LabelingMapping<L> in) {
    @SuppressWarnings(value = { "unchecked" }) final LabelingMapping<L> result = (LabelingMapping<L>) ops().run(net.imagej.ops.copy.CopyLabelingMapping.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyLabelingMapping.class) public <L extends java.lang.Object> LabelingMapping<L> labelingMapping(final LabelingMapping<L> out, final LabelingMapping<L> in) {
    @SuppressWarnings(value = { "unchecked" }) final LabelingMapping<L> result = (LabelingMapping<L>) ops().run(net.imagej.ops.copy.CopyLabelingMapping.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyRAI.class) public <T extends java.lang.Object> RandomAccessibleInterval<T> rai(final RandomAccessibleInterval<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<T> result = (RandomAccessibleInterval<T>) ops().run(net.imagej.ops.copy.CopyRAI.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyRAI.class) public <T extends java.lang.Object> RandomAccessibleInterval<T> rai(final RandomAccessibleInterval<T> out, final RandomAccessibleInterval<T> in) {
    @SuppressWarnings(value = { "unchecked" }) final RandomAccessibleInterval<T> result = (RandomAccessibleInterval<T>) ops().run(net.imagej.ops.copy.CopyRAI.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyType.class) public <T extends Type<T>> T type(final T in) {
    @SuppressWarnings(value = { "unchecked" }) final T result = (T) ops().run(net.imagej.ops.copy.CopyType.class, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyType.class) public <T extends Type<T>> T type(final T out, final T in) {
    @SuppressWarnings(value = { "unchecked" }) final T result = (T) ops().run(net.imagej.ops.copy.CopyType.class, out, in);
    return result;
  }

  @OpMethod(op = net.imagej.ops.copy.CopyBooleanType.class) public <B1 extends BooleanType<B1>, B2 extends BooleanType<B2>> B2 type(final B2 out, final B1 in) {
    @SuppressWarnings(value = { "unchecked" }) final B2 result = (B2) ops().run(net.imagej.ops.copy.CopyType.class, out, in);
    return result;
  }
}