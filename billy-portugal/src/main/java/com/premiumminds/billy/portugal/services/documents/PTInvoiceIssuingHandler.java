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
    return this.issue(document, parameters, this.daoInvoice, PTInvoiceIssuingHandler.INVOICE_TYPE);
  }
}