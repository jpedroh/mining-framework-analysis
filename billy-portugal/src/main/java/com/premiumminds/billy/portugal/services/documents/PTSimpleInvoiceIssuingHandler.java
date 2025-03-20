package com.premiumminds.billy.portugal.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSimpleInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTSimpleInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;

public class PTSimpleInvoiceIssuingHandler extends PTGenericInvoiceIssuingHandler<PTSimpleInvoiceEntity, PTIssuingParams> {
  public final static TYPE INVOICE_TYPE = TYPE.FS;

  private final DAOPTSimpleInvoice daoSimpleInvoice;

  @Inject public PTSimpleInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries, DAOPTSimpleInvoice daoSimpleInvoice) {
    super(daoInvoiceSeries);
    this.daoSimpleInvoice = daoSimpleInvoice;
  }

  @Override public PTSimpleInvoiceEntity issue(PTSimpleInvoiceEntity document, PTIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoSimpleInvoice, PTSimpleInvoiceIssuingHandler.INVOICE_TYPE);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(T document, P parameters) throws DocumentIssuingException {
    final PTIssuingParams parametersPT = (PTIssuingParams) parameters;
    return this.issue(document, parametersPT, this.daoSimpleInvoice, PTSimpleInvoiceIssuingHandler.INVOICE_TYPE);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTSimpleInvoiceIssuingHandler.java/right.java
}