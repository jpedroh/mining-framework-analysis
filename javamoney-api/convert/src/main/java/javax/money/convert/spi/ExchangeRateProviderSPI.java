package javax.money.convert.spi;
import javax.money.CurrencyUnit;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateType;

public interface ExchangeRateProviderSPI {
  public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, boolean deferred);

  public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, Long timestamp);

  public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, long timestamp);
}