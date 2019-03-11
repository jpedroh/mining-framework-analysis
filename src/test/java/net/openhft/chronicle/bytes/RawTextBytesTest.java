package net.openhft.chronicle.bytes;

import net.openhft.chronicle.core.io.RawText;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RawTextBytesTest {
    @Test
    public void appendBase10() {
        Bytes bytes = Bytes.allocateDirect(32);
        for (long l = Long.MAX_VALUE; l > 0; l /= 2) {
            testAppendBase10(bytes, l);
            testAppendBase10(bytes, 1-l);
        }
        bytes.release();
    }

    static void testAppendBase10(Bytes bytes, long l) {
        long address = bytes.clear().addressForRead(0);
        long end = RawText.appendBase10(address, l);
        bytes.readLimit(end - address);
        String message = bytes.toString();
        assertEquals(message, l, bytes.parseLong());
    }
}
