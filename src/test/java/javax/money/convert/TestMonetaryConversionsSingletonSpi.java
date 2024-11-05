package javax.money.convert;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.spi.MonetaryConversionsSingletonSpi;

/**
 * @author Anatole Tresch
 * @author Werner
 * @version 0.3 on 11.05.14.
 */
public class TestMonetaryConversionsSingletonSpi implements MonetaryConversionsSingletonSpi {
  private ExchangeRateProvider provider = new DummyRateProvider();

  @Override public ExchangeRateProvider getExchangeRateProvider(ConversionQuery conversionQuery) {
    if (conversionQuery.getProviderNames().isEmpty() || conversionQuery.getProviderNames().contains("test")) {
      return provider;
    }
    throw new MonetaryException("No such rate provider(s): " + conversionQuery.getProviderNames());
  }

  @Override public boolean isExchangeRateProviderAvailable(ConversionQuery conversionQuery) {
    return conversionQuery.getProviderNames().isEmpty() || conversionQuery.getProviderNames().contains("test");
  }

  @Override public boolean isConversionAvailable(ConversionQuery conversionQuery) {
    return conversionQuery.getProviderNames().isEmpty() || conversionQuery.getProviderNames().contains("test");
  }

  @Override public Collection<String> getProviderNames() {
    return Collections.singletonList("test");
  }

  @Override public List<String> getDefaultProviderChain() {
    return new ArrayList<>(getProviderNames());
  }

  private static final class DummyConversion implements CurrencyConversion {
    private CurrencyUnit termCurrency;

    private ConversionContext ctx = ConversionContext.of();

    public DummyConversion(CurrencyUnit termCurrency) {
      this.termCurrency = termCurrency;
    }

    @Override public CurrencyUnit getCurrency() {
      return termCurrency;
    }

    @Override public ConversionContext getConversionContext() {
      return ctx;
    }

    @Override public ExchangeRate getExchangeRate(MonetaryAmount sourceAmount) {
      return new DefaultExchangeRate.Builder(getClass().getSimpleName(), RateType.OTHER).setBaseCurrency(sourceAmount.getCurrency()).setTermCurrency(termCurrency).setFactor(TestNumberValue.of(1)).build();
    }

    @Override public ExchangeRateProvider getExchangeRateProvider() {
      return null;
    }

    @Override public MonetaryAmount apply(MonetaryAmount value) {
      return value;
    }
  }

  private static final class DummyRateProvider implements ExchangeRateProvider {
    private ProviderContext ctx = ProviderContext.of("test");

    @Override public ProviderContext getProviderContext() {
      return ctx;
    }

    @Override public boolean isAvailable(CurrencyUnit base, CurrencyUnit term) {
      return false;
    }

    @Override public boolean isAvailable(ConversionQuery conversionContext) {
      return false;
    }

    @Override public ExchangeRate getExchangeRate(ConversionQuery query) {
      if (query.getBaseCurrency().getCurrencyCode().equals("test1") && query.getCurrency().getCurrencyCode().equals("test2")) {
        return new DefaultExchangeRate.Builder(getClass().getSimpleName(), RateType.OTHER).setBaseCurrency(query.getBaseCurrency()).setTermCurrency(query.getCurrency()).setFactor(TestNumberValue.of(new BigDecimal("0.5"))).build();
      }
      if (query.getBaseCurrency().getCurrencyCode().equals("test2") && query.getCurrency().getCurrencyCode().equals("test1")) {
        return new DefaultExchangeRate.Builder(getClass().getSimpleName(), RateType.OTHER).setBaseCurrency(query.getBaseCurrency()).setTermCurrency(query.getCurrency()).setFactor(TestNumberValue.of(new BigDecimal("2"))).build();
      }
      return new DefaultExchangeRate.Builder(getClass().getSimpleName(), RateType.OTHER).setBaseCurrency(query.getBaseCurrency()).setTermCurrency(query.getCurrency()).setFactor(TestNumberValue.of(1)).build();
    }

    @Override public ExchangeRate getReversed(ExchangeRate rate) {
      return getExchangeRate(rate.getCurrency(), rate.getBaseCurrency());
    }

    @Override public CurrencyConversion getCurrencyConversion(ConversionQuery query) {
      return new DummyConversion(query.getCurrency());
    }
  }
}