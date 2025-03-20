package com.premiumminds.billy.spain.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditReceipt;
import com.premiumminds.billy.spain.persistence.entities.ESCreditReceiptEntity;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;

public class ESCreditReceiptIssuingHandler extends ESGenericInvoiceIssuingHandler<ESCreditReceiptEntity, ESIssuingParams> {
  private final DAOESCreditReceipt daoCreditReceipt;

  @Inject public ESCreditReceiptIssuingHandler(DAOInvoiceSeries invoiceSeries, DAOESCreditReceipt daoCreditReceipt) {
    super(invoiceSeries);
    this.daoCreditReceipt = daoCreditReceipt;
  }

  @Override public ESCreditReceiptEntity issue(ESCreditReceiptEntity document, ESIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoCreditReceipt);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(final T document, P parameters) throws DocumentIssuingException {
    final ESIssuingParams parametersES = (ESIssuingParams) parameters;
    return this.issue(document, parametersES, this.daoCreditReceipt);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESCreditReceiptIssuingHandler.java/right.java
}