package org.apache.tamaya.core.internal.converters;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;
import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to LocalDate.
 */
@Component(service = PropertyConverter.class) public class LocalDateConverter implements PropertyConverter<LocalDate> {
  private final Logger LOG = Logger.getLogger(getClass().getName());

  @Override public LocalDate convert(String value, ConversionContext context) {
    context.addSupportedFormats(getClass(), LocalDate.now().toString());
    try {
      return LocalDate.parse(value);
    } catch (Exception e) {
      LOG.log(Level.FINEST, e, () -> "Cannot parse LocalDate: " + value);
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