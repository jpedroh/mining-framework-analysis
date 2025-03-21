package org.apache.commons.collections4.bloomfilter;

public class BitCountProducerFromArrayCountingBloomFilterTest extends AbstractBitCountProducerTest {
  protected Shape shape = Shape.fromKM(17, 72);

  @Override protected BitCountProducer createProducer() {
    ArrayCountingBloomFilter filter = new ArrayCountingBloomFilter(shape);
    Hasher hasher = new IncrementingHasher(0, 1);
    filter.merge(hasher);
    return filter;
  }

  @Override protected BitCountProducer createEmptyProducer() {
    return new ArrayCountingBloomFilter(shape);
  }
}