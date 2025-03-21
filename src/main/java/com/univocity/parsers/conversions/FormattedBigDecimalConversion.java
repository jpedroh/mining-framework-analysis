package com.univocity.parsers.conversions;
import java.math.*;
import java.text.*;

/**
 * Converts formatted Strings to instances of {@link java.math.BigDecimal} and vice versa.
 *
 * <p> This class supports multiple numeric formats. For example, you can define conversions from numbers represented by different Strings such as "1,000,000.00 and $5.00".
 * <p> The reverse conversion from a BigDecimal to String (in <code>revert(BigDecimal)</code> will return a formatted String using the pattern provided in this class constructor
 * <p> The numeric patterns must follow the pattern rules of {@link java.text.DecimalFormat}
 *
 * @see java.text.DecimalFormat
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FormattedBigDecimalConversion extends NumericConversion<BigDecimal> {
  /**
	 * Defines a conversion from String to {@link java.math.BigDecimal} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param valueIfStringIsNull default BigDecimal to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a BigDecimal input is null. Used when <code>revert(BigDecimal)</code> is invoked.
	 * @param numericFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a BigDecimal into a String in <code>revert(BigDecimal)</code>.
	 */
  public FormattedBigDecimalConversion(BigDecimal valueIfStringIsNull, String valueIfObjectIsNull, String... numericFormats) {
    super(valueIfStringIsNull, valueIfObjectIsNull, numericFormats);
  }

  /**
	 * Defines a conversion from String to {@link java.math.BigDecimal} using a sequence of acceptable numeric patterns. The patterns
	 * must be added to this conversion class through the {@link #addFormat(String, String...)} method.
	 *
	 * @param valueIfStringIsNull default BigDecimal to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a BigDecimal input is null. Used when {@link BigDecimalConversion#revert(BigDecimal)} is invoked.
	 */
  public FormattedBigDecimalConversion(BigDecimal valueIfStringIsNull, String valueIfObjectIsNull) {
    super(valueIfStringIsNull, valueIfObjectIsNull);
  }

  /**
	 * Defines a conversion from String to {@link java.math.BigDecimal} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param numericFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a BigDecimal into a String in {@link BigDecimalConversion#revert(BigDecimal)}.
	 */
  public FormattedBigDecimalConversion(String... numericFormats) {
    super(null, null, numericFormats);
  }

  /**
	 * Defines a conversion from String to{@link java.math.BigDecimal} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param numericFormatters list formatters of acceptable numeric patterns. The first formatter in this sequence will be used to convert a BigDecimal into a String in {@link BigDecimalConversion#revert(BigDecimal)}.
	 */
  public FormattedBigDecimalConversion(DecimalFormat... numericFormatters) {
    super(numericFormatters);
  }

  /**
	 * Defines a conversion from String to {@link java.math.BigDecimal} using a sequence of acceptable numeric patterns. The patterns
	 * must be added to this conversion class through the {@link #addFormat(String, String...)} method.
	 *
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 */
  public FormattedBigDecimalConversion() {
    super();
  }

  /**
	 * Configures the Decimal format instance created by the parent class to parse BigDecimals.
	 */
  @Override protected void configureFormatter(DecimalFormat formatter) {
    formatter.setParseBigDecimal(true);
  }
}