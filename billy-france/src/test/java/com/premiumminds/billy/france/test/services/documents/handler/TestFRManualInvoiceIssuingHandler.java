package com.premiumminds.billy.france.test.services.documents.handler;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.france.persistence.dao.DAOFRInvoice;
import com.premiumminds.billy.france.persistence.entities.FRInvoiceEntity;
import com.premiumminds.billy.france.services.documents.FRInvoiceIssuingHandler;
import com.premiumminds.billy.france.test.FRPersistencyAbstractTest;
import com.premiumminds.billy.france.test.services.documents.FRDocumentAbstractTest;

public class TestFRManualInvoiceIssuingHandler extends FRDocumentAbstractTest {
  private FRInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  private String DEFAULT_SERIES = INVOICE_TYPE.FT + " " + FRPersistencyAbstractTest.DEFAULT_SERIES;

  @BeforeEach public void setUpNewManualInvoice() {
    this.handler = this.getInstance(FRInvoiceIssuingHandler.class);
    try {
      FRInvoiceEntity invoice = this.newInvoice(INVOICE_TYPE.FT, SOURCE_BILLING.MANUAL);
      this.createSeries(invoice, this.DEFAULT_SERIES);
      this.issueNewInvoice(this.handler, invoice, this.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
  }

  @Test public void testIssuedManualInvoiceSimple() throws DocumentIssuingException {
    FRInvoiceEntity issuedInvoice = this.getInstance(DAOFRInvoice.class).get(this.issuedInvoiceUID);
    Assertions.assertEquals(this.DEFAULT_SERIES, issuedInvoice.getSeries());
    Assertions.assertTrue(1 == issuedInvoice.getSeriesNumber());
    String formatedNumber = this.DEFAULT_SERIES + "/1";
    Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
  }
}