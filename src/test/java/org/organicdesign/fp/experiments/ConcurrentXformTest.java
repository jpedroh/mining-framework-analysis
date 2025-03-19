package org.organicdesign.fp.experiments;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertArrayEquals;

@RunWith(value = JUnit4.class) public class ConcurrentXformTest {
  @Test public void arrayCorrectness() {
    Long[] is = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L };
    IntRange range = IntRange.of(1, 9);
    assertArrayEquals(ConcurrentXform.of(1, range).toArray(), is);
    assertArrayEquals(ConcurrentXform.of(2, range).toArray(), is);
    assertArrayEquals(ConcurrentXform.of(3, range).toArray(), is);
    assertArrayEquals(ConcurrentXform.of(4, range).toArray(), is);
    assertArrayEquals(ConcurrentXform.of(5, range).toArray(), is);
  }


<<<<<<< /usr/src/app/output/glenkpeterson/j-cicle/5e22f4be3ec0b3915db2bfc84fcb1b583ae76f02/src/test/java/org/organicdesign/fp/experiments/ConcurrentXformTest.java/left.java
  @Test @Ignore public void tryStuff() {
    System.out.println();
    IntRange range = IntRange.of(-10000000, 10000000);
    ConcurrentXform cx = ConcurrentXform.of(1, range);
    long startTime = System.currentTimeMillis();
    cx.toArray();
    System.out.println("Time: " + (System.currentTimeMillis() - startTime));
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Test public void linkedListCorrectness() {
    Long[] is = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L };
    IntRange range = IntRange.of(1, 9);
    assertArrayEquals(ConcurrentXform.of(1, range).toLinkedList().toArray(), is);
    assertArrayEquals(ConcurrentXform.of(2, range).toLinkedList().toArray(), is);
    assertArrayEquals(ConcurrentXform.of(3, range).toLinkedList().toArray(), is);
    assertArrayEquals(ConcurrentXform.of(4, range).toLinkedList().toArray(), is);
    assertArrayEquals(ConcurrentXform.of(5, range).toLinkedList().toArray(), is);
  }

  @Test @Ignore public void arraySpeed() {
    System.out.println();
    IntRange range = IntRange.of(-10000000, 10000000);
    ConcurrentXform cx = ConcurrentXform.of(2, range);
    long startTime = System.currentTimeMillis();
    cx.toArray();
    System.out.println("Time: " + (System.currentTimeMillis() - startTime));
  }

  @Test @Ignore public void linkedListSpeed() {
    System.out.println();
    IntRange range = IntRange.of(-10000000, 10000000);
    ConcurrentXform cx = ConcurrentXform.of(2, range);
    long startTime = System.currentTimeMillis();
    cx.toLinkedList();
    System.out.println("Time: " + (System.currentTimeMillis() - startTime));
  }
}