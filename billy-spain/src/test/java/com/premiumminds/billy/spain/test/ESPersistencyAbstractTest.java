package com.premiumminds.billy.spain.test;
import com.google.inject.Guice;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import org.junit.jupiter.api.BeforeEach;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import com.premiumminds.billy.core.services.StringID;
import com.premiumminds.billy.core.services.entities.Business;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.persistence.entities.jpa.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.spain.persistence.entities.ESCreditReceiptEntity;
import com.premiumminds.billy.spain.services.entities.ESReceipt;
import com.premiumminds.billy.spain.test.util.ESCreditReceiptTestUtil;
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

  @BeforeEach public void setUpModules() {
    ESAbstractTest.injector = Guice.createInjector(new SpainDependencyModule(), new SpainTestPersistenceDependencyModule());
    ESAbstractTest.injector.getInstance(SpainDependencyModule.Initializer.class);
    ESAbstractTest.injector.getInstance(SpainTestPersistenceDependencyModule.Initializer.class);
    SpainBootstrap.execute(ESAbstractTest.injector);
  }

  @AfterEach public void tearDown() {
    ESAbstractTest.injector.getInstance(SpainTestPersistenceDependencyModule.Finalizer.class);
  }

  public ESInvoiceEntity getNewIssuedInvoice() {
    return this.getNewIssuedInvoice(StringID.fromValue(UUID.randomUUID().toString()));
  }

  public ESInvoiceEntity getNewIssuedInvoice(StringID<Business> businessUID) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters(ESPersistencyAbstractTest.DEFAULT_SERIES, "30000");
    try {
      return (ESInvoiceEntity) service.issueDocument(new ESInvoiceTestUtil(ESAbstractTest.injector).getInvoiceBuilder(new ESBusinessTestUtil(ESAbstractTest.injector).getBusinessEntity(businessUID)), parameters);
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ESReceiptEntity getNewIssuedReceipt(StringID<Business> businessUID) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters(ESPersistencyAbstractTest.DEFAULT_SERIES, "007");
    try {
      return (ESReceiptEntity) service.issueDocument(new ESReceiptTestUtil(ESAbstractTest.injector).getReceiptBuilder(new ESBusinessTestUtil(ESAbstractTest.injector).getBusinessEntity(businessUID)), parameters);
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ESCreditReceiptEntity getNewIssuedCreditReceipt(ESReceipt receipt) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters("RC", "30000");
    this.createSeries(receipt, "RC");
    try {
      return (ESCreditReceiptEntity) service.issueDocument(new ESCreditReceiptTestUtil(ESAbstractTest.injector).getCreditReceiptBuilder((ESReceiptEntity) receipt), parameters);
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ESCreditNoteEntity getNewIssuedCreditnote(ESInvoice reference) {
    Services service = new Services(ESAbstractTest.injector);
    ESIssuingParams parameters = new ESIssuingParamsImpl();
    parameters = this.getParameters("NC", "30000");
    this.createSeries(reference, "NC");
    try {
      return (ESCreditNoteEntity) service.issueDocument(new ESCreditNoteTestUtil(ESAbstractTest.injector).getCreditNoteBuilder((ESInvoiceEntity) reference), parameters);
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
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

  protected void createSeries(StringID<Business> businessUID) {
    this.createSeries(new ESReceiptTestUtil(ESAbstractTest.injector).getReceiptBuilder(new ESBusinessTestUtil(ESAbstractTest.injector).getBusinessEntity(businessUID)).build(), ESPersistencyAbstractTest.DEFAULT_SERIES);
  }

  protected void createSeries(StringID<Business> businessUID, String series) {
    this.createSeries(new ESReceiptTestUtil(ESAbstractTest.injector).getReceiptBuilder(new ESBusinessTestUtil(ESAbstractTest.injector).getBusinessEntity(businessUID)).build(), series);
  }

  protected <T extends GenericInvoice> void createSeries(T document, String series) {
    final DAOInvoiceSeries daoInvoiceSeries = ESAbstractTest.injector.getInstance(DAOInvoiceSeries.class);
    final JPAInvoiceSeriesEntity seriesEntity = new JPAInvoiceSeriesEntity();
    seriesEntity.setSeries(series);
    seriesEntity.setBusiness(document.getBusiness());
    daoInvoiceSeries.create(seriesEntity);
  }
}