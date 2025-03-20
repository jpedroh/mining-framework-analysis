package javax.money.convert.spi;
import javax.money.CurrencyUnit;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateType;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Anatole Tresch
 * @version 0.1.1
 */
public interface ExchangeRateProviderSPI {
  /**
	 * Get exchange rates for the given parameters.
	 * 
	 * @param source
	 *            the source currency.
	 * @param target
	 *            the target currency.
	 * @param type
	 *            Allows to determine the kind of rate to returned. {@code null}
	 *            means any type.
	 * @param deferred
	 *            If the quote should be deferred.
	 * @return the according exchange rate, or null.
	 */
  public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, boolean deferred);

  /**
	 * Get exchange rates for the given parameters.
	 * 
	 * @param source
	 *            the source currency.
	 * @param target
	 *            the target currency.
	 * @param type
	 *            Allows to determine the kind of rate to returned. {@code null}
	 *            means any type.
	 * @param timestamp
	 *            the required target UTC timestamp for the rate, or {@code null} for the
	 *            latest available.
	 * @param validityDuration
	 *            how long the quote should be considered valid.
	 * @return the according exchange rate, or null.
	 */
  public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, 
<<<<<<< /usr/src/app/output/javamoney/jsr354-api/75bfd2e554316270cfbd84c551e34e6f07c2b4d5/javamoney-api/convert/src/main/java/javax/money/convert/spi/ExchangeRateProviderSPI.java/left.java
  Long timestamp
=======
  long timestamp
>>>>>>> /usr/src/app/output/javamoney/jsr354-api/75bfd2e554316270cfbd84c551e34e6f07c2b4d5/javamoney-api/convert/src/main/java/javax/money/convert/spi/ExchangeRateProviderSPI.java/right.java
  );
}