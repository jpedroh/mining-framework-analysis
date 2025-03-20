package com.premiumminds.billy.spain.test.services.documents.handler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.dao.DAOESInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.ESInvoiceIssuingHandler;
import com.premiumminds.billy.spain.test.ESPersistencyAbstractTest;
import com.premiumminds.billy.spain.test.services.documents.ESDocumentAbstractTest;

public class TestESManualInvoiceIssuingHandler extends ESDocumentAbstractTest {
  private ESInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  private String DEFAULT_SERIES = INVOICE_TYPE.FT + " " + ESPersistencyAbstractTest.DEFAULT_SERIES;

  @Before public void setUpNewManualInvoice() {
    this.handler = this.getInstance(ESInvoiceIssuingHandler.class);
    try {
      ESInvoiceEntity invoice = this.newInvoice(INVOICE_TYPE.FT, SOURCE_BILLING.MANUAL);
      this.issueNewInvoice(this.handler, invoice, this.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException e) {
      e.printStackTrace();
    }
  }

  @Test public void testIssuedManualInvoiceSimple() throws DocumentIssuingException {
    ESInvoiceEntity issuedInvoice = this.getInstance(DAOESInvoice.class).get(this.issuedInvoiceUID);
    Assert.assertEquals(this.DEFAULT_SERIES, issuedInvoice.getSeries());
    Assert.assertTrue(1 == issuedInvoice.getSeriesNumber());
    String formatedNumber = this.DEFAULT_SERIES + "/1";
    Assert.assertEquals(formatedNumber, issuedInvoice.getNumber());
  }
}