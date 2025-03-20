package javax.money.format;
import java.io.IOException;
import javax.money.CurrencyUnit;
import javax.money.format.common.FormatException;
import javax.money.format.common.StyledFormatter;

/**
 * Formats instances of {@link CurrencyUnit} to and from a String.
 * TODO see Formatter, maybe rename to *Printer like suggested by Joda sandbox
 */
public interface CurrencyFormatter extends StyledFormatter<CurrencyUnit> {
  /**
	 * Formats a currency's symbol value to a {@code String}.
	 * 
	 * @param currency
	 *            the currency to print, not null
	 * @return the string printed using the settings of this formatter
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 * @throws FormatException
	 *             if there is a problem while printing
	 */
  public String formatSymbol(CurrencyUnit currency);

  /**
	 * Formats a currency value to a {@code String}.
	 * 
	 * @param currency
	 *            the currency to print, not null
	 * @return the string printed using the settings of this formatter
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 * @throws FormatException
	 *             if there is a problem while printing
	 */
  public String formatDisplayName(CurrencyUnit currency);

  /**
	 * Prints an currency's symbol value to an {@code Appendable} converting any
	 * {@code IOException} to a {@code MoneyFormatException}.
	 * <p>
	 * Example implementations of {@code Appendable} are {@code StringBuilder},
	 * {@code StringBuffer} or {@code Writer}. Note that {@code StringBuilder}
	 * and {@code StringBuffer} never throw an {@code IOException}.
	 * 
	 * @param appendable
	 *            the appendable to add to, not null
	 * @param currency
	 *            the currency to print, not null
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 * @throws FormatException
	 *             if there is a problem while printing
	 * @throws IOException
	 *             if an IO error occurs
	 */
  public void printSymbol(Appendable appendable, CurrencyUnit currency) throws IOException;

  /**
	 * Prints a currency's name to an {@code Appendable} converting any
	 * {@code IOException} to a {@code MoneyFormatException}.
	 * <p>
	 * Example implementations of {@code Appendable} are {@code StringBuilder},
	 * {@code StringBuffer} or {@code Writer}. Note that {@code StringBuilder}
	 * and {@code StringBuffer} never throw an {@code IOException}.
	 * 
	 * @param appendable
	 *            the appendable to add to, not null
	 * @param moneyProvider
	 *            the money to print, not null
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 * @throws FormatException
	 *             if there is a problem while printing
	 * @throws IOException
	 *             if an IO error occurs
	 */
  public void printDisplayName(Appendable appendable, CurrencyUnit currency) throws IOException;
}