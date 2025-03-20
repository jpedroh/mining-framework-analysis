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
import com.premiumminds.billy.france.services.entities.FRInvoice;
import com.premiumminds.billy.france.test.FRPersistencyAbstractTest;
import com.premiumminds.billy.france.test.services.documents.FRDocumentAbstractTest;

public class TestFRInvoiceIssuingHandler extends FRDocumentAbstractTest {
  private FRInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  private String DEFAULT_SERIES = INVOICE_TYPE.FT + " " + FRPersistencyAbstractTest.DEFAULT_SERIES;

  @BeforeEach public void setUpNewInvoice() {
    this.handler = this.getInstance(FRInvoiceIssuingHandler.class);
    try {
      FRInvoiceEntity invoice = this.newInvoice(INVOICE_TYPE.FT);
      this.createSeries(invoice, this.DEFAULT_SERIES);
      this.issueNewInvoice(this.handler, invoice, this.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
      e.printStackTrace();
    }
  }

  @Test public void testIssuedInvoiceSimple() {
    FRInvoice issuedInvoice = this.getInstance(DAOFRInvoice.class).get(this.issuedInvoiceUID);
    Assertions.assertEquals(this.DEFAULT_SERIES, issuedInvoice.getSeries());
    Assertions.assertTrue(1 == issuedInvoice.getSeriesNumber());
    String formatedNumber = this.DEFAULT_SERIES + "/1";
    Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
  }

  @Test public void testIssuedInvoiceSameSeries() throws DocumentIssuingException, SeriesUniqueCodeNotFilled, DocumentSeriesDoesNotExistException {
    FRInvoice issuedInvoice = this.getInstance(DAOFRInvoice.class).get(this.issuedInvoiceUID);
    Integer nextNumber = 2;
    FRInvoiceEntity newInvoice = this.newInvoice(INVOICE_TYPE.FT);
    UID newInvoiceUID = newInvoice.getUID();
    newInvoice.setBusiness(issuedInvoice.getBusiness());
    this.issueNewInvoice(this.handler, newInvoice, this.DEFAULT_SERIES);
    FRInvoice lastInvoice = this.getInstance(DAOFRInvoice.class).get(newInvoiceUID);
    Assertions.assertEquals(this.DEFAULT_SERIES, lastInvoice.getSeries());
    Assertions.assertEquals(nextNumber, lastInvoice.getSeriesNumber());
    String formatedNumber = this.DEFAULT_SERIES + "/" + nextNumber;
    Assertions.assertEquals(formatedNumber, lastInvoice.getNumber());
  }

  @Test public void testIssuedInvoiceDifferentSeries() throws DocumentIssuingException, SeriesUniqueCodeNotFilled, DocumentSeriesDoesNotExistException {
    Integer nextNumber = 1;
    String newSeries = "FT NEW_SERIES";
    FRInvoiceEntity newInvoice = this.newInvoice(INVOICE_TYPE.FT);
    UID newInvoiceUID = newInvoice.getUID();
    this.createSeries(newInvoice, newSeries);
    this.issueNewInvoice(this.handler, newInvoice, newSeries);
    FRInvoice issuedInvoice = this.getInstance(DAOFRInvoice.class).get(newInvoiceUID);
    Assertions.assertEquals(newSeries, issuedInvoice.getSeries());
    Assertions.assertEquals(nextNumber, issuedInvoice.getSeriesNumber());
    String formatedNumber = newSeries + "/" + nextNumber;
    Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
  }

  @Test public void testIssuedInvoiceSameSourceBilling() throws DocumentIssuingException, SeriesUniqueCodeNotFilled, DocumentSeriesDoesNotExistException {
    FRInvoiceEntity newInvoice = this.newInvoice(INVOICE_TYPE.FT);
    UID newInvoiceUID = newInvoice.getUID();
    this.createSeries(newInvoice, this.DEFAULT_SERIES);
    this.issueNewInvoice(this.handler, newInvoice, this.DEFAULT_SERIES);
    this.getInstance(DAOFRInvoice.class).get(newInvoiceUID);
  }

  @Test public void testSeriesDoesNotExist() {
    FRInvoiceEntity invoiceEntity = this.newInvoice(INVOICE_TYPE.FT);
    Assertions.assertThrows(DocumentSeriesDoesNotExistException.class, () -> this.issueNewInvoice(this.handler, invoiceEntity, "A RANDOM SERIES"));
  }
}