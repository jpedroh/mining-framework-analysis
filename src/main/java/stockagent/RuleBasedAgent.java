package stockagent;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class RuleBasedAgent {
  private Portfolio portfolio;

  private MarketSensor sensor;

  Random random = new Random();

  public RuleBasedAgent(Portfolio portfolio, MarketSensor sensor) throws IOException {
    this.portfolio = portfolio;
    this.sensor = sensor;
  }

  public void buyStock(MarketSensor sensor, String symbol) throws IOException {
    Stock stock = YahooFinance.get(symbol);
    BigDecimal pricing = sensor.getStockPrice(symbol);
    double currMoney = (portfolio.getBuyingPower()) * .10;
    double num = portfolio.getBuyingPower();
    if (currMoney > pricing.doubleValue()) {
      int shares = (int) (currMoney / pricing.doubleValue());
      portfolio.getPortfolio().put(stock, shares);
      portfolio.getPriceBoughtAt().put(stock, (pricing.doubleValue()));
      portfolio.setBuyingPower(num - currMoney);
    } else {
      System.out.println("COULDN\'T BUY STOCK DUE TO INSUFFICIENT FUNDS " + symbol);
    }
  }

  public Stock chooseStock(MarketSensor sensor) {
    List<String> key = new ArrayList<String>(sensor.getStocks().keySet());
    String randomKey = key.get(random.nextInt(key.size()));
    Stock value = sensor.getStocks().get(randomKey);
    return value;
  }
}