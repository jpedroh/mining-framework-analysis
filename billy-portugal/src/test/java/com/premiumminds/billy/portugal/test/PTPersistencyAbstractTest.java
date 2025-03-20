package com.premiumminds.billy.portugal.test;
import org.junit.After;
import org.junit.Before;
import com.google.inject.Guice;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.PortugalBootstrap;
import com.premiumminds.billy.portugal.PortugalDependencyModule;
import com.premiumminds.billy.portugal.persistence.entities.PTCreditNoteEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParamsImpl;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.test.util.PTBusinessTestUtil;
import com.premiumminds.billy.portugal.test.util.PTCreditNoteTestUtil;
import com.premiumminds.billy.portugal.test.util.PTInvoiceTestUtil;
import com.premiumminds.billy.portugal.util.KeyGenerator;
import com.premiumminds.billy.portugal.util.Services;

public class PTPersistencyAbstractTest extends PTAbstractTest {
  protected static final String PRIVATE_KEY_DIR = "/keys/private.pem";

  protected static final String DEFAULT_SERIES = "DEFAULT";

  @Before public void setUpModules() {
    PTAbstractTest.injector = Guice.createInjector(new PortugalDependencyModule(), new PortugalTestPersistenceDependencyModule());
    PTAbstractTest.injector.getInstance(PortugalDependencyModule.Initializer.class);
    PTAbstractTest.injector.getInstance(PortugalTestPersistenceDependencyModule.Initializer.class);
    PortugalBootstrap.execute(PTAbstractTest.injector);
  }

  @After public void tearDown() {
    PTAbstractTest.injector.getInstance(PortugalTestPersistenceDependencyModule.Finalizer.class);
  }

  public PTInvoiceEntity getNewIssuedInvoice() {
    return this.getNewIssuedInvoice((new UID()).toString());
  }

  public PTInvoiceEntity getNewIssuedInvoice(String businessUID) {
    Services service = new Services(PTAbstractTest.injector);
    PTIssuingParams parameters = new PTIssuingParamsImpl();
    parameters = this.getParameters(PTPersistencyAbstractTest.DEFAULT_SERIES, "30000", "1");
    try {
      return (PTInvoiceEntity) service.issueDocument(new PTInvoiceTestUtil(PTAbstractTest.injector).getInvoiceBuilder(new PTBusinessTestUtil(PTAbstractTest.injector).getBusinessEntity(businessUID), SourceBilling.P), parameters);
    } catch (DocumentIssuingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public PTCreditNoteEntity getNewIssuedCreditnote(PTInvoice reference) {
    Services service = new Services(PTAbstractTest.injector);
    PTIssuingParams parameters = new PTIssuingParamsImpl();
    parameters = this.getParameters("NC", "30000", "1");
    try {
      return (PTCreditNoteEntity) service.issueDocument(new PTCreditNoteTestUtil(PTAbstractTest.injector).getCreditNoteBuilder((PTInvoiceEntity) reference), parameters);
    } catch (DocumentIssuingException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected PTIssuingParams getParameters(String series, String EACCode, String privateKeyVersion) {
    PTIssuingParams parameters = new PTIssuingParamsImpl();
    KeyGenerator generator = new KeyGenerator(PTPersistencyAbstractTest.PRIVATE_KEY_DIR);
    parameters.setPrivateKey(generator.getPrivateKey());
    parameters.setPublicKey(generator.getPublicKey());
    parameters.setPrivateKeyVersion(privateKeyVersion);
    parameters.setEACCode(EACCode);
    parameters.setInvoiceSeries(series);
    return parameters;
  }
}