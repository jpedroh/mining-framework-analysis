package com.github.jinahya.bit.io;

/**
 * A class for testing {@link ArrayByteInput}.
 */
import org.junit.jupiter.api.Test;
import static java.util.concurrent.ThreadLocalRandom.current;
<<<<<<< /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/left.java
class ArrayByteInputTest extends AbstractByteInputTest<ArrayByteInput, byte[]> {
||||||| /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/base.java
public class ArrayByteInputTest extends AbstractByteInputTest<ArrayByteInput, byte[]> {
=======
@ExtendWith({ArrayByteInputParameterResolver.class, ArrayByteSourceParameterResolver.class})
class ArrayByteInputTest extends AbstractByteInputTest<ArrayByteInput, byte[]> {
>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputTest.java/right.java

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    ArrayByteInputTest() {
        super(ArrayByteInput.class, byte[].class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void testGetIndex(final ArrayByteInput byteInput) {
        final int index = byteInput.getIndex();
    }

    @Test
    void testSetIndex(final ArrayByteInput byteInput) {
        byteInput.setIndex(current().nextInt());
    }
}
