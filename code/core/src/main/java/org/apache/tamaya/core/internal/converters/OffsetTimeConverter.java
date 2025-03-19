package org.apache.tamaya.core.internal.converters;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;
import java.time.OffsetTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to OffsetTime.
 */
@Component(service = PropertyConverter.class) public class OffsetTimeConverter implements PropertyConverter<OffsetTime> {
  private final Logger LOG = Logger.getLogger(getClass().getName());

  @Override public OffsetTime convert(String value, ConversionContext context) {
    context.addSupportedFormats(getClass(), OffsetTime.now().toString());
    try {
      return OffsetTime.parse(value);
    } catch (Exception e) {
      LOG.log(Level.FINEST, e, () -> "Cannot parse OffsetTime: " + value);
      return null;
    }
  }

  @Override public boolean equals(Object o) {
    return Objects.nonNull(o) && getClass().equals(o.getClass());
  }

  @Override public int hashCode() {
    return getClass().hashCode();
  }
}