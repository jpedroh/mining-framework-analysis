package org.apache.tamaya.core.internal.converters;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Float, using the Java number syntax:
 * (-)?[0-9]*\.[0-9]*. In case of error the value given also is tried being parsed as integral number using
 * {@link LongConverter}. Additionally the following values are supported:
 * <ul>
 * <li>NaN (ignoring case)</li>
 * <li>POSITIVE_INFINITY (ignoring case)</li>
 * <li>NEGATIVE_INFINITY (ignoring case)</li>
 * <li>MIN_VALUE (ignoring case)</li>
 * <li>MIN (ignoring case)</li>
 * <li>MAX_VALUE (ignoring case)</li>
 * <li>MAX (ignoring case)</li>
 * </ul>
 */
@Component(service = PropertyConverter.class) public class FloatConverter implements PropertyConverter<Float> {
  /**
     * The logger.
     */
  private static final Logger LOG = Logger.getLogger(FloatConverter.class.getName());

  /**
     * The converter used, when floating point parse failed.
     */
  private final IntegerConverter integerConverter = new IntegerConverter();

  @Override public Float convert(String value, ConversionContext context) {
    context.addSupportedFormats(getClass(), "<float>", "MIN", "MIN_VALUE", "MAX", "MAX_VALUE", "POSITIVE_INFINITY", "NEGATIVE_INFINITY", "NAN");
    String trimmed = Objects.requireNonNull(value).trim();
    switch (trimmed.toUpperCase(Locale.ENGLISH)) {
      case "POSITIVE_INFINITY":
      return Float.POSITIVE_INFINITY;
      case "NEGATIVE_INFINITY":
      return Float.NEGATIVE_INFINITY;
      case "NAN":
      return Float.NaN;
      case "MIN_VALUE":
      case "MIN":
      return Float.MIN_VALUE;
      case "MAX_VALUE":
      case "MAX":
      return Float.MAX_VALUE;
      default:
      try {
        return Float.valueOf(trimmed);
      } catch (Exception e) {
        LOG.finest("Parsing of float as floating number failed, trying parsing integral" + " number/hex instead...");
      }
      Integer val = integerConverter.convert(trimmed, context);
      if (val != null) {
        return val.floatValue();
      }
      LOG.finest("Unparseable float value: " + trimmed);
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