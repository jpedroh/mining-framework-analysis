package org.apache.commons.collections4.bloomfilter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test standard methods in the {@link BloomFilter} interface.
 */
public abstract class AbstractBloomFilterTest<T extends BloomFilter> {
  /**
     * The shape of the Bloom filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
  protected final Shape getTestShape() {
    return Shape.fromKM(17, 72);
  }

  /**
     * Create an empty version of the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @return a BloomFilter implementation.
     */
  protected abstract T createEmptyFilter(Shape shape);

  /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param hasher the hasher to use to create the filter.
     * @return a BloomFilter implementation.
     */
  protected final T createFilter(final Shape shape, final Hasher hasher) {
    final T bf = createEmptyFilter(shape);
    bf.merge(hasher);
    return bf;
  }

  /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param producer A BitMap producer to build the filter with.
     * @return a BloomFilter implementation.
     */
  protected final T createFilter(final Shape shape, final BitMapProducer producer) {
    final T bf = createEmptyFilter(shape);
    bf.merge(producer);
    return bf;
  }

  /**
     * Create the BloomFilter implementation we are testing.
     *
     * @param shape the shape of the filter.
     * @param producer An Index producer to build the filter with.
     * @return a BloomFilter implementation.
     */
  protected final T createFilter(final Shape shape, final IndexProducer producer) {
    final T bf = createEmptyFilter(shape);
    bf.merge(producer);
    return bf;
  }

  /**
     *
     */
  @Test public void testMergeWithBadHasher() {
    final BloomFilter f = createEmptyFilter(getTestShape());
    assertThrows(IllegalArgumentException.class, () -> f.merge(new BadHasher(getTestShape().getNumberOfBits())));
    final BloomFilter f2 = createEmptyFilter(getTestShape());
    assertThrows(IllegalArgumentException.class, () -> f2.merge(new BadHasher(-1)));
  }

