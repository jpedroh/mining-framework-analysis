package org.apache.tamaya.core.internal.converters;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to URL, using new URL(value).
 */
@Component(service = PropertyConverter.class) public class URLConverter implements PropertyConverter<URL> {
  private final Logger LOG = Logger.getLogger(getClass().getName());

  @Override public URL convert(String value, ConversionContext context) {
    if (value == null || value.isEmpty()) {
      return null;
    }
    context.addSupportedFormats(getClass(), "<URL>");
    String trimmed = Objects.requireNonNull(value).trim();
    try {
      return new URL(trimmed);
    } catch (Exception e) {
      LOG.log(Level.FINE, "Unparseable URL: " + trimmed, e);
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