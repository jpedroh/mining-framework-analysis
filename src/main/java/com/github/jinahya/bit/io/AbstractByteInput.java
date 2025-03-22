package com.github.jinahya.bit.io;

/**
 * An abstract class for implementing {@link ByteInput}.
 *
 * @param <T> byte source type parameter
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public abstract class AbstractByteInput<T extends java.lang.Object> implements ByteInput {
  /**
     * Creates a new instance built on top of specified byte source.
     *
     * @param source the underlying byte source or {@code null} if it is intended to be lazily initialized and set.
     */
  public AbstractByteInput(final T source) {
    super();
    this.source = source;
  }

  /**
     * Returns the current value of {@link #source}.
     *
     * @return the current value of {@link #source}
     */
  public T getSource() {
    return source;
  }

  /**
     * Replaces the current value of {@link #source} with given.
     *
     * @param source new value for {@link #source}
     */
  public void setSource(final T source) {
    this.source = source;
  }

  /**
     * Replaces the current value of {@link #source} with given and returns this instance.
     *
     * @param source new value for {@link #source}
     * @return this instance
     * @see #setSource(java.lang.Object)
     */
  public AbstractByteInput<T> source(final T source) {
    setSource(source);
    return this;
  }

  /**
     * The underlying byte source.
     */
  protected T source;
}