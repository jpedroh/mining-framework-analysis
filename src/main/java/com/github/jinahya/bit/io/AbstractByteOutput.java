package com.github.jinahya.bit.io;

/**
 * An abstract class for implementing {@link ByteOutput}.
 *
 * @param <T> byte target type parameter
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public abstract class AbstractByteOutput<T extends java.lang.Object> implements ByteOutput {
  /**
     * Creates a new instance on top of given byte target.
     *
     * @param target the underlying byte target; {@code null} if it is supposed to be lazily initialized and set.
     */
  public AbstractByteOutput(final T target) {
    super();
    this.target = target;
  }

  /**
     * Returns the current value of {@link #target}.
     *
     * @return the current value of {@link #target}.
     */
  public T getTarget() {
    return target;
  }

  /**
     * Replaces the value of {@link #target} with given.
     *
     * @param target new value for {@link #target}.
     */
  public void setTarget(final T target) {
    this.target = target;
  }

  /**
     * Replaces the value of {@link #target} with given and returns this instance.
     *
     * @param target new value for {@link #target}.
     * @return this instance.
     * @see #setTarget(java.lang.Object)
     */
  public AbstractByteOutput<T> target(final T target) {
    setTarget(target);
    return this;
  }

  /**
     * The underlying byte target.
     */
  protected T target;
}