  @Test public void testMergeWithHasher() {
    for (int i = 0; i < 5; i++) {
      final BloomFilter f = createEmptyFilter(getTestShape());
      final int[] expected = DefaultIndexProducerTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits());
      final Hasher hasher = new ArrayHasher(expected);
      f.merge(hasher);
      assertArrayEquals(DefaultIndexProducerTest.unique(expected), f.asIndexArray());
    }
  }

  @Test public void testMergeWithBitMapProducer() {
    for (int i = 0; i < 5; i++) {
      final long[] values = new long[2];
      for (final int idx : DefaultIndexProducerTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits())) {
        BitMap.set(values, idx);
      }
      final BloomFilter f = createFilter(getTestShape(), BitMapProducer.fromBitMapArray(values));
      final List<Long> lst = new ArrayList<>();
      for (final long l : values) {
        lst.add(l);
      }
      assertTrue(f.forEachBitMap((l) -> lst.remove(Long.valueOf(l))));
      assertTrue(lst.isEmpty());
    }
    final BitMapProducer badProducer = BitMapProducer.fromBitMapArray(0L, Long.MAX_VALUE);
    final BloomFilter bf = createEmptyFilter(getTestShape());
    assertThrows(IllegalArgumentException.class, () -> bf.merge(badProducer));
    final BitMapProducer badProducer2 = BitMapProducer.fromBitMapArray(0x80_00_00_00_00_00_00_00L);
    final BloomFilter bf2 = createEmptyFilter(Shape.fromKM(3, 32));
    assertThrows(IllegalArgumentException.class, () -> bf2.merge(badProducer2));
  }

  @Test public void testMergeWithIndexProducer() {
    for (int i = 0; i < 5; i++) {
      final int[] values = DefaultIndexProducerTest.generateIntArray(getTestShape().getNumberOfHashFunctions(), getTestShape().getNumberOfBits());
      final BloomFilter f = createFilter(getTestShape(), IndexProducer.fromIndexArray(values));
      final BitSet uniqueValues = DefaultIndexProducerTest.uniqueSet(values);
      assertTrue(f.forEachIndex((idx) -> {
        final boolean result = uniqueValues.get(idx);
        uniqueValues.clear(idx);
        return result;
      }));
      assertTrue(uniqueValues.isEmpty());
    }
    final BloomFilter f1 = createEmptyFilter(getTestShape());
    assertThrows(IllegalArgumentException.class, () -> f1.merge(IndexProducer.fromIndexArray(new int[] { getTestShape().getNumberOfBits() })));
    final BloomFilter f2 = createEmptyFilter(getTestShape());
    assertThrows(IllegalArgumentException.class, () -> f2.merge(IndexProducer.fromIndexArray(new int[] { -1 })));
  }

  @Test public final void testContains() {
    BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
    final BloomFilter bf2 = TestingHashers.populateFromHashersFrom1AndFrom11(createEmptyFilter(getTestShape()));
    assertTrue(bf1.contains(bf1), "BF Should contain itself");
    assertTrue(bf2.contains(bf2), "BF2 Should contain itself");
    assertFalse(bf1.contains(bf2), "BF should not contain BF2");
    assertTrue(bf2.contains(bf1), "BF2 should contain BF");
    assertTrue(bf2.contains(new IncrementingHasher(1, 1)), "BF2 Should contain this hasher");
    assertFalse(bf2.contains(new IncrementingHasher(1, 3)), "BF2 Should not contain this hasher");
    IndexProducer indexProducer = new IncrementingHasher(1, 1).indices(getTestShape());
    assertTrue(bf2.contains(indexProducer), "BF2 Should contain this hasher");
    indexProducer = new IncrementingHasher(1, 3).indices(getTestShape());
    assertFalse(bf2.contains(indexProducer), "BF2 Should not contain this hasher");
    BitMapProducer bitMapProducer = BitMapProducer.fromIndexProducer(new IncrementingHasher(1, 1).indices(getTestShape()), getTestShape().getNumberOfBits());
    assertTrue(bf2.contains(bitMapProducer), "BF2 Should contain this hasher");
    bitMapProducer = BitMapProducer.fromIndexProducer(new IncrementingHasher(1, 3).indices(getTestShape()), getTestShape().getNumberOfBits());
    assertFalse(bf2.contains(bitMapProducer), "BF2 Should not contain this hasher");
    bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
    final BloomFilter bf3 = createFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), Long.SIZE - 1), TestingHashers.FROM1);
    assertTrue(bf1.contains(bf3));
    assertTrue(bf3.contains(bf1));
    final BloomFilter bf4 = TestingHashers.populateFromHashersFrom1AndFrom11(createEmptyFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), Long.SIZE - 1)));
    assertFalse(bf1.contains(bf4));
    assertTrue(bf4.contains(bf1));
  }

  @Test public void testClear() {
    final BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
    assertNotEquals(0, bf1.cardinality());
    bf1.clear();
    assertEquals(0, bf1.cardinality());
  }

  /**
     * Tests that the estimated intersection calculations are correct.
     */
  @Test public final void testEstimateIntersection() {
    final BloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
    final BloomFilter bf2 = TestingHashers.populateFromHashersFrom1AndFrom11(createEmptyFilter(getTestShape()));
    final BloomFilter bf3 = createFilter(getTestShape(), fullHasher);
    assertEquals(1, bf.estimateIntersection(bf2));
    assertEquals(1, bf2.estimateIntersection(bf));
    assertEquals(1, bf.estimateIntersection(bf3));
    assertEquals(1, bf2.estimateIntersection(bf));
    assertEquals(2, bf3.estimateIntersection(bf2));
    final BloomFilter bf4 = createEmptyFilter(getTestShape());
    assertEquals(0, bf.estimateIntersection(bf4));
    assertEquals(0, bf4.estimateIntersection(bf));
    HasherCollection firstHalf = new HasherCollection(new IncrementingHasher(0, 1), new IncrementingHasher(17, 1), new IncrementingHasher(33, 1));
    HasherCollection secondHalf = new HasherCollection(new IncrementingHasher(50, 1), new IncrementingHasher(67, 1));
    BloomFilter bf5 = createFilter(getTestShape(), firstHalf);
    BloomFilter bf6 = createFilter(getTestShape(), secondHalf);
    assertThrows(IllegalArgumentException.class, () -> bf5.estimateIntersection(bf6));
    assertEquals(Integer.MAX_VALUE, bf3.estimateIntersection(bf3));
  }

  /**
     * Tests that the andCardinality calculations are correct.
     */
  @Test public final void testEstimateUnion() {
    final BloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
    final BloomFilter bf2 = createFilter(getTestShape(), TestingHashers.FROM11);
    assertEquals(2, bf.estimateUnion(bf2));
    assertEquals(2, bf2.estimateUnion(bf));
    final BloomFilter bf3 = createEmptyFilter(getTestShape());
    assertEquals(1, bf.estimateUnion(bf3));
    assertEquals(1, bf3.estimateUnion(bf));
  }

  /**
     * Tests that the size estimate is correctly calculated.
     */
  @Test public final void testEstimateN() {
    BloomFilter filter1 = createFilter(getTestShape(), TestingHashers.FROM1);
    assertEquals(1, filter1.estimateN());
    filter1.merge(new IncrementingHasher(4, 1));
    assertEquals(1, filter1.estimateN());
    filter1.merge(new IncrementingHasher(17, 1));
    assertEquals(3, filter1.estimateN());
    filter1 = createFilter(getTestShape(), fullHasher);
    assertEquals(Integer.MAX_VALUE, filter1.estimateN());
  }

  /**
     * Tests that asBitMapArray works correctly.
     */
  @Test public final void testAsBitMapArray() {
    final IncrementingHasher hasher = new IncrementingHasher(63, 1);
    final BloomFilter bf = createFilter(Shape.fromKM(2, 72), hasher);
    final long[] lb = bf.asBitMapArray();
    assertEquals(2, lb.length);
    assertEquals(0x8000000000000000L, lb[0]);
    assertEquals(0x1, lb[1]);
  }

  /**
     * Tests that isFull() returns the proper values.
     */
  @Test public final void testIsFull() {
    BloomFilter filter = createEmptyFilter(getTestShape());
    assertFalse(filter.isFull(), "Should not be full");
    filter = TestingHashers.populateEntireFilter(filter);
    assertTrue(filter.isFull(), "Should be full");
    filter = createFilter(getTestShape(), new IncrementingHasher(1, 3));
    assertFalse(filter.isFull(), "Should not be full");
  }

  /**
     * Tests that merging bloom filters works as expected with a generic BloomFilter.
     */
  @Test public final void testMerge() {
    final BloomFilter bf1 = createFilter(getTestShape(), TestingHashers.FROM1);
    final BloomFilter bf2 = createFilter(getTestShape(), TestingHashers.FROM11);
    final BloomFilter bf3 = bf1.copy();
    bf3.merge(bf2);
    final long[] bf1Val = bf1.asBitMapArray();
    final long[] bf2Val = bf2.asBitMapArray();
    for (int i = 0; i < bf1Val.length; i++) {
      bf1Val[i] |= bf2Val[i];
    }
    bf1.merge(bf2);
    final long[] bf1New = bf1.asBitMapArray();
    for (int i = 0; i < bf1Val.length; i++) {
      assertEquals(bf1Val[i], bf1New[i], "Bad value at " + i);
    }
    assertTrue(bf1.contains(bf2), "Should contain bf2");
    assertTrue(bf1.contains(bf3), "Should contain bf3");
    final BloomFilter bf4 = createFilter(getTestShape(), TestingHashers.FROM1);
    bf4.merge(TestingHashers.FROM11);
    assertTrue(bf4.contains(bf2), "Should contain Bf2");
    assertTrue(bf4.contains(bf3), "Should contain Bf3");
    assertThrows(IllegalArgumentException.class, () -> bf1.merge(new BadHasher(bf1.getShape().getNumberOfBits())));
    assertThrows(IllegalArgumentException.class, () -> bf1.merge(new BadHasher(-1)));
    final BloomFilter bf5 = new SimpleBloomFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), 3 * Long.SIZE));
    bf5.merge(new IncrementingHasher(Long.SIZE * 2, 1));
    assertThrows(IllegalArgumentException.class, () -> bf1.merge(bf5));
    final BloomFilter bf6 = new SparseBloomFilter(Shape.fromKM(getTestShape().getNumberOfHashFunctions(), 3 * Long.SIZE));
    bf6.merge(new IncrementingHasher(Long.SIZE * 2, 1));
    assertThrows(IllegalArgumentException.class, () -> bf1.merge(bf6));
  }

  private void assertIndexProducerMerge(final Shape shape, final int[] values, final int[] expected) {
    final IndexProducer indices = IndexProducer.fromIndexArray(values);
    final BloomFilter filter = createFilter(shape, indices);
    final List<Integer> lst = new ArrayList<>();
    filter.forEachIndex((x) -> {
      lst.add(x);
      return true;
    });
    assertEquals(expected.length, lst.size());
    for (final int value : expected) {
      assertTrue(lst.contains(Integer.valueOf(value)), "Missing " + value);
    }
  }

  private void assertFailedIndexProducerConstructor(final Shape shape, final int[] values) {
    final IndexProducer indices = IndexProducer.fromIndexArray(values);
    assertThrows(IllegalArgumentException.class, () -> createFilter(shape, indices));
  }

  @Test public void testIndexProducerMerge() {
    final Shape shape = Shape.fromKM(5, 10);
    assertIndexProducerMerge(shape, new int[] { 0, 2, 4, 6, 8 }, new int[] { 0, 2, 4, 6, 8 });
    assertIndexProducerMerge(shape, new int[] { 0, 2, 4, 2, 8 }, new int[] { 0, 2, 4, 8 });
    assertFailedIndexProducerConstructor(shape, new int[] { 0, 2, 4, -2, 8 });
    assertFailedIndexProducerConstructor(shape, new int[] { 0, 2, 4, 12, 8 });
    assertIndexProducerMerge(shape, new int[0], new int[0]);
  }

  @Test public void testBitMapProducerSize() {
    final int[] idx = new int[1];
    createFilter(getTestShape(), TestingHashers.FROM1).forEachBitMap((i) -> {
      idx[0]++;
      return true;
    });
    assertEquals(BitMap.numberOfBitMaps(getTestShape().getNumberOfBits()), idx[0]);
    idx[0] = 0;
    createEmptyFilter(getTestShape()).forEachBitMap((i) -> {
      idx[0]++;
      return true;
    });
    assertEquals(BitMap.numberOfBitMaps(getTestShape().getNumberOfBits()), idx[0]);
  }

  class BadHasher implements Hasher {
    IndexProducer producer;

    BadHasher(final int value) {
      this.producer = IndexProducer.fromIndexArray(new int[] { value });
    }

    @Override public IndexProducer indices(final Shape shape) {
      return producer;
    }

    @Override public IndexProducer uniqueIndices(final Shape shape) {
      return producer;
    }
  }
}