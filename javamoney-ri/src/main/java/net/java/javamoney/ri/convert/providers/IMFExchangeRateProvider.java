package net.java.javamoney.ri.convert.providers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.money.CurrencyUnit;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateType;
import javax.money.convert.spi.ExchangeRateProviderSPI;
import net.java.javamoney.ri.convert.ExchangeRateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMFExchangeRateProvider implements ExchangeRateProviderSPI {
  private static final Logger LOGGER = LoggerFactory.getLogger(IMFExchangeRateProvider.class);

  private Map<CurrencyUnit, ExchangeRate> currencyToSDRMap = new HashMap<CurrencyUnit, ExchangeRate>();

  private Map<CurrencyUnit, ExchangeRate> sDRToCurrency = new HashMap<CurrencyUnit, ExchangeRate>();

  public void loadRates() {
    InputStream is = null;
    try {
      URL url = new URL("http://www.imf.org/external/np/fin/data/rms_five.aspx?tsvflag=Y");
      is = url.openStream();
      loadRates(is);
    } catch (Exception e) {
      LOGGER.error("Error", e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          LOGGER.warn("Error closing input stream.", e);
        }
      }
    }
  }

  private void loadRates(InputStream inputStream) throws IOException {
    CurrencyUnit sdr = getCurrency("SDR");
    NumberFormat f = new DecimalFormat("#0.0000000000");
    f.setGroupingUsed(false);
    BufferedReader pr = new BufferedReader(new InputStreamReader(inputStream));
    String line = pr.readLine();
    int sdrToCurrency = 0;
    while (line != null) {
      Long[] timestamps = null;
      if (line.startsWith("SDRs per Currency unit")) {
        sdrToCurrency = 1;
      } else {
        if (line.startsWith("Currency units per SDR")) {
          sdrToCurrency = 2;
        } else {
          if (line.startsWith("Currency ")) {
            timestamps = readTimestamps(line);
          }
        }
      }
      String[] parts = line.split("\\t");
      CurrencyUnit currency = getCurrency(parts[0]);
      Double[] values = parseValues(f, parts, 1);
      line = pr.readLine();
    }
  }

  private Double[] parseValues(NumberFormat f, String[] parts, int i) {
    return null;
  }

  private Long[] readTimestamps(String line) {
    return null;
  }

  private CurrencyUnit getCurrency(String string) {
    return null;
  }

  public static void main(String[] args) {
    new IMFExchangeRateProvider().loadRates();
  }

  @Override public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, boolean deferred) {
    return null;
  }

  @Override public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target, ExchangeRateType type, Long timestamp) {
    return null;
  }
}