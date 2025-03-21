package org.apache.commons.collections4.bloomfilter;

/**
 * Contains functions to convert {@code int} indices into Bloom filter bit positions.
 */
public final class BloomFilterIndexer {
  /** A bit shift to apply to an integer to divided by 64 (2^6). */
  private static final int DIVIDE_BY_64 = 6;

  /** Do not instantiate. */
  private BloomFilterIndexer() {
  }

  /**
     * Check the index is positive.
     *
     * @param bitIndex the bit index
     * @throws IndexOutOfBoundsException if the index is not positive
     */
  public static void checkPositive(int bitIndex) {
    if (bitIndex < 0) {
      throw new IndexOutOfBoundsException("Negative bitIndex: " + bitIndex);
    }
  }

  /**
     * Gets the filter index for the specified bit index assuming the filter is using 64-bit longs
     * to store bits starting at index 0.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code bitIndex / 64}.
     *
     * <p>The divide is performed using bit shifts. If the input is negative the behaviour
     * is not defined.
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter index
     * @see #checkPositive(int)
     */
  public static int getLongIndex(int bitIndex) {
    return bitIndex >> DIVIDE_BY_64;
  }

  /**
     * Gets the filter bit mask for the specified bit index assuming the filter is using 64-bit
     * longs to store bits starting at index 0. The returned value is a {@code long} with only
     * 1 bit set.
     *
     * <p>The index is assumed to be positive. For a positive index the result will match
     * {@code 1L << (bitIndex % 64)}.
     *
     * <p>If the input is negative the behaviour is not defined.
     *
     * @param bitIndex the bit index (assumed to be positive)
     * @return the filter bit
     * @see #checkPositive(int)
     */
  public static long getLongBit(int bitIndex) {
    return 1L << bitIndex;
  }
}