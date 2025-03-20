package com.premiumminds.billy.spain.test;
import org.junit.After;
import org.junit.Before;
import com.google.inject.Guice;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.SpainBootstrap;
import com.premiumminds.billy.spain.SpainDependencyModule;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.ESInvoiceEntity;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntity;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParamsImpl;
import com.premiumminds.billy.spain.services.entities.ESInvoice;
import com.premiumminds.billy.spain.test.util.ESBusinessTestUtil;
import com.premiumminds.billy.spain.test.util.ESCreditNoteTestUtil;
import com.premiumminds.billy.spain.test.util.ESInvoiceTestUtil;
import com.premiumminds.billy.spain.test.util.ESReceiptTestUtil;
import com.premiumminds.billy.spain.util.Services;

public class ESPersistencyAbstractTest extends ESAbstractTest {
  protected static final String PRIVATE_KEY_DIR = "/keys/private.pem";

  protected static final String DEFAULT_SERIES = "DEFAULT";

  @Before public void setUpModules() {
    ESAbstractTest.injector = Guice.createInjector(new SpainDependencyModule(), new SpainTestPersistenceDependencyModule());
    ESAbstractTest.injector.getInstance(SpainDependencyModule.Initializer.class);
    ESAbstractTest.injector.getInstance(SpainTestPersistenceDependencyModule.Initializer.class);
    SpainBootstrap.execute(ESAbstractTest.injector);
  }

  @After public void tearDown() {
    ESAbstractTest.injector.getInstance(SpainTestPersistenceDependencyModule.Finalizer.class);
  }

  public ESInvoiceEntity getNewIssuedInvoice() {
    return this.getNewIssuedInvoice((new UID()).toString());
  }

  public ESInvoiceEntity getNewIssuedInvoice(String businessUID) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters(ESPersistencyAbstractTest.DEFAULT_SERIES, "30000");
    try {
      return (ESInvoiceEntity) service.issueDocument(new ESInvoiceTestUtil(ESAbstractTest.injector).getInvoiceBuilder(new ESBusinessTestUtil(ESAbstractTest.injector).getBusinessEntity(businessUID)), parameters);
    } catch (DocumentIssuingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ESReceiptEntity getNewIssuedReceipt(String businessUID) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters(ESPersistencyAbstractTest.DEFAULT_SERIES, "007");
    try {
      return (ESReceiptEntity) service.issueDocument(new ESReceiptTestUtil(ESAbstractTest.injector).getReceiptBuilder(new ESBusinessTestUtil(ESAbstractTest.injector).getBusinessEntity(businessUID)), parameters);
    } catch (DocumentIssuingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ESCreditNoteEntity getNewIssuedCreditnote(ESInvoice reference) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters("NC", "30000");
    try {
      return (ESCreditNoteEntity) service.issueDocument(new ESCreditNoteTestUtil(ESAbstractTest.injector).getCreditNoteBuilder((ESInvoiceEntity) reference), parameters);
    } catch (DocumentIssuingException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected ESIssuingParams getParameters(String series, String EACCode) {
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters.setEACCode(EACCode);
    parameters.setInvoiceSeries(series);
    return parameters;
  }
}