package com.premiumminds.billy.spain.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.dao.DAOESInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;

public class ESInvoiceIssuingHandler extends ESGenericInvoiceIssuingHandler<ESInvoiceEntity, ESIssuingParams> {
  private final DAOESInvoice daoInvoice;

  @Inject public ESInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries, DAOESInvoice daoInvoice) {
    super(daoInvoiceSeries);
    this.daoInvoice = daoInvoice;
  }

  @Override public ESInvoiceEntity issue(ESInvoiceEntity document, ESIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoInvoice);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(final T document, P parameters) throws DocumentIssuingException {
    final ESIssuingParams parametersES = (ESIssuingParams) parameters;
    return this.issue(document, parametersES, this.daoInvoice);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESInvoiceIssuingHandler.java/right.java
}