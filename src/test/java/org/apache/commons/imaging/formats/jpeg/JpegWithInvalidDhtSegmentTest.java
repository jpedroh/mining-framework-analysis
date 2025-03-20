package org.apache.commons.imaging.formats.jpeg;
import static org.apache.commons.imaging.test.TestResources.fileResource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.junit.jupiter.api.Test;

/**
 * Test that an invalid segment will not cause an ArrayIndexOutOfBoundsException
 * when the huffman table is created in a DHT segment.
 */
public class JpegWithInvalidDhtSegmentTest {
  @Test public void testSingleImage() {
    final File imageFile = fileResource("/IMAGING-215/ArrayIndexOutOfBoundsException_DhtSegment_79.jpeg");
    assertThrows(ImagingException.class, () -> Imaging.getMetadata(imageFile));
  }
}