package com.premiumminds.billy.spain.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.dao.DAOESReceipt;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntity;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;

public class ESReceiptIssuingHandler extends ESGenericInvoiceIssuingHandler<ESReceiptEntity, ESIssuingParams> {
  private final DAOESReceipt daoReceipt;

  @Inject public ESReceiptIssuingHandler(DAOInvoiceSeries daoInvoiceSeries, DAOESReceipt daoReceipt) {
    super(daoInvoiceSeries);
    this.daoReceipt = daoReceipt;
  }

  @Override public ESReceiptEntity issue(ESReceiptEntity document, ESIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoReceipt);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(T document, P parameters) throws DocumentIssuingException {
    final ESIssuingParams parametersES = (ESIssuingParams) parameters;
    return this.issue(document, parametersES, this.daoReceipt);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESReceiptIssuingHandler.java/right.java
}