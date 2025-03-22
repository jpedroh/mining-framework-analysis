package org.fluentlenium.example.spring.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component public class SeleniumBrowserConfigProperties {
  @Value(value = "${selenium.browser.type}") private BrowserType browserType;

  @Value(value = "${selenium.hub.enabled}") private Boolean useHub;

  @Value(value = "${selenium.hub.location}") private String hubLocation;

  @Value(value = "${selenium.get.url}") private String pageUrl;

  @Value(value = "${firefoxdriver.path}") private String firefoxDriverPath;

  @Value(value = "${chromedriver.path}") private String chromeDriverPath;

  @Value(value = "${safaridriver.path}") private String safariDriverPath;

  @Value(value = "${iedriver.path}") private String ieDriverPath;

  @Value(value = "${edgedriver.path}") private String edgeDriverPath;

  @Value(value = "${operadriver.path}") private String operaDriverPath;

  public BrowserConfig getBrowserConfig() {
    return new BrowserConfig(browserType, useHub, hubLocation);
  }

  public String getPageUrl() {
    return pageUrl;
  }

  public String getDriverExecutablePath() {
    switch (browserType) {
      case SAFARI:
      return safariDriverPath;
      case FIREFOX:
      return firefoxDriverPath;
      case IE:
      return ieDriverPath;
      case EDGE:
      return edgeDriverPath;
      case OPERA:
      return operaDriverPath;
      default:
      return chromeDriverPath;
    }
  }
}