package com.premiumminds.billy.portugal.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTReceiptInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTReceiptInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;

public class PTReceiptInvoiceIssuingHandler extends PTGenericInvoiceIssuingHandler<PTReceiptInvoiceEntity, PTIssuingParams> {
  public final static TYPE INVOICE_TYPE = TYPE.FR;

  private final DAOPTReceiptInvoice daoReceiptInvoice;

  @Inject public PTReceiptInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries, DAOPTReceiptInvoice daoRecepit) {
    super(daoInvoiceSeries);
    this.daoReceiptInvoice = daoRecepit;
  }

  @Override public PTReceiptInvoiceEntity issue(PTReceiptInvoiceEntity document, PTIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoReceiptInvoice, PTReceiptInvoiceIssuingHandler.INVOICE_TYPE);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(T document, P parameters) throws DocumentIssuingException {
    final PTIssuingParams parametersPT = (PTIssuingParams) parameters;
    return this.issue(document, parametersPT, this.daoReceiptInvoice, PTReceiptInvoiceIssuingHandler.INVOICE_TYPE);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTReceiptInvoiceIssuingHandler.java/right.java
}