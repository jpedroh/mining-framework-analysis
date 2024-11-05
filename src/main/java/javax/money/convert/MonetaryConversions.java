package javax.money.convert;
import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryException;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryConversionsSingletonSpi;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public final class MonetaryConversions {
  private static final MonetaryConversionsSingletonSpi MONETARY_CONVERSION_SPI = Optional.of(Bootstrap.getService(MonetaryConversionsSingletonSpi.class)).get();

  private MonetaryConversions() {
  }

  public static CurrencyConversion getConversion(CurrencyUnit termCurrency, String... providers) {
    Objects.requireNonNull(providers);
    Objects.requireNonNull(termCurrency);
    if (providers.length == 0) {
      return MONETARY_CONVERSION_SPI.getConversion(ConversionQueryBuilder.of().setTermCurrency(termCurrency).setProviderNames(getDefaultProviderChain()).build());
    }
    return MONETARY_CONVERSION_SPI.getConversion(ConversionQueryBuilder.of().setTermCurrency(termCurrency).setProviderNames(providers).build());
  }

  public static CurrencyConversion getConversion(String termCurrencyCode, String... providers) {
    Objects.requireNonNull(termCurrencyCode, "Term currency code may not be null");
    return getConversion(MonetaryCurrencies.getCurrency(termCurrencyCode), providers);
  }

  public static CurrencyConversion getConversion(ConversionQuery conversionQuery) {
    return Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi " + "loaded, " + "query functionality is not " + "available.")).getConversion(conversionQuery);
  }

  public static boolean isConversionAvailable(ConversionQuery conversionQuery) {
    return Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi " + "loaded, " + "query functionality is not " + "available.")).isConversionAvailable(conversionQuery);
  }

  public static boolean isConversionAvailable(String currencyCode, String... providers) {
    return Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi " + "loaded, " + "query functionality is not " + "available.")).isConversionAvailable(MonetaryCurrencies.getCurrency(currencyCode), providers);
  }

  public static boolean isConversionAvailable(CurrencyUnit termCurrency, String... providers) {
    return Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi " + "loaded, " + "query functionality is not " + "available.")).isConversionAvailable(termCurrency, providers);
  }

  public static ExchangeRateProvider getExchangeRateProvider(String... providers) {
    if (providers.length == 0) {
      List<String> defaultProviderChain = getDefaultProviderChain();
      return MONETARY_CONVERSION_SPI.getExchangeRateProvider(ConversionQueryBuilder.of().setProviderNames(defaultProviderChain.toArray(new String[defaultProviderChain.size()])).build());
    }
    ExchangeRateProvider provider = MONETARY_CONVERSION_SPI.getExchangeRateProvider(ConversionQueryBuilder.of().setProviderNames(providers).build());
    return Optional.ofNullable(provider).orElseThrow(() -> new MonetaryException("No such rate provider: " + Arrays.toString(providers)));
  }

  public static ExchangeRateProvider getExchangeRateProvider(ConversionQuery conversionQuery) {
    return Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi loaded, query functionality is not available.")).getExchangeRateProvider(conversionQuery);
  }

  public static boolean isExchangeRateProviderAvailable(ConversionQuery conversionQuery) {
    return Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi " + "loaded, " + "query functionality is not " + "available.")).isExchangeRateProviderAvailable(conversionQuery);
  }

  public static Collection<String> getProviderNames() {
    Collection<String> providers = Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi loaded, query functionality is not available.")).getProviderNames();
    if (Objects.isNull(providers)) {
      Logger.getLogger(MonetaryConversions.class.getName()).warning("No supported rate/conversion providers returned by SPI: " + MONETARY_CONVERSION_SPI.getClass().getName());
      return Collections.emptySet();
    }
    return providers;
  }

  public static List<String> getDefaultProviderChain() {
    List<String> defaultChain = Optional.ofNullable(MONETARY_CONVERSION_SPI).orElseThrow(() -> new MonetaryException("No MonetaryConveresionsSingletonSpi loaded, query functionality is not available.")).getDefaultProviderChain();
    Objects.requireNonNull(defaultChain, "No default provider chain provided by SPI: " + MONETARY_CONVERSION_SPI.getClass().getName());
    return defaultChain;
  }

  public static ExchangeRateProvider getExchangeRateProvider(ExchangeRateProviderSupplier provider, ExchangeRateProviderSupplier... providers) {
    List<ExchangeRateProviderSupplier> suplliers = new ArrayList<>();
    suplliers.add(Objects.requireNonNull(provider));
    Stream.of(providers).forEach(suplliers::add);
    String[] array = suplliers.stream().map(ExchangeRateProviderSupplier::get).toArray(String[]::new);
    return getExchangeRateProvider(array);
  }
}