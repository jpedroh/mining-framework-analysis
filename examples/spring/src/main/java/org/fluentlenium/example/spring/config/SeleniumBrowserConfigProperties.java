package org.fluentlenium.example.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SeleniumBrowserConfigProperties {
    @Value("${selenium.browser.type}")
    private BrowserType browserType;
    @Value("${selenium.hub.enabled}")
    private Boolean useHub;
    @Value("${selenium.hub.location}")
    private String hubLocation;
    @Value("${selenium.get.url}")
    private String pageUrl;

    @Value("${firefoxdriver.path}")
    private String firefoxDriverPath;
    @Value("${chromedriver.path}")
    private String chromeDriverPath;
    @Value("${safaridriver.path}")
    private String safariDriverPath;
    @Value("${iedriver.path}")
    private String ieDriverPath;
    @Value("${edgedriver.path}")
    private String edgeDriverPath;
    @Value("${operadriver.path}")
    private String operaDriverPath;
    public BrowserConfig getBrowserConfig() {
        return new BrowserConfig(browserType, useHub, hubLocation);
    }
    public String getPageUrl() {
        return pageUrl;
    }
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/66d1ddb3128c56fa065ebd928b1831cf2cb21da5/examples/spring/src/main/java/org/fluentlenium/example/spring/config/SeleniumBrowserConfigProperties.java/left.java
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
||||||| /usr/src/app/output/fluentlenium/fluentlenium/66d1ddb3128c56fa065ebd928b1831cf2cb21da5/examples/spring/src/main/java/org/fluentlenium/example/spring/config/SeleniumBrowserConfigProperties.java/base.java
=======
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
            default:
                return chromeDriverPath;
        }
    }
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/66d1ddb3128c56fa065ebd928b1831cf2cb21da5/examples/spring/src/main/java/org/fluentlenium/example/spring/config/SeleniumBrowserConfigProperties.java/right.java
}
