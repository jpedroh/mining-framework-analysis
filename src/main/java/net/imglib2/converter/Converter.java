package net.imglib2.converter;
import java.util.function.BiConsumer;

/**
 * This interface is equivalent to the {@link BiConsumer} interface and exists
 * for historical reasons only.  Its main use is for functions with one input
 * variable and one pre-allocated output on individual pixels.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public interface Converter<A extends java.lang.Object, B extends java.lang.Object> {
  public void convert(A input, B output);
}