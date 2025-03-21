package org.apache.commons.collections4.bloomfilter;

public class BitMapProducerFromArrayCountingBloomFilterTest extends AbstractBitMapProducerTest {
  protected Shape shape = Shape.fromKM(17, 72);

  @Override protected BitMapProducer createProducer() {
    ArrayCountingBloomFilter filter = new ArrayCountingBloomFilter(shape);
    Hasher hasher = new IncrementingHasher(0, 1);
    filter.merge(hasher);
    return filter;
  }

  @Override protected BitMapProducer createEmptyProducer() {
    return new ArrayCountingBloomFilter(shape);
  }
}