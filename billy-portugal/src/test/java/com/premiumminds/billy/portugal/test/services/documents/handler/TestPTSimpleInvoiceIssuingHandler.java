package com.premiumminds.billy.portugal.test.services.documents.handler;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.exceptions.BillySimpleInvoiceException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSimpleInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTSimpleInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.PTSimpleInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice.CLIENTTYPE;
import com.premiumminds.billy.portugal.test.PTAbstractTest;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;
import com.premiumminds.billy.portugal.test.services.documents.PTDocumentAbstractTest;
import com.premiumminds.billy.portugal.test.util.PTSimpleInvoiceTestUtil;

public class TestPTSimpleInvoiceIssuingHandler extends PTDocumentAbstractTest {
  private static final TYPE DEFAULT_TYPE = TYPE.FS;

  private static final SourceBilling SOURCE_BILLING = SourceBilling.P;

  private PTSimpleInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  @BeforeEach public void setUpNewSimpleInvoice() {
    this.handler = this.getInstance(PTSimpleInvoiceIssuingHandler.class);
    try {
      PTSimpleInvoiceEntity invoice = this.newInvoice(TestPTSimpleInvoiceIssuingHandler.DEFAULT_TYPE, TestPTSimpleInvoiceIssuingHandler.SOURCE_BILLING);
      this.createSeries(invoice, PTPersistencyAbstractTest.DEFAULT_SERIES);
      this.issueNewInvoice(this.handler, invoice, PTPersistencyAbstractTest.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
  }

  @Test public void testIssuedInvoiceSimple() {
    PTSimpleInvoice issuedInvoice = this.getInstance(DAOPTSimpleInvoice.class).get(this.issuedInvoiceUID);
    Assertions.assertEquals(PTPersistencyAbstractTest.DEFAULT_SERIES, issuedInvoice.getSeries());
    Assertions.assertTrue(1 == issuedInvoice.getSeriesNumber());
    String formatedNumber = TestPTSimpleInvoiceIssuingHandler.DEFAULT_TYPE + " " + PTPersistencyAbstractTest.DEFAULT_SERIES + "/1";
    Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
    Assertions.assertEquals(TestPTSimpleInvoiceIssuingHandler.SOURCE_BILLING, issuedInvoice.getSourceBilling());
  }

  @Test public void testBusinessSimpleInvoice() {
    PTSimpleInvoiceTestUtil simpleInvoiceTestUtil = new PTSimpleInvoiceTestUtil(PTAbstractTest.injector);
    Assertions.assertThrows(BillySimpleInvoiceException.class, () -> simpleInvoiceTestUtil.getSimpleInvoiceEntity(TestPTSimpleInvoiceIssuingHandler.SOURCE_BILLING, CLIENTTYPE.BUSINESS));
  }
}