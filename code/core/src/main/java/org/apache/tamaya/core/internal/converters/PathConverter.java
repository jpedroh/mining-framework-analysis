package org.apache.tamaya.core.internal.converters;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Path, using FileSystem.getPath(value).
 */
@Component(service = PropertyConverter.class) public class PathConverter implements PropertyConverter<Path> {
  private final Logger LOG = Logger.getLogger(getClass().getName());

  @Override public Path convert(String value, ConversionContext context) {
    if (value == null || value.isEmpty()) {
      return null;
    }
    context.addSupportedFormats(getClass(), "<File>");
    String trimmed = Objects.requireNonNull(value).trim();
    try {
      return FileSystems.getDefault().getPath(value);
    } catch (Exception e) {
      LOG.log(Level.FINE, "Unparseable Path: " + trimmed, e);
    }
    return null;
  }

  @Override public boolean equals(Object o) {
    return Objects.nonNull(o) && getClass().equals(o.getClass());
  }

  @Override public int hashCode() {
    return getClass().hashCode();
  }
}