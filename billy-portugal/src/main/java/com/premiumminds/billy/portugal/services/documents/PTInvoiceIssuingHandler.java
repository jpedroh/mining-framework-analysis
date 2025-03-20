package com.premiumminds.billy.portugal.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;

public class PTInvoiceIssuingHandler extends PTGenericInvoiceIssuingHandler<PTInvoiceEntity, PTIssuingParams> {
  public final static TYPE INVOICE_TYPE = TYPE.FT;

  private final DAOPTInvoice daoInvoice;

  @Inject public PTInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries, DAOPTInvoice daoInvoice) {
    super(daoInvoiceSeries);
    this.daoInvoice = daoInvoice;
  }

  @Override public PTInvoiceEntity issue(PTInvoiceEntity document, PTIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoInvoice, PTInvoiceIssuingHandler.INVOICE_TYPE);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(final T document, P parameters) throws DocumentIssuingException {
    final PTIssuingParams parametersPT = (PTIssuingParams) parameters;
    return this.issue(document, parametersPT, this.daoInvoice, PTInvoiceIssuingHandler.INVOICE_TYPE);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTInvoiceIssuingHandler.java/right.java
}