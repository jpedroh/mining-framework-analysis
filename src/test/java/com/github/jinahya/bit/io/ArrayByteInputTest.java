package com.github.jinahya.bit.io;

<<<<<<< Unknown file: This is a bug in JDime.
=======
import org.junit.jupiter.api.Test;

>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/right.java

<<<<<<< Unknown file: This is a bug in JDime.
=======
import org.junit.jupiter.api.extension.ExtendWith;

>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/right.java

<<<<<<< Unknown file: This is a bug in JDime.
=======
import static java.util.concurrent.ThreadLocalRandom.current;

>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/right.java

/**
 * A class for testing {@link ArrayByteInput}.
 */

<<<<<<< Unknown file: This is a bug in JDime.
=======
@ExtendWith(value = { ArrayByteInputParameterResolver.class, ArrayByteSourceParameterResolver.class })
>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/right.java
 class ArrayByteInputTest extends AbstractByteInputTest<ArrayByteInput, byte[]> {
  /**
     * Creates a new instance.
     */
  ArrayByteInputTest() {
    super(ArrayByteInput.class, byte[].class);
  }

  @Test void testGetIndex(final ArrayByteInput byteInput) {
    final int index = byteInput.getIndex();
  }

  @Test void testSetIndex(final ArrayByteInput byteInput) {
    byteInput.setIndex(current().nextInt());
  }
}