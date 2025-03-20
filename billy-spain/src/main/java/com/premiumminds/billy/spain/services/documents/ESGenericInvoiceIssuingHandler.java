package com.premiumminds.billy.spain.services.documents;
import java.util.Date;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import com.premiumminds.billy.core.persistence.dao.AbstractDAOGenericInvoice;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.services.documents.DocumentIssuingHandler;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.entities.ESGenericInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.exceptions.InvalidInvoiceDateException;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;


<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
public abstract class ESGenericInvoiceIssuingHandler<T extends ESGenericInvoiceEntity, P extends ESIssuingParams> implements DocumentIssuingHandler<T, P> {
  protected DAOInvoiceSeries daoInvoiceSeries;

  @Inject public ESGenericInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries) {
    this.daoInvoiceSeries = daoInvoiceSeries;
  }

  protected <D extends AbstractDAOGenericInvoice<T>> T issue(final T document, final ESIssuingParams parametersES, final D daoInvoice) throws DocumentIssuingException {
    String series = parametersES.getInvoiceSeries();
    InvoiceSeriesEntity invoiceSeriesEntity = getInvoiceSeries(document, series, LockModeType.PESSIMISTIC_WRITE);
    document.initializeEntityDates();
    Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
    Integer seriesNumber = 1;
    T latestInvoice = daoInvoice.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(), document.getBusiness().getUID().toString());
    if (null != latestInvoice) {
      seriesNumber = latestInvoice.getSeriesNumber() + 1;
      Date latestInvoiceDate = latestInvoice.getDate();
      if (latestInvoiceDate.compareTo(invoiceDate) > 0) {
        throw new InvalidInvoiceDateException();
      }
    }
    String formatedNumber = parametersES.getInvoiceSeries() + "/" + seriesNumber;
    document.setDate(invoiceDate);
    document.setNumber(formatedNumber);
    document.setSeries(invoiceSeriesEntity.getSeries());
    document.setSeriesNumber(seriesNumber);
    document.setBilled(false);
    document.setCancelled(false);
    document.setEACCode(parametersES.getEACCode());
    document.setCurrency(document.getCurrency());
    daoInvoice.create(document);
    return document;
  }

  private InvoiceSeriesEntity getInvoiceSeries(final T document, String series, LockModeType lockMode) {
    InvoiceSeriesEntity invoiceSeriesEntity = daoInvoiceSeries.getSeries(series, document.getBusiness().getUID().toString(), lockMode);
    if (null == invoiceSeriesEntity) {
      InvoiceSeriesEntity entity = new JPAInvoiceSeriesEntity();
      entity.setBusiness(document.getBusiness());
      entity.setSeries(series);
      invoiceSeriesEntity = daoInvoiceSeries.create(entity);
    }
    return invoiceSeriesEntity;
  }
}
=======
public abstract class ESGenericInvoiceIssuingHandler extends DocumentIssuingHandlerImpl implements DocumentIssuingHandler {
  protected DAOInvoiceSeries daoInvoiceSeries;

  @Inject public ESGenericInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries) {
    this.daoInvoiceSeries = daoInvoiceSeries;
  }

  @Override public abstract <T extends GenericInvoice, P extends IssuingParams> T issue(T document, P parameters) throws DocumentIssuingException;

  protected <T extends GenericInvoice, D extends DAOGenericInvoice> T issue(final T document, final ESIssuingParams parametersES, final D daoInvoice) throws DocumentIssuingException {
    String series = parametersES.getInvoiceSeries();
    InvoiceSeriesEntity invoiceSeriesEntity = this.getInvoiceSeries(document, series, LockModeType.PESSIMISTIC_WRITE);
    ESGenericInvoiceEntity documentEntity = (ESGenericInvoiceEntity) document;
    ((BaseEntity) document).initializeEntityDates();
    Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
    Integer seriesNumber = 1;
    ESGenericInvoiceEntity latestInvoice = daoInvoice.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(), document.getBusiness().getUID().toString());
    if (null != latestInvoice) {
      seriesNumber = latestInvoice.getSeriesNumber() + 1;
      Date latestInvoiceDate = latestInvoice.getDate();
      if (latestInvoiceDate.compareTo(invoiceDate) > 0) {
        throw new InvalidInvoiceDateException();
      }
    }
    String formatedNumber = parametersES.getInvoiceSeries() + "/" + seriesNumber;
    documentEntity.setDate(invoiceDate);
    documentEntity.setNumber(formatedNumber);
    documentEntity.setSeries(invoiceSeriesEntity.getSeries());
    documentEntity.setSeriesNumber(seriesNumber);
    documentEntity.setBilled(false);
    documentEntity.setCancelled(false);
    documentEntity.setEACCode(parametersES.getEACCode());
    documentEntity.setCurrency(document.getCurrency());
    daoInvoice.create(documentEntity);
    return (T) documentEntity;
  }

  private <T extends GenericInvoice> InvoiceSeriesEntity getInvoiceSeries(final T document, String series, LockModeType lockMode) {
    InvoiceSeriesEntity invoiceSeriesEntity = this.daoInvoiceSeries.getSeries(series, document.getBusiness().getUID().toString(), lockMode);
    if (null == invoiceSeriesEntity) {
      InvoiceSeriesEntity entity = new JPAInvoiceSeriesEntity();
      entity.setBusiness(document.getBusiness());
      entity.setSeries(series);
      invoiceSeriesEntity = this.daoInvoiceSeries.create(entity);
    }
    return invoiceSeriesEntity;
  }
}
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java
