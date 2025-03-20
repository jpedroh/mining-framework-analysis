package com.premiumminds.billy.spain.services.documents;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.dao.DAOESSimpleInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESSimpleInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;

public class ESSimpleInvoiceIssuingHandler extends ESGenericInvoiceIssuingHandler<ESSimpleInvoiceEntity, ESIssuingParams> {
  private final DAOESSimpleInvoice daoSimpleInvoice;

  @Inject public ESSimpleInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries, DAOESSimpleInvoice daoSimpleInvoice) {
    super(daoInvoiceSeries);
    this.daoSimpleInvoice = daoSimpleInvoice;
  }

  @Override public ESSimpleInvoiceEntity issue(ESSimpleInvoiceEntity document, ESIssuingParams parameters) throws DocumentIssuingException {
    return issue(document, parameters, daoSimpleInvoice);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public <T extends GenericInvoice, P extends IssuingParams> T issue(T document, P parameters) throws DocumentIssuingException {
    final ESIssuingParams parametersES = (ESIssuingParams) parameters;
    return this.issue(document, parametersES, this.daoSimpleInvoice);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESSimpleInvoiceIssuingHandler.java/right.java
}