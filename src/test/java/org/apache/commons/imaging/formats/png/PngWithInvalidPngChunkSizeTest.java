package org.apache.commons.imaging.formats.png;
import static org.apache.commons.imaging.test.TestResources.fileResource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.examples.ImageReadExample.ManagedImageBufferedImageFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests for PNG files with invalid chunk sizes.
 */
public class PngWithInvalidPngChunkSizeTest {
  /**
     * Test that an image with an invalid negative PNG chunk size causes an
     * ImageReadException instead of other exception types.
     */
  @Test public void testPngWithInvalidNegativePngChunkSize() {
    final File imageFile = fileResource("/IMAGING-210/testfile.png");
    final PngImagingParameters params = new PngImagingParameters();
    params.setBufferedImageFactory(new ManagedImageBufferedImageFactory());
    final PngImageParser jpegImageParser = new PngImageParser();
    assertThrows(ImagingException.class, () -> jpegImageParser.getBufferedImage(imageFile, params));
  }

  /**
     * Test that an image with an invalid PNG chunk size causes an
     * ImageReadException instead of other exception types.
     */
  @Test public void testPngWithInvalidPngChunkSize() {
    final File imageFile = fileResource("/IMAGING-211/testfile_2.png");
    final PngImagingParameters params = new PngImagingParameters();
    params.setBufferedImageFactory(new ManagedImageBufferedImageFactory());
    final PngImageParser jpegImageParser = new PngImageParser();
    assertThrows(ImagingException.class, () -> jpegImageParser.getBufferedImage(imageFile, params));
  }
}