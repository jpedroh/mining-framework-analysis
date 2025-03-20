package com.premiumminds.billy.portugal.test.services.export.qrcode;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.PTInvoiceIssuingHandler;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.export.exceptions.RequiredFieldNotFoundException;
import com.premiumminds.billy.portugal.services.export.qrcode.QRCodeStringGenerator;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;
import com.premiumminds.billy.portugal.test.services.documents.PTDocumentAbstractTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestQRCodeStringGenerator extends PTDocumentAbstractTest {
  private static final TYPE DEFAULT_TYPE = TYPE.FT;

  private static final SourceBilling SOURCE_BILLING = SourceBilling.P;

  private PTInvoiceIssuingHandler handler;

  private UID issuedInvoiceUID;

  private QRCodeStringGenerator underTest;

  private DAOInvoiceSeries daoInvoiceSeries;

  @BeforeEach public void setUp() {
    this.handler = this.getInstance(PTInvoiceIssuingHandler.class);
    this.daoInvoiceSeries = this.getInstance(DAOInvoiceSeries.class);
    this.underTest = new QRCodeStringGenerator();
  }

  @Test public void testGenerateQRCodeData() throws SeriesUniqueCodeNotFilled {
    generateInvoice();
    final PTInvoice document = this.getInstance(DAOPTInvoice.class).get(this.issuedInvoiceUID);
    String result = null;
    try {
      result = underTest.generateQRCodeData(document);
    } catch (RequiredFieldNotFoundException e) {
      Assertions.fail();
    }
    final LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    final String hash = String.valueOf(document.getHash().charAt(0)) + document.getHash().charAt(10) + document.getHash().charAt(20) + document.getHash().charAt(30);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("A:123456789*B:123456789*C:PT*D:FT*E:N*F:" + now.format(formatter) + "*G:FT DEFAULT/1*H:ATCUD12345-1*I1:PT*I7:0.37*I8:0.08*N:0.08*O:0.45*Q:" + hash + "*R:1", result);
  }

  private void generateInvoice() throws SeriesUniqueCodeNotFilled {
    try {
      PTInvoiceEntity invoice = this.newInvoice(DEFAULT_TYPE, SOURCE_BILLING);
      InvoiceSeriesEntity entity = new JPAInvoiceSeriesEntity();
      entity.setBusiness(invoice.getBusiness());
      entity.setSeries(PTPersistencyAbstractTest.DEFAULT_SERIES);

<<<<<<< /usr/src/app/output/premium-minds/billy/9420e73394b22e375a57d57822bad925e7f9572a/billy-portugal/src/test/java/com/premiumminds/billy/portugal/test/services/export/qrcode/TestQRCodeStringGenerator.java/left.java
      entity.setSeriesUniqueCode("ATCUD12345");
=======
      if (withATCUD) {
        entity.setSeriesUniqueCode("ATCUD12345");
      }
>>>>>>> /usr/src/app/output/premium-minds/billy/9420e73394b22e375a57d57822bad925e7f9572a/billy-portugal/src/test/java/com/premiumminds/billy/portugal/test/services/export/qrcode/TestQRCodeStringGenerator.java/right.java

      daoInvoiceSeries.create(entity);
      this.issueNewInvoice(this.handler, invoice, PTPersistencyAbstractTest.DEFAULT_SERIES);
      this.issuedInvoiceUID = invoice.getUID();
    } catch (DocumentIssuingException | DocumentSeriesDoesNotExistException e) {
      Assertions.fail(e.getMessage());
    }
  }
}