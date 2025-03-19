package stockagent;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StockAgent {
  public void buyStock(LocalSensor sensor, Stock symbol) throws IOException;

  public void sellStock();

  public HashMap<Stock, Integer> getPorfolio();

  public double getBuyingPower();
}