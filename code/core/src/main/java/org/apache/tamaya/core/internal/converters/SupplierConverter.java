package org.apache.tamaya.core.internal.converters;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Supplier.
 */
@Component(service = PropertyConverter.class) public class SupplierConverter implements PropertyConverter<Supplier> {
  private static final Logger LOG = Logger.getLogger(SupplierConverter.class.getName());

  @Override public Supplier convert(String value, ConversionContext context) {
    return () -> {
      try {
        Type targetType = context.getTargetType().getType();
        ParameterizedType pt = (ParameterizedType) targetType;
        if (String.class.equals(pt.getActualTypeArguments()[0])) {
          return value;
        }
        ConvertQuery converter = new ConvertQuery(value, TypeLiteral.of(pt.getActualTypeArguments()[0]));
        Object o = context.getConfiguration().query(converter);
        if (o == null) {
          throw new ConfigException("No such value: " + context.getKey());
        }
        return o;
      } catch (Exception e) {
        throw new ConfigException("Error evaluating config value.", e);
      }
    };
  }

  @Override public boolean equals(Object o) {
    return Objects.nonNull(o) && getClass().equals(o.getClass());
  }

  @Override public int hashCode() {
    return getClass().hashCode();
  }
}