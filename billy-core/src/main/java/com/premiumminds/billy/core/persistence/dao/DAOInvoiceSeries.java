package com.premiumminds.billy.core.persistence.dao;
import javax.persistence.LockModeType;
import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;

public interface DAOInvoiceSeries extends DAO<InvoiceSeriesEntity> {
  public InvoiceSeriesEntity getSeries(String series, String businessUID, LockModeType lockMode);
}