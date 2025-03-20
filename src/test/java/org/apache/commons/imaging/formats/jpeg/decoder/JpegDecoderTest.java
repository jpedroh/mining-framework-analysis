package org.apache.commons.imaging.formats.jpeg.decoder;
import static org.apache.commons.imaging.test.TestResources.fileResource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.bytesource.ByteSource;
import org.junit.jupiter.api.Test;

/**
 * Tests for the JpegDecoder.
 */
public class JpegDecoderTest {
  /**
     * Test that a bad file does not hang or take too long to be processed.
     */
  @Test public void testDecodeBadFile() {
    final File inputFile = fileResource("/IMAGING-220/timeout-48eb4251935b4ca8b26d1859ea525c1b42ae0c78.jpeg");
    final ByteSource byteSourceFile = ByteSource.file(inputFile);
    assertThrows(ImagingException.class, () -> new JpegDecoder().decode(byteSourceFile));
  }
}