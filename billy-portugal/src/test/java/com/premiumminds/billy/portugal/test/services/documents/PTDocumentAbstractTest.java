package com.premiumminds.billy.portugal.test.services.documents;
import java.util.Date;
import org.junit.Before;
import com.premiumminds.billy.core.exceptions.NotImplementedException;
import com.premiumminds.billy.core.services.documents.DocumentIssuingHandler;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTGenericInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParamsImpl;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;
import com.premiumminds.billy.portugal.test.PTAbstractTest;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;
import com.premiumminds.billy.portugal.test.util.PTInvoiceTestUtil;
import com.premiumminds.billy.portugal.test.util.PTReceiptInvoiceTestUtil;
import com.premiumminds.billy.portugal.test.util.PTSimpleInvoiceTestUtil;
import com.premiumminds.billy.portugal.util.KeyGenerator;

public class PTDocumentAbstractTest extends PTPersistencyAbstractTest {
  protected PTIssuingParams parameters;

  @Before public void setUpParamenters() {
    KeyGenerator generator = new KeyGenerator(PTPersistencyAbstractTest.PRIVATE_KEY_DIR);
    this.parameters = new PTIssuingParamsImpl();
    this.parameters.setPrivateKey(generator.getPrivateKey());
    this.parameters.setPublicKey(generator.getPublicKey());
    this.parameters.setPrivateKeyVersion("1");
    this.parameters.setEACCode("31400");
  }

  @SuppressWarnings(value = { "unchecked" }) protected <T extends PTGenericInvoiceEntity> T newInvoice(TYPE type, SourceBilling billing) {
    switch (type) {
      case FT:
      return (T) new PTInvoiceTestUtil(PTAbstractTest.injector).getInvoiceEntity(billing);
      case FS:
      return (T) new PTSimpleInvoiceTestUtil(PTAbstractTest.injector).getSimpleInvoiceEntity(billing);
      case FR:
      return (T) new PTReceiptInvoiceTestUtil(PTAbstractTest.injector).getReceiptInvoiceEntity(billing);
      case NC:
      throw new NotImplementedException();
      case ND:
      throw new NotImplementedException();
      default:
      return null;
    }
  }

  protected <T extends DocumentIssuingHandler, I extends PTGenericInvoiceEntity> void issueNewInvoice(T handler, I invoice, String series) throws DocumentIssuingException {
    DAOPTInvoice dao = this.getInstance(DAOPTInvoice.class);
    dao.beginTransaction();
    try {
      invoice.initializeEntityDates();
      this.issueNewInvoice(handler, invoice, series, new Date(invoice.getCreateTimestamp().getTime() + 100));
      dao.commit();
    } catch (DocumentIssuingException up) {
      dao.rollback();
      throw up;
    }
  }

  protected <T extends DocumentIssuingHandler, I extends PTGenericInvoiceEntity> void issueNewInvoice(T handler, I invoice, String series, Date date) throws DocumentIssuingException {
    this.parameters.setInvoiceSeries(series);
    invoice.setDate(date);
    handler.issue(invoice, this.parameters);
  }
}