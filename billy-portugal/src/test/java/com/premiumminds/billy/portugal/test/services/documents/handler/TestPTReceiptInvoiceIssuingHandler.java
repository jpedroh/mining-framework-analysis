package com.premiumminds.billy.portugal.test.services.documents.handler;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTReceiptInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTReceiptInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.PTReceiptInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;
import com.premiumminds.billy.portugal.services.entities.PTReceiptInvoice;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;
import com.premiumminds.billy.portugal.test.services.documents.PTDocumentAbstractTest;

public class TestPTReceiptInvoiceIssuingHandler extends PTDocumentAbstractTest {
  private static final TYPE DEFAULT_TYPE = TYPE.FR;

  private static final SourceBilling SOURCE_BILLING = SourceBilling.P;

  private PTReceiptInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  @BeforeEach public void setUpNewInvoice() {
    this.handler = this.getInstance(PTReceiptInvoiceIssuingHandler.class);
    try {
      PTReceiptInvoiceEntity invoice = this.newInvoice(TestPTReceiptInvoiceIssuingHandler.DEFAULT_TYPE, TestPTReceiptInvoiceIssuingHandler.SOURCE_BILLING);
      this.createSeries(invoice, PTPersistencyAbstractTest.DEFAULT_SERIES);
      this.issueNewInvoice(this.handler, invoice, PTPersistencyAbstractTest.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
  }

  @Test public void testIssuedInvoiceSimple() {
    PTReceiptInvoice issuedInvoice = this.getInstance(DAOPTReceiptInvoice.class).get(this.issuedInvoiceUID);
    Assertions.assertEquals(PTPersistencyAbstractTest.DEFAULT_SERIES, issuedInvoice.getSeries());
    Assertions.assertTrue(1 == issuedInvoice.getSeriesNumber());
    String formatedNumber = TestPTReceiptInvoiceIssuingHandler.DEFAULT_TYPE + " " + PTPersistencyAbstractTest.DEFAULT_SERIES + "/1";
    Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
    Assertions.assertEquals(TestPTReceiptInvoiceIssuingHandler.SOURCE_BILLING, issuedInvoice.getSourceBilling());
  }
}