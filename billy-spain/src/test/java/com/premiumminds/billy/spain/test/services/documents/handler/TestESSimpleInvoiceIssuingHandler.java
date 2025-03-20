package com.premiumminds.billy.spain.test.services.documents.handler;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.exceptions.BillySimpleInvoiceException;
import com.premiumminds.billy.spain.persistence.dao.DAOESSimpleInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESSimpleInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.ESSimpleInvoiceIssuingHandler;
import com.premiumminds.billy.spain.services.entities.ESSimpleInvoice;
import com.premiumminds.billy.spain.services.entities.ESSimpleInvoice.CLIENTTYPE;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.ESPersistencyAbstractTest;
import com.premiumminds.billy.spain.test.services.documents.ESDocumentAbstractTest;
import com.premiumminds.billy.spain.test.util.ESSimpleInvoiceTestUtil;

public class TestESSimpleInvoiceIssuingHandler extends ESDocumentAbstractTest {
  private String DEFAULT_SERIES = INVOICE_TYPE.FS + " " + ESPersistencyAbstractTest.DEFAULT_SERIES;

  private ESSimpleInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  @BeforeEach public void setUpNewSimpleInvoice() {
    this.handler = this.getInstance(ESSimpleInvoiceIssuingHandler.class);
    try {
      ESSimpleInvoiceEntity invoice = this.newInvoice(INVOICE_TYPE.FS, SOURCE_BILLING.APPLICATION);
      this.createSeries(invoice, this.DEFAULT_SERIES);
      this.issueNewInvoice(this.handler, invoice, this.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
  }

  @Test public void testIssuedInvoiceSimple() {
    ESSimpleInvoice issuedInvoice = this.getInstance(DAOESSimpleInvoice.class).get(this.issuedInvoiceUID);
    Assertions.assertEquals(this.DEFAULT_SERIES, issuedInvoice.getSeries());
    Assertions.assertTrue(1 == issuedInvoice.getSeriesNumber());
    String formatedNumber = this.DEFAULT_SERIES + "/1";
    Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
  }

  @Test public void testBusinessSimpleInvoice() {
    ESSimpleInvoiceTestUtil simpleInvoiceTestUtil = new ESSimpleInvoiceTestUtil(ESAbstractTest.injector);
    Assertions.assertThrows(BillySimpleInvoiceException.class, () -> simpleInvoiceTestUtil.getSimpleInvoiceEntity(CLIENTTYPE.BUSINESS));
  }
}