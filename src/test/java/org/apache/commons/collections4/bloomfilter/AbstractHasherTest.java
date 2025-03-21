package org.apache.commons.collections4.bloomfilter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public abstract class AbstractHasherTest extends AbstractIndexProducerTest {
  protected abstract Hasher createHasher();

  protected abstract Hasher createEmptyHasher();

  /**
     * A method to get the number of items in a hasher.  Mostly applies to
     * Collections of hashers.
     * @param hasher the hasher to check.
     * @return the number of hashers in the hasher
     */
  protected abstract int getHasherSize(Hasher hasher);

  /**
     * The shape of the Hashers filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
  protected final Shape getTestShape() {
    return Shape.fromKM(17, 72);
  }

  @Override protected IndexProducer createProducer() {
    return createHasher().indices(getTestShape());
  }

  @Override protected IndexProducer createEmptyProducer() {
    return createEmptyHasher().indices(getTestShape());
  }

  @ParameterizedTest @CsvSource(value = { "17, 72", "3, 14", "5, 67868", "75, 10" }) public void testHashing(int k, int m) {
    int[] count = { 0 };
    Hasher hasher = createHasher();
    hasher.indices(Shape.fromKM(k, m)).forEachIndex((i) -> {
      assertTrue(i >= 0 && i < m, () -> "Out of range: " + i + ", m=" + m);
      count[0]++;
      return true;
    });
    assertEquals(k * getHasherSize(hasher), count[0], () -> String.format("Did not produce k=%d * m=%d indices", k, getHasherSize(hasher)));
  }

  @Test public void testUniqueIndex() {
    Shape shape = Shape.fromKM(75, 10);
    Hasher hasher = 
<<<<<<< /usr/src/app/output/apache/commons-collections/8ed3c228f6a028dddb3141a32abfea7688a49443/src/test/java/org/apache/commons/collections4/bloomfilter/AbstractHasherTest.java/left.java
    new IncrementingHasher(5, 12)
=======
    createHasher()
>>>>>>> /usr/src/app/output/apache/commons-collections/8ed3c228f6a028dddb3141a32abfea7688a49443/src/test/java/org/apache/commons/collections4/bloomfilter/AbstractHasherTest.java/right.java
    ;
    IndexProducer producer = hasher.indices(shape);
    List<Integer> full = Arrays.stream(producer.asIndexArray()).boxed().collect(Collectors.toList());
    producer = hasher.uniqueIndices(shape);
    List<Integer> unique = Arrays.stream(producer.asIndexArray()).boxed().collect(Collectors.toList());
    assertTrue(full.size() > unique.size());
    Set<Integer> set = new HashSet<Integer>(unique);
    assertEquals(set.size(), unique.size());
  }
}