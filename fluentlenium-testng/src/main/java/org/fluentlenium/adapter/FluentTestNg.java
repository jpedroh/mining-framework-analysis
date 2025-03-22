package org.fluentlenium.adapter;
import org.fluentlenium.core.FluentAdapter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
* All TestNG Test should extends this class. It provides default parameters.
*/
public abstract class FluentTestNg extends FluentAdapter {
  public FluentTestNg() {
    super();
  }

  @BeforeClass public void beforeClass() {
    this.initFluent(getDefaultDriver());
    initTest();
  }

  @AfterClass public void afterClass() {
    if (getDriver() != null) {
      getDriver().quit();
    }
  }
}