package com.premiumminds.billy.portugal.test.services.persistence;
import org.junit.Before;
import com.premiumminds.billy.portugal.BillyPortugal;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParamsImpl;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;
import com.premiumminds.billy.portugal.util.KeyGenerator;

public class PTPersistenceServiceAbstractTest extends PTPersistencyAbstractTest {
  protected PTIssuingParams parameters;

  protected BillyPortugal billy;

  @Before public void setUpParamenters() {
    KeyGenerator generator = new KeyGenerator(PTPersistencyAbstractTest.PRIVATE_KEY_DIR);
    this.parameters = new PTIssuingParamsImpl();
    this.parameters.setPrivateKey(generator.getPrivateKey());
    this.parameters.setPublicKey(generator.getPublicKey());
    this.parameters.setPrivateKeyVersion("1");
    this.parameters.setEACCode("31400");
    this.billy = this.getInstance(BillyPortugal.class);
  }
